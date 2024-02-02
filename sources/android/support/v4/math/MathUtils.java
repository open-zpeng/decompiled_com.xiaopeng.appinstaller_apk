package android.support.v4.math;
/* loaded from: classes.dex */
public class MathUtils {
    public static int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
}
