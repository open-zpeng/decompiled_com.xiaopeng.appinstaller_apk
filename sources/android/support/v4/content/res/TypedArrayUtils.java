package android.support.v4.content.res;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import org.xmlpull.v1.XmlPullParser;
/* loaded from: classes.dex */
public class TypedArrayUtils {
    public static boolean hasAttribute(XmlPullParser parser, String attrName) {
        return parser.getAttributeValue("http://schemas.android.com/apk/res/android", attrName) != null;
    }

    public static float getNamedFloat(TypedArray a2, XmlPullParser parser, String attrName, int resId, float defaultValue) {
        boolean hasAttr = hasAttribute(parser, attrName);
        if (!hasAttr) {
            return defaultValue;
        }
        return a2.getFloat(resId, defaultValue);
    }

    public static boolean getNamedBoolean(TypedArray a2, XmlPullParser parser, String attrName, int resId, boolean defaultValue) {
        boolean hasAttr = hasAttribute(parser, attrName);
        if (!hasAttr) {
            return defaultValue;
        }
        return a2.getBoolean(resId, defaultValue);
    }

    public static int getNamedInt(TypedArray a2, XmlPullParser parser, String attrName, int resId, int defaultValue) {
        boolean hasAttr = hasAttribute(parser, attrName);
        if (!hasAttr) {
            return defaultValue;
        }
        return a2.getInt(resId, defaultValue);
    }

    public static int getNamedColor(TypedArray a2, XmlPullParser parser, String attrName, int resId, int defaultValue) {
        boolean hasAttr = hasAttribute(parser, attrName);
        if (!hasAttr) {
            return defaultValue;
        }
        return a2.getColor(resId, defaultValue);
    }

    public static int getNamedResourceId(TypedArray a2, XmlPullParser parser, String attrName, int resId, int defaultValue) {
        boolean hasAttr = hasAttribute(parser, attrName);
        if (!hasAttr) {
            return defaultValue;
        }
        return a2.getResourceId(resId, defaultValue);
    }

    public static String getNamedString(TypedArray a2, XmlPullParser parser, String attrName, int resId) {
        boolean hasAttr = hasAttribute(parser, attrName);
        if (!hasAttr) {
            return null;
        }
        return a2.getString(resId);
    }

    public static TypedValue peekNamedValue(TypedArray a2, XmlPullParser parser, String attrName, int resId) {
        boolean hasAttr = hasAttribute(parser, attrName);
        if (!hasAttr) {
            return null;
        }
        return a2.peekValue(resId);
    }

    public static TypedArray obtainAttributes(Resources res, Resources.Theme theme, AttributeSet set, int[] attrs) {
        if (theme == null) {
            return res.obtainAttributes(set, attrs);
        }
        return theme.obtainStyledAttributes(set, attrs, 0, 0);
    }

    public static boolean getBoolean(TypedArray a2, int index, int fallbackIndex, boolean defaultValue) {
        boolean val = a2.getBoolean(fallbackIndex, defaultValue);
        return a2.getBoolean(index, val);
    }

    public static Drawable getDrawable(TypedArray a2, int index, int fallbackIndex) {
        Drawable val = a2.getDrawable(index);
        if (val == null) {
            return a2.getDrawable(fallbackIndex);
        }
        return val;
    }

    public static int getInt(TypedArray a2, int index, int fallbackIndex, int defaultValue) {
        int val = a2.getInt(fallbackIndex, defaultValue);
        return a2.getInt(index, val);
    }

    public static int getResourceId(TypedArray a2, int index, int fallbackIndex, int defaultValue) {
        int val = a2.getResourceId(fallbackIndex, defaultValue);
        return a2.getResourceId(index, val);
    }

    public static String getString(TypedArray a2, int index, int fallbackIndex) {
        String val = a2.getString(index);
        if (val == null) {
            return a2.getString(fallbackIndex);
        }
        return val;
    }

    public static CharSequence getText(TypedArray a2, int index, int fallbackIndex) {
        CharSequence val = a2.getText(index);
        if (val == null) {
            return a2.getText(fallbackIndex);
        }
        return val;
    }

    public static CharSequence[] getTextArray(TypedArray a2, int index, int fallbackIndex) {
        CharSequence[] val = a2.getTextArray(index);
        if (val == null) {
            return a2.getTextArray(fallbackIndex);
        }
        return val;
    }

    public static int getAttr(Context context, int attr, int fallbackAttr) {
        TypedValue value = new TypedValue();
        context.getTheme().resolveAttribute(attr, value, true);
        if (value.resourceId != 0) {
            return attr;
        }
        return fallbackAttr;
    }
}
