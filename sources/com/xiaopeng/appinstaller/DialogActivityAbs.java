package com.xiaopeng.appinstaller;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.xui.widget.XButton;
import com.xiaopeng.xui.widget.XTextView;
/* loaded from: classes.dex */
public abstract class DialogActivityAbs extends AppCompatActivity {
    protected XButton mDialogBtn1;
    protected XButton mDialogBtn2;
    protected XTextView mDialogMsg;
    protected XTextView mDialogTitle;
    protected View mLoadingView;

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(14);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_xp_dialog_activity);
        this.mDialogTitle = (XTextView) findViewById(R.id.dialog_title);
        this.mDialogMsg = (XTextView) findViewById(R.id.dialog_message);
        this.mLoadingView = findViewById(R.id.loading);
        this.mDialogBtn1 = (XButton) findViewById(R.id.dialog_button1);
        this.mDialogBtn2 = (XButton) findViewById(R.id.dialog_button2);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void showLoading() {
        this.mDialogTitle.setVisibility(8);
        this.mDialogMsg.setVisibility(8);
        this.mLoadingView.setVisibility(0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void hideLoading() {
        this.mLoadingView.setVisibility(8);
        this.mDialogTitle.setVisibility(0);
        this.mDialogMsg.setVisibility(0);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(5894);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStop() {
        super.onStop();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(-1);
    }

    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (ThemeManager.isThemeChanged(newConfig)) {
            ThemeManager.setWindowBackgroundDrawable(newConfig, getWindow(), getDrawable(R.drawable.x_bg_dialog));
        }
    }
}
