package com.xiaopeng.appinstaller.permission.ui.handheld;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceGroup;
import android.util.IconDrawableFactory;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import com.xiaopeng.appinstaller.R;
import com.xiaopeng.appinstaller.permission.model.AppPermissionGroup;
import com.xiaopeng.appinstaller.permission.model.Permission;
import com.xiaopeng.appinstaller.permission.ui.handheld.AllAppPermissionsFragment;
import com.xiaopeng.appinstaller.permission.utils.ArrayUtils;
import com.xiaopeng.appinstaller.permission.utils.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
/* loaded from: classes.dex */
public final class AllAppPermissionsFragment extends SettingsWithHeader {
    private List<AppPermissionGroup> mGroups;

    public static AllAppPermissionsFragment newInstance(String packageName) {
        return newInstance(packageName, null);
    }

    public static AllAppPermissionsFragment newInstance(String packageName, String filterGroup) {
        AllAppPermissionsFragment instance = new AllAppPermissionsFragment();
        Bundle arguments = new Bundle();
        arguments.putString("android.intent.extra.PACKAGE_NAME", packageName);
        arguments.putString("com.xiaopeng.appinstaller.extra.FILTER_GROUP", filterGroup);
        instance.setArguments(arguments);
        return instance;
    }

    @Override // android.preference.PreferenceFragment, android.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        ActionBar ab = getActivity().getActionBar();
        if (ab != null) {
            if (getArguments().getString("com.xiaopeng.appinstaller.extra.FILTER_GROUP") == null) {
                ab.setTitle(R.string.all_permissions);
            } else {
                ab.setTitle(R.string.app_permissions);
            }
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override // android.app.Fragment
    public void onResume() {
        super.onResume();
        updateUi();
    }

    @Override // android.app.Fragment
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 16908332) {
            getFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateUi() {
        Intent infoIntent;
        if (getPreferenceScreen() != null) {
            getPreferenceScreen().removeAll();
        }
        addPreferencesFromResource(R.xml.all_permissions);
        PreferenceGroup otherGroup = (PreferenceGroup) findPreference("other_perms");
        ArrayList<Preference> prefs = new ArrayList<>();
        prefs.add(otherGroup);
        String pkg = getArguments().getString("android.intent.extra.PACKAGE_NAME");
        String filterGroup = getArguments().getString("com.xiaopeng.appinstaller.extra.FILTER_GROUP");
        otherGroup.removeAll();
        PackageManager pm = getContext().getPackageManager();
        int i = 4096;
        int i2 = 0;
        try {
            PackageInfo info = pm.getPackageInfo(pkg, 4096);
            ApplicationInfo appInfo = info.applicationInfo;
            Drawable icon = IconDrawableFactory.newInstance(getContext()).getBadgedIcon(appInfo);
            CharSequence label = appInfo.loadLabel(pm);
            if (!getActivity().getIntent().getBooleanExtra("hideInfoButton", false)) {
                infoIntent = new Intent("android.settings.APPLICATION_DETAILS_SETTINGS").setData(Uri.fromParts("package", pkg, null));
            } else {
                infoIntent = null;
            }
            setHeader(icon, label, infoIntent);
            if (info.requestedPermissions != null) {
                int i3 = 0;
                while (true) {
                    int i4 = i3;
                    if (i4 >= info.requestedPermissions.length) {
                        break;
                    }
                    try {
                        PermissionInfo perm = pm.getPermissionInfo(info.requestedPermissions[i4], i2);
                        if ((perm.flags & 1073741824) != 0 && (perm.flags & 2) == 0 && ((!appInfo.isInstantApp() || (perm.protectionLevel & i) != 0) && (appInfo.targetSdkVersion >= 23 || (perm.protectionLevel & 8192) == 0))) {
                            if ((perm.protectionLevel & 15) == 1) {
                                PackageItemInfo group = getGroup(perm.group, pm);
                                if (group == null) {
                                    group = perm;
                                }
                                if (filterGroup == null || group.name.equals(filterGroup)) {
                                    PreferenceGroup pref = findOrCreate(group, pm, prefs);
                                    pref.addPreference(getPreference(info, perm, group, pm));
                                }
                            } else if (filterGroup == null && (perm.protectionLevel & 15) == 0) {
                                otherGroup.addPreference(getPreference(info, perm, getGroup(perm.group, pm), pm));
                            }
                            if (filterGroup != null) {
                                getPreferenceScreen().removePreference(otherGroup);
                            }
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        Log.e("AllAppPermissionsFragment", "Can't get permission info for " + info.requestedPermissions[i4], e);
                    }
                    i3 = i4 + 1;
                    i = 4096;
                    i2 = 0;
                }
            }
        } catch (PackageManager.NameNotFoundException e2) {
            Log.e("AllAppPermissionsFragment", "Problem getting package info for " + pkg, e2);
        }
        Collections.sort(prefs, new Comparator<Preference>() { // from class: com.xiaopeng.appinstaller.permission.ui.handheld.AllAppPermissionsFragment.1
            @Override // java.util.Comparator
            public int compare(Preference lhs, Preference rhs) {
                String lKey = lhs.getKey();
                String rKey = rhs.getKey();
                if (lKey.equals("other_perms")) {
                    return 1;
                }
                if (rKey.equals("other_perms")) {
                    return -1;
                }
                if (Utils.isModernPermissionGroup(lKey) != Utils.isModernPermissionGroup(rKey)) {
                    return Utils.isModernPermissionGroup(lKey) ? -1 : 1;
                }
                return lhs.getTitle().toString().compareTo(rhs.getTitle().toString());
            }
        });
        int i5 = 0;
        while (true) {
            int i6 = i5;
            if (i6 < prefs.size()) {
                prefs.get(i6).setOrder(i6);
                i5 = i6 + 1;
            } else {
                return;
            }
        }
    }

    private PermissionGroupInfo getGroup(String group, PackageManager pm) {
        try {
            return pm.getPermissionGroupInfo(group, 0);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private PreferenceGroup findOrCreate(PackageItemInfo group, PackageManager pm, ArrayList<Preference> prefs) {
        PreferenceGroup pref = (PreferenceGroup) findPreference(group.name);
        if (pref == null) {
            PreferenceGroup pref2 = new PreferenceCategory(getContext());
            pref2.setKey(group.name);
            pref2.setTitle(group.loadLabel(pm));
            prefs.add(pref2);
            getPreferenceScreen().addPreference(pref2);
            return pref2;
        }
        return pref;
    }

    private Preference getPreference(PackageInfo packageInfo, PermissionInfo perm, PackageItemInfo group, PackageManager pm) {
        Preference pref;
        Drawable icon;
        final boolean mutable = Utils.isPermissionIndividuallyControlled(getContext(), perm.name);
        if (mutable) {
            pref = new MyMultiTargetSwitchPreference(getContext(), perm.name, getPermissionGroup(packageInfo, perm.name));
        } else {
            pref = new Preference(getContext());
        }
        if (perm.icon != 0) {
            icon = perm.loadIcon(pm);
        } else if (group != null && group.icon != 0) {
            icon = group.loadIcon(pm);
        } else {
            icon = getContext().getDrawable(R.drawable.ic_perm_device_info);
        }
        pref.setIcon(Utils.applyTint(getContext(), icon, 16843817));
        pref.setTitle(perm.loadSafeLabel(pm, 20000.0f, 1));
        pref.setSingleLineTitle(false);
        final CharSequence desc = perm.loadDescription(pm);
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.xiaopeng.appinstaller.permission.ui.handheld.-$$Lambda$AllAppPermissionsFragment$zFyQqb6AHD-ZG5kalKf9v6XjnrQ
            @Override // android.preference.Preference.OnPreferenceClickListener
            public final boolean onPreferenceClick(Preference preference) {
                return AllAppPermissionsFragment.lambda$getPreference$0(AllAppPermissionsFragment.this, desc, mutable, preference);
            }
        });
        return pref;
    }

    public static /* synthetic */ boolean lambda$getPreference$0(AllAppPermissionsFragment allAppPermissionsFragment, CharSequence desc, boolean mutable, Preference preference) {
        new AlertDialog.Builder(allAppPermissionsFragment.getContext()).setMessage(desc).setPositiveButton(17039370, (DialogInterface.OnClickListener) null).show();
        return mutable;
    }

    private AppPermissionGroup getPermissionGroup(PackageInfo packageInfo, String permission) {
        AppPermissionGroup appPermissionGroup = null;
        if (this.mGroups != null) {
            int groupCount = this.mGroups.size();
            int i = 0;
            while (true) {
                if (i >= groupCount) {
                    break;
                }
                AppPermissionGroup currentPermissionGroup = this.mGroups.get(i);
                if (!currentPermissionGroup.hasPermission(permission)) {
                    i++;
                } else {
                    appPermissionGroup = currentPermissionGroup;
                    break;
                }
            }
        }
        if (appPermissionGroup == null) {
            appPermissionGroup = AppPermissionGroup.create(getContext(), packageInfo, permission);
            if (this.mGroups == null) {
                this.mGroups = new ArrayList();
            }
            this.mGroups.add(appPermissionGroup);
        }
        return appPermissionGroup;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class MyMultiTargetSwitchPreference extends MultiTargetSwitchPreference {
        MyMultiTargetSwitchPreference(Context context, final String permission, final AppPermissionGroup appPermissionGroup) {
            super(context);
            setChecked(appPermissionGroup.areRuntimePermissionsGranted(new String[]{permission}));
            setSwitchOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.appinstaller.permission.ui.handheld.-$$Lambda$AllAppPermissionsFragment$MyMultiTargetSwitchPreference$FPhKo0_T-GmCwwdQwC5lng8PJzY
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    AllAppPermissionsFragment.MyMultiTargetSwitchPreference.lambda$new$0(AppPermissionGroup.this, permission, view);
                }
            });
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static /* synthetic */ void lambda$new$0(AppPermissionGroup appPermissionGroup, String permission, View v) {
            Switch switchView = (Switch) v;
            if (switchView.isChecked()) {
                appPermissionGroup.grantRuntimePermissions(false, new String[]{permission});
                if (appPermissionGroup.doesSupportRuntimePermissions()) {
                    String[] revokedPermissionsToFix = null;
                    int permissionCount = appPermissionGroup.getPermissions().size();
                    int grantedCount = 0;
                    for (int grantedCount2 = 0; grantedCount2 < permissionCount; grantedCount2++) {
                        Permission current = appPermissionGroup.getPermissions().get(grantedCount2);
                        if (!current.isGranted()) {
                            if (!current.isUserFixed()) {
                                revokedPermissionsToFix = ArrayUtils.appendString(revokedPermissionsToFix, current.getName());
                            }
                        } else {
                            grantedCount++;
                        }
                    }
                    if (revokedPermissionsToFix != null) {
                        appPermissionGroup.revokeRuntimePermissions(true, revokedPermissionsToFix);
                        return;
                    } else if (appPermissionGroup.getPermissions().size() == grantedCount) {
                        appPermissionGroup.grantRuntimePermissions(false);
                        return;
                    } else {
                        return;
                    }
                }
                return;
            }
            appPermissionGroup.revokeRuntimePermissions(true, new String[]{permission});
            if (appPermissionGroup.doesSupportRuntimePermissions() && !appPermissionGroup.areRuntimePermissionsGranted()) {
                appPermissionGroup.revokeRuntimePermissions(false);
            }
        }
    }
}
