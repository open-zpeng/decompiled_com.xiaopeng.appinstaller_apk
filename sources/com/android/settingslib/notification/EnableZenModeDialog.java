package com.android.settingslib.notification;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.content.Context;
import android.net.Uri;
import android.service.notification.Condition;
import android.service.notification.ZenModeConfig;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Slog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.logging.MetricsLogger;
import com.android.settingslib.R;
import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Objects;
import xiaopeng.widget.BuildConfig;
/* loaded from: classes.dex */
public class EnableZenModeDialog {
    @VisibleForTesting
    protected static final int COUNTDOWN_ALARM_CONDITION_INDEX = 2;
    @VisibleForTesting
    protected static final int COUNTDOWN_CONDITION_INDEX = 1;
    @VisibleForTesting
    protected static final int FOREVER_CONDITION_INDEX = 0;
    private AlarmManager mAlarmManager;
    private boolean mAttached;
    private int mBucketIndex;
    @VisibleForTesting
    protected Context mContext;
    @VisibleForTesting
    protected Uri mForeverId;
    @VisibleForTesting
    protected LayoutInflater mLayoutInflater;
    @VisibleForTesting
    protected NotificationManager mNotificationManager;
    private int mUserId;
    @VisibleForTesting
    protected TextView mZenAlarmWarning;
    private RadioGroup mZenRadioGroup;
    @VisibleForTesting
    protected LinearLayout mZenRadioGroupContent;
    private static final boolean DEBUG = Log.isLoggable("EnableZenModeDialog", 3);
    private static final int[] MINUTE_BUCKETS = ZenModeConfig.MINUTE_BUCKETS;
    private static final int MIN_BUCKET_MINUTES = MINUTE_BUCKETS[0];
    private static final int MAX_BUCKET_MINUTES = MINUTE_BUCKETS[MINUTE_BUCKETS.length - 1];
    private static final int DEFAULT_BUCKET_INDEX = Arrays.binarySearch(MINUTE_BUCKETS, 60);

