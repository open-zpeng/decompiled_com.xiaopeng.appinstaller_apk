package a.a.a;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
/* loaded from: classes.dex */
public class a {

    /* renamed from: a  reason: collision with root package name */
    private static final Object f0a = new Object();

    /* renamed from: b  reason: collision with root package name */
    private static TypedValue f1b;

    public static ColorStateList a(Context context, int i) {
        return Build.VERSION.SDK_INT >= 23 ? context.getColorStateList(i) : context.getResources().getColorStateList(i);
    }

    public static Drawable b(Context context, int i) {
        int i2 = Build.VERSION.SDK_INT;
        if (i2 >= 21) {
            return context.getDrawable(i);
        }
        if (i2 < 16) {
            synchronized (f0a) {
                if (f1b == null) {
                    f1b = new TypedValue();
                }
                context.getResources().getValue(i, f1b, true);
                i = f1b.resourceId;
            }
        }
        return context.getResources().getDrawable(i);
    }
}
