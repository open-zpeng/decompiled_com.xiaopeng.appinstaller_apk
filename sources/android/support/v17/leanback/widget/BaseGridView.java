package android.support.v17.leanback.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.v17.leanback.R;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
/* loaded from: classes.dex */
public abstract class BaseGridView extends RecyclerView {
    private boolean mAnimateChildLayout;
    RecyclerView.RecyclerListener mChainedRecyclerListener;
    private boolean mHasOverlappingRendering;
    int mInitialPrefetchItemCount;
    final GridLayoutManager mLayoutManager;
    private OnKeyInterceptListener mOnKeyInterceptListener;
    private OnMotionInterceptListener mOnMotionInterceptListener;
    private OnTouchInterceptListener mOnTouchInterceptListener;
    private OnUnhandledKeyListener mOnUnhandledKeyListener;

    /* loaded from: classes.dex */
    public interface OnKeyInterceptListener {
        boolean onInterceptKeyEvent(KeyEvent keyEvent);
    }

    /* loaded from: classes.dex */
    public interface OnMotionInterceptListener {
        boolean onInterceptMotionEvent(MotionEvent motionEvent);
    }

    /* loaded from: classes.dex */
    public interface OnTouchInterceptListener {
        boolean onInterceptTouchEvent(MotionEvent motionEvent);
    }

