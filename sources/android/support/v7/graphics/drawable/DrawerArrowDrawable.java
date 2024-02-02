package android.support.v7.graphics.drawable;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
/* loaded from: classes.dex */
public class DrawerArrowDrawable extends Drawable {
    private static final float ARROW_HEAD_ANGLE = (float) Math.toRadians(45.0d);
    private float mArrowHeadLength;
    private float mArrowShaftLength;
    private float mBarGap;
    private float mBarLength;
    private int mDirection;
    private float mMaxCutForBarSize;
    private final Paint mPaint;
    private final Path mPath;
    private float mProgress;
    private final int mSize;
    private boolean mSpin;
    private boolean mVerticalMirror;

    public void setVerticalMirror(boolean verticalMirror) {
        if (this.mVerticalMirror != verticalMirror) {
            this.mVerticalMirror = verticalMirror;
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        int i = this.mDirection;
        boolean flipToPointRight = false;
        if (i != 3) {
            switch (i) {
                case 0:
                    flipToPointRight = false;
                    break;
                case 1:
                    flipToPointRight = true;
                    break;
                default:
                    if (DrawableCompat.getLayoutDirection(this) == 1) {
                        flipToPointRight = true;
                        break;
                    }
                    break;
            }
        } else if (DrawableCompat.getLayoutDirection(this) == 0) {
            flipToPointRight = true;
        }
        boolean flipToPointRight2 = flipToPointRight;
        float arrowHeadBarLength = lerp(this.mBarLength, (float) Math.sqrt(this.mArrowHeadLength * this.mArrowHeadLength * 2.0f), this.mProgress);
        float arrowShaftLength = lerp(this.mBarLength, this.mArrowShaftLength, this.mProgress);
        float arrowShaftCut = Math.round(lerp(0.0f, this.mMaxCutForBarSize, this.mProgress));
        float rotation = lerp(0.0f, ARROW_HEAD_ANGLE, this.mProgress);
        float canvasRotate = lerp(flipToPointRight2 ? 0.0f : -180.0f, flipToPointRight2 ? 180.0f : 0.0f, this.mProgress);
        float arrowWidth = (float) Math.round(arrowHeadBarLength * Math.cos(rotation));
        float arrowHeight = (float) Math.round(arrowHeadBarLength * Math.sin(rotation));
        this.mPath.rewind();
        float topBottomBarOffset = lerp(this.mBarGap + this.mPaint.getStrokeWidth(), -this.mMaxCutForBarSize, this.mProgress);
        float arrowEdge = (-arrowShaftLength) / 2.0f;
        this.mPath.moveTo(arrowEdge + arrowShaftCut, 0.0f);
        this.mPath.rLineTo(arrowShaftLength - (arrowShaftCut * 2.0f), 0.0f);
        this.mPath.moveTo(arrowEdge, topBottomBarOffset);
        this.mPath.rLineTo(arrowWidth, arrowHeight);
        this.mPath.moveTo(arrowEdge, -topBottomBarOffset);
        this.mPath.rLineTo(arrowWidth, -arrowHeight);
        this.mPath.close();
        canvas.save();
        float barThickness = this.mPaint.getStrokeWidth();
        int remainingSpace = (int) ((bounds.height() - (3.0f * barThickness)) - (this.mBarGap * 2.0f));
        float yOffset = (remainingSpace / 4) * 2;
        canvas.translate(bounds.centerX(), yOffset + (1.5f * barThickness) + this.mBarGap);
        if (this.mSpin) {
            canvas.rotate((this.mVerticalMirror ^ flipToPointRight2 ? -1 : 1) * canvasRotate);
        } else if (flipToPointRight2) {
            canvas.rotate(180.0f);
        }
        canvas.drawPath(this.mPath, this.mPaint);
        canvas.restore();
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        if (alpha != this.mPaint.getAlpha()) {
            this.mPaint.setAlpha(alpha);
            invalidateSelf();
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
        invalidateSelf();
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mSize;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mSize;
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    public void setProgress(float progress) {
        if (this.mProgress != progress) {
            this.mProgress = progress;
            invalidateSelf();
        }
    }

    private static float lerp(float a2, float b2, float t) {
        return ((b2 - a2) * t) + a2;
    }
}
