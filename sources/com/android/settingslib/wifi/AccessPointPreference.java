package com.android.settingslib.wifi;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Looper;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.android.settingslib.R;
/* loaded from: classes.dex */
public class AccessPointPreference extends Preference {
    private AccessPoint mAccessPoint;
    private Drawable mBadge;
    private final UserBadgeCache mBadgeCache;
    private final int mBadgePadding;
    private CharSequence mContentDescription;
    private int mDefaultIconResId;
    private boolean mForSavedNetworks;
    private final StateListDrawable mFrictionSld;
    private final IconInjector mIconInjector;
    private int mLevel;
    private final Runnable mNotifyChanged;
    private boolean mShowDivider;
    private TextView mTitleView;
    private int mWifiSpeed;
    private static final int[] STATE_SECURED_OWE = {R.attr.state_encrypted_owe};
    private static final int[] STATE_SECURED_SAE = {R.attr.state_encrypted_sae};
    private static final int[] STATE_SECURED = {R.attr.state_encrypted};
    private static final int[] STATE_METERED = {R.attr.state_metered};
    private static final int[] FRICTION_ATTRS = {R.attr.wifi_friction};
    private static final int[] WIFI_CONNECTION_STRENGTH = {R.string.accessibility_no_wifi, R.string.accessibility_wifi_one_bar, R.string.accessibility_wifi_two_bars, R.string.accessibility_wifi_three_bars, R.string.accessibility_wifi_signal_full};

    /* loaded from: classes.dex */
    public static class UserBadgeCache {
    }

    public AccessPointPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mForSavedNetworks = false;
        this.mWifiSpeed = 0;
        this.mNotifyChanged = new Runnable() { // from class: com.android.settingslib.wifi.AccessPointPreference.1
            @Override // java.lang.Runnable
            public void run() {
                AccessPointPreference.this.notifyChanged();
            }
        };
        this.mFrictionSld = null;
        this.mBadgePadding = 0;
        this.mBadgeCache = null;
        this.mIconInjector = new IconInjector(context);
    }

    AccessPointPreference(AccessPoint accessPoint, Context context, UserBadgeCache cache, int iconResId, boolean forSavedNetworks, StateListDrawable frictionSld, int level, IconInjector iconInjector) {
        super(context);
        this.mForSavedNetworks = false;
        this.mWifiSpeed = 0;
        this.mNotifyChanged = new Runnable() { // from class: com.android.settingslib.wifi.AccessPointPreference.1
            @Override // java.lang.Runnable
            public void run() {
                AccessPointPreference.this.notifyChanged();
            }
        };
        setLayoutResource(R.layout.preference_access_point);
        setWidgetLayoutResource(getWidgetLayoutResourceId());
        this.mBadgeCache = cache;
        this.mAccessPoint = accessPoint;
        this.mForSavedNetworks = forSavedNetworks;
        this.mAccessPoint.setTag(this);
        this.mLevel = level;
        this.mDefaultIconResId = iconResId;
        this.mFrictionSld = frictionSld;
        this.mIconInjector = iconInjector;
        this.mBadgePadding = context.getResources().getDimensionPixelSize(R.dimen.wifi_preference_badge_padding);
    }

    protected int getWidgetLayoutResourceId() {
        return R.layout.access_point_friction_widget;
    }

    @Override // android.support.v7.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder view) {
        super.onBindViewHolder(view);
        if (this.mAccessPoint == null) {
            return;
        }
        Drawable drawable = getIcon();
        if (drawable != null) {
            drawable.setLevel(this.mLevel);
        }
        this.mTitleView = (TextView) view.findViewById(16908310);
        if (this.mTitleView != null) {
            this.mTitleView.setCompoundDrawablesRelativeWithIntrinsicBounds((Drawable) null, (Drawable) null, this.mBadge, (Drawable) null);
            this.mTitleView.setCompoundDrawablePadding(this.mBadgePadding);
        }
        view.itemView.setContentDescription(this.mContentDescription);
        ImageView frictionImageView = (ImageView) view.findViewById(R.id.friction_icon);
        bindFrictionImage(frictionImageView);
        View divider = view.findViewById(R.id.two_target_divider);
        divider.setVisibility(shouldShowDivider() ? 0 : 4);
    }

    public boolean shouldShowDivider() {
        return this.mShowDivider;
    }

    private void bindFrictionImage(ImageView frictionImageView) {
        if (frictionImageView == null || this.mFrictionSld == null) {
            return;
        }
        if (this.mAccessPoint.getSecurity() == 5) {
            this.mFrictionSld.setState(STATE_SECURED_SAE);
        } else if (this.mAccessPoint.getSecurity() == 4) {
            this.mFrictionSld.setState(STATE_SECURED_OWE);
        } else if (this.mAccessPoint.getSecurity() != 0) {
            this.mFrictionSld.setState(STATE_SECURED);
        } else if (this.mAccessPoint.isMetered()) {
            this.mFrictionSld.setState(STATE_METERED);
        }
        Drawable drawable = this.mFrictionSld.getCurrent();
        frictionImageView.setImageDrawable(drawable);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.preference.Preference
    public void notifyChanged() {
        if (Looper.getMainLooper() != Looper.myLooper()) {
            postNotifyChanged();
        } else {
            super.notifyChanged();
        }
    }

    static void setTitle(AccessPointPreference preference, AccessPoint ap, boolean savedNetworks) {
        if (savedNetworks) {
            preference.setTitle(ap.getConfigName());
        } else {
            preference.setTitle(ap.getSsidStr());
        }
    }

    static CharSequence buildContentDescription(Context context, Preference pref, AccessPoint ap) {
        String string;
        CharSequence contentDescription = pref.getTitle();
        CharSequence summary = pref.getSummary();
        if (!TextUtils.isEmpty(summary)) {
            contentDescription = TextUtils.concat(contentDescription, ",", summary);
        }
        int level = ap.getLevel();
        if (level >= 0 && level < WIFI_CONNECTION_STRENGTH.length) {
            contentDescription = TextUtils.concat(contentDescription, ",", context.getString(WIFI_CONNECTION_STRENGTH[level]));
        }
        CharSequence[] charSequenceArr = new CharSequence[3];
        charSequenceArr[0] = contentDescription;
        charSequenceArr[1] = ",";
        if (ap.getSecurity() == 0) {
            string = context.getString(R.string.accessibility_wifi_security_type_none);
        } else {
            string = context.getString(R.string.accessibility_wifi_security_type_secured);
        }
        charSequenceArr[2] = string;
        return TextUtils.concat(charSequenceArr);
    }

    private void postNotifyChanged() {
        if (this.mTitleView != null) {
            this.mTitleView.post(this.mNotifyChanged);
        }
    }

    /* loaded from: classes.dex */
    static class IconInjector {
        private final Context mContext;

        public IconInjector(Context context) {
            this.mContext = context;
        }
    }
}
