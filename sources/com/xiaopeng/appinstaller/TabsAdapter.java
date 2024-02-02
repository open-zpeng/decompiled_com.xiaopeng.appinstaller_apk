package com.xiaopeng.appinstaller;

import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import android.widget.TabWidget;
import java.util.ArrayList;
/* loaded from: classes.dex */
public class TabsAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener, TabHost.OnTabChangeListener {
    private final Context mContext;
    private final TabHost mTabHost;
    private final ArrayList<TabInfo> mTabs = new ArrayList<>();
    private final Rect mTempRect = new Rect();
    private final ViewPager mViewPager;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class TabInfo {
        private final String tag;
        private final View view;

        TabInfo(String _tag, View _view) {
            this.tag = _tag;
            this.view = _view;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class DummyTabFactory implements TabHost.TabContentFactory {
        private final Context mContext;

        public DummyTabFactory(Context context) {
            this.mContext = context;
        }

        @Override // android.widget.TabHost.TabContentFactory
        public View createTabContent(String tag) {
            View v = new View(this.mContext);
            v.setMinimumWidth(0);
            v.setMinimumHeight(0);
            return v;
        }
    }

    public TabsAdapter(Activity activity, TabHost tabHost, ViewPager pager) {
        this.mContext = activity;
        this.mTabHost = tabHost;
        this.mViewPager = pager;
        this.mTabHost.setOnTabChangedListener(this);
        this.mViewPager.setAdapter(this);
        this.mViewPager.setOnPageChangeListener(this);
    }

    public void addTab(TabHost.TabSpec tabSpec, View view) {
        tabSpec.setContent(new DummyTabFactory(this.mContext));
        String tag = tabSpec.getTag();
        TabInfo info = new TabInfo(tag, view);
        this.mTabs.add(info);
        this.mTabHost.addTab(tabSpec);
        notifyDataSetChanged();
    }

    @Override // android.support.v4.view.PagerAdapter
    public int getCount() {
        return this.mTabs.size();
    }

    @Override // android.support.v4.view.PagerAdapter
    public Object instantiateItem(ViewGroup container, int position) {
        View view = this.mTabs.get(position).view;
        container.addView(view);
        return view;
    }

    @Override // android.support.v4.view.PagerAdapter
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override // android.support.v4.view.PagerAdapter
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override // android.widget.TabHost.OnTabChangeListener
    public void onTabChanged(String tabId) {
        int position = this.mTabHost.getCurrentTab();
        this.mViewPager.setCurrentItem(position);
    }

    @Override // android.support.v4.view.ViewPager.OnPageChangeListener
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override // android.support.v4.view.ViewPager.OnPageChangeListener
    public void onPageSelected(int position) {
        TabWidget widget = this.mTabHost.getTabWidget();
        int oldFocusability = widget.getDescendantFocusability();
        widget.setDescendantFocusability(393216);
        this.mTabHost.setCurrentTab(position);
        widget.setDescendantFocusability(oldFocusability);
        View tab = widget.getChildTabViewAt(position);
        this.mTempRect.set(tab.getLeft(), tab.getTop(), tab.getRight(), tab.getBottom());
        widget.requestRectangleOnScreen(this.mTempRect, false);
        View contentView = this.mTabs.get(position).view;
        if (contentView instanceof CaffeinatedScrollView) {
            ((CaffeinatedScrollView) contentView).awakenScrollBars();
        }
    }

    @Override // android.support.v4.view.ViewPager.OnPageChangeListener
    public void onPageScrollStateChanged(int state) {
    }
}
