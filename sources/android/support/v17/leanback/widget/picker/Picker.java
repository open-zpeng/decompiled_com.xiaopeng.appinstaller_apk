package android.support.v17.leanback.widget.picker;

import android.content.Context;
import android.graphics.Rect;
import android.support.v17.leanback.R;
import android.support.v17.leanback.widget.OnChildViewHolderSelectedListener;
import android.support.v17.leanback.widget.VerticalGridView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import xiaopeng.widget.BuildConfig;
/* loaded from: classes.dex */
public class Picker extends FrameLayout {
    private Interpolator mAccelerateInterpolator;
    private int mAlphaAnimDuration;
    private final OnChildViewHolderSelectedListener mColumnChangeListener;
    final List<VerticalGridView> mColumnViews;
    ArrayList<PickerColumn> mColumns;
    private Interpolator mDecelerateInterpolator;
    private float mFocusedAlpha;
    private float mInvisibleColumnAlpha;
    private ArrayList<PickerValueListener> mListeners;
    private int mPickerItemLayoutId;
    private int mPickerItemTextViewId;
    private ViewGroup mPickerView;
    private ViewGroup mRootView;
    private int mSelectedColumn;
    private List<CharSequence> mSeparators;
    private float mUnfocusedAlpha;
    private float mVisibleColumnAlpha;
    private float mVisibleItems;
    private float mVisibleItemsActivated;

    /* loaded from: classes.dex */
    public interface PickerValueListener {
        void onValueChanged(Picker picker, int i);
    }

    public final void setSeparators(List<CharSequence> separators) {
        this.mSeparators.clear();
        this.mSeparators.addAll(separators);
    }

    public final int getPickerItemLayoutId() {
        return this.mPickerItemLayoutId;
    }

    public final int getPickerItemTextViewId() {
        return this.mPickerItemTextViewId;
    }

    public Picker(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mColumnViews = new ArrayList();
        this.mVisibleItemsActivated = 3.0f;
        this.mVisibleItems = 1.0f;
        this.mSelectedColumn = 0;
        this.mSeparators = new ArrayList();
        this.mPickerItemLayoutId = R.layout.lb_picker_item;
        this.mPickerItemTextViewId = 0;
        this.mColumnChangeListener = new OnChildViewHolderSelectedListener() { // from class: android.support.v17.leanback.widget.picker.Picker.1
            @Override // android.support.v17.leanback.widget.OnChildViewHolderSelectedListener
            public void onChildViewHolderSelected(RecyclerView parent, RecyclerView.ViewHolder child, int position, int subposition) {
                parent.getAdapter();
                int colIndex = Picker.this.mColumnViews.indexOf(parent);
                Picker.this.updateColumnAlpha(colIndex, true);
                if (child != null) {
                    int newValue = Picker.this.mColumns.get(colIndex).getMinValue() + position;
                    Picker.this.onColumnValueChanged(colIndex, newValue);
                }
            }
        };
        setEnabled(true);
        setDescendantFocusability(262144);
        this.mFocusedAlpha = 1.0f;
        this.mUnfocusedAlpha = 1.0f;
        this.mVisibleColumnAlpha = 0.5f;
        this.mInvisibleColumnAlpha = 0.0f;
        this.mAlphaAnimDuration = 200;
        this.mDecelerateInterpolator = new DecelerateInterpolator(2.5f);
        this.mAccelerateInterpolator = new AccelerateInterpolator(2.5f);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        this.mRootView = (ViewGroup) inflater.inflate(R.layout.lb_picker, (ViewGroup) this, true);
        this.mPickerView = (ViewGroup) this.mRootView.findViewById(R.id.picker);
    }

    public PickerColumn getColumnAt(int colIndex) {
        if (this.mColumns == null) {
            return null;
        }
        return this.mColumns.get(colIndex);
    }

    public int getColumnsCount() {
        if (this.mColumns == null) {
            return 0;
        }
        return this.mColumns.size();
    }

