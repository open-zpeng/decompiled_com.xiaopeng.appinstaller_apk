package android.support.transition;

import android.animation.LayoutTransition;
import android.util.Log;
import android.view.ViewGroup;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/* loaded from: classes.dex */
class ViewGroupUtilsApi14 {
    private static Method sCancelMethod;
    private static boolean sCancelMethodFetched;
    private static LayoutTransition sEmptyLayoutTransition;
    private static Field sLayoutSuppressedField;
    private static boolean sLayoutSuppressedFieldFetched;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void suppressLayout(ViewGroup group, boolean suppress) {
        if (sEmptyLayoutTransition == null) {
            sEmptyLayoutTransition = new LayoutTransition() { // from class: android.support.transition.ViewGroupUtilsApi14.1
                @Override // android.animation.LayoutTransition
                public boolean isChangingLayout() {
                    return true;
                }
            };
            sEmptyLayoutTransition.setAnimator(2, null);
            sEmptyLayoutTransition.setAnimator(0, null);
            sEmptyLayoutTransition.setAnimator(1, null);
            sEmptyLayoutTransition.setAnimator(3, null);
            sEmptyLayoutTransition.setAnimator(4, null);
        }
        if (suppress) {
            LayoutTransition layoutTransition = group.getLayoutTransition();
            if (layoutTransition != null) {
                if (layoutTransition.isRunning()) {
                    cancelLayoutTransition(layoutTransition);
                }
                if (layoutTransition != sEmptyLayoutTransition) {
                    group.setTag(R.id.transition_layout_save, layoutTransition);
                }
            }
            group.setLayoutTransition(sEmptyLayoutTransition);
            return;
        }
        group.setLayoutTransition(null);
        if (!sLayoutSuppressedFieldFetched) {
            try {
                sLayoutSuppressedField = ViewGroup.class.getDeclaredField("mLayoutSuppressed");
                sLayoutSuppressedField.setAccessible(true);
            } catch (NoSuchFieldException e) {
                Log.i("ViewGroupUtilsApi14", "Failed to access mLayoutSuppressed field by reflection");
            }
            sLayoutSuppressedFieldFetched = true;
        }
        boolean layoutSuppressed = false;
        if (sLayoutSuppressedField != null) {
            try {
                layoutSuppressed = sLayoutSuppressedField.getBoolean(group);
                if (layoutSuppressed) {
                    sLayoutSuppressedField.setBoolean(group, false);
                }
            } catch (IllegalAccessException e2) {
                Log.i("ViewGroupUtilsApi14", "Failed to get mLayoutSuppressed field by reflection");
            }
        }
        if (layoutSuppressed) {
            group.requestLayout();
        }
        LayoutTransition layoutTransition2 = (LayoutTransition) group.getTag(R.id.transition_layout_save);
        if (layoutTransition2 != null) {
            group.setTag(R.id.transition_layout_save, null);
            group.setLayoutTransition(layoutTransition2);
        }
    }

    private static void cancelLayoutTransition(LayoutTransition t) {
        if (!sCancelMethodFetched) {
            try {
                sCancelMethod = LayoutTransition.class.getDeclaredMethod("cancel", new Class[0]);
                sCancelMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                Log.i("ViewGroupUtilsApi14", "Failed to access cancel method by reflection");
            }
            sCancelMethodFetched = true;
        }
        if (sCancelMethod != null) {
            try {
                sCancelMethod.invoke(t, new Object[0]);
            } catch (IllegalAccessException e2) {
                Log.i("ViewGroupUtilsApi14", "Failed to access cancel method by reflection");
            } catch (InvocationTargetException e3) {
                Log.i("ViewGroupUtilsApi14", "Failed to invoke cancel method by reflection");
            }
        }
    }
}
