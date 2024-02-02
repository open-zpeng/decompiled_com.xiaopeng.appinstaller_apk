package com.xiaopeng.appinstaller;

import android.app.ActivityThread;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageDeleteObserver2;
import android.content.pm.IPackageInstaller;
import android.content.pm.VersionedPackage;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.UserHandle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.xiaopeng.appinstaller.EventResultPersister;
/* loaded from: classes.dex */
public class UninstallUninstalling extends AppCompatActivity implements EventResultPersister.EventResultObserver {
    private static final String LOG_TAG = UninstallUninstalling.class.getSimpleName();
    private ApplicationInfo mAppInfo;
    private IBinder mCallback;
    private String mLabel;
    private boolean mReturnResult;
    private int mUninstallId;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int i = 0;
        setFinishOnTouchOutside(false);
        this.mAppInfo = (ApplicationInfo) getIntent().getParcelableExtra("com.android.packageinstaller.applicationInfo");
        this.mCallback = getIntent().getIBinderExtra("android.content.pm.extra.CALLBACK");
        this.mReturnResult = getIntent().getBooleanExtra("android.intent.extra.RETURN_RESULT", false);
        this.mLabel = getIntent().getStringExtra("com.android.packageinstaller.extra.APP_LABEL");
        try {
            if (savedInstanceState != null) {
                this.mUninstallId = savedInstanceState.getInt("com.android.packageinstaller.UNINSTALL_ID");
                UninstallEventReceiver.addObserver(this, this.mUninstallId, this);
                return;
            }
            boolean allUsers = getIntent().getBooleanExtra("android.intent.extra.UNINSTALL_ALL_USERS", false);
            UserHandle user = (UserHandle) getIntent().getParcelableExtra("android.intent.extra.USER");
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            Fragment prev = getFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                transaction.remove(prev);
            }
            DialogFragment dialog = new UninstallUninstallingFragment();
            dialog.setCancelable(false);
            dialog.show(transaction, "dialog");
            this.mUninstallId = UninstallEventReceiver.addObserver(this, Integer.MIN_VALUE, this);
            Intent broadcastIntent = new Intent("com.android.packageinstaller.ACTION_UNINSTALL_COMMIT");
            broadcastIntent.setFlags(268435456);
            broadcastIntent.putExtra("EventResultPersister.EXTRA_ID", this.mUninstallId);
            broadcastIntent.setPackage(getPackageName());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, this.mUninstallId, broadcastIntent, 134217728);
            try {
                IPackageInstaller packageInstaller = ActivityThread.getPackageManager().getPackageInstaller();
                VersionedPackage versionedPackage = new VersionedPackage(this.mAppInfo.packageName, -1);
                String packageName = getPackageName();
                if (allUsers) {
                    i = 2;
                }
                packageInstaller.uninstall(versionedPackage, packageName, i, pendingIntent.getIntentSender(), user.getIdentifier());
            } catch (RemoteException e) {
                e.rethrowFromSystemServer();
            }
        } catch (EventResultPersister.OutOfIdsException | IllegalArgumentException e2) {
            Log.e(LOG_TAG, "Fails to start uninstall", e2);
            onResult(1, -1, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("com.android.packageinstaller.UNINSTALL_ID", this.mUninstallId);
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
    }

    @Override // com.xiaopeng.appinstaller.EventResultPersister.EventResultObserver
    public void onResult(int status, int legacyStatus, String message) {
        if (this.mCallback != null) {
            IPackageDeleteObserver2 observer = IPackageDeleteObserver2.Stub.asInterface(this.mCallback);
            try {
                observer.onPackageDeleted(this.mAppInfo.packageName, legacyStatus, message);
            } catch (RemoteException e) {
            }
        } else {
            if (this.mReturnResult) {
                Intent result = new Intent();
                result.putExtra("android.intent.extra.INSTALL_RESULT", legacyStatus);
                setResult(status == 0 ? -1 : 1, result);
            } else if (status != 0) {
                Toast.makeText(this, getString(R.string.uninstall_failed_app, new Object[]{this.mLabel}), 1).show();
            }
        }
        finish();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        UninstallEventReceiver.removeObserver(this, this.mUninstallId);
        super.onDestroy();
    }

    /* loaded from: classes.dex */
    public static class UninstallUninstallingFragment extends DialogFragment {
        @Override // android.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
            dialogBuilder.setCancelable(false);
            dialogBuilder.setMessage(getActivity().getString(R.string.uninstalling_app, new Object[]{((UninstallUninstalling) getActivity()).mLabel}));
            Dialog dialog = dialogBuilder.create();
            dialog.setCanceledOnTouchOutside(false);
            return dialog;
        }
    }
}
