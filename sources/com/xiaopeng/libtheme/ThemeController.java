package com.xiaopeng.libtheme;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings;
import com.xiaopeng.libtheme.ThemeManager;
import java.util.ArrayList;
import java.util.Iterator;
import xiaopeng.widget.BuildConfig;
/* loaded from: classes.dex */
public class ThemeController {
    private static final int MSG_STATE_NOTIFY = 1001;
    private static final int MSG_STATE_TIMEOUT = 1002;
    public static final int STATE_THEME_CHANGED = 2;
    public static final int STATE_THEME_PREPARE = 1;
    public static final int STATE_THEME_UNKNOWN = 0;
    private static final String TAG = "ThemeController";
    private static final int VIEW_DISABLE = 0;
    private static final int VIEW_ENABLED = 1;
    private static final int VIEW_UNKNOWN = -1;
    private Context mContext;
    private final b mThemeObserver;
    private static final String KEY_THEME_MODE = "key_theme_mode";
    public static final Uri URI_THEME_MODE = Settings.Secure.getUriFor(KEY_THEME_MODE);
    private static final String KEY_THEME_STATE = "key_theme_type";
    public static final Uri URI_THEME_STATE = Settings.Secure.getUriFor(KEY_THEME_STATE);
    private static final String KEY_DAYNIGHT_MODE = "ui_night_mode";
    public static final Uri URI_DAYNIGHT_MODE = Settings.Secure.getUriFor(KEY_DAYNIGHT_MODE);
    public static final long DEFAULT_STATE_DELAY = SystemProperties.getLong("persist.sys.theme.settings.delay", 3000);
    public static final long DEFAULT_STATE_TIMEOUT = SystemProperties.getLong("persist.sys.theme.settings.timeout", 3000);
    private static ThemeController sThemeController = null;
    private int mThemeMode = 0;
    private int mThemeState = 0;
    private int mDaynightMode = 0;
    private int mViewState = -1;
    private final Handler mHandler = new a();
    private ArrayList<OnThemeListener> mThemeListeners = new ArrayList<>();

    /* loaded from: classes.dex */
    public interface OnThemeListener {
        void onStateChanged(boolean z);

        void onThemeChanged(boolean z, Uri uri);
    }

    /* loaded from: classes.dex */
    class a extends Handler {
        a() {
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            super.handleMessage(message);
            int i = message.what;
            if (i == ThemeController.MSG_STATE_NOTIFY) {
                ThemeController.this.onStateChanged(message.arg1 == 1);
            } else if (i != ThemeController.MSG_STATE_TIMEOUT) {
            } else {
                ThemeController.this.handleStateEvent();
            }
        }
    }

    /* loaded from: classes.dex */
    private class b {

        /* renamed from: a  reason: collision with root package name */
        private Context f13a;

        /* renamed from: b  reason: collision with root package name */
        private final ContentObserver f14b;

        /* loaded from: classes.dex */
        class a extends ContentObserver {
            a(Handler handler, ThemeController themeController) {
                super(handler);
            }

            @Override // android.database.ContentObserver
            public void onChange(boolean z, Uri uri) {
                super.onChange(z, uri);
                if (z) {
                    return;
                }
                ThemeController.this.readThemeSettings();
                ThemeController.this.performStateChanged(z, uri);
                ThemeController.this.performThemeChanged(z, uri);
            }
        }

        public b(Context context, Handler handler) {
            this.f13a = context;
            this.f14b = new a(handler, ThemeController.this);
        }

        public void a() {
            this.f13a.getContentResolver().registerContentObserver(ThemeController.URI_THEME_MODE, true, this.f14b);
            this.f13a.getContentResolver().registerContentObserver(ThemeController.URI_THEME_STATE, true, this.f14b);
            this.f13a.getContentResolver().registerContentObserver(ThemeController.URI_DAYNIGHT_MODE, true, this.f14b);
        }
    }

    private ThemeController(Context context) {
        this.mContext = context;
        this.mThemeObserver = new b(context, this.mHandler);
        this.mThemeObserver.a();
        readThemeSettings();
    }

