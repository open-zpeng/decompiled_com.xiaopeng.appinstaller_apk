package android.support.v4.widget;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.icu.text.DecimalFormatSymbols;
import android.os.Build;
import android.support.v4.os.BuildCompat;
import android.support.v4.text.PrecomputedTextCompat;
import android.support.v4.util.Preconditions;
import android.text.Editable;
import android.text.TextDirectionHeuristic;
import android.text.TextDirectionHeuristics;
import android.text.TextPaint;
import android.text.method.PasswordTransformationMethod;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
/* loaded from: classes.dex */
public final class TextViewCompat {
    public static void setCompoundDrawablesRelative(TextView textView, Drawable start, Drawable top, Drawable end, Drawable bottom) {
        if (Build.VERSION.SDK_INT >= 18) {
            textView.setCompoundDrawablesRelative(start, top, end, bottom);
        } else if (Build.VERSION.SDK_INT >= 17) {
            boolean rtl = textView.getLayoutDirection() == 1;
            textView.setCompoundDrawables(rtl ? end : start, top, rtl ? start : end, bottom);
        } else {
            textView.setCompoundDrawables(start, top, end, bottom);
        }
    }

    public static void setTextAppearance(TextView textView, int resId) {
        if (Build.VERSION.SDK_INT >= 23) {
            textView.setTextAppearance(resId);
        } else {
            textView.setTextAppearance(textView.getContext(), resId);
        }
    }

    public static Drawable[] getCompoundDrawablesRelative(TextView textView) {
        if (Build.VERSION.SDK_INT >= 18) {
            return textView.getCompoundDrawablesRelative();
        }
        if (Build.VERSION.SDK_INT >= 17) {
            boolean rtl = textView.getLayoutDirection() == 1;
            Drawable[] compounds = textView.getCompoundDrawables();
            if (rtl) {
                Drawable start = compounds[2];
                Drawable end = compounds[0];
                compounds[0] = start;
                compounds[2] = end;
            }
            return compounds;
        }
        return textView.getCompoundDrawables();
    }

    public static ActionMode.Callback wrapCustomSelectionActionModeCallback(TextView textView, ActionMode.Callback callback) {
        if (Build.VERSION.SDK_INT < 26 || Build.VERSION.SDK_INT > 27 || (callback instanceof OreoCallback)) {
            return callback;
        }
        return new OreoCallback(callback, textView);
    }

    @TargetApi(26)
    /* loaded from: classes.dex */
    private static class OreoCallback implements ActionMode.Callback {
        private final ActionMode.Callback mCallback;
        private boolean mCanUseMenuBuilderReferences;
        private boolean mInitializedMenuBuilderReferences = false;
        private Class mMenuBuilderClass;
        private Method mMenuBuilderRemoveItemAtMethod;
        private final TextView mTextView;

        OreoCallback(ActionMode.Callback callback, TextView textView) {
            this.mCallback = callback;
            this.mTextView = textView;
        }

        @Override // android.view.ActionMode.Callback
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            return this.mCallback.onCreateActionMode(mode, menu);
        }

