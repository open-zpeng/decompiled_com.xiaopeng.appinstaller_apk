package com.xiaopeng.appinstaller;

import android.app.ActivityThread;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageInstaller;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.PackageParser;
import android.content.pm.PackageUserState;
import android.content.pm.VersionedPackage;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Pair;
import com.android.internal.content.PackageHelper;
import com.xiaopeng.appinstaller.EventResultPersister;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import xiaopeng.widget.BuildConfig;
/* loaded from: classes.dex */
public class SilentInstallerService extends Service {
    private static final String ALLOW_UNKNOWN_SOURCES_KEY = SilentInstallerService.class.getName() + "ALLOW_UNKNOWN_SOURCES_KEY";
    private static volatile PowerManager.WakeLock lockStatic = null;
    private int mInstallId;
    private InstallingAsyncTask mInstallingTask;
    private NotificationChannel mNotificationChannel;
    private ServiceHandler mServiceHandler;
    private PackageInstaller.SessionCallback mSessionCallback;
    private int mSessionId;
    private int mUnInstallId;
    private int mInstallNotificationId = 1;
    private final Map<String, Integer> mNotifIdMap = new ArrayMap();
    private final int START_INSTALL = 1;
    private final int START_UNINSTALL = 2;
    private final int START_STAGING = 3;
    private int ID_SERVICE_START = 0;