    public static ThemeController getInstance(Context context) {
        if (sThemeController == null) {
            synchronized (ThemeController.class) {
                if (sThemeController == null) {
                    sThemeController = new ThemeController(context);
                }
            }
        }
        return sThemeController;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleStateEvent() {
        boolean z = this.mDaynightMode == 0;
        boolean isThemeWorking = isThemeWorking();
        long j = isThemeWorking ? 0L : z ? DEFAULT_STATE_DELAY : 1000L;
        this.mHandler.removeMessages(MSG_STATE_NOTIFY);
        Handler handler = this.mHandler;
        handler.sendMessageDelayed(handler.obtainMessage(MSG_STATE_NOTIFY, isThemeWorking ? 1 : 0, 0), j);
        if (isThemeWorking) {
            this.mHandler.removeMessages(MSG_STATE_TIMEOUT);
            this.mHandler.sendEmptyMessageDelayed(MSG_STATE_TIMEOUT, DEFAULT_STATE_TIMEOUT);
        }
        StringBuffer stringBuffer = new StringBuffer(BuildConfig.FLAVOR);
        stringBuffer.append("handleStateChanged");
        stringBuffer.append(" auto=" + z);
        stringBuffer.append(" isThemeWorking=" + isThemeWorking);
        stringBuffer.append(" disable=" + isThemeWorking);
        stringBuffer.append(" delay=" + j);
        stringBuffer.append(" themeState=" + this.mThemeState);
        stringBuffer.append(" daynightMode=" + this.mDaynightMode);
        ThemeManager.Logger.log(TAG, stringBuffer.toString());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onStateChanged(boolean z) {
        ThemeManager.Logger.log(TAG, "onStateChanged disableView=" + z);
        if ((!z) == this.mViewState) {
            ThemeManager.Logger.log(TAG, "onStateChanged same state not to listeners");
            return;
        }
        setViewState(z);
        ArrayList<OnThemeListener> arrayList = this.mThemeListeners;
        if (arrayList != null) {
            Iterator<OnThemeListener> it = arrayList.iterator();
            while (it.hasNext()) {
                OnThemeListener next = it.next();
                if (next != null) {
                    next.onStateChanged(z);
                }
            }
        }
    }

    private void onThemeChanged(boolean z, Uri uri) {
        ArrayList<OnThemeListener> arrayList = this.mThemeListeners;
        if (arrayList != null) {
            Iterator<OnThemeListener> it = arrayList.iterator();
            while (it.hasNext()) {
                OnThemeListener next = it.next();
                if (next != null) {
                    next.onThemeChanged(z, uri);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void performStateChanged(boolean z, Uri uri) {
        resetViewStateIfNeed(z, uri);
        handleStateEvent();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public synchronized void performThemeChanged(boolean z, Uri uri) {
        onThemeChanged(z, uri);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void readThemeSettings() {
        ContentResolver contentResolver = this.mContext.getContentResolver();
        this.mThemeMode = Settings.Secure.getInt(contentResolver, KEY_THEME_MODE, 0);
        this.mThemeState = Settings.Secure.getInt(contentResolver, KEY_THEME_STATE, 0);
        this.mDaynightMode = Settings.Secure.getInt(contentResolver, KEY_DAYNIGHT_MODE, 0);
        ThemeManager.Logger.log(TAG, "readThemeSettings themeState=" + this.mThemeState + " daynightMode=" + this.mDaynightMode);
    }

    private void resetViewStateIfNeed(boolean z, Uri uri) {
        ThemeManager.Logger.log(TAG, "resetViewStateIfNeed uri=" + uri);
        if (Settings.Secure.getUriFor(KEY_DAYNIGHT_MODE).equals(uri)) {
            this.mViewState = -1;
        }
    }

    private void setViewState(boolean z) {
        this.mViewState = !z ? 1 : 0;
    }

    public boolean isThemeWorking() {
        return ThemeManager.isThemeWorking(this.mContext) || this.mThemeState != 0;
    }

    public void register(OnThemeListener onThemeListener) {
        ThemeManager.Logger.log(TAG, "register listener=" + onThemeListener);
        this.mThemeListeners.add(onThemeListener);
    }

    public void unregister(OnThemeListener onThemeListener) {
        ThemeManager.Logger.log(TAG, "unregister listener=" + onThemeListener);
        if (this.mThemeListeners.contains(onThemeListener)) {
            this.mThemeListeners.remove(onThemeListener);
        }
    }
}
