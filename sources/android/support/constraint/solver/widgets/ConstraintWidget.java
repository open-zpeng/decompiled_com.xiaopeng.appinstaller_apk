package android.support.constraint.solver.widgets;

import android.support.constraint.solver.ArrayRow;
import android.support.constraint.solver.Cache;
import android.support.constraint.solver.LinearSystem;
import android.support.constraint.solver.SolverVariable;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import java.util.ArrayList;
import xiaopeng.widget.BuildConfig;
/* loaded from: classes.dex */
public class ConstraintWidget {
    public static float DEFAULT_BIAS = 0.5f;
    boolean mBottomHasCentered;
    private Object mCompanionWidget;
    int mDistToBottom;
    int mDistToLeft;
    int mDistToRight;
    int mDistToTop;
    boolean mHorizontalChainFixedPosition;
    boolean mHorizontalWrapVisited;
    boolean mIsHeightWrapContent;
    boolean mIsWidthWrapContent;
    boolean mLeftHasCentered;
    protected int mMinHeight;
    protected int mMinWidth;
    boolean mRightHasCentered;
    boolean mTopHasCentered;
    boolean mVerticalChainFixedPosition;
    boolean mVerticalWrapVisited;
    private int mWrapHeight;
    private int mWrapWidth;
    public int mHorizontalResolution = -1;
    public int mVerticalResolution = -1;
    int mMatchConstraintDefaultWidth = 0;
    int mMatchConstraintDefaultHeight = 0;
    int mMatchConstraintMinWidth = 0;
    int mMatchConstraintMaxWidth = 0;
    float mMatchConstraintPercentWidth = 1.0f;
    int mMatchConstraintMinHeight = 0;
    int mMatchConstraintMaxHeight = 0;
    float mMatchConstraintPercentHeight = 1.0f;
    ConstraintAnchor mLeft = new ConstraintAnchor(this, ConstraintAnchor.Type.LEFT);
    ConstraintAnchor mTop = new ConstraintAnchor(this, ConstraintAnchor.Type.TOP);
    ConstraintAnchor mRight = new ConstraintAnchor(this, ConstraintAnchor.Type.RIGHT);
    ConstraintAnchor mBottom = new ConstraintAnchor(this, ConstraintAnchor.Type.BOTTOM);
    ConstraintAnchor mBaseline = new ConstraintAnchor(this, ConstraintAnchor.Type.BASELINE);
    ConstraintAnchor mCenterX = new ConstraintAnchor(this, ConstraintAnchor.Type.CENTER_X);
    ConstraintAnchor mCenterY = new ConstraintAnchor(this, ConstraintAnchor.Type.CENTER_Y);
    ConstraintAnchor mCenter = new ConstraintAnchor(this, ConstraintAnchor.Type.CENTER);
    protected ConstraintAnchor[] mListAnchors = {this.mLeft, this.mRight, this.mTop, this.mBottom, this.mBaseline};
    protected ArrayList<ConstraintAnchor> mAnchors = new ArrayList<>();
    ConstraintWidget mParent = null;
    int mWidth = 0;
    int mHeight = 0;
    protected float mDimensionRatio = 0.0f;
    protected int mDimensionRatioSide = -1;
    protected int mSolverLeft = 0;
    protected int mSolverTop = 0;
    protected int mSolverRight = 0;
    protected int mSolverBottom = 0;
    protected int mX = 0;
    protected int mY = 0;
    private int mDrawX = 0;
    private int mDrawY = 0;
    private int mDrawWidth = 0;
    private int mDrawHeight = 0;
    protected int mOffsetX = 0;
    protected int mOffsetY = 0;
    int mBaselineDistance = 0;
    float mHorizontalBiasPercent = DEFAULT_BIAS;
    float mVerticalBiasPercent = DEFAULT_BIAS;
    DimensionBehaviour mHorizontalDimensionBehaviour = DimensionBehaviour.FIXED;
    DimensionBehaviour mVerticalDimensionBehaviour = DimensionBehaviour.FIXED;
    private int mContainerItemSkip = 0;
    private int mVisibility = 0;
    private String mDebugName = null;
    private String mType = null;
    int mHorizontalChainStyle = 0;
    int mVerticalChainStyle = 0;
    float mHorizontalWeight = 0.0f;
    float mVerticalWeight = 0.0f;
    ConstraintWidget mHorizontalNextWidget = null;
    ConstraintWidget mVerticalNextWidget = null;

    /* loaded from: classes.dex */
    public enum DimensionBehaviour {
        FIXED,
        WRAP_CONTENT,
        MATCH_CONSTRAINT,
        MATCH_PARENT
    }

