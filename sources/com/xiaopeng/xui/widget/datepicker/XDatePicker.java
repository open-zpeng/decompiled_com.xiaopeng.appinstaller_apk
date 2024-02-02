package com.xiaopeng.xui.widget.datepicker;

import android.content.Context;
import android.content.res.Configuration;
import android.icu.util.Calendar;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.autofill.AutofillManager;
import android.view.autofill.AutofillValue;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.widget.XFrameLayout;
import java.util.Locale;
/* loaded from: classes.dex */
public class XDatePicker extends XFrameLayout {
    private static final String LOG_TAG = XDatePicker.class.getSimpleName();
    private final c mDelegate;

    /* loaded from: classes.dex */
    public interface OnDateChangedListener {
        void onDateChanged(XDatePicker xDatePicker, int i, int i2, int i3);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class a implements OnDateChangedListener {
        a() {
        }

        @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.OnDateChangedListener
        public void onDateChanged(XDatePicker xDatePicker, int i, int i2, int i3) {
            AutofillManager autofillManager = (AutofillManager) XDatePicker.this.getContext().getSystemService(AutofillManager.class);
            if (autofillManager != null) {
                autofillManager.notifyValueChanged(XDatePicker.this);
            }
        }
    }

    /* loaded from: classes.dex */
    static abstract class b implements c {
        protected OnDateChangedListener mAutoFillChangeListener;
        private long mAutofilledValue;
        protected Context mContext;
        protected Calendar mCurrentDate;
        protected Locale mCurrentLocale;
        protected OnDateChangedListener mOnDateChangedListener;
        protected XDatePicker mXDelegator;

        /* JADX INFO: Access modifiers changed from: package-private */
        /* loaded from: classes.dex */
        public static class a extends View.BaseSavedState {
            public static final Parcelable.Creator<a> CREATOR = new C0004a();

            /* renamed from: a  reason: collision with root package name */
            private final int f116a;

            /* renamed from: b  reason: collision with root package name */
            private final int f117b;
            private final int c;
            private final long d;
            private final long e;
            private final int f;
            private final int g;
            private final int h;

            /* renamed from: com.xiaopeng.xui.widget.datepicker.XDatePicker$b$a$a  reason: collision with other inner class name */
            /* loaded from: classes.dex */
            static class C0004a implements Parcelable.Creator<a> {
                C0004a() {
                }

                /* JADX WARN: Can't rename method to resolve collision */
                @Override // android.os.Parcelable.Creator
                public a createFromParcel(Parcel parcel) {
                    return new a(parcel, null);
                }

                /* JADX WARN: Can't rename method to resolve collision */
                @Override // android.os.Parcelable.Creator
                public a[] newArray(int i) {
                    return new a[i];
                }
            }

            private a(Parcel parcel) {
                super(parcel);
                this.f116a = parcel.readInt();
                this.f117b = parcel.readInt();
                this.c = parcel.readInt();
                this.d = parcel.readLong();
                this.e = parcel.readLong();
                this.f = parcel.readInt();
                this.g = parcel.readInt();
                this.h = parcel.readInt();
            }

            /* synthetic */ a(Parcel parcel, a aVar) {
                this(parcel);
            }

            public a(Parcelable parcelable, int i, int i2, int i3, long j, long j2) {
                this(parcelable, i, i2, i3, j, j2, 0, 0, 0);
            }

            public a(Parcelable parcelable, int i, int i2, int i3, long j, long j2, int i4, int i5, int i6) {
                super(parcelable);
                this.f116a = i;
                this.f117b = i2;
                this.c = i3;
                this.d = j;
                this.e = j2;
                this.f = i4;
                this.g = i5;
                this.h = i6;
            }

            public int a() {
                return this.c;
            }

            public int b() {
                return this.f117b;
            }

            public int c() {
                return this.f116a;
            }

            @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
            public void writeToParcel(Parcel parcel, int i) {
                super.writeToParcel(parcel, i);
                parcel.writeInt(this.f116a);
                parcel.writeInt(this.f117b);
                parcel.writeInt(this.c);
                parcel.writeLong(this.d);
                parcel.writeLong(this.e);
                parcel.writeInt(this.f);
                parcel.writeInt(this.g);
                parcel.writeInt(this.h);
            }
        }

        public b(XDatePicker xDatePicker, Context context) {
            this.mXDelegator = xDatePicker;
            this.mContext = context;
            setCurrentLocale(Locale.getDefault());
        }

