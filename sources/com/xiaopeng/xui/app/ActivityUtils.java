package com.xiaopeng.xui.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
@Deprecated
/* loaded from: classes.dex */
public class ActivityUtils {
    private static final String TAG = "ActivityUtils";

    public static void finish(Activity activity) {
        if (activity != null) {
            try {
                boolean isChild = activity.isChild();
                activity.finish();
                if (activity.isFinishing() || isChild) {
                    return;
                }
                startHome(activity);
            } catch (Exception e) {
                Log.d(TAG, "finish e=" + e);
            }
        }
    }

    public static int makeIntentFlag() {
        return 270548992;
    }

    public static boolean moveTaskToBack(Activity activity, boolean z) {
        boolean z2 = false;
        if (activity != null) {
            try {
                boolean isChild = activity.isChild();
                boolean moveTaskToBack = activity.moveTaskToBack(z);
                if (!moveTaskToBack && !isChild) {
                    try {
                        startHome(activity);
                    } catch (Exception e) {
                        e = e;
                        z2 = moveTaskToBack;
                        Log.d(TAG, "moveTaskToBack e=" + e);
                        return z2;
                    }
                }
                return moveTaskToBack;
            } catch (Exception e2) {
                e = e2;
            }
        }
        return z2;
    }

    public static void startHome(Context context) {
        try {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.addCategory("android.intent.category.HOME");
            intent.addFlags(270532608);
            context.startActivity(intent);
        } catch (Exception e) {
            Log.d(TAG, "startHome e=" + e);
        }
    }
}
