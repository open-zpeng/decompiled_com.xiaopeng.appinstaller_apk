package com.xiaopeng.appinstaller.permission.utils;

import android.content.pm.PackageInfo;
import android.os.Process;
import android.util.EventLog;
import com.xiaopeng.appinstaller.permission.model.AppPermissionGroup;
import com.xiaopeng.appinstaller.permission.model.Permission;
import java.util.List;
/* loaded from: classes.dex */
public final class SafetyNetLogger {
    public static void logPermissionsRequested(PackageInfo packageInfo, List<AppPermissionGroup> groups) {
        EventLog.writeEvent(1397638484, "individual_permissions_requested", Integer.valueOf(packageInfo.applicationInfo.uid), buildChangedPermissionForPackageMessage(packageInfo.packageName, groups));
    }

    public static void logPermissionsToggled(String packageName, List<AppPermissionGroup> groups) {
        EventLog.writeEvent(1397638484, "individual_permissions_toggled", Integer.valueOf(Process.myUid()), buildChangedPermissionForPackageMessage(packageName, groups));
    }

    private static String buildChangedPermissionForPackageMessage(String packageName, List<AppPermissionGroup> groups) {
        StringBuilder builder = new StringBuilder();
        builder.append(packageName);
        builder.append(':');
        int groupCount = groups.size();
        for (int groupNum = 0; groupNum < groupCount; groupNum++) {
            AppPermissionGroup group = groups.get(groupNum);
            int permissionCount = group.getPermissions().size();
            for (int permissionNum = 0; permissionNum < permissionCount; permissionNum++) {
                Permission permission = group.getPermissions().get(permissionNum);
                if (groupNum > 0 || permissionNum > 0) {
                    builder.append(';');
                }
                builder.append(permission.getName());
                builder.append('|');
                if (group.doesSupportRuntimePermissions()) {
                    builder.append(permission.isGranted());
                    builder.append('|');
                } else {
                    builder.append(permission.isGranted() && (permission.getAppOp() == null || permission.isAppOpAllowed()));
                    builder.append('|');
                }
                builder.append(permission.getFlags());
            }
        }
        return builder.toString();
    }
}
