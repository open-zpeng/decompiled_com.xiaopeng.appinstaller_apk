package com.xiaopeng.appinstaller.permission.ui.handheld;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.RemoteCallback;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.preference.TwoStatePreference;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.xiaopeng.appinstaller.R;
import com.xiaopeng.appinstaller.permission.model.AppPermissionGroup;
import com.xiaopeng.appinstaller.permission.model.AppPermissions;
import com.xiaopeng.appinstaller.permission.model.Permission;
import com.xiaopeng.appinstaller.permission.ui.ConfirmActionDialogFragment;
import com.xiaopeng.appinstaller.permission.utils.ArrayUtils;
import com.xiaopeng.appinstaller.permission.utils.Utils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/* loaded from: classes.dex */
public final class ReviewPermissionsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener, View.OnClickListener, ConfirmActionDialogFragment.OnActionConfirmedListener {
    private AppPermissions mAppPermissions;
    private Button mCancelButton;
    private Button mContinueButton;
    private PreferenceCategory mCurrentPermissionsCategory;
    private boolean mHasConfirmedRevoke;
    private Button mMoreInfoButton;
    private PreferenceCategory mNewPermissionsCategory;

    public static ReviewPermissionsFragment newInstance(PackageInfo packageInfo) {
        Bundle arguments = new Bundle();
        arguments.putParcelable("com.xiaopeng.appinstaller.permission.ui.extra.PACKAGE_INFO", packageInfo);
        ReviewPermissionsFragment instance = new ReviewPermissionsFragment();
        instance.setArguments(arguments);
        instance.setRetainInstance(true);
        return instance;
    }

