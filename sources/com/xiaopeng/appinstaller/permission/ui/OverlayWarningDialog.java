package com.xiaopeng.appinstaller.permission.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.xiaopeng.appinstaller.R;
/* loaded from: classes.dex */
public class OverlayWarningDialog extends Activity implements DialogInterface.OnClickListener, DialogInterface.OnDismissListener {
    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new AlertDialog.Builder(this).setTitle(R.string.screen_overlay_title).setMessage(R.string.screen_overlay_message).setPositiveButton(R.string.screen_overlay_button, this).setOnDismissListener(this).show();
    }

    @Override // android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        finish();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        finish();
        try {
            startActivity(new Intent("android.settings.action.MANAGE_OVERLAY_PERMISSION"));
        } catch (ActivityNotFoundException e) {
            Log.w("OverlayWarningDialog", "No manage overlay settings", e);
        }
    }
}
