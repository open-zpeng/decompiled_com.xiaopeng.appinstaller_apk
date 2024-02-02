package android.support.v4.graphics;

import android.graphics.Color;
/* loaded from: classes.dex */
public final class ColorUtils {
    private static final ThreadLocal<double[]> TEMP_ARRAY = new ThreadLocal<>();

    public static int compositeColors(int foreground, int background) {
        int bgAlpha = Color.alpha(background);
        int fgAlpha = Color.alpha(foreground);
        int a2 = compositeAlpha(fgAlpha, bgAlpha);
        int r = compositeComponent(Color.red(foreground), fgAlpha, Color.red(background), bgAlpha, a2);
        int g = compositeComponent(Color.green(foreground), fgAlpha, Color.green(background), bgAlpha, a2);
        int b2 = compositeComponent(Color.blue(foreground), fgAlpha, Color.blue(background), bgAlpha, a2);
        return Color.argb(a2, r, g, b2);
    }

    private static int compositeAlpha(int foregroundAlpha, int backgroundAlpha) {
        return 255 - (((255 - backgroundAlpha) * (255 - foregroundAlpha)) / 255);
    }

    private static int compositeComponent(int fgC, int fgA, int bgC, int bgA, int a2) {
        if (a2 == 0) {
            return 0;
        }
        return (((255 * fgC) * fgA) + ((bgC * bgA) * (255 - fgA))) / (a2 * 255);
    }

    public static int setAlphaComponent(int color, int alpha) {
        if (alpha < 0 || alpha > 255) {
            throw new IllegalArgumentException("alpha must be between 0 and 255.");
        }
        return (16777215 & color) | (alpha << 24);
    }

    static float circularInterpolate(float a2, float b2, float f) {
        if (Math.abs(b2 - a2) > 180.0f) {
            if (b2 > a2) {
                a2 += 360.0f;
            } else {
                b2 += 360.0f;
            }
        }
        return (((b2 - a2) * f) + a2) % 360.0f;
    }
}
