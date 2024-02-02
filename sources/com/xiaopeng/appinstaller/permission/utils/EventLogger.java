package com.xiaopeng.appinstaller.permission.utils;

import android.metrics.LogMaker;
import com.android.internal.logging.MetricsLogger;
/* loaded from: classes.dex */
public class EventLogger {
    private static final MetricsLogger sMetricsLogger = new MetricsLogger();

    public static void logPermission(int action, String name, String packageName) {
        LogMaker log = new LogMaker(action);
        log.setPackageName(packageName);
        log.addTaggedData(1241, name);
        sMetricsLogger.write(log);
    }
}
