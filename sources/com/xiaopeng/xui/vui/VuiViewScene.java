package com.xiaopeng.xui.vui;

import android.util.Log;
import android.view.View;
import b.a.a.a.c;
import b.a.a.a.d;
import b.a.a.a.e;
import b.a.a.a.k.b;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.vui.floatinglayer.VuiFloatingLayerManager;
import java.util.List;
import xiaopeng.widget.BuildConfig;
/* loaded from: classes.dex */
public abstract class VuiViewScene implements e, IVuiViewScene {
    private View mRootView;
    private d mVuiEngine = null;
    private String mSceneId = BuildConfig.FLAVOR;
    private c mVuiElementListener = null;
    private List<Integer> customViewIds = null;
    private View.OnAttachStateChangeListener mOnAttachStateChangeListener = new a();

    /* loaded from: classes.dex */
    class a implements View.OnAttachStateChangeListener {
        a() {
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewAttachedToWindow(View view) {
            if (Xui.isVuiEnable()) {
                VuiViewScene.this.createVuiScene();
            }
        }

        @Override // android.view.View.OnAttachStateChangeListener
        public void onViewDetachedFromWindow(View view) {
            if (Xui.isVuiEnable()) {
                VuiViewScene.this.destroyVuiScene();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void createVuiScene() {
        if (this.mVuiEngine == null || this.mSceneId == null || this.mRootView == null) {
            return;
        }
        log("createVuiScene");
        this.mVuiEngine.a(this.mSceneId, this.mRootView, this);
        this.mVuiEngine.c(this.mSceneId);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void destroyVuiScene() {
        if (this.mVuiEngine != null && this.mSceneId != null) {
            log("destroyVuiScene");
            this.mVuiEngine.b(this.mSceneId);
            this.mVuiEngine.a(this.mSceneId);
            this.mVuiEngine = null;
        }
        if (this.mVuiElementListener != null) {
            this.mVuiElementListener = null;
        }
    }

    private void log(String str) {
        Log.d("VuiViewScene", " mSceneId " + this.mSceneId + "  " + str + " hashcode " + hashCode() + " name " + getClass().getSimpleName());
    }

    public void onBuildScene() {
        if (this.mVuiEngine == null || this.mSceneId == null || this.mRootView == null) {
            return;
        }
        log("onBuildScene");
        onBuildScenePrepare();
        this.mVuiEngine.a(this.mSceneId, this.mRootView, this.customViewIds, this.mVuiElementListener);
    }

    protected abstract void onBuildScenePrepare();

    public boolean onInterceptVuiEvent(View view, b bVar) {
        log("onInterceptVuiEvent");
        if (view == null) {
            return false;
        }
        c cVar = this.mVuiElementListener;
        if (cVar != null) {
            return cVar.onVuiElementEvent(view, bVar);
        }
        VuiFloatingLayerManager.show(view);
        return false;
    }

    public void onVuiEvent(View view, b bVar) {
        log("VuiViewScene onVuiEvent");
        if (view == null) {
            return;
        }
        c cVar = this.mVuiElementListener;
        if (cVar != null) {
            cVar.onVuiElementEvent(view, bVar);
        } else {
            VuiFloatingLayerManager.show(view);
        }
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void setCustomViewIdList(List<Integer> list) {
        this.customViewIds = list;
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void setVuiElementListener(c cVar) {
        this.mVuiElementListener = cVar;
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void setVuiEngine(d dVar) {
        this.mVuiEngine = dVar;
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void setVuiSceneId(String str) {
        this.mSceneId = str;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setVuiView(View view) {
        log("initVui");
        if (Xui.isVuiEnable()) {
            View view2 = this.mRootView;
            if (view2 != null) {
                view2.removeOnAttachStateChangeListener(this.mOnAttachStateChangeListener);
            }
            this.mRootView = view;
            view.addOnAttachStateChangeListener(this.mOnAttachStateChangeListener);
        }
    }
}
