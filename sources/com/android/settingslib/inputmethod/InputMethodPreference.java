package com.android.settingslib.inputmethod;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.preference.Preference;
import android.text.TextUtils;
import android.util.Log;
import android.view.inputmethod.InputMethodInfo;
import android.widget.Toast;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.inputmethod.InputMethodUtils;
import com.android.settingslib.R;
import com.android.settingslib.RestrictedSwitchPreference;
import xiaopeng.widget.BuildConfig;
/* loaded from: classes.dex */
public class InputMethodPreference extends RestrictedSwitchPreference implements Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private static final String TAG = InputMethodPreference.class.getSimpleName();
    private AlertDialog mDialog;
    private final boolean mHasPriorityInSorting;
    private final InputMethodInfo mImi;
    private final InputMethodSettingValuesWrapper mInputMethodSettingValues;
    private final boolean mIsAllowedByOrganization;
    private final OnSavePreferenceListener mOnSaveListener;

    /* loaded from: classes.dex */
    public interface OnSavePreferenceListener {
        void onSaveInputMethodPreference(InputMethodPreference inputMethodPreference);
    }

    @VisibleForTesting
    InputMethodPreference(Context context, InputMethodInfo imi, CharSequence title, boolean isAllowedByOrganization, OnSavePreferenceListener onSaveListener) {
        super(context);
        this.mDialog = null;
        boolean z = false;
        setPersistent(false);
        this.mImi = imi;
        this.mIsAllowedByOrganization = isAllowedByOrganization;
        this.mOnSaveListener = onSaveListener;
        setSwitchTextOn(BuildConfig.FLAVOR);
        setSwitchTextOff(BuildConfig.FLAVOR);
        setKey(imi.getId());
        setTitle(title);
        String settingsActivity = imi.getSettingsActivity();
        if (TextUtils.isEmpty(settingsActivity)) {
            setIntent(null);
        } else {
            Intent intent = new Intent("android.intent.action.MAIN");
            intent.setClassName(imi.getPackageName(), settingsActivity);
            setIntent(intent);
        }
        this.mInputMethodSettingValues = InputMethodSettingValuesWrapper.getInstance(context);
        if (InputMethodUtils.isSystemIme(imi) && this.mInputMethodSettingValues.isValidSystemNonAuxAsciiCapableIme(imi, context)) {
            z = true;
        }
        this.mHasPriorityInSorting = z;
        setOnPreferenceClickListener(this);
        setOnPreferenceChangeListener(this);
    }

    private boolean isImeEnabler() {
        return getWidgetLayoutResource() != 0;
    }

    @Override // android.support.v7.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (isImeEnabler()) {
            if (isChecked()) {
                setCheckedInternal(false);
                return false;
            }
            if (InputMethodUtils.isSystemIme(this.mImi)) {
                if (this.mImi.getServiceInfo().directBootAware || isTv()) {
                    setCheckedInternal(true);
                } else if (!isTv()) {
                    showDirectBootWarnDialog();
                }
            } else {
                showSecurityWarnDialog();
            }
            return false;
        }
        return false;
    }

    @Override // android.support.v7.preference.Preference.OnPreferenceClickListener
    public boolean onPreferenceClick(Preference preference) {
        if (isImeEnabler()) {
            return true;
        }
        Context context = getContext();
        try {
            Intent intent = getIntent();
            if (intent != null) {
                context.startActivity(intent);
            }
        } catch (ActivityNotFoundException e) {
            Log.d(TAG, "IME's Settings Activity Not Found", e);
            String message = context.getString(R.string.failed_to_open_app_settings_toast, this.mImi.loadLabel(context.getPackageManager()));
            Toast.makeText(context, message, 1).show();
        }
        return true;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setCheckedInternal(boolean checked) {
        super.setChecked(checked);
        this.mOnSaveListener.onSaveInputMethodPreference(this);
        notifyChanged();
    }

    private void showSecurityWarnDialog() {
        if (this.mDialog != null && this.mDialog.isShowing()) {
            this.mDialog.dismiss();
        }
        Context context = getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle(17039380);
        CharSequence label = this.mImi.getServiceInfo().applicationInfo.loadLabel(context.getPackageManager());
        builder.setMessage(context.getString(R.string.ime_security_warning, label));
        builder.setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.settingslib.inputmethod.-$$Lambda$InputMethodPreference$pHt4-6FWRQ9Ts6PuJy_AB14MhJc
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                InputMethodPreference.lambda$showSecurityWarnDialog$0(InputMethodPreference.this, dialogInterface, i);
            }
        });
        builder.setNegativeButton(17039360, new DialogInterface.OnClickListener() { // from class: com.android.settingslib.inputmethod.-$$Lambda$InputMethodPreference$HH5dtwzFZv06UNDXJAO6Cyx4kxo
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                InputMethodPreference.this.setCheckedInternal(false);
            }
        });
        this.mDialog = builder.create();
        this.mDialog.show();
    }

    public static /* synthetic */ void lambda$showSecurityWarnDialog$0(InputMethodPreference inputMethodPreference, DialogInterface dialog, int which) {
        if (inputMethodPreference.mImi.getServiceInfo().directBootAware || inputMethodPreference.isTv()) {
            inputMethodPreference.setCheckedInternal(true);
        } else {
            inputMethodPreference.showDirectBootWarnDialog();
        }
    }

    private boolean isTv() {
        return (getContext().getResources().getConfiguration().uiMode & 15) == 4;
    }

    private void showDirectBootWarnDialog() {
        if (this.mDialog != null && this.mDialog.isShowing()) {
            this.mDialog.dismiss();
        }
        Context context = getContext();
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setMessage(context.getText(R.string.direct_boot_unaware_dialog_message));
        builder.setPositiveButton(17039370, new DialogInterface.OnClickListener() { // from class: com.android.settingslib.inputmethod.-$$Lambda$InputMethodPreference$FTfMqDGTv2yWgiGfPYaiYBCHriY
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                InputMethodPreference.this.setCheckedInternal(true);
            }
        });
        builder.setNegativeButton(17039360, new DialogInterface.OnClickListener() { // from class: com.android.settingslib.inputmethod.-$$Lambda$InputMethodPreference$_R1WCgG1LabBNKieYWiJs9NnYv4
            @Override // android.content.DialogInterface.OnClickListener
            public final void onClick(DialogInterface dialogInterface, int i) {
                InputMethodPreference.this.setCheckedInternal(false);
            }
        });
        this.mDialog = builder.create();
        this.mDialog.show();
    }
}
