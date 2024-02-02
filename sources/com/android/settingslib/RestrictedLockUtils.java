package com.android.settingslib;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.UserInfo;
import android.os.UserHandle;
import android.os.UserManager;
import java.util.List;
import java.util.Objects;
/* loaded from: classes.dex */
public class RestrictedLockUtils {
    static Proxy sProxy = new Proxy();

    public static EnforcedAdmin checkIfRestrictionEnforced(Context context, String userRestriction, int userId) {
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService("device_policy");
        if (dpm == null) {
            return null;
        }
        UserManager um = UserManager.get(context);
        List<UserManager.EnforcingUser> enforcingUsers = um.getUserRestrictionSources(userRestriction, UserHandle.of(userId));
        if (enforcingUsers.isEmpty()) {
            return null;
        }
        if (enforcingUsers.size() > 1) {
            return EnforcedAdmin.createDefaultEnforcedAdminWithRestriction(userRestriction);
        }
        int restrictionSource = enforcingUsers.get(0).getUserRestrictionSource();
        int adminUserId = enforcingUsers.get(0).getUserHandle().getIdentifier();
        if (restrictionSource == 4) {
            if (adminUserId == userId) {
                return getProfileOwner(context, userRestriction, adminUserId);
            }
            UserInfo parentUser = um.getProfileParent(adminUserId);
            if (parentUser != null && parentUser.id == userId) {
                return getProfileOwner(context, userRestriction, adminUserId);
            }
            return EnforcedAdmin.createDefaultEnforcedAdminWithRestriction(userRestriction);
        } else if (restrictionSource != 2) {
            return null;
        } else {
            if (adminUserId == userId) {
                return getDeviceOwner(context, userRestriction);
            }
            return EnforcedAdmin.createDefaultEnforcedAdminWithRestriction(userRestriction);
        }
    }

    public static boolean hasBaseUserRestriction(Context context, String userRestriction, int userId) {
        UserManager um = (UserManager) context.getSystemService("user");
        return um.hasBaseUserRestriction(userRestriction, UserHandle.of(userId));
    }

    public static EnforcedAdmin getProfileOrDeviceOwner(Context context, int userId) {
        return getProfileOrDeviceOwner(context, null, userId);
    }

    public static EnforcedAdmin getProfileOrDeviceOwner(Context context, String enforcedRestriction, int userId) {
        DevicePolicyManager dpm;
        ComponentName adminComponent;
        if (userId == -10000 || (dpm = (DevicePolicyManager) context.getSystemService("device_policy")) == null) {
            return null;
        }
        ComponentName adminComponent2 = dpm.getProfileOwnerAsUser(userId);
        if (adminComponent2 != null) {
            return new EnforcedAdmin(adminComponent2, enforcedRestriction, userId);
        }
        if (dpm.getDeviceOwnerUserId() != userId || (adminComponent = dpm.getDeviceOwnerComponentOnAnyUser()) == null) {
            return null;
        }
        return new EnforcedAdmin(adminComponent, enforcedRestriction, userId);
    }

    private static EnforcedAdmin getDeviceOwner(Context context, String enforcedRestriction) {
        ComponentName adminComponent;
        DevicePolicyManager dpm = (DevicePolicyManager) context.getSystemService("device_policy");
        if (dpm == null || (adminComponent = dpm.getDeviceOwnerComponentOnAnyUser()) == null) {
            return null;
        }
        return new EnforcedAdmin(adminComponent, enforcedRestriction, dpm.getDeviceOwnerUserId());
    }

    private static EnforcedAdmin getProfileOwner(Context context, String enforcedRestriction, int userId) {
        DevicePolicyManager dpm;
        ComponentName adminComponent;
        if (userId == -10000 || (dpm = (DevicePolicyManager) context.getSystemService("device_policy")) == null || (adminComponent = dpm.getProfileOwnerAsUser(userId)) == null) {
            return null;
        }
        return new EnforcedAdmin(adminComponent, enforcedRestriction, userId);
    }

    public static void sendShowAdminSupportDetailsIntent(Context context, EnforcedAdmin admin) {
        Intent intent = getShowAdminSupportDetailsIntent(context, admin);
        int targetUserId = UserHandle.myUserId();
        if (admin != null && admin.userId != -10000 && isCurrentUserOrProfile(context, admin.userId)) {
            targetUserId = admin.userId;
        }
        intent.putExtra("android.app.extra.RESTRICTION", admin.enforcedRestriction);
        context.startActivityAsUser(intent, new UserHandle(targetUserId));
    }

    public static Intent getShowAdminSupportDetailsIntent(Context context, EnforcedAdmin admin) {
        Intent intent = new Intent("android.settings.SHOW_ADMIN_SUPPORT_DETAILS");
        if (admin != null) {
            if (admin.component != null) {
                intent.putExtra("android.app.extra.DEVICE_ADMIN", admin.component);
            }
            int adminUserId = UserHandle.myUserId();
            if (admin.userId != -10000) {
                adminUserId = admin.userId;
            }
            intent.putExtra("android.intent.extra.USER_ID", adminUserId);
        }
        return intent;
    }

    public static boolean isCurrentUserOrProfile(Context context, int userId) {
        UserManager um = UserManager.get(context);
        for (UserInfo userInfo : um.getProfiles(UserHandle.myUserId())) {
            if (userInfo.id == userId) {
                return true;
            }
        }
        return false;
    }

    /* loaded from: classes.dex */
    public static class EnforcedAdmin {
        public static final EnforcedAdmin MULTIPLE_ENFORCED_ADMIN = new EnforcedAdmin();
        public ComponentName component;
        public String enforcedRestriction;
        public int userId;

        public static EnforcedAdmin createDefaultEnforcedAdminWithRestriction(String enforcedRestriction) {
            EnforcedAdmin enforcedAdmin = new EnforcedAdmin();
            enforcedAdmin.enforcedRestriction = enforcedRestriction;
            return enforcedAdmin;
        }

        public EnforcedAdmin(ComponentName component, String enforcedRestriction, int userId) {
            this.component = null;
            this.enforcedRestriction = null;
            this.userId = -10000;
            this.component = component;
            this.enforcedRestriction = enforcedRestriction;
            this.userId = userId;
        }

        public EnforcedAdmin() {
            this.component = null;
            this.enforcedRestriction = null;
            this.userId = -10000;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            EnforcedAdmin that = (EnforcedAdmin) o;
            if (this.userId == that.userId && Objects.equals(this.component, that.component) && Objects.equals(this.enforcedRestriction, that.enforcedRestriction)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            return Objects.hash(this.component, this.enforcedRestriction, Integer.valueOf(this.userId));
        }

        public String toString() {
            return "EnforcedAdmin{component=" + this.component + ", enforcedRestriction='" + this.enforcedRestriction + ", userId=" + this.userId + '}';
        }
    }

    /* loaded from: classes.dex */
    static class Proxy {
        Proxy() {
        }
    }
}
