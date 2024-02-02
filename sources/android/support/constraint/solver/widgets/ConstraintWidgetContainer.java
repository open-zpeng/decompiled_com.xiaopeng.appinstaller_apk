package android.support.constraint.solver.widgets;

import android.support.constraint.solver.LinearSystem;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.support.constraint.solver.widgets.ConstraintWidget;
import android.support.v7.preference.Preference;
import java.util.ArrayList;
import java.util.Arrays;
/* loaded from: classes.dex */
public class ConstraintWidgetContainer extends WidgetContainer {
    static boolean ALLOW_ROOT_GROUP = true;
    int mPaddingBottom;
    int mPaddingLeft;
    int mPaddingRight;
    int mPaddingTop;
    private Snapshot mSnapshot;
    int mWrapHeight;
    int mWrapWidth;
    protected LinearSystem mSystem = new LinearSystem();
    protected LinearSystem mBackgroundSystem = null;
    private int mHorizontalChainsSize = 0;
    private int mVerticalChainsSize = 0;
    private ConstraintWidget[] mMatchConstraintsChainedWidgets = new ConstraintWidget[4];
    private ConstraintWidget[] mVerticalChainsArray = new ConstraintWidget[4];
    private ConstraintWidget[] mHorizontalChainsArray = new ConstraintWidget[4];
    private int mOptimizationLevel = 2;
    private boolean[] flags = new boolean[3];
    private ConstraintWidget[] mChainEnds = new ConstraintWidget[4];
    private boolean mWidthMeasuredTooSmall = false;
    private boolean mHeightMeasuredTooSmall = false;

    public void setOptimizationLevel(int value) {
        this.mOptimizationLevel = value;
    }

    @Override // android.support.constraint.solver.widgets.WidgetContainer, android.support.constraint.solver.widgets.ConstraintWidget
    public void reset() {
        this.mSystem.reset();
        this.mPaddingLeft = 0;
        this.mPaddingRight = 0;
        this.mPaddingTop = 0;
        this.mPaddingBottom = 0;
        super.reset();
    }

    public boolean isWidthMeasuredTooSmall() {
        return this.mWidthMeasuredTooSmall;
    }

    public boolean isHeightMeasuredTooSmall() {
        return this.mHeightMeasuredTooSmall;
    }

    public boolean addChildrenToSolver(LinearSystem system, int group) {
        addToSolver(system, group);
        int count = this.mChildren.size();
        boolean setMatchParent = false;
        int i = 0;
        if (this.mOptimizationLevel == 2 || this.mOptimizationLevel == 4) {
            if (optimize(system)) {
                return false;
            }
        } else {
            setMatchParent = true;
        }
        while (true) {
            int i2 = i;
            if (i2 >= count) {
                break;
            }
            ConstraintWidget widget = this.mChildren.get(i2);
            if (widget instanceof ConstraintWidgetContainer) {
                ConstraintWidget.DimensionBehaviour horizontalBehaviour = widget.mHorizontalDimensionBehaviour;
                ConstraintWidget.DimensionBehaviour verticalBehaviour = widget.mVerticalDimensionBehaviour;
                if (horizontalBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    widget.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
                }
                if (verticalBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    widget.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
                }
                widget.addToSolver(system, group);
                if (horizontalBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    widget.setHorizontalDimensionBehaviour(horizontalBehaviour);
                }
                if (verticalBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    widget.setVerticalDimensionBehaviour(verticalBehaviour);
                }
            } else {
                if (setMatchParent) {
                    Optimizer.checkMatchParent(this, system, widget);
                }
                widget.addToSolver(system, group);
            }
            i = i2 + 1;
        }
        if (this.mHorizontalChainsSize > 0) {
            applyHorizontalChain(system);
        }
        if (this.mVerticalChainsSize > 0) {
            applyVerticalChain(system);
            return true;
        }
        return true;
    }

