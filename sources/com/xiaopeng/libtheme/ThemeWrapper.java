package com.xiaopeng.libtheme;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import com.android.internal.util.ArrayUtils;
import com.xiaopeng.libtheme.ThemeManager;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/* loaded from: classes.dex */
public class ThemeWrapper {
    private static final String TAG = "ThemeWrapper";
    private static final long TIMEOUT = 5000;
    private static final int TYPE_DRAWABLE_BOTTOM = 3;
    private static final int TYPE_DRAWABLE_END = 5;
    private static final int TYPE_DRAWABLE_LEFT = 0;
    private static final int TYPE_DRAWABLE_RIGHT = 2;
    private static final int TYPE_DRAWABLE_START = 4;
    private static final int TYPE_DRAWABLE_TOP = 1;
    private static HashMap<String, List<ThemeView>> sThemeCache;
    private Handler mHandler = new g(null);
    public static final boolean DEBUG = ThemeManager.DEBUG;
    private static volatile ThemeWrapper sThemeWrapper = null;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class a implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        final /* synthetic */ Context f16a;

        /* renamed from: b  reason: collision with root package name */
        final /* synthetic */ ThemeData f17b;

        a(ThemeWrapper themeWrapper, Context context, ThemeData themeData) {
            this.f16a = context;
            this.f17b = themeData;
        }

        @Override // java.lang.Runnable
        public void run() {
            ThemeWrapper.sThemeCache.put(this.f17b.xml, ThemeParser.parseXml(this.f16a, this.f17b.xml));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class b implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        final /* synthetic */ Context f18a;

        /* renamed from: b  reason: collision with root package name */
        final /* synthetic */ ThemeData f19b;

        b(Context context, ThemeData themeData) {
            this.f18a = context;
            this.f19b = themeData;
        }

        @Override // java.lang.Runnable
        public void run() {
            ThemeWrapper.this.handleRefreshXml(this.f18a, this.f19b);
        }
    }

    /* loaded from: classes.dex */
    class c implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        final /* synthetic */ android.widget.TextView f20a;

        /* renamed from: b  reason: collision with root package name */
        final /* synthetic */ int f21b;
        final /* synthetic */ int c;

