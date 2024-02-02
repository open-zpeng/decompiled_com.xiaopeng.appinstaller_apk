package com.xiaopeng.appinstaller;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.xiaopeng.appinstaller.PackageUtil;
import java.io.File;
import java.util.List;
/* loaded from: classes.dex */
public class InstallSuccess extends Activity {
    private static final String LOG_TAG = InstallSuccess.class.getSimpleName();

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        PackageUtil.AppSnippet as;
        List<ResolveInfo> list;
        super.onCreate(savedInstanceState);
        if (getIntent().getBooleanExtra("android.intent.extra.RETURN_RESULT", false)) {
            Intent result = new Intent();
            result.putExtra("android.intent.extra.INSTALL_RESULT", 1);
            setResult(-1, result);
            finish();
            return;
        }
        Intent intent = getIntent();
        final ApplicationInfo appInfo = (ApplicationInfo) intent.getParcelableExtra("com.android.packageinstaller.applicationInfo");
        Uri packageURI = intent.getData();
        setContentView(R.layout.install_success);
        PackageManager pm = getPackageManager();
        if ("package".equals(packageURI.getScheme())) {
            as = new PackageUtil.AppSnippet(pm.getApplicationLabel(appInfo), pm.getApplicationIcon(appInfo));
        } else {
            File sourceFile = new File(packageURI.getPath());
            as = PackageUtil.getAppSnippet(this, appInfo, sourceFile);
        }
        PackageUtil.initSnippetForNewApp(this, as, R.id.app_snippet);
        findViewById(R.id.done_button).setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.appinstaller.-$$Lambda$InstallSuccess$bWjS5wV715LjwkWIGEoUQcOWtPo
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                InstallSuccess.lambda$onCreate$0(InstallSuccess.this, appInfo, view);
            }
        });
        final Intent launchIntent = getPackageManager().getLaunchIntentForPackage(appInfo.packageName);
        boolean enabled = false;
        if (launchIntent != null && (list = getPackageManager().queryIntentActivities(launchIntent, 0)) != null && list.size() > 0) {
            enabled = true;
        }
        Button launchButton = (Button) findViewById(R.id.launch_button);
        if (enabled) {
            launchButton.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.appinstaller.-$$Lambda$InstallSuccess$ZvtpDdw4MVKxQD-uTI9oqCJCwYw
                @Override // android.view.View.OnClickListener
                public final void onClick(View view) {
                    InstallSuccess.lambda$onCreate$1(InstallSuccess.this, launchIntent, view);
                }
            });
        } else {
            launchButton.setEnabled(false);
        }
    }

    public static /* synthetic */ void lambda$onCreate$0(InstallSuccess installSuccess, ApplicationInfo appInfo, View view) {
        if (appInfo.packageName != null) {
            String str = LOG_TAG;
            Log.i(str, "Finished installing " + appInfo.packageName);
        }
        installSuccess.finish();
    }

    public static /* synthetic */ void lambda$onCreate$1(InstallSuccess installSuccess, Intent launchIntent, View view) {
        try {
            installSuccess.startActivity(launchIntent);
        } catch (ActivityNotFoundException | SecurityException e) {
            Log.e(LOG_TAG, "Could not start activity", e);
        }
        installSuccess.finish();
    }
}