    public void setColumns(List<PickerColumn> columns) {
        if (this.mSeparators.size() == 0) {
            throw new IllegalStateException("Separators size is: " + this.mSeparators.size() + ". At least one separator must be provided");
        }
        if (this.mSeparators.size() == 1) {
            CharSequence separator = this.mSeparators.get(0);
            this.mSeparators.clear();
            this.mSeparators.add(BuildConfig.FLAVOR);
            for (int i = 0; i < columns.size() - 1; i++) {
                this.mSeparators.add(separator);
            }
            this.mSeparators.add(BuildConfig.FLAVOR);
        } else if (this.mSeparators.size() != columns.size() + 1) {
            throw new IllegalStateException("Separators size: " + this.mSeparators.size() + " mustequal the size of columns: " + columns.size() + " + 1");
        }
        this.mColumnViews.clear();
        this.mPickerView.removeAllViews();
        this.mColumns = new ArrayList<>(columns);
        if (this.mSelectedColumn > this.mColumns.size() - 1) {
            this.mSelectedColumn = this.mColumns.size() - 1;
        }
        LayoutInflater inflater = LayoutInflater.from(getContext());
        int totalCol = getColumnsCount();
        if (!TextUtils.isEmpty(this.mSeparators.get(0))) {
            TextView separator2 = (TextView) inflater.inflate(R.layout.lb_picker_separator, this.mPickerView, false);
            separator2.setText(this.mSeparators.get(0));
            this.mPickerView.addView(separator2);
        }
        for (int i2 = 0; i2 < totalCol; i2++) {
            int colIndex = i2;
            VerticalGridView columnView = (VerticalGridView) inflater.inflate(R.layout.lb_picker_column, this.mPickerView, false);
            updateColumnSize(columnView);
            columnView.setWindowAlignment(0);
            columnView.setHasFixedSize(false);
            columnView.setFocusable(isActivated());
            columnView.setItemViewCacheSize(0);
            this.mColumnViews.add(columnView);
            this.mPickerView.addView(columnView);
            if (!TextUtils.isEmpty(this.mSeparators.get(i2 + 1))) {
                TextView separator3 = (TextView) inflater.inflate(R.layout.lb_picker_separator, this.mPickerView, false);
                separator3.setText(this.mSeparators.get(i2 + 1));
                this.mPickerView.addView(separator3);
            }
            columnView.setAdapter(new PickerScrollArrayAdapter(getContext(), getPickerItemLayoutId(), getPickerItemTextViewId(), colIndex));
            columnView.setOnChildViewHolderSelectedListener(this.mColumnChangeListener);
        }
    }

    public void setColumnAt(int columnIndex, PickerColumn column) {
        this.mColumns.set(columnIndex, column);
        VerticalGridView columnView = this.mColumnViews.get(columnIndex);
        PickerScrollArrayAdapter adapter = (PickerScrollArrayAdapter) columnView.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        columnView.setSelectedPosition(column.getCurrentValue() - column.getMinValue());
    }

    public void setColumnValue(int columnIndex, int value, boolean runAnimation) {
        PickerColumn column = this.mColumns.get(columnIndex);
        if (column.getCurrentValue() != value) {
            column.setCurrentValue(value);
            notifyValueChanged(columnIndex);
            VerticalGridView columnView = this.mColumnViews.get(columnIndex);
            if (columnView != null) {
                int position = value - this.mColumns.get(columnIndex).getMinValue();
                if (runAnimation) {
                    columnView.setSelectedPositionSmooth(position);
                } else {
                    columnView.setSelectedPosition(position);
                }
            }
        }
    }

    private void notifyValueChanged(int columnIndex) {
        if (this.mListeners != null) {
            for (int i = this.mListeners.size() - 1; i >= 0; i--) {
                this.mListeners.get(i).onValueChanged(this, columnIndex);
            }
        }
    }

    void updateColumnAlpha(int colIndex, boolean animate) {
        VerticalGridView column = this.mColumnViews.get(colIndex);
        int selected = column.getSelectedPosition();
        int i = 0;
        while (i < column.getAdapter().getItemCount()) {
            View item = column.getLayoutManager().findViewByPosition(i);
            if (item != null) {
                setOrAnimateAlpha(item, selected == i, colIndex, animate);
            }
            i++;
        }
    }

    void setOrAnimateAlpha(View view, boolean selected, int colIndex, boolean animate) {
        boolean columnShownAsActivated = colIndex == this.mSelectedColumn || !hasFocus();
        if (selected) {
            if (columnShownAsActivated) {
                setOrAnimateAlpha(view, animate, this.mFocusedAlpha, -1.0f, this.mDecelerateInterpolator);
            } else {
                setOrAnimateAlpha(view, animate, this.mUnfocusedAlpha, -1.0f, this.mDecelerateInterpolator);
            }
        } else if (columnShownAsActivated) {
            setOrAnimateAlpha(view, animate, this.mVisibleColumnAlpha, -1.0f, this.mDecelerateInterpolator);
        } else {
            setOrAnimateAlpha(view, animate, this.mInvisibleColumnAlpha, -1.0f, this.mDecelerateInterpolator);
        }
    }

    private void setOrAnimateAlpha(View view, boolean animate, float destAlpha, float startAlpha, Interpolator interpolator) {
        view.animate().cancel();
        if (!animate) {
            view.setAlpha(destAlpha);
            return;
        }
        if (startAlpha >= 0.0f) {
            view.setAlpha(startAlpha);
        }
        view.animate().alpha(destAlpha).setDuration(this.mAlphaAnimDuration).setInterpolator(interpolator).start();
    }