        /* loaded from: classes.dex */
        class a implements ValueAnimator.AnimatorUpdateListener {
            a() {
            }

            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                c.this.f20a.setTextColor(((Integer) valueAnimator.getAnimatedValue()).intValue());
            }
        }

        c(android.widget.TextView textView, int i, int i2) {
            this.f20a = textView;
            this.f21b = i;
            this.c = i2;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                if (this.f20a != null) {
                    ValueAnimator ofObject = ValueAnimator.ofObject(new ArgbEvaluator(), Integer.valueOf(this.f21b), Integer.valueOf(this.c));
                    ofObject.addUpdateListener(new a());
                    ofObject.setDuration((int) (ThemeManager.THEME_ANIMATION_INTERVAL / 2));
                    ofObject.start();
                }
            } catch (Exception e) {
                if (ThemeWrapper.DEBUG) {
                    Log.d(ThemeWrapper.TAG, "startTextColorAnimation e=" + e);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    class d implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        final /* synthetic */ android.widget.TextView f23a;

        /* renamed from: b  reason: collision with root package name */
        final /* synthetic */ ColorStateList f24b;
        final /* synthetic */ int c;

        /* loaded from: classes.dex */
        class a implements ValueAnimator.AnimatorUpdateListener {
            a() {
            }

            @Override // android.animation.ValueAnimator.AnimatorUpdateListener
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                d.this.f23a.setTextColor(((Integer) valueAnimator.getAnimatedValue()).intValue());
            }
        }

        /* loaded from: classes.dex */
        class b implements Runnable {
            b() {
            }

            @Override // java.lang.Runnable
            public void run() {
                d dVar = d.this;
                dVar.f23a.setTextColor(dVar.f24b);
            }
        }

        d(android.widget.TextView textView, ColorStateList colorStateList, int i) {
            this.f23a = textView;
            this.f24b = colorStateList;
            this.c = i;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                if (this.f23a == null || this.f24b == null) {
                    return;
                }
                ValueAnimator ofObject = ValueAnimator.ofObject(new ArgbEvaluator(), Integer.valueOf(this.c), Integer.valueOf(this.f24b.getDefaultColor()));
                ofObject.addUpdateListener(new a());
                ofObject.setDuration((int) (ThemeManager.THEME_ANIMATION_INTERVAL / 2));
                ofObject.start();
                this.f23a.postDelayed(new b(), ThemeManager.THEME_ANIMATION_INTERVAL * 2);
            } catch (Exception e) {
                if (ThemeWrapper.DEBUG) {
                    Log.d(ThemeWrapper.TAG, "startTextColorAnimation e=" + e);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    class e implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        final /* synthetic */ ImageView f27a;

        /* renamed from: b  reason: collision with root package name */
        final /* synthetic */ Drawable f28b;
        final /* synthetic */ Drawable c;

        /* loaded from: classes.dex */
        class a implements Runnable {
            a() {
            }

            @Override // java.lang.Runnable
            public void run() {
                e eVar = e.this;
                eVar.f27a.setImageDrawable(eVar.c);
            }
        }

        e(ImageView imageView, Drawable drawable, Drawable drawable2) {
            this.f27a = imageView;
            this.f28b = drawable;
            this.c = drawable2;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                if (this.f27a == null || this.f28b == null || this.c == null) {
                    return;
                }
                if (this.c.getLevel() != this.f28b.getLevel()) {
                    this.c.setLevel(this.f28b.getLevel());
                }
                TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{this.f28b, this.c});
                this.f27a.setImageDrawable(transitionDrawable);
                transitionDrawable.startTransition((int) (ThemeManager.THEME_ANIMATION_INTERVAL / 2));
                this.f27a.postDelayed(new a(), (int) (ThemeManager.THEME_ANIMATION_INTERVAL / 2));
            } catch (Exception e) {
                if (ThemeWrapper.DEBUG) {
                    Log.d(ThemeWrapper.TAG, "startImageDrawableTransition e=" + e);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    class f implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        final /* synthetic */ View f30a;

        /* renamed from: b  reason: collision with root package name */
        final /* synthetic */ Drawable f31b;
        final /* synthetic */ Drawable c;

        /* loaded from: classes.dex */
        class a implements Runnable {
            a() {
            }

            @Override // java.lang.Runnable
            public void run() {
                f fVar = f.this;
                fVar.f30a.setBackground(fVar.c);
            }
        }

        f(View view, Drawable drawable, Drawable drawable2) {
            this.f30a = view;
            this.f31b = drawable;
            this.c = drawable2;
        }

        @Override // java.lang.Runnable
        public void run() {
            try {
                if (this.f30a == null || this.f31b == null || this.c == null) {
                    return;
                }
                if (this.c.getLevel() != this.f31b.getLevel()) {
                    this.c.setLevel(this.f31b.getLevel());
                }
                TransitionDrawable transitionDrawable = new TransitionDrawable(new Drawable[]{this.f31b, this.c});
                this.f30a.setBackground(transitionDrawable);
                transitionDrawable.startTransition((int) (ThemeManager.THEME_ANIMATION_INTERVAL / 2));
                this.f30a.postDelayed(new a(), (int) (ThemeManager.THEME_ANIMATION_INTERVAL / 2));
            } catch (Exception e) {
                if (ThemeWrapper.DEBUG) {
                    Log.d(ThemeWrapper.TAG, "startViewBackgroundTransition e=" + e);
                }
            }
        }
    }

    /* loaded from: classes.dex */
    private static class g extends Handler {
        private g() {
        }

        /* synthetic */ g(a aVar) {
            this();
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            super.handleMessage(message);
            if (message.what != 100) {
                return;
            }
            ThemeWrapper.getInstance().onTimeout();
        }
    }

    private ThemeWrapper() {
        sThemeCache = new HashMap<>();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static ThemeWrapper getInstance() {
        if (sThemeWrapper == null) {
            synchronized (ThemeWrapper.class) {
                if (sThemeWrapper == null) {
                    sThemeWrapper = new ThemeWrapper();
                }
            }
        }
        return sThemeWrapper;
    }

    private static Drawable getOverlayDrawable(Drawable drawable, Drawable drawable2) {
        if (drawable != null && drawable2 != null && drawable.getLevel() != drawable2.getLevel()) {
            drawable.setLevel(drawable2.getLevel());
        }
        return drawable;
    }

    /* JADX WARN: Code restructure failed: missing block: B:18:0x002b, code lost:
        if (r0 != null) goto L19;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0036, code lost:
        if (r0 == null) goto L8;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0038, code lost:
        r0.recycle();
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static int getThemeResourceId(int r3, android.content.Context r4) {
        /*
            r0 = 0
            if (r4 == 0) goto L8
            android.content.Context r4 = r4.getApplicationContext()
            goto L9
        L8:
            r4 = r0
        L9:
            r1 = 1
            r2 = 0
            if (r4 == 0) goto L19
            android.content.res.Resources$Theme r4 = r4.getTheme()
            int[] r0 = new int[r1]
            r0[r2] = r3
            android.content.res.TypedArray r0 = r4.obtainStyledAttributes(r0)
        L19:
            if (r0 == 0) goto L3b
            int r3 = r0.getResourceId(r2, r2)     // Catch: java.lang.Throwable -> L2e java.lang.Exception -> L35
            if (r3 == 0) goto L22
            goto L23
        L22:
            r1 = r2
        L23:
            if (r1 == 0) goto L2b
            if (r0 == 0) goto L2a
            r0.recycle()
        L2a:
            return r3
        L2b:
            if (r0 == 0) goto L3b
            goto L38
        L2e:
            r3 = move-exception
            if (r0 == 0) goto L34
            r0.recycle()
        L34:
            throw r3
        L35:
            r3 = move-exception
            if (r0 == 0) goto L3b
        L38:
            r0.recycle()
        L3b:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.libtheme.ThemeWrapper.getThemeResourceId(int, android.content.Context):int");
    }

    /* JADX WARN: Code restructure failed: missing block: B:18:0x0027, code lost:
        if (r0 != null) goto L19;
     */
    /* JADX WARN: Code restructure failed: missing block: B:25:0x0032, code lost:
        if (r0 == null) goto L8;
     */
    /* JADX WARN: Code restructure failed: missing block: B:26:0x0034, code lost:
        r0.recycle();
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static int getThemeResourceId(int r3, android.content.Context r4, android.util.AttributeSet r5, int r6, int r7) {
        /*
            r0 = 0
            if (r4 == 0) goto L8
            android.content.Context r4 = r4.getApplicationContext()
            goto L9
        L8:
            r4 = r0
        L9:
            r1 = 1
            r2 = 0
            if (r4 == 0) goto L15
            int[] r0 = new int[r1]
            r0[r2] = r3
            android.content.res.TypedArray r0 = r4.obtainStyledAttributes(r5, r0, r6, r7)
        L15:
            if (r0 == 0) goto L37
            int r3 = r0.getResourceId(r2, r2)     // Catch: java.lang.Throwable -> L2a java.lang.Exception -> L31
            if (r3 == 0) goto L1e
            goto L1f
        L1e:
            r1 = r2
        L1f:
            if (r1 == 0) goto L27
            if (r0 == 0) goto L26
            r0.recycle()
        L26:
            return r3
        L27:
            if (r0 == 0) goto L37
            goto L34
        L2a:
            r3 = move-exception
            if (r0 == 0) goto L30
            r0.recycle()
        L30:
            throw r3
        L31:
            r3 = move-exception
            if (r0 == 0) goto L37
        L34:
            r0.recycle()
        L37:
            return r2
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.libtheme.ThemeWrapper.getThemeResourceId(int, android.content.Context, android.util.AttributeSet, int, int):int");
    }

    private View getThemeView(Context context, View view, ThemeView themeView) {
        int i;
        View findViewById;
        View view2 = null;
        if (context == null || themeView == null || view == null) {
            return null;
        }
        if (themeView.resId > 0 && (i = themeView.resRoot) > 0 && (findViewById = view.findViewById(i)) != null) {
            view2 = findViewById.findViewById(themeView.resId);
        }
        int i2 = themeView.resId;
        return (i2 <= 0 || view2 != null) ? view2 : view.findViewById(i2);
    }

    protected static String getViewInfo(View view, int i, String str) {
        String str2;
        TypedValue typedValue;
        StringBuffer stringBuffer = new StringBuffer("getViewInfo");
        if (view != null) {
            try {
                Context applicationContext = view.getContext().getApplicationContext();
                if (applicationContext != null) {
                    applicationContext.getResources().getValue(i, new TypedValue(), true);
                    stringBuffer.append(" view.id=0x" + Integer.toHexString(view.getId()));
                    stringBuffer.append(" view.resId=0x" + Integer.toHexString(i));
                    stringBuffer.append(" view.attr=" + str);
                    stringBuffer.append(" view.toString=" + view.toString());
                    stringBuffer.append(" view.value.string=" + ((Object) typedValue.string));
                    str2 = " view.value=" + typedValue.toString();
                } else {
                    str2 = " context null";
                }
                stringBuffer.append(str2);
            } catch (Exception e2) {
                stringBuffer.append(" error=" + e2);
            }
        }
        return stringBuffer.toString();
    }

    private void handleRefreshList(Context context, ThemeData themeData) {
        List<ThemeView> list;
        if (themeData == null || (list = themeData.list) == null) {
            return;
        }
        for (ThemeView themeView : list) {
            updateViewResource(context, themeData.root, themeView);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleRefreshXml(Context context, ThemeData themeData) {
        if (themeData == null || TextUtils.isEmpty(themeData.xml)) {
            return;
        }
        boolean containsKey = sThemeCache.containsKey(themeData.xml);
        ThemeManager.Logger.log("handleRefreshXml xmlCached=" + containsKey);
        if (!containsKey) {
            this.mHandler.postDelayed(new b(context, themeData), 10L);
            return;
        }
        List<ThemeView> list = sThemeCache.get(themeData.xml);
        if (list != null) {
            for (ThemeView themeView : list) {
                updateViewResource(context, themeData.root, themeView);
            }
        }
    }

    private synchronized void handleThemeChanged(Context context, ThemeData themeData) {
        ThemeManager.Logger.log("handleThemeChanged");
        put(context, themeData);
        timeout();
        refresh(context, themeData);
    }

    private static void invokeViewResource(Context context, View view, String str, int i) {
        try {
            if (TextUtils.isEmpty(str) || str.length() <= 0) {
                return;
            }
            view.getClass().getPackage().getName();
            String name = view.getClass().getName();
            String str2 = "set" + str.substring(0, 1).toUpperCase() + str.substring(1);
            ThemeManager.Logger.log("invokeViewResource view info:" + getViewInfo(view, i, str));
            Class<?> cls = Class.forName(name);
            if (cls != null) {
                cls.getMethod(str2, Integer.TYPE).invoke(view, Integer.valueOf(i));
            }
        } catch (Exception e2) {
            ThemeManager.Logger.log(TAG, "invokeViewResource view=" + view + " e=" + e2);
        }
    }

    protected static long now() {
        return System.currentTimeMillis();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onTimeout() {
        this.mHandler.removeCallbacksAndMessages(null);
        HashMap<String, List<ThemeView>> hashMap = sThemeCache;
        if (hashMap != null) {
            hashMap.clear();
        }
        sThemeCache = null;
        sThemeWrapper = null;
        this.mHandler = null;
        ThemeManager.Logger.log("onTimeout time=" + now());
    }

    private void put(Context context, ThemeData themeData) {
        if (context == null || themeData == null) {
            return;
        }
        try {
            if (TextUtils.isEmpty(themeData.xml)) {
                return;
            }
            this.mHandler.post(new a(this, context, themeData));
        } catch (Exception e2) {
        }
    }

    private void refresh(Context context, ThemeData themeData) {
        handleRefreshList(context, themeData);
        handleRefreshXml(context, themeData);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static HashMap<String, Integer> resolveAttribute(Context context, AttributeSet attributeSet, int i, int i2, List<String> list) {
        boolean z;
        int globalAttribute;
        int themeResourceId;
        HashMap<String, Integer> hashMap = new HashMap<>();
        if (attributeSet != null) {
            int attributeCount = attributeSet.getAttributeCount();
            z = false;
            for (int i3 = 0; i3 < attributeCount; i3++) {
                try {
                    String attributeName = attributeSet.getAttributeName(i3);
                    String attributeValue = attributeSet.getAttributeValue(i3);
                    ThemeManager.Logger.log("resolveAttribute common attr name=" + attributeName + " value=" + attributeValue);
                    if (!TextUtils.isEmpty(attributeName) && !TextUtils.isEmpty(attributeValue)) {
                        boolean z2 = list != null && list.contains(attributeName);
                        if ((ThemeManager.AttributeSet.hasAttribute(attributeName) || z2) && attributeValue.startsWith("@")) {
                            if (ThemeManager.AttributeSet.isThemeAttribute(attributeName)) {
                                z = true;
                            } else {
                                hashMap.put(attributeName, Integer.valueOf(Integer.parseInt(attributeValue.substring(1))));
                            }
                        }
                    }
                } catch (Exception e2) {
                }
            }
        } else {
            z = false;
        }
        if (z) {
            context.getTheme();
            for (String str : ThemeManager.AttributeSet.sAttributeMap.keySet()) {
                int intValue = ThemeManager.AttributeSet.sAttributeMap.get(str).intValue();
                if (intValue != 0) {
                    int themeResourceId2 = getThemeResourceId(intValue, context, attributeSet, i, i2);
                    ThemeManager.Logger.log("resolveAttribute theme attr key=" + str + " resId=0x" + Integer.toHexString(themeResourceId2));
                    if (themeResourceId2 != 0) {
                        hashMap.put(str, Integer.valueOf(themeResourceId2));
                    }
                }
            }
        }
        if (i > 0 || i2 > 0) {
            try {
                HashMap<String, Integer> hashMap2 = ThemeManager.AttributeSet.sAttributeMap;
                HashMap hashMap3 = new HashMap();
                for (String str2 : hashMap2.keySet()) {
                    hashMap3.put(hashMap2.get(str2), str2);
                }
                int[] convertToIntArray = ArrayUtils.convertToIntArray(new ArrayList(hashMap3.keySet()));
                if (convertToIntArray != null && !hashMap3.isEmpty()) {
                    for (int i4 : convertToIntArray) {
                        String str3 = (String) hashMap3.get(Integer.valueOf(i4));
                        int themeResourceId3 = getThemeResourceId(i4, context, null, i, i2);
                        ThemeManager.Logger.log("resolveAttribute style attr key=" + str3 + " resId=0x" + Integer.toHexString(themeResourceId3));
                        if (!hashMap.containsKey(str3) && themeResourceId3 != 0) {
                            hashMap.put(str3, Integer.valueOf(themeResourceId3));
                        }
                    }
                }
            } catch (Exception e3) {
                ThemeManager.Logger.log(TAG, "resolveAttribute style e=" + e3);
            }
        }
        if (list != null && !list.isEmpty()) {
            for (String str4 : list) {
                if (!TextUtils.isEmpty(str4) && ThemeManager.AttributeSet.hasGlobalAttribute(str4) && (globalAttribute = ThemeManager.AttributeSet.getGlobalAttribute(str4)) != 0 && (themeResourceId = getThemeResourceId(globalAttribute, context)) != 0 && !hashMap.containsKey(str4)) {
                    hashMap.put(str4, Integer.valueOf(themeResourceId));
                }
            }
        }
        return hashMap;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void setAttributeValue(HashMap<String, Integer> hashMap, String str, int i) {
        if (hashMap == null || TextUtils.isEmpty(str) || i == 0) {
            return;
        }
        try {
            hashMap.put(str, Integer.valueOf(i));
        } catch (Exception e2) {
        }
    }

    private static void setCompoundDrawables(int i, android.widget.TextView textView, Drawable drawable) {
        if (drawable == null || textView == null) {
            return;
        }
        switch (i) {
            case 0:
            case 1:
            case 2:
            case 3:
                Drawable[] compoundDrawables = textView.getCompoundDrawables();
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                if (compoundDrawables != null) {
                    compoundDrawables[i] = getOverlayDrawable(drawable, compoundDrawables[i]);
                    textView.setCompoundDrawables(compoundDrawables[0], compoundDrawables[1], compoundDrawables[2], compoundDrawables[3]);
                    return;
                }
                return;
            case 4:
            case 5:
                Drawable[] compoundDrawablesRelative = textView.getCompoundDrawablesRelative();
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                if (compoundDrawablesRelative != null) {
                    char c2 = i == 4 ? (char) 0 : (char) 2;
                    if (compoundDrawablesRelative != null) {
                        compoundDrawablesRelative[c2] = getOverlayDrawable(drawable, compoundDrawablesRelative[c2]);
                        textView.setCompoundDrawablesRelative(compoundDrawablesRelative[0], compoundDrawablesRelative[1], compoundDrawablesRelative[2], compoundDrawablesRelative[3]);
                        return;
                    }
                    return;
                }
                return;
            default:
                return;
        }
    }

    private static void setVerticalThumbDrawable(View view, Drawable drawable) {
        try {
            Method declaredMethod = View.class.getDeclaredMethod("getScrollCache", new Class[0]);
            declaredMethod.setAccessible(true);
            Object invoke = declaredMethod.invoke(view, new Object[0]);
            Object obj = invoke.getClass().getField("scrollBar").get(invoke);
            Field declaredField = obj.getClass().getDeclaredField("mVerticalThumb");
            declaredField.setAccessible(true);
            declaredField.set(obj, drawable);
        } catch (Exception e2) {
            e2.printStackTrace();
        }
    }

    private static void setViewResource(Context context, View view, String str, int i) {
        int i2;
        android.widget.TextView textView;
        Context applicationContext = context != null ? context.getApplicationContext() : null;
        if (applicationContext == null || view == null || TextUtils.isEmpty(str)) {
            return;
        }
        Resources resources = applicationContext.getResources();
        applicationContext.getTheme();
        try {
            if (ThemeManager.AttributeSet.ALPHA.equals(str)) {
                if (Build.VERSION.SDK_INT >= 29) {
                    view.setAlpha(resources.getFloat(i));
                }
            } else if (ThemeManager.AttributeSet.BACKGROUND.equals(str)) {
                view.setBackground(getOverlayDrawable(a.a.a.a.b(applicationContext, i), view.getBackground()));
            } else if (ThemeManager.AttributeSet.FOREGROUND.equals(str)) {
                if (Build.VERSION.SDK_INT >= 23) {
                    view.setForeground(getOverlayDrawable(a.a.a.a.b(applicationContext, i), view.getForeground()));
                }
            } else if (ThemeManager.AttributeSet.SCROLLBAR_THUMB_VERTICAL.equals(str)) {
                if (view.isScrollContainer()) {
                    setVerticalThumbDrawable(view, a.a.a.a.b(applicationContext, i));
                }
            } else if (ThemeManager.AttributeSet.SRC.equals(str)) {
                if (view instanceof ImageView) {
                    ImageView imageView = (ImageView) view;
                    imageView.setImageDrawable(getOverlayDrawable(a.a.a.a.b(applicationContext, i), imageView.getDrawable()));
                }
            } else if (ThemeManager.AttributeSet.TEXT_COLOR.equals(str)) {
                if (view instanceof android.widget.TextView) {
                    ((android.widget.TextView) view).setTextColor(a.a.a.a.a(applicationContext, i));
                }
            } else if (ThemeManager.AttributeSet.TEXT_COLOR_HINT.equals(str)) {
                if (view instanceof android.widget.TextView) {
                    ((android.widget.TextView) view).setHintTextColor(a.a.a.a.a(applicationContext, i));
                }
            } else if (ThemeManager.AttributeSet.DRAWABLE_LEFT.equals(str)) {
                if (view instanceof android.widget.TextView) {
                    i2 = 0;
                    textView = (android.widget.TextView) view;
                    setCompoundDrawables(i2, textView, a.a.a.a.b(applicationContext, i));
                }
            } else if (ThemeManager.AttributeSet.DRAWABLE_TOP.equals(str)) {
                if (view instanceof android.widget.TextView) {
                    i2 = 1;
                    textView = (android.widget.TextView) view;
                    setCompoundDrawables(i2, textView, a.a.a.a.b(applicationContext, i));
                }
            } else if (ThemeManager.AttributeSet.DRAWABLE_RIGHT.equals(str)) {
                if (view instanceof android.widget.TextView) {
                    i2 = 2;
                    textView = (android.widget.TextView) view;
                    setCompoundDrawables(i2, textView, a.a.a.a.b(applicationContext, i));
                }
            } else if (ThemeManager.AttributeSet.DRAWABLE_BOTTOM.equals(str)) {
                if (view instanceof android.widget.TextView) {
                    i2 = 3;
                    textView = (android.widget.TextView) view;
                    setCompoundDrawables(i2, textView, a.a.a.a.b(applicationContext, i));
                }
            } else if (ThemeManager.AttributeSet.DRAWABLE_START.equals(str)) {
                if (view instanceof android.widget.TextView) {
                    i2 = 4;
                    textView = (android.widget.TextView) view;
                    setCompoundDrawables(i2, textView, a.a.a.a.b(applicationContext, i));
                }
            } else if (ThemeManager.AttributeSet.DRAWABLE_END.equals(str)) {
                if (view instanceof android.widget.TextView) {
                    i2 = 5;
                    textView = (android.widget.TextView) view;
                    setCompoundDrawables(i2, textView, a.a.a.a.b(applicationContext, i));
                }
            } else if (ThemeManager.AttributeSet.PROGRESS_DRAWABLE.equals(str)) {
                if (view instanceof ProgressBar) {
                    ((ProgressBar) view).setProgressDrawable(a.a.a.a.b(applicationContext, i));
                }
            } else if (ThemeManager.AttributeSet.THUMB.equals(str)) {
                if (view instanceof SeekBar) {
                    ((SeekBar) view).setThumb(a.a.a.a.b(applicationContext, i));
                }
            } else if (ThemeManager.AttributeSet.BUTTON.equals(str)) {
                if (view instanceof CompoundButton) {
                    ((CompoundButton) view).setButtonDrawable(a.a.a.a.b(applicationContext, i));
                }
            } else if (ThemeManager.AttributeSet.DIVIDER.equals(str) && (view instanceof ListView)) {
                ((ListView) view).setDivider(a.a.a.a.b(applicationContext, i));
            }
            view.refreshDrawableState();
            ThemeManager.Logger.log("setViewResource view info:" + getViewInfo(view, i, str));
        } catch (Exception e2) {
            ThemeManager.Logger.log("setViewResource error attr=" + str + " view=" + view + " e=" + e2);
        }
    }

    protected static void startImageDrawableTransition(ImageView imageView, Drawable drawable, Drawable drawable2) {
        e eVar = new e(imageView, drawable, drawable2);
        if (imageView != null) {
            imageView.post(eVar);
        }
    }

    protected static void startTextColorTransition(android.widget.TextView textView, int i, int i2) {
        c cVar = new c(textView, i, i2);
        if (textView != null) {
            textView.post(cVar);
        }
    }

    protected static void startTextColorTransition(android.widget.TextView textView, int i, ColorStateList colorStateList) {
        d dVar = new d(textView, colorStateList, i);
        if (textView != null) {
            textView.post(dVar);
        }
    }

    protected static void startViewBackgroundTransition(View view, Drawable drawable, Drawable drawable2) {
        f fVar = new f(view, drawable, drawable2);
        if (view != null) {
            view.post(fVar);
        }
    }

    private void timeout() {
        this.mHandler.removeMessages(100);
        this.mHandler.sendEmptyMessageDelayed(100, TIMEOUT);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public static void updateAttribute(View view, HashMap<String, Integer> hashMap) {
        if (view == null || hashMap == null) {
            return;
        }
        for (String str : hashMap.keySet()) {
            if (ThemeManager.AttributeSet.hasAttribute(str)) {
                setViewResource(view.getContext(), view, str, hashMap.get(str).intValue());
            } else {
                invokeViewResource(view.getContext(), view, str, hashMap.get(str) != null ? hashMap.get(str).intValue() : 0);
            }
        }
    }

    private void updateViewResource(Context context, View view, ThemeView themeView) {
        int i;
        try {
            View themeView2 = getThemeView(context, view, themeView);
            if (context == null || view == null || themeView == null || themeView2 == null || themeView.resValue == null || TextUtils.isEmpty(themeView.resAttr)) {
                return;
            }
            String str = themeView.resAttr;
            try {
                i = Integer.parseInt(themeView.resValue.toString());
            } catch (Exception e2) {
                i = -1;
            }
            if (ThemeManager.AttributeSet.hasAttribute(str)) {
                setViewResource(context, themeView2, str, i);
            } else {
                invokeViewResource(context, themeView2, str, i);
            }
        } catch (Exception e3) {
            ThemeManager.Logger.log(TAG, "updateViewResource e=" + e3);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public synchronized void onConfigurationChanged(Context context, ThemeData themeData, Configuration configuration) {
        Context applicationContext = context.getApplicationContext();
        boolean isNightMode = ThemeManager.isNightMode(applicationContext);
        boolean isThemeChanged = ThemeManager.isThemeChanged(configuration);
        ThemeManager.Logger.log(TAG, "onConfigurationChanged isNightMode=" + isNightMode + " isThemeChanged=" + isThemeChanged);
        if (isThemeChanged) {
            handleThemeChanged(applicationContext, themeData);
        }
    }
}
