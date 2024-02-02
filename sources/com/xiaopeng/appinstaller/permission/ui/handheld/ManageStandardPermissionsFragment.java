package com.xiaopeng.appinstaller.permission.ui.handheld;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.view.MenuItem;
import com.xiaopeng.appinstaller.R;
import com.xiaopeng.appinstaller.permission.model.PermissionGroup;
import com.xiaopeng.appinstaller.permission.utils.Utils;
import java.util.List;
/* loaded from: classes.dex */
public final class ManageStandardPermissionsFragment extends ManagePermissionsFragment {
    @Override // com.xiaopeng.appinstaller.permission.ui.handheld.ManagePermissionsFragment, android.preference.PreferenceFragment, android.app.Fragment
    public /* bridge */ /* synthetic */ void onCreate(Bundle bundle) {
        super.onCreate(bundle);
    }

    @Override // com.xiaopeng.appinstaller.permission.ui.handheld.ManagePermissionsFragment, com.xiaopeng.appinstaller.permission.model.PermissionGroups.PermissionsGroupsChangeCallback
    public /* bridge */ /* synthetic */ void onPermissionGroupsChanged() {
        super.onPermissionGroupsChanged();
    }

    @Override // com.xiaopeng.appinstaller.permission.ui.handheld.ManagePermissionsFragment, android.preference.Preference.OnPreferenceClickListener
    public /* bridge */ /* synthetic */ boolean onPreferenceClick(Preference preference) {
        return super.onPreferenceClick(preference);
    }

    public static ManageStandardPermissionsFragment newInstance() {
        return new ManageStandardPermissionsFragment();
    }

    @Override // android.preference.PreferenceFragment, android.app.Fragment
    public void onStart() {
        super.onStart();
        getActivity().setTitle(R.string.app_permissions);
    }

    @Override // android.app.Fragment
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 16908332) {
            getActivity().finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override // com.xiaopeng.appinstaller.permission.ui.handheld.ManagePermissionsFragment
    protected void updatePermissionsUi() {
        PreferenceScreen screen = updatePermissionsUi(true);
        if (screen == null) {
            return;
        }
        List<PermissionGroup> groups = getPermissions().getGroups();
        int numExtraPermissions = 0;
        for (PermissionGroup group : groups) {
            if (!group.getDeclaringPackage().equals("android")) {
                numExtraPermissions++;
            }
        }
        Preference additionalPermissionsPreference = screen.findPreference("extra_prefs_key");
        if (numExtraPermissions == 0) {
            if (additionalPermissionsPreference != null) {
                screen.removePreference(additionalPermissionsPreference);
                return;
            }
            return;
        }
        if (additionalPermissionsPreference == null) {
            additionalPermissionsPreference = new Preference(getActivity());
            additionalPermissionsPreference.setKey("extra_prefs_key");
            additionalPermissionsPreference.setIcon(Utils.applyTint(getActivity(), (int) R.drawable.ic_more_items, 16843817));
            additionalPermissionsPreference.setTitle(R.string.additional_permissions);
            additionalPermissionsPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() { // from class: com.xiaopeng.appinstaller.permission.ui.handheld.-$$Lambda$ManageStandardPermissionsFragment$jH2UE0dCQbMYzzoW1XOxI42mJmI
                @Override // android.preference.Preference.OnPreferenceClickListener
                public final boolean onPreferenceClick(Preference preference) {
                    return ManageStandardPermissionsFragment.lambda$updatePermissionsUi$0(ManageStandardPermissionsFragment.this, preference);
                }
            });
            screen.addPreference(additionalPermissionsPreference);
        }
        additionalPermissionsPreference.setSummary(getResources().getQuantityString(R.plurals.additional_permissions_more, numExtraPermissions, Integer.valueOf(numExtraPermissions)));
    }

    public static /* synthetic */ boolean lambda$updatePermissionsUi$0(ManageStandardPermissionsFragment manageStandardPermissionsFragment, Preference preference) {
        ManageCustomPermissionsFragment frag = new ManageCustomPermissionsFragment();
        frag.setTargetFragment(manageStandardPermissionsFragment, 0);
        FragmentTransaction ft = manageStandardPermissionsFragment.getFragmentManager().beginTransaction();
        ft.replace(16908290, frag);
        ft.addToBackStack(null);
        ft.commit();
        return true;
    }
}
