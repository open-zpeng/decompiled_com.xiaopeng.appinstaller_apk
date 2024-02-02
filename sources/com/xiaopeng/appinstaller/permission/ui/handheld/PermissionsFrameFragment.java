package com.xiaopeng.appinstaller.permission.ui.handheld;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;
import android.widget.TextView;
import com.xiaopeng.appinstaller.R;
/* loaded from: classes.dex */
public abstract class PermissionsFrameFragment extends PreferenceFragment {
    private boolean mIsLoading;
    private View mLoadingView;
    private ViewGroup mPreferencesContainer;
    private ViewGroup mPrefsView;

    @Override // android.preference.PreferenceFragment, android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.permissions_frame, container, false);
        this.mPrefsView = (ViewGroup) rootView.findViewById(R.id.prefs_container);
        if (this.mPrefsView == null) {
            this.mPrefsView = rootView;
        }
        this.mLoadingView = rootView.findViewById(R.id.loading_container);
        this.mPreferencesContainer = (ViewGroup) super.onCreateView(inflater, this.mPrefsView, savedInstanceState);
        setLoading(this.mIsLoading, false, true);
        this.mPrefsView.addView(this.mPreferencesContainer);
        return rootView;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void setLoading(boolean loading, boolean animate) {
        setLoading(loading, animate, false);
    }

    private void setLoading(boolean loading, boolean animate, boolean force) {
        if (this.mIsLoading != loading || force) {
            this.mIsLoading = loading;
            if (getView() == null) {
                animate = false;
            }
            if (this.mPrefsView != null) {
                setViewShown(this.mPrefsView, !loading, animate);
            }
            if (this.mLoadingView != null) {
                setViewShown(this.mLoadingView, loading, animate);
            }
        }
    }

    public ListView getListView() {
        ListView listView = super.getListView();
        if (listView.getEmptyView() == null) {
            TextView emptyView = (TextView) getView().findViewById(R.id.no_permissions);
            listView.setEmptyView(emptyView);
        }
        return listView;
    }

    private void setViewShown(final View view, boolean shown, boolean animate) {
        if (animate) {
            Animation animation = AnimationUtils.loadAnimation(getContext(), shown ? 17432576 : 17432577);
            if (shown) {
                view.setVisibility(0);
            } else {
                animation.setAnimationListener(new Animation.AnimationListener() { // from class: com.xiaopeng.appinstaller.permission.ui.handheld.PermissionsFrameFragment.1
                    @Override // android.view.animation.Animation.AnimationListener
                    public void onAnimationStart(Animation animation2) {
                    }

                    @Override // android.view.animation.Animation.AnimationListener
                    public void onAnimationRepeat(Animation animation2) {
                    }

                    @Override // android.view.animation.Animation.AnimationListener
                    public void onAnimationEnd(Animation animation2) {
                        view.setVisibility(4);
                    }
                });
            }
            view.startAnimation(animation);
            return;
        }
        view.clearAnimation();
        view.setVisibility(shown ? 0 : 4);
    }
}
