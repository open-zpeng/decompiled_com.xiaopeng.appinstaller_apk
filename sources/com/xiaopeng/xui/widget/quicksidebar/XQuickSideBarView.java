package com.xiaopeng.xui.widget.quicksidebar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import com.xiaopeng.libtheme.ThemeViewModel;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.sound.XSoundEffectManager;
import com.xiaopeng.xui.view.XView;
import com.xiaopeng.xui.view.XViewDelegate;
import com.xiaopeng.xui.view.font.XFontScaleHelper;
import com.xiaopeng.xui.widget.quicksidebar.listener.OnQuickSideBarTouchListener;
import java.util.Arrays;
import java.util.List;
/* loaded from: classes.dex */
public class XQuickSideBarView extends XView implements XViewDelegate.onFontScaleChangeCallback {
    private OnQuickSideBarTouchListener listener;
    private int mChoose;
    private int mHeight;
    private float mItemHeight;
    private final float mItemStartY;
    private float mItemWidth;
    private List<String> mLetters;
    private Paint mPaint;
    private int mTextColor;
    private int mTextColorChoose;
    private float mTextSize;
    private float mTextSizeChoose;
    private int mWidth;

    public XQuickSideBarView(Context context) {
        this(context, null);
    }

    public XQuickSideBarView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public XQuickSideBarView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mChoose = -1;
        this.mPaint = new Paint();
        this.mItemStartY = 14.0f;
        init(context, attributeSet);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void a() {
        initDefaultColor();
        invalidate();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void a(XFontScaleHelper xFontScaleHelper, XFontScaleHelper xFontScaleHelper2) {
        if (xFontScaleHelper != null) {
            this.mTextSize = xFontScaleHelper.getCurrentFontSize(getResources().getDisplayMetrics());
        }
        if (xFontScaleHelper2 != null) {
            this.mTextSizeChoose = xFontScaleHelper2.getCurrentFontSize(getResources().getDisplayMetrics());
        }
        invalidate();
    }

    private int getColor(int i) {
        return getResources().getColor(i, getContext().getTheme());
    }

    private void init(Context context, AttributeSet attributeSet) {
        this.mLetters = Arrays.asList(context.getResources().getStringArray(R.array.x_quick_side_bar_letters));
        initDefaultColor();
        this.mTextSize = context.getResources().getDimensionPixelSize(R.dimen.x_sidebar_textsize);
        this.mTextSizeChoose = context.getResources().getDimensionPixelSize(R.dimen.x_sidebar_choose_textsize);
        this.mItemHeight = context.getResources().getDimension(R.dimen.x_sidebar_item_height);
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.XQuickSideBarView);
            this.mTextColor = obtainStyledAttributes.getColor(R.styleable.XQuickSideBarView_sidebarTextColor, this.mTextColor);
            this.mTextColorChoose = obtainStyledAttributes.getColor(R.styleable.XQuickSideBarView_sidebarTextColorChoose, this.mTextColorChoose);
            this.mTextSize = obtainStyledAttributes.getDimension(R.styleable.XQuickSideBarView_sidebarTextSize, this.mTextSize);
            this.mTextSizeChoose = obtainStyledAttributes.getDimension(R.styleable.XQuickSideBarView_sidebarTextSizeChoose, this.mTextSizeChoose);
            final XFontScaleHelper create = XFontScaleHelper.create(obtainStyledAttributes, R.styleable.XQuickSideBarView_sidebarTextSize, R.dimen.x_sidebar_textsize);
            final XFontScaleHelper create2 = XFontScaleHelper.create(obtainStyledAttributes, R.styleable.XQuickSideBarView_sidebarTextSizeChoose, R.dimen.x_sidebar_choose_textsize);
            XViewDelegate xViewDelegate = this.mXViewDelegate;
            if (xViewDelegate != null) {
                xViewDelegate.setFontScaleChangeCallback(new XViewDelegate.onFontScaleChangeCallback() { // from class: com.xiaopeng.xui.widget.quicksidebar.-$$Lambda$XQuickSideBarView$wcIFCzTxRGfFMe7aaRvF9NveDCE
                    @Override // com.xiaopeng.xui.view.XViewDelegate.onFontScaleChangeCallback
                    public final void onFontScaleChanged() {
                        XQuickSideBarView.this.a(create, create2);
                    }
                });
            }
            this.mItemHeight = obtainStyledAttributes.getDimension(R.styleable.XQuickSideBarView_sidebarItemHeight, this.mItemHeight);
            obtainStyledAttributes.recycle();
        }
        XViewDelegate xViewDelegate2 = this.mXViewDelegate;
        if (xViewDelegate2 != null && xViewDelegate2.getThemeViewModel() != null) {
            this.mXViewDelegate.getThemeViewModel().setCallback(new ThemeViewModel.OnCallback() { // from class: com.xiaopeng.xui.widget.quicksidebar.-$$Lambda$XQuickSideBarView$7RA_D8JqddGTfbqjstyLXDHIC5c
                @Override // com.xiaopeng.libtheme.ThemeViewModel.OnCallback
                public final void onThemeChanged() {
                    XQuickSideBarView.this.a();
                }
            });
        }
        setSoundEffectsEnabled(false);
    }

    private void initDefaultColor() {
        this.mTextColor = getColor(R.color.x_theme_text_03);
        this.mTextColorChoose = getColor(R.color.x_side_bar_text_choose_color);
    }

    @Override // android.view.View
    public boolean dispatchTouchEvent(MotionEvent motionEvent) {
        int action = motionEvent.getAction();
        float y = motionEvent.getY();
        int i = this.mChoose;
        int i2 = (int) ((y - 14.0f) / this.mItemHeight);
        switch (action) {
            case 0:
            case 2:
                if (i != i2 && i2 >= 0 && i2 < this.mLetters.size()) {
                    this.mChoose = i2;
                    invalidate();
                    XSoundEffectManager.get().play(2);
                    OnQuickSideBarTouchListener onQuickSideBarTouchListener = this.listener;
                    if (onQuickSideBarTouchListener != null) {
                        onQuickSideBarTouchListener.onLetterTouched(true);
                        this.listener.onLetterScrolling(this.mLetters.get(i2), this.mChoose);
                        break;
                    }
                }
                break;
            case 1:
                this.mChoose = -1;
                OnQuickSideBarTouchListener onQuickSideBarTouchListener2 = this.listener;
                if (onQuickSideBarTouchListener2 != null) {
                    onQuickSideBarTouchListener2.onLetterTouched(false);
                }
                invalidate();
                return true;
            case 3:
                XSoundEffectManager.get().release(2);
                OnQuickSideBarTouchListener onQuickSideBarTouchListener3 = this.listener;
                if (onQuickSideBarTouchListener3 != null) {
                    onQuickSideBarTouchListener3.onLetterTouched(false);
                    return true;
                }
                break;
            default:
                return true;
        }
        return true;
    }

    public List<String> getLetters() {
        return this.mLetters;
    }

    public OnQuickSideBarTouchListener getListener() {
        return this.listener;
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        int i;
        super.onDraw(canvas);
        int i2 = 0;
        while (i2 < this.mLetters.size()) {
            Paint paint = new Paint();
            if (i2 == this.mChoose) {
                this.mPaint.setTextSize(this.mTextSizeChoose);
                this.mPaint.setColor(this.mTextColorChoose);
                i = R.color.x_side_bar_item_choose_bg_color;
            } else {
                this.mPaint.setTextSize(this.mTextSize);
                this.mPaint.setColor(this.mTextColor);
                i = R.color.x_side_bar_item_bg_color;
            }
            paint.setColor(getColor(i));
            paint.setStyle(Paint.Style.FILL);
            paint.setAntiAlias(true);
            float f = this.mItemWidth;
            float f2 = (this.mWidth - f) / 2.0f;
            float f3 = this.mItemHeight;
            int i3 = i2 + 1;
            RectF rectF = new RectF(f2, (i2 * f3) + 14.0f, f + f2, (i3 * f3) + 14.0f);
            canvas.drawRoundRect(rectF, 4.0f, 4.0f, paint);
            this.mPaint.setAntiAlias(true);
            this.mPaint.setStyle(Paint.Style.FILL);
            this.mPaint.setTextAlign(Paint.Align.CENTER);
            this.mPaint.setFakeBoldText(true);
            this.mPaint.setTypeface(Typeface.create(getResources().getString(R.string.x_font_typeface_medium), 0));
            Paint.FontMetrics fontMetrics = this.mPaint.getFontMetrics();
            float f4 = fontMetrics.bottom;
            canvas.drawText(this.mLetters.get(i2), rectF.centerX(), rectF.centerY() + (((f4 - fontMetrics.top) / 2.0f) - f4), this.mPaint);
            this.mPaint.reset();
            i2 = i3;
        }
    }

    @Override // com.xiaopeng.xui.view.XViewDelegate.onFontScaleChangeCallback
    public void onFontScaleChanged() {
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        this.mHeight = getMeasuredHeight();
        this.mWidth = getMeasuredWidth();
        float size = this.mHeight / this.mLetters.size();
        float f = this.mItemHeight;
        if (f <= size) {
            size = f;
        }
        this.mItemHeight = size;
        this.mItemWidth = this.mItemHeight;
    }

    public void setLetters(List<String> list) {
        this.mLetters = list;
        invalidate();
    }

    public void setOnQuickSideBarTouchListener(OnQuickSideBarTouchListener onQuickSideBarTouchListener) {
        this.listener = onQuickSideBarTouchListener;
    }
}
