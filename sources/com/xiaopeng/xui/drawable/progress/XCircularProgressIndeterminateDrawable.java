package com.xiaopeng.xui.drawable.progress;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.graphics.XLightPaint;
import org.xmlpull.v1.XmlPullParser;
/* loaded from: classes.dex */
public class XCircularProgressIndeterminateDrawable extends Drawable {
    private static final float ARC_START_ANGLE = 8.0f;
    private static final float ARC_SWEEP_ANGLE = 340.0f;
    private static final float STROKE_WIDTH_DEFAULT = 0.0f;
    private boolean mEnableLight;
    private int mEndColor;
    private float mInset;
    private float mLightRadius;
    private int mStartColor;
    private float mStrokeWidth;
    protected Paint mPaint = new Paint();
    private Rect mBounds = getBounds();
    private RectF mInnerBounds = new RectF(getBounds());

    public XCircularProgressIndeterminateDrawable() {
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setDither(true);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
        this.mStartColor = Xui.getContext().getColor(17170445);
        this.mEndColor = Xui.getContext().getColor(R.color.colorPrimary);
    }

    private void updateAttr(Resources resources, AttributeSet attributeSet, Resources.Theme theme) {
        TypedArray obtainStyledAttributes = theme != null ? theme.obtainStyledAttributes(attributeSet, R.styleable.XCircularProgressIndeterminateDrawable, 0, 0) : resources.obtainAttributes(attributeSet, R.styleable.XCircularProgressIndeterminateDrawable);
        this.mStartColor = obtainStyledAttributes.getColor(R.styleable.XCircularProgressIndeterminateDrawable_progress_indeterminate_startColor, this.mStartColor);
        this.mEndColor = obtainStyledAttributes.getColor(R.styleable.XCircularProgressIndeterminateDrawable_progress_indeterminate_endColor, this.mEndColor);
        this.mStrokeWidth = obtainStyledAttributes.getDimension(R.styleable.XCircularProgressIndeterminateDrawable_progress_indeterminate_strokeWidth, 0.0f);
        this.mEnableLight = obtainStyledAttributes.getBoolean(R.styleable.XCircularProgressIndeterminateDrawable_progress_indeterminate_enable_light, false);
        this.mInset = obtainStyledAttributes.getDimensionPixelSize(R.styleable.XCircularProgressIndeterminateDrawable_progress_indeterminate_inset, 0);
        this.mLightRadius = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.XCircularProgressIndeterminateDrawable_progress_indeterminate_light_radius, 0);
        obtainStyledAttributes.recycle();
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.drawArc(this.mInnerBounds, ARC_START_ANGLE, ARC_SWEEP_ANGLE, false, this.mPaint);
        canvas.restore();
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet) {
        super.inflate(resources, xmlPullParser, attributeSet);
        updateAttr(resources, attributeSet, null);
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) {
        super.inflate(resources, xmlPullParser, attributeSet, theme);
        updateAttr(resources, attributeSet, theme);
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        this.mPaint.setShader(new SweepGradient(this.mBounds.centerX(), this.mBounds.centerY(), new int[]{this.mStartColor, this.mEndColor}, (float[]) null));
        if (this.mStrokeWidth == 0.0f) {
            this.mStrokeWidth = this.mBounds.width() / 10.0f;
        }
        this.mPaint.setStrokeWidth(this.mStrokeWidth);
        this.mInnerBounds.set(rect);
        RectF rectF = this.mInnerBounds;
        float f = this.mInset + (this.mStrokeWidth / 2.0f);
        rectF.inset(f, f);
        if (this.mEnableLight) {
            if (this.mLightRadius == 0.0f) {
                this.mLightRadius = this.mStrokeWidth;
            }
            XLightPaint xLightPaint = new XLightPaint(this.mPaint);
            xLightPaint.setLightRadius(this.mLightRadius);
            xLightPaint.apply();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.mPaint.setAlpha(i);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
    }

    public void setInset(float f) {
        this.mInset = f;
    }

    public void setLightRadius(float f) {
        this.mLightRadius = f;
    }

    public void setStrokeWidth(float f) {
        this.mStrokeWidth = f;
    }
}
