package com.xiaopeng.xui.sound;

import android.util.Log;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
/* loaded from: classes.dex */
public class XSoundEffectManager {
    private static final String TAG = "XSoundEffectManager";
    private ExecutorService mExecutorService;
    private boolean mIsDestroy;
    private ConcurrentHashMap<Integer, XSoundEffect> mPoolHashMap;
    private ConcurrentHashMap<Integer, Boolean> mReleaseMap;

    /* loaded from: classes.dex */
    class a implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        final /* synthetic */ int f43a;

        a(int i) {
            this.f43a = i;
        }

        @Override // java.lang.Runnable
        public void run() {
            XSoundEffectManager xSoundEffectManager;
            String format;
            Object[] objArr;
            if (XSoundEffectManager.this.mIsDestroy) {
                xSoundEffectManager = XSoundEffectManager.this;
                objArr = new Object[]{Integer.valueOf(this.f43a)};
            } else {
                Boolean bool = (Boolean) XSoundEffectManager.this.mReleaseMap.get(Integer.valueOf(this.f43a));
                if (bool == null || !bool.booleanValue()) {
                    long currentTimeMillis = System.currentTimeMillis();
                    XSoundEffect xSoundEffect = (XSoundEffect) XSoundEffectManager.this.mPoolHashMap.get(Integer.valueOf(this.f43a));
                    if (xSoundEffect == null) {
                        xSoundEffect = com.xiaopeng.xui.sound.b.a(this.f43a);
                        XSoundEffectManager.this.mPoolHashMap.put(Integer.valueOf(this.f43a), xSoundEffect);
                    }
                    xSoundEffect.play();
                    xSoundEffectManager = XSoundEffectManager.this;
                    format = String.format("play time : %s ,resource:%s", Long.valueOf(System.currentTimeMillis() - currentTimeMillis), Integer.valueOf(this.f43a));
                    xSoundEffectManager.log(format);
                }
                xSoundEffectManager = XSoundEffectManager.this;
                objArr = new Object[]{Integer.valueOf(this.f43a)};
            }
            format = String.format("play not resource:%s", objArr);
            xSoundEffectManager.log(format);
        }
    }

    /* loaded from: classes.dex */
    class b implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        final /* synthetic */ int f45a;

        b(int i) {
            this.f45a = i;
        }

        @Override // java.lang.Runnable
        public void run() {
            long currentTimeMillis = System.currentTimeMillis();
            XSoundEffect xSoundEffect = (XSoundEffect) XSoundEffectManager.this.mPoolHashMap.get(Integer.valueOf(this.f45a));
            if (xSoundEffect != null) {
                xSoundEffect.release();
            }
            XSoundEffectManager.this.log(String.format("release time : %s ,resource:%s", Long.valueOf(System.currentTimeMillis() - currentTimeMillis), Integer.valueOf(this.f45a)));
        }
    }

    /* loaded from: classes.dex */
    class c implements Runnable {
        c() {
        }

        @Override // java.lang.Runnable
        public void run() {
            long currentTimeMillis = System.currentTimeMillis();
            for (XSoundEffect xSoundEffect : XSoundEffectManager.this.mPoolHashMap.values()) {
                if (xSoundEffect != null) {
                    xSoundEffect.release();
                }
            }
            XSoundEffectManager.this.mPoolHashMap.clear();
            XSoundEffectManager.this.mReleaseMap.clear();
            XSoundEffectManager xSoundEffectManager = XSoundEffectManager.this;
            xSoundEffectManager.log("destroy time : " + (System.currentTimeMillis() - currentTimeMillis));
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class d implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        final /* synthetic */ int f48a;

        /* renamed from: b  reason: collision with root package name */
        final /* synthetic */ String f49b;
        final /* synthetic */ int c;
        final /* synthetic */ int d;

        d(int i, String str, int i2, int i3) {
            this.f48a = i;
            this.f49b = str;
            this.c = i2;
            this.d = i3;
        }

        @Override // java.lang.Runnable
        public void run() {
            XSoundEffectManager xSoundEffectManager = XSoundEffectManager.this;
            xSoundEffectManager.log("resetResource--resource:" + this.f48a + ",path:" + this.f49b + ",location:" + this.c + ",streamType:" + this.d);
            long currentTimeMillis = System.currentTimeMillis();
            com.xiaopeng.xui.sound.b.a(this.f48a, this.f49b, this.c, this.d);
            XSoundEffect xSoundEffect = (XSoundEffect) XSoundEffectManager.this.mPoolHashMap.get(Integer.valueOf(this.f48a));
            if (xSoundEffect != null) {
                xSoundEffect.release();
                XSoundEffectManager.this.mPoolHashMap.remove(Integer.valueOf(this.f48a));
            }
            XSoundEffectManager xSoundEffectManager2 = XSoundEffectManager.this;
            xSoundEffectManager2.log("resetResource time : " + (System.currentTimeMillis() - currentTimeMillis));
        }
    }

    /* loaded from: classes.dex */
    class e implements Runnable {

        /* renamed from: a  reason: collision with root package name */
        final /* synthetic */ int f50a;

        e(int i) {
            this.f50a = i;
        }

        @Override // java.lang.Runnable
        public void run() {
            long currentTimeMillis = System.currentTimeMillis();
            com.xiaopeng.xui.sound.b.b(this.f50a);
            XSoundEffect xSoundEffect = (XSoundEffect) XSoundEffectManager.this.mPoolHashMap.get(Integer.valueOf(this.f50a));
            if (xSoundEffect != null) {
                xSoundEffect.release();
                XSoundEffectManager.this.mPoolHashMap.remove(Integer.valueOf(this.f50a));
            }
            XSoundEffectManager.this.log(String.format("restoreResource time : %s ,resource:%s", Long.valueOf(System.currentTimeMillis() - currentTimeMillis), Integer.valueOf(this.f50a)));
        }
    }

    /* loaded from: classes.dex */
    private static class f {

        /* renamed from: a  reason: collision with root package name */
        private static final XSoundEffectManager f52a = new XSoundEffectManager(null);
    }

    private XSoundEffectManager() {
        this.mPoolHashMap = new ConcurrentHashMap<>();
        this.mExecutorService = Executors.newSingleThreadExecutor();
        this.mReleaseMap = new ConcurrentHashMap<>();
    }

    /* synthetic */ XSoundEffectManager(a aVar) {
        this();
    }

    public static XSoundEffectManager get() {
        return f.f52a;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void log(String str) {
        Log.d(TAG, str);
    }

    public synchronized void destroy() {
        log("destroy");
        this.mIsDestroy = true;
        this.mExecutorService.execute(new c());
    }

    public synchronized void play(int i) {
        log("play :" + i);
        this.mIsDestroy = false;
        this.mReleaseMap.put(Integer.valueOf(i), false);
        this.mExecutorService.execute(new a(i));
    }

    public synchronized void release(int i) {
        log("release :" + i);
        this.mReleaseMap.put(Integer.valueOf(i), true);
        this.mExecutorService.execute(new b(i));
    }

    public synchronized void resetResource(int i, String str, int i2) {
        resetResource(i, str, i2, 5);
    }

    public synchronized void resetResource(int i, String str, int i2, int i3) {
        log("resetResource :" + i);
        this.mExecutorService.execute(new d(i, str, i2, i3));
    }

    public synchronized void restoreResource(int i) {
        log("restoreResource :" + i);
        this.mExecutorService.execute(new e(i));
    }
}
