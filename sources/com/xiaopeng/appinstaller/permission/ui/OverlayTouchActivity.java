package com.xiaopeng.appinstaller.permission.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
/* loaded from: classes.dex */
public class OverlayTouchActivity extends AppCompatActivity {
    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        getWindow().addPrivateFlags(524288);
        super.onCreate(savedInstanceState);
    }
}
