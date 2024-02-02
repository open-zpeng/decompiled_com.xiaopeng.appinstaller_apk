package com.xiaopeng.xui.app;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
/* loaded from: classes.dex */
public class XFullscreenActivity extends AppCompatActivity {
    public static final int FEATURE_XUI_FULLSCREEN = 14;

    private static void enterFullscreen(Activity activity, int i) {
        if (i > 0) {
            activity.requestWindowFeature(i);
        }
        activity.getWindow().getDecorView().setSystemUiVisibility(5894);
    }

    private static void exitFullscreen(Activity activity) {
        activity.getWindow().getDecorView().setSystemUiVisibility(-1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        enterFullscreen(this, 14);
    }
}
