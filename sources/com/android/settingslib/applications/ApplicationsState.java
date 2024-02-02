package com.android.settingslib.applications;

import android.app.ActivityManager;
import android.app.usage.StorageStats;
import android.app.usage.StorageStatsManager;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.content.pm.ParceledListSlice;
import android.content.pm.ResolveInfo;
import android.content.pm.UserInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.IconDrawableFactory;
import android.util.Log;
import android.util.SparseArray;
import com.android.internal.util.ArrayUtils;
import com.android.settingslib.applications.ApplicationsState;
import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
/* loaded from: classes.dex */
public class ApplicationsState {
    final ArrayList<WeakReference<Session>> mActiveSessions;
    final int mAdminRetrieveFlags;
    final ArrayList<AppEntry> mAppEntries;
    List<ApplicationInfo> mApplications;
    final BackgroundHandler mBackgroundHandler;
    final Context mContext;
    String mCurComputingSizePkg;
    int mCurComputingSizeUserId;
    UUID mCurComputingSizeUuid;
    long mCurId;
    final IconDrawableFactory mDrawableFactory;
    final SparseArray<HashMap<String, AppEntry>> mEntriesMap;
    boolean mHaveDisabledApps;
    boolean mHaveInstantApps;
    final InterestingConfigChanges mInterestingConfigChanges;
    final IPackageManager mIpm;
    final MainHandler mMainHandler;
    PackageIntentReceiver mPackageIntentReceiver;
    final PackageManager mPm;
    final ArrayList<Session> mRebuildingSessions;
    boolean mResumed;
    final int mRetrieveFlags;
    final ArrayList<Session> mSessions;
    boolean mSessionsChanged;
    final StorageStatsManager mStats;
    final UserManager mUm;
    static final Pattern REMOVE_DIACRITICALS_PATTERN = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
    static final Object sLock = new Object();
    public static final Comparator<AppEntry> ALPHA_COMPARATOR = new Comparator<AppEntry>() { // from class: com.android.settingslib.applications.ApplicationsState.1
        private final Collator sCollator = Collator.getInstance();

        @Override // java.util.Comparator
        public int compare(AppEntry object1, AppEntry object2) {
            int compareResult;
            int compareResult2 = this.sCollator.compare(object1.label, object2.label);
            if (compareResult2 != 0) {
                return compareResult2;
            }
            if (object1.info != null && object2.info != null && (compareResult = this.sCollator.compare(object1.info.packageName, object2.info.packageName)) != 0) {
                return compareResult;
            }
            return object1.info.uid - object2.info.uid;
        }
    };
    public static final Comparator<AppEntry> SIZE_COMPARATOR = new Comparator<AppEntry>() { // from class: com.android.settingslib.applications.ApplicationsState.2
        @Override // java.util.Comparator
        public int compare(AppEntry object1, AppEntry object2) {
            if (object1.size < object2.size) {
                return 1;
            }
            if (object1.size > object2.size) {
                return -1;
            }
            return ApplicationsState.ALPHA_COMPARATOR.compare(object1, object2);
        }
    };
    public static final Comparator<AppEntry> INTERNAL_SIZE_COMPARATOR = new Comparator<AppEntry>() { // from class: com.android.settingslib.applications.ApplicationsState.3
        @Override // java.util.Comparator
        public int compare(AppEntry object1, AppEntry object2) {
            if (object1.internalSize < object2.internalSize) {
                return 1;
            }
            if (object1.internalSize > object2.internalSize) {
                return -1;
            }
            return ApplicationsState.ALPHA_COMPARATOR.compare(object1, object2);
        }
    };
    public static final Comparator<AppEntry> EXTERNAL_SIZE_COMPARATOR = new Comparator<AppEntry>() { // from class: com.android.settingslib.applications.ApplicationsState.4
        @Override // java.util.Comparator
        public int compare(AppEntry object1, AppEntry object2) {
            if (object1.externalSize < object2.externalSize) {
                return 1;
            }
            if (object1.externalSize > object2.externalSize) {
                return -1;
            }
            return ApplicationsState.ALPHA_COMPARATOR.compare(object1, object2);
        }
    };
    public static final AppFilter FILTER_PERSONAL = new AppFilter() { // from class: com.android.settingslib.applications.ApplicationsState.5
        private int mCurrentUser;

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
            this.mCurrentUser = ActivityManager.getCurrentUser();
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry entry) {
            return UserHandle.getUserId(entry.info.uid) == this.mCurrentUser;
        }
    };
    public static final AppFilter FILTER_WITHOUT_DISABLED_UNTIL_USED = new AppFilter() { // from class: com.android.settingslib.applications.ApplicationsState.6
        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry entry) {
            return entry.info.enabledSetting != 4;
        }
    };
    public static final AppFilter FILTER_WORK = new AppFilter() { // from class: com.android.settingslib.applications.ApplicationsState.7
        private int mCurrentUser;

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
            this.mCurrentUser = ActivityManager.getCurrentUser();
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry entry) {
            return UserHandle.getUserId(entry.info.uid) != this.mCurrentUser;
        }
    };
    public static final AppFilter FILTER_DOWNLOADED_AND_LAUNCHER = new AppFilter() { // from class: com.android.settingslib.applications.ApplicationsState.8
        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry entry) {
            if (AppUtils.isInstant(entry.info)) {
                return false;
            }
            if (ApplicationsState.hasFlag(entry.info.flags, 128) || !ApplicationsState.hasFlag(entry.info.flags, 1) || entry.hasLauncherEntry) {
                return true;
            }
            return ApplicationsState.hasFlag(entry.info.flags, 1) && entry.isHomeApp;
        }
    };
    public static final AppFilter FILTER_DOWNLOADED_AND_LAUNCHER_AND_INSTANT = new AppFilter() { // from class: com.android.settingslib.applications.ApplicationsState.9
        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry entry) {
            return AppUtils.isInstant(entry.info) || ApplicationsState.FILTER_DOWNLOADED_AND_LAUNCHER.filterApp(entry);
        }
    };
    public static final AppFilter FILTER_THIRD_PARTY = new AppFilter() { // from class: com.android.settingslib.applications.ApplicationsState.10
        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry entry) {
            return ApplicationsState.hasFlag(entry.info.flags, 128) || !ApplicationsState.hasFlag(entry.info.flags, 1);
        }
    };
    public static final AppFilter FILTER_DISABLED = new AppFilter() { // from class: com.android.settingslib.applications.ApplicationsState.11
        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry entry) {
            return (entry.info.enabled || AppUtils.isInstant(entry.info)) ? false : true;
        }
    };
    public static final AppFilter FILTER_INSTANT = new AppFilter() { // from class: com.android.settingslib.applications.ApplicationsState.12
        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry entry) {
            return AppUtils.isInstant(entry.info);
        }
    };
    public static final AppFilter FILTER_ALL_ENABLED = new AppFilter() { // from class: com.android.settingslib.applications.ApplicationsState.13
        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry entry) {
            return entry.info.enabled && !AppUtils.isInstant(entry.info);
        }
    };
    public static final AppFilter FILTER_EVERYTHING = new AppFilter() { // from class: com.android.settingslib.applications.ApplicationsState.14
        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry entry) {
            return true;
        }
    };
    public static final AppFilter FILTER_WITH_DOMAIN_URLS = new AppFilter() { // from class: com.android.settingslib.applications.ApplicationsState.15
        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry entry) {
            return !AppUtils.isInstant(entry.info) && ApplicationsState.hasFlag(entry.info.privateFlags, 16);
        }
    };
    public static final AppFilter FILTER_NOT_HIDE = new AppFilter() { // from class: com.android.settingslib.applications.ApplicationsState.16
        private String[] mHidePackageNames;

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init(Context context) {
            this.mHidePackageNames = context.getResources().getStringArray(17236014);
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry entry) {
            if (ArrayUtils.contains(this.mHidePackageNames, entry.info.packageName)) {
                return entry.info.enabled && entry.info.enabledSetting != 4;
            }
            return true;
        }
    };
    public static final AppFilter FILTER_GAMES = new AppFilter() { // from class: com.android.settingslib.applications.ApplicationsState.17
        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry info) {
            boolean isGame;
            synchronized (info.info) {
                isGame = ApplicationsState.hasFlag(info.info.flags, 33554432) || info.info.category == 0;
            }
            return isGame;
        }
    };
    public static final AppFilter FILTER_AUDIO = new AppFilter() { // from class: com.android.settingslib.applications.ApplicationsState.18
        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry entry) {
            boolean isMusicApp;
            synchronized (entry) {
                boolean z = true;
                if (entry.info.category != 1) {
                    z = false;
                }
                isMusicApp = z;
            }
            return isMusicApp;
        }
    };
    public static final AppFilter FILTER_MOVIES = new AppFilter() { // from class: com.android.settingslib.applications.ApplicationsState.19
        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry entry) {
            boolean isMovieApp;
            synchronized (entry) {
                isMovieApp = entry.info.category == 2;
            }
            return isMovieApp;
        }
    };
    public static final AppFilter FILTER_PHOTOS = new AppFilter() { // from class: com.android.settingslib.applications.ApplicationsState.20
        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry entry) {
            boolean isPhotosApp;
            synchronized (entry) {
                isPhotosApp = entry.info.category == 3;
            }
            return isPhotosApp;
        }
    };
    public static final AppFilter FILTER_OTHER_APPS = new AppFilter() { // from class: com.android.settingslib.applications.ApplicationsState.21
        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public void init() {
        }

        @Override // com.android.settingslib.applications.ApplicationsState.AppFilter
        public boolean filterApp(AppEntry entry) {
            boolean isCategorized;
            synchronized (entry) {
                if (!ApplicationsState.FILTER_AUDIO.filterApp(entry) && !ApplicationsState.FILTER_GAMES.filterApp(entry) && !ApplicationsState.FILTER_MOVIES.filterApp(entry) && !ApplicationsState.FILTER_PHOTOS.filterApp(entry)) {
                    isCategorized = false;
                }
                isCategorized = true;
            }
            return !isCategorized;
        }
    };

    /* loaded from: classes.dex */
    public interface Callbacks {
        void onAllSizesComputed();

        void onLauncherInfoChanged();

        void onLoadEntriesCompleted();

        void onPackageIconChanged();

        void onPackageListChanged();

        void onPackageSizeChanged(String str);

        void onRebuildComplete(ArrayList<AppEntry> arrayList);

        void onRunningStateChanged(boolean z);
    }

    /* loaded from: classes.dex */
    public static class SizeInfo {
    }

    void doResumeIfNeededLocked() {
        if (this.mResumed) {
            return;
        }
        this.mResumed = true;
        if (this.mPackageIntentReceiver == null) {
            this.mPackageIntentReceiver = new PackageIntentReceiver();
            this.mPackageIntentReceiver.registerReceiver();
        }
        this.mApplications = new ArrayList();
        for (UserInfo user : this.mUm.getProfiles(UserHandle.myUserId())) {
            try {
                if (this.mEntriesMap.indexOfKey(user.id) < 0) {
                    this.mEntriesMap.put(user.id, new HashMap<>());
                }
                ParceledListSlice<ApplicationInfo> list = this.mIpm.getInstalledApplications(user.isAdmin() ? this.mAdminRetrieveFlags : this.mRetrieveFlags, user.id);
                this.mApplications.addAll(list.getList());
            } catch (RemoteException e) {
            }
        }
        int i = 0;
        if (this.mInterestingConfigChanges.applyNewConfig(this.mContext.getResources())) {
            clearEntries();
        } else {
            for (int i2 = 0; i2 < this.mAppEntries.size(); i2++) {
                this.mAppEntries.get(i2).sizeStale = true;
            }
        }
        this.mHaveDisabledApps = false;
        this.mHaveInstantApps = false;
        while (true) {
            int i3 = i;
            if (i3 >= this.mApplications.size()) {
                break;
            }
            ApplicationInfo info = this.mApplications.get(i3);
            if (!info.enabled) {
                if (info.enabledSetting != 3) {
                    this.mApplications.remove(i3);
                    i3--;
                    i = i3 + 1;
                } else {
                    this.mHaveDisabledApps = true;
                }
            }
            if (!this.mHaveInstantApps && AppUtils.isInstant(info)) {
                this.mHaveInstantApps = true;
            }
            int userId = UserHandle.getUserId(info.uid);
            AppEntry entry = this.mEntriesMap.get(userId).get(info.packageName);
            if (entry != null) {
                entry.info = info;
            }
            i = i3 + 1;
        }
        if (this.mAppEntries.size() > this.mApplications.size()) {
            clearEntries();
        }
        this.mCurComputingSizePkg = null;
        if (!this.mBackgroundHandler.hasMessages(2)) {
            this.mBackgroundHandler.sendEmptyMessage(2);
        }
    }

    void clearEntries() {
        for (int i = 0; i < this.mEntriesMap.size(); i++) {
            this.mEntriesMap.valueAt(i).clear();
        }
        this.mAppEntries.clear();
    }

    void doPauseIfNeededLocked() {
        if (!this.mResumed) {
            return;
        }
        for (int i = 0; i < this.mSessions.size(); i++) {
            if (this.mSessions.get(i).mResumed) {
                return;
            }
        }
        doPauseLocked();
    }

    void doPauseLocked() {
        this.mResumed = false;
        if (this.mPackageIntentReceiver != null) {
            this.mPackageIntentReceiver.unregisterReceiver();
            this.mPackageIntentReceiver = null;
        }
    }

    int indexOfApplicationInfoLocked(String pkgName, int userId) {
        for (int i = this.mApplications.size() - 1; i >= 0; i--) {
            ApplicationInfo appInfo = this.mApplications.get(i);
            if (appInfo.packageName.equals(pkgName) && UserHandle.getUserId(appInfo.uid) == userId) {
                return i;
            }
        }
        return -1;
    }

    void addPackage(String pkgName, int userId) {
        try {
            synchronized (this.mEntriesMap) {
                if (this.mResumed) {
                    if (indexOfApplicationInfoLocked(pkgName, userId) >= 0) {
                        return;
                    }
                    ApplicationInfo info = this.mIpm.getApplicationInfo(pkgName, this.mUm.isUserAdmin(userId) ? this.mAdminRetrieveFlags : this.mRetrieveFlags, userId);
                    if (info == null) {
                        return;
                    }
                    if (!info.enabled) {
                        if (info.enabledSetting != 3) {
                            return;
                        }
                        this.mHaveDisabledApps = true;
                    }
                    if (AppUtils.isInstant(info)) {
                        this.mHaveInstantApps = true;
                    }
                    this.mApplications.add(info);
                    if (!this.mBackgroundHandler.hasMessages(2)) {
                        this.mBackgroundHandler.sendEmptyMessage(2);
                    }
                    if (!this.mMainHandler.hasMessages(2)) {
                        this.mMainHandler.sendEmptyMessage(2);
                    }
                }
            }
        } catch (RemoteException e) {
        }
    }

    public void removePackage(String pkgName, int userId) {
        synchronized (this.mEntriesMap) {
            int idx = indexOfApplicationInfoLocked(pkgName, userId);
            if (idx >= 0) {
                AppEntry entry = this.mEntriesMap.get(userId).get(pkgName);
                if (entry != null) {
                    this.mEntriesMap.get(userId).remove(pkgName);
                    this.mAppEntries.remove(entry);
                }
                ApplicationInfo info = this.mApplications.get(idx);
                this.mApplications.remove(idx);
                if (!info.enabled) {
                    this.mHaveDisabledApps = false;
                    Iterator<ApplicationInfo> it = this.mApplications.iterator();
                    while (true) {
                        if (!it.hasNext()) {
                            break;
                        }
                        ApplicationInfo otherInfo = it.next();
                        if (!otherInfo.enabled) {
                            this.mHaveDisabledApps = true;
                            break;
                        }
                    }
                }
                if (AppUtils.isInstant(info)) {
                    this.mHaveInstantApps = false;
                    Iterator<ApplicationInfo> it2 = this.mApplications.iterator();
                    while (true) {
                        if (!it2.hasNext()) {
                            break;
                        }
                        ApplicationInfo otherInfo2 = it2.next();
                        if (AppUtils.isInstant(otherInfo2)) {
                            this.mHaveInstantApps = true;
                            break;
                        }
                    }
                }
                if (!this.mMainHandler.hasMessages(2)) {
                    this.mMainHandler.sendEmptyMessage(2);
                }
            }
        }
    }

    public void invalidatePackage(String pkgName, int userId) {
        removePackage(pkgName, userId);
        addPackage(pkgName, userId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void addUser(int userId) {
        int[] profileIds = this.mUm.getProfileIdsWithDisabled(UserHandle.myUserId());
        if (ArrayUtils.contains(profileIds, userId)) {
            synchronized (this.mEntriesMap) {
                this.mEntriesMap.put(userId, new HashMap<>());
                if (this.mResumed) {
                    doPauseLocked();
                    doResumeIfNeededLocked();
                }
                if (!this.mMainHandler.hasMessages(2)) {
                    this.mMainHandler.sendEmptyMessage(2);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void removeUser(int userId) {
        synchronized (this.mEntriesMap) {
            HashMap<String, AppEntry> userMap = this.mEntriesMap.get(userId);
            if (userMap != null) {
                for (AppEntry appEntry : userMap.values()) {
                    this.mAppEntries.remove(appEntry);
                    this.mApplications.remove(appEntry.info);
                }
                this.mEntriesMap.remove(userId);
                if (!this.mMainHandler.hasMessages(2)) {
                    this.mMainHandler.sendEmptyMessage(2);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public AppEntry getEntryLocked(ApplicationInfo info) {
        int userId = UserHandle.getUserId(info.uid);
        AppEntry entry = this.mEntriesMap.get(userId).get(info.packageName);
        if (entry == null) {
            Context context = this.mContext;
            long j = this.mCurId;
            this.mCurId = 1 + j;
            AppEntry entry2 = new AppEntry(context, info, j);
            this.mEntriesMap.get(userId).put(info.packageName, entry2);
            this.mAppEntries.add(entry2);
            return entry2;
        } else if (entry.info != info) {
            entry.info = info;
            return entry;
        } else {
            return entry;
        }
    }

    void rebuildActiveSessions() {
        synchronized (this.mEntriesMap) {
            if (this.mSessionsChanged) {
                this.mActiveSessions.clear();
                for (int i = 0; i < this.mSessions.size(); i++) {
                    Session s = this.mSessions.get(i);
                    if (s.mResumed) {
                        this.mActiveSessions.add(new WeakReference<>(s));
                    }
                }
            }
        }
    }

    /* loaded from: classes.dex */
    public class Session implements LifecycleObserver {
        final Callbacks mCallbacks;
        private int mFlags;
        private final boolean mHasLifecycle;
        ArrayList<AppEntry> mLastAppList;
        boolean mRebuildAsync;
        Comparator<AppEntry> mRebuildComparator;
        AppFilter mRebuildFilter;
        boolean mRebuildForeground;
        boolean mRebuildRequested;
        ArrayList<AppEntry> mRebuildResult;
        final Object mRebuildSync;
        boolean mResumed;
        final /* synthetic */ ApplicationsState this$0;

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        public void onResume() {
            synchronized (this.this$0.mEntriesMap) {
                if (!this.mResumed) {
                    this.mResumed = true;
                    this.this$0.mSessionsChanged = true;
                    this.this$0.doResumeIfNeededLocked();
                }
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        public void onPause() {
            synchronized (this.this$0.mEntriesMap) {
                if (this.mResumed) {
                    this.mResumed = false;
                    this.this$0.mSessionsChanged = true;
                    this.this$0.mBackgroundHandler.removeMessages(1, this);
                    this.this$0.doPauseIfNeededLocked();
                }
            }
        }

        void handleRebuildList() {
            List<AppEntry> apps;
            synchronized (this.mRebuildSync) {
                if (this.mRebuildRequested) {
                    AppFilter filter = this.mRebuildFilter;
                    Comparator<AppEntry> comparator = this.mRebuildComparator;
                    this.mRebuildRequested = false;
                    this.mRebuildFilter = null;
                    this.mRebuildComparator = null;
                    if (this.mRebuildForeground) {
                        Process.setThreadPriority(-2);
                        this.mRebuildForeground = false;
                    }
                    if (filter != null) {
                        filter.init(this.this$0.mContext);
                    }
                    synchronized (this.this$0.mEntriesMap) {
                        apps = new ArrayList<>(this.this$0.mAppEntries);
                    }
                    ArrayList<AppEntry> filteredApps = new ArrayList<>();
                    for (int i = 0; i < apps.size(); i++) {
                        AppEntry entry = apps.get(i);
                        if (entry != null && (filter == null || filter.filterApp(entry))) {
                            synchronized (this.this$0.mEntriesMap) {
                                if (comparator != null) {
                                    try {
                                        entry.ensureLabel(this.this$0.mContext);
                                    } finally {
                                    }
                                }
                                filteredApps.add(entry);
                            }
                        }
                    }
                    if (comparator != null) {
                        synchronized (this.this$0.mEntriesMap) {
                            Collections.sort(filteredApps, comparator);
                        }
                    }
                    synchronized (this.mRebuildSync) {
                        if (!this.mRebuildRequested) {
                            this.mLastAppList = filteredApps;
                            if (!this.mRebuildAsync) {
                                this.mRebuildResult = filteredApps;
                                this.mRebuildSync.notifyAll();
                            } else if (!this.this$0.mMainHandler.hasMessages(1, this)) {
                                Message msg = this.this$0.mMainHandler.obtainMessage(1, this);
                                this.this$0.mMainHandler.sendMessage(msg);
                            }
                        }
                    }
                    Process.setThreadPriority(10);
                }
            }
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        public void onDestroy() {
            if (!this.mHasLifecycle) {
                onPause();
            }
            synchronized (this.this$0.mEntriesMap) {
                this.this$0.mSessions.remove(this);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class MainHandler extends Handler {
        final /* synthetic */ ApplicationsState this$0;

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            this.this$0.rebuildActiveSessions();
            switch (msg.what) {
                case 1:
                    Session s = (Session) msg.obj;
                    Iterator<WeakReference<Session>> it = this.this$0.mActiveSessions.iterator();
                    while (it.hasNext()) {
                        WeakReference<Session> sessionRef = it.next();
                        Session session = sessionRef.get();
                        if (session != null && session == s) {
                            s.mCallbacks.onRebuildComplete(s.mLastAppList);
                        }
                    }
                    return;
                case 2:
                    Iterator<WeakReference<Session>> it2 = this.this$0.mActiveSessions.iterator();
                    while (it2.hasNext()) {
                        WeakReference<Session> sessionRef2 = it2.next();
                        Session session2 = sessionRef2.get();
                        if (session2 != null) {
                            session2.mCallbacks.onPackageListChanged();
                        }
                    }
                    return;
                case 3:
                    Iterator<WeakReference<Session>> it3 = this.this$0.mActiveSessions.iterator();
                    while (it3.hasNext()) {
                        WeakReference<Session> sessionRef3 = it3.next();
                        Session session3 = sessionRef3.get();
                        if (session3 != null) {
                            session3.mCallbacks.onPackageIconChanged();
                        }
                    }
                    return;
                case 4:
                    Iterator<WeakReference<Session>> it4 = this.this$0.mActiveSessions.iterator();
                    while (it4.hasNext()) {
                        WeakReference<Session> sessionRef4 = it4.next();
                        Session session4 = sessionRef4.get();
                        if (session4 != null) {
                            session4.mCallbacks.onPackageSizeChanged((String) msg.obj);
                        }
                    }
                    return;
                case 5:
                    Iterator<WeakReference<Session>> it5 = this.this$0.mActiveSessions.iterator();
                    while (it5.hasNext()) {
                        WeakReference<Session> sessionRef5 = it5.next();
                        Session session5 = sessionRef5.get();
                        if (session5 != null) {
                            session5.mCallbacks.onAllSizesComputed();
                        }
                    }
                    return;
                case 6:
                    Iterator<WeakReference<Session>> it6 = this.this$0.mActiveSessions.iterator();
                    while (it6.hasNext()) {
                        WeakReference<Session> sessionRef6 = it6.next();
                        Session session6 = sessionRef6.get();
                        if (session6 != null) {
                            session6.mCallbacks.onRunningStateChanged(msg.arg1 != 0);
                        }
                    }
                    return;
                case 7:
                    Iterator<WeakReference<Session>> it7 = this.this$0.mActiveSessions.iterator();
                    while (it7.hasNext()) {
                        WeakReference<Session> sessionRef7 = it7.next();
                        Session session7 = sessionRef7.get();
                        if (session7 != null) {
                            session7.mCallbacks.onLauncherInfoChanged();
                        }
                    }
                    return;
                case 8:
                    Iterator<WeakReference<Session>> it8 = this.this$0.mActiveSessions.iterator();
                    while (it8.hasNext()) {
                        WeakReference<Session> sessionRef8 = it8.next();
                        Session session8 = sessionRef8.get();
                        if (session8 != null) {
                            session8.mCallbacks.onLoadEntriesCompleted();
                        }
                    }
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class BackgroundHandler extends Handler {
        boolean mRunning;
        final IPackageStatsObserver.Stub mStatsObserver;
        final /* synthetic */ ApplicationsState this$0;

        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:206:0x03ea -> B:207:0x03eb). Please submit an issue!!! */
        /* JADX WARN: Unsupported multi-entry loop pattern (BACK_EDGE: B:226:? -> B:134:0x028d). Please submit an issue!!! */
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            int numDone;
            ArrayList<Session> rebuildingSessions;
            Intent launchIntent;
            ArrayList<Session> rebuildingSessions2;
            Intent launchIntent2;
            StringBuilder sb;
            ArrayList<Session> rebuildingSessions3 = null;
            synchronized (this.this$0.mRebuildingSessions) {
                try {
                    if (this.this$0.mRebuildingSessions.size() > 0) {
                        rebuildingSessions3 = new ArrayList<>(this.this$0.mRebuildingSessions);
                        this.this$0.mRebuildingSessions.clear();
                    }
                    try {
                        int i = 0;
                        if (rebuildingSessions3 != null) {
                            for (int i2 = 0; i2 < rebuildingSessions3.size(); i2++) {
                                rebuildingSessions3.get(i2).handleRebuildList();
                            }
                        }
                        int flags = getCombinedSessionFlags(this.this$0.mSessions);
                        int i3 = 8388608;
                        boolean z = true;
                        switch (msg.what) {
                            case 1:
                                return;
                            case 2:
                                synchronized (this.this$0.mEntriesMap) {
                                    numDone = 0;
                                    int numDone2 = 0;
                                    while (numDone2 < this.this$0.mApplications.size() && numDone < 6) {
                                        if (!this.mRunning) {
                                            this.mRunning = true;
                                            Message m = this.this$0.mMainHandler.obtainMessage(6, 1);
                                            this.this$0.mMainHandler.sendMessage(m);
                                        }
                                        ApplicationInfo info = this.this$0.mApplications.get(numDone2);
                                        int userId = UserHandle.getUserId(info.uid);
                                        if (this.this$0.mEntriesMap.get(userId).get(info.packageName) == null) {
                                            numDone++;
                                            this.this$0.getEntryLocked(info);
                                        }
                                        if (userId != 0) {
                                            if (this.this$0.mEntriesMap.indexOfKey(0) >= 0) {
                                                AppEntry entry = this.this$0.mEntriesMap.get(0).get(info.packageName);
                                                if (entry != null && !ApplicationsState.hasFlag(entry.info.flags, i3)) {
                                                    this.this$0.mEntriesMap.get(0).remove(info.packageName);
                                                    this.this$0.mAppEntries.remove(entry);
                                                }
                                            }
                                            numDone2++;
                                            i3 = 8388608;
                                        }
                                        numDone2++;
                                        i3 = 8388608;
                                    }
                                }
                                if (numDone >= 6) {
                                    sendEmptyMessage(2);
                                    return;
                                }
                                if (!this.this$0.mMainHandler.hasMessages(8)) {
                                    this.this$0.mMainHandler.sendEmptyMessage(8);
                                }
                                sendEmptyMessage(3);
                                return;
                            case 3:
                                if (ApplicationsState.hasFlag(flags, 1)) {
                                    List<ResolveInfo> homeActivities = new ArrayList<>();
                                    this.this$0.mPm.getHomeActivities(homeActivities);
                                    synchronized (this.this$0.mEntriesMap) {
                                        int entryCount = this.this$0.mEntriesMap.size();
                                        int i4 = 0;
                                        while (true) {
                                            int i5 = i4;
                                            if (i5 < entryCount) {
                                                HashMap<String, AppEntry> userEntries = this.this$0.mEntriesMap.valueAt(i5);
                                                for (ResolveInfo activity : homeActivities) {
                                                    AppEntry entry2 = userEntries.get(activity.activityInfo.packageName);
                                                    if (entry2 != null) {
                                                        entry2.isHomeApp = true;
                                                    }
                                                }
                                                i4 = i5 + 1;
                                            }
                                        }
                                    }
                                }
                                sendEmptyMessage(4);
                                return;
                            case 4:
                            case 5:
                                if ((msg.what == 4 && ApplicationsState.hasFlag(flags, 8)) || (msg.what == 5 && ApplicationsState.hasFlag(flags, 16))) {
                                    Intent launchIntent3 = new Intent("android.intent.action.MAIN", (Uri) null);
                                    launchIntent3.addCategory(msg.what == 4 ? "android.intent.category.LAUNCHER" : "android.intent.category.LEANBACK_LAUNCHER");
                                    int i6 = 0;
                                    while (i6 < this.this$0.mEntriesMap.size()) {
                                        int userId2 = this.this$0.mEntriesMap.keyAt(i6);
                                        List<ResolveInfo> intents = this.this$0.mPm.queryIntentActivitiesAsUser(launchIntent3, 786944, userId2);
                                        synchronized (this.this$0.mEntriesMap) {
                                            try {
                                                HashMap<String, AppEntry> userEntries2 = this.this$0.mEntriesMap.valueAt(i6);
                                                int N = intents.size();
                                                int j = i;
                                                while (true) {
                                                    int j2 = j;
                                                    int N2 = N;
                                                    if (j2 < N2) {
                                                        ResolveInfo resolveInfo = intents.get(j2);
                                                        String packageName = resolveInfo.activityInfo.packageName;
                                                        AppEntry entry3 = userEntries2.get(packageName);
                                                        if (entry3 != null) {
                                                            try {
                                                                entry3.hasLauncherEntry = z;
                                                                rebuildingSessions2 = rebuildingSessions3;
                                                                try {
                                                                    entry3.launcherEntryEnabled = resolveInfo.activityInfo.enabled | entry3.launcherEntryEnabled;
                                                                    launchIntent2 = launchIntent3;
                                                                } catch (Throwable th) {
                                                                    th = th;
                                                                    throw th;
                                                                }
                                                            } catch (Throwable th2) {
                                                                th = th2;
                                                            }
                                                        } else {
                                                            rebuildingSessions2 = rebuildingSessions3;
                                                            try {
                                                                sb = new StringBuilder();
                                                                launchIntent2 = launchIntent3;
                                                            } catch (Throwable th3) {
                                                                th = th3;
                                                                throw th;
                                                            }
                                                            try {
                                                                sb.append("Cannot find pkg: ");
                                                                sb.append(packageName);
                                                                sb.append(" on user ");
                                                                sb.append(userId2);
                                                                Log.w("ApplicationsState", sb.toString());
                                                            } catch (Throwable th4) {
                                                                th = th4;
                                                                throw th;
                                                            }
                                                        }
                                                        j = j2 + 1;
                                                        N = N2;
                                                        rebuildingSessions3 = rebuildingSessions2;
                                                        launchIntent3 = launchIntent2;
                                                        z = true;
                                                    } else {
                                                        rebuildingSessions = rebuildingSessions3;
                                                        launchIntent = launchIntent3;
                                                    }
                                                }
                                            } catch (Throwable th5) {
                                                th = th5;
                                            }
                                        }
                                        i6++;
                                        rebuildingSessions3 = rebuildingSessions;
                                        launchIntent3 = launchIntent;
                                        i = 0;
                                        z = true;
                                    }
                                    if (!this.this$0.mMainHandler.hasMessages(7)) {
                                        this.this$0.mMainHandler.sendEmptyMessage(7);
                                    }
                                }
                                if (msg.what == 4) {
                                    sendEmptyMessage(5);
                                    return;
                                } else {
                                    sendEmptyMessage(6);
                                    return;
                                }
                            case 6:
                                if (ApplicationsState.hasFlag(flags, 2)) {
                                    int numDone3 = 0;
                                    synchronized (this.this$0.mEntriesMap) {
                                        while (true) {
                                            int i7 = i;
                                            if (i7 < this.this$0.mAppEntries.size() && numDone3 < 2) {
                                                AppEntry entry4 = this.this$0.mAppEntries.get(i7);
                                                if (entry4.icon == null || !entry4.mounted) {
                                                    synchronized (entry4) {
                                                        if (entry4.ensureIconLocked(this.this$0.mContext, this.this$0.mDrawableFactory)) {
                                                            if (!this.mRunning) {
                                                                this.mRunning = true;
                                                                Message m2 = this.this$0.mMainHandler.obtainMessage(6, 1);
                                                                this.this$0.mMainHandler.sendMessage(m2);
                                                            }
                                                            numDone3++;
                                                        }
                                                    }
                                                }
                                                i = i7 + 1;
                                            }
                                        }
                                    }
                                    if (numDone3 > 0 && !this.this$0.mMainHandler.hasMessages(3)) {
                                        this.this$0.mMainHandler.sendEmptyMessage(3);
                                    }
                                    if (numDone3 >= 2) {
                                        sendEmptyMessage(6);
                                        break;
                                    }
                                }
                                sendEmptyMessage(7);
                                break;
                            case 7:
                                if (ApplicationsState.hasFlag(flags, 4)) {
                                    synchronized (this.this$0.mEntriesMap) {
                                        if (this.this$0.mCurComputingSizePkg == null) {
                                            long now = SystemClock.uptimeMillis();
                                            for (int i8 = 0; i8 < this.this$0.mAppEntries.size(); i8++) {
                                                AppEntry entry5 = this.this$0.mAppEntries.get(i8);
                                                if (ApplicationsState.hasFlag(entry5.info.flags, 8388608) && (entry5.size == -1 || entry5.sizeStale)) {
                                                    if (entry5.sizeLoadStart == 0 || entry5.sizeLoadStart < now - 20000) {
                                                        if (!this.mRunning) {
                                                            this.mRunning = true;
                                                            Message m3 = this.this$0.mMainHandler.obtainMessage(6, 1);
                                                            this.this$0.mMainHandler.sendMessage(m3);
                                                        }
                                                        entry5.sizeLoadStart = now;
                                                        this.this$0.mCurComputingSizeUuid = entry5.info.storageUuid;
                                                        this.this$0.mCurComputingSizePkg = entry5.info.packageName;
                                                        this.this$0.mCurComputingSizeUserId = UserHandle.getUserId(entry5.info.uid);
                                                        this.this$0.mBackgroundHandler.post(new Runnable() { // from class: com.android.settingslib.applications.-$$Lambda$ApplicationsState$BackgroundHandler$7jhXQzAcRoT6ACDzmPBTQMi7Ldc
                                                            @Override // java.lang.Runnable
                                                            public final void run() {
                                                                ApplicationsState.BackgroundHandler.lambda$handleMessage$0(ApplicationsState.BackgroundHandler.this);
                                                            }
                                                        });
                                                    }
                                                    return;
                                                }
                                            }
                                            if (!this.this$0.mMainHandler.hasMessages(5)) {
                                                this.this$0.mMainHandler.sendEmptyMessage(5);
                                                this.mRunning = false;
                                                Message m4 = this.this$0.mMainHandler.obtainMessage(6, 0);
                                                this.this$0.mMainHandler.sendMessage(m4);
                                            }
                                            break;
                                        } else {
                                            return;
                                        }
                                    }
                                }
                                break;
                            default:
                                return;
                        }
                    } catch (Throwable th6) {
                        th = th6;
                        throw th;
                    }
                } catch (Throwable th7) {
                    th = th7;
                }
            }
        }

        public static /* synthetic */ void lambda$handleMessage$0(BackgroundHandler backgroundHandler) {
            try {
                StorageStats stats = backgroundHandler.this$0.mStats.queryStatsForPackage(backgroundHandler.this$0.mCurComputingSizeUuid, backgroundHandler.this$0.mCurComputingSizePkg, UserHandle.of(backgroundHandler.this$0.mCurComputingSizeUserId));
                PackageStats legacy = new PackageStats(backgroundHandler.this$0.mCurComputingSizePkg, backgroundHandler.this$0.mCurComputingSizeUserId);
                legacy.codeSize = stats.getCodeBytes();
                legacy.dataSize = stats.getDataBytes();
                legacy.cacheSize = stats.getCacheBytes();
                try {
                    backgroundHandler.mStatsObserver.onGetStatsCompleted(legacy, true);
                } catch (RemoteException e) {
                }
            } catch (PackageManager.NameNotFoundException | IOException e2) {
                Log.w("ApplicationsState", "Failed to query stats: " + e2);
                try {
                    backgroundHandler.mStatsObserver.onGetStatsCompleted((PackageStats) null, false);
                } catch (RemoteException e3) {
                }
            }
        }

        private int getCombinedSessionFlags(List<Session> sessions) {
            int flags;
            synchronized (this.this$0.mEntriesMap) {
                flags = 0;
                for (Session session : sessions) {
                    flags |= session.mFlags;
                }
            }
            return flags;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class PackageIntentReceiver extends BroadcastReceiver {
        private PackageIntentReceiver() {
        }

        void registerReceiver() {
            IntentFilter filter = new IntentFilter("android.intent.action.PACKAGE_ADDED");
            filter.addAction("android.intent.action.PACKAGE_REMOVED");
            filter.addAction("android.intent.action.PACKAGE_CHANGED");
            filter.addDataScheme("package");
            ApplicationsState.this.mContext.registerReceiver(this, filter);
            IntentFilter sdFilter = new IntentFilter();
            sdFilter.addAction("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE");
            sdFilter.addAction("android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE");
            ApplicationsState.this.mContext.registerReceiver(this, sdFilter);
            IntentFilter userFilter = new IntentFilter();
            userFilter.addAction("android.intent.action.USER_ADDED");
            userFilter.addAction("android.intent.action.USER_REMOVED");
            ApplicationsState.this.mContext.registerReceiver(this, userFilter);
        }

        void unregisterReceiver() {
            ApplicationsState.this.mContext.unregisterReceiver(this);
        }

        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String actionStr = intent.getAction();
            int i = 0;
            if ("android.intent.action.PACKAGE_ADDED".equals(actionStr)) {
                Uri data = intent.getData();
                String pkgName = data.getEncodedSchemeSpecificPart();
                while (i < ApplicationsState.this.mEntriesMap.size()) {
                    ApplicationsState.this.addPackage(pkgName, ApplicationsState.this.mEntriesMap.keyAt(i));
                    i++;
                }
            } else if ("android.intent.action.PACKAGE_REMOVED".equals(actionStr)) {
                Uri data2 = intent.getData();
                String pkgName2 = data2.getEncodedSchemeSpecificPart();
                while (i < ApplicationsState.this.mEntriesMap.size()) {
                    ApplicationsState.this.removePackage(pkgName2, ApplicationsState.this.mEntriesMap.keyAt(i));
                    i++;
                }
            } else if ("android.intent.action.PACKAGE_CHANGED".equals(actionStr)) {
                Uri data3 = intent.getData();
                String pkgName3 = data3.getEncodedSchemeSpecificPart();
                while (i < ApplicationsState.this.mEntriesMap.size()) {
                    ApplicationsState.this.invalidatePackage(pkgName3, ApplicationsState.this.mEntriesMap.keyAt(i));
                    i++;
                }
            } else if ("android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE".equals(actionStr) || "android.intent.action.EXTERNAL_APPLICATIONS_UNAVAILABLE".equals(actionStr)) {
                String[] pkgList = intent.getStringArrayExtra("android.intent.extra.changed_package_list");
                if (pkgList == null || pkgList.length == 0) {
                    return;
                }
                boolean avail = "android.intent.action.EXTERNAL_APPLICATIONS_AVAILABLE".equals(actionStr);
                if (avail) {
                    for (String pkgName4 : pkgList) {
                        for (int i2 = 0; i2 < ApplicationsState.this.mEntriesMap.size(); i2++) {
                            ApplicationsState.this.invalidatePackage(pkgName4, ApplicationsState.this.mEntriesMap.keyAt(i2));
                        }
                    }
                }
            } else if ("android.intent.action.USER_ADDED".equals(actionStr)) {
                ApplicationsState.this.addUser(intent.getIntExtra("android.intent.extra.user_handle", -10000));
            } else if ("android.intent.action.USER_REMOVED".equals(actionStr)) {
                ApplicationsState.this.removeUser(intent.getIntExtra("android.intent.extra.user_handle", -10000));
            }
        }
    }

    /* loaded from: classes.dex */
    public static class AppEntry extends SizeInfo {
        public final File apkFile;
        public long externalSize;
        public boolean hasLauncherEntry;
        public Drawable icon;
        public final long id;
        public ApplicationInfo info;
        public long internalSize;
        public boolean isHomeApp;
        public String label;
        public boolean launcherEntryEnabled;
        public boolean mounted;
        public long sizeLoadStart;
        public long size = -1;
        public boolean sizeStale = true;

        public AppEntry(Context context, ApplicationInfo info, long id) {
            this.apkFile = new File(info.sourceDir);
            this.id = id;
            this.info = info;
            ensureLabel(context);
        }

        public void ensureLabel(Context context) {
            if (this.label == null || !this.mounted) {
                if (!this.apkFile.exists()) {
                    this.mounted = false;
                    this.label = this.info.packageName;
                    return;
                }
                this.mounted = true;
                CharSequence label = this.info.loadLabel(context.getPackageManager());
                this.label = label != null ? label.toString() : this.info.packageName;
            }
        }

        boolean ensureIconLocked(Context context, IconDrawableFactory drawableFactory) {
            if (this.icon == null) {
                if (this.apkFile.exists()) {
                    this.icon = drawableFactory.getBadgedIcon(this.info);
                    return true;
                }
                this.mounted = false;
                this.icon = context.getDrawable(17303567);
            } else if (!this.mounted && this.apkFile.exists()) {
                this.mounted = true;
                this.icon = drawableFactory.getBadgedIcon(this.info);
                return true;
            }
            return false;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static boolean hasFlag(int flags, int flag) {
        return (flags & flag) != 0;
    }

    /* loaded from: classes.dex */
    public interface AppFilter {
        boolean filterApp(AppEntry appEntry);

        void init();

        default void init(Context context) {
            init();
        }
    }
}
