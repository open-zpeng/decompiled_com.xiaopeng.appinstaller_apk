package com.xiaopeng.appinstaller;

import android.content.Context;
import android.os.SystemProperties;
/* loaded from: classes.dex */
public class DeviceUtils {
    public static boolean isTelevision(Context context) {
        int uiMode = context.getResources().getConfiguration().uiMode;
        return (uiMode & 15) == 4;
    }

    public static boolean isDebugAllowInstall() {
        return SystemProperties.getBoolean("persist.xp.debug.installer.allow_install", false);
    }
}
