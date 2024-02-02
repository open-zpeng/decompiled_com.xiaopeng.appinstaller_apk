package com.android.settingslib.wifi;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.NetworkKey;
import android.net.ScoredNetwork;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkScoreCache;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.TtsSpan;
import android.util.ArraySet;
import android.util.Log;
import com.android.internal.annotations.VisibleForTesting;
import com.android.settingslib.utils.ThreadUtils;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import xiaopeng.widget.BuildConfig;
/* loaded from: classes.dex */
public class AccessPoint implements Comparable<AccessPoint> {
    static final AtomicInteger sLastId = new AtomicInteger(0);
    private String bssid;
    AccessPointListener mAccessPointListener;
    private int mCarrierApEapType;
    private String mCarrierName;
    private WifiConfiguration mConfig;
    private final Context mContext;
    private String mFqdn;
    int mId;
    private WifiInfo mInfo;
    private boolean mIsCarrierAp;
    private boolean mIsScoredNetworkMetered;
    private String mKey;
    private NetworkInfo mNetworkInfo;
    private String mProviderFriendlyName;
    private int mRssi;
    private final ArraySet<ScanResult> mScanResults;
    private final Map<String, TimestampedScoredNetwork> mScoredNetworkCache;
    private int mSpeed;
    private Object mTag;
    private int networkId;
    private int pskType;
    private int security;
    private String ssid;

    /* loaded from: classes.dex */
    public interface AccessPointListener {
        void onAccessPointChanged(AccessPoint accessPoint);

        void onLevelChanged(AccessPoint accessPoint);
    }

