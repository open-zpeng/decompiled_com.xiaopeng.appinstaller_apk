package com.xiaopeng.xui.widget.timepicker;

import android.content.Context;
import android.icu.util.Calendar;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.math.MathUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewStructure;
import android.view.autofill.AutofillManager;
import android.view.autofill.AutofillValue;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.widget.XFrameLayout;
import java.util.Locale;
/* loaded from: classes.dex */
public class XTimePicker extends XFrameLayout {
    private static final String LOG_TAG = XTimePicker.class.getSimpleName();
    private final b mDelegate;

    /* loaded from: classes.dex */
    static abstract class AbstractTimePickerDelegate implements b {

        /* renamed from: a  reason: collision with root package name */
        protected final XTimePicker f147a;

        /* renamed from: b  reason: collision with root package name */
        protected final Context f148b;
        protected final Locale c;
        protected OnTimeChangedListener d;
        protected OnTimeChangedListener e;
        private long f;

        /* loaded from: classes.dex */
        protected static class SavedState extends View.BaseSavedState {
            private final int mCurrentItemShowing;
            private final int mHour;
            private final int mMinute;

            private SavedState(Parcel parcel) {
                super(parcel);
                this.mHour = parcel.readInt();
                this.mMinute = parcel.readInt();
                this.mCurrentItemShowing = parcel.readInt();
            }

            public SavedState(Parcelable parcelable, int i, int i2) {
                this(parcelable, i, i2, 0);
            }

            public SavedState(Parcelable parcelable, int i, int i2, int i3) {
                super(parcelable);
                this.mHour = i;
                this.mMinute = i2;
                this.mCurrentItemShowing = i3;
            }

            public int getHour() {
                return this.mHour;
            }

            public int getMinute() {
                return this.mMinute;
            }

            @Override // android.view.View.BaseSavedState, android.view.AbsSavedState, android.os.Parcelable
            public void writeToParcel(Parcel parcel, int i) {
                super.writeToParcel(parcel, i);
                parcel.writeInt(this.mHour);
                parcel.writeInt(this.mMinute);
                parcel.writeInt(this.mCurrentItemShowing);
            }
        }

        public AbstractTimePickerDelegate(XTimePicker xTimePicker, Context context) {
            this.f147a = xTimePicker;
            this.f148b = context;
            this.c = context.getResources().getConfiguration().getLocales().get(0);
        }

        @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.b
        public final AutofillValue a() {
            long j = this.f;
            if (j != 0) {
                return AutofillValue.forDate(j);
            }
            Calendar calendar = Calendar.getInstance(this.c);
            calendar.set(11, d());
            calendar.set(12, c());
            return AutofillValue.forDate(calendar.getTimeInMillis());
        }

        @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.b
        public final void a(AutofillValue autofillValue) {
            if (autofillValue != null && autofillValue.isDate()) {
                long dateValue = autofillValue.getDateValue();
                Calendar calendar = Calendar.getInstance(this.c);
                calendar.setTimeInMillis(dateValue);
                a(calendar.get(11), calendar.get(12));
                this.f = dateValue;
                return;
            }
            String str = XTimePicker.LOG_TAG;
            Log.w(str, autofillValue + " could not be autofilled into " + this);
        }

        @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.b
        public void a(OnTimeChangedListener onTimeChangedListener) {
            this.d = onTimeChangedListener;
        }

        @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.b
        public void b(OnTimeChangedListener onTimeChangedListener) {
            this.e = onTimeChangedListener;
        }

        /* JADX INFO: Access modifiers changed from: protected */
        public void e() {
            this.f = 0L;
        }
    }

    /* loaded from: classes.dex */
    public interface OnTimeChangedListener {
        void onTimeChanged(XTimePicker xTimePicker, int i, int i2);
    }

    /* loaded from: classes.dex */
    class a implements OnTimeChangedListener {
        a() {
        }

        @Override // com.xiaopeng.xui.widget.timepicker.XTimePicker.OnTimeChangedListener
        public void onTimeChanged(XTimePicker xTimePicker, int i, int i2) {
            AutofillManager autofillManager = (AutofillManager) XTimePicker.this.getContext().getSystemService(AutofillManager.class);
            if (autofillManager != null) {
                autofillManager.notifyValueChanged(XTimePicker.this);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public interface b {
        Parcelable a(Parcelable parcelable);

        AutofillValue a();

        void a(int i);

        void a(int i, int i2);

        void a(AutofillValue autofillValue);

        void a(OnTimeChangedListener onTimeChangedListener);

        int b();

        void b(int i);

        void b(Parcelable parcelable);

        void b(OnTimeChangedListener onTimeChangedListener);

        int c();

        int d();

        boolean isEnabled();

        void setEnabled(boolean z);
    }

    public XTimePicker(Context context) {
        this(context, null);
    }

    public XTimePicker(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public XTimePicker(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, R.style.XTimePicker);
    }

    public XTimePicker(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        if (getImportantForAutofill() == 0) {
            setImportantForAutofill(1);
        }
        this.mDelegate = new com.xiaopeng.xui.widget.timepicker.a(this, context, attributeSet, i, i2);
        this.mDelegate.b(new a());
    }

    @Override // android.view.View
    public void autofill(AutofillValue autofillValue) {
        if (isEnabled()) {
            this.mDelegate.a(autofillValue);
        }
    }

    @Override // android.view.ViewGroup, android.view.View
    public void dispatchProvideAutofillStructure(ViewStructure viewStructure, int i) {
        viewStructure.setAutofillId(getAutofillId());
        onProvideAutofillStructure(viewStructure, i);
    }

    @Override // android.widget.FrameLayout, android.view.ViewGroup, android.view.View
    public CharSequence getAccessibilityClassName() {
        return XTimePicker.class.getName();
    }

    @Override // android.view.View
    public AutofillValue getAutofillValue() {
        if (isEnabled()) {
            return this.mDelegate.a();
        }
        return null;
    }

    @Override // android.view.View
    public int getBaseline() {
        return this.mDelegate.b();
    }

    public Integer getCurrentHour() {
        return Integer.valueOf(getHour());
    }

    public Integer getCurrentMinute() {
        return Integer.valueOf(getMinute());
    }

    public int getHour() {
        return this.mDelegate.d();
    }

    public int getMinute() {
        return this.mDelegate.c();
    }

    @Override // android.view.View
    public boolean isEnabled() {
        return this.mDelegate.isEnabled();
    }

    @Override // android.view.View
    protected void onRestoreInstanceState(Parcelable parcelable) {
        View.BaseSavedState baseSavedState = (View.BaseSavedState) parcelable;
        super.onRestoreInstanceState(baseSavedState.getSuperState());
        this.mDelegate.b(baseSavedState);
    }

    @Override // android.view.View
    protected Parcelable onSaveInstanceState() {
        return this.mDelegate.a(super.onSaveInstanceState());
    }

    public void setCurrentHour(Integer num) {
        setHour(num.intValue());
    }

    public void setCurrentMinute(Integer num) {
        setMinute(num.intValue());
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        super.setEnabled(z);
        this.mDelegate.setEnabled(z);
    }

    public void setHour(int i) {
        this.mDelegate.b(MathUtils.clamp(i, 0, 23));
    }

    public void setMinute(int i) {
        this.mDelegate.a(MathUtils.clamp(i, 0, 59));
    }

    public void setOnTimeChangedListener(OnTimeChangedListener onTimeChangedListener) {
        this.mDelegate.a(onTimeChangedListener);
    }
}
