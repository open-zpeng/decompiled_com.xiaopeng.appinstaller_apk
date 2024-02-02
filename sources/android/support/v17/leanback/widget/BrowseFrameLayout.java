package android.support.v17.leanback.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
/* loaded from: classes.dex */
public class BrowseFrameLayout extends FrameLayout {
    private OnFocusSearchListener mListener;
    private OnChildFocusListener mOnChildFocusListener;
    private View.OnKeyListener mOnDispatchKeyListener;

    /* loaded from: classes.dex */
    public interface OnChildFocusListener {
        void onRequestChildFocus(View view, View view2);

        boolean onRequestFocusInDescendants(int i, Rect rect);
    }

    /* loaded from: classes.dex */
    public interface OnFocusSearchListener {
        View onFocusSearch(View view, int i);
    }

    public BrowseFrameLayout(Context context) {
        this(context, null, 0);
    }

    public BrowseFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BrowseFrameLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override // android.view.ViewGroup
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        if (this.mOnChildFocusListener != null && this.mOnChildFocusListener.onRequestFocusInDescendants(direction, previouslyFocusedRect)) {
            return true;
        }
        return super.onRequestFocusInDescendants(direction, previouslyFocusedRect);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public View focusSearch(View focused, int direction) {
        View view;
        if (this.mListener != null && (view = this.mListener.onFocusSearch(focused, direction)) != null) {
            return view;
        }
        return super.focusSearch(focused, direction);
    }

    @Override // android.view.ViewGroup, android.view.ViewParent
    public void requestChildFocus(View child, View focused) {
        if (this.mOnChildFocusListener != null) {
            this.mOnChildFocusListener.onRequestChildFocus(child, focused);
        }
        super.requestChildFocus(child, focused);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        boolean consumed = super.dispatchKeyEvent(event);
        if (this.mOnDispatchKeyListener != null && !consumed) {
            return this.mOnDispatchKeyListener.onKey(getRootView(), event.getKeyCode(), event);
        }
        return consumed;
    }
}
