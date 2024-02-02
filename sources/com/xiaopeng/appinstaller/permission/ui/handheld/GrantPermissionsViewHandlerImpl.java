package com.xiaopeng.appinstaller.permission.ui.handheld;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.xiaopeng.appinstaller.R;
import com.xiaopeng.appinstaller.permission.ui.GrantPermissionsViewHandler;
import com.xiaopeng.xui.widget.XCheckBox;
/* loaded from: classes.dex */
public class GrantPermissionsViewHandlerImpl implements View.OnClickListener, GrantPermissionsViewHandler {
    private final Activity mActivity;
    private Button mAllowButton;
    private final String mAppPackageName;
    private ViewGroup mButtonBar;
    private ViewGroup mCurrentDesc;
    private TextView mCurrentGroupView;
    private Button mDenyButton;
    private ViewGroup mDescContainer;
    private XCheckBox mDoNotAskCheckbox;
    private boolean mDoNotAskChecked;
    private int mGroupCount;
    private Icon mGroupIcon;
    private int mGroupIndex;
    private CharSequence mGroupMessage;
    private String mGroupName;
    private ImageView mIconView;
    private TextView mMessageView;
    private Button mMoreInfoButton;
    private final boolean mPermissionReviewRequired;
    private GrantPermissionsViewHandler.ResultListener mResultListener;
    private LinearLayout mRootView;
    private boolean mShowDonNotAsk;

    public GrantPermissionsViewHandlerImpl(Activity activity, String appPackageName) {
        this.mActivity = activity;
        this.mAppPackageName = appPackageName;
        this.mPermissionReviewRequired = activity.getPackageManager().isPermissionReviewModeEnabled();
    }

    public GrantPermissionsViewHandlerImpl setResultListener(GrantPermissionsViewHandler.ResultListener listener) {
        this.mResultListener = listener;
        return this;
    }

    @Override // com.xiaopeng.appinstaller.permission.ui.GrantPermissionsViewHandler
    public void saveInstanceState(Bundle arguments) {
        arguments.putString("ARG_GROUP_NAME", this.mGroupName);
        arguments.putInt("ARG_GROUP_COUNT", this.mGroupCount);
        arguments.putInt("ARG_GROUP_INDEX", this.mGroupIndex);
        arguments.putParcelable("ARG_GROUP_ICON", this.mGroupIcon);
        arguments.putCharSequence("ARG_GROUP_MESSAGE", this.mGroupMessage);
        arguments.putBoolean("ARG_GROUP_SHOW_DO_NOT_ASK", this.mShowDonNotAsk);
        arguments.putBoolean("ARG_GROUP_DO_NOT_ASK_CHECKED", this.mDoNotAskCheckbox.isChecked());
    }

    @Override // com.xiaopeng.appinstaller.permission.ui.GrantPermissionsViewHandler
    public void loadInstanceState(Bundle savedInstanceState) {
        this.mGroupName = savedInstanceState.getString("ARG_GROUP_NAME");
        this.mGroupMessage = savedInstanceState.getCharSequence("ARG_GROUP_MESSAGE");
        this.mGroupIcon = (Icon) savedInstanceState.getParcelable("ARG_GROUP_ICON");
        this.mGroupCount = savedInstanceState.getInt("ARG_GROUP_COUNT");
        this.mGroupIndex = savedInstanceState.getInt("ARG_GROUP_INDEX");
        this.mShowDonNotAsk = savedInstanceState.getBoolean("ARG_GROUP_SHOW_DO_NOT_ASK");
        this.mDoNotAskChecked = savedInstanceState.getBoolean("ARG_GROUP_DO_NOT_ASK_CHECKED");
        updateDoNotAskCheckBox();
    }

