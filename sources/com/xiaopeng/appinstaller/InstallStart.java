package com.xiaopeng.appinstaller;

import android.app.ActivityManager;
import android.app.AppGlobals;
import android.app.IActivityManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.xiaopeng.app.xpPackageInfo;
/* loaded from: classes.dex */
public class InstallStart extends AppCompatActivity {
    private static final String LOG_TAG = InstallStart.class.getSimpleName();
    private boolean mAbortInstall = false;
    private String mCallingPackage;
    private IActivityManager mIActivityManager;
    private IPackageManager mIPackageManager;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        int sessionId;
        super.onCreate(savedInstanceState);
        this.mIPackageManager = AppGlobals.getPackageManager();
        Intent intent = getIntent();
        String callingPackage = getCallingPackage();
        this.mCallingPackage = callingPackage;
        boolean isSessionInstall = "android.content.pm.action.CONFIRM_PERMISSIONS".equals(intent.getAction());
        if (isSessionInstall) {
            sessionId = intent.getIntExtra("android.content.pm.extra.SESSION_ID", -1);
        } else {
            sessionId = -1;
        }
        if (callingPackage == null && sessionId != -1) {
            PackageInstaller packageInstaller = getPackageManager().getPackageInstaller();
            PackageInstaller.SessionInfo sessionInfo = packageInstaller.getSessionInfo(sessionId);
            callingPackage = sessionInfo != null ? sessionInfo.getInstallerPackageName() : null;
        }
        ApplicationInfo sourceInfo = getSourceInfo(callingPackage);
        int originatingUid = getOriginatingUid(sourceInfo);
        boolean isTrustedSource = false;
        if (sourceInfo != null && (sourceInfo.privateFlags & 8) != 0) {
            isTrustedSource = intent.getBooleanExtra("android.intent.extra.NOT_UNKNOWN_SOURCE", false);
        }
        if (!isTrustedSource && originatingUid != -1) {
            int targetSdkVersion = PackageUtil.getMaxTargetSdkVersionForUid(this, originatingUid);
            if (targetSdkVersion < 0) {
                Log.w(LOG_TAG, "Cannot get target sdk version for uid " + originatingUid);
                this.mAbortInstall = true;
            } else if (targetSdkVersion >= 26 && !declaresAppOpPermission(originatingUid, "android.permission.REQUEST_INSTALL_PACKAGES")) {
                Log.e(LOG_TAG, "Requesting uid " + originatingUid + " needs to declare permission android.permission.REQUEST_INSTALL_PACKAGES");
                this.mAbortInstall = true;
            }
        }
        if (this.mAbortInstall) {
            setResult(0);
            finish();
            return;
        }
        if (callingPackage == null) {
            callingPackage = getPackageNameForUid(originatingUid);
        }
        Intent nextActivity = new Intent(intent);
        nextActivity.setFlags(33554432);
        nextActivity.putExtra("EXTRA_CALLING_PACKAGE", callingPackage);
        nextActivity.putExtra("EXTRA_ORIGINAL_SOURCE_INFO", sourceInfo);
        nextActivity.putExtra("android.intent.extra.ORIGINATING_UID", originatingUid);
        if (!DeviceUtils.isDebugAllowInstall() && callingPackage != null && !checkInstallPermission(callingPackage)) {
            nextActivity.addFlags(268435457);
            Log.d(LOG_TAG, "Install forbidden " + callingPackage + ", intent:" + nextActivity);
            nextActivity.setClassName("com.xiaopeng.appstore", "com.xiaopeng.appstore.installer.XpInstallForbiddenActivity");
        } else if (isSessionInstall) {
            nextActivity.setClass(this, PackageInstallerActivity.class);
        } else {
            Uri packageUri = intent.getData();
            if (packageUri != null && (packageUri.getScheme().equals("file") || packageUri.getScheme().equals("content"))) {
                nextActivity.setClass(this, InstallStaging.class);
            } else if (packageUri != null && packageUri.getScheme().equals("package")) {
                nextActivity.setClass(this, PackageInstallerActivity.class);
            } else {
                Intent result = new Intent();
                result.putExtra("android.intent.extra.INSTALL_RESULT", -3);
                setResult(1, result);
                nextActivity = null;
            }
        }
        if (nextActivity != null) {
            startActivity(nextActivity);
        }
        finish();
    }

    private boolean checkInstallPermission(String reqPackage) {
        if (reqPackage.equals("com.xiaopeng.appstore")) {
            return true;
        }
        try {
            xpPackageInfo info = AppGlobals.getPackageManager().getXpPackageInfo(reqPackage);
            if (xpPackageInfo.packageInstallEnable(info)) {
                String str = LOG_TAG;
                Log.d(str, "Install Permission is available for " + reqPackage);
                return true;
            }
            return false;
        } catch (RemoteException e) {
            return false;
        }
    }

    private String getPackageNameForUid(int sourceUid) {
        String[] packagesForUid = getPackageManager().getPackagesForUid(sourceUid);
        if (packagesForUid == null) {
            return null;
        }
        return packagesForUid[0];
    }

    private boolean declaresAppOpPermission(int uid, String permission) {
        String[] packages;
        try {
            packages = this.mIPackageManager.getAppOpPermissionPackages(permission);
        } catch (RemoteException e) {
        }
        if (packages == null) {
            return false;
        }
        for (String packageName : packages) {
            if (uid == getPackageManager().getPackageUid(packageName, 0)) {
                return true;
            }
        }
        return false;
    }

    private ApplicationInfo getSourceInfo(String callingPackage) {
        if (callingPackage != null) {
            try {
                return getPackageManager().getApplicationInfo(callingPackage, 0);
            } catch (PackageManager.NameNotFoundException e) {
                return null;
            }
        }
        return null;
    }

    private int getOriginatingUid(ApplicationInfo sourceInfo) {
        int callingUid;
        int uidFromIntent = getIntent().getIntExtra("android.intent.extra.ORIGINATING_UID", -1);
        if (sourceInfo != null) {
            callingUid = sourceInfo.uid;
        } else {
            try {
                callingUid = getIActivityManager().getLaunchedFromUid(getActivityToken());
            } catch (RemoteException e) {
                Log.e(LOG_TAG, "Could not determine the launching uid.");
                this.mAbortInstall = true;
                return -1;
            }
        }
        try {
            if (this.mIPackageManager.checkUidPermission("android.permission.MANAGE_DOCUMENTS", callingUid) == 0) {
                return uidFromIntent;
            }
        } catch (RemoteException e2) {
        }
        if (isSystemDownloadsProvider(callingUid)) {
            return uidFromIntent;
        }
        return callingUid;
    }

    private boolean isSystemDownloadsProvider(int uid) {
        ProviderInfo downloadProviderPackage = getPackageManager().resolveContentProvider("downloads", 0);
        if (downloadProviderPackage == null) {
            return false;
        }
        ApplicationInfo appInfo = downloadProviderPackage.applicationInfo;
        return appInfo.isSystemApp() && uid == appInfo.uid;
    }

    private IActivityManager getIActivityManager() {
        if (this.mIActivityManager == null) {
            return ActivityManager.getService();
        }
        return this.mIActivityManager;
    }

    @VisibleForTesting
    void injectIActivityManager(IActivityManager iActivityManager) {
        this.mIActivityManager = iActivityManager;
    }

    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
}
