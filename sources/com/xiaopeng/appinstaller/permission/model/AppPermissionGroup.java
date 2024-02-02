package com.xiaopeng.appinstaller.permission.model;

import android.annotation.SystemApi;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageItemInfo;
import android.content.pm.PackageManager;
import android.content.pm.PermissionGroupInfo;
import android.content.pm.PermissionInfo;
import android.os.Process;
import android.os.UserHandle;
import android.util.ArrayMap;
import com.xiaopeng.appinstaller.R;
import com.xiaopeng.appinstaller.permission.utils.ArrayUtils;
import com.xiaopeng.appinstaller.permission.utils.LocationUtils;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public final class AppPermissionGroup implements Comparable<AppPermissionGroup> {
    private final ActivityManager mActivityManager;
    private final AppOpsManager mAppOps;
    private final boolean mAppSupportsRuntimePermissions;
    private final Collator mCollator;
    private boolean mContainsEphemeralPermission;
    private boolean mContainsPreRuntimePermission;
    private final Context mContext;
    private final String mDeclaringPackage;
    private final CharSequence mDescription;
    private final String mIconPkg;
    private final int mIconResId;
    private final boolean mIsEphemeralApp;
    private final CharSequence mLabel;
    private final String mName;
    private final PackageInfo mPackageInfo;
    private final PackageManager mPackageManager;
    private final ArrayMap<String, Permission> mPermissions = new ArrayMap<>();
    private final int mRequest;
    private final UserHandle mUserHandle;

    public static AppPermissionGroup create(Context context, PackageInfo packageInfo, String permissionName) {
        try {
            PermissionInfo permissionInfo = context.getPackageManager().getPermissionInfo(permissionName, 0);
            if ((permissionInfo.protectionLevel & 15) != 1 || (permissionInfo.flags & 1073741824) == 0 || (permissionInfo.flags & 2) != 0) {
                return null;
            }
            PackageItemInfo groupInfo = permissionInfo;
            if (permissionInfo.group != null) {
                try {
                    groupInfo = context.getPackageManager().getPermissionGroupInfo(permissionInfo.group, 0);
                } catch (PackageManager.NameNotFoundException e) {
                }
            }
            List<PermissionInfo> permissionInfos = null;
            if (groupInfo instanceof PermissionGroupInfo) {
                try {
                    permissionInfos = context.getPackageManager().queryPermissionsByGroup(groupInfo.name, 0);
                } catch (PackageManager.NameNotFoundException e2) {
                }
            }
            return create(context, packageInfo, groupInfo, permissionInfos, Process.myUserHandle());
        } catch (PackageManager.NameNotFoundException e3) {
            return null;
        }
    }

    public static AppPermissionGroup create(Context context, PackageInfo packageInfo, PackageItemInfo groupInfo, List<PermissionInfo> permissionInfos, UserHandle userHandle) {
        List<PermissionInfo> permissionInfos2;
        AppPermissionGroup group = new AppPermissionGroup(context, packageInfo, groupInfo.name, groupInfo.packageName, groupInfo.loadLabel(context.getPackageManager()), loadGroupDescription(context, groupInfo), getRequest(groupInfo), groupInfo.packageName, groupInfo.icon, userHandle);
        if (groupInfo instanceof PermissionInfo) {
            permissionInfos2 = new ArrayList<>();
            permissionInfos2.add((PermissionInfo) groupInfo);
        } else {
            permissionInfos2 = permissionInfos;
        }
        if (permissionInfos2 == null || permissionInfos2.isEmpty()) {
            return null;
        }
        int permissionCount = packageInfo.requestedPermissions.length;
        for (int i = 0; i < permissionCount; i++) {
            String requestedPermission = packageInfo.requestedPermissions[i];
            PermissionInfo requestedPermissionInfo = null;
            Iterator<PermissionInfo> it = permissionInfos2.iterator();
            while (true) {
                if (!it.hasNext()) {
                    break;
                }
                PermissionInfo permissionInfo = it.next();
                if (requestedPermission.equals(permissionInfo.name)) {
                    requestedPermissionInfo = permissionInfo;
                    break;
                }
            }
            if (requestedPermissionInfo != null && (requestedPermissionInfo.protectionLevel & 15) == 1 && (packageInfo.applicationInfo.targetSdkVersion > 22 || "android".equals(groupInfo.packageName))) {
                boolean granted = (packageInfo.requestedPermissionsFlags[i] & 2) != 0;
                String appOp = "android".equals(requestedPermissionInfo.packageName) ? AppOpsManager.permissionToOp(requestedPermissionInfo.name) : null;
                boolean appOpAllowed = appOp != null && ((AppOpsManager) context.getSystemService(AppOpsManager.class)).checkOpNoThrow(appOp, packageInfo.applicationInfo.uid, packageInfo.packageName) == 0;
                int flags = context.getPackageManager().getPermissionFlags(requestedPermission, packageInfo.packageName, userHandle);
                Permission permission = new Permission(requestedPermission, granted, appOp, appOpAllowed, flags, requestedPermissionInfo.protectionLevel);
                group.addPermission(permission);
            }
        }
        return group;
    }

    private static int getRequest(PackageItemInfo group) {
        if (group instanceof PermissionGroupInfo) {
            return ((PermissionGroupInfo) group).requestRes;
        }
        if (group instanceof PermissionInfo) {
            return ((PermissionInfo) group).requestRes;
        }
        return 0;
    }

    private static CharSequence loadGroupDescription(Context context, PackageItemInfo group) {
        CharSequence description = null;
        if (group instanceof PermissionGroupInfo) {
            description = ((PermissionGroupInfo) group).loadDescription(context.getPackageManager());
        } else if (group instanceof PermissionInfo) {
            description = ((PermissionInfo) group).loadDescription(context.getPackageManager());
        }
        if (description == null || description.length() <= 0) {
            CharSequence description2 = context.getString(R.string.default_permission_description);
            return description2;
        }
        return description;
    }

    private AppPermissionGroup(Context context, PackageInfo packageInfo, String name, String declaringPackage, CharSequence label, CharSequence description, int request, String iconPkg, int iconResId, UserHandle userHandle) {
        this.mContext = context;
        this.mUserHandle = userHandle;
        this.mPackageManager = this.mContext.getPackageManager();
        this.mPackageInfo = packageInfo;
        this.mAppSupportsRuntimePermissions = packageInfo.applicationInfo.targetSdkVersion > 22;
        this.mIsEphemeralApp = packageInfo.applicationInfo.isInstantApp();
        this.mAppOps = (AppOpsManager) context.getSystemService(AppOpsManager.class);
        this.mActivityManager = (ActivityManager) context.getSystemService(ActivityManager.class);
        this.mDeclaringPackage = declaringPackage;
        this.mName = name;
        this.mLabel = label;
        this.mDescription = description;
        this.mCollator = Collator.getInstance(context.getResources().getConfiguration().getLocales().get(0));
        this.mRequest = request;
        if (iconResId != 0) {
            this.mIconPkg = iconPkg;
            this.mIconResId = iconResId;
            return;
        }
        this.mIconPkg = context.getPackageName();
        this.mIconResId = R.drawable.ic_perm_device_info;
    }

    public boolean doesSupportRuntimePermissions() {
        return this.mAppSupportsRuntimePermissions;
    }

    public boolean isGrantingAllowed() {
        return (!this.mIsEphemeralApp || this.mContainsEphemeralPermission) && (this.mAppSupportsRuntimePermissions || this.mContainsPreRuntimePermission);
    }

    public boolean isReviewRequired() {
        if (this.mAppSupportsRuntimePermissions) {
            return false;
        }
        int permissionCount = this.mPermissions.size();
        for (int i = 0; i < permissionCount; i++) {
            Permission permission = this.mPermissions.valueAt(i);
            if (permission.isReviewRequired()) {
                return true;
            }
        }
        return false;
    }

    public void resetReviewRequired() {
        int permissionCount = this.mPermissions.size();
        for (int i = 0; i < permissionCount; i++) {
            Permission permission = this.mPermissions.valueAt(i);
            if (permission.isReviewRequired()) {
                permission.resetReviewRequired();
                this.mPackageManager.updatePermissionFlags(permission.getName(), this.mPackageInfo.packageName, 64, 0, this.mUserHandle);
            }
        }
    }

    public boolean hasGrantedByDefaultPermission() {
        int permissionCount = this.mPermissions.size();
        for (int i = 0; i < permissionCount; i++) {
            Permission permission = this.mPermissions.valueAt(i);
            if (permission.isGrantedByDefault()) {
                return true;
            }
        }
        return false;
    }

    public PackageInfo getApp() {
        return this.mPackageInfo;
    }

    public String getName() {
        return this.mName;
    }

    public String getDeclaringPackage() {
        return this.mDeclaringPackage;
    }

    public String getIconPkg() {
        return this.mIconPkg;
    }

    public int getIconResId() {
        return this.mIconResId;
    }

    public CharSequence getLabel() {
        return this.mLabel;
    }

    @SystemApi
    public int getRequest() {
        return this.mRequest;
    }

    public CharSequence getDescription() {
        return this.mDescription;
    }

    public int getUserId() {
        return this.mUserHandle.getIdentifier();
    }

    public boolean hasPermission(String permission) {
        return this.mPermissions.get(permission) != null;
    }

    public boolean areRuntimePermissionsGranted() {
        return areRuntimePermissionsGranted(null);
    }

    public boolean areRuntimePermissionsGranted(String[] filterPermissions) {
        if (LocationUtils.isLocationGroupAndProvider(this.mName, this.mPackageInfo.packageName)) {
            return LocationUtils.isLocationEnabled(this.mContext);
        }
        int permissionCount = this.mPermissions.size();
        for (int i = 0; i < permissionCount; i++) {
            Permission permission = this.mPermissions.valueAt(i);
            if (filterPermissions == null || ArrayUtils.contains(filterPermissions, permission.getName())) {
                if (this.mAppSupportsRuntimePermissions) {
                    if (permission.isGranted()) {
                        return true;
                    }
                } else if (permission.isGranted() && ((permission.getAppOp() == null || permission.isAppOpAllowed()) && !permission.isReviewRequired())) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean grantRuntimePermissions(boolean fixedByTheUser) {
        return grantRuntimePermissions(fixedByTheUser, null);
    }

    public boolean grantRuntimePermissions(boolean fixedByTheUser, String[] filterPermissions) {
        int uid = this.mPackageInfo.applicationInfo.uid;
        for (Permission permission : this.mPermissions.values()) {
            if (filterPermissions == null || ArrayUtils.contains(filterPermissions, permission.getName())) {
                if (!permission.isGrantingAllowed(this.mIsEphemeralApp, this.mAppSupportsRuntimePermissions)) {
                    continue;
                } else if (this.mAppSupportsRuntimePermissions) {
                    if (permission.isSystemFixed()) {
                        return false;
                    }
                    if (permission.hasAppOp() && !permission.isAppOpAllowed()) {
                        permission.setAppOpAllowed(true);
                        this.mAppOps.setUidMode(permission.getAppOp(), uid, 0);
                    }
                    if (!permission.isGranted()) {
                        permission.setGranted(true);
                        this.mPackageManager.grantRuntimePermission(this.mPackageInfo.packageName, permission.getName(), this.mUserHandle);
                    }
                    if (!fixedByTheUser && (permission.isUserFixed() || permission.isUserSet())) {
                        permission.setUserFixed(false);
                        permission.setUserSet(false);
                        this.mPackageManager.updatePermissionFlags(permission.getName(), this.mPackageInfo.packageName, 3, 0, this.mUserHandle);
                    }
                } else if (permission.isGranted()) {
                    int killUid = -1;
                    int mask = 0;
                    if (permission.hasAppOp()) {
                        if (!permission.isAppOpAllowed()) {
                            permission.setAppOpAllowed(true);
                            this.mAppOps.setUidMode(permission.getAppOp(), uid, 0);
                            killUid = uid;
                        }
                        if (permission.shouldRevokeOnUpgrade()) {
                            permission.setRevokeOnUpgrade(false);
                            mask = 0 | 8;
                        }
                    }
                    if (permission.isReviewRequired()) {
                        permission.resetReviewRequired();
                        mask |= 64;
                    }
                    if (mask != 0) {
                        this.mPackageManager.updatePermissionFlags(permission.getName(), this.mPackageInfo.packageName, mask, 0, this.mUserHandle);
                    }
                    if (killUid != -1) {
                        this.mActivityManager.killUid(uid, "Permission related app op changed");
                    }
                }
            }
        }
        return true;
    }

    public boolean revokeRuntimePermissions(boolean fixedByTheUser) {
        return revokeRuntimePermissions(fixedByTheUser, null);
    }

    public boolean revokeRuntimePermissions(boolean fixedByTheUser, String[] filterPermissions) {
        int uid = this.mPackageInfo.applicationInfo.uid;
        for (Permission permission : this.mPermissions.values()) {
            if (filterPermissions == null || ArrayUtils.contains(filterPermissions, permission.getName())) {
                if (this.mAppSupportsRuntimePermissions) {
                    if (permission.isSystemFixed()) {
                        return false;
                    }
                    if (permission.isGranted()) {
                        permission.setGranted(false);
                        this.mPackageManager.revokeRuntimePermission(this.mPackageInfo.packageName, permission.getName(), this.mUserHandle);
                    }
                    if (fixedByTheUser) {
                        if (permission.isUserSet() || !permission.isUserFixed()) {
                            permission.setUserSet(false);
                            permission.setUserFixed(true);
                            this.mPackageManager.updatePermissionFlags(permission.getName(), this.mPackageInfo.packageName, 3, 2, this.mUserHandle);
                        }
                    } else if (!permission.isUserSet() || permission.isUserFixed()) {
                        permission.setUserSet(true);
                        permission.setUserFixed(false);
                        this.mPackageManager.updatePermissionFlags(permission.getName(), this.mPackageInfo.packageName, 3, 1, this.mUserHandle);
                    }
                } else if (permission.isGranted()) {
                    int mask = 0;
                    int flags = 0;
                    int killUid = -1;
                    if (permission.hasAppOp()) {
                        if (permission.isAppOpAllowed()) {
                            permission.setAppOpAllowed(false);
                            this.mAppOps.setUidMode(permission.getAppOp(), uid, 1);
                            killUid = uid;
                        }
                        if (!permission.shouldRevokeOnUpgrade()) {
                            permission.setRevokeOnUpgrade(true);
                            mask = 0 | 8;
                            flags = 0 | 8;
                        }
                    }
                    if (mask != 0) {
                        this.mPackageManager.updatePermissionFlags(permission.getName(), this.mPackageInfo.packageName, mask, flags, this.mUserHandle);
                    }
                    if (killUid != -1) {
                        this.mActivityManager.killUid(uid, "Permission related app op changed");
                    }
                }
            }
        }
        return true;
    }

    public void setPolicyFixed() {
        int permissionCount = this.mPermissions.size();
        for (int i = 0; i < permissionCount; i++) {
            Permission permission = this.mPermissions.valueAt(i);
            permission.setPolicyFixed(true);
            this.mPackageManager.updatePermissionFlags(permission.getName(), this.mPackageInfo.packageName, 4, 4, this.mUserHandle);
        }
    }

    public List<Permission> getPermissions() {
        return new ArrayList(this.mPermissions.values());
    }

    public boolean isUserFixed() {
        int permissionCount = this.mPermissions.size();
        for (int i = 0; i < permissionCount; i++) {
            Permission permission = this.mPermissions.valueAt(i);
            if (permission.isUserFixed()) {
                return true;
            }
        }
        return false;
    }

    public boolean isPolicyFixed() {
        int permissionCount = this.mPermissions.size();
        for (int i = 0; i < permissionCount; i++) {
            Permission permission = this.mPermissions.valueAt(i);
            if (permission.isPolicyFixed()) {
                return true;
            }
        }
        return false;
    }

    public boolean isUserSet() {
        int permissionCount = this.mPermissions.size();
        for (int i = 0; i < permissionCount; i++) {
            Permission permission = this.mPermissions.valueAt(i);
            if (permission.isUserSet()) {
                return true;
            }
        }
        return false;
    }

    public boolean isSystemFixed() {
        int permissionCount = this.mPermissions.size();
        for (int i = 0; i < permissionCount; i++) {
            Permission permission = this.mPermissions.valueAt(i);
            if (permission.isSystemFixed()) {
                return true;
            }
        }
        return false;
    }

    @Override // java.lang.Comparable
    public int compareTo(AppPermissionGroup another) {
        int result = this.mCollator.compare(this.mLabel.toString(), another.mLabel.toString());
        if (result == 0) {
            return this.mPackageInfo.applicationInfo.uid - another.mPackageInfo.applicationInfo.uid;
        }
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        AppPermissionGroup other = (AppPermissionGroup) obj;
        if (this.mName == null) {
            if (other.mName != null) {
                return false;
            }
        } else if (!this.mName.equals(other.mName)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        if (this.mName != null) {
            return this.mName.hashCode();
        }
        return 0;
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getClass().getSimpleName());
        builder.append("{name=");
        builder.append(this.mName);
        if (!this.mPermissions.isEmpty()) {
            builder.append(", <has permissions>}");
        } else {
            builder.append('}');
        }
        return builder.toString();
    }

    private void addPermission(Permission permission) {
        this.mPermissions.put(permission.getName(), permission);
        if (permission.isEphemeral()) {
            this.mContainsEphemeralPermission = true;
        }
        if (!permission.isRuntimeOnly()) {
            this.mContainsPreRuntimePermission = true;
        }
    }
}
