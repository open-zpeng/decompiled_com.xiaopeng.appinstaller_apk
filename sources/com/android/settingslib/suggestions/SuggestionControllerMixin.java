package com.android.settingslib.suggestions;

import android.app.LoaderManager;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.service.settings.suggestions.Suggestion;
import java.util.List;
/* loaded from: classes.dex */
public class SuggestionControllerMixin implements LoaderManager.LoaderCallbacks<List<Suggestion>>, LifecycleObserver {
    private final Context mContext;
    private final SuggestionControllerHost mHost;
    private final SuggestionController mSuggestionController;
    private boolean mSuggestionLoaded;

    /* loaded from: classes.dex */
    public interface SuggestionControllerHost {
        void onSuggestionReady(List<Suggestion> list);
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onStart() {
        this.mSuggestionController.start();
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop() {
        this.mSuggestionController.stop();
    }

    @Override // android.app.LoaderManager.LoaderCallbacks
    public Loader<List<Suggestion>> onCreateLoader(int id, Bundle args) {
        if (id == 42) {
            this.mSuggestionLoaded = false;
            return new SuggestionLoader(this.mContext, this.mSuggestionController);
        }
        throw new IllegalArgumentException("This loader id is not supported " + id);
    }

    @Override // android.app.LoaderManager.LoaderCallbacks
    public void onLoadFinished(Loader<List<Suggestion>> loader, List<Suggestion> data) {
        this.mSuggestionLoaded = true;
        this.mHost.onSuggestionReady(data);
    }

    @Override // android.app.LoaderManager.LoaderCallbacks
    public void onLoaderReset(Loader<List<Suggestion>> loader) {
        this.mSuggestionLoaded = false;
    }
}
