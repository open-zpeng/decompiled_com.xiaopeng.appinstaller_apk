package com.xiaopeng.appinstaller.permission.model;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Loader;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.ArraySet;
import com.xiaopeng.appinstaller.R;
import com.xiaopeng.appinstaller.permission.model.PermissionApps;
import com.xiaopeng.appinstaller.permission.utils.Utils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
/* loaded from: classes.dex */
public final class PermissionGroups implements LoaderManager.LoaderCallbacks<List<PermissionGroup>> {
    private final PermissionsGroupsChangeCallback mCallback;
    private final Context mContext;
    private final ArrayList<PermissionGroup> mGroups = new ArrayList<>();
    private final LoaderManager mLoaderManager;

    /* loaded from: classes.dex */
    public interface PermissionsGroupsChangeCallback {
        void onPermissionGroupsChanged();
    }

    public PermissionGroups(Context context, LoaderManager loaderManager, PermissionsGroupsChangeCallback callback) {
        this.mContext = context;
        this.mLoaderManager = loaderManager;
        this.mCallback = callback;
        this.mLoaderManager.initLoader(0, null, this);
    }

    @Override // android.app.LoaderManager.LoaderCallbacks
    public Loader<List<PermissionGroup>> onCreateLoader(int id, Bundle args) {
        return new PermissionsLoader(this.mContext);
    }

    @Override // android.app.LoaderManager.LoaderCallbacks
    public void onLoadFinished(Loader<List<PermissionGroup>> loader, List<PermissionGroup> groups) {
        if (this.mGroups.equals(groups)) {
            return;
        }
        this.mGroups.clear();
        this.mGroups.addAll(groups);
        this.mCallback.onPermissionGroupsChanged();
    }

    @Override // android.app.LoaderManager.LoaderCallbacks
    public void onLoaderReset(Loader<List<PermissionGroup>> loader) {
        this.mGroups.clear();
        this.mCallback.onPermissionGroupsChanged();
    }

    public List<PermissionGroup> getGroups() {
        return this.mGroups;
    }

    public PermissionGroup getGroup(String name) {
        Iterator<PermissionGroup> it = this.mGroups.iterator();
        while (it.hasNext()) {
            PermissionGroup group = it.next();
            if (group.getName().equals(name)) {
                return group;
            }
        }
        return null;
    }

    /* loaded from: classes.dex */
    private static final class PermissionsLoader extends AsyncTaskLoader<List<PermissionGroup>> implements PackageManager.OnPermissionsChangedListener {
        public PermissionsLoader(Context context) {
            super(context);
        }

        @Override // android.content.Loader
        protected void onStartLoading() {
            getContext().getPackageManager().addOnPermissionsChangeListener(this);
            forceLoad();
        }

        @Override // android.content.Loader
        protected void onStopLoading() {
            getContext().getPackageManager().removeOnPermissionsChangeListener(this);
        }

