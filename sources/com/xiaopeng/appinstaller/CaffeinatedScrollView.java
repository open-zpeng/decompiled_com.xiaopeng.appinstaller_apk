package com.xiaopeng.appinstaller;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.ScrollView;
/* loaded from: classes.dex */
class CaffeinatedScrollView extends ScrollView {
    private int mBottomSlop;
    private Runnable mFullScrollAction;

    public CaffeinatedScrollView(Context context) {
        super(context);
    }

    public CaffeinatedScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override // android.view.View
    public boolean awakenScrollBars() {
        return super.awakenScrollBars();
    }

    public void setFullScrollAction(Runnable action) {
        this.mFullScrollAction = action;
        this.mBottomSlop = (int) (4.0f * getResources().getDisplayMetrics().density);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        checkFullScrollAction();
    }

    @Override // android.view.View
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        checkFullScrollAction();
    }

    private void checkFullScrollAction() {
        if (this.mFullScrollAction != null) {
            int daBottom = getChildAt(0).getBottom();
            int screenBottom = (getScrollY() + getHeight()) - getPaddingBottom();
            if (daBottom - screenBottom < this.mBottomSlop) {
                this.mFullScrollAction.run();
                this.mFullScrollAction = null;
            }
        }
    }
}
