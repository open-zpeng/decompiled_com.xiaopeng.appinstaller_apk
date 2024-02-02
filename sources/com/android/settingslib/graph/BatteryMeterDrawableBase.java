package com.android.settingslib.graph;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import com.android.settingslib.R;
import com.android.settingslib.Utils;
/* loaded from: classes.dex */
public class BatteryMeterDrawableBase extends Drawable {
    public static final String TAG = BatteryMeterDrawableBase.class.getSimpleName();
    protected final Paint mBatteryPaint;
    protected final Paint mBoltPaint;
    private final float[] mBoltPoints;
    protected float mButtonHeightFraction;
    private int mChargeColor;
    private boolean mCharging;
    private final int[] mColors;
    protected final Context mContext;
    private final int mCriticalLevel;
    protected final Paint mFramePaint;
    private int mHeight;
    private final int mIntrinsicHeight;
    private final int mIntrinsicWidth;
    protected final Paint mPlusPaint;
    private final float[] mPlusPoints;
    private boolean mPowerSaveEnabled;
    protected final Paint mPowersavePaint;
    private boolean mShowPercent;
    private float mSubpixelSmoothingLeft;
    private float mSubpixelSmoothingRight;
    private float mTextHeight;
    protected final Paint mTextPaint;
    private String mWarningString;
    private float mWarningTextHeight;
    protected final Paint mWarningTextPaint;
    private int mWidth;
    private int mLevel = -1;
    protected boolean mPowerSaveAsColorError = true;
    private int mIconTint = -1;
    private float mOldDarkIntensity = -1.0f;
    private final Path mBoltPath = new Path();
    private final Path mPlusPath = new Path();
    private final Rect mPadding = new Rect();
    private final RectF mFrame = new RectF();
    private final RectF mButtonFrame = new RectF();
    private final RectF mBoltFrame = new RectF();
    private final RectF mPlusFrame = new RectF();
    private final Path mShapePath = new Path();
    private final Path mOutlinePath = new Path();
    private final Path mTextPath = new Path();

