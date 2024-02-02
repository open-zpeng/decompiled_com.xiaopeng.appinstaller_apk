package com.xiaopeng.xui.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.theme.XThemeManager;
import com.xiaopeng.xui.view.XViewDelegate;
import com.xiaopeng.xui.view.font.XFontScaleHelper;
import com.xiaopeng.xui.vui.VuiView;
import com.xiaopeng.xui.vui.floatinglayer.VuiFloatingLayerManager;
import java.util.Arrays;
import xiaopeng.widget.BuildConfig;
/* loaded from: classes.dex */
public class XTabLayout extends XLinearLayout implements b.a.a.a.c {
    private static final long DURATION = 300;
    private static final float INDICATOR_DAY_HEIGHT_PERCENT = 1.0f;
    private static final double K = 1.048d;
    private static final double K2 = 4.648d;
    public static final int STYLE_DAY = 1;
    public static final int STYLE_NIGHT = 2;
    private static final String TAG = "XTabLayout";
    private final int MARGIN_DAY;
    private Paint mBlurPaint;
    private Paint mBlurPaint2;
    private View.OnClickListener mChildClickListener;
    private int mCurrentEnd;
    private int mCurrentEnd2;
    private int mCurrentStart;
    private int mCurrentStart2;
    private float mDivideValue;
    private CharSequence[] mIconVuiLabels;
    private boolean mIndicatorAnimatorEnable;
    private int mIndicatorColor;
    private int mIndicatorColor2;
    private int mIndicatorColorFrom;
    private int mIndicatorColorTo;
    private boolean mIndicatorDayNightDiff;
    private float mIndicatorHeight;
    private float mIndicatorMarginBottom;
    private float mIndicatorMaxHeight;
    private float mIndicatorMinHeight;
    private int mIndicatorShadowColor;
    private int mIndicatorShadowColor2;
    private float mIndicatorShadowRadius;
    private float mIndicatorShadowRadius2;
    private float mIndicatorWidth;
    private float mIndicatorWidthPercent;
    private boolean mIsDetachedFromWindow;
    private boolean mIsDetachedNightTheme;
    private OnTabChangeListener mOnTabChangeListener;
    private int mPaddingNight;
    private Paint mPaint;
    private Paint mPaint2;
    private int mSelectTabIndex;
    private int mStyle;
    private boolean mTabCustomBackground;
    private boolean mTabsBarStyle;
    private int mTempEnd;
    private int mTempEnd2;
    private int mTempStart;
    private int mTempStart2;
    private int[] mTitleIcons;
    private CharSequence[] mTitleString;
    private int mTitleTextColorRes;
    private ColorStateList mTitleTextColorStateList;
    private float mTitleTextSize;
    private int mToEnd;
    private int mToEnd2;
    private int mToStart;
    private int mToStart2;
    private ValueAnimator mValueAnimator;

    /* loaded from: classes.dex */
    public interface OnTabChangeListener {
        boolean onInterceptTabChange(XTabLayout xTabLayout, int i, boolean z, boolean z2);

        void onTabChangeEnd(XTabLayout xTabLayout, int i, boolean z, boolean z2);

        void onTabChangeStart(XTabLayout xTabLayout, int i, boolean z, boolean z2);
    }

    /* loaded from: classes.dex */
    public static abstract class OnTabChangeListenerAdapter implements OnTabChangeListener {
        @Override // com.xiaopeng.xui.widget.XTabLayout.OnTabChangeListener
        public boolean onInterceptTabChange(XTabLayout xTabLayout, int i, boolean z, boolean z2) {
            return false;
        }

        @Override // com.xiaopeng.xui.widget.XTabLayout.OnTabChangeListener
        public void onTabChangeStart(XTabLayout xTabLayout, int i, boolean z, boolean z2) {
        }
    }

    /* loaded from: classes.dex */
    class a implements View.OnClickListener {
        a() {
        }

