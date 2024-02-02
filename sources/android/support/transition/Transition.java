package android.support.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TimeInterpolator;
import android.graphics.Path;
import android.support.v4.util.ArrayMap;
import android.support.v4.util.LongSparseArray;
import android.support.v4.view.ViewCompat;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import xiaopeng.widget.BuildConfig;
/* loaded from: classes.dex */
public abstract class Transition implements Cloneable {
    private static final int[] DEFAULT_MATCH_ORDER = {2, 1, 3, 4};
    private static final PathMotion STRAIGHT_PATH_MOTION = new PathMotion() { // from class: android.support.transition.Transition.1
        @Override // android.support.transition.PathMotion
        public Path getPath(float startX, float startY, float endX, float endY) {
            Path path = new Path();
            path.moveTo(startX, startY);
            path.lineTo(endX, endY);
            return path;
        }
    };
    private static ThreadLocal<ArrayMap<Animator, AnimationInfo>> sRunningAnimators = new ThreadLocal<>();
    private ArrayList<TransitionValues> mEndValuesList;
    private EpicenterCallback mEpicenterCallback;
    private ArrayMap<String, String> mNameOverrides;
    TransitionPropagation mPropagation;
    private ArrayList<TransitionValues> mStartValuesList;
    private String mName = getClass().getName();
    private long mStartDelay = -1;
    long mDuration = -1;
    private TimeInterpolator mInterpolator = null;
    ArrayList<Integer> mTargetIds = new ArrayList<>();
    ArrayList<View> mTargets = new ArrayList<>();
    private ArrayList<String> mTargetNames = null;
    private ArrayList<Class> mTargetTypes = null;
    private ArrayList<Integer> mTargetIdExcludes = null;
    private ArrayList<View> mTargetExcludes = null;
    private ArrayList<Class> mTargetTypeExcludes = null;
    private ArrayList<String> mTargetNameExcludes = null;
    private ArrayList<Integer> mTargetIdChildExcludes = null;
    private ArrayList<View> mTargetChildExcludes = null;
    private ArrayList<Class> mTargetTypeChildExcludes = null;
    private TransitionValuesMaps mStartValues = new TransitionValuesMaps();
    private TransitionValuesMaps mEndValues = new TransitionValuesMaps();
    TransitionSet mParent = null;
    private int[] mMatchOrder = DEFAULT_MATCH_ORDER;
    private ViewGroup mSceneRoot = null;
    boolean mCanRemoveViews = false;
    private ArrayList<Animator> mCurrentAnimators = new ArrayList<>();
    private int mNumInstances = 0;
    private boolean mPaused = false;
    private boolean mEnded = false;
    private ArrayList<TransitionListener> mListeners = null;
    private ArrayList<Animator> mAnimators = new ArrayList<>();
    private PathMotion mPathMotion = STRAIGHT_PATH_MOTION;

    /* loaded from: classes.dex */
    public static abstract class EpicenterCallback {
    }

    /* loaded from: classes.dex */
    public interface TransitionListener {
        void onTransitionEnd(Transition transition);

        void onTransitionPause(Transition transition);

        void onTransitionResume(Transition transition);

        void onTransitionStart(Transition transition);
    }

    public abstract void captureEndValues(TransitionValues transitionValues);

    public abstract void captureStartValues(TransitionValues transitionValues);

    public Transition setDuration(long duration) {
        this.mDuration = duration;
        return this;
    }

    public long getDuration() {
        return this.mDuration;
    }

    public Transition setStartDelay(long startDelay) {
        this.mStartDelay = startDelay;
        return this;
    }

    public long getStartDelay() {
        return this.mStartDelay;
    }

    public Transition setInterpolator(TimeInterpolator interpolator) {
        this.mInterpolator = interpolator;
        return this;
    }

    public TimeInterpolator getInterpolator() {
        return this.mInterpolator;
    }

