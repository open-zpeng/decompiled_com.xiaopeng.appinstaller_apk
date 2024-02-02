package com.xiaopeng.appinstaller.permission.ui.handheld;

import android.content.Context;
import android.preference.SwitchPreference;
import android.view.View;
import android.widget.Switch;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class MultiTargetSwitchPreference extends SwitchPreference {
    private View.OnClickListener mSwitchOnClickLister;

    public MultiTargetSwitchPreference(Context context) {
        super(context);
    }

    public void setCheckedOverride(boolean checked) {
        super.setChecked(checked);
    }

    @Override // android.preference.TwoStatePreference
    public void setChecked(boolean checked) {
        if (this.mSwitchOnClickLister == null) {
            super.setChecked(checked);
        }
    }

    public void setSwitchOnClickListener(View.OnClickListener listener) {
        this.mSwitchOnClickLister = listener;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.preference.SwitchPreference, android.preference.Preference
    public void onBindView(View view) {
        super.onBindView(view);
        Switch switchView = (Switch) view.findViewById(16908352);
        if (this.mSwitchOnClickLister != null) {
            switchView.setOnClickListener(this.mSwitchOnClickLister);
            int padding = (int) (((view.getMeasuredHeight() - switchView.getMeasuredHeight()) / 2) + 0.5f);
            switchView.setPadding(padding, padding, 0, padding);
        }
    }
}
