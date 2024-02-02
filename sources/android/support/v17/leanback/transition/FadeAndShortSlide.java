package android.support.v17.leanback.transition;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.TimeInterpolator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.v17.leanback.R;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.transition.Visibility;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
/* loaded from: classes.dex */
public class FadeAndShortSlide extends Visibility {
    private float mDistance;
    private Visibility mFade;
    private CalculateSlide mSlideCalculator;
    final CalculateSlide sCalculateTopBottom;
    private static final TimeInterpolator sDecelerate = new DecelerateInterpolator();
    static final CalculateSlide sCalculateStart = new CalculateSlide() { // from class: android.support.v17.leanback.transition.FadeAndShortSlide.1
        @Override // android.support.v17.leanback.transition.FadeAndShortSlide.CalculateSlide
        public float getGoneX(FadeAndShortSlide t, ViewGroup sceneRoot, View view, int[] position) {
            boolean isRtl = sceneRoot.getLayoutDirection() == 1;
            if (isRtl) {
                float x = view.getTranslationX() + t.getHorizontalDistance(sceneRoot);
                return x;
            }
            float x2 = view.getTranslationX();
            return x2 - t.getHorizontalDistance(sceneRoot);
        }
    };
    static final CalculateSlide sCalculateEnd = new CalculateSlide() { // from class: android.support.v17.leanback.transition.FadeAndShortSlide.2
        @Override // android.support.v17.leanback.transition.FadeAndShortSlide.CalculateSlide
        public float getGoneX(FadeAndShortSlide t, ViewGroup sceneRoot, View view, int[] position) {
            boolean isRtl = sceneRoot.getLayoutDirection() == 1;
            if (isRtl) {
                float x = view.getTranslationX() - t.getHorizontalDistance(sceneRoot);
                return x;
            }
            float x2 = view.getTranslationX();
            return x2 + t.getHorizontalDistance(sceneRoot);
        }
    };
    static final CalculateSlide sCalculateStartEnd = new CalculateSlide() { // from class: android.support.v17.leanback.transition.FadeAndShortSlide.3
        @Override // android.support.v17.leanback.transition.FadeAndShortSlide.CalculateSlide
        public float getGoneX(FadeAndShortSlide t, ViewGroup sceneRoot, View view, int[] position) {
            int sceneRootCenter;
            int viewCenter = position[0] + (view.getWidth() / 2);
            sceneRoot.getLocationOnScreen(position);
            Rect center = t.getEpicenter();
            if (center == null) {
                sceneRootCenter = position[0] + (sceneRoot.getWidth() / 2);
            } else {
                sceneRootCenter = center.centerX();
            }
            if (viewCenter < sceneRootCenter) {
                return view.getTranslationX() - t.getHorizontalDistance(sceneRoot);
            }
            return view.getTranslationX() + t.getHorizontalDistance(sceneRoot);
        }
    };
    static final CalculateSlide sCalculateBottom = new CalculateSlide() { // from class: android.support.v17.leanback.transition.FadeAndShortSlide.4
        @Override // android.support.v17.leanback.transition.FadeAndShortSlide.CalculateSlide
        public float getGoneY(FadeAndShortSlide t, ViewGroup sceneRoot, View view, int[] position) {
            return view.getTranslationY() + t.getVerticalDistance(sceneRoot);
        }
    };
    static final CalculateSlide sCalculateTop = new CalculateSlide() { // from class: android.support.v17.leanback.transition.FadeAndShortSlide.5
        @Override // android.support.v17.leanback.transition.FadeAndShortSlide.CalculateSlide
        public float getGoneY(FadeAndShortSlide t, ViewGroup sceneRoot, View view, int[] position) {
            return view.getTranslationY() - t.getVerticalDistance(sceneRoot);
        }
    };

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static abstract class CalculateSlide {
        CalculateSlide() {
        }

        float getGoneX(FadeAndShortSlide t, ViewGroup sceneRoot, View view, int[] position) {
            return view.getTranslationX();
        }

        float getGoneY(FadeAndShortSlide t, ViewGroup sceneRoot, View view, int[] position) {
            return view.getTranslationY();
        }
    }

    float getHorizontalDistance(ViewGroup sceneRoot) {
        return this.mDistance >= 0.0f ? this.mDistance : sceneRoot.getWidth() / 4;
    }

    float getVerticalDistance(ViewGroup sceneRoot) {
        return this.mDistance >= 0.0f ? this.mDistance : sceneRoot.getHeight() / 4;
    }

    public FadeAndShortSlide() {
        this(8388611);
    }

    public FadeAndShortSlide(int slideEdge) {
        this.mFade = new Fade();
        this.mDistance = -1.0f;
        this.sCalculateTopBottom = new CalculateSlide() { // from class: android.support.v17.leanback.transition.FadeAndShortSlide.6
            @Override // android.support.v17.leanback.transition.FadeAndShortSlide.CalculateSlide
            public float getGoneY(FadeAndShortSlide t, ViewGroup sceneRoot, View view, int[] position) {
                int sceneRootCenter;
                int viewCenter = position[1] + (view.getHeight() / 2);
                sceneRoot.getLocationOnScreen(position);
                Rect center = FadeAndShortSlide.this.getEpicenter();
                if (center == null) {
                    sceneRootCenter = position[1] + (sceneRoot.getHeight() / 2);
                } else {
                    sceneRootCenter = center.centerY();
                }
                if (viewCenter < sceneRootCenter) {
                    return view.getTranslationY() - t.getVerticalDistance(sceneRoot);
                }
                return view.getTranslationY() + t.getVerticalDistance(sceneRoot);
            }
        };
        setSlideEdge(slideEdge);
    }

