package com.android.settingslib.wifi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.NetworkKey;
import android.net.NetworkRequest;
import android.net.NetworkScoreManager;
import android.net.ScoredNetwork;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkScoreCache;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.ArraySet;
import android.util.Log;
import android.widget.Toast;
import com.android.settingslib.R;
import com.android.settingslib.core.lifecycle.LifecycleObserver;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.utils.ThreadUtils;
import com.android.settingslib.wifi.WifiTracker;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
/* loaded from: classes.dex */
public class WifiTracker implements LifecycleObserver, OnDestroy, OnStart, OnStop {
    public static boolean sVerboseLogging;
    private final ConnectivityManager mConnectivityManager;
    private final Context mContext;
    private final IntentFilter mFilter;
    private WifiInfo mLastInfo;
    private NetworkInfo mLastNetworkInfo;
    private final WifiListenerExecutor mListener;
    private long mMaxSpeedLabelScoreCacheAge;
    private WifiTrackerNetworkCallback mNetworkCallback;
    private final NetworkRequest mNetworkRequest;
    private final NetworkScoreManager mNetworkScoreManager;
    private boolean mNetworkScoringUiEnabled;
    private boolean mRegistered;
    Scanner mScanner;
    private WifiNetworkScoreCache mScoreCache;
    private final WifiManager mWifiManager;
    Handler mWorkHandler;
    private HandlerThread mWorkThread;
    private final AtomicBoolean mConnected = new AtomicBoolean(false);
    private final Object mLock = new Object();
    private final List<AccessPoint> mInternalAccessPoints = new ArrayList();
    private final Set<NetworkKey> mRequestedScores = new ArraySet();
    private boolean mStaleScanResults = true;
    private final HashMap<String, ScanResult> mScanResultCache = new HashMap<>();
    final BroadcastReceiver mReceiver = new BroadcastReceiver() { // from class: com.android.settingslib.wifi.WifiTracker.2
        @Override // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.net.wifi.WIFI_STATE_CHANGED".equals(action)) {
                WifiTracker.this.updateWifiState(intent.getIntExtra("wifi_state", 4));
            } else if ("android.net.wifi.SCAN_RESULTS".equals(action)) {
                WifiTracker.this.mStaleScanResults = false;
                WifiTracker.this.fetchScansAndConfigsAndUpdateAccessPoints();
            } else if ("android.net.wifi.CONFIGURED_NETWORKS_CHANGE".equals(action) || "android.net.wifi.LINK_CONFIGURATION_CHANGED".equals(action)) {
                WifiTracker.this.fetchScansAndConfigsAndUpdateAccessPoints();
            } else if ("android.net.wifi.STATE_CHANGE".equals(action)) {
                NetworkInfo info = (NetworkInfo) intent.getParcelableExtra("networkInfo");
                WifiTracker.this.updateNetworkInfo(info);
                WifiTracker.this.fetchScansAndConfigsAndUpdateAccessPoints();
            } else if ("android.net.wifi.RSSI_CHANGED".equals(action)) {
                NetworkInfo info2 = WifiTracker.this.mConnectivityManager.getNetworkInfo(WifiTracker.this.mWifiManager.getCurrentNetwork());
                WifiTracker.this.updateNetworkInfo(info2);
            }
        }
    };

    /* loaded from: classes.dex */
    public interface WifiListener {
        void onAccessPointsChanged();

        void onConnectedChanged();

        void onWifiStateChanged(int i);
    }

    static /* synthetic */ boolean access$1000() {
        return isVerboseLoggingEnabled();
    }

    private static final boolean DBG() {
        return Log.isLoggable("WifiTracker", 3);
    }

    private static boolean isVerboseLoggingEnabled() {
        return sVerboseLogging || Log.isLoggable("WifiTracker", 2);
    }

    WifiTracker(Context context, WifiListener wifiListener, WifiManager wifiManager, ConnectivityManager connectivityManager, NetworkScoreManager networkScoreManager, IntentFilter filter) {
        this.mContext = context;
        this.mWifiManager = wifiManager;
        this.mListener = new WifiListenerExecutor(wifiListener);
        this.mConnectivityManager = connectivityManager;
        sVerboseLogging = this.mWifiManager.getVerboseLoggingLevel() > 0;
        this.mFilter = filter;
        this.mNetworkRequest = new NetworkRequest.Builder().clearCapabilities().addCapability(15).addTransportType(1).build();
        this.mNetworkScoreManager = networkScoreManager;
        HandlerThread workThread = new HandlerThread("WifiTracker{" + Integer.toHexString(System.identityHashCode(this)) + "}", 10);
        workThread.start();
        setWorkThread(workThread);
    }

    void setWorkThread(HandlerThread workThread) {
        this.mWorkThread = workThread;
        this.mWorkHandler = new Handler(workThread.getLooper());
        this.mScoreCache = new WifiNetworkScoreCache(this.mContext, new WifiNetworkScoreCache.CacheListener(this.mWorkHandler) { // from class: com.android.settingslib.wifi.WifiTracker.1
            public void networkCacheUpdated(List<ScoredNetwork> networks) {
                if (WifiTracker.this.mRegistered) {
                    if (Log.isLoggable("WifiTracker", 2)) {
                        Log.v("WifiTracker", "Score cache was updated with networks: " + networks);
                    }
                    WifiTracker.this.updateNetworkScores();
                }
            }
        });
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnDestroy
    public void onDestroy() {
        this.mWorkThread.quit();
    }

    private void pauseScanning() {
        if (this.mScanner != null) {
            this.mScanner.pause();
            this.mScanner = null;
        }
        this.mStaleScanResults = true;
    }

    public void resumeScanning() {
        if (this.mScanner == null) {
            this.mScanner = new Scanner();
        }
        if (this.mWifiManager.isWifiEnabled()) {
            this.mScanner.resume();
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStart
    public void onStart() {
        forceUpdate();
        registerScoreCache();
        this.mNetworkScoringUiEnabled = Settings.Global.getInt(this.mContext.getContentResolver(), "network_scoring_ui_enabled", 0) == 1;
        this.mMaxSpeedLabelScoreCacheAge = Settings.Global.getLong(this.mContext.getContentResolver(), "speed_label_cache_eviction_age_millis", 1200000L);
        resumeScanning();
        if (!this.mRegistered) {
            this.mContext.registerReceiver(this.mReceiver, this.mFilter, null, this.mWorkHandler);
            this.mNetworkCallback = new WifiTrackerNetworkCallback();
            this.mConnectivityManager.registerNetworkCallback(this.mNetworkRequest, this.mNetworkCallback, this.mWorkHandler);
            this.mRegistered = true;
        }
    }

    private void forceUpdate() {
        this.mLastInfo = this.mWifiManager.getConnectionInfo();
        this.mLastNetworkInfo = this.mConnectivityManager.getNetworkInfo(this.mWifiManager.getCurrentNetwork());
        fetchScansAndConfigsAndUpdateAccessPoints();
    }

    private void registerScoreCache() {
        this.mNetworkScoreManager.registerNetworkScoreCache(1, this.mScoreCache, 2);
    }

    private void requestScoresForNetworkKeys(Collection<NetworkKey> keys) {
        if (keys.isEmpty()) {
            return;
        }
        if (DBG()) {
            Log.d("WifiTracker", "Requesting scores for Network Keys: " + keys);
        }
        this.mNetworkScoreManager.requestScores((NetworkKey[]) keys.toArray(new NetworkKey[keys.size()]));
        synchronized (this.mLock) {
            this.mRequestedScores.addAll(keys);
        }
    }

    @Override // com.android.settingslib.core.lifecycle.events.OnStop
    public void onStop() {
        if (this.mRegistered) {
            this.mContext.unregisterReceiver(this.mReceiver);
            this.mConnectivityManager.unregisterNetworkCallback(this.mNetworkCallback);
            this.mRegistered = false;
        }
        unregisterScoreCache();
        pauseScanning();
        this.mWorkHandler.removeCallbacksAndMessages(null);
    }

    private void unregisterScoreCache() {
        this.mNetworkScoreManager.unregisterNetworkScoreCache(1, this.mScoreCache);
        synchronized (this.mLock) {
            this.mRequestedScores.clear();
        }
    }

    private ArrayMap<String, List<ScanResult>> updateScanResultCache(List<ScanResult> newResults) {
        List<ScanResult> resultList;
        for (ScanResult newResult : newResults) {
            if (newResult.SSID != null && !newResult.SSID.isEmpty()) {
                this.mScanResultCache.put(newResult.BSSID, newResult);
            }
        }
        if (!this.mStaleScanResults) {
            evictOldScans();
        }
        ArrayMap<String, List<ScanResult>> scanResultsByApKey = new ArrayMap<>();
        for (ScanResult result : this.mScanResultCache.values()) {
            if (result.SSID != null && result.SSID.length() != 0 && !result.capabilities.contains("[IBSS]")) {
                String apKey = AccessPoint.getKey(result);
                if (scanResultsByApKey.containsKey(apKey)) {
                    resultList = scanResultsByApKey.get(apKey);
                } else {
                    resultList = new ArrayList<>();
                    scanResultsByApKey.put(apKey, resultList);
                }
                resultList.add(result);
            }
        }
        return scanResultsByApKey;
    }

    private void evictOldScans() {
        long nowMs = SystemClock.elapsedRealtime();
        Iterator<ScanResult> iter = this.mScanResultCache.values().iterator();
        while (iter.hasNext()) {
            ScanResult result = iter.next();
            if (nowMs - (result.timestamp / 1000) > 25000) {
                iter.remove();
            }
        }
    }

    private WifiConfiguration getWifiConfigurationForNetworkId(int networkId, List<WifiConfiguration> configs) {
        if (configs != null) {
            for (WifiConfiguration config : configs) {
                if (this.mLastInfo != null && networkId == config.networkId && (!config.selfAdded || config.numAssociation != 0)) {
                    return config;
                }
            }
            return null;
        }
        return null;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void fetchScansAndConfigsAndUpdateAccessPoints() {
        List<ScanResult> newScanResults = this.mWifiManager.getScanResults();
        if (isVerboseLoggingEnabled()) {
            Log.i("WifiTracker", "Fetched scan results: " + newScanResults);
        }
        List<WifiConfiguration> configs = this.mWifiManager.getConfiguredNetworks();
        updateAccessPoints(newScanResults, configs);
    }

    private void updateAccessPoints(List<ScanResult> newScanResults, List<WifiConfiguration> configs) {
        Map<String, WifiConfiguration> configsByKey = new ArrayMap<>(configs.size());
        if (configs != null) {
            for (WifiConfiguration config : configs) {
                configsByKey.put(AccessPoint.getKey(config), config);
            }
        }
        ArrayMap<String, List<ScanResult>> scanResultsByApKey = updateScanResultCache(newScanResults);
        WifiConfiguration connectionConfig = null;
        if (this.mLastInfo != null) {
            connectionConfig = getWifiConfigurationForNetworkId(this.mLastInfo.getNetworkId(), configs);
        }
        WifiConfiguration connectionConfig2 = connectionConfig;
        synchronized (this.mLock) {
            List<AccessPoint> cachedAccessPoints = new ArrayList<>(this.mInternalAccessPoints);
            ArrayList<AccessPoint> accessPoints = new ArrayList<>();
            List<NetworkKey> scoresToRequest = new ArrayList<>();
            for (Map.Entry<String, List<ScanResult>> entry : scanResultsByApKey.entrySet()) {
                for (ScanResult result : entry.getValue()) {
                    NetworkKey key = NetworkKey.createFromScanResult(result);
                    if (key != null && !this.mRequestedScores.contains(key)) {
                        scoresToRequest.add(key);
                    }
                }
                AccessPoint accessPoint = getCachedOrCreate(entry.getValue(), cachedAccessPoints);
                if (this.mLastInfo != null && this.mLastNetworkInfo != null) {
                    accessPoint.update(connectionConfig2, this.mLastInfo, this.mLastNetworkInfo);
                }
                accessPoint.update(configsByKey.get(entry.getKey()));
                accessPoints.add(accessPoint);
            }
            if (accessPoints.isEmpty() && connectionConfig2 != null) {
                AccessPoint activeAp = new AccessPoint(this.mContext, connectionConfig2);
                activeAp.update(connectionConfig2, this.mLastInfo, this.mLastNetworkInfo);
                accessPoints.add(activeAp);
                scoresToRequest.add(NetworkKey.createFromWifiInfo(this.mLastInfo));
            }
            requestScoresForNetworkKeys(scoresToRequest);
            Iterator<AccessPoint> it = accessPoints.iterator();
            while (it.hasNext()) {
                AccessPoint ap = it.next();
                ap.update(this.mScoreCache, this.mNetworkScoringUiEnabled, this.mMaxSpeedLabelScoreCacheAge);
            }
            Collections.sort(accessPoints);
            if (DBG()) {
                Log.d("WifiTracker", "------ Dumping SSIDs that were not seen on this scan ------");
                for (AccessPoint prevAccessPoint : this.mInternalAccessPoints) {
                    if (prevAccessPoint.getSsid() != null) {
                        String prevSsid = prevAccessPoint.getSsidStr();
                        boolean found = false;
                        Iterator<AccessPoint> it2 = accessPoints.iterator();
                        while (true) {
                            if (!it2.hasNext()) {
                                break;
                            }
                            AccessPoint newAccessPoint = it2.next();
                            if (newAccessPoint.getSsidStr() != null && newAccessPoint.getSsidStr().equals(prevSsid)) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            Log.d("WifiTracker", "Did not find " + prevSsid + " in this scan");
                        }
                    }
                }
                Log.d("WifiTracker", "---- Done dumping SSIDs that were not seen on this scan ----");
            }
            this.mInternalAccessPoints.clear();
            this.mInternalAccessPoints.addAll(accessPoints);
        }
        conditionallyNotifyListeners();
    }

    AccessPoint getCachedOrCreate(List<ScanResult> scanResults, List<AccessPoint> cache) {
        int N = cache.size();
        for (int i = 0; i < N; i++) {
            if (cache.get(i).getKey().equals(AccessPoint.getKey(scanResults.get(0)))) {
                AccessPoint ret = cache.remove(i);
                ret.setScanResults(scanResults);
                return ret;
            }
        }
        AccessPoint accessPoint = new AccessPoint(this.mContext, scanResults);
        return accessPoint;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNetworkInfo(NetworkInfo networkInfo) {
        if (!this.mWifiManager.isWifiEnabled()) {
            clearAccessPointsAndConditionallyUpdate();
            return;
        }
        if (networkInfo != null) {
            this.mLastNetworkInfo = networkInfo;
            if (DBG()) {
                Log.d("WifiTracker", "mLastNetworkInfo set: " + this.mLastNetworkInfo);
            }
            if (networkInfo.isConnected() != this.mConnected.getAndSet(networkInfo.isConnected())) {
                this.mListener.onConnectedChanged();
            }
        }
        WifiConfiguration connectionConfig = null;
        this.mLastInfo = this.mWifiManager.getConnectionInfo();
        if (DBG()) {
            Log.d("WifiTracker", "mLastInfo set as: " + this.mLastInfo);
        }
        if (this.mLastInfo != null) {
            connectionConfig = getWifiConfigurationForNetworkId(this.mLastInfo.getNetworkId(), this.mWifiManager.getConfiguredNetworks());
        }
        boolean updated = false;
        boolean reorder = false;
        synchronized (this.mLock) {
            for (int i = this.mInternalAccessPoints.size() - 1; i >= 0; i--) {
                AccessPoint ap = this.mInternalAccessPoints.get(i);
                boolean previouslyConnected = ap.isActive();
                if (ap.update(connectionConfig, this.mLastInfo, this.mLastNetworkInfo)) {
                    updated = true;
                    if (previouslyConnected != ap.isActive()) {
                        reorder = true;
                    }
                }
                if (ap.update(this.mScoreCache, this.mNetworkScoringUiEnabled, this.mMaxSpeedLabelScoreCacheAge)) {
                    reorder = true;
                    updated = true;
                }
            }
            if (reorder) {
                Collections.sort(this.mInternalAccessPoints);
            }
            if (updated) {
                conditionallyNotifyListeners();
            }
        }
    }

    private void clearAccessPointsAndConditionallyUpdate() {
        synchronized (this.mLock) {
            if (!this.mInternalAccessPoints.isEmpty()) {
                this.mInternalAccessPoints.clear();
                conditionallyNotifyListeners();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateNetworkScores() {
        synchronized (this.mLock) {
            boolean updated = false;
            for (int i = 0; i < this.mInternalAccessPoints.size(); i++) {
                if (this.mInternalAccessPoints.get(i).update(this.mScoreCache, this.mNetworkScoringUiEnabled, this.mMaxSpeedLabelScoreCacheAge)) {
                    updated = true;
                }
            }
            if (updated) {
                Collections.sort(this.mInternalAccessPoints);
                conditionallyNotifyListeners();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateWifiState(int state) {
        if (state == 3) {
            if (this.mScanner != null) {
                this.mScanner.resume();
            }
        } else {
            clearAccessPointsAndConditionallyUpdate();
            this.mLastInfo = null;
            this.mLastNetworkInfo = null;
            if (this.mScanner != null) {
                this.mScanner.pause();
            }
            this.mStaleScanResults = true;
        }
        this.mListener.onWifiStateChanged(state);
    }

    /* loaded from: classes.dex */
    private final class WifiTrackerNetworkCallback extends ConnectivityManager.NetworkCallback {
        private WifiTrackerNetworkCallback() {
        }

        @Override // android.net.ConnectivityManager.NetworkCallback
        public void onCapabilitiesChanged(Network network, NetworkCapabilities nc) {
            if (network.equals(WifiTracker.this.mWifiManager.getCurrentNetwork())) {
                WifiTracker.this.updateNetworkInfo(null);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class Scanner extends Handler {
        private int mRetry = 0;

        Scanner() {
        }

        void resume() {
            if (!hasMessages(0)) {
                sendEmptyMessage(0);
            }
        }

        void pause() {
            this.mRetry = 0;
            removeMessages(0);
        }

        boolean isScanning() {
            return hasMessages(0);
        }

        @Override // android.os.Handler
        public void handleMessage(Message message) {
            if (message.what != 0) {
                return;
            }
            if (WifiTracker.this.mWifiManager.startScan()) {
                this.mRetry = 0;
            } else {
                int i = this.mRetry + 1;
                this.mRetry = i;
                if (i >= 3) {
                    this.mRetry = 0;
                    if (WifiTracker.this.mContext != null) {
                        Toast.makeText(WifiTracker.this.mContext, R.string.wifi_fail_to_scan, 1).show();
                        return;
                    }
                    return;
                }
            }
            sendEmptyMessageDelayed(0, 10000L);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class WifiListenerExecutor implements WifiListener {
        private final WifiListener mDelegatee;

        public WifiListenerExecutor(WifiListener listener) {
            this.mDelegatee = listener;
        }

        @Override // com.android.settingslib.wifi.WifiTracker.WifiListener
        public void onWifiStateChanged(final int state) {
            runAndLog(new Runnable() { // from class: com.android.settingslib.wifi.-$$Lambda$WifiTracker$WifiListenerExecutor$PZBvWEzpVHhaI95PbZNbzEgAH1I
                @Override // java.lang.Runnable
                public final void run() {
                    WifiTracker.WifiListenerExecutor.this.mDelegatee.onWifiStateChanged(state);
                }
            }, String.format("Invoking onWifiStateChanged callback with state %d", Integer.valueOf(state)));
        }

        @Override // com.android.settingslib.wifi.WifiTracker.WifiListener
        public void onConnectedChanged() {
            final WifiListener wifiListener = this.mDelegatee;
            Objects.requireNonNull(wifiListener);
            runAndLog(new Runnable() { // from class: com.android.settingslib.wifi.-$$Lambda$6PbPNXCvqbAnKbPWPJrs-dDWQEQ
                @Override // java.lang.Runnable
                public final void run() {
                    WifiTracker.WifiListener.this.onConnectedChanged();
                }
            }, "Invoking onConnectedChanged callback");
        }

        @Override // com.android.settingslib.wifi.WifiTracker.WifiListener
        public void onAccessPointsChanged() {
            final WifiListener wifiListener = this.mDelegatee;
            Objects.requireNonNull(wifiListener);
            runAndLog(new Runnable() { // from class: com.android.settingslib.wifi.-$$Lambda$evcvquoPxZkPmBIit31UXvhXEJk
                @Override // java.lang.Runnable
                public final void run() {
                    WifiTracker.WifiListener.this.onAccessPointsChanged();
                }
            }, "Invoking onAccessPointsChanged callback");
        }

        private void runAndLog(final Runnable r, final String verboseLog) {
            ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settingslib.wifi.-$$Lambda$WifiTracker$WifiListenerExecutor$BMWc3s6WnR_Ijg_9a3gQADAjI3Y
                @Override // java.lang.Runnable
                public final void run() {
                    WifiTracker.WifiListenerExecutor.lambda$runAndLog$1(WifiTracker.WifiListenerExecutor.this, verboseLog, r);
                }
            });
        }

        public static /* synthetic */ void lambda$runAndLog$1(WifiListenerExecutor wifiListenerExecutor, String verboseLog, Runnable r) {
            if (WifiTracker.this.mRegistered) {
                if (WifiTracker.access$1000()) {
                    Log.i("WifiTracker", verboseLog);
                }
                r.run();
            }
        }
    }

    private void conditionallyNotifyListeners() {
        if (this.mStaleScanResults) {
            return;
        }
        this.mListener.onAccessPointsChanged();
    }
}
