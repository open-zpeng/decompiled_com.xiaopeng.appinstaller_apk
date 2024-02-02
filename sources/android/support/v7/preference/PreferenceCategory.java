package android.support.v7.preference;

import android.content.Context;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.os.BuildCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.util.AttributeSet;
/* loaded from: classes.dex */
public class PreferenceCategory extends PreferenceGroup {
    private static final String TAG = "PreferenceCategory";

    public PreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public PreferenceCategory(Context context, AttributeSet attrs, int defStyleAttr) {
        this(context, attrs, defStyleAttr, 0);
    }

    public PreferenceCategory(Context context, AttributeSet attrs) {
        this(context, attrs, TypedArrayUtils.getAttr(context, R.attr.preferenceCategoryStyle, 16842892));
    }

    public PreferenceCategory(Context context) {
        this(context, null);
    }

    @Override // android.support.v7.preference.Preference
    public boolean isEnabled() {
        return false;
    }

    @Override // android.support.v7.preference.Preference
    public boolean shouldDisableDependents() {
        return !super.isEnabled();
    }

    @Override // android.support.v7.preference.Preference
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        if (BuildCompat.isAtLeastP()) {
            holder.itemView.setAccessibilityHeading(true);
        }
    }

    @Override // android.support.v7.preference.Preference
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfoCompat info) {
        AccessibilityNodeInfoCompat.CollectionItemInfoCompat existingItemInfo;
        super.onInitializeAccessibilityNodeInfo(info);
        if (BuildCompat.isAtLeastP() || (existingItemInfo = info.getCollectionItemInfo()) == null) {
            return;
        }
        AccessibilityNodeInfoCompat.CollectionItemInfoCompat newItemInfo = AccessibilityNodeInfoCompat.CollectionItemInfoCompat.obtain(existingItemInfo.getRowIndex(), existingItemInfo.getRowSpan(), existingItemInfo.getColumnIndex(), existingItemInfo.getColumnSpan(), true, existingItemInfo.isSelected());
        info.setCollectionItemInfo(newItemInfo);
    }
}
