package xiaopeng.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.math.MathUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
/* loaded from: classes.dex */
public class SimpleSlider extends View {
    private static final int MAX_LEVEL = 10000;
    private static final int MAX_VALUE = 100;
    private static final int MIN_VALUE = 0;
    private static final int NO_ALPHA = 255;
    public static final int TOUCH_MODE_SEEK = 1;
    public static final int TOUCH_MODE_SLIDE = 0;
    private float mDisabledAlpha;
    private boolean mEnabled;
    private int mMax;
    private boolean mMaxInitialized;
    private int mMin;
    private boolean mMinInitialized;
    private OnSlideChangeListener mOnSlideChangeListener;
    private int mProgress;
    private Drawable mProgressDrawable;
    private int mProgressDrawableRes;
    private int mScaledTouchSlop;
    private float mSlideScale;
    private Drawable mTickMark;
    private boolean mTickMarkAll;
    private int mTickMarkRes;
    private TouchEventHandler mTouchEventHandler;
    protected int mTouchMode;

    /* loaded from: classes.dex */
    public interface OnSlideChangeListener {
        void onProgressChanged(SimpleSlider simpleSlider, int i, boolean z);

        void onStartTrackingTouch(SimpleSlider simpleSlider);

        void onStopTrackingTouch(SimpleSlider simpleSlider);
    }

    /* loaded from: classes.dex */
    public static class SeekHandler implements TouchEventHandler {
        private boolean mIsDragging;
        private SimpleSlider mSimpleSlider;
        private float mTouchDownX;

        private boolean handleSeekTouch(MotionEvent motionEvent) {
            switch (motionEvent.getAction()) {
                case 0:
                    if (this.mSimpleSlider.isInScrollingContainer()) {
                        this.mTouchDownX = motionEvent.getX();
                        return true;
                    }
                    onStartTrackingTouch();
                    trackTouchEvent(motionEvent);
                    this.mSimpleSlider.attemptClaimDrag();
                    return true;
                case 1:
                    if (!this.mIsDragging) {
                        onStartTrackingTouch();
                    }
                    trackTouchEvent(motionEvent);
                    onStopTrackingTouch();
                    return true;
                case 2:
                    if (this.mIsDragging) {
                        trackTouchEvent(motionEvent);
                        return true;
                    }
                    if (Math.abs(motionEvent.getX() - this.mTouchDownX) <= this.mSimpleSlider.getScaledTouchSlop()) {
                        return true;
                    }
                    onStartTrackingTouch();
                    trackTouchEvent(motionEvent);
                    this.mSimpleSlider.attemptClaimDrag();
                    return true;
                case 3:
                    if (!this.mIsDragging) {
                        return true;
                    }
                    onStopTrackingTouch();
                    return true;
                default:
                    return true;
            }
        }

        private void onStartTrackingTouch() {
            this.mIsDragging = true;
            this.mSimpleSlider.onStartTrackingTouch();
        }

        private void onStopTrackingTouch() {
            this.mIsDragging = false;
            this.mSimpleSlider.onStopTrackingTouch();
        }

        private void trackTouchEvent(MotionEvent motionEvent) {
            this.mSimpleSlider.setProgressInternal(Math.round(((Math.round(motionEvent.getX()) / ((this.mSimpleSlider.getWidth() - ((View) this.mSimpleSlider).mPaddingLeft) - ((View) this.mSimpleSlider).mPaddingRight)) * (this.mSimpleSlider.getMax() - this.mSimpleSlider.getMin())) + this.mSimpleSlider.getMin() + 0.0f), true);
        }

        @Override // xiaopeng.widget.SimpleSlider.TouchEventHandler
        public boolean onTouchEvent(SimpleSlider simpleSlider, MotionEvent motionEvent) {
            this.mSimpleSlider = simpleSlider;
            return handleSeekTouch(motionEvent);
        }
    }

    /* loaded from: classes.dex */
    public static class SlideHandler implements TouchEventHandler {
        private boolean mIsDragging;
        private SimpleSlider mSimpleSlider;
        private int mTouchDownProgress;
        private float mTouchDownX;

