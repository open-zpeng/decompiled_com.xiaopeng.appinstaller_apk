package android.support.v17.leanback.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
/* loaded from: classes.dex */
class PlaybackControlsRowView extends LinearLayout {
    private OnUnhandledKeyListener mOnUnhandledKeyListener;

    /* loaded from: classes.dex */
    public interface OnUnhandledKeyListener {
        boolean onUnhandledKey(KeyEvent keyEvent);
    }

    public PlaybackControlsRowView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlaybackControlsRowView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (super.dispatchKeyEvent(event)) {
            return true;
        }
        return this.mOnUnhandledKeyListener != null && this.mOnUnhandledKeyListener.onUnhandledKey(event);
    }

    @Override // android.view.ViewGroup
    protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
        View focused = findFocus();
        if (focused != null && focused.requestFocus(direction, previouslyFocusedRect)) {
            return true;
        }
        return super.onRequestFocusInDescendants(direction, previouslyFocusedRect);
    }

    @Override // android.view.View
    public boolean hasOverlappingRendering() {
        return false;
    }
}
