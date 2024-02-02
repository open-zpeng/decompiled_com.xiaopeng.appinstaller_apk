package android.support.v7.app;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.graphics.drawable.DrawerArrowDrawable;
import android.view.View;
/* loaded from: classes.dex */
public class ActionBarDrawerToggle implements DrawerLayout.DrawerListener {
    private final Delegate mActivityImpl;
    private final int mCloseDrawerContentDescRes;
    boolean mDrawerIndicatorEnabled;
    private boolean mDrawerSlideAnimationEnabled;
    private final int mOpenDrawerContentDescRes;
    private DrawerArrowDrawable mSlider;

    /* loaded from: classes.dex */
    public interface Delegate {
        void setActionBarDescription(int i);
    }

    @Override // android.support.v4.widget.DrawerLayout.DrawerListener
    public void onDrawerSlide(View drawerView, float slideOffset) {
        if (this.mDrawerSlideAnimationEnabled) {
            setPosition(Math.min(1.0f, Math.max(0.0f, slideOffset)));
        } else {
            setPosition(0.0f);
        }
    }

    @Override // android.support.v4.widget.DrawerLayout.DrawerListener
    public void onDrawerOpened(View drawerView) {
        setPosition(1.0f);
        if (this.mDrawerIndicatorEnabled) {
            setActionBarDescription(this.mCloseDrawerContentDescRes);
        }
    }

    @Override // android.support.v4.widget.DrawerLayout.DrawerListener
    public void onDrawerClosed(View drawerView) {
        setPosition(0.0f);
        if (this.mDrawerIndicatorEnabled) {
            setActionBarDescription(this.mOpenDrawerContentDescRes);
        }
    }

    @Override // android.support.v4.widget.DrawerLayout.DrawerListener
    public void onDrawerStateChanged(int newState) {
    }

    void setActionBarDescription(int contentDescRes) {
        this.mActivityImpl.setActionBarDescription(contentDescRes);
    }

    private void setPosition(float position) {
        if (position == 1.0f) {
            this.mSlider.setVerticalMirror(true);
        } else if (position == 0.0f) {
            this.mSlider.setVerticalMirror(false);
        }
        this.mSlider.setProgress(position);
    }
}
