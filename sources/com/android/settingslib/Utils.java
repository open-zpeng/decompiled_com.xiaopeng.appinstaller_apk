package com.android.settingslib;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import com.android.internal.annotations.VisibleForTesting;
/* loaded from: classes.dex */
public class Utils {
    @VisibleForTesting
    static final String STORAGE_MANAGER_ENABLED_PROPERTY = "ro.storage_manager.enabled";
    static final int[] WIFI_PIE = {17302787, 17302788, 17302789, 17302790, 17302791};

    public static int getDefaultColor(Context context, int resId) {
        ColorStateList list = context.getResources().getColorStateList(resId, context.getTheme());
        return list.getDefaultColor();
    }

    public static int getColorAttr(Context context, int attr) {
        TypedArray ta = context.obtainStyledAttributes(new int[]{attr});
        int colorAccent = ta.getColor(0, 0);
        ta.recycle();
        return colorAccent;
    }
}
