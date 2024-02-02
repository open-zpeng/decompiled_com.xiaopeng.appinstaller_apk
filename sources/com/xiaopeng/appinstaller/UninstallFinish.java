package com.xiaopeng.appinstaller;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.admin.IDevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.UserInfo;
import android.graphics.drawable.Icon;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;
import android.widget.Toast;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public class UninstallFinish extends BroadcastReceiver {
    private static final String LOG_TAG = UninstallFinish.class.getSimpleName();

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        List<UserInfo> users;
        int blockingUserId;
        IPackageManager packageManager;
        String str;
        int blockingUserId2;
        UserInfo otherBlockingUser;
        NotificationChannel uninstallFailureChannel;
        int returnCode = intent.getIntExtra("android.content.pm.extra.STATUS", 0);
        Log.i(LOG_TAG, "Uninstall finished extras=" + intent.getExtras());
        if (returnCode == -1) {
            context.startActivity((Intent) intent.getParcelableExtra("android.intent.extra.INTENT"));
            return;
        }
        int uninstallId = intent.getIntExtra("com.android.packageinstaller.extra.UNINSTALL_ID", 0);
        ApplicationInfo appInfo = (ApplicationInfo) intent.getParcelableExtra("com.android.packageinstaller.applicationInfo");
        String appLabel = intent.getStringExtra("com.android.packageinstaller.extra.APP_LABEL");
        boolean allUsers = intent.getBooleanExtra("android.intent.extra.UNINSTALL_ALL_USERS", false);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NotificationManager.class);
        UserManager userManager = (UserManager) context.getSystemService(UserManager.class);
        NotificationChannel uninstallFailureChannel2 = new NotificationChannel("uninstall failure", context.getString(R.string.uninstall_failure_notification_channel), 3);
        notificationManager.createNotificationChannel(uninstallFailureChannel2);
        Notification.Builder uninstallFailedNotification = new Notification.Builder(context, "uninstall failure");
        if (returnCode == 0) {
            notificationManager.cancel(uninstallId);
            Toast.makeText(context, context.getString(R.string.uninstall_done_app, appLabel), 1).show();
            return;
        }
        if (returnCode == 2) {
            int legacyStatus = intent.getIntExtra("android.content.pm.extra.LEGACY_STATUS", 0);
            if (legacyStatus == -4) {
                IPackageManager packageManager2 = IPackageManager.Stub.asInterface(ServiceManager.getService("package"));
                List<UserInfo> users2 = userManager.getUsers();
                int blockingUserId3 = -10000;
                int i = 0;
                while (true) {
                    int i2 = i;
                    int i3 = users2.size();
                    if (i2 >= i3) {
                        break;
                    }
                    UserInfo user = users2.get(i2);
                    try {
                        str = appInfo.packageName;
                        users = users2;
                        blockingUserId = blockingUserId3;
                    } catch (RemoteException e) {
                        e = e;
                        users = users2;
                        blockingUserId = blockingUserId3;
                    }
                    try {
                        blockingUserId2 = user.id;
                    } catch (RemoteException e2) {
                        e = e2;
                        packageManager = packageManager2;
                        Log.e(LOG_TAG, "Failed to talk to package manager", e);
                        i = i2 + 1;
                        users2 = users;
                        blockingUserId3 = blockingUserId;
                        packageManager2 = packageManager;
                    }
                    if (!packageManager2.getBlockUninstallForUser(str, blockingUserId2)) {
                        packageManager = packageManager2;
                        i = i2 + 1;
                        users2 = users;
                        blockingUserId3 = blockingUserId;
                        packageManager2 = packageManager;
                    } else {
                        blockingUserId3 = user.id;
                        break;
                    }
                }
                int myUserId = UserHandle.myUserId();
                if (isProfileOfOrSame(userManager, myUserId, blockingUserId3)) {
                    addDeviceManagerButton(context, uninstallFailedNotification);
                } else {
                    addManageUsersButton(context, uninstallFailedNotification);
                }
                if (blockingUserId3 == -10000) {
                    Log.d(LOG_TAG, "Uninstall failed for " + appInfo.packageName + " with code " + returnCode + " no blocking user");
                } else if (blockingUserId3 == 0) {
                    setBigText(uninstallFailedNotification, context.getString(R.string.uninstall_blocked_device_owner));
                } else if (allUsers) {
                    setBigText(uninstallFailedNotification, context.getString(R.string.uninstall_all_blocked_profile_owner));
                } else {
                    setBigText(uninstallFailedNotification, context.getString(R.string.uninstall_blocked_profile_owner));
                }
            } else if (legacyStatus == -2) {
                IDevicePolicyManager dpm = IDevicePolicyManager.Stub.asInterface(ServiceManager.getService("device_policy"));
                int myUserId2 = UserHandle.myUserId();
                Iterator it = userManager.getUsers().iterator();
                while (true) {
                    if (!it.hasNext()) {
                        otherBlockingUser = null;
                        break;
                    }
                    Iterator it2 = it;
                    UserInfo user2 = (UserInfo) it.next();
                    if (isProfileOfOrSame(userManager, myUserId2, user2.id)) {
                        it = it2;
                    } else {
                        try {
                            uninstallFailureChannel = uninstallFailureChannel2;
                        } catch (RemoteException e3) {
                            e = e3;
                            uninstallFailureChannel = uninstallFailureChannel2;
                        }
                        try {
                            if (dpm.packageHasActiveAdmins(appInfo.packageName, user2.id)) {
                                otherBlockingUser = user2;
                                break;
                            }
                        } catch (RemoteException e4) {
                            e = e4;
                            Log.e(LOG_TAG, "Failed to talk to package manager", e);
                            it = it2;
                            uninstallFailureChannel2 = uninstallFailureChannel;
                        }
                        it = it2;
                        uninstallFailureChannel2 = uninstallFailureChannel;
                    }
                }
                if (otherBlockingUser == null) {
                    Log.d(LOG_TAG, "Uninstall failed because " + appInfo.packageName + " is a device admin");
                    addDeviceManagerButton(context, uninstallFailedNotification);
                    setBigText(uninstallFailedNotification, context.getString(R.string.uninstall_failed_device_policy_manager));
                } else {
                    Log.d(LOG_TAG, "Uninstall failed because " + appInfo.packageName + " is a device admin of user " + otherBlockingUser);
                    setBigText(uninstallFailedNotification, String.format(context.getString(R.string.uninstall_failed_device_policy_manager_of_user), otherBlockingUser.name));
                }
            } else {
                Log.d(LOG_TAG, "Uninstall blocked for " + appInfo.packageName + " with legacy code " + legacyStatus);
            }
            uninstallFailedNotification.setContentTitle(context.getString(R.string.uninstall_failed_app, appLabel));
            uninstallFailedNotification.setOngoing(false);
            uninstallFailedNotification.setSmallIcon(R.drawable.ic_error);
            notificationManager.notify(uninstallId, uninstallFailedNotification.build());
        }
        Log.d(LOG_TAG, "Uninstall failed for " + appInfo.packageName + " with code " + returnCode);
        uninstallFailedNotification.setContentTitle(context.getString(R.string.uninstall_failed_app, appLabel));
        uninstallFailedNotification.setOngoing(false);
        uninstallFailedNotification.setSmallIcon(R.drawable.ic_error);
        notificationManager.notify(uninstallId, uninstallFailedNotification.build());
    }

    private boolean isProfileOfOrSame(UserManager userManager, int userId, int profileId) {
        if (userId == profileId) {
            return true;
        }
        UserInfo parentUser = userManager.getProfileParent(profileId);
        return parentUser != null && parentUser.id == userId;
    }

    private void setBigText(Notification.Builder builder, CharSequence text) {
        builder.setStyle(new Notification.BigTextStyle().bigText(text));
    }

    private void addManageUsersButton(Context context, Notification.Builder builder) {
        Intent intent = new Intent("android.settings.USER_SETTINGS");
        intent.setFlags(1342177280);
        builder.addAction(new Notification.Action.Builder(Icon.createWithResource(context, (int) R.drawable.ic_settings_multiuser), context.getString(R.string.manage_users), PendingIntent.getActivity(context, 0, intent, 134217728)).build());
    }

    private void addDeviceManagerButton(Context context, Notification.Builder builder) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.Settings$DeviceAdminSettingsActivity");
        intent.setFlags(1342177280);
        builder.addAction(new Notification.Action.Builder(Icon.createWithResource(context, (int) R.drawable.ic_lock), context.getString(R.string.manage_device_administrators), PendingIntent.getActivity(context, 0, intent, 134217728)).build());
    }
}
