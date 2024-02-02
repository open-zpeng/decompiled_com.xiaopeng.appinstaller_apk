package com.xiaopeng.appinstaller.permission.model;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionInfo;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.IconDrawableFactory;
import android.util.Log;
import android.util.SparseArray;
import com.xiaopeng.appinstaller.R;
import com.xiaopeng.appinstaller.permission.utils.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class PermissionApps {
    private ArrayMap<String, PermissionApp> mAppLookup;
    private final PmCache mCache;
    private final Callback mCallback;
    private final Context mContext;
    private final String mGroupName;
    private Drawable mIcon;
    private CharSequence mLabel;
    private List<PermissionApp> mPermApps;
    private final PackageManager mPm;
    private boolean mRefreshing;
    private boolean mSkipUi;

    /* loaded from: classes.dex */
    public interface Callback {
        void onPermissionsLoaded(PermissionApps permissionApps);
    }

    public PermissionApps(Context context, String groupName, Callback callback) {
        this(context, groupName, callback, null);
    }

    public PermissionApps(Context context, String groupName, Callback callback, PmCache cache) {
        this.mCache = cache;
        this.mContext = context;
        this.mPm = this.mContext.getPackageManager();
        this.mGroupName = groupName;
        this.mCallback = callback;
        loadGroupInfo();
    }

    public String getGroupName() {
        return this.mGroupName;
    }

    public void refresh(boolean getUiInfo) {
        if (this.mCallback == null) {
            throw new IllegalStateException("callback needs to be set");
        }
        if (!this.mRefreshing) {
            this.mRefreshing = true;
            this.mSkipUi = !getUiInfo;
            new PermissionAppsLoader().execute(new Void[0]);
        }
    }

    public void refreshSync() {
        this.mSkipUi = true;
        createMap(loadPermissionApps());
    }

    public int getGrantedCount(ArraySet<String> launcherPkgs) {
        int count = 0;
        for (PermissionApp app : this.mPermApps) {
            if (Utils.shouldShowPermission(app) && !Utils.isSystem(app, launcherPkgs) && app.areRuntimePermissionsGranted()) {
                count++;
            }
        }
        return count;
    }

    public int getTotalCount(ArraySet<String> launcherPkgs) {
        int count = 0;
        for (PermissionApp app : this.mPermApps) {
            if (Utils.shouldShowPermission(app) && !Utils.isSystem(app, launcherPkgs)) {
                count++;
            }
        }
        return count;
    }

    public List<PermissionApp> getApps() {
        return this.mPermApps;
    }

    public PermissionApp getApp(String key) {
        return this.mAppLookup.get(key);
    }

    public CharSequence getLabel() {
        return this.mLabel;
    }

    public Drawable getIcon() {
        return this.mIcon;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public List<PermissionApp> loadPermissionApps() {
        PackageItemInfo groupInfo;
        UserManager userManager;
        Iterator<UserHandle> it;
        AppPermissionGroup group;
        Drawable icon;
        PermissionApps permissionApps = this;
        PackageItemInfo groupInfo2 = permissionApps.getGroupInfo(permissionApps.mGroupName);
        if (groupInfo2 == null) {
            return Collections.emptyList();
        }
        List<PermissionInfo> groupPermInfos = permissionApps.getGroupPermissionInfos(permissionApps.mGroupName);
        if (groupPermInfos == null) {
            return Collections.emptyList();
        }
        ArrayList<PermissionApp> permApps = new ArrayList<>();
        IconDrawableFactory iconFactory = IconDrawableFactory.newInstance(permissionApps.mContext);
        UserManager userManager2 = (UserManager) permissionApps.mContext.getSystemService(UserManager.class);
        Iterator<UserHandle> it2 = userManager2.getUserProfiles().iterator();
        while (it2.hasNext()) {
            UserHandle user = it2.next();
            List<PackageInfo> apps = permissionApps.mCache != null ? permissionApps.mCache.getPackages(user.getIdentifier()) : permissionApps.mPm.getInstalledPackagesAsUser(4096, user.getIdentifier());
            int N = apps.size();
            int i = 0;
            while (i < N) {
                PackageInfo app = apps.get(i);
                if (app.requestedPermissions == null) {
                    groupInfo = groupInfo2;
                    userManager = userManager2;
                    it = it2;
                } else {
                    int j = 0;
                    while (true) {
                        if (j >= app.requestedPermissions.length) {
                            groupInfo = groupInfo2;
                            userManager = userManager2;
                            it = it2;
                            break;
                        }
                        String requestedPerm = app.requestedPermissions[j];
                        PermissionInfo requestedPermissionInfo = null;
                        Iterator<PermissionInfo> it3 = groupPermInfos.iterator();
                        while (true) {
                            if (it3.hasNext()) {
                                userManager = userManager2;
                                PermissionInfo groupPermInfo = it3.next();
                                it = it2;
                                if (!requestedPerm.equals(groupPermInfo.name)) {
                                    userManager2 = userManager;
                                    it2 = it;
                                } else {
                                    requestedPermissionInfo = groupPermInfo;
                                    break;
                                }
                            } else {
                                userManager = userManager2;
                                it = it2;
                                break;
                            }
                        }
                        if (requestedPermissionInfo == null || (requestedPermissionInfo.protectionLevel & 15) != 1 || (requestedPermissionInfo.flags & 1073741824) == 0 || (requestedPermissionInfo.flags & 2) != 0 || (group = AppPermissionGroup.create(permissionApps.mContext, app, groupInfo2, groupPermInfos, user)) == null) {
                            j++;
                            userManager2 = userManager;
                            it2 = it;
                            groupInfo2 = groupInfo2;
                            permissionApps = this;
                        } else {
                            String label = permissionApps.mSkipUi ? app.packageName : app.applicationInfo.loadLabel(permissionApps.mPm).toString();
                            if (!permissionApps.mSkipUi) {
                                icon = iconFactory.getBadgedIcon(app.applicationInfo, UserHandle.getUserId(group.getApp().applicationInfo.uid));
                            } else {
                                icon = null;
                            }
                            groupInfo = groupInfo2;
                            PermissionApp permApp = new PermissionApp(app.packageName, group, label, icon, app.applicationInfo);
                            permApps.add(permApp);
                        }
                    }
                }
                i++;
                userManager2 = userManager;
                it2 = it;
                groupInfo2 = groupInfo;
                permissionApps = this;
            }
            permissionApps = this;
        }
        Collections.sort(permApps);
        return permApps;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createMap(List<PermissionApp> result) {
        this.mAppLookup = new ArrayMap<>();
        for (PermissionApp app : result) {
            this.mAppLookup.put(app.getKey(), app);
        }
        this.mPermApps = result;
    }

    private PackageItemInfo getGroupInfo(String groupName) {
        try {
            return this.mContext.getPackageManager().getPermissionGroupInfo(groupName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            try {
                return this.mContext.getPackageManager().getPermissionInfo(groupName, 0);
            } catch (PackageManager.NameNotFoundException e2) {
                return null;
            }
        }
    }

    private List<PermissionInfo> getGroupPermissionInfos(String groupName) {
        try {
            return this.mContext.getPackageManager().queryPermissionsByGroup(groupName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            try {
                PermissionInfo permissionInfo = this.mContext.getPackageManager().getPermissionInfo(groupName, 0);
                List<PermissionInfo> permissions = new ArrayList<>();
                permissions.add(permissionInfo);
                return permissions;
            } catch (PackageManager.NameNotFoundException e2) {
                return null;
            }
        }
    }

    private void loadGroupInfo() {
        PermissionInfo permInfo;
        try {
            permInfo = this.mPm.getPermissionGroupInfo(this.mGroupName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            try {
                permInfo = this.mPm.getPermissionInfo(this.mGroupName, 0);
                if ((permInfo.protectionLevel & 15) != 1) {
                    Log.w("PermissionApps", this.mGroupName + " is not a runtime permission");
                    return;
                }
            } catch (PackageManager.NameNotFoundException reallyNotFound) {
                Log.w("PermissionApps", "Can't find permission: " + this.mGroupName, reallyNotFound);
                return;
            }
        }
        this.mLabel = permInfo.loadLabel(this.mPm);
        if (((PackageItemInfo) permInfo).icon != 0) {
            this.mIcon = permInfo.loadUnbadgedIcon(this.mPm);
        } else {
            this.mIcon = this.mContext.getDrawable(R.drawable.ic_perm_device_info);
        }
        this.mIcon = Utils.applyTint(this.mContext, this.mIcon, 16843817);
    }

    /* loaded from: classes.dex */
    public static class PermissionApp implements Comparable<PermissionApp> {
        private final AppPermissionGroup mAppPermissionGroup;
        private final Drawable mIcon;
        private final ApplicationInfo mInfo;
        private final String mLabel;
        private final String mPackageName;

        public PermissionApp(String packageName, AppPermissionGroup appPermissionGroup, String label, Drawable icon, ApplicationInfo info) {
            this.mPackageName = packageName;
            this.mAppPermissionGroup = appPermissionGroup;
            this.mLabel = label;
            this.mIcon = icon;
            this.mInfo = info;
        }

        public ApplicationInfo getAppInfo() {
            return this.mInfo;
        }

        public String getKey() {
            return this.mPackageName + getUid();
        }

        public String getLabel() {
            return this.mLabel;
        }

        public Drawable getIcon() {
            return this.mIcon;
        }

        public boolean areRuntimePermissionsGranted() {
            return this.mAppPermissionGroup.areRuntimePermissionsGranted();
        }

        public void grantRuntimePermissions() {
            this.mAppPermissionGroup.grantRuntimePermissions(false);
        }

        public void revokeRuntimePermissions() {
            this.mAppPermissionGroup.revokeRuntimePermissions(false);
        }

        public boolean isPolicyFixed() {
            return this.mAppPermissionGroup.isPolicyFixed();
        }

        public boolean isSystemFixed() {
            return this.mAppPermissionGroup.isSystemFixed();
        }

        public boolean hasGrantedByDefaultPermissions() {
            return this.mAppPermissionGroup.hasGrantedByDefaultPermission();
        }

        public boolean doesSupportRuntimePermissions() {
            return this.mAppPermissionGroup.doesSupportRuntimePermissions();
        }

        public int getUserId() {
            return this.mAppPermissionGroup.getUserId();
        }

        public String getPackageName() {
            return this.mPackageName;
        }

        public AppPermissionGroup getPermissionGroup() {
            return this.mAppPermissionGroup;
        }

        @Override // java.lang.Comparable
        public int compareTo(PermissionApp another) {
            int result = this.mLabel.compareTo(another.mLabel);
            if (result == 0) {
                return getKey().compareTo(another.getKey());
            }
            return result;
        }

        public int getUid() {
            return this.mAppPermissionGroup.getApp().applicationInfo.uid;
        }
    }

    /* loaded from: classes.dex */
    private class PermissionAppsLoader extends AsyncTask<Void, Void, List<PermissionApp>> {
        private PermissionAppsLoader() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public List<PermissionApp> doInBackground(Void... args) {
            return PermissionApps.this.loadPermissionApps();
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(List<PermissionApp> result) {
            PermissionApps.this.mRefreshing = false;
            PermissionApps.this.createMap(result);
            if (PermissionApps.this.mCallback != null) {
                PermissionApps.this.mCallback.onPermissionsLoaded(PermissionApps.this);
            }
        }
    }

    /* loaded from: classes.dex */
    public static class PmCache {
        private final SparseArray<List<PackageInfo>> mPackageInfoCache = new SparseArray<>();
        private final PackageManager mPm;

        public PmCache(PackageManager pm) {
            this.mPm = pm;
        }

        public synchronized List<PackageInfo> getPackages(int userId) {
            List<PackageInfo> ret;
            ret = this.mPackageInfoCache.get(userId);
            if (ret == null) {
                ret = this.mPm.getInstalledPackagesAsUser(4096, userId);
                this.mPackageInfoCache.put(userId, ret);
            }
            return ret;
        }
    }
}
