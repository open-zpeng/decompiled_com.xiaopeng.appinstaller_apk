package com.xiaopeng.appinstaller.permission.ui;

import android.app.KeyguardManager;
import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageParser;
import android.content.pm.PermissionInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.text.Html;
import android.text.Spanned;
import android.util.ArraySet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import com.android.internal.content.PackageMonitor;
import com.xiaopeng.appinstaller.R;
import com.xiaopeng.appinstaller.permission.model.AppPermissionGroup;
import com.xiaopeng.appinstaller.permission.model.AppPermissions;
import com.xiaopeng.appinstaller.permission.model.Permission;
import com.xiaopeng.appinstaller.permission.ui.GrantPermissionsViewHandler;
import com.xiaopeng.appinstaller.permission.ui.handheld.GrantPermissionsViewHandlerImpl;
import com.xiaopeng.appinstaller.permission.utils.ArrayUtils;
import com.xiaopeng.appinstaller.permission.utils.EventLogger;
import com.xiaopeng.appinstaller.permission.utils.SafetyNetLogger;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.app.XToast;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
/* loaded from: classes.dex */
public class GrantPermissionsActivity extends OverlayTouchActivity implements GrantPermissionsViewHandler.ResultListener {
    private AppPermissions mAppPermissions;
    private String mCallingPackage;
    private int[] mGrantResults;
    private PackageMonitor mPackageMonitor;
    private PackageManager.OnPermissionsChangedListener mPermissionChangeListener;
    private String[] mRequestedPermissions;
    boolean mResultSet;
    private GrantPermissionsViewHandler mViewHandler;
    private LinkedHashMap<String, GroupState> mRequestGrantPermissionGroups = new LinkedHashMap<>();
    private Handler mHandler = new Handler();
    private final Runnable mFinishTimeoutRunnable = new Runnable() { // from class: com.xiaopeng.appinstaller.permission.ui.GrantPermissionsActivity.1
        @Override // java.lang.Runnable
        public void run() {
            GrantPermissionsActivity.this.handleFinishTimeout();
        }
    };

    private int getPermissionPolicy() {
        DevicePolicyManager devicePolicyManager = (DevicePolicyManager) getSystemService(DevicePolicyManager.class);
        return devicePolicyManager.getPermissionPolicy(null);
    }

