package android.support.v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.appcompat.R;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.widget.TextView;
/* loaded from: classes.dex */
public class DialogTitle extends TextView {
    public DialogTitle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DialogTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DialogTitle(Context context) {
        super(context);
    }

    @Override // android.widget.TextView, android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int lineCount;
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Layout layout = getLayout();
        if (layout != null && (lineCount = layout.getLineCount()) > 0) {
            int ellipsisCount = layout.getEllipsisCount(lineCount - 1);
            if (ellipsisCount > 0) {
                setSingleLine(false);
                setMaxLines(2);
                TypedArray a2 = getContext().obtainStyledAttributes(null, R.styleable.TextAppearance, 16842817, 16973892);
                int textSize = a2.getDimensionPixelSize(R.styleable.TextAppearance_android_textSize, 0);
                if (textSize != 0) {
                    setTextSize(0, textSize);
                }
                a2.recycle();
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        }
    }

    @Override // android.widget.TextView
    public void setCustomSelectionActionModeCallback(ActionMode.Callback actionModeCallback) {
        super.setCustomSelectionActionModeCallback(TextViewCompat.wrapCustomSelectionActionModeCallback(this, actionModeCallback));
    }
}
