package com.xiaopeng.appinstaller.permission.ui.handheld;

import android.content.Context;
import android.preference.PreferenceScreen;
import android.view.View;
import android.widget.TextView;
import com.android.settingslib.RestrictedLockUtils;
import com.xiaopeng.appinstaller.R;
/* loaded from: classes.dex */
public class RestrictedSwitchPreference extends MultiTargetSwitchPreference {
    private final Context mContext;
    private boolean mDisabledByAdmin;
    private RestrictedLockUtils.EnforcedAdmin mEnforcedAdmin;
    private final int mSwitchWidgetResId;

    @Override // com.xiaopeng.appinstaller.permission.ui.handheld.MultiTargetSwitchPreference, android.preference.TwoStatePreference
    public /* bridge */ /* synthetic */ void setChecked(boolean z) {
        super.setChecked(z);
    }

    @Override // com.xiaopeng.appinstaller.permission.ui.handheld.MultiTargetSwitchPreference
    public /* bridge */ /* synthetic */ void setCheckedOverride(boolean z) {
        super.setCheckedOverride(z);
    }

    @Override // com.xiaopeng.appinstaller.permission.ui.handheld.MultiTargetSwitchPreference
    public /* bridge */ /* synthetic */ void setSwitchOnClickListener(View.OnClickListener onClickListener) {
        super.setSwitchOnClickListener(onClickListener);
    }

    public RestrictedSwitchPreference(Context context) {
        super(context);
        this.mSwitchWidgetResId = getWidgetLayoutResource();
        this.mContext = context;
    }

    @Override // com.xiaopeng.appinstaller.permission.ui.handheld.MultiTargetSwitchPreference, android.preference.SwitchPreference, android.preference.Preference
    public void onBindView(View view) {
        TextView summaryView;
        super.onBindView(view);
        if (this.mDisabledByAdmin) {
            view.setEnabled(true);
        }
        if (this.mDisabledByAdmin && (summaryView = (TextView) view.findViewById(16908304)) != null) {
            summaryView.setText(isChecked() ? R.string.enabled_by_admin : R.string.disabled_by_admin);
            summaryView.setVisibility(0);
        }
    }

    @Override // android.preference.Preference
    public void setEnabled(boolean enabled) {
        if (enabled && this.mDisabledByAdmin) {
            setDisabledByAdmin(null);
        } else {
            super.setEnabled(enabled);
        }
    }

    public void setDisabledByAdmin(RestrictedLockUtils.EnforcedAdmin admin) {
        boolean disabled = admin != null;
        this.mEnforcedAdmin = admin;
        if (this.mDisabledByAdmin != disabled) {
            this.mDisabledByAdmin = disabled;
            setWidgetLayoutResource(disabled ? R.layout.restricted_icon : this.mSwitchWidgetResId);
            setEnabled(disabled ? false : true);
        }
    }

    public void performClick(PreferenceScreen preferenceScreen) {
        if (this.mDisabledByAdmin) {
            RestrictedLockUtils.sendShowAdminSupportDetailsIntent(this.mContext, this.mEnforcedAdmin);
        } else {
            super.performClick(preferenceScreen);
        }
    }
}
