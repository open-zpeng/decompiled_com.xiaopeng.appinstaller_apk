package com.xiaopeng.xui.app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.utils.XDialogUtils;
import com.xiaopeng.xui.vui.VuiViewScene;
/* loaded from: classes.dex */
public class XDialogPure extends VuiViewScene {
    private Dialog mDialog;
    private int mSystemDialogOffsetY;

    /* loaded from: classes.dex */
    public static class Parameters {
        private boolean mFullScreen = Xui.isDialogFullScreen();
        private int mTheme;

        private Parameters() {
        }

        public static Parameters Builder() {
            return new Parameters();
        }

        public Parameters setFullScreen(boolean z) {
            this.mFullScreen = z;
            return this;
        }

        public Parameters setTheme(int i) {
            this.mTheme = i;
            return this;
        }
    }

    public XDialogPure(Context context) {
        this(context, null);
    }

    public XDialogPure(Context context, Parameters parameters) {
        parameters = parameters == null ? Parameters.Builder() : parameters;
        this.mDialog = parameters.mTheme != 0 ? new Dialog(context, parameters.mTheme) : new Dialog(context, R.style.XAppTheme_XDialog);
        if (parameters.mFullScreen) {
            XDialogUtils.requestFullScreen(this.mDialog);
        }
        this.mSystemDialogOffsetY = (int) context.getResources().getDimension(R.dimen.x_dialog_system_offset_y);
    }

    private void logs(String str) {
        Log.d("XDialogPure", str);
    }

    public void cancel() {
        logs("cancel");
        this.mDialog.cancel();
    }

    public void dismiss() {
        logs("dismiss");
        this.mDialog.dismiss();
    }

    public Dialog getDialog() {
        return this.mDialog;
    }

    public boolean isShowing() {
        return this.mDialog.isShowing();
    }

    @Override // com.xiaopeng.xui.vui.VuiViewScene
    public void onBuildScenePrepare() {
    }

    public void setCancelable(boolean z) {
        this.mDialog.setCancelable(z);
    }

    public void setCanceledOnTouchOutside(boolean z) {
        this.mDialog.setCanceledOnTouchOutside(z);
    }

    public void setContentView(View view) {
        this.mDialog.setContentView(view);
        setVuiView(view);
    }

    public void setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        this.mDialog.setOnCancelListener(onCancelListener);
    }

    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.mDialog.setOnDismissListener(onDismissListener);
    }

    public void setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
        this.mDialog.setOnKeyListener(onKeyListener);
    }

    public void setOnShowListener(DialogInterface.OnShowListener onShowListener) {
        this.mDialog.setOnShowListener(onShowListener);
    }

    public void setSystemDialog(int i) {
        if (this.mDialog.getWindow() != null) {
            this.mDialog.getWindow().setType(i);
        }
    }

    public void show() {
        if (this.mDialog.getWindow() != null) {
            WindowManager.LayoutParams attributes = this.mDialog.getWindow().getAttributes();
            attributes.gravity = 17;
            attributes.y = attributes.type != 9 ? this.mSystemDialogOffsetY : 0;
            this.mDialog.getWindow().setAttributes(attributes);
        }
        this.mDialog.show();
    }
}
