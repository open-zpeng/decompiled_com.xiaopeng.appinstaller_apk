package com.xiaopeng.appinstaller;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageParser;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.File;
/* loaded from: classes.dex */
public class PackageUtil {
    private static final String LOG_TAG = PackageUtil.class.getSimpleName();

    public static PackageParser.Package getPackageInfo(Context context, File sourceFile) {
        PackageParser parser = new PackageParser();
        parser.setCallback(new PackageParser.CallbackImpl(context.getPackageManager()));
        try {
            return parser.parsePackage(sourceFile, 0);
        } catch (PackageParser.PackageParserException e) {
            return null;
        }
    }

    public static View initSnippetForNewApp(Activity pContext, AppSnippet as, int snippetId) {
        View appSnippet = pContext.findViewById(snippetId);
        if (as.icon != null) {
            ((ImageView) appSnippet.findViewById(R.id.app_icon)).setImageDrawable(as.icon);
        }
        ((TextView) appSnippet.findViewById(R.id.app_name)).setText(as.label);
        return appSnippet;
    }

    /* loaded from: classes.dex */
    public static class AppSnippet {
        public Drawable icon;
        public CharSequence label;

        public AppSnippet(CharSequence label, Drawable icon) {
            this.label = label;
            this.icon = icon;
        }
    }

    public static AppSnippet getAppSnippet(Activity pContext, ApplicationInfo appInfo, File sourceFile) {
        String archiveFilePath = sourceFile.getAbsolutePath();
        Resources pRes = pContext.getResources();
        AssetManager assmgr = new AssetManager();
        assmgr.addAssetPath(archiveFilePath);
        Resources res = new Resources(assmgr, pRes.getDisplayMetrics(), pRes.getConfiguration());
        CharSequence label = null;
        if (appInfo.labelRes != 0) {
            try {
                label = res.getText(appInfo.labelRes);
            } catch (Resources.NotFoundException e) {
            }
        }
        if (label == null) {
            label = appInfo.nonLocalizedLabel != null ? appInfo.nonLocalizedLabel : appInfo.packageName;
        }
        Drawable icon = null;
        try {
            if (appInfo.icon != 0) {
                try {
                    icon = res.getDrawable(appInfo.icon);
                } catch (Resources.NotFoundException e2) {
                }
            }
            if (icon == null) {
                icon = pContext.getPackageManager().getDefaultActivityIcon();
            }
        } catch (OutOfMemoryError e3) {
            Log.i(LOG_TAG, "Could not load app icon", e3);
        }
        return new AppSnippet(label, icon);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int getMaxTargetSdkVersionForUid(Context context, int uid) {
        PackageManager pm = context.getPackageManager();
        String[] packages = pm.getPackagesForUid(uid);
        if (packages == null) {
            return -1;
        }
        int targetSdkVersion = -1;
        for (String packageName : packages) {
            try {
                ApplicationInfo info = pm.getApplicationInfo(packageName, 0);
                targetSdkVersion = Math.max(targetSdkVersion, info.targetSdkVersion);
            } catch (PackageManager.NameNotFoundException e) {
            }
        }
        return targetSdkVersion;
    }
}
