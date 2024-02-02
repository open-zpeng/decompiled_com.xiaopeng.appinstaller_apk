package com.xiaopeng.appinstaller.permission.model;

import android.graphics.drawable.Drawable;
/* loaded from: classes.dex */
public final class PermissionGroup implements Comparable<PermissionGroup> {
    private final String mDeclaringPackage;
    private final int mGranted;
    private final Drawable mIcon;
    private final CharSequence mLabel;
    private final String mName;
    private final int mTotal;

    /* JADX INFO: Access modifiers changed from: package-private */
    public PermissionGroup(String name, String declaringPackage, CharSequence label, Drawable icon, int total, int granted) {
        this.mDeclaringPackage = declaringPackage;
        this.mName = name;
        this.mLabel = label;
        this.mIcon = icon;
        this.mTotal = total;
        this.mGranted = granted;
    }

    public String getName() {
        return this.mName;
    }

    public String getDeclaringPackage() {
        return this.mDeclaringPackage;
    }

    public CharSequence getLabel() {
        return this.mLabel;
    }

    public Drawable getIcon() {
        return this.mIcon;
    }

    public int getTotal() {
        return this.mTotal;
    }

    public int getGranted() {
        return this.mGranted;
    }

    @Override // java.lang.Comparable
    public int compareTo(PermissionGroup another) {
        return this.mLabel.toString().compareTo(another.mLabel.toString());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PermissionGroup other = (PermissionGroup) obj;
        if (this.mName == null) {
            if (other.mName != null) {
                return false;
            }
        } else if (!this.mName.equals(other.mName)) {
            return false;
        }
        if (this.mTotal == other.mTotal && this.mGranted == other.mGranted) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return (this.mName != null ? this.mName.hashCode() + this.mTotal : this.mTotal) + this.mGranted;
    }
}