    public FadeAndShortSlide(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mFade = new Fade();
        this.mDistance = -1.0f;
        this.sCalculateTopBottom = new CalculateSlide() { // from class: android.support.v17.leanback.transition.FadeAndShortSlide.6
            @Override // android.support.v17.leanback.transition.FadeAndShortSlide.CalculateSlide
            public float getGoneY(FadeAndShortSlide t, ViewGroup sceneRoot, View view, int[] position) {
                int sceneRootCenter;
                int viewCenter = position[1] + (view.getHeight() / 2);
                sceneRoot.getLocationOnScreen(position);
                Rect center = FadeAndShortSlide.this.getEpicenter();
                if (center == null) {
                    sceneRootCenter = position[1] + (sceneRoot.getHeight() / 2);
                } else {
                    sceneRootCenter = center.centerY();
                }
                if (viewCenter < sceneRootCenter) {
                    return view.getTranslationY() - t.getVerticalDistance(sceneRoot);
                }
                return view.getTranslationY() + t.getVerticalDistance(sceneRoot);
            }
        };
        TypedArray a2 = context.obtainStyledAttributes(attrs, R.styleable.lbSlide);
        int edge = a2.getInt(R.styleable.lbSlide_lb_slideEdge, 8388611);
        setSlideEdge(edge);
        a2.recycle();
    }

    @Override // android.transition.Transition
    public void setEpicenterCallback(Transition.EpicenterCallback epicenterCallback) {
        this.mFade.setEpicenterCallback(epicenterCallback);
        super.setEpicenterCallback(epicenterCallback);
    }

    private void captureValues(TransitionValues transitionValues) {
        View view = transitionValues.view;
        int[] position = new int[2];
        view.getLocationOnScreen(position);
        transitionValues.values.put("android:fadeAndShortSlideTransition:screenPosition", position);
    }

    @Override // android.transition.Visibility, android.transition.Transition
    public void captureStartValues(TransitionValues transitionValues) {
        this.mFade.captureStartValues(transitionValues);
        super.captureStartValues(transitionValues);
        captureValues(transitionValues);
    }

    @Override // android.transition.Visibility, android.transition.Transition
    public void captureEndValues(TransitionValues transitionValues) {
        this.mFade.captureEndValues(transitionValues);
        super.captureEndValues(transitionValues);
        captureValues(transitionValues);
    }

    public void setSlideEdge(int slideEdge) {
        if (slideEdge == 48) {
            this.mSlideCalculator = sCalculateTop;
        } else if (slideEdge == 80) {
            this.mSlideCalculator = sCalculateBottom;
        } else if (slideEdge == 112) {
            this.mSlideCalculator = this.sCalculateTopBottom;
        } else if (slideEdge == 8388611) {
            this.mSlideCalculator = sCalculateStart;
        } else if (slideEdge == 8388613) {
            this.mSlideCalculator = sCalculateEnd;
        } else if (slideEdge == 8388615) {
            this.mSlideCalculator = sCalculateStartEnd;
        } else {
            throw new IllegalArgumentException("Invalid slide direction");
        }
    }

    @Override // android.transition.Visibility
    public Animator onAppear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        if (endValues == null || sceneRoot == view) {
            return null;
        }
        int[] position = (int[]) endValues.values.get("android:fadeAndShortSlideTransition:screenPosition");
        int left = position[0];
        int top = position[1];
        float endX = view.getTranslationX();
        float startX = this.mSlideCalculator.getGoneX(this, sceneRoot, view, position);
        float endY = view.getTranslationY();
        float startY = this.mSlideCalculator.getGoneY(this, sceneRoot, view, position);
        Animator slideAnimator = TranslationAnimationCreator.createAnimation(view, endValues, left, top, startX, startY, endX, endY, sDecelerate, this);
        Animator fadeAnimator = this.mFade.onAppear(sceneRoot, view, startValues, endValues);
        if (slideAnimator == null) {
            return fadeAnimator;
        }
        if (fadeAnimator == null) {
            return slideAnimator;
        }
        AnimatorSet set = new AnimatorSet();
        set.play(slideAnimator).with(fadeAnimator);
        return set;
    }

    @Override // android.transition.Visibility
    public Animator onDisappear(ViewGroup sceneRoot, View view, TransitionValues startValues, TransitionValues endValues) {
        if (startValues == null || sceneRoot == view) {
            return null;
        }
        int[] position = (int[]) startValues.values.get("android:fadeAndShortSlideTransition:screenPosition");
        int left = position[0];
        int top = position[1];
        float startX = view.getTranslationX();
        float endX = this.mSlideCalculator.getGoneX(this, sceneRoot, view, position);
        float startY = view.getTranslationY();
        float endY = this.mSlideCalculator.getGoneY(this, sceneRoot, view, position);
        Animator slideAnimator = TranslationAnimationCreator.createAnimation(view, startValues, left, top, startX, startY, endX, endY, sDecelerate, this);
        Animator fadeAnimator = this.mFade.onDisappear(sceneRoot, view, startValues, endValues);
        if (slideAnimator == null) {
            return fadeAnimator;
        }
        if (fadeAnimator == null) {
            return slideAnimator;
        }
        AnimatorSet set = new AnimatorSet();
        set.play(slideAnimator).with(fadeAnimator);
        return set;
    }

    @Override // android.transition.Transition
    public Transition addListener(Transition.TransitionListener listener) {
        this.mFade.addListener(listener);
        return super.addListener(listener);
    }

    @Override // android.transition.Transition
    public Transition removeListener(Transition.TransitionListener listener) {
        this.mFade.removeListener(listener);
        return super.removeListener(listener);
    }

    @Override // android.transition.Transition
    public Transition clone() {
        FadeAndShortSlide clone = (FadeAndShortSlide) super.clone();
        clone.mFade = (Visibility) this.mFade.clone();
        return clone;
    }
}