    @Override // com.xiaopeng.appinstaller.permission.ui.GrantPermissionsViewHandler
    public void updateUi(String groupName, int groupCount, int groupIndex, Icon icon, CharSequence message, boolean showDonNotAsk) {
        this.mGroupName = groupName;
        this.mGroupCount = groupCount;
        this.mGroupIndex = groupIndex;
        this.mGroupIcon = icon;
        this.mGroupMessage = message;
        this.mShowDonNotAsk = showDonNotAsk;
        this.mDoNotAskChecked = false;
        if (this.mIconView != null) {
            if (this.mGroupIndex > 0) {
                animateToPermission();
                return;
            }
            updateDescription();
            updateGroup();
            updateDoNotAskCheckBox();
        }
    }

    public void onConfigurationChanged() {
    }

    private void animateOldContent(Runnable callback) {
        Interpolator interpolator = AnimationUtils.loadInterpolator(this.mActivity, 17563663);
        this.mIconView.animate().scaleX(0.0f).scaleY(0.0f).setDuration(200L).setInterpolator(interpolator).start();
        this.mCurrentDesc.animate().alpha(0.0f).setDuration(200L).setInterpolator(interpolator).withEndAction(callback).start();
        if (!this.mShowDonNotAsk && this.mDoNotAskCheckbox.getVisibility() == 0) {
            this.mDoNotAskCheckbox.animate().alpha(0.0f).setDuration(200L).setInterpolator(interpolator).start();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void attachNewContent(Runnable callback) {
        this.mCurrentDesc = (ViewGroup) LayoutInflater.from(this.mActivity).inflate(R.layout.permission_description, this.mDescContainer, false);
        this.mDescContainer.removeAllViews();
        this.mDescContainer.addView(this.mCurrentDesc);
        this.mMessageView = (TextView) this.mCurrentDesc.findViewById(R.id.permission_message);
        this.mIconView = (ImageView) this.mCurrentDesc.findViewById(R.id.permission_icon);
        boolean doNotAskWasShown = this.mDoNotAskCheckbox.getVisibility() == 0;
        updateDescription();
        updateGroup();
        updateDoNotAskCheckBox();
        if (!doNotAskWasShown && this.mShowDonNotAsk) {
            this.mDoNotAskCheckbox.setAlpha(0.0f);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void animateNewContent() {
        Interpolator interpolator = AnimationUtils.loadInterpolator(this.mActivity, 17563662);
        this.mCurrentDesc.animate().translationX(0.0f).setDuration(300L).setInterpolator(interpolator).start();
        if (this.mShowDonNotAsk && this.mDoNotAskCheckbox.getVisibility() == 0 && this.mDoNotAskCheckbox.getAlpha() < 1.0f) {
            this.mDoNotAskCheckbox.setAlpha(0.0f);
            this.mDoNotAskCheckbox.animate().alpha(1.0f).setDuration(300L).setInterpolator(interpolator).start();
        }
    }

    private void animateToPermission() {
        animateOldContent(new Runnable() { // from class: com.xiaopeng.appinstaller.permission.ui.handheld.GrantPermissionsViewHandlerImpl.1
            @Override // java.lang.Runnable
            public void run() {
                GrantPermissionsViewHandlerImpl.this.attachNewContent(new Runnable() { // from class: com.xiaopeng.appinstaller.permission.ui.handheld.GrantPermissionsViewHandlerImpl.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        GrantPermissionsViewHandlerImpl.this.animateNewContent();
                    }
                });
            }
        });
    }

    @Override // com.xiaopeng.appinstaller.permission.ui.GrantPermissionsViewHandler
    public int getContentView() {
        return R.layout.grant_permissions;
    }

    @Override // com.xiaopeng.appinstaller.permission.ui.GrantPermissionsViewHandler
    public View createView() {
        this.mRootView = (LinearLayout) this.mActivity.findViewById(R.id.permission_root_view);
        this.mRootView.setOnClickListener(this);
        this.mButtonBar = (ViewGroup) this.mRootView.findViewById(R.id.button_group);
        this.mMessageView = (TextView) this.mRootView.findViewById(R.id.permission_message);
        this.mIconView = (ImageView) this.mRootView.findViewById(R.id.permission_icon);
        this.mCurrentGroupView = (TextView) this.mRootView.findViewById(R.id.current_page_text);
        this.mDoNotAskCheckbox = (XCheckBox) this.mRootView.findViewById(R.id.do_not_ask_checkbox);
        this.mAllowButton = (Button) this.mRootView.findViewById(R.id.permission_allow_button);
        this.mAllowButton.setOnClickListener(this);
        if (this.mPermissionReviewRequired) {
            this.mMoreInfoButton = (Button) this.mRootView.findViewById(R.id.permission_more_info_button);
            this.mMoreInfoButton.setVisibility(0);
            this.mMoreInfoButton.setOnClickListener(this);
        }
        this.mDescContainer = (ViewGroup) this.mRootView.findViewById(R.id.desc_container);
        this.mCurrentDesc = (ViewGroup) this.mRootView.findViewById(R.id.perm_desc_root);
        this.mDenyButton = (Button) this.mRootView.findViewById(R.id.permission_deny_button);
        this.mDenyButton.setOnClickListener(this);
        this.mDoNotAskCheckbox.setOnClickListener(this);
        if (this.mGroupName != null) {
            updateDescription();
            updateGroup();
            updateDoNotAskCheckBox();
        }
        return this.mRootView;
    }

    private void updateDescription() {
        if (this.mGroupIcon != null) {
            this.mIconView.setImageDrawable(this.mGroupIcon.loadDrawable(this.mActivity));
        }
        this.mMessageView.setText(this.mGroupMessage);
    }

    private void updateGroup() {
        if (this.mGroupCount > 1) {
            this.mCurrentGroupView.setVisibility(0);
            this.mCurrentGroupView.setText(this.mActivity.getString(R.string.current_permission_template, new Object[]{Integer.valueOf(this.mGroupIndex + 1), Integer.valueOf(this.mGroupCount)}));
            return;
        }
        this.mCurrentGroupView.setVisibility(8);
    }

    private void updateDoNotAskCheckBox() {
        if (this.mShowDonNotAsk) {
            this.mDoNotAskCheckbox.setVisibility(0);
            this.mDoNotAskCheckbox.setOnClickListener(this);
            this.mDoNotAskCheckbox.setChecked(this.mDoNotAskChecked);
            this.mAllowButton.setEnabled(true ^ this.mDoNotAskChecked);
            return;
        }
        this.mDoNotAskCheckbox.setVisibility(8);
        this.mDoNotAskCheckbox.setOnClickListener(null);
        this.mAllowButton.setEnabled(true);
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View view) {
        int id = view.getId();
        boolean z = true;
        if (id == R.id.do_not_ask_checkbox) {
            this.mAllowButton.setEnabled(!this.mDoNotAskCheckbox.isChecked());
        } else if (id == R.id.permission_allow_button) {
            if (this.mResultListener != null) {
                view.performAccessibilityAction(128, null);
                this.mResultListener.onPermissionGrantResult(this.mGroupName, true, false);
            }
        } else if (id != R.id.permission_deny_button) {
            if (id == R.id.permission_more_info_button) {
                Intent intent = new Intent("android.intent.action.MANAGE_APP_PERMISSIONS");
                intent.putExtra("android.intent.extra.PACKAGE_NAME", this.mAppPackageName);
                intent.putExtra("com.xiaopeng.appinstaller.extra.ALL_PERMISSIONS", true);
                this.mActivity.startActivity(intent);
            }
        } else {
            this.mAllowButton.setEnabled(true);
            if (this.mResultListener != null) {
                view.performAccessibilityAction(128, null);
                GrantPermissionsViewHandler.ResultListener resultListener = this.mResultListener;
                String str = this.mGroupName;
                if (!this.mShowDonNotAsk || !this.mDoNotAskCheckbox.isChecked()) {
                    z = false;
                }
                resultListener.onPermissionGrantResult(str, false, z);
            }
        }
    }
}