    @Override // android.preference.PreferenceFragment, android.app.Fragment
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        PackageInfo packageInfo = (PackageInfo) getArguments().getParcelable("com.xiaopeng.appinstaller.permission.ui.extra.PACKAGE_INFO");
        if (packageInfo == null) {
            activity.finish();
            return;
        }
        this.mAppPermissions = new AppPermissions(activity, packageInfo, null, false, new Runnable() { // from class: com.xiaopeng.appinstaller.permission.ui.handheld.ReviewPermissionsFragment.1
            @Override // java.lang.Runnable
            public void run() {
                ReviewPermissionsFragment.this.getActivity().finish();
            }
        });
        if (this.mAppPermissions.getPermissionGroups().isEmpty()) {
            activity.finish();
            return;
        }
        boolean reviewRequired = false;
        Iterator<AppPermissionGroup> it = this.mAppPermissions.getPermissionGroups().iterator();
        while (true) {
            if (!it.hasNext()) {
                break;
            }
            AppPermissionGroup group = it.next();
            if (group.isReviewRequired()) {
                reviewRequired = true;
                break;
            }
        }
        if (!reviewRequired) {
            activity.finish();
        }
    }

    @Override // android.preference.PreferenceFragment, android.app.Fragment
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindUi();
    }

    @Override // android.app.Fragment
    public void onResume() {
        super.onResume();
        this.mAppPermissions.refresh();
        loadPreferences();
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (view == this.mContinueButton) {
            confirmPermissionsReview();
            executeCallback(true);
        } else if (view == this.mCancelButton) {
            executeCallback(false);
            activity.setResult(0);
        } else if (view == this.mMoreInfoButton) {
            Intent intent = new Intent("android.intent.action.MANAGE_APP_PERMISSIONS");
            intent.putExtra("android.intent.extra.PACKAGE_NAME", this.mAppPermissions.getPackageInfo().packageName);
            intent.putExtra("com.xiaopeng.appinstaller.extra.ALL_PERMISSIONS", true);
            getActivity().startActivity(intent);
        }
        activity.finish();
    }

    @Override // android.preference.Preference.OnPreferenceChangeListener
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (this.mHasConfirmedRevoke) {
            return true;
        }
        if (preference instanceof SwitchPreference) {
            SwitchPreference switchPreference = (SwitchPreference) preference;
            if (switchPreference.isChecked()) {
                showWarnRevokeDialog(switchPreference.getKey());
                return false;
            }
            return true;
        }
        return false;
    }

    @Override // com.xiaopeng.appinstaller.permission.ui.ConfirmActionDialogFragment.OnActionConfirmedListener
    public void onActionConfirmed(String action) {
        Preference preference = getPreferenceManager().findPreference(action);
        if (preference instanceof SwitchPreference) {
            SwitchPreference switchPreference = (SwitchPreference) preference;
            switchPreference.setChecked(false);
            this.mHasConfirmedRevoke = true;
        }
    }

    private void showWarnRevokeDialog(String groupName) {
        DialogFragment fragment = ConfirmActionDialogFragment.newInstance(getString(R.string.old_sdk_deny_warning), groupName);
        fragment.show(getFragmentManager(), fragment.getClass().getName());
    }

    private void confirmPermissionsReview() {
        List<PreferenceGroup> preferenceGroups = new ArrayList<>();
        if (this.mNewPermissionsCategory != null) {
            preferenceGroups.add(this.mNewPermissionsCategory);
            preferenceGroups.add(this.mCurrentPermissionsCategory);
        } else {
            preferenceGroups.add(getPreferenceScreen());
        }
        int preferenceGroupCount = preferenceGroups.size();
        for (int groupNum = 0; groupNum < preferenceGroupCount; groupNum++) {
            PreferenceGroup preferenceGroup = preferenceGroups.get(groupNum);
            int preferenceCount = preferenceGroup.getPreferenceCount();
            for (int prefNum = 0; prefNum < preferenceCount; prefNum++) {
                Preference preference = preferenceGroup.getPreference(prefNum);
                if (preference instanceof TwoStatePreference) {
                    TwoStatePreference twoStatePreference = (TwoStatePreference) preference;
                    String groupName = preference.getKey();
                    AppPermissionGroup group = this.mAppPermissions.getPermissionGroup(groupName);
                    if (twoStatePreference.isChecked()) {
                        int permissionCount = group.getPermissions().size();
                        String[] permissionsToGrant = null;
                        for (int j = 0; j < permissionCount; j++) {
                            Permission permission = group.getPermissions().get(j);
                            if (permission.isReviewRequired()) {
                                permissionsToGrant = ArrayUtils.appendString(permissionsToGrant, permission.getName());
                            }
                        }
                        if (permissionsToGrant != null) {
                            group.grantRuntimePermissions(false, permissionsToGrant);
                        }
                    } else {
                        group.revokeRuntimePermissions(false);
                    }
                    group.resetReviewRequired();
                }
            }
        }
    }

    private void bindUi() {
        int labelTemplateResId;
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        Drawable icon = this.mAppPermissions.getPackageInfo().applicationInfo.loadIcon(activity.getPackageManager());
        ImageView iconView = (ImageView) activity.findViewById(R.id.app_icon);
        iconView.setImageDrawable(icon);
        if (isPackageUpdated()) {
            labelTemplateResId = R.string.permission_review_title_template_update;
        } else {
            labelTemplateResId = R.string.permission_review_title_template_install;
        }
        Spanned message = Html.fromHtml(getString(labelTemplateResId, new Object[]{this.mAppPermissions.getAppLabel()}), 0);
        activity.setTitle(message.toString());
        TextView permissionsMessageView = (TextView) activity.findViewById(R.id.permissions_message);
        permissionsMessageView.setText(message);
        this.mContinueButton = (Button) getActivity().findViewById(R.id.continue_button);
        this.mContinueButton.setOnClickListener(this);
        this.mCancelButton = (Button) getActivity().findViewById(R.id.cancel_button);
        this.mCancelButton.setOnClickListener(this);
        this.mMoreInfoButton = (Button) getActivity().findViewById(R.id.permission_more_info_button);
        this.mMoreInfoButton.setOnClickListener(this);
    }

    private void loadPreferences() {
        Preference cachedPreference;
        SwitchPreference preference;
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        PreferenceScreen screen = getPreferenceScreen();
        if (screen == null) {
            screen = getPreferenceManager().createPreferenceScreen(getActivity());
            setPreferenceScreen(screen);
        } else {
            screen.removeAll();
        }
        this.mCurrentPermissionsCategory = null;
        PreferenceGroup oldNewPermissionsCategory = this.mNewPermissionsCategory;
        this.mNewPermissionsCategory = null;
        boolean isPackageUpdated = isPackageUpdated();
        for (AppPermissionGroup group : this.mAppPermissions.getPermissionGroups()) {
            if (Utils.shouldShowPermission(group, this.mAppPermissions.getPackageInfo().packageName) && "android".equals(group.getDeclaringPackage())) {
                if (oldNewPermissionsCategory == null) {
                    cachedPreference = null;
                } else {
                    cachedPreference = oldNewPermissionsCategory.findPreference(group.getName());
                }
                if (cachedPreference instanceof SwitchPreference) {
                    preference = (SwitchPreference) cachedPreference;
                } else {
                    preference = new SwitchPreference(getActivity());
                    preference.setKey(group.getName());
                    Drawable icon = Utils.loadDrawable(activity.getPackageManager(), group.getIconPkg(), group.getIconResId());
                    preference.setIcon(Utils.applyTint(getContext(), icon, 16843817));
                    preference.setTitle(group.getLabel());
                    preference.setSummary(group.getDescription());
                    preference.setPersistent(false);
                    preference.setOnPreferenceChangeListener(this);
                }
                preference.setChecked(group.areRuntimePermissionsGranted() || group.isReviewRequired());
                if (group.isPolicyFixed()) {
                    preference.setEnabled(false);
                    preference.setSummary(getString(R.string.permission_summary_enforced_by_policy));
                } else {
                    preference.setEnabled(true);
                }
                if (group.isReviewRequired()) {
                    if (!isPackageUpdated) {
                        screen.addPreference(preference);
                    } else {
                        if (this.mNewPermissionsCategory == null) {
                            this.mNewPermissionsCategory = new PreferenceCategory(activity);
                            this.mNewPermissionsCategory.setTitle(R.string.new_permissions_category);
                            this.mNewPermissionsCategory.setOrder(1);
                            screen.addPreference(this.mNewPermissionsCategory);
                        }
                        this.mNewPermissionsCategory.addPreference(preference);
                    }
                } else {
                    if (this.mCurrentPermissionsCategory == null) {
                        this.mCurrentPermissionsCategory = new PreferenceCategory(activity);
                        this.mCurrentPermissionsCategory.setTitle(R.string.current_permissions_category);
                        this.mCurrentPermissionsCategory.setOrder(2);
                        screen.addPreference(this.mCurrentPermissionsCategory);
                    }
                    this.mCurrentPermissionsCategory.addPreference(preference);
                }
            }
        }
    }

    private boolean isPackageUpdated() {
        List<AppPermissionGroup> groups = this.mAppPermissions.getPermissionGroups();
        int groupCount = groups.size();
        for (int i = 0; i < groupCount; i++) {
            AppPermissionGroup group = groups.get(i);
            if (!group.isReviewRequired()) {
                return true;
            }
        }
        return false;
    }

    private void executeCallback(boolean success) {
        IntentSender intent;
        Activity activity = getActivity();
        if (activity == null) {
            return;
        }
        if (success && (intent = (IntentSender) activity.getIntent().getParcelableExtra("android.intent.extra.INTENT")) != null) {
            int flagMask = 0;
            int flagValues = 0;
            try {
                if (activity.getIntent().getBooleanExtra("android.intent.extra.RESULT_NEEDED", false)) {
                    flagMask = 33554432;
                    flagValues = 33554432;
                }
                activity.startIntentSenderForResult(intent, -1, null, flagMask, flagValues, 0);
                return;
            } catch (IntentSender.SendIntentException e) {
                return;
            }
        }
        RemoteCallback callback = activity.getIntent().getParcelableExtra("android.intent.extra.REMOTE_CALLBACK");
        if (callback != null) {
            Bundle result = new Bundle();
            result.putBoolean("android.intent.extra.RETURN_RESULT", success);
            callback.sendResult(result);
        }
    }
}