        @Override // android.view.ActionMode.Callback
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            recomputeProcessTextMenuItems(menu);
            return this.mCallback.onPrepareActionMode(mode, menu);
        }

        @Override // android.view.ActionMode.Callback
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            return this.mCallback.onActionItemClicked(mode, item);
        }

        @Override // android.view.ActionMode.Callback
        public void onDestroyActionMode(ActionMode mode) {
            this.mCallback.onDestroyActionMode(mode);
        }

        private void recomputeProcessTextMenuItems(Menu menu) {
            Context context = this.mTextView.getContext();
            PackageManager packageManager = context.getPackageManager();
            if (!this.mInitializedMenuBuilderReferences) {
                this.mInitializedMenuBuilderReferences = true;
                try {
                    this.mMenuBuilderClass = Class.forName("com.android.internal.view.menu.MenuBuilder");
                    this.mMenuBuilderRemoveItemAtMethod = this.mMenuBuilderClass.getDeclaredMethod("removeItemAt", Integer.TYPE);
                    this.mCanUseMenuBuilderReferences = true;
                } catch (ClassNotFoundException | NoSuchMethodException e) {
                    this.mMenuBuilderClass = null;
                    this.mMenuBuilderRemoveItemAtMethod = null;
                    this.mCanUseMenuBuilderReferences = false;
                }
            }
            try {
                Method removeItemAtMethod = (this.mCanUseMenuBuilderReferences && this.mMenuBuilderClass.isInstance(menu)) ? this.mMenuBuilderRemoveItemAtMethod : menu.getClass().getDeclaredMethod("removeItemAt", Integer.TYPE);
                for (int i = menu.size() - 1; i >= 0; i--) {
                    MenuItem item = menu.getItem(i);
                    if (item.getIntent() != null && "android.intent.action.PROCESS_TEXT".equals(item.getIntent().getAction())) {
                        removeItemAtMethod.invoke(menu, Integer.valueOf(i));
                    }
                }
                List<ResolveInfo> supportedActivities = getSupportedActivities(context, packageManager);
                for (int i2 = 0; i2 < supportedActivities.size(); i2++) {
                    ResolveInfo info = supportedActivities.get(i2);
                    menu.add(0, 0, 100 + i2, info.loadLabel(packageManager)).setIntent(createProcessTextIntentForResolveInfo(info, this.mTextView)).setShowAsAction(1);
                }
            } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e2) {
            }
        }

        private List<ResolveInfo> getSupportedActivities(Context context, PackageManager packageManager) {
            List<ResolveInfo> supportedActivities = new ArrayList<>();
            boolean canStartActivityForResult = context instanceof Activity;
            if (!canStartActivityForResult) {
                return supportedActivities;
            }
            List<ResolveInfo> unfiltered = packageManager.queryIntentActivities(createProcessTextIntent(), 0);
            for (ResolveInfo info : unfiltered) {
                if (isSupportedActivity(info, context)) {
                    supportedActivities.add(info);
                }
            }
            return supportedActivities;
        }

        private boolean isSupportedActivity(ResolveInfo info, Context context) {
            if (context.getPackageName().equals(info.activityInfo.packageName)) {
                return true;
            }
            if (info.activityInfo.exported) {
                return info.activityInfo.permission == null || context.checkSelfPermission(info.activityInfo.permission) == 0;
            }
            return false;
        }

        private Intent createProcessTextIntentForResolveInfo(ResolveInfo info, TextView textView11) {
            return createProcessTextIntent().putExtra("android.intent.extra.PROCESS_TEXT_READONLY", !isEditable(textView11)).setClassName(info.activityInfo.packageName, info.activityInfo.name);
        }

        private boolean isEditable(TextView textView11) {
            return (textView11 instanceof Editable) && textView11.onCheckIsTextEditor() && textView11.isEnabled();
        }

        private Intent createProcessTextIntent() {
            return new Intent().setAction("android.intent.action.PROCESS_TEXT").setType("text/plain");
        }
    }

    public static void setFirstBaselineToTopHeight(TextView textView, int firstBaselineToTopHeight) {
        int fontMetricsTop;
        Preconditions.checkArgumentNonnegative(firstBaselineToTopHeight);
        if (BuildCompat.isAtLeastP()) {
            textView.setFirstBaselineToTopHeight(firstBaselineToTopHeight);
            return;
        }
        Paint.FontMetricsInt fontMetrics = textView.getPaint().getFontMetricsInt();
        if (Build.VERSION.SDK_INT < 16 || textView.getIncludeFontPadding()) {
            fontMetricsTop = fontMetrics.top;
        } else {
            fontMetricsTop = fontMetrics.ascent;
        }
        if (firstBaselineToTopHeight > Math.abs(fontMetricsTop)) {
            int paddingTop = firstBaselineToTopHeight - (-fontMetricsTop);
            textView.setPadding(textView.getPaddingLeft(), paddingTop, textView.getPaddingRight(), textView.getPaddingBottom());
        }
    }

    public static void setLastBaselineToBottomHeight(TextView textView, int lastBaselineToBottomHeight) {
        int fontMetricsBottom;
        Preconditions.checkArgumentNonnegative(lastBaselineToBottomHeight);
        Paint.FontMetricsInt fontMetrics = textView.getPaint().getFontMetricsInt();
        if (Build.VERSION.SDK_INT < 16 || textView.getIncludeFontPadding()) {
            fontMetricsBottom = fontMetrics.bottom;
        } else {
            fontMetricsBottom = fontMetrics.descent;
        }
        if (lastBaselineToBottomHeight > Math.abs(fontMetricsBottom)) {
            int paddingBottom = lastBaselineToBottomHeight - fontMetricsBottom;
            textView.setPadding(textView.getPaddingLeft(), textView.getPaddingTop(), textView.getPaddingRight(), paddingBottom);
        }
    }

    public static int getFirstBaselineToTopHeight(TextView textView) {
        return textView.getPaddingTop() - textView.getPaint().getFontMetricsInt().top;
    }

    public static int getLastBaselineToBottomHeight(TextView textView) {
        return textView.getPaddingBottom() + textView.getPaint().getFontMetricsInt().bottom;
    }

    public static void setLineHeight(TextView textView, int lineHeight) {
        Preconditions.checkArgumentNonnegative(lineHeight);
        int fontHeight = textView.getPaint().getFontMetricsInt(null);
        if (lineHeight != fontHeight) {
            textView.setLineSpacing(lineHeight - fontHeight, 1.0f);
        }
    }

    public static PrecomputedTextCompat.Params getTextMetricsParams(TextView textView) {
        if (BuildCompat.isAtLeastP()) {
            return new PrecomputedTextCompat.Params(textView.getTextMetricsParams());
        }
        PrecomputedTextCompat.Params.Builder builder = new PrecomputedTextCompat.Params.Builder(new TextPaint(textView.getPaint()));
        if (Build.VERSION.SDK_INT >= 23) {
            builder.setBreakStrategy(textView.getBreakStrategy());
            builder.setHyphenationFrequency(textView.getHyphenationFrequency());
        }
        if (Build.VERSION.SDK_INT >= 18) {
            builder.setTextDirection(getTextDirectionHeuristic(textView));
        }
        return builder.build();
    }

    public static void setTextMetricsParams(TextView textView, PrecomputedTextCompat.Params params) {
        if (Build.VERSION.SDK_INT >= 18) {
            textView.setTextDirection(getTextDirection(params.getTextDirection()));
        }
        if (Build.VERSION.SDK_INT < 23) {
            float paintTextScaleX = params.getTextPaint().getTextScaleX();
            textView.getPaint().set(params.getTextPaint());
            if (paintTextScaleX == textView.getTextScaleX()) {
                textView.setTextScaleX((paintTextScaleX / 2.0f) + 1.0f);
            }
            textView.setTextScaleX(paintTextScaleX);
            return;
        }
        textView.getPaint().set(params.getTextPaint());
        textView.setBreakStrategy(params.getBreakStrategy());
        textView.setHyphenationFrequency(params.getHyphenationFrequency());
    }

    public static void setPrecomputedText(TextView textView, PrecomputedTextCompat precomputed) {
        if (BuildCompat.isAtLeastP()) {
            textView.setText(precomputed.getPrecomputedText());
            return;
        }
        PrecomputedTextCompat.Params param = getTextMetricsParams(textView);
        if (!param.equals(precomputed.getParams())) {
            throw new IllegalArgumentException("Given text can not be applied to TextView.");
        }
        textView.setText(precomputed);
    }

    private static TextDirectionHeuristic getTextDirectionHeuristic(TextView textView) {
        if (textView.getTransformationMethod() instanceof PasswordTransformationMethod) {
            return TextDirectionHeuristics.LTR;
        }
        if (BuildCompat.isAtLeastP() && (textView.getInputType() & 15) == 3) {
            DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(textView.getTextLocale());
            String zero = symbols.getDigitStrings()[0];
            int firstCodepoint = zero.codePointAt(0);
            byte digitDirection = Character.getDirectionality(firstCodepoint);
            if (digitDirection == 1 || digitDirection == 2) {
                return TextDirectionHeuristics.RTL;
            }
            return TextDirectionHeuristics.LTR;
        }
        boolean defaultIsRtl = textView.getLayoutDirection() == 1;
        switch (textView.getTextDirection()) {
            case 2:
                return TextDirectionHeuristics.ANYRTL_LTR;
            case 3:
                return TextDirectionHeuristics.LTR;
            case 4:
                return TextDirectionHeuristics.RTL;
            case 5:
                return TextDirectionHeuristics.LOCALE;
            case 6:
                return TextDirectionHeuristics.FIRSTSTRONG_LTR;
            case 7:
                return TextDirectionHeuristics.FIRSTSTRONG_RTL;
            default:
                return defaultIsRtl ? TextDirectionHeuristics.FIRSTSTRONG_RTL : TextDirectionHeuristics.FIRSTSTRONG_LTR;
        }
    }

    private static int getTextDirection(TextDirectionHeuristic heuristic) {
        if (heuristic == TextDirectionHeuristics.FIRSTSTRONG_RTL || heuristic == TextDirectionHeuristics.FIRSTSTRONG_LTR) {
            return 1;
        }
        if (heuristic == TextDirectionHeuristics.ANYRTL_LTR) {
            return 2;
        }
        if (heuristic == TextDirectionHeuristics.LTR) {
            return 3;
        }
        if (heuristic == TextDirectionHeuristics.RTL) {
            return 4;
        }
        if (heuristic == TextDirectionHeuristics.LOCALE) {
            return 5;
        }
        if (heuristic == TextDirectionHeuristics.FIRSTSTRONG_LTR) {
            return 6;
        }
        return heuristic == TextDirectionHeuristics.FIRSTSTRONG_RTL ? 7 : 1;
    }
}
