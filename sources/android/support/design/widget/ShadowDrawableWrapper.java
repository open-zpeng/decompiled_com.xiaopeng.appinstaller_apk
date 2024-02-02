package android.support.design.widget;

import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.v7.graphics.drawable.DrawableWrapper;
/* loaded from: classes.dex */
class ShadowDrawableWrapper extends DrawableWrapper {
    static final double COS_45 = Math.cos(Math.toRadians(45.0d));
    private boolean addPaddingForCorners;
    final RectF contentBounds;
    float cornerRadius;
    final Paint cornerShadowPaint;
    Path cornerShadowPath;
    private boolean dirty;
    final Paint edgeShadowPaint;
    float maxShadowSize;
    private boolean printedShadowClipWarning;
    float rawMaxShadowSize;
    float rawShadowSize;
    private float rotation;
    private final int shadowEndColor;
    private final int shadowMiddleColor;
    float shadowSize;
    private final int shadowStartColor;

    private static int toEven(float value) {
        int i = Math.round(value);
        return i % 2 == 1 ? i - 1 : i;
    }

    @Override // android.support.v7.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
        super.setAlpha(alpha);
        this.cornerShadowPaint.setAlpha(alpha);
        this.edgeShadowPaint.setAlpha(alpha);
    }

    @Override // android.support.v7.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect bounds) {
        this.dirty = true;
    }

    void setShadowSize(float shadowSize, float maxShadowSize) {
        if (shadowSize < 0.0f || maxShadowSize < 0.0f) {
            throw new IllegalArgumentException("invalid shadow size");
        }
        float shadowSize2 = toEven(shadowSize);
        float maxShadowSize2 = toEven(maxShadowSize);
        if (shadowSize2 > maxShadowSize2) {
            shadowSize2 = maxShadowSize2;
            if (!this.printedShadowClipWarning) {
                this.printedShadowClipWarning = true;
            }
        }
        if (this.rawShadowSize == shadowSize2 && this.rawMaxShadowSize == maxShadowSize2) {
            return;
        }
        this.rawShadowSize = shadowSize2;
        this.rawMaxShadowSize = maxShadowSize2;
        this.shadowSize = Math.round(1.5f * shadowSize2);
        this.maxShadowSize = maxShadowSize2;
        this.dirty = true;
        invalidateSelf();
    }

    @Override // android.support.v7.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public boolean getPadding(Rect padding) {
        int vOffset = (int) Math.ceil(calculateVerticalPadding(this.rawMaxShadowSize, this.cornerRadius, this.addPaddingForCorners));
        int hOffset = (int) Math.ceil(calculateHorizontalPadding(this.rawMaxShadowSize, this.cornerRadius, this.addPaddingForCorners));
        padding.set(hOffset, vOffset, hOffset, vOffset);
        return true;
    }

    public static float calculateVerticalPadding(float maxShadowSize, float cornerRadius, boolean addPaddingForCorners) {
        if (addPaddingForCorners) {
            return (float) ((1.5f * maxShadowSize) + ((1.0d - COS_45) * cornerRadius));
        }
        return 1.5f * maxShadowSize;
    }

    public static float calculateHorizontalPadding(float maxShadowSize, float cornerRadius, boolean addPaddingForCorners) {
        if (addPaddingForCorners) {
            return (float) (maxShadowSize + ((1.0d - COS_45) * cornerRadius));
        }
        return maxShadowSize;
    }

    @Override // android.support.v7.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public int getOpacity() {
        return -3;
    }

    @Override // android.support.v7.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (this.dirty) {
            buildComponents(getBounds());
            this.dirty = false;
        }
        drawShadow(canvas);
        super.draw(canvas);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void setRotation(float rotation) {
        if (this.rotation != rotation) {
            this.rotation = rotation;
            invalidateSelf();
        }
    }

    private void drawShadow(Canvas canvas) {
        float shadowScaleBottom;
        float shadowScaleTop;
        float shadowScaleHorizontal;
        int saved;
        float shadowOffsetHorizontal;
        float shadowScaleHorizontal2;
        int rotateSaved = canvas.save();
        canvas.rotate(this.rotation, this.contentBounds.centerX(), this.contentBounds.centerY());
        float edgeShadowTop = (-this.cornerRadius) - this.shadowSize;
        float shadowOffset = this.cornerRadius;
        boolean drawHorizontalEdges = this.contentBounds.width() - (2.0f * shadowOffset) > 0.0f;
        boolean drawVerticalEdges = this.contentBounds.height() - (2.0f * shadowOffset) > 0.0f;
        float shadowOffsetTop = this.rawShadowSize - (this.rawShadowSize * 0.25f);
        float shadowOffsetHorizontal2 = this.rawShadowSize - (this.rawShadowSize * 0.5f);
        float shadowOffsetBottom = this.rawShadowSize - (this.rawShadowSize * 1.0f);
        float shadowScaleHorizontal3 = shadowOffset / (shadowOffset + shadowOffsetHorizontal2);
        float shadowScaleTop2 = shadowOffset / (shadowOffset + shadowOffsetTop);
        float shadowScaleBottom2 = shadowOffset / (shadowOffset + shadowOffsetBottom);
        int saved2 = canvas.save();
        canvas.translate(this.contentBounds.left + shadowOffset, this.contentBounds.top + shadowOffset);
        canvas.scale(shadowScaleHorizontal3, shadowScaleTop2);
        canvas.drawPath(this.cornerShadowPath, this.cornerShadowPaint);
        if (drawHorizontalEdges) {
            canvas.scale(1.0f / shadowScaleHorizontal3, 1.0f);
            saved = saved2;
            shadowScaleBottom = shadowScaleBottom2;
            shadowScaleTop = shadowScaleTop2;
            shadowScaleHorizontal = shadowScaleHorizontal3;
            shadowOffsetHorizontal = 1.0f;
            canvas.drawRect(0.0f, edgeShadowTop, this.contentBounds.width() - (2.0f * shadowOffset), -this.cornerRadius, this.edgeShadowPaint);
        } else {
            shadowScaleBottom = shadowScaleBottom2;
            shadowScaleTop = shadowScaleTop2;
            shadowScaleHorizontal = shadowScaleHorizontal3;
            saved = saved2;
            shadowOffsetHorizontal = 1.0f;
        }
        canvas.restoreToCount(saved);
        int saved3 = canvas.save();
        canvas.translate(this.contentBounds.right - shadowOffset, this.contentBounds.bottom - shadowOffset);
        float shadowScaleHorizontal4 = shadowScaleHorizontal;
        canvas.scale(shadowScaleHorizontal4, shadowScaleBottom);
        canvas.rotate(180.0f);
        canvas.drawPath(this.cornerShadowPath, this.cornerShadowPaint);
        if (drawHorizontalEdges) {
            canvas.scale(shadowOffsetHorizontal / shadowScaleHorizontal4, shadowOffsetHorizontal);
            shadowScaleHorizontal2 = shadowScaleHorizontal4;
            canvas.drawRect(0.0f, edgeShadowTop, this.contentBounds.width() - (2.0f * shadowOffset), (-this.cornerRadius) + this.shadowSize, this.edgeShadowPaint);
        } else {
            shadowScaleHorizontal2 = shadowScaleHorizontal4;
        }
        canvas.restoreToCount(saved3);
        int saved4 = canvas.save();
        canvas.translate(this.contentBounds.left + shadowOffset, this.contentBounds.bottom - shadowOffset);
        canvas.scale(shadowScaleHorizontal2, shadowScaleBottom);
        canvas.rotate(270.0f);
        canvas.drawPath(this.cornerShadowPath, this.cornerShadowPaint);
        if (drawVerticalEdges) {
            canvas.scale(1.0f / shadowScaleBottom, 1.0f);
            canvas.drawRect(0.0f, edgeShadowTop, this.contentBounds.height() - (2.0f * shadowOffset), -this.cornerRadius, this.edgeShadowPaint);
        }
        canvas.restoreToCount(saved4);
        int saved5 = canvas.save();
        canvas.translate(this.contentBounds.right - shadowOffset, this.contentBounds.top + shadowOffset);
        float shadowScaleTop3 = shadowScaleTop;
        canvas.scale(shadowScaleHorizontal2, shadowScaleTop3);
        canvas.rotate(90.0f);
        canvas.drawPath(this.cornerShadowPath, this.cornerShadowPaint);
        if (drawVerticalEdges) {
            canvas.scale(1.0f / shadowScaleTop3, 1.0f);
            canvas.drawRect(0.0f, edgeShadowTop, this.contentBounds.height() - (2.0f * shadowOffset), -this.cornerRadius, this.edgeShadowPaint);
        }
        canvas.restoreToCount(saved5);
        canvas.restoreToCount(rotateSaved);
    }

    private void buildShadowCorners() {
        int i;
        RectF innerBounds = new RectF(-this.cornerRadius, -this.cornerRadius, this.cornerRadius, this.cornerRadius);
        RectF outerBounds = new RectF(innerBounds);
        outerBounds.inset(-this.shadowSize, -this.shadowSize);
        if (this.cornerShadowPath == null) {
            this.cornerShadowPath = new Path();
        } else {
            this.cornerShadowPath.reset();
        }
        this.cornerShadowPath.setFillType(Path.FillType.EVEN_ODD);
        this.cornerShadowPath.moveTo(-this.cornerRadius, 0.0f);
        this.cornerShadowPath.rLineTo(-this.shadowSize, 0.0f);
        this.cornerShadowPath.arcTo(outerBounds, 180.0f, 90.0f, false);
        this.cornerShadowPath.arcTo(innerBounds, 270.0f, -90.0f, false);
        this.cornerShadowPath.close();
        float shadowRadius = -outerBounds.top;
        if (shadowRadius > 0.0f) {
            float startRatio = this.cornerRadius / shadowRadius;
            float midRatio = startRatio + ((1.0f - startRatio) / 2.0f);
            i = 3;
            this.cornerShadowPaint.setShader(new RadialGradient(0.0f, 0.0f, shadowRadius, new int[]{0, this.shadowStartColor, this.shadowMiddleColor, this.shadowEndColor}, new float[]{0.0f, startRatio, midRatio, 1.0f}, Shader.TileMode.CLAMP));
        } else {
            i = 3;
        }
        Paint paint = this.edgeShadowPaint;
        float f = innerBounds.top;
        float f2 = outerBounds.top;
        int[] iArr = new int[i];
        iArr[0] = this.shadowStartColor;
        iArr[1] = this.shadowMiddleColor;
        iArr[2] = this.shadowEndColor;
        float[] fArr = new float[i];
        // fill-array-data instruction
        fArr[0] = 0.0f;
        fArr[1] = 0.5f;
        fArr[2] = 1.0f;
        paint.setShader(new LinearGradient(0.0f, f, 0.0f, f2, iArr, fArr, Shader.TileMode.CLAMP));
        this.edgeShadowPaint.setAntiAlias(false);
    }

    private void buildComponents(Rect bounds) {
        float verticalOffset = this.rawMaxShadowSize * 1.5f;
        this.contentBounds.set(bounds.left + this.rawMaxShadowSize, bounds.top + verticalOffset, bounds.right - this.rawMaxShadowSize, bounds.bottom - verticalOffset);
        getWrappedDrawable().setBounds((int) this.contentBounds.left, (int) this.contentBounds.top, (int) this.contentBounds.right, (int) this.contentBounds.bottom);
        buildShadowCorners();
    }

    public void setShadowSize(float size) {
        setShadowSize(size, this.rawMaxShadowSize);
    }

    public float getShadowSize() {
        return this.rawShadowSize;
    }
}
