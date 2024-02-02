package com.xiaopeng.xui.drawable;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.graphics.XLightPaint;
import org.xmlpull.v1.XmlPullParser;
/* loaded from: classes.dex */
public class XToggleDrawable extends Drawable {
    private static final int BRIGHTNESS_DEFAULT = 0;
    private static final float LIGHT_STRENGTH_DEFAULT = 0.5f;
    private static final int TOGGLE_STYLE_DEFAULT = 0;
    private XLightPaint mXLightPaint;
    private static final int COLOR_DEFAULT = getColor(R.color.x_toggle_default_color);
    private static final float STROKE_WIDTH_DEFAULT = getDimen(R.dimen.x_toggle_stroke_size);
    private static final float LIGHT_RADIUS_DEFAULT = getDimen(R.dimen.x_toggle_light_radius);
    private static final float RECT_ROUND_RADIUS = getDimen(R.dimen.x_toggle_rect_round_radius);
    private Paint mPaint = new Paint(1);
    private boolean mChecked = false;

    private static int getColor(int i) {
        return Xui.getContext().getResources().getColor(i);
    }

    private static float getDimen(int i) {
        return Xui.getContext().getResources().getDimension(i);
    }

    /* JADX WARN: Removed duplicated region for block: B:14:0x0071  */
    /* JADX WARN: Removed duplicated region for block: B:16:? A[RETURN, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void updateAttr(android.content.res.Resources r9, android.util.AttributeSet r10, android.content.res.Resources.Theme r11) {
        /*
            r8 = this;
            r0 = 0
            if (r11 == 0) goto La
            int[] r9 = com.xiaopeng.xpui.R.styleable.XToggleDrawable
            android.content.res.TypedArray r9 = r11.obtainStyledAttributes(r10, r9, r0, r0)
            goto L10
        La:
            int[] r11 = com.xiaopeng.xpui.R.styleable.XToggleDrawable
            android.content.res.TypedArray r9 = r9.obtainAttributes(r10, r11)
        L10:
            int r10 = com.xiaopeng.xpui.R.styleable.XToggleDrawable_toggle_stroke_color
            int r11 = com.xiaopeng.xui.drawable.XToggleDrawable.COLOR_DEFAULT
            int r10 = r9.getColor(r10, r11)
            int r11 = com.xiaopeng.xpui.R.styleable.XToggleDrawable_toggle_stroke_width
            float r1 = com.xiaopeng.xui.drawable.XToggleDrawable.STROKE_WIDTH_DEFAULT
            float r11 = r9.getDimension(r11, r1)
            int r1 = com.xiaopeng.xpui.R.styleable.XToggleDrawable_toggle_enable_light
            boolean r1 = r9.getBoolean(r1, r0)
            int r2 = com.xiaopeng.xpui.R.styleable.XToggleDrawable_toggle_light_radius
            float r3 = com.xiaopeng.xui.drawable.XToggleDrawable.LIGHT_RADIUS_DEFAULT
            float r2 = r9.getDimension(r2, r3)
            int r3 = com.xiaopeng.xpui.R.styleable.XToggleDrawable_toggle_brightness
            int r3 = r9.getInt(r3, r0)
            int r4 = com.xiaopeng.xpui.R.styleable.XToggleDrawable_toggle_light_strength
            r5 = 1056964608(0x3f000000, float:0.5)
            float r4 = r9.getFloat(r4, r5)
            int r5 = com.xiaopeng.xpui.R.styleable.XToggleDrawable_toggle_light_type
            int r5 = r9.getInt(r5, r0)
            int r6 = com.xiaopeng.xpui.R.styleable.XToggleDrawable_toggle_light_color
            int r7 = com.xiaopeng.xui.drawable.XToggleDrawable.COLOR_DEFAULT
            int r6 = r9.getColor(r6, r7)
            int r7 = com.xiaopeng.xpui.R.styleable.XToggleDrawable_toggle_style
            int r0 = r9.getInt(r7, r0)
            r9.recycle()
            android.graphics.Paint r9 = r8.mPaint
            r9.setColor(r10)
            android.graphics.Paint r9 = r8.mPaint
            r9.setStrokeWidth(r11)
            if (r0 != 0) goto L67
            android.graphics.Paint r9 = r8.mPaint
            android.graphics.Paint$Style r10 = android.graphics.Paint.Style.STROKE
        L63:
            r9.setStyle(r10)
            goto L6f
        L67:
            r9 = 1
            if (r0 != r9) goto L6f
            android.graphics.Paint r9 = r8.mPaint
            android.graphics.Paint$Style r10 = android.graphics.Paint.Style.FILL
            goto L63
        L6f:
            if (r1 == 0) goto L98
            com.xiaopeng.xui.graphics.XLightPaint r9 = new com.xiaopeng.xui.graphics.XLightPaint
            android.graphics.Paint r10 = r8.mPaint
            r9.<init>(r10)
            r8.mXLightPaint = r9
            com.xiaopeng.xui.graphics.XLightPaint r9 = r8.mXLightPaint
            r9.setLightRadius(r2)
            com.xiaopeng.xui.graphics.XLightPaint r9 = r8.mXLightPaint
            r9.setBrightness(r3)
            com.xiaopeng.xui.graphics.XLightPaint r9 = r8.mXLightPaint
            r9.setLightColor(r6)
            com.xiaopeng.xui.graphics.XLightPaint r9 = r8.mXLightPaint
            r9.setLightStrength(r4)
            com.xiaopeng.xui.graphics.XLightPaint r9 = r8.mXLightPaint
            r9.setLightType(r5)
            com.xiaopeng.xui.graphics.XLightPaint r9 = r8.mXLightPaint
            r9.apply()
        L98:
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.xui.drawable.XToggleDrawable.updateAttr(android.content.res.Resources, android.util.AttributeSet, android.content.res.Resources$Theme):void");
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        if (this.mChecked) {
            int save = canvas.save();
            Rect bounds = getBounds();
            float f = RECT_ROUND_RADIUS;
            canvas.drawRoundRect(bounds.left, bounds.top, bounds.right, bounds.bottom, f, f, this.mPaint);
            canvas.restoreToCount(save);
        }
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
    public boolean isStateful() {
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] iArr) {
        boolean z;
        int length = iArr.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                z = false;
                break;
            } else if (iArr[i] == 16842912) {
                z = true;
                break;
            } else {
                i++;
            }
        }
        boolean z2 = this.mChecked != z;
        this.mChecked = z;
        return z2;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.mPaint.setAlpha(i);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }
}
