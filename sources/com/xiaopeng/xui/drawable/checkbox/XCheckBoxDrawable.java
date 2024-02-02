package com.xiaopeng.xui.drawable.checkbox;

import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.MaskFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.StateSet;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.theme.XThemeManager;
import org.xmlpull.v1.XmlPullParser;
/* loaded from: classes.dex */
public class XCheckBoxDrawable extends Drawable {
    private static final int[] CHECKED_STATE_SET = {16842912};
    private MaskFilter mBlurMaskFilter;
    private ColorStateList mColorBg;
    private ColorStateList mColorFront;
    private float mDrawablePadding;
    private float mFrameWidth;
    private Path mNikePath;
    private Paint mPaint;

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        this.mPaint.setStyle(Paint.Style.FILL);
        this.mPaint.setColor(this.mColorBg.getColorForState(getState(), R.color.x_check_box_inner_color));
        float f = this.mDrawablePadding;
        float f2 = f + this.mFrameWidth;
        canvas.drawRect(f, f, f2, f2, this.mPaint);
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setStrokeWidth(3.0f);
        this.mPaint.setColor(this.mColorFront.getColorForState(getState(), R.color.x_check_box_frame_color));
        float f3 = this.mDrawablePadding;
        float f4 = f3 + this.mFrameWidth;
        canvas.drawRoundRect(f3, f3, f4, f4, 3.0f, 3.0f, this.mPaint);
        if (StateSet.stateSetMatches(CHECKED_STATE_SET, getState())) {
            if (XThemeManager.isNight(Xui.getContext())) {
                this.mPaint.setMaskFilter(this.mBlurMaskFilter);
            }
            this.mPaint.setStrokeWidth(4.0f);
            canvas.drawPath(this.mNikePath, this.mPaint);
            this.mPaint.setMaskFilter(null);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return 0;
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) {
        super.inflate(resources, xmlPullParser, attributeSet, theme);
        this.mPaint = new Paint(1);
        this.mColorBg = resources.getColorStateList(R.color.x_check_box_inner_color, theme);
        this.mColorFront = resources.getColorStateList(R.color.x_check_box_frame_color, theme);
        this.mDrawablePadding = 28.5f;
        this.mFrameWidth = 33.0f;
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mPaint.setStrokeJoin(Paint.Join.ROUND);
        this.mNikePath = new Path();
        this.mNikePath.moveTo(36.0f, 46.0f);
        this.mNikePath.lineTo(41.0f, 52.0f);
        this.mNikePath.lineTo(54.0f, 40.0f);
        this.mBlurMaskFilter = new BlurMaskFilter(6.0f, BlurMaskFilter.Blur.SOLID);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] iArr) {
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }
}
