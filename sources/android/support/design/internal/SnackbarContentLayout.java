package android.support.design.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.design.snackbar.ContentViewCallback;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
/* loaded from: classes.dex */
public class SnackbarContentLayout extends LinearLayout implements ContentViewCallback {
    private Button actionView;
    private int maxInlineActionWidth;
    private int maxWidth;
    private TextView messageView;

    public SnackbarContentLayout(Context context) {
        this(context, null);
    }

    public SnackbarContentLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a2 = context.obtainStyledAttributes(attrs, R.styleable.SnackbarLayout);
        this.maxWidth = a2.getDimensionPixelSize(R.styleable.SnackbarLayout_android_maxWidth, -1);
        this.maxInlineActionWidth = a2.getDimensionPixelSize(R.styleable.SnackbarLayout_maxActionInlineWidth, -1);
        a2.recycle();
    }

    @Override // android.view.View
    protected void onFinishInflate() {
        super.onFinishInflate();
        this.messageView = (TextView) findViewById(R.id.snackbar_text);
        this.actionView = (Button) findViewById(R.id.snackbar_action);
    }

    @Override // android.widget.LinearLayout, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (this.maxWidth > 0 && getMeasuredWidth() > this.maxWidth) {
            widthMeasureSpec = View.MeasureSpec.makeMeasureSpec(this.maxWidth, 1073741824);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
        int multiLineVPadding = getResources().getDimensionPixelSize(R.dimen.design_snackbar_padding_vertical_2lines);
        int singleLineVPadding = getResources().getDimensionPixelSize(R.dimen.design_snackbar_padding_vertical);
        boolean isMultiLine = this.messageView.getLayout().getLineCount() > 1;
        boolean remeasure = false;
        if (isMultiLine && this.maxInlineActionWidth > 0 && this.actionView.getMeasuredWidth() > this.maxInlineActionWidth) {
            if (updateViewsWithinLayout(1, multiLineVPadding, multiLineVPadding - singleLineVPadding)) {
                remeasure = true;
            }
        } else {
            int messagePadding = isMultiLine ? multiLineVPadding : singleLineVPadding;
            if (updateViewsWithinLayout(0, messagePadding, messagePadding)) {
                remeasure = true;
            }
        }
        if (remeasure) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private boolean updateViewsWithinLayout(int orientation, int messagePadTop, int messagePadBottom) {
        boolean changed = false;
        if (orientation != getOrientation()) {
            setOrientation(orientation);
            changed = true;
        }
        if (this.messageView.getPaddingTop() != messagePadTop || this.messageView.getPaddingBottom() != messagePadBottom) {
            updateTopBottomPadding(this.messageView, messagePadTop, messagePadBottom);
            return true;
        }
        return changed;
    }

    private static void updateTopBottomPadding(View view, int topPadding, int bottomPadding) {
        if (ViewCompat.isPaddingRelative(view)) {
            ViewCompat.setPaddingRelative(view, ViewCompat.getPaddingStart(view), topPadding, ViewCompat.getPaddingEnd(view), bottomPadding);
        } else {
            view.setPadding(view.getPaddingLeft(), topPadding, view.getPaddingRight(), bottomPadding);
        }
    }

    @Override // android.support.design.snackbar.ContentViewCallback
    public void animateContentIn(int delay, int duration) {
        this.messageView.setAlpha(0.0f);
        this.messageView.animate().alpha(1.0f).setDuration(duration).setStartDelay(delay).start();
        if (this.actionView.getVisibility() == 0) {
            this.actionView.setAlpha(0.0f);
            this.actionView.animate().alpha(1.0f).setDuration(duration).setStartDelay(delay).start();
        }
    }

    @Override // android.support.design.snackbar.ContentViewCallback
    public void animateContentOut(int delay, int duration) {
        this.messageView.setAlpha(1.0f);
        this.messageView.animate().alpha(0.0f).setDuration(duration).setStartDelay(delay).start();
        if (this.actionView.getVisibility() == 0) {
            this.actionView.setAlpha(1.0f);
            this.actionView.animate().alpha(0.0f).setDuration(duration).setStartDelay(delay).start();
        }
    }
}
