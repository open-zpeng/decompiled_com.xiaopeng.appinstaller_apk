package com.xiaopeng.xui.vui.floatinglayer;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.ImageDecoder;
import android.graphics.drawable.Drawable;
import android.util.Log;
import java.io.IOException;
/* loaded from: classes.dex */
public class VuiImageDecoderUtils {
    private static final String TAG = "VuiImageDecoderUtils";
    private static final String TOUCH_DEFAULT_WEBP = "anim/floating_touch.webp";
    private static final String TOUCH_ELEMENT_WEBP = "anim/floating_element.webp";
    private static final String TOUCH_ELEMENT_WEBP_NIGHT = "anim/floating_element_night.webp";

    @TargetApi(28)
    public static Drawable decoderImage(Context context, int i) {
        return decoderImage(context, i, false);
    }

    @TargetApi(28)
    public static Drawable decoderImage(Context context, int i, boolean z) {
        String str = TAG;
        Log.d(str, "decoderImage type : " + i + ", isNight : " + z);
        try {
            return ImageDecoder.decodeDrawable(ImageDecoder.createSource(context.getAssets(), i != 1 ? TOUCH_DEFAULT_WEBP : z ? TOUCH_ELEMENT_WEBP_NIGHT : TOUCH_ELEMENT_WEBP));
        } catch (IOException e) {
            Log.d(TAG, "decodeException:", e);
            return null;
        }
    }

    public static int getAnimateTimeOut(int i) {
        return i != 1 ? 1500 : 5500;
    }

    public static boolean isSupportAlpha(int i) {
        return i == 1;
    }

    public static boolean isSupportNight(int i) {
        return i == 1;
    }
}
