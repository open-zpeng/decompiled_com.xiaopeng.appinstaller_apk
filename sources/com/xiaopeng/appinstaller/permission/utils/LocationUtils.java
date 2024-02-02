package com.xiaopeng.appinstaller.permission.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.ILocationManager;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.provider.Settings;
import com.xiaopeng.appinstaller.R;
/* loaded from: classes.dex */
public class LocationUtils {
    public static void showLocationDialog(final Context context, CharSequence label) {
        new AlertDialog.Builder(context).setIcon(R.drawable.ic_dialog_alert_material).setTitle(17039380).setMessage(context.getString(R.string.location_warning, label)).setNegativeButton(R.string.ok, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.location_settings, new DialogInterface.OnClickListener() { // from class: com.xiaopeng.appinstaller.permission.utils.LocationUtils.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
            }
        }).show();
    }

    public static boolean isLocationEnabled(Context context) {
        return Settings.Secure.getInt(context.getContentResolver(), "location_mode", 0) != 0;
    }

    public static boolean isLocationGroupAndProvider(String groupName, String packageName) {
        return "android.permission-group.LOCATION".equals(groupName) && isNetworkLocationProvider(packageName);
    }

    private static boolean isNetworkLocationProvider(String packageName) {
        ILocationManager locationService = ILocationManager.Stub.asInterface(ServiceManager.getService("location"));
        try {
            return packageName.equals(locationService.getNetworkProviderPackage());
        } catch (RemoteException e) {
            return false;
        }
    }
}
