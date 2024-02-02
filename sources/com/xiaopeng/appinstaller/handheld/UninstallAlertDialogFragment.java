package com.xiaopeng.appinstaller.handheld;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.pm.UserInfo;
import android.os.Bundle;
import android.os.Process;
import android.os.UserManager;
import com.xiaopeng.appinstaller.R;
import com.xiaopeng.appinstaller.UninstallerActivity;
/* loaded from: classes.dex */
public class UninstallAlertDialogFragment extends DialogFragment implements DialogInterface.OnClickListener {
    @Override // android.app.DialogFragment
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        PackageManager pm = getActivity().getPackageManager();
        UninstallerActivity.DialogInfo dialogInfo = ((UninstallerActivity) getActivity()).getDialogInfo();
        CharSequence appLabel = dialogInfo.appInfo.loadSafeLabel(pm);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        StringBuilder messageBuilder = new StringBuilder();
        if (dialogInfo.activityInfo != null) {
            CharSequence activityLabel = dialogInfo.activityInfo.loadSafeLabel(pm);
            if (!activityLabel.equals(appLabel)) {
                messageBuilder.append(getString(R.string.uninstall_activity_text, new Object[]{activityLabel}));
                messageBuilder.append(" ");
                messageBuilder.append(appLabel);
                messageBuilder.append(".\n\n");
            }
        }
        boolean isUpdate = (dialogInfo.appInfo.flags & 128) != 0;
        UserManager userManager = UserManager.get(getActivity());
        if (isUpdate) {
            if (isSingleUser(userManager)) {
                messageBuilder.append(getString(R.string.uninstall_update_text));
            } else {
                messageBuilder.append(getString(R.string.uninstall_update_text_multiuser));
            }
        } else if (dialogInfo.allUsers && !isSingleUser(userManager)) {
            messageBuilder.append(getString(R.string.uninstall_application_text_all_users));
        } else if (!dialogInfo.user.equals(Process.myUserHandle())) {
            UserInfo userInfo = userManager.getUserInfo(dialogInfo.user.getIdentifier());
            messageBuilder.append(getString(R.string.uninstall_application_text_user, new Object[]{userInfo.name}));
        } else {
            messageBuilder.append(getString(R.string.uninstall_application_text));
        }
        dialogBuilder.setTitle(appLabel);
        dialogBuilder.setPositiveButton(17039370, this);
        dialogBuilder.setNegativeButton(17039360, this);
        dialogBuilder.setMessage(messageBuilder.toString());
        return dialogBuilder.create();
    }

    @Override // android.content.DialogInterface.OnClickListener
    public void onClick(DialogInterface dialog, int which) {
        if (which == -1) {
            ((UninstallerActivity) getActivity()).startUninstallProgress();
        } else {
            ((UninstallerActivity) getActivity()).dispatchAborted();
        }
    }

    @Override // android.app.DialogFragment, android.content.DialogInterface.OnDismissListener
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (isAdded()) {
            getActivity().finish();
        }
    }

    private boolean isSingleUser(UserManager userManager) {
        int userCount = userManager.getUserCount();
        if (userCount != 1) {
            return UserManager.isSplitSystemUser() && userCount == 2;
        }
        return true;
    }
}
