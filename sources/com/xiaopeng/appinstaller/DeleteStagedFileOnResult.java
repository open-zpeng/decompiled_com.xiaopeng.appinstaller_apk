package com.xiaopeng.appinstaller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import java.io.File;
/* loaded from: classes.dex */
public class DeleteStagedFileOnResult extends Activity {
    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Intent installIntent = new Intent(getIntent());
            installIntent.setClass(this, PackageInstallerActivity.class);
            installIntent.setFlags(65536);
            startActivityForResult(installIntent, 0);
        }
    }

    @Override // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        File sourceFile = new File(getIntent().getData().getPath());
        sourceFile.delete();
        setResult(resultCode, data);
        finish();
    }
}
