package com.xiaopeng.appinstaller.permission.ui;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import com.xiaopeng.appinstaller.permission.ui.handheld.AllAppPermissionsFragment;
import com.xiaopeng.appinstaller.permission.ui.handheld.AppPermissionsFragment;
import com.xiaopeng.appinstaller.permission.ui.handheld.ManageStandardPermissionsFragment;
import com.xiaopeng.appinstaller.permission.ui.handheld.PermissionAppsFragment;
/* loaded from: classes.dex */
public final class ManagePermissionsActivity extends OverlayTouchActivity {
    @Override // com.xiaopeng.appinstaller.permission.ui.OverlayTouchActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        Fragment fragment;
        Fragment fragment2;
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            return;
        }
        String action = getIntent().getAction();
        char c = 65535;
        int hashCode = action.hashCode();
        if (hashCode != -1168603379) {
            if (hashCode != 1685512017) {
                if (hashCode == 1861372431 && action.equals("android.intent.action.MANAGE_PERMISSIONS")) {
                    c = 0;
                }
            } else if (action.equals("android.intent.action.MANAGE_APP_PERMISSIONS")) {
                c = 1;
            }
        } else if (action.equals("android.intent.action.MANAGE_PERMISSION_APPS")) {
            c = 2;
        }
        switch (c) {
            case 0:
                fragment = ManageStandardPermissionsFragment.newInstance();
                break;
            case 1:
                String packageName = getIntent().getStringExtra("android.intent.extra.PACKAGE_NAME");
                if (packageName == null) {
                    Log.i("ManagePermissionsActivity", "Missing mandatory argument EXTRA_PACKAGE_NAME");
                    finish();
                    return;
                }
                boolean allPermissions = getIntent().getBooleanExtra("com.xiaopeng.appinstaller.extra.ALL_PERMISSIONS", false);
                if (allPermissions) {
                    fragment2 = AllAppPermissionsFragment.newInstance(packageName);
                } else {
                    fragment2 = AppPermissionsFragment.newInstance(packageName);
                }
                fragment = fragment2;
                break;
            case 2:
                String permissionName = getIntent().getStringExtra("android.intent.extra.PERMISSION_NAME");
                if (permissionName == null) {
                    Log.i("ManagePermissionsActivity", "Missing mandatory argument EXTRA_PERMISSION_NAME");
                    finish();
                    return;
                }
                fragment = PermissionAppsFragment.newInstance(permissionName);
                break;
            default:
                Log.w("ManagePermissionsActivity", "Unrecognized action " + action);
                finish();
                return;
        }
        getFragmentManager().beginTransaction().replace(16908290, fragment).commit();
    }

    @Override // android.app.Activity
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
}
