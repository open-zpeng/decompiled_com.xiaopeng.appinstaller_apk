package android.support.v7.preference;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
/* loaded from: classes.dex */
public class ListPreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat {
    private int mClickedDialogEntryIndex;
    private CharSequence[] mEntries;
    private CharSequence[] mEntryValues;

    public static ListPreferenceDialogFragmentCompat newInstance(String key) {
        ListPreferenceDialogFragmentCompat fragment = new ListPreferenceDialogFragmentCompat();
        Bundle b2 = new Bundle(1);
        b2.putString("key", key);
        fragment.setArguments(b2);
        return fragment;
    }

    @Override // android.support.v7.preference.PreferenceDialogFragmentCompat, android.support.v4.app.DialogFragment, android.support.v4.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            ListPreference preference = getListPreference();
            if (preference.getEntries() == null || preference.getEntryValues() == null) {
                throw new IllegalStateException("ListPreference requires an entries array and an entryValues array.");
            }
            this.mClickedDialogEntryIndex = preference.findIndexOfValue(preference.getValue());
            this.mEntries = preference.getEntries();
            this.mEntryValues = preference.getEntryValues();
            return;
        }
        this.mClickedDialogEntryIndex = savedInstanceState.getInt("ListPreferenceDialogFragment.index", 0);
        this.mEntries = savedInstanceState.getCharSequenceArray("ListPreferenceDialogFragment.entries");
        this.mEntryValues = savedInstanceState.getCharSequenceArray("ListPreferenceDialogFragment.entryValues");
    }

    @Override // android.support.v7.preference.PreferenceDialogFragmentCompat, android.support.v4.app.DialogFragment, android.support.v4.app.Fragment
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("ListPreferenceDialogFragment.index", this.mClickedDialogEntryIndex);
        outState.putCharSequenceArray("ListPreferenceDialogFragment.entries", this.mEntries);
        outState.putCharSequenceArray("ListPreferenceDialogFragment.entryValues", this.mEntryValues);
    }

    private ListPreference getListPreference() {
        return (ListPreference) getPreference();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.preference.PreferenceDialogFragmentCompat
    public void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
        builder.setSingleChoiceItems(this.mEntries, this.mClickedDialogEntryIndex, new DialogInterface.OnClickListener() { // from class: android.support.v7.preference.ListPreferenceDialogFragmentCompat.1
            @Override // android.content.DialogInterface.OnClickListener
            public void onClick(DialogInterface dialog, int which) {
                ListPreferenceDialogFragmentCompat.this.mClickedDialogEntryIndex = which;
                ListPreferenceDialogFragmentCompat.this.onClick(dialog, -1);
                dialog.dismiss();
            }
        });
        builder.setPositiveButton((CharSequence) null, (DialogInterface.OnClickListener) null);
    }

    @Override // android.support.v7.preference.PreferenceDialogFragmentCompat
    public void onDialogClosed(boolean positiveResult) {
        ListPreference preference = getListPreference();
        if (positiveResult && this.mClickedDialogEntryIndex >= 0) {
            String value = this.mEntryValues[this.mClickedDialogEntryIndex].toString();
            if (preference.callChangeListener(value)) {
                preference.setValue(value);
            }
        }
    }
}
