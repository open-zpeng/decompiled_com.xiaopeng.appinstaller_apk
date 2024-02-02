package com.xiaopeng.xui.drawable.progress;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.graphics.XLightPaint;
import org.xmlpull.v1.XmlPullParser;
/* loaded from: classes.dex */
public class XCircularProgressPgDrawable extends Drawable {
    private static final int PROGRESS_COLOR_DEFAULT = Xui.getContext().getColor(R.color.colorPrimary);
    private static final float PROGRESS_STROKE_WIDTH_DEFAULT = 0.0f;
    private boolean mEnableLight;
    private float mInset;
    private float mLightRadius;
    private int mProgressColor;
    private float mStrokeWidth;
    protected Paint mPaint = new Paint();
    private Rect mOutBounds = getBounds();
    private RectF mInnerBounds = new RectF(getBounds());

    public XCircularProgressPgDrawable() {
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setDither(true);
        this.mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    private void updateAttr(Resources resources, AttributeSet attributeSet, Resources.Theme theme) {
        TypedArray obtainStyledAttributes = theme != null ? theme.obtainStyledAttributes(attributeSet, R.styleable.XCircularProgressPgDrawable, 0, 0) : resources.obtainAttributes(attributeSet, R.styleable.XCircularProgressPgDrawable);
        this.mProgressColor = obtainStyledAttributes.getColor(R.styleable.XCircularProgressPgDrawable_progress_color, PROGRESS_COLOR_DEFAULT);
        this.mStrokeWidth = obtainStyledAttributes.getDimension(R.styleable.XCircularProgressPgDrawable_progress_pg_strokeWidth, 0.0f);
        this.mEnableLight = obtainStyledAttributes.getBoolean(R.styleable.XCircularProgressPgDrawable_progress_enable_light, false);
        this.mInset = obtainStyledAttributes.getDimensionPixelSize(R.styleable.XCircularProgressPgDrawable_progress_pg_inset, 0);
        this.mLightRadius = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.XCircularProgressPgDrawable_progress_pg_light_radius, 0);
        obtainStyledAttributes.recycle();
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        canvas.save();
        canvas.drawArc(this.mInnerBounds, -90.0f, ((getLevel() * 1.0f) / 10000.0f) * 360.0f, false, this.mPaint);
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
        this.mInnerBounds.set(rect);
        if (this.mStrokeWidth == 0.0f) {
            this.mStrokeWidth = (this.mOutBounds.width() * 1.0f) / 10.0f;
        }
        RectF rectF = this.mInnerBounds;
        float f = this.mInset + (this.mStrokeWidth / 2.0f);
        rectF.inset(f, f);
        this.mPaint.setColor(this.mProgressColor);
        this.mPaint.setStrokeWidth(this.mStrokeWidth);
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
    protected boolean onLevelChange(int i) {
        invalidateSelf();
        return super.onLevelChange(i);
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
