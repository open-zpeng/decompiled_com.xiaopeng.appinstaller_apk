package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v17.leanback.R;
import android.support.v4.widget.TextViewCompat;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.widget.TextView;
/* loaded from: classes.dex */
class ResizingTextView extends TextView {
    private float mDefaultLineSpacingExtra;
    private int mDefaultPaddingBottom;
    private int mDefaultPaddingTop;
    private int mDefaultTextSize;
    private boolean mDefaultsInitialized;
    private boolean mIsResized;
    private boolean mMaintainLineSpacing;
    private int mResizedPaddingAdjustmentBottom;
    private int mResizedPaddingAdjustmentTop;
    private int mResizedTextSize;
    private int mTriggerConditions;

    public ResizingTextView(Context ctx, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(ctx, attrs, defStyleAttr);
        this.mIsResized = false;
        this.mDefaultsInitialized = false;
        TypedArray a2 = ctx.obtainStyledAttributes(attrs, R.styleable.lbResizingTextView, defStyleAttr, defStyleRes);
        try {
            this.mTriggerConditions = a2.getInt(R.styleable.lbResizingTextView_resizeTrigger, 1);
            this.mResizedTextSize = a2.getDimensionPixelSize(R.styleable.lbResizingTextView_resizedTextSize, -1);
            this.mMaintainLineSpacing = a2.getBoolean(R.styleable.lbResizingTextView_maintainLineSpacing, false);
            this.mResizedPaddingAdjustmentTop = a2.getDimensionPixelOffset(R.styleable.lbResizingTextView_resizedPaddingAdjustmentTop, 0);
            this.mResizedPaddingAdjustmentBottom = a2.getDimensionPixelOffset(R.styleable.lbResizingTextView_resizedPaddingAdjustmentBottom, 0);
        } finally {
            a2.recycle();
        }
    }

    public ResizingTextView(Context ctx, AttributeSet attrs, int defStyleAttr) {
        this(ctx, attrs, defStyleAttr, 0);
    }

    public ResizingTextView(Context ctx, AttributeSet attrs) {
        this(ctx, attrs, 16842884);
    }

    public ResizingTextView(Context ctx) {
        this(ctx, null);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (!this.mDefaultsInitialized) {
            this.mDefaultTextSize = (int) getTextSize();
            this.mDefaultLineSpacingExtra = getLineSpacingExtra();
            this.mDefaultPaddingTop = getPaddingTop();
            this.mDefaultPaddingBottom = getPaddingBottom();
            this.mDefaultsInitialized = true;
        }
        setTextSize(0, this.mDefaultTextSize);
        setLineSpacing(this.mDefaultLineSpacingExtra, getLineSpacingMultiplier());
        setPaddingTopAndBottom(this.mDefaultPaddingTop, this.mDefaultPaddingBottom);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        boolean resizeText = false;
        Layout layout = getLayout();
        if (layout != null && (this.mTriggerConditions & 1) > 0) {
            int lineCount = layout.getLineCount();
            int maxLines = getMaxLines();
            if (maxLines > 1) {
                resizeText = lineCount == maxLines;
            }
        }
        int currentSizePx = (int) getTextSize();
        boolean remeasure = false;
        if (resizeText) {
            if (this.mResizedTextSize != -1 && currentSizePx != this.mResizedTextSize) {
                setTextSize(0, this.mResizedTextSize);
                remeasure = true;
            }
            float targetLineSpacingExtra = (this.mDefaultLineSpacingExtra + this.mDefaultTextSize) - this.mResizedTextSize;
            if (this.mMaintainLineSpacing && getLineSpacingExtra() != targetLineSpacingExtra) {
                setLineSpacing(targetLineSpacingExtra, getLineSpacingMultiplier());
                remeasure = true;
            }
            int paddingTop = this.mDefaultPaddingTop + this.mResizedPaddingAdjustmentTop;
            int paddingBottom = this.mDefaultPaddingBottom + this.mResizedPaddingAdjustmentBottom;
            if (getPaddingTop() != paddingTop || getPaddingBottom() != paddingBottom) {
                setPaddingTopAndBottom(paddingTop, paddingBottom);
                remeasure = true;
            }
        } else {
            if (this.mResizedTextSize != -1 && currentSizePx != this.mDefaultTextSize) {
                setTextSize(0, this.mDefaultTextSize);
                remeasure = true;
            }
            if (this.mMaintainLineSpacing && getLineSpacingExtra() != this.mDefaultLineSpacingExtra) {
                setLineSpacing(this.mDefaultLineSpacingExtra, getLineSpacingMultiplier());
                remeasure = true;
            }
            if (getPaddingTop() != this.mDefaultPaddingTop || getPaddingBottom() != this.mDefaultPaddingBottom) {
                setPaddingTopAndBottom(this.mDefaultPaddingTop, this.mDefaultPaddingBottom);
                remeasure = true;
            }
        }
        this.mIsResized = resizeText;
        if (remeasure) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private void setPaddingTopAndBottom(int paddingTop, int paddingBottom) {
        if (isPaddingRelative()) {
            setPaddingRelative(getPaddingStart(), paddingTop, getPaddingEnd(), paddingBottom);
        } else {
            setPadding(getPaddingLeft(), paddingTop, getPaddingRight(), paddingBottom);
        }
    }

    @Override // android.widget.TextView
    public void setCustomSelectionActionModeCallback(ActionMode.Callback actionModeCallback) {
        super.setCustomSelectionActionModeCallback(TextViewCompat.wrapCustomSelectionActionModeCallback(this, actionModeCallback));
    }
}
