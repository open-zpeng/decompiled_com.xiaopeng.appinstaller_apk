package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.Log;
import com.xiaopeng.libtheme.ThemeViewModel;
import com.xiaopeng.xui.utils.XBackgroundPaddingUtils;
import com.xiaopeng.xui.view.XViewDelegate;
import com.xiaopeng.xui.vui.VuiView;
/* loaded from: classes.dex */
public class XButton extends AppCompatButton implements VuiView {
    private Rect mRectBgPadding;
    protected XViewDelegate mXViewDelegate;

    public XButton(Context context) {
        super(context);
    }

    public XButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mXViewDelegate = XViewDelegate.create(this, attributeSet);
        init(attributeSet);
    }

    public XButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mXViewDelegate = XViewDelegate.create(this, attributeSet, i);
        init(attributeSet);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void a() {
        post(new Runnable() { // from class: com.xiaopeng.xui.widget.-$$Lambda$XButton$oGoyYszmUhAAaxvo1dW6sU8MZg8
            @Override // java.lang.Runnable
            public final void run() {
                XButton.this.b();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void b() {
        if (this.mRectBgPadding != null) {
            Log.v("xpui", "XButton change theme reset backgroundPadding");
            Rect rect = this.mRectBgPadding;
            this.mRectBgPadding = XBackgroundPaddingUtils.backgroundPadding(this, rect.left, rect.top, rect.right, rect.bottom);
        }
    }

    private void init(AttributeSet attributeSet) {
        initVui(this, attributeSet);
        this.mRectBgPadding = XBackgroundPaddingUtils.backgroundPadding(this, attributeSet);
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate == null || xViewDelegate.getThemeViewModel() == null) {
            return;
        }
        this.mXViewDelegate.getThemeViewModel().setCallback(new ThemeViewModel.OnCallback() { // from class: com.xiaopeng.xui.widget.-$$Lambda$XButton$uFZuFpAGdwxwpA_PLcqysHA15_I
            @Override // com.xiaopeng.libtheme.ThemeViewModel.OnCallback
            public final void onThemeChanged() {
                XButton.this.a();
            }
        });
    }

    public void backgroundPadding(int i, int i2, int i3, int i4) {
        this.mRectBgPadding = XBackgroundPaddingUtils.backgroundPadding(this, i, i2, i3, i4);
    }

    protected void finalize() {
        super.finalize();
        releaseVui();
    }

    @Override // android.widget.TextView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null) {
            xViewDelegate.onAttachedToWindow();
        }
    }

    @Override // android.widget.TextView, android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null) {
            xViewDelegate.onConfigurationChanged(configuration);
        }
    }

    @Override // android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null) {
            xViewDelegate.onDetachedFromWindow();
        }
    }
}