    public void reset() {
        this.mLeft.reset();
        this.mTop.reset();
        this.mRight.reset();
        this.mBottom.reset();
        this.mBaseline.reset();
        this.mCenterX.reset();
        this.mCenterY.reset();
        this.mCenter.reset();
        this.mParent = null;
        this.mWidth = 0;
        this.mHeight = 0;
        this.mDimensionRatio = 0.0f;
        this.mDimensionRatioSide = -1;
        this.mX = 0;
        this.mY = 0;
        this.mDrawX = 0;
        this.mDrawY = 0;
        this.mDrawWidth = 0;
        this.mDrawHeight = 0;
        this.mOffsetX = 0;
        this.mOffsetY = 0;
        this.mBaselineDistance = 0;
        this.mMinWidth = 0;
        this.mMinHeight = 0;
        this.mWrapWidth = 0;
        this.mWrapHeight = 0;
        this.mHorizontalBiasPercent = DEFAULT_BIAS;
        this.mVerticalBiasPercent = DEFAULT_BIAS;
        this.mHorizontalDimensionBehaviour = DimensionBehaviour.FIXED;
        this.mVerticalDimensionBehaviour = DimensionBehaviour.FIXED;
        this.mCompanionWidget = null;
        this.mContainerItemSkip = 0;
        this.mVisibility = 0;
        this.mDebugName = null;
        this.mType = null;
        this.mHorizontalWrapVisited = false;
        this.mVerticalWrapVisited = false;
        this.mHorizontalChainStyle = 0;
        this.mVerticalChainStyle = 0;
        this.mHorizontalChainFixedPosition = false;
        this.mVerticalChainFixedPosition = false;
        this.mHorizontalWeight = 0.0f;
        this.mVerticalWeight = 0.0f;
        this.mHorizontalResolution = -1;
        this.mVerticalResolution = -1;
    }

    public ConstraintWidget() {
        addAnchors();
    }

    public void resetSolverVariables(Cache cache) {
        this.mLeft.resetSolverVariable(cache);
        this.mTop.resetSolverVariable(cache);
        this.mRight.resetSolverVariable(cache);
        this.mBottom.resetSolverVariable(cache);
        this.mBaseline.resetSolverVariable(cache);
        this.mCenter.resetSolverVariable(cache);
        this.mCenterX.resetSolverVariable(cache);
        this.mCenterY.resetSolverVariable(cache);
    }

    private void addAnchors() {
        this.mAnchors.add(this.mLeft);
        this.mAnchors.add(this.mTop);
        this.mAnchors.add(this.mRight);
        this.mAnchors.add(this.mBottom);
        this.mAnchors.add(this.mCenterX);
        this.mAnchors.add(this.mCenterY);
        this.mAnchors.add(this.mBaseline);
    }

    public boolean isRoot() {
        return this.mParent == null;
    }

    public ConstraintWidget getParent() {
        return this.mParent;
    }

    public void setParent(ConstraintWidget widget) {
        this.mParent = widget;
    }

    public void setWidthWrapContent(boolean widthWrapContent) {
        this.mIsWidthWrapContent = widthWrapContent;
    }

    public void setHeightWrapContent(boolean heightWrapContent) {
        this.mIsHeightWrapContent = heightWrapContent;
    }

    public void setVisibility(int visibility) {
        this.mVisibility = visibility;
    }

    public int getVisibility() {
        return this.mVisibility;
    }

    public String getDebugName() {
        return this.mDebugName;
    }

    public String toString() {
        String str;
        String str2;
        StringBuilder sb = new StringBuilder();
        if (this.mType != null) {
            str = "type: " + this.mType + " ";
        } else {
            str = BuildConfig.FLAVOR;
        }
        sb.append(str);
        if (this.mDebugName != null) {
            str2 = "id: " + this.mDebugName + " ";
        } else {
            str2 = BuildConfig.FLAVOR;
        }
        sb.append(str2);
        sb.append("(");
        sb.append(this.mX);
        sb.append(", ");
        sb.append(this.mY);
        sb.append(") - (");
        sb.append(this.mWidth);
        sb.append(" x ");
        sb.append(this.mHeight);
        sb.append(")");
        sb.append(" wrap: (");
        sb.append(this.mWrapWidth);
        sb.append(" x ");
        sb.append(this.mWrapHeight);
        sb.append(")");
        return sb.toString();
    }

    public int getX() {
        return this.mX;
    }

    public int getY() {
        return this.mY;
    }

    public int getWidth() {
        if (this.mVisibility == 8) {
            return 0;
        }
        return this.mWidth;
    }

    public int getOptimizerWrapWidth() {
        int w;
        int w2 = this.mWidth;
        if (this.mHorizontalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT) {
            if (this.mMatchConstraintDefaultWidth == 1) {
                w = Math.max(this.mMatchConstraintMinWidth, w2);
            } else if (this.mMatchConstraintMinWidth > 0) {
                w = this.mMatchConstraintMinWidth;
                this.mWidth = w;
            } else {
                w = 0;
            }
            if (this.mMatchConstraintMaxWidth > 0 && this.mMatchConstraintMaxWidth < w) {
                return this.mMatchConstraintMaxWidth;
            }
            return w;
        }
        return w2;
    }