    static /* synthetic */ int access$600(SilentInstallerService x0) {
        return x0.mSessionId;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            Intent intent = (Intent) msg.obj;
            switch (msg.what) {
                case 1:
                    SilentInstallerService.this.installPackage(intent);
                    return;
                case 2:
                    SilentInstallerService.this.uninstallPackage(intent);
                    return;
                case 3:
                    SilentInstallerService.this.stagingPackage(intent);
                    return;
                default:
                    return;
            }
        }
    }

    @Override // android.app.Service
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override // android.app.Service
    public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread("PackageInstallerThread", 10);
        thread.start();
        this.mServiceHandler = new ServiceHandler(thread.getLooper());
        this.mSessionCallback = new InstallSessionCallback();
        getPackageManager().getPackageInstaller().registerSessionCallback(this.mSessionCallback);
    }

    @Override // android.app.Service
    public int onStartCommand(Intent intent, int flags, int startId) {
        this.ID_SERVICE_START = startId;
        if (intent == null) {
            Log.e("Loong/SilentInstallerService", "Got null intent.");
            finishServiceEarly(startId);
            return 2;
        }
        Uri packageUri = intent.getData();
        Log.d("Loong/SilentInstallerService", "onStartCommand packageURI:" + packageUri + "/action:" + intent.getAction() + "/" + intent.getPackage());
        if (packageUri == null) {
            Log.e("Loong/SilentInstallerService", "No package URI in intent");
            finishServiceEarly(startId);
            return 2;
        }
        if (intent.getAction().equals("android.intent.action.INSTALL_PACKAGE") || intent.getAction().equals("android.intent.action.VIEW") || intent.getAction().equals("xp.intent.action.INSTALL_PACKAGE")) {
            Message msg = this.mServiceHandler.obtainMessage(3);
            msg.obj = intent;
            this.mServiceHandler.sendMessage(msg);
        } else if (intent.getAction().equals("android.intent.action.UNINSTALL_PACKAGE") || intent.getAction().equals("android.intent.action.DELETE") || intent.getAction().equals("xp.intent.action.UNINSTALL_PACKAGE")) {
            Message msg2 = this.mServiceHandler.obtainMessage(2);
            msg2.obj = intent;
            this.mServiceHandler.sendMessage(msg2);
        } else {
            Log.e("Loong/SilentInstallerService", "Action error");
            finishServiceEarly(startId);
        }
        return 2;
    }

    @Override // android.app.Service
    public void onDestroy() {
        super.onDestroy();
        Log.d("Loong/SilentInstallerService", "onDestroy");
        if (this.mInstallingTask != null) {
            this.mInstallingTask.cancel(true);
            synchronized (this.mInstallingTask) {
                while (!this.mInstallingTask.isDone) {
                    try {
                        this.mInstallingTask.wait();
                    } catch (InterruptedException e) {
                        Log.i("Loong/SilentInstallerService", "Interrupted while waiting for installing task to cancel", e);
                    }
                }
            }
        }
        InstallEventReceiver.removeObserver(this, this.mInstallId);
        getPackageManager().getPackageInstaller().unregisterSessionCallback(this.mSessionCallback);
        UninstallEventReceiver.removeObserver(this, this.mUnInstallId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public Intent stagingPackage(Intent intent) {
        if (intent == null) {
            return null;
        }
        Uri packageUri = intent.getData();
        File stagedFileWithContent = TemporaryFileManager.writeContentFromUri(this, packageUri);
        if (stagedFileWithContent == null) {
            return null;
        }
        intent.setData(Uri.fromFile(stagedFileWithContent));
        Message msg = this.mServiceHandler.obtainMessage(1);
        msg.obj = intent;
        this.mServiceHandler.sendMessage(msg);
        return intent;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void installPackage(Intent intent) {
        Uri packageUri = intent.getData();
        String mCallingPackage = intent.getStringExtra("EXTRA_CALLING_PACKAGE");
        intent.getParcelableExtra("EXTRA_ORIGINAL_SOURCE_INFO");
        int mOriginatingUid = intent.getIntExtra("android.intent.extra.ORIGINATING_UID", -1);
        String mOriginatingPackage = mOriginatingUid != -1 ? getPackageNameForUid(mOriginatingUid, mCallingPackage) : null;
        Uri mOriginatingURI = (Uri) intent.getParcelableExtra("android.intent.extra.ORIGINATING_URI");
        Uri mReferrerURI = (Uri) intent.getParcelableExtra("android.intent.extra.REFERRER");
        Log.d("Loong/SilentInstallerService", "installPackage mCallingPackage:" + mCallingPackage + "/packageUri:" + packageUri + "/mOriginatingPackage:" + mOriginatingPackage + "/mOriginationgURI:" + mOriginatingURI + "/mReferrerURI:" + mReferrerURI);
        PackageInfo packageInfo = processPackageUri(packageUri);
        getApplicationInfo(packageInfo);
        Intent newIntent = new Intent();
        newIntent.putExtra("com.android.packageinstaller.applicationInfo", packageInfo.applicationInfo);
        newIntent.setData(packageUri);
        newIntent.setClass(this, InstallInstalling.class);
        String installerPackageName = intent.getStringExtra("android.intent.extra.INSTALLER_PACKAGE_NAME");
        if (mOriginatingURI != null) {
            newIntent.putExtra("android.intent.extra.ORIGINATING_URI", mOriginatingURI);
        }
        if (mReferrerURI != null) {
            newIntent.putExtra("android.intent.extra.REFERRER", mReferrerURI);
        }
        if (mOriginatingUid != -1) {
            newIntent.putExtra("android.intent.extra.ORIGINATING_UID", mOriginatingUid);
        }
        if (installerPackageName != null) {
            newIntent.putExtra("android.intent.extra.INSTALLER_PACKAGE_NAME", installerPackageName);
        }
        if (intent.getBooleanExtra("android.intent.extra.RETURN_RESULT", false)) {
            newIntent.putExtra("android.intent.extra.RETURN_RESULT", true);
        }
        newIntent.addFlags(33554432);
        newIntent.putExtra("xp.intent.extra.INSTALLER_PACKAGE_MANAGER", intent.getIntExtra("xp.intent.extra.INSTALLER_PACKAGE_MANAGER", 0));
        startInstall(newIntent);
    }

    private void startInstall(Intent intent) {
        Uri mPackageURI = intent.getData();
        int flag = intent.getIntExtra("xp.intent.extra.INSTALLER_PACKAGE_MANAGER", 0);
        PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(1);
        params.installFlags = 16384 | flag;
        params.referrerUri = (Uri) intent.getParcelableExtra("android.intent.extra.REFERRER");
        params.originatingUri = (Uri) intent.getParcelableExtra("android.intent.extra.ORIGINATING_URI");
        params.originatingUid = intent.getIntExtra("android.intent.extra.ORIGINATING_UID", -1);
        params.installerPackageName = intent.getStringExtra("android.intent.extra.INSTALLER_PACKAGE_NAME");
        File file = new File(mPackageURI.getPath());
        try {
            PackageParser.PackageLite pkg = PackageParser.parsePackageLite(file, 0);
            params.setAppPackageName(pkg.packageName);
            params.setInstallLocation(pkg.installLocation);
            params.setSize(PackageHelper.calculateInstalledSize(pkg, false, params.abiOverride));
        } catch (PackageParser.PackageParserException e) {
            Log.e("Loong/SilentInstallerService", "Cannot parse package " + file + ". Assuming defaults.");
            Log.e("Loong/SilentInstallerService", "Cannot calculate installed size " + file + ". Try only apk size.");
            params.setSize(file.length());
        } catch (IOException e2) {
            Log.e("Loong/SilentInstallerService", "Cannot calculate installed size " + file + ". Try only apk size.");
            params.setSize(file.length());
        }
        Log.d("Loong/SilentInstallerService", "onCreate sessionParams:" + params.toString());
        try {
            this.mInstallId = InstallEventReceiver.addObserver(getApplicationContext(), Integer.MIN_VALUE, new EventResultPersister.EventResultObserver() { // from class: com.xiaopeng.appinstaller.SilentInstallerService.1
                @Override // com.xiaopeng.appinstaller.EventResultPersister.EventResultObserver
                public void onResult(int status, int legacyStatus, String message) {
                    Log.d("Loong/SilentInstallerService", "EventResultObserver status:" + status + "/legacyStatus:" + legacyStatus + "/message:" + message);
                    SilentInstallerService.this.finishServiceNow();
                }
            });
        } catch (EventResultPersister.OutOfIdsException e3) {
        }
        try {
            this.mSessionId = getPackageManager().getPackageInstaller().createSession(params);
        } catch (IOException e4) {
            finishServiceNow();
        }
        PackageInstaller installer = getPackageManager().getPackageInstaller();
        PackageInstaller.SessionInfo sessionInfo = installer.getSessionInfo(this.mSessionId);
        if (sessionInfo != null && !sessionInfo.isActive()) {
            this.mInstallingTask = new InstallingAsyncTask();
            this.mInstallingTask.execute(mPackageURI);
        }
    }

    private PackageInfo processPackageUri(Uri packageUri) {
        char c;
        String scheme = packageUri.getScheme();
        PackageInfo mPkgInfo = null;
        int hashCode = scheme.hashCode();
        if (hashCode != -807062458) {
            if (hashCode == 3143036 && scheme.equals("file")) {
                c = 1;
            }
            c = 65535;
        } else {
            if (scheme.equals("package")) {
                c = 0;
            }
            c = 65535;
        }
        switch (c) {
            case 0:
                try {
                    mPkgInfo = getPackageManager().getPackageInfo(packageUri.getSchemeSpecificPart(), 12288);
                } catch (PackageManager.NameNotFoundException e) {
                }
                if (mPkgInfo == null) {
                    Log.w("Loong/SilentInstallerService", "Requested package " + packageUri.getScheme() + " not available. Discontinuing installation");
                    return null;
                }
                return mPkgInfo;
            case 1:
                File sourceFile = new File(packageUri.getPath());
                PackageParser.Package parsed = PackageUtil.getPackageInfo(this, sourceFile);
                if (parsed == null) {
                    Log.w("Loong/SilentInstallerService", "Parse error when parsing manifest. Discontinuing installation");
                    return null;
                }
                PackageInfo mPkgInfo2 = PackageParser.generatePackageInfo(parsed, (int[]) null, 4096, 0L, 0L, (Set) null, new PackageUserState());
                return mPkgInfo2;
            default:
                throw new IllegalArgumentException("Unexpected URI scheme " + packageUri);
        }
    }

    private ApplicationInfo getApplicationInfo(PackageInfo mPkgInfo) {
        String pkgName = mPkgInfo.packageName;
        String[] oldName = getPackageManager().canonicalToCurrentPackageNames(new String[]{pkgName});
        if (oldName != null && oldName.length > 0 && oldName[0] != null) {
            pkgName = oldName[0];
            mPkgInfo.packageName = pkgName;
            mPkgInfo.applicationInfo.packageName = pkgName;
        }
        try {
            ApplicationInfo mAppInfo = getPackageManager().getApplicationInfo(pkgName, 8192);
            if ((mAppInfo.flags & 8388608) == 0) {
                return null;
            }
            return mAppInfo;
        } catch (PackageManager.NameNotFoundException e) {
            return null;
        }
    }

    private String getPackageNameForUid(int sourceUid, String mCallingPackage) {
        String[] packagesForUid = getPackageManager().getPackagesForUid(sourceUid);
        if (packagesForUid == null) {
            return null;
        }
        if (packagesForUid.length > 1) {
            if (mCallingPackage != null) {
                for (String packageName : packagesForUid) {
                    if (packageName.equals(mCallingPackage)) {
                        return packageName;
                    }
                }
            }
            Log.i("Loong/SilentInstallerService", "Multiple packages found for source uid " + sourceUid);
        }
        return packagesForUid[0];
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void uninstallPackage(Intent intent) {
        try {
            this.mUnInstallId = UninstallEventReceiver.addObserver(this, Integer.MIN_VALUE, new EventResultPersister.EventResultObserver() { // from class: com.xiaopeng.appinstaller.SilentInstallerService.2
                @Override // com.xiaopeng.appinstaller.EventResultPersister.EventResultObserver
                public void onResult(int status, int legacyStatus, String message) {
                    Log.d("Loong/SilentInstallerService", "onResult status:" + status + "/legacyStatus:" + legacyStatus + "/message:" + message);
                    SilentInstallerService.this.finishServiceNow();
                }
            });
        } catch (EventResultPersister.OutOfIdsException e) {
            e.printStackTrace();
        }
        int i = 0;
        boolean allUsers = intent.getBooleanExtra("android.intent.extra.UNINSTALL_ALL_USERS", false);
        UserHandle user = (UserHandle) intent.getParcelableExtra("android.intent.extra.USER");
        if (user == null) {
            user = Process.myUserHandle();
        } else {
            UserManager userManager = (UserManager) getSystemService("user");
            List<UserHandle> profiles = userManager.getUserProfiles();
            if (!profiles.contains(user)) {
                Log.e("Loong/SilentInstallerService", "User " + Process.myUserHandle() + " can't request uninstall for user " + user);
                return;
            }
        }
        String packageName = intent.getData().getEncodedSchemeSpecificPart();
        Intent broadcastIntent = new Intent("com.android.packageinstaller.ACTION_INSTALL_COMMIT");
        broadcastIntent.setFlags(268435456);
        broadcastIntent.putExtra("EventResultPersister.EXTRA_ID", this.mUnInstallId);
        broadcastIntent.setPackage(getPackageName());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, this.mUnInstallId, broadcastIntent, 134217728);
        try {
            IPackageInstaller packageInstaller = ActivityThread.getPackageManager().getPackageInstaller();
            VersionedPackage versionedPackage = new VersionedPackage(packageName, -1);
            String packageName2 = getPackageName();
            if (allUsers) {
                i = 2;
            }
            packageInstaller.uninstall(versionedPackage, packageName2, i, pendingIntent.getIntentSender(), user.getIdentifier());
        } catch (RemoteException e2) {
            e2.rethrowFromSystemServer();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void finishServiceNow() {
        PowerManager.WakeLock lock = getLock(getApplicationContext());
        finishService(lock, this.ID_SERVICE_START);
    }

    private synchronized PowerManager.WakeLock getLock(Context context) {
        if (lockStatic == null) {
            PowerManager mgr = (PowerManager) context.getSystemService("power");
            lockStatic = mgr.newWakeLock(1, context.getClass().getSimpleName());
            lockStatic.setReferenceCounted(true);
        }
        return lockStatic;
    }

    private void finishServiceEarly(int startId) {
        Pair<Integer, Notification> notifPair = buildNotification(getApplicationContext().getPackageName(), BuildConfig.FLAVOR);
        startForeground(((Integer) notifPair.first).intValue(), (Notification) notifPair.second);
        finishService(null, startId);
    }

    private void finishService(PowerManager.WakeLock lock, int startId) {
        if (lock != null && lock.isHeld()) {
            lock.release();
        }
        stopSelf(startId);
    }

    private synchronized Pair<Integer, Notification> buildNotification(String packageName, String title) {
        int notifId;
        if (this.mNotifIdMap.containsKey(packageName)) {
            notifId = this.mNotifIdMap.get(packageName).intValue();
        } else {
            notifId = this.mInstallNotificationId;
            this.mInstallNotificationId = notifId + 1;
            this.mNotifIdMap.put(packageName, Integer.valueOf(notifId));
        }
        if (this.mNotificationChannel == null) {
            this.mNotificationChannel = new NotificationChannel("silent_app_install_uninstall", getString(R.string.wear_app_channel), 1);
            NotificationManager notificationManager = (NotificationManager) getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(this.mNotificationChannel);
        }
        return new Pair<>(Integer.valueOf(notifId), new Notification.Builder(this, "silent_app_install_uninstall").setSmallIcon(R.drawable.ic_file_download).setContentTitle(title).build());
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
            Log.d("Loong/SilentInstallerService", "onProgressChanged sessionId:" + sessionId + "/progress:" + progress);
            int unused = SilentInstallerService.this.mSessionId;
        }

        @Override // android.content.pm.PackageInstaller.SessionCallback
        public void onFinished(int sessionId, boolean success) {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class InstallingAsyncTask extends AsyncTask<Uri, Void, PackageInstaller.Session> {
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
        public android.content.pm.PackageInstaller.Session doInBackground(android.net.Uri... r20) {
            /*
                Method dump skipped, instructions count: 278
                To view this dump add '--comments-level debug' option
            */
            throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.appinstaller.SilentInstallerService.InstallingAsyncTask.doInBackground(android.net.Uri[]):android.content.pm.PackageInstaller$Session");
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
                SilentInstallerService.this.getPackageManager().getPackageInstaller().abandonSession(SilentInstallerService.this.mSessionId);
                if (!isCancelled()) {
                    Log.e("Loong/SilentInstallerService", "Installing AsyncTask failure");
                }
                SilentInstallerService.this.finishServiceNow();
                return;
            }
            Intent broadcastIntent = new Intent("com.android.packageinstaller.ACTION_INSTALL_COMMIT");
            broadcastIntent.setFlags(268435456);
            broadcastIntent.setPackage(SilentInstallerService.this.getPackageManager().getPermissionControllerPackageName());
            broadcastIntent.putExtra("EventResultPersister.EXTRA_ID", SilentInstallerService.this.mInstallId);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(SilentInstallerService.this, SilentInstallerService.this.mInstallId, broadcastIntent, 134217728);
            session.commit(pendingIntent.getIntentSender());
        }
    }
}
