package com.xiaopeng.xui.widget.slider;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.support.v4.content.ContextCompat;
import com.xiaopeng.xpui.R;
import xiaopeng.widget.BuildConfig;
/* loaded from: classes.dex */
public class IndicatorDrawable extends Drawable {
    private static float INDICATOR_TEXT_SIZE = 24.0f;
    private static int INDICATOR_TEXT_VERTICAL = 10;
    private static int MAX_INDICATOR_SIZE = 116;
    private static int MIN_INDICATOR_SIZE = 56;
    private static final int TEXT_PADDING = 50;
    private static int TEXT_PADDING_TOP = 42;
    NinePatchDrawable bgDay9;
    NinePatchDrawable bgDayDisable9;
    NinePatchDrawable bgNight9;
    NinePatchDrawable bgNightDisable9;
    private Rect bonds;
    private float indicatorCenter;
    private boolean isEnabled;
    private boolean isNight;
    private int slideWidth;
    private int textWidth;
    private final Paint textPaint = new Paint(1);
    private String indicatorText = BuildConfig.FLAVOR;

    public IndicatorDrawable(Context context) {
        float f = this.indicatorCenter;
        float f2 = MIN_INDICATOR_SIZE / 2;
        int i = INDICATOR_TEXT_VERTICAL;
        this.bonds = new Rect((int) (f - f2), i, (int) (f + f2), i + 50);
        this.textPaint.setTextSize(INDICATOR_TEXT_SIZE);
        this.textPaint.setStyle(Paint.Style.FILL);
        this.textPaint.setTextAlign(Paint.Align.CENTER);
        this.bgDay9 = (NinePatchDrawable) ContextCompat.getDrawable(context, R.drawable.x_ic_slider_tag_day);
        this.bgNight9 = (NinePatchDrawable) ContextCompat.getDrawable(context, R.drawable.x_ic_slider_tag_night);
        this.bgDayDisable9 = (NinePatchDrawable) ContextCompat.getDrawable(context, R.drawable.x_ic_slider_tag_day_disable);
        this.bgNightDisable9 = (NinePatchDrawable) ContextCompat.getDrawable(context, R.drawable.x_ic_slider_tag_night_disable);
    }

    private void resetBounds() {
        int max = Math.max(this.textWidth + 50, MIN_INDICATOR_SIZE);
        float f = this.indicatorCenter;
        float f2 = max / 2;
        float f3 = f - f2;
        int i = this.slideWidth;
        float f4 = (i - f) - f2;
        if (f3 < 0.0f) {
            Rect rect = this.bonds;
            rect.left = 0;
            rect.right = max;
        } else if (f4 < 0.0f) {
            Rect rect2 = this.bonds;
            rect2.left = i - max;
            rect2.right = i;
        } else {
            Rect rect3 = this.bonds;
            rect3.left = (int) f3;
            rect3.right = (int) (f + f2);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        NinePatchDrawable ninePatchDrawable;
        if (this.isNight) {
            if (this.isEnabled) {
                this.bgNight9.setBounds(this.bonds);
                ninePatchDrawable = this.bgNight9;
            } else {
                this.bgNightDisable9.setBounds(this.bonds);
                ninePatchDrawable = this.bgNightDisable9;
            }
        } else if (this.isEnabled) {
            this.bgDay9.setBounds(this.bonds);
            ninePatchDrawable = this.bgDay9;
        } else {
            this.bgDayDisable9.setBounds(this.bonds);
            ninePatchDrawable = this.bgDayDisable9;
        }
        ninePatchDrawable.draw(canvas);
        this.textPaint.setColor(this.isEnabled ? -1 : 1560281087);
        String str = this.indicatorText;
        Rect rect = this.bonds;
        canvas.drawText(str, (rect.left + rect.right) / 2, TEXT_PADDING_TOP, this.textPaint);
    }

    public void draw(Canvas canvas, boolean z, boolean z2) {
        this.isNight = z;
        this.isEnabled = z2;
        draw(canvas);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return 0;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    public void updateCenter(float f, String str, boolean z, int i) {
        this.isNight = z;
        this.indicatorText = str;
        this.indicatorCenter = f;
        this.textWidth = (int) this.textPaint.measureText(str);
        this.slideWidth = i;
        resetBounds();
        invalidateSelf();
    }
}
