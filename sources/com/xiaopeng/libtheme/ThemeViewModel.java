package com.xiaopeng.libtheme;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import com.xiaopeng.libtheme.ThemeManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import xiaopeng.widget.BuildConfig;
/* loaded from: classes.dex */
public class ThemeViewModel {
    private HashMap<String, Integer> mAttributeSet;
    private OnCallback mCallback;
    private List<OnCallback> mCallbacks = new ArrayList();
    private int mUiMode;

    /* loaded from: classes.dex */
    public interface OnCallback {
        void onThemeChanged();
    }

    private ThemeViewModel(Context context, HashMap<String, Integer> hashMap) {
        this.mUiMode = context.getResources().getConfiguration().uiMode;
        this.mAttributeSet = hashMap == null ? new HashMap<>() : hashMap;
    }

    public static ThemeViewModel create(Context context) {
        return new ThemeViewModel(context, new HashMap());
    }

    public static ThemeViewModel create(Context context, AttributeSet attributeSet) {
        return new ThemeViewModel(context, ThemeManager.resolveAttribute(context, attributeSet, 0, 0, null));
    }

    public static ThemeViewModel create(Context context, AttributeSet attributeSet, int i, int i2) {
        return new ThemeViewModel(context, ThemeManager.resolveAttribute(context, attributeSet, i, i2, null));
    }

    public static ThemeViewModel create(Context context, AttributeSet attributeSet, int i, int i2, List<String> list) {
        return new ThemeViewModel(context, ThemeManager.resolveAttribute(context, attributeSet, i, i2, list));
    }

    private String getAttrInfo() {
        try {
            return (this.mAttributeSet == null || this.mAttributeSet.keySet() == null) ? BuildConfig.FLAVOR : Arrays.toString(this.mAttributeSet.keySet().toArray());
        } catch (Exception e) {
            return BuildConfig.FLAVOR;
        }
    }

    private String getViewInfo(View view) {
        return view != null ? view.toString() : "view null";
    }

    private boolean isUiModeChanged(int i) {
        return i != this.mUiMode;
    }

    private void updateThemeResource(View view) {
        ThemeManager.Logger.log("ThemeViewModel updateThemeResource view=" + getViewInfo(view) + " attr=" + getAttrInfo());
        ThemeManager.updateAttribute(view, this.mAttributeSet);
        OnCallback onCallback = this.mCallback;
        if (onCallback != null) {
            onCallback.onThemeChanged();
        }
        List<OnCallback> list = this.mCallbacks;
        if (list == null || list.size() <= 0) {
            return;
        }
        for (OnCallback onCallback2 : this.mCallbacks) {
            if (onCallback2 != null) {
                onCallback2.onThemeChanged();
            }
        }
    }

    public void addCallback(OnCallback onCallback) {
        List<OnCallback> list;
        if (onCallback == null || (list = this.mCallbacks) == null) {
            return;
        }
        list.add(onCallback);
    }

    public void onAttachedToWindow(View view) {
        int uiMode = ThemeManager.getUiMode(view.getContext());
        ThemeManager.Logger.log("ThemeViewModel onAttachedToWindow newMode=" + uiMode + " oldMode=" + this.mUiMode + " view=" + getViewInfo(view));
        if (isUiModeChanged(uiMode)) {
            updateThemeResource(view);
        }
        this.mUiMode = uiMode;
    }

    public void onConfigurationChanged(View view, Configuration configuration) {
        int i = configuration != null ? configuration.uiMode : 0;
        boolean isThemeChanged = ThemeManager.isThemeChanged(configuration);
        ThemeManager.Logger.log("ThemeViewModel onConfigurationChanged isThemeChanged=" + isThemeChanged + " uiMode=" + i + " view=" + getViewInfo(view));
        if (isThemeChanged) {
            updateThemeResource(view);
            this.mUiMode = configuration != null ? configuration.uiMode : this.mUiMode;
        }
    }

    public void onDetachedFromWindow(View view) {
    }

    public void onWindowFocusChanged(View view, boolean z) {
        if (z) {
            int uiMode = ThemeManager.getUiMode(view.getContext());
            ThemeManager.Logger.log("ThemeViewModel onWindowFocusChanged newMode=" + uiMode + " oldMode=" + this.mUiMode + " view=" + getViewInfo(view));
            if (isUiModeChanged(uiMode)) {
                updateThemeResource(view);
            }
            this.mUiMode = uiMode;
        }
    }

    public void onWindowVisibilityChanged(View view, int i) {
        if (i == 0) {
            int uiMode = ThemeManager.getUiMode(view.getContext());
            ThemeManager.Logger.log("ThemeViewModel onWindowVisibilityChanged newMode=" + uiMode + " oldMode=" + this.mUiMode + " view=" + getViewInfo(view));
            if (isUiModeChanged(uiMode)) {
                updateThemeResource(view);
            }
            this.mUiMode = uiMode;
        }
    }

    public void removeCallback(OnCallback onCallback) {
        List<OnCallback> list;
        if (onCallback == null || (list = this.mCallbacks) == null || !list.contains(onCallback)) {
            return;
        }
        this.mCallbacks.remove(onCallback);
    }

    public void setBackground(Drawable drawable) {
        this.mAttributeSet.put(ThemeManager.AttributeSet.BACKGROUND, 0);
    }

    public void setBackgroundColor(int i) {
        this.mAttributeSet.put(ThemeManager.AttributeSet.BACKGROUND, 0);
    }

    public void setBackgroundDrawable(Drawable drawable) {
        this.mAttributeSet.put(ThemeManager.AttributeSet.BACKGROUND, 0);
    }

    public void setBackgroundResource(int i) {
        this.mAttributeSet.put(ThemeManager.AttributeSet.BACKGROUND, Integer.valueOf(i));
    }

    public void setCallback(OnCallback onCallback) {
        this.mCallback = onCallback;
    }

    public void setImageBitmap(Bitmap bitmap) {
        this.mAttributeSet.put(ThemeManager.AttributeSet.SRC, 0);
    }

    public void setImageDrawable(Drawable drawable) {
        this.mAttributeSet.put(ThemeManager.AttributeSet.SRC, 0);
    }

    public void setImageResource(int i) {
        this.mAttributeSet.put(ThemeManager.AttributeSet.SRC, Integer.valueOf(i));
    }

    public void setTextColor(int i) {
        this.mAttributeSet.put(ThemeManager.AttributeSet.TEXT_COLOR, Integer.valueOf(i));
    }

    public void setThemeAttribute(String str, int i) {
        if (TextUtils.isEmpty(str) || !this.mAttributeSet.containsKey(str)) {
            return;
        }
        this.mAttributeSet.put(str, Integer.valueOf(i));
    }
}
