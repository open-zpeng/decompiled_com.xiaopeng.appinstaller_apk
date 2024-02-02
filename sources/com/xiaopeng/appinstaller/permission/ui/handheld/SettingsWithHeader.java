package com.xiaopeng.appinstaller.permission.ui.handheld;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.xiaopeng.appinstaller.R;
/* loaded from: classes.dex */
public abstract class SettingsWithHeader extends PermissionsFrameFragment implements View.OnClickListener {
    private View mHeader;
    protected Drawable mIcon;
    protected Intent mInfoIntent;
    protected CharSequence mLabel;

    @Override // com.xiaopeng.appinstaller.permission.ui.handheld.PermissionsFrameFragment, android.preference.PreferenceFragment, android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) super.onCreateView(inflater, container, savedInstanceState);
        return root;
    }

    public void setHeader(Drawable icon, CharSequence label, Intent infoIntent) {
        this.mIcon = icon;
        this.mLabel = label;
        this.mInfoIntent = infoIntent;
        updateHeader();
    }

    private void updateHeader() {
        if (this.mHeader != null) {
            ImageView appIcon = (ImageView) this.mHeader.findViewById(R.id.icon);
            appIcon.setImageDrawable(this.mIcon);
            TextView appName = (TextView) this.mHeader.findViewById(R.id.name);
            appName.setText(this.mLabel);
            View info = this.mHeader.findViewById(R.id.info);
            if (this.mInfoIntent == null) {
                info.setVisibility(8);
                return;
            }
            info.setVisibility(0);
            info.setClickable(true);
            info.setOnClickListener(this);
        }
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        getActivity().startActivity(this.mInfoIntent);
    }
}
