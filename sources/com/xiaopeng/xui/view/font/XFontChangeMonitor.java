package com.xiaopeng.xui.view.font;

import android.content.Context;
import android.content.res.Configuration;
import com.xiaopeng.xui.view.XViewDelegate;
/* loaded from: classes.dex */
public class XFontChangeMonitor {
    private Context mContext;
    private float mFontScale;
    private XViewDelegate.onFontScaleChangeCallback mOnFontChangeCallback;

    public XFontChangeMonitor(Context context) {
        this.mContext = context;
        this.mFontScale = context.getResources().getConfiguration().fontScale;
    }

    private void checkFont(Configuration configuration) {
        float f = this.mFontScale;
        float f2 = configuration.fontScale;
        if (f != f2) {
            this.mFontScale = f2;
            XViewDelegate.onFontScaleChangeCallback onfontscalechangecallback = this.mOnFontChangeCallback;
            if (onfontscalechangecallback != null) {
                onfontscalechangecallback.onFontScaleChanged();
            }
        }
    }

    public void onAttachedToWindow() {
        checkFont(this.mContext.getResources().getConfiguration());
    }

    public void onConfigurationChanged(Configuration configuration) {
        checkFont(configuration);
    }

    public void setOnFontScaleChangeCallback(XViewDelegate.onFontScaleChangeCallback onfontscalechangecallback) {
        this.mOnFontChangeCallback = onfontscalechangecallback;
    }
}
