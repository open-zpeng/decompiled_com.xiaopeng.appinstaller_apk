package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import b.a.a.a.b;
import b.a.a.a.c;
import b.a.a.a.g;
import b.a.a.a.i;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.utils.XTouchAreaUtils;
import com.xiaopeng.xui.widget.XTabLayout;
import xiaopeng.widget.BuildConfig;
/* loaded from: classes.dex */
public class XTabsBar extends XRelativeLayout implements c {
    private XImageButton mIconBtn;
    private ViewGroup mIconBtnLay;
    private OnTabsBarClickListener mOnTabChangeListener;
    private XTabLayout mTabLayout;

    /* loaded from: classes.dex */
    public interface OnTabsBarClickListener extends XTabLayout.OnTabChangeListener {
        void onTabsBarCloseClick(View view);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class a implements XTabLayout.OnTabChangeListener {
        a() {
        }

        @Override // com.xiaopeng.xui.widget.XTabLayout.OnTabChangeListener
        public boolean onInterceptTabChange(XTabLayout xTabLayout, int i, boolean z, boolean z2) {
            if (XTabsBar.this.mOnTabChangeListener != null) {
                return XTabsBar.this.mOnTabChangeListener.onInterceptTabChange(xTabLayout, i, z, z2);
            }
            return false;
        }

        @Override // com.xiaopeng.xui.widget.XTabLayout.OnTabChangeListener
        public void onTabChangeEnd(XTabLayout xTabLayout, int i, boolean z, boolean z2) {
            if (XTabsBar.this.mOnTabChangeListener != null) {
                XTabsBar.this.mOnTabChangeListener.onTabChangeEnd(xTabLayout, i, z, z2);
            }
        }

        @Override // com.xiaopeng.xui.widget.XTabLayout.OnTabChangeListener
        public void onTabChangeStart(XTabLayout xTabLayout, int i, boolean z, boolean z2) {
            if (XTabsBar.this.mOnTabChangeListener != null) {
                XTabsBar.this.mOnTabChangeListener.onTabChangeStart(xTabLayout, i, z, z2);
            }
        }
    }

    public XTabsBar(Context context) {
        this(context, null);
    }

    public XTabsBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public XTabsBar(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, 0);
    }

