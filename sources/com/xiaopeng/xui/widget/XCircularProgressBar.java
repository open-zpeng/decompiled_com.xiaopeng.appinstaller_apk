package com.xiaopeng.xui.widget;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.RotateDrawable;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import com.xiaopeng.libtheme.ThemeManager;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.Xui;
import com.xiaopeng.xui.drawable.progress.XCircularProgressBgDrawable;
import com.xiaopeng.xui.drawable.progress.XCircularProgressIndeterminateDrawable;
import com.xiaopeng.xui.drawable.progress.XCircularProgressPgDrawable;
import com.xiaopeng.xui.view.XViewDelegate;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
/* loaded from: classes.dex */
public class XCircularProgressBar extends ProgressBar {
    public static final int INDICATOR_TYPE_PAUSED = 0;
    public static final int INDICATOR_TYPE_PLAYING_PAUSE = 2;
    public static final int INDICATOR_TYPE_PLAYING_STOP = 1;
    public static final int INDICATOR_TYPE_START_DOWNLOAD = 3;
    public static final float OPAQUE = 1.0f;
    public static final float TRANSLUCENT = 0.16f;
    private XCircularProgressBgDrawable mCircularBgDrawable;
    private XCircularProgressIndeterminateDrawable mCircularIndeterminateDrawable;
    private XCircularProgressPgDrawable mCircularPgDrawable;
    private boolean mEnableIndicator;
    private int mIndeterminateDrId;
    private Drawable mIndicatorPause;
    private int mIndicatorPauseId;
    private Drawable mIndicatorPlay;
    private int mIndicatorPlayId;
    private Drawable mIndicatorStart;
    private int mIndicatorStartId;
    private Drawable mIndicatorStop;
    private int mIndicatorStopId;
    private int mIndicatorType;
    private float mInset;
    private float mLightRadius;
    private float mStrokeWidth;
    private XViewDelegate mXViewDelegate;
    private static final int[] STATE_PLAYING_STOP = {R.attr.progress_state_playing_stop};
    private static final int[] STATE_PLAYING_PAUSE = {R.attr.progress_state_playing_pause};
    private static final int[] STATE_PAUSED = {R.attr.progress_state_paused};
    private static final int[] STATE_START_DOWNLOAD = {R.attr.progress_state_start_download};

    @Retention(RetentionPolicy.SOURCE)
    /* loaded from: classes.dex */
    public @interface IndicatorType {
    }

    public XCircularProgressBar(Context context) {
        this(context, null);
    }