    public int getOptimizerWrapHeight() {
        int h;
        int h2 = this.mHeight;
        if (this.mVerticalDimensionBehaviour == DimensionBehaviour.MATCH_CONSTRAINT) {
            if (this.mMatchConstraintDefaultHeight == 1) {
                h = Math.max(this.mMatchConstraintMinHeight, h2);
            } else if (this.mMatchConstraintMinHeight > 0) {
                h = this.mMatchConstraintMinHeight;
                this.mHeight = h;
            } else {
                h = 0;
            }
            if (this.mMatchConstraintMaxHeight > 0 && this.mMatchConstraintMaxHeight < h) {
                return this.mMatchConstraintMaxHeight;
            }
            return h;
        }
        return h2;
    }

    public int getWrapWidth() {
        return this.mWrapWidth;
    }

    public int getHeight() {
        if (this.mVisibility == 8) {
            return 0;
        }
        return this.mHeight;
    }

    public int getWrapHeight() {
        return this.mWrapHeight;
    }

    public int getDrawX() {
        return this.mDrawX + this.mOffsetX;
    }

    public int getDrawY() {
        return this.mDrawY + this.mOffsetY;
    }

    public int getDrawBottom() {
        return getDrawY() + this.mDrawHeight;
    }

    public int getDrawRight() {
        return getDrawX() + this.mDrawWidth;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getRootX() {
        return this.mX + this.mOffsetX;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public int getRootY() {
        return this.mY + this.mOffsetY;
    }

    public int getRight() {
        return getX() + this.mWidth;
    }

    public int getBottom() {
        return getY() + this.mHeight;
    }

    public boolean hasBaseline() {
        return this.mBaselineDistance > 0;
    }

    public int getBaselineDistance() {
        return this.mBaselineDistance;
    }

    public Object getCompanionWidget() {
        return this.mCompanionWidget;
    }

    public ArrayList<ConstraintAnchor> getAnchors() {
        return this.mAnchors;
    }

    public void setX(int x) {
        this.mX = x;
    }

    public void setY(int y) {
        this.mY = y;
    }

    public void setOrigin(int x, int y) {
        this.mX = x;
        this.mY = y;
    }

    public void setOffset(int x, int y) {
        this.mOffsetX = x;
        this.mOffsetY = y;
    }

    public void updateDrawPosition() {
        int left = this.mX;
        int top = this.mY;
        int right = this.mX + this.mWidth;
        int bottom = this.mY + this.mHeight;
        this.mDrawX = left;
        this.mDrawY = top;
        this.mDrawWidth = right - left;
        this.mDrawHeight = bottom - top;
    }

    public void setWidth(int w) {
        this.mWidth = w;
        if (this.mWidth < this.mMinWidth) {
            this.mWidth = this.mMinWidth;
        }
    }

    public void setHeight(int h) {
        this.mHeight = h;
        if (this.mHeight < this.mMinHeight) {
            this.mHeight = this.mMinHeight;
        }
    }

    public void setHorizontalMatchStyle(int horizontalMatchStyle, int min, int max, float percent) {
        this.mMatchConstraintDefaultWidth = horizontalMatchStyle;
        this.mMatchConstraintMinWidth = min;
        this.mMatchConstraintMaxWidth = max;
        this.mMatchConstraintPercentWidth = percent;
    }

    public void setVerticalMatchStyle(int verticalMatchStyle, int min, int max, float percent) {
        this.mMatchConstraintDefaultHeight = verticalMatchStyle;
        this.mMatchConstraintMinHeight = min;
        this.mMatchConstraintMaxHeight = max;
        this.mMatchConstraintPercentHeight = percent;
    }

    public void setDimensionRatio(String ratio) {
        int commaIndex;
        if (ratio == null || ratio.length() == 0) {
            this.mDimensionRatio = 0.0f;
            return;
        }
        int dimensionRatioSide = -1;
        float dimensionRatio = 0.0f;
        int len = ratio.length();
        int commaIndex2 = ratio.indexOf(44);
        if (commaIndex2 > 0 && commaIndex2 < len - 1) {
            String dimension = ratio.substring(0, commaIndex2);
            if (dimension.equalsIgnoreCase("W")) {
                dimensionRatioSide = 0;
            } else if (dimension.equalsIgnoreCase("H")) {
                dimensionRatioSide = 1;
            }
            commaIndex = commaIndex2 + 1;
        } else {
            commaIndex = 0;
        }
        int colonIndex = ratio.indexOf(58);
        if (colonIndex >= 0 && colonIndex < len - 1) {
            String nominator = ratio.substring(commaIndex, colonIndex);
            String denominator = ratio.substring(colonIndex + 1);
            if (nominator.length() > 0 && denominator.length() > 0) {
                try {
                    float nominatorValue = Float.parseFloat(nominator);
                    float denominatorValue = Float.parseFloat(denominator);
                    if (nominatorValue > 0.0f && denominatorValue > 0.0f) {
                        dimensionRatio = dimensionRatioSide == 1 ? Math.abs(denominatorValue / nominatorValue) : Math.abs(nominatorValue / denominatorValue);
                    }
                } catch (NumberFormatException e) {
                }
            }
        } else {
            String r = ratio.substring(commaIndex);
            if (r.length() > 0) {
                try {
                    dimensionRatio = Float.parseFloat(r);
                } catch (NumberFormatException e2) {
                }
            }
        }
        if (dimensionRatio > 0.0f) {
            this.mDimensionRatio = dimensionRatio;
            this.mDimensionRatioSide = dimensionRatioSide;
        }
    }

    public void setHorizontalBiasPercent(float horizontalBiasPercent) {
        this.mHorizontalBiasPercent = horizontalBiasPercent;
    }

    public void setVerticalBiasPercent(float verticalBiasPercent) {
        this.mVerticalBiasPercent = verticalBiasPercent;
    }

    public void setMinWidth(int w) {
        if (w < 0) {
            this.mMinWidth = 0;
        } else {
            this.mMinWidth = w;
        }
    }

    public void setMinHeight(int h) {
        if (h < 0) {
            this.mMinHeight = 0;
        } else {
            this.mMinHeight = h;
        }
    }

    public void setWrapWidth(int w) {
        this.mWrapWidth = w;
    }

    public void setWrapHeight(int h) {
        this.mWrapHeight = h;
    }

    public void setFrame(int left, int top, int right, int bottom) {
        int w = right - left;
        int h = bottom - top;
        this.mX = left;
        this.mY = top;
        if (this.mVisibility == 8) {
            this.mWidth = 0;
            this.mHeight = 0;
            return;
        }
        if (this.mHorizontalDimensionBehaviour == DimensionBehaviour.FIXED && w < this.mWidth) {
            w = this.mWidth;
        }
        if (this.mVerticalDimensionBehaviour == DimensionBehaviour.FIXED && h < this.mHeight) {
            h = this.mHeight;
        }
        this.mWidth = w;
        this.mHeight = h;
        if (this.mHeight < this.mMinHeight) {
            this.mHeight = this.mMinHeight;
        }
        if (this.mWidth < this.mMinWidth) {
            this.mWidth = this.mMinWidth;
        }
    }

    public void setHorizontalDimension(int left, int right) {
        this.mX = left;
        this.mWidth = right - left;
        if (this.mWidth < this.mMinWidth) {
            this.mWidth = this.mMinWidth;
        }
    }

    public void setVerticalDimension(int top, int bottom) {
        this.mY = top;
        this.mHeight = bottom - top;
        if (this.mHeight < this.mMinHeight) {
            this.mHeight = this.mMinHeight;
        }
    }

    public void setBaselineDistance(int baseline) {
        this.mBaselineDistance = baseline;
    }

    public void setCompanionWidget(Object companion) {
        this.mCompanionWidget = companion;
    }

    public void setHorizontalWeight(float horizontalWeight) {
        this.mHorizontalWeight = horizontalWeight;
    }

    public void setVerticalWeight(float verticalWeight) {
        this.mVerticalWeight = verticalWeight;
    }

    public void setHorizontalChainStyle(int horizontalChainStyle) {
        this.mHorizontalChainStyle = horizontalChainStyle;
    }

    public void setVerticalChainStyle(int verticalChainStyle) {
        this.mVerticalChainStyle = verticalChainStyle;
    }

    public void immediateConnect(ConstraintAnchor.Type startType, ConstraintWidget target, ConstraintAnchor.Type endType, int margin, int goneMargin) {
        ConstraintAnchor startAnchor = getAnchor(startType);
        ConstraintAnchor endAnchor = target.getAnchor(endType);
        startAnchor.connect(endAnchor, margin, goneMargin, ConstraintAnchor.Strength.STRONG, 0, true);
    }

    public void resetAnchors() {
        ConstraintWidget parent = getParent();
        if (parent != null && (parent instanceof ConstraintWidgetContainer)) {
            ConstraintWidgetContainer parentContainer = (ConstraintWidgetContainer) getParent();
            if (parentContainer.handlesInternalConstraints()) {
                return;
            }
        }
        int mAnchorsSize = this.mAnchors.size();
        for (int i = 0; i < mAnchorsSize; i++) {
            ConstraintAnchor anchor = this.mAnchors.get(i);
            anchor.reset();
        }
    }

    public ConstraintAnchor getAnchor(ConstraintAnchor.Type anchorType) {
        switch (anchorType) {
            case LEFT:
                return this.mLeft;
            case TOP:
                return this.mTop;
            case RIGHT:
                return this.mRight;
            case BOTTOM:
                return this.mBottom;
            case BASELINE:
                return this.mBaseline;
            case CENTER:
                return this.mCenter;
            case CENTER_X:
                return this.mCenterX;
            case CENTER_Y:
                return this.mCenterY;
            case NONE:
                return null;
            default:
                throw new AssertionError(anchorType.name());
        }
    }

    public DimensionBehaviour getHorizontalDimensionBehaviour() {
        return this.mHorizontalDimensionBehaviour;
    }

    public DimensionBehaviour getVerticalDimensionBehaviour() {
        return this.mVerticalDimensionBehaviour;
    }

    public void setHorizontalDimensionBehaviour(DimensionBehaviour behaviour) {
        this.mHorizontalDimensionBehaviour = behaviour;
        if (this.mHorizontalDimensionBehaviour == DimensionBehaviour.WRAP_CONTENT) {
            setWidth(this.mWrapWidth);
        }
    }

    public void setVerticalDimensionBehaviour(DimensionBehaviour behaviour) {
        this.mVerticalDimensionBehaviour = behaviour;
        if (this.mVerticalDimensionBehaviour == DimensionBehaviour.WRAP_CONTENT) {
            setHeight(this.mWrapHeight);
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:216:0x03eb, code lost:
        if (r14 == (-1)) goto L258;
     */
    /* JADX WARN: Removed duplicated region for block: B:182:0x02ca  */
    /* JADX WARN: Removed duplicated region for block: B:201:0x03be  */
    /* JADX WARN: Removed duplicated region for block: B:204:0x03d3 A[RETURN] */
    /* JADX WARN: Removed duplicated region for block: B:205:0x03d4  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    public void addToSolver(android.support.constraint.solver.LinearSystem r51, int r52) {
        /*
            Method dump skipped, instructions count: 1690
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.constraint.solver.widgets.ConstraintWidget.addToSolver(android.support.constraint.solver.LinearSystem, int):void");
    }

    private void applyConstraints(LinearSystem system, boolean wrapContent, boolean dimensionFixed, ConstraintAnchor beginAnchor, ConstraintAnchor endAnchor, int beginPosition, int endPosition, int dimension, int minDimension, float bias, boolean useRatio, boolean inChain, int matchConstraintDefault, int matchMinDimension, int matchMaxDimension, float matchPercentDimension) {
        boolean dimensionFixed2;
        int dimension2;
        int beginAnchorMargin;
        int dimension3;
        SolverVariable begin;
        int matchMaxDimension2;
        int matchMinDimension2;
        int endAnchorMargin;
        SolverVariable begin2;
        ConstraintWidget constraintWidget;
        SolverVariable percentEnd;
        SolverVariable percentBegin;
        int matchMaxDimension3;
        int i;
        int beginAnchorMargin2;
        SolverVariable end;
        SolverVariable end2;
        ConstraintWidget constraintWidget2;
        SolverVariable percentEnd2;
        SolverVariable percentBegin2;
        SolverVariable end3;
        SolverVariable endTarget;
        ConstraintWidget constraintWidget3;
        SolverVariable percentBegin3;
        SolverVariable percentEnd3;
        int dimension4;
        SolverVariable endTarget2;
        SolverVariable beginTarget;
        SolverVariable end4;
        int matchMinDimension3;
        int matchMaxDimension4;
        int endAnchorMargin2;
        int matchMaxDimension5;
        SolverVariable percentEnd4;
        SolverVariable percentBegin4;
        SolverVariable begin3 = system.createObjectVariable(beginAnchor);
        SolverVariable end5 = system.createObjectVariable(endAnchor);
        SolverVariable beginTarget2 = system.createObjectVariable(beginAnchor.getTarget());
        SolverVariable endTarget3 = system.createObjectVariable(endAnchor.getTarget());
        int beginAnchorMargin3 = beginAnchor.getMargin();
        int endAnchorMargin3 = endAnchor.getMargin();
        if (this.mVisibility == 8) {
            dimension2 = 0;
            dimensionFixed2 = true;
        } else {
            dimensionFixed2 = dimensionFixed;
            dimension2 = dimension;
        }
        int matchMinDimension4 = matchMinDimension;
        if (matchMinDimension4 == -2) {
            matchMinDimension4 = dimension2;
        }
        int matchMaxDimension6 = matchMaxDimension;
        if (matchMaxDimension6 == -2) {
            int matchMaxDimension7 = dimension2;
            matchMaxDimension6 = matchMaxDimension7;
        }
        int dimension5 = Math.max(dimension2, matchMinDimension4);
        if (beginTarget2 == null && endTarget3 == null) {
            system.addConstraint(system.createRow().createRowEquals(begin3, beginPosition));
            if (matchConstraintDefault == 2) {
                if (beginAnchor.getType() == ConstraintAnchor.Type.TOP || beginAnchor.getType() == ConstraintAnchor.Type.BOTTOM) {
                    SolverVariable percentBegin5 = system.createObjectVariable(this.mParent.getAnchor(ConstraintAnchor.Type.TOP));
                    SolverVariable percentEnd5 = system.createObjectVariable(this.mParent.getAnchor(ConstraintAnchor.Type.BOTTOM));
                    percentEnd4 = percentEnd5;
                    percentBegin4 = percentBegin5;
                } else {
                    SolverVariable percentBegin6 = system.createObjectVariable(this.mParent.getAnchor(ConstraintAnchor.Type.LEFT));
                    percentBegin4 = percentBegin6;
                    SolverVariable percentEnd6 = system.createObjectVariable(this.mParent.getAnchor(ConstraintAnchor.Type.RIGHT));
                    percentEnd4 = percentEnd6;
                }
                endAnchorMargin2 = endAnchorMargin3;
                matchMaxDimension4 = matchMaxDimension6;
                matchMaxDimension5 = beginAnchorMargin3;
                dimension4 = dimension5;
                endTarget2 = endTarget3;
                beginTarget = beginTarget2;
                end4 = end5;
                matchMinDimension3 = matchMinDimension4;
                system.addConstraint(system.createRow().createRowDimensionRatio(end5, begin3, percentEnd4, percentBegin4, matchPercentDimension));
            } else {
                dimension4 = dimension5;
                endTarget2 = endTarget3;
                beginTarget = beginTarget2;
                end4 = end5;
                matchMinDimension3 = matchMinDimension4;
                matchMaxDimension4 = matchMaxDimension6;
                endAnchorMargin2 = endAnchorMargin3;
                matchMaxDimension5 = beginAnchorMargin3;
                if (!useRatio) {
                    if (wrapContent) {
                        system.addConstraint(LinearSystem.createRowEquals(system, end4, begin3, minDimension, true));
                    } else if (dimensionFixed2) {
                        system.addConstraint(LinearSystem.createRowEquals(system, end4, begin3, dimension4, false));
                    } else {
                        system.addConstraint(system.createRow().createRowEquals(end4, endPosition));
                    }
                }
            }
            return;
        }
        int matchMinDimension5 = matchMinDimension4;
        int matchMaxDimension8 = matchMaxDimension6;
        if (beginTarget2 == null || endTarget3 != null) {
            beginAnchorMargin = beginAnchorMargin3;
            if (beginTarget2 != null || endTarget3 == null) {
                if (!dimensionFixed2) {
                    dimension3 = dimension5;
                    if (useRatio) {
                        system.addGreaterThan(begin3, beginTarget2, beginAnchorMargin, 3);
                        system.addLowerThan(end5, endTarget3, (-1) * endAnchorMargin3, 3);
                        system.addConstraint(LinearSystem.createRowCentering(system, begin3, beginTarget2, beginAnchorMargin, bias, endTarget3, end5, endAnchorMargin3, true));
                        return;
                    } else if (inChain) {
                        return;
                    } else {
                        if (matchConstraintDefault == 1) {
                            begin = begin3;
                            matchMaxDimension2 = matchMaxDimension8;
                            matchMinDimension2 = matchMinDimension5;
                            endAnchorMargin = endAnchorMargin3;
                        } else if (matchConstraintDefault == 2) {
                            begin = begin3;
                            matchMaxDimension2 = matchMaxDimension8;
                            matchMinDimension2 = matchMinDimension5;
                            endAnchorMargin = endAnchorMargin3;
                        } else if (matchMinDimension5 != 0 || matchMaxDimension8 != 0) {
                            if (matchMaxDimension8 > 0) {
                                matchMaxDimension3 = matchMaxDimension8;
                                system.addLowerThan(end5, begin3, matchMaxDimension3, 3);
                            } else {
                                matchMaxDimension3 = matchMaxDimension8;
                            }
                            if (matchMinDimension5 > 0) {
                                i = 2;
                                system.addGreaterThan(end5, begin3, matchMinDimension5, 2);
                            } else {
                                i = 2;
                            }
                            system.addGreaterThan(begin3, beginTarget2, beginAnchorMargin, i);
                            system.addLowerThan(end5, endTarget3, -endAnchorMargin3, i);
                            system.addCentering(begin3, beginTarget2, beginAnchorMargin, bias, endTarget3, end5, endAnchorMargin3, 4);
                            return;
                        } else {
                            system.addConstraint(system.createRow().createRowEquals(begin3, beginTarget2, beginAnchorMargin));
                            system.addConstraint(system.createRow().createRowEquals(end5, endTarget3, (-1) * endAnchorMargin3));
                        }
                        if (matchConstraintDefault == 2) {
                            if (beginAnchor.getType() == ConstraintAnchor.Type.TOP) {
                                begin2 = begin;
                                constraintWidget = this;
                            } else if (beginAnchor.getType() == ConstraintAnchor.Type.BOTTOM) {
                                begin2 = begin;
                                constraintWidget = this;
                            } else {
                                begin2 = begin;
                                percentBegin = system.createObjectVariable(this.mParent.getAnchor(ConstraintAnchor.Type.LEFT));
                                percentEnd = system.createObjectVariable(this.mParent.getAnchor(ConstraintAnchor.Type.RIGHT));
                                system.addConstraint(system.createRow().createRowDimensionRatio(end5, begin2, percentEnd, percentBegin, matchPercentDimension));
                            }
                            percentBegin = system.createObjectVariable(constraintWidget.mParent.getAnchor(ConstraintAnchor.Type.TOP));
                            percentEnd = system.createObjectVariable(constraintWidget.mParent.getAnchor(ConstraintAnchor.Type.BOTTOM));
                            system.addConstraint(system.createRow().createRowDimensionRatio(end5, begin2, percentEnd, percentBegin, matchPercentDimension));
                        } else {
                            begin2 = begin;
                        }
                        int dimension6 = dimension3;
                        if (matchMinDimension2 > dimension6) {
                            int dimension7 = matchMinDimension2;
                            dimension6 = dimension7;
                        }
                        int matchMaxDimension9 = matchMaxDimension2;
                        if (matchMaxDimension9 > 0) {
                            if (matchMaxDimension9 < dimension6) {
                                dimension6 = matchMaxDimension9;
                            } else {
                                system.addLowerThan(end5, begin2, matchMaxDimension9, 3);
                            }
                        }
                        system.addEquality(end5, begin2, dimension6, 3);
                        system.addGreaterThan(begin2, beginTarget2, beginAnchorMargin, 2);
                        int endAnchorMargin4 = endAnchorMargin;
                        system.addLowerThan(end5, endTarget3, -endAnchorMargin4, 2);
                        system.addCentering(begin2, beginTarget2, beginAnchorMargin, bias, endTarget3, end5, endAnchorMargin4, 4);
                        return;
                    }
                }
                if (wrapContent) {
                    system.addConstraint(LinearSystem.createRowEquals(system, end5, begin3, minDimension, true));
                } else {
                    system.addConstraint(system.createRow().createRowEquals(end5, begin3, dimension5));
                }
                if (beginAnchor.getStrength() != endAnchor.getStrength()) {
                    if (beginAnchor.getStrength() == ConstraintAnchor.Strength.STRONG) {
                        beginAnchorMargin2 = beginAnchorMargin;
                        system.addConstraint(system.createRow().createRowEquals(begin3, beginTarget2, beginAnchorMargin2));
                        SolverVariable slack = system.createSlackVariable();
                        ArrayRow row = system.createRow();
                        row.createRowLowerThan(end5, endTarget3, slack, (-1) * endAnchorMargin3);
                        system.addConstraint(row);
                    } else {
                        beginAnchorMargin2 = beginAnchorMargin;
                        SolverVariable slack2 = system.createSlackVariable();
                        ArrayRow row2 = system.createRow();
                        row2.createRowGreaterThan(begin3, beginTarget2, slack2, beginAnchorMargin2);
                        system.addConstraint(row2);
                        system.addConstraint(system.createRow().createRowEquals(end5, endTarget3, (-1) * endAnchorMargin3));
                    }
                } else if (beginTarget2 == endTarget3) {
                    system.addConstraint(LinearSystem.createRowCentering(system, begin3, beginTarget2, 0, 0.5f, endTarget3, end5, 0, true));
                    return;
                } else if (inChain) {
                    return;
                } else {
                    boolean useBidirectionalError = beginAnchor.getConnectionType() != ConstraintAnchor.ConnectionType.STRICT;
                    system.addConstraint(LinearSystem.createRowGreaterThan(system, begin3, beginTarget2, beginAnchorMargin, useBidirectionalError));
                    boolean useBidirectionalError2 = endAnchor.getConnectionType() != ConstraintAnchor.ConnectionType.STRICT;
                    system.addConstraint(LinearSystem.createRowLowerThan(system, end5, endTarget3, (-1) * endAnchorMargin3, useBidirectionalError2));
                    dimension3 = dimension5;
                    system.addConstraint(LinearSystem.createRowCentering(system, begin3, beginTarget2, beginAnchorMargin, bias, endTarget3, end5, endAnchorMargin3, false));
                }
                return;
            }
            system.addConstraint(system.createRow().createRowEquals(end5, endTarget3, (-1) * endAnchorMargin3));
            if (matchConstraintDefault == 2) {
                if (beginAnchor.getType() == ConstraintAnchor.Type.TOP) {
                    end2 = end5;
                    constraintWidget2 = this;
                } else if (beginAnchor.getType() == ConstraintAnchor.Type.BOTTOM) {
                    end2 = end5;
                    constraintWidget2 = this;
                } else {
                    end2 = end5;
                    percentBegin2 = system.createObjectVariable(this.mParent.getAnchor(ConstraintAnchor.Type.LEFT));
                    percentEnd2 = system.createObjectVariable(this.mParent.getAnchor(ConstraintAnchor.Type.RIGHT));
                    end = end2;
                    system.addConstraint(system.createRow().createRowDimensionRatio(end2, begin3, percentEnd2, percentBegin2, matchPercentDimension));
                }
                percentBegin2 = system.createObjectVariable(constraintWidget2.mParent.getAnchor(ConstraintAnchor.Type.TOP));
                percentEnd2 = system.createObjectVariable(constraintWidget2.mParent.getAnchor(ConstraintAnchor.Type.BOTTOM));
                end = end2;
                system.addConstraint(system.createRow().createRowDimensionRatio(end2, begin3, percentEnd2, percentBegin2, matchPercentDimension));
            } else {
                end = end5;
                if (wrapContent) {
                    system.addConstraint(LinearSystem.createRowEquals(system, end, begin3, minDimension, true));
                } else if (useRatio) {
                } else {
                    if (!dimensionFixed2) {
                        system.addConstraint(system.createRow().createRowEquals(begin3, beginPosition));
                    }
                    system.addConstraint(system.createRow().createRowEquals(end, begin3, dimension5));
                }
            }
        } else {
            system.addConstraint(system.createRow().createRowEquals(begin3, beginTarget2, beginAnchorMargin3));
            if (matchConstraintDefault == 2) {
                if (beginAnchor.getType() == ConstraintAnchor.Type.TOP) {
                    endTarget = endTarget3;
                    constraintWidget3 = this;
                } else if (beginAnchor.getType() == ConstraintAnchor.Type.BOTTOM) {
                    endTarget = endTarget3;
                    constraintWidget3 = this;
                } else {
                    endTarget = endTarget3;
                    SolverVariable percentBegin7 = system.createObjectVariable(this.mParent.getAnchor(ConstraintAnchor.Type.LEFT));
                    percentBegin3 = percentBegin7;
                    SolverVariable percentEnd7 = system.createObjectVariable(this.mParent.getAnchor(ConstraintAnchor.Type.RIGHT));
                    percentEnd3 = percentEnd7;
                    beginAnchorMargin = beginAnchorMargin3;
                    end3 = end5;
                    system.addConstraint(system.createRow().createRowDimensionRatio(end5, begin3, percentEnd3, percentBegin3, matchPercentDimension));
                }
                SolverVariable percentBegin8 = system.createObjectVariable(constraintWidget3.mParent.getAnchor(ConstraintAnchor.Type.TOP));
                SolverVariable percentEnd8 = system.createObjectVariable(constraintWidget3.mParent.getAnchor(ConstraintAnchor.Type.BOTTOM));
                percentEnd3 = percentEnd8;
                percentBegin3 = percentBegin8;
                beginAnchorMargin = beginAnchorMargin3;
                end3 = end5;
                system.addConstraint(system.createRow().createRowDimensionRatio(end5, begin3, percentEnd3, percentBegin3, matchPercentDimension));
            } else {
                beginAnchorMargin = beginAnchorMargin3;
                end3 = end5;
                if (wrapContent) {
                    system.addConstraint(LinearSystem.createRowEquals(system, end3, begin3, minDimension, true));
                } else if (!useRatio) {
                    if (dimensionFixed2) {
                        system.addConstraint(system.createRow().createRowEquals(end3, begin3, dimension5));
                    } else {
                        system.addConstraint(system.createRow().createRowEquals(end3, endPosition));
                    }
                }
            }
        }
    }

    public void updateFromSolver(LinearSystem system, int group) {
        if (group == Integer.MAX_VALUE) {
            int left = system.getObjectVariableValue(this.mLeft);
            int top = system.getObjectVariableValue(this.mTop);
            int right = system.getObjectVariableValue(this.mRight);
            int bottom = system.getObjectVariableValue(this.mBottom);
            setFrame(left, top, right, bottom);
        } else if (group == -2) {
            setFrame(this.mSolverLeft, this.mSolverTop, this.mSolverRight, this.mSolverBottom);
        } else {
            if (this.mLeft.mGroup == group) {
                this.mSolverLeft = system.getObjectVariableValue(this.mLeft);
            }
            if (this.mTop.mGroup == group) {
                this.mSolverTop = system.getObjectVariableValue(this.mTop);
            }
            if (this.mRight.mGroup == group) {
                this.mSolverRight = system.getObjectVariableValue(this.mRight);
            }
            if (this.mBottom.mGroup == group) {
                this.mSolverBottom = system.getObjectVariableValue(this.mBottom);
            }
        }
    }
}
