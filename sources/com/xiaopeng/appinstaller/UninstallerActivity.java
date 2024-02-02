package com.xiaopeng.appinstaller;

import android.app.ActivityManager;
import android.app.ActivityThread;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDeleteObserver2;
import android.content.pm.IPackageInstaller;
import android.content.pm.IPackageManager;
import android.content.pm.VersionedPackage;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import com.xiaopeng.app.xpPackageInfo;
import com.xiaopeng.appinstaller.EventResultPersister;
import com.xiaopeng.appinstaller.handheld.UninstallAlertDialogFragment;
import java.util.List;
/* loaded from: classes.dex */
public class UninstallerActivity extends AppCompatActivity {
    private DialogInfo mDialogInfo;
    private String mPackageName;

    /* loaded from: classes.dex */
    public static class DialogInfo {
        public ActivityInfo activityInfo;
        public boolean allUsers;
        public ApplicationInfo appInfo;
        public IBinder callback;
        public UserHandle user;
    }

    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle icicle) {
        super.onCreate(null);
        try {
            int callingUid = ActivityManager.getService().getLaunchedFromUid(getActivityToken());
            String callingPackage = getPackageNameForUid(callingUid);
            if (callingPackage == null) {
                Log.e("UninstallerActivity", "Package not found for originating uid " + callingUid);
                setResult(1);
                finish();
                return;
            }
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService("appops");
            if (appOpsManager.noteOpNoThrow("android:request_delete_packages", callingUid, callingPackage) != 0) {
                Log.e("UninstallerActivity", "Install from uid " + callingUid + " disallowed by AppOps");
                setResult(1);
                finish();
            } else if (PackageUtil.getMaxTargetSdkVersionForUid(this, callingUid) >= 28 && AppGlobals.getPackageManager().checkUidPermission("android.permission.REQUEST_DELETE_PACKAGES", callingUid) != 0 && AppGlobals.getPackageManager().checkUidPermission("android.permission.DELETE_PACKAGES", callingUid) != 0) {
                Log.e("UninstallerActivity", "Uid " + callingUid + " does not have android.permission.REQUEST_DELETE_PACKAGES or android.permission.DELETE_PACKAGES");
                setResult(1);
                finish();
            } else {
                Log.d("UninstallerActivity", "onCreate callingPackage:" + callingPackage + "/callingUid:" + callingUid);
                if (!DeviceUtils.isDebugAllowInstall() && !checkUninstallPermission(callingPackage)) {
                    showUserIsNotAllowed();
                    return;
                }
                Intent intent = getIntent();
                Uri packageUri = intent.getData();
                if (packageUri == null) {
                    Log.e("UninstallerActivity", "No package URI in intent");
                    showAppNotFound();
                    return;
                }
                this.mPackageName = packageUri.getEncodedSchemeSpecificPart();
                if (this.mPackageName == null) {
                    Log.e("UninstallerActivity", "Invalid package name in URI: " + packageUri);
                    showAppNotFound();
                    return;
                }
                IPackageManager pm = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
                this.mDialogInfo = new DialogInfo();
                this.mDialogInfo.allUsers = intent.getBooleanExtra("android.intent.extra.UNINSTALL_ALL_USERS", false);
                if (this.mDialogInfo.allUsers && !UserManager.get(this).isAdminUser()) {
                    Log.e("UninstallerActivity", "Only admin user can request uninstall for all users");
                    showUserIsNotAllowed();
                    return;
                }
                this.mDialogInfo.user = (UserHandle) intent.getParcelableExtra("android.intent.extra.USER");
                if (this.mDialogInfo.user == null) {
                    this.mDialogInfo.user = Process.myUserHandle();
                } else {
                    UserManager userManager = (UserManager) getSystemService("user");
                    List<UserHandle> profiles = userManager.getUserProfiles();
                    if (!profiles.contains(this.mDialogInfo.user)) {
                        Log.e("UninstallerActivity", "User " + Process.myUserHandle() + " can't request uninstall for user " + this.mDialogInfo.user);
                        showUserIsNotAllowed();
                        return;
                    }
                }
                this.mDialogInfo.callback = intent.getIBinderExtra("android.content.pm.extra.CALLBACK");
                try {
                    this.mDialogInfo.appInfo = pm.getApplicationInfo(this.mPackageName, 4194304, this.mDialogInfo.user.getIdentifier());
                } catch (RemoteException e) {
                    Log.e("UninstallerActivity", "Unable to get packageName. Package manager is dead?");
                }
                if (this.mDialogInfo.appInfo == null) {
                    Log.e("UninstallerActivity", "Invalid packageName: " + this.mPackageName);
                    showAppNotFound();
                    return;
                }
                String className = packageUri.getFragment();
                if (className != null) {
                    try {
                        this.mDialogInfo.activityInfo = pm.getActivityInfo(new ComponentName(this.mPackageName, className), 0, this.mDialogInfo.user.getIdentifier());
                    } catch (RemoteException e2) {
                        Log.e("UninstallerActivity", "Unable to get className. Package manager is dead?");
                    }
                }
                showConfirmationDialog();
            }
        } catch (RemoteException e3) {
            Log.e("UninstallerActivity", "Could not determine the launching uid.");
            setResult(1);
            finish();
        }
    }

    private boolean checkUninstallPermission(String reqPackage) {
        if (reqPackage.equals("com.xiaopeng.appstore")) {
            return true;
        }
        try {
            xpPackageInfo info = AppGlobals.getPackageManager().getXpPackageInfo(reqPackage);
            if (xpPackageInfo.packageInstallEnable(info)) {
                Log.d("UninstallerActivity", "Uninstall Permission is available for " + reqPackage);
                return true;
            }
            return false;
        } catch (RemoteException e) {
            return false;
        }
    }

    public DialogInfo getDialogInfo() {
        return this.mDialogInfo;
    }

    private void showConfirmationDialog() {
        showDialogFragment(new UninstallAlertDialogFragment(), 0, 0);
    }

    private void showAppNotFound() {
        showErrorDialog(R.string.app_not_found_dlg_title, R.string.app_not_found_dlg_text);
    }

    private void showUserIsNotAllowed() {
        showErrorDialog(R.string.uninstall_failed, R.string.user_is_not_allowed_dlg_text);
    }

    private void showGenericError() {
        showErrorDialog(0, R.string.generic_error_dlg_text);
    }

    private void showErrorDialog(int title, int text) {
        ErrorDialogActivity.startAction(this, title, text);
        finish();
    }

    private void showDialogFragment(DialogFragment fragment, int title, int text) {
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        Fragment prev = getFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        Bundle args = new Bundle();
        if (title != 0) {
            args.putInt("com.android.packageinstaller.arg.title", title);
        }
        args.putInt("com.android.packageinstaller.arg.text", text);
        fragment.setArguments(args);
        fragment.show(ft, "dialog");
    }

    public void startUninstallProgress() {
        int i = 0;
        boolean returnResult = getIntent().getBooleanExtra("android.intent.extra.RETURN_RESULT", false);
        CharSequence label = this.mDialogInfo.appInfo.loadSafeLabel(getPackageManager());
        if (returnResult || this.mDialogInfo.callback != null || getCallingActivity() != null) {
            Intent newIntent = new Intent(this, UninstallUninstalling.class);
            newIntent.putExtra("android.intent.extra.USER", this.mDialogInfo.user);
            newIntent.putExtra("android.intent.extra.UNINSTALL_ALL_USERS", this.mDialogInfo.allUsers);
            newIntent.putExtra("com.android.packageinstaller.applicationInfo", this.mDialogInfo.appInfo);
            newIntent.putExtra("com.android.packageinstaller.extra.APP_LABEL", label);
            newIntent.putExtra("android.content.pm.extra.CALLBACK", this.mDialogInfo.callback);
            if (returnResult) {
                newIntent.putExtra("android.intent.extra.RETURN_RESULT", true);
            }
            if (returnResult || getCallingActivity() != null) {
                newIntent.addFlags(33554432);
            }
            startActivity(newIntent);
            return;
        }
        try {
            int uninstallId = UninstallEventReceiver.getNewId(this);
            Intent broadcastIntent = new Intent(this, UninstallFinish.class);
            broadcastIntent.setFlags(268435456);
            broadcastIntent.putExtra("android.intent.extra.UNINSTALL_ALL_USERS", this.mDialogInfo.allUsers);
            broadcastIntent.putExtra("com.android.packageinstaller.applicationInfo", this.mDialogInfo.appInfo);
            broadcastIntent.putExtra("com.android.packageinstaller.extra.APP_LABEL", label);
            broadcastIntent.putExtra("com.android.packageinstaller.extra.UNINSTALL_ID", uninstallId);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, uninstallId, broadcastIntent, 134217728);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NotificationManager.class);
            NotificationChannel uninstallingChannel = new NotificationChannel("uninstalling", getString(R.string.uninstalling_notification_channel), 1);
            notificationManager.createNotificationChannel(uninstallingChannel);
            Notification uninstallingNotification = new Notification.Builder(this, "uninstalling").setSmallIcon(R.drawable.ic_remove).setProgress(0, 1, true).setContentTitle(getString(R.string.uninstalling_app, new Object[]{label})).setOngoing(true).build();
            notificationManager.notify(uninstallId, uninstallingNotification);
            try {
                Log.i("UninstallerActivity", "Uninstalling extras=" + broadcastIntent.getExtras());
                IPackageInstaller packageInstaller = ActivityThread.getPackageManager().getPackageInstaller();
                VersionedPackage versionedPackage = new VersionedPackage(this.mDialogInfo.appInfo.packageName, -1);
                String packageName = getPackageName();
                if (this.mDialogInfo.allUsers) {
                    i = 2;
                }
                packageInstaller.uninstall(versionedPackage, packageName, i, pendingIntent.getIntentSender(), this.mDialogInfo.user.getIdentifier());
            } catch (Exception e) {
                notificationManager.cancel(uninstallId);
                Log.e("UninstallerActivity", "Cannot start uninstall", e);
                showGenericError();
            }
        } catch (EventResultPersister.OutOfIdsException e2) {
            showGenericError();
        }
    }

    public void dispatchAborted() {
        if (this.mDialogInfo != null && this.mDialogInfo.callback != null) {
            IPackageDeleteObserver2 observer = IPackageDeleteObserver2.Stub.asInterface(this.mDialogInfo.callback);
            try {
                observer.onPackageDeleted(this.mPackageName, -5, "Cancelled by user");
            } catch (RemoteException e) {
            }
        }
    }

    private String getPackageNameForUid(int sourceUid) {
        String[] packagesForUid = getPackageManager().getPackagesForUid(sourceUid);
        if (packagesForUid == null) {
            return null;
        }
        return packagesForUid[0];
    }
}
