package com.xiaopeng.appinstaller.permission.ui;

import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import com.xiaopeng.appinstaller.R;
import com.xiaopeng.appinstaller.permission.ui.ConfirmActionDialogFragment;
import com.xiaopeng.appinstaller.permission.ui.handheld.ReviewPermissionsFragment;
/* loaded from: classes.dex */
public final class ReviewPermissionsActivity extends Activity implements ConfirmActionDialogFragment.OnActionConfirmedListener {
    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PackageInfo packageInfo = getTargetPackageInfo();
        if (packageInfo == null) {
            finish();
            return;
        }
        setContentView(R.layout.review_permissions);
        if (getFragmentManager().findFragmentById(R.id.preferences_frame) == null) {
            getFragmentManager().beginTransaction().add(R.id.preferences_frame, ReviewPermissionsFragment.newInstance(packageInfo)).commit();
        }
    }

    @Override // com.xiaopeng.appinstaller.permission.ui.ConfirmActionDialogFragment.OnActionConfirmedListener
    public void onActionConfirmed(String action) {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.preferences_frame);
        if (fragment instanceof ConfirmActionDialogFragment.OnActionConfirmedListener) {
            ((ConfirmActionDialogFragment.OnActionConfirmedListener) fragment).onActionConfirmed(action);
        }
    }

    private PackageInfo getTargetPackageInfo() {
        String packageName = getIntent().getStringExtra("android.intent.extra.PACKAGE_NAME");
        if (TextUtils.isEmpty(packageName)) {
            return null;
        }
        try {
            return getPackageManager().getPackageInfo(packageName, 4096);
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }
}