        public final void autofill(AutofillValue autofillValue) {
            if (autofillValue != null && autofillValue.isDate()) {
                long dateValue = autofillValue.getDateValue();
                Calendar calendar = Calendar.getInstance(this.mCurrentLocale);
                calendar.setTimeInMillis(dateValue);
                updateDate(calendar.get(1), calendar.get(2), calendar.get(5));
                this.mAutofilledValue = dateValue;
                return;
            }
            String str = XDatePicker.LOG_TAG;
            Log.w(str, autofillValue + " could not be autofilled into " + this);
        }

        public final AutofillValue getAutofillValue() {
            long j = this.mAutofilledValue;
            if (j == 0) {
                j = this.mCurrentDate.getTimeInMillis();
            }
            return AutofillValue.forDate(j);
        }

        protected String getFormattedCurrentDate() {
            return DateUtils.formatDateTime(this.mContext, this.mCurrentDate.getTimeInMillis(), 22);
        }

        protected void onLocaleChanged(Locale locale) {
        }

        public void onPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
            accessibilityEvent.getText().add(getFormattedCurrentDate());
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void resetAutofilledValue() {
            this.mAutofilledValue = 0L;
        }

        @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.c
        public void setAutoFillChangeListener(OnDateChangedListener onDateChangedListener) {
            this.mAutoFillChangeListener = onDateChangedListener;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void setCurrentLocale(Locale locale) {
            if (locale.equals(this.mCurrentLocale)) {
                return;
            }
            this.mCurrentLocale = locale;
            onLocaleChanged(locale);
        }

        @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.c
        public void setOnDateChangedListener(OnDateChangedListener onDateChangedListener) {
            this.mOnDateChangedListener = onDateChangedListener;
        }
    }

    /* loaded from: classes.dex */
    interface c {
        int getDayOfMonth();

        Calendar getMaxDate();

        Calendar getMinDate();

        int getMonth();

        int getYear();

        void init(int i, int i2, int i3, OnDateChangedListener onDateChangedListener);

        boolean isEnabled();

        void onConfigurationChanged(Configuration configuration);

        void setAutoFillChangeListener(OnDateChangedListener onDateChangedListener);

        void setEnabled(boolean z);

        void setMaxDate(long j);

        void setMinDate(long j);

        void setOnDateChangedListener(OnDateChangedListener onDateChangedListener);

        void updateDate(int i, int i2, int i3);
    }

    public XDatePicker(Context context) {
        this(context, null);
    }

    public XDatePicker(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public XDatePicker(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, R.style.XDatePicker);
    }

    public XDatePicker(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        if (getImportantForAutofill() == 0) {
            setImportantForAutofill(1);
        }
        this.mDelegate = createSpinnerUIDelegate(context, attributeSet, i, i2);
        this.mDelegate.setAutoFillChangeListener(new a());
    }

    private c createSpinnerUIDelegate(Context context, AttributeSet attributeSet, int i, int i2) {
        return new XDatePickerSpinnerDelegate(this, context, attributeSet, i, i2);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return XDatePicker.class.getName();
    }

    public int getDayOfMonth() {
        return this.mDelegate.getDayOfMonth();
    }

    public long getMaxDate() {
        return this.mDelegate.getMaxDate().getTimeInMillis();
    }

    public long getMinDate() {
        return this.mDelegate.getMinDate().getTimeInMillis();
    }

    public int getMonth() {
        return this.mDelegate.getMonth();
    }

    public int getYear() {
        return this.mDelegate.getYear();
    }

    public void init(int i, int i2, int i3, OnDateChangedListener onDateChangedListener) {
        this.mDelegate.init(i, i2, i3, onDateChangedListener);
    }

    @Override // android.view.View
    public boolean isEnabled() {
        return this.mDelegate.isEnabled();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.XFrameLayout, android.view.View
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.mDelegate.onConfigurationChanged(configuration);
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        if (this.mDelegate.isEnabled() == z) {
            return;
        }
        super.setEnabled(z);
        this.mDelegate.setEnabled(z);
    }

    public void setMaxDate(long j) {
        this.mDelegate.setMaxDate(j);
    }

    public void setMinDate(long j) {
        this.mDelegate.setMinDate(j);
    }

    public void setOnDateChangedListener(OnDateChangedListener onDateChangedListener) {
        this.mDelegate.setOnDateChangedListener(onDateChangedListener);
    }

    public void updateDate(int i, int i2, int i3) {
        this.mDelegate.updateDate(i, i2, i3);
    }
}
