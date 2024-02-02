package com.xiaopeng.appinstaller.permission.model;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.BidiFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
/* loaded from: classes.dex */
public final class AppPermissions {
    private final CharSequence mAppLabel;
    private final Context mContext;
    private final String[] mFilterPermissions;
    private final ArrayList<AppPermissionGroup> mGroups = new ArrayList<>();
    private final LinkedHashMap<String, AppPermissionGroup> mNameToGroupMap = new LinkedHashMap<>();
    private final Runnable mOnErrorCallback;
    private PackageInfo mPackageInfo;
    private final boolean mSortGroups;

    public AppPermissions(Context context, PackageInfo packageInfo, String[] filterPermissions, boolean sortGroups, Runnable onErrorCallback) {
        this.mContext = context;
        this.mPackageInfo = packageInfo;
        this.mFilterPermissions = filterPermissions;
        this.mAppLabel = BidiFormatter.getInstance().unicodeWrap(packageInfo.applicationInfo.loadSafeLabel(context.getPackageManager()).toString());
        this.mSortGroups = sortGroups;
        this.mOnErrorCallback = onErrorCallback;
        loadPermissionGroups();
    }

    public PackageInfo getPackageInfo() {
        return this.mPackageInfo;
    }

    public void refresh() {
        loadPackageInfo();
        loadPermissionGroups();
    }

    public CharSequence getAppLabel() {
        return this.mAppLabel;
    }

    public AppPermissionGroup getPermissionGroup(String name) {
        return this.mNameToGroupMap.get(name);
    }

    public List<AppPermissionGroup> getPermissionGroups() {
        return this.mGroups;
    }

    private void loadPackageInfo() {
        try {
            this.mPackageInfo = this.mContext.getPackageManager().getPackageInfo(this.mPackageInfo.packageName, 4096);
        } catch (PackageManager.NameNotFoundException e) {
            if (this.mOnErrorCallback != null) {
                this.mOnErrorCallback.run();
            }
        }
    }

    private void loadPermissionGroups() {
        String[] strArr;
        this.mGroups.clear();
        if (this.mPackageInfo.requestedPermissions == null) {
            return;
        }
        if (this.mFilterPermissions != null) {
            for (String filterPermission : this.mFilterPermissions) {
                String[] strArr2 = this.mPackageInfo.requestedPermissions;
                int length = strArr2.length;
                int i = 0;
                while (true) {
                    if (i < length) {
                        String requestedPerm = strArr2[i];
                        if (!filterPermission.equals(requestedPerm)) {
                            i++;
                        } else {
                            addPermissionGroupIfNeeded(requestedPerm);
                            break;
                        }
                    }
                }
            }
        } else {
            for (String requestedPerm2 : this.mPackageInfo.requestedPermissions) {
                addPermissionGroupIfNeeded(requestedPerm2);
            }
        }
        if (this.mSortGroups) {
            Collections.sort(this.mGroups);
        }
        this.mNameToGroupMap.clear();
        Iterator<AppPermissionGroup> it = this.mGroups.iterator();
        while (it.hasNext()) {
            AppPermissionGroup group = it.next();
            this.mNameToGroupMap.put(group.getName(), group);
        }
    }

    private void addPermissionGroupIfNeeded(String permission) {
        AppPermissionGroup group;
        if (getGroupForPermission(permission) != null || (group = AppPermissionGroup.create(this.mContext, this.mPackageInfo, permission)) == null) {
            return;
        }
        this.mGroups.add(group);
    }

    public AppPermissionGroup getGroupForPermission(String permission) {
        Iterator<AppPermissionGroup> it = this.mGroups.iterator();
        while (it.hasNext()) {
            AppPermissionGroup group = it.next();
            if (group.hasPermission(permission)) {
                return group;
            }
        }
        return null;
    }
}
