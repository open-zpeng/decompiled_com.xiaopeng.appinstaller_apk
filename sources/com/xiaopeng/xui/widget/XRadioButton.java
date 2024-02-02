package com.xiaopeng.xui.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.theme.XThemeManager;
/* loaded from: classes.dex */
public class XRadioButton extends XAppCompatRadioButton implements ValueAnimator.AnimatorUpdateListener {
    private static final int ANIMATION_ALPHA_OUT = 150;
    private static final int ANIMATION_DURATION = 450;
    private static final int ANIMATION_TO_CIRCLE = 100;
    private static final int ANIMATION_TO_CIRCLE_SMALL = 100;
    private static final int ANIMATION_TO_VERTICAL = 100;
    private static final float BLUR_ALPHA_DISABLE = 0.36f;
    private static final float BLUR_ALPHA_ENABLE = 1.0f;
    private static final String TAG = XRadioButton.class.getSimpleName();
    private final float DIMEN_16;
    private final float DIMEN_16_5;
    private final float DIMEN_18;
    private final float DIMEN_3;
    private final float DIMEN_5;
    private final float DIMEN_6;
    private final float DIMEN_8;
    private boolean isDetachedFromWindow;
    private boolean isDetachedNightTheme;
    private int mBgColor;
    private int mBgDisableColor;
    private int mBgEnableNormalColor;
    private int mBgEnablePressedColor;
    private float mBlurDstAlpha;
    private BlurMaskFilter mBlurMaskFilter;
    private float mCenterX;
    private float mCenterY;
    private int mCircleColor;
    private int mCircleDisableColor;
    private int mCircleEnableColor;
    private int mCircleLightColor;
    private float mCircleShadowRadius;
    private int mCurrentAnimValue;
    private float mHeight;
    private float mInnerRadius;
    private float mLeft;
    private BlurMaskFilter mSolidBlurMaskFilter;
    private float mTop;
    private ValueAnimator mValueAnimator;
    private float mWidth;
    private Xfermode mXFerModeAdd;

    public XRadioButton(Context context) {
        this(context, null);
    }

