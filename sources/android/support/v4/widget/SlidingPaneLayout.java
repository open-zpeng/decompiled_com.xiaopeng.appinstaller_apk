package android.support.v4.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.view.AbsSavedState;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class SlidingPaneLayout extends ViewGroup {
    private boolean mCanSlide;
    private int mCoveredFadeColor;
    private boolean mDisplayListReflectionLoaded;
    final ViewDragHelper mDragHelper;
    private boolean mFirstLayout;
    private Method mGetDisplayList;
    private float mInitialMotionX;
    private float mInitialMotionY;
    boolean mIsUnableToDrag;
    private final int mOverhangSize;
    private int mParallaxBy;
    private float mParallaxOffset;
    final ArrayList<DisableLayerRunnable> mPostedRunnables;
    boolean mPreservedOpenState;
    private Field mRecreateDisplayList;
    private Drawable mShadowDrawableLeft;
    private Drawable mShadowDrawableRight;
    float mSlideOffset;
    int mSlideRange;
    View mSlideableView;
    private int mSliderFadeColor;
    private final Rect mTmpRect;

    void updateObscuredViewsVisibility(View panel) {
        int bottom;
        int top;
        int right;
        int left;
        boolean isLayoutRtl;
        int vis;
        View view = panel;
        boolean isLayoutRtl2 = isLayoutRtlSupport();
        int startBound = isLayoutRtl2 ? getWidth() - getPaddingRight() : getPaddingLeft();
        int endBound = isLayoutRtl2 ? getPaddingLeft() : getWidth() - getPaddingRight();
        int topBound = getPaddingTop();
        int bottomBound = getHeight() - getPaddingBottom();
        if (view != null && viewIsOpaque(panel)) {
            left = panel.getLeft();
            right = panel.getRight();
            top = panel.getTop();
            bottom = panel.getBottom();
        } else {
            bottom = 0;
            top = 0;
            right = 0;
            left = 0;
        }
        int i = 0;
        int childCount = getChildCount();
        while (i < childCount) {
            View child = getChildAt(i);
            if (child == view) {
                return;
            }
            if (child.getVisibility() == 8) {
                isLayoutRtl = isLayoutRtl2;
            } else {
                int clampedChildLeft = Math.max(isLayoutRtl2 ? endBound : startBound, child.getLeft());
                int clampedChildTop = Math.max(topBound, child.getTop());
                isLayoutRtl = isLayoutRtl2;
                int clampedChildRight = Math.min(isLayoutRtl2 ? startBound : endBound, child.getRight());
                int clampedChildBottom = Math.min(bottomBound, child.getBottom());
                if (clampedChildLeft >= left && clampedChildTop >= top && clampedChildRight <= right && clampedChildBottom <= bottom) {
                    vis = 4;
                } else {
                    vis = 0;
                }
                child.setVisibility(vis);
            }
            i++;
            isLayoutRtl2 = isLayoutRtl;
            view = panel;
        }
    }

    void setAllChildrenVisible() {
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() == 4) {
                child.setVisibility(0);
            }
        }
    }

    private static boolean viewIsOpaque(View v) {
        Drawable bg;
        if (v.isOpaque()) {
            return true;
        }
        return Build.VERSION.SDK_INT < 18 && (bg = v.getBackground()) != null && bg.getOpacity() == -1;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mFirstLayout = true;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mFirstLayout = true;
        int count = this.mPostedRunnables.size();
        for (int i = 0; i < count; i++) {
            DisableLayerRunnable dlr = this.mPostedRunnables.get(i);
            dlr.run();
        }
        this.mPostedRunnables.clear();
    }

    /* JADX WARN: Code restructure failed: missing block: B:38:0x00bb, code lost:
        if (r14.width == 0) goto L20;
     */
    @Override // android.view.View
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void onMeasure(int r33, int r34) {
        /*
            Method dump skipped, instructions count: 694
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.widget.SlidingPaneLayout.onMeasure(int, int):void");
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b2) {
        int paddingStart;
        int childLeft;
        int childRight;
        boolean isLayoutRtl = isLayoutRtlSupport();
        if (!isLayoutRtl) {
            this.mDragHelper.setEdgeTrackingEnabled(1);
        } else {
            this.mDragHelper.setEdgeTrackingEnabled(2);
        }
        int width = r - l;
        int paddingStart2 = isLayoutRtl ? getPaddingRight() : getPaddingLeft();
        int paddingEnd = isLayoutRtl ? getPaddingLeft() : getPaddingRight();
        int paddingTop = getPaddingTop();
        int childCount = getChildCount();
        int xStart = paddingStart2;
        int nextXStart = xStart;
        if (this.mFirstLayout) {
            this.mSlideOffset = (this.mCanSlide && this.mPreservedOpenState) ? 1.0f : 0.0f;
        }
        int xStart2 = xStart;
        int xStart3 = 0;
        while (xStart3 < childCount) {
            View child = getChildAt(xStart3);
            if (child.getVisibility() == 8) {
                paddingStart = paddingStart2;
            } else {
                LayoutParams lp = (LayoutParams) child.getLayoutParams();
                int childWidth = child.getMeasuredWidth();
                int offset = 0;
                if (lp.slideable) {
                    int margin = lp.leftMargin + lp.rightMargin;
                    int range = (Math.min(nextXStart, (width - paddingEnd) - this.mOverhangSize) - xStart2) - margin;
                    this.mSlideRange = range;
                    int lpMargin = isLayoutRtl ? lp.rightMargin : lp.leftMargin;
                    paddingStart = paddingStart2;
                    int margin2 = width - paddingEnd;
                    lp.dimWhenOffset = ((xStart2 + lpMargin) + range) + (childWidth / 2) > margin2;
                    int pos = (int) (range * this.mSlideOffset);
                    xStart2 += pos + lpMargin;
                    this.mSlideOffset = pos / this.mSlideRange;
                } else {
                    paddingStart = paddingStart2;
                    if (!this.mCanSlide || this.mParallaxBy == 0) {
                        xStart2 = nextXStart;
                    } else {
                        xStart2 = nextXStart;
                        offset = (int) ((1.0f - this.mSlideOffset) * this.mParallaxBy);
                    }
                }
                if (isLayoutRtl) {
                    childRight = (width - xStart2) + offset;
                    childLeft = childRight - childWidth;
                } else {
                    childLeft = xStart2 - offset;
                    childRight = childLeft + childWidth;
                }
                int childBottom = paddingTop + child.getMeasuredHeight();
                child.layout(childLeft, paddingTop, childRight, childBottom);
                nextXStart += child.getWidth();
            }
            xStart3++;
            paddingStart2 = paddingStart;
        }
        if (this.mFirstLayout) {
            if (this.mCanSlide) {
                if (this.mParallaxBy != 0) {
                    parallaxOtherViews(this.mSlideOffset);
                }
                if (((LayoutParams) this.mSlideableView.getLayoutParams()).dimWhenOffset) {
                    dimChildView(this.mSlideableView, this.mSlideOffset, this.mSliderFadeColor);
                }
            } else {
                for (int i = 0; i < childCount; i++) {
                    dimChildView(getChildAt(i), 0.0f, this.mSliderFadeColor);
                }
            }
            updateObscuredViewsVisibility(this.mSlideableView);
        }
        this.mFirstLayout = false;
    }

    @Override // android.view.View
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (w != oldw) {
            this.mFirstLayout = true;
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        if (!isInTouchMode() && !this.mCanSlide) {
            this.mPreservedOpenState = child == this.mSlideableView;
        }
    }

    @Override // android.view.ViewGroup
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        View secondChild;
        int action = ev.getActionMasked();
        if (!this.mCanSlide && action == 0 && getChildCount() > 1 && (secondChild = getChildAt(1)) != null) {
            this.mPreservedOpenState = !this.mDragHelper.isViewUnder(secondChild, (int) ev.getX(), (int) ev.getY());
        }
        if (!this.mCanSlide || (this.mIsUnableToDrag && action != 0)) {
            this.mDragHelper.cancel();
            return super.onInterceptTouchEvent(ev);
        } else if (action == 3 || action == 1) {
            this.mDragHelper.cancel();
            return false;
        } else {
            boolean interceptTap = false;
            if (action == 0) {
                this.mIsUnableToDrag = false;
                float x = ev.getX();
                float y = ev.getY();
                this.mInitialMotionX = x;
                this.mInitialMotionY = y;
                if (this.mDragHelper.isViewUnder(this.mSlideableView, (int) x, (int) y) && isDimmed(this.mSlideableView)) {
                    interceptTap = true;
                }
            } else if (action == 2) {
                float x2 = ev.getX();
                float y2 = ev.getY();
                float adx = Math.abs(x2 - this.mInitialMotionX);
                float ady = Math.abs(y2 - this.mInitialMotionY);
                int slop = this.mDragHelper.getTouchSlop();
                if (adx > slop && ady > adx) {
                    this.mDragHelper.cancel();
                    this.mIsUnableToDrag = true;
                    return false;
                }
            }
            boolean interceptForDrag = this.mDragHelper.shouldInterceptTouchEvent(ev);
            return interceptForDrag || interceptTap;
        }
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent ev) {
        if (!this.mCanSlide) {
            return super.onTouchEvent(ev);
        }
        this.mDragHelper.processTouchEvent(ev);
        switch (ev.getActionMasked()) {
            case 0:
                float x = ev.getX();
                float y = ev.getY();
                this.mInitialMotionX = x;
                this.mInitialMotionY = y;
                break;
            case 1:
                if (isDimmed(this.mSlideableView)) {
                    float x2 = ev.getX();
                    float y2 = ev.getY();
                    float dx = x2 - this.mInitialMotionX;
                    float dy = y2 - this.mInitialMotionY;
                    int slop = this.mDragHelper.getTouchSlop();
                    if ((dx * dx) + (dy * dy) < slop * slop && this.mDragHelper.isViewUnder(this.mSlideableView, (int) x2, (int) y2)) {
                        closePane(this.mSlideableView, 0);
                        break;
                    }
                }
                break;
        }
        return true;
    }

    private boolean closePane(View pane, int initialVelocity) {
        if (this.mFirstLayout || smoothSlideTo(0.0f, initialVelocity)) {
            this.mPreservedOpenState = false;
            return true;
        }
        return false;
    }

    private boolean openPane(View pane, int initialVelocity) {
        if (this.mFirstLayout || smoothSlideTo(1.0f, initialVelocity)) {
            this.mPreservedOpenState = true;
            return true;
        }
        return false;
    }

    public boolean openPane() {
        return openPane(this.mSlideableView, 0);
    }

    public boolean closePane() {
        return closePane(this.mSlideableView, 0);
    }

    public boolean isOpen() {
        return !this.mCanSlide || this.mSlideOffset == 1.0f;
    }

    public boolean isSlideable() {
        return this.mCanSlide;
    }

    private void dimChildView(View v, float mag, int fadeColor) {
        LayoutParams lp = (LayoutParams) v.getLayoutParams();
        if (mag <= 0.0f || fadeColor == 0) {
            if (v.getLayerType() != 0) {
                if (lp.dimPaint != null) {
                    lp.dimPaint.setColorFilter(null);
                }
                DisableLayerRunnable dlr = new DisableLayerRunnable(v);
                this.mPostedRunnables.add(dlr);
                ViewCompat.postOnAnimation(this, dlr);
                return;
            }
            return;
        }
        int baseAlpha = ((-16777216) & fadeColor) >>> 24;
        int imag = (int) (baseAlpha * mag);
        int color = (imag << 24) | (16777215 & fadeColor);
        if (lp.dimPaint == null) {
            lp.dimPaint = new Paint();
        }
        lp.dimPaint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_OVER));
        if (v.getLayerType() != 2) {
            v.setLayerType(2, lp.dimPaint);
        }
        invalidateChildRegion(v);
    }

    @Override // android.view.ViewGroup
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        int save = canvas.save();
        if (this.mCanSlide && !lp.slideable && this.mSlideableView != null) {
            canvas.getClipBounds(this.mTmpRect);
            if (isLayoutRtlSupport()) {
                this.mTmpRect.left = Math.max(this.mTmpRect.left, this.mSlideableView.getRight());
            } else {
                this.mTmpRect.right = Math.min(this.mTmpRect.right, this.mSlideableView.getLeft());
            }
            canvas.clipRect(this.mTmpRect);
        }
        boolean result = super.drawChild(canvas, child, drawingTime);
        canvas.restoreToCount(save);
        return result;
    }

    void invalidateChildRegion(View v) {
        if (Build.VERSION.SDK_INT >= 17) {
            ViewCompat.setLayerPaint(v, ((LayoutParams) v.getLayoutParams()).dimPaint);
            return;
        }
        if (Build.VERSION.SDK_INT >= 16) {
            if (!this.mDisplayListReflectionLoaded) {
                try {
                    this.mGetDisplayList = View.class.getDeclaredMethod("getDisplayList", null);
                } catch (NoSuchMethodException e) {
                    Log.e("SlidingPaneLayout", "Couldn't fetch getDisplayList method; dimming won't work right.", e);
                }
                try {
                    this.mRecreateDisplayList = View.class.getDeclaredField("mRecreateDisplayList");
                    this.mRecreateDisplayList.setAccessible(true);
                } catch (NoSuchFieldException e2) {
                    Log.e("SlidingPaneLayout", "Couldn't fetch mRecreateDisplayList field; dimming will be slow.", e2);
                }
                this.mDisplayListReflectionLoaded = true;
            }
            if (this.mGetDisplayList == null || this.mRecreateDisplayList == null) {
                v.invalidate();
                return;
            }
            try {
                this.mRecreateDisplayList.setBoolean(v, true);
                this.mGetDisplayList.invoke(v, null);
            } catch (Exception e3) {
                Log.e("SlidingPaneLayout", "Error refreshing display list state", e3);
            }
        }
        ViewCompat.postInvalidateOnAnimation(this, v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
    }

    boolean smoothSlideTo(float slideOffset, int velocity) {
        int startBound;
        if (this.mCanSlide) {
            boolean isLayoutRtl = isLayoutRtlSupport();
            LayoutParams lp = (LayoutParams) this.mSlideableView.getLayoutParams();
            if (isLayoutRtl) {
                int startBound2 = getPaddingRight() + lp.rightMargin;
                int childWidth = this.mSlideableView.getWidth();
                startBound = (int) (getWidth() - ((startBound2 + (this.mSlideRange * slideOffset)) + childWidth));
            } else {
                int x = getPaddingLeft();
                int startBound3 = x + lp.leftMargin;
                startBound = (int) (startBound3 + (this.mSlideRange * slideOffset));
            }
            if (this.mDragHelper.smoothSlideViewTo(this.mSlideableView, startBound, this.mSlideableView.getTop())) {
                setAllChildrenVisible();
                ViewCompat.postInvalidateOnAnimation(this);
                return true;
            }
            return false;
        }
        return false;
    }

    @Override // android.view.View
    public void computeScroll() {
        if (this.mDragHelper.continueSettling(true)) {
            if (!this.mCanSlide) {
                this.mDragHelper.abort();
            } else {
                ViewCompat.postInvalidateOnAnimation(this);
            }
        }
    }

    @Override // android.view.View
    public void draw(Canvas c) {
        Drawable shadowDrawable;
        int right;
        int left;
        super.draw(c);
        boolean isLayoutRtl = isLayoutRtlSupport();
        if (isLayoutRtl) {
            shadowDrawable = this.mShadowDrawableRight;
        } else {
            shadowDrawable = this.mShadowDrawableLeft;
        }
        View shadowView = getChildCount() > 1 ? getChildAt(1) : null;
        if (shadowView == null || shadowDrawable == null) {
            return;
        }
        int top = shadowView.getTop();
        int bottom = shadowView.getBottom();
        int shadowWidth = shadowDrawable.getIntrinsicWidth();
        if (isLayoutRtlSupport()) {
            left = shadowView.getRight();
            right = left + shadowWidth;
        } else {
            right = shadowView.getLeft();
            left = right - shadowWidth;
        }
        shadowDrawable.setBounds(left, top, right, bottom);
        shadowDrawable.draw(c);
    }

    /* JADX WARN: Removed duplicated region for block: B:12:0x0024  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void parallaxOtherViews(float r12) {
        /*
            r11 = this;
            boolean r0 = r11.isLayoutRtlSupport()
            android.view.View r1 = r11.mSlideableView
            android.view.ViewGroup$LayoutParams r1 = r1.getLayoutParams()
            android.support.v4.widget.SlidingPaneLayout$LayoutParams r1 = (android.support.v4.widget.SlidingPaneLayout.LayoutParams) r1
            boolean r2 = r1.dimWhenOffset
            r3 = 0
            if (r2 == 0) goto L1c
            if (r0 == 0) goto L16
            int r2 = r1.rightMargin
            goto L18
        L16:
            int r2 = r1.leftMargin
        L18:
            if (r2 > 0) goto L1c
            r2 = 1
            goto L1d
        L1c:
            r2 = r3
        L1d:
            int r4 = r11.getChildCount()
        L22:
            if (r3 >= r4) goto L5f
            android.view.View r5 = r11.getChildAt(r3)
            android.view.View r6 = r11.mSlideableView
            if (r5 != r6) goto L2d
            goto L5c
        L2d:
            float r6 = r11.mParallaxOffset
            r7 = 1065353216(0x3f800000, float:1.0)
            float r6 = r7 - r6
            int r8 = r11.mParallaxBy
            float r8 = (float) r8
            float r6 = r6 * r8
            int r6 = (int) r6
            r11.mParallaxOffset = r12
            float r8 = r7 - r12
            int r9 = r11.mParallaxBy
            float r9 = (float) r9
            float r8 = r8 * r9
            int r8 = (int) r8
            int r9 = r6 - r8
            if (r0 == 0) goto L47
            int r10 = -r9
            goto L48
        L47:
            r10 = r9
        L48:
            r5.offsetLeftAndRight(r10)
            if (r2 == 0) goto L5c
            if (r0 == 0) goto L53
            float r10 = r11.mParallaxOffset
            float r10 = r10 - r7
            goto L57
        L53:
            float r10 = r11.mParallaxOffset
            float r10 = r7 - r10
        L57:
            int r7 = r11.mCoveredFadeColor
            r11.dimChildView(r5, r10, r7)
        L5c:
            int r3 = r3 + 1
            goto L22
        L5f:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.widget.SlidingPaneLayout.parallaxOtherViews(float):void");
    }

    boolean isDimmed(View child) {
        if (child == null) {
            return false;
        }
        LayoutParams lp = (LayoutParams) child.getLayoutParams();
        return this.mCanSlide && lp.dimWhenOffset && this.mSlideOffset > 0.0f;
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof ViewGroup.MarginLayoutParams ? new LayoutParams((ViewGroup.MarginLayoutParams) p) : new LayoutParams(p);
    }

    @Override // android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return (p instanceof LayoutParams) && super.checkLayoutParams(p);
    }

    @Override // android.view.ViewGroup
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState ss = new SavedState(superState);
        ss.isOpen = isSlideable() ? isOpen() : this.mPreservedOpenState;
        return ss;
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof SavedState)) {
            super.onRestoreInstanceState(state);
            return;
        }
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        if (ss.isOpen) {
            openPane();
        } else {
            closePane();
        }
        this.mPreservedOpenState = ss.isOpen;
    }

    /* loaded from: classes.dex */
    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        private static final int[] ATTRS = {16843137};
        Paint dimPaint;
        boolean dimWhenOffset;
        boolean slideable;
        public float weight;

        public LayoutParams() {
            super(-1, -1);
            this.weight = 0.0f;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
            this.weight = 0.0f;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams source) {
            super(source);
            this.weight = 0.0f;
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            this.weight = 0.0f;
            TypedArray a2 = c.obtainStyledAttributes(attrs, ATTRS);
            this.weight = a2.getFloat(0, 0.0f);
            a2.recycle();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class SavedState extends AbsSavedState {
        public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.ClassLoaderCreator<SavedState>() { // from class: android.support.v4.widget.SlidingPaneLayout.SavedState.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.ClassLoaderCreator
            public SavedState createFromParcel(Parcel in, ClassLoader loader) {
                return new SavedState(in, null);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in, null);
            }

            @Override // android.os.Parcelable.Creator
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
        boolean isOpen;

        SavedState(Parcelable superState) {
            super(superState);
        }

        SavedState(Parcel in, ClassLoader loader) {
            super(in, loader);
            this.isOpen = in.readInt() != 0;
        }

        @Override // android.support.v4.view.AbsSavedState, android.os.Parcelable
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeInt(this.isOpen ? 1 : 0);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public class DisableLayerRunnable implements Runnable {
        final View mChildView;

        DisableLayerRunnable(View childView) {
            this.mChildView = childView;
        }

        @Override // java.lang.Runnable
        public void run() {
            if (this.mChildView.getParent() == SlidingPaneLayout.this) {
                this.mChildView.setLayerType(0, null);
                SlidingPaneLayout.this.invalidateChildRegion(this.mChildView);
            }
            SlidingPaneLayout.this.mPostedRunnables.remove(this);
        }
    }

    boolean isLayoutRtlSupport() {
        return ViewCompat.getLayoutDirection(this) == 1;
    }
}
