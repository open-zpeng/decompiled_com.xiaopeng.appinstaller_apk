package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.util.Log;
import com.xiaopeng.libtheme.ThemeViewModel;
import com.xiaopeng.xui.utils.XBackgroundPaddingUtils;
import com.xiaopeng.xui.view.XViewDelegate;
import com.xiaopeng.xui.vui.VuiView;
/* loaded from: classes.dex */
public class XImageButton extends AppCompatImageButton implements VuiView {
    Rect mRectBgPadding;
    protected XViewDelegate mXViewDelegate;

    public XImageButton(Context context) {
        super(context);
    }

    public XImageButton(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.mXViewDelegate = XViewDelegate.create(this, attributeSet);
        init(attributeSet);
    }

    public XImageButton(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        this.mXViewDelegate = XViewDelegate.create(this, attributeSet, i);
        init(attributeSet);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void a() {
        post(new Runnable() { // from class: com.xiaopeng.xui.widget.-$$Lambda$XImageButton$BOyb95w0oY6gmty6WDxaVEUspP0
            @Override // java.lang.Runnable
            public final void run() {
                XImageButton.this.b();
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void b() {
        if (this.mRectBgPadding != null) {
            Log.v("xpui", "XImageButton change theme reset backgroundPadding");
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
        this.mXViewDelegate.getThemeViewModel().setCallback(new ThemeViewModel.OnCallback() { // from class: com.xiaopeng.xui.widget.-$$Lambda$XImageButton$leoSeoV1VKVapoD9oMtA8PzbMKI
            @Override // com.xiaopeng.libtheme.ThemeViewModel.OnCallback
            public final void onThemeChanged() {
                XImageButton.this.a();
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

    @Override // android.widget.ImageView, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null) {
            xViewDelegate.onAttachedToWindow();
        }
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null) {
            xViewDelegate.onConfigurationChanged(configuration);
        }
    }

    @Override // android.widget.ImageView, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null) {
            xViewDelegate.onDetachedFromWindow();
        }
    }

    @Override // android.support.v7.widget.AppCompatImageButton, android.widget.ImageView
    public void setImageResource(int i) {
        super.setImageResource(i);
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate == null || xViewDelegate.getThemeViewModel() == null) {
            return;
        }
        this.mXViewDelegate.getThemeViewModel().setImageResource(i);
    }
}
