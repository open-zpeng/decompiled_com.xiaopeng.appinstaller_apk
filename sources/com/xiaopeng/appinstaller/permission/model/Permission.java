package com.xiaopeng.appinstaller.permission.model;
/* loaded from: classes.dex */
public final class Permission {
    private final String mAppOp;
    private boolean mAppOpAllowed;
    private int mFlags;
    private boolean mGranted;
    private boolean mIsEphemeral;
    private boolean mIsRuntimeOnly;
    private final String mName;

    public Permission(String name, boolean granted, String appOp, boolean appOpAllowed, int flags, int protectionLevel) {
        this.mName = name;
        this.mGranted = granted;
        this.mAppOp = appOp;
        this.mAppOpAllowed = appOpAllowed;
        this.mFlags = flags;
        this.mIsEphemeral = (protectionLevel & 4096) != 0;
        this.mIsRuntimeOnly = (protectionLevel & 8192) != 0;
    }

    public String getName() {
        return this.mName;
    }

    public String getAppOp() {
        return this.mAppOp;
    }

    public int getFlags() {
        return this.mFlags;
    }

    public boolean hasAppOp() {
        return this.mAppOp != null;
    }

    public boolean isGranted() {
        return this.mGranted;
    }

    public boolean isReviewRequired() {
        return (this.mFlags & 64) != 0;
    }

    public void resetReviewRequired() {
        this.mFlags &= -65;
    }

    public void setGranted(boolean mGranted) {
        this.mGranted = mGranted;
    }

    public boolean isAppOpAllowed() {
        return this.mAppOpAllowed;
    }

    public boolean isUserFixed() {
        return (this.mFlags & 2) != 0;
    }

    public void setUserFixed(boolean userFixed) {
        if (userFixed) {
            this.mFlags |= 2;
        } else {
            this.mFlags &= -3;
        }
    }

    public boolean isSystemFixed() {
        return (this.mFlags & 16) != 0;
    }

    public boolean isPolicyFixed() {
        return (this.mFlags & 4) != 0;
    }

    public boolean isUserSet() {
        return (this.mFlags & 1) != 0;
    }

    public boolean isGrantedByDefault() {
        return (this.mFlags & 32) != 0;
    }

    public void setUserSet(boolean userSet) {
        if (userSet) {
            this.mFlags |= 1;
        } else {
            this.mFlags &= -2;
        }
    }

    public void setPolicyFixed(boolean policyFixed) {
        if (policyFixed) {
            this.mFlags |= 4;
        } else {
            this.mFlags &= -5;
        }
    }

    public boolean shouldRevokeOnUpgrade() {
        return (this.mFlags & 8) != 0;
    }

    public void setRevokeOnUpgrade(boolean revokeOnUpgrade) {
        if (revokeOnUpgrade) {
            this.mFlags |= 8;
        } else {
            this.mFlags &= -9;
        }
    }

    public void setAppOpAllowed(boolean mAppOpAllowed) {
        this.mAppOpAllowed = mAppOpAllowed;
    }

    public boolean isEphemeral() {
        return this.mIsEphemeral;
    }

    public boolean isRuntimeOnly() {
        return this.mIsRuntimeOnly;
    }

    public boolean isGrantingAllowed(boolean isEphemeralApp, boolean supportsRuntimePermissions) {
        return (!isEphemeralApp || isEphemeral()) && (supportsRuntimePermissions || !isRuntimeOnly());
    }
}
