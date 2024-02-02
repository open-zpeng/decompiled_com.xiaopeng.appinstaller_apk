package com.android.settingslib.core.lifecycle;

import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.LifecycleRegistry;
import android.arch.lifecycle.OnLifecycleEvent;
import android.util.Log;
import com.android.settingslib.core.lifecycle.events.OnDestroy;
import com.android.settingslib.core.lifecycle.events.OnPause;
import com.android.settingslib.core.lifecycle.events.OnResume;
import com.android.settingslib.core.lifecycle.events.OnStart;
import com.android.settingslib.core.lifecycle.events.OnStop;
import com.android.settingslib.utils.ThreadUtils;
import java.util.List;
/* loaded from: classes.dex */
public class Lifecycle extends LifecycleRegistry {
    private final List<LifecycleObserver> mObservers;

    @Override // android.arch.lifecycle.LifecycleRegistry, android.arch.lifecycle.Lifecycle
    public void addObserver(android.arch.lifecycle.LifecycleObserver observer) {
        ThreadUtils.ensureMainThread();
        super.addObserver(observer);
        if (observer instanceof LifecycleObserver) {
            this.mObservers.add((LifecycleObserver) observer);
        }
    }

    @Override // android.arch.lifecycle.LifecycleRegistry, android.arch.lifecycle.Lifecycle
    public void removeObserver(android.arch.lifecycle.LifecycleObserver observer) {
        ThreadUtils.ensureMainThread();
        super.removeObserver(observer);
        if (observer instanceof LifecycleObserver) {
            this.mObservers.remove(observer);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onStart() {
        int size = this.mObservers.size();
        for (int i = 0; i < size; i++) {
            LifecycleObserver observer = this.mObservers.get(i);
            if (observer instanceof OnStart) {
                ((OnStart) observer).onStart();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onResume() {
        int size = this.mObservers.size();
        for (int i = 0; i < size; i++) {
            LifecycleObserver observer = this.mObservers.get(i);
            if (observer instanceof OnResume) {
                ((OnResume) observer).onResume();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onPause() {
        int size = this.mObservers.size();
        for (int i = 0; i < size; i++) {
            LifecycleObserver observer = this.mObservers.get(i);
            if (observer instanceof OnPause) {
                ((OnPause) observer).onPause();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onStop() {
        int size = this.mObservers.size();
        for (int i = 0; i < size; i++) {
            LifecycleObserver observer = this.mObservers.get(i);
            if (observer instanceof OnStop) {
                ((OnStop) observer).onStop();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onDestroy() {
        int size = this.mObservers.size();
        for (int i = 0; i < size; i++) {
            LifecycleObserver observer = this.mObservers.get(i);
            if (observer instanceof OnDestroy) {
                ((OnDestroy) observer).onDestroy();
            }
        }
    }

    /* loaded from: classes.dex */
    private class LifecycleProxy implements android.arch.lifecycle.LifecycleObserver {
        final /* synthetic */ Lifecycle this$0;

        @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
        public void onLifecycleEvent(LifecycleOwner owner, Lifecycle.Event event) {
            switch (event) {
                case ON_CREATE:
                default:
                    return;
                case ON_START:
                    this.this$0.onStart();
                    return;
                case ON_RESUME:
                    this.this$0.onResume();
                    return;
                case ON_PAUSE:
                    this.this$0.onPause();
                    return;
                case ON_STOP:
                    this.this$0.onStop();
                    return;
                case ON_DESTROY:
                    this.this$0.onDestroy();
                    return;
                case ON_ANY:
                    Log.wtf("LifecycleObserver", "Should not receive an 'ANY' event!");
                    return;
            }
        }
    }
}
