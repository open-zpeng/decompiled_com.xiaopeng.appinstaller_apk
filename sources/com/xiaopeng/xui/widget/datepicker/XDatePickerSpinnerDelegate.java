package com.xiaopeng.xui.widget.datepicker;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.icu.util.Calendar;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.LinearLayout;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.widget.XNumberPicker;
import com.xiaopeng.xui.widget.datepicker.XDatePicker;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;
/* loaded from: classes.dex */
public class XDatePickerSpinnerDelegate extends XDatePicker.b {
    private static final String DATE_FORMAT = "MM/dd/yyyy";
    private static final boolean DEFAULT_ENABLED_STATE = true;
    private static final int DEFAULT_END_YEAR = 2100;
    private static final boolean DEFAULT_SPINNERS_SHOWN = true;
    private static final int DEFAULT_START_YEAR = 1900;
    private final DateFormat mDateFormat;
    private final XNumberPicker mDaySpinner;
    private boolean mIsEnabled;
    private Calendar mMaxDate;
    private Calendar mMinDate;
    private final XNumberPicker mMonthSpinner;
    private int mNumberOfMonths;
    private String[] mShortMonths;
    private final LinearLayout mSpinners;
    private Calendar mTempDate;
    private final XNumberPicker mYearSpinner;

    /* loaded from: classes.dex */
    class a implements XNumberPicker.OnValueChangeListener {
        a() {
        }

        @Override // com.xiaopeng.xui.widget.XNumberPicker.OnValueChangeListener
        public void onValueChange(XNumberPicker xNumberPicker, int i, int i2) {
            XDatePickerSpinnerDelegate.this.mTempDate.setTimeInMillis(XDatePickerSpinnerDelegate.this.mCurrentDate.getTimeInMillis());
            if (xNumberPicker == XDatePickerSpinnerDelegate.this.mDaySpinner) {
                int actualMaximum = XDatePickerSpinnerDelegate.this.mTempDate.getActualMaximum(5);
                if (i == actualMaximum && i2 == 1) {
                    XDatePickerSpinnerDelegate.this.mTempDate.add(5, 1);
                } else if (i == 1 && i2 == actualMaximum) {
                    XDatePickerSpinnerDelegate.this.mTempDate.add(5, -1);
                } else {
                    XDatePickerSpinnerDelegate.this.mTempDate.add(5, i2 - i);
                }
            } else if (xNumberPicker == XDatePickerSpinnerDelegate.this.mMonthSpinner) {
                if (i == 11 && i2 == 0) {
                    XDatePickerSpinnerDelegate.this.mTempDate.add(2, 1);
                } else if (i == 0 && i2 == 11) {
                    XDatePickerSpinnerDelegate.this.mTempDate.add(2, -1);
                } else {
                    XDatePickerSpinnerDelegate.this.mTempDate.add(2, i2 - i);
                }
            } else if (xNumberPicker != XDatePickerSpinnerDelegate.this.mYearSpinner) {
                throw new IllegalArgumentException();
            } else {
                XDatePickerSpinnerDelegate.this.mTempDate.set(1, i2);
            }
            XDatePickerSpinnerDelegate xDatePickerSpinnerDelegate = XDatePickerSpinnerDelegate.this;
            xDatePickerSpinnerDelegate.setDate(xDatePickerSpinnerDelegate.mTempDate.get(1), XDatePickerSpinnerDelegate.this.mTempDate.get(2), XDatePickerSpinnerDelegate.this.mTempDate.get(5));
            XDatePickerSpinnerDelegate.this.updateSpinners();
            XDatePickerSpinnerDelegate.this.notifyDateChanged();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public XDatePickerSpinnerDelegate(XDatePicker xDatePicker, Context context, AttributeSet attributeSet, int i, int i2) {
        super(xDatePicker, context);
        this.mDateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        this.mIsEnabled = true;
        this.mXDelegator = xDatePicker;
        this.mContext = context;
        setCurrentLocale(Locale.getDefault());
        TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.XDatePicker, i, i2);
        boolean z = obtainStyledAttributes.getBoolean(R.styleable.XDatePicker_dp_spinnersShown, true);
        int i3 = obtainStyledAttributes.getInt(R.styleable.XDatePicker_dp_startYear, DEFAULT_START_YEAR);
        int i4 = obtainStyledAttributes.getInt(R.styleable.XDatePicker_dp_endYear, DEFAULT_END_YEAR);
        String string = obtainStyledAttributes.getString(R.styleable.XDatePicker_dp_minDate);
        String string2 = obtainStyledAttributes.getString(R.styleable.XDatePicker_dp_maxDate);
        int resourceId = obtainStyledAttributes.getResourceId(R.styleable.XDatePicker_dp_xDatePickerLayout, R.layout.x_date_picker_layout);
        obtainStyledAttributes.recycle();
        ((LayoutInflater) context.getSystemService("layout_inflater")).inflate(resourceId, (ViewGroup) this.mXDelegator, true).setSaveFromParentEnabled(false);
        a aVar = new a();
        this.mSpinners = (LinearLayout) this.mXDelegator.findViewById(R.id.pickers);
        this.mDaySpinner = (XNumberPicker) this.mXDelegator.findViewById(R.id.day);
        this.mDaySpinner.setFormatter(XNumberPicker.getTwoDigitFormatter());
        this.mDaySpinner.setOnLongPressUpdateInterval(100L);
        this.mDaySpinner.setOnValueChangedListener(aVar);
        this.mMonthSpinner = (XNumberPicker) this.mXDelegator.findViewById(R.id.month);
        this.mMonthSpinner.setMinValue(0);
        this.mMonthSpinner.setMaxValue(this.mNumberOfMonths - 1);
        this.mMonthSpinner.setDisplayedValues(this.mShortMonths);
        this.mMonthSpinner.setOnLongPressUpdateInterval(200L);
        this.mMonthSpinner.setOnValueChangedListener(aVar);
        this.mYearSpinner = (XNumberPicker) this.mXDelegator.findViewById(R.id.year);
        this.mYearSpinner.setOnLongPressUpdateInterval(100L);
        this.mYearSpinner.setOnValueChangedListener(aVar);
        setSpinnersShown(z);
        this.mTempDate.clear();
        if (TextUtils.isEmpty(string) || !parseDate(string, this.mTempDate)) {
            this.mTempDate.set(i3, 0, 1);
        }
        setMinDate(this.mTempDate.getTimeInMillis());
        this.mTempDate.clear();
        if (TextUtils.isEmpty(string2) || !parseDate(string2, this.mTempDate)) {
            this.mTempDate.set(i4, 11, 31);
        }
        setMaxDate(this.mTempDate.getTimeInMillis());
        this.mCurrentDate.setTimeInMillis(System.currentTimeMillis());
        init(this.mCurrentDate.get(1), this.mCurrentDate.get(2), this.mCurrentDate.get(5), null);
        reorderSpinners();
        if (this.mXDelegator.getImportantForAccessibility() == 0) {
            this.mXDelegator.setImportantForAccessibility(1);
        }
    }

