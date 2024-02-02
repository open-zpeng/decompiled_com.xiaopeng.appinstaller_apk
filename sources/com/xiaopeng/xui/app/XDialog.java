package com.xiaopeng.xui.app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import b.a.a.a.c;
import b.a.a.a.d;
import com.xiaopeng.libtheme.ThemeViewModel;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.app.XDialogInterface;
import com.xiaopeng.xui.utils.XDialogUtils;
import com.xiaopeng.xui.vui.IVuiViewScene;
import com.xiaopeng.xui.widget.dialogview.XDialogView;
import com.xiaopeng.xui.widget.dialogview.XDialogViewInterface;
import java.util.List;
/* loaded from: classes.dex */
public class XDialog implements IVuiViewScene {
    private static int sObjectSize;
    private Context mContext;
    private Dialog mDialog;
    private XDialogInterface.OnClickListener mNegativeListener;
    private XDialogViewInterface.OnClickListener mNegativeListenerProxy;
    private XDialogInterface.OnCloseListener mOnCloseListener;
    private XDialogViewInterface.OnCloseListener mOnCloseListenerProxy;
    private XDialogInterface.OnCountDownListener mOnCountDownListener;
    private XDialogViewInterface.OnCountDownListener mOnCountDownListenerProxy;
    private DialogInterface.OnKeyListener mOnKeyListener;
    private XDialogInterface.OnClickListener mPositiveListener;
    private XDialogViewInterface.OnClickListener mPositiveListenerProxy;
    private int mSystemDialogOffsetY;
    private XDialogView mXDialogView;

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

    public XDialog(Context context) {
        this(context, 0);
    }

    public XDialog(Context context, int i) {
        this(context, i, null);
    }

    public XDialog(Context context, int i, Parameters parameters) {
        this.mXDialogView = new XDialogView(context, i);
        this.mContext = context;
        parameters = parameters == null ? Parameters.Builder() : parameters;
        this.mDialog = parameters.mTheme != 0 ? new Dialog(context, parameters.mTheme) : new Dialog(context, R.style.XAppTheme_XDialog);
        if (parameters.mFullScreen) {
            XDialogUtils.requestFullScreen(this.mDialog);
        }
        this.mDialog.setContentView(this.mXDialogView.getContentView());
        init();
        sObjectSize++;
    }

