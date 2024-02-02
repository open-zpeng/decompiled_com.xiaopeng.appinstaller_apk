package com.android.settingslib.applications;

import android.content.res.Configuration;
import android.content.res.Resources;
/* loaded from: classes.dex */
public class InterestingConfigChanges {
    private final int mFlags;
    private final Configuration mLastConfiguration;
    private int mLastDensity;

    public boolean applyNewConfig(Resources res) {
        int configChanges = this.mLastConfiguration.updateFrom(Configuration.generateDelta(this.mLastConfiguration, res.getConfiguration()));
        boolean densityChanged = this.mLastDensity != res.getDisplayMetrics().densityDpi;
        if (densityChanged || (this.mFlags & configChanges) != 0) {
            this.mLastDensity = res.getDisplayMetrics().densityDpi;
            return true;
        }
        return false;
    }
}
