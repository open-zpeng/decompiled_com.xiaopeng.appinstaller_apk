package com.android.settingslib;

import android.content.Context;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
/* loaded from: classes.dex */
public class TwoTargetPreference extends Preference {
    private int mIconSize;
    private int mMediumIconSize;
    private int mSmallIconSize;

    public TwoTargetPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public TwoTargetPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        setLayoutResource(R.layout.preference_two_target);
        this.mSmallIconSize = context.getResources().getDimensionPixelSize(R.dimen.two_target_pref_small_icon_size);
        this.mMediumIconSize = context.getResources().getDimensionPixelSize(R.dimen.two_target_pref_medium_icon_size);
        int secondTargetResId = getSecondTargetResId();
        if (secondTargetResId != 0) {
            setWidgetLayoutResource(secondTargetResId);
        }
    }

    @Override // android.support.v7.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        ImageView icon = (ImageView) holder.itemView.findViewById(16908294);
        switch (this.mIconSize) {
            case 1:
                icon.setLayoutParams(new LinearLayout.LayoutParams(this.mMediumIconSize, this.mMediumIconSize));
                break;
            case 2:
                icon.setLayoutParams(new LinearLayout.LayoutParams(this.mSmallIconSize, this.mSmallIconSize));
                break;
        }
        View divider = holder.findViewById(R.id.two_target_divider);
        View widgetFrame = holder.findViewById(16908312);
        boolean shouldHideSecondTarget = shouldHideSecondTarget();
        if (divider != null) {
            divider.setVisibility(shouldHideSecondTarget ? 8 : 0);
        }
        if (widgetFrame != null) {
            widgetFrame.setVisibility(shouldHideSecondTarget ? 8 : 0);
        }
    }

    protected boolean shouldHideSecondTarget() {
        return getSecondTargetResId() == 0;
    }

    protected int getSecondTargetResId() {
        return 0;
    }
}
