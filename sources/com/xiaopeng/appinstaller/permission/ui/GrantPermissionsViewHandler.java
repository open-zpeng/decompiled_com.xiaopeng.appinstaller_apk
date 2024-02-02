package com.xiaopeng.appinstaller.permission.ui;

import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.view.View;
/* loaded from: classes.dex */
public interface GrantPermissionsViewHandler {

    /* loaded from: classes.dex */
    public interface ResultListener {
        void onPermissionGrantResult(String str, boolean z, boolean z2);
    }

    View createView();

    int getContentView();

    void loadInstanceState(Bundle bundle);

    void saveInstanceState(Bundle bundle);

    void updateUi(String str, int i, int i2, Icon icon, CharSequence charSequence, boolean z);
}
