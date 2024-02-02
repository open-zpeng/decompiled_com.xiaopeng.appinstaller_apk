package com.xiaopeng.appinstaller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
/* loaded from: classes.dex */
public class Utils {
    public static void startAppStoreDetail(Context context, String pacakgeName) {
        Intent appstoreIntent = new Intent();
        String uri = "market://details?id=" + pacakgeName;
        appstoreIntent.setData(Uri.parse(uri));
        appstoreIntent.setPackage("com.xiaopeng.appstore");
        if (!(context instanceof Activity)) {
            appstoreIntent.addFlags(268435456);
        }
        context.startActivity(appstoreIntent);
    }
}
