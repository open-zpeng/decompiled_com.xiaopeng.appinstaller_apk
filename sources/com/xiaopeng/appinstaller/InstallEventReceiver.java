package com.xiaopeng.appinstaller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.xiaopeng.appinstaller.EventResultPersister;
/* loaded from: classes.dex */
public class InstallEventReceiver extends BroadcastReceiver {
    private static final Object sLock = new Object();
    private static EventResultPersister sReceiver;

    private static EventResultPersister getReceiver(Context context) {
        synchronized (sLock) {
            if (sReceiver == null) {
                sReceiver = new EventResultPersister(TemporaryFileManager.getInstallStateFile(context));
            }
        }
        return sReceiver;
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        getReceiver(context).onEventReceived(context, intent);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static int addObserver(Context context, int id, EventResultPersister.EventResultObserver observer) throws EventResultPersister.OutOfIdsException {
        return getReceiver(context).addObserver(id, observer);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void removeObserver(Context context, int id) {
        getReceiver(context).removeObserver(id);
    }
}