    public String[] getTransitionProperties() {
        return null;
    }

    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        return null;
    }

    private void matchInstances(ArrayMap<View, TransitionValues> unmatchedStart, ArrayMap<View, TransitionValues> unmatchedEnd) {
        TransitionValues end;
        for (int i = unmatchedStart.size() - 1; i >= 0; i--) {
            View view = unmatchedStart.keyAt(i);
            if (view != null && isValidTarget(view) && (end = unmatchedEnd.remove(view)) != null && end.view != null && isValidTarget(end.view)) {
                TransitionValues start = unmatchedStart.removeAt(i);
                this.mStartValuesList.add(start);
                this.mEndValuesList.add(end);
            }
        }
    }

    private void matchItemIds(ArrayMap<View, TransitionValues> unmatchedStart, ArrayMap<View, TransitionValues> unmatchedEnd, LongSparseArray<View> startItemIds, LongSparseArray<View> endItemIds) {
        View endView;
        int numStartIds = startItemIds.size();
        for (int i = 0; i < numStartIds; i++) {
            View startView = startItemIds.valueAt(i);
            if (startView != null && isValidTarget(startView) && (endView = endItemIds.get(startItemIds.keyAt(i))) != null && isValidTarget(endView)) {
                TransitionValues startValues = unmatchedStart.get(startView);
                TransitionValues endValues = unmatchedEnd.get(endView);
                if (startValues != null && endValues != null) {
                    this.mStartValuesList.add(startValues);
                    this.mEndValuesList.add(endValues);
                    unmatchedStart.remove(startView);
                    unmatchedEnd.remove(endView);
                }
            }
        }
    }

    private void matchIds(ArrayMap<View, TransitionValues> unmatchedStart, ArrayMap<View, TransitionValues> unmatchedEnd, SparseArray<View> startIds, SparseArray<View> endIds) {
        View endView;
        int numStartIds = startIds.size();
        for (int i = 0; i < numStartIds; i++) {
            View startView = startIds.valueAt(i);
            if (startView != null && isValidTarget(startView) && (endView = endIds.get(startIds.keyAt(i))) != null && isValidTarget(endView)) {
                TransitionValues startValues = unmatchedStart.get(startView);
                TransitionValues endValues = unmatchedEnd.get(endView);
                if (startValues != null && endValues != null) {
                    this.mStartValuesList.add(startValues);
                    this.mEndValuesList.add(endValues);
                    unmatchedStart.remove(startView);
                    unmatchedEnd.remove(endView);
                }
            }
        }
    }

    private void matchNames(ArrayMap<View, TransitionValues> unmatchedStart, ArrayMap<View, TransitionValues> unmatchedEnd, ArrayMap<String, View> startNames, ArrayMap<String, View> endNames) {
        View endView;
        int numStartNames = startNames.size();
        for (int i = 0; i < numStartNames; i++) {
            View startView = startNames.valueAt(i);
            if (startView != null && isValidTarget(startView) && (endView = endNames.get(startNames.keyAt(i))) != null && isValidTarget(endView)) {
                TransitionValues startValues = unmatchedStart.get(startView);
                TransitionValues endValues = unmatchedEnd.get(endView);
                if (startValues != null && endValues != null) {
                    this.mStartValuesList.add(startValues);
                    this.mEndValuesList.add(endValues);
                    unmatchedStart.remove(startView);
                    unmatchedEnd.remove(endView);
                }
            }
        }
    }

    private void addUnmatched(ArrayMap<View, TransitionValues> unmatchedStart, ArrayMap<View, TransitionValues> unmatchedEnd) {
        for (int i = 0; i < unmatchedStart.size(); i++) {
            TransitionValues start = unmatchedStart.valueAt(i);
            if (isValidTarget(start.view)) {
                this.mStartValuesList.add(start);
                this.mEndValuesList.add(null);
            }
        }
        for (int i2 = 0; i2 < unmatchedEnd.size(); i2++) {
            TransitionValues end = unmatchedEnd.valueAt(i2);
            if (isValidTarget(end.view)) {
                this.mEndValuesList.add(end);
                this.mStartValuesList.add(null);
            }
        }
    }

    private void matchStartAndEnd(TransitionValuesMaps startValues, TransitionValuesMaps endValues) {
        ArrayMap<View, TransitionValues> unmatchedStart = new ArrayMap<>(startValues.mViewValues);
        ArrayMap<View, TransitionValues> unmatchedEnd = new ArrayMap<>(endValues.mViewValues);
        for (int i = 0; i < this.mMatchOrder.length; i++) {
            switch (this.mMatchOrder[i]) {
                case 1:
                    matchInstances(unmatchedStart, unmatchedEnd);
                    break;
                case 2:
                    matchNames(unmatchedStart, unmatchedEnd, startValues.mNameValues, endValues.mNameValues);
                    break;
                case 3:
                    matchIds(unmatchedStart, unmatchedEnd, startValues.mIdValues, endValues.mIdValues);
                    break;
                case 4:
                    matchItemIds(unmatchedStart, unmatchedEnd, startValues.mItemIdValues, endValues.mItemIdValues);
                    break;
            }
        }
        addUnmatched(unmatchedStart, unmatchedEnd);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void createAnimators(ViewGroup sceneRoot, TransitionValuesMaps startValues, TransitionValuesMaps endValues, ArrayList<TransitionValues> startValuesList, ArrayList<TransitionValues> endValuesList) {
        int startValuesListCount;
        int i;
        Animator animator;
        TransitionValues infoValues;
        Animator animator2;
        View view;
        Animator animator3;
        Animator animator4;
        int numExistingAnims;
        ArrayMap<Animator, AnimationInfo> runningAnimators = getRunningAnimators();
        SparseIntArray startDelays = new SparseIntArray();
        int startValuesListCount2 = startValuesList.size();
        long minStartDelay = Long.MAX_VALUE;
        int i2 = 0;
        while (true) {
            int i3 = i2;
            if (i3 >= startValuesListCount2) {
                break;
            }
            TransitionValues start = startValuesList.get(i3);
            TransitionValues end = endValuesList.get(i3);
            if (start != null && !start.mTargetedTransitions.contains(this)) {
                start = null;
            }
            TransitionValues start2 = start;
            if (end != null && !end.mTargetedTransitions.contains(this)) {
                end = null;
            }
            TransitionValues end2 = end;
            if (start2 == null && end2 == null) {
                startValuesListCount = startValuesListCount2;
                i = i3;
            } else {
                boolean isChanged = start2 == null || end2 == null || isTransitionRequired(start2, end2);
                if (!isChanged || (animator = createAnimator(sceneRoot, start2, end2)) == null) {
                    startValuesListCount = startValuesListCount2;
                    i = i3;
                } else {
                    TransitionValues infoValues2 = null;
                    if (end2 == null) {
                        startValuesListCount = startValuesListCount2;
                        i = i3;
                        infoValues = null;
                        animator2 = animator;
                        view = start2.view;
                    } else {
                        View view2 = end2.view;
                        String[] properties = getTransitionProperties();
                        if (view2 == null || properties == null) {
                            animator3 = animator;
                            startValuesListCount = startValuesListCount2;
                            i = i3;
                        } else {
                            animator3 = animator;
                            if (properties.length > 0) {
                                infoValues2 = new TransitionValues();
                                infoValues2.view = view2;
                                startValuesListCount = startValuesListCount2;
                                TransitionValues newValues = endValues.mViewValues.get(view2);
                                if (newValues != null) {
                                    int j = 0;
                                    while (true) {
                                        int j2 = j;
                                        if (j2 >= properties.length) {
                                            break;
                                        }
                                        infoValues2.values.put(properties[j2], newValues.values.get(properties[j2]));
                                        j = j2 + 1;
                                        i3 = i3;
                                        newValues = newValues;
                                    }
                                }
                                i = i3;
                                int numExistingAnims2 = runningAnimators.size();
                                int j3 = 0;
                                while (j3 < numExistingAnims2) {
                                    Animator anim = runningAnimators.keyAt(j3);
                                    AnimationInfo info = runningAnimators.get(anim);
                                    if (info.mValues == null || info.mView != view2) {
                                        numExistingAnims = numExistingAnims2;
                                    } else {
                                        numExistingAnims = numExistingAnims2;
                                        if (info.mName.equals(getName()) && info.mValues.equals(infoValues2)) {
                                            animator4 = null;
                                            break;
                                        }
                                    }
                                    j3++;
                                    numExistingAnims2 = numExistingAnims;
                                }
                            } else {
                                startValuesListCount = startValuesListCount2;
                                i = i3;
                            }
                        }
                        animator4 = animator3;
                        animator2 = animator4;
                        infoValues = infoValues2;
                        view = view2;
                    }
                    if (animator2 != null) {
                        if (this.mPropagation != null) {
                            long delay = this.mPropagation.getStartDelay(sceneRoot, this, start2, end2);
                            startDelays.put(this.mAnimators.size(), (int) delay);
                            minStartDelay = Math.min(delay, minStartDelay);
                        }
                        runningAnimators.put(animator2, new AnimationInfo(view, getName(), this, ViewUtils.getWindowId(sceneRoot), infoValues));
                        this.mAnimators.add(animator2);
                        minStartDelay = minStartDelay;
                    }
                }
            }
            i2 = i + 1;
            startValuesListCount2 = startValuesListCount;
        }
        if (minStartDelay != 0) {
            int i4 = 0;
            while (true) {
                int i5 = i4;
                if (i5 < startDelays.size()) {
                    int index = startDelays.keyAt(i5);
                    Animator animator5 = this.mAnimators.get(index);
                    animator5.setStartDelay((startDelays.valueAt(i5) - minStartDelay) + animator5.getStartDelay());
                    i4 = i5 + 1;
                } else {
                    return;
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isValidTarget(View target) {
        int targetId = target.getId();
        if (this.mTargetIdExcludes == null || !this.mTargetIdExcludes.contains(Integer.valueOf(targetId))) {
            if (this.mTargetExcludes == null || !this.mTargetExcludes.contains(target)) {
                if (this.mTargetTypeExcludes != null) {
                    int numTypes = this.mTargetTypeExcludes.size();
                    for (int i = 0; i < numTypes; i++) {
                        Class type = this.mTargetTypeExcludes.get(i);
                        if (type.isInstance(target)) {
                            return false;
                        }
                    }
                }
                if (this.mTargetNameExcludes == null || ViewCompat.getTransitionName(target) == null || !this.mTargetNameExcludes.contains(ViewCompat.getTransitionName(target))) {
                    if ((this.mTargetIds.size() == 0 && this.mTargets.size() == 0 && ((this.mTargetTypes == null || this.mTargetTypes.isEmpty()) && (this.mTargetNames == null || this.mTargetNames.isEmpty()))) || this.mTargetIds.contains(Integer.valueOf(targetId)) || this.mTargets.contains(target)) {
                        return true;
                    }
                    if (this.mTargetNames == null || !this.mTargetNames.contains(ViewCompat.getTransitionName(target))) {
                        if (this.mTargetTypes != null) {
                            for (int i2 = 0; i2 < this.mTargetTypes.size(); i2++) {
                                if (this.mTargetTypes.get(i2).isInstance(target)) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    }
                    return true;
                }
                return false;
            }
            return false;
        }
        return false;
    }

    private static ArrayMap<Animator, AnimationInfo> getRunningAnimators() {
        ArrayMap<Animator, AnimationInfo> runningAnimators = sRunningAnimators.get();
        if (runningAnimators == null) {
            ArrayMap<Animator, AnimationInfo> runningAnimators2 = new ArrayMap<>();
            sRunningAnimators.set(runningAnimators2);
            return runningAnimators2;
        }
        return runningAnimators;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void runAnimators() {
        start();
        ArrayMap<Animator, AnimationInfo> runningAnimators = getRunningAnimators();
        Iterator<Animator> it = this.mAnimators.iterator();
        while (it.hasNext()) {
            Animator anim = it.next();
            if (runningAnimators.containsKey(anim)) {
                start();
                runAnimator(anim, runningAnimators);
            }
        }
        this.mAnimators.clear();
        end();
    }

    private void runAnimator(Animator animator, final ArrayMap<Animator, AnimationInfo> runningAnimators) {
        if (animator != null) {
            animator.addListener(new AnimatorListenerAdapter() { // from class: android.support.transition.Transition.2
                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationStart(Animator animation) {
                    Transition.this.mCurrentAnimators.add(animation);
                }

                @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
                public void onAnimationEnd(Animator animation) {
                    runningAnimators.remove(animation);
                    Transition.this.mCurrentAnimators.remove(animation);
                }
            });
            animate(animator);
        }
    }

    public Transition addTarget(View target) {
        this.mTargets.add(target);
        return this;
    }

    public Transition removeTarget(View target) {
        this.mTargets.remove(target);
        return this;
    }

    public List<Integer> getTargetIds() {
        return this.mTargetIds;
    }

    public List<View> getTargets() {
        return this.mTargets;
    }

    public List<String> getTargetNames() {
        return this.mTargetNames;
    }

    public List<Class> getTargetTypes() {
        return this.mTargetTypes;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void captureValues(ViewGroup sceneRoot, boolean start) {
        clearValues(start);
        if ((this.mTargetIds.size() > 0 || this.mTargets.size() > 0) && ((this.mTargetNames == null || this.mTargetNames.isEmpty()) && (this.mTargetTypes == null || this.mTargetTypes.isEmpty()))) {
            for (int i = 0; i < this.mTargetIds.size(); i++) {
                int id = this.mTargetIds.get(i).intValue();
                View view = sceneRoot.findViewById(id);
                if (view != null) {
                    TransitionValues values = new TransitionValues();
                    values.view = view;
                    if (start) {
                        captureStartValues(values);
                    } else {
                        captureEndValues(values);
                    }
                    values.mTargetedTransitions.add(this);
                    capturePropagationValues(values);
                    if (start) {
                        addViewValues(this.mStartValues, view, values);
                    } else {
                        addViewValues(this.mEndValues, view, values);
                    }
                }
            }
            for (int i2 = 0; i2 < this.mTargets.size(); i2++) {
                View view2 = this.mTargets.get(i2);
                TransitionValues values2 = new TransitionValues();
                values2.view = view2;
                if (start) {
                    captureStartValues(values2);
                } else {
                    captureEndValues(values2);
                }
                values2.mTargetedTransitions.add(this);
                capturePropagationValues(values2);
                if (start) {
                    addViewValues(this.mStartValues, view2, values2);
                } else {
                    addViewValues(this.mEndValues, view2, values2);
                }
            }
        } else {
            captureHierarchy(sceneRoot, start);
        }
        if (!start && this.mNameOverrides != null) {
            int numOverrides = this.mNameOverrides.size();
            ArrayList<View> overriddenViews = new ArrayList<>(numOverrides);
            for (int i3 = 0; i3 < numOverrides; i3++) {
                String fromName = this.mNameOverrides.keyAt(i3);
                overriddenViews.add(this.mStartValues.mNameValues.remove(fromName));
            }
            for (int i4 = 0; i4 < numOverrides; i4++) {
                View view3 = overriddenViews.get(i4);
                if (view3 != null) {
                    String toName = this.mNameOverrides.valueAt(i4);
                    this.mStartValues.mNameValues.put(toName, view3);
                }
            }
        }
    }

    private static void addViewValues(TransitionValuesMaps transitionValuesMaps, View view, TransitionValues transitionValues) {
        transitionValuesMaps.mViewValues.put(view, transitionValues);
        int id = view.getId();
        if (id >= 0) {
            if (transitionValuesMaps.mIdValues.indexOfKey(id) >= 0) {
                transitionValuesMaps.mIdValues.put(id, null);
            } else {
                transitionValuesMaps.mIdValues.put(id, view);
            }
        }
        String name = ViewCompat.getTransitionName(view);
        if (name != null) {
            if (transitionValuesMaps.mNameValues.containsKey(name)) {
                transitionValuesMaps.mNameValues.put(name, null);
            } else {
                transitionValuesMaps.mNameValues.put(name, view);
            }
        }
        if (view.getParent() instanceof ListView) {
            ListView listview = (ListView) view.getParent();
            if (listview.getAdapter().hasStableIds()) {
                int position = listview.getPositionForView(view);
                long itemId = listview.getItemIdAtPosition(position);
                if (transitionValuesMaps.mItemIdValues.indexOfKey(itemId) >= 0) {
                    View alreadyMatched = transitionValuesMaps.mItemIdValues.get(itemId);
                    if (alreadyMatched != null) {
                        ViewCompat.setHasTransientState(alreadyMatched, false);
                        transitionValuesMaps.mItemIdValues.put(itemId, null);
                        return;
                    }
                    return;
                }
                ViewCompat.setHasTransientState(view, true);
                transitionValuesMaps.mItemIdValues.put(itemId, view);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void clearValues(boolean start) {
        if (start) {
            this.mStartValues.mViewValues.clear();
            this.mStartValues.mIdValues.clear();
            this.mStartValues.mItemIdValues.clear();
            return;
        }
        this.mEndValues.mViewValues.clear();
        this.mEndValues.mIdValues.clear();
        this.mEndValues.mItemIdValues.clear();
    }

    private void captureHierarchy(View view, boolean start) {
        if (view == null) {
            return;
        }
        int id = view.getId();
        if (this.mTargetIdExcludes != null && this.mTargetIdExcludes.contains(Integer.valueOf(id))) {
            return;
        }
        if (this.mTargetExcludes != null && this.mTargetExcludes.contains(view)) {
            return;
        }
        if (this.mTargetTypeExcludes != null) {
            int numTypes = this.mTargetTypeExcludes.size();
            for (int i = 0; i < numTypes; i++) {
                if (this.mTargetTypeExcludes.get(i).isInstance(view)) {
                    return;
                }
            }
        }
        if (view.getParent() instanceof ViewGroup) {
            TransitionValues values = new TransitionValues();
            values.view = view;
            if (start) {
                captureStartValues(values);
            } else {
                captureEndValues(values);
            }
            values.mTargetedTransitions.add(this);
            capturePropagationValues(values);
            if (start) {
                addViewValues(this.mStartValues, view, values);
            } else {
                addViewValues(this.mEndValues, view, values);
            }
        }
        if (view instanceof ViewGroup) {
            if (this.mTargetIdChildExcludes != null && this.mTargetIdChildExcludes.contains(Integer.valueOf(id))) {
                return;
            }
            if (this.mTargetChildExcludes != null && this.mTargetChildExcludes.contains(view)) {
                return;
            }
            if (this.mTargetTypeChildExcludes != null) {
                int numTypes2 = this.mTargetTypeChildExcludes.size();
                for (int i2 = 0; i2 < numTypes2; i2++) {
                    if (this.mTargetTypeChildExcludes.get(i2).isInstance(view)) {
                        return;
                    }
                }
            }
            ViewGroup parent = (ViewGroup) view;
            for (int i3 = 0; i3 < parent.getChildCount(); i3++) {
                captureHierarchy(parent.getChildAt(i3), start);
            }
        }
    }

    public TransitionValues getTransitionValues(View view, boolean start) {
        if (this.mParent != null) {
            return this.mParent.getTransitionValues(view, start);
        }
        TransitionValuesMaps valuesMaps = start ? this.mStartValues : this.mEndValues;
        return valuesMaps.mViewValues.get(view);
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public TransitionValues getMatchedTransitionValues(View view, boolean viewInStart) {
        if (this.mParent != null) {
            return this.mParent.getMatchedTransitionValues(view, viewInStart);
        }
        ArrayList<TransitionValues> lookIn = viewInStart ? this.mStartValuesList : this.mEndValuesList;
        if (lookIn == null) {
            return null;
        }
        int count = lookIn.size();
        int index = -1;
        int i = 0;
        while (true) {
            if (i >= count) {
                break;
            }
            TransitionValues values = lookIn.get(i);
            if (values == null) {
                return null;
            }
            if (values.view != view) {
                i++;
            } else {
                index = i;
                break;
            }
        }
        if (index < 0) {
            return null;
        }
        ArrayList<TransitionValues> matchIn = viewInStart ? this.mEndValuesList : this.mStartValuesList;
        return matchIn.get(index);
    }

    public void pause(View sceneRoot) {
        if (!this.mEnded) {
            ArrayMap<Animator, AnimationInfo> runningAnimators = getRunningAnimators();
            int numOldAnims = runningAnimators.size();
            WindowIdImpl windowId = ViewUtils.getWindowId(sceneRoot);
            for (int i = numOldAnims - 1; i >= 0; i--) {
                AnimationInfo info = runningAnimators.valueAt(i);
                if (info.mView != null && windowId.equals(info.mWindowId)) {
                    Animator anim = runningAnimators.keyAt(i);
                    AnimatorUtils.pause(anim);
                }
            }
            if (this.mListeners != null && this.mListeners.size() > 0) {
                ArrayList<TransitionListener> tmpListeners = (ArrayList) this.mListeners.clone();
                int numListeners = tmpListeners.size();
                for (int i2 = 0; i2 < numListeners; i2++) {
                    tmpListeners.get(i2).onTransitionPause(this);
                }
            }
            this.mPaused = true;
        }
    }

    public void resume(View sceneRoot) {
        if (this.mPaused) {
            if (!this.mEnded) {
                ArrayMap<Animator, AnimationInfo> runningAnimators = getRunningAnimators();
                int numOldAnims = runningAnimators.size();
                WindowIdImpl windowId = ViewUtils.getWindowId(sceneRoot);
                for (int i = numOldAnims - 1; i >= 0; i--) {
                    AnimationInfo info = runningAnimators.valueAt(i);
                    if (info.mView != null && windowId.equals(info.mWindowId)) {
                        Animator anim = runningAnimators.keyAt(i);
                        AnimatorUtils.resume(anim);
                    }
                }
                if (this.mListeners != null && this.mListeners.size() > 0) {
                    ArrayList<TransitionListener> tmpListeners = (ArrayList) this.mListeners.clone();
                    int numListeners = tmpListeners.size();
                    for (int i2 = 0; i2 < numListeners; i2++) {
                        tmpListeners.get(i2).onTransitionResume(this);
                    }
                }
            }
            this.mPaused = false;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void playTransition(ViewGroup sceneRoot) {
        AnimationInfo oldInfo;
        this.mStartValuesList = new ArrayList<>();
        this.mEndValuesList = new ArrayList<>();
        matchStartAndEnd(this.mStartValues, this.mEndValues);
        ArrayMap<Animator, AnimationInfo> runningAnimators = getRunningAnimators();
        int numOldAnims = runningAnimators.size();
        WindowIdImpl windowId = ViewUtils.getWindowId(sceneRoot);
        for (int i = numOldAnims - 1; i >= 0; i--) {
            Animator anim = runningAnimators.keyAt(i);
            if (anim != null && (oldInfo = runningAnimators.get(anim)) != null && oldInfo.mView != null && windowId.equals(oldInfo.mWindowId)) {
                TransitionValues oldValues = oldInfo.mValues;
                View oldView = oldInfo.mView;
                TransitionValues startValues = getTransitionValues(oldView, true);
                TransitionValues endValues = getMatchedTransitionValues(oldView, true);
                boolean cancel = !(startValues == null && endValues == null) && oldInfo.mTransition.isTransitionRequired(oldValues, endValues);
                if (cancel) {
                    if (anim.isRunning() || anim.isStarted()) {
                        anim.cancel();
                    } else {
                        runningAnimators.remove(anim);
                    }
                }
            }
        }
        createAnimators(sceneRoot, this.mStartValues, this.mEndValues, this.mStartValuesList, this.mEndValuesList);
        runAnimators();
    }

    public boolean isTransitionRequired(TransitionValues startValues, TransitionValues endValues) {
        if (startValues == null || endValues == null) {
            return false;
        }
        String[] properties = getTransitionProperties();
        if (properties != null) {
            for (String property : properties) {
                if (isValueChanged(startValues, endValues, property)) {
                    return true;
                }
            }
            return false;
        }
        for (String key : startValues.values.keySet()) {
            if (isValueChanged(startValues, endValues, key)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isValueChanged(TransitionValues oldValues, TransitionValues newValues, String key) {
        Object oldValue = oldValues.values.get(key);
        Object newValue = newValues.values.get(key);
        if (oldValue == null && newValue == null) {
            return false;
        }
        if (oldValue == null || newValue == null) {
            return true;
        }
        boolean changed = !oldValue.equals(newValue);
        return changed;
    }

    protected void animate(Animator animator) {
        if (animator == null) {
            end();
            return;
        }
        if (getDuration() >= 0) {
            animator.setDuration(getDuration());
        }
        if (getStartDelay() >= 0) {
            animator.setStartDelay(getStartDelay());
        }
        if (getInterpolator() != null) {
            animator.setInterpolator(getInterpolator());
        }
        animator.addListener(new AnimatorListenerAdapter() { // from class: android.support.transition.Transition.3
            @Override // android.animation.AnimatorListenerAdapter, android.animation.Animator.AnimatorListener
            public void onAnimationEnd(Animator animation) {
                Transition.this.end();
                animation.removeListener(this);
            }
        });
        animator.start();
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void start() {
        if (this.mNumInstances == 0) {
            if (this.mListeners != null && this.mListeners.size() > 0) {
                ArrayList<TransitionListener> tmpListeners = (ArrayList) this.mListeners.clone();
                int numListeners = tmpListeners.size();
                for (int i = 0; i < numListeners; i++) {
                    tmpListeners.get(i).onTransitionStart(this);
                }
            }
            this.mEnded = false;
        }
        this.mNumInstances++;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void end() {
        this.mNumInstances--;
        if (this.mNumInstances == 0) {
            if (this.mListeners != null && this.mListeners.size() > 0) {
                ArrayList<TransitionListener> tmpListeners = (ArrayList) this.mListeners.clone();
                int numListeners = tmpListeners.size();
                for (int i = 0; i < numListeners; i++) {
                    tmpListeners.get(i).onTransitionEnd(this);
                }
            }
            for (int i2 = 0; i2 < this.mStartValues.mItemIdValues.size(); i2++) {
                View view = this.mStartValues.mItemIdValues.valueAt(i2);
                if (view != null) {
                    ViewCompat.setHasTransientState(view, false);
                }
            }
            for (int i3 = 0; i3 < this.mEndValues.mItemIdValues.size(); i3++) {
                View view2 = this.mEndValues.mItemIdValues.valueAt(i3);
                if (view2 != null) {
                    ViewCompat.setHasTransientState(view2, false);
                }
            }
            this.mEnded = true;
        }
    }

    public Transition addListener(TransitionListener listener) {
        if (this.mListeners == null) {
            this.mListeners = new ArrayList<>();
        }
        this.mListeners.add(listener);
        return this;
    }

    public Transition removeListener(TransitionListener listener) {
        if (this.mListeners == null) {
            return this;
        }
        this.mListeners.remove(listener);
        if (this.mListeners.size() == 0) {
            this.mListeners = null;
        }
        return this;
    }

    public void setPathMotion(PathMotion pathMotion) {
        if (pathMotion == null) {
            this.mPathMotion = STRAIGHT_PATH_MOTION;
        } else {
            this.mPathMotion = pathMotion;
        }
    }

    public PathMotion getPathMotion() {
        return this.mPathMotion;
    }

    public void setEpicenterCallback(EpicenterCallback epicenterCallback) {
        this.mEpicenterCallback = epicenterCallback;
    }

    public EpicenterCallback getEpicenterCallback() {
        return this.mEpicenterCallback;
    }

    public void setPropagation(TransitionPropagation transitionPropagation) {
        this.mPropagation = transitionPropagation;
    }

    public TransitionPropagation getPropagation() {
        return this.mPropagation;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void capturePropagationValues(TransitionValues transitionValues) {
        String[] propertyNames;
        if (this.mPropagation == null || transitionValues.values.isEmpty() || (propertyNames = this.mPropagation.getPropagationProperties()) == null) {
            return;
        }
        boolean containsAll = true;
        int i = 0;
        while (true) {
            if (i >= propertyNames.length) {
                break;
            } else if (transitionValues.values.containsKey(propertyNames[i])) {
                i++;
            } else {
                containsAll = false;
                break;
            }
        }
        if (!containsAll) {
            this.mPropagation.captureValues(transitionValues);
        }
    }

    public String toString() {
        return toString(BuildConfig.FLAVOR);
    }

    @Override // 
    /* renamed from: clone */
    public Transition mo1clone() {
        try {
            Transition clone = (Transition) super.clone();
            clone.mAnimators = new ArrayList<>();
            clone.mStartValues = new TransitionValuesMaps();
            clone.mEndValues = new TransitionValuesMaps();
            clone.mStartValuesList = null;
            clone.mEndValuesList = null;
            return clone;
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public String getName() {
        return this.mName;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String toString(String indent) {
        String result = indent + getClass().getSimpleName() + "@" + Integer.toHexString(hashCode()) + ": ";
        if (this.mDuration != -1) {
            result = result + "dur(" + this.mDuration + ") ";
        }
        if (this.mStartDelay != -1) {
            result = result + "dly(" + this.mStartDelay + ") ";
        }
        if (this.mInterpolator != null) {
            result = result + "interp(" + this.mInterpolator + ") ";
        }
        if (this.mTargetIds.size() > 0 || this.mTargets.size() > 0) {
            String result2 = result + "tgts(";
            int i = 0;
            if (this.mTargetIds.size() > 0) {
                String result3 = result2;
                for (int i2 = 0; i2 < this.mTargetIds.size(); i2++) {
                    if (i2 > 0) {
                        result3 = result3 + ", ";
                    }
                    result3 = result3 + this.mTargetIds.get(i2);
                }
                result2 = result3;
            }
            if (this.mTargets.size() > 0) {
                while (true) {
                    int i3 = i;
                    if (i3 >= this.mTargets.size()) {
                        break;
                    }
                    if (i3 > 0) {
                        result2 = result2 + ", ";
                    }
                    result2 = result2 + this.mTargets.get(i3);
                    i = i3 + 1;
                }
            }
            return result2 + ")";
        }
        return result;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class AnimationInfo {
        String mName;
        Transition mTransition;
        TransitionValues mValues;
        View mView;
        WindowIdImpl mWindowId;

        AnimationInfo(View view, String name, Transition transition, WindowIdImpl windowId, TransitionValues values) {
            this.mView = view;
            this.mName = name;
            this.mValues = values;
            this.mWindowId = windowId;
            this.mTransition = transition;
        }
    }
}