    private boolean optimize(LinearSystem system) {
        int count = this.mChildren.size();
        boolean done = false;
        int dv = 0;
        int dv2 = 0;
        int n = 0;
        for (int i = 0; i < count; i++) {
            ConstraintWidget widget = this.mChildren.get(i);
            widget.mHorizontalResolution = -1;
            widget.mVerticalResolution = -1;
            if (widget.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT || widget.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                widget.mHorizontalResolution = 1;
                widget.mVerticalResolution = 1;
            }
            if (widget instanceof Barrier) {
                widget.mHorizontalResolution = 1;
                widget.mVerticalResolution = 1;
            }
        }
        while (!done) {
            int prev = dv;
            int preh = dv2;
            n++;
            int dh = 0;
            int dv3 = 0;
            for (int dv4 = 0; dv4 < count; dv4++) {
                ConstraintWidget widget2 = this.mChildren.get(dv4);
                if (widget2.mHorizontalResolution == -1) {
                    if (this.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                        widget2.mHorizontalResolution = 1;
                    } else {
                        Optimizer.checkHorizontalSimpleDependency(this, system, widget2);
                    }
                }
                if (widget2.mVerticalResolution == -1) {
                    if (this.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                        widget2.mVerticalResolution = 1;
                    } else {
                        Optimizer.checkVerticalSimpleDependency(this, system, widget2);
                    }
                }
                if (widget2.mVerticalResolution == -1) {
                    dv3++;
                }
                if (widget2.mHorizontalResolution == -1) {
                    dh++;
                }
            }
            if (dv3 == 0 && dh == 0) {
                done = true;
            } else if (prev == dv3 && preh == dh) {
                done = true;
            }
            dv = dv3;
            dv2 = dh;
        }
        int sv = 0;
        int sv2 = 0;
        for (int sh = 0; sh < count; sh++) {
            ConstraintWidget widget3 = this.mChildren.get(sh);
            if (widget3.mHorizontalResolution == 1 || widget3.mHorizontalResolution == -1) {
                sv2++;
            }
            if (widget3.mVerticalResolution == 1 || widget3.mVerticalResolution == -1) {
                sv++;
            }
        }
        return sv2 == 0 && sv == 0;
    }

