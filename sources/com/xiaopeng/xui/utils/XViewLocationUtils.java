package com.xiaopeng.xui.utils;

import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import xiaopeng.widget.BuildConfig;
/* loaded from: classes.dex */
public class XViewLocationUtils {
    private static final int FADING_EDGE_LENGTH = 60;

    /* loaded from: classes.dex */
    public interface OnCorrectionLocationListener {
        void onCorrectionLocationEnd(View view);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public static /* synthetic */ void a(View view, ViewGroup viewGroup, OnCorrectionLocationListener onCorrectionLocationListener) {
        Rect rect = new Rect();
        boolean localVisibleRect = view.getLocalVisibleRect(rect);
        int height = view.getHeight();
        String str = BuildConfig.FLAVOR;
        int i = rect.top;
        if (i < 0) {
            viewGroup.scrollBy(0, i - 60);
            str = "top all ";
        } else if (i > 0) {
            if (rect.height() < height) {
                viewGroup.scrollBy(0, (-rect.top) - 60);
                str = "top a part ";
            } else {
                viewGroup.scrollBy(0, (rect.bottom - viewGroup.getHeight()) + 60);
                str = "bottom all ";
            }
        } else if (rect.height() < height) {
            viewGroup.scrollBy(0, (height - rect.bottom) + 60);
            str = "bottom a part ";
        }
        log("scrollByLocation localVisible : " + localVisibleRect + ", top " + rect.top + ", bottom " + rect.bottom + ", rect h:" + rect.height() + ",h:" + height + ",  " + str);
        onCorrectionLocationListener.onCorrectionLocationEnd(view);
    }

    private static ViewGroup isInScrollContainer(View view) {
        for (ViewParent parent = view.getParent(); parent instanceof ViewGroup; parent = parent.getParent()) {
            ViewGroup viewGroup = (ViewGroup) parent;
            if (viewGroup.shouldDelayChildPressedState()) {
                return viewGroup;
            }
        }
        return null;
    }

    private static void log(String str) {
        Log.d("ViewLocation", str);
    }

    public static void scrollByLocation(View view, OnCorrectionLocationListener onCorrectionLocationListener) {
        ViewGroup isInScrollContainer = isInScrollContainer(view);
        if (isInScrollContainer != null) {
            scrollByLocation(isInScrollContainer, view, onCorrectionLocationListener);
        } else {
            onCorrectionLocationListener.onCorrectionLocationEnd(view);
        }
    }

    public static void scrollByLocation(final ViewGroup viewGroup, final View view, final OnCorrectionLocationListener onCorrectionLocationListener) {
        view.post(new Runnable() { // from class: com.xiaopeng.xui.utils.-$$Lambda$XViewLocationUtils$SRSzpVtqHZsT85-3ucY4j0pjS2Q
            @Override // java.lang.Runnable
            public final void run() {
                XViewLocationUtils.a(view, viewGroup, onCorrectionLocationListener);
            }
        });
    }
}
