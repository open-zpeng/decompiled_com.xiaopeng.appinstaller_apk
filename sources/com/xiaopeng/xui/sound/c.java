package com.xiaopeng.xui.sound;

import android.content.res.AssetFileDescriptor;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.util.AndroidRuntimeException;
import android.util.Log;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.sound.XSoundEffect;
import java.io.IOException;
import xiaopeng.widget.BuildConfig;
/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: classes.dex */
public class c extends com.xiaopeng.xui.sound.a {

    /* renamed from: b  reason: collision with root package name */
    private int f56b;
    private int c;
    private SoundPool d;
    private XSoundEffect.SoundLoader e;
    private boolean f;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class a implements SoundPool.OnLoadCompleteListener {
        a() {
        }

        @Override // android.media.SoundPool.OnLoadCompleteListener
        public void onLoadComplete(SoundPool soundPool, int i, int i2) {
            c cVar = c.this;
            cVar.a("load onLoadComplete--,sampleId:" + i + ",status:" + i2);
            if (c.this.f) {
                c.this.f = false;
                c.this.play();
            }
        }
    }

    /* loaded from: classes.dex */
    private class b implements XSoundEffect.SoundLoader {
        private b() {
        }

        /* synthetic */ b(c cVar, a aVar) {
            this();
        }

        @Override // com.xiaopeng.xui.sound.XSoundEffect.SoundLoader
        public int load(d dVar) {
            AssetFileDescriptor assetFileDescriptor;
            IOException e;
            if (c.this.d == null) {
                c.this.a("LoaderAssets--resource:" + dVar + ",mSoundPool is null");
                return 0;
            }
            try {
                assetFileDescriptor = Xui.getContext().getAssets().openFd(c.this.f53a.b());
                try {
                    c.this.a("LoaderAssets--resource:" + dVar + ",path:" + c.this.f53a.b() + ", is success");
                } catch (IOException e2) {
                    e = e2;
                    c.this.a("LoaderAssets--resource:" + dVar + ",path:" + c.this.f53a.b() + ", is error");
                    e.printStackTrace();
                    return c.this.d.load(assetFileDescriptor, 1);
                }
            } catch (IOException e3) {
                assetFileDescriptor = null;
                e = e3;
            }
            try {
                return c.this.d.load(assetFileDescriptor, 1);
            } catch (AndroidRuntimeException e4) {
                e4.printStackTrace();
                return 0;
            }
        }
    }

    /* renamed from: com.xiaopeng.xui.sound.c$c  reason: collision with other inner class name */
    /* loaded from: classes.dex */
    private class C0002c implements XSoundEffect.SoundLoader {
        private C0002c() {
        }

        /* synthetic */ C0002c(c cVar, a aVar) {
            this();
        }

        @Override // com.xiaopeng.xui.sound.XSoundEffect.SoundLoader
        public int load(d dVar) {
            if (c.this.d != null) {
                return c.this.d.load(c.this.f53a.b(), 1);
            }
            c cVar = c.this;
            cVar.a("LoaderSystem--resource:" + dVar + ",mSoundPool is null");
            return 0;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public c(d dVar) {
        super(dVar);
        XSoundEffect.SoundLoader bVar;
        a("constructor");
        int a2 = dVar.a();
        if (a2 != 0) {
            bVar = a2 == 1 ? new C0002c(this, null) : bVar;
            b();
        }
        bVar = new b(this, null);
        this.e = bVar;
        b();
    }

    private SoundPool a() {
        SoundPool.Builder builder = new SoundPool.Builder();
        builder.setMaxStreams(1);
        AudioAttributes.Builder builder2 = new AudioAttributes.Builder();
        builder2.setLegacyStreamType(this.f53a.c());
        a("createSoundPool mSoundID : " + this.f56b + ",StreamType " + this.f53a.c());
        builder.setAudioAttributes(builder2.build());
        return builder.build();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void a(String str) {
        Log.d("SoundEffectPool", str);
    }

    private void b() {
        a("init");
        this.d = a();
        c();
    }

    private void c() {
        String str;
        XSoundEffect.SoundLoader soundLoader = this.e;
        if (soundLoader != null) {
            this.f56b = soundLoader.load(this.f53a);
            str = "load mSoundID : " + this.f56b;
        } else {
            str = "load mISoundLoader is null";
        }
        a(str);
        this.d.setOnLoadCompleteListener(new a());
    }

    @Override // com.xiaopeng.xui.sound.XSoundEffect
    public void pause() {
        a("pause");
        SoundPool soundPool = this.d;
        if (soundPool != null) {
            soundPool.pause(this.c);
        }
    }

    @Override // com.xiaopeng.xui.sound.XSoundEffect
    public void play() {
        a("play");
        SoundPool soundPool = this.d;
        if (soundPool == null) {
            b();
            return;
        }
        this.f = true;
        int i = this.f56b;
        if (i == 0) {
            c();
        } else {
            this.c = soundPool.play(i, 1.0f, 1.0f, 1, 0, 1.0f);
        }
    }

    @Override // com.xiaopeng.xui.sound.XSoundEffect
    public void release() {
        a(BuildConfig.BUILD_TYPE);
        SoundPool soundPool = this.d;
        if (soundPool != null) {
            soundPool.release();
            this.d = null;
        }
    }

    @Override // com.xiaopeng.xui.sound.XSoundEffect
    public void resume() {
        a("resume");
        SoundPool soundPool = this.d;
        if (soundPool != null) {
            soundPool.resume(this.c);
        }
    }
}
