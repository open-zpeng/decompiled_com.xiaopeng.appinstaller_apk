package com.xiaopeng.xui.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.widget.XDialog;
import com.xiaopeng.xui.widget.datepicker.XDatePicker;
import java.util.Calendar;
@Deprecated
/* loaded from: classes.dex */
public class XDatePickerDialog extends XDialog implements XDialog.XDialogListener, XDatePicker.OnDateChangedListener {
    private final XDatePicker mDatePicker;
    private OnXDateSetListener mDateSetListener;

    /* loaded from: classes.dex */
    public interface OnXDateSetListener {
        void onDateSet(XDatePicker xDatePicker, int i, int i2, int i3);
    }

    public XDatePickerDialog(Context context) {
        this(context, R.style.XDialogView_Large, null, Calendar.getInstance(), -1, -1, -1);
    }

    public XDatePickerDialog(Context context, int i) {
        this(context, i, null, Calendar.getInstance(), -1, -1, -1);
    }

    public XDatePickerDialog(Context context, int i, OnXDateSetListener onXDateSetListener, int i2, int i3, int i4) {
        this(context, i, onXDateSetListener, null, i2, i3, i4);
    }

    private XDatePickerDialog(Context context, int i, OnXDateSetListener onXDateSetListener, Calendar calendar, int i2, int i3, int i4) {
        super(context, resolveDialogTheme(context, i));
        View inflate = LayoutInflater.from(context).inflate(R.layout.x_date_picker, getContentView(), false);
        setCustomView(inflate, false);
        setPositiveButton(" ", this);
        setNegativeButton(" ", this);
        if (calendar != null) {
            i2 = calendar.get(1);
            i3 = calendar.get(2);
            i4 = calendar.get(5);
        }
        this.mDatePicker = (XDatePicker) inflate.findViewById(R.id.x_date_picker);
        this.mDatePicker.init(i2, i3, i4, this);
        this.mDateSetListener = onXDateSetListener;
    }

    public XDatePickerDialog(Context context, OnXDateSetListener onXDateSetListener, int i, int i2, int i3) {
        this(context, R.style.XDialogView_Large, onXDateSetListener, null, i, i2, i3);
    }

    private static int resolveDialogTheme(Context context, int i) {
        return i == 0 ? R.style.XDialogView_Large : i;
    }

    public XDatePicker getXDatePicker() {
        return this.mDatePicker;
    }

    @Override // com.xiaopeng.xui.widget.XDialog.XDialogListener
    public void onClick(XDialog xDialog, int i) {
        if (i == -2) {
            super.getDialog().cancel();
        } else if (i == -1 && this.mDateSetListener != null) {
            this.mDatePicker.clearFocus();
            OnXDateSetListener onXDateSetListener = this.mDateSetListener;
            XDatePicker xDatePicker = this.mDatePicker;
            onXDateSetListener.onDateSet(xDatePicker, xDatePicker.getYear(), this.mDatePicker.getMonth(), this.mDatePicker.getDayOfMonth());
        }
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.OnDateChangedListener
    public void onDateChanged(XDatePicker xDatePicker, int i, int i2, int i3) {
        this.mDatePicker.init(i, i2, i3, this);
    }

    @Override // com.xiaopeng.xui.widget.XDialog
    @Deprecated
    public XDialog setNegativeButton(CharSequence charSequence, XDialog.XDialogListener xDialogListener) {
        return super.setNegativeButton(charSequence, this);
    }

    public void setNegativeButtonText(String str) {
        setNegativeButton(str, this);
    }

    public void setOnXDateSetListener(OnXDateSetListener onXDateSetListener) {
        this.mDateSetListener = onXDateSetListener;
    }

    @Override // com.xiaopeng.xui.widget.XDialog
    @Deprecated
    public XDialog setPositiveButton(CharSequence charSequence, XDialog.XDialogListener xDialogListener) {
        return super.setPositiveButton(charSequence, this);
    }

    public void setPositiveButtonText(String str) {
        setPositiveButton(str, this);
    }

    public void updateDate(int i, int i2, int i3) {
        this.mDatePicker.updateDate(i, i2, i3);
    }
}
