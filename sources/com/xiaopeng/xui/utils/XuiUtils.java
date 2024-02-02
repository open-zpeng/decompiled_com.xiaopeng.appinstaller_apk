package com.xiaopeng.xui.utils;

import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import com.xiaopeng.xui.Xui;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public class XuiUtils {
    public static int dip2px(float f) {
        return (int) ((f * Xui.getContext().getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static List<View> findViewsByType(ViewGroup viewGroup, Class cls) {
        ArrayList arrayList = new ArrayList();
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View childAt = viewGroup.getChildAt(i);
            if (cls.isInstance(childAt)) {
                arrayList.add(childAt);
            } else if (childAt instanceof ViewGroup) {
                arrayList.addAll(findViewsByType((ViewGroup) childAt, cls));
            }
        }
        return arrayList;
    }

    public static int getColor(int i, Resources.Theme theme) {
        return Xui.getContext().getResources().getColor(i, theme);
    }

    public static float getFontScale() {
        return Xui.getContext().getResources().getDisplayMetrics().scaledDensity;
    }

    public static int getScreenHeight() {
        return Xui.getContext().getResources().getDisplayMetrics().heightPixels;
    }

    public static int getScreenWidth() {
        return Xui.getContext().getResources().getDisplayMetrics().widthPixels;
    }

    public static int px2dip(float f) {
        return (int) ((f / Xui.getContext().getResources().getDisplayMetrics().density) + 0.5f);
    }

    public static int px2sp(float f) {
        return (int) ((f / Xui.getContext().getResources().getDisplayMetrics().scaledDensity) + 0.5f);
    }

    public static int sp2px(float f) {
        return (int) ((f * Xui.getContext().getResources().getDisplayMetrics().scaledDensity) + 0.5f);
    }
}
