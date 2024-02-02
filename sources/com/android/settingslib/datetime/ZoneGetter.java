package com.android.settingslib.datetime;

import java.util.List;
import libcore.util.TimeZoneFinder;
/* loaded from: classes.dex */
public class ZoneGetter {

    /* loaded from: classes.dex */
    public static final class ZoneGetterData {
        public List<String> lookupTimeZoneIdsByCountry(String country) {
            return TimeZoneFinder.getInstance().lookupTimeZoneIdsByCountry(country);
        }
    }
}