    public AccessPoint(Context context, WifiConfiguration config) {
        this.mScanResults = new ArraySet<>();
        this.mScoredNetworkCache = new HashMap();
        this.networkId = -1;
        this.pskType = 0;
        this.mRssi = Integer.MIN_VALUE;
        this.mSpeed = 0;
        this.mIsScoredNetworkMetered = false;
        this.mIsCarrierAp = false;
        this.mCarrierApEapType = -1;
        this.mCarrierName = null;
        this.mContext = context;
        loadConfig(config);
        this.mId = sLastId.incrementAndGet();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AccessPoint(Context context, Collection<ScanResult> results) {
        this.mScanResults = new ArraySet<>();
        this.mScoredNetworkCache = new HashMap();
        this.networkId = -1;
        this.pskType = 0;
        this.mRssi = Integer.MIN_VALUE;
        this.mSpeed = 0;
        this.mIsScoredNetworkMetered = false;
        this.mIsCarrierAp = false;
        this.mCarrierApEapType = -1;
        this.mCarrierName = null;
        this.mContext = context;
        if (results.isEmpty()) {
            throw new IllegalArgumentException("Cannot construct with an empty ScanResult list");
        }
        this.mScanResults.addAll(results);
        ScanResult firstResult = results.iterator().next();
        this.ssid = firstResult.SSID;
        this.bssid = firstResult.BSSID;
        this.security = getSecurity(firstResult);
        if (this.security == 2) {
            this.pskType = getPskType(firstResult);
        }
        updateKey();
        updateRssi();
        this.mIsCarrierAp = firstResult.isCarrierAp;
        this.mCarrierApEapType = firstResult.carrierApEapType;
        this.mCarrierName = firstResult.carrierName;
        this.mId = sLastId.incrementAndGet();
    }

    @VisibleForTesting
    void loadConfig(WifiConfiguration config) {
        this.ssid = config.SSID == null ? BuildConfig.FLAVOR : removeDoubleQuotes(config.SSID);
        this.bssid = config.BSSID;
        this.security = getSecurity(config);
        updateKey();
        this.networkId = config.networkId;
        this.mConfig = config;
    }

    private void updateKey() {
        StringBuilder builder = new StringBuilder();
        if (TextUtils.isEmpty(getSsidStr())) {
            builder.append(getBssid());
        } else {
            builder.append(getSsidStr());
        }
        builder.append(',');
        builder.append(getSecurity());
        this.mKey = builder.toString();
    }

    @Override // java.lang.Comparable
    public int compareTo(AccessPoint other) {
        if (!isActive() || other.isActive()) {
            if (isActive() || !other.isActive()) {
                if (!isReachable() || other.isReachable()) {
                    if (isReachable() || !other.isReachable()) {
                        if (!isSaved() || other.isSaved()) {
                            if (isSaved() || !other.isSaved()) {
                                if (getSpeed() != other.getSpeed()) {
                                    return other.getSpeed() - getSpeed();
                                }
                                int difference = WifiManager.calculateSignalLevel(other.mRssi, 5) - WifiManager.calculateSignalLevel(this.mRssi, 5);
                                if (difference != 0) {
                                    return difference;
                                }
                                int difference2 = getSsidStr().compareToIgnoreCase(other.getSsidStr());
                                if (difference2 != 0) {
                                    return difference2;
                                }
                                return getSsidStr().compareTo(other.getSsidStr());
                            }
                            return 1;
                        }
                        return -1;
                    }
                    return 1;
                }
                return -1;
            }
            return 1;
        }
        return -1;
    }

    public boolean equals(Object other) {
        return (other instanceof AccessPoint) && compareTo((AccessPoint) other) == 0;
    }

    public int hashCode() {
        int result = this.mInfo != null ? 0 + (13 * this.mInfo.hashCode()) : 0;
        return result + (19 * this.mRssi) + (23 * this.networkId) + (29 * this.ssid.hashCode());
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("AccessPoint(");
        StringBuilder builder = sb.append(this.ssid);
        if (this.bssid != null) {
            builder.append(":");
            builder.append(this.bssid);
        }
        if (isSaved()) {
            builder.append(',');
            builder.append("saved");
        }
        if (isActive()) {
            builder.append(',');
            builder.append("active");
        }
        if (isEphemeral()) {
            builder.append(',');
            builder.append("ephemeral");
        }
        if (isConnectable()) {
            builder.append(',');
            builder.append("connectable");
        }
        if (this.security != 0) {
            builder.append(',');
            builder.append(securityToString(this.security, this.pskType));
        }
        builder.append(",level=");
        builder.append(getLevel());
        if (this.mSpeed != 0) {
            builder.append(",speed=");
            builder.append(this.mSpeed);
        }
        builder.append(",metered=");
        builder.append(isMetered());
        if (isVerboseLoggingEnabled()) {
            builder.append(",rssi=");
            builder.append(this.mRssi);
            builder.append(",scan cache size=");
            builder.append(this.mScanResults.size());
        }
        builder.append(')');
        return builder.toString();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean update(WifiNetworkScoreCache scoreCache, boolean scoringUiEnabled, long maxScoreCacheAgeMillis) {
        boolean scoreChanged = false;
        if (scoringUiEnabled) {
            scoreChanged = updateScores(scoreCache, maxScoreCacheAgeMillis);
        }
        return updateMetered(scoreCache) || scoreChanged;
    }

    private boolean updateScores(WifiNetworkScoreCache scoreCache, long maxScoreCacheAgeMillis) {
        long nowMillis = SystemClock.elapsedRealtime();
        Iterator<ScanResult> it = this.mScanResults.iterator();
        while (it.hasNext()) {
            ScanResult result = it.next();
            ScoredNetwork score = scoreCache.getScoredNetwork(result);
            if (score != null) {
                TimestampedScoredNetwork timedScore = this.mScoredNetworkCache.get(result.BSSID);
                if (timedScore == null) {
                    this.mScoredNetworkCache.put(result.BSSID, new TimestampedScoredNetwork(score, nowMillis));
                } else {
                    timedScore.update(score, nowMillis);
                }
            }
        }
        final long evictionCutoff = nowMillis - maxScoreCacheAgeMillis;
        final Iterator<TimestampedScoredNetwork> iterator = this.mScoredNetworkCache.values().iterator();
        iterator.forEachRemaining(new Consumer() { // from class: com.android.settingslib.wifi.-$$Lambda$AccessPoint$OIXfUc7y1PqI_zmQ3STe_086YzY
            @Override // java.util.function.Consumer
            public final void accept(Object obj) {
                AccessPoint.lambda$updateScores$0(evictionCutoff, iterator, (TimestampedScoredNetwork) obj);
            }
        });
        return updateSpeed();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static /* synthetic */ void lambda$updateScores$0(long evictionCutoff, Iterator iterator, TimestampedScoredNetwork timestampedScoredNetwork) {
        if (timestampedScoredNetwork.getUpdatedTimestampMillis() < evictionCutoff) {
            iterator.remove();
        }
    }

    private boolean updateSpeed() {
        int oldSpeed = this.mSpeed;
        this.mSpeed = generateAverageSpeedForSsid();
        boolean changed = oldSpeed != this.mSpeed;
        if (isVerboseLoggingEnabled() && changed) {
            Log.i("SettingsLib.AccessPoint", String.format("%s: Set speed to %d", this.ssid, Integer.valueOf(this.mSpeed)));
        }
        return changed;
    }

    private int generateAverageSpeedForSsid() {
        if (this.mScoredNetworkCache.isEmpty()) {
            return 0;
        }
        if (Log.isLoggable("SettingsLib.AccessPoint", 3)) {
            Log.d("SettingsLib.AccessPoint", String.format("Generating fallbackspeed for %s using cache: %s", getSsidStr(), this.mScoredNetworkCache));
        }
        int count = 0;
        int totalSpeed = 0;
        for (TimestampedScoredNetwork timedScore : this.mScoredNetworkCache.values()) {
            int speed = timedScore.getScore().calculateBadge(this.mRssi);
            if (speed != 0) {
                count++;
                totalSpeed += speed;
            }
        }
        int speed2 = count == 0 ? 0 : totalSpeed / count;
        if (isVerboseLoggingEnabled()) {
            Log.i("SettingsLib.AccessPoint", String.format("%s generated fallback speed is: %d", getSsidStr(), Integer.valueOf(speed2)));
        }
        return roundToClosestSpeedEnum(speed2);
    }

    private boolean updateMetered(WifiNetworkScoreCache scoreCache) {
        boolean oldMetering = this.mIsScoredNetworkMetered;
        this.mIsScoredNetworkMetered = false;
        if (isActive() && this.mInfo != null) {
            NetworkKey key = NetworkKey.createFromWifiInfo(this.mInfo);
            ScoredNetwork score = scoreCache.getScoredNetwork(key);
            if (score != null) {
                this.mIsScoredNetworkMetered |= score.meteredHint;
            }
        } else {
            Iterator<ScanResult> it = this.mScanResults.iterator();
            while (it.hasNext()) {
                ScanResult result = it.next();
                ScoredNetwork score2 = scoreCache.getScoredNetwork(result);
                if (score2 != null) {
                    this.mIsScoredNetworkMetered |= score2.meteredHint;
                }
            }
        }
        return oldMetering == this.mIsScoredNetworkMetered;
    }

    public static String getKey(ScanResult result) {
        StringBuilder builder = new StringBuilder();
        if (TextUtils.isEmpty(result.SSID)) {
            builder.append(result.BSSID);
        } else {
            builder.append(result.SSID);
        }
        builder.append(',');
        builder.append(getSecurity(result));
        return builder.toString();
    }

    public static String getKey(WifiConfiguration config) {
        StringBuilder builder = new StringBuilder();
        if (TextUtils.isEmpty(config.SSID)) {
            builder.append(config.BSSID);
        } else {
            builder.append(removeDoubleQuotes(config.SSID));
        }
        builder.append(',');
        builder.append(getSecurity(config));
        return builder.toString();
    }

    public String getKey() {
        return this.mKey;
    }

    public boolean matches(WifiConfiguration config) {
        if (config.isPasspoint() && this.mConfig != null && this.mConfig.isPasspoint()) {
            return this.ssid.equals(removeDoubleQuotes(config.SSID)) && config.FQDN.equals(this.mConfig.FQDN);
        } else if (this.ssid.equals(removeDoubleQuotes(config.SSID)) && this.security == getSecurity(config)) {
            return this.mConfig == null || this.mConfig.shared == config.shared;
        } else {
            return false;
        }
    }

    public int getLevel() {
        return WifiManager.calculateSignalLevel(this.mRssi, 5);
    }

    private void updateRssi() {
        if (isActive()) {
            return;
        }
        int rssi = Integer.MIN_VALUE;
        Iterator<ScanResult> it = this.mScanResults.iterator();
        while (it.hasNext()) {
            ScanResult result = it.next();
            if (result.level > rssi) {
                rssi = result.level;
            }
        }
        if (rssi != Integer.MIN_VALUE && this.mRssi != Integer.MIN_VALUE) {
            this.mRssi = (this.mRssi + rssi) / 2;
        } else {
            this.mRssi = rssi;
        }
    }

    public boolean isMetered() {
        return this.mIsScoredNetworkMetered || WifiConfiguration.isMetered(this.mConfig, this.mInfo);
    }

    public int getSecurity() {
        return this.security;
    }

    public String getSsidStr() {
        return this.ssid;
    }

    public String getBssid() {
        return this.bssid;
    }

    public CharSequence getSsid() {
        SpannableString str = new SpannableString(this.ssid);
        str.setSpan(new TtsSpan.TelephoneBuilder(this.ssid).build(), 0, this.ssid.length(), 18);
        return str;
    }

    public String getConfigName() {
        if (this.mConfig != null && this.mConfig.isPasspoint()) {
            return this.mConfig.providerFriendlyName;
        }
        if (this.mFqdn != null) {
            return this.mProviderFriendlyName;
        }
        return this.ssid;
    }

    public NetworkInfo.DetailedState getDetailedState() {
        if (this.mNetworkInfo != null) {
            return this.mNetworkInfo.getDetailedState();
        }
        Log.w("SettingsLib.AccessPoint", "NetworkInfo is null, cannot return detailed state");
        return null;
    }

    public boolean isActive() {
        return (this.mNetworkInfo == null || (this.networkId == -1 && this.mNetworkInfo.getState() == NetworkInfo.State.DISCONNECTED)) ? false : true;
    }

    public boolean isConnectable() {
        return getLevel() != -1 && getDetailedState() == null;
    }

    public boolean isEphemeral() {
        return (this.mInfo == null || !this.mInfo.isEphemeral() || this.mNetworkInfo == null || this.mNetworkInfo.getState() == NetworkInfo.State.DISCONNECTED) ? false : true;
    }

    public boolean isPasspoint() {
        return this.mConfig != null && this.mConfig.isPasspoint();
    }

    private boolean isInfoForThisAccessPoint(WifiConfiguration config, WifiInfo info) {
        if (!isPasspoint() && this.networkId != -1) {
            return this.networkId == info.getNetworkId();
        } else if (config != null) {
            return matches(config);
        } else {
            return this.ssid.equals(removeDoubleQuotes(info.getSSID()));
        }
    }

    public boolean isSaved() {
        return this.networkId != -1;
    }

    public void setTag(Object tag) {
        this.mTag = tag;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setScanResults(Collection<ScanResult> scanResults) {
        String key = getKey();
        for (ScanResult result : scanResults) {
            String scanResultKey = getKey(result);
            if (!this.mKey.equals(scanResultKey)) {
                throw new IllegalArgumentException(String.format("ScanResult %s\nkey of %s did not match current AP key %s", result, scanResultKey, key));
            }
        }
        int oldLevel = getLevel();
        this.mScanResults.clear();
        this.mScanResults.addAll(scanResults);
        updateRssi();
        int newLevel = getLevel();
        if (newLevel > 0 && newLevel != oldLevel) {
            updateSpeed();
            ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settingslib.wifi.-$$Lambda$AccessPoint$MkkIS1nUbezHicDMmYnviyiBJyo
                @Override // java.lang.Runnable
                public final void run() {
                    AccessPoint.lambda$setScanResults$1(AccessPoint.this);
                }
            });
        }
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settingslib.wifi.-$$Lambda$AccessPoint$0Yq14aFJZLjPMzFGAvglLaxsblI
            @Override // java.lang.Runnable
            public final void run() {
                AccessPoint.lambda$setScanResults$2(AccessPoint.this);
            }
        });
        if (!scanResults.isEmpty()) {
            ScanResult result2 = scanResults.iterator().next();
            if (this.security == 2) {
                this.pskType = getPskType(result2);
            }
            this.mIsCarrierAp = result2.isCarrierAp;
            this.mCarrierApEapType = result2.carrierApEapType;
            this.mCarrierName = result2.carrierName;
        }
    }

