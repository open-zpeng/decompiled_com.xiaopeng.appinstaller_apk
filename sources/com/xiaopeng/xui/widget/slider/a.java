package com.xiaopeng.xui.widget.slider;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import com.xiaopeng.xpui.R;
import com.xiaopeng.xui.theme.XThemeManager;
import com.xiaopeng.xui.view.XView;
/* loaded from: classes.dex */
class a extends XView {

    /* renamed from: a  reason: collision with root package name */
    private boolean f142a;

    /* renamed from: b  reason: collision with root package name */
    private Paint f143b;
    int c;
    int d;
    private ValueAnimator e;
    private float f;
    private float g;
    private float h;
    int i;
    int j;
    private Paint k;
    private boolean l;
    private float m;

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: com.xiaopeng.xui.widget.slider.a$a  reason: collision with other inner class name */
    /* loaded from: classes.dex */
    public class C0007a implements ValueAnimator.AnimatorUpdateListener {
        C0007a() {
        }

        @Override // android.animation.ValueAnimator.AnimatorUpdateListener
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            a.this.m = ((Float) valueAnimator.getAnimatedValue()).floatValue();
            a.this.invalidate();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public class b implements Animator.AnimatorListener {
        b() {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationCancel(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationEnd(Animator animator) {
            a.this.f143b.setStrokeWidth(4.0f);
            a.this.k.setMaskFilter(null);
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationRepeat(Animator animator) {
        }

        @Override // android.animation.Animator.AnimatorListener
        public void onAnimationStart(Animator animator) {
        }
    }

    /* loaded from: classes.dex */
    class c implements Runnable {
        c() {
        }

        @Override // java.lang.Runnable
        public void run() {
            a.this.invalidate();
        }
    }

    public a(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, R.style.XSliderLine);
    }

    public a(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet);
        this.c = 671088640;
        this.d = -1;
        this.f = 1.55f;
        this.g = 5.0f;
        this.h = this.g / this.f;
        this.i = 671088640;
        this.j = -15301639;
        this.l = XThemeManager.isNight(getContext());
        this.m = 1.0f;
        a(context, attributeSet);
    }

    public a(Context context, boolean z) {
        this(context, (AttributeSet) null);
        this.f142a = z;
    }

    private void a(Context context, AttributeSet attributeSet) {
        setLayerType(1, null);
        if (attributeSet != null) {
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, R.styleable.SlideLineView);
            this.i = obtainStyledAttributes.getColor(R.styleable.SlideLineView_slider_line_un_select, this.i);
            this.j = obtainStyledAttributes.getColor(R.styleable.SlideLineView_slider_line_select, this.j);
            obtainStyledAttributes.recycle();
        }
        this.f143b = new Paint(1);
        this.f143b.setStyle(Paint.Style.FILL);
        this.f143b.setStrokeCap(Paint.Cap.ROUND);
        this.f143b.setStrokeWidth(4.0f);
        this.f143b.setColor(this.j);
        this.k = new Paint(1);
        this.k.setStyle(Paint.Style.FILL);
        this.k.setStrokeCap(Paint.Cap.ROUND);
        this.k.setStrokeWidth(4.0f);
        this.k.setColor(4);
        this.e = ValueAnimator.ofFloat(0.0f, 2.0f, 1.0f);
        this.e.setDuration(800L);
        this.e.addUpdateListener(new C0007a());
        this.e.setInterpolator(new DecelerateInterpolator());
        this.e.addListener(new b());
        setEnabled(true);
    }

    private void b(boolean z) {
        this.i = z ? 671088640 : 503316480;
        this.j = z ? this.j | (-1291845632) : this.j & 1291845631;
    }

    public void a(boolean z) {
        Paint paint;
        int i;
        this.f142a = z;
        this.f143b.setStrokeWidth(z ? 2.0f : 4.0f);
        if (z) {
            if (this.l) {
                paint = this.k;
                i = this.j;
            } else {
                paint = this.k;
                i = this.d;
            }
            paint.setColor(i);
            this.k.setMaskFilter(new BlurMaskFilter(4.0f, BlurMaskFilter.Blur.NORMAL));
        }
        if (z) {
            this.e.start();
        } else {
            this.e.cancel();
        }
        invalidate();
    }

    public boolean a() {
        return this.f142a;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // com.xiaopeng.xui.view.XView, android.view.View
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        this.l = XThemeManager.isNight(getContext());
        postDelayed(new c(), 500L);
    }

    @Override // android.view.View
    protected void onDraw(Canvas canvas) {
        float width;
        float height;
        float width2;
        float height2;
        Paint paint;
        super.onDraw(canvas);
        if (this.f142a) {
            if (!this.l) {
                this.f143b.setColor(this.d);
                this.k.setColor(this.d);
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, this.m * 2.0f, this.f143b);
                canvas.drawCircle(getWidth() / 2, getHeight() / 2, this.m * 2.0f, this.k);
                return;
            }
            this.f143b.setColor(this.j);
            this.k.setColor(this.j);
            canvas.drawLine((getWidth() / 2) - (this.h * this.m), (getHeight() / 2) + (this.g * this.m), (getWidth() / 2) + (this.h * this.m), (getHeight() / 2) - (this.g * this.m), this.f143b);
            width = (getWidth() / 2) - (this.h * this.m);
            height = (getHeight() / 2) + (this.g * this.m);
            width2 = (getWidth() / 2) + (this.h * this.m);
            height2 = (getHeight() / 2) - (this.g * this.m);
            paint = this.k;
        } else if (!this.l) {
            this.f143b.setColor(this.c);
            canvas.drawCircle(getWidth() / 2, getHeight() / 2, 2.0f, this.f143b);
            return;
        } else {
            this.f143b.setColor(this.i);
            width = (getWidth() / 2) - this.h;
            height = (getHeight() / 2) + this.g;
            width2 = (getWidth() / 2) + this.h;
            height2 = (getHeight() / 2) - this.g;
            paint = this.f143b;
        }
        canvas.drawLine(width, height, width2, height2, paint);
    }

    @Override // android.view.View
    protected void onMeasure(int i, int i2) {
        super.onMeasure(i, i2);
        setMeasuredDimension(22, 40);
    }

    @Override // android.view.View
    public void setEnabled(boolean z) {
        b(z);
        invalidate();
    }
}