    /* JADX WARN: Multi-variable type inference failed */
    /* JADX WARN: Type inference failed for: r11v2, types: [int] */
    /* JADX WARN: Type inference failed for: r14v0 */
    /* JADX WARN: Type inference failed for: r14v1, types: [int] */
    /* JADX WARN: Type inference failed for: r4v3, types: [int] */
    @Override // com.xiaopeng.appinstaller.permission.ui.OverlayTouchActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onCreate(Bundle icicle) {
        AppPermissionGroup group;
        boolean z;
        requestWindowFeature(14);
        super.onCreate(icicle);
        this.mCallingPackage = getCallingPackage();
        this.mPackageMonitor = new PackageMonitor() { // from class: com.xiaopeng.appinstaller.permission.ui.GrantPermissionsActivity.2
            public void onPackageRemoved(String packageName, int uid) {
                if (GrantPermissionsActivity.this.mCallingPackage.equals(packageName)) {
                    Log.w("GrantPermissionsActivity", GrantPermissionsActivity.this.mCallingPackage + " was uninstalled");
                    GrantPermissionsActivity.this.finish();
                }
            }
        };
        setFinishOnTouchOutside(true);
        getWindow().addFlags(524288);
        getWindow().addFlags(2097152);
        setTitle(R.string.permission_request_title);
        this.mViewHandler = new GrantPermissionsViewHandlerImpl(this, this.mCallingPackage).setResultListener(this);
        this.mRequestedPermissions = getIntent().getStringArrayExtra("android.content.pm.extra.REQUEST_PERMISSIONS_NAMES");
        boolean z2 = false;
        if (this.mRequestedPermissions == null) {
            this.mRequestedPermissions = new String[0];
        }
        int requestedPermCount = this.mRequestedPermissions.length;
        this.mGrantResults = new int[requestedPermCount];
        Arrays.fill(this.mGrantResults, -1);
        if (requestedPermCount == 0) {
            setResultAndFinish();
            return;
        }
        try {
            this.mPermissionChangeListener = new PermissionChangeListener();
            PackageInfo callingPackageInfo = getCallingPackageInfo();
            if (callingPackageInfo == null || callingPackageInfo.requestedPermissions == null || callingPackageInfo.requestedPermissions.length <= 0) {
                setResultAndFinish();
            } else if (callingPackageInfo.applicationInfo.targetSdkVersion < 23) {
                this.mRequestedPermissions = new String[0];
                this.mGrantResults = new int[0];
                setResultAndFinish();
            } else {
                updateAlreadyGrantedPermissions(callingPackageInfo, getPermissionPolicy());
                this.mAppPermissions = new AppPermissions(this, callingPackageInfo, null, false, new Runnable() { // from class: com.xiaopeng.appinstaller.permission.ui.GrantPermissionsActivity.3
                    @Override // java.lang.Runnable
                    public void run() {
                        GrantPermissionsActivity.this.setResultAndFinish();
                    }
                });
                PackageManager pm = getPackageManager();
                String[] strArr = this.mRequestedPermissions;
                int length = strArr.length;
                int i = 0;
                while (i < length) {
                    String requestedPermission = strArr[i];
                    if (requestedPermission != null) {
                        int pmResult = pm.checkPermission(requestedPermission, this.mCallingPackage);
                        Log.i("GrantPermissionsActivity", "pm Result:" + pmResult + " requestedPermission:" + requestedPermission + " mCallingPackage:" + this.mCallingPackage);
                        if (pmResult == 2) {
                            Log.i("GrantPermissionsActivity", "pm PERMISSION_DENIED_AND_STOP_CHECK");
                            Xui.init(getApplication());
                            XToast.show((int) R.string.toast_for_function_cannot_use);
                            setResultAndFinish();
                            return;
                        }
                        Iterator<AppPermissionGroup> it = this.mAppPermissions.getPermissionGroups().iterator();
                        while (true) {
                            if (it.hasNext()) {
                                AppPermissionGroup nextGroup = it.next();
                                group = nextGroup.hasPermission(requestedPermission) ? nextGroup : null;
                            }
                        }
                        if (group != null && group.isGrantingAllowed()) {
                            if (!group.isUserFixed() && !group.isPolicyFixed()) {
                                switch (getPermissionPolicy()) {
                                    case 1:
                                        if (!group.areRuntimePermissionsGranted()) {
                                            group.grantRuntimePermissions(z2, computeAffectedPermissions(callingPackageInfo, requestedPermission));
                                        }
                                        group.setPolicyFixed();
                                        break;
                                    case 2:
                                        if (group.areRuntimePermissionsGranted()) {
                                            group.revokeRuntimePermissions(z2, computeAffectedPermissions(callingPackageInfo, requestedPermission));
                                        }
                                        group.setPolicyFixed();
                                        break;
                                    default:
                                        if (group.areRuntimePermissionsGranted()) {
                                            z = false;
                                            group.grantRuntimePermissions(false, computeAffectedPermissions(callingPackageInfo, requestedPermission));
                                            updateGrantResults(group);
                                            break;
                                        } else {
                                            GroupState state = this.mRequestGrantPermissionGroups.get(group.getName());
                                            if (state == null) {
                                                state = new GroupState(group);
                                                this.mRequestGrantPermissionGroups.put(group.getName(), state);
                                            }
                                            String[] affectedPermissions = computeAffectedPermissions(callingPackageInfo, requestedPermission);
                                            if (affectedPermissions != null) {
                                                int length2 = affectedPermissions.length;
                                                for (int i2 = z2; i2 < length2; i2++) {
                                                    String affectedPermission = affectedPermissions[i2];
                                                    state.affectedPermissions = ArrayUtils.appendString(state.affectedPermissions, affectedPermission);
                                                }
                                            }
                                            z = false;
                                            break;
                                        }
                                }
                            } else {
                                z = z2;
                                updateGrantResults(group);
                            }
                            i++;
                            z2 = z;
                        }
                    }
                    z = z2;
                    i++;
                    z2 = z;
                }
                boolean z3 = z2;
                setContentView(this.mViewHandler.getContentView());
                this.mViewHandler.createView();
                if (!showNextPermissionGroupGrantRequest()) {
                    setResultAndFinish();
                } else if (icicle == null) {
                    int numRequestedPermissions = this.mRequestedPermissions.length;
                    while (true) {
                        ?? r4 = z3;
                        if (r4 >= numRequestedPermissions) {
                            return;
                        }
                        String permission = this.mRequestedPermissions[r4];
                        EventLogger.logPermission(1242, permission, this.mAppPermissions.getPackageInfo().packageName);
                        z3 = r4 + 1;
                    }
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            setResultAndFinish();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void updateIfPermissionsWereGranted() {
        updateAlreadyGrantedPermissions(getCallingPackageInfo(), getPermissionPolicy());
        ArraySet<String> grantedPermissionNames = new ArraySet<>(this.mRequestedPermissions.length);
        for (int i = 0; i < this.mRequestedPermissions.length; i++) {
            if (this.mGrantResults[i] == 0) {
                grantedPermissionNames.add(this.mRequestedPermissions[i]);
            }
        }
        int numGroups = this.mAppPermissions.getPermissionGroups().size();
        boolean mightShowNextGroup = true;
        for (int groupNum = 0; groupNum < numGroups; groupNum++) {
            AppPermissionGroup group = this.mAppPermissions.getPermissionGroups().get(groupNum);
            GroupState groupState = this.mRequestGrantPermissionGroups.get(group.getName());
            if (groupState != null && groupState.mState == 0) {
                boolean allAffectedPermissionsOfThisGroupAreGranted = true;
                if (groupState.affectedPermissions == null) {
                    allAffectedPermissionsOfThisGroupAreGranted = false;
                } else {
                    int permNum = 0;
                    while (true) {
                        if (permNum < groupState.affectedPermissions.length) {
                            if (grantedPermissionNames.contains(groupState.affectedPermissions[permNum])) {
                                permNum++;
                            } else {
                                allAffectedPermissionsOfThisGroupAreGranted = false;
                                break;
                            }
                        } else {
                            break;
                        }
                    }
                }
                if (allAffectedPermissionsOfThisGroupAreGranted) {
                    groupState.mState = 1;
                    if (mightShowNextGroup && !showNextPermissionGroupGrantRequest()) {
                        setResultAndFinish();
                    }
                } else {
                    mightShowNextGroup = false;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStart() {
        super.onStart();
        PackageManager pm = getPackageManager();
        pm.addOnPermissionsChangeListener(this.mPermissionChangeListener);
        this.mPackageMonitor.register(this, getMainLooper(), false);
        try {
            pm.getPackageInfo(this.mCallingPackage, 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.w("GrantPermissionsActivity", this.mCallingPackage + " was uninstalled while this activity was stopped", e);
            finish();
        }
        Log.d("GrantPermissionsActivity", "onStart");
        updateIfPermissionsWereGranted();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onResume() {
        super.onResume();
        handleFinishTimeoutTask(false);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(5894);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onPause() {
        Log.i("GrantPermissionsActivity", "onPause start.");
        super.onPause();
        Log.i("GrantPermissionsActivity", "onPause end.");
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onStop() {
        Log.d("GrantPermissionsActivity", "onStop start");
        super.onStop();
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(-1);
        this.mPackageMonitor.unregister();
        getPackageManager().removeOnPermissionsChangeListener(this.mPermissionChangeListener);
        finishAndGrantPermission(true);
        Log.d("GrantPermissionsActivity", "onStop end");
    }

    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity, android.content.ComponentCallbacks
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        View decor = getWindow().getDecorView();
        if (decor.getParent() != null) {
            getWindowManager().removeViewImmediate(decor);
            getWindowManager().addView(decor, decor.getLayoutParams());
            if (this.mViewHandler instanceof GrantPermissionsViewHandlerImpl) {
                ((GrantPermissionsViewHandlerImpl) this.mViewHandler).onConfigurationChanged();
            }
        }
        if (ThemeManager.isThemeChanged(newConfig)) {
            ThemeManager.setWindowBackgroundDrawable(newConfig, getWindow(), getDrawable(R.drawable.x_bg_dialog));
        }
    }

    @Override // android.app.Activity, android.view.Window.Callback
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View rootView = getWindow().getDecorView();
        if (rootView.getTop() != 0) {
            ev.setLocation(ev.getX(), ev.getY() - rootView.getTop());
        }
        return super.dispatchTouchEvent(ev);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        this.mViewHandler.saveInstanceState(outState);
    }

    @Override // android.app.Activity
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.mViewHandler.loadInstanceState(savedInstanceState);
    }

    private boolean showNextPermissionGroupGrantRequest() {
        Resources resources;
        Icon icon;
        Log.i("GrantPermissionsActivity", "showNextPermissionGroupGrantRequest start");
        int groupCount = this.mRequestGrantPermissionGroups.size();
        int currentIndex = 0;
        for (GroupState groupState : this.mRequestGrantPermissionGroups.values()) {
            if (groupState.mState == 0) {
                CharSequence appLabel = this.mAppPermissions.getAppLabel();
                Spanned message = null;
                int requestMessageId = groupState.mGroup.getRequest();
                if (requestMessageId != 0) {
                    try {
                        message = Html.fromHtml(getPackageManager().getResourcesForApplication(groupState.mGroup.getDeclaringPackage()).getString(requestMessageId, appLabel), 0);
                    } catch (PackageManager.NameNotFoundException e) {
                    }
                }
                if (message == null) {
                    message = Html.fromHtml(getString(R.string.permission_warning_template, new Object[]{appLabel, groupState.mGroup.getDescription()}), 0);
                }
                Spanned message2 = message;
                setTitle(message2);
                try {
                    resources = getPackageManager().getResourcesForApplication(groupState.mGroup.getIconPkg());
                } catch (PackageManager.NameNotFoundException e2) {
                    resources = Resources.getSystem();
                }
                try {
                    icon = Icon.createWithResource(resources, groupState.mGroup.getIconResId());
                } catch (Resources.NotFoundException e3) {
                    Log.e("GrantPermissionsActivity", "Cannot load icon for group" + groupState.mGroup.getName(), e3);
                    icon = null;
                }
                Icon icon2 = icon;
                this.mViewHandler.updateUi(groupState.mGroup.getName(), groupCount, currentIndex, icon2, message2, groupState.mGroup.isUserSet());
                Log.i("GrantPermissionsActivity", "showNextPermissionGroupGrantRequest end true");
                return true;
            }
            currentIndex++;
        }
        Log.i("GrantPermissionsActivity", "showNextPermissionGroupGrantRequest end false");
        return false;
    }

    private boolean isOutOfBounds(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int slop = ViewConfiguration.get(this).getScaledWindowTouchSlop();
        View decorView = getWindow().getDecorView();
        return x < (-slop) || y < (-slop) || x > decorView.getWidth() + slop || y > decorView.getHeight() + slop;
    }

    private boolean isTouchOutSide(MotionEvent event) {
        boolean isOutSide = (event.getAction() == 0 && isOutOfBounds(event)) || event.getAction() == 4;
        if (isOutSide) {
            Log.d("GrantPermissionsActivity", "touchOutSide event=" + event);
        }
        View decorView = getWindow().getDecorView();
        return decorView != null && isOutSide;
    }

    @Override // android.app.Activity
    public boolean onTouchEvent(MotionEvent event) {
        if (isTouchOutSide(event)) {
            finish();
            return true;
        }
        return super.onTouchEvent(event);
    }

    @Override // com.xiaopeng.appinstaller.permission.ui.GrantPermissionsViewHandler.ResultListener
    public void onPermissionGrantResult(final String name, final boolean granted, final boolean doNotAskAgain) {
        Log.i("GrantPermissionsActivity", "onPermissionGrantResult name:" + name + " granted:" + granted + ", notAskAgain:" + doNotAskAgain);
        KeyguardManager kgm = (KeyguardManager) getSystemService(KeyguardManager.class);
        if (kgm.isDeviceLocked()) {
            kgm.requestDismissKeyguard(this, new KeyguardManager.KeyguardDismissCallback() { // from class: com.xiaopeng.appinstaller.permission.ui.GrantPermissionsActivity.4
                @Override // android.app.KeyguardManager.KeyguardDismissCallback
                public void onDismissError() {
                    Log.e("GrantPermissionsActivity", "Cannot dismiss keyguard perm=" + name + " granted=" + granted + " doNotAskAgain=" + doNotAskAgain);
                }

                @Override // android.app.KeyguardManager.KeyguardDismissCallback
                public void onDismissCancelled() {
                }

                @Override // android.app.KeyguardManager.KeyguardDismissCallback
                public void onDismissSucceeded() {
                    GrantPermissionsActivity.this.onPermissionGrantResult(name, granted, doNotAskAgain);
                }
            });
            return;
        }
        GroupState groupState = this.mRequestGrantPermissionGroups.get(name);
        if (groupState != null && groupState.mGroup != null) {
            if (granted) {
                groupState.mGroup.grantRuntimePermissions(doNotAskAgain, groupState.affectedPermissions);
                groupState.mState = 1;
            } else {
                groupState.mGroup.revokeRuntimePermissions(doNotAskAgain, groupState.affectedPermissions);
                groupState.mState = 2;
                int numRequestedPermissions = this.mRequestedPermissions.length;
                for (int i = 0; i < numRequestedPermissions; i++) {
                    String permission = this.mRequestedPermissions[i];
                    if (groupState.mGroup.hasPermission(permission)) {
                        EventLogger.logPermission(1244, permission, this.mAppPermissions.getPackageInfo().packageName);
                    }
                }
            }
            updateGrantResults(groupState.mGroup);
        }
        if (!showNextPermissionGroupGrantRequest()) {
            setResultAndFinish();
        }
        Log.i("GrantPermissionsActivity", "onPermissionGrantResult name:" + name + " end");
    }

    private void updateGrantResults(AppPermissionGroup group) {
        for (Permission permission : group.getPermissions()) {
            int index = ArrayUtils.indexOf(this.mRequestedPermissions, permission.getName());
            if (index >= 0) {
                this.mGrantResults[index] = permission.isGranted() ? 0 : -1;
            }
        }
    }

    @Override // android.support.v7.app.AppCompatActivity, android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return keyCode == 4;
    }

    @Override // android.app.Activity, android.view.KeyEvent.Callback
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return keyCode == 4;
    }

    @Override // android.app.Activity
    public void finish() {
        handleFinishTimeoutTask(true);
        setResultIfNeeded(0);
        super.finish();
    }

    private void finishAndGrantPermission(boolean granted) {
        Log.d("GrantPermissionsActivity", "finishAndGrantPermission start");
        for (GroupState groupState : this.mRequestGrantPermissionGroups.values()) {
            if (groupState != null && groupState.mGroup != null && groupState.mState == 0) {
                if (granted) {
                    groupState.mGroup.grantRuntimePermissions(false, groupState.affectedPermissions);
                    groupState.mState = 1;
                } else {
                    groupState.mGroup.revokeRuntimePermissions(false, groupState.affectedPermissions);
                    groupState.mState = 2;
                    int numRequestedPermissions = this.mRequestedPermissions.length;
                    for (int i = 0; i < numRequestedPermissions; i++) {
                        String permission = this.mRequestedPermissions[i];
                        if (groupState.mGroup.hasPermission(permission)) {
                            EventLogger.logPermission(1244, permission, this.mAppPermissions.getPackageInfo().packageName);
                        }
                    }
                }
                updateGrantResults(groupState.mGroup);
            }
        }
        finish();
        Log.d("GrantPermissionsActivity", "finishAndGrantPermission end");
    }

    private int computePermissionGrantState(PackageInfo callingPackageInfo, String permission, int permissionPolicy) {
        boolean permissionRequested = false;
        int i = 0;
        while (true) {
            if (i >= callingPackageInfo.requestedPermissions.length) {
                break;
            } else if (!permission.equals(callingPackageInfo.requestedPermissions[i])) {
                i++;
            } else {
                permissionRequested = true;
                if ((callingPackageInfo.requestedPermissionsFlags[i] & 2) != 0) {
                    return 0;
                }
            }
        }
        if (permissionRequested) {
            try {
                PermissionInfo pInfo = getPackageManager().getPermissionInfo(permission, 0);
                if ((pInfo.protectionLevel & 15) != 1) {
                    return -1;
                }
                if ((pInfo.protectionLevel & 4096) == 0 && callingPackageInfo.applicationInfo.isInstantApp()) {
                    return -1;
                }
                if ((pInfo.protectionLevel & 8192) != 0) {
                    if (callingPackageInfo.applicationInfo.targetSdkVersion < 23) {
                        return -1;
                    }
                }
                if (permissionPolicy == 1) {
                    return 0;
                }
                return -1;
            } catch (PackageManager.NameNotFoundException e) {
                return -1;
            }
        }
        return -1;
    }

    private PackageInfo getCallingPackageInfo() {
        try {
            return getPackageManager().getPackageInfo(this.mCallingPackage, 4096);
        } catch (PackageManager.NameNotFoundException e) {
            Log.i("GrantPermissionsActivity", "No package: " + this.mCallingPackage, e);
            return null;
        }
    }

    private void updateAlreadyGrantedPermissions(PackageInfo callingPackageInfo, int permissionPolicy) {
        int requestedPermCount = this.mRequestedPermissions.length;
        for (int i = 0; i < requestedPermCount; i++) {
            String permission = this.mRequestedPermissions[i];
            if (permission != null && computePermissionGrantState(callingPackageInfo, permission, permissionPolicy) == 0) {
                this.mGrantResults[i] = 0;
            }
        }
    }

    private void setResultIfNeeded(int resultCode) {
        if (!this.mResultSet) {
            this.mResultSet = true;
            logRequestedPermissionGroups();
            Intent result = new Intent("android.content.pm.action.REQUEST_PERMISSIONS");
            result.putExtra("android.content.pm.extra.REQUEST_PERMISSIONS_NAMES", this.mRequestedPermissions);
            result.putExtra("android.content.pm.extra.REQUEST_PERMISSIONS_RESULTS", this.mGrantResults);
            setResult(resultCode, result);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void setResultAndFinish() {
        setResultIfNeeded(-1);
        finish();
    }

    private void logRequestedPermissionGroups() {
        if (this.mRequestGrantPermissionGroups.isEmpty()) {
            return;
        }
        int groupCount = this.mRequestGrantPermissionGroups.size();
        List<AppPermissionGroup> groups = new ArrayList<>(groupCount);
        for (GroupState groupState : this.mRequestGrantPermissionGroups.values()) {
            groups.add(groupState.mGroup);
        }
        SafetyNetLogger.logPermissionsRequested(this.mAppPermissions.getPackageInfo(), groups);
    }

    private static String[] computeAffectedPermissions(PackageInfo callingPkg, String permission) {
        PackageParser.SplitPermissionInfo[] splitPermissionInfoArr;
        if (callingPkg.applicationInfo.targetSdkVersion <= 25) {
            return null;
        }
        String[] permissions = {permission};
        String[] permissions2 = permissions;
        for (PackageParser.SplitPermissionInfo splitPerm : PackageParser.SPLIT_PERMISSIONS) {
            if (splitPerm.targetSdk > 25 && callingPkg.applicationInfo.targetSdkVersion < splitPerm.targetSdk && permission.equals(splitPerm.rootPerm)) {
                String[] permissions3 = permissions2;
                for (int i = 0; i < splitPerm.newPerms.length; i++) {
                    String newPerm = splitPerm.newPerms[i];
                    permissions3 = ArrayUtils.appendString(permissions3, newPerm);
                }
                permissions2 = permissions3;
            }
        }
        return permissions2;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static final class GroupState {
        String[] affectedPermissions;
        final AppPermissionGroup mGroup;
        int mState = 0;

        GroupState(AppPermissionGroup group) {
            this.mGroup = group;
        }
    }

    /* loaded from: classes.dex */
    private class PermissionChangeListener implements PackageManager.OnPermissionsChangedListener {
        final int mCallingPackageUid;

        PermissionChangeListener() throws PackageManager.NameNotFoundException {
            this.mCallingPackageUid = GrantPermissionsActivity.this.getPackageManager().getPackageUid(GrantPermissionsActivity.this.mCallingPackage, 0);
        }

        public void onPermissionsChanged(int uid) {
            if (uid == this.mCallingPackageUid) {
                GrantPermissionsActivity.this.updateIfPermissionsWereGranted();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.app.Activity
    public void onDestroy() {
        super.onDestroy();
        Log.d("GrantPermissionsActivity", "onDestroy");
        handleFinishTimeoutTask(false);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleFinishTimeout() {
        boolean finishing = isFinishing();
        Log.i("GrantPermissionsActivity", "handleFinishTimeout finishing=" + finishing);
        if (finishing) {
            try {
                Process.killProcess(Process.myPid());
            } catch (Error | Exception e) {
            }
        }
    }

    private void handleFinishTimeoutTask(boolean post) {
        Log.i("GrantPermissionsActivity", "handleFinishTimeoutTask post=" + post);
        this.mHandler.removeCallbacks(this.mFinishTimeoutRunnable);
        if (post) {
            this.mHandler.postDelayed(this.mFinishTimeoutRunnable, 15000L);
        }
    }
}
