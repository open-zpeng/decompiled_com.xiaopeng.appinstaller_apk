package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.View;
import b.a.a.a.b;
import b.a.a.a.c;
import b.a.a.a.k.a;
import com.xiaopeng.xui.view.XViewDelegate;
import com.xiaopeng.xui.vui.VuiView;
import com.xiaopeng.xui.vui.floatinglayer.VuiFloatingLayerManager;
/* loaded from: classes.dex */
public class XSeekBar extends AppCompatSeekBar implements c, VuiView {
    protected XViewDelegate mXViewDelegate;

    public XSeekBar(Context context) {
        super(context);
    }

    public XSeekBar(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mXViewDelegate = XViewDelegate.create(this, attributeSet);
        initVui(this, attributeSet);
    }

    public XSeekBar(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mXViewDelegate = XViewDelegate.create(this, attributeSet, i, 0);
        initVui(this, attributeSet);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void a(Double d, View view) {
        if (d.doubleValue() >= 0.0d && d.doubleValue() <= 100.0d) {
            setProgress(d.intValue());
        }
        VuiFloatingLayerManager.show(view, (getWidth() / 2) - ((getProgress() / getMax()) * getWidth()), 0);
    }

    protected void finalize() {
        super.finalize();
        releaseVui();
    }

    @Override // android.widget.ProgressBar, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null) {
            xViewDelegate.onAttachedToWindow();
        }
    }

    public a onBuildVuiElement(String str, b bVar) {
        return null;
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null) {
            xViewDelegate.onConfigurationChanged(configuration);
        }
    }

    @Override // android.widget.ProgressBar, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null) {
            xViewDelegate.onDetachedFromWindow();
        }
    }

    @Override // b.a.a.a.c
    public boolean onVuiElementEvent(final View view, b.a.a.a.k.b bVar) {
        final Double d;
        log("SeekBar onVuiElementEvent");
        if (view == null || (d = (Double) bVar.a(bVar)) == null) {
            return false;
        }
        post(new Runnable() { // from class: com.xiaopeng.xui.widget.-$$Lambda$XSeekBar$_BR2mTd1pqQ1sKC8dVpo7Df13nU
            @Override // java.lang.Runnable
            public final void run() {
                XSeekBar.this.a(d, view);
            }
        });
        return true;
    }
}
