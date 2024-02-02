package com.xiaopeng.appinstaller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.xiaopeng.appinstaller.InstallStaging;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
/* loaded from: classes.dex */
public class InstallStaging extends Activity {
    private static final String LOG_TAG = "Loong/" + InstallStaging.class.getSimpleName();
    private File mStagedFile;
    private StagingAsyncTask mStagingTask;

    @Override // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.install_staging);
        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() { // from class: com.xiaopeng.appinstaller.-$$Lambda$InstallStaging$sQJG9IvbDq8Zm_9DaxtwHa0zPBI
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                InstallStaging.lambda$onCreate$0(InstallStaging.this, view);
            }
        });
        if (savedInstanceState != null) {
            this.mStagedFile = new File(savedInstanceState.getString("STAGED_FILE"));
            if (!this.mStagedFile.exists()) {
                this.mStagedFile = null;
            }
        }
    }

    public static /* synthetic */ void lambda$onCreate$0(InstallStaging installStaging, View view) {
        if (installStaging.mStagingTask != null) {
            installStaging.mStagingTask.cancel(true);
        }
        installStaging.setResult(0);
        installStaging.finish();
    }

    @Override // android.app.Activity
    protected void onResume() {
        super.onResume();
        if (this.mStagingTask == null) {
            if (this.mStagedFile == null) {
                try {
                    this.mStagedFile = TemporaryFileManager.getStagedFile(this);
                } catch (IOException e) {
                    showError();
                    return;
                }
            }
            this.mStagingTask = new StagingAsyncTask();
            this.mStagingTask.execute(getIntent().getData());
        }
    }

    @Override // android.app.Activity
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("STAGED_FILE", this.mStagedFile.getPath());
    }

    @Override // android.app.Activity
    protected void onDestroy() {
        if (this.mStagingTask != null) {
            this.mStagingTask.cancel(true);
        }
        super.onDestroy();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void showError() {
        new ErrorDialog().showAllowingStateLoss(getFragmentManager(), "error");
        Intent result = new Intent();
        result.putExtra("android.intent.extra.INSTALL_RESULT", -2);
        setResult(1, result);
    }

    /* loaded from: classes.dex */
    public static class ErrorDialog extends DialogFragment {
        private Activity mActivity;

        @Override // android.app.DialogFragment, android.app.Fragment
        public void onAttach(Context context) {
            super.onAttach(context);
            this.mActivity = (Activity) context;
        }

        @Override // android.app.DialogFragment
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog alertDialog = new AlertDialog.Builder(this.mActivity).setMessage(R.string.Parse_error_dlg_text).setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() { // from class: com.xiaopeng.appinstaller.-$$Lambda$InstallStaging$ErrorDialog$GYalHwMWiUf6guEaS1JpEu_NsDs
                @Override // android.content.DialogInterface.OnClickListener
                public final void onClick(DialogInterface dialogInterface, int i) {
                    InstallStaging.ErrorDialog.this.mActivity.finish();
                }
            }).create();
            alertDialog.setCanceledOnTouchOutside(false);
            return alertDialog;
        }

        @Override // android.app.DialogFragment, android.content.DialogInterface.OnCancelListener
        public void onCancel(DialogInterface dialog) {
            super.onCancel(dialog);
            this.mActivity.finish();
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public final class StagingAsyncTask extends AsyncTask<Uri, Void, Boolean> {
        private StagingAsyncTask() {
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public Boolean doInBackground(Uri... params) {
            if (params == null || params.length <= 0) {
                return false;
            }
            Uri packageUri = params[0];
            String str = InstallStaging.LOG_TAG;
            Log.d(str, "doInBackground packageUri:" + packageUri.getPath());
            try {
                InputStream in = InstallStaging.this.getContentResolver().openInputStream(packageUri);
                if (in == null) {
                    if (in != null) {
                        $closeResource(null, in);
                    }
                    return false;
                }
                OutputStream out = new FileOutputStream(InstallStaging.this.mStagedFile);
                byte[] buffer = new byte[1048576];
                while (true) {
                    int bytesRead = in.read(buffer);
                    if (bytesRead < 0) {
                        $closeResource(null, out);
                        if (in != null) {
                            $closeResource(null, in);
                        }
                        return true;
                    } else if (isCancelled()) {
                        $closeResource(null, out);
                        if (in != null) {
                            $closeResource(null, in);
                        }
                        return false;
                    } else {
                        out.write(buffer, 0, bytesRead);
                    }
                }
            } catch (IOException | IllegalStateException | SecurityException e) {
                Log.w(InstallStaging.LOG_TAG, "Error staging apk from content URI", e);
                return false;
            }
        }

        private static /* synthetic */ void $closeResource(Throwable x0, AutoCloseable x1) {
            if (x0 == null) {
                x1.close();
                return;
            }
            try {
                x1.close();
            } catch (Throwable th) {
                x0.addSuppressed(th);
            }
        }

        /* JADX INFO: Access modifiers changed from: protected */
        @Override // android.os.AsyncTask
        public void onPostExecute(Boolean success) {
            if (!success.booleanValue()) {
                InstallStaging.this.showError();
                return;
            }
            Intent installIntent = new Intent(InstallStaging.this.getIntent());
            installIntent.setClass(InstallStaging.this, DeleteStagedFileOnResult.class);
            installIntent.setData(Uri.fromFile(InstallStaging.this.mStagedFile));
            if (installIntent.getBooleanExtra("android.intent.extra.RETURN_RESULT", false)) {
                installIntent.addFlags(33554432);
            }
            installIntent.addFlags(65536);
            InstallStaging.this.startActivity(installIntent);
            InstallStaging.this.finish();
        }
    }
}