        @Override // android.view.View.OnClickListener
        public void onClick(View view) {
            if (XTabLayout.this.mValueAnimator == null || !XTabLayout.this.mValueAnimator.isRunning()) {
                XTabLayout xTabLayout = XTabLayout.this;
                xTabLayout.selectTab(xTabLayout.indexOfChild(view), true, true);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class b implements ViewGroup.OnHierarchyChangeListener {

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public class a implements Runnable {

            /* renamed from: a  reason: collision with root package name */
            final /* synthetic */ boolean f99a;

            /* renamed from: com.xiaopeng.xui.widget.XTabLayout$b$a$a  reason: collision with other inner class name */
            /* loaded from: classes.dex */
            class C0003a implements i {
                C0003a() {
                }

                @Override // com.xiaopeng.xui.widget.XTabLayout.i
                public void a() {
                    if (XTabLayout.this.mOnTabChangeListener != null) {
                        OnTabChangeListener onTabChangeListener = XTabLayout.this.mOnTabChangeListener;
                        XTabLayout xTabLayout = XTabLayout.this;
                        onTabChangeListener.onTabChangeStart(xTabLayout, xTabLayout.mSelectTabIndex, a.this.f99a, false);
                    }
                }

                @Override // com.xiaopeng.xui.widget.XTabLayout.i
                public void b() {
                    if (XTabLayout.this.mOnTabChangeListener != null) {
                        OnTabChangeListener onTabChangeListener = XTabLayout.this.mOnTabChangeListener;
                        XTabLayout xTabLayout = XTabLayout.this;
                        onTabChangeListener.onTabChangeEnd(xTabLayout, xTabLayout.mSelectTabIndex, a.this.f99a, false);
                    }
                }
            }

            a(boolean z) {
                this.f99a = z;
            }

            @Override // java.lang.Runnable
            public void run() {
                XTabLayout.this.moveIndicatorTo(true, new C0003a());
            }
        }

        b() {
        }

        private void a(boolean z) {
            XTabLayout.this.post(new a(z));
        }

        @Override // android.view.ViewGroup.OnHierarchyChangeListener
        public void onChildViewAdded(View view, View view2) {
            view2.setOnClickListener(XTabLayout.this.mChildClickListener);
            if (XTabLayout.this.mSelectTabIndex < 0) {
                XTabLayout xTabLayout = XTabLayout.this;
                xTabLayout.mSelectTabIndex = xTabLayout.indexOfChild(view2);
                view2.setSelected(true);
            }
            Object tag = view2.getTag();
            a(tag == null ? false : ((Boolean) tag).booleanValue());
        }

        @Override // android.view.ViewGroup.OnHierarchyChangeListener
        public void onChildViewRemoved(View view, View view2) {
            view2.setOnClickListener(null);
            Object tag = view2.getTag();
            a(tag == null ? false : ((Boolean) tag).booleanValue());
        }
    }

    /* loaded from: classes.dex */
    class c implements i {

        /* renamed from: a  reason: collision with root package name */
        final /* synthetic */ boolean f102a;

        c(boolean z) {
            this.f102a = z;
        }

        @Override // com.xiaopeng.xui.widget.XTabLayout.i
        public void a() {
            if (!this.f102a || XTabLayout.this.mOnTabChangeListener == null) {
                return;
            }
            XTabLayout.this.mOnTabChangeListener.onTabChangeStart(XTabLayout.this, -1, true, false);
        }

        @Override // com.xiaopeng.xui.widget.XTabLayout.i
        public void b() {
            if (!this.f102a || XTabLayout.this.mOnTabChangeListener == null) {
                return;
            }
            XTabLayout.this.mOnTabChangeListener.onTabChangeEnd(XTabLayout.this, -1, true, false);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class d implements i {

        /* renamed from: a  reason: collision with root package name */
        final /* synthetic */ boolean f104a;

        /* renamed from: b  reason: collision with root package name */
        final /* synthetic */ View f105b;
        final /* synthetic */ boolean c;
        final /* synthetic */ int d;

        d(boolean z, View view, boolean z2, int i) {
            this.f104a = z;
            this.f105b = view;
            this.c = z2;
            this.d = i;
        }

        @Override // com.xiaopeng.xui.widget.XTabLayout.i
        public void a() {
            if (!this.f104a || XTabLayout.this.mOnTabChangeListener == null) {
                return;
            }
            if (this.f105b == null) {
                XTabLayout.this.mOnTabChangeListener.onTabChangeStart(XTabLayout.this, -1, true, this.c);
            } else {
                XTabLayout.this.mOnTabChangeListener.onTabChangeStart(XTabLayout.this, this.d, true, this.c);
            }
        }

        @Override // com.xiaopeng.xui.widget.XTabLayout.i
        public void b() {
            if (!this.f104a || XTabLayout.this.mOnTabChangeListener == null) {
                return;
            }
            if (this.f105b == null) {
                XTabLayout.this.mOnTabChangeListener.onTabChangeEnd(XTabLayout.this, -1, true, this.c);
            } else {
                XTabLayout.this.mOnTabChangeListener.onTabChangeEnd(XTabLayout.this, this.d, true, this.c);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class e implements ValueAnimator.AnimatorUpdateListener {
        e() {
        }

        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            XTabLayout xTabLayout;
            float f;
            XTabLayout xTabLayout2;
            int pow;
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            float min = Math.min(floatValue, XTabLayout.this.mDivideValue) / XTabLayout.this.mDivideValue;
            if (floatValue < XTabLayout.this.mDivideValue) {
                xTabLayout = XTabLayout.this;
                f = xTabLayout.mIndicatorMaxHeight - ((floatValue / XTabLayout.this.mDivideValue) * (XTabLayout.this.mIndicatorMaxHeight - XTabLayout.this.mIndicatorMinHeight));
            } else {
                xTabLayout = XTabLayout.this;
                f = xTabLayout.mIndicatorMaxHeight + ((((floatValue - XTabLayout.this.mDivideValue) / (1.0f - XTabLayout.this.mDivideValue)) - 1.0f) * (XTabLayout.this.mIndicatorMaxHeight - XTabLayout.this.mIndicatorMinHeight));
            }
            xTabLayout.mIndicatorHeight = (int) f;
            if (XTabLayout.this.mToStart > XTabLayout.this.mCurrentStart) {
                XTabLayout xTabLayout3 = XTabLayout.this;
                xTabLayout3.mTempStart = (int) (xTabLayout3.mCurrentStart + (Math.pow(floatValue, XTabLayout.K) * (XTabLayout.this.mToStart - XTabLayout.this.mCurrentStart)));
                xTabLayout2 = XTabLayout.this;
                pow = (int) (xTabLayout2.mCurrentEnd + ((XTabLayout.this.mToEnd - XTabLayout.this.mCurrentEnd) * min));
            } else {
                XTabLayout xTabLayout4 = XTabLayout.this;
                xTabLayout4.mTempStart = (int) (xTabLayout4.mCurrentStart + ((XTabLayout.this.mToStart - XTabLayout.this.mCurrentStart) * min));
                xTabLayout2 = XTabLayout.this;
                pow = (int) (xTabLayout2.mCurrentEnd + (Math.pow(floatValue, XTabLayout.K) * (XTabLayout.this.mToEnd - XTabLayout.this.mCurrentEnd)));
            }
            xTabLayout2.mTempEnd = pow;
            if (XTabLayout.this.mToStart2 > XTabLayout.this.mCurrentStart2) {
                XTabLayout xTabLayout5 = XTabLayout.this;
                xTabLayout5.mTempStart2 = (int) (xTabLayout5.mCurrentStart2 + (Math.pow(floatValue, XTabLayout.K2) * (XTabLayout.this.mToStart2 - XTabLayout.this.mCurrentStart2)));
                XTabLayout xTabLayout6 = XTabLayout.this;
                xTabLayout6.mTempEnd2 = (int) (xTabLayout6.mCurrentEnd2 + (min * (XTabLayout.this.mToEnd2 - XTabLayout.this.mCurrentEnd2)));
            } else {
                XTabLayout xTabLayout7 = XTabLayout.this;
                xTabLayout7.mTempStart2 = (int) (xTabLayout7.mCurrentStart2 + (min * (XTabLayout.this.mToStart2 - XTabLayout.this.mCurrentStart2)));
                XTabLayout xTabLayout8 = XTabLayout.this;
                xTabLayout8.mTempEnd2 = (int) (xTabLayout8.mCurrentEnd2 + (Math.pow(floatValue, XTabLayout.K2) * (XTabLayout.this.mToEnd2 - XTabLayout.this.mCurrentEnd2)));
            }
            XTabLayout.this.invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class f extends AnimatorListenerAdapter {
        f() {
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            super.onAnimationEnd(animator);
            XTabLayout.this.assignPosition();
            XTabLayout.this.invalidate();
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator) {
            super.onAnimationStart(animator);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class g extends AnimatorListenerAdapter {

        /* renamed from: a  reason: collision with root package name */
        final /* synthetic */ i f108a;

        g(i iVar) {
            this.f108a = iVar;
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            super.onAnimationEnd(animator);
            XTabLayout.this.mValueAnimator.removeListener(this);
            i iVar = this.f108a;
            if (iVar != null) {
                iVar.b();
            }
        }

        @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator) {
            super.onAnimationStart(animator);
            i iVar = this.f108a;
            if (iVar != null) {
                iVar.a();
            }
        }
    }

    /* loaded from: classes.dex */
    class h implements Runnable {
        h() {
        }

        @Override // java.lang.Runnable
        public void run() {
            XTabLayout.this.moveIndicatorTo(false, null);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public interface i {
        void a();

        void b();
    }

    public XTabLayout(Context context) {
        this(context, null);
    }

    public XTabLayout(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public XTabLayout(Context context, AttributeSet attributeSet, int i2) {
        this(context, attributeSet, i2, 0);
    }

    public XTabLayout(Context context, AttributeSet attributeSet, int i2, int i3) {
        super(context, attributeSet, i2, i3 == 0 ? R.style.XTabLayoutAppearance : i3);
        XViewDelegate xViewDelegate;
        this.MARGIN_DAY = 0;
        this.mPaint = new Paint(1);
        this.mBlurPaint = new Paint(1);
        this.mPaint2 = new Paint(1);
        this.mBlurPaint2 = new Paint(1);
        this.mCurrentStart = 0;
        this.mCurrentEnd = 0;
        this.mCurrentStart2 = 0;
        this.mCurrentEnd2 = 0;
        this.mToStart = 0;
        this.mToStart2 = 0;
        this.mToEnd = 0;
        this.mToEnd2 = 0;
        this.mTempStart = 0;
        this.mTempStart2 = 0;
        this.mTempEnd = 0;
        this.mTempEnd2 = 0;
        this.mDivideValue = 0.6f;
        this.mSelectTabIndex = -1;
        this.mStyle = 2;
        this.mChildClickListener = new a();
        this.mIsDetachedFromWindow = true;
        if (Build.VERSION.SDK_INT <= 26) {
            setLayerType(1, null);
        }
        Resources.Theme theme = getContext().getTheme();
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.XTabLayout, i2 == 0 ? R.style.XTabLayoutAppearance : i2, R.style.XTabLayoutAppearance);
        this.mTitleString = obtainStyledAttributes.getTextArray(R.styleable.XTabLayout_titles);
        this.mTitleTextSize = obtainStyledAttributes.getDimension(R.styleable.XTabLayout_titleTextSize, 35.0f);
        final XFontScaleHelper create = XFontScaleHelper.create(obtainStyledAttributes, R.styleable.XTabLayout_titleTextSize);
        if (create != null && (xViewDelegate = this.mXViewDelegate) != null) {
            xViewDelegate.setFontScaleChangeCallback(new XViewDelegate.onFontScaleChangeCallback() { // from class: com.xiaopeng.xui.widget.-$$Lambda$XTabLayout$y87KNDq4w28PZ1_fwB_ObS-VnVo
                @Override // com.xiaopeng.xui.view.XViewDelegate.onFontScaleChangeCallback
                public final void onFontScaleChanged() {
                    XTabLayout.this.a(create);
                }
            });
        }
        this.mTitleTextColorStateList = obtainStyledAttributes.getColorStateList(R.styleable.XTabLayout_titleTextColorStateList);
        int integer = obtainStyledAttributes.getInteger(R.styleable.XTabLayout_selectTab, 0);
        this.mIndicatorWidth = obtainStyledAttributes.getDimension(R.styleable.XTabLayout_indicatorWidth, 0.0f);
        this.mIndicatorWidthPercent = obtainStyledAttributes.getFraction(R.styleable.XTabLayout_indicatorWidthPercent, 1, 1, 0.7f);
        this.mIndicatorMaxHeight = obtainStyledAttributes.getDimension(R.styleable.XTabLayout_indicatorMaxHeight, dp(4));
        this.mIndicatorMinHeight = obtainStyledAttributes.getDimension(R.styleable.XTabLayout_indicatorMinHeight, dp(2));
        this.mIndicatorHeight = this.mIndicatorMaxHeight;
        this.mIndicatorMarginBottom = obtainStyledAttributes.getDimension(R.styleable.XTabLayout_indicatorMarginBottom, dp(6));
        this.mIndicatorColor = obtainStyledAttributes.getColor(R.styleable.XTabLayout_indicatorColor, getResources().getColor(R.color.x_theme_primary_normal, theme));
        this.mIndicatorColorFrom = obtainStyledAttributes.getColor(R.styleable.XTabLayout_indicatorColorFrom, -1);
        this.mIndicatorColorTo = obtainStyledAttributes.getColor(R.styleable.XTabLayout_indicatorColorTo, -1);
        this.mIndicatorShadowColor = obtainStyledAttributes.getColor(R.styleable.XTabLayout_indicatorShadowColor, -15880455);
        this.mIndicatorShadowRadius = obtainStyledAttributes.getDimension(R.styleable.XTabLayout_indicatorShadowRadius, dp(4));
        this.mIndicatorColor2 = obtainStyledAttributes.getColor(R.styleable.XTabLayout_indicatorColor2, -11243894);
        this.mIndicatorShadowColor2 = obtainStyledAttributes.getColor(R.styleable.XTabLayout_indicatorShadowColor2, -15880455);
        this.mIndicatorShadowRadius2 = obtainStyledAttributes.getDimension(R.styleable.XTabLayout_indicatorShadowRadius2, dp(4));
        this.mIndicatorAnimatorEnable = obtainStyledAttributes.getBoolean(R.styleable.XTabLayout_indicatorAnimatorEnable, true);
        this.mTitleTextColorRes = obtainStyledAttributes.getResourceId(R.styleable.XTabLayout_titleTextColorStateList, -1);
        this.mTabsBarStyle = obtainStyledAttributes.getBoolean(R.styleable.XTabLayout_tabsBarStyle, false);
        this.mTabCustomBackground = obtainStyledAttributes.getBoolean(R.styleable.XTabLayout_tabCustomBackground, false);
        this.mPaddingNight = obtainStyledAttributes.getDimensionPixelSize(R.styleable.XTabLayout_tabPaddingNight, dp(40));
        this.mIndicatorDayNightDiff = obtainStyledAttributes.getBoolean(R.styleable.XTabLayout_indicatorDayNightDiff, false);
        int resourceId = obtainStyledAttributes.getResourceId(R.styleable.XTabLayout_tabLayoutIcons, 0);
        if (resourceId != 0) {
            TypedArray obtainTypedArray = getResources().obtainTypedArray(resourceId);
            this.mTitleIcons = new int[obtainTypedArray.length()];
            for (int i4 = 0; i4 < obtainTypedArray.length(); i4++) {
                this.mTitleIcons[i4] = obtainTypedArray.getResourceId(i4, 0);
            }
            obtainTypedArray.recycle();
        }
        this.mIconVuiLabels = obtainStyledAttributes.getTextArray(R.styleable.XTabLayout_tabLayoutVuiLabels);
        Log.d(TAG, "iconsId:" + resourceId + ",mTitleIcons:" + Arrays.toString(this.mTitleIcons) + ",mIconVuiLabels:" + Arrays.toString(this.mIconVuiLabels));
        setGravity(17);
        obtainStyledAttributes.recycle();
        setWillNotDraw(false);
        init();
        selectTab(integer, false, false);
        setStyle(XThemeManager.isNight(getContext()) ? 2 : 1);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void a(XFontScaleHelper xFontScaleHelper) {
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            View childAt = getChildAt(i2);
            if (childAt instanceof TextView) {
                xFontScaleHelper.refreshTextSize((TextView) childAt);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void a(Double d2, View view) {
        if (d2.intValue() < getChildCount()) {
            view = getChildAt(d2.intValue());
        }
        VuiFloatingLayerManager.show(view);
        boolean z = view instanceof b.a.a.a.a;
        if (z) {
            ((b.a.a.a.a) view).setPerformVuiAction(true);
        }
        selectTab(d2.intValue(), true, true);
        if (z) {
            ((b.a.a.a.a) view).setPerformVuiAction(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void assignPosition() {
        this.mCurrentStart = this.mToStart;
        this.mCurrentEnd = this.mToEnd;
        this.mCurrentStart2 = this.mToStart2;
        this.mCurrentEnd2 = this.mToEnd2;
        int i2 = this.mCurrentStart;
        this.mTempStart = i2;
        this.mTempEnd = this.mCurrentEnd;
        this.mTempStart2 = i2;
        this.mTempEnd2 = this.mCurrentEnd2;
    }

    private void drawDayIndicator(Canvas canvas) {
        float f2;
        float f3;
        float f4;
        if (!ThemeManager.isNightMode(getContext())) {
            this.mPaint.setMaskFilter(null);
        }
        this.mPaint.setAlpha(isEnabled(this.mSelectTabIndex) ? 255 : 92);
        float height = (getHeight() * 1.0f) / 2.0f;
        float height2 = getHeight() >> 1;
        int i2 = this.mTempStart;
        int i3 = this.mTempEnd;
        if (i2 < i3) {
            f2 = i2;
            f3 = height2 - height;
            f4 = i3;
        } else {
            f2 = i3;
            f3 = height2 - height;
            f4 = i2;
        }
        canvas.drawRoundRect(f2, f3, f4, height2 + height, height, height, this.mPaint);
    }

    private void drawNightIndicator(Canvas canvas) {
        BlurMaskFilter blurMaskFilter;
        Paint paint;
        float f2;
        float f3;
        int i2;
        if (ThemeManager.isNightMode(getContext())) {
            this.mPaint.setMaskFilter(new BlurMaskFilter(this.mIndicatorShadowRadius, BlurMaskFilter.Blur.SOLID));
            paint = this.mPaint2;
            blurMaskFilter = new BlurMaskFilter(this.mIndicatorShadowRadius2, BlurMaskFilter.Blur.SOLID);
        } else {
            blurMaskFilter = null;
            this.mPaint.setMaskFilter(null);
            paint = this.mPaint2;
        }
        paint.setMaskFilter(blurMaskFilter);
        this.mPaint.setAlpha(isEnabled(this.mSelectTabIndex) ? 255 : 92);
        this.mPaint2.setAlpha(isEnabled(this.mSelectTabIndex) ? 255 : 92);
        float f4 = this.mIndicatorHeight / 2.0f;
        float height = getHeight() - this.mIndicatorMarginBottom;
        if (this.mTempStart2 < this.mTempEnd2) {
            float max = Math.max(this.mIndicatorHeight, 1.0f);
            f2 = this.mTempStart2;
            f3 = height - max;
            i2 = this.mTempEnd2;
        } else {
            float max2 = Math.max(this.mIndicatorHeight, 1.0f);
            f2 = this.mTempEnd2;
            f3 = height - max2;
            i2 = this.mTempStart2;
        }
        canvas.drawRoundRect(f2, f3, i2, height, f4, f4, this.mPaint2);
    }

    private int getIndicatorOffset(int i2) {
        float f2;
        float f3 = this.mIndicatorWidth;
        if (f3 != 0.0f) {
            f2 = (i2 - f3) / 2.0f;
        } else {
            f2 = i2 * ((1.0f - this.mIndicatorWidthPercent) / 2.0f);
        }
        return (int) f2;
    }

    private void getIndicatorPosition() {
        int i2;
        int selectedTabIndex = getSelectedTabIndex();
        if (selectedTabIndex < 0) {
            i2 = 0;
            this.mToStart = 0;
            this.mToEnd = 0;
            this.mToStart2 = 0;
        } else {
            this.mToStart = getTabViewStart(selectedTabIndex);
            this.mToEnd = getTabViewEnd(selectedTabIndex);
            this.mToStart2 = this.mToStart;
            i2 = this.mToEnd;
        }
        this.mToEnd2 = i2;
    }

    private View getSelectView() {
        return getChildAt(this.mSelectTabIndex);
    }

    private int getTabViewEnd(int i2) {
        if (i2 < 0 || getWidth() <= 0) {
            return 0;
        }
        if (this.mStyle != 2) {
            return ((i2 + 1) * (getWidth() / getTabCount())) + 0;
        }
        int width = (getWidth() - (this.mPaddingNight * 2)) / getTabCount();
        return this.mPaddingNight + (((i2 + 1) * width) - getIndicatorOffset(width));
    }

    private int getTabViewStart(int i2) {
        if (i2 < 0 || getWidth() <= 0) {
            return 0;
        }
        if (this.mStyle == 2) {
            int width = (getWidth() - (this.mPaddingNight * 2)) / getTabCount();
            return this.mPaddingNight + (i2 * width) + getIndicatorOffset(width);
        }
        return 0 + (i2 * (getWidth() / getTabCount()));
    }

    private void init() {
        this.mPaint.setStrokeWidth(0.0f);
        this.mPaint.setColor(this.mIndicatorColor);
        this.mBlurPaint.setStrokeWidth(0.0f);
        this.mBlurPaint.setColor(this.mIndicatorShadowColor);
        if (Build.VERSION.SDK_INT <= 26) {
            setLayerType(1, this.mBlurPaint);
        }
        this.mPaint2.setStrokeWidth(0.0f);
        this.mPaint2.setColor(this.mIndicatorColor2);
        this.mBlurPaint2.setStrokeWidth(0.0f);
        this.mBlurPaint2.setColor(this.mIndicatorShadowColor2);
        if (Build.VERSION.SDK_INT <= 26) {
            setLayerType(1, this.mBlurPaint2);
        }
        CharSequence[] charSequenceArr = this.mTitleString;
        if (charSequenceArr == null || charSequenceArr.length <= 0) {
            int[] iArr = this.mTitleIcons;
            if (iArr != null && iArr.length > 0) {
                for (int i2 = 0; i2 < this.mTitleIcons.length; i2++) {
                    String str = BuildConfig.FLAVOR;
                    CharSequence[] charSequenceArr2 = this.mIconVuiLabels;
                    if (charSequenceArr2 != null && charSequenceArr2.length > i2) {
                        str = charSequenceArr2[i2].toString();
                    }
                    addTab(this.mTitleIcons[i2], str);
                }
            }
        } else {
            for (CharSequence charSequence : charSequenceArr) {
                addTab(charSequence);
            }
        }
        for (int i3 = 0; i3 < getChildCount(); i3++) {
            getChildAt(i3).setOnClickListener(this.mChildClickListener);
        }
        setOnHierarchyChangeListener(new b());
    }

    private void log(Object obj) {
        Log.d(TAG, "(" + hashCode() + ") " + obj);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void moveIndicatorTo(boolean z, i iVar) {
        if (getTabCount() <= 0) {
            return;
        }
        boolean z2 = z && this.mIndicatorAnimatorEnable;
        getIndicatorPosition();
        if (!z2) {
            assignPosition();
            if (iVar != null) {
                iVar.a();
                iVar.b();
            }
            invalidate();
            return;
        }
        if (this.mValueAnimator == null) {
            this.mValueAnimator = ValueAnimator.ofFloat(0.0f, 1.0f);
            this.mValueAnimator.setDuration(DURATION);
            this.mValueAnimator.addUpdateListener(new e());
            this.mValueAnimator.addListener(new f());
            this.mValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        }
        this.mValueAnimator.addListener(new g(iVar));
        this.mValueAnimator.start();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void selectTab(int i2, boolean z, boolean z2) {
        boolean z3;
        log("selectTab: index = [" + i2 + "], animator = [" + z + "], fromUser = [" + z2 + "],mSelectTabIndex:" + this.mSelectTabIndex);
        if (i2 >= getTabCount() || i2 < 0 || i2 == this.mSelectTabIndex) {
            return;
        }
        OnTabChangeListener onTabChangeListener = this.mOnTabChangeListener;
        if (onTabChangeListener == null || !onTabChangeListener.onInterceptTabChange(this, i2, true, z2)) {
            View childAt = getChildAt(i2);
            View selectView = getSelectView();
            if (childAt != selectView) {
                if (childAt != null) {
                    childAt.setSelected(true);
                }
                if (selectView != null) {
                    selectView.setSelected(false);
                }
                this.mSelectTabIndex = i2;
                z3 = true;
            } else {
                z3 = false;
            }
            moveIndicatorTo(z, new d(z3, childAt, z2, i2));
        }
    }

    public int addTab(int i2, int i3, String str) {
        XImageView xImageView = new XImageView(getContext());
        xImageView.setLayoutParams(new LinearLayout.LayoutParams(0, -1, 1.0f));
        xImageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        xImageView.setImageResource(i2);
        xImageView.setVuiElementType(b.a.a.a.g.TEXTVIEW);
        xImageView.setVuiLabel(str);
        xImageView.setTag(Boolean.valueOf(i3 == this.mSelectTabIndex));
        int i4 = this.mSelectTabIndex;
        if (i3 <= i4) {
            this.mSelectTabIndex = i4 + 1;
        }
        xImageView.setSoundEffectsEnabled(isSoundEffectsEnabled());
        addView(xImageView, i3);
        return i3;
    }

    public int addTab(int i2, String str) {
        return addTab(i2, getChildCount(), str);
    }

    public int addTab(CharSequence charSequence) {
        return addTab(charSequence, getChildCount());
    }

    public int addTab(CharSequence charSequence, int i2) {
        TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.x_tab_layout_title_view, (ViewGroup) this, false);
        textView.setText(charSequence);
        textView.setTextColor(this.mTitleTextColorStateList);
        textView.setTextSize(0, this.mTitleTextSize);
        textView.setTag(Boolean.valueOf(i2 == this.mSelectTabIndex));
        int i3 = this.mSelectTabIndex;
        if (i2 <= i3) {
            this.mSelectTabIndex = i3 + 1;
        }
        textView.setSoundEffectsEnabled(isSoundEffectsEnabled());
        addView(textView, i2);
        return i2;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        int i2 = this.mStyle;
        if (i2 == 1) {
            drawDayIndicator(canvas);
        } else if (i2 == 2) {
            drawNightIndicator(canvas);
        }
        super.dispatchDraw(canvas);
    }

    protected int dp(int i2) {
        return (int) TypedValue.applyDimension(1, i2, getResources().getDisplayMetrics());
    }

    protected float dpF(float f2) {
        return TypedValue.applyDimension(1, f2, getResources().getDisplayMetrics());
    }

    public int getSelectedTabIndex() {
        return this.mSelectTabIndex;
    }

    public int getTabCount() {
        return getChildCount();
    }

    public CharSequence getTabTitle(int i2) {
        return getChildAt(i2) instanceof TextView ? ((TextView) getChildAt(i2)).getText() : BuildConfig.FLAVOR;
    }

    public boolean isAllTabClickable() {
        int childCount = getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            if (!getChildAt(i2).isClickable()) {
                return false;
            }
        }
        return true;
    }

    public boolean isEnabled(int i2) {
        int childCount = getChildCount();
        View childAt = getChildAt(i2);
        if (i2 >= childCount || childAt == null) {
            return false;
        }
        return childAt.isEnabled();
    }

    public boolean isIndicatorAnimatorEnable() {
        return this.mIndicatorAnimatorEnable;
    }

    public boolean isTabClickable(int i2) {
        return getChildAt(i2).isClickable();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XLinearLayout, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        log("onAttachedToWindow:" + XThemeManager.isNight(getContext()));
        if (!this.mIsDetachedFromWindow || this.mIsDetachedNightTheme == XThemeManager.isNight(getContext())) {
            return;
        }
        this.mIsDetachedFromWindow = false;
        setStyle(XThemeManager.isNight(getContext()) ? 2 : 1);
    }

    public b.a.a.a.k.a onBuildVuiElement(String str, b.a.a.a.b bVar) {
        for (int i2 = 0; i2 < getChildCount(); i2++) {
            View childAt = getChildAt(i2);
            if (childAt instanceof VuiView) {
                VuiView vuiView = (VuiView) childAt;
                vuiView.setVuiPosition(i2);
                vuiView.setVuiElementId(str + "_" + i2);
                StringBuilder sb = new StringBuilder();
                sb.append("onBuildVuiElement:");
                sb.append(str);
                Log.d(TAG, sb.toString());
            }
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XLinearLayout, android.view.View
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        log("isThemeChanged:" + XThemeManager.isThemeChanged(configuration) + ",isNight:" + XThemeManager.isNight(getContext()));
        if (XThemeManager.isThemeChanged(configuration)) {
            setStyle(XThemeManager.isNight(getContext()) ? 2 : 1);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XLinearLayout, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mIsDetachedFromWindow = true;
        this.mIsDetachedNightTheme = XThemeManager.isNight(getContext());
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i2, int i3, int i4, int i5) {
        super.onLayout(z, i2, i3, i4, i5);
    }

    @Override // android.view.View
    protected void onSizeChanged(int i2, int i3, int i4, int i5) {
        super.onSizeChanged(i2, i3, i4, i5);
        post(new h());
    }

    @Override // b.a.a.a.c
    public boolean onVuiElementEvent(final View view, b.a.a.a.k.b bVar) {
        final Double d2;
        log("tablayout onVuiElementEvent");
        if (view == null || (d2 = (Double) bVar.a(bVar)) == null) {
            return false;
        }
        post(new Runnable() { // from class: com.xiaopeng.xui.widget.-$$Lambda$XTabLayout$TBjenhrtSNU5QxME-E7bxYOmcgE
            @Override // java.lang.Runnable
            public final void run() {
                XTabLayout.this.a(d2, view);
            }
        });
        return true;
    }

    public void removeTab(int i2) {
        View childAt;
        if (getChildAt(i2) == null) {
            throw new IllegalArgumentException("targetView is not exits. index = " + i2 + ", tabCount = " + getChildCount());
        }
        boolean z = this.mSelectTabIndex == i2;
        if (this.mSelectTabIndex < getTabCount() - 1) {
            getChildAt(this.mSelectTabIndex).setSelected(false);
            getChildAt(i2).setTag(Boolean.valueOf(z));
            removeViewAt(i2);
            childAt = getChildAt(this.mSelectTabIndex);
        } else {
            getChildAt(this.mSelectTabIndex).setSelected(false);
            this.mSelectTabIndex--;
            getChildAt(i2).setTag(Boolean.valueOf(z));
            removeViewAt(i2);
            childAt = getChildAt(this.mSelectTabIndex);
            if (childAt == null) {
                return;
            }
        }
        childAt.setSelected(true);
    }

    public void removeTab(int i2, int i3) {
        if (i2 >= getTabCount() || i3 >= getTabCount()) {
            return;
        }
        if (i2 == i3) {
            removeTab(i2);
            return;
        }
        boolean z = i3 == this.mSelectTabIndex;
        if (z) {
            getChildAt(this.mSelectTabIndex).setSelected(false);
            getChildAt(i3).setSelected(true);
        }
        this.mSelectTabIndex = i3;
        getChildAt(i2).setTag(Boolean.valueOf(z));
        removeViewAt(i2);
    }

    public void selectTab(int i2) {
        selectTab(i2, true);
    }

    public void selectTab(int i2, boolean z) {
        selectTab(i2, z, false);
    }

    public void selectedNoneTab(boolean z, boolean z2) {
        View selectView = getSelectView();
        if (selectView != null) {
            selectView.setSelected(false);
        }
        this.mSelectTabIndex = -1;
        moveIndicatorTo(z, new c(z2));
        invalidate();
    }

    public void setAllTabClickable(boolean z) {
        int childCount = getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            getChildAt(i2).setClickable(z);
        }
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        int childCount = getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            getChildAt(i2).setEnabled(z);
        }
        if (Build.VERSION.SDK_INT > 26) {
            int i3 = z ? 255 : 92;
            this.mPaint.setAlpha(i3);
            this.mBlurPaint.setAlpha(i3);
            this.mPaint2.setAlpha(i3);
            this.mBlurPaint2.setAlpha(i3);
            invalidate();
        }
    }

    public void setEnabled(boolean z, int i2) {
        int childCount = getChildCount();
        View childAt = getChildAt(i2);
        if (i2 >= childCount || childAt == null) {
            return;
        }
        childAt.setEnabled(z);
        invalidate();
    }

    public void setIndicatorAnimatorEnable(boolean z) {
        this.mIndicatorAnimatorEnable = z;
    }

    public void setOnTabChangeListener(OnTabChangeListener onTabChangeListener) {
        this.mOnTabChangeListener = onTabChangeListener;
    }

    @Override // android.view.View
    public void setSoundEffectsEnabled(boolean z) {
        super.setSoundEffectsEnabled(z);
        int childCount = getChildCount();
        for (int i2 = 0; i2 < childCount; i2++) {
            View childAt = getChildAt(i2);
            if (childAt != null) {
                childAt.setSoundEffectsEnabled(z);
            }
        }
    }

    public void setStyle(int i2) {
        int[] iArr;
        this.mIsDetachedNightTheme = XThemeManager.isNight(getContext());
        if (!this.mIndicatorDayNightDiff) {
            i2 = 1;
        }
        this.mStyle = i2;
        if (this.mTabsBarStyle) {
            this.mStyle = 2;
        }
        if (!this.mTabCustomBackground) {
            setBackground(getContext().getDrawable(XThemeManager.isNight(getContext()) ? R.drawable.x_tabs_layout_background : R.drawable.x_tabs_layout_background_day));
        }
        if (this.mStyle == 2) {
            int i3 = this.mPaddingNight;
            setPadding(i3, 0, i3, 0);
        } else {
            setPadding(0, 0, 0, 0);
        }
        if (this.mTitleTextColorRes > 0) {
            for (int i4 = 0; i4 < getChildCount(); i4++) {
                View childAt = getChildAt(i4);
                if (childAt instanceof TextView) {
                    ((TextView) childAt).setTextColor(getResources().getColorStateList(this.mTitleTextColorRes, getContext().getTheme()));
                } else if ((childAt instanceof ImageView) && (iArr = this.mTitleIcons) != null && iArr.length > i4) {
                    ((ImageView) childAt).setImageResource(iArr[i4]);
                }
            }
        }
        moveIndicatorTo(false, null);
    }

    public void setTabClickable(int i2, boolean z) {
        getChildAt(i2).setClickable(z);
    }

    public void updateTabTitle(int i2, int i3) {
        View childAt = getChildAt(i2);
        if (childAt instanceof TextView) {
            ((TextView) childAt).setText(i3);
        }
    }

    public void updateTabTitle(int i2, String str) {
        View childAt = getChildAt(i2);
        if (childAt instanceof TextView) {
            ((TextView) childAt).setText(str);
        }
    }
}