    public XTabsBar(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        initView(context, attributeSet, i, i2);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void a(View view) {
        OnTabsBarClickListener onTabsBarClickListener = this.mOnTabChangeListener;
        if (onTabsBarClickListener != null) {
            onTabsBarClickListener.onTabsBarCloseClick(view);
        }
    }

    private void initView(Context context, AttributeSet attributeSet, int i, int i2) {
        Resources.Theme theme = getContext().getTheme();
        if (i == 0) {
            i = R.style.XTabsBarAppearance_Left;
        }
        TypedArray obtainStyledAttributes = theme.obtainStyledAttributes(attributeSet, R.styleable.XTabsBar, i, i);
        float dimension = obtainStyledAttributes.getDimension(R.styleable.XTabsBar_tabsBarMarginStart, dp(24));
        float dimension2 = obtainStyledAttributes.getDimension(R.styleable.XTabsBar_tabsBarTabGap, 0.0f);
        float dimension3 = obtainStyledAttributes.getDimension(R.styleable.XTabsBar_tabsBarTabWidth, dp(ThemeManager.UI_MODE_THEME_MASK));
        float dimension4 = obtainStyledAttributes.getDimension(R.styleable.XTabsBar_tabsBarTabHeight, dp(130));
        int integer = obtainStyledAttributes.getInteger(R.styleable.XTabsBar_tabsBarBtnVisibility, 0);
        int integer2 = obtainStyledAttributes.getInteger(R.styleable.XTabsBar_tabsBarTitleVisibility, 8);
        obtainStyledAttributes.recycle();
        LayoutInflater.from(context).inflate(R.layout.x_tabsbar, this);
        this.mIconBtn = (XImageButton) findViewById(R.id.x_tabsbar_btn_close);
        this.mIconBtn.setVisibility(integer);
        this.mIconBtn.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.xui.widget.-$$Lambda$XTabsBar$TYh-q9rc2F7qksMQ1rRmiwOOSUc
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                XTabsBar.this.a(view);
            }
        });
        this.mIconBtnLay = (ViewGroup) findViewById(R.id.x_x_tabsbar_btn_close_lay);
        ((XTextView) findViewById(R.id.x_tabsbar_tv_title)).setVisibility(integer2);
        this.mTabLayout = new XTabLayout(context, attributeSet, i, i2);
        int tabCount = this.mTabLayout.getTabCount();
        int i3 = (int) ((dimension3 * tabCount) + (dimension2 * (tabCount - 1)));
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        if (tabCount == 0) {
            i3 = (int) (displayMetrics.widthPixels - dimension);
        }
        int i4 = getResources().getConfiguration().orientation;
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(i3, (int) dimension4);
        if (i4 == 1) {
            layoutParams.addRule(14);
        } else {
            layoutParams.addRule(9);
            layoutParams.leftMargin = (int) dimension;
        }
        addView(this.mTabLayout, layoutParams);
        this.mTabLayout.setOnTabChangeListener(new a());
    }

    public void addTab(String str) {
        XTabLayout xTabLayout = this.mTabLayout;
        if (xTabLayout != null) {
            xTabLayout.addTab(str);
        }
    }

    public void addTab(String str, int i) {
        XTabLayout xTabLayout = this.mTabLayout;
        if (xTabLayout != null) {
            xTabLayout.addTab(str, i);
        }
    }

    protected int dp(int i) {
        return (int) TypedValue.applyDimension(1, i, getResources().getDisplayMetrics());
    }

    public int getSelectedTabIndex() {
        XTabLayout xTabLayout = this.mTabLayout;
        if (xTabLayout != null) {
            return xTabLayout.getSelectedTabIndex();
        }
        return -1;
    }

    public int getTabCount() {
        XTabLayout xTabLayout = this.mTabLayout;
        if (xTabLayout != null) {
            return xTabLayout.getTabCount();
        }
        return 0;
    }

    public CharSequence getTabTitle(int i) {
        XTabLayout xTabLayout = this.mTabLayout;
        return xTabLayout != null ? xTabLayout.getTabTitle(i) : BuildConfig.FLAVOR;
    }

    @Override // android.view.View
    public boolean isEnabled() {
        XTabLayout xTabLayout = this.mTabLayout;
        return xTabLayout != null && xTabLayout.isEnabled();
    }

    public boolean isEnabled(int i) {
        XTabLayout xTabLayout = this.mTabLayout;
        return xTabLayout != null && xTabLayout.isEnabled(i);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XRelativeLayout, android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        XTouchAreaUtils.extendTouchAreaAsParentSameSize(this.mIconBtn, this.mIconBtnLay);
    }

    public b.a.a.a.k.a onBuildVuiElement(String str, b bVar) {
        this.mTabLayout.setVuiElementType(g.XTABLAYOUT);
        this.mTabLayout.setVuiMode(i.NORMAL);
        return null;
    }

    @Override // b.a.a.a.c
    public boolean onVuiElementEvent(View view, b.a.a.a.k.b bVar) {
        return false;
    }

    public void removeTab(int i) {
        XTabLayout xTabLayout = this.mTabLayout;
        if (xTabLayout != null) {
            xTabLayout.removeTab(i);
        }
    }

    public void removeTab(int i, int i2) {
        XTabLayout xTabLayout = this.mTabLayout;
        if (xTabLayout != null) {
            xTabLayout.removeTab(i, i2);
        }
    }

    public void selectTab(int i) {
        XTabLayout xTabLayout = this.mTabLayout;
        if (xTabLayout != null) {
            xTabLayout.selectTab(i, true);
        }
    }

    public void selectTab(int i, boolean z) {
        XTabLayout xTabLayout = this.mTabLayout;
        if (xTabLayout != null) {
            xTabLayout.selectTab(i, z);
        }
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        XTabLayout xTabLayout = this.mTabLayout;
        if (xTabLayout != null) {
            xTabLayout.setEnabled(z);
        }
    }

    public void setEnabled(boolean z, int i) {
        XTabLayout xTabLayout = this.mTabLayout;
        if (xTabLayout != null) {
            xTabLayout.setEnabled(z, i);
        }
    }

    public void setOnTabsClickListener(OnTabsBarClickListener onTabsBarClickListener) {
        this.mOnTabChangeListener = onTabsBarClickListener;
    }

    public void setTabLayoutVisible(boolean z) {
        XTabLayout xTabLayout = this.mTabLayout;
        if (xTabLayout != null) {
            xTabLayout.setVisibility(z ? 0 : 8);
        }
    }

    public void updateTabTitle(int i, String str) {
        XTabLayout xTabLayout = this.mTabLayout;
        if (xTabLayout != null) {
            xTabLayout.updateTabTitle(i, str);
        }
    }
}
