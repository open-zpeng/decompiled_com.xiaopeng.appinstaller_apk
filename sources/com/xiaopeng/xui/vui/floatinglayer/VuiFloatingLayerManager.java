package com.xiaopeng.xui.vui.floatinglayer;

import android.view.View;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.utils.XViewLocationUtils;
import com.xiaopeng.xui.vui.VuiView;
/* loaded from: classes.dex */
public class VuiFloatingLayerManager {
    private static IVuiFloatingLayer mVuiFloatingLayer;

    private VuiFloatingLayerManager() {
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void a(View view, int i, int i2, int i3, View view2) {
        mVuiFloatingLayer.showFloatingLayer(view, i, i2, i3);
    }

    public static void hide() {
        hide(0);
    }

    public static void hide(int i) {
        IVuiFloatingLayer iVuiFloatingLayer = mVuiFloatingLayer;
        if (iVuiFloatingLayer != null) {
            iVuiFloatingLayer.hideFloatingLayer(i);
        }
    }

    public static void show(View view) {
        show(view, 0);
    }

    public static void show(View view, int i) {
        show(view, i, 0, 0);
    }

    public static void show(View view, int i, int i2) {
        show(view, 0, i, i2);
    }

    private static void show(View view, int i, int i2, int i3) {
        show(view, i, i2, i3, true);
    }

    private static void show(final View view, final int i, final int i2, final int i3, boolean z) {
        if (i == 0 && (view instanceof VuiView) && ((VuiView) view).getVuiDisableHitEffect()) {
            return;
        }
        if (mVuiFloatingLayer == null) {
            mVuiFloatingLayer = new VuiFloatingLayer(Xui.getContext());
        }
        if (z) {
            XViewLocationUtils.scrollByLocation(view, new XViewLocationUtils.OnCorrectionLocationListener() { // from class: com.xiaopeng.xui.vui.floatinglayer.-$$Lambda$VuiFloatingLayerManager$Vw1BaAnEdj4Rae_RLmVvR-PzpHE
                @Override // com.xiaopeng.xui.utils.XViewLocationUtils.OnCorrectionLocationListener
                public final void onCorrectionLocationEnd(View view2) {
                    VuiFloatingLayerManager.a(view, i, i2, i3, view2);
                }
            });
        } else {
            mVuiFloatingLayer.showFloatingLayer(view, i, i2, i3);
        }
    }

    public static void show(View view, boolean z) {
        show(view, 0, 0, 0, false);
    }
}
