package com.xiaopeng.xui.widget.slider;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewParent;
import b.a.a.a.b;
import b.a.a.a.c;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.theme.XThemeManager;
import com.xiaopeng.xui.vui.floatinglayer.VuiFloatingLayerManager;
import com.xiaopeng.xui.widget.XViewGroup;
import java.text.DecimalFormat;
import org.json.JSONException;
import org.json.JSONObject;
import xiaopeng.widget.BuildConfig;
/* loaded from: classes.dex */
public class XSlider extends XViewGroup implements c {
    private static final int BG_ITEM_MARGIN = 18;
    public static final int BG_ITEM_SIZE = 30;
    private static final int CHILDREN_LAYOUT_HEIGHT = 40;
    private static final int CHILDREN_LAYOUT_WIDTH = 20;
    private static final int INDICATOR_BALL_RADIUS = 9;
    private static final int INDICATOR_HOLD_HORIZONTAL = 0;
    private static final int INDICATOR_HOLD_VERTICAL = 40;
    private static final int INDICATOR_MARGIN = 16;
    private static final int INDICATOR_OUTER = 7;
    private float accuracy;
    private LinearGradient barGradient;
    int bgBallColor;
    int bgDayColor;
    private LinearGradient bgGradient;
    private final Paint bgGradientPaint;
    private float bgHeight;
    private float bgItemGap;
    int bgLineColorSelect;
    private Paint bgLinePaint;
    int bgNightColor;
    private Paint bgPaint;
    private float bgVertical;
    private Paint bollPaint;
    private float currentUpdateIndex;
    private int customBackground;
    private int decimal;
    private DecimalFormat decimalFormat;
    private float desireHeight;
    private float desireWidth;
    private int disableAlpha;
    private int enableAlpha;
    private int endColor;
    private int endIndex;
    private final Paint gradientPaint;
    private boolean hidePop;
    IndicatorDrawable indicatorDrawable;
    private float indicatorValue;
    private float indicatorX;
    private int initIndex;
    private boolean isNight;
    private int leftColor;
    int lineColorSelect;
    private Paint lineSelectPaint;
    private boolean mIsDragging;
    private float mScaledTouchSlop;
    private int mStep;
    private float mTouchDownX;
    private String prefixUnit;
    private ProgressChangeListener progressChangeListener;
    private int rightColor;
    private float roundRadius;
    private SliderProgressListener sliderProgressListener;
    private int startIndex;
    private BitmapDrawable thumb;
    private int topColor;
    private String unit;
    private int upperLimit;
    private int workableTotalWidth;

    /* loaded from: classes.dex */
    public interface ProgressChangeListener {
        void onProgressChanged(XSlider xSlider, float f, String str, boolean z);
    }

    /* loaded from: classes.dex */
    public interface SliderProgressListener {
        void onProgressChanged(XSlider xSlider, float f, String str);

        void onStartTrackingTouch(XSlider xSlider);

        void onStopTrackingTouch(XSlider xSlider);
    }

    /* loaded from: classes.dex */
    class a implements Runnable {
        a() {
        }

        @Override // java.lang.Runnable
        public void run() {
            XSlider.this.invalidate();
        }
    }

    public XSlider(Context context) {
        super(context);
        this.gradientPaint = new Paint(1);
        this.bgGradientPaint = new Paint(1);
        this.enableAlpha = 92;
        this.bgLineColorSelect = -15945223;
        this.bgNightColor = 1543503872;
        this.bgDayColor = 1560281087;
        this.bgBallColor = -12871169;
        this.lineColorSelect = -1;
        this.customBackground = 0;
        this.desireHeight = 100.0f;
        this.desireWidth = 644.0f;
        this.bgHeight = 40.0f;
        this.roundRadius = 16.0f;
        this.disableAlpha = 40;
        this.initIndex = -1;
        this.upperLimit = Integer.MIN_VALUE;
        this.bgVertical = 16.0f;
        this.accuracy = 1.0f;
        this.isNight = XThemeManager.isNight(getContext());
        this.endColor = 1560281087;
        this.topColor = -1;
        this.rightColor = -12871169;
        this.leftColor = -12860929;
        this.mStep = 1;
        this.hidePop = false;
    }

