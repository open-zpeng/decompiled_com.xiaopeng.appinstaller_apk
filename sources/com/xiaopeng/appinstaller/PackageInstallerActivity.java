package com.xiaopeng.appinstaller;

import android.app.Activity;
import android.app.AppGlobals;
import android.app.AppOpsManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.PackageParser;
import android.content.pm.PackageUserState;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.os.RemoteException;
import android.os.UserManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AppSecurityPermissions;
import android.widget.Button;
import android.widget.TabHost;
import android.widget.TextView;
import com.xiaopeng.appinstaller.PackageInstallerActivity;
import com.xiaopeng.appinstaller.PackageUtil;
import com.xiaopeng.appinstaller.permission.ui.OverlayTouchActivity;
import com.xiaopeng.xui.widget.dialogview.XDialogView;
import com.xiaopeng.xui.widget.dialogview.XDialogViewInterface;
import java.io.File;
import java.util.Set;
/* loaded from: classes.dex */
public class PackageInstallerActivity extends OverlayTouchActivity implements View.OnClickListener {
    private static final String ALLOW_UNKNOWN_SOURCES_KEY = PackageInstallerActivity.class.getName() + "ALLOW_UNKNOWN_SOURCES_KEY";
    private boolean mAllowUnknownSources;
    AppOpsManager mAppOpsManager;
    private PackageUtil.AppSnippet mAppSnippet;
    String mCallingPackage;
    private Button mCancel;
    private boolean mEnableOk;
    PackageInstaller mInstaller;
    IPackageManager mIpm;
    private Button mOk;
    private String mOriginatingPackage;
    private Uri mOriginatingURI;
    private Uri mPackageURI;
    PackageInfo mPkgInfo;
    PackageManager mPm;
    private Uri mReferrerURI;
    ApplicationInfo mSourceInfo;
    UserManager mUserManager;
    private int mSessionId = -1;
    private int mOriginatingUid = -1;
    private boolean localLOGV = false;
    private ApplicationInfo mAppInfo = null;
    CaffeinatedScrollView mScrollView = null;
    private boolean mOkCanInstall = false;

    private void startInstallConfirm() {
        int msg;
        int i;
        if (this.mAppInfo != null) {
            bindUi(R.layout.install_confirm_perm_update, true);
        } else {
            bindUi(R.layout.install_confirm_perm, true);
        }
        ((TextView) findViewById(R.id.install_confirm_question)).setText(R.string.install_confirm_question);
        TabHost tabHost = (TabHost) findViewById(16908306);
        tabHost.setup();
        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        TabsAdapter adapter = new TabsAdapter(this, tabHost, viewPager);
        boolean supportsRuntimePermissions = this.mPkgInfo.applicationInfo.targetSdkVersion >= 23;
        boolean permVisible = false;
        this.mScrollView = null;
        this.mOkCanInstall = false;
        int msg2 = 0;
        AppSecurityPermissions perms = new AppSecurityPermissions(this, this.mPkgInfo);
        int N = perms.getPermissionCount(65535);
        if (this.mAppInfo != null) {
            if ((this.mAppInfo.flags & 1) != 0) {
                i = R.string.install_confirm_question_update_system;
            } else {
                i = R.string.install_confirm_question_update;
            }
            msg2 = i;
            this.mScrollView = new CaffeinatedScrollView(this);
            this.mScrollView.setFillViewport(true);
            boolean newPermissionsFound = false;
            if (!supportsRuntimePermissions) {
                newPermissionsFound = perms.getPermissionCount(4) > 0;
                if (newPermissionsFound) {
                    permVisible = true;
                    this.mScrollView.addView(perms.getPermissionsView(4));
                }
            }
            if (!supportsRuntimePermissions && !newPermissionsFound) {
                LayoutInflater inflater = (LayoutInflater) getSystemService("layout_inflater");
                TextView label = (TextView) inflater.inflate(R.layout.label, (ViewGroup) null);
                label.setText(R.string.no_new_perms);
                this.mScrollView.addView(label);
            }
            adapter.addTab(tabHost.newTabSpec("new").setIndicator(getText(R.string.newPerms)), this.mScrollView);
        }
        if (!supportsRuntimePermissions && N > 0) {
            permVisible = true;
            LayoutInflater inflater2 = (LayoutInflater) getSystemService("layout_inflater");
            View root = inflater2.inflate(R.layout.permissions_list, (ViewGroup) null);
            if (this.mScrollView == null) {
                this.mScrollView = (CaffeinatedScrollView) root.findViewById(R.id.scrollview);
            }
            ((ViewGroup) root.findViewById(R.id.permission_list)).addView(perms.getPermissionsView(65535));
            adapter.addTab(tabHost.newTabSpec("all").setIndicator(getText(R.string.allPerms)), root);
        }
        if (!permVisible) {
            if (this.mAppInfo != null) {
                if ((this.mAppInfo.flags & 1) != 0) {
                    msg = R.string.install_confirm_question_update_system_no_perms;
                } else {
                    msg = R.string.install_confirm_question_update_no_perms;
                }
            } else {
                msg = R.string.install_confirm_question_no_perms;
            }
            msg2 = msg;
            bindUi(R.layout.install_confirm, true);
            this.mScrollView = null;
        }
        if (msg2 != 0) {
            ((TextView) findViewById(R.id.install_confirm_question)).setText(msg2);
        }
        if (this.mScrollView != null) {
            this.mScrollView.setFullScrollAction(new Runnable() { // from class: com.xiaopeng.appinstaller.PackageInstallerActivity.1
                @Override // java.lang.Runnable
                public void run() {
                    PackageInstallerActivity.this.mOk.setText(R.string.install);
                    PackageInstallerActivity.this.mOkCanInstall = true;
                }
            });
            return;
        }
        this.mOk.setText(R.string.install);
        this.mOkCanInstall = true;
    }

