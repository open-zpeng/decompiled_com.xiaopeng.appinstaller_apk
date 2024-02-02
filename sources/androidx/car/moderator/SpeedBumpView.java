package androidx.car.moderator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
/* loaded from: classes.dex */
public class SpeedBumpView extends FrameLayout {
    private final SpeedBumpController mSpeedBumpController;

    public SpeedBumpView(Context context) {
        super(context);
        this.mSpeedBumpController = new SpeedBumpController(this);
        addView(this.mSpeedBumpController.getLockoutMessageView());
    }

    public SpeedBumpView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mSpeedBumpController = new SpeedBumpController(this);
        addView(this.mSpeedBumpController.getLockoutMessageView());
    }

    public SpeedBumpView(Context context, AttributeSet attrs, int defStyleAttrs) {
        super(context, attrs, defStyleAttrs);
        this.mSpeedBumpController = new SpeedBumpController(this);
        addView(this.mSpeedBumpController.getLockoutMessageView());
    }

    public SpeedBumpView(Context context, AttributeSet attrs, int defStyleAttrs, int defStyleRes) {
        super(context, attrs, defStyleAttrs, defStyleRes);
        this.mSpeedBumpController = new SpeedBumpController(this);
        addView(this.mSpeedBumpController.getLockoutMessageView());
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.mSpeedBumpController.start();
    }

    @Override // android.view.ViewGroup, android.view.View
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.mSpeedBumpController.stop();
    }

    @Override // android.view.ViewGroup
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        this.mSpeedBumpController.getLockoutMessageView().bringToFront();
    }

    @Override // android.view.ViewGroup, android.view.View
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return this.mSpeedBumpController.onTouchEvent(ev) && super.dispatchTouchEvent(ev);
    }
}
