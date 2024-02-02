package com.xiaopeng.appinstaller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.xiaopeng.appinstaller.InstallFailed;
import com.xiaopeng.appinstaller.PackageUtil;
import java.io.File;
/* loaded from: classes.dex */
public class InstallFailed extends Activity {
    private static final String LOG_TAG = InstallFailed.class.getSimpleName();
    private CharSequence mLabel;

    private int getExplanationFromErrorCode(int statusCode) {
        String str = LOG_TAG;
        Log.d(str, "Installation status code: " + statusCode);
        if (statusCode != 2) {
            if (statusCode == 7) {
                return R.string.install_failed_incompatible;
            }
            switch (statusCode) {
                case 4:
                    return R.string.install_failed_invalid_apk;
                case 5:
                    return R.string.install_failed_conflict;
                default:
                    return R.string.install_failed;
            }
        }
        return R.string.install_failed_blocked;
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        PackageUtil.AppSnippet as;
        super.onCreate(savedInstanceState);
        int statusCode = getIntent().getIntExtra("android.content.pm.extra.STATUS", 1);
        if (getIntent().getBooleanExtra("android.intent.extra.RETURN_RESULT", false)) {
            int legacyStatus = getIntent().getIntExtra("android.content.pm.extra.LEGACY_STATUS", -110);
            Intent result = new Intent();
            result.putExtra("android.intent.extra.INSTALL_RESULT", legacyStatus);
            setResult(1, result);
            finish();
            return;
        }
        Intent intent = getIntent();
        ApplicationInfo appInfo = (ApplicationInfo) intent.getParcelableExtra("com.android.packageinstaller.applicationInfo");
        Uri packageURI = intent.getData();
        setContentView(R.layout.install_failed);
        PackageManager pm = getPackageManager();
        if ("package".equals(packageURI.getScheme())) {
            as = new PackageUtil.AppSnippet(pm.getApplicationLabel(appInfo), pm.getApplicationIcon(appInfo));
        } else {
            File sourceFile = new File(packageURI.getPath());
            as = PackageUtil.getAppSnippet(this, appInfo, sourceFile);
        }
        this.mLabel = as.label;
        PackageUtil.initSnippetForNewApp(this, as, R.id.app_snippet);
        if (statusCode == 6) {
            new OutOfSpaceDialog().show(getFragmentManager(), "outofspace");
        }
        ((TextView) findViewById(R.id.simple_status)).setText(getExplanationFromErrorCode(statusCode));
        findViewById(R.id.done_button).setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.appinstaller.-$$Lambda$InstallFailed$RSVO4jS0ZGSKwp_0xKTHeIOWaMQ
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                InstallFailed.this.finish();
            }
        });
    }

    /* loaded from: classes.dex */
    public static class OutOfSpaceDialog extends DialogFragment {
        private InstallFailed mActivity;

        @Override // android.app.DialogFragment, android.app.Fragment
        public void onAttach(Context context) {
            super.onAttach(context);
            this.mActivity = (InstallFailed) context;
        }

        @Override // android.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(this.mActivity).setTitle(R.string.out_of_space_dlg_title).setMessage(getString(R.string.out_of_space_dlg_text, new Object[]{this.mActivity.mLabel})).setPositiveButton(R.string.manage_applications, new DialogInterface.OnClickListener() { // from class: com.xiaopeng.appinstaller.-$$Lambda$InstallFailed$OutOfSpaceDialog$m9zj2I4c3Z19tF_KyZWn4dvwXwc
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    InstallFailed.OutOfSpaceDialog.lambda$onCreateDialog$0(InstallFailed.OutOfSpaceDialog.this, dialogInterface, i);
                }
            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.xiaopeng.appinstaller.-$$Lambda$InstallFailed$OutOfSpaceDialog$odw_vnKiISFtoiUTqgrCyMC1fOc
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    InstallFailed.OutOfSpaceDialog.this.mActivity.finish();
                }
            }).create();
        }

        public static /* synthetic */ void lambda$onCreateDialog$0(OutOfSpaceDialog outOfSpaceDialog, DialogInterface dialog, int which) {
            Intent intent = new Intent("android.intent.action.MANAGE_PACKAGE_STORAGE");
            outOfSpaceDialog.startActivity(intent);
            outOfSpaceDialog.mActivity.finish();
        }

        @Override // android.app.DialogFragment, android.content.DialogInterface.OnCancelListener
        public void onCancel(DialogInterface dialog) {
            super.onCancel(dialog);
            this.mActivity.finish();
        }
    }
}
