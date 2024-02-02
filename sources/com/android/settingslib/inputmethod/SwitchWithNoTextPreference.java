package com.android.settingslib.inputmethod;

import android.content.Context;
import android.support.v14.preference.SwitchPreference;
import xiaopeng.widget.BuildConfig;
/* loaded from: classes.dex */
public class SwitchWithNoTextPreference extends SwitchPreference {
    public SwitchWithNoTextPreference(Context context) {
        super(context);
        setSwitchTextOn(BuildConfig.FLAVOR);
        setSwitchTextOff(BuildConfig.FLAVOR);
    }
}
