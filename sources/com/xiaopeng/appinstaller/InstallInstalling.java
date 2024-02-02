package com.xiaopeng.appinstaller;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.PackageParser;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import com.android.internal.content.PackageHelper;
import com.xiaopeng.appinstaller.EventResultPersister;
import java.io.File;
import java.io.IOException;
/* loaded from: classes.dex */
public class InstallInstalling extends Activity {
    private static final String LOG_TAG = InstallInstalling.class.getSimpleName();
    private Button mCancelButton;
    private int mInstallId;
    private InstallingAsyncTask mInstallingTask;
    private Uri mPackageURI;
    private PackageInstaller.SessionCallback mSessionCallback;
    private int mSessionId;

    static /* synthetic */ int access$200(InstallInstalling x0) {
        return x0.mSessionId;
    }

    static /* synthetic */ Uri access$300(InstallInstalling x0) {
        return x0.mPackageURI;
    }

    static /* synthetic */ String access$400() {
        return LOG_TAG;
    }

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.install_installing);
        ApplicationInfo appInfo = (ApplicationInfo) getIntent().getParcelableExtra("com.android.packageinstaller.applicationInfo");
        this.mPackageURI = getIntent().getData();
        if ("package".equals(this.mPackageURI.getScheme())) {
            try {
                getPackageManager().installExistingPackage(appInfo.packageName);
                launchSuccess();
                return;
            } catch (PackageManager.NameNotFoundException e) {
                launchFailure(-110, null);
                return;
            }
        }
        File sourceFile = new File(this.mPackageURI.getPath());
        PackageUtil.initSnippetForNewApp(this, PackageUtil.getAppSnippet(this, appInfo, sourceFile), R.id.app_snippet);
        if (savedInstanceState != null) {
            this.mSessionId = savedInstanceState.getInt("com.android.packageinstaller.SESSION_ID");
            this.mInstallId = savedInstanceState.getInt("com.android.packageinstaller.INSTALL_ID");
            try {
                InstallEventReceiver.addObserver(this, this.mInstallId, new EventResultPersister.EventResultObserver() { // from class: com.xiaopeng.appinstaller.-$$Lambda$InstallInstalling$39oWifKjrrvF3LTlmmwNU7PAjOk
                    @Override // com.xiaopeng.appinstaller.EventResultPersister.EventResultObserver
                    public final void onResult(int i, int i2, String str) {
                        InstallInstalling.this.launchFinishBasedOnResult(i, i2, str);
                    }
                });
            } catch (EventResultPersister.OutOfIdsException e2) {
            }
        } else {
            PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(1);
            params.installFlags = 16384;
            params.referrerUri = (Uri) getIntent().getParcelableExtra("android.intent.extra.REFERRER");
            params.originatingUri = (Uri) getIntent().getParcelableExtra("android.intent.extra.ORIGINATING_URI");
            params.originatingUid = getIntent().getIntExtra("android.intent.extra.ORIGINATING_UID", -1);
            params.installerPackageName = getIntent().getStringExtra("android.intent.extra.INSTALLER_PACKAGE_NAME");
            File file = new File(this.mPackageURI.getPath());
            try {
                PackageParser.PackageLite pkg = PackageParser.parsePackageLite(file, 0);
                params.setAppPackageName(pkg.packageName);
                params.setInstallLocation(pkg.installLocation);
                params.setSize(PackageHelper.calculateInstalledSize(pkg, false, params.abiOverride));
            } catch (PackageParser.PackageParserException e3) {
                String str = LOG_TAG;
                Log.e(str, "Cannot parse package " + file + ". Assuming defaults.");
                String str2 = LOG_TAG;
                Log.e(str2, "Cannot calculate installed size " + file + ". Try only apk size.");
                params.setSize(file.length());
            } catch (IOException e4) {
                String str3 = LOG_TAG;
                Log.e(str3, "Cannot calculate installed size " + file + ". Try only apk size.");
                params.setSize(file.length());
            }
            try {
                this.mInstallId = InstallEventReceiver.addObserver(this, Integer.MIN_VALUE, new EventResultPersister.EventResultObserver() { // from class: com.xiaopeng.appinstaller.-$$Lambda$InstallInstalling$39oWifKjrrvF3LTlmmwNU7PAjOk
                    @Override // com.xiaopeng.appinstaller.EventResultPersister.EventResultObserver
                    public final void onResult(int i, int i2, String str4) {
                        InstallInstalling.this.launchFinishBasedOnResult(i, i2, str4);
                    }
                });
            } catch (EventResultPersister.OutOfIdsException e5) {
                launchFailure(-110, null);
            }
            try {
                this.mSessionId = getPackageManager().getPackageInstaller().createSession(params);
            } catch (IOException e6) {
                launchFailure(-110, null);
            }
        }
        this.mCancelButton = (Button) findViewById(R.id.cancel_button);
        this.mCancelButton.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.appinstaller.-$$Lambda$InstallInstalling$7pc9mh4Z07nGLK3pp28-8M_d-fU
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                InstallInstalling.lambda$onCreate$0(InstallInstalling.this, view);
            }
        });
        this.mSessionCallback = new InstallSessionCallback();
    }

    public static /* synthetic */ void lambda$onCreate$0(InstallInstalling installInstalling, View view) {
        if (installInstalling.mInstallingTask != null) {
            installInstalling.mInstallingTask.cancel(true);
        }
        if (installInstalling.mSessionId > 0) {
            installInstalling.getPackageManager().getPackageInstaller().abandonSession(installInstalling.mSessionId);
            installInstalling.mSessionId = 0;
        }
        installInstalling.setResult(0);
        installInstalling.finish();
    }

    private void launchSuccess() {
        Intent successIntent = new Intent(getIntent());
        successIntent.setClass(this, InstallSuccess.class);
        successIntent.addFlags(33554432);
        startActivity(successIntent);
        finish();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void launchFailure(int legacyStatus, String statusMessage) {
        Intent failureIntent = new Intent(getIntent());
        failureIntent.setClass(this, InstallFailed.class);
        failureIntent.addFlags(33554432);
        failureIntent.putExtra("android.content.pm.extra.LEGACY_STATUS", legacyStatus);
        failureIntent.putExtra("android.content.pm.extra.STATUS_MESSAGE", statusMessage);
        startActivity(failureIntent);
        finish();
    }

    @Override // android.app.Activity
    protected void onStart() {
        super.onStart();
        getPackageManager().getPackageInstaller().registerSessionCallback(this.mSessionCallback);
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        if (this.mInstallingTask == null) {
            PackageInstaller installer = getPackageManager().getPackageInstaller();
            PackageInstaller.SessionInfo sessionInfo = installer.getSessionInfo(this.mSessionId);
            if (sessionInfo == null || sessionInfo.isActive()) {
                this.mCancelButton.setEnabled(false);
                setFinishOnTouchOutside(false);
                return;
            }
            this.mInstallingTask = new InstallingAsyncTask();
            this.mInstallingTask.execute(new Void[0]);
        }
    }

    @Override // android.app.Activity
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("com.android.packageinstaller.SESSION_ID", this.mSessionId);
        outState.putInt("com.android.packageinstaller.INSTALL_ID", this.mInstallId);
    }

    @Override // android.app.Activity
    public void onBackPressed() {
        if (this.mCancelButton.isEnabled()) {
            super.onBackPressed();
        }
    }

    @Override // android.app.Activity
    protected void onStop() {
        super.onStop();
        getPackageManager().getPackageInstaller().unregisterSessionCallback(this.mSessionCallback);
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        if (this.mInstallingTask != null) {
            this.mInstallingTask.cancel(true);
            synchronized (this.mInstallingTask) {
                while (!this.mInstallingTask.isDone) {
                    try {
                        this.mInstallingTask.wait();
                    } catch (InterruptedException e) {
                        Log.i(LOG_TAG, "Interrupted while waiting for installing task to cancel", e);
                    }
                }
            }
        }
        InstallEventReceiver.removeObserver(this, this.mInstallId);
        super.onDestroy();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void launchFinishBasedOnResult(int statusCode, int legacyStatus, String statusMessage) {
        if (statusCode == 0) {
            launchSuccess();
        } else {
            launchFailure(legacyStatus, statusMessage);
        }
    }

    /* loaded from: classes.dex */
    private class InstallSessionCallback extends PackageInstaller.SessionCallback {
        private InstallSessionCallback() {
        }

        @Override // android.content.pm.PackageInstaller.SessionCallback
        public void onCreated(int sessionId) {
        }

        @Override // android.content.pm.PackageInstaller.SessionCallback
        public void onBadgingChanged(int sessionId) {
        }

        @Override // android.content.pm.PackageInstaller.SessionCallback
        public void onActiveChanged(int sessionId, boolean active) {
        }

        @Override // android.content.pm.PackageInstaller.SessionCallback
        public void onProgressChanged(int sessionId, float progress) {
            if (sessionId == InstallInstalling.this.mSessionId) {
                ProgressBar progressBar = (ProgressBar) InstallInstalling.this.findViewById(R.id.progress_bar);
                progressBar.setMax(Preference.DEFAULT_ORDER);
                progressBar.setProgress((int) (2.1474836E9f * progress));
            }
        }

        @Override // android.content.pm.PackageInstaller.SessionCallback
        public void onFinished(int sessionId, boolean success) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class InstallingAsyncTask extends AsyncTask<Void, Void, PackageInstaller.Session> {
        volatile boolean isDone;

        private InstallingAsyncTask() {
        }

        /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
            jadx.core.utils.exceptions.JadxRuntimeException: Found unreachable blocks
            	at jadx.core.dex.visitors.blocks.DominatorTree.sortBlocks(DominatorTree.java:35)
            	at jadx.core.dex.visitors.blocks.DominatorTree.compute(DominatorTree.java:25)
            	at jadx.core.dex.visitors.blocks.BlockProcessor.computeDominators(BlockProcessor.java:202)
            	at jadx.core.dex.visitors.blocks.BlockProcessor.processBlocksTree(BlockProcessor.java:45)
            	at jadx.core.dex.visitors.blocks.BlockProcessor.visit(BlockProcessor.java:39)
            */
        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public android.content.pm.PackageInstaller.Session doInBackground(java.lang.Void... r15) {
            /*
                Method dump skipped, instructions count: 196
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.appinstaller.InstallInstalling.InstallingAsyncTask.doInBackground(java.lang.Void[]):android.content.pm.PackageInstaller$Session");
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
        public void onPostExecute(PackageInstaller.Session session) {
            if (session == null) {
                InstallInstalling.this.getPackageManager().getPackageInstaller().abandonSession(InstallInstalling.this.mSessionId);
                if (!isCancelled()) {
                    InstallInstalling.this.launchFailure(-2, null);
                    return;
                }
                return;
            }
            Intent broadcastIntent = new Intent("com.android.packageinstaller.ACTION_INSTALL_COMMIT");
            broadcastIntent.setFlags(268435456);
            broadcastIntent.setPackage(InstallInstalling.this.getPackageManager().getPermissionControllerPackageName());
            broadcastIntent.putExtra("EventResultPersister.EXTRA_ID", InstallInstalling.this.mInstallId);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(InstallInstalling.this, InstallInstalling.this.mInstallId, broadcastIntent, 134217728);
            session.commit(pendingIntent.getIntentSender());
            InstallInstalling.this.mCancelButton.setEnabled(false);
            InstallInstalling.this.setFinishOnTouchOutside(false);
        }
    }
}
