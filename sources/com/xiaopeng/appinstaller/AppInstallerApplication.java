package com.xiaopeng.appinstaller;

import android.app.Application;
import android.content.pm.PackageItemInfo;
import com.xiaopeng.xui.Xui;
/* loaded from: classes.dex */
public class AppInstallerApplication extends Application {
    @Override // android.app.Application
    public void onCreate() {
        super.onCreate();
        PackageItemInfo.setForceSafeLabels(true);
        Xui.init(this);
    }
}
