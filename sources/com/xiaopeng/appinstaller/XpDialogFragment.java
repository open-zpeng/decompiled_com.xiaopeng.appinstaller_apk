package com.xiaopeng.appinstaller;

import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.xiaopeng.xui.utils.XDialogUtils;
import com.xiaopeng.xui.widget.dialogview.XDialogView;
/* loaded from: classes.dex */
public class XpDialogFragment extends DialogFragment {
    protected XDialogView mXDialogView;

    @Override // android.app.Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.mXDialogView = new XDialogView(getContext(), R.style.XDialogView);
        XDialogUtils.requestFullScreen(getDialog());
        return this.mXDialogView.getContentView();
    }

    @Override // android.app.DialogFragment
    public int getTheme() {
        return 2131821278;
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("XpDialogFragment", "onCreate: " + getClass().getSimpleName() + "@" + hashCode());
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public void onStart() {
        super.onStart();
        Log.i("XpDialogFragment", "onStart: " + getClass().getSimpleName() + "@" + hashCode());
    }

    @Override // android.app.Fragment
    public void onResume() {
        super.onResume();
        Log.i("XpDialogFragment", "onResume: " + getClass().getSimpleName() + "@" + hashCode());
    }

    @Override // android.app.Fragment
    public void onPause() {
        super.onPause();
        Log.i("XpDialogFragment", "onPause: " + getClass().getSimpleName() + "@" + hashCode());
    }

    @Override // android.app.DialogFragment, android.app.Fragment
    public void onStop() {
        super.onStop();
        Log.i("XpDialogFragment", "onStop: " + getClass().getSimpleName() + "@" + hashCode());
    }

    @Override // android.app.Fragment
    public void onDestroy() {
        super.onDestroy();
        Log.i("XpDialogFragment", "onDestroy: " + getClass().getSimpleName() + "@" + hashCode());
    }

    @Override // android.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Log.i("XpDialogFragment", "onDismiss: " + getClass().getSimpleName() + "@" + hashCode());
    }
}
