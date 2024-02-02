package com.xiaopeng.xui.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.widget.XDialog;
import com.xiaopeng.xui.widget.XNumberPicker;
@Deprecated
/* loaded from: classes.dex */
public class XNumberPickerDialog extends XDialog implements XDialog.XDialogListener, XNumberPicker.OnValueChangeListener {
    private final XNumberPicker mNumberPicker;
    private OnXNumberSetListener mNumberSetListener;

    /* loaded from: classes.dex */
    public interface OnXNumberSetListener {
        void onXNumberSet(XNumberPicker xNumberPicker, CharSequence charSequence);
    }

    public XNumberPickerDialog(Context context, int i, String[] strArr) {
        super(context, resolveDialogTheme(context, i));
        View inflate = LayoutInflater.from(context).inflate(R.layout.x_number_picker, getContentView(), false);
        setCustomView(inflate, false);
        setPositiveButton(" ", this);
        setNegativeButton(" ", this);
        this.mNumberPicker = (XNumberPicker) inflate.findViewById(R.id.x_number_picker);
        if (strArr != null) {
            this.mNumberPicker.setMinValue(1);
            this.mNumberPicker.setMaxValue(strArr.length);
            this.mNumberPicker.setDisplayedValues(strArr);
        }
        this.mNumberPicker.setOnValueChangedListener(this);
    }

    public XNumberPickerDialog(Context context, String[] strArr) {
        this(context, 0, strArr);
    }

    private static int resolveDialogTheme(Context context, int i) {
        return i == 0 ? R.style.XDialogView_Large : i;
    }

    public XNumberPicker getXNumberPicker() {
        return this.mNumberPicker;
    }

    @Override // com.xiaopeng.xui.widget.XDialog.XDialogListener
    public void onClick(XDialog xDialog, int i) {
        OnXNumberSetListener onXNumberSetListener;
        if (i == -2) {
            super.getDialog().cancel();
            this.mNumberPicker.setValue(2);
        } else if (i == -1 && (onXNumberSetListener = this.mNumberSetListener) != null) {
            XNumberPicker xNumberPicker = this.mNumberPicker;
            onXNumberSetListener.onXNumberSet(xNumberPicker, xNumberPicker.getDisplayedValueForCurrentSelection());
        }
    }

    @Override // com.xiaopeng.xui.widget.XNumberPicker.OnValueChangeListener
    public void onValueChange(XNumberPicker xNumberPicker, int i, int i2) {
    }

    @Override // com.xiaopeng.xui.widget.XDialog
    @Deprecated
    public XDialog setNegativeButton(CharSequence charSequence, XDialog.XDialogListener xDialogListener) {
        return super.setNegativeButton(charSequence, this);
    }

    public void setNegativeButtonText(String str) {
        setNegativeButton(str, this);
    }

    @Override // com.xiaopeng.xui.widget.XDialog
    @Deprecated
    public XDialog setPositiveButton(CharSequence charSequence, XDialog.XDialogListener xDialogListener) {
        return super.setPositiveButton(charSequence, this);
    }

    public void setPositiveButtonText(String str) {
        setPositiveButton(str, this);
    }

    public void setXNumberSetListener(OnXNumberSetListener onXNumberSetListener) {
        this.mNumberSetListener = onXNumberSetListener;
    }
}