        private boolean handleSlide(MotionEvent motionEvent) {
            switch (motionEvent.getActionMasked()) {
                case 0:
                    this.mTouchDownX = motionEvent.getX();
                    break;
                case 1:
                case 3:
                    if (this.mIsDragging) {
                        this.mSimpleSlider.onStopTrackingTouch();
                        this.mIsDragging = false;
                        return true;
                    }
                    break;
                case 2:
                    if (this.mIsDragging) {
                        trackTouchEvent(motionEvent);
                        return true;
                    } else if (Math.abs(motionEvent.getX() - this.mTouchDownX) > this.mSimpleSlider.getScaledTouchSlop()) {
                        this.mTouchDownProgress = this.mSimpleSlider.getProgress();
                        this.mIsDragging = true;
                        this.mSimpleSlider.onStartTrackingTouch();
                        trackTouchEvent(motionEvent);
                        this.mSimpleSlider.attemptClaimDrag();
                        return true;
                    }
                    break;
                default:
                    return true;
            }
            return true;
        }

        private void trackTouchEvent(MotionEvent motionEvent) {
            this.mSimpleSlider.setProgressInternal(Math.round(this.mTouchDownProgress + ((Math.round(motionEvent.getX() - this.mTouchDownX) / ((this.mSimpleSlider.getWidth() - ((View) this.mSimpleSlider).mPaddingLeft) - ((View) this.mSimpleSlider).mPaddingRight)) * this.mSimpleSlider.getSlideScale() * (this.mSimpleSlider.getMax() - this.mSimpleSlider.getMin())) + this.mSimpleSlider.getMin()), true);
        }

        @Override // xiaopeng.widget.SimpleSlider.TouchEventHandler
        public boolean onTouchEvent(SimpleSlider simpleSlider, MotionEvent motionEvent) {
            this.mSimpleSlider = simpleSlider;
            return handleSlide(motionEvent);
        }
    }

    /* loaded from: classes.dex */
    public interface TouchEventHandler {
        boolean onTouchEvent(SimpleSlider simpleSlider, MotionEvent motionEvent);
    }

    public SimpleSlider(Context context) {
        this(context, null);
    }

