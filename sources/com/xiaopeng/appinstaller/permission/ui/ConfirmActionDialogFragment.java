package com.xiaopeng.appinstaller.permission.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import com.xiaopeng.appinstaller.R;
/* loaded from: classes.dex */
public final class ConfirmActionDialogFragment extends DialogFragment {

    /* loaded from: classes.dex */
    public interface OnActionConfirmedListener {
        void onActionConfirmed(String str);
    }

    public static ConfirmActionDialogFragment newInstance(CharSequence message, String action) {
        Bundle arguments = new Bundle();
        arguments.putCharSequence("MESSAGE", message);
        arguments.putString("ACTION", action);
        ConfirmActionDialogFragment fragment = new ConfirmActionDialogFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override // android.app.DialogFragment
    public Dialog onCreateDialog(Bundle bundle) {
        return new AlertDialog.Builder(getContext()).setMessage(getArguments().getString("MESSAGE")).setNegativeButton(R.string.cancel, (DialogInterface.OnClickListener) null).setPositiveButton(R.string.grant_dialog_button_deny_anyway, new DialogInterface.OnClickListener() { // from class: com.xiaopeng.appinstaller.permission.ui.ConfirmActionDialogFragment.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                Activity activity = ConfirmActionDialogFragment.this.getActivity();
                if (activity instanceof OnActionConfirmedListener) {
                    String groupName = ConfirmActionDialogFragment.this.getArguments().getString("ACTION");
                    ((OnActionConfirmedListener) activity).onActionConfirmed(groupName);
                }
            }
        }).create();
    }
}
