package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v17.leanback.R;
import android.util.AttributeSet;
import android.util.TypedValue;
/* loaded from: classes.dex */
public class VerticalGridView extends BaseGridView {
    public VerticalGridView(Context context) {
        this(context, null);
    }

    public VerticalGridView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerticalGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mLayoutManager.setOrientation(1);
        initAttributes(context, attrs);
    }

    protected void initAttributes(Context context, AttributeSet attrs) {
        initBaseGridViewAttributes(context, attrs);
        TypedArray a2 = context.obtainStyledAttributes(attrs, R.styleable.lbVerticalGridView);
        setColumnWidth(a2);
        setNumColumns(a2.getInt(R.styleable.lbVerticalGridView_numberOfColumns, 1));
        a2.recycle();
    }

    void setColumnWidth(TypedArray array) {
        TypedValue typedValue = array.peekValue(R.styleable.lbVerticalGridView_columnWidth);
        if (typedValue != null) {
            int size = array.getLayoutDimension(R.styleable.lbVerticalGridView_columnWidth, 0);
            setColumnWidth(size);
        }
    }

    public void setNumColumns(int numColumns) {
        this.mLayoutManager.setNumRows(numColumns);
        requestLayout();
    }

    public void setColumnWidth(int width) {
        this.mLayoutManager.setRowHeight(width);
        requestLayout();
    }
}
