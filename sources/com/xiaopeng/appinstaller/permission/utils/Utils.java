package com.xiaopeng.appinstaller.permission.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.ArraySet;
import android.util.Log;
import android.util.TypedValue;
import com.xiaopeng.appinstaller.permission.model.AppPermissionGroup;
import com.xiaopeng.appinstaller.permission.model.PermissionApps;
/* loaded from: classes.dex */
public final class Utils {
    public static final String[] MODERN_PERMISSION_GROUPS = {"android.permission-group.CALENDAR", "android.permission-group.CALL_LOG", "android.permission-group.CAMERA", "android.permission-group.CONTACTS", "android.permission-group.LOCATION", "android.permission-group.SENSORS", "android.permission-group.SMS", "android.permission-group.PHONE", "android.permission-group.MICROPHONE", "android.permission-group.STORAGE"};
    private static final Intent LAUNCHER_INTENT = new Intent("android.intent.action.MAIN", (Uri) null).addCategory("android.intent.category.LAUNCHER");

    public static Drawable loadDrawable(PackageManager pm, String pkg, int resId) {
        try {
            return pm.getResourcesForApplication(pkg).getDrawable(resId, null);
        } catch (PackageManager.NameNotFoundException | Resources.NotFoundException e) {
            Log.d("Utils", "Couldn't get resource", e);
            return null;
        }
    }

    public static boolean isModernPermissionGroup(String name) {
        String[] strArr;
        for (String modernGroup : MODERN_PERMISSION_GROUPS) {
            if (modernGroup.equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean shouldShowPermission(AppPermissionGroup group, String packageName) {
        if ((!group.isSystemFixed() || LocationUtils.isLocationGroupAndProvider(group.getName(), packageName)) && group.isGrantingAllowed()) {
            boolean isPlatformPermission = group.getDeclaringPackage().equals("android");
            return !isPlatformPermission || isModernPermissionGroup(group.getName());
        }
        return false;
    }

    public static boolean shouldShowPermission(PermissionApps.PermissionApp app) {
        if (app.isSystemFixed() && !LocationUtils.isLocationGroupAndProvider(app.getPermissionGroup().getName(), app.getPackageName())) {
            return false;
        }
        return true;
    }

    public static Drawable applyTint(Context context, Drawable icon, int attr) {
        Resources.Theme theme = context.getTheme();
        TypedValue typedValue = new TypedValue();
        theme.resolveAttribute(attr, typedValue, true);
        Drawable icon2 = icon.mutate();
        icon2.setTint(context.getColor(typedValue.resourceId));
        return icon2;
    }

    public static Drawable applyTint(Context context, int iconResId, int attr) {
        return applyTint(context, context.getDrawable(iconResId), attr);
    }

    public static ArraySet<String> getLauncherPackages(Context context) {
        ArraySet<String> launcherPkgs = new ArraySet<>();
        for (ResolveInfo info : context.getPackageManager().queryIntentActivities(LAUNCHER_INTENT, 0)) {
            launcherPkgs.add(info.activityInfo.packageName);
        }
        return launcherPkgs;
    }

    public static boolean isSystem(PermissionApps.PermissionApp app, ArraySet<String> launcherPkgs) {
        return isSystem(app.getAppInfo(), launcherPkgs);
    }

    public static boolean isSystem(ApplicationInfo info, ArraySet<String> launcherPkgs) {
        return info.isSystemApp() && (info.flags & 128) == 0 && !launcherPkgs.contains(info.packageName);
    }

    public static boolean areGroupPermissionsIndividuallyControlled(Context context, String group) {
        if (context.getPackageManager().isPermissionReviewModeEnabled()) {
            return "android.permission-group.SMS".equals(group) || "android.permission-group.PHONE".equals(group) || "android.permission-group.CONTACTS".equals(group);
        }
        return false;
    }

    public static boolean isPermissionIndividuallyControlled(Context context, String permission) {
        if (context.getPackageManager().isPermissionReviewModeEnabled()) {
            return "android.permission.READ_CONTACTS".equals(permission) || "android.permission.WRITE_CONTACTS".equals(permission) || "android.permission.SEND_SMS".equals(permission) || "android.permission.RECEIVE_SMS".equals(permission) || "android.permission.READ_SMS".equals(permission) || "android.permission.RECEIVE_MMS".equals(permission) || "android.permission.CALL_PHONE".equals(permission) || "android.permission.READ_CALL_LOG".equals(permission) || "android.permission.WRITE_CALL_LOG".equals(permission);
        }
        return false;
    }
}
