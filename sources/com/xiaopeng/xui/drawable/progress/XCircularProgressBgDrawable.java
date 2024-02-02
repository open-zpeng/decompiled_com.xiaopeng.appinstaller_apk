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
import org.xmlpull.v1.XmlPullParser;
/* loaded from: classes.dex */
public class XCircularProgressBgDrawable extends Drawable {
    private static final int PROGRESS_BACKGROUND_COLOR_DEFAULT = -1;
    private static final float STROKE_WIDTH_DEFAULT = 0.0f;
    private Drawable mIndicatorPause;
    private Drawable mIndicatorPlay;
    private Drawable mIndicatorStart;
    private Drawable mIndicatorStop;
    private float mInset;
    private float mStrokeWidth;
    private Rect mTmpIndicatorBounds = new Rect();
    private Paint mPaint = new Paint();
    private Rect mOutBounds = getBounds();
    private RectF mInnerBounds = new RectF(getBounds());
    private boolean mShowIndicator = true;
    private boolean mPlayingStop = false;
    private boolean mPlayingPause = false;
    private boolean mPaused = false;
    private boolean mStartDownload = false;
    private int mProgressBgColor = Xui.getContext().getColor(R.color.x_circular_progress_circle_bg_color);

    public XCircularProgressBgDrawable() {
        this.mPaint.setStyle(Paint.Style.STROKE);
        this.mPaint.setAntiAlias(true);
        this.mPaint.setDither(true);
        this.mPaint.setColor(this.mProgressBgColor);
    }

    private void applyIndicatorBounds(Drawable drawable, Rect rect) {
        if (drawable == null || rect == null) {
            return;
        }
        Rect bounds = getBounds();
        float width = bounds.width();
        float height = bounds.height();
        float intrinsicWidth = drawable.getIntrinsicWidth();
        float intrinsicHeight = drawable.getIntrinsicHeight();
        if (intrinsicWidth >= width || intrinsicHeight >= height) {
            rect.set(bounds);
        } else {
            int round = Math.round(bounds.left + ((width - intrinsicWidth) / 2.0f));
            int round2 = Math.round(bounds.top + ((height - intrinsicHeight) / 2.0f));
            rect.set(round, round2, Math.round(intrinsicWidth) + round, Math.round(intrinsicHeight) + round2);
        }
        drawable.setBounds(rect);
    }

