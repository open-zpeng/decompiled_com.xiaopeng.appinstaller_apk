package com.xiaopeng.appinstaller.permission.ui.handheld;

import android.app.ActionBar;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.util.ArraySet;
import android.util.Log;
import com.xiaopeng.appinstaller.R;
import com.xiaopeng.appinstaller.permission.model.PermissionApps;
import com.xiaopeng.appinstaller.permission.model.PermissionGroup;
import com.xiaopeng.appinstaller.permission.model.PermissionGroups;
import com.xiaopeng.appinstaller.permission.utils.Utils;
import java.util.List;
/* loaded from: classes.dex */
abstract class ManagePermissionsFragment extends PermissionsFrameFragment implements Preference.OnPreferenceClickListener, PermissionGroups.PermissionsGroupsChangeCallback {
    private ArraySet<String> mLauncherPkgs;
    private PermissionGroups mPermissions;

    protected abstract void updatePermissionsUi();

    @Override // android.preference.PreferenceFragment, android.app.Fragment
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setLoading(true, false);
        setHasOptionsMenu(true);
        ActionBar ab = getActivity().getActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        this.mLauncherPkgs = Utils.getLauncherPackages(getContext());
        this.mPermissions = new PermissionGroups(getContext(), getLoaderManager(), this);
    }

    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        PermissionGroup group = this.mPermissions.getGroup(key);
        if (group == null) {
            return false;
        }
        Intent intent = new Intent("android.intent.action.MANAGE_PERMISSION_APPS").putExtra("android.intent.extra.PERMISSION_NAME", key);
        try {
            getActivity().startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            Log.w("ManagePermissionsFragment", "No app to handle " + intent);
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public PermissionGroups getPermissions() {
        return this.mPermissions;
    }

    public void onPermissionGroupsChanged() {
        updatePermissionsUi();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public PreferenceScreen updatePermissionsUi(boolean addSystemPermissions) {
        Context context = getActivity();
        if (context == null) {
            return null;
        }
        List<PermissionGroup> groups = this.mPermissions.getGroups();
        PreferenceScreen screen = getPreferenceScreen();
        if (screen == null) {
            screen = getPreferenceManager().createPreferenceScreen(getActivity());
            setPreferenceScreen(screen);
        } else {
            screen.removeAll();
        }
        new PermissionApps.PmCache(getContext().getPackageManager());
        for (PermissionGroup group : groups) {
            boolean isSystemPermission = group.getDeclaringPackage().equals("android");
            if (addSystemPermissions == isSystemPermission) {
                Preference preference = findPreference(group.getName());
                if (preference == null) {
                    preference = new Preference(context);
                    preference.setOnPreferenceClickListener(this);
                    preference.setKey(group.getName());
                    preference.setIcon(Utils.applyTint(context, group.getIcon(), 16843817));
                    preference.setTitle(group.getLabel());
                    preference.setSummary(" ");
                    preference.setPersistent(false);
                    screen.addPreference(preference);
                }
                preference.setSummary(getString(R.string.app_permissions_group_summary, new Object[]{Integer.valueOf(group.getGranted()), Integer.valueOf(group.getTotal())}));
            }
        }
        if (screen.getPreferenceCount() != 0) {
            setLoading(false, true);
        }
        return screen;
    }
}
