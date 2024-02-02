package com.xiaopeng.xui.view;

import android.content.res.Configuration;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;
import com.xiaopeng.libtheme.ThemeViewModel;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.view.XViewDelegate;
import com.xiaopeng.xui.view.delegate.XViewDelegatePart;
import com.xiaopeng.xui.view.font.XFontChangeMonitor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
class a extends XViewDelegate {

    /* renamed from: a  reason: collision with root package name */
    private View f62a;

    /* renamed from: b  reason: collision with root package name */
    private ThemeViewModel f63b;
    private XFontChangeMonitor c;
    private ArrayList<XViewDelegatePart> d = new ArrayList<>();

    /* JADX INFO: Access modifiers changed from: package-private */
    public a(View view, AttributeSet attributeSet, int i, int i2, List<String> list) {
        XViewDelegatePart createFont;
        this.f62a = view;
        if (Xui.isFontScaleDynamicChangeEnable() && (view instanceof TextView) && (createFont = XViewDelegatePart.createFont((TextView) view, attributeSet, i, i2)) != null) {
            this.d.add(createFont);
        }
        this.f63b = ThemeViewModel.create(view.getContext(), attributeSet, i, i2, list);
    }

    @Override // com.xiaopeng.xui.view.XViewDelegate
    public ThemeViewModel getThemeViewModel() {
        return this.f63b;
    }

    @Override // com.xiaopeng.xui.view.XViewDelegate
    public void onAttachedToWindow() {
        Iterator<XViewDelegatePart> it = this.d.iterator();
        while (it.hasNext()) {
            it.next().onAttachedToWindow();
        }
        ThemeViewModel themeViewModel = this.f63b;
        if (themeViewModel != null) {
            themeViewModel.onAttachedToWindow(this.f62a);
        }
        XFontChangeMonitor xFontChangeMonitor = this.c;
        if (xFontChangeMonitor != null) {
            xFontChangeMonitor.onAttachedToWindow();
        }
    }

    @Override // com.xiaopeng.xui.view.XViewDelegate
    public void onConfigurationChanged(Configuration configuration) {
        Iterator<XViewDelegatePart> it = this.d.iterator();
        while (it.hasNext()) {
            it.next().onConfigurationChanged(configuration);
        }
        ThemeViewModel themeViewModel = this.f63b;
        if (themeViewModel != null) {
            themeViewModel.onConfigurationChanged(this.f62a, configuration);
        }
        XFontChangeMonitor xFontChangeMonitor = this.c;
        if (xFontChangeMonitor != null) {
            xFontChangeMonitor.onConfigurationChanged(configuration);
        }
    }

    @Override // com.xiaopeng.xui.view.XViewDelegate
    public void onDetachedFromWindow() {
        Iterator<XViewDelegatePart> it = this.d.iterator();
        while (it.hasNext()) {
            it.next().onDetachedFromWindow();
        }
        ThemeViewModel themeViewModel = this.f63b;
        if (themeViewModel != null) {
            themeViewModel.onDetachedFromWindow(this.f62a);
        }
    }

    @Override // com.xiaopeng.xui.view.XViewDelegate
    public void onWindowFocusChanged(boolean z) {
        ThemeViewModel themeViewModel = this.f63b;
        if (themeViewModel != null) {
            themeViewModel.onWindowFocusChanged(this.f62a, z);
        }
    }

    @Override // com.xiaopeng.xui.view.XViewDelegate
    public void setFontScaleChangeCallback(XViewDelegate.onFontScaleChangeCallback onfontscalechangecallback) {
        if (onfontscalechangecallback != null && this.c == null) {
            this.c = new XFontChangeMonitor(this.f62a.getContext());
        }
        this.c.setOnFontScaleChangeCallback(onfontscalechangecallback);
    }
}
