package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;
import b.a.a.a.b;
import b.a.a.a.c;
import b.a.a.a.f;
import b.a.a.a.k.a;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.xui.view.XViewDelegate;
import com.xiaopeng.xui.vui.VuiView;
import java.util.Collections;
/* loaded from: classes.dex */
public class XScrollView extends ScrollView implements c, VuiView {
    protected XViewDelegate mXViewDelegate;

    public XScrollView(Context context) {
        super(context);
        init(null, 0, 0);
    }

    public XScrollView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init(attributeSet, 0, 0);
    }

    public XScrollView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
        init(attributeSet, i, 0);
    }

    public XScrollView(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        init(attributeSet, i, i2);
    }

    private void init(AttributeSet attributeSet, int i, int i2) {
        this.mXViewDelegate = XViewDelegate.create(this, attributeSet, i, i2, Collections.singletonList(ThemeManager.AttributeSet.SCROLLBAR_THUMB_VERTICAL));
        initVui(this, attributeSet);
    }

    protected void finalize() {
        super.finalize();
        releaseVui();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null) {
            xViewDelegate.onAttachedToWindow();
        }
    }

    public a onBuildVuiElement(String str, b bVar) {
        View childAt = getChildAt(0);
        if (childAt != null && getHeight() < childAt.getMeasuredHeight()) {
            setVuiAction(f.SCROLLBYY.a());
        }
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

    @Override // android.widget.ScrollView, android.view.ViewGroup, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        XViewDelegate xViewDelegate = this.mXViewDelegate;
        if (xViewDelegate != null) {
            xViewDelegate.onDetachedFromWindow();
        }
    }

    @Override // b.a.a.a.c
    public boolean onVuiElementEvent(View view, b.a.a.a.k.b bVar) {
        return false;
    }
}