    public XRadioButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.DIMEN_18 = px(18.0f);
        this.DIMEN_16 = px(16.0f);
        this.DIMEN_16_5 = px(16.5f);
        this.DIMEN_3 = px(3.0f);
        this.DIMEN_5 = px(5.0f);
        this.DIMEN_6 = px(6.0f);
        this.DIMEN_8 = px(8.0f);
        this.isDetachedFromWindow = true;
        init();
    }

    public XRadioButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.DIMEN_18 = px(18.0f);
        this.DIMEN_16 = px(16.0f);
        this.DIMEN_16_5 = px(16.5f);
        this.DIMEN_3 = px(3.0f);
        this.DIMEN_5 = px(5.0f);
        this.DIMEN_6 = px(6.0f);
        this.DIMEN_8 = px(8.0f);
        this.isDetachedFromWindow = true;
        init();
    }

    private int changeColorAlpha(float f, int i) {
        return Color.argb((int) (f * 255.0f), Color.red(i), Color.green(i), Color.blue(i));
    }

    private void changeParams() {
        float f;
        if (isEnabled()) {
            this.mBgColor = isPressed() ? this.mBgEnablePressedColor : this.mBgEnableNormalColor;
            this.mCircleColor = this.mCircleEnableColor;
            f = 1.0f;
        } else {
            this.mBgColor = this.mBgDisableColor;
            this.mCircleColor = this.mCircleDisableColor;
            f = BLUR_ALPHA_DISABLE;
        }
        this.mBlurDstAlpha = f;
        this.mCircleShadowRadius = ThemeManager.isNightMode(getContext()) ? this.DIMEN_6 : 0.0f;
        updateInnerShape();
        updateLightAlpha();
        invalidate();
    }

    private int getColor(int i) {
        return getResources().getColor(i, getContext().getTheme());
    }

    private void init() {
        this.mXFerModeAdd = new PorterDuffXfermode(PorterDuff.Mode.ADD);
        this.mBlurMaskFilter = new BlurMaskFilter(this.DIMEN_8, BlurMaskFilter.Blur.NORMAL);
        this.mSolidBlurMaskFilter = new BlurMaskFilter(this.DIMEN_6, BlurMaskFilter.Blur.SOLID);
        this.mCenterX = getResources().getDimension(R.dimen.x_radio_button_center_hor);
        initColor();
        setButtonDrawable(R.drawable.x_radio_button_drawable);
        setBackgroundColor(getColor(17170445));
    }

    private void initColor() {
        this.isDetachedNightTheme = XThemeManager.isNight(getContext());
        this.mBgEnableNormalColor = getColor(R.color.x_theme_primary_neutral_normal);
        this.mBgEnablePressedColor = getColor(R.color.x_theme_primary_neutral_pressed);
        this.mBgDisableColor = getColor(R.color.x_theme_primary_neutral_disable);
        this.mCircleEnableColor = getColor(R.color.x_theme_primary_normal);
        this.mCircleDisableColor = getColor(R.color.x_radio_disable_color);
        changeParams();
    }

    private float px(float f) {
        return TypedValue.applyDimension(1, f, getResources().getDisplayMetrics());
    }

    private void resetAnimValue() {
        this.mCurrentAnimValue = isChecked() ? ANIMATION_DURATION : 0;
    }

    private void runAnimation(int i) {
        this.mValueAnimator = ValueAnimator.ofInt(this.mCurrentAnimValue, i);
        this.mValueAnimator.setDuration(Math.abs(i - this.mCurrentAnimValue));
        this.mValueAnimator.addUpdateListener(this);
        this.mValueAnimator.start();
    }

    private void stopAnimation() {
        ValueAnimator valueAnimator = this.mValueAnimator;
        if (valueAnimator == null || !valueAnimator.isRunning()) {
            return;
        }
        this.mValueAnimator.cancel();
    }

    private void updateInnerShape() {
        float f;
        int i = this.mCurrentAnimValue;
        if (i == 0) {
            float f2 = this.mCenterX;
            float f3 = this.DIMEN_16_5;
            this.mLeft = f2 - f3;
            this.mWidth = f3 * 2.0f;
            this.mTop = this.mCenterY;
            this.mHeight = 0.0f;
            this.mInnerRadius = 0.0f;
        } else if (i <= 100) {
            float f4 = i / 100.0f;
            float f5 = this.mCenterX;
            float f6 = this.DIMEN_16_5;
            float f7 = this.DIMEN_3;
            float f8 = f6 - f7;
            this.mLeft = (f5 - f6) + (f8 * f4);
            this.mWidth = (f6 * 2.0f) - ((f8 * 2.0f) * f4);
            float f9 = f7 * f4;
            this.mTop = this.mCenterY - f9;
            this.mHeight = this.DIMEN_6 * f4;
            this.mInnerRadius = f9;
        } else {
            if (i <= 200) {
                float f10 = (i - 100) / 100.0f;
                float f11 = this.mCenterX;
                f = this.DIMEN_3;
                this.mLeft = f11 - f;
                this.mWidth = 2.0f * f;
                this.mTop = (this.mCenterY - f) - (this.DIMEN_5 * f10);
                float f12 = this.DIMEN_6;
                this.mHeight = f12 + ((this.DIMEN_16 - f12) * f10);
            } else if (i > 300) {
                float f13 = this.mCenterX;
                float f14 = this.DIMEN_8;
                this.mLeft = f13 - f14;
                float f15 = this.DIMEN_16;
                this.mWidth = f15;
                this.mTop = this.mCenterY - f14;
                this.mHeight = f15;
                this.mInnerRadius = f14;
                return;
            } else {
                float f16 = ((i - 100) - 100) / 100.0f;
                float f17 = this.mCenterX;
                float f18 = this.DIMEN_3;
                float f19 = this.DIMEN_5;
                float f20 = f19 * f16;
                this.mLeft = (f17 - f18) - f20;
                this.mWidth = (f18 * 2.0f) + (f19 * 2.0f * f16);
                this.mTop = this.mCenterY - this.DIMEN_8;
                this.mHeight = this.DIMEN_16;
                f = f18 + f20;
            }
            this.mInnerRadius = f;
        }
    }

    private void updateLightAlpha() {
        float f;
        int i = this.mCurrentAnimValue;
        if (i <= 300) {
            f = (this.mBlurDstAlpha * i) / 300.0f;
        } else {
            float f2 = this.mBlurDstAlpha;
            f = f2 - (((((i - 100) - 100) - 100) * f2) / 150.0f);
        }
        this.mCircleLightColor = changeColorAlpha(f, this.mCircleColor);
    }

    @Override // android.animation.ValueAnimator.AnimatorUpdateListener
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        this.mCurrentAnimValue = ((Integer) valueAnimator.getAnimatedValue()).intValue();
        updateInnerShape();
        updateLightAlpha();
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XAppCompatRadioButton, android.widget.TextView, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (!this.isDetachedFromWindow || this.isDetachedNightTheme == XThemeManager.isNight(getContext())) {
            return;
        }
        this.isDetachedFromWindow = false;
        initColor();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XAppCompatRadioButton, android.widget.TextView, android.view.View
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (XThemeManager.isThemeChanged(configuration)) {
            initColor();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XAppCompatRadioButton, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.isDetachedFromWindow = true;
        this.isDetachedNightTheme = XThemeManager.isNight(getContext());
    }

    @Override // android.widget.CompoundButton, android.widget.TextView, android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        TextPaint paint = getLayout().getPaint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(this.mBgColor);
        canvas.drawCircle(this.mCenterX, this.mCenterY, this.DIMEN_18, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(this.DIMEN_3);
        paint.setColor(this.mCircleColor);
        int saveLayer = canvas.saveLayer(0.0f, 0.0f, canvas.getWidth(), canvas.getHeight(), paint);
        canvas.drawCircle(this.mCenterX, this.mCenterY, this.DIMEN_16_5, paint);
        paint.setColor(this.mCircleLightColor);
        paint.setMaskFilter(this.mBlurMaskFilter);
        paint.setXfermode(this.mXFerModeAdd);
        canvas.drawCircle(this.mCenterX, this.mCenterY, this.DIMEN_16_5, paint);
        canvas.restoreToCount(saveLayer);
        paint.setMaskFilter(null);
        paint.setXfermode(null);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(this.mCircleColor);
        if (this.mCircleShadowRadius > 0.0f) {
            paint.setMaskFilter(this.mSolidBlurMaskFilter);
        }
        int saveLayer2 = canvas.saveLayer(0.0f, 0.0f, canvas.getWidth(), canvas.getHeight(), paint);
        float f = this.mLeft;
        float f2 = this.mTop;
        float f3 = this.mInnerRadius;
        canvas.drawRoundRect(f, f2, f + this.mWidth, f2 + this.mHeight, f3, f3, paint);
        paint.setMaskFilter(null);
        paint.setColor(this.mCircleLightColor);
        paint.setMaskFilter(this.mBlurMaskFilter);
        paint.setXfermode(this.mXFerModeAdd);
        float f4 = this.mLeft;
        float f5 = this.mTop;
        float f6 = this.mInnerRadius;
        canvas.drawRoundRect(f4, f5, f4 + this.mWidth, f5 + this.mHeight, f6, f6, paint);
        canvas.restoreToCount(saveLayer2);
        paint.setMaskFilter(null);
        paint.setXfermode(null);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        super.onLayout(z, i, i2, i3, i4);
        this.mCenterY = getHeight() / 2.0f;
        updateInnerShape();
    }

    @Override // android.widget.TextView, android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
    }

    @Override // android.widget.CompoundButton, android.widget.Checkable
    public void setChecked(boolean z) {
        boolean isChecked = isChecked();
        super.setChecked(z);
        if (!isAttachedToWindow() || getWidth() == 0) {
            resetAnimValue();
            changeParams();
        } else if (isChecked != z) {
            stopAnimation();
            runAnimation(z ? ANIMATION_DURATION : 0);
        }
    }

    @Override // android.widget.TextView, android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        changeParams();
    }

    @Override // android.view.View
    public void setPressed(boolean z) {
        super.setPressed(z);
        changeParams();
    }
}
