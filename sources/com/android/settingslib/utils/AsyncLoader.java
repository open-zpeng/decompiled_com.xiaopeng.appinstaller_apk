package com.android.settingslib.utils;

import android.content.AsyncTaskLoader;
import android.content.Context;
/* loaded from: classes.dex */
public abstract class AsyncLoader<T> extends AsyncTaskLoader<T> {
    private T mResult;

    protected abstract void onDiscardResult(T t);

    public AsyncLoader(Context context) {
        super(context);
    }

    @Override // android.content.Loader
    protected void onStartLoading() {
        if (this.mResult != null) {
            deliverResult(this.mResult);
        }
        if (takeContentChanged() || this.mResult == null) {
            forceLoad();
        }
    }

    @Override // android.content.Loader
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override // android.content.Loader
    public void deliverResult(T data) {
        if (isReset()) {
            if (data != null) {
                onDiscardResult(data);
                return;
            }
            return;
        }
        T oldResult = this.mResult;
        this.mResult = data;
        if (isStarted()) {
            super.deliverResult(data);
        }
        if (oldResult != null && oldResult != this.mResult) {
            onDiscardResult(oldResult);
        }
    }

    @Override // android.content.Loader
    protected void onReset() {
        super.onReset();
        onStopLoading();
        if (this.mResult != null) {
            onDiscardResult(this.mResult);
        }
        this.mResult = null;
    }

    @Override // android.content.AsyncTaskLoader
    public void onCanceled(T data) {
        super.onCanceled(data);
        if (data != null) {
            onDiscardResult(data);
        }
    }
}
