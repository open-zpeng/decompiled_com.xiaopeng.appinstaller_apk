package com.xiaopeng.xui.drawable.ripple;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.Transformation;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.Xui;
/* loaded from: classes.dex */
public class XRipple {
    private static final long ANIMATION_TIME = 400;
    private static final String TAG = "XRipple";
    private AnimTransformListener mAnimTransformListener;
    private ValueAnimator mAnimationPress;
    private ValueAnimator mAnimationUp;
    private int mBackgroundColor;
    private Drawable.Callback mCallback;
    private float mClearDistance;
    private float mCurrentDistance;
    private float mDownX;
    private float mDownY;
    private boolean mIsAnimating;
    private boolean mIsPressAnimating;
    private boolean mIsTouched;
    private boolean mIsUpAnimating;
    private float mMaxPressRadius;
    private boolean mNeedUpAnim;
    private Paint mPaint;
    private float mPressRadius;
    private int mRippleAlpha;
    private int mRippleColor;
    private Path mRipplePath;
    private float mRippleRadius;
    private RectF mRippleRectF;
    private boolean mSupportScale;
    private View mView;

    /* loaded from: classes.dex */
    public interface AnimTransformListener {
        void onDownTransformation(float f, Transformation transformation);

        void onUpTransformation(float f, Transformation transformation);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class a implements ValueAnimator.AnimatorUpdateListener {
        a() {
        }

        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            XRipple xRipple = XRipple.this;
            xRipple.mPressRadius = xRipple.mMaxPressRadius * floatValue;
            if (XRipple.this.mAnimTransformListener != null) {
                XRipple.this.mAnimTransformListener.onDownTransformation(floatValue, null);
            }
            if (XRipple.this.mView != null && XRipple.this.mSupportScale) {
                float f = 1.0f - (floatValue * 0.1f);
                XRipple.this.mView.setScaleX(f);
                XRipple.this.mView.setScaleY(f);
            }
            XRipple.this.invalidateSelf();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class b implements Animator.AnimatorListener {
        b() {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            if (XRipple.this.mIsPressAnimating) {
                XRipple.this.mIsPressAnimating = false;
                if (XRipple.this.mNeedUpAnim) {
                    XRipple.this.mNeedUpAnim = false;
                    XRipple.this.mAnimationUp.start();
                    XRipple.this.mIsUpAnimating = true;
                }
            }
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator) {
            XRipple.this.mPaint.setAlpha(XRipple.this.mRippleAlpha);
            XRipple.this.mIsPressAnimating = true;
            XRipple.this.mIsAnimating = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class c implements ValueAnimator.AnimatorUpdateListener {
        c() {
        }

        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            float floatValue = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            XRipple xRipple = XRipple.this;
            float f = 1.0f - floatValue;
            xRipple.mCurrentDistance = xRipple.mClearDistance * f;
            if (XRipple.this.mAnimTransformListener != null) {
                XRipple.this.mAnimTransformListener.onUpTransformation(floatValue, null);
            }
            XRipple.this.mPaint.setAlpha((int) (XRipple.this.mRippleAlpha * f));
            if (XRipple.this.mView != null && XRipple.this.mSupportScale) {
                float f2 = (floatValue * 0.1f) + 0.9f;
                XRipple.this.mView.setScaleX(f2);
                XRipple.this.mView.setScaleY(f2);
            }
            XRipple.this.invalidateSelf();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class d implements Animator.AnimatorListener {
        d() {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            XRipple.this.mPaint.setAlpha(XRipple.this.mRippleAlpha);
            XRipple.this.mIsUpAnimating = false;
            XRipple.this.mIsAnimating = false;
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator) {
            XRipple.this.mIsUpAnimating = true;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public XRipple() {
        init();
    }

    public XRipple(Drawable.Callback callback) {
        this.mCallback = callback;
        getView(callback);
        init();
    }

    private void drawRippleBackground(Canvas canvas) {
        int i = this.mBackgroundColor;
        if (i != 0) {
            this.mPaint.setColor(i);
            RectF rectF = this.mRippleRectF;
            float f = this.mRippleRadius;
            canvas.drawRoundRect(rectF, f, f, this.mPaint);
            this.mPaint.setColor(this.mRippleColor);
        }
    }

    private void getView(Drawable.Callback callback) {
        if (callback == null) {
            String str = TAG;
            Log.d(str, hashCode() + ",callback is null");
        } else if (callback instanceof Drawable) {
            getView(((Drawable) callback).getCallback());
        } else if (callback instanceof View) {
            this.mView = (View) callback;
        } else {
            String str2 = TAG;
            Log.d(str2, hashCode() + ",callback is " + callback);
        }
    }

    private void init() {
        this.mRipplePath = new Path();
        this.mRippleColor = Xui.getContext().getColor(R.color.x_ripple_default_color);
        this.mPaint = new Paint();
        this.mPaint.setAntiAlias(true);
        this.mPaint.setColor(this.mRippleColor);
        this.mRippleAlpha = this.mPaint.getAlpha();
        initAnimation();
    }

    private void initAnimation() {
        AccelerateDecelerateInterpolator accelerateDecelerateInterpolator = new AccelerateDecelerateInterpolator();
        this.mAnimationPress = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(ANIMATION_TIME);
        this.mAnimationPress.addUpdateListener(new a());
        this.mAnimationPress.addListener(new b());
        this.mAnimationPress.setInterpolator(accelerateDecelerateInterpolator);
        this.mAnimationPress.setDuration(ANIMATION_TIME);
        this.mAnimationUp = ValueAnimator.ofFloat(0.0f, 1.0f).setDuration(ANIMATION_TIME);
        this.mAnimationUp.addUpdateListener(new c());
        this.mAnimationUp.addListener(new d());
        this.mAnimationUp.setInterpolator(accelerateDecelerateInterpolator);
        this.mAnimationUp.setDuration(ANIMATION_TIME);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void invalidateSelf() {
        Drawable.Callback callback = this.mCallback;
        if (callback == null) {
            Log.d(TAG, "Callback is null");
        } else if (callback instanceof View) {
            ((View) callback).invalidate();
        } else if (callback instanceof Drawable) {
            ((Drawable) callback).invalidateSelf();
        }
    }

    private boolean isVisible() {
        Drawable.Callback callback = this.mCallback;
        if (callback != null) {
            if (callback instanceof View) {
                return ((View) callback).getVisibility() == 0;
            } else if (callback instanceof Drawable) {
                return ((Drawable) callback).isVisible();
            }
        }
        return true;
    }

    private void resetPath() {
        if (this.mRippleRectF != null) {
            this.mRipplePath.reset();
            Path path = this.mRipplePath;
            RectF rectF = this.mRippleRectF;
            float f = this.mRippleRadius;
            path.addRoundRect(rectF, f, f, Path.Direction.CW);
        }
    }

    public void drawRipple(Canvas canvas) {
        int saveLayer;
        if (isVisible()) {
            if (this.mRippleRectF == null) {
                setRippleRect(new RectF(0.0f, 0.0f, canvas.getWidth(), canvas.getHeight()));
            }
            if (this.mIsPressAnimating && this.mIsAnimating) {
                drawRippleBackground(canvas);
                saveLayer = canvas.saveLayer(0.0f, 0.0f, canvas.getWidth(), canvas.getHeight(), null);
                canvas.clipPath(this.mRipplePath);
                canvas.drawCircle(this.mDownX, this.mDownY, this.mPressRadius, this.mPaint);
            } else if (!this.mIsUpAnimating || !this.mIsAnimating) {
                if (this.mIsTouched && this.mIsAnimating) {
                    drawRippleBackground(canvas);
                    RectF rectF = this.mRippleRectF;
                    float f = this.mRippleRadius;
                    canvas.drawRoundRect(rectF, f, f, this.mPaint);
                    return;
                }
                return;
            } else {
                drawRippleBackground(canvas);
                saveLayer = canvas.saveLayer(0.0f, 0.0f, canvas.getWidth(), canvas.getHeight(), null);
                RectF rectF2 = this.mRippleRectF;
                float f2 = this.mRippleRadius;
                canvas.drawRoundRect(rectF2, f2, f2, this.mPaint);
                this.mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                float f3 = this.mCurrentDistance;
                float f4 = (1.0f - (f3 / this.mClearDistance)) * this.mRippleRadius;
                RectF rectF3 = this.mRippleRectF;
                float f5 = f3 + rectF3.left;
                float f6 = f3 + rectF3.top;
                float width = rectF3.width() - this.mCurrentDistance;
                RectF rectF4 = this.mRippleRectF;
                canvas.drawRoundRect(f5, f6, width + rectF4.left, (rectF4.height() - this.mCurrentDistance) + this.mRippleRectF.top, f4, f4, this.mPaint);
                this.mPaint.setXfermode(null);
            }
            canvas.restoreToCount(saveLayer);
        }
    }

    public void onTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        if (action == 0) {
            pressDown(motionEvent.getX(), motionEvent.getY());
        } else if (action == 1 || action == 3) {
            pressUp();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void pressDown(float f, float f2) {
        RectF rectF;
        if (!isVisible() || (rectF = this.mRippleRectF) == null) {
            return;
        }
        this.mDownX = f;
        this.mDownY = f2;
        this.mNeedUpAnim = false;
        this.mIsAnimating = false;
        float f3 = this.mDownX - rectF.left;
        float f4 = this.mDownY - rectF.top;
        if (f3 < rectF.width() / 2.0f) {
            f3 = this.mRippleRectF.width() - f3;
        }
        if (f4 < this.mRippleRectF.height() / 2.0f) {
            f4 = this.mRippleRectF.height() - f4;
        }
        this.mMaxPressRadius = (float) Math.sqrt((f3 * f3) + (f4 * f4));
        if (this.mIsUpAnimating) {
            this.mIsUpAnimating = false;
            this.mAnimationUp.cancel();
        }
        this.mIsPressAnimating = true;
        if (this.mAnimationPress.isRunning()) {
            this.mAnimationPress.cancel();
        }
        this.mAnimationPress.start();
        this.mIsTouched = true;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void pressUp() {
        if (isVisible()) {
            this.mIsTouched = false;
            if (this.mIsPressAnimating) {
                this.mNeedUpAnim = true;
                return;
            }
            this.mAnimationUp.start();
            this.mIsUpAnimating = true;
        }
    }

    public void setAnimTransformListener(AnimTransformListener animTransformListener) {
        this.mAnimTransformListener = animTransformListener;
    }

    public void setCallback(Drawable.Callback callback) {
        this.mCallback = callback;
        getView(callback);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPressDuration(long j) {
        this.mAnimationPress.setDuration(j);
    }

    public void setPressInterpolator(Interpolator interpolator) {
        this.mAnimationPress.setInterpolator(interpolator);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRippleBackgroundColor(int i) {
        this.mBackgroundColor = i;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRippleColor(int i) {
        this.mRippleColor = i;
        this.mPaint.setColor(i);
        this.mRippleAlpha = this.mPaint.getAlpha();
    }

    public void setRippleRadius(float f) {
        this.mRippleRadius = f;
        resetPath();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setRippleRect(RectF rectF) {
        this.mRippleRectF = rectF;
        this.mClearDistance = (this.mRippleRectF.width() > this.mRippleRectF.height() ? this.mRippleRectF.height() : this.mRippleRectF.width()) / 2.0f;
        resetPath();
    }

    public void setSupportScale(boolean z) {
        this.mSupportScale = false;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setUpDuration(long j) {
        this.mAnimationUp.setDuration(j);
    }

    public void setUpInterpolator(Interpolator interpolator) {
        this.mAnimationUp.setInterpolator(interpolator);
    }

    @Deprecated
    public void setView(Drawable.Callback callback) {
        this.mCallback = callback;
        getView(callback);
    }

    public void setVisible(boolean z) {
        if (z) {
            View view = this.mView;
            if (view == null || !this.mSupportScale) {
                return;
            }
            view.setScaleX(1.0f);
            this.mView.setScaleY(1.0f);
            return;
        }
        this.mAnimationPress.cancel();
        this.mAnimationUp.cancel();
        this.mIsPressAnimating = false;
        this.mIsUpAnimating = false;
        this.mIsAnimating = false;
        this.mPressRadius = 0.0f;
        this.mCurrentDistance = 0.0f;
    }
}
