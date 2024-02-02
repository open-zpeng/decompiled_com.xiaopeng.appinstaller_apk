package com.xiaopeng.appinstaller;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import java.io.File;
import java.io.IOException;
/* loaded from: classes.dex */
public class TemporaryFileManager extends BroadcastReceiver {
    private static final String LOG_TAG = TemporaryFileManager.class.getSimpleName();

    public static File getStagedFile(Context context) throws IOException {
        return File.createTempFile("package", ".apk", context.getNoBackupFilesDir());
    }

    /*  JADX ERROR: JadxRuntimeException in pass: BlockProcessor
        jadx.core.utils.exceptions.JadxRuntimeException: Found unreachable blocks
        	at jadx.core.dex.visitors.blocks.DominatorTree.sortBlocks(DominatorTree.java:35)
        	at jadx.core.dex.visitors.blocks.DominatorTree.compute(DominatorTree.java:25)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.computeDominators(BlockProcessor.java:202)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.processBlocksTree(BlockProcessor.java:45)
        	at jadx.core.dex.visitors.blocks.BlockProcessor.visit(BlockProcessor.java:39)
        */
    public static java.io.File writeContentFromUri(android.content.Context r8, android.net.Uri r9) {
        /*
            r0 = 0
            r1 = r0
            android.content.ContentResolver r2 = r8.getContentResolver()     // Catch: java.lang.Throwable -> L59
            java.io.InputStream r2 = r2.openInputStream(r9)     // Catch: java.lang.Throwable -> L59
            if (r2 != 0) goto L13
        Ld:
            if (r2 == 0) goto L12
            $closeResource(r0, r2)     // Catch: java.lang.Throwable -> L59
        L12:
            return r0
        L13:
            java.io.File r3 = getStagedFile(r8)     // Catch: java.lang.Throwable -> L48
            r1 = r3
            java.io.FileOutputStream r3 = new java.io.FileOutputStream     // Catch: java.lang.Throwable -> L48
            r3.<init>(r1)     // Catch: java.lang.Throwable -> L48
            r4 = 1048576(0x100000, float:1.469368E-39)
            byte[] r4 = new byte[r4]     // Catch: java.lang.Throwable -> L3a
        L21:
            int r5 = r2.read(r4)     // Catch: java.lang.Throwable -> L3a
            r6 = r5
            if (r5 < 0) goto L2d
            r5 = 0
            r3.write(r4, r5, r6)     // Catch: java.lang.Throwable -> L3a
            goto L21
        L2d:
            $closeResource(r0, r3)     // Catch: java.lang.Throwable -> L48
            if (r2 == 0) goto L35
            $closeResource(r0, r2)     // Catch: java.lang.Throwable -> L59
        L35:
            return r1
        L37:
            r4 = move-exception
            r5 = r0
            goto L40
        L3a:
            r4 = move-exception
            throw r4     // Catch: java.lang.Throwable -> L3c
        L3c:
            r5 = move-exception
            r7 = r5
            r5 = r4
            r4 = r7
        L40:
            $closeResource(r5, r3)     // Catch: java.lang.Throwable -> L48
            throw r4     // Catch: java.lang.Throwable -> L48
        L44:
            r3 = move-exception
            r4 = r1
            r1 = r0
            goto L4f
        L48:
            r3 = move-exception
            throw r3     // Catch: java.lang.Throwable -> L4a
        L4a:
            r4 = move-exception
            r7 = r4
            r4 = r1
            r1 = r3
            r3 = r7
        L4f:
            if (r2 == 0) goto L58
            $closeResource(r1, r2)     // Catch: java.lang.Throwable -> L55
            goto L58
        L55:
            r2 = move-exception
            r1 = r4
            goto L5a
        L58:
            throw r3     // Catch: java.lang.Throwable -> L55
        L59:
            r2 = move-exception
        L5a:
            java.lang.String r3 = com.xiaopeng.appinstaller.TemporaryFileManager.LOG_TAG
            java.lang.String r4 = "Error staging apk from content URI"
            android.util.Log.w(r3, r4, r2)
            return r0
        */
        throw new UnsupportedOperationException("Method not decompiled: com.xiaopeng.appinstaller.TemporaryFileManager.writeContentFromUri(android.content.Context, android.net.Uri):java.io.File");
    }

    private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) {
        if (x0 == null) {
            x1.close();
            return;
        }
        try {
            x1.close();
        } catch (Throwable th) {
            x0.addSuppressed(th);
        }
    }

    public static File getInstallStateFile(Context context) {
        return new File(context.getNoBackupFilesDir(), "install_results.xml");
    }

    public static File getUninstallStateFile(Context context) {
        return new File(context.getNoBackupFilesDir(), "uninstall_results.xml");
    }

    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        long systemBootTime = System.currentTimeMillis() - SystemClock.elapsedRealtime();
        File[] filesOnBoot = context.getNoBackupFilesDir().listFiles();
        if (filesOnBoot == null) {
            return;
        }
        for (File fileOnBoot : filesOnBoot) {
            if (systemBootTime > fileOnBoot.lastModified()) {
                boolean wasDeleted = fileOnBoot.delete();
                if (!wasDeleted) {
                    Log.w(LOG_TAG, "Could not delete " + fileOnBoot.getName() + " onBoot");
                }
            } else {
                Log.w(LOG_TAG, fileOnBoot.getName() + " was created before onBoot broadcast was received");
            }
        }
    }
}
