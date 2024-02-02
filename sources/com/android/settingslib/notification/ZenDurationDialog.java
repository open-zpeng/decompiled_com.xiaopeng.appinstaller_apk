package com.android.settingslib.notification;

import android.app.ActivityManager;
import android.content.Context;
import android.provider.Settings;
import android.service.notification.Condition;
import android.service.notification.ZenModeConfig;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;
import com.android.internal.logging.MetricsLogger;
import com.android.internal.policy.PhoneWindow;
import com.android.settingslib.R;
import java.util.Arrays;
import xiaopeng.widget.BuildConfig;
/* loaded from: classes.dex */
public class ZenDurationDialog {
    protected static final int ALWAYS_ASK_CONDITION_INDEX = 2;
    protected static final int COUNTDOWN_CONDITION_INDEX = 1;
    protected static final int FOREVER_CONDITION_INDEX = 0;
    private int MAX_MANUAL_DND_OPTIONS;
    protected int mBucketIndex;
    protected Context mContext;
    protected LayoutInflater mLayoutInflater;
    private RadioGroup mZenRadioGroup;
    protected LinearLayout mZenRadioGroupContent;
    private static final int[] MINUTE_BUCKETS = ZenModeConfig.MINUTE_BUCKETS;
    protected static final int MIN_BUCKET_MINUTES = MINUTE_BUCKETS[0];
    protected static final int MAX_BUCKET_MINUTES = MINUTE_BUCKETS[MINUTE_BUCKETS.length - 1];
    private static final int DEFAULT_BUCKET_INDEX = Arrays.binarySearch(MINUTE_BUCKETS, 60);

    protected void updateZenDuration(int currZenDuration) {
        int checkedRadioButtonId = this.mZenRadioGroup.getCheckedRadioButtonId();
        int newZenDuration = Settings.Global.getInt(this.mContext.getContentResolver(), "zen_duration", 0);
        switch (checkedRadioButtonId) {
            case 0:
                newZenDuration = 0;
                MetricsLogger.action(this.mContext, 1343);
                break;
            case 1:
                ConditionTag tag = getConditionTagAt(checkedRadioButtonId);
                newZenDuration = tag.countdownZenDuration;
                MetricsLogger.action(this.mContext, 1342, newZenDuration);
                break;
            case 2:
                newZenDuration = -1;
                MetricsLogger.action(this.mContext, 1344);
                break;
        }
        if (currZenDuration != newZenDuration) {
            Settings.Global.putInt(this.mContext.getContentResolver(), "zen_duration", newZenDuration);
        }
    }

    protected View getContentView() {
        if (this.mLayoutInflater == null) {
            this.mLayoutInflater = new PhoneWindow(this.mContext).getLayoutInflater();
        }
        View contentView = this.mLayoutInflater.inflate(R.layout.zen_mode_duration_dialog, (ViewGroup) null);
        ScrollView container = (ScrollView) contentView.findViewById(R.id.zen_duration_container);
        this.mZenRadioGroup = (RadioGroup) container.findViewById(R.id.zen_radio_buttons);
        this.mZenRadioGroupContent = (LinearLayout) container.findViewById(R.id.zen_radio_buttons_content);
        for (int i = 0; i < this.MAX_MANUAL_DND_OPTIONS; i++) {
            View radioButton = this.mLayoutInflater.inflate(R.layout.zen_mode_radio_button, (ViewGroup) this.mZenRadioGroup, false);
            this.mZenRadioGroup.addView(radioButton);
            radioButton.setId(i);
            View radioButtonContent = this.mLayoutInflater.inflate(R.layout.zen_mode_condition, (ViewGroup) this.mZenRadioGroupContent, false);
            radioButtonContent.setId(this.MAX_MANUAL_DND_OPTIONS + i);
            this.mZenRadioGroupContent.addView(radioButtonContent);
        }
        return contentView;
    }

    protected void setupRadioButtons(int zenDuration) {
        int checkedIndex = 2;
        if (zenDuration == 0) {
            checkedIndex = 0;
        } else if (zenDuration > 0) {
            checkedIndex = 1;
        }
        bindTag(zenDuration, this.mZenRadioGroupContent.getChildAt(0), 0);
        bindTag(zenDuration, this.mZenRadioGroupContent.getChildAt(1), 1);
        bindTag(zenDuration, this.mZenRadioGroupContent.getChildAt(2), 2);
        getConditionTagAt(checkedIndex).rb.setChecked(true);
    }

