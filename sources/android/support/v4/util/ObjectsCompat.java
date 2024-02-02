package android.support.v4.util;

import android.os.Build;
import java.util.Arrays;
import java.util.Objects;
/* loaded from: classes.dex */
public class ObjectsCompat {
    public static boolean equals(Object a2, Object b2) {
        if (Build.VERSION.SDK_INT >= 19) {
            return Objects.equals(a2, b2);
        }
        return a2 == b2 || (a2 != null && a2.equals(b2));
    }

    public static int hash(Object... values) {
        if (Build.VERSION.SDK_INT >= 19) {
            return Objects.hash(values);
        }
        return Arrays.hashCode(values);
    }
}
