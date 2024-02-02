package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.support.v7.preference.Preference;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.Scroller;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.sound.XSoundEffectManager;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;
import xiaopeng.widget.BuildConfig;
/* loaded from: classes.dex */
public class XNumberPicker extends XLinearLayout {
    private static final long DEFAULT_LONG_PRESS_UPDATE_INTERVAL = 300;
    private static final int DEFAULT_TEXT_SIZE = 20;
    private static final int LAYOUT_ROUNDING_OFFSET = 5;
    private static final int SELECTED_TEXT_LAYOUT_OFFSET = 0;
    private static final int SELECTOR_ADJUSTMENT_DURATION_MILLIS = 800;
    private static final int SELECTOR_MAX_FLING_VELOCITY_ADJUSTMENT = 8;
    private static final int SELECTOR_MIDDLE_ITEM_INDEX = 2;
    private static final int SELECTOR_WHEEL_ITEM_COUNT = 5;
    private static final int SIZE_UNSPECIFIED = -1;
    private static final int SNAP_SCROLL_DURATION = 300;
    private static final int SYMBOL_LAYOUT_OFFSET = 35;
    private static final int SYMBOL_WIDTH = 6;
    private static final int TEXT_LAYOUT_DEFAULT = 1;
    private static final int TEXT_LAYOUT_LEFT = 0;
    private static final int TEXT_LAYOUT_MIDDLE = 1;
    private static final int TEXT_LAYOUT_RIGHT = 2;
    private static final float TOP_AND_BOTTOM_FADING_EDGE_STRENGTH = 0.9f;
    private static final int UNSCALED_DEFAULT_SELECTION_DIVIDERS_DISTANCE = 48;
    private static final int UNSCALED_DEFAULT_SELECTION_DIVIDER_HEIGHT = 2;
    private final Scroller mAdjustScroller;
    private a mBeginSoftInputOnLongPressCommand;
    private int mBottomSelectionDividerBottom;
    private b mChangeCurrentByOneFromLongPressCommand;
    private final boolean mComputeMaxWidth;
    private int mCurrentScrollOffset;
    private String[] mDisplayedValues;
    private final Scroller mFlingScroller;
    private Formatter mFormatter;
    private boolean mHideWheelUntilFocused;
    private boolean mIgnoreMoveEvents;
    private int mInitialScrollOffset;
    private long mLastDownEventTime;
    private float mLastDownEventY;
    private float mLastDownOrMoveEventY;
    private int mLastHandledDownDpadKeyCode;
    private int mLastHoveredChildVirtualViewId;
    private long mLongPressUpdateInterval;
    private final int mMaxHeight;
    private int mMaxValue;
    private int mMaxWidth;
    private int mMaximumFlingVelocity;
    private final int mMinHeight;
    private int mMinValue;
    private final int mMinWidth;
    private int mMinimumFlingVelocity;
    private OnScrollListener mOnScrollListener;
    private OnValueChangeListener mOnValueChangeListener;
    private boolean mPerformClickOnTap;
    private final c mPressedStateHelper;
    private int mPreviousScrollerY;
    private int mScrollState;
    private int mSelectedSelectorElementHeight;
    private final int mSelectedTextSize;
    private final Drawable mSelectionDivider;
    private final int mSelectionDividerHeight;
    private final int mSelectionDividersDistance;
    private int mSelectorElementHeight;
    private final SparseArray<String> mSelectorIndexToStringCache;
    private final int[] mSelectorIndices;
    private int mSelectorTextGapHeight;
    private final Paint mSelectorWheelPaint;
    private final int mSolidColor;
    private final Drawable mSymbol;
    private ColorStateList mTextColors;
    private int mTextLayout;
    private int mTextLayoutMargin;
    private final int mTextSize;
    private int mTopSelectionDividerTop;
    private int mTouchSlop;
    private int mValue;
    private VelocityTracker mVelocityTracker;
    private int mWidth;
    private boolean mWrapSelectorWheel;
    private boolean mWrapSelectorWheelPreferred;
    private static final d sTwoDigitFormatter = new d();
    private static final char[] DIGIT_CHARACTERS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 1632, 1633, 1634, 1635, 1636, 1637, 1638, 1639, 1640, 1641, 1776, 1777, 1778, 1779, 1780, 1781, 1782, 1783, 1784, 1785, 2406, 2407, 2408, 2409, 2410, 2411, 2412, 2413, 2414, 2415, 2534, 2535, 2536, 2537, 2538, 2539, 2540, 2541, 2542, 2543, 3302, 3303, 3304, 3305, 3306, 3307, 3308, 3309, 3310, 3311};

    /* loaded from: classes.dex */
    public static class CustomEditText extends AppCompatEditText {
        public CustomEditText(Context context, AttributeSet attributeSet) {
            super(context, attributeSet);
        }

        @Override // android.widget.TextView
        public void onEditorAction(int i) {
            super.onEditorAction(i);
            if (i == 6) {
                clearFocus();
            }
        }
    }

    /* loaded from: classes.dex */
    public interface Formatter {
        String format(int i);
    }

    /* loaded from: classes.dex */
    public interface OnScrollListener {
        public static final int SCROLL_STATE_FLING = 2;
        public static final int SCROLL_STATE_IDLE = 0;
        public static final int SCROLL_STATE_TOUCH_SCROLL = 1;

        @Retention(RetentionPolicy.SOURCE)
        /* loaded from: classes.dex */
        public @interface ScrollState {
        }

        void onScrollStateChange(XNumberPicker xNumberPicker, int i);
    }

    /* loaded from: classes.dex */
    public interface OnValueChangeListener {
        void onValueChange(XNumberPicker xNumberPicker, int i, int i2);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class a implements Runnable {
        a() {
        }

        @Override // java.lang.Runnable
        public void run() {
            XNumberPicker.this.performLongClick();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class b implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        private boolean f90a;

        b() {
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void a(boolean z) {
            this.f90a = z;
        }

        @Override // java.lang.Runnable
        public void run() {
            XNumberPicker.this.changeValueByOne(this.f90a);
            XNumberPicker xNumberPicker = XNumberPicker.this;
            xNumberPicker.postDelayed(this, xNumberPicker.mLongPressUpdateInterval);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class c implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        private int f92a;

        /* renamed from: b  reason: collision with root package name */
        private int f93b;

        c() {
        }

        public void a() {
            this.f93b = 0;
            this.f92a = 0;
            XNumberPicker.this.removeCallbacks(this);
        }

        public void a(int i) {
            a();
            this.f93b = 1;
            this.f92a = i;
            XNumberPicker.this.postDelayed(this, ViewConfiguration.getTapTimeout());
        }

        public void b(int i) {
            a();
            this.f93b = 2;
            this.f92a = i;
            XNumberPicker.this.post(this);
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.f93b != 1) {
                return;
            }
            int i = this.f92a;
            if (i == 1) {
                XNumberPicker xNumberPicker = XNumberPicker.this;
                xNumberPicker.invalidate(0, xNumberPicker.mBottomSelectionDividerBottom, XNumberPicker.this.getRight(), XNumberPicker.this.getBottom());
            } else if (i != 2) {
            } else {
                XNumberPicker xNumberPicker2 = XNumberPicker.this;
                xNumberPicker2.invalidate(0, 0, xNumberPicker2.getRight(), XNumberPicker.this.mTopSelectionDividerTop);
            }
        }
    }

    /* loaded from: classes.dex */
    private static class d implements Formatter {

        /* renamed from: b  reason: collision with root package name */
        char f95b;
        java.util.Formatter c;

        /* renamed from: a  reason: collision with root package name */
        final StringBuilder f94a = new StringBuilder();
        final Object[] d = new Object[1];

        d() {
            c(Locale.getDefault());
        }

        private java.util.Formatter a(Locale locale) {
            return new java.util.Formatter(this.f94a, locale);
        }

        private static char b(Locale locale) {
            return '0';
        }

        private void c(Locale locale) {
            this.c = a(locale);
            this.f95b = b(locale);
        }

        @Override // com.xiaopeng.xui.widget.XNumberPicker.Formatter
        public String format(int i) {
            Locale locale = Locale.getDefault();
            if (this.f95b != b(locale)) {
                c(locale);
            }
            this.d[0] = Integer.valueOf(i);
            StringBuilder sb = this.f94a;
            sb.delete(0, sb.length());
            this.c.format("%02d", this.d);
            return this.c.toString();
        }
    }

    public XNumberPicker(Context context) {
        this(context, null);
    }

    public XNumberPicker(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.style.XNumberPicker);
    }

    public XNumberPicker(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, R.style.XNumberPicker);
    }

    /* JADX WARN: Removed duplicated region for block: B:42:0x01a5  */
    /* JADX WARN: Removed duplicated region for block: B:45:0x01b0  */
    /* JADX WARN: Removed duplicated region for block: B:47:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public XNumberPicker(android.content.Context r5, android.util.AttributeSet r6, int r7, int r8) {
        /*
            Method dump skipped, instructions count: 439
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.xui.widget.XNumberPicker.<init>(android.content.Context, android.util.AttributeSet, int, int):void");
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void changeValueByOne(boolean z) {
        if (!moveToFinalScrollerPosition(this.mFlingScroller)) {
            moveToFinalScrollerPosition(this.mAdjustScroller);
        }
        this.mPreviousScrollerY = 0;
        if (z) {
            this.mFlingScroller.startScroll(0, 0, 0, -this.mSelectorElementHeight, SNAP_SCROLL_DURATION);
        } else {
            this.mFlingScroller.startScroll(0, 0, 0, this.mSelectorElementHeight, SNAP_SCROLL_DURATION);
        }
        invalidate();
    }

    private void decrementSelectorIndices(int[] iArr) {
        if (iArr.length - 1 >= 0) {
            System.arraycopy(iArr, 0, iArr, 1, iArr.length - 1);
        }
        int i = iArr[1] - 1;
        if (this.mWrapSelectorWheel && i < this.mMinValue) {
            i = this.mMaxValue;
        }
        iArr[0] = i;
        ensureCachedScrollSelectorValue(i);
    }

    private void ensureCachedScrollSelectorValue(int i) {
        String str;
        SparseArray<String> sparseArray = this.mSelectorIndexToStringCache;
        if (sparseArray.get(i) != null) {
            return;
        }
        int i2 = this.mMinValue;
        if (i < i2 || i > this.mMaxValue) {
            str = BuildConfig.FLAVOR;
        } else {
            String[] strArr = this.mDisplayedValues;
            str = strArr != null ? strArr[i - i2] : formatNumber(i);
        }
        sparseArray.put(i, str);
    }

    private boolean ensureScrollWheelAdjusted() {
        int i = this.mInitialScrollOffset - this.mCurrentScrollOffset;
        if (i != 0) {
            this.mPreviousScrollerY = 0;
            int abs = Math.abs(i);
            int i2 = this.mSelectorElementHeight;
            if (abs > i2 / 2) {
                if (i > 0) {
                    i2 = -i2;
                }
                i += i2;
            }
            this.mAdjustScroller.startScroll(0, 0, 0, i, SELECTOR_ADJUSTMENT_DURATION_MILLIS);
            invalidate();
            return true;
        }
        return false;
    }

    private void fling(int i) {
        Scroller scroller;
        int i2;
        int i3;
        this.mPreviousScrollerY = 0;
        if (i > 0) {
            scroller = this.mFlingScroller;
            i2 = 0;
            i3 = 0;
        } else {
            scroller = this.mFlingScroller;
            i2 = 0;
            i3 = Preference.DEFAULT_ORDER;
        }
        scroller.fling(i2, i3, 0, i, 0, 0, 0, Preference.DEFAULT_ORDER);
        invalidate();
    }

    private String formatNumber(int i) {
        Formatter formatter = this.mFormatter;
        return formatter != null ? formatter.format(i) : formatNumberWithLocale(i);
    }

    private static String formatNumberWithLocale(int i) {
        return String.format(Locale.getDefault(), "%d", Integer.valueOf(i));
    }

    private int getSelectedPos(String str) {
        try {
            if (this.mDisplayedValues == null) {
                return Integer.parseInt(str);
            }
            for (int i = 0; i < this.mDisplayedValues.length; i++) {
                str = str.toLowerCase();
                if (this.mDisplayedValues[i].toLowerCase().startsWith(str)) {
                    return this.mMinValue + i;
                }
            }
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return this.mMinValue;
        }
    }

    public static Formatter getTwoDigitFormatter() {
        return sTwoDigitFormatter;
    }

    private int getWrappedSelectorIndex(int i) {
        int i2 = this.mMaxValue;
        if (i > i2) {
            int i3 = this.mMinValue;
            return (i3 + ((i - i2) % (i2 - i3))) - 1;
        }
        int i4 = this.mMinValue;
        return i < i4 ? (i2 - ((i4 - i) % (i2 - i4))) + 1 : i;
    }

    private void incrementSelectorIndices(int[] iArr) {
        if (iArr.length - 1 >= 0) {
            System.arraycopy(iArr, 1, iArr, 0, iArr.length - 1);
        }
        int i = iArr[iArr.length - 2] + 1;
        if (this.mWrapSelectorWheel && i > this.mMaxValue) {
            i = this.mMinValue;
        }
        iArr[iArr.length - 1] = i;
        ensureCachedScrollSelectorValue(i);
    }

    private void initializeFadingEdges() {
        setVerticalFadingEdgeEnabled(true);
        setFadingEdgeLength(((getBottom() - getTop()) - this.mTextSize) / 2);
    }

    private void initializeSelectorWheel() {
        initializeSelectorWheelIndices();
        int[] iArr = this.mSelectorIndices;
        this.mSelectorTextGapHeight = (int) ((((getBottom() - getTop()) - (((iArr.length - 1) * this.mTextSize) + this.mSelectedTextSize)) / iArr.length) + 0.5f);
        int i = this.mTextSize;
        int i2 = this.mSelectorTextGapHeight;
        this.mSelectorElementHeight = i + i2;
        this.mSelectedSelectorElementHeight = this.mSelectedTextSize + i2;
        this.mInitialScrollOffset = i2 + (i / 2);
        this.mCurrentScrollOffset = this.mInitialScrollOffset;
    }

    private void initializeSelectorWheelIndices() {
        this.mSelectorIndexToStringCache.clear();
        int[] iArr = this.mSelectorIndices;
        int value = getValue();
        for (int i = 0; i < this.mSelectorIndices.length; i++) {
            int i2 = (i - 2) + value;
            if (this.mWrapSelectorWheel) {
                i2 = getWrappedSelectorIndex(i2);
            }
            iArr[i] = i2;
            ensureCachedScrollSelectorValue(iArr[i]);
        }
    }

    private int makeMeasureSpec(int i, int i2) {
        if (i2 == -1) {
            return i;
        }
        int size = View.MeasureSpec.getSize(i);
        int mode = View.MeasureSpec.getMode(i);
        if (mode != Integer.MIN_VALUE) {
            if (mode != 0) {
                if (mode == 1073741824) {
                    return i;
                }
                throw new IllegalArgumentException("Unknown measure mode: " + mode);
            }
            return View.MeasureSpec.makeMeasureSpec(i2, 1073741824);
        }
        return View.MeasureSpec.makeMeasureSpec(Math.min(size, i2), 1073741824);
    }

    private boolean moveToFinalScrollerPosition(Scroller scroller) {
        scroller.forceFinished(true);
        int finalY = scroller.getFinalY() - scroller.getCurrY();
        int i = this.mInitialScrollOffset - ((this.mCurrentScrollOffset + finalY) % this.mSelectorElementHeight);
        if (i != 0) {
            int abs = Math.abs(i);
            int i2 = this.mSelectorElementHeight;
            if (abs > i2 / 2) {
                i = i > 0 ? i - i2 : i + i2;
            }
            scrollBy(0, finalY + i);
            return true;
        }
        return false;
    }

    private void notifyChange(int i, int i2) {
        OnValueChangeListener onValueChangeListener = this.mOnValueChangeListener;
        if (onValueChangeListener != null) {
            onValueChangeListener.onValueChange(this, i, this.mValue);
        }
    }

    private void onScrollStateChange(int i) {
        if (this.mScrollState == i) {
            return;
        }
        this.mScrollState = i;
        OnScrollListener onScrollListener = this.mOnScrollListener;
        if (onScrollListener != null) {
            onScrollListener.onScrollStateChange(this, i);
        }
    }

    private void onScrollerFinished(Scroller scroller) {
        if (scroller == this.mFlingScroller) {
            ensureScrollWheelAdjusted();
            onScrollStateChange(0);
        }
    }

    private void postBeginSoftInputOnLongPressCommand() {
        a aVar = this.mBeginSoftInputOnLongPressCommand;
        if (aVar == null) {
            this.mBeginSoftInputOnLongPressCommand = new a();
        } else {
            removeCallbacks(aVar);
        }
        postDelayed(this.mBeginSoftInputOnLongPressCommand, ViewConfiguration.getLongPressTimeout());
    }

    private void postChangeCurrentByOneFromLongPress(boolean z, long j) {
        b bVar = this.mChangeCurrentByOneFromLongPressCommand;
        if (bVar == null) {
            this.mChangeCurrentByOneFromLongPressCommand = new b();
        } else {
            removeCallbacks(bVar);
        }
        this.mChangeCurrentByOneFromLongPressCommand.a(z);
        postDelayed(this.mChangeCurrentByOneFromLongPressCommand, j);
    }

    private void removeAllCallbacks() {
        b bVar = this.mChangeCurrentByOneFromLongPressCommand;
        if (bVar != null) {
            removeCallbacks(bVar);
        }
        a aVar = this.mBeginSoftInputOnLongPressCommand;
        if (aVar != null) {
            removeCallbacks(aVar);
        }
        this.mPressedStateHelper.a();
    }

    private void removeBeginSoftInputCommand() {
        a aVar = this.mBeginSoftInputOnLongPressCommand;
        if (aVar != null) {
            removeCallbacks(aVar);
        }
    }

    private void removeChangeCurrentByOneFromLongPress() {
        b bVar = this.mChangeCurrentByOneFromLongPressCommand;
        if (bVar != null) {
            removeCallbacks(bVar);
        }
    }

    private int resolveSizeAndStateRespectingMinSize(int i, int i2, int i3) {
        return i != -1 ? LinearLayout.resolveSizeAndState(Math.max(i, i2), i3, 0) : i2;
    }

    private void setValueInternal(int i, boolean z) {
        if (this.mValue == i) {
            return;
        }
        int wrappedSelectorIndex = this.mWrapSelectorWheel ? getWrappedSelectorIndex(i) : Math.min(Math.max(i, this.mMinValue), this.mMaxValue);
        int i2 = this.mValue;
        this.mValue = wrappedSelectorIndex;
        if (z) {
            XSoundEffectManager.get().play(2);
            notifyChange(i2, wrappedSelectorIndex);
        }
        initializeSelectorWheelIndices();
        invalidate();
    }

    private void tryComputeMaxWidth() {
        int i;
        if (this.mComputeMaxWidth) {
            String[] strArr = this.mDisplayedValues;
            int i2 = 0;
            if (strArr == null) {
                float f = 0.0f;
                for (int i3 = 0; i3 <= 9; i3++) {
                    float measureText = this.mSelectorWheelPaint.measureText(formatNumberWithLocale(i3));
                    if (measureText > f) {
                        f = measureText;
                    }
                }
                for (int i4 = this.mMaxValue; i4 > 0; i4 /= 10) {
                    i2++;
                }
                i = (int) (i2 * f);
            } else {
                int length = strArr.length;
                int length2 = strArr.length;
                int i5 = 0;
                while (i2 < length2) {
                    float measureText2 = this.mSelectorWheelPaint.measureText(strArr[i2]);
                    if (measureText2 > i5) {
                        i5 = (int) measureText2;
                    }
                    i2++;
                }
                i = i5;
            }
            if (this.mMaxWidth != i) {
                int i6 = this.mMinWidth;
                if (i > i6) {
                    this.mMaxWidth = i;
                } else {
                    this.mMaxWidth = i6;
                }
                invalidate();
            }
        }
    }

    private void updateWrapSelectorWheel() {
        boolean z = true;
        if (!((this.mMaxValue - this.mMinValue) + 1 >= this.mSelectorIndices.length) || !this.mWrapSelectorWheelPreferred) {
            z = false;
        }
        this.mWrapSelectorWheel = z;
    }

    @Override // android.view.View
    public void computeScroll() {
        Scroller scroller = this.mFlingScroller;
        if (scroller.isFinished()) {
            scroller = this.mAdjustScroller;
            if (scroller.isFinished()) {
                return;
            }
        }
        scroller.computeScrollOffset();
        int currY = scroller.getCurrY();
        if (this.mPreviousScrollerY == 0) {
            this.mPreviousScrollerY = scroller.getStartY();
        }
        scrollBy(0, currY - this.mPreviousScrollerY);
        this.mPreviousScrollerY = currY;
        if (scroller.isFinished()) {
            onScrollerFinished(scroller);
        } else {
            invalidate();
        }
    }

    @Override // android.view.View
    protected int computeVerticalScrollExtent() {
        return getHeight();
    }

    @Override // android.view.View
    protected int computeVerticalScrollOffset() {
        return this.mCurrentScrollOffset;
    }

    @Override // android.view.View
    protected int computeVerticalScrollRange() {
        return ((this.mMaxValue - this.mMinValue) + 1) * this.mSelectorElementHeight;
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent keyEvent) {
        int keyCode = keyEvent.getKeyCode();
        if (keyCode == 19 || keyCode == 20) {
            int action = keyEvent.getAction();
            if (action == 0) {
                if (!this.mWrapSelectorWheel) {
                    if (keyCode == 20) {
                    }
                }
                requestFocus();
                this.mLastHandledDownDpadKeyCode = keyCode;
                removeAllCallbacks();
                if (this.mFlingScroller.isFinished()) {
                    changeValueByOne(keyCode == 20);
                }
                return true;
            } else if (action == 1 && this.mLastHandledDownDpadKeyCode == keyCode) {
                this.mLastHandledDownDpadKeyCode = -1;
                return true;
            }
        } else if (keyCode == 23 || keyCode == 66) {
            removeAllCallbacks();
        }
        return super.dispatchKeyEvent(keyEvent);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 1 || actionMasked == 3) {
            removeAllCallbacks();
        }
        return super.dispatchTouchEvent(motionEvent);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTrackballEvent(MotionEvent motionEvent) {
        int actionMasked = motionEvent.getActionMasked();
        if (actionMasked == 1 || actionMasked == 3) {
            removeAllCallbacks();
        }
        return super.dispatchTrackballEvent(motionEvent);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        Drawable drawable = this.mSelectionDivider;
        if (drawable != null && drawable.isStateful() && drawable.setState(getDrawableState())) {
            invalidateDrawable(drawable);
        }
    }

    @Override // android.view.View
    protected float getBottomFadingEdgeStrength() {
        return TOP_AND_BOTTOM_FADING_EDGE_STRENGTH;
    }

    public CharSequence getDisplayedValueForCurrentSelection() {
        return this.mSelectorIndexToStringCache.get(getValue());
    }

    public String[] getDisplayedValues() {
        return this.mDisplayedValues;
    }

    public int getMaxValue() {
        return this.mMaxValue;
    }

    public int getMinValue() {
        return this.mMinValue;
    }

    @Override // android.view.View
    public int getSolidColor() {
        return this.mSolidColor;
    }

    @Override // android.view.View
    protected float getTopFadingEdgeStrength() {
        return TOP_AND_BOTTOM_FADING_EDGE_STRENGTH;
    }

    public int getValue() {
        return this.mValue;
    }

    public boolean getWrapSelectorWheel() {
        return this.mWrapSelectorWheel;
    }

    @Override // android.view.ViewGroup, android.view.View
    public void jumpDrawablesToCurrentState() {
        super.jumpDrawablesToCurrentState();
        Drawable drawable = this.mSelectionDivider;
        if (drawable != null) {
            drawable.jumpToCurrentState();
        }
    }

    @Override // com.xiaopeng.xui.widget.XLinearLayout, android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (ThemeManager.isThemeChanged(configuration)) {
            this.mTextColors = getContext().getResources().getColorStateList(R.color.x_number_picker_text_color, null);
            postInvalidate();
        }
    }

    @Override // com.xiaopeng.xui.widget.XLinearLayout, android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeAllCallbacks();
    }

    /* JADX WARN: Code restructure failed: missing block: B:17:0x0044, code lost:
        if (r8 != 2) goto L18;
     */
    /* JADX WARN: Code restructure failed: missing block: B:21:0x005e, code lost:
        if (r8 != 2) goto L18;
     */
    /* JADX WARN: Code restructure failed: missing block: B:22:0x0060, code lost:
        r7 = (getRight() - getLeft()) / 2.0f;
     */
    /* JADX WARN: Code restructure failed: missing block: B:23:0x006e, code lost:
        r7 = (getRight() - getLeft()) - r11.mTextLayoutMargin;
     */
    @Override // android.widget.LinearLayout, android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void onDraw(android.graphics.Canvas r12) {
        /*
            Method dump skipped, instructions count: 256
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.xui.widget.XNumberPicker.onDraw(android.graphics.Canvas):void");
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
        if (isEnabled() && motionEvent.getActionMasked() == 0) {
            removeAllCallbacks();
            float y = motionEvent.getY();
            this.mLastDownEventY = y;
            this.mLastDownOrMoveEventY = y;
            this.mLastDownEventTime = motionEvent.getEventTime();
            this.mIgnoreMoveEvents = false;
            this.mPerformClickOnTap = false;
            float f = this.mLastDownEventY;
            if (f < this.mTopSelectionDividerTop) {
                if (this.mScrollState == 0) {
                    this.mPressedStateHelper.a(2);
                }
            } else if (f > this.mBottomSelectionDividerBottom && this.mScrollState == 0) {
                this.mPressedStateHelper.a(1);
            }
            getParent().requestDisallowInterceptTouchEvent(true);
            if (!this.mFlingScroller.isFinished()) {
                this.mFlingScroller.forceFinished(true);
                this.mAdjustScroller.forceFinished(true);
                onScrollStateChange(0);
                return true;
            } else if (!this.mAdjustScroller.isFinished()) {
                this.mFlingScroller.forceFinished(true);
                this.mAdjustScroller.forceFinished(true);
                return true;
            } else {
                float f2 = this.mLastDownEventY;
                if (f2 < this.mTopSelectionDividerTop) {
                    postChangeCurrentByOneFromLongPress(false, ViewConfiguration.getLongPressTimeout());
                    return true;
                } else if (f2 > this.mBottomSelectionDividerBottom) {
                    postChangeCurrentByOneFromLongPress(true, ViewConfiguration.getLongPressTimeout());
                    return true;
                } else {
                    this.mPerformClickOnTap = true;
                    postBeginSoftInputOnLongPressCommand();
                    return true;
                }
            }
        }
        return false;
    }

    @Override // android.widget.LinearLayout, android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        if (z) {
            initializeSelectorWheel();
            initializeFadingEdges();
            int height = getHeight();
            int i5 = this.mSelectionDividersDistance;
            int i6 = this.mSelectionDividerHeight;
            this.mTopSelectionDividerTop = (((height - i5) / 2) - i6) + 5;
            this.mBottomSelectionDividerBottom = this.mTopSelectionDividerTop + (i6 * 2) + i5;
        }
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(makeMeasureSpec(i, this.mMaxWidth), makeMeasureSpec(i2, this.mMaxHeight));
        int resolveSizeAndStateRespectingMinSize = resolveSizeAndStateRespectingMinSize(this.mMinWidth, getMeasuredWidth(), i);
        this.mWidth = resolveSizeAndStateRespectingMinSize;
        setMeasuredDimension(resolveSizeAndStateRespectingMinSize, resolveSizeAndStateRespectingMinSize(this.mMinHeight, getMeasuredHeight(), i2));
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (isEnabled()) {
            if (this.mVelocityTracker == null) {
                this.mVelocityTracker = VelocityTracker.obtain();
            }
            this.mVelocityTracker.addMovement(motionEvent);
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked != 1) {
                if (actionMasked == 2 && !this.mIgnoreMoveEvents) {
                    float y = motionEvent.getY();
                    if (this.mScrollState == 1) {
                        scrollBy(0, (int) (y - this.mLastDownOrMoveEventY));
                        invalidate();
                    } else if (((int) Math.abs(y - this.mLastDownEventY)) > this.mTouchSlop) {
                        removeAllCallbacks();
                        onScrollStateChange(1);
                    }
                    this.mLastDownOrMoveEventY = y;
                    return true;
                }
                return true;
            }
            removeBeginSoftInputCommand();
            removeChangeCurrentByOneFromLongPress();
            this.mPressedStateHelper.a();
            VelocityTracker velocityTracker = this.mVelocityTracker;
            velocityTracker.computeCurrentVelocity(1000, this.mMaximumFlingVelocity);
            int yVelocity = (int) velocityTracker.getYVelocity();
            if (Math.abs(yVelocity) > this.mMinimumFlingVelocity) {
                fling(yVelocity);
                onScrollStateChange(2);
            } else {
                int y2 = (int) motionEvent.getY();
                int abs = (int) Math.abs(y2 - this.mLastDownEventY);
                long eventTime = motionEvent.getEventTime() - this.mLastDownEventTime;
                if (abs > this.mTouchSlop || eventTime >= ViewConfiguration.getTapTimeout()) {
                    ensureScrollWheelAdjusted();
                } else if (this.mPerformClickOnTap) {
                    this.mPerformClickOnTap = false;
                    performClick();
                } else {
                    int i = (y2 / this.mSelectorElementHeight) - 2;
                    if (i > 0) {
                        changeValueByOne(true);
                        this.mPressedStateHelper.b(1);
                    } else if (i < 0) {
                        changeValueByOne(false);
                        this.mPressedStateHelper.b(2);
                    }
                }
                onScrollStateChange(0);
            }
            this.mVelocityTracker.recycle();
            this.mVelocityTracker = null;
            return true;
        }
        return false;
    }

    @Override // android.view.View
    public boolean performClick() {
        return super.performClick();
    }

    @Override // android.view.View
    public boolean performLongClick() {
        return super.performLongClick();
    }

    @Override // android.view.View
    public void scrollBy(int i, int i2) {
        int i3;
        int[] iArr = this.mSelectorIndices;
        int i4 = this.mCurrentScrollOffset;
        if ((!this.mWrapSelectorWheel && i2 > 0 && iArr[2] <= this.mMinValue) || (!this.mWrapSelectorWheel && i2 < 0 && iArr[2] >= this.mMaxValue)) {
            this.mCurrentScrollOffset = this.mInitialScrollOffset;
            return;
        }
        this.mCurrentScrollOffset += i2;
        while (true) {
            int i5 = this.mCurrentScrollOffset;
            int i6 = i5 - this.mInitialScrollOffset;
            int i7 = this.mSelectorTextGapHeight;
            if (i6 <= i7) {
                break;
            }
            this.mCurrentScrollOffset = i5 - ((i7 * 2) + 1);
            decrementSelectorIndices(iArr);
            setValueInternal(iArr[2], true);
            if (!this.mWrapSelectorWheel && iArr[2] <= this.mMinValue) {
                this.mCurrentScrollOffset = this.mInitialScrollOffset;
            }
        }
        while (true) {
            i3 = this.mCurrentScrollOffset;
            int i8 = i3 - this.mInitialScrollOffset;
            int i9 = this.mSelectorTextGapHeight;
            if (i8 >= (-i9)) {
                break;
            }
            this.mCurrentScrollOffset = i3 + (i9 * 2) + 1;
            incrementSelectorIndices(iArr);
            setValueInternal(iArr[2], true);
            if (!this.mWrapSelectorWheel && iArr[2] >= this.mMaxValue) {
                this.mCurrentScrollOffset = this.mInitialScrollOffset;
            }
        }
        if (i4 != i3) {
            onScrollChanged(0, i3, 0, i4);
        }
    }

    public void setDisplayedValues(String[] strArr) {
        if (this.mDisplayedValues == strArr) {
            return;
        }
        this.mDisplayedValues = strArr;
        initializeSelectorWheelIndices();
        tryComputeMaxWidth();
    }

    public void setFormatter(Formatter formatter) {
        if (formatter == this.mFormatter) {
            return;
        }
        this.mFormatter = formatter;
        initializeSelectorWheelIndices();
    }

    public void setMaxValue(int i) {
        if (this.mMaxValue == i) {
            return;
        }
        if (i < 0) {
            throw new IllegalArgumentException("maxValue must be >= 0");
        }
        this.mMaxValue = i;
        int i2 = this.mMaxValue;
        if (i2 < this.mValue) {
            this.mValue = i2;
        }
        updateWrapSelectorWheel();
        invalidate();
    }

    public void setMinValue(int i) {
        if (this.mMinValue == i) {
            return;
        }
        if (i < 0) {
            throw new IllegalArgumentException("minValue must be >= 0");
        }
        this.mMinValue = i;
        int i2 = this.mMinValue;
        if (i2 > this.mValue) {
            this.mValue = i2;
        }
        updateWrapSelectorWheel();
        invalidate();
    }

    public void setOnLongPressUpdateInterval(long j) {
        this.mLongPressUpdateInterval = j;
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.mOnScrollListener = onScrollListener;
    }

    public void setOnValueChangedListener(OnValueChangeListener onValueChangeListener) {
        this.mOnValueChangeListener = onValueChangeListener;
    }

    public void setValue(int i) {
        setValueInternal(i, false);
    }

    public void setWrapSelectorWheel(boolean z) {
        this.mWrapSelectorWheelPreferred = z;
        updateWrapSelectorWheel();
    }
}
