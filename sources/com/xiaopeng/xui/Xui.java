package com.xiaopeng.xui;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import com.xiaopeng.xui.drawable.shimmer.XShimmer;
/* loaded from: classes.dex */
public class Xui {
    private static Application mApp;
    private static boolean sDialogFullScreen;
    private static boolean sFontScaleDynamicChangeEnable;
    private static boolean sVuiEnable;

    public static void clear() {
    }

    public static Context getContext() {
        Application application = mApp;
        if (application != null) {
            return application;
        }
        throw new RuntimeException("Xui must be call Xui#init()!");
    }

    public static void init(Application application) {
        mApp = application;
        Log.d("xpui", "0.5.0-20-SNAPSHOT_xpdev_5317667_2020/11/02 16:17:37");
        XShimmer.msGlobalEnable = false;
    }

    public static boolean isDialogFullScreen() {
        return sDialogFullScreen;
    }

    public static boolean isFontScaleDynamicChangeEnable() {
        return sFontScaleDynamicChangeEnable;
    }

    public static boolean isVuiEnable() {
        return sVuiEnable;
    }

    public static void release() {
    }

    public static void setDialogFullScreen(boolean z) {
        sDialogFullScreen = z;
    }

    public static void setFontScaleDynamicChangeEnable(boolean z) {
        sFontScaleDynamicChangeEnable = z;
    }

    public static void setVuiEnable(boolean z) {
        sVuiEnable = z;
    }
}