    private void showDialogInner(int id) {
        DialogFragment currentDialog = (DialogFragment) getFragmentManager().findFragmentByTag("dialog");
        if (currentDialog != null) {
            currentDialog.dismissAllowingStateLoss();
        }
        DialogFragment newDialog = createDialog(id);
        if (newDialog != null) {
            newDialog.showAllowingStateLoss(getFragmentManager(), "dialog");
        }
    }

    private DialogFragment createDialog(int id) {
        switch (id) {
            case 2:
                return SimpleErrorDialog.newInstance(R.string.Parse_error_dlg_text);
            case 3:
                return OutOfSpaceDialog.newInstance(this.mPm.getApplicationLabel(this.mPkgInfo.applicationInfo));
            case 4:
                return InstallErrorDialog.newInstance(this.mPm.getApplicationLabel(this.mPkgInfo.applicationInfo));
            case 5:
                return SimpleErrorDialog.newInstance(R.string.unknown_apps_user_restriction_dlg_text);
            case 6:
                return AnonymousSourceDialog.newInstance();
            case 7:
                return NotSupportedOnWearDialog.newInstance();
            case 8:
                return XpExternalSourcesBlockedDialog.newInstance(this.mOriginatingPackage);
            case 9:
                return SimpleErrorDialog.newInstance(R.string.install_apps_user_restriction_dlg_text);
            default:
                return null;
        }
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onActivityResult(int request, int result, Intent data) {
        if (request == 1 && result == -1) {
            this.mAllowUnknownSources = true;
            int appOpCode = AppOpsManager.permissionToOpCode("android.permission.REQUEST_INSTALL_PACKAGES");
            this.mAppOpsManager.noteOpNoThrow(appOpCode, this.mOriginatingUid, this.mOriginatingPackage);
            DialogFragment currentDialog = (DialogFragment) getFragmentManager().findFragmentByTag("dialog");
            if (currentDialog != null) {
                currentDialog.dismissAllowingStateLoss();
            }
            initiateInstall();
            return;
        }
        finish();
    }

    private String getPackageNameForUid(int sourceUid) {
        String[] packagesForUid = this.mPm.getPackagesForUid(sourceUid);
        if (packagesForUid == null) {
            return null;
        }
        if (packagesForUid.length > 1) {
            if (this.mCallingPackage != null) {
                for (String packageName : packagesForUid) {
                    if (packageName.equals(this.mCallingPackage)) {
                        return packageName;
                    }
                }
            }
            Log.i("PackageInstallerActivity", "Multiple packages found for source uid " + sourceUid);
        }
        return packagesForUid[0];
    }

    private boolean isInstallRequestFromUnknownSource(Intent intent) {
        return this.mCallingPackage == null || !intent.getBooleanExtra("android.intent.extra.NOT_UNKNOWN_SOURCE", false) || this.mSourceInfo == null || (this.mSourceInfo.privateFlags & 8) == 0;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void initiateInstall() {
        String pkgName = this.mPkgInfo.packageName;
        String[] oldName = this.mPm.canonicalToCurrentPackageNames(new String[]{pkgName});
        if (oldName != null && oldName.length > 0 && oldName[0] != null) {
            pkgName = oldName[0];
            this.mPkgInfo.packageName = pkgName;
            this.mPkgInfo.applicationInfo.packageName = pkgName;
        }
        try {
            this.mAppInfo = this.mPm.getApplicationInfo(pkgName, 8192);
            if ((this.mAppInfo.flags & 8388608) == 0) {
                this.mAppInfo = null;
            }
        } catch (PackageManager.NameNotFoundException e) {
            this.mAppInfo = null;
        }
        startInstallConfirm();
    }

    void setPmResult(int pmResult) {
        Intent result = new Intent();
        result.putExtra("android.intent.extra.INSTALL_RESULT", pmResult);
        int i = 1;
        if (pmResult == 1) {
            i = -1;
        }
        setResult(i, result);
    }

    @Override // com.xiaopeng.appinstaller.permission.ui.OverlayTouchActivity, android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    protected void onCreate(Bundle icicle) {
        String str;
        Uri packageUri;
        super.onCreate(null);
        if (icicle != null) {
            this.mAllowUnknownSources = icicle.getBoolean(ALLOW_UNKNOWN_SOURCES_KEY);
        }
        this.mPm = getPackageManager();
        this.mIpm = AppGlobals.getPackageManager();
        this.mAppOpsManager = (AppOpsManager) getSystemService("appops");
        this.mInstaller = this.mPm.getPackageInstaller();
        this.mUserManager = (UserManager) getSystemService("user");
        Intent intent = getIntent();
        this.mCallingPackage = intent.getStringExtra("EXTRA_CALLING_PACKAGE");
        this.mSourceInfo = (ApplicationInfo) intent.getParcelableExtra("EXTRA_ORIGINAL_SOURCE_INFO");
        this.mOriginatingUid = intent.getIntExtra("android.intent.extra.ORIGINATING_UID", -1);
        if (this.mOriginatingUid == -1) {
            str = null;
        } else {
            str = getPackageNameForUid(this.mOriginatingUid);
        }
        this.mOriginatingPackage = str;
        if ("android.content.pm.action.CONFIRM_PERMISSIONS".equals(intent.getAction())) {
            int sessionId = intent.getIntExtra("android.content.pm.extra.SESSION_ID", -1);
            PackageInstaller.SessionInfo info = this.mInstaller.getSessionInfo(sessionId);
            if (info == null || !info.sealed || info.resolvedBaseCodePath == null) {
                Log.w("PackageInstallerActivity", "Session " + this.mSessionId + " in funky state; ignoring");
                finish();
                return;
            }
            this.mSessionId = sessionId;
            packageUri = Uri.fromFile(new File(info.resolvedBaseCodePath));
            this.mOriginatingURI = null;
            this.mReferrerURI = null;
        } else {
            this.mSessionId = -1;
            packageUri = intent.getData();
            this.mOriginatingURI = (Uri) intent.getParcelableExtra("android.intent.extra.ORIGINATING_URI");
            this.mReferrerURI = (Uri) intent.getParcelableExtra("android.intent.extra.REFERRER");
        }
        if (packageUri == null) {
            Log.w("PackageInstallerActivity", "Unspecified source");
            setPmResult(-3);
            finish();
            return;
        }
        boolean wasSetUp = processPackageUri(packageUri);
        if (!wasSetUp) {
            return;
        }
        bindUi(R.layout.install_confirm, false);
        checkIfAllowedAndInitiateInstall();
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onResume() {
        super.onResume();
        if (this.mOk != null) {
            this.mOk.setEnabled(this.mEnableOk);
        }
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    protected void onPause() {
        super.onPause();
        if (this.mOk != null) {
            this.mOk.setEnabled(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.support.v7.app.AppCompatActivity, android.support.v4.app.FragmentActivity, android.support.v4.app.SupportActivity, android.app.Activity
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(ALLOW_UNKNOWN_SOURCES_KEY, this.mAllowUnknownSources);
    }

    private void bindUi(int layout, boolean enableOk) {
        setContentView(layout);
        this.mOk = (Button) findViewById(R.id.ok_button);
        this.mCancel = (Button) findViewById(R.id.cancel_button);
        this.mOk.setOnClickListener(this);
        this.mCancel.setOnClickListener(this);
        this.mEnableOk = enableOk;
        this.mOk.setEnabled(enableOk);
        PackageUtil.initSnippetForNewApp(this, this.mAppSnippet, R.id.app_snippet);
    }

    private void checkIfAllowedAndInitiateInstall() {
        int installAppsRestrictionSource = this.mUserManager.getUserRestrictionSource("no_install_apps", Process.myUserHandle());
        if ((installAppsRestrictionSource & 1) != 0) {
            showDialogInner(9);
        } else if (installAppsRestrictionSource != 0) {
            startActivity(new Intent("android.settings.SHOW_ADMIN_SUPPORT_DETAILS"));
            finish();
        } else if (this.mAllowUnknownSources || !isInstallRequestFromUnknownSource(getIntent())) {
            initiateInstall();
        } else {
            int unknownSourcesRestrictionSource = this.mUserManager.getUserRestrictionSource("no_install_unknown_sources", Process.myUserHandle());
            if ((unknownSourcesRestrictionSource & 1) != 0) {
                showDialogInner(5);
            } else if (unknownSourcesRestrictionSource != 0) {
                startActivity(new Intent("android.settings.SHOW_ADMIN_SUPPORT_DETAILS"));
                finish();
            } else {
                handleUnknownSources();
            }
        }
    }

    private void handleUnknownSources() {
        if (this.mOriginatingPackage == null) {
            Log.i("PackageInstallerActivity", "No source found for package " + this.mPkgInfo.packageName);
            showDialogInner(6);
            return;
        }
        int appOpCode = AppOpsManager.permissionToOpCode("android.permission.REQUEST_INSTALL_PACKAGES");
        int appOpMode = this.mAppOpsManager.noteOpNoThrow(appOpCode, this.mOriginatingUid, this.mOriginatingPackage);
        if (appOpMode == 0) {
            initiateInstall();
            return;
        }
        switch (appOpMode) {
            case 2:
                break;
            default:
                Log.e("PackageInstallerActivity", "Invalid app op mode " + appOpMode + " for OP_REQUEST_INSTALL_PACKAGES found for uid " + this.mOriginatingUid);
                finish();
                return;
            case 3:
                try {
                    int result = this.mIpm.checkUidPermission("android.permission.REQUEST_INSTALL_PACKAGES", this.mOriginatingUid);
                    if (result == 0) {
                        initiateInstall();
                        return;
                    }
                } catch (RemoteException e) {
                    Log.e("PackageInstallerActivity", "Unable to talk to package manager");
                }
                this.mAppOpsManager.setMode(appOpCode, this.mOriginatingUid, this.mOriginatingPackage, 2);
                break;
        }
        showDialogInner(8);
    }

    private boolean processPackageUri(Uri packageUri) {
        char c;
        this.mPackageURI = packageUri;
        String scheme = packageUri.getScheme();
        int hashCode = scheme.hashCode();
        if (hashCode != -807062458) {
            if (hashCode == 3143036 && scheme.equals("file")) {
                c = 1;
            }
            c = 65535;
        } else {
            if (scheme.equals("package")) {
                c = 0;
            }
            c = 65535;
        }
        switch (c) {
            case 0:
                try {
                    this.mPkgInfo = this.mPm.getPackageInfo(packageUri.getSchemeSpecificPart(), 12288);
                } catch (PackageManager.NameNotFoundException e) {
                }
                if (this.mPkgInfo == null) {
                    Log.w("PackageInstallerActivity", "Requested package " + packageUri.getScheme() + " not available. Discontinuing installation");
                    showDialogInner(2);
                    setPmResult(-2);
                    return false;
                }
                this.mAppSnippet = new PackageUtil.AppSnippet(this.mPm.getApplicationLabel(this.mPkgInfo.applicationInfo), this.mPm.getApplicationIcon(this.mPkgInfo.applicationInfo));
                break;
            case 1:
                File sourceFile = new File(packageUri.getPath());
                PackageParser.Package parsed = PackageUtil.getPackageInfo(this, sourceFile);
                if (parsed == null) {
                    Log.w("PackageInstallerActivity", "Parse error when parsing manifest. Discontinuing installation");
                    showDialogInner(2);
                    setPmResult(-2);
                    return false;
                }
                this.mPkgInfo = PackageParser.generatePackageInfo(parsed, (int[]) null, 4096, 0L, 0L, (Set) null, new PackageUserState());
                this.mAppSnippet = PackageUtil.getAppSnippet(this, this.mPkgInfo.applicationInfo, sourceFile);
                break;
            default:
                throw new IllegalArgumentException("Unexpected URI scheme " + packageUri);
        }
        return true;
    }

    @Override // android.support.v4.app.FragmentActivity, android.app.Activity
    public void onBackPressed() {
        if (this.mSessionId != -1) {
            this.mInstaller.setPermissionsResult(this.mSessionId, false);
        }
        super.onBackPressed();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        if (v == this.mOk) {
            if (this.mOk.isEnabled()) {
                if (this.mOkCanInstall || this.mScrollView == null) {
                    if (this.mSessionId != -1) {
                        this.mInstaller.setPermissionsResult(this.mSessionId, true);
                        finish();
                        return;
                    }
                    startInstall();
                    return;
                }
                this.mScrollView.pageScroll(130);
            }
        } else if (v == this.mCancel) {
            setResult(0);
            if (this.mSessionId != -1) {
                this.mInstaller.setPermissionsResult(this.mSessionId, false);
            }
            finish();
        }
    }

    private void startInstall() {
        Intent newIntent = new Intent();
        newIntent.putExtra("com.android.packageinstaller.applicationInfo", this.mPkgInfo.applicationInfo);
        newIntent.setData(this.mPackageURI);
        newIntent.setClass(this, InstallInstalling.class);
        String installerPackageName = getIntent().getStringExtra("android.intent.extra.INSTALLER_PACKAGE_NAME");
        if (this.mOriginatingURI != null) {
            newIntent.putExtra("android.intent.extra.ORIGINATING_URI", this.mOriginatingURI);
        }
        if (this.mReferrerURI != null) {
            newIntent.putExtra("android.intent.extra.REFERRER", this.mReferrerURI);
        }
        if (this.mOriginatingUid != -1) {
            newIntent.putExtra("android.intent.extra.ORIGINATING_UID", this.mOriginatingUid);
        }
        if (installerPackageName != null) {
            newIntent.putExtra("android.intent.extra.INSTALLER_PACKAGE_NAME", installerPackageName);
        }
        if (getIntent().getBooleanExtra("android.intent.extra.RETURN_RESULT", false)) {
            newIntent.putExtra("android.intent.extra.RETURN_RESULT", true);
        }
        newIntent.addFlags(33554432);
        Log.i("PackageInstallerActivity", "downloaded app uri=" + this.mPackageURI);
        startActivity(newIntent);
        finish();
    }

    /* loaded from: classes.dex */
    public static class SimpleErrorDialog extends DialogFragment {
        private static final String MESSAGE_KEY = SimpleErrorDialog.class.getName() + "MESSAGE_KEY";

        static SimpleErrorDialog newInstance(int message) {
            SimpleErrorDialog dialog = new SimpleErrorDialog();
            Bundle args = new Bundle();
            args.putInt(MESSAGE_KEY, message);
            dialog.setArguments(args);
            return dialog;
        }

        @Override // android.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity()).setMessage(getArguments().getInt(MESSAGE_KEY)).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { // from class: com.xiaopeng.appinstaller.-$$Lambda$PackageInstallerActivity$SimpleErrorDialog$P6S0xN7gGlb8TLaVRstVcX4pp2w
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PackageInstallerActivity.SimpleErrorDialog.this.getActivity().finish();
                }
            }).create();
        }
    }

    /* loaded from: classes.dex */
    public static class AnonymousSourceDialog extends DialogFragment {
        static AnonymousSourceDialog newInstance() {
            return new AnonymousSourceDialog();
        }

        @Override // android.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity()).setMessage(R.string.anonymous_source_warning).setPositiveButton(R.string.anonymous_source_continue, new DialogInterface.OnClickListener() { // from class: com.xiaopeng.appinstaller.-$$Lambda$PackageInstallerActivity$AnonymousSourceDialog$D2gITxIxJ5ui-vRzS7X6Bo_P_SU
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PackageInstallerActivity.AnonymousSourceDialog.lambda$onCreateDialog$0(PackageInstallerActivity.AnonymousSourceDialog.this, dialogInterface, i);
                }
            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.xiaopeng.appinstaller.-$$Lambda$PackageInstallerActivity$AnonymousSourceDialog$F5fE4H36bsGEJDUqxGBqUEm9OIw
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PackageInstallerActivity.AnonymousSourceDialog.this.getActivity().finish();
                }
            }).create();
        }

        public static /* synthetic */ void lambda$onCreateDialog$0(AnonymousSourceDialog anonymousSourceDialog, DialogInterface dialog, int which) {
            PackageInstallerActivity activity = (PackageInstallerActivity) anonymousSourceDialog.getActivity();
            activity.mAllowUnknownSources = true;
            activity.initiateInstall();
        }

        @Override // android.app.DialogFragment, android.content.DialogInterface.OnCancelListener
        public void onCancel(DialogInterface dialog) {
            getActivity().finish();
        }
    }

    /* loaded from: classes.dex */
    public static class NotSupportedOnWearDialog extends SimpleErrorDialog {
        static SimpleErrorDialog newInstance() {
            return SimpleErrorDialog.newInstance(R.string.wear_not_allowed_dlg_text);
        }

        @Override // android.app.DialogFragment, android.content.DialogInterface.OnCancelListener
        public void onCancel(DialogInterface dialog) {
            getActivity().setResult(-1);
            getActivity().finish();
        }
    }

    /* loaded from: classes.dex */
    public static class OutOfSpaceDialog extends AppErrorDialog {
        static AppErrorDialog newInstance(CharSequence applicationLabel) {
            OutOfSpaceDialog dialog = new OutOfSpaceDialog();
            dialog.setArgument(applicationLabel);
            return dialog;
        }

        @Override // com.xiaopeng.appinstaller.PackageInstallerActivity.AppErrorDialog
        protected Dialog createDialog(CharSequence argument) {
            String dlgText = getString(R.string.out_of_space_dlg_text, new Object[]{argument});
            return new AlertDialog.Builder(getActivity()).setMessage(dlgText).setPositiveButton(R.string.manage_applications, new DialogInterface.OnClickListener() { // from class: com.xiaopeng.appinstaller.-$$Lambda$PackageInstallerActivity$OutOfSpaceDialog$XMIkNmyg7eaOWZXIfjslaid9zVo
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PackageInstallerActivity.OutOfSpaceDialog.lambda$createDialog$0(PackageInstallerActivity.OutOfSpaceDialog.this, dialogInterface, i);
                }
            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() { // from class: com.xiaopeng.appinstaller.-$$Lambda$PackageInstallerActivity$OutOfSpaceDialog$7Yu4CFQYPKFjDQJT-_DmsaGlkrI
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PackageInstallerActivity.OutOfSpaceDialog.this.getActivity().finish();
                }
            }).create();
        }

        public static /* synthetic */ void lambda$createDialog$0(OutOfSpaceDialog outOfSpaceDialog, DialogInterface dialog, int which) {
            Intent intent = new Intent("android.intent.action.MANAGE_PACKAGE_STORAGE");
            intent.setFlags(268435456);
            outOfSpaceDialog.startActivity(intent);
            outOfSpaceDialog.getActivity().finish();
        }
    }

    /* loaded from: classes.dex */
    public static class InstallErrorDialog extends AppErrorDialog {
        static AppErrorDialog newInstance(CharSequence applicationLabel) {
            InstallErrorDialog dialog = new InstallErrorDialog();
            dialog.setArgument(applicationLabel);
            return dialog;
        }

        @Override // com.xiaopeng.appinstaller.PackageInstallerActivity.AppErrorDialog
        protected Dialog createDialog(CharSequence argument) {
            return new AlertDialog.Builder(getActivity()).setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() { // from class: com.xiaopeng.appinstaller.-$$Lambda$PackageInstallerActivity$InstallErrorDialog$anEMP0o1s_5ayqUrF_BFz8AkLLg
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    PackageInstallerActivity.InstallErrorDialog.this.getActivity().finish();
                }
            }).setMessage(getString(R.string.install_failed_msg, new Object[]{argument})).create();
        }
    }

    /* loaded from: classes.dex */
    public static class XpExternalSourcesBlockedDialog extends XpDialogFragment {
        private static final String ARGUMENT_KEY = XpExternalSourcesBlockedDialog.class.getName() + "ARGUMENT_KEY";
        private boolean mSuccess = false;

        static XpExternalSourcesBlockedDialog newInstance(String originationPkg) {
            XpExternalSourcesBlockedDialog dialog = new XpExternalSourcesBlockedDialog();
            Bundle args = new Bundle();
            args.putCharSequence(ARGUMENT_KEY, originationPkg);
            dialog.setArguments(args);
            return dialog;
        }

        @Override // com.xiaopeng.appinstaller.XpDialogFragment, android.app.DialogFragment, android.app.Fragment
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.mSuccess = false;
        }

        @Override // android.app.DialogFragment, android.app.Fragment
        public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            String argument = getArguments().getString(ARGUMENT_KEY);
            try {
                PackageManager pm = getActivity().getPackageManager();
                ApplicationInfo sourceInfo = pm.getApplicationInfo(argument, 0);
                CharSequence label = pm.getApplicationLabel(sourceInfo);
                this.mXDialogView.setTitle(label).setIcon(pm.getApplicationIcon(sourceInfo)).setMessage(R.string.untrusted_external_source_warning).setPositiveButton(getContext().getString(R.string.external_sources_allow_once), new XDialogViewInterface.OnClickListener() { // from class: com.xiaopeng.appinstaller.-$$Lambda$PackageInstallerActivity$XpExternalSourcesBlockedDialog$d-wksImYojN7adcTFpMWK4Ay8Ns
                    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewInterface.OnClickListener
                    public final void onClick(XDialogView xDialogView, int i) {
                        PackageInstallerActivity.XpExternalSourcesBlockedDialog.lambda$onActivityCreated$0(PackageInstallerActivity.XpExternalSourcesBlockedDialog.this, xDialogView, i);
                    }
                }).setNegativeButton(getContext().getString(R.string.cancel), new XDialogViewInterface.OnClickListener() { // from class: com.xiaopeng.appinstaller.-$$Lambda$PackageInstallerActivity$XpExternalSourcesBlockedDialog$mApNIaq8-h-6g0Oawu_MCTG8eX4
                    @Override // com.xiaopeng.xui.widget.dialogview.XDialogViewInterface.OnClickListener
                    public final void onClick(XDialogView xDialogView, int i) {
                        PackageInstallerActivity.XpExternalSourcesBlockedDialog.this.dismiss();
                    }
                });
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("PackageInstallerActivity", "Did not find app info for " + argument);
                dismiss();
            }
        }

        public static /* synthetic */ void lambda$onActivityCreated$0(XpExternalSourcesBlockedDialog xpExternalSourcesBlockedDialog, XDialogView dialogView, int which) {
            PackageInstallerActivity activity = (PackageInstallerActivity) xpExternalSourcesBlockedDialog.getActivity();
            activity.initiateInstall();
            xpExternalSourcesBlockedDialog.mSuccess = true;
            xpExternalSourcesBlockedDialog.dismiss();
        }

        @Override // com.xiaopeng.appinstaller.XpDialogFragment, android.app.DialogFragment, android.content.DialogInterface.OnDismissListener
        public void onDismiss(DialogInterface dialog) {
            super.onDismiss(dialog);
            if (!this.mSuccess) {
                Activity activity = getActivity();
                if (activity != null) {
                    getActivity().finish();
                    return;
                } else {
                    Log.w("PackageInstallerActivity", "XpExternalSourcesBlockedDialog onDismiss, activity is null.");
                    return;
                }
            }
            Log.i("PackageInstallerActivity", "XpExternalSourcesBlockedDialog onDismiss, success.");
        }
    }

    /* loaded from: classes.dex */
    public static abstract class AppErrorDialog extends DialogFragment {
        private static final String ARGUMENT_KEY = AppErrorDialog.class.getName() + "ARGUMENT_KEY";

        protected abstract Dialog createDialog(CharSequence charSequence);

        protected void setArgument(CharSequence argument) {
            Bundle args = new Bundle();
            args.putCharSequence(ARGUMENT_KEY, argument);
            setArguments(args);
        }

        @Override // android.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return createDialog(getArguments().getString(ARGUMENT_KEY));
        }

        @Override // android.app.DialogFragment, android.content.DialogInterface.OnCancelListener
        public void onCancel(DialogInterface dialog) {
            getActivity().finish();
        }
    }
}
