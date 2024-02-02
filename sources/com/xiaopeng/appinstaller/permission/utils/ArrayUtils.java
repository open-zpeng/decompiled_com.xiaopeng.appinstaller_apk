package com.xiaopeng.appinstaller.permission.utils;

import android.text.TextUtils;
import java.util.Objects;
/* loaded from: classes.dex */
public final class ArrayUtils {
    public static <T> boolean contains(T[] array, T value) {
        return indexOf(array, value) != -1;
    }

    public static <T> int indexOf(T[] array, T value) {
        if (array == null) {
            return -1;
        }
        for (int i = 0; i < array.length; i++) {
            if (Objects.equals(array[i], value)) {
                return i;
            }
        }
        return -1;
    }

    public static String[] appendString(String[] cur, String val) {
        if (cur == null) {
            return new String[]{val};
        }
        int N = cur.length;
        for (String str : cur) {
            if (TextUtils.equals(str, val)) {
                return cur;
            }
        }
        int i = N + 1;
        String[] ret = new String[i];
        System.arraycopy(cur, 0, ret, 0, N);
        ret[N] = val;
        return ret;
    }
}
