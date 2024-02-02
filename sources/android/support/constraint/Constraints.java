package android.support.constraint;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.ViewGroup;
/* loaded from: classes.dex */
public class Constraints extends ViewGroup {
    ConstraintSet myConstraintSet;

    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* loaded from: classes.dex */
    public static class LayoutParams extends ConstraintLayout.LayoutParams {
        public float alpha;
        public boolean applyElevation;
        public float elevation;
        public float rotation;
        public float rotationX;
        public float rotationY;
        public float scaleX;
        public float scaleY;
        public float transformPivotX;
        public float transformPivotY;
        public float translationX;
        public float translationY;
        public float translationZ;

        public LayoutParams(int width, int height) {
            super(width, height);
            this.alpha = 1.0f;
            this.applyElevation = false;
            this.elevation = 0.0f;
            this.rotation = 0.0f;
            this.rotationX = 0.0f;
            this.rotationY = 0.0f;
            this.scaleX = 1.0f;
            this.scaleY = 1.0f;
            this.transformPivotX = 0.0f;
            this.transformPivotY = 0.0f;
            this.translationX = 0.0f;
            this.translationY = 0.0f;
            this.translationZ = 0.0f;
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            this.alpha = 1.0f;
            this.applyElevation = false;
            this.elevation = 0.0f;
            this.rotation = 0.0f;
            this.rotationX = 0.0f;
            this.rotationY = 0.0f;
            this.scaleX = 1.0f;
            this.scaleY = 1.0f;
            this.transformPivotX = 0.0f;
            this.transformPivotY = 0.0f;
            this.translationX = 0.0f;
            this.translationY = 0.0f;
            this.translationZ = 0.0f;
            TypedArray a2 = c.obtainStyledAttributes(attrs, R.styleable.ConstraintSet);
            int N = a2.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a2.getIndex(i);
                if (attr == R.styleable.ConstraintSet_android_alpha) {
                    this.alpha = a2.getFloat(attr, this.alpha);
                } else if (attr == R.styleable.ConstraintSet_android_elevation) {
                    this.elevation = a2.getFloat(attr, this.elevation);
                    this.applyElevation = true;
                } else if (attr == R.styleable.ConstraintSet_android_rotationX) {
                    this.rotationX = a2.getFloat(attr, this.rotationX);
                } else if (attr == R.styleable.ConstraintSet_android_rotationY) {
                    this.rotationY = a2.getFloat(attr, this.rotationY);
                } else if (attr == R.styleable.ConstraintSet_android_rotation) {
                    this.rotation = a2.getFloat(attr, this.rotation);
                } else if (attr == R.styleable.ConstraintSet_android_scaleX) {
                    this.scaleX = a2.getFloat(attr, this.scaleX);
                } else if (attr == R.styleable.ConstraintSet_android_scaleY) {
                    this.scaleY = a2.getFloat(attr, this.scaleY);
                } else if (attr == R.styleable.ConstraintSet_android_transformPivotX) {
                    this.transformPivotX = a2.getFloat(attr, this.transformPivotX);
                } else if (attr == R.styleable.ConstraintSet_android_transformPivotY) {
                    this.transformPivotY = a2.getFloat(attr, this.transformPivotY);
                } else if (attr == R.styleable.ConstraintSet_android_translationX) {
                    this.translationX = a2.getFloat(attr, this.translationX);
                } else if (attr == R.styleable.ConstraintSet_android_translationY) {
                    this.translationY = a2.getFloat(attr, this.translationY);
                } else if (attr == R.styleable.ConstraintSet_android_translationZ) {
                    this.translationX = a2.getFloat(attr, this.translationZ);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new ConstraintLayout.LayoutParams(p);
    }

    public ConstraintSet getConstraintSet() {
        if (this.myConstraintSet == null) {
            this.myConstraintSet = new ConstraintSet();
        }
        this.myConstraintSet.clone(this);
        return this.myConstraintSet;
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int l, int t, int r, int b2) {
    }
}
