package com.xiaopeng.xui.vui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import b.a.a.a.c;
import b.a.a.a.d;
import com.xiaopeng.xui.vui.listenner.VuiRecyclerViewGlobalLayoutListener;
import com.xiaopeng.xui.vui.listenner.VuiRecyclerViewScrollListener;
/* loaded from: classes.dex */
public class VuiRecyclerView extends RecyclerView implements c, VuiView {
    private static final String TAG = "VuiRecyclerView";
    public boolean isVuiCanScrollDown;
    public boolean isVuiCanScrollRight;
    private final RecyclerView.AdapterDataObserver mAdapterDataObserver;
    private VuiRecyclerViewGlobalLayoutListener mGlobalLayoutListener;
    private boolean mIsItemChanged;
    private boolean mIsNeedLayout;
    private boolean mIsNeedScroll;
    private boolean mIsVuiCanControlScroll;
    private Runnable mRun;
    private String mSceneId;
    private VuiRecyclerViewScrollListener mScrollListener;
    private d mVuiEngine;

    /* loaded from: classes.dex */
    class a implements Runnable {
        a() {
        }

        @Override // java.lang.Runnable
        public void run() {
            VuiRecyclerView.this.mVuiEngine.a(VuiRecyclerView.this.mSceneId, VuiRecyclerView.this);
        }
    }

    /* loaded from: classes.dex */
    class b extends RecyclerView.AdapterDataObserver {
        b() {
        }

        @Override // android.support.v7.widget.RecyclerView.AdapterDataObserver
        public void onChanged() {
            if (!VuiRecyclerView.this.mIsNeedLayout || VuiRecyclerView.this.mGlobalLayoutListener == null) {
                return;
            }
            VuiRecyclerView.this.mGlobalLayoutListener.setNeedUpdate(true);
        }

        @Override // android.support.v7.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeChanged(int i, int i2) {
            if (VuiRecyclerView.this.mIsItemChanged) {
                VuiRecyclerView.this.updateVuiScene();
            }
        }