    public XSlider(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.gradientPaint = new Paint(1);
        this.bgGradientPaint = new Paint(1);
        this.enableAlpha = 92;
        this.bgLineColorSelect = -15945223;
        this.bgNightColor = 1543503872;
        this.bgDayColor = 1560281087;
        this.bgBallColor = -12871169;
        this.lineColorSelect = -1;
        this.customBackground = 0;
        this.desireHeight = 100.0f;
        this.desireWidth = 644.0f;
        this.bgHeight = 40.0f;
        this.roundRadius = 16.0f;
        this.disableAlpha = 40;
        this.initIndex = -1;
        this.upperLimit = Integer.MIN_VALUE;
        this.bgVertical = 16.0f;
        this.accuracy = 1.0f;
        this.isNight = XThemeManager.isNight(getContext());
        this.endColor = 1560281087;
        this.topColor = -1;
        this.rightColor = -12871169;
        this.leftColor = -12860929;
        this.mStep = 1;
        this.hidePop = false;
        initView(context, attributeSet);
        initPaint();
    }

    public XSlider(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.gradientPaint = new Paint(1);
        this.bgGradientPaint = new Paint(1);
        this.enableAlpha = 92;
        this.bgLineColorSelect = -15945223;
        this.bgNightColor = 1543503872;
        this.bgDayColor = 1560281087;
        this.bgBallColor = -12871169;
        this.lineColorSelect = -1;
        this.customBackground = 0;
        this.desireHeight = 100.0f;
        this.desireWidth = 644.0f;
        this.bgHeight = 40.0f;
        this.roundRadius = 16.0f;
        this.disableAlpha = 40;
        this.initIndex = -1;
        this.upperLimit = Integer.MIN_VALUE;
        this.bgVertical = 16.0f;
        this.accuracy = 1.0f;
        this.isNight = XThemeManager.isNight(getContext());
        this.endColor = 1560281087;
        this.topColor = -1;
        this.rightColor = -12871169;
        this.leftColor = -12860929;
        this.mStep = 1;
        this.hidePop = false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void a() {
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void a(int i, boolean z) {
        int i2;
        ProgressChangeListener progressChangeListener;
        float f = i - this.startIndex;
        this.indicatorX = ((f / (this.endIndex - i2)) * this.workableTotalWidth) + 16.0f;
        this.indicatorValue = f;
        invalidate();
        notifyChildren(false, false);
        if (!this.hidePop) {
            this.indicatorDrawable.updateCenter(filterValidValue(), getPopString(), this.isNight, getWidth());
        }
        if (!z || (progressChangeListener = this.progressChangeListener) == null) {
            return;
        }
        progressChangeListener.onProgressChanged(this, this.indicatorValue + this.startIndex, this.unit, true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void a(View view) {
        VuiFloatingLayerManager.show(view, ((int) getIndicatorLocationX()) - (getWidth() / 2), (int) ((getHeightExIndicator() / 2.0f) - (getHeight() / 2)));
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void b() {
        if (!this.hidePop) {
            this.indicatorDrawable.updateCenter(filterValidValue(), getPopString(), this.isNight, getWidth());
        }
        invalidate();
    }

    private float filterValidValue() {
        if (this.indicatorX < 16.0f) {
            return 16.0f;
        }
        float width = (getWidth() - 16) - upperLimitDistance();
        float f = this.indicatorX;
        return f > width ? width : f;
    }

    private float getWidthExIndicator() {
        return getWidth() + 0;
    }

    private void initPaint() {
        if (!this.hidePop) {
            this.indicatorDrawable = new IndicatorDrawable(getContext());
        }
        this.bgPaint = new Paint(1);
        this.bgPaint.setStyle(Paint.Style.FILL);
        this.bgPaint.setColor(this.bgNightColor);
        this.bgLinePaint = new Paint(1);
        this.bgLinePaint.setStyle(Paint.Style.FILL);
        this.bgLinePaint.setStrokeCap(Paint.Cap.ROUND);
        this.bgLinePaint.setColor(this.bgLineColorSelect);
        this.bgLinePaint.setStrokeWidth(16.0f);
        this.bollPaint = new Paint(1);
        this.bollPaint.setStyle(Paint.Style.FILL);
        this.bollPaint.setColor(-1);
        this.lineSelectPaint = new Paint(1);
        this.lineSelectPaint.setStyle(Paint.Style.FILL);
        this.lineSelectPaint.setStrokeCap(Paint.Cap.ROUND);
        this.lineSelectPaint.setStrokeWidth(12.0f);
        this.lineSelectPaint.setColor(this.lineColorSelect);
        setEnabled(true);
        this.thumb = (BitmapDrawable) ContextCompat.getDrawable(getContext(), R.drawable.x_ic_slider_slideblock_night);
    }

    private void initView(Context context, AttributeSet attributeSet) {
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.XSlider);
            this.unit = obtainStyledAttributes.getString(R.styleable.XSlider_slider_unit);
            this.startIndex = obtainStyledAttributes.getInteger(R.styleable.XSlider_slider_start_index, 0);
            this.mStep = obtainStyledAttributes.getInteger(R.styleable.XSlider_slider_step, 1);
            this.initIndex = obtainStyledAttributes.getInteger(R.styleable.XSlider_slider_init_index, -1);
            this.endIndex = obtainStyledAttributes.getInteger(R.styleable.XSlider_slider_end_index, 100);
            this.upperLimit = obtainStyledAttributes.getInteger(R.styleable.XSlider_slider_upper_limit, Integer.MIN_VALUE);
            this.decimal = obtainStyledAttributes.getInteger(R.styleable.XSlider_slider_index_decimal, 0);
            this.prefixUnit = obtainStyledAttributes.getString(R.styleable.XSlider_slider_unit_prefix);
            this.bgNightColor = obtainStyledAttributes.getColor(R.styleable.XSlider_slider_bg_color, this.bgNightColor);
            this.bgLineColorSelect = obtainStyledAttributes.getColor(R.styleable.XSlider_slider_bg_line_color, this.bgLineColorSelect);
            this.customBackground = obtainStyledAttributes.getColor(R.styleable.XSlider_slider_background, 0);
            this.accuracy = obtainStyledAttributes.getFloat(R.styleable.XSlider_slider_accuracy, 0.0f);
            this.hidePop = obtainStyledAttributes.getBoolean(R.styleable.XSlider_slider_hide_pop, false);
            if (this.initIndex == -1) {
                int i = this.startIndex;
                int i2 = this.endIndex;
                if (i > i2) {
                    i = i2;
                }
                this.initIndex = i;
            }
            int i3 = this.initIndex;
            int i4 = this.startIndex;
            this.indicatorValue = i3 - i4;
            if (this.endIndex == i4) {
                throw new RuntimeException("startIndex = endIndex!!! please check the xml");
            }
            int i5 = this.decimal;
            this.decimalFormat = i5 == 0 ? null : i5 == 1 ? new DecimalFormat("0.0") : new DecimalFormat("0.00");
            if (this.accuracy == 0.0f) {
                int i6 = this.decimal;
                this.accuracy = i6 == 0 ? 1.0f : i6 == 1 ? 0.1f : 0.01f;
            }
            obtainStyledAttributes.recycle();
        }
        setMinimumWidth(80);
        setBackground(new ColorDrawable(this.customBackground));
        this.mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    private void invalidateAll() {
        invalidate();
        if (this.hidePop) {
            return;
        }
        this.indicatorDrawable.updateCenter(filterValidValue(), getPopString(), this.isNight, getWidth());
    }

    private boolean isInScrollContainer() {
        for (ViewParent parent = getParent(); parent instanceof ViewGroup; parent = parent.getParent()) {
            if (((ViewGroup) parent).shouldDelayChildPressedState()) {
                return true;
            }
        }
        return false;
    }

    private void notifyChildren(boolean z, boolean z2) {
        float filterValidValue = filterValidValue();
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            com.xiaopeng.xui.widget.slider.a aVar = (com.xiaopeng.xui.widget.slider.a) getChildAt(i);
            if (aVar.getX() + (aVar.getWidth() / 2) > filterValidValue()) {
                aVar.a(false);
            } else if (!aVar.a()) {
                aVar.a(true);
            }
        }
        if (z) {
            float f = filterValidValue - 16.0f;
            int i2 = this.endIndex;
            int i3 = this.startIndex;
            this.indicatorValue = (f / this.workableTotalWidth) * (i2 - i3);
            if (this.sliderProgressListener != null) {
                if (!z2) {
                    float f2 = this.indicatorValue + i3;
                    float f3 = this.currentUpdateIndex;
                    float f4 = this.accuracy;
                    if (f2 < f3 + f4 && f2 > f3 - f4 && f3 != 0.0f) {
                        return;
                    }
                }
                this.sliderProgressListener.onProgressChanged(this, this.indicatorValue + this.startIndex, this.unit);
                int i4 = this.endIndex;
                int i5 = this.startIndex;
                this.indicatorValue = (f / this.workableTotalWidth) * (i4 - i5);
                this.currentUpdateIndex = ((int) this.indicatorValue) + i5;
            }
        }
    }

    private int resetAlpha(int i, int i2) {
        return (i & 16777215) | (i2 << 24);
    }

    private void setAlphaByEnable(boolean z) {
        this.bgNightColor = resetAlpha(this.bgNightColor, z ? this.enableAlpha : this.disableAlpha);
        this.bgDayColor = resetAlpha(this.bgDayColor, z ? this.enableAlpha : this.disableAlpha);
        this.bgBallColor = resetAlpha(this.bgBallColor, z ? 255 : this.disableAlpha);
    }

    private void stickIndicator() {
        float f;
        if (this.mStep == 1) {
            return;
        }
        this.indicatorX = (((int) (((this.indicatorX - 16.0f) / f) + 0.5d)) * (this.workableTotalWidth / (this.endIndex - this.startIndex))) + 16.0f;
    }

    private float upperLimitDistance() {
        int i;
        int i2;
        int i3 = this.upperLimit;
        if (i3 != Integer.MIN_VALUE && (i = this.startIndex) < (i2 = this.endIndex) && i <= i3 && i3 <= i2) {
            return ((i2 - i3) * this.workableTotalWidth) / (i2 - i);
        }
        return 0.0f;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        float filterValidValue = filterValidValue();
        if (filterValidValue == 0.0f) {
            return;
        }
        float heightExIndicator = getHeightExIndicator() / 2.0f;
        if (!this.hidePop) {
            this.indicatorDrawable.draw(canvas, this.isNight, isEnabled());
        }
        if (isEnabled()) {
            if (this.isNight) {
                canvas.drawBitmap(this.thumb.getBitmap(), filterValidValue - (this.thumb.getBitmap().getWidth() / 2), heightExIndicator - (this.thumb.getBitmap().getHeight() / 2), this.bgLinePaint);
            } else {
                canvas.drawCircle(filterValidValue, heightExIndicator, 9.0f, this.bollPaint);
            }
        }
    }

    public float getHeightExIndicator() {
        return getHeight() + 40;
    }

    public float getIndicatorLocationX() {
        return this.indicatorX;
    }

    public float getIndicatorValue() {
        return (this.indicatorValue + this.startIndex) * this.mStep;
    }

    protected String getPopString() {
        StringBuilder sb;
        int i;
        if (this.unit == null) {
            this.unit = BuildConfig.FLAVOR;
        }
        if (this.prefixUnit == null) {
            this.prefixUnit = BuildConfig.FLAVOR;
        }
        if (this.decimalFormat == null) {
            if (this.mStep == 1) {
                sb = new StringBuilder();
                sb.append(this.prefixUnit);
                i = this.startIndex + ((int) this.indicatorValue);
            } else {
                sb = new StringBuilder();
                sb.append(this.prefixUnit);
                i = (this.startIndex + ((int) (this.indicatorValue + 0.5d))) * this.mStep;
            }
            sb.append(i);
        } else {
            sb = new StringBuilder();
            sb.append(this.prefixUnit);
            sb.append(this.decimalFormat.format((this.startIndex + this.indicatorValue) * this.mStep));
        }
        sb.append(this.unit);
        return sb.toString();
    }

    public b.a.a.a.k.a onBuildVuiElement(String str, b bVar) {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("minValue", this.startIndex);
            jSONObject.put("maxValue", this.endIndex);
            jSONObject.put("interval", (int) Math.ceil((this.endIndex - this.startIndex) / 10.0d));
            setVuiProps(jSONObject);
            return null;
        } catch (JSONException e) {
            return null;
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XViewGroup, android.view.View
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.isNight = XThemeManager.isNight(getContext());
        postDelayed(new a(), 1000L);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XViewGroup, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        float heightExIndicator;
        float widthExIndicator;
        float heightExIndicator2;
        float f;
        Paint paint;
        float heightExIndicator3;
        float filterValidValue;
        float heightExIndicator4;
        float f2;
        Paint paint2;
        super.onDraw(canvas);
        if (this.isNight) {
            this.bgPaint.setColor(this.bgNightColor);
            float f3 = this.roundRadius;
            canvas.drawRoundRect(0.0f, (getHeightExIndicator() / 2.0f) - this.bgVertical, getWidthExIndicator(), (getHeightExIndicator() / 2.0f) + this.bgVertical, f3, f3, this.bgPaint);
            return;
        }
        if (isEnabled()) {
            this.bgGradientPaint.setShader(this.bgGradient);
            heightExIndicator = (getHeightExIndicator() / 2.0f) - this.bgVertical;
            widthExIndicator = getWidthExIndicator();
            heightExIndicator2 = (getHeightExIndicator() / 2.0f) + this.bgVertical;
            f = this.roundRadius;
            paint = this.bgGradientPaint;
        } else {
            this.bgPaint.setColor(this.bgDayColor);
            heightExIndicator = (getHeightExIndicator() / 2.0f) - this.bgVertical;
            widthExIndicator = getWidthExIndicator();
            heightExIndicator2 = (getHeightExIndicator() / 2.0f) + this.bgVertical;
            f = this.roundRadius;
            paint = this.bgPaint;
        }
        canvas.drawRoundRect(0.0f, heightExIndicator, widthExIndicator, heightExIndicator2, f, f, paint);
        if (isEnabled()) {
            this.gradientPaint.setShader(this.barGradient);
            heightExIndicator3 = (getHeightExIndicator() / 2.0f) - this.bgVertical;
            filterValidValue = filterValidValue() + 9.0f + 7.0f;
            heightExIndicator4 = (getHeightExIndicator() / 2.0f) + this.bgVertical;
            f2 = this.roundRadius;
            paint2 = this.gradientPaint;
        } else {
            this.bgPaint.setColor(this.bgBallColor);
            heightExIndicator3 = (getHeightExIndicator() / 2.0f) - this.bgVertical;
            filterValidValue = filterValidValue() + 9.0f + 7.0f;
            heightExIndicator4 = (getHeightExIndicator() / 2.0f) + this.bgVertical;
            f2 = this.roundRadius;
            paint2 = this.bgPaint;
        }
        canvas.drawRoundRect(0.0f, heightExIndicator3, filterValidValue, heightExIndicator4, f2, f2, paint2);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean z, int i, int i2, int i3, int i4) {
        float width = (getWidth() - 36) / 29.0f;
        for (int i5 = 0; i5 < getChildCount(); i5++) {
            float f = (i5 * width) + 18.0f;
            getChildAt(i5).layout((int) (f - 10.0f), (((int) getHeightExIndicator()) / 2) - 20, (int) (f + 10.0f), (((int) getHeightExIndicator()) / 2) + 20);
        }
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        setMeasuredDimension(View.MeasureSpec.getMode(i) == Integer.MIN_VALUE ? (int) this.desireWidth : getMeasuredWidth(), (int) this.desireHeight);
    }

    @Override // android.view.View
    protected void onSizeChanged(int i, int i2, int i3, int i4) {
        super.onSizeChanged(i, i2, i3, i4);
        setPadding(0, 0, 0, 0);
        this.workableTotalWidth = i - 32;
        this.bgItemGap = this.workableTotalWidth / 29.0f;
        int i5 = this.initIndex;
        int i6 = this.startIndex;
        this.indicatorX = (Math.abs((i5 - i6) / (this.endIndex - i6)) * this.workableTotalWidth) + 16.0f;
        int i7 = 0;
        while (true) {
            boolean z = true;
            if (i7 >= 30) {
                break;
            }
            Context context = getContext();
            if (this.indicatorX <= (this.bgItemGap * i7) + 16.0f) {
                z = false;
            }
            addView(new com.xiaopeng.xui.widget.slider.a(context, z));
            i7++;
        }
        this.bgGradient = new LinearGradient(0.0f, (getHeightExIndicator() / 2.0f) - this.bgVertical, 0.0f, (getHeightExIndicator() / 2.0f) + this.bgVertical, new int[]{this.topColor, this.endColor}, (float[]) null, Shader.TileMode.REPEAT);
        this.barGradient = new LinearGradient(16.0f, 0.0f, this.workableTotalWidth, 0.0f, new int[]{this.leftColor, this.rightColor}, (float[]) null, Shader.TileMode.CLAMP);
        if (!this.hidePop) {
            this.indicatorDrawable.updateCenter(filterValidValue(), getPopString(), this.isNight, getWidth());
        }
        invalidate();
    }

    @Override // android.view.View
    public boolean onTouchEvent(MotionEvent motionEvent) {
        if (isEnabled()) {
            int action = motionEvent.getAction();
            if (action != 0) {
                if (action == 1) {
                    if (this.mIsDragging) {
                        this.mIsDragging = false;
                    } else {
                        SliderProgressListener sliderProgressListener = this.sliderProgressListener;
                        if (sliderProgressListener != null) {
                            sliderProgressListener.onStartTrackingTouch(this);
                        }
                    }
                    this.indicatorX = motionEvent.getX();
                    stickIndicator();
                    notifyChildren(true, true);
                    getParent().requestDisallowInterceptTouchEvent(false);
                    SliderProgressListener sliderProgressListener2 = this.sliderProgressListener;
                    if (sliderProgressListener2 != null) {
                        sliderProgressListener2.onStopTrackingTouch(this);
                    }
                    invalidateAll();
                    return true;
                } else if (action != 2) {
                    return true;
                } else {
                    if (!this.mIsDragging) {
                        if (Math.abs(motionEvent.getX() - this.mTouchDownX) > this.mScaledTouchSlop) {
                            this.mIsDragging = true;
                            SliderProgressListener sliderProgressListener3 = this.sliderProgressListener;
                            if (sliderProgressListener3 != null) {
                                sliderProgressListener3.onStartTrackingTouch(this);
                            }
                            this.indicatorX = motionEvent.getX();
                            getParent().requestDisallowInterceptTouchEvent(true);
                            notifyChildren(true, false);
                            invalidateAll();
                        }
                        return true;
                    }
                }
            } else if (isInScrollContainer()) {
                this.mTouchDownX = motionEvent.getX();
                return true;
            } else {
                this.mIsDragging = true;
                getParent().requestDisallowInterceptTouchEvent(true);
                SliderProgressListener sliderProgressListener4 = this.sliderProgressListener;
                if (sliderProgressListener4 != null) {
                    sliderProgressListener4.onStartTrackingTouch(this);
                }
            }
            this.indicatorX = motionEvent.getX();
            notifyChildren(true, false);
            invalidateAll();
            return true;
        }
        return true;
    }

    @Override // b.a.a.a.c
    public boolean onVuiElementEvent(final View view, b.a.a.a.k.b bVar) {
        Double d;
        log("slider onVuiElementEvent");
        if (view == null || (d = (Double) bVar.a(bVar)) == null) {
            return false;
        }
        setCurrentIndex((int) Math.ceil(d.doubleValue()), true);
        post(new Runnable() { // from class: com.xiaopeng.xui.widget.slider.-$$Lambda$XSlider$BMqamYypJeRu7IfOR_s-5g2BIFM
            @Override // java.lang.Runnable
            public final void run() {
                XSlider.this.a(view);
            }
        });
        return true;
    }

    public void setAccuracy(float f) {
        this.accuracy = f;
    }

    public void setCurrentIndex(int i) {
        setCurrentIndex(i, false);
    }

    public void setCurrentIndex(final int i, final boolean z) {
        post(new Runnable() { // from class: com.xiaopeng.xui.widget.slider.-$$Lambda$XSlider$gUy47Qd-u9S0DP9ewBThhWxsUAg
            @Override // java.lang.Runnable
            public final void run() {
                XSlider.this.a(i, z);
            }
        });
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setEnabled(z);
        }
        setAlphaByEnable(z);
        invalidate();
    }

    public void setEndIndex(int i) {
        if (this.startIndex == i) {
            throw new RuntimeException("startIndex = endIndex!!!");
        }
        this.endIndex = i;
        post(new Runnable() { // from class: com.xiaopeng.xui.widget.slider.-$$Lambda$XSlider$8G5iRPgMuZnZ2CYvwhi0cOA6S9U
            @Override // java.lang.Runnable
            public final void run() {
                XSlider.this.a();
            }
        });
    }

    public void setInitIndex(int i) {
        int i2 = this.endIndex;
        if (i > i2) {
            this.initIndex = i2;
            return;
        }
        int i3 = this.startIndex;
        if (i < i3) {
            this.initIndex = i3;
            return;
        }
        this.initIndex = i;
        this.indicatorValue = i - i3;
        invalidate();
    }

    public void setProgressChangeListener(ProgressChangeListener progressChangeListener) {
        this.progressChangeListener = progressChangeListener;
    }

    public void setSliderProgressListener(SliderProgressListener sliderProgressListener) {
        this.sliderProgressListener = sliderProgressListener;
    }

    public void setStartIndex(int i) {
        if (i == this.endIndex) {
            throw new RuntimeException("startIndex = endIndex!!!");
        }
        this.startIndex = i;
        post(new Runnable() { // from class: com.xiaopeng.xui.widget.slider.-$$Lambda$XSlider$VD0raqeMEiG-aCD3rK79WKYLuWM
            @Override // java.lang.Runnable
            public final void run() {
                XSlider.this.b();
            }
        });
    }
}
