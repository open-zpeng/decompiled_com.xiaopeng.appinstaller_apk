package com.xiaopeng.appinstaller.permission.ui.handheld;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import com.android.settingslib.HelpUtils;
import com.android.settingslib.RestrictedLockUtils;
import com.xiaopeng.appinstaller.DeviceUtils;
import com.xiaopeng.appinstaller.R;
import com.xiaopeng.appinstaller.permission.model.AppPermissionGroup;
import com.xiaopeng.appinstaller.permission.model.PermissionApps;
import com.xiaopeng.appinstaller.permission.utils.LocationUtils;
import com.xiaopeng.appinstaller.permission.utils.SafetyNetLogger;
import com.xiaopeng.appinstaller.permission.utils.Utils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public final class PermissionAppsFragment extends PermissionsFrameFragment implements Preference.OnPreferenceChangeListener, PermissionApps.Callback {
    private static final String SHOW_SYSTEM_KEY = PermissionAppsFragment.class.getName() + "_showSystem";
    private PreferenceScreen mExtraScreen;
    private boolean mHasConfirmedRevoke;
    private boolean mHasSystemApps;
    private MenuItem mHideSystemMenu;
    private ArraySet<String> mLauncherPkgs;
    private PermissionApps.Callback mOnPermissionsLoadedListener;
    private PermissionApps mPermissionApps;
    private boolean mShowSystem;
    private MenuItem mShowSystemMenu;
    private ArrayMap<String, AppPermissionGroup> mToggledGroups;

    public static PermissionAppsFragment newInstance(String permissionName) {
        return (PermissionAppsFragment) setPermissionName(new PermissionAppsFragment(), permissionName);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static <T extends Fragment> T setPermissionName(T fragment, String permissionName) {
        Bundle arguments = new Bundle();
        arguments.putString("android.intent.extra.PERMISSION_NAME", permissionName);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override // android.preference.PreferenceFragment, android.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            this.mShowSystem = savedInstanceState.getBoolean(SHOW_SYSTEM_KEY);
        }
        setLoading(true, false);
        setHasOptionsMenu(true);
        ActionBar ab = getActivity().getActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        this.mLauncherPkgs = Utils.getLauncherPackages(getContext());
        String groupName = getArguments().getString("android.intent.extra.PERMISSION_NAME");
        this.mPermissionApps = new PermissionApps(getActivity(), groupName, this);
        this.mPermissionApps.refresh(true);
    }

    @Override // android.preference.PreferenceFragment, android.app.Fragment
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(SHOW_SYSTEM_KEY, this.mShowSystem);
    }

    @Override // android.app.Fragment
    public void onResume() {
        super.onResume();
        this.mPermissionApps.refresh(true);
    }

    @Override // android.app.Fragment
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (this.mHasSystemApps) {
            this.mShowSystemMenu = menu.add(0, 1, 0, R.string.menu_show_system);
            this.mHideSystemMenu = menu.add(0, 2, 0, R.string.menu_hide_system);
            updateMenu();
        }
        HelpUtils.prepareHelpMenuItem(getActivity(), menu, (int) R.string.help_app_permissions, getClass().getName());
    }

    @Override // android.app.Fragment
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == 16908332) {
            getActivity().finish();
            return true;
        }
        switch (itemId) {
            case 1:
            case 2:
                this.mShowSystem = item.getItemId() == 1;
                if (this.mPermissionApps.getApps() != null) {
                    onPermissionsLoaded(this.mPermissionApps);
                }
                updateMenu();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateMenu() {
        this.mShowSystemMenu.setVisible(!this.mShowSystem);
        this.mHideSystemMenu.setVisible(this.mShowSystem);
    }

    @Override // android.preference.PreferenceFragment, android.app.Fragment
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindUi(this, this.mPermissionApps);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void bindUi(Fragment fragment, PermissionApps permissionApps) {
        permissionApps.getIcon();
        CharSequence label = permissionApps.getLabel();
        ActionBar ab = fragment.getActivity().getActionBar();
        if (ab != null) {
            ab.setTitle(fragment.getString(R.string.permission_title, label));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setOnPermissionsLoadedListener(PermissionApps.Callback callback) {
        this.mOnPermissionsLoadedListener = callback;
    }

    @Override // com.xiaopeng.appinstaller.permission.model.PermissionApps.Callback
    public void onPermissionsLoaded(PermissionApps permissionApps) {
        Preference pref;
        boolean z;
        CharSequence charSequence;
        CharSequence charSequence2;
        Context context = getActivity();
        if (context == null) {
            return;
        }
        boolean isTelevision = DeviceUtils.isTelevision(context);
        PreferenceScreen screen = getPreferenceScreen();
        if (screen == null) {
            screen = getPreferenceManager().createPreferenceScreen(getActivity());
            setPreferenceScreen(screen);
        }
        screen.setOrderingAsAdded(false);
        ArraySet<String> preferencesToRemove = new ArraySet<>();
        int n = screen.getPreferenceCount();
        for (int i = 0; i < n; i++) {
            preferencesToRemove.add(screen.getPreference(i).getKey());
        }
        if (this.mExtraScreen != null) {
            int n2 = this.mExtraScreen.getPreferenceCount();
            for (int i2 = 0; i2 < n2; i2++) {
                preferencesToRemove.add(this.mExtraScreen.getPreference(i2).getKey());
            }
        }
        this.mHasSystemApps = false;
        boolean menuOptionsInvalided = false;
        for (PermissionApps.PermissionApp app : permissionApps.getApps()) {
            if (Utils.shouldShowPermission(app) && app.getAppInfo().enabled) {
                String key = app.getKey();
                preferencesToRemove.remove(key);
                Preference existingPref = screen.findPreference(key);
                if (existingPref == null && this.mExtraScreen != null) {
                    existingPref = this.mExtraScreen.findPreference(key);
                }
                boolean isSystemApp = Utils.isSystem(app, this.mLauncherPkgs);
                if (isSystemApp && !menuOptionsInvalided) {
                    this.mHasSystemApps = true;
                    getActivity().invalidateOptionsMenu();
                    menuOptionsInvalided = true;
                }
                if (!isSystemApp || isTelevision || this.mShowSystem) {
                    if (existingPref != null) {
                        boolean isPolicyFixed = app.isPolicyFixed();
                        RestrictedLockUtils.EnforcedAdmin enforcedAdmin = RestrictedLockUtils.getProfileOrDeviceOwner(getActivity(), app.getUserId());
                        if (!isTelevision && (existingPref instanceof RestrictedSwitchPreference)) {
                            ((RestrictedSwitchPreference) existingPref).setDisabledByAdmin(isPolicyFixed ? enforcedAdmin : null);
                            if (!isPolicyFixed) {
                                charSequence2 = null;
                            } else {
                                charSequence2 = getString(R.string.disabled_by_admin_summary_text);
                            }
                            existingPref.setSummary(charSequence2);
                        } else {
                            existingPref.setEnabled(!isPolicyFixed);
                            if (!isPolicyFixed) {
                                charSequence = null;
                            } else {
                                charSequence = getString(R.string.permission_summary_enforced_by_policy);
                            }
                            existingPref.setSummary(charSequence);
                        }
                        existingPref.setPersistent(false);
                        if (existingPref instanceof SwitchPreference) {
                            ((SwitchPreference) existingPref).setChecked(app.areRuntimePermissionsGranted());
                        }
                    } else {
                        RestrictedSwitchPreference pref2 = new RestrictedSwitchPreference(context);
                        pref2.setOnPreferenceChangeListener(this);
                        pref2.setKey(app.getKey());
                        pref2.setIcon(app.getIcon());
                        pref2.setTitle(app.getLabel());
                        RestrictedLockUtils.EnforcedAdmin enforcedAdmin2 = RestrictedLockUtils.getProfileOrDeviceOwner(getActivity(), app.getUserId());
                        if (app.isPolicyFixed()) {
                            if (!isTelevision && enforcedAdmin2 != null) {
                                pref2.setDisabledByAdmin(enforcedAdmin2);
                                pref2.setSummary(R.string.disabled_by_admin_summary_text);
                            } else {
                                z = false;
                                pref2.setEnabled(false);
                                pref2.setSummary(R.string.permission_summary_enforced_by_policy);
                                pref2.setPersistent(z);
                                pref2.setChecked(app.areRuntimePermissionsGranted());
                                if (!isSystemApp && isTelevision) {
                                    if (this.mExtraScreen == null) {
                                        this.mExtraScreen = getPreferenceManager().createPreferenceScreen(context);
                                    }
                                    this.mExtraScreen.addPreference(pref2);
                                } else {
                                    screen.addPreference(pref2);
                                }
                            }
                        }
                        z = false;
                        pref2.setPersistent(z);
                        pref2.setChecked(app.areRuntimePermissionsGranted());
                        if (!isSystemApp) {
                        }
                        screen.addPreference(pref2);
                    }
                } else if (existingPref != null) {
                    screen.removePreference(existingPref);
                }
            }
        }
        if (this.mExtraScreen != null) {
            preferencesToRemove.remove("_showSystem");
            Preference pref3 = screen.findPreference("_showSystem");
            if (pref3 == null) {
                pref3 = new Preference(context);
                pref3.setKey("_showSystem");
                pref3.setIcon(Utils.applyTint(context, (int) R.drawable.ic_toc, 16843817));
                pref3.setTitle("Show system apps");
                pref3.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.xiaopeng.appinstaller.permission.ui.handheld.PermissionAppsFragment.1
                    @Override // android.preference.Preference.OnPreferenceClickListener
                    public boolean onPreferenceClick(Preference preference) {
                        SystemAppsFragment frag = new SystemAppsFragment();
                        PermissionAppsFragment.setPermissionName(frag, PermissionAppsFragment.this.getArguments().getString("android.intent.extra.PERMISSION_NAME"));
                        frag.setTargetFragment(PermissionAppsFragment.this, 0);
                        PermissionAppsFragment.this.getFragmentManager().beginTransaction().replace(16908290, frag).addToBackStack("SystemApps").commit();
                        return true;
                    }
                });
                screen.addPreference(pref3);
            }
            int grantedCount = 0;
            int n3 = this.mExtraScreen.getPreferenceCount();
            for (int i3 = 0; i3 < n3; i3++) {
                if (((SwitchPreference) this.mExtraScreen.getPreference(i3)).isChecked()) {
                    grantedCount++;
                }
            }
            pref3.setSummary(getString(R.string.app_permissions_group_summary, new Object[]{Integer.valueOf(grantedCount), Integer.valueOf(this.mExtraScreen.getPreferenceCount())}));
        }
        Iterator<String> it = preferencesToRemove.iterator();
        while (it.hasNext()) {
            String key2 = it.next();
            Preference pref4 = screen.findPreference(key2);
            if (pref4 != null) {
                screen.removePreference(pref4);
            } else if (this.mExtraScreen != null && (pref = this.mExtraScreen.findPreference(key2)) != null) {
                this.mExtraScreen.removePreference(pref);
            }
        }
        setLoading(false, true);
        if (this.mOnPermissionsLoadedListener != null) {
            this.mOnPermissionsLoadedListener.onPermissionsLoaded(permissionApps);
        }
    }

    @Override // android.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(final Preference preference, Object newValue) {
        String pkg = preference.getKey();
        final PermissionApps.PermissionApp app = this.mPermissionApps.getApp(pkg);
        if (app == null) {
            return false;
        }
        addToggledGroup(app.getPackageName(), app.getPermissionGroup());
        if (LocationUtils.isLocationGroupAndProvider(this.mPermissionApps.getGroupName(), app.getPackageName())) {
            LocationUtils.showLocationDialog(getContext(), app.getLabel());
            return false;
        } else if (newValue == Boolean.TRUE) {
            app.grantRuntimePermissions();
            return true;
        } else {
            final boolean grantedByDefault = app.hasGrantedByDefaultPermissions();
            if (grantedByDefault || (!app.doesSupportRuntimePermissions() && !this.mHasConfirmedRevoke)) {
                new AlertDialog.Builder(getContext()).setMessage(grantedByDefault ? R.string.system_warning : R.string.old_sdk_deny_warning).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.grant_dialog_button_deny_anyway, new DialogInterface.OnClickListener() { // from class: com.xiaopeng.appinstaller.permission.ui.handheld.PermissionAppsFragment.2
                    @Override // android.content.DialogInterface.OnClickListener
                    public void onClick(DialogInterface dialog, int which) {
                        ((SwitchPreference) preference).setChecked(false);
                        app.revokeRuntimePermissions();
                        if (!grantedByDefault) {
                            PermissionAppsFragment.this.mHasConfirmedRevoke = true;
                        }
                    }
                }).show();
                return false;
            }
            app.revokeRuntimePermissions();
            return true;
        }
    }

    @Override // android.app.Fragment
    public void onPause() {
        super.onPause();
        logToggledGroups();
    }

    private void addToggledGroup(String packageName, AppPermissionGroup group) {
        if (this.mToggledGroups == null) {
            this.mToggledGroups = new ArrayMap<>();
        }
        if (this.mToggledGroups.containsKey(packageName)) {
            this.mToggledGroups.remove(packageName);
        } else {
            this.mToggledGroups.put(packageName, group);
        }
    }

    private void logToggledGroups() {
        if (this.mToggledGroups != null) {
            int groupCount = this.mToggledGroups.size();
            for (int i = 0; i < groupCount; i++) {
                String packageName = this.mToggledGroups.keyAt(i);
                List<AppPermissionGroup> groups = new ArrayList<>();
                groups.add(this.mToggledGroups.valueAt(i));
                SafetyNetLogger.logPermissionsToggled(packageName, groups);
            }
            this.mToggledGroups = null;
        }
    }

    /* loaded from: classes.dex */
    public static class SystemAppsFragment extends PermissionsFrameFragment implements PermissionApps.Callback {
        PermissionAppsFragment mOuterFragment;

        @Override // android.preference.PreferenceFragment, android.app.Fragment
        public void onCreate(Bundle savedInstanceState) {
            this.mOuterFragment = (PermissionAppsFragment) getTargetFragment();
            setLoading(true, false);
            super.onCreate(savedInstanceState);
            if (this.mOuterFragment.mExtraScreen == null) {
                this.mOuterFragment.setOnPermissionsLoadedListener(this);
            } else {
                setPreferenceScreen();
            }
        }

        @Override // android.preference.PreferenceFragment, android.app.Fragment
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            String groupName = getArguments().getString("android.intent.extra.PERMISSION_NAME");
            PermissionApps permissionApps = new PermissionApps(getActivity(), groupName, null);
            PermissionAppsFragment.bindUi(this, permissionApps);
        }

        @Override // com.xiaopeng.appinstaller.permission.model.PermissionApps.Callback
        public void onPermissionsLoaded(PermissionApps permissionApps) {
            setPreferenceScreen();
            this.mOuterFragment.setOnPermissionsLoadedListener(null);
        }

        private void setPreferenceScreen() {
            setPreferenceScreen(this.mOuterFragment.mExtraScreen);
            setLoading(false, true);
        }
    }
}