    private void bindTag(int currZenDuration, View row, int rowIndex) {
        final ConditionTag tag = row.getTag() != null ? (ConditionTag) row.getTag() : new ConditionTag();
        row.setTag(tag);
        if (tag.rb == null) {
            tag.rb = (RadioButton) this.mZenRadioGroup.getChildAt(rowIndex);
        }
        if (currZenDuration <= 0) {
            tag.countdownZenDuration = MINUTE_BUCKETS[DEFAULT_BUCKET_INDEX];
        } else {
            tag.countdownZenDuration = currZenDuration;
        }
        tag.rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() { // from class: com.android.settingslib.notification.ZenDurationDialog.2
            @Override // android.widget.CompoundButton.OnCheckedChangeListener
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tag.rb.setChecked(true);
                }
            }
        });
        updateUi(tag, row, rowIndex);
    }

    protected ConditionTag getConditionTagAt(int index) {
        return (ConditionTag) this.mZenRadioGroupContent.getChildAt(index).getTag();
    }

    private void setupUi(final ConditionTag tag, View row) {
        if (tag.lines == null) {
            tag.lines = row.findViewById(16908290);
            tag.lines.setAccessibilityLiveRegion(1);
        }
        if (tag.line1 == null) {
            tag.line1 = (TextView) row.findViewById(16908308);
        }
        row.findViewById(16908309).setVisibility(8);
        tag.lines.setOnClickListener(new View.OnClickListener() { // from class: com.android.settingslib.notification.ZenDurationDialog.3
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                tag.rb.setChecked(true);
            }
        });
    }

    private void updateButtons(final ConditionTag tag, final View row, final int rowIndex) {
        boolean z;
        ImageView button1 = (ImageView) row.findViewById(16908313);
        button1.setOnClickListener(new View.OnClickListener() { // from class: com.android.settingslib.notification.ZenDurationDialog.4
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                ZenDurationDialog.this.onClickTimeButton(row, tag, false, rowIndex);
            }
        });
        ImageView button2 = (ImageView) row.findViewById(16908314);
        button2.setOnClickListener(new View.OnClickListener() { // from class: com.android.settingslib.notification.ZenDurationDialog.5
            @Override // android.view.View.OnClickListener
            public void onClick(View v) {
                ZenDurationDialog.this.onClickTimeButton(row, tag, true, rowIndex);
            }
        });
        long time = tag.countdownZenDuration;
        boolean z2 = true;
        if (rowIndex == 1) {
            button1.setVisibility(0);
            button2.setVisibility(0);
            if (time > MIN_BUCKET_MINUTES) {
                z = true;
            } else {
                z = false;
            }
            button1.setEnabled(z);
            if (tag.countdownZenDuration == MAX_BUCKET_MINUTES) {
                z2 = false;
            }
            button2.setEnabled(z2);
            button1.setAlpha(button1.isEnabled() ? 1.0f : 0.5f);
            button2.setAlpha(button2.isEnabled() ? 1.0f : 0.5f);
            return;
        }
        button1.setVisibility(8);
        button2.setVisibility(8);
    }

    protected void updateUi(ConditionTag tag, View row, int rowIndex) {
        if (tag.lines == null) {
            setupUi(tag, row);
        }
        updateButtons(tag, row, rowIndex);
        String radioContentText = BuildConfig.FLAVOR;
        switch (rowIndex) {
            case 0:
                radioContentText = this.mContext.getString(17041161);
                break;
            case 1:
                Condition condition = ZenModeConfig.toTimeCondition(this.mContext, tag.countdownZenDuration, ActivityManager.getCurrentUser(), false);
                radioContentText = condition.line1;
                break;
            case 2:
                radioContentText = this.mContext.getString(R.string.zen_mode_duration_always_prompt_title);
                break;
        }
        tag.line1.setText(radioContentText);
    }

    /* JADX WARN: Code restructure failed: missing block: B:17:0x002b, code lost:
        r11.mBucketIndex = r3;
        r0 = r8;
     */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    protected void onClickTimeButton(android.view.View r12, com.android.settingslib.notification.ZenDurationDialog.ConditionTag r13, boolean r14, int r15) {
        /*
            r11 = this;
            r0 = -1
            int[] r1 = com.android.settingslib.notification.ZenDurationDialog.MINUTE_BUCKETS
            int r1 = r1.length
            int r2 = r11.mBucketIndex
            r3 = 0
            r4 = -1
            r5 = 1
            if (r2 != r4) goto L3f
            int r2 = r13.countdownZenDuration
            long r6 = (long) r2
        Lf:
            r2 = r3
            if (r2 >= r1) goto L32
            if (r14 == 0) goto L16
            r3 = r2
            goto L19
        L16:
            int r3 = r1 + (-1)
            int r3 = r3 - r2
        L19:
            int[] r8 = com.android.settingslib.notification.ZenDurationDialog.MINUTE_BUCKETS
            r8 = r8[r3]
            if (r14 == 0) goto L24
            long r9 = (long) r8
            int r9 = (r9 > r6 ? 1 : (r9 == r6 ? 0 : -1))
            if (r9 > 0) goto L2b
        L24:
            if (r14 != 0) goto L2f
            long r9 = (long) r8
            int r9 = (r9 > r6 ? 1 : (r9 == r6 ? 0 : -1))
            if (r9 >= 0) goto L2f
        L2b:
            r11.mBucketIndex = r3
            r0 = r8
            goto L32
        L2f:
            int r3 = r2 + 1
            goto Lf
        L32:
            if (r0 != r4) goto L3e
            int r2 = com.android.settingslib.notification.ZenDurationDialog.DEFAULT_BUCKET_INDEX
            r11.mBucketIndex = r2
            int[] r2 = com.android.settingslib.notification.ZenDurationDialog.MINUTE_BUCKETS
            int r3 = r11.mBucketIndex
            r0 = r2[r3]
        L3e:
            goto L58
        L3f:
            int r2 = r1 + (-1)
            int r6 = r11.mBucketIndex
            if (r14 == 0) goto L47
            r4 = r5
        L47:
            int r6 = r6 + r4
            int r2 = java.lang.Math.min(r2, r6)
            int r2 = java.lang.Math.max(r3, r2)
            r11.mBucketIndex = r2
            int[] r2 = com.android.settingslib.notification.ZenDurationDialog.MINUTE_BUCKETS
            int r3 = r11.mBucketIndex
            r0 = r2[r3]
        L58:
            r13.countdownZenDuration = r0
            r11.bindTag(r0, r12, r15)
            android.widget.RadioButton r2 = r13.rb
            r2.setChecked(r5)
            return
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.settingslib.notification.ZenDurationDialog.onClickTimeButton(android.view.View, com.android.settingslib.notification.ZenDurationDialog$ConditionTag, boolean, int):void");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    /* loaded from: classes.dex */
    public static class ConditionTag {
        public int countdownZenDuration;
        public TextView line1;
        public View lines;
        public RadioButton rb;

        protected ConditionTag() {
        }
    }
}
