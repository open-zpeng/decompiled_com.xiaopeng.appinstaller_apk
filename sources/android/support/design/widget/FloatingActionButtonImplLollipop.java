package android.support.design.widget;

import android.graphics.Rect;
import android.graphics.drawable.InsetDrawable;
import android.os.Build;
/* loaded from: classes.dex */
class FloatingActionButtonImplLollipop extends FloatingActionButtonImpl {
    private InsetDrawable insetDrawable;

    /* JADX INFO: Access modifiers changed from: package-private */
    public FloatingActionButtonImplLollipop(VisibilityAwareImageButton view, ShadowViewDelegate shadowViewDelegate) {
        super(view, shadowViewDelegate);
    }

    @Override // android.support.design.widget.FloatingActionButtonImpl
    public float getElevation() {
        return this.view.getElevation();
    }

    @Override // android.support.design.widget.FloatingActionButtonImpl
    void onPaddingUpdated(Rect padding) {
        if (this.shadowViewDelegate.isCompatPaddingEnabled()) {
            this.insetDrawable = new InsetDrawable(this.rippleDrawable, padding.left, padding.top, padding.right, padding.bottom);
            this.shadowViewDelegate.setBackgroundDrawable(this.insetDrawable);
            return;
        }
        this.shadowViewDelegate.setBackgroundDrawable(this.rippleDrawable);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.design.widget.FloatingActionButtonImpl
    public void onDrawableStateChanged(int[] state) {
        if (Build.VERSION.SDK_INT == 21) {
            if (this.view.isEnabled()) {
                this.view.setElevation(this.elevation);
                if (this.view.isPressed()) {
                    this.view.setTranslationZ(this.pressedTranslationZ);
                    return;
                } else if (this.view.isFocused() || this.view.isHovered()) {
                    this.view.setTranslationZ(this.hoveredFocusedTranslationZ);
                    return;
                } else {
                    this.view.setTranslationZ(0.0f);
                    return;
                }
            }
            this.view.setElevation(0.0f);
            this.view.setTranslationZ(0.0f);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @Override // android.support.design.widget.FloatingActionButtonImpl
    public void jumpDrawableToCurrentState() {
    }

    @Override // android.support.design.widget.FloatingActionButtonImpl
    boolean requirePreDrawListener() {
        return false;
    }

    @Override // android.support.design.widget.FloatingActionButtonImpl
    void getPadding(Rect rect) {
        if (this.shadowViewDelegate.isCompatPaddingEnabled()) {
            float radius = this.shadowViewDelegate.getRadius();
            float maxShadowSize = getElevation() + this.pressedTranslationZ;
            int hPadding = (int) Math.ceil(ShadowDrawableWrapper.calculateHorizontalPadding(maxShadowSize, radius, false));
            int vPadding = (int) Math.ceil(ShadowDrawableWrapper.calculateVerticalPadding(maxShadowSize, radius, false));
            rect.set(hPadding, vPadding, hPadding, vPadding);
            return;
        }
        rect.set(0, 0, 0, 0);
    }
}