    private void updateAttr(Resources resources, AttributeSet attributeSet, Resources.Theme theme) {
        TypedArray obtainStyledAttributes = theme != null ? theme.obtainStyledAttributes(attributeSet, R.styleable.XCircularProgressBgDrawable, 0, 0) : resources.obtainAttributes(attributeSet, R.styleable.XCircularProgressBgDrawable);
        int color = obtainStyledAttributes.getColor(R.styleable.XCircularProgressBgDrawable_progress_backgroundColor, -1);
        if (color != -1) {
            this.mProgressBgColor = color;
            this.mPaint.setColor(this.mProgressBgColor);
        }
        this.mStrokeWidth = obtainStyledAttributes.getDimension(R.styleable.XCircularProgressBgDrawable_progress_background_strokeWidth, 0.0f);
        this.mInset = obtainStyledAttributes.getDimensionPixelSize(R.styleable.XCircularProgressBgDrawable_progress_background_inset, 0);
        this.mIndicatorPlay = obtainStyledAttributes.getDrawable(R.styleable.XCircularProgressBgDrawable_progress_bg_indicator_play);
        this.mIndicatorPause = obtainStyledAttributes.getDrawable(R.styleable.XCircularProgressBgDrawable_progress_bg_indicator_pause);
        this.mIndicatorStop = obtainStyledAttributes.getDrawable(R.styleable.XCircularProgressBgDrawable_progress_bg_indicator_stop);
        this.mIndicatorStart = obtainStyledAttributes.getDrawable(R.styleable.XCircularProgressBgDrawable_progress_bg_indicator_start);
        obtainStyledAttributes.recycle();
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Drawable drawable;
        int save = canvas.save();
        canvas.drawArc(this.mInnerBounds, 0.0f, 360.0f, false, this.mPaint);
        canvas.restoreToCount(save);
        if (this.mShowIndicator) {
            if (this.mPlayingStop) {
                drawable = this.mIndicatorStop;
                if (drawable == null) {
                    return;
                }
            } else if (this.mPlayingPause) {
                drawable = this.mIndicatorPause;
                if (drawable == null) {
                    return;
                }
            } else if (this.mPaused) {
                drawable = this.mIndicatorPlay;
                if (drawable == null) {
                    return;
                }
            } else if (!this.mStartDownload || (drawable = this.mIndicatorStart) == null) {
                return;
            }
            drawable.draw(canvas);
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
    protected void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        applyIndicatorBounds(this.mIndicatorPlay, this.mTmpIndicatorBounds);
        applyIndicatorBounds(this.mIndicatorPause, this.mTmpIndicatorBounds);
        applyIndicatorBounds(this.mIndicatorStop, this.mTmpIndicatorBounds);
        applyIndicatorBounds(this.mIndicatorStart, this.mTmpIndicatorBounds);
        if (this.mStrokeWidth == 0.0f) {
            this.mStrokeWidth = (this.mOutBounds.width() * 1.0f) / 10.0f;
        }
        this.mInnerBounds.set(rect);
        RectF rectF = this.mInnerBounds;
        float f = this.mInset + (this.mStrokeWidth / 2.0f);
        rectF.inset(f, f);
        this.mPaint.setStrokeWidth(this.mStrokeWidth);
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] iArr) {
        boolean z;
        boolean z2;
        boolean z3;
        boolean z4;
        int length = iArr.length;
        boolean z5 = false;
        int i = 0;
        boolean z6 = false;
        while (true) {
            if (i >= length) {
                z = false;
                z2 = false;
                z3 = false;
                break;
            }
            int i2 = iArr[i];
            if (i2 == R.attr.progress_state_playing_stop) {
                z2 = false;
                z3 = false;
                z = true;
                break;
            } else if (i2 == R.attr.progress_state_playing_pause) {
                z = false;
                z3 = false;
                z2 = true;
                break;
            } else if (i2 == R.attr.progress_state_paused) {
                z = false;
                z2 = false;
                z3 = true;
                break;
            } else {
                if (i2 == R.attr.progress_state_start_download) {
                    z6 = true;
                }
                i++;
            }
        }
        if (z != this.mPlayingStop) {
            this.mPlayingStop = z;
            z4 = true;
        } else {
            z4 = false;
        }
        if (z2 != this.mPlayingPause) {
            this.mPlayingPause = z2;
            z4 = true;
        }
        if (z3 != this.mPaused) {
            this.mPaused = z3;
            z4 = true;
        }
        if (z6 != this.mStartDownload) {
            this.mStartDownload = z6;
            z4 = true;
        }
        if (z4) {
            if (this.mPlayingStop || this.mPlayingPause || this.mPaused || this.mStartDownload) {
                z5 = true;
            }
            this.mShowIndicator = z5;
            return true;
        }
        return super.onStateChange(iArr);
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
        this.mPaint.setAlpha(i);
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mPaint.setColorFilter(colorFilter);
    }

    public void setIndicatorPause(Drawable drawable) {
        this.mIndicatorPause = drawable;
    }

    public void setIndicatorPlay(Drawable drawable) {
        this.mIndicatorPlay = drawable;
    }

    public void setIndicatorStart(Drawable drawable) {
        this.mIndicatorStart = drawable;
    }

    public void setIndicatorStop(Drawable drawable) {
        this.mIndicatorStop = drawable;
    }

    public void setInset(float f) {
        this.mInset = f;
    }

    public void setStrokeWidth(float f) {
        this.mStrokeWidth = f;
    }
}