    public static /* synthetic */ void lambda$setScanResults$1(AccessPoint accessPoint) {
        if (accessPoint.mAccessPointListener != null) {
            accessPoint.mAccessPointListener.onLevelChanged(accessPoint);
        }
    }

    public static /* synthetic */ void lambda$setScanResults$2(AccessPoint accessPoint) {
        if (accessPoint.mAccessPointListener != null) {
            accessPoint.mAccessPointListener.onAccessPointChanged(accessPoint);
        }
    }

    public boolean update(WifiConfiguration config, WifiInfo info, NetworkInfo networkInfo) {
        boolean updated = false;
        int oldLevel = getLevel();
        if (info != null && isInfoForThisAccessPoint(config, info)) {
            updated = this.mInfo == null;
            if (this.mConfig != config) {
                update(config);
            }
            if (this.mRssi != info.getRssi() && info.getRssi() != -127) {
                this.mRssi = info.getRssi();
                updated = true;
            } else if (this.mNetworkInfo != null && networkInfo != null && this.mNetworkInfo.getDetailedState() != networkInfo.getDetailedState()) {
                updated = true;
            }
            this.mInfo = info;
            this.mNetworkInfo = networkInfo;
        } else if (this.mInfo != null) {
            updated = true;
            this.mInfo = null;
            this.mNetworkInfo = null;
        }
        if (updated && this.mAccessPointListener != null) {
            ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settingslib.wifi.-$$Lambda$AccessPoint$S7H59e_8IxpVPy0V68Oc2-zX-rg
                @Override // java.lang.Runnable
                public final void run() {
                    AccessPoint.lambda$update$3(AccessPoint.this);
                }
            });
            if (oldLevel != getLevel()) {
                ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settingslib.wifi.-$$Lambda$AccessPoint$QW-1Uw0oxoaKqUtEtPO0oPvH5ng
                    @Override // java.lang.Runnable
                    public final void run() {
                        AccessPoint.lambda$update$4(AccessPoint.this);
                    }
                });
            }
        }
        return updated;
    }

    public static /* synthetic */ void lambda$update$3(AccessPoint accessPoint) {
        if (accessPoint.mAccessPointListener != null) {
            accessPoint.mAccessPointListener.onAccessPointChanged(accessPoint);
        }
    }

    public static /* synthetic */ void lambda$update$4(AccessPoint accessPoint) {
        if (accessPoint.mAccessPointListener != null) {
            accessPoint.mAccessPointListener.onLevelChanged(accessPoint);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void update(WifiConfiguration config) {
        this.mConfig = config;
        this.networkId = config != null ? config.networkId : -1;
        ThreadUtils.postOnMainThread(new Runnable() { // from class: com.android.settingslib.wifi.-$$Lambda$AccessPoint$QyP0aXhFuWtm7lmBu1IY3qbfmBA
            @Override // java.lang.Runnable
            public final void run() {
                AccessPoint.lambda$update$5(AccessPoint.this);
            }
        });
    }

    public static /* synthetic */ void lambda$update$5(AccessPoint accessPoint) {
        if (accessPoint.mAccessPointListener != null) {
            accessPoint.mAccessPointListener.onAccessPointChanged(accessPoint);
        }
    }

    @VisibleForTesting
    void setRssi(int rssi) {
        this.mRssi = rssi;
    }

    int getSpeed() {
        return this.mSpeed;
    }

    private static int roundToClosestSpeedEnum(int speed) {
        if (speed < 5) {
            return 0;
        }
        if (speed < 7) {
            return 5;
        }
        if (speed < 15) {
            return 10;
        }
        if (speed < 25) {
            return 20;
        }
        return 30;
    }

    public boolean isReachable() {
        return this.mRssi != Integer.MIN_VALUE;
    }

    private static int getPskType(ScanResult result) {
        boolean wpa = result.capabilities.contains("WPA-PSK");
        boolean wpa2 = result.capabilities.contains("WPA2-PSK");
        if (wpa2 && wpa) {
            return 3;
        }
        if (wpa2) {
            return 2;
        }
        if (wpa) {
            return 1;
        }
        Log.w("SettingsLib.AccessPoint", "Received abnormal flag string: " + result.capabilities);
        return 0;
    }

    private static int getSecurity(ScanResult result) {
        if (result.capabilities.contains("DPP")) {
            return 6;
        }
        if (result.capabilities.contains("SAE")) {
            return 5;
        }
        if (result.capabilities.contains("OWE")) {
            return 4;
        }
        if (result.capabilities.contains("WEP")) {
            return 1;
        }
        if (result.capabilities.contains("PSK")) {
            return 2;
        }
        if (result.capabilities.contains("EAP")) {
            return 3;
        }
        return 0;
    }

    static int getSecurity(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(1)) {
            return 2;
        }
        if (config.allowedKeyManagement.get(2) || config.allowedKeyManagement.get(3)) {
            return 3;
        }
        if (config.allowedKeyManagement.get(10)) {
            return 6;
        }
        if (config.allowedKeyManagement.get(11)) {
            return 5;
        }
        if (config.allowedKeyManagement.get(12)) {
            return 4;
        }
        return config.wepKeys[0] != null ? 1 : 0;
    }

    public static String securityToString(int security, int pskType) {
        if (security == 1) {
            return "WEP";
        }
        if (security == 2) {
            if (pskType == 1) {
                return "WPA";
            }
            if (pskType == 2) {
                return "WPA2";
            }
            if (pskType == 3) {
                return "WPA_WPA2";
            }
            return "PSK";
        } else if (security == 3) {
            return "EAP";
        } else {
            if (security == 6) {
                return "DPP";
            }
            if (security == 5) {
                return "SAE";
            }
            if (security == 4) {
                return "OWE";
            }
            return "NONE";
        }
    }

    static String removeDoubleQuotes(String string) {
        if (TextUtils.isEmpty(string)) {
            return BuildConfig.FLAVOR;
        }
        int length = string.length();
        if (length > 1 && string.charAt(0) == '\"' && string.charAt(length - 1) == '\"') {
            return string.substring(1, length - 1);
        }
        return string;
    }

    private static boolean isVerboseLoggingEnabled() {
        return WifiTracker.sVerboseLogging || Log.isLoggable("SettingsLib.AccessPoint", 2);
    }
}
