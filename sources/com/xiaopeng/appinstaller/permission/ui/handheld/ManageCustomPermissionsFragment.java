package com.xiaopeng.appinstaller.permission.ui.handheld;

import android.os.Bundle;
import android.preference.Preference;
import android.view.MenuItem;
import com.xiaopeng.appinstaller.R;
/* loaded from: classes.dex */
public class ManageCustomPermissionsFragment extends ManagePermissionsFragment {
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

    @Override // android.preference.PreferenceFragment, android.app.Fragment
    public void onStart() {
        super.onStart();
        getActivity().setTitle(R.string.additional_permissions);
    }

    @Override // android.app.Fragment
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == 16908332) {
            getFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override // com.xiaopeng.appinstaller.permission.ui.handheld.ManagePermissionsFragment
    protected void updatePermissionsUi() {
        updatePermissionsUi(false);
    }
}
