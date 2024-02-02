package com.xiaopeng.xui.drawable.ripple;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.StateSet;
import com.xiaopeng.xpui.R;
import org.xmlpull.v1.XmlPullParser;
/* loaded from: classes.dex */
public class XRippleDrawable extends Drawable {
    private static final String TAG = XRippleDrawable.class.getSimpleName();
    private boolean mIsTouchDown;
    private float mDownX = -1.0f;
    private float mDownY = -1.0f;
    private int[] stateSpecPress = new int[2];
    private XRipple mRipple = new XRipple();

    public XRippleDrawable() {
        int[] iArr = this.stateSpecPress;
        iArr[0] = 16842919;
        iArr[1] = 16842910;
    }

    private void inflateAttrs(Resources resources, AttributeSet attributeSet, Resources.Theme theme) {
        if (resources == null || attributeSet == null) {
            return;
        }
        TypedArray obtainStyledAttributes = theme != null ? theme.obtainStyledAttributes(attributeSet, R.styleable.XRippleDrawable, 0, 0) : resources.obtainAttributes(attributeSet, R.styleable.XRippleDrawable);
        int color = obtainStyledAttributes.getColor(R.styleable.XRippleDrawable_rippleBackgroundColor, 0);
        int color2 = obtainStyledAttributes.getColor(R.styleable.XRippleDrawable_rippleColor, 0);
        boolean z = obtainStyledAttributes.getBoolean(R.styleable.XRippleDrawable_rippleSupportScale, false);
        int i = obtainStyledAttributes.getInt(R.styleable.XRippleDrawable_rippleDuration, resources.getInteger(R.integer.x_ripple_default_anim));
        this.mRipple.setRippleRadius(obtainStyledAttributes.getDimensionPixelSize(R.styleable.XRippleDrawable_rippleRadius, 0));
        if (color2 != 0) {
            this.mRipple.setRippleColor(color2);
        }
        this.mRipple.setRippleBackgroundColor(color);
        this.mRipple.setSupportScale(z);
        long j = i;
        this.mRipple.setPressDuration(j);
        this.mRipple.setUpDuration(j);
        obtainStyledAttributes.recycle();
    }

    private void initRipple(Rect rect) {
        this.mRipple.setCallback(getCallback());
        this.mRipple.setRippleRect(new RectF(rect.left, rect.top, rect.right, rect.bottom));
    }

    @Override // android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        XRipple xRipple = this.mRipple;
        if (xRipple != null) {
            xRipple.drawRipple(canvas);
        }
    }

    @Override // android.graphics.drawable.Drawable
    public int getOpacity() {
        return 0;
    }

    public XRipple getXRipple() {
        return this.mRipple;
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet) {
        inflateAttrs(resources, attributeSet, null);
        super.inflate(resources, xmlPullParser, attributeSet);
    }

    @Override // android.graphics.drawable.Drawable
    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) {
        inflateAttrs(resources, attributeSet, theme);
        super.inflate(resources, xmlPullParser, attributeSet, theme);
    }

    @Override // android.graphics.drawable.Drawable
    public boolean isStateful() {
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    protected void onBoundsChange(Rect rect) {
        super.onBoundsChange(rect);
        initRipple(rect);
    }

    @Override // android.graphics.drawable.Drawable
    protected boolean onStateChange(int[] iArr) {
        if (iArr != null && this.mRipple != null) {
            if (StateSet.stateSetMatches(this.stateSpecPress, iArr) && !this.mIsTouchDown) {
                float f = this.mDownX;
                if (f > 0.0f) {
                    float f2 = this.mDownY;
                    if (f2 > 0.0f) {
                        this.mIsTouchDown = true;
                        this.mRipple.pressDown(f, f2);
                        return true;
                    }
                }
            }
            if (this.mIsTouchDown) {
                this.mIsTouchDown = false;
                this.mRipple.pressUp();
            }
        }
        return true;
    }

    @Override // android.graphics.drawable.Drawable
    public void setAlpha(int i) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setColorFilter(ColorFilter colorFilter) {
    }

    @Override // android.graphics.drawable.Drawable
    public void setHotspot(float f, float f2) {
        super.setHotspot(f, f2);
        this.mDownX = f;
        this.mDownY = f2;
    }

    @Override // android.graphics.drawable.Drawable
    public boolean setVisible(boolean z, boolean z2) {
        boolean visible = super.setVisible(z, z2);
        XRipple xRipple = this.mRipple;
        if (xRipple != null && visible) {
            xRipple.setVisible(z);
        }
        return visible;
    }
}
