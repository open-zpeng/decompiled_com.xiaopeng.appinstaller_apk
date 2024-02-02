package com.xiaopeng.xui.sound;

import android.util.SparseArray;
/* loaded from: classes.dex */
class b {

    /* renamed from: a  reason: collision with root package name */
    private static SparseArray<d> f54a = new SparseArray<>();

    /* renamed from: b  reason: collision with root package name */
    private static SparseArray<d> f55b;

    /* loaded from: classes.dex */
    static class a extends d {
        a(String str) {
            super("system/media/audio/xiaopeng/cdu/wav/" + str, 1, 5);
        }
    }

    static {
        f54a.put(-1, new a("CDU_touch.wav"));
        f54a.put(1, new a("CDU_touch.wav"));
        f54a.put(2, new a("CDU_wheel_scroll_7.wav"));
        f54a.put(3, new a("CDU_switch_on_2.wav"));
        f54a.put(4, new a("CDU_switch_off_2.wav"));
        f54a.put(5, new a("CDU_delete_4.wav"));
        f55b = f54a.clone();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static XSoundEffect a(int i) {
        return new c(f54a.get(i));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void a(int i, String str, int i2, int i3) {
        f54a.put(i, new d(str, i2, i3));
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void b(int i) {
        f54a.put(i, f55b.get(i));
    }
}
