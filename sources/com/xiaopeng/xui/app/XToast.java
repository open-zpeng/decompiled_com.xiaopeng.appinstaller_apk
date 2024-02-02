package com.xiaopeng.xui.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.widget.XTextView;
/* loaded from: classes.dex */
public class XToast {
    private XToast() {
    }

    private static Context getApplicationContext() {
        return Xui.getContext();
    }

    private static Toast makeToast(int i) {
        Context applicationContext = getApplicationContext();
        View inflate = LayoutInflater.from(applicationContext).inflate(i, (ViewGroup) null);
        Toast toast = new Toast(applicationContext);
        toast.setGravity(8388661, 0, 0);
        toast.setView(inflate);
        return toast;
    }

    public static void show(int i) {
        showShort(i);
    }

    private static void show(int i, int i2) {
        show(Xui.getContext().getString(i), i2);
    }

    public static void show(String str) {
        showShort(str);
    }

    private static void show(String str, int i) {
        Toast makeToast = makeToast(R.layout.x_toast);
        makeToast.setDuration(i);
        ((XTextView) makeToast.getView().findViewById(R.id.textView)).setText(str);
        makeToast.show();
    }

    public static void showLong(int i) {
        show(i, 1);
    }

    public static void showLong(String str) {
        show(str, 1);
    }

    public static void showShort(int i) {
        show(i, 0);
    }

    public static void showShort(String str) {
        show(str, 0);
    }
}
