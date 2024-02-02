package com.xiaopeng.xui.widget.dialogview;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import com.xiaopeng.libtheme.ThemeViewModel;
import com.xiaopeng.xui.vui.VuiViewScene;
import com.xiaopeng.xui.widget.dialogview.XDialogViewInterface;
/* loaded from: classes.dex */
public class XDialogView extends VuiViewScene {
    private XDialogViewDelegate mXDialogViewDelegate;

    public XDialogView(Context context) {
        this(context, 0);
    }

    public XDialogView(Context context, int i) {
        this.mXDialogViewDelegate = XDialogViewDelegate.create(this, context, i);
        setVuiView(this.mXDialogViewDelegate.getContentView());
    }

    public ViewGroup getContentView() {
        return this.mXDialogViewDelegate.getContentView();
    }

    public boolean isNegativeButtonEnable() {
        return this.mXDialogViewDelegate.isNegativeButtonEnable();
    }

    public boolean isNegativeButtonShowing() {
        return this.mXDialogViewDelegate.isNegativeButtonShowing();
    }

    public boolean isPositiveButtonEnable() {
        return this.mXDialogViewDelegate.isPositiveButtonEnable();
    }

    public boolean isPositiveButtonShowing() {
        return this.mXDialogViewDelegate.isPositiveButtonShowing();
    }

    @Override // com.xiaopeng.xui.vui.VuiViewScene
    public void onBuildScenePrepare() {
        this.mXDialogViewDelegate.onBuildScenePrepare();
    }

    public boolean onKey(int i, KeyEvent keyEvent) {
        return this.mXDialogViewDelegate.onKey(i, keyEvent);
    }

    public XDialogView setCloseVisibility(boolean z) {
        this.mXDialogViewDelegate.setCloseVisibility(z);
        return this;
    }

    public XDialogView setCustomView(int i) {
        this.mXDialogViewDelegate.setCustomView(i);
        return this;
    }

    public XDialogView setCustomView(int i, boolean z) {
        this.mXDialogViewDelegate.setCustomView(i, z);
        return this;
    }

    public XDialogView setCustomView(View view) {
        this.mXDialogViewDelegate.setCustomView(view);
        return this;
    }

    public XDialogView setCustomView(View view, boolean z) {
        this.mXDialogViewDelegate.setCustomView(view, z);
        return this;
    }

    public XDialogView setIcon(int i) {
        this.mXDialogViewDelegate.setIcon(i);
        return this;
    }

    public XDialogView setIcon(Drawable drawable) {
        this.mXDialogViewDelegate.setIcon(drawable);
        return this;
    }

    public XDialogView setMessage(int i) {
        this.mXDialogViewDelegate.setMessage(i);
        return this;
    }

    public XDialogView setMessage(CharSequence charSequence) {
        this.mXDialogViewDelegate.setMessage(charSequence);
        return this;
    }

    public XDialogView setNegativeButton(CharSequence charSequence) {
        this.mXDialogViewDelegate.setNegativeButton(charSequence);
        return this;
    }

    public XDialogView setNegativeButton(CharSequence charSequence, XDialogViewInterface.OnClickListener onClickListener) {
        this.mXDialogViewDelegate.setNegativeButton(charSequence, onClickListener);
        return this;
    }

    public XDialogView setNegativeButtonEnable(boolean z) {
        this.mXDialogViewDelegate.setNegativeButtonEnable(z);
        return this;
    }

    public XDialogView setNegativeButtonInterceptDismiss(boolean z) {
        this.mXDialogViewDelegate.setNegativeButtonInterceptDismiss(z);
        return this;
    }

    public XDialogView setOnCloseListener(XDialogViewInterface.OnCloseListener onCloseListener) {
        this.mXDialogViewDelegate.setOnCloseListener(onCloseListener);
        return this;
    }

    public XDialogView setOnCountDownListener(XDialogViewInterface.OnCountDownListener onCountDownListener) {
        this.mXDialogViewDelegate.setOnCountDownListener(onCountDownListener);
        return this;
    }

    public XDialogView setOnDismissListener(XDialogViewInterface.OnDismissListener onDismissListener) {
        this.mXDialogViewDelegate.setOnDismissListener(onDismissListener);
        return this;
    }

    public XDialogView setPositiveButton(CharSequence charSequence) {
        this.mXDialogViewDelegate.setPositiveButton(charSequence);
        return this;
    }

    public XDialogView setPositiveButton(CharSequence charSequence, XDialogViewInterface.OnClickListener onClickListener) {
        this.mXDialogViewDelegate.setPositiveButton(charSequence, onClickListener);
        return this;
    }

    public XDialogView setPositiveButtonEnable(boolean z) {
        this.mXDialogViewDelegate.setPositiveButtonEnable(z);
        return this;
    }

    public XDialogView setPositiveButtonInterceptDismiss(boolean z) {
        this.mXDialogViewDelegate.setPositiveButtonInterceptDismiss(z);
        return this;
    }

    public void setThemeCallback(ThemeViewModel.OnCallback onCallback) {
        this.mXDialogViewDelegate.setThemeCallback(onCallback);
    }

    public XDialogView setTitle(int i) {
        this.mXDialogViewDelegate.setTitle(i);
        return this;
    }

    public XDialogView setTitle(CharSequence charSequence) {
        this.mXDialogViewDelegate.setTitle(charSequence);
        return this;
    }

    public XDialogView setTitleVisibility(boolean z) {
        this.mXDialogViewDelegate.setTitleVisibility(z);
        return this;
    }

    public void startNegativeButtonCountDown(int i) {
        this.mXDialogViewDelegate.startNegativeButtonCountDown(i);
    }

    public void startPositiveButtonCountDown(int i) {
        this.mXDialogViewDelegate.startPositiveButtonCountDown(i);
    }
}
