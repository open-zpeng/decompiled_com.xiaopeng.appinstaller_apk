package com.android.settingslib.inputmethod;

import android.app.ActivityManager;
import android.content.Context;
import android.os.RemoteException;
import android.util.Log;
import android.util.Slog;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.view.inputmethod.InputMethodSubtype;
import com.android.internal.inputmethod.InputMethodUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
/* loaded from: classes.dex */
public class InputMethodSettingValuesWrapper {
    private static final String TAG = InputMethodSettingValuesWrapper.class.getSimpleName();
    private static volatile InputMethodSettingValuesWrapper sInstance;
    private final InputMethodManager mImm;
    private final InputMethodUtils.InputMethodSettings mSettings;
    private final ArrayList<InputMethodInfo> mMethodList = new ArrayList<>();
    private final HashMap<String, InputMethodInfo> mMethodMap = new HashMap<>();
    private final HashSet<InputMethodInfo> mAsciiCapableEnabledImis = new HashSet<>();

    public static InputMethodSettingValuesWrapper getInstance(Context context) {
        if (sInstance == null) {
            synchronized (TAG) {
                if (sInstance == null) {
                    sInstance = new InputMethodSettingValuesWrapper(context);
                }
            }
        }
        return sInstance;
    }

    private static int getDefaultCurrentUserId() {
        try {
            return ActivityManager.getService().getCurrentUser().id;
        } catch (RemoteException e) {
            Slog.w(TAG, "Couldn't get current user ID; guessing it's 0", e);
            return 0;
        }
    }

    private InputMethodSettingValuesWrapper(Context context) {
        this.mSettings = new InputMethodUtils.InputMethodSettings(context.getResources(), context.getContentResolver(), this.mMethodMap, this.mMethodList, getDefaultCurrentUserId(), false);
        this.mImm = (InputMethodManager) context.getSystemService("input_method");
        refreshAllInputMethodAndSubtypes();
    }

    public void refreshAllInputMethodAndSubtypes() {
        synchronized (this.mMethodMap) {
            this.mMethodList.clear();
            this.mMethodMap.clear();
            List<InputMethodInfo> imms = this.mImm.getInputMethodList();
            this.mMethodList.addAll(imms);
            for (InputMethodInfo imi : imms) {
                this.mMethodMap.put(imi.getId(), imi);
            }
            updateAsciiCapableEnabledImis();
        }
    }

    private void updateAsciiCapableEnabledImis() {
        synchronized (this.mMethodMap) {
            this.mAsciiCapableEnabledImis.clear();
            List<InputMethodInfo> enabledImis = this.mSettings.getEnabledInputMethodListLocked();
            for (InputMethodInfo imi : enabledImis) {
                int subtypeCount = imi.getSubtypeCount();
                int i = 0;
                while (true) {
                    if (i < subtypeCount) {
                        InputMethodSubtype subtype = imi.getSubtypeAt(i);
                        if (!"keyboard".equalsIgnoreCase(subtype.getMode()) || !subtype.isAsciiCapable()) {
                            i++;
                        } else {
                            this.mAsciiCapableEnabledImis.add(imi);
                            break;
                        }
                    }
                }
            }
        }
    }

    public boolean isValidSystemNonAuxAsciiCapableIme(InputMethodInfo imi, Context context) {
        if (imi.isAuxiliaryIme()) {
            return false;
        }
        Locale systemLocale = context.getResources().getConfiguration().locale;
        if (InputMethodUtils.isSystemImeThatHasSubtypeOf(imi, context, true, systemLocale, false, InputMethodUtils.SUBTYPE_MODE_ANY)) {
            return true;
        }
        if (this.mAsciiCapableEnabledImis.isEmpty()) {
            Log.w(TAG, "ascii capable subtype enabled imi not found. Fall back to English Keyboard subtype.");
            return InputMethodUtils.containsSubtypeOf(imi, Locale.ENGLISH, false, "keyboard");
        }
        return this.mAsciiCapableEnabledImis.contains(imi);
    }
}