    /* loaded from: classes.dex */
    public interface OnUnhandledKeyListener {
        boolean onUnhandledKey(KeyEvent keyEvent);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public BaseGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mAnimateChildLayout = true;
        this.mHasOverlappingRendering = true;
        this.mInitialPrefetchItemCount = 4;
        this.mLayoutManager = new GridLayoutManager(this);
        setLayoutManager(this.mLayoutManager);
        setPreserveFocusAfterLayout(false);
        setDescendantFocusability(262144);
        setHasFixedSize(true);
        setChildrenDrawingOrderEnabled(true);
        setWillNotDraw(true);
        setOverScrollMode(2);
        ((SimpleItemAnimator) getItemAnimator()).setSupportsChangeAnimations(false);
        super.setRecyclerListener(new RecyclerView.RecyclerListener() { // from class: android.support.v17.leanback.widget.BaseGridView.1
            @Override // android.support.v7.widget.RecyclerView.RecyclerListener
            public void onViewRecycled(RecyclerView.ViewHolder holder) {
                BaseGridView.this.mLayoutManager.onChildRecycled(holder);
                if (BaseGridView.this.mChainedRecyclerListener != null) {
                    BaseGridView.this.mChainedRecyclerListener.onViewRecycled(holder);
                }
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void initBaseGridViewAttributes(Context context, AttributeSet attrs) {
        TypedArray a2 = context.obtainStyledAttributes(attrs, R.styleable.lbBaseGridView);
        boolean throughFront = a2.getBoolean(R.styleable.lbBaseGridView_focusOutFront, false);
        boolean throughEnd = a2.getBoolean(R.styleable.lbBaseGridView_focusOutEnd, false);
        this.mLayoutManager.setFocusOutAllowed(throughFront, throughEnd);
        boolean throughSideStart = a2.getBoolean(R.styleable.lbBaseGridView_focusOutSideStart, true);
        boolean throughSideEnd = a2.getBoolean(R.styleable.lbBaseGridView_focusOutSideEnd, true);
        this.mLayoutManager.setFocusOutSideAllowed(throughSideStart, throughSideEnd);
        this.mLayoutManager.setVerticalSpacing(a2.getDimensionPixelSize(R.styleable.lbBaseGridView_android_verticalSpacing, a2.getDimensionPixelSize(R.styleable.lbBaseGridView_verticalMargin, 0)));
        this.mLayoutManager.setHorizontalSpacing(a2.getDimensionPixelSize(R.styleable.lbBaseGridView_android_horizontalSpacing, a2.getDimensionPixelSize(R.styleable.lbBaseGridView_horizontalMargin, 0)));
        if (a2.hasValue(R.styleable.lbBaseGridView_android_gravity)) {
            setGravity(a2.getInt(R.styleable.lbBaseGridView_android_gravity, 0));
        }
        a2.recycle();
    }

    public void setWindowAlignment(int windowAlignment) {
        this.mLayoutManager.setWindowAlignment(windowAlignment);
        requestLayout();
    }

    public int getVerticalSpacing() {
        return this.mLayoutManager.getVerticalSpacing();
    }

    public void setOnChildViewHolderSelectedListener(OnChildViewHolderSelectedListener listener) {
        this.mLayoutManager.setOnChildViewHolderSelectedListener(listener);
    }

    public void setSelectedPosition(int position) {
        this.mLayoutManager.setSelection(position, 0);
    }

    public void setSelectedPositionSmooth(int position) {
        this.mLayoutManager.setSelectionSmooth(position);
    }

    public int getSelectedPosition() {
        return this.mLayoutManager.getSelection();
    }

    public void setGravity(int gravity) {
        this.mLayoutManager.setGravity(gravity);
        requestLayout();
    }

    @Override // android.support.v7.widget.RecyclerView, android.view.ViewGroup
    public boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        return this.mLayoutManager.gridOnRequestFocusInDescendants(this, direction, previouslyFocusedRect);
    }

    @Override // android.support.v7.widget.RecyclerView, android.view.ViewGroup
    public int getChildDrawingOrder(int childCount, int i) {
        return this.mLayoutManager.getChildDrawingOrder(this, childCount, i);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final boolean isChildrenDrawingOrderEnabledInternal() {
        return isChildrenDrawingOrderEnabled();
    }

    @Override // android.view.View
    public View focusSearch(int direction) {
        View view;
        if (isFocused() && (view = this.mLayoutManager.findViewByPosition(this.mLayoutManager.getSelection())) != null) {
            return focusSearch(view, direction);
        }
        return super.focusSearch(direction);
    }

    @Override // android.view.View
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        this.mLayoutManager.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        if ((this.mOnKeyInterceptListener == null || !this.mOnKeyInterceptListener.onInterceptKeyEvent(event)) && !super.dispatchKeyEvent(event)) {
            return this.mOnUnhandledKeyListener != null && this.mOnUnhandledKeyListener.onUnhandledKey(event);
        }
        return true;
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (this.mOnTouchInterceptListener != null && this.mOnTouchInterceptListener.onInterceptTouchEvent(event)) {
            return true;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override // android.view.ViewGroup, android.view.View
    protected boolean dispatchGenericFocusedEvent(MotionEvent event) {
        if (this.mOnMotionInterceptListener != null && this.mOnMotionInterceptListener.onInterceptMotionEvent(event)) {
            return true;
        }
        return super.dispatchGenericFocusedEvent(event);
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return this.mHasOverlappingRendering;
    }

    @Override // android.view.View
    public void onRtlPropertiesChanged(int layoutDirection) {
        this.mLayoutManager.onRtlPropertiesChanged(layoutDirection);
    }

    @Override // android.support.v7.widget.RecyclerView
    public void setRecyclerListener(RecyclerView.RecyclerListener listener) {
        this.mChainedRecyclerListener = listener;
    }

    @Override // android.support.v7.widget.RecyclerView
    public void scrollToPosition(int position) {
        if (this.mLayoutManager.isSlidingChildViews()) {
            this.mLayoutManager.setSelectionWithSub(position, 0, 0);
        } else {
            super.scrollToPosition(position);
        }
    }

    @Override // android.support.v7.widget.RecyclerView
    public void smoothScrollToPosition(int position) {
        if (this.mLayoutManager.isSlidingChildViews()) {
            this.mLayoutManager.setSelectionWithSub(position, 0, 0);
        } else {
            super.smoothScrollToPosition(position);
        }
    }
}