    private Calendar getCalendarForLocale(Calendar calendar, Locale locale) {
        if (calendar == null) {
            return Calendar.getInstance(locale);
        }
        long timeInMillis = calendar.getTimeInMillis();
        Calendar calendar2 = Calendar.getInstance(locale);
        calendar2.setTimeInMillis(timeInMillis);
        return calendar2;
    }

    private boolean isNewDate(int i, int i2, int i3) {
        return (this.mCurrentDate.get(1) == i && this.mCurrentDate.get(2) == i2 && this.mCurrentDate.get(5) == i3) ? false : true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyDateChanged() {
        this.mXDelegator.sendAccessibilityEvent(4);
        XDatePicker.OnDateChangedListener onDateChangedListener = this.mOnDateChangedListener;
        if (onDateChangedListener != null) {
            onDateChangedListener.onDateChanged(this.mXDelegator, getYear(), getMonth(), getDayOfMonth());
        }
        XDatePicker.OnDateChangedListener onDateChangedListener2 = this.mAutoFillChangeListener;
        if (onDateChangedListener2 != null) {
            onDateChangedListener2.onDateChanged(this.mXDelegator, getYear(), getMonth(), getDayOfMonth());
        }
    }

    private boolean parseDate(String str, Calendar calendar) {
        try {
            calendar.setTime(this.mDateFormat.parse(str));
            return true;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void reorderSpinners() {
        LinearLayout linearLayout;
        XNumberPicker xNumberPicker;
        this.mSpinners.removeAllViews();
        char[] cArr = {'y', 'M', 'd'};
        for (char c : cArr) {
            if (c == 'M') {
                linearLayout = this.mSpinners;
                xNumberPicker = this.mMonthSpinner;
            } else if (c == 'd') {
                linearLayout = this.mSpinners;
                xNumberPicker = this.mDaySpinner;
            } else if (c != 'y') {
                throw new IllegalArgumentException(Arrays.toString(cArr));
            } else {
                linearLayout = this.mSpinners;
                xNumberPicker = this.mYearSpinner;
            }
            linearLayout.addView(xNumberPicker);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setDate(int i, int i2, int i3) {
        Calendar calendar;
        Calendar calendar2;
        this.mCurrentDate.set(i, i2, i3);
        resetAutofilledValue();
        if (this.mCurrentDate.before(this.mMinDate)) {
            calendar = this.mCurrentDate;
            calendar2 = this.mMinDate;
        } else if (!this.mCurrentDate.after(this.mMaxDate)) {
            return;
        } else {
            calendar = this.mCurrentDate;
            calendar2 = this.mMaxDate;
        }
        calendar.setTimeInMillis(calendar2.getTimeInMillis());
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* JADX WARN: Removed duplicated region for block: B:13:0x00c5 A[LOOP:0: B:11:0x00bd->B:13:0x00c5, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:17:0x010a A[LOOP:1: B:15:0x0102->B:17:0x010a, LOOP_END] */
    /* JADX WARN: Removed duplicated region for block: B:21:0x0162 A[LOOP:2: B:19:0x015f->B:21:0x0162, LOOP_END] */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void updateSpinners() {
        /*
            Method dump skipped, instructions count: 432
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.xui.widget.datepicker.XDatePickerSpinnerDelegate.updateSpinners():void");
    }

    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        onPopulateAccessibilityEvent(accessibilityEvent);
        return true;
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.c
    public int getDayOfMonth() {
        return this.mCurrentDate.get(5);
    }

    public int getFirstDayOfWeek() {
        return 0;
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.c
    public Calendar getMaxDate() {
        if (this.mMaxDate != null) {
            return this.mMinDate;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set(DEFAULT_END_YEAR, 11, 30);
        return calendar;
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.c
    public Calendar getMinDate() {
        Calendar calendar = this.mMinDate;
        if (calendar != null) {
            return calendar;
        }
        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(DEFAULT_START_YEAR, 1, 1);
        return calendar2;
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.c
    public int getMonth() {
        return this.mCurrentDate.get(2);
    }

    public boolean getSpinnersShown() {
        return this.mSpinners.isShown();
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.c
    public int getYear() {
        return this.mCurrentDate.get(1);
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.c
    public void init(int i, int i2, int i3, XDatePicker.OnDateChangedListener onDateChangedListener) {
        setDate(i, i2, i3);
        updateSpinners();
        this.mOnDateChangedListener = onDateChangedListener;
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.c
    public boolean isEnabled() {
        return this.mIsEnabled;
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.c
    public void onConfigurationChanged(Configuration configuration) {
        setCurrentLocale(configuration.getLocales().get(0));
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.b
    public /* bridge */ /* synthetic */ void onPopulateAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        super.onPopulateAccessibilityEvent(accessibilityEvent);
    }

    public void onRestoreInstanceState(Parcelable parcelable) {
        if (parcelable instanceof XDatePicker.b.a) {
            XDatePicker.b.a aVar = (XDatePicker.b.a) parcelable;
            setDate(aVar.c(), aVar.b(), aVar.a());
            updateSpinners();
        }
    }

    public Parcelable onSaveInstanceState(Parcelable parcelable) {
        return new XDatePicker.b.a(parcelable, getYear(), getMonth(), getDayOfMonth(), getMinDate().getTimeInMillis(), getMaxDate().getTimeInMillis());
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.b, com.xiaopeng.xui.widget.datepicker.XDatePicker.c
    public /* bridge */ /* synthetic */ void setAutoFillChangeListener(XDatePicker.OnDateChangedListener onDateChangedListener) {
        super.setAutoFillChangeListener(onDateChangedListener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.b
    public void setCurrentLocale(Locale locale) {
        super.setCurrentLocale(locale);
        this.mTempDate = getCalendarForLocale(this.mTempDate, locale);
        this.mMinDate = getCalendarForLocale(this.mMinDate, locale);
        this.mMaxDate = getCalendarForLocale(this.mMaxDate, locale);
        this.mCurrentDate = getCalendarForLocale(this.mCurrentDate, locale);
        this.mNumberOfMonths = this.mTempDate.getActualMaximum(2) + 1;
        this.mShortMonths = new DateFormatSymbols().getShortMonths();
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.c
    public void setEnabled(boolean z) {
        this.mDaySpinner.setEnabled(z);
        this.mMonthSpinner.setEnabled(z);
        this.mYearSpinner.setEnabled(z);
        this.mIsEnabled = z;
    }

    public void setFirstDayOfWeek(int i) {
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.c
    public void setMaxDate(long j) {
        this.mTempDate.setTimeInMillis(j);
        if (this.mTempDate.get(1) == this.mMaxDate.get(1) && this.mTempDate.get(6) == this.mMaxDate.get(6)) {
            return;
        }
        this.mMaxDate.setTimeInMillis(j);
        if (this.mCurrentDate.after(this.mMaxDate)) {
            this.mCurrentDate.setTimeInMillis(this.mMaxDate.getTimeInMillis());
        }
        updateSpinners();
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.c
    public void setMinDate(long j) {
        this.mTempDate.setTimeInMillis(j);
        if (this.mTempDate.get(1) == this.mMinDate.get(1) && this.mTempDate.get(6) == this.mMinDate.get(6)) {
            return;
        }
        this.mMinDate.setTimeInMillis(j);
        if (this.mCurrentDate.before(this.mMinDate)) {
            this.mCurrentDate.setTimeInMillis(this.mMinDate.getTimeInMillis());
        }
        updateSpinners();
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.b, com.xiaopeng.xui.widget.datepicker.XDatePicker.c
    public /* bridge */ /* synthetic */ void setOnDateChangedListener(XDatePicker.OnDateChangedListener onDateChangedListener) {
        super.setOnDateChangedListener(onDateChangedListener);
    }

    public void setSpinnersShown(boolean z) {
        this.mSpinners.setVisibility(z ? 0 : 8);
    }

    @Override // com.xiaopeng.xui.widget.datepicker.XDatePicker.c
    public void updateDate(int i, int i2, int i3) {
        if (isNewDate(i, i2, i3)) {
            setDate(i, i2, i3);
            updateSpinners();
            notifyDateChanged();
        }
    }
}