    /* JADX WARN: Removed duplicated region for block: B:204:0x0539  */
    /* JADX WARN: Removed duplicated region for block: B:205:0x053c  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void applyHorizontalChain(android.support.constraint.solver.LinearSystem r41) {
        /*
            Method dump skipped, instructions count: 1485
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.constraint.solver.widgets.ConstraintWidgetContainer.applyHorizontalChain(android.support.constraint.solver.LinearSystem):void");
    }

    /* JADX WARN: Removed duplicated region for block: B:215:0x055f  */
    /* JADX WARN: Removed duplicated region for block: B:216:0x0562  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private void applyVerticalChain(android.support.constraint.solver.LinearSystem r43) {
        /*
            Method dump skipped, instructions count: 1523
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.constraint.solver.widgets.ConstraintWidgetContainer.applyVerticalChain(android.support.constraint.solver.LinearSystem):void");
    }

    public void updateChildrenFromSolver(LinearSystem system, int group, boolean[] flags) {
        flags[2] = false;
        updateFromSolver(system, group);
        int count = this.mChildren.size();
        for (int i = 0; i < count; i++) {
            ConstraintWidget widget = this.mChildren.get(i);
            widget.updateFromSolver(system, group);
            if (widget.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && widget.getWidth() < widget.getWrapWidth()) {
                flags[2] = true;
            }
            if (widget.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && widget.getHeight() < widget.getWrapHeight()) {
                flags[2] = true;
            }
        }
    }

    @Override // android.support.constraint.solver.widgets.WidgetContainer
    public void layout() {
        int prex = this.mX;
        int prey = this.mY;
        int prew = Math.max(0, getWidth());
        int preh = Math.max(0, getHeight());
        this.mWidthMeasuredTooSmall = false;
        this.mHeightMeasuredTooSmall = false;
        if (this.mParent != null) {
            if (this.mSnapshot == null) {
                this.mSnapshot = new Snapshot(this);
            }
            this.mSnapshot.updateFrom(this);
            setX(this.mPaddingLeft);
            setY(this.mPaddingTop);
            resetAnchors();
            resetSolverVariables(this.mSystem.getCache());
        } else {
            this.mX = 0;
            this.mY = 0;
        }
        boolean wrap_override = false;
        ConstraintWidget.DimensionBehaviour originalVerticalDimensionBehaviour = this.mVerticalDimensionBehaviour;
        ConstraintWidget.DimensionBehaviour originalHorizontalDimensionBehaviour = this.mHorizontalDimensionBehaviour;
        boolean z = true;
        if (this.mOptimizationLevel == 2 && (this.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT || this.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT)) {
            findWrapSize(this.mChildren, this.flags);
            wrap_override = this.flags[0];
            if (prew > 0 && preh > 0 && (this.mWrapWidth > prew || this.mWrapHeight > preh)) {
                wrap_override = false;
            }
            if (wrap_override) {
                if (this.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    this.mHorizontalDimensionBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
                    if (prew <= 0 || prew >= this.mWrapWidth) {
                        setWidth(Math.max(this.mMinWidth, this.mWrapWidth));
                    } else {
                        this.mWidthMeasuredTooSmall = true;
                        setWidth(prew);
                    }
                }
                if (this.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT) {
                    this.mVerticalDimensionBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
                    if (preh <= 0 || preh >= this.mWrapHeight) {
                        setHeight(Math.max(this.mMinHeight, this.mWrapHeight));
                    } else {
                        this.mHeightMeasuredTooSmall = true;
                        setHeight(preh);
                    }
                }
            }
        }
        resetChains();
        int count = this.mChildren.size();
        for (int i = 0; i < count; i++) {
            ConstraintWidget widget = this.mChildren.get(i);
            if (widget instanceof WidgetContainer) {
                ((WidgetContainer) widget).layout();
            }
        }
        boolean needsSolving = true;
        boolean wrap_override2 = wrap_override;
        int countSolve = 0;
        while (needsSolving) {
            int countSolve2 = countSolve + 1;
            try {
                this.mSystem.reset();
                needsSolving = addChildrenToSolver(this.mSystem, Preference.DEFAULT_ORDER);
                if (needsSolving) {
                    this.mSystem.minimize();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (needsSolving) {
                updateChildrenFromSolver(this.mSystem, Preference.DEFAULT_ORDER, this.flags);
            } else {
                updateFromSolver(this.mSystem, Preference.DEFAULT_ORDER);
                int i2 = 0;
                while (true) {
                    if (i2 >= count) {
                        break;
                    }
                    ConstraintWidget widget2 = this.mChildren.get(i2);
                    if (widget2.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && widget2.getWidth() < widget2.getWrapWidth()) {
                        this.flags[2] = z;
                        break;
                    } else if (widget2.mVerticalDimensionBehaviour != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT || widget2.getHeight() >= widget2.getWrapHeight()) {
                        i2++;
                    } else {
                        this.flags[2] = z;
                        break;
                    }
                }
            }
            boolean needsSolving2 = false;
            if (countSolve2 < 8 && this.flags[2]) {
                int maxY = 0;
                int maxX = 0;
                for (int maxX2 = 0; maxX2 < count; maxX2++) {
                    ConstraintWidget widget3 = this.mChildren.get(maxX2);
                    maxX = Math.max(maxX, widget3.mX + widget3.getWidth());
                    maxY = Math.max(maxY, widget3.mY + widget3.getHeight());
                }
                int i3 = this.mMinWidth;
                int maxX3 = Math.max(i3, maxX);
                int maxY2 = Math.max(this.mMinHeight, maxY);
                if (originalHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT && getWidth() < maxX3) {
                    setWidth(maxX3);
                    this.mHorizontalDimensionBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
                    wrap_override2 = true;
                    needsSolving2 = true;
                }
                if (originalVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT && getHeight() < maxY2) {
                    setHeight(maxY2);
                    this.mVerticalDimensionBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
                    wrap_override2 = true;
                    needsSolving2 = true;
                }
            }
            int maxX4 = this.mMinWidth;
            int width = Math.max(maxX4, getWidth());
            if (width > getWidth()) {
                setWidth(width);
                this.mHorizontalDimensionBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
                wrap_override2 = true;
                needsSolving2 = true;
            }
            int height = Math.max(this.mMinHeight, getHeight());
            if (height > getHeight()) {
                setHeight(height);
                this.mVerticalDimensionBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
                wrap_override2 = true;
                needsSolving2 = true;
            }
            if (!wrap_override2) {
                if (this.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT && prew > 0 && getWidth() > prew) {
                    this.mWidthMeasuredTooSmall = true;
                    wrap_override2 = true;
                    this.mHorizontalDimensionBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
                    setWidth(prew);
                    needsSolving2 = true;
                }
                if (this.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT && preh > 0 && getHeight() > preh) {
                    z = true;
                    this.mHeightMeasuredTooSmall = true;
                    this.mVerticalDimensionBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
                    setHeight(preh);
                    needsSolving2 = true;
                    wrap_override2 = true;
                    needsSolving = needsSolving2;
                    countSolve = countSolve2;
                }
            }
            z = true;
            needsSolving = needsSolving2;
            countSolve = countSolve2;
        }
        if (this.mParent != null) {
            int width2 = Math.max(this.mMinWidth, getWidth());
            int height2 = Math.max(this.mMinHeight, getHeight());
            this.mSnapshot.applyTo(this);
            setWidth(this.mPaddingLeft + width2 + this.mPaddingRight);
            setHeight(this.mPaddingTop + height2 + this.mPaddingBottom);
        } else {
            this.mX = prex;
            this.mY = prey;
        }
        if (wrap_override2) {
            this.mHorizontalDimensionBehaviour = originalHorizontalDimensionBehaviour;
            this.mVerticalDimensionBehaviour = originalVerticalDimensionBehaviour;
        }
        resetSolverVariables(this.mSystem.getCache());
        if (this == getRootConstraintContainer()) {
            updateDrawPosition();
        }
    }

    public void findHorizontalWrapRecursive(ConstraintWidget widget, boolean[] flags) {
        boolean z = false;
        if (widget.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && widget.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && widget.mDimensionRatio > 0.0f) {
            flags[0] = false;
        } else if (widget.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && widget.mMatchConstraintDefaultWidth == 2) {
            flags[0] = false;
        } else {
            int w = widget.getOptimizerWrapWidth();
            if (widget.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && widget.mVerticalDimensionBehaviour != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && widget.mDimensionRatio > 0.0f) {
                flags[0] = false;
                return;
            }
            int distToRight = w;
            int distToLeft = w;
            ConstraintWidget leftWidget = null;
            ConstraintWidget rightWidget = null;
            widget.mHorizontalWrapVisited = true;
            if (widget instanceof Guideline) {
                Guideline guideline = (Guideline) widget;
                if (guideline.getOrientation() == 1) {
                    distToLeft = 0;
                    distToRight = 0;
                    if (guideline.getRelativeBegin() != -1) {
                        distToLeft = guideline.getRelativeBegin();
                    } else if (guideline.getRelativeEnd() != -1) {
                        distToRight = guideline.getRelativeEnd();
                    } else if (guideline.getRelativePercent() != -1.0f) {
                        flags[0] = false;
                        return;
                    }
                }
            } else if (!widget.mRight.isConnected() && !widget.mLeft.isConnected()) {
                distToLeft += widget.getX();
            } else if (widget.mRight.mTarget != null && widget.mLeft.mTarget != null && widget.mIsWidthWrapContent && widget.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                flags[0] = false;
                return;
            } else if (widget.mRight.mTarget != null && widget.mLeft.mTarget != null && (widget.mRight.mTarget == widget.mLeft.mTarget || (widget.mRight.mTarget.mOwner == widget.mLeft.mTarget.mOwner && widget.mRight.mTarget.mOwner != widget.mParent))) {
                flags[0] = false;
                return;
            } else {
                if (widget.mRight.mTarget != null) {
                    rightWidget = widget.mRight.mTarget.mOwner;
                    distToRight += widget.mRight.getMargin();
                    if (!rightWidget.isRoot() && !rightWidget.mHorizontalWrapVisited) {
                        findHorizontalWrapRecursive(rightWidget, flags);
                    }
                }
                if (widget.mLeft.mTarget != null) {
                    leftWidget = widget.mLeft.mTarget.mOwner;
                    distToLeft += widget.mLeft.getMargin();
                    if (!leftWidget.isRoot() && !leftWidget.mHorizontalWrapVisited) {
                        findHorizontalWrapRecursive(leftWidget, flags);
                    }
                }
                if (widget.mRight.mTarget != null && !rightWidget.isRoot()) {
                    if (widget.mRight.mTarget.mType == ConstraintAnchor.Type.RIGHT) {
                        distToRight += rightWidget.mDistToRight - rightWidget.getOptimizerWrapWidth();
                    } else if (widget.mRight.mTarget.getType() == ConstraintAnchor.Type.LEFT) {
                        distToRight += rightWidget.mDistToRight;
                    }
                    widget.mRightHasCentered = rightWidget.mRightHasCentered || !(rightWidget.mLeft.mTarget == null || rightWidget.mRight.mTarget == null || rightWidget.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
                    if (widget.mRightHasCentered && (rightWidget.mLeft.mTarget == null || rightWidget.mLeft.mTarget.mOwner != widget)) {
                        distToRight += distToRight - rightWidget.mDistToRight;
                    }
                }
                if (widget.mLeft.mTarget != null && !leftWidget.isRoot()) {
                    if (widget.mLeft.mTarget.getType() == ConstraintAnchor.Type.LEFT) {
                        distToLeft += leftWidget.mDistToLeft - leftWidget.getOptimizerWrapWidth();
                    } else if (widget.mLeft.mTarget.getType() == ConstraintAnchor.Type.RIGHT) {
                        distToLeft += leftWidget.mDistToLeft;
                    }
                    if (leftWidget.mLeftHasCentered || (leftWidget.mLeft.mTarget != null && leftWidget.mRight.mTarget != null && leftWidget.mHorizontalDimensionBehaviour != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT)) {
                        z = true;
                    }
                    widget.mLeftHasCentered = z;
                    if (widget.mLeftHasCentered && (leftWidget.mRight.mTarget == null || leftWidget.mRight.mTarget.mOwner != widget)) {
                        distToLeft += distToLeft - leftWidget.mDistToLeft;
                    }
                }
            }
            if (widget.getVisibility() == 8) {
                distToLeft -= widget.mWidth;
                distToRight -= widget.mWidth;
            }
            widget.mDistToLeft = distToLeft;
            widget.mDistToRight = distToRight;
        }
    }

    public void findVerticalWrapRecursive(ConstraintWidget widget, boolean[] flags) {
        boolean z = false;
        if (widget.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && widget.mHorizontalDimensionBehaviour != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && widget.mDimensionRatio > 0.0f) {
            flags[0] = false;
        } else if (widget.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT && widget.mMatchConstraintDefaultHeight == 2) {
            flags[0] = false;
        } else {
            int h = widget.getOptimizerWrapHeight();
            int distToTop = h;
            int distToBottom = h;
            ConstraintWidget topWidget = null;
            ConstraintWidget bottomWidget = null;
            widget.mVerticalWrapVisited = true;
            if (widget instanceof Guideline) {
                Guideline guideline = (Guideline) widget;
                if (guideline.getOrientation() == 0) {
                    distToTop = 0;
                    distToBottom = 0;
                    if (guideline.getRelativeBegin() != -1) {
                        distToTop = guideline.getRelativeBegin();
                    } else if (guideline.getRelativeEnd() != -1) {
                        distToBottom = guideline.getRelativeEnd();
                    } else if (guideline.getRelativePercent() != -1.0f) {
                        flags[0] = false;
                        return;
                    }
                }
            } else if (widget.mBaseline.mTarget == null && widget.mTop.mTarget == null && widget.mBottom.mTarget == null) {
                distToTop += widget.getY();
            } else if (widget.mBottom.mTarget != null && widget.mTop.mTarget != null && widget.mIsHeightWrapContent && widget.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                flags[0] = false;
                return;
            } else if (widget.mBottom.mTarget != null && widget.mTop.mTarget != null && (widget.mBottom.mTarget == widget.mTop.mTarget || (widget.mBottom.mTarget.mOwner == widget.mTop.mTarget.mOwner && widget.mBottom.mTarget.mOwner != widget.mParent))) {
                flags[0] = false;
                return;
            } else if (widget.mBaseline.isConnected()) {
                ConstraintWidget baseLineWidget = widget.mBaseline.mTarget.getOwner();
                if (!baseLineWidget.mVerticalWrapVisited) {
                    findVerticalWrapRecursive(baseLineWidget, flags);
                }
                int distToTop2 = Math.max((baseLineWidget.mDistToTop - baseLineWidget.mHeight) + h, h);
                int distToBottom2 = Math.max((baseLineWidget.mDistToBottom - baseLineWidget.mHeight) + h, h);
                if (widget.getVisibility() == 8) {
                    distToTop2 -= widget.mHeight;
                    distToBottom2 -= widget.mHeight;
                }
                widget.mDistToTop = distToTop2;
                widget.mDistToBottom = distToBottom2;
                return;
            } else {
                if (widget.mTop.isConnected()) {
                    topWidget = widget.mTop.mTarget.getOwner();
                    distToTop += widget.mTop.getMargin();
                    if (!topWidget.isRoot() && !topWidget.mVerticalWrapVisited) {
                        findVerticalWrapRecursive(topWidget, flags);
                    }
                }
                if (widget.mBottom.isConnected()) {
                    bottomWidget = widget.mBottom.mTarget.getOwner();
                    distToBottom += widget.mBottom.getMargin();
                    if (!bottomWidget.isRoot() && !bottomWidget.mVerticalWrapVisited) {
                        findVerticalWrapRecursive(bottomWidget, flags);
                    }
                }
                if (widget.mTop.mTarget != null && !topWidget.isRoot()) {
                    if (widget.mTop.mTarget.getType() == ConstraintAnchor.Type.TOP) {
                        distToTop += topWidget.mDistToTop - topWidget.getOptimizerWrapHeight();
                    } else if (widget.mTop.mTarget.getType() == ConstraintAnchor.Type.BOTTOM) {
                        distToTop += topWidget.mDistToTop;
                    }
                    widget.mTopHasCentered = topWidget.mTopHasCentered || !(topWidget.mTop.mTarget == null || topWidget.mTop.mTarget.mOwner == widget || topWidget.mBottom.mTarget == null || topWidget.mBottom.mTarget.mOwner == widget || topWidget.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
                    if (widget.mTopHasCentered && (topWidget.mBottom.mTarget == null || topWidget.mBottom.mTarget.mOwner != widget)) {
                        distToTop += distToTop - topWidget.mDistToTop;
                    }
                }
                if (widget.mBottom.mTarget != null && !bottomWidget.isRoot()) {
                    if (widget.mBottom.mTarget.getType() == ConstraintAnchor.Type.BOTTOM) {
                        distToBottom += bottomWidget.mDistToBottom - bottomWidget.getOptimizerWrapHeight();
                    } else if (widget.mBottom.mTarget.getType() == ConstraintAnchor.Type.TOP) {
                        distToBottom += bottomWidget.mDistToBottom;
                    }
                    if (bottomWidget.mBottomHasCentered || (bottomWidget.mTop.mTarget != null && bottomWidget.mTop.mTarget.mOwner != widget && bottomWidget.mBottom.mTarget != null && bottomWidget.mBottom.mTarget.mOwner != widget && bottomWidget.mVerticalDimensionBehaviour != ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT)) {
                        z = true;
                    }
                    widget.mBottomHasCentered = z;
                    if (widget.mBottomHasCentered && (bottomWidget.mTop.mTarget == null || bottomWidget.mTop.mTarget.mOwner != widget)) {
                        distToBottom += distToBottom - bottomWidget.mDistToBottom;
                    }
                }
            }
            if (widget.getVisibility() == 8) {
                distToTop -= widget.mHeight;
                distToBottom -= widget.mHeight;
            }
            widget.mDistToTop = distToTop;
            widget.mDistToBottom = distToBottom;
        }
    }

    /* JADX WARN: Type inference failed for: r11v0 */
    /* JADX WARN: Type inference failed for: r11v1, types: [boolean] */
    /* JADX WARN: Type inference failed for: r11v29 */
    public void findWrapSize(ArrayList<ConstraintWidget> children, boolean[] flags) {
        int maxLeftDist = 0;
        int maxRightDist = 0;
        int maxConnectWidth = 0;
        int size = children.size();
        ?? r11 = 0;
        flags[0] = true;
        int maxConnectHeight = 0;
        int maxConnectHeight2 = 0;
        int maxBottomDist = 0;
        int maxTopDist = 0;
        while (maxTopDist < size) {
            try {
                ConstraintWidget widget = children.get(maxTopDist);
                if (!widget.isRoot()) {
                    if (!widget.mHorizontalWrapVisited) {
                        findHorizontalWrapRecursive(widget, flags);
                    }
                    if (!flags[r11]) {
                        for (int j = r11; j < size; j++) {
                            ConstraintWidget child = children.get(j);
                            child.mHorizontalWrapVisited = r11;
                            child.mVerticalWrapVisited = r11;
                            child.mLeftHasCentered = r11;
                            child.mRightHasCentered = r11;
                            child.mTopHasCentered = r11;
                            child.mBottomHasCentered = r11;
                        }
                        return;
                    }
                    if (!widget.mVerticalWrapVisited) {
                        findVerticalWrapRecursive(widget, flags);
                    }
                    if (!flags[r11]) {
                        for (int j2 = r11; j2 < size; j2++) {
                            ConstraintWidget child2 = children.get(j2);
                            child2.mHorizontalWrapVisited = r11;
                            child2.mVerticalWrapVisited = r11;
                            child2.mLeftHasCentered = r11;
                            child2.mRightHasCentered = r11;
                            child2.mTopHasCentered = r11;
                            child2.mBottomHasCentered = r11;
                        }
                        return;
                    }
                    int connectWidth = (widget.mDistToLeft + widget.mDistToRight) - widget.getWidth();
                    int connectHeight = (widget.mDistToTop + widget.mDistToBottom) - widget.getHeight();
                    if (widget.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_PARENT) {
                        connectWidth = widget.getWidth() + widget.mLeft.mMargin + widget.mRight.mMargin;
                    }
                    if (widget.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_PARENT) {
                        connectHeight = widget.getHeight() + widget.mTop.mMargin + widget.mBottom.mMargin;
                    }
                    if (widget.getVisibility() == 8) {
                        connectWidth = 0;
                        connectHeight = 0;
                    }
                    maxLeftDist = Math.max(maxLeftDist, widget.mDistToLeft);
                    maxRightDist = Math.max(maxRightDist, widget.mDistToRight);
                    maxConnectHeight2 = Math.max(maxConnectHeight2, widget.mDistToBottom);
                    maxBottomDist = Math.max(maxBottomDist, widget.mDistToTop);
                    maxConnectWidth = Math.max(maxConnectWidth, connectWidth);
                    maxConnectHeight = Math.max(maxConnectHeight, connectHeight);
                }
                maxTopDist++;
                r11 = 0;
            } catch (Throwable th) {
                for (int j3 = 0; j3 < size; j3++) {
                    ConstraintWidget child3 = children.get(j3);
                    child3.mHorizontalWrapVisited = false;
                    child3.mVerticalWrapVisited = false;
                    child3.mLeftHasCentered = false;
                    child3.mRightHasCentered = false;
                    child3.mTopHasCentered = false;
                    child3.mBottomHasCentered = false;
                }
                throw th;
            }
        }
        int max = Math.max(maxLeftDist, maxRightDist);
        this.mWrapWidth = Math.max(this.mMinWidth, Math.max(max, maxConnectWidth));
        int max2 = Math.max(maxBottomDist, maxConnectHeight2);
        this.mWrapHeight = Math.max(this.mMinHeight, Math.max(max2, maxConnectHeight));
        for (int j4 = 0; j4 < size; j4++) {
            ConstraintWidget child4 = children.get(j4);
            child4.mHorizontalWrapVisited = false;
            child4.mVerticalWrapVisited = false;
            child4.mLeftHasCentered = false;
            child4.mRightHasCentered = false;
            child4.mTopHasCentered = false;
            child4.mBottomHasCentered = false;
        }
    }