        @Override // android.content.AsyncTaskLoader
        public List<PermissionGroup> loadInBackground() {
            List<PackageInfo> installedPackages;
            Set<String> seenPermissions;
            PackageManager packageManager;
            String[] strArr;
            PermissionsLoader permissionsLoader = this;
            ArraySet<String> launcherPkgs = Utils.getLauncherPackages(getContext());
            PermissionApps.PmCache pmCache = new PermissionApps.PmCache(getContext().getPackageManager());
            List<PermissionGroup> groups = new ArrayList<>();
            Set<String> seenPermissions2 = new ArraySet<>();
            PackageManager packageManager2 = getContext().getPackageManager();
            int i = 0;
            List<PermissionGroupInfo> groupInfos = packageManager2.getAllPermissionGroups(0);
            for (PermissionGroupInfo groupInfo : groupInfos) {
                if (isLoadInBackgroundCanceled()) {
                    return Collections.emptyList();
                }
                try {
                    List<PermissionInfo> groupPermissions = packageManager2.queryPermissionsByGroup(groupInfo.name, i);
                    boolean hasRuntimePermissions = false;
                    for (PermissionInfo groupPermission : groupPermissions) {
                        seenPermissions2.add(groupPermission.name);
                        if ((groupPermission.protectionLevel & 15) == 1 && (groupPermission.flags & 1073741824) != 0 && (groupPermission.flags & 2) == 0) {
                            hasRuntimePermissions = true;
                        }
                    }
                    if (hasRuntimePermissions) {
                        CharSequence label = permissionsLoader.loadItemInfoLabel(groupInfo);
                        Drawable icon = permissionsLoader.loadItemInfoIcon(groupInfo);
                        PermissionApps permApps = new PermissionApps(getContext(), groupInfo.name, null, pmCache);
                        permApps.refreshSync();
                        PermissionGroup group = new PermissionGroup(groupInfo.name, groupInfo.packageName, label, icon, permApps.getTotalCount(launcherPkgs), permApps.getGrantedCount(launcherPkgs));
                        groups.add(group);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                }
                i = 0;
            }
            List<PackageInfo> installedPackages2 = getContext().getPackageManager().getInstalledPackages(4096);
            Set<String> requestedPermissions = new ArraySet<>();
            for (PackageInfo installedPackage : installedPackages2) {
                if (installedPackage.requestedPermissions != null) {
                    for (String requestedPermission : installedPackage.requestedPermissions) {
                        requestedPermissions.add(requestedPermission);
                    }
                }
            }
            for (PackageInfo installedPackage2 : installedPackages2) {
                if (installedPackage2.permissions != null) {
                    PermissionInfo[] permissionInfoArr = installedPackage2.permissions;
                    int length = permissionInfoArr.length;
                    int i2 = 0;
                    while (i2 < length) {
                        PermissionInfo permissionInfo = permissionInfoArr[i2];
                        if (seenPermissions2.add(permissionInfo.name)) {
                            installedPackages = installedPackages2;
                            if ((permissionInfo.protectionLevel & 15) != 1) {
                                seenPermissions = seenPermissions2;
                                packageManager = packageManager2;
                            } else if ((permissionInfo.flags & 1073741824) != 0 && requestedPermissions.contains(permissionInfo.name)) {
                                CharSequence label2 = permissionsLoader.loadItemInfoLabel(permissionInfo);
                                Drawable icon2 = permissionsLoader.loadItemInfoIcon(permissionInfo);
                                seenPermissions = seenPermissions2;
                                packageManager = packageManager2;
                                PermissionApps permApps2 = new PermissionApps(getContext(), permissionInfo.name, null, pmCache);
                                permApps2.refreshSync();
                                PermissionGroup group2 = new PermissionGroup(permissionInfo.name, permissionInfo.packageName, label2, icon2, permApps2.getTotalCount(launcherPkgs), permApps2.getGrantedCount(launcherPkgs));
                                groups.add(group2);
                            } else {
                                seenPermissions = seenPermissions2;
                                packageManager = packageManager2;
                            }
                        } else {
                            installedPackages = installedPackages2;
                            seenPermissions = seenPermissions2;
                            packageManager = packageManager2;
                        }
                        i2++;
                        installedPackages2 = installedPackages;
                        seenPermissions2 = seenPermissions;
                        packageManager2 = packageManager;
                        permissionsLoader = this;
                    }
                    permissionsLoader = this;
                }
            }
            Collections.sort(groups);
            return groups;
        }

        private CharSequence loadItemInfoLabel(PackageItemInfo itemInfo) {
            CharSequence label = itemInfo.loadLabel(getContext().getPackageManager());
            if (label == null) {
                return itemInfo.name;
            }
            return label;
        }

        private Drawable loadItemInfoIcon(PackageItemInfo itemInfo) {
            Drawable icon = null;
            if (itemInfo.icon > 0) {
                icon = Utils.loadDrawable(getContext().getPackageManager(), itemInfo.packageName, itemInfo.icon);
            }
            if (icon == null) {
                Drawable icon2 = getContext().getDrawable(R.drawable.ic_perm_device_info);
                return icon2;
            }
            return icon;
        }

        public void onPermissionsChanged(int uid) {
            forceLoad();
        }
    }
}
