package com.xiaopeng.xui.widget.timepicker;

import android.content.Context;
import android.content.res.TypedArray;
import android.icu.util.Calendar;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.widget.XNumberPicker;
import com.xiaopeng.xui.widget.timepicker.XTimePicker;
import java.util.Locale;
import xiaopeng.widget.BuildConfig;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class a extends XTimePicker.AbstractTimePickerDelegate {
    private final XNumberPicker g;
    private final XNumberPicker h;
    private final Calendar i;
    private boolean j;

    /* renamed from: com.xiaopeng.xui.widget.timepicker.a$a  reason: collision with other inner class name */
    /* loaded from: classes.dex */
    class C0008a implements XNumberPicker.OnValueChangeListener {
        C0008a() {
        }

        @Override // com.xiaopeng.xui.widget.XNumberPicker.OnValueChangeListener
        public void onValueChange(XNumberPicker xNumberPicker, int i, int i2) {
            a.this.f();
        }
    }

    /* loaded from: classes.dex */
    class b implements XNumberPicker.OnValueChangeListener {
        b() {
        }

        @Override // com.xiaopeng.xui.widget.XNumberPicker.OnValueChangeListener
        public void onValueChange(XNumberPicker xNumberPicker, int i, int i2) {
            int value;
            int minValue = a.this.h.getMinValue();
            int maxValue = a.this.h.getMaxValue();
            if (i != maxValue || i2 != minValue) {
                if (i == minValue && i2 == maxValue) {
                    value = a.this.g.getValue() - 1;
                }
                a.this.f();
            }
            value = a.this.g.getValue() + 1;
            a.this.g.setValue(value);
            a.this.f();
        }
    }

    public a(XTimePicker xTimePicker, Context context, AttributeSet attributeSet, int i, int i2) {
        super(xTimePicker, context);
        this.j = true;
        TypedArray obtainStyledAttributes = this.f148b.obtainStyledAttributes(attributeSet, R.styleable.XTimePicker, i, i2);
        int resourceId = obtainStyledAttributes.getResourceId(R.styleable.XTimePicker_tp_xTimePickerLayout, R.layout.x_time_picker_layout);
        obtainStyledAttributes.recycle();
        LayoutInflater.from(this.f148b).inflate(resourceId, (ViewGroup) this.f147a, true).setSaveFromParentEnabled(false);
        this.g = (XNumberPicker) xTimePicker.findViewById(R.id.hour);
        this.g.setMinValue(0);
        this.g.setMaxValue(23);
        this.g.setOnLongPressUpdateInterval(100L);
        String[] strArr = new String[24];
        for (int i3 = 0; i3 < strArr.length; i3++) {
            strArr[i3] = BuildConfig.FLAVOR + String.format(Locale.getDefault(), "%02d", Integer.valueOf(i3)) + "时";
        }
        this.g.setDisplayedValues(strArr);
        this.g.setOnValueChangedListener(new C0008a());
        this.h = (XNumberPicker) this.f147a.findViewById(R.id.minute);
        this.h.setMinValue(0);
        this.h.setMaxValue(59);
        this.h.setOnLongPressUpdateInterval(100L);
        this.h.setFormatter(XNumberPicker.getTwoDigitFormatter());
        String[] strArr2 = new String[60];
        for (int i4 = 0; i4 < strArr2.length; i4++) {
            strArr2[i4] = BuildConfig.FLAVOR + String.format(Locale.getDefault(), "%02d", Integer.valueOf(i4)) + "分";
        }
        this.h.setDisplayedValues(strArr2);
        this.h.setOnValueChangedListener(new b());
        this.i = Calendar.getInstance(this.c);
        b(this.i.get(11));
        a(this.i.get(12));
        if (!isEnabled()) {
            setEnabled(false);
        }
        if (this.f147a.getImportantForAccessibility() == 0) {
            this.f147a.setImportantForAccessibility(1);
        }
    }

    private void a(int i, boolean z) {
        if (i == d()) {
            return;
        }
        e();
        this.g.setValue(i);
        if (z) {
            f();
        }
    }

    private void b(int i, boolean z) {
        if (i == c()) {
            return;
        }
        e();
        this.h.setValue(i);
        if (z) {
            f();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void f() {
        this.f147a.sendAccessibilityEvent(4);
        XTimePicker.OnTimeChangedListener onTimeChangedListener = this.d;
        if (onTimeChangedListener != null) {
            onTimeChangedListener.onTimeChanged(this.f147a, d(), c());
        }
        XTimePicker.OnTimeChangedListener onTimeChangedListener2 = this.e;
        if (onTimeChangedListener2 != null) {
            onTimeChangedListener2.onTimeChanged(this.f147a, d(), c());
        }
    }

    @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.b
    public Parcelable a(Parcelable parcelable) {
        return new XTimePicker.AbstractTimePickerDelegate.SavedState(parcelable, d(), c());
    }

    @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.b
    public void a(int i) {
        b(i, true);
    }

    @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.b
    public void a(int i, int i2) {
        a(i, false);
        b(i2, false);
        f();
    }

    @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.b
    public int b() {
        return this.g.getBaseline();
    }

    @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.b
    public void b(int i) {
        a(i, true);
    }

    @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.b
    public void b(Parcelable parcelable) {
        if (parcelable instanceof XTimePicker.AbstractTimePickerDelegate.SavedState) {
            XTimePicker.AbstractTimePickerDelegate.SavedState savedState = (XTimePicker.AbstractTimePickerDelegate.SavedState) parcelable;
            b(savedState.getHour());
            a(savedState.getMinute());
        }
    }

    @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.b
    public int c() {
        return this.h.getValue();
    }

    @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.b
    public int d() {
        return this.g.getValue();
    }

    @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.b
    public boolean isEnabled() {
        return this.j;
    }

    @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.b
    public void setEnabled(boolean z) {
        this.h.setEnabled(z);
        this.g.setEnabled(z);
        this.j = z;
    }
}
