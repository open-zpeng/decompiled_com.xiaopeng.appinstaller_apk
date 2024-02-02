package android.support.design.internal;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
/* loaded from: classes.dex */
public final class ThemeEnforcement {
    private static final int[] APPCOMPAT_CHECK_ATTRS = {R.attr.colorPrimary};
    private static final int[] MATERIAL_CHECK_ATTRS = {R.attr.colorSecondaryLight};

    public static TypedArray obtainStyledAttributes(Context context, AttributeSet set, int[] attrs, int defStyleAttr, int defStyleRes) {
        checkCompatibleTheme(context, set, defStyleAttr, defStyleRes);
        return context.obtainStyledAttributes(set, attrs, defStyleAttr, defStyleRes);
    }

    private static void checkCompatibleTheme(Context context, AttributeSet set, int defStyleAttr, int defStyleRes) {
        TypedArray a2 = context.obtainStyledAttributes(set, R.styleable.ThemeEnforcement, defStyleAttr, defStyleRes);
        boolean enforceMaterialTheme = a2.getBoolean(R.styleable.ThemeEnforcement_enforceMaterialTheme, false);
        a2.recycle();
        if (enforceMaterialTheme) {
            checkMaterialTheme(context);
        }
        checkAppCompatTheme(context);
    }

    public static void checkAppCompatTheme(Context context) {
        checkTheme(context, APPCOMPAT_CHECK_ATTRS, "Theme.AppCompat");
    }

    public static void checkMaterialTheme(Context context) {
        checkTheme(context, MATERIAL_CHECK_ATTRS, "Theme.MaterialComponents");
    }

    private static void checkTheme(Context context, int[] themeAttributes, String themeName) {
        TypedArray a2 = context.obtainStyledAttributes(themeAttributes);
        boolean failed = !a2.hasValue(0);
        a2.recycle();
        if (failed) {
            throw new IllegalArgumentException("The style on this component requires your app theme to be " + themeName + " (or a descendant).");
        }
    }
}