    public BatteryMeterDrawableBase(Context context, int frameColor) {
        this.mContext = context;
        Resources res = context.getResources();
        TypedArray levels = res.obtainTypedArray(R.array.batterymeter_color_levels);
        TypedArray colors = res.obtainTypedArray(R.array.batterymeter_color_values);
        int N = levels.length();
        this.mColors = new int[2 * N];
        for (int i = 0; i < N; i++) {
            this.mColors[2 * i] = levels.getInt(i, 0);
            if (colors.getType(i) == 2) {
                this.mColors[(2 * i) + 1] = Utils.getColorAttr(context, colors.getThemeAttributeId(i, 0));
            } else {
                this.mColors[(2 * i) + 1] = colors.getColor(i, 0);
            }
        }
        levels.recycle();
        colors.recycle();
        this.mWarningString = context.getString(R.string.battery_meter_very_low_overlay_symbol);
        this.mCriticalLevel = this.mContext.getResources().getInteger(17694757);
        this.mButtonHeightFraction = context.getResources().getFraction(R.fraction.battery_button_height_fraction, 1, 1);
        this.mSubpixelSmoothingLeft = context.getResources().getFraction(R.fraction.battery_subpixel_smoothing_left, 1, 1);
        this.mSubpixelSmoothingRight = context.getResources().getFraction(R.fraction.battery_subpixel_smoothing_right, 1, 1);
        this.mFramePaint = new Paint(1);
        this.mFramePaint.setColor(frameColor);
        this.mFramePaint.setDither(true);
        this.mFramePaint.setStrokeWidth(0.0f);
        this.mFramePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.mBatteryPaint = new Paint(1);
        this.mBatteryPaint.setDither(true);
        this.mBatteryPaint.setStrokeWidth(0.0f);
        this.mBatteryPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        this.mTextPaint = new Paint(1);
        Typeface font = Typeface.create("sans-serif-condensed", 1);
        this.mTextPaint.setTypeface(font);
        this.mTextPaint.setTextAlign(Paint.Align.CENTER);
        this.mWarningTextPaint = new Paint(1);
        Typeface font2 = Typeface.create("sans-serif", 1);
        this.mWarningTextPaint.setTypeface(font2);
        this.mWarningTextPaint.setTextAlign(Paint.Align.CENTER);
        if (this.mColors.length > 1) {
            this.mWarningTextPaint.setColor(this.mColors[1]);
        }
        this.mChargeColor = Utils.getDefaultColor(this.mContext, R.color.meter_consumed_color);
        this.mBoltPaint = new Paint(1);
        this.mBoltPaint.setColor(Utils.getDefaultColor(this.mContext, R.color.batterymeter_bolt_color));
        this.mBoltPoints = loadPoints(res, R.array.batterymeter_bolt_points);
        this.mPlusPaint = new Paint(1);
        this.mPlusPaint.setColor(Utils.getDefaultColor(this.mContext, R.color.batterymeter_plus_color));
        this.mPlusPoints = loadPoints(res, R.array.batterymeter_plus_points);
        this.mPowersavePaint = new Paint(1);
        this.mPowersavePaint.setColor(this.mPlusPaint.getColor());
        this.mPowersavePaint.setStyle(Paint.Style.STROKE);
        this.mPowersavePaint.setStrokeWidth(context.getResources().getDimensionPixelSize(R.dimen.battery_powersave_outline_thickness));
        this.mIntrinsicWidth = context.getResources().getDimensionPixelSize(R.dimen.battery_width);
        this.mIntrinsicHeight = context.getResources().getDimensionPixelSize(R.dimen.battery_height);
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicHeight() {
        return this.mIntrinsicHeight;
    }

    @Override // android.graphics.drawable.Drawable
    public int getIntrinsicWidth() {
        return this.mIntrinsicWidth;
    }

    public void setBatteryLevel(int val) {
        this.mLevel = val;
        postInvalidate();
    }

    protected void postInvalidate() {
        unscheduleSelf(new Runnable() { // from class: com.android.settingslib.graph.-$$Lambda$BatteryMeterDrawableBase$ExJ0HHRzS2_LMtcBJqtFiovbn0w
            @Override // java.lang.Runnable
            public final void run() {
                BatteryMeterDrawableBase.this.invalidateSelf();
            }
        });
        scheduleSelf(new Runnable() { // from class: com.android.settingslib.graph.-$$Lambda$BatteryMeterDrawableBase$ExJ0HHRzS2_LMtcBJqtFiovbn0w
            @Override // java.lang.Runnable
            public final void run() {
                BatteryMeterDrawableBase.this.invalidateSelf();
            }
        }, 0L);
    }

    private static float[] loadPoints(Resources res, int pointArrayRes) {
        int[] pts = res.getIntArray(pointArrayRes);
        int maxY = 0;
        int maxX = 0;
        for (int maxX2 = 0; maxX2 < pts.length; maxX2 += 2) {
            maxX = Math.max(maxX, pts[maxX2]);
            maxY = Math.max(maxY, pts[maxX2 + 1]);
        }
        int i = pts.length;
        float[] ptsF = new float[i];
        for (int i2 = 0; i2 < pts.length; i2 += 2) {
            ptsF[i2] = pts[i2] / maxX;
            ptsF[i2 + 1] = pts[i2 + 1] / maxY;
        }
        return ptsF;
    }

    @Override // android.graphics.drawable.Drawable
    public void setBounds(int left, int top, int right, int bottom) {
        super.setBounds(left, top, right, bottom);
        updateSize();
    }

    private void updateSize() {
        Rect bounds = getBounds();
        this.mHeight = (bounds.bottom - this.mPadding.bottom) - (bounds.top + this.mPadding.top);
        this.mWidth = (bounds.right - this.mPadding.right) - (bounds.left + this.mPadding.left);
        this.mWarningTextPaint.setTextSize(this.mHeight * 0.75f);
        this.mWarningTextHeight = -this.mWarningTextPaint.getFontMetrics().ascent;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean getPadding(Rect padding) {
        if (this.mPadding.left == 0 && this.mPadding.top == 0 && this.mPadding.right == 0 && this.mPadding.bottom == 0) {
            return super.getPadding(padding);
        }
        padding.set(this.mPadding);
        return true;
    }

    public void setPadding(int left, int top, int right, int bottom) {
        this.mPadding.left = left;
        this.mPadding.top = top;
        this.mPadding.right = right;
        this.mPadding.bottom = bottom;
        updateSize();
    }

    private int getColorForLevel(int percent) {
        int color = 0;
        for (int i = 0; i < this.mColors.length; i += 2) {
            int thresh = this.mColors[i];
            color = this.mColors[i + 1];
            if (percent <= thresh) {
                if (i == this.mColors.length - 2) {
                    return this.mIconTint;
                } else {
                    return color;
                }
            }
        }
        return color;
    }

    protected int batteryColorForLevel(int level) {
        if (this.mCharging || (this.mPowerSaveEnabled && this.mPowerSaveAsColorError)) {
            return this.mChargeColor;
        }
        return getColorForLevel(level);
    }

    /* JADX WARN: Removed duplicated region for block: B:68:0x0387  */
    /* JADX WARN: Removed duplicated region for block: B:69:0x038b  */
    /* JADX WARN: Removed duplicated region for block: B:73:0x03bb  */
    /* JADX WARN: Removed duplicated region for block: B:76:0x03c1  */
    /* JADX WARN: Removed duplicated region for block: B:83:0x040f  */
    /* JADX WARN: Removed duplicated region for block: B:84:0x042b  */
    @Override // android.graphics.drawable.Drawable
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void draw(android.graphics.Canvas r41) {
        /*
            Method dump skipped, instructions count: 1094
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.graph.BatteryMeterDrawableBase.draw(android.graphics.Canvas):void");
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int alpha) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
        this.mFramePaint.setColorFilter(colorFilter);
        this.mBatteryPaint.setColorFilter(colorFilter);
        this.mWarningTextPaint.setColorFilter(colorFilter);
        this.mBoltPaint.setColorFilter(colorFilter);
        this.mPlusPaint.setColorFilter(colorFilter);
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return 0;
    }

    protected float getAspectRatio() {
        return 0.58f;
    }

    protected float getRadiusRatio() {
        return 0.05882353f;
    }
}