    public SimpleSlider(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.style.SimpleSlider);
    }

    public SimpleSlider(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, R.style.SimpleSlider);
    }

    public SimpleSlider(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mProgress = 0;
        this.mMin = 0;
        this.mMax = 100;
        this.mMaxInitialized = false;
        this.mMinInitialized = false;
        this.mSlideScale = 1.0f;
        this.mProgressDrawableRes = -1;
        this.mTickMarkRes = -1;
        this.mTickMarkAll = false;
        this.mTouchMode = 0;
        this.mEnabled = true;
        init(context, attributeSet, i, i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void attemptClaimDrag() {
        ViewParent viewParent = ((View) this).mParent;
        if (viewParent != null) {
            viewParent.requestDisallowInterceptTouchEvent(true);
        }
    }

    private void init(Context context, AttributeSet attributeSet, int i, int i2) {
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.SimpleSlider, i, i2);
        this.mProgressDrawableRes = obtainStyledAttributes.getResourceId(R.styleable.SimpleSlider_android_progressDrawable, this.mProgressDrawableRes);
        setProgressDrawable(obtainStyledAttributes.getDrawable(R.styleable.SimpleSlider_android_progressDrawable));
        this.mTickMarkRes = obtainStyledAttributes.getResourceId(R.styleable.SimpleSlider_android_tickMark, this.mTickMarkRes);
        setTickMark(obtainStyledAttributes.getDrawable(R.styleable.SimpleSlider_android_tickMark));
        this.mTickMarkAll = obtainStyledAttributes.getBoolean(R.styleable.SimpleSlider_ss_tickMarkAll, this.mTickMarkAll);
        this.mSlideScale = obtainStyledAttributes.getFloat(R.styleable.SimpleSlider_ss_slideScale, this.mSlideScale);
        this.mEnabled = obtainStyledAttributes.getBoolean(R.styleable.SimpleSlider_android_enabled, this.mEnabled);
        this.mDisabledAlpha = obtainStyledAttributes.getFloat(R.styleable.SimpleSlider_android_disabledAlpha, 0.36f);
        setMin(obtainStyledAttributes.getInt(R.styleable.SimpleSlider_android_min, this.mMin));
        setMax(obtainStyledAttributes.getInt(R.styleable.SimpleSlider_android_max, this.mMax));
        setProgress(obtainStyledAttributes.getInt(R.styleable.SimpleSlider_android_progress, this.mProgress));
        this.mTouchMode = obtainStyledAttributes.getInt(R.styleable.SimpleSlider_ss_touchMode, this.mTouchMode);
        this.mTouchEventHandler = this.mTouchMode == 1 ? new SeekHandler() : new SlideHandler();
        obtainStyledAttributes.recycle();
        this.mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    private int limitProgress(int i) {
        return MathUtils.clamp(i, this.mMin, this.mMax);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setProgressInternal(int i, boolean z) {
        int limitProgress = limitProgress(i);
        this.mProgress = limitProgress;
        int i2 = this.mMax;
        int i3 = this.mMin;
        int i4 = i2 - i3;
        setVisualProgress(i4 > 0 ? (limitProgress - i3) / i4 : 0.0f);
        OnSlideChangeListener onSlideChangeListener = this.mOnSlideChangeListener;
        if (onSlideChangeListener != null) {
            onSlideChangeListener.onProgressChanged(this, limitProgress, z);
        }
    }

    private void updateDrawableBounds(int i, int i2) {
        int i3 = i - (((View) this).mPaddingRight + ((View) this).mPaddingLeft);
        int i4 = i2 - (((View) this).mPaddingTop + ((View) this).mPaddingBottom);
        Drawable drawable = this.mProgressDrawable;
        if (drawable != null) {
            drawable.setBounds(0, 0, i3, i4);
        }
    }

    private void updateDrawableState() {
        int[] drawableState = getDrawableState();
        Drawable drawable = this.mProgressDrawable;
        if ((drawable == null || !drawable.isStateful()) ? false : drawable.setState(drawableState)) {
            invalidate();
        }
    }

    @Override // android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        if (isEnabled()) {
            int actionMasked = motionEvent.getActionMasked();
            if (actionMasked == 0) {
                setPressed(true);
            } else if (actionMasked == 1 || actionMasked == 3) {
                setPressed(false);
            }
            return super.dispatchTouchEvent(motionEvent);
        }
        return true;
    }

    protected void drawTickMarks(Canvas canvas) {
        Drawable tickMark = getTickMark();
        if (tickMark != null) {
            int max = getMax() - getMin();
            if (max > 1) {
                int intrinsicWidth = tickMark.getIntrinsicWidth();
                int intrinsicHeight = tickMark.getIntrinsicHeight();
                int i = intrinsicWidth >= 0 ? intrinsicWidth / 2 : 1;
                int i2 = intrinsicHeight >= 0 ? intrinsicHeight / 2 : 1;
                tickMark.setBounds(-i, -i2, i, i2);
                float width = ((getWidth() - ((View) this).mPaddingLeft) - ((View) this).mPaddingRight) / max;
                int save = canvas.save();
                canvas.translate(((View) this).mPaddingLeft, getHeight() >> 1);
                canvas.translate(width, 0.0f);
                for (int i3 = 1; i3 < max && (this.mTickMarkAll || i3 < this.mProgress); i3++) {
                    tickMark.draw(canvas);
                    canvas.translate(width, 0.0f);
                }
                canvas.translate(width, 0.0f);
                canvas.restoreToCount(save);
            }
        }
    }

    @Override // android.view.View
    public void drawableHotspotChanged(float f, float f2) {
        super.drawableHotspotChanged(f, f2);
        Drawable drawable = this.mProgressDrawable;
        if (drawable != null) {
            drawable.setHotspot(f, f2);
        }
    }

    @Override // android.view.View
    protected void drawableStateChanged() {
        Drawable drawable = this.mProgressDrawable;
        int i = NO_ALPHA;
        if (drawable != null) {
            drawable.setAlpha(isEnabled() ? NO_ALPHA : (int) (this.mDisabledAlpha * 255.0f));
        }
        Drawable drawable2 = this.mTickMark;
        if (drawable2 != null) {
            if (!isEnabled()) {
                i = (int) (this.mDisabledAlpha * 255.0f);
            }
            drawable2.setAlpha(i);
        }
        super.drawableStateChanged();
    }

    public int getMax() {
        return this.mMax;
    }

    public int getMin() {
        return this.mMin;
    }

    public int getProgress() {
        return this.mProgress;
    }

    public int getRange() {
        return this.mMax - this.mMin;
    }

    int getScaledTouchSlop() {
        return this.mScaledTouchSlop;
    }

    public float getSlideScale() {
        return this.mSlideScale;
    }

    public Drawable getTickMark() {
        return this.mTickMark;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        Drawable drawable = this.mProgressDrawable;
        if (drawable != null) {
            drawable.draw(canvas);
        }
        drawTickMarks(canvas);
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        setEnabled(this.mEnabled);
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        updateDrawableBounds(i, i2);
    }

    protected void onStartTrackingTouch() {
        OnSlideChangeListener onSlideChangeListener = this.mOnSlideChangeListener;
        if (onSlideChangeListener != null) {
            onSlideChangeListener.onStartTrackingTouch(this);
        }
    }

    protected void onStopTrackingTouch() {
        OnSlideChangeListener onSlideChangeListener = this.mOnSlideChangeListener;
        if (onSlideChangeListener != null) {
            onSlideChangeListener.onStopTrackingTouch(this);
        }
    }

    @Override // android.view.View
    @SuppressLint({"ClickableViewAccessibility"})
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (isEnabled()) {
            TouchEventHandler touchEventHandler = this.mTouchEventHandler;
            return touchEventHandler != null ? touchEventHandler.onTouchEvent(this, motionEvent) : super.onTouchEvent(motionEvent);
        }
        return false;
    }

    public void refreshVisual() {
        if (this.mProgressDrawableRes != -1) {
            setProgressDrawable(getContext().getDrawable(this.mProgressDrawableRes));
        }
        if (this.mTickMarkRes != -1) {
            setTickMark(getContext().getDrawable(this.mTickMarkRes));
        }
        int range = getRange();
        setVisualProgress(range > 0 ? (getProgress() - getMin()) / range : 0.0f);
    }

    public void setMax(int i) {
        int i2;
        if (this.mMinInitialized && i < (i2 = this.mMin)) {
            i = i2;
        }
        this.mMaxInitialized = true;
        if (!this.mMinInitialized || i == this.mMax) {
            this.mMax = i;
            return;
        }
        this.mMax = i;
        postInvalidate();
        if (this.mProgress > i) {
            this.mProgress = i;
        }
        setProgress(this.mProgress);
    }

    public void setMin(int i) {
        int i2;
        if (this.mMaxInitialized && i > (i2 = this.mMax)) {
            i = i2;
        }
        this.mMinInitialized = true;
        if (!this.mMaxInitialized || i == this.mMin) {
            this.mMin = i;
            return;
        }
        this.mMin = i;
        postInvalidate();
        if (this.mProgress < i) {
            this.mProgress = i;
        }
        setProgress(this.mProgress);
    }

    public void setOnSlideChangeListener(OnSlideChangeListener onSlideChangeListener) {
        this.mOnSlideChangeListener = onSlideChangeListener;
    }

    public void setProgress(int i) {
        setProgressInternal(i, false);
    }

    public void setProgressDrawable(Drawable drawable) {
        Drawable drawable2 = this.mProgressDrawable;
        if (drawable2 != drawable) {
            if (drawable2 != null) {
                drawable2.setCallback(null);
                unscheduleDrawable(this.mProgressDrawable);
            }
            this.mProgressDrawable = drawable;
            if (drawable != null) {
                drawable.setCallback(this);
                drawable.setLayoutDirection(getLayoutDirection());
                if (drawable.isStateful()) {
                    drawable.setState(getDrawableState());
                }
                updateDrawableBounds(getWidth(), getHeight());
                updateDrawableState();
            }
        }
    }

    public void setSlideScale(float f) {
        this.mSlideScale = f;
    }

    public void setTickMark(Drawable drawable) {
        Drawable drawable2 = this.mTickMark;
        if (drawable2 != null) {
            drawable2.setCallback(null);
        }
        this.mTickMark = drawable;
        if (drawable != null) {
            drawable.setCallback(this);
            drawable.setLayoutDirection(getLayoutDirection());
            if (drawable.isStateful()) {
                drawable.setState(getDrawableState());
            }
        }
        invalidate();
    }

    public void setTouchEventHandler(TouchEventHandler touchEventHandler) {
        this.mTouchEventHandler = touchEventHandler;
    }

    protected void setVisualProgress(float f) {
        Drawable drawable = this.mProgressDrawable;
        if ((drawable instanceof LayerDrawable) && (drawable = ((LayerDrawable) drawable).findDrawableByLayerId(16908301)) == null) {
            drawable = this.mProgressDrawable;
        }
        if (drawable != null) {
            drawable.setLevel((int) (f * 10000.0f));
        } else {
            invalidate();
        }
    }

    @Override // android.view.View
    protected boolean verifyDrawable(Drawable drawable) {
        return drawable == this.mProgressDrawable || super.verifyDrawable(drawable);
    }
}
