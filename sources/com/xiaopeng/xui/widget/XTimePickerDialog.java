package com.xiaopeng.xui.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.widget.XDialog;
import com.xiaopeng.xui.widget.timepicker.XTimePicker;
@Deprecated
/* loaded from: classes.dex */
public class XTimePickerDialog extends XDialog implements XDialog.XDialogListener, XTimePicker.OnTimeChangedListener {
    private final XTimePicker mTimePicker;
    private final OnXTimeSetListener mTimeSetListener;

    /* loaded from: classes.dex */
    public interface OnXTimeSetListener {
        void onTimeSet(XTimePicker xTimePicker, int i, int i2);
    }

    public XTimePickerDialog(Context context, int i, OnXTimeSetListener onXTimeSetListener, int i2, int i3) {
        super(context, resolveDialogTheme(context, i));
        this.mTimeSetListener = onXTimeSetListener;
        View inflate = LayoutInflater.from(context).inflate(R.layout.x_time_picker, getContentView(), false);
        setCustomView(inflate, false);
        setPositiveButton(" ", this);
        setNegativeButton(" ", this);
        this.mTimePicker = (XTimePicker) inflate.findViewById(R.id.x_time_picker);
        this.mTimePicker.setCurrentHour(Integer.valueOf(i2));
        this.mTimePicker.setCurrentMinute(Integer.valueOf(i3));
        this.mTimePicker.setOnTimeChangedListener(this);
    }

    public XTimePickerDialog(Context context, OnXTimeSetListener onXTimeSetListener, int i, int i2) {
        this(context, 0, onXTimeSetListener, i, i2);
    }

    static int resolveDialogTheme(Context context, int i) {
        return i == 0 ? R.style.XDialogView_Large : i;
    }

    public XTimePicker getXTimePicker() {
        return this.mTimePicker;
    }

    @Override // com.xiaopeng.xui.widget.XDialog.XDialogListener
    public void onClick(XDialog xDialog, int i) {
        OnXTimeSetListener onXTimeSetListener;
        if (i == -2) {
            super.getDialog().cancel();
        } else if (i == -1 && (onXTimeSetListener = this.mTimeSetListener) != null) {
            XTimePicker xTimePicker = this.mTimePicker;
            onXTimeSetListener.onTimeSet(xTimePicker, xTimePicker.getCurrentHour().intValue(), this.mTimePicker.getCurrentMinute().intValue());
        }
    }

    @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.OnTimeChangedListener
    public void onTimeChanged(XTimePicker xTimePicker, int i, int i2) {
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

    public void updateTime(int i, int i2) {
        this.mTimePicker.setCurrentHour(Integer.valueOf(i));
        this.mTimePicker.setCurrentMinute(Integer.valueOf(i2));
    }
}
