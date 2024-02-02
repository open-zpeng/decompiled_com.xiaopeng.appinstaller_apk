package android.support.design.math;
/* loaded from: classes.dex */
public final class MathUtils {
    public static float max(float a2, float b2, float c, float d) {
        return (a2 <= b2 || a2 <= c || a2 <= d) ? (b2 <= c || b2 <= d) ? c > d ? c : d : b2 : a2;
    }

    public static float dist(float x1, float y1, float x2, float y2) {
        float x = x2 - x1;
        float y = y2 - y1;
        return (float) Math.hypot(x, y);
    }

    public static float lerp(float start, float stop, float amount) {
        return ((1.0f - amount) * start) + (amount * stop);
    }

    public static boolean geq(float a2, float b2, float epsilon) {
        return a2 + epsilon >= b2;
    }

    public static float distanceToFurthestCorner(float pointX, float pointY, float rectLeft, float rectTop, float rectRight, float rectBottom) {
        return max(dist(pointX, pointY, rectLeft, rectTop), dist(pointX, pointY, rectRight, rectTop), dist(pointX, pointY, rectRight, rectBottom), dist(pointX, pointY, rectLeft, rectBottom));
    }
}
