package com.xiaopeng.xui.view.delegate;

import android.content.res.Configuration;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;
/* loaded from: classes.dex */
public abstract class XViewDelegatePart {
    public static XViewDelegatePart createFont(TextView textView, AttributeSet attributeSet, int i, int i2) {
        return XViewDelegateFontScale.create(textView, attributeSet, i, i2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void log(String str) {
        Log.d("XViewDelegatePart", getClass().getSimpleName() + "----" + str);
    }

    public abstract void onAttachedToWindow();

    public abstract void onConfigurationChanged(Configuration configuration);

    public abstract void onDetachedFromWindow();
}
