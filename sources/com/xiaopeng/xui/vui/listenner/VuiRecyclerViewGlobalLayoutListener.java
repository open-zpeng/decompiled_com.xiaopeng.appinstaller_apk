package com.xiaopeng.xui.vui.listenner;

import android.view.ViewTreeObserver;
import com.xiaopeng.xui.vui.VuiRecyclerView;
/* loaded from: classes.dex */
public class VuiRecyclerViewGlobalLayoutListener implements ViewTreeObserver.OnGlobalLayoutListener {
    private boolean isNeedUpdate = true;
    private VuiRecyclerView mVuiRecycleView;

    public VuiRecyclerViewGlobalLayoutListener(VuiRecyclerView vuiRecyclerView) {
        this.mVuiRecycleView = null;
        this.mVuiRecycleView = vuiRecyclerView;
    }

    @Override // android.view.ViewTreeObserver.OnGlobalLayoutListener
    public void onGlobalLayout() {
        VuiRecyclerView vuiRecyclerView;
        if (!this.isNeedUpdate || (vuiRecyclerView = this.mVuiRecycleView) == null) {
            return;
        }
        vuiRecyclerView.updateVuiScene();
        this.isNeedUpdate = false;
    }

    public void setNeedUpdate(boolean z) {
        this.isNeedUpdate = z;
    }
}