    @VisibleForTesting
    protected void bind(Condition condition, View row, int rowId) {
        if (condition == null) {
            throw new IllegalArgumentException("condition must not be null");
        }
        boolean enabled = condition.state == 1;
        final ConditionTag tag = row.getTag() != null ? (ConditionTag) row.getTag() : new ConditionTag();
        row.setTag(tag);
        boolean first = tag.rb == null;
        if (tag.rb == null) {
            tag.rb = (RadioButton) this.mZenRadioGroup.getChildAt(rowId);
        }
        tag.condition = condition;
        final Uri conditionId = getConditionId(tag.condition);
        if (DEBUG) {
            Log.d("EnableZenModeDialog", "bind i=" + this.mZenRadioGroupContent.indexOfChild(row) + " first=" + first + " condition=" + conditionId);
        }
        tag.rb.setEnabled(enabled);
        tag.rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settingslib.notification.EnableZenModeDialog.2
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tag.rb.setChecked(true);
                    if (EnableZenModeDialog.DEBUG) {
                        Log.d("EnableZenModeDialog", "onCheckedChanged " + conditionId);
                    }
                    MetricsLogger.action(EnableZenModeDialog.this.mContext, 164);
                    EnableZenModeDialog.this.updateAlarmWarningText(tag.condition);
                }
            }
        });
        updateUi(tag, row, condition, enabled, rowId, conditionId);
        row.setVisibility(0);
    }

    @VisibleForTesting
    protected ConditionTag getConditionTagAt(int index) {
        return (ConditionTag) this.mZenRadioGroupContent.getChildAt(index).getTag();
    }

    @VisibleForTesting
    protected void bindConditions(Condition c) {
        bind(forever(), this.mZenRadioGroupContent.getChildAt(0), 0);
        if (c == null) {
            bindGenericCountdown();
            bindNextAlarm(getTimeUntilNextAlarmCondition());
        } else if (isForever(c)) {
            getConditionTagAt(0).rb.setChecked(true);
            bindGenericCountdown();
            bindNextAlarm(getTimeUntilNextAlarmCondition());
        } else if (isAlarm(c)) {
            bindGenericCountdown();
            bindNextAlarm(c);
            getConditionTagAt(2).rb.setChecked(true);
        } else if (isCountdown(c)) {
            bindNextAlarm(getTimeUntilNextAlarmCondition());
            bind(c, this.mZenRadioGroupContent.getChildAt(1), 1);
            getConditionTagAt(1).rb.setChecked(true);
        } else {
            Slog.d("EnableZenModeDialog", "Invalid manual condition: " + c);
        }
    }

    public static Uri getConditionId(Condition condition) {
        if (condition != null) {
            return condition.id;
        }
        return null;
    }

    public Condition forever() {
        Uri foreverId = Condition.newId(this.mContext).appendPath("forever").build();
        return new Condition(foreverId, foreverSummary(this.mContext), BuildConfig.FLAVOR, BuildConfig.FLAVOR, 0, 1, 0);
    }

    public long getNextAlarm() {
        AlarmManager.AlarmClockInfo info = this.mAlarmManager.getNextAlarmClock(this.mUserId);
        if (info != null) {
            return info.getTriggerTime();
        }
        return 0L;
    }

    @VisibleForTesting
    protected boolean isAlarm(Condition c) {
        return c != null && ZenModeConfig.isValidCountdownToAlarmConditionId(c.id);
    }

    @VisibleForTesting
    protected boolean isCountdown(Condition c) {
        return c != null && ZenModeConfig.isValidCountdownConditionId(c.id);
    }

    private boolean isForever(Condition c) {
        return c != null && this.mForeverId.equals(c.id);
    }

    private String foreverSummary(Context context) {
        return context.getString(17041161);
    }

    private static void setToMidnight(Calendar calendar) {
        calendar.set(11, 0);
        calendar.set(12, 0);
        calendar.set(13, 0);
        calendar.set(14, 0);
    }

    @VisibleForTesting
    protected Condition getTimeUntilNextAlarmCondition() {
        GregorianCalendar weekRange = new GregorianCalendar();
        setToMidnight(weekRange);
        weekRange.add(5, 6);
        long nextAlarmMs = getNextAlarm();
        if (nextAlarmMs > 0) {
            GregorianCalendar nextAlarm = new GregorianCalendar();
            nextAlarm.setTimeInMillis(nextAlarmMs);
            setToMidnight(nextAlarm);
            if (weekRange.compareTo((Calendar) nextAlarm) >= 0) {
                return ZenModeConfig.toNextAlarmCondition(this.mContext, nextAlarmMs, ActivityManager.getCurrentUser());
            }
            return null;
        }
        return null;
    }

    @VisibleForTesting
    protected void bindGenericCountdown() {
        this.mBucketIndex = DEFAULT_BUCKET_INDEX;
        Condition countdown = ZenModeConfig.toTimeCondition(this.mContext, MINUTE_BUCKETS[this.mBucketIndex], ActivityManager.getCurrentUser());
        if (!this.mAttached || getConditionTagAt(1).condition == null) {
            bind(countdown, this.mZenRadioGroupContent.getChildAt(1), 1);
        }
    }

    private void updateUi(final ConditionTag tag, final View row, Condition condition, boolean enabled, final int rowId, Uri conditionId) {
        if (tag.lines == null) {
            tag.lines = row.findViewById(16908290);
            tag.lines.setAccessibilityLiveRegion(1);
        }
        if (tag.line1 == null) {
            tag.line1 = (TextView) row.findViewById(16908308);
        }
        if (tag.line2 == null) {
            tag.line2 = (TextView) row.findViewById(16908309);
        }
        String line1 = !TextUtils.isEmpty(condition.line1) ? condition.line1 : condition.summary;
        String line2 = condition.line2;
        tag.line1.setText(line1);
        if (TextUtils.isEmpty(line2)) {
            tag.line2.setVisibility(8);
        } else {
            tag.line2.setVisibility(0);
            tag.line2.setText(line2);
        }
        tag.lines.setEnabled(enabled);
        tag.lines.setAlpha(enabled ? 1.0f : 0.4f);
        tag.lines.setOnClickListener(new View.OnClickListener() { // from class: com.android.settingslib.notification.EnableZenModeDialog.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                tag.rb.setChecked(true);
            }
        });
        ImageView button1 = (ImageView) row.findViewById(16908313);
        button1.setOnClickListener(new View.OnClickListener() { // from class: com.android.settingslib.notification.EnableZenModeDialog.4
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                EnableZenModeDialog.this.onClickTimeButton(row, tag, false, rowId);
            }
        });
        ImageView button2 = (ImageView) row.findViewById(16908314);
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settingslib.notification.EnableZenModeDialog.5
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                EnableZenModeDialog.this.onClickTimeButton(row, tag, true, rowId);
            }
        });
        long time = ZenModeConfig.tryParseCountdownConditionId(conditionId);
        if (rowId == 1 && time > 0) {
            button1.setVisibility(0);
            button2.setVisibility(0);
            if (this.mBucketIndex > -1) {
                button1.setEnabled(this.mBucketIndex > 0);
                button2.setEnabled(this.mBucketIndex < MINUTE_BUCKETS.length - 1);
            } else {
                long span = time - System.currentTimeMillis();
                button1.setEnabled(span > ((long) (MIN_BUCKET_MINUTES * 60000)));
                Condition maxCondition = ZenModeConfig.toTimeCondition(this.mContext, MAX_BUCKET_MINUTES, ActivityManager.getCurrentUser());
                button2.setEnabled(!Objects.equals(condition.summary, maxCondition.summary));
            }
            button1.setAlpha(button1.isEnabled() ? 1.0f : 0.5f);
            button2.setAlpha(button2.isEnabled() ? 1.0f : 0.5f);
            return;
        }
        button1.setVisibility(8);
        button2.setVisibility(8);
    }

    @VisibleForTesting
    protected void bindNextAlarm(Condition c) {
        View alarmContent = this.mZenRadioGroupContent.getChildAt(2);
        ConditionTag tag = (ConditionTag) alarmContent.getTag();
        if (c != null && (!this.mAttached || tag == null || tag.condition == null)) {
            bind(c, alarmContent, 2);
        }
        ConditionTag tag2 = (ConditionTag) alarmContent.getTag();
        int i = 0;
        boolean showAlarm = (tag2 == null || tag2.condition == null) ? false : true;
        this.mZenRadioGroup.getChildAt(2).setVisibility(showAlarm ? 0 : 8);
        if (!showAlarm) {
            i = 8;
        }
        alarmContent.setVisibility(i);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onClickTimeButton(View row, ConditionTag tag, boolean up, int rowId) {
        MetricsLogger.action(this.mContext, 163, up);
        Condition newCondition = null;
        int N = MINUTE_BUCKETS.length;
        if (this.mBucketIndex == -1) {
            Uri conditionId = getConditionId(tag.condition);
            long time = ZenModeConfig.tryParseCountdownConditionId(conditionId);
            long now = System.currentTimeMillis();
            for (int i = 0; i < N; i++) {
                int j = up ? i : (N - 1) - i;
                int bucketMinutes = MINUTE_BUCKETS[j];
                long bucketTime = now + (60000 * bucketMinutes);
                if ((up && bucketTime > time) || (!up && bucketTime < time)) {
                    this.mBucketIndex = j;
                    newCondition = ZenModeConfig.toTimeCondition(this.mContext, bucketTime, bucketMinutes, ActivityManager.getCurrentUser(), false);
                    break;
                }
            }
            if (newCondition == null) {
                this.mBucketIndex = DEFAULT_BUCKET_INDEX;
                newCondition = ZenModeConfig.toTimeCondition(this.mContext, MINUTE_BUCKETS[this.mBucketIndex], ActivityManager.getCurrentUser());
            }
        } else {
            this.mBucketIndex = Math.max(0, Math.min(N - 1, this.mBucketIndex + (up ? 1 : -1)));
            newCondition = ZenModeConfig.toTimeCondition(this.mContext, MINUTE_BUCKETS[this.mBucketIndex], ActivityManager.getCurrentUser());
        }
        bind(newCondition, row, rowId);
        updateAlarmWarningText(tag.condition);
        tag.rb.setChecked(true);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateAlarmWarningText(Condition condition) {
        String warningText = computeAlarmWarningText(condition);
        this.mZenAlarmWarning.setText(warningText);
        this.mZenAlarmWarning.setVisibility(warningText == null ? 8 : 0);
    }

    @VisibleForTesting
    protected String computeAlarmWarningText(Condition condition) {
        boolean allowAlarms = (this.mNotificationManager.getNotificationPolicy().priorityCategories & 32) != 0;
        if (allowAlarms) {
            return null;
        }
        long now = System.currentTimeMillis();
        long nextAlarm = getNextAlarm();
        if (nextAlarm < now) {
            return null;
        }
        int warningRes = 0;
        if (condition == null || isForever(condition)) {
            warningRes = R.string.zen_alarm_warning_indef;
        } else {
            long time = ZenModeConfig.tryParseCountdownConditionId(condition.id);
            if (time > now && nextAlarm < time) {
                warningRes = R.string.zen_alarm_warning;
            }
        }
        if (warningRes == 0) {
            return null;
        }
        return this.mContext.getResources().getString(warningRes, getTime(nextAlarm, now));
    }

    @VisibleForTesting
    protected String getTime(long nextAlarm, long now) {
        boolean soon = nextAlarm - now < 86400000;
        boolean is24 = DateFormat.is24HourFormat(this.mContext, ActivityManager.getCurrentUser());
        String skeleton = soon ? is24 ? "Hm" : "hma" : is24 ? "EEEHm" : "EEEhma";
        String pattern = DateFormat.getBestDateTimePattern(Locale.getDefault(), skeleton);
        CharSequence formattedTime = DateFormat.format(pattern, nextAlarm);
        int templateRes = soon ? R.string.alarm_template : R.string.alarm_template_far;
        return this.mContext.getResources().getString(templateRes, formattedTime);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @VisibleForTesting
    /* loaded from: classes.dex */
    public static class ConditionTag {
        public Condition condition;
        public TextView line1;
        public TextView line2;
        public View lines;
        public RadioButton rb;

        protected ConditionTag() {
        }
    }
}
