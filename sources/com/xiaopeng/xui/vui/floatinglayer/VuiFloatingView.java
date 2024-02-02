package com.xiaopeng.xui.vui.floatinglayer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Animatable2;
import android.graphics.drawable.AnimatedImageDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.xpui.R;
@SuppressLint({"ViewConstructor"})
/* loaded from: classes.dex */
public class VuiFloatingView extends FrameLayout {
    private static final String TAG = "VuiFloatingView";
    private AlphaAnimation mAlphaAnimation;
    private AnimatedImageDrawable mAnimatedImageDrawable;
    private Animatable2.AnimationCallback mAnimationCallback;
    private Context mContext;
    private Drawable mDrawable;
    private ImageView mFloatingView;
    private boolean mIsNight;
    private boolean mNeedReLoadDrawable;
    private OnTouchListener mOnTouchListener;
    private int mType;

    /* loaded from: classes.dex */
    public interface OnTouchListener {
        void onTouch(int i);
    }

    public VuiFloatingView(Context context, int i) {
        super(context);
        this.mNeedReLoadDrawable = true;
        this.mContext = context;
        this.mType = i;
        this.mFloatingView = (ImageView) LayoutInflater.from(this.mContext).inflate(R.layout.vui_floating, this).findViewById(R.id.floating_view);
        if (VuiImageDecoderUtils.isSupportAlpha(i)) {
            this.mAlphaAnimation = new AlphaAnimation(0.0f, 1.0f);
            this.mAlphaAnimation.setDuration(600L);
        }
    }

    private void checkAnimatorDrawable() {
        if (VuiImageDecoderUtils.isSupportNight(this.mType)) {
            boolean isNightMode = ThemeManager.isNightMode(getContext());
            if (this.mIsNight == isNightMode && !this.mNeedReLoadDrawable) {
                return;
            }
            refreshAnimatorDrawable(VuiImageDecoderUtils.decoderImage(this.mContext, this.mType, isNightMode));
            Log.d(TAG, "checkAnimatorDrawable isNight " + isNightMode + " ,mNeedReLoadDrawable " + this.mNeedReLoadDrawable);
            this.mIsNight = isNightMode;
        } else if (!this.mNeedReLoadDrawable) {
            return;
        } else {
            refreshAnimatorDrawable(VuiImageDecoderUtils.decoderImage(this.mContext, this.mType, false));
            Log.d(TAG, "checkAnimatorDrawable mNeedReLoadDrawable " + this.mNeedReLoadDrawable);
        }
        this.mNeedReLoadDrawable = false;
    }

    @SuppressLint({"NewApi"})
    private void refreshAnimatorDrawable(Drawable drawable) {
        if (drawable == null) {
            Log.e(TAG, "refreshAnimatorDrawable drawable is null");
            return;
        }
        this.mFloatingView.setImageDrawable(drawable);
        this.mDrawable = drawable;
        if (!(drawable instanceof AnimatedImageDrawable)) {
            Log.e(TAG, "refreshAnimatorDrawable drawable is not AnimatedImageDrawable");
            return;
        }
        this.mAnimatedImageDrawable = (AnimatedImageDrawable) drawable;
        this.mAnimatedImageDrawable.setRepeatCount(0);
    }

    public void destroy() {
        AnimatedImageDrawable animatedImageDrawable = this.mAnimatedImageDrawable;
        if (animatedImageDrawable != null) {
            animatedImageDrawable.unregisterAnimationCallback(this.mAnimationCallback);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        OnTouchListener onTouchListener;
        if ((motionEvent.getAction() == 0 || motionEvent.getAction() == 4) && (onTouchListener = this.mOnTouchListener) != null) {
            onTouchListener.onTouch(this.mType);
            this.mNeedReLoadDrawable = true;
        }
        return true;
    }

    public int getVisibleHeight() {
        AnimatedImageDrawable animatedImageDrawable = this.mAnimatedImageDrawable;
        if (animatedImageDrawable != null) {
            return animatedImageDrawable.getIntrinsicHeight();
        }
        Drawable drawable = this.mDrawable;
        if (drawable != null) {
            return drawable.getIntrinsicHeight();
        }
        return 0;
    }

    public int getVisibleWidth() {
        AnimatedImageDrawable animatedImageDrawable = this.mAnimatedImageDrawable;
        if (animatedImageDrawable != null) {
            return animatedImageDrawable.getIntrinsicWidth();
        }
        Drawable drawable = this.mDrawable;
        if (drawable != null) {
            return drawable.getIntrinsicWidth();
        }
        return 0;
    }

    public void prepare() {
        checkAnimatorDrawable();
    }

    public void registerAnimationCallback(Animatable2.AnimationCallback animationCallback) {
        AnimatedImageDrawable animatedImageDrawable = this.mAnimatedImageDrawable;
        if (animatedImageDrawable != null) {
            animatedImageDrawable.unregisterAnimationCallback(this.mAnimationCallback);
            this.mAnimationCallback = animationCallback;
            this.mAnimatedImageDrawable.registerAnimationCallback(this.mAnimationCallback);
        }
    }

    public void requestNeedReLoadDrawable() {
        this.mNeedReLoadDrawable = true;
    }

    @SuppressLint({"NewApi"})
    public void reset() {
        AnimatedImageDrawable animatedImageDrawable = this.mAnimatedImageDrawable;
        if (animatedImageDrawable != null) {
            this.mFloatingView.setImageDrawable(animatedImageDrawable);
        }
    }

    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this.mOnTouchListener = onTouchListener;
    }

    public void start() {
        if (this.mAnimatedImageDrawable != null) {
            Log.v(TAG, " isRunning " + this.mAnimatedImageDrawable.isRunning());
            if (!this.mAnimatedImageDrawable.isRunning()) {
                this.mAnimatedImageDrawable.start();
            }
            AlphaAnimation alphaAnimation = this.mAlphaAnimation;
            if (alphaAnimation != null) {
                this.mFloatingView.startAnimation(alphaAnimation);
            }
        }
    }

    public void stop() {
        AnimatedImageDrawable animatedImageDrawable = this.mAnimatedImageDrawable;
        if (animatedImageDrawable != null && animatedImageDrawable.isRunning()) {
            this.mAnimatedImageDrawable.stop();
        }
        AlphaAnimation alphaAnimation = this.mAlphaAnimation;
        if (alphaAnimation != null) {
            alphaAnimation.cancel();
        }
    }

    public void unRegisterAnimationCallback(Animatable2.AnimationCallback animationCallback) {
        AnimatedImageDrawable animatedImageDrawable = this.mAnimatedImageDrawable;
        if (animatedImageDrawable != null) {
            animatedImageDrawable.unregisterAnimationCallback(animationCallback);
        }
    }
}
