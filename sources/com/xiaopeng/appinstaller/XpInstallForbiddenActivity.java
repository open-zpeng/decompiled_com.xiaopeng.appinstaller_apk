package com.xiaopeng.appinstaller;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import java.io.File;
import java.io.IOException;
import xiaopeng.widget.BuildConfig;
/* loaded from: classes.dex */
public class XpInstallForbiddenActivity extends DialogActivityAbs {
    private static final Uri APP_STORE_PROVIDER_URI = Uri.parse("content://appstore/remote_states");
    private String mCallingPackage = null;
    private File mStagedFile;
    private StagingAsyncTask mStagingTask;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.appinstaller.DialogActivityAbs, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        this.mCallingPackage = intent.getStringExtra("EXTRA_CALLING_PACKAGE");
        this.mDialogBtn1.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.appinstaller.XpInstallForbiddenActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                XpInstallForbiddenActivity.this.finish();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.appinstaller.DialogActivityAbs, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        Log.d("XpInstallForbiddenActivity", "onResume callingPackage:" + this.mCallingPackage);
        if (this.mStagingTask == null) {
            if (this.mStagedFile == null) {
                try {
                    this.mStagedFile = TemporaryFileManager.getStagedFile(this);
                } catch (IOException e) {
                    showError();
                    return;
                }
            }
            showLoading();
            this.mStagingTask = new StagingAsyncTask();
            this.mStagingTask.execute(getIntent().getData());
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        if (this.mStagingTask != null) {
            this.mStagingTask.cancel(true);
        }
        super.onDestroy();
        Log.d("XpInstallForbiddenActivity", "onDestroy callingPackage:" + this.mCallingPackage);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showError() {
        Log.d("XpInstallForbiddenActivity", "showError");
        Intent result = new Intent();
        result.putExtra("android.intent.extra.INSTALL_RESULT", -2);
        setResult(1, result);
        this.mDialogTitle.setText(R.string.install_failed);
        this.mDialogMsg.setText(R.string.Parse_error_dlg_text);
        this.mDialogBtn1.setText(R.string.btn_dismiss);
        this.mDialogBtn1.setVisibility(0);
        this.mDialogBtn1.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.appinstaller.XpInstallForbiddenActivity.2
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                XpInstallForbiddenActivity.this.finish();
            }
        });
        this.mDialogBtn2.setText(BuildConfig.FLAVOR);
        this.mDialogBtn2.setVisibility(8);
        this.mDialogBtn2.setOnClickListener(null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:22:0x0084  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public boolean queryHasUpdate(java.lang.String r9) {
        /*
            r8 = this;
            java.lang.String r0 = "XpInstallForbiddenActivity"
            java.lang.StringBuilder r1 = new java.lang.StringBuilder
            r1.<init>()
            java.lang.String r2 = "queryHasUpdate start: pn="
            r1.append(r2)
            r1.append(r9)
            java.lang.String r1 = r1.toString()
            android.util.Log.d(r0, r1)
            android.content.ContentResolver r2 = r8.getContentResolver()
            android.net.Uri r3 = com.xiaopeng.appinstaller.XpInstallForbiddenActivity.APP_STORE_PROVIDER_URI
            java.lang.String r5 = "packageName LIKE ?"
            r0 = 1
            java.lang.String[] r6 = new java.lang.String[r0]
            r1 = 0
            r6[r1] = r9
            r4 = 0
            r7 = 0
            android.database.Cursor r2 = r2.query(r3, r4, r5, r6, r7)
            r3 = 0
            if (r2 == 0) goto L6c
            int r4 = r2.getCount()     // Catch: java.lang.Throwable -> L69
            if (r4 == 0) goto L6c
            boolean r4 = r2.moveToFirst()     // Catch: java.lang.Throwable -> L69
            if (r4 == 0) goto L82
            r4 = 2
            int r4 = r2.getInt(r4)     // Catch: java.lang.Throwable -> L69
            if (r4 <= 0) goto L41
            goto L42
        L41:
            r0 = r1
        L42:
            java.lang.String r1 = "XpInstallForbiddenActivity"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L69
            r4.<init>()     // Catch: java.lang.Throwable -> L69
            java.lang.String r5 = "queryHasUpdate finish: pn="
            r4.append(r5)     // Catch: java.lang.Throwable -> L69
            r4.append(r9)     // Catch: java.lang.Throwable -> L69
            java.lang.String r5 = " update="
            r4.append(r5)     // Catch: java.lang.Throwable -> L69
            r4.append(r0)     // Catch: java.lang.Throwable -> L69
            java.lang.String r4 = r4.toString()     // Catch: java.lang.Throwable -> L69
            android.util.Log.d(r1, r4)     // Catch: java.lang.Throwable -> L69
            if (r2 == 0) goto L66
            $closeResource(r3, r2)
        L66:
            return r0
        L67:
            r0 = move-exception
            goto L89
        L69:
            r0 = move-exception
            r3 = r0
            goto L88
        L6c:
            java.lang.String r0 = "XpInstallForbiddenActivity"
            java.lang.StringBuilder r4 = new java.lang.StringBuilder     // Catch: java.lang.Throwable -> L69
            r4.<init>()     // Catch: java.lang.Throwable -> L69
            java.lang.String r5 = "queryHasUpdate finish: no data relative to "
            r4.append(r5)     // Catch: java.lang.Throwable -> L69
            r4.append(r9)     // Catch: java.lang.Throwable -> L69
            java.lang.String r4 = r4.toString()     // Catch: java.lang.Throwable -> L69
            android.util.Log.d(r0, r4)     // Catch: java.lang.Throwable -> L69
        L82:
            if (r2 == 0) goto L87
            $closeResource(r3, r2)
        L87:
            return r1
        L88:
            throw r3     // Catch: java.lang.Throwable -> L67
        L89:
            if (r2 == 0) goto L8e
            $closeResource(r3, r2)
        L8e:
            throw r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.appinstaller.XpInstallForbiddenActivity.queryHasUpdate(java.lang.String):boolean");
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) {
        if (x0 == null) {
            x1.close();
            return;
        }
        try {
            x1.close();
        } catch (Throwable th) {
            x0.addSuppressed(th);
        }
    }

    /* loaded from: classes.dex */
    private final class StagingAsyncTask extends AsyncTask<Uri, Void, Boolean> {
        private boolean mHasUpdate;
        private String mInstallingPackage;
        private boolean mIsUpdate;
        private Uri mPackgeUri;

        private StagingAsyncTask() {
            this.mIsUpdate = false;
            this.mHasUpdate = false;
            this.mInstallingPackage = null;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        /* JADX WARN: Code restructure failed: missing block: B:27:0x005f, code lost:
            $closeResource(null, r4);
         */
        /* JADX WARN: Code restructure failed: missing block: B:28:0x0062, code lost:
            if (r2 == null) goto L29;
         */
        /* JADX WARN: Code restructure failed: missing block: B:29:0x0064, code lost:
            $closeResource(null, r2);
         */
        /* JADX WARN: Code restructure failed: missing block: B:30:0x0067, code lost:
            return false;
         */
        @Override // android.os.AsyncTask
        /*
            Code decompiled incorrectly, please refer to instructions dump.
            To view partially-correct add '--show-bad-code' argument
        */
        public java.lang.Boolean doInBackground(android.net.Uri... r14) {
            /*
                Method dump skipped, instructions count: 301
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.appinstaller.XpInstallForbiddenActivity.StagingAsyncTask.doInBackground(android.net.Uri[]):java.lang.Boolean");
        }

        private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) {
            if (x0 == null) {
                x1.close();
                return;
            }
            try {
                x1.close();
            } catch (Throwable th) {
                x0.addSuppressed(th);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean success) {
            if (isCancelled()) {
                Log.i("XpInstallForbiddenActivity", "StagingAsyncTask onPostExecute canceled. installing=" + this.mInstallingPackage + ", hasUpdate=" + this.mHasUpdate);
                return;
            }
            XpInstallForbiddenActivity.this.hideLoading();
            if (success.booleanValue()) {
                Log.d("XpInstallForbiddenActivity", "StagingAsyncTask success " + this.mPackgeUri);
                if (this.mIsUpdate) {
                    XpInstallForbiddenActivity.this.mDialogTitle.setText(R.string.update_forbidden_title);
                    if (this.mHasUpdate) {
                        XpInstallForbiddenActivity.this.mDialogMsg.setText(R.string.update_forbidden_msg1);
                        XpInstallForbiddenActivity.this.mDialogBtn1.setText(R.string.btn_go_update);
                        XpInstallForbiddenActivity.this.mDialogBtn1.setVisibility(0);
                        XpInstallForbiddenActivity.this.mDialogBtn1.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.appinstaller.XpInstallForbiddenActivity.StagingAsyncTask.1
                            @Override // android.view.View.OnClickListener
                            public void onClick(View v) {
                                Utils.startAppStoreDetail(XpInstallForbiddenActivity.this, StagingAsyncTask.this.mInstallingPackage);
                                XpInstallForbiddenActivity.this.finish();
                            }
                        });
                        XpInstallForbiddenActivity.this.mDialogBtn2.setText(R.string.btn_dismiss);
                        XpInstallForbiddenActivity.this.mDialogBtn2.setVisibility(0);
                        XpInstallForbiddenActivity.this.mDialogBtn2.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.appinstaller.XpInstallForbiddenActivity.StagingAsyncTask.2
                            @Override // android.view.View.OnClickListener
                            public void onClick(View v) {
                                XpInstallForbiddenActivity.this.finish();
                            }
                        });
                    } else {
                        XpInstallForbiddenActivity.this.mDialogMsg.setText(R.string.update_forbidden_msg);
                        XpInstallForbiddenActivity.this.mDialogBtn1.setText(R.string.btn_dismiss);
                        XpInstallForbiddenActivity.this.mDialogBtn1.setVisibility(0);
                        XpInstallForbiddenActivity.this.mDialogBtn1.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.appinstaller.XpInstallForbiddenActivity.StagingAsyncTask.3
                            @Override // android.view.View.OnClickListener
                            public void onClick(View v) {
                                XpInstallForbiddenActivity.this.finish();
                            }
                        });
                        XpInstallForbiddenActivity.this.mDialogBtn2.setText(BuildConfig.FLAVOR);
                        XpInstallForbiddenActivity.this.mDialogBtn2.setVisibility(8);
                        XpInstallForbiddenActivity.this.mDialogBtn2.setOnClickListener(null);
                    }
                } else {
                    XpInstallForbiddenActivity.this.mDialogTitle.setText(R.string.install_failed);
                    XpInstallForbiddenActivity.this.mDialogMsg.setText(R.string.unknown_apps_admin_dlg_text);
                    XpInstallForbiddenActivity.this.mDialogBtn1.setText(R.string.btn_dismiss);
                    XpInstallForbiddenActivity.this.mDialogBtn1.setVisibility(0);
                    XpInstallForbiddenActivity.this.mDialogBtn1.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.appinstaller.XpInstallForbiddenActivity.StagingAsyncTask.4
                        @Override // android.view.View.OnClickListener
                        public void onClick(View v) {
                            XpInstallForbiddenActivity.this.finish();
                        }
                    });
                    XpInstallForbiddenActivity.this.mDialogBtn2.setText(BuildConfig.FLAVOR);
                    XpInstallForbiddenActivity.this.mDialogBtn2.setVisibility(8);
                    XpInstallForbiddenActivity.this.mDialogBtn2.setOnClickListener(null);
                }
            } else {
                Log.w("XpInstallForbiddenActivity", "StagingAsyncTask error " + this.mPackgeUri);
                XpInstallForbiddenActivity.this.showError();
            }
            if (XpInstallForbiddenActivity.this.mStagedFile != null) {
                XpInstallForbiddenActivity.this.mStagedFile.delete();
            }
        }

        @Override // android.os.AsyncTask
        protected void onCancelled() {
            Log.i("XpInstallForbiddenActivity", "StagingAsyncTask onCancelled. uri=" + this.mPackgeUri + ", hasUpdate=" + this.mHasUpdate);
            if (XpInstallForbiddenActivity.this.mStagedFile != null) {
                XpInstallForbiddenActivity.this.mStagedFile.delete();
            }
        }

        public PackageInfo queryPackageInfo(String packageName) {
            try {
                return XpInstallForbiddenActivity.this.getPackageManager().getPackageInfo(packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                return null;
            }
        }
    }
}
