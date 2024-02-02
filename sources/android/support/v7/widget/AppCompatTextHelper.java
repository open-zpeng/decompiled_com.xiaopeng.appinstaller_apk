package android.support.v7.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.widget.AutoSizeableTextView;
import android.support.v7.appcompat.R;
import android.widget.TextView;
import java.lang.ref.WeakReference;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class AppCompatTextHelper {
    private boolean mAsyncFontPending;
    private final AppCompatTextViewAutoSizeHelper mAutoSizeTextHelper;
    private TintInfo mDrawableBottomTint;
    private TintInfo mDrawableEndTint;
    private TintInfo mDrawableLeftTint;
    private TintInfo mDrawableRightTint;
    private TintInfo mDrawableStartTint;
    private TintInfo mDrawableTopTint;
    private Typeface mFontTypeface;
    private int mStyle = 0;
    private final TextView mView;

    /* JADX INFO: Access modifiers changed from: package-private */
    public AppCompatTextHelper(TextView view) {
        this.mView = view;
        this.mAutoSizeTextHelper = new AppCompatTextViewAutoSizeHelper(this.mView);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* JADX WARN: Removed duplicated region for block: B:83:0x01e4  */
    /* JADX WARN: Removed duplicated region for block: B:85:0x01eb  */
    /* JADX WARN: Removed duplicated region for block: B:87:0x01f2  */
    /* JADX WARN: Removed duplicated region for block: B:89:? A[RETURN, SYNTHETIC] */
    @android.annotation.SuppressLint({"NewApi"})
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void loadFromAttributes(android.util.AttributeSet r19, int r20) {
        /*
            Method dump skipped, instructions count: 504
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v7.widget.AppCompatTextHelper.loadFromAttributes(android.util.AttributeSet, int):void");
    }

    private void updateTypefaceAndStyle(Context context, TintTypedArray a2) {
        String fontFamilyName;
        this.mStyle = a2.getInt(R.styleable.TextAppearance_android_textStyle, this.mStyle);
        boolean z = true;
        if (a2.hasValue(R.styleable.TextAppearance_android_fontFamily) || a2.hasValue(R.styleable.TextAppearance_fontFamily)) {
            this.mFontTypeface = null;
            int fontFamilyId = a2.hasValue(R.styleable.TextAppearance_fontFamily) ? R.styleable.TextAppearance_fontFamily : R.styleable.TextAppearance_android_fontFamily;
            if (!context.isRestricted()) {
                final WeakReference<TextView> textViewWeak = new WeakReference<>(this.mView);
                ResourcesCompat.FontCallback replyCallback = new ResourcesCompat.FontCallback() { // from class: android.support.v7.widget.AppCompatTextHelper.1
                    @Override // android.support.v4.content.res.ResourcesCompat.FontCallback
                    public void onFontRetrieved(Typeface typeface) {
                        AppCompatTextHelper.this.onAsyncTypefaceReceived(textViewWeak, typeface);
                    }

                    @Override // android.support.v4.content.res.ResourcesCompat.FontCallback
                    public void onFontRetrievalFailed(int reason) {
                    }
                };
                try {
                    this.mFontTypeface = a2.getFont(fontFamilyId, this.mStyle, replyCallback);
                    if (this.mFontTypeface != null) {
                        z = false;
                    }
                    this.mAsyncFontPending = z;
                } catch (Resources.NotFoundException | UnsupportedOperationException e) {
                }
            }
            if (this.mFontTypeface == null && (fontFamilyName = a2.getString(fontFamilyId)) != null) {
                this.mFontTypeface = Typeface.create(fontFamilyName, this.mStyle);
            }
        } else if (a2.hasValue(R.styleable.TextAppearance_android_typeface)) {
            this.mAsyncFontPending = false;
            int typefaceIndex = a2.getInt(R.styleable.TextAppearance_android_typeface, 1);
            switch (typefaceIndex) {
                case 1:
                    this.mFontTypeface = Typeface.SANS_SERIF;
                    return;
                case 2:
                    this.mFontTypeface = Typeface.SERIF;
                    return;
                case 3:
                    this.mFontTypeface = Typeface.MONOSPACE;
                    return;
                default:
                    return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onAsyncTypefaceReceived(WeakReference<TextView> textViewWeak, Typeface typeface) {
        if (this.mAsyncFontPending) {
            this.mFontTypeface = typeface;
            TextView textView = textViewWeak.get();
            if (textView != null) {
                textView.setTypeface(typeface, this.mStyle);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onSetTextAppearance(Context context, int resId) {
        ColorStateList textColor;
        TintTypedArray a2 = TintTypedArray.obtainStyledAttributes(context, resId, R.styleable.TextAppearance);
        if (a2.hasValue(R.styleable.TextAppearance_textAllCaps)) {
            setAllCaps(a2.getBoolean(R.styleable.TextAppearance_textAllCaps, false));
        }
        if (Build.VERSION.SDK_INT < 23 && a2.hasValue(R.styleable.TextAppearance_android_textColor) && (textColor = a2.getColorStateList(R.styleable.TextAppearance_android_textColor)) != null) {
            this.mView.setTextColor(textColor);
        }
        updateTypefaceAndStyle(context, a2);
        a2.recycle();
        if (this.mFontTypeface != null) {
            this.mView.setTypeface(this.mFontTypeface, this.mStyle);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAllCaps(boolean allCaps) {
        this.mView.setAllCaps(allCaps);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void applyCompoundDrawablesTints() {
        if (this.mDrawableLeftTint != null || this.mDrawableTopTint != null || this.mDrawableRightTint != null || this.mDrawableBottomTint != null) {
            Drawable[] compoundDrawables = this.mView.getCompoundDrawables();
            applyCompoundDrawableTint(compoundDrawables[0], this.mDrawableLeftTint);
            applyCompoundDrawableTint(compoundDrawables[1], this.mDrawableTopTint);
            applyCompoundDrawableTint(compoundDrawables[2], this.mDrawableRightTint);
            applyCompoundDrawableTint(compoundDrawables[3], this.mDrawableBottomTint);
        }
        if (Build.VERSION.SDK_INT >= 17) {
            if (this.mDrawableStartTint != null || this.mDrawableEndTint != null) {
                Drawable[] compoundDrawables2 = this.mView.getCompoundDrawablesRelative();
                applyCompoundDrawableTint(compoundDrawables2[0], this.mDrawableStartTint);
                applyCompoundDrawableTint(compoundDrawables2[2], this.mDrawableEndTint);
            }
        }
    }

    private void applyCompoundDrawableTint(Drawable drawable, TintInfo info) {
        if (drawable != null && info != null) {
            AppCompatDrawableManager.tintDrawable(drawable, info, this.mView.getDrawableState());
        }
    }

    private static TintInfo createTintInfo(Context context, AppCompatDrawableManager drawableManager, int drawableId) {
        ColorStateList tintList = drawableManager.getTintList(context, drawableId);
        if (tintList != null) {
            TintInfo tintInfo = new TintInfo();
            tintInfo.mHasTintList = true;
            tintInfo.mTintList = tintList;
            return tintInfo;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (!AutoSizeableTextView.PLATFORM_SUPPORTS_AUTOSIZE) {
            autoSizeText();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setTextSize(int unit, float size) {
        if (!AutoSizeableTextView.PLATFORM_SUPPORTS_AUTOSIZE && !isAutoSizeEnabled()) {
            setTextSizeInternal(unit, size);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void autoSizeText() {
        this.mAutoSizeTextHelper.autoSizeText();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isAutoSizeEnabled() {
        return this.mAutoSizeTextHelper.isAutoSizeEnabled();
    }

    private void setTextSizeInternal(int unit, float size) {
        this.mAutoSizeTextHelper.setTextSizeInternal(unit, size);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAutoSizeTextTypeWithDefaults(int autoSizeTextType) {
        this.mAutoSizeTextHelper.setAutoSizeTextTypeWithDefaults(autoSizeTextType);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAutoSizeTextTypeUniformWithConfiguration(int autoSizeMinTextSize, int autoSizeMaxTextSize, int autoSizeStepGranularity, int unit) throws IllegalArgumentException {
        this.mAutoSizeTextHelper.setAutoSizeTextTypeUniformWithConfiguration(autoSizeMinTextSize, autoSizeMaxTextSize, autoSizeStepGranularity, unit);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setAutoSizeTextTypeUniformWithPresetSizes(int[] presetSizes, int unit) throws IllegalArgumentException {
        this.mAutoSizeTextHelper.setAutoSizeTextTypeUniformWithPresetSizes(presetSizes, unit);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getAutoSizeTextType() {
        return this.mAutoSizeTextHelper.getAutoSizeTextType();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getAutoSizeStepGranularity() {
        return this.mAutoSizeTextHelper.getAutoSizeStepGranularity();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getAutoSizeMinTextSize() {
        return this.mAutoSizeTextHelper.getAutoSizeMinTextSize();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int getAutoSizeMaxTextSize() {
        return this.mAutoSizeTextHelper.getAutoSizeMaxTextSize();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public int[] getAutoSizeTextAvailableSizes() {
        return this.mAutoSizeTextHelper.getAutoSizeTextAvailableSizes();
    }
}
