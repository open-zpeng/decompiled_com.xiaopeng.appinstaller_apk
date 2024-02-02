package com.xiaopeng.xui.utils;

import android.text.TextPaint;
import android.util.Log;
import android.widget.TextView;
import xiaopeng.widget.BuildConfig;
/* loaded from: classes.dex */
public class XTextLayoutUtils {
    private static final boolean mcIsPrinterLog = false;

    public static String adjustLastLineWords(TextView textView, String str, int i, int i2) {
        String str2;
        String replace = str.replace("\n", BuildConfig.FLAVOR);
        TextPaint paint = textView.getPaint();
        float measureText = paint.measureText(replace);
        log("width " + measureText + ", lastLinMinWidth  " + i + ", maxWidth  " + i2);
        float f = (float) i2;
        if (measureText < f) {
            log(" width is less than the given value ");
            return replace;
        }
        int i3 = i2 / 2;
        if (i > i3) {
            i = i3 - 50;
            log(" lastLinMinWidth more than 1 and a half. lastLinMinWidth reset to " + i);
        }
        int length = replace.length() - 1;
        int i4 = ((int) (measureText / f)) + (measureText % f == 0.0f ? 0 : 1);
        if (i4 > textView.getMaxLines()) {
            str2 = " lines " + i4 + ", getMaxLines " + textView.getMaxLines();
        } else {
            int length2 = replace.length() - 1;
            float f2 = 0.0f;
            while (true) {
                if (length2 < 0) {
                    break;
                }
                char charAt = replace.charAt(length2);
                f2 += paint.measureText(String.valueOf(charAt));
                if (isNotEmojiCharacter(charAt) && f2 > i) {
                    length = length2;
                    break;
                }
                length2--;
            }
            str2 = " lastLinMinWidthIndex " + length + ", length  " + replace.length();
        }
        log(str2);
        StringBuilder sb = new StringBuilder();
        float f3 = 0.0f;
        for (int i5 = 0; i5 < replace.length(); i5++) {
            char charAt2 = replace.charAt(i5);
            f3 += paint.measureText(String.valueOf(charAt2));
            if (f3 >= f && isNotEmojiCharacter(charAt2)) {
                if (i5 < length) {
                    sb.append("\n");
                } else {
                    sb.insert(length, "\n");
                }
                f3 = 0.0f;
            }
            sb.append(charAt2);
        }
        return sb.toString();
    }

    public static boolean isChinese(char c) {
        return c >= 19968 && c <= 40869;
    }

    public static boolean isNotEmojiCharacter(char c) {
        if (c == 0 || c == '\t' || c == '\n' || c == '\r') {
            return true;
        }
        if (c < ' ' || c > 55295) {
            if (c < 57344 || c > 65533) {
                return c >= 0 && c <= 65535;
            }
            return true;
        }
        return true;
    }

    private static void log(String str) {
        Log.v("xui-toast-text", str);
    }
}
