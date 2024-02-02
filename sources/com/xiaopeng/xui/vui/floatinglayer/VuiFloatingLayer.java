package com.xiaopeng.xui.vui.floatinglayer;

import android.content.Context;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.WindowManager;
import com.xiaopeng.xui.app.XDialogSystemType;
import com.xiaopeng.xui.vui.floatinglayer.VuiFloatingView;
import java.util.Arrays;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class VuiFloatingLayer implements IVuiFloatingLayer {

    /* renamed from: a  reason: collision with root package name */
    private Context f79a;
    private WindowManager e;
    private WindowManager.LayoutParams f;

    /* renamed from: b  reason: collision with root package name */
    private SparseArray<VuiFloatingView> f80b = new SparseArray<>();
    private SparseArray<LayerInfo> c = new SparseArray<>();
    private SparseArray<a> d = new SparseArray<>();
    private Handler g = new Handler();
    private SparseArray<c> h = new SparseArray<>();
    private SparseArray<b> i = new SparseArray<>();

    /* loaded from: classes.dex */
    public class LayerInfo {
        int[] location;
        int mCenterOffsetX;
        int mCenterOffsetY;
        int targetHeight;
        int targetWidth;

        public LayerInfo() {
        }

        public String toString() {
            return "LayerInfo{targetWidth=" + this.targetWidth + ", targetHeight=" + this.targetHeight + ", location=" + Arrays.toString(this.location) + ", mCenterOffsetX=" + this.mCenterOffsetX + ", mCenterOffsetY=" + this.mCenterOffsetY + '}';
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class a extends Animatable2.AnimationCallback {

        /* renamed from: a  reason: collision with root package name */
        private int f81a;

        a(int i) {
            this.f81a = i;
        }

        @Override // android.graphics.drawable.Animatable2.AnimationCallback
        public void onAnimationEnd(Drawable drawable) {
            Log.d("VuiFloatingLayer", "onAnimationEnd ");
            if (drawable instanceof AnimatedImageDrawable) {
                ((AnimatedImageDrawable) drawable).unregisterAnimationCallback(this);
            }
            VuiFloatingLayer.this.e(this.f81a);
        }

        @Override // android.graphics.drawable.Animatable2.AnimationCallback
        public void onAnimationStart(Drawable drawable) {
            Log.d("VuiFloatingLayer", "onAnimationStart ");
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class b implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        private int f83a;

        b(int i) {
            this.f83a = i;
        }

        @Override // java.lang.Runnable
        public void run() {
            Log.d("VuiFloatingLayer", "RunnableTimeOut type " + this.f83a);
            VuiFloatingLayer.this.d(this.f83a).requestNeedReLoadDrawable();
            VuiFloatingLayer.this.e(this.f83a);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class c implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        private int f85a;

        c(int i) {
            this.f85a = i;
        }

        @Override // java.lang.Runnable
        public void run() {
            Log.d("VuiFloatingLayer", "touch RunnableTouch type " + this.f85a);
            VuiFloatingLayer.this.e(this.f85a);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public VuiFloatingLayer(Context context) {
        Log.d("VuiFloatingLayer", "init...");
        this.f79a = context;
        this.e = (WindowManager) this.f79a.getSystemService("window");
        this.f = new WindowManager.LayoutParams();
        WindowManager.LayoutParams layoutParams = this.f;
        layoutParams.gravity = 8388659;
        layoutParams.type = XDialogSystemType.TYPE_SYSTEM_DIALOG;
        layoutParams.flags = 328488;
        layoutParams.format = -2;
    }

    private a a(int i) {
        a aVar = this.d.get(i);
        if (aVar == null) {
            a aVar2 = new a(i);
            this.d.put(i, aVar2);
            return aVar2;
        }
        return aVar;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void a(int i, int i2) {
        this.g.postDelayed(c(i), 150L);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void a(int i, VuiFloatingView vuiFloatingView, a aVar) {
        Log.d("VuiFloatingLayer", "registerAnimationCallback...type : " + i);
        vuiFloatingView.registerAnimationCallback(aVar);
    }

    private void a(View view, WindowManager.LayoutParams layoutParams, int i) {
        if (view.getParent() == null) {
            Log.d("VuiFloatingLayer", "add to window");
            this.e.addView(view, layoutParams);
        } else {
            Log.d("VuiFloatingLayer", "update to window");
            this.e.updateViewLayout(view, layoutParams);
        }
        this.g.postDelayed(b(i), VuiImageDecoderUtils.getAnimateTimeOut(i));
    }

    private void a(VuiFloatingView vuiFloatingView) {
        Log.d("VuiFloatingLayer", "startAnimation...");
        if (vuiFloatingView != null) {
            vuiFloatingView.start();
        } else {
            Log.d("VuiFloatingLayer", "view is null");
        }
    }

    private void a(VuiFloatingView vuiFloatingView, LayerInfo layerInfo, int i) {
        if (layerInfo == null) {
            return;
        }
        int visibleWidth = vuiFloatingView.getVisibleWidth();
        int visibleHeight = vuiFloatingView.getVisibleHeight();
        WindowManager.LayoutParams layoutParams = this.f;
        layoutParams.width = visibleWidth;
        layoutParams.height = visibleHeight;
        int[] a2 = com.xiaopeng.xui.vui.floatinglayer.a.a(i, layerInfo, visibleWidth, visibleHeight);
        WindowManager.LayoutParams layoutParams2 = this.f;
        layoutParams2.x = a2[0];
        layoutParams2.y = a2[1];
        a(vuiFloatingView, layoutParams2, i);
    }

    private b b(int i) {
        b bVar = this.i.get(i);
        if (bVar == null) {
            b bVar2 = new b(i);
            this.i.put(i, bVar2);
            return bVar2;
        }
        return bVar;
    }

    private void b(VuiFloatingView vuiFloatingView) {
        Log.d("VuiFloatingLayer", "stopAnimation...");
        if (vuiFloatingView != null) {
            vuiFloatingView.stop();
        } else {
            Log.d("VuiFloatingLayer", "view is null");
        }
    }

    private c c(int i) {
        c cVar = this.h.get(i);
        if (cVar == null) {
            c cVar2 = new c(i);
            this.h.put(i, cVar2);
            return cVar2;
        }
        return cVar;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public VuiFloatingView d(final int i) {
        VuiFloatingView vuiFloatingView = this.f80b.get(i);
        if (vuiFloatingView == null) {
            VuiFloatingView vuiFloatingView2 = new VuiFloatingView(this.f79a, i);
            this.f80b.put(i, vuiFloatingView2);
            vuiFloatingView2.setOnTouchListener(new VuiFloatingView.OnTouchListener() { // from class: com.xiaopeng.xui.vui.floatinglayer.-$$Lambda$VuiFloatingLayer$Eu2ON8JKrTyKf_ni9zslfe6YG1c
                @Override // com.xiaopeng.xui.vui.floatinglayer.VuiFloatingView.OnTouchListener
                public final void onTouch(int i2) {
                    VuiFloatingLayer.this.a(i, i2);
                }
            });
            return vuiFloatingView2;
        }
        return vuiFloatingView;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void e(int i) {
        VuiFloatingView d = d(i);
        b(d);
        if (d == null || d.getParent() == null) {
            Log.d("VuiFloatingLayer", "view is null or view all in window");
            return;
        }
        this.e.removeView(d);
        this.g.removeCallbacks(b(i));
        Log.d("VuiFloatingLayer", "remove window..type : " + i);
    }

    @Override // com.xiaopeng.xui.vui.floatinglayer.IVuiFloatingLayer
    public void hideFloatingLayer(int i) {
        Log.d("VuiFloatingLayer", "hideFloatingLayer...type : " + i);
        b(d(i));
        e(i);
    }

    @Override // com.xiaopeng.xui.vui.floatinglayer.IVuiFloatingLayer
    public void showFloatingLayer(View view, final int i, int i2, int i3) {
        Log.d("VuiFloatingLayer", "showFloatingLayer...type : " + i);
        this.g.removeCallbacks(c(i));
        this.g.removeCallbacks(b(i));
        LayerInfo layerInfo = this.c.get(i);
        if (layerInfo == null) {
            layerInfo = new LayerInfo();
            this.c.put(i, layerInfo);
        }
        layerInfo.targetWidth = view.getMeasuredWidth();
        layerInfo.targetHeight = view.getMeasuredHeight();
        layerInfo.mCenterOffsetX = i2;
        layerInfo.mCenterOffsetY = i3;
        layerInfo.location = new int[2];
        view.getLocationOnScreen(layerInfo.location);
        final VuiFloatingView d = d(i);
        final a a2 = a(i);
        d.unRegisterAnimationCallback(a2);
        d.postDelayed(new Runnable() { // from class: com.xiaopeng.xui.vui.floatinglayer.-$$Lambda$VuiFloatingLayer$7hkc9MsA0RE-mnr4Cqwg5Kw2h-M
            @Override // java.lang.Runnable
            public final void run() {
                VuiFloatingLayer.a(i, d, a2);
            }
        }, 500L);
        b(d);
        d.prepare();
        a(d, layerInfo, i);
        a(d);
    }
}