    public void onColumnValueChanged(int columnIndex, int newValue) {
        PickerColumn column = this.mColumns.get(columnIndex);
        if (column.getCurrentValue() != newValue) {
            column.setCurrentValue(newValue);
            notifyValueChanged(columnIndex);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView textView;

        ViewHolder(View v, TextView textView) {
            super(v);
            this.textView = textView;
        }
    }

    /* loaded from: classes.dex */
    class PickerScrollArrayAdapter extends RecyclerView.Adapter<ViewHolder> {
        private final int mColIndex;
        private PickerColumn mData;
        private final int mResource;
        private final int mTextViewResourceId;

        PickerScrollArrayAdapter(Context context, int resource, int textViewResourceId, int colIndex) {
            this.mResource = resource;
            this.mColIndex = colIndex;
            this.mTextViewResourceId = textViewResourceId;
            this.mData = Picker.this.mColumns.get(this.mColIndex);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.support.v7.widget.RecyclerView.Adapter
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            TextView textView;
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View v = inflater.inflate(this.mResource, parent, false);
            if (this.mTextViewResourceId != 0) {
                textView = (TextView) v.findViewById(this.mTextViewResourceId);
            } else {
                textView = (TextView) v;
            }
            ViewHolder vh = new ViewHolder(v, textView);
            return vh;
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (holder.textView != null && this.mData != null) {
                holder.textView.setText(this.mData.getLabelFor(this.mData.getMinValue() + position));
            }
            Picker.this.setOrAnimateAlpha(holder.itemView, Picker.this.mColumnViews.get(this.mColIndex).getSelectedPosition() == position, this.mColIndex, false);
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public void onViewAttachedToWindow(ViewHolder holder) {
            holder.itemView.setFocusable(Picker.this.isActivated());
        }

        @Override // android.support.v7.widget.RecyclerView.Adapter
        public int getItemCount() {
            if (this.mData == null) {
                return 0;
            }
            return this.mData.getCount();
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (isActivated()) {
            int keyCode = event.getKeyCode();
            if (keyCode == 23 || keyCode == 66) {
                if (event.getAction() == 1) {
                    performClick();
                }
                return true;
            }
            return super.dispatchKeyEvent(event);
        }
        return super.dispatchKeyEvent(event);
    }

    @Override // android.view.ViewGroup
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        int column = getSelectedColumn();
        if (column < this.mColumnViews.size()) {
            return this.mColumnViews.get(column).requestFocus(direction, previouslyFocusedRect);
        }
        return false;
    }

    protected int getPickerItemHeightPixels() {
        return getContext().getResources().getDimensionPixelSize(R.dimen.picker_item_height);
    }

    private void updateColumnSize() {
        for (int i = 0; i < getColumnsCount(); i++) {
            updateColumnSize(this.mColumnViews.get(i));
        }
    }

    private void updateColumnSize(VerticalGridView columnView) {
        ViewGroup.LayoutParams lp = columnView.getLayoutParams();
        float itemCount = isActivated() ? getActivatedVisibleItemCount() : getVisibleItemCount();
        lp.height = (int) ((getPickerItemHeightPixels() * itemCount) + (columnView.getVerticalSpacing() * (itemCount - 1.0f)));
        columnView.setLayoutParams(lp);
    }

    private void updateItemFocusable() {
        boolean activated = isActivated();
        for (int i = 0; i < getColumnsCount(); i++) {
            VerticalGridView grid = this.mColumnViews.get(i);
            for (int j = 0; j < grid.getChildCount(); j++) {
                View view = grid.getChildAt(j);
                view.setFocusable(activated);
            }
        }
    }

    public float getActivatedVisibleItemCount() {
        return this.mVisibleItemsActivated;
    }

    public float getVisibleItemCount() {
        return 1.0f;
    }

    @Override // android.view.View
    public void setActivated(boolean activated) {
        if (activated == isActivated()) {
            super.setActivated(activated);
            return;
        }
        super.setActivated(activated);
        boolean hadFocus = hasFocus();
        int column = getSelectedColumn();
        setDescendantFocusability(131072);
        if (!activated && hadFocus && isFocusable()) {
            requestFocus();
        }
        for (int i = 0; i < getColumnsCount(); i++) {
            this.mColumnViews.get(i).setFocusable(activated);
        }
        updateColumnSize();
        updateItemFocusable();
        if (activated && hadFocus && column >= 0) {
            this.mColumnViews.get(column).requestFocus();
        }
        setDescendantFocusability(262144);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestChildFocus(View child, View focused) {
        super.requestChildFocus(child, focused);
        for (int i = 0; i < this.mColumnViews.size(); i++) {
            if (this.mColumnViews.get(i).hasFocus()) {
                setSelectedColumn(i);
            }
        }
    }

    public void setSelectedColumn(int columnIndex) {
        if (this.mSelectedColumn != columnIndex) {
            this.mSelectedColumn = columnIndex;
            for (int i = 0; i < this.mColumnViews.size(); i++) {
                updateColumnAlpha(i, true);
            }
        }
    }

    public int getSelectedColumn() {
        return this.mSelectedColumn;
    }
}
