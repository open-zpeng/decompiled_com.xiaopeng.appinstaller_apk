package com.xiaopeng.appinstaller.permission.ui.handheld;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.util.IconDrawableFactory;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.RestrictedLockUtils;
import com.xiaopeng.appinstaller.R;
import com.xiaopeng.appinstaller.permission.model.AppPermissionGroup;
import com.xiaopeng.appinstaller.permission.model.AppPermissions;
import com.xiaopeng.appinstaller.permission.model.Permission;
import com.xiaopeng.appinstaller.permission.utils.LocationUtils;
import com.xiaopeng.appinstaller.permission.utils.SafetyNetLogger;
import com.xiaopeng.appinstaller.permission.utils.Utils;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public final class AppPermissionsFragment extends SettingsWithHeader implements Preference.OnPreferenceChangeListener {
    private AppPermissions mAppPermissions;
    private PreferenceScreen mExtraScreen;
    private boolean mHasConfirmedRevoke;
    private List<AppPermissionGroup> mToggledGroups;

    public static AppPermissionsFragment newInstance(String packageName) {
        return (AppPermissionsFragment) setPackageName(new AppPermissionsFragment(), packageName);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static <T extends Fragment> T setPackageName(T fragment, String packageName) {
        Bundle arguments = new Bundle();
        arguments.putString("android.intent.extra.PACKAGE_NAME", packageName);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override // android.preference.PreferenceFragment, android.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLoading(true, false);
        setHasOptionsMenu(true);
        ActionBar ab = getActivity().getActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        String packageName = getArguments().getString("android.intent.extra.PACKAGE_NAME");
        Activity activity = getActivity();
        PackageInfo packageInfo = getPackageInfo(activity, packageName);
        if (packageInfo == null) {
            Toast.makeText(activity, (int) R.string.app_not_found_dlg_title, 1).show();
            activity.finish();
            return;
        }
        this.mAppPermissions = new AppPermissions(activity, packageInfo, null, true, new Runnable() { // from class: com.xiaopeng.appinstaller.permission.ui.handheld.AppPermissionsFragment.1
            @Override // java.lang.Runnable
            public void run() {
                AppPermissionsFragment.this.getActivity().finish();
            }
        });
        loadPreferences();
    }

    @Override // android.app.Fragment
    public void onResume() {
        super.onResume();
        this.mAppPermissions.refresh();
        loadPreferences();
        setPreferencesCheckedState();
    }

    @Override // android.app.Fragment
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == 0) {
            showAllPermissions(null);
            return true;
        } else if (itemId == 16908332) {
            getActivity().finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override // android.preference.PreferenceFragment, android.app.Fragment
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (this.mAppPermissions != null) {
            bindUi(this, this.mAppPermissions.getPackageInfo());
        }
    }

    @Override // android.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.add(0, 0, 0, R.string.all_permissions);
        HelpUtils.prepareHelpMenuItem(getActivity(), menu, (int) R.string.help_app_permissions, getClass().getName());
    }

    private void showAllPermissions(String filterGroup) {
        Fragment frag = AllAppPermissionsFragment.newInstance(getArguments().getString("android.intent.extra.PACKAGE_NAME"), filterGroup);
        getFragmentManager().beginTransaction().replace(16908290, frag).addToBackStack("AllPerms").commit();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void bindUi(SettingsWithHeader fragment, PackageInfo packageInfo) {
        Activity activity = fragment.getActivity();
        PackageManager pm = activity.getPackageManager();
        ApplicationInfo appInfo = packageInfo.applicationInfo;
        Intent infoIntent = null;
        if (!activity.getIntent().getBooleanExtra("hideInfoButton", false)) {
            infoIntent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS").setData(Uri.fromParts("package", packageInfo.packageName, null));
        }
        Drawable icon = IconDrawableFactory.newInstance(activity).getBadgedIcon(appInfo);
        CharSequence label = appInfo.loadLabel(pm);
        fragment.setHeader(icon, label, infoIntent);
        ActionBar ab = activity.getActionBar();
        if (ab != null) {
            ab.setTitle(R.string.app_permissions);
        }
    }

    private void loadPreferences() {
        Context context = getActivity();
        if (context == null) {
            return;
        }
        PreferenceScreen screen = getPreferenceScreen();
        if (screen == null) {
            screen = getPreferenceManager().createPreferenceScreen(getActivity());
            setPreferenceScreen(screen);
        }
        screen.removeAll();
        if (this.mExtraScreen != null) {
            this.mExtraScreen.removeAll();
        }
        Preference extraPerms = new Preference(context);
        extraPerms.setIcon(R.drawable.ic_toc);
        extraPerms.setTitle(R.string.additional_permissions);
        for (final AppPermissionGroup group : this.mAppPermissions.getPermissionGroups()) {
            if (Utils.shouldShowPermission(group, this.mAppPermissions.getPackageInfo().packageName)) {
                boolean isPlatform = group.getDeclaringPackage().equals("android");
                final RestrictedSwitchPreference preference = new RestrictedSwitchPreference(context);
                preference.setChecked(group.areRuntimePermissionsGranted());
                if (Utils.areGroupPermissionsIndividuallyControlled(getContext(), group.getName())) {
                    preference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.xiaopeng.appinstaller.permission.ui.handheld.-$$Lambda$AppPermissionsFragment$6Q50r5OEB3jSiNWu8dDoUpmfQio
                        @Override // android.preference.Preference.OnPreferenceClickListener
                        public final boolean onPreferenceClick(Preference preference2) {
                            return AppPermissionsFragment.lambda$loadPreferences$0(AppPermissionsFragment.this, group, preference2);
                        }
                    });
                    preference.setSwitchOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.appinstaller.permission.ui.handheld.-$$Lambda$AppPermissionsFragment$vEiH0YHTZIwswBjVkJotqIr5OHg
                        @Override // android.view.View.OnClickListener
                        public final void onClick(View view) {
                            AppPermissionsFragment.lambda$loadPreferences$1(AppPermissionsFragment.this, preference, group, view);
                        }
                    });
                    updateSummaryForIndividuallyControlledPermissionGroup(group, preference);
                } else {
                    preference.setOnPreferenceChangeListener(this);
                }
                preference.setKey(group.getName());
                Drawable icon = Utils.loadDrawable(context.getPackageManager(), group.getIconPkg(), group.getIconResId());
                preference.setIcon(Utils.applyTint(getContext(), icon, 16843817));
                preference.setTitle(group.getLabel());
                if (group.isPolicyFixed()) {
                    RestrictedLockUtils.EnforcedAdmin admin = RestrictedLockUtils.getProfileOrDeviceOwner(getContext(), group.getUserId());
                    if (admin != null) {
                        preference.setDisabledByAdmin(admin);
                        preference.setSummary(R.string.disabled_by_admin_summary_text);
                    } else {
                        preference.setSummary(R.string.permission_summary_enforced_by_policy);
                        preference.setEnabled(false);
                    }
                }
                preference.setPersistent(false);
                if (isPlatform) {
                    screen.addPreference(preference);
                } else {
                    if (this.mExtraScreen == null) {
                        this.mExtraScreen = getPreferenceManager().createPreferenceScreen(context);
                    }
                    this.mExtraScreen.addPreference(preference);
                }
            }
        }
        if (this.mExtraScreen != null) {
            extraPerms.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.xiaopeng.appinstaller.permission.ui.handheld.AppPermissionsFragment.2
                @Override // android.preference.Preference.OnPreferenceClickListener
                public boolean onPreferenceClick(Preference preference2) {
                    AdditionalPermissionsFragment frag = new AdditionalPermissionsFragment();
                    AppPermissionsFragment.setPackageName(frag, AppPermissionsFragment.this.getArguments().getString("android.intent.extra.PACKAGE_NAME"));
                    frag.setTargetFragment(AppPermissionsFragment.this, 0);
                    AppPermissionsFragment.this.getFragmentManager().beginTransaction().replace(16908290, frag).addToBackStack(null).commit();
                    return true;
                }
            });
            int count = this.mExtraScreen.getPreferenceCount();
            extraPerms.setSummary(getResources().getQuantityString(R.plurals.additional_permissions_more, count, Integer.valueOf(count)));
            screen.addPreference(extraPerms);
        }
        setLoading(false, true);
    }

    public static /* synthetic */ boolean lambda$loadPreferences$0(AppPermissionsFragment appPermissionsFragment, AppPermissionGroup group, Preference pref) {
        appPermissionsFragment.showAllPermissions(group.getName());
        return false;
    }

    public static /* synthetic */ void lambda$loadPreferences$1(AppPermissionsFragment appPermissionsFragment, RestrictedSwitchPreference preference, AppPermissionGroup group, View v) {
        Switch switchView = (Switch) v;
        appPermissionsFragment.onPreferenceChange(preference, Boolean.valueOf(switchView.isChecked()));
        appPermissionsFragment.updateSummaryForIndividuallyControlledPermissionGroup(group, preference);
        preference.setCheckedOverride(switchView.isChecked());
    }

    @Override // android.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(final Preference preference, Object newValue) {
        String groupName = preference.getKey();
        final AppPermissionGroup group = this.mAppPermissions.getPermissionGroup(groupName);
        if (group == null) {
            return false;
        }
        addToggledGroup(group);
        if (LocationUtils.isLocationGroupAndProvider(group.getName(), group.getApp().packageName)) {
            LocationUtils.showLocationDialog(getContext(), this.mAppPermissions.getAppLabel());
            return false;
        } else if (newValue == Boolean.TRUE) {
            group.grantRuntimePermissions(false);
            return true;
        } else {
            final boolean grantedByDefault = group.hasGrantedByDefaultPermission();
            if (grantedByDefault || (!group.doesSupportRuntimePermissions() && !this.mHasConfirmedRevoke)) {
                new AlertDialog.Builder(getContext()).setMessage(grantedByDefault ? R.string.system_warning : R.string.old_sdk_deny_warning).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.xiaopeng.appinstaller.permission.ui.handheld.-$$Lambda$AppPermissionsFragment$mesTKFzEN0tVdfW7tbgbD4C6XXI
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        AppPermissionsFragment.lambda$onPreferenceChange$2(preference, dialogInterface, i);
                    }
                }).setPositiveButton(R.string.grant_dialog_button_deny_anyway, new DialogInterface.OnClickListener() { // from class: com.xiaopeng.appinstaller.permission.ui.handheld.-$$Lambda$AppPermissionsFragment$dwkGuAxUUncgZXv1ILllIWG351Y
                    @Override // android.content.DialogInterface.OnClickListener
                    public final void onClick(DialogInterface dialogInterface, int i) {
                        AppPermissionsFragment.lambda$onPreferenceChange$3(AppPermissionsFragment.this, preference, group, grantedByDefault, dialogInterface, i);
                    }
                }).show();
                return false;
            }
            group.revokeRuntimePermissions(false);
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$onPreferenceChange$2(Preference preference, DialogInterface dialog, int which) {
        if (preference instanceof MultiTargetSwitchPreference) {
            ((MultiTargetSwitchPreference) preference).setCheckedOverride(true);
        }
    }

    public static /* synthetic */ void lambda$onPreferenceChange$3(AppPermissionsFragment appPermissionsFragment, Preference preference, AppPermissionGroup group, boolean grantedByDefault, DialogInterface dialog, int which) {
        ((SwitchPreference) preference).setChecked(false);
        group.revokeRuntimePermissions(false);
        if (Utils.areGroupPermissionsIndividuallyControlled(appPermissionsFragment.getContext(), group.getName())) {
            appPermissionsFragment.updateSummaryForIndividuallyControlledPermissionGroup(group, preference);
        }
        if (!grantedByDefault) {
            appPermissionsFragment.mHasConfirmedRevoke = true;
        }
    }

    @Override // android.app.Fragment
    public void onPause() {
        super.onPause();
        logToggledGroups();
    }

    private void updateSummaryForIndividuallyControlledPermissionGroup(AppPermissionGroup group, Preference preference) {
        int revokedCount;
        int resId;
        List<Permission> permissions = group.getPermissions();
        int permissionCount = permissions.size();
        int revokedCount2 = 0;
        for (revokedCount = 0; revokedCount < permissionCount; revokedCount = revokedCount + 1) {
            Permission permission = permissions.get(revokedCount);
            if (group.doesSupportRuntimePermissions()) {
                revokedCount = permission.isGranted() ? revokedCount + 1 : 0;
                revokedCount2++;
            } else {
                if (permission.isAppOpAllowed() && !permission.isReviewRequired()) {
                }
                revokedCount2++;
            }
        }
        if (revokedCount2 == 0) {
            resId = R.string.permission_revoked_none;
        } else if (revokedCount2 == permissionCount) {
            resId = R.string.permission_revoked_all;
        } else {
            resId = R.string.permission_revoked_count;
        }
        String summary = getString(resId, new Object[]{Integer.valueOf(revokedCount2)});
        preference.setSummary(summary);
    }

    private void addToggledGroup(AppPermissionGroup group) {
        if (this.mToggledGroups == null) {
            this.mToggledGroups = new ArrayList();
        }
        if (this.mToggledGroups.contains(group)) {
            this.mToggledGroups.remove(group);
        } else {
            this.mToggledGroups.add(group);
        }
    }

    private void logToggledGroups() {
        if (this.mToggledGroups != null) {
            String packageName = this.mAppPermissions.getPackageInfo().packageName;
            SafetyNetLogger.logPermissionsToggled(packageName, this.mToggledGroups);
            this.mToggledGroups = null;
        }
    }

    private void setPreferencesCheckedState() {
        setPreferencesCheckedState(getPreferenceScreen());
        if (this.mExtraScreen != null) {
            setPreferencesCheckedState(this.mExtraScreen);
        }
    }

    private void setPreferencesCheckedState(PreferenceScreen screen) {
        int preferenceCount = screen.getPreferenceCount();
        for (int i = 0; i < preferenceCount; i++) {
            Preference preference = screen.getPreference(i);
            if (preference instanceof SwitchPreference) {
                SwitchPreference switchPref = (SwitchPreference) preference;
                AppPermissionGroup group = this.mAppPermissions.getPermissionGroup(switchPref.getKey());
                if (group != null) {
                    switchPref.setChecked(group.areRuntimePermissionsGranted());
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static PackageInfo getPackageInfo(Activity activity, String packageName) {
        try {
            return activity.getPackageManager().getPackageInfo(packageName, 4096);
        } catch (PackageManager.NameNotFoundException e) {
            Log.i("ManagePermsFragment", "No package:" + activity.getCallingPackage(), e);
            return null;
        }
    }

    /* loaded from: classes.dex */
    public static class AdditionalPermissionsFragment extends SettingsWithHeader {
        AppPermissionsFragment mOuterFragment;

        @Override // android.preference.PreferenceFragment, android.app.Fragment
        public void onCreate(Bundle savedInstanceState) {
            this.mOuterFragment = (AppPermissionsFragment) getTargetFragment();
            super.onCreate(savedInstanceState);
            setHeader(this.mOuterFragment.mIcon, this.mOuterFragment.mLabel, this.mOuterFragment.mInfoIntent);
            setHasOptionsMenu(true);
            setPreferenceScreen(this.mOuterFragment.mExtraScreen);
        }

        @Override // android.preference.PreferenceFragment, android.app.Fragment
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            String packageName = getArguments().getString("android.intent.extra.PACKAGE_NAME");
            AppPermissionsFragment.bindUi(this, AppPermissionsFragment.getPackageInfo(getActivity(), packageName));
        }

        @Override // android.app.Fragment
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == 16908332) {
                getFragmentManager().popBackStack();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }
}
