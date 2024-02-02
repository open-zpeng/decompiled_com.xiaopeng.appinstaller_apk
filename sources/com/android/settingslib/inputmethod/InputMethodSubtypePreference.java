package com.android.settingslib.inputmethod;

import android.content.Context;
import android.text.TextUtils;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.inputmethod.InputMethodUtils;
import java.util.Locale;
/* loaded from: classes.dex */
public class InputMethodSubtypePreference extends SwitchWithNoTextPreference {
    private final boolean mIsSystemLanguage;
    private final boolean mIsSystemLocale;

    @VisibleForTesting
    InputMethodSubtypePreference(Context context, String prefKey, CharSequence title, String subtypeLocaleString, Locale systemLocale) {
        super(context);
        boolean z = false;
        setPersistent(false);
        setKey(prefKey);
        setTitle(title);
        if (TextUtils.isEmpty(subtypeLocaleString)) {
            this.mIsSystemLocale = false;
            this.mIsSystemLanguage = false;
            return;
        }
        this.mIsSystemLocale = subtypeLocaleString.equals(systemLocale.toString());
        this.mIsSystemLanguage = (this.mIsSystemLocale || InputMethodUtils.getLanguageFromLocaleString(subtypeLocaleString).equals(systemLocale.getLanguage())) ? true : true;
    }
}