    public boolean handlesInternalConstraints() {
        return false;
    }

    private void resetChains() {
        this.mHorizontalChainsSize = 0;
        this.mVerticalChainsSize = 0;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addChain(ConstraintWidget constraintWidget, int type) {
        ConstraintWidget widget = constraintWidget;
        if (type == 0) {
            while (widget.mLeft.mTarget != null && widget.mLeft.mTarget.mOwner.mRight.mTarget != null && widget.mLeft.mTarget.mOwner.mRight.mTarget == widget.mLeft && widget.mLeft.mTarget.mOwner != widget) {
                widget = widget.mLeft.mTarget.mOwner;
            }
            addHorizontalChain(widget);
        } else if (type == 1) {
            while (widget.mTop.mTarget != null && widget.mTop.mTarget.mOwner.mBottom.mTarget != null && widget.mTop.mTarget.mOwner.mBottom.mTarget == widget.mTop && widget.mTop.mTarget.mOwner != widget) {
                widget = widget.mTop.mTarget.mOwner;
            }
            addVerticalChain(widget);
        }
    }

    private void addHorizontalChain(ConstraintWidget widget) {
        for (int i = 0; i < this.mHorizontalChainsSize; i++) {
            if (this.mHorizontalChainsArray[i] == widget) {
                return;
            }
        }
        int i2 = this.mHorizontalChainsSize;
        if (i2 + 1 >= this.mHorizontalChainsArray.length) {
            this.mHorizontalChainsArray = (ConstraintWidget[]) Arrays.copyOf(this.mHorizontalChainsArray, this.mHorizontalChainsArray.length * 2);
        }
        this.mHorizontalChainsArray[this.mHorizontalChainsSize] = widget;
        this.mHorizontalChainsSize++;
    }

    private void addVerticalChain(ConstraintWidget widget) {
        for (int i = 0; i < this.mVerticalChainsSize; i++) {
            if (this.mVerticalChainsArray[i] == widget) {
                return;
            }
        }
        int i2 = this.mVerticalChainsSize;
        if (i2 + 1 >= this.mVerticalChainsArray.length) {
            this.mVerticalChainsArray = (ConstraintWidget[]) Arrays.copyOf(this.mVerticalChainsArray, this.mVerticalChainsArray.length * 2);
        }
        this.mVerticalChainsArray[this.mVerticalChainsSize] = widget;
        this.mVerticalChainsSize++;
    }

    private int countMatchConstraintsChainedWidgets(LinearSystem system, ConstraintWidget[] chainEnds, ConstraintWidget widget, int direction, boolean[] flags) {
        char c;
        ConstraintWidget last;
        char c2;
        ConstraintWidget widget2 = widget;
        flags[0] = true;
        flags[1] = false;
        ConstraintWidget constraintWidget = null;
        chainEnds[0] = null;
        chainEnds[2] = null;
        chainEnds[1] = null;
        chainEnds[3] = null;
        int i = 5;
        int i2 = 8;
        if (direction == 0) {
            boolean fixedPosition = true;
            if (widget2.mLeft.mTarget != null && widget2.mLeft.mTarget.mOwner != this) {
                fixedPosition = false;
            }
            widget2.mHorizontalNextWidget = null;
            ConstraintWidget firstVisible = null;
            if (widget.getVisibility() != 8) {
                firstVisible = widget2;
            }
            int count = 0;
            ConstraintWidget firstVisible2 = firstVisible;
            ConstraintWidget last2 = null;
            while (widget2.mRight.mTarget != null) {
                widget2.mHorizontalNextWidget = constraintWidget;
                if (widget2.getVisibility() != 8) {
                    if (firstVisible2 == null) {
                        firstVisible2 = widget2;
                    }
                    if (firstVisible != null && firstVisible != widget2) {
                        firstVisible.mHorizontalNextWidget = widget2;
                    }
                    ConstraintWidget lastVisible = widget2;
                    firstVisible = lastVisible;
                } else {
                    system.addEquality(widget2.mLeft.mSolverVariable, widget2.mLeft.mTarget.mSolverVariable, 0, 5);
                    system.addEquality(widget2.mRight.mSolverVariable, widget2.mLeft.mSolverVariable, 0, 5);
                }
                if (widget2.getVisibility() != 8 && widget2.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                    if (widget2.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                        flags[0] = false;
                    }
                    if (widget2.mDimensionRatio <= 0.0f) {
                        flags[0] = false;
                        if (count + 1 >= this.mMatchConstraintsChainedWidgets.length) {
                            this.mMatchConstraintsChainedWidgets = (ConstraintWidget[]) Arrays.copyOf(this.mMatchConstraintsChainedWidgets, this.mMatchConstraintsChainedWidgets.length * 2);
                        }
                        this.mMatchConstraintsChainedWidgets[count] = widget2;
                        count++;
                    }
                }
                if (widget2.mRight.mTarget.mOwner.mLeft.mTarget == null || widget2.mRight.mTarget.mOwner.mLeft.mTarget.mOwner != widget2 || widget2.mRight.mTarget.mOwner == widget2) {
                    break;
                }
                widget2 = widget2.mRight.mTarget.mOwner;
                last2 = widget2;
                constraintWidget = null;
            }
            if (widget2.mRight.mTarget != null && widget2.mRight.mTarget.mOwner != this) {
                fixedPosition = false;
            }
            if (widget2.mLeft.mTarget != null) {
                last = last2;
                if (last.mRight.mTarget != null) {
                    c2 = 1;
                    widget2.mHorizontalChainFixedPosition = fixedPosition;
                    last.mHorizontalNextWidget = null;
                    chainEnds[0] = widget2;
                    chainEnds[2] = firstVisible2;
                    chainEnds[c2] = last;
                    chainEnds[3] = firstVisible;
                    return count;
                }
            } else {
                last = last2;
            }
            c2 = 1;
            flags[1] = true;
            widget2.mHorizontalChainFixedPosition = fixedPosition;
            last.mHorizontalNextWidget = null;
            chainEnds[0] = widget2;
            chainEnds[2] = firstVisible2;
            chainEnds[c2] = last;
            chainEnds[3] = firstVisible;
            return count;
        }
        boolean fixedPosition2 = true;
        if (widget2.mTop.mTarget != null && widget2.mTop.mTarget.mOwner != this) {
            fixedPosition2 = false;
        }
        widget2.mVerticalNextWidget = null;
        ConstraintWidget firstVisible3 = null;
        if (widget.getVisibility() != 8) {
            firstVisible3 = widget2;
        }
        ConstraintWidget last3 = null;
        int count2 = 0;
        ConstraintWidget firstVisible4 = firstVisible3;
        while (widget2.mBottom.mTarget != null) {
            widget2.mVerticalNextWidget = null;
            if (widget2.getVisibility() != i2) {
                if (firstVisible4 == null) {
                    firstVisible4 = widget2;
                }
                if (firstVisible3 != null && firstVisible3 != widget2) {
                    firstVisible3.mVerticalNextWidget = widget2;
                }
                firstVisible3 = widget2;
            } else {
                system.addEquality(widget2.mTop.mSolverVariable, widget2.mTop.mTarget.mSolverVariable, 0, i);
                system.addEquality(widget2.mBottom.mSolverVariable, widget2.mTop.mSolverVariable, 0, i);
            }
            i2 = 8;
            if (widget2.getVisibility() != 8 && widget2.mVerticalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                if (widget2.mHorizontalDimensionBehaviour == ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT) {
                    flags[0] = false;
                }
                if (widget2.mDimensionRatio <= 0.0f) {
                    flags[0] = false;
                    if (count2 + 1 >= this.mMatchConstraintsChainedWidgets.length) {
                        this.mMatchConstraintsChainedWidgets = (ConstraintWidget[]) Arrays.copyOf(this.mMatchConstraintsChainedWidgets, this.mMatchConstraintsChainedWidgets.length * 2);
                    }
                    this.mMatchConstraintsChainedWidgets[count2] = widget2;
                    count2++;
                }
            }
            if (widget2.mBottom.mTarget.mOwner.mTop.mTarget == null || widget2.mBottom.mTarget.mOwner.mTop.mTarget.mOwner != widget2 || widget2.mBottom.mTarget.mOwner == widget2) {
                break;
            }
            widget2 = widget2.mBottom.mTarget.mOwner;
            last3 = widget2;
            i = 5;
        }
        if (widget2.mBottom.mTarget != null && widget2.mBottom.mTarget.mOwner != this) {
            fixedPosition2 = false;
        }
        if (widget2.mTop.mTarget == null || last3.mBottom.mTarget == null) {
            c = 1;
            flags[1] = true;
        } else {
            c = 1;
        }
        widget2.mVerticalChainFixedPosition = fixedPosition2;
        last3.mVerticalNextWidget = null;
        chainEnds[0] = widget2;
        chainEnds[2] = firstVisible4;
        chainEnds[c] = last3;
        chainEnds[3] = firstVisible3;
        return count2;
    }
}