    public XCircularProgressBar(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.style.XProgressBar_Circular_Medium);
    }

    public XCircularProgressBar(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, R.style.XProgressBar_Circular_Medium);
    }

    public XCircularProgressBar(Context context, AttributeSet attributeSet, int i, int i2) {
        super(context, attributeSet, i, i2);
        this.mEnableIndicator = false;
        init(attributeSet, i, i2);
    }

    public static Drawable getDrawable(int i) {
        return Xui.getContext().getResources().getDrawable(i, null);
    }

    private void init(AttributeSet attributeSet, int i, int i2) {
        if (attributeSet == null) {
            return;
        }
        this.mXViewDelegate = XViewDelegate.create(this, attributeSet);
        TypedArray obtainStyledAttributes = getContext().obtainStyledAttributes(attributeSet, R.styleable.XCircularProgressBar, i, i2);
        this.mEnableIndicator = obtainStyledAttributes.getBoolean(R.styleable.XCircularProgressBar_progress_enableIndicator, false);
        this.mIndicatorType = obtainStyledAttributes.getInt(R.styleable.XCircularProgressBar_progress_indicatorType, 0);
        this.mInset = obtainStyledAttributes.getDimensionPixelSize(R.styleable.XCircularProgressBar_progress_inset, 0);
        this.mStrokeWidth = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.XCircularProgressBar_progress_strokeWidth, 0);
        boolean z = obtainStyledAttributes.getBoolean(R.styleable.XCircularProgressBar_android_enabled, true);
        this.mLightRadius = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.XCircularProgressBar_progress_light_radius, 0);
        this.mIndicatorPlay = obtainStyledAttributes.getDrawable(R.styleable.XCircularProgressBar_progress_indicator_play);
        this.mIndicatorPause = obtainStyledAttributes.getDrawable(R.styleable.XCircularProgressBar_progress_indicator_pause);
        this.mIndicatorStop = obtainStyledAttributes.getDrawable(R.styleable.XCircularProgressBar_progress_indicator_stop);
        this.mIndicatorStart = obtainStyledAttributes.getDrawable(R.styleable.XCircularProgressBar_progress_indicator_start);
        this.mIndicatorPlayId = obtainStyledAttributes.getResourceId(R.styleable.XCircularProgressBar_progress_indicator_play, 0);
        this.mIndicatorPauseId = obtainStyledAttributes.getResourceId(R.styleable.XCircularProgressBar_progress_indicator_pause, 0);
        this.mIndicatorStopId = obtainStyledAttributes.getResourceId(R.styleable.XCircularProgressBar_progress_indicator_stop, 0);
        this.mIndicatorStartId = obtainStyledAttributes.getResourceId(R.styleable.XCircularProgressBar_progress_indicator_start, 0);
        this.mIndeterminateDrId = obtainStyledAttributes.getResourceId(R.styleable.XCircularProgressBar_android_indeterminateDrawable, 0);
        obtainStyledAttributes.recycle();
        setEnabled(z);
        if (this.mIndicatorType == 3) {
            super.setProgress(getMax());
        }
        setProgressDrawableProp();
        setIndeterminateProp();
    }

    private void parseIndeterminateDrawable(Drawable drawable) {
        if (drawable instanceof RotateDrawable) {
            Drawable drawable2 = ((RotateDrawable) drawable).getDrawable();
            if (drawable2 instanceof XCircularProgressIndeterminateDrawable) {
                this.mCircularIndeterminateDrawable = (XCircularProgressIndeterminateDrawable) drawable2;
            }
        }
    }

    private void parseProgressDrawable(Drawable drawable) {
        if (drawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) drawable;
            Drawable findDrawableByLayerId = layerDrawable.findDrawableByLayerId(16908301);
            if (findDrawableByLayerId instanceof XCircularProgressPgDrawable) {
                this.mCircularPgDrawable = (XCircularProgressPgDrawable) findDrawableByLayerId;
            }
            Drawable findDrawableByLayerId2 = layerDrawable.findDrawableByLayerId(16908288);
            if (findDrawableByLayerId2 instanceof XCircularProgressBgDrawable) {
                this.mCircularBgDrawable = (XCircularProgressBgDrawable) findDrawableByLayerId2;
            }
        }
    }

    private void setIndeterminateProp() {
        XCircularProgressIndeterminateDrawable xCircularProgressIndeterminateDrawable = this.mCircularIndeterminateDrawable;
        if (xCircularProgressIndeterminateDrawable != null) {
            xCircularProgressIndeterminateDrawable.setInset(this.mInset);
            this.mCircularIndeterminateDrawable.setStrokeWidth(this.mStrokeWidth);
            this.mCircularIndeterminateDrawable.setLightRadius(this.mLightRadius);
        }
    }

    private void setProgressDrawableProp() {
        XCircularProgressPgDrawable xCircularProgressPgDrawable = this.mCircularPgDrawable;
        if (xCircularProgressPgDrawable != null) {
            xCircularProgressPgDrawable.setInset(this.mInset);
            this.mCircularPgDrawable.setStrokeWidth(this.mStrokeWidth);
            this.mCircularPgDrawable.setLightRadius(this.mLightRadius);
        }
        XCircularProgressBgDrawable xCircularProgressBgDrawable = this.mCircularBgDrawable;
        if (xCircularProgressBgDrawable != null) {
            xCircularProgressBgDrawable.setInset(this.mInset);
            this.mCircularBgDrawable.setStrokeWidth(this.mStrokeWidth);
            this.mCircularBgDrawable.setIndicatorPlay(this.mIndicatorPlay);
            this.mCircularBgDrawable.setIndicatorPause(this.mIndicatorPause);
            this.mCircularBgDrawable.setIndicatorStop(this.mIndicatorStop);
            this.mCircularBgDrawable.setIndicatorStart(this.mIndicatorStart);
        }
    }

    @Override // android.widget.ProgressBar, android.view.View
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        setAlpha(isEnabled() ? 1.0f : 0.16f);
    }

    public int getIndicatorType() {
        return this.mIndicatorType;
    }

    @Override // android.widget.ProgressBar, android.view.View
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mXViewDelegate.onAttachedToWindow();
    }

    @Override // android.view.View
    protected void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        if (ThemeManager.isThemeChanged(configuration)) {
            int i = this.mIndicatorStartId;
            if (i != 0) {
                this.mIndicatorStart = getDrawable(i);
            }
            int i2 = this.mIndicatorPauseId;
            if (i2 != 0) {
                this.mIndicatorPause = getDrawable(i2);
            }
            int i3 = this.mIndicatorStopId;
            if (i3 != 0) {
                this.mIndicatorStop = getDrawable(i3);
            }
            int i4 = this.mIndicatorPlayId;
            if (i4 != 0) {
                this.mIndicatorPlay = getDrawable(i4);
            }
            int i5 = this.mIndeterminateDrId;
            if (i5 != 0) {
                setIndeterminateDrawable(getDrawable(i5));
            }
        }
        this.mXViewDelegate.onConfigurationChanged(configuration);
    }

    @Override // android.view.View
    protected int[] onCreateDrawableState(int i) {
        int[] iArr;
        if (isIndeterminate() || !this.mEnableIndicator) {
            return super.onCreateDrawableState(i);
        }
        int[] onCreateDrawableState = super.onCreateDrawableState(i + 1);
        int i2 = this.mIndicatorType;
        if (i2 == 0) {
            iArr = STATE_PAUSED;
        } else if (i2 == 1) {
            iArr = STATE_PLAYING_STOP;
        } else if (i2 == 2) {
            iArr = STATE_PLAYING_PAUSE;
        } else if (i2 != 3) {
            return onCreateDrawableState;
        } else {
            iArr = STATE_START_DOWNLOAD;
        }
        ProgressBar.mergeDrawableStates(onCreateDrawableState, iArr);
        return onCreateDrawableState;
    }

    @Override // android.widget.ProgressBar, android.view.View
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mXViewDelegate.onDetachedFromWindow();
    }

    public void setEnableIndicator(boolean z) {
        this.mEnableIndicator = z;
        refreshDrawableState();
    }

    @Override // android.widget.ProgressBar
    public synchronized void setIndeterminate(boolean z) {
        super.setIndeterminate(z);
        if (!z) {
            refreshDrawableState();
        }
    }

    @Override // android.widget.ProgressBar
    public void setIndeterminateDrawable(Drawable drawable) {
        parseIndeterminateDrawable(drawable);
        setIndeterminateProp();
        super.setIndeterminateDrawable(drawable);
    }

    public void setIndicatorType(int i) {
        if (this.mIndicatorType == 3) {
            super.setProgress(getMin());
        }
        if (i == 3) {
            super.setProgress(getMax());
        }
        this.mIndicatorType = i;
        refreshDrawableState();
    }

    @Override // android.widget.ProgressBar
    public synchronized void setProgress(int i) {
        if (this.mIndicatorType == 3) {
            i = getMax();
        }
        super.setProgress(i);
    }

    @Override // android.widget.ProgressBar
    public void setProgressDrawable(Drawable drawable) {
        parseProgressDrawable(drawable);
        setProgressDrawableProp();
        super.setProgressDrawable(drawable);
    }
}
