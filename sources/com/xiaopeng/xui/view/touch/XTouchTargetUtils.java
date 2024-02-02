package com.xiaopeng.xui.view.touch;

import android.graphics.Rect;
import android.util.Log;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
/* loaded from: classes.dex */
public class XTouchTargetUtils {
    private static final Rect HIT_RECT = new Rect();

    /* loaded from: classes.dex */
    static class a implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        final /* synthetic */ View f64a;

        /* renamed from: b  reason: collision with root package name */
        final /* synthetic */ int f65b;
        final /* synthetic */ int c;
        final /* synthetic */ int d;
        final /* synthetic */ int e;
        final /* synthetic */ int f;

        a(View view, int i, int i2, int i3, int i4, int i5) {
            this.f64a = view;
            this.f65b = i;
            this.c = i2;
            this.d = i3;
            this.e = i4;
            this.f = i5;
        }

        @Override // java.lang.Runnable
        public void run() {
            XTouchTargetUtils.extendViewTouchTarget(this.f64a, XTouchTargetUtils.findViewAncestor(this.f64a, this.f65b), this.c, this.d, this.e, this.f);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class b implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        final /* synthetic */ View f66a;

        /* renamed from: b  reason: collision with root package name */
        final /* synthetic */ View f67b;
        final /* synthetic */ int c;
        final /* synthetic */ int d;
        final /* synthetic */ int e;
        final /* synthetic */ int f;

        b(View view, View view2, int i, int i2, int i3, int i4) {
            this.f66a = view;
            this.f67b = view2;
            this.c = i;
            this.d = i2;
            this.e = i3;
            this.f = i4;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (!this.f66a.isAttachedToWindow()) {
                XTouchTargetUtils.log("not isAttachedToWindow " + hashCode());
                return;
            }
            this.f66a.getHitRect(XTouchTargetUtils.HIT_RECT);
            if (XTouchTargetUtils.HIT_RECT.width() == 0 || XTouchTargetUtils.HIT_RECT.height() == 0) {
                XTouchTargetUtils.log(" width or height == 0 " + hashCode());
                e eVar = new e(this.f67b, this.c, this.d, this.e, this.f);
                this.f66a.addOnLayoutChangeListener(eVar);
                this.f66a.addOnAttachStateChangeListener(eVar);
                return;
            }
            Rect rect = new Rect();
            rect.set(XTouchTargetUtils.HIT_RECT);
            View view = this.f66a;
            while (true) {
                ViewParent parent = view.getParent();
                if (parent == this.f67b) {
                    rect.left -= this.c;
                    rect.top -= this.d;
                    rect.right += this.e;
                    rect.bottom += this.f;
                    XTouchDelegate xTouchDelegate = new XTouchDelegate(rect, this.f66a);
                    XTouchDelegateGroup orCreateTouchDelegateGroup = XTouchTargetUtils.getOrCreateTouchDelegateGroup(this.f67b);
                    orCreateTouchDelegateGroup.addTouchDelegate(xTouchDelegate);
                    this.f67b.setTouchDelegate(orCreateTouchDelegateGroup);
                    this.f66a.addOnAttachStateChangeListener(new f(xTouchDelegate, orCreateTouchDelegateGroup));
                    return;
                } else if (!(parent instanceof View)) {
                    return;
                } else {
                    view = (View) parent;
                    view.getHitRect(XTouchTargetUtils.HIT_RECT);
                    rect.offset(XTouchTargetUtils.HIT_RECT.left, XTouchTargetUtils.HIT_RECT.top);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class c implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        final /* synthetic */ View f68a;

        /* renamed from: b  reason: collision with root package name */
        final /* synthetic */ ViewGroup f69b;

        c(View view, ViewGroup viewGroup) {
            this.f68a = view;
            this.f69b = viewGroup;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (!this.f68a.isAttachedToWindow()) {
                XTouchTargetUtils.log(" as not isAttachedToWindow " + hashCode());
            } else if (this.f69b.getWidth() != 0 && this.f69b.getHeight() != 0) {
                XTouchDelegate xTouchDelegate = new XTouchDelegate(new Rect(0, 0, this.f69b.getWidth(), this.f69b.getHeight()), this.f68a);
                XTouchDelegateGroup orCreateTouchDelegateGroup = XTouchTargetUtils.getOrCreateTouchDelegateGroup(this.f69b);
                orCreateTouchDelegateGroup.addTouchDelegate(xTouchDelegate);
                this.f69b.setTouchDelegate(orCreateTouchDelegateGroup);
                this.f68a.addOnAttachStateChangeListener(new f(xTouchDelegate, orCreateTouchDelegateGroup));
            } else {
                XTouchTargetUtils.log(" as width or height == 0 " + hashCode());
                d dVar = new d(this.f69b);
                this.f68a.addOnLayoutChangeListener(dVar);
                this.f68a.addOnAttachStateChangeListener(dVar);
            }
        }
    }

    /* loaded from: classes.dex */
    private static class d implements View.OnAttachStateChangeListener, View.OnLayoutChangeListener {

        /* renamed from: b  reason: collision with root package name */
        private static int f70b;

        /* renamed from: a  reason: collision with root package name */
        private ViewGroup f71a;

        d(ViewGroup viewGroup) {
            this.f71a = viewGroup;
            f70b++;
        }

        protected void finalize() {
            super.finalize();
            f70b--;
            if (f70b == 0) {
                Log.d("xui-touch", " LayoutAttachStateChangeListener2  finalize " + f70b);
            }
        }

        @Override // android.view.View.OnLayoutChangeListener
        public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            if (view.getWidth() <= 0 || view.getHeight() <= 0) {
                return;
            }
            XTouchTargetUtils.extendTouchAreaAsParentSameSize(view, this.f71a);
            this.f71a = null;
            view.removeOnLayoutChangeListener(this);
            view.removeOnAttachStateChangeListener(this);
            XTouchTargetUtils.log(" LayoutAttachStateChangeListener2  onLayoutChange ");
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View view) {
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View view) {
            view.removeOnLayoutChangeListener(this);
            view.removeOnAttachStateChangeListener(this);
            XTouchTargetUtils.log(" LayoutAttachStateChangeListener2  onViewDetachedFromWindow ");
        }
    }

    /* loaded from: classes.dex */
    private static class e implements View.OnAttachStateChangeListener, View.OnLayoutChangeListener {
        private static int f;

        /* renamed from: a  reason: collision with root package name */
        private View f72a;

        /* renamed from: b  reason: collision with root package name */
        int f73b;
        int c;
        int d;
        int e;

        e(View view, int i, int i2, int i3, int i4) {
            this.f72a = view;
            this.f73b = i;
            this.c = i2;
            this.d = i3;
            this.e = i4;
            f++;
        }

        protected void finalize() {
            super.finalize();
            f--;
            if (f == 0) {
                Log.d("xui-touch", " LayoutAttachStateChangeListener  finalize " + f);
            }
        }

        @Override // android.view.View.OnLayoutChangeListener
        public void onLayoutChange(View view, int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8) {
            if (view.getWidth() <= 0 || view.getHeight() <= 0) {
                return;
            }
            XTouchTargetUtils.extendViewTouchTarget(view, this.f72a, this.f73b, this.c, this.d, this.e);
            this.f72a = null;
            view.removeOnLayoutChangeListener(this);
            view.removeOnAttachStateChangeListener(this);
            XTouchTargetUtils.log(" LayoutAttachStateChangeListener  onLayoutChange ");
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View view) {
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View view) {
            view.removeOnLayoutChangeListener(this);
            view.removeOnAttachStateChangeListener(this);
            XTouchTargetUtils.log(" LayoutAttachStateChangeListener  onViewDetachedFromWindow ");
        }
    }

    /* loaded from: classes.dex */
    private static class f implements View.OnAttachStateChangeListener {
        private static int c;

        /* renamed from: a  reason: collision with root package name */
        private XTouchDelegate f74a;

        /* renamed from: b  reason: collision with root package name */
        private XTouchDelegateGroup f75b;

        f(XTouchDelegate xTouchDelegate, XTouchDelegateGroup xTouchDelegateGroup) {
            this.f74a = xTouchDelegate;
            this.f75b = xTouchDelegateGroup;
            c++;
        }

        protected void finalize() {
            super.finalize();
            c--;
            if (c == 0) {
                Log.d("xui-touch", " MyStateChangeListener finalize " + c);
            }
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View view) {
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View view) {
            view.removeOnAttachStateChangeListener(this);
            this.f75b.removeTouchDelegate(this.f74a);
            XTouchTargetUtils.log("  MyStateChangeListener onViewDetachedFromWindow " + view.hashCode());
            this.f75b = null;
            this.f74a = null;
        }
    }

    public static void extendTouchAreaAsParentSameSize(View view, ViewGroup viewGroup) {
        if (view == null || viewGroup == null) {
            return;
        }
        view.post(new c(view, viewGroup));
    }

    public static void extendViewTouchTarget(View view, int i, int i2, int i3, int i4, int i5) {
        view.post(new a(view, i, i2, i3, i4, i5));
    }

    public static void extendViewTouchTarget(View view, View view2, int i, int i2, int i3, int i4) {
        if (view == null || view2 == null) {
            return;
        }
        view2.post(new b(view, view2, i, i2, i3, i4));
    }

    public static View findViewAncestor(View view, int i) {
        while (view != null && view.getId() != i) {
            if (!(view.getParent() instanceof View)) {
                return null;
            }
            view = (View) view.getParent();
        }
        return view;
    }

    public static XTouchDelegateGroup getOrCreateTouchDelegateGroup(View view) {
        TouchDelegate touchDelegate = view.getTouchDelegate();
        if (touchDelegate != null) {
            if (touchDelegate instanceof XTouchDelegateGroup) {
                return (XTouchDelegateGroup) touchDelegate;
            }
            XTouchDelegateGroup xTouchDelegateGroup = new XTouchDelegateGroup(view);
            if (touchDelegate instanceof XTouchDelegate) {
                xTouchDelegateGroup.addTouchDelegate((XTouchDelegate) touchDelegate);
            }
            return xTouchDelegateGroup;
        }
        return new XTouchDelegateGroup(view);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static void log(String str) {
    }
}
