package com.xiaopeng.appinstaller;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
/* loaded from: classes.dex */
public class ErrorDialogActivity extends DialogActivityAbs {
    public static void startAction(Context context, int titleResId, int msgResId) {
        Intent it = new Intent(context, ErrorDialogActivity.class);
        it.setFlags(33554432);
        it.putExtra("PARAM_TITLE_RES", titleResId);
        it.putExtra("PARAM_DESC_RES", msgResId);
        context.startActivity(it);
    }

    @Override // com.xiaopeng.appinstaller.DialogActivityAbs, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        int titleResId = intent.getIntExtra("PARAM_TITLE_RES", 0);
        if (titleResId == 0) {
            this.mDialogTitle.setVisibility(8);
        } else {
            this.mDialogTitle.setText(titleResId);
            this.mDialogTitle.setVisibility(0);
        }
        int msgResId = intent.getIntExtra("PARAM_DESC_RES", 0);
        if (msgResId == 0) {
            this.mDialogMsg.setVisibility(8);
        } else {
            this.mDialogMsg.setText(msgResId);
            this.mDialogMsg.setVisibility(0);
        }
        this.mDialogBtn2.setVisibility(8);
        this.mDialogBtn1.setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.appinstaller.ErrorDialogActivity.1
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                ErrorDialogActivity.this.setResult(1);
                ErrorDialogActivity.this.finish();
            }
        });
        hideLoading();
    }
}