    public XDialog(Context context, Parameters parameters) {
        this(context, 0, parameters);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void a() {
        if (this.mDialog.getWindow() != null) {
            this.mDialog.getWindow().setBackgroundDrawableResource(R.drawable.x_bg_dialog);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void a(XDialogView xDialogView) {
        dismiss();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean a(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
        DialogInterface.OnKeyListener onKeyListener = this.mOnKeyListener;
        if (onKeyListener == null || !onKeyListener.onKey(dialogInterface, i, keyEvent)) {
            return this.mXDialogView.onKey(i, keyEvent);
        }
        logs("custom key listener return true  keyCode : " + i + ", event " + keyEvent.getAction());
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean a(XDialogView xDialogView, int i) {
        XDialogInterface.OnCountDownListener onCountDownListener = this.mOnCountDownListener;
        if (onCountDownListener != null) {
            return onCountDownListener.onCountDown(this, i);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void b(XDialogView xDialogView, int i) {
        XDialogInterface.OnClickListener onClickListener = this.mNegativeListener;
        if (onClickListener != null) {
            onClickListener.onClick(this, i);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ boolean b(XDialogView xDialogView) {
        XDialogInterface.OnCloseListener onCloseListener = this.mOnCloseListener;
        if (onCloseListener != null) {
            return onCloseListener.onClose(this);
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public /* synthetic */ void c(XDialogView xDialogView, int i) {
        XDialogInterface.OnClickListener onClickListener = this.mPositiveListener;
        if (onClickListener != null) {
            onClickListener.onClick(this, i);
        }
    }

    private void init() {
        this.mSystemDialogOffsetY = (int) this.mContext.getResources().getDimension(R.dimen.x_dialog_system_offset_y);
        this.mXDialogView.setOnDismissListener(new XDialogViewInterface.OnDismissListener() { // from class: com.xiaopeng.xui.app.-$$Lambda$XDialog$loUzlfjSf2ScdbF1jOdXNGHyR1g
            @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewInterface.OnDismissListener
            public final void onDismiss(XDialogView xDialogView) {
                XDialog.this.a(xDialogView);
            }
        });
        this.mXDialogView.setThemeCallback(new ThemeViewModel.OnCallback() { // from class: com.xiaopeng.xui.app.-$$Lambda$XDialog$Hy_tlGW9-Kgz-pqYDxAH1J477yA
            @Override // com.xiaopeng.libtheme.ThemeViewModel.OnCallback
            public final void onThemeChanged() {
                XDialog.this.a();
            }
        });
        this.mDialog.setOnKeyListener(new DialogInterface.OnKeyListener() { // from class: com.xiaopeng.xui.app.-$$Lambda$XDialog$h-r0Js-ZeH1z02GDF3IzgA8-glo
            @Override // android.content.DialogInterface.OnKeyListener
            public final boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                boolean a2;
                a2 = XDialog.this.a(dialogInterface, i, keyEvent);
                return a2;
            }
        });
    }

    private void initCloseListenerProxy() {
        if (this.mOnCloseListenerProxy == null) {
            this.mOnCloseListenerProxy = new XDialogViewInterface.OnCloseListener() { // from class: com.xiaopeng.xui.app.-$$Lambda$XDialog$uEzCVsReFVzzLmkevl2kTXP4ziM
                @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewInterface.OnCloseListener
                public final boolean onClose(XDialogView xDialogView) {
                    boolean b2;
                    b2 = XDialog.this.b(xDialogView);
                    return b2;
                }
            };
            this.mXDialogView.setOnCloseListener(this.mOnCloseListenerProxy);
        }
    }

    private void initCountDownListenerProxy() {
        if (this.mOnCountDownListenerProxy == null) {
            this.mOnCountDownListenerProxy = new XDialogViewInterface.OnCountDownListener() { // from class: com.xiaopeng.xui.app.-$$Lambda$XDialog$PxgiByGtBqohidjbCjskV8dpfPw
                @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewInterface.OnCountDownListener
                public final boolean onCountDown(XDialogView xDialogView, int i) {
                    boolean a2;
                    a2 = XDialog.this.a(xDialogView, i);
                    return a2;
                }
            };
            this.mXDialogView.setOnCountDownListener(this.mOnCountDownListenerProxy);
        }
    }

    private void initNegativeListenerProxy() {
        if (this.mNegativeListenerProxy == null) {
            this.mNegativeListenerProxy = new XDialogViewInterface.OnClickListener() { // from class: com.xiaopeng.xui.app.-$$Lambda$XDialog$wmTQpk784411XndkQ_zRH8B2Fd8
                @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewInterface.OnClickListener
                public final void onClick(XDialogView xDialogView, int i) {
                    XDialog.this.b(xDialogView, i);
                }
            };
        }
    }

    private void initPositiveListenerProxy() {
        if (this.mPositiveListenerProxy == null) {
            this.mPositiveListenerProxy = new XDialogViewInterface.OnClickListener() { // from class: com.xiaopeng.xui.app.-$$Lambda$XDialog$lH12VNBTmiL-sR3uaEtSf_-5kBY
                @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewInterface.OnClickListener
                public final void onClick(XDialogView xDialogView, int i) {
                    XDialog.this.c(xDialogView, i);
                }
            };
        }
    }

    private void logs(String str) {
        Log.d("XDialog", str + "--hashcode " + hashCode());
    }

    public void cancel() {
        logs("cancel");
        this.mDialog.cancel();
    }

    public void dismiss() {
        logs("dismiss");
        this.mDialog.dismiss();
    }

    protected void finalize() {
        super.finalize();
        sObjectSize--;
        logs(" finalize object size " + sObjectSize);
    }

    public ViewGroup getContentView() {
        return this.mXDialogView.getContentView();
    }

    public Dialog getDialog() {
        return this.mDialog;
    }

    public boolean isNegativeButtonEnable() {
        return this.mXDialogView.isNegativeButtonEnable();
    }

    public boolean isNegativeButtonShowing() {
        return this.mXDialogView.isNegativeButtonShowing();
    }

    public boolean isPositiveButtonEnable() {
        return this.mXDialogView.isPositiveButtonEnable();
    }

    public boolean isPositiveButtonShowing() {
        return this.mXDialogView.isPositiveButtonShowing();
    }

    public boolean isShowing() {
        return this.mDialog.isShowing();
    }

    public XDialog setCancelable(boolean z) {
        this.mDialog.setCancelable(z);
        return this;
    }

    public XDialog setCanceledOnTouchOutside(boolean z) {
        this.mDialog.setCanceledOnTouchOutside(z);
        return this;
    }

    public XDialog setCloseVisibility(boolean z) {
        this.mXDialogView.setCloseVisibility(z);
        return this;
    }

    public XDialog setCustomView(int i) {
        this.mXDialogView.setCustomView(i);
        return this;
    }

    public XDialog setCustomView(int i, boolean z) {
        this.mXDialogView.setCustomView(i, z);
        return this;
    }

    public XDialog setCustomView(View view) {
        this.mXDialogView.setCustomView(view);
        return this;
    }

    public XDialog setCustomView(View view, boolean z) {
        this.mXDialogView.setCustomView(view, z);
        return this;
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void setCustomViewIdList(List<Integer> list) {
        this.mXDialogView.setCustomViewIdList(list);
    }

    public XDialog setIcon(int i) {
        this.mXDialogView.setIcon(i);
        return this;
    }

    public XDialog setIcon(Drawable drawable) {
        this.mXDialogView.setIcon(drawable);
        return this;
    }

    public XDialog setMessage(int i) {
        this.mXDialogView.setMessage(i);
        return this;
    }

    public XDialog setMessage(CharSequence charSequence) {
        this.mXDialogView.setMessage(charSequence);
        return this;
    }

    public XDialog setNegativeButton(CharSequence charSequence) {
        this.mXDialogView.setNegativeButton(charSequence);
        return this;
    }

    public XDialog setNegativeButton(CharSequence charSequence, XDialogInterface.OnClickListener onClickListener) {
        this.mNegativeListener = onClickListener;
        if (onClickListener != null) {
            initNegativeListenerProxy();
        }
        this.mXDialogView.setNegativeButton(charSequence, this.mNegativeListenerProxy);
        return this;
    }

    public XDialog setNegativeButtonEnable(boolean z) {
        this.mXDialogView.setNegativeButtonEnable(z);
        return this;
    }

    public XDialog setNegativeButtonInterceptDismiss(boolean z) {
        this.mXDialogView.setNegativeButtonInterceptDismiss(z);
        return this;
    }

    public XDialog setOnCancelListener(DialogInterface.OnCancelListener onCancelListener) {
        this.mDialog.setOnCancelListener(onCancelListener);
        return this;
    }

    public XDialog setOnCloseListener(XDialogInterface.OnCloseListener onCloseListener) {
        this.mOnCloseListener = onCloseListener;
        if (onCloseListener != null) {
            initCloseListenerProxy();
        }
        return this;
    }

    public XDialog setOnCountDownListener(XDialogInterface.OnCountDownListener onCountDownListener) {
        this.mOnCountDownListener = onCountDownListener;
        if (onCountDownListener != null) {
            initCountDownListenerProxy();
        }
        return this;
    }

    public XDialog setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.mDialog.setOnDismissListener(onDismissListener);
        return this;
    }

    public XDialog setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
        this.mOnKeyListener = onKeyListener;
        return this;
    }

    public XDialog setOnShowListener(DialogInterface.OnShowListener onShowListener) {
        this.mDialog.setOnShowListener(onShowListener);
        return this;
    }

    public XDialog setPositiveButton(CharSequence charSequence) {
        this.mXDialogView.setPositiveButton(charSequence);
        return this;
    }

    public XDialog setPositiveButton(CharSequence charSequence, XDialogInterface.OnClickListener onClickListener) {
        this.mPositiveListener = onClickListener;
        if (onClickListener != null) {
            initPositiveListenerProxy();
        }
        this.mXDialogView.setPositiveButton(charSequence, this.mPositiveListenerProxy);
        return this;
    }

    public XDialog setPositiveButtonEnable(boolean z) {
        this.mXDialogView.setPositiveButtonEnable(z);
        return this;
    }

    public XDialog setPositiveButtonInterceptDismiss(boolean z) {
        this.mXDialogView.setPositiveButtonInterceptDismiss(z);
        return this;
    }

    public XDialog setSystemDialog(int i) {
        if (this.mDialog.getWindow() != null) {
            this.mDialog.getWindow().setType(i);
        }
        return this;
    }

    public XDialog setTitle(int i) {
        this.mXDialogView.setTitle(i);
        return this;
    }

    public XDialog setTitle(CharSequence charSequence) {
        this.mXDialogView.setTitle(charSequence);
        return this;
    }

    public XDialog setTitleVisibility(boolean z) {
        this.mXDialogView.setTitleVisibility(z);
        return this;
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void setVuiElementListener(c cVar) {
        this.mXDialogView.setVuiElementListener(cVar);
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void setVuiEngine(d dVar) {
        this.mXDialogView.setVuiEngine(dVar);
    }

    @Override // com.xiaopeng.xui.vui.IVuiViewScene
    public void setVuiSceneId(String str) {
        this.mXDialogView.setVuiSceneId(str);
    }

    public void show() {
        show(0, 0);
    }

    public void show(int i, int i2) {
        logs("show");
        if (i > 0 && i2 == 0) {
            this.mXDialogView.startPositiveButtonCountDown(i);
        }
        if (i2 > 0 && i == 0) {
            this.mXDialogView.startNegativeButtonCountDown(i2);
        }
        if (this.mDialog.getWindow() != null) {
            WindowManager.LayoutParams attributes = this.mDialog.getWindow().getAttributes();
            attributes.gravity = 17;
            attributes.y = attributes.type != 9 ? this.mSystemDialogOffsetY : 0;
            this.mDialog.getWindow().setAttributes(attributes);
        }
        this.mDialog.show();
    }
}
