package android.support.v4.app;

import android.app.AppOpsManager;
import android.content.Context;
import android.os.Build;
/* loaded from: classes.dex */
public final class AppOpsManagerCompat {
    public static String permissionToOp(String permission) {
        if (Build.VERSION.SDK_INT >= 23) {
            return AppOpsManager.permissionToOp(permission);
        }
        return null;
    }

    public static int noteProxyOpNoThrow(Context context, String op, String proxiedPackageName) {
        if (Build.VERSION.SDK_INT >= 23) {
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(AppOpsManager.class);
            return appOpsManager.noteProxyOpNoThrow(op, proxiedPackageName);
        }
        return 1;
    }
}
