package android.support.v7.preference.internal;

import android.content.Context;
import android.support.v7.preference.DialogPreference;
import android.util.AttributeSet;
import java.util.Set;
/* loaded from: classes.dex */
public abstract class AbstractMultiSelectListPreference extends DialogPreference {
    public abstract CharSequence[] getEntries();

    public abstract CharSequence[] getEntryValues();

    public abstract Set<String> getValues();

    public abstract void setValues(Set<String> set);

    public AbstractMultiSelectListPreference(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public AbstractMultiSelectListPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
}
