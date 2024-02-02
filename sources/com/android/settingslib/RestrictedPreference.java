package com.android.settingslib;

import android.content.Context;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;
/* loaded from: classes.dex */
public class RestrictedPreference extends TwoTargetPreference {
    RestrictedPreferenceHelper mHelper;

    public RestrictedPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mHelper = new RestrictedPreferenceHelper(context, this, attrs);
    }

    public RestrictedPreference(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public RestrictedPreference(Context context, AttributeSet attrs) {
        this(context, attrs, TypedArrayUtils.getAttr(context, R.attr.preferenceStyle, 16842894));
    }

    @Override // com.android.settingslib.TwoTargetPreference
    protected int getSecondTargetResId() {
        return R.layout.restricted_icon;
    }

    @Override // com.android.settingslib.TwoTargetPreference
    protected boolean shouldHideSecondTarget() {
        return !isDisabledByAdmin();
    }

    @Override // com.android.settingslib.TwoTargetPreference, android.support.v7.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        this.mHelper.onBindViewHolder(holder);
        View restrictedIcon = holder.findViewById(R.id.restricted_icon);
        if (restrictedIcon != null) {
            restrictedIcon.setVisibility(isDisabledByAdmin() ? 0 : 8);
        }
    }

    @Override // android.support.v7.preference.Preference
    public void performClick() {
        if (!this.mHelper.performClick()) {
            super.performClick();
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.preference.Preference
    public void onAttachedToHierarchy(PreferenceManager preferenceManager) {
        this.mHelper.onAttachedToHierarchy();
        super.onAttachedToHierarchy(preferenceManager);
    }

    @Override // android.support.v7.preference.Preference
    public void setEnabled(boolean enabled) {
        if (enabled && isDisabledByAdmin()) {
            this.mHelper.setDisabledByAdmin(null);
        } else {
            super.setEnabled(enabled);
        }
    }

    public boolean isDisabledByAdmin() {
        return this.mHelper.isDisabledByAdmin();
    }
}