        @Override // android.support.v7.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeChanged(int i, int i2, Object obj) {
            if (VuiRecyclerView.this.mIsItemChanged) {
                VuiRecyclerView.this.updateVuiScene();
            }
        }

        @Override // android.support.v7.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeInserted(int i, int i2) {
            if (!VuiRecyclerView.this.mIsNeedLayout || VuiRecyclerView.this.mGlobalLayoutListener == null) {
                return;
            }
            VuiRecyclerView.this.mGlobalLayoutListener.setNeedUpdate(true);
        }

        @Override // android.support.v7.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeMoved(int i, int i2, int i3) {
            if (!VuiRecyclerView.this.mIsNeedLayout || VuiRecyclerView.this.mGlobalLayoutListener == null) {
                return;
            }
            VuiRecyclerView.this.mGlobalLayoutListener.setNeedUpdate(true);
        }

        @Override // android.support.v7.widget.RecyclerView.AdapterDataObserver
        public void onItemRangeRemoved(int i, int i2) {
            if (!VuiRecyclerView.this.mIsNeedLayout || VuiRecyclerView.this.mGlobalLayoutListener == null) {
                return;
            }
            VuiRecyclerView.this.mGlobalLayoutListener.setNeedUpdate(true);
        }
    }

    public VuiRecyclerView(Context context) {
        super(context);
        this.mIsVuiCanControlScroll = true;
        this.mRun = new a();
        this.mIsNeedScroll = true;
        this.mIsNeedLayout = true;
        this.mIsItemChanged = false;
        this.mAdapterDataObserver = new b();
        this.isVuiCanScrollDown = false;
        this.isVuiCanScrollRight = false;
        initListener();
    }

    public VuiRecyclerView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mIsVuiCanControlScroll = true;
        this.mRun = new a();
        this.mIsNeedScroll = true;
        this.mIsNeedLayout = true;
        this.mIsItemChanged = false;
        this.mAdapterDataObserver = new b();
        this.isVuiCanScrollDown = false;
        this.isVuiCanScrollRight = false;
        initVui(this, attributeSet);
        initListener();
    }

    public VuiRecyclerView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mIsVuiCanControlScroll = true;
        this.mRun = new a();
        this.mIsNeedScroll = true;
        this.mIsNeedLayout = true;
        this.mIsItemChanged = false;
        this.mAdapterDataObserver = new b();
        this.isVuiCanScrollDown = false;
        this.isVuiCanScrollRight = false;
        initVui(this, attributeSet);
        initListener();
    }

    private void initListener() {
        this.mGlobalLayoutListener = new VuiRecyclerViewGlobalLayoutListener(this);
        this.mScrollListener = new VuiRecyclerViewScrollListener(this);
        setVuiLayoutLoadable(true);
    }

    protected void finalize() {
        super.finalize();
        releaseVui();
    }

    public void initVuiAttr(String str, d dVar) {
        this.mSceneId = str;
        this.mVuiEngine = dVar;
    }

    public void initVuiAttr(String str, d dVar, boolean z) {
        this.mSceneId = str;
        this.mVuiEngine = dVar;
        this.mIsItemChanged = z;
    }

    public void initVuiAttr(String str, d dVar, boolean z, boolean z2) {
        this.mSceneId = str;
        this.mIsNeedScroll = z;
        this.mIsNeedLayout = z2;
        this.mVuiEngine = dVar;
    }

    public void initVuiAttr(String str, d dVar, boolean z, boolean z2, boolean z3) {
        this.mSceneId = str;
        this.mIsNeedScroll = z;
        this.mIsNeedLayout = z2;
        this.mVuiEngine = dVar;
        this.mIsItemChanged = z3;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.widget.RecyclerView, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        VuiRecyclerViewScrollListener vuiRecyclerViewScrollListener;
        super.onAttachedToWindow();
        Log.i(TAG, "onAttachedToWindow:" + this.mSceneId);
        if (this.mIsNeedLayout && this.mGlobalLayoutListener != null) {
            getViewTreeObserver().addOnGlobalLayoutListener(this.mGlobalLayoutListener);
        }
        if (!this.mIsNeedScroll || (vuiRecyclerViewScrollListener = this.mScrollListener) == null) {
            return;
        }
        addOnScrollListener(vuiRecyclerViewScrollListener);
    }

    /* JADX WARN: Code restructure failed: missing block: B:34:0x006d, code lost:
        if (r7.isVuiCanScrollRight == false) goto L26;
     */
    /* JADX WARN: Removed duplicated region for block: B:20:0x003b A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:39:0x003c A[EXC_TOP_SPLITTER, SYNTHETIC] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public b.a.a.a.k.a onBuildVuiElement(java.lang.String r8, b.a.a.a.b r9) {
        /*
            r7 = this;
            boolean r8 = r7.mIsVuiCanControlScroll
            r9 = 0
            if (r8 != 0) goto L6
            return r9
        L6:
            r8 = -1
            boolean r0 = r7.canScrollVertically(r8)
            r1 = 1
            boolean r2 = r7.canScrollVertically(r1)
            boolean r8 = r7.canScrollHorizontally(r8)
            boolean r3 = r7.canScrollHorizontally(r1)
            if (r0 != 0) goto L2c
            if (r2 != 0) goto L2c
            boolean r4 = r7.isVuiCanScrollDown
            if (r4 == 0) goto L21
            goto L2c
        L21:
            if (r8 != 0) goto L29
            if (r3 != 0) goto L29
            boolean r4 = r7.isVuiCanScrollRight
            if (r4 == 0) goto L35
        L29:
            b.a.a.a.f r4 = b.a.a.a.f.SCROLLBYX
            goto L2e
        L2c:
            b.a.a.a.f r4 = b.a.a.a.f.SCROLLBYY
        L2e:
            java.lang.String r4 = r4.a()
            r7.setVuiAction(r4)
        L35:
            java.lang.String r4 = r7.getVuiAction()
            if (r4 != 0) goto L3c
            return r9
        L3c:
            org.json.JSONObject r4 = new org.json.JSONObject     // Catch: org.json.JSONException -> L76
            r4.<init>()     // Catch: org.json.JSONException -> L76
            java.lang.String r5 = r7.getVuiAction()     // Catch: org.json.JSONException -> L76
            b.a.a.a.f r6 = b.a.a.a.f.SCROLLBYY     // Catch: org.json.JSONException -> L76
            java.lang.String r6 = r6.a()     // Catch: org.json.JSONException -> L76
            boolean r5 = r5.equals(r6)     // Catch: org.json.JSONException -> L76
            r6 = 0
            if (r5 == 0) goto L62
            java.lang.String r8 = "canScrollUp"
            r4.put(r8, r0)     // Catch: org.json.JSONException -> L76
            java.lang.String r8 = "canScrollDown"
            if (r2 != 0) goto L6f
            boolean r0 = r7.isVuiCanScrollDown     // Catch: org.json.JSONException -> L76
            if (r0 == 0) goto L60
            goto L6f
        L60:
            r1 = r6
            goto L6f
        L62:
            java.lang.String r0 = "canScrollLeft"
            r4.put(r0, r8)     // Catch: org.json.JSONException -> L76
            java.lang.String r8 = "canScrollRight"
            if (r3 != 0) goto L6f
            boolean r0 = r7.isVuiCanScrollRight     // Catch: org.json.JSONException -> L76
            if (r0 == 0) goto L60
        L6f:
            r4.put(r8, r1)     // Catch: org.json.JSONException -> L76
            r7.setVuiProps(r4)     // Catch: org.json.JSONException -> L76
            return r9
        L76:
            r8 = move-exception
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.xui.vui.VuiRecyclerView.onBuildVuiElement(java.lang.String, b.a.a.a.b):b.a.a.a.k.a");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.widget.RecyclerView, android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        VuiRecyclerViewScrollListener vuiRecyclerViewScrollListener;
        super.onDetachedFromWindow();
        Log.i(TAG, "onDetachedFromWindow:" + this.mSceneId);
        if (this.mIsNeedLayout && this.mGlobalLayoutListener != null) {
            getViewTreeObserver().removeOnGlobalLayoutListener(this.mGlobalLayoutListener);
        }
        if (!this.mIsNeedScroll || (vuiRecyclerViewScrollListener = this.mScrollListener) == null) {
            return;
        }
        removeOnScrollListener(vuiRecyclerViewScrollListener);
    }

    @Override // b.a.a.a.c
    public boolean onVuiElementEvent(View view, b.a.a.a.k.b bVar) {
        return false;
    }

    @Override // android.support.v7.widget.RecyclerView
    public void setAdapter(RecyclerView.Adapter adapter) {
        VuiRecyclerViewGlobalLayoutListener vuiRecyclerViewGlobalLayoutListener;
        RecyclerView.Adapter adapter2 = getAdapter();
        if (adapter2 != null) {
            adapter2.unregisterAdapterDataObserver(this.mAdapterDataObserver);
        }
        super.setAdapter(adapter);
        if (adapter != null) {
            adapter.registerAdapterDataObserver(this.mAdapterDataObserver);
        }
        if (!this.mIsNeedLayout || (vuiRecyclerViewGlobalLayoutListener = this.mGlobalLayoutListener) == null) {
            return;
        }
        vuiRecyclerViewGlobalLayoutListener.setNeedUpdate(true);
    }

    public void setCanVuiScrollDown(boolean z) {
        this.isVuiCanScrollDown = z;
    }

    public void setCanVuiScrollRight(boolean z) {
        this.isVuiCanScrollRight = z;
    }

    @Override // android.view.View
    public void setVisibility(int i) {
        super.setVisibility(i);
        updateVuiScene();
    }

    public void setVuiCanControlScroll(boolean z) {
        this.mIsVuiCanControlScroll = z;
    }

    public void updateVuiScene() {
        if (TextUtils.isEmpty(this.mSceneId) || this.mVuiEngine == null) {
            Log.i(TAG, "updateVuiScene sceneid is empty");
            return;
        }
        removeCallbacks(this.mRun);
        post(this.mRun);
    }

    public void updateVuiScene(int i) {
        if (TextUtils.isEmpty(this.mSceneId) || this.mVuiEngine == null) {
            Log.i(TAG, "updateVuiScene sceneid is empty");
            return;
        }
        removeCallbacks(this.mRun);
        postDelayed(this.mRun, i);
    }
}
