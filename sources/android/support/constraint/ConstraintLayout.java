package android.support.constraint;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.support.constraint.solver.widgets.ConstraintWidget;
import android.support.constraint.solver.widgets.ConstraintWidgetContainer;
import android.support.v7.preference.Preference;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.InflateException;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;
/* loaded from: classes.dex */
public class ConstraintLayout extends ViewGroup {
    static final boolean ALLOWS_EMBEDDED = false;
    public static final int DESIGN_INFO_ID = 0;
    private static final boolean SIMPLE_LAYOUT = true;
    private static final String TAG = "ConstraintLayout";
    private static final boolean USE_CONSTRAINTS_HELPER = true;
    public static final String VERSION = "ConstraintLayout-1.1.0-beta1";
    SparseArray<View> mChildrenByIds;
    private ArrayList<ConstraintHelper> mConstraintHelpers;
    private ConstraintSet mConstraintSet;
    private int mConstraintSetId;
    private HashMap<String, Integer> mDesignIds;
    private boolean mDirtyHierarchy;
    ConstraintWidgetContainer mLayoutWidget;
    private int mMaxHeight;
    private int mMaxWidth;
    private int mMinHeight;
    private int mMinWidth;
    private int mOptimizationLevel;
    private String mTitle;
    private final ArrayList<ConstraintWidget> mVariableDimensionsWidgets;

    public void setDesignInformation(int type, Object value1, Object value2) {
        if (type == 0 && (value1 instanceof String) && (value2 instanceof Integer)) {
            if (this.mDesignIds == null) {
                this.mDesignIds = new HashMap<>();
            }
            String name = (String) value1;
            int index = name.indexOf("/");
            if (index != -1) {
                name = name.substring(index + 1);
            }
            int id = ((Integer) value2).intValue();
            this.mDesignIds.put(name, Integer.valueOf(id));
        }
    }

    public Object getDesignInformation(int type, Object value) {
        if (type == 0 && (value instanceof String)) {
            String name = (String) value;
            if (this.mDesignIds != null && this.mDesignIds.containsKey(name)) {
                return this.mDesignIds.get(name);
            }
            return null;
        }
        return null;
    }

    public ConstraintLayout(Context context) {
        super(context);
        this.mChildrenByIds = new SparseArray<>();
        this.mConstraintHelpers = new ArrayList<>(4);
        this.mVariableDimensionsWidgets = new ArrayList<>(100);
        this.mLayoutWidget = new ConstraintWidgetContainer();
        this.mMinWidth = 0;
        this.mMinHeight = 0;
        this.mMaxWidth = Preference.DEFAULT_ORDER;
        this.mMaxHeight = Preference.DEFAULT_ORDER;
        this.mDirtyHierarchy = true;
        this.mOptimizationLevel = 2;
        this.mConstraintSet = null;
        this.mConstraintSetId = -1;
        this.mDesignIds = new HashMap<>();
        init(null);
    }

    public ConstraintLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mChildrenByIds = new SparseArray<>();
        this.mConstraintHelpers = new ArrayList<>(4);
        this.mVariableDimensionsWidgets = new ArrayList<>(100);
        this.mLayoutWidget = new ConstraintWidgetContainer();
        this.mMinWidth = 0;
        this.mMinHeight = 0;
        this.mMaxWidth = Preference.DEFAULT_ORDER;
        this.mMaxHeight = Preference.DEFAULT_ORDER;
        this.mDirtyHierarchy = true;
        this.mOptimizationLevel = 2;
        this.mConstraintSet = null;
        this.mConstraintSetId = -1;
        this.mDesignIds = new HashMap<>();
        init(attrs);
    }

    public ConstraintLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mChildrenByIds = new SparseArray<>();
        this.mConstraintHelpers = new ArrayList<>(4);
        this.mVariableDimensionsWidgets = new ArrayList<>(100);
        this.mLayoutWidget = new ConstraintWidgetContainer();
        this.mMinWidth = 0;
        this.mMinHeight = 0;
        this.mMaxWidth = Preference.DEFAULT_ORDER;
        this.mMaxHeight = Preference.DEFAULT_ORDER;
        this.mDirtyHierarchy = true;
        this.mOptimizationLevel = 2;
        this.mConstraintSet = null;
        this.mConstraintSetId = -1;
        this.mDesignIds = new HashMap<>();
        init(attrs);
    }

    @Override // android.view.View
    public void setId(int id) {
        this.mChildrenByIds.remove(getId());
        super.setId(id);
        this.mChildrenByIds.put(getId(), this);
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getTitle() {
        return this.mTitle;
    }

    private void init(AttributeSet attrs) {
        this.mLayoutWidget.setCompanionWidget(this);
        this.mChildrenByIds.put(getId(), this);
        this.mConstraintSet = null;
        if (attrs != null) {
            TypedArray a2 = getContext().obtainStyledAttributes(attrs, R.styleable.ConstraintLayout_Layout);
            int N = a2.getIndexCount();
            for (int i = 0; i < N; i++) {
                int attr = a2.getIndex(i);
                if (attr == R.styleable.ConstraintLayout_Layout_android_minWidth) {
                    this.mMinWidth = a2.getDimensionPixelOffset(attr, this.mMinWidth);
                } else if (attr == R.styleable.ConstraintLayout_Layout_android_minHeight) {
                    this.mMinHeight = a2.getDimensionPixelOffset(attr, this.mMinHeight);
                } else if (attr == R.styleable.ConstraintLayout_Layout_android_maxWidth) {
                    this.mMaxWidth = a2.getDimensionPixelOffset(attr, this.mMaxWidth);
                } else if (attr == R.styleable.ConstraintLayout_Layout_android_maxHeight) {
                    this.mMaxHeight = a2.getDimensionPixelOffset(attr, this.mMaxHeight);
                } else if (attr == R.styleable.ConstraintLayout_Layout_layout_optimizationLevel) {
                    this.mOptimizationLevel = a2.getInt(attr, this.mOptimizationLevel);
                } else if (attr == R.styleable.ConstraintLayout_Layout_title) {
                    this.mTitle = a2.getString(attr);
                } else if (attr == R.styleable.ConstraintLayout_Layout_constraintSet) {
                    int id = a2.getResourceId(attr, 0);
                    try {
                        this.mConstraintSet = new ConstraintSet();
                        this.mConstraintSet.load(getContext(), id);
                    } catch (Resources.NotFoundException e) {
                        this.mConstraintSet = null;
                    }
                    this.mConstraintSetId = id;
                }
            }
            a2.recycle();
        }
        this.mLayoutWidget.setOptimizationLevel(this.mOptimizationLevel);
    }

    @Override // android.view.ViewGroup
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        if (Build.VERSION.SDK_INT < 14) {
            onViewAdded(child);
        }
    }

    @Override // android.view.ViewGroup, android.view.ViewManager
    public void removeView(View view) {
        super.removeView(view);
        if (Build.VERSION.SDK_INT < 14) {
            onViewRemoved(view);
        }
    }

    @Override // android.view.ViewGroup
    public void onViewAdded(View view) {
        if (Build.VERSION.SDK_INT >= 14) {
            super.onViewAdded(view);
        }
        ConstraintWidget widget = getViewWidget(view);
        if ((view instanceof Guideline) && !(widget instanceof android.support.constraint.solver.widgets.Guideline)) {
            LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
            layoutParams.widget = new android.support.constraint.solver.widgets.Guideline();
            layoutParams.isGuideline = true;
            ((android.support.constraint.solver.widgets.Guideline) layoutParams.widget).setOrientation(layoutParams.orientation);
        }
        if (view instanceof ConstraintHelper) {
            ConstraintHelper helper = (ConstraintHelper) view;
            helper.validateParams();
            ((LayoutParams) view.getLayoutParams()).isHelper = true;
            if (!this.mConstraintHelpers.contains(helper)) {
                this.mConstraintHelpers.add(helper);
            }
        }
        this.mChildrenByIds.put(view.getId(), view);
        this.mDirtyHierarchy = true;
    }

    @Override // android.view.ViewGroup
    public void onViewRemoved(View view) {
        if (Build.VERSION.SDK_INT >= 14) {
            super.onViewRemoved(view);
        }
        this.mChildrenByIds.remove(view.getId());
        this.mLayoutWidget.remove(getViewWidget(view));
        this.mConstraintHelpers.remove(view);
        this.mDirtyHierarchy = true;
    }

    public void setMinWidth(int value) {
        if (value == this.mMinWidth) {
            return;
        }
        this.mMinWidth = value;
        requestLayout();
    }

    public void setMinHeight(int value) {
        if (value == this.mMinHeight) {
            return;
        }
        this.mMinHeight = value;
        requestLayout();
    }

    public int getMinWidth() {
        return this.mMinWidth;
    }

    public int getMinHeight() {
        return this.mMinHeight;
    }

    public void setMaxWidth(int value) {
        if (value == this.mMaxWidth) {
            return;
        }
        this.mMaxWidth = value;
        requestLayout();
    }

    public void setMaxHeight(int value) {
        if (value == this.mMaxHeight) {
            return;
        }
        this.mMaxHeight = value;
        requestLayout();
    }

    public int getMaxWidth() {
        return this.mMaxWidth;
    }

    public int getMaxHeight() {
        return this.mMaxHeight;
    }

    private void updateHierarchy() {
        int count = getChildCount();
        boolean recompute = false;
        int i = 0;
        while (true) {
            if (i >= count) {
                break;
            }
            View child = getChildAt(i);
            if (!child.isLayoutRequested()) {
                i++;
            } else {
                recompute = true;
                break;
            }
        }
        if (recompute) {
            this.mVariableDimensionsWidgets.clear();
            setChildrenConstraints();
        }
    }

    private void setChildrenConstraints() {
        int helperCount;
        int i;
        int resolvedLeftToRight;
        int resolveGoneLeftMargin;
        int resolveGoneRightMargin;
        int resolvedRightToLeft;
        int resolvedRightToRight;
        float resolvedHorizontalBias;
        int resolvedLeftToLeft;
        int count;
        float resolvedHorizontalBias2;
        int count2;
        int resolvedLeftToLeft2;
        ConstraintWidget target;
        ConstraintWidget target2;
        ConstraintWidget target3;
        ConstraintWidget target4;
        boolean z;
        int i2;
        boolean isInEditMode = isInEditMode();
        int count3 = getChildCount();
        boolean z2 = false;
        if (isInEditMode) {
            int i3 = 0;
            while (true) {
                int i4 = i3;
                if (i4 >= count3) {
                    break;
                }
                View view = getChildAt(i4);
                try {
                    String IdAsString = getResources().getResourceName(view.getId());
                    setDesignInformation(0, IdAsString, Integer.valueOf(view.getId()));
                } catch (Resources.NotFoundException e) {
                }
                i3 = i4 + 1;
            }
        }
        int i5 = this.mConstraintSetId;
        int i6 = -1;
        if (i5 != -1) {
            for (int i7 = 0; i7 < count3; i7++) {
                View child = getChildAt(i7);
                if (child.getId() == this.mConstraintSetId && (child instanceof Constraints)) {
                    this.mConstraintSet = ((Constraints) child).getConstraintSet();
                }
            }
        }
        if (this.mConstraintSet != null) {
            this.mConstraintSet.applyToInternal(this);
        }
        this.mLayoutWidget.removeAllChildren();
        int helperCount2 = this.mConstraintHelpers.size();
        if (helperCount2 > 0) {
            for (int i8 = 0; i8 < helperCount2; i8++) {
                ConstraintHelper helper = this.mConstraintHelpers.get(i8);
                helper.updatePreLayout(this);
            }
        }
        for (int i9 = 0; i9 < count3; i9++) {
            View child2 = getChildAt(i9);
            if (child2 instanceof Placeholder) {
                ((Placeholder) child2).updatePreLayout(this);
            }
        }
        int i10 = 0;
        while (i10 < count3) {
            View child3 = getChildAt(i10);
            ConstraintWidget widget = getViewWidget(child3);
            if (widget != null) {
                LayoutParams layoutParams = (LayoutParams) child3.getLayoutParams();
                layoutParams.validate();
                if (layoutParams.helped) {
                    layoutParams.helped = z2;
                } else {
                    widget.reset();
                }
                widget.setVisibility(child3.getVisibility());
                if (layoutParams.isInPlaceholder) {
                    widget.setVisibility(8);
                }
                widget.setCompanionWidget(child3);
                this.mLayoutWidget.add(widget);
                if (!layoutParams.verticalDimensionFixed || !layoutParams.horizontalDimensionFixed) {
                    this.mVariableDimensionsWidgets.add(widget);
                }
                if (layoutParams.isGuideline) {
                    android.support.constraint.solver.widgets.Guideline guideline = (android.support.constraint.solver.widgets.Guideline) widget;
                    if (layoutParams.guideBegin != i6) {
                        guideline.setGuideBegin(layoutParams.guideBegin);
                    }
                    if (layoutParams.guideEnd != i6) {
                        guideline.setGuideEnd(layoutParams.guideEnd);
                    }
                    if (layoutParams.guidePercent != -1.0f) {
                        guideline.setGuidePercent(layoutParams.guidePercent);
                    }
                } else if (layoutParams.resolvedLeftToLeft != i6 || layoutParams.resolvedLeftToRight != i6 || layoutParams.resolvedRightToLeft != i6 || layoutParams.resolvedRightToRight != i6 || layoutParams.topToTop != i6 || layoutParams.topToBottom != i6 || layoutParams.bottomToTop != i6 || layoutParams.bottomToBottom != i6 || layoutParams.baselineToBaseline != i6 || layoutParams.editorAbsoluteX != i6 || layoutParams.editorAbsoluteY != i6 || layoutParams.width == i6 || layoutParams.height == i6) {
                    int resolvedLeftToLeft3 = layoutParams.resolvedLeftToLeft;
                    int resolvedLeftToRight2 = layoutParams.resolvedLeftToRight;
                    int resolvedRightToLeft2 = layoutParams.resolvedRightToLeft;
                    int resolvedRightToRight2 = layoutParams.resolvedRightToRight;
                    int resolveGoneLeftMargin2 = layoutParams.resolveGoneLeftMargin;
                    int resolveGoneRightMargin2 = layoutParams.resolveGoneRightMargin;
                    float resolvedHorizontalBias3 = layoutParams.resolvedHorizontalBias;
                    helperCount = helperCount2;
                    if (Build.VERSION.SDK_INT < 17) {
                        int resolvedLeftToLeft4 = layoutParams.leftToLeft;
                        resolvedLeftToRight = layoutParams.leftToRight;
                        int resolvedRightToLeft3 = layoutParams.rightToLeft;
                        int resolvedRightToRight3 = layoutParams.rightToRight;
                        int resolveGoneLeftMargin3 = layoutParams.goneLeftMargin;
                        int i11 = layoutParams.goneRightMargin;
                        float resolvedHorizontalBias4 = layoutParams.horizontalBias;
                        if (resolvedLeftToLeft4 == -1 && resolvedLeftToRight == -1) {
                            if (layoutParams.startToStart == -1) {
                                if (layoutParams.startToEnd != -1) {
                                    resolvedLeftToRight = layoutParams.startToEnd;
                                }
                            } else {
                                resolvedLeftToLeft4 = layoutParams.startToStart;
                            }
                        }
                        if (resolvedRightToLeft3 == -1 && resolvedRightToRight3 == -1) {
                            if (layoutParams.endToStart == -1) {
                                if (layoutParams.endToEnd != -1) {
                                    resolvedRightToRight3 = layoutParams.endToEnd;
                                }
                            } else {
                                resolvedRightToLeft3 = layoutParams.endToStart;
                            }
                        }
                        resolveGoneLeftMargin = resolveGoneLeftMargin3;
                        resolveGoneRightMargin = i11;
                        resolvedRightToLeft = resolvedRightToLeft3;
                        resolvedLeftToLeft = resolvedLeftToLeft4;
                        i = -1;
                        resolvedRightToRight = resolvedRightToRight3;
                        resolvedHorizontalBias = resolvedHorizontalBias4;
                    } else {
                        i = -1;
                        resolvedLeftToRight = resolvedLeftToRight2;
                        resolveGoneLeftMargin = resolveGoneLeftMargin2;
                        resolveGoneRightMargin = resolveGoneRightMargin2;
                        resolvedRightToLeft = resolvedRightToLeft2;
                        resolvedRightToRight = resolvedRightToRight2;
                        resolvedHorizontalBias = resolvedHorizontalBias3;
                        resolvedLeftToLeft = resolvedLeftToLeft3;
                    }
                    if (resolvedLeftToLeft != i) {
                        ConstraintWidget target5 = getTargetWidget(resolvedLeftToLeft);
                        if (target5 != null) {
                            resolvedHorizontalBias2 = resolvedHorizontalBias;
                            count = count3;
                            count2 = resolvedRightToRight;
                            resolvedLeftToLeft2 = resolvedRightToLeft;
                            int resolvedRightToLeft4 = resolveGoneLeftMargin;
                            widget.immediateConnect(ConstraintAnchor.Type.LEFT, target5, ConstraintAnchor.Type.LEFT, layoutParams.leftMargin, resolvedRightToLeft4);
                        } else {
                            count = count3;
                            resolvedHorizontalBias2 = resolvedHorizontalBias;
                            count2 = resolvedRightToRight;
                            resolvedLeftToLeft2 = resolvedRightToLeft;
                        }
                    } else {
                        count = count3;
                        resolvedHorizontalBias2 = resolvedHorizontalBias;
                        count2 = resolvedRightToRight;
                        resolvedLeftToLeft2 = resolvedRightToLeft;
                        if (resolvedLeftToRight != -1 && (target = getTargetWidget(resolvedLeftToRight)) != null) {
                            widget.immediateConnect(ConstraintAnchor.Type.LEFT, target, ConstraintAnchor.Type.RIGHT, layoutParams.leftMargin, resolveGoneLeftMargin);
                        }
                    }
                    if (resolvedLeftToLeft2 != -1) {
                        ConstraintWidget target6 = getTargetWidget(resolvedLeftToLeft2);
                        if (target6 != null) {
                            widget.immediateConnect(ConstraintAnchor.Type.RIGHT, target6, ConstraintAnchor.Type.LEFT, layoutParams.rightMargin, resolveGoneRightMargin);
                        }
                    } else if (count2 != -1 && (target2 = getTargetWidget(count2)) != null) {
                        widget.immediateConnect(ConstraintAnchor.Type.RIGHT, target2, ConstraintAnchor.Type.RIGHT, layoutParams.rightMargin, resolveGoneRightMargin);
                    }
                    if (layoutParams.topToTop != -1) {
                        ConstraintWidget target7 = getTargetWidget(layoutParams.topToTop);
                        if (target7 != null) {
                            widget.immediateConnect(ConstraintAnchor.Type.TOP, target7, ConstraintAnchor.Type.TOP, layoutParams.topMargin, layoutParams.goneTopMargin);
                        }
                    } else if (layoutParams.topToBottom != -1 && (target3 = getTargetWidget(layoutParams.topToBottom)) != null) {
                        widget.immediateConnect(ConstraintAnchor.Type.TOP, target3, ConstraintAnchor.Type.BOTTOM, layoutParams.topMargin, layoutParams.goneTopMargin);
                    }
                    if (layoutParams.bottomToTop != -1) {
                        ConstraintWidget target8 = getTargetWidget(layoutParams.bottomToTop);
                        if (target8 != null) {
                            widget.immediateConnect(ConstraintAnchor.Type.BOTTOM, target8, ConstraintAnchor.Type.TOP, layoutParams.bottomMargin, layoutParams.goneBottomMargin);
                        }
                    } else if (layoutParams.bottomToBottom != -1 && (target4 = getTargetWidget(layoutParams.bottomToBottom)) != null) {
                        widget.immediateConnect(ConstraintAnchor.Type.BOTTOM, target4, ConstraintAnchor.Type.BOTTOM, layoutParams.bottomMargin, layoutParams.goneBottomMargin);
                    }
                    if (layoutParams.baselineToBaseline != -1) {
                        View view2 = this.mChildrenByIds.get(layoutParams.baselineToBaseline);
                        ConstraintWidget target9 = getTargetWidget(layoutParams.baselineToBaseline);
                        if (target9 != null && view2 != null && (view2.getLayoutParams() instanceof LayoutParams)) {
                            LayoutParams targetParams = (LayoutParams) view2.getLayoutParams();
                            layoutParams.needsBaseline = true;
                            targetParams.needsBaseline = true;
                            ConstraintAnchor baseline = widget.getAnchor(ConstraintAnchor.Type.BASELINE);
                            ConstraintAnchor targetBaseline = target9.getAnchor(ConstraintAnchor.Type.BASELINE);
                            baseline.connect(targetBaseline, 0, -1, ConstraintAnchor.Strength.STRONG, 0, true);
                            widget.getAnchor(ConstraintAnchor.Type.TOP).reset();
                            widget.getAnchor(ConstraintAnchor.Type.BOTTOM).reset();
                        }
                    }
                    if (resolvedHorizontalBias2 >= 0.0f && resolvedHorizontalBias2 != 0.5f) {
                        widget.setHorizontalBiasPercent(resolvedHorizontalBias2);
                    }
                    if (layoutParams.verticalBias >= 0.0f && layoutParams.verticalBias != 0.5f) {
                        widget.setVerticalBiasPercent(layoutParams.verticalBias);
                    }
                    if (isInEditMode && (layoutParams.editorAbsoluteX != -1 || layoutParams.editorAbsoluteY != -1)) {
                        widget.setOrigin(layoutParams.editorAbsoluteX, layoutParams.editorAbsoluteY);
                    }
                    if (!layoutParams.horizontalDimensionFixed) {
                        if (layoutParams.width == -1) {
                            widget.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_PARENT);
                            widget.getAnchor(ConstraintAnchor.Type.LEFT).mMargin = layoutParams.leftMargin;
                            widget.getAnchor(ConstraintAnchor.Type.RIGHT).mMargin = layoutParams.rightMargin;
                        } else {
                            widget.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
                            widget.setWidth(0);
                        }
                    } else {
                        widget.setHorizontalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
                        widget.setWidth(layoutParams.width);
                    }
                    if (!layoutParams.verticalDimensionFixed) {
                        i2 = -1;
                        if (layoutParams.height == -1) {
                            widget.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_PARENT);
                            widget.getAnchor(ConstraintAnchor.Type.TOP).mMargin = layoutParams.topMargin;
                            widget.getAnchor(ConstraintAnchor.Type.BOTTOM).mMargin = layoutParams.bottomMargin;
                            z = false;
                        } else {
                            widget.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.MATCH_CONSTRAINT);
                            z = false;
                            widget.setHeight(0);
                        }
                    } else {
                        z = false;
                        i2 = -1;
                        widget.setVerticalDimensionBehaviour(ConstraintWidget.DimensionBehaviour.FIXED);
                        widget.setHeight(layoutParams.height);
                    }
                    if (layoutParams.dimensionRatio != null) {
                        widget.setDimensionRatio(layoutParams.dimensionRatio);
                    }
                    widget.setHorizontalWeight(layoutParams.horizontalWeight);
                    widget.setVerticalWeight(layoutParams.verticalWeight);
                    widget.setHorizontalChainStyle(layoutParams.horizontalChainStyle);
                    widget.setVerticalChainStyle(layoutParams.verticalChainStyle);
                    widget.setHorizontalMatchStyle(layoutParams.matchConstraintDefaultWidth, layoutParams.matchConstraintMinWidth, layoutParams.matchConstraintMaxWidth, layoutParams.matchConstraintPercentWidth);
                    widget.setVerticalMatchStyle(layoutParams.matchConstraintDefaultHeight, layoutParams.matchConstraintMinHeight, layoutParams.matchConstraintMaxHeight, layoutParams.matchConstraintPercentHeight);
                    i10++;
                    z2 = z;
                    i6 = i2;
                    helperCount2 = helperCount;
                    count3 = count;
                }
            }
            helperCount = helperCount2;
            count = count3;
            z = z2;
            i2 = i6;
            i10++;
            z2 = z;
            i6 = i2;
            helperCount2 = helperCount;
            count3 = count;
        }
    }

    private final ConstraintWidget getTargetWidget(int id) {
        if (id == 0) {
            return this.mLayoutWidget;
        }
        View view = this.mChildrenByIds.get(id);
        if (view == this) {
            return this.mLayoutWidget;
        }
        if (view == null) {
            return null;
        }
        return ((LayoutParams) view.getLayoutParams()).widget;
    }

    public final ConstraintWidget getViewWidget(View view) {
        if (view == this) {
            return this.mLayoutWidget;
        }
        if (view == null) {
            return null;
        }
        return ((LayoutParams) view.getLayoutParams()).widget;
    }

    private void internalMeasureChildren(int parentWidthSpec, int parentHeightSpec) {
        int baseline;
        int childWidthMeasureSpec;
        int childHeightMeasureSpec;
        int heightPadding = getPaddingTop() + getPaddingBottom();
        int widthPadding = getPaddingLeft() + getPaddingRight();
        int widgetsCount = getChildCount();
        for (int i = 0; i < widgetsCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != 8) {
                LayoutParams params = (LayoutParams) child.getLayoutParams();
                ConstraintWidget widget = params.widget;
                if (!params.isGuideline && !params.isHelper) {
                    int width = params.width;
                    int height = params.height;
                    boolean doMeasure = params.horizontalDimensionFixed || params.verticalDimensionFixed || (!params.horizontalDimensionFixed && params.matchConstraintDefaultWidth == 1) || params.width == -1 || (!params.verticalDimensionFixed && (params.matchConstraintDefaultHeight == 1 || params.height == -1));
                    boolean didWrapMeasureWidth = false;
                    boolean didWrapMeasureHeight = false;
                    if (doMeasure) {
                        if (width == 0 || width == -1) {
                            childWidthMeasureSpec = getChildMeasureSpec(parentWidthSpec, widthPadding, -2);
                            didWrapMeasureWidth = true;
                        } else {
                            childWidthMeasureSpec = getChildMeasureSpec(parentWidthSpec, widthPadding, width);
                        }
                        int childWidthMeasureSpec2 = childWidthMeasureSpec;
                        if (height == 0 || height == -1) {
                            childHeightMeasureSpec = getChildMeasureSpec(parentHeightSpec, heightPadding, -2);
                            didWrapMeasureHeight = true;
                        } else {
                            childHeightMeasureSpec = getChildMeasureSpec(parentHeightSpec, heightPadding, height);
                        }
                        child.measure(childWidthMeasureSpec2, childHeightMeasureSpec);
                        widget.setWidthWrapContent(width == -2);
                        widget.setHeightWrapContent(height == -2);
                        width = child.getMeasuredWidth();
                        height = child.getMeasuredHeight();
                    }
                    widget.setWidth(width);
                    widget.setHeight(height);
                    if (didWrapMeasureWidth) {
                        widget.setWrapWidth(width);
                    }
                    if (didWrapMeasureHeight) {
                        widget.setWrapHeight(height);
                    }
                    if (params.needsBaseline && (baseline = child.getBaseline()) != -1) {
                        widget.setBaselineDistance(baseline);
                    }
                }
            }
        }
        for (int i2 = 0; i2 < widgetsCount; i2++) {
            View child2 = getChildAt(i2);
            if (child2 instanceof Placeholder) {
                ((Placeholder) child2).updatePostMeasure(this);
            }
        }
        int helperCount = this.mConstraintHelpers.size();
        if (helperCount > 0) {
            int i3 = 0;
            while (true) {
                int i4 = i3;
                if (i4 < helperCount) {
                    ConstraintHelper helper = this.mConstraintHelpers.get(i4);
                    helper.updatePostMeasure(this);
                    i3 = i4 + 1;
                } else {
                    return;
                }
            }
        }
    }

    @Override // android.view.View
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childState;
        int startingHeight;
        int startingWidth;
        int startingWidth2;
        int startingHeight2;
        int sizeDependentWidgetsCount;
        int startingHeight3;
        int startingWidth3;
        int widthSpec;
        int heightSpec;
        int baseline;
        ConstraintLayout constraintLayout = this;
        int i = widthMeasureSpec;
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        constraintLayout.mLayoutWidget.setX(paddingLeft);
        constraintLayout.mLayoutWidget.setY(paddingTop);
        setSelfDimensionBehaviour(widthMeasureSpec, heightMeasureSpec);
        int startingWidth4 = constraintLayout.mLayoutWidget.getWidth();
        int startingHeight4 = constraintLayout.mLayoutWidget.getHeight();
        if (constraintLayout.mDirtyHierarchy) {
            constraintLayout.mDirtyHierarchy = false;
            updateHierarchy();
        }
        internalMeasureChildren(widthMeasureSpec, heightMeasureSpec);
        if (getChildCount() > 0) {
            solveLinearSystem();
        }
        int sizeDependentWidgetsCount2 = constraintLayout.mVariableDimensionsWidgets.size();
        int heightPadding = getPaddingBottom() + paddingTop;
        int widthPadding = getPaddingRight() + paddingLeft;
        if (sizeDependentWidgetsCount2 <= 0) {
            childState = 0;
        } else {
            boolean needSolverPass = false;
            boolean containerWrapWidth = constraintLayout.mLayoutWidget.getHorizontalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
            boolean containerWrapHeight = constraintLayout.mLayoutWidget.getVerticalDimensionBehaviour() == ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
            int minWidth = Math.max(constraintLayout.mLayoutWidget.getWidth(), constraintLayout.mMinWidth);
            int minHeight = Math.max(constraintLayout.mLayoutWidget.getHeight(), constraintLayout.mMinHeight);
            childState = 0;
            int childState2 = minHeight;
            int minHeight2 = 0;
            while (true) {
                int paddingTop2 = paddingTop;
                if (minHeight2 >= sizeDependentWidgetsCount2) {
                    break;
                }
                ConstraintWidget widget = constraintLayout.mVariableDimensionsWidgets.get(minHeight2);
                int sizeDependentWidgetsCount3 = sizeDependentWidgetsCount2;
                View child = (View) widget.getCompanionWidget();
                if (child == null) {
                    startingWidth3 = startingWidth4;
                    startingHeight3 = startingHeight4;
                } else {
                    startingHeight3 = startingHeight4;
                    LayoutParams params = (LayoutParams) child.getLayoutParams();
                    startingWidth3 = startingWidth4;
                    if (!params.isHelper && !params.isGuideline && child.getVisibility() != 8) {
                        int widthSpec2 = params.width;
                        if (widthSpec2 == -2) {
                            widthSpec = getChildMeasureSpec(i, widthPadding, params.width);
                        } else {
                            int widthSpec3 = widget.getWidth();
                            widthSpec = View.MeasureSpec.makeMeasureSpec(widthSpec3, 1073741824);
                        }
                        if (params.height == -2) {
                            heightSpec = getChildMeasureSpec(heightMeasureSpec, heightPadding, params.height);
                        } else {
                            int heightSpec2 = widget.getHeight();
                            heightSpec = View.MeasureSpec.makeMeasureSpec(heightSpec2, 1073741824);
                        }
                        child.measure(widthSpec, heightSpec);
                        int measuredWidth = child.getMeasuredWidth();
                        int widthSpec4 = child.getMeasuredHeight();
                        int heightSpec3 = widget.getWidth();
                        if (measuredWidth != heightSpec3) {
                            widget.setWidth(measuredWidth);
                            if (containerWrapWidth && widget.getRight() > minWidth) {
                                int w = widget.getRight() + widget.getAnchor(ConstraintAnchor.Type.RIGHT).getMargin();
                                minWidth = Math.max(minWidth, w);
                            }
                            needSolverPass = true;
                        }
                        if (widthSpec4 != widget.getHeight()) {
                            widget.setHeight(widthSpec4);
                            if (containerWrapHeight && widget.getBottom() > childState2) {
                                int h = widget.getBottom() + widget.getAnchor(ConstraintAnchor.Type.BOTTOM).getMargin();
                                childState2 = Math.max(childState2, h);
                            }
                            needSolverPass = true;
                        }
                        if (params.needsBaseline && (baseline = child.getBaseline()) != -1 && baseline != widget.getBaselineDistance()) {
                            widget.setBaselineDistance(baseline);
                            needSolverPass = true;
                        }
                        if (Build.VERSION.SDK_INT >= 11) {
                            childState = combineMeasuredStates(childState, child.getMeasuredState());
                        }
                    }
                }
                minHeight2++;
                paddingTop = paddingTop2;
                sizeDependentWidgetsCount2 = sizeDependentWidgetsCount3;
                startingHeight4 = startingHeight3;
                startingWidth4 = startingWidth3;
                constraintLayout = this;
                i = widthMeasureSpec;
            }
            int startingWidth5 = startingWidth4;
            int startingHeight5 = startingHeight4;
            int sizeDependentWidgetsCount4 = sizeDependentWidgetsCount2;
            if (needSolverPass) {
                constraintLayout = this;
                startingWidth = startingWidth5;
                constraintLayout.mLayoutWidget.setWidth(startingWidth);
                startingHeight = startingHeight5;
                constraintLayout.mLayoutWidget.setHeight(startingHeight);
                solveLinearSystem();
                boolean needSolverPass2 = false;
                if (constraintLayout.mLayoutWidget.getWidth() < minWidth) {
                    constraintLayout.mLayoutWidget.setWidth(minWidth);
                    needSolverPass2 = true;
                }
                if (constraintLayout.mLayoutWidget.getHeight() < childState2) {
                    constraintLayout.mLayoutWidget.setHeight(childState2);
                    needSolverPass2 = true;
                }
                if (needSolverPass2) {
                    solveLinearSystem();
                }
            } else {
                startingHeight = startingHeight5;
                startingWidth = startingWidth5;
                constraintLayout = this;
            }
            int i2 = 0;
            while (true) {
                int i3 = i2;
                int sizeDependentWidgetsCount5 = sizeDependentWidgetsCount4;
                if (i3 >= sizeDependentWidgetsCount5) {
                    break;
                }
                ConstraintWidget widget2 = constraintLayout.mVariableDimensionsWidgets.get(i3);
                View child2 = (View) widget2.getCompanionWidget();
                if (child2 == null) {
                    startingWidth2 = startingWidth;
                    startingHeight2 = startingHeight;
                } else {
                    startingWidth2 = startingWidth;
                    int startingWidth6 = child2.getWidth();
                    startingHeight2 = startingHeight;
                    int startingHeight6 = widget2.getWidth();
                    if (startingWidth6 != startingHeight6 || child2.getHeight() != widget2.getHeight()) {
                        int widthSpec5 = View.MeasureSpec.makeMeasureSpec(widget2.getWidth(), 1073741824);
                        sizeDependentWidgetsCount = sizeDependentWidgetsCount5;
                        int heightSpec4 = View.MeasureSpec.makeMeasureSpec(widget2.getHeight(), 1073741824);
                        child2.measure(widthSpec5, heightSpec4);
                        i2 = i3 + 1;
                        startingWidth = startingWidth2;
                        startingHeight = startingHeight2;
                        sizeDependentWidgetsCount4 = sizeDependentWidgetsCount;
                    }
                }
                sizeDependentWidgetsCount = sizeDependentWidgetsCount5;
                i2 = i3 + 1;
                startingWidth = startingWidth2;
                startingHeight = startingHeight2;
                sizeDependentWidgetsCount4 = sizeDependentWidgetsCount;
            }
        }
        int androidLayoutWidth = constraintLayout.mLayoutWidget.getWidth() + widthPadding;
        int androidLayoutHeight = constraintLayout.mLayoutWidget.getHeight() + heightPadding;
        if (Build.VERSION.SDK_INT >= 11) {
            int resolvedWidthSize = resolveSizeAndState(androidLayoutWidth, widthMeasureSpec, childState);
            int resolvedHeightSize = resolveSizeAndState(androidLayoutHeight, heightMeasureSpec, childState << 16);
            int resolvedWidthSize2 = Math.min(constraintLayout.mMaxWidth, resolvedWidthSize) & 16777215;
            int resolvedHeightSize2 = Math.min(constraintLayout.mMaxHeight, resolvedHeightSize) & 16777215;
            if (constraintLayout.mLayoutWidget.isWidthMeasuredTooSmall()) {
                resolvedWidthSize2 |= 16777216;
            }
            if (constraintLayout.mLayoutWidget.isHeightMeasuredTooSmall()) {
                resolvedHeightSize2 |= 16777216;
            }
            constraintLayout.setMeasuredDimension(resolvedWidthSize2, resolvedHeightSize2);
            return;
        }
        constraintLayout.setMeasuredDimension(androidLayoutWidth, androidLayoutHeight);
    }

    private void setSelfDimensionBehaviour(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = View.MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = View.MeasureSpec.getSize(heightMeasureSpec);
        int heightPadding = getPaddingTop() + getPaddingBottom();
        int widthPadding = getPaddingLeft() + getPaddingRight();
        ConstraintWidget.DimensionBehaviour widthBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
        ConstraintWidget.DimensionBehaviour heightBehaviour = ConstraintWidget.DimensionBehaviour.FIXED;
        int desiredWidth = 0;
        int desiredHeight = 0;
        getLayoutParams();
        if (widthMode == Integer.MIN_VALUE) {
            widthBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
            desiredWidth = widthSize;
        } else if (widthMode != 0) {
            if (widthMode == 1073741824) {
                desiredWidth = Math.min(this.mMaxWidth, widthSize) - widthPadding;
            }
        } else {
            widthBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
        }
        if (heightMode == Integer.MIN_VALUE) {
            heightBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
            desiredHeight = heightSize;
        } else if (heightMode != 0) {
            if (heightMode == 1073741824) {
                desiredHeight = Math.min(this.mMaxHeight, heightSize) - heightPadding;
            }
        } else {
            heightBehaviour = ConstraintWidget.DimensionBehaviour.WRAP_CONTENT;
        }
        this.mLayoutWidget.setMinWidth(0);
        this.mLayoutWidget.setMinHeight(0);
        this.mLayoutWidget.setHorizontalDimensionBehaviour(widthBehaviour);
        this.mLayoutWidget.setWidth(desiredWidth);
        this.mLayoutWidget.setVerticalDimensionBehaviour(heightBehaviour);
        this.mLayoutWidget.setHeight(desiredHeight);
        this.mLayoutWidget.setMinWidth((this.mMinWidth - getPaddingLeft()) - getPaddingRight());
        this.mLayoutWidget.setMinHeight((this.mMinHeight - getPaddingTop()) - getPaddingBottom());
    }

    protected void solveLinearSystem() {
        this.mLayoutWidget.layout();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int widgetsCount = getChildCount();
        boolean isInEditMode = isInEditMode();
        int helperCount = this.mConstraintHelpers.size();
        if (helperCount > 0) {
            for (int i = 0; i < helperCount; i++) {
                ConstraintHelper helper = this.mConstraintHelpers.get(i);
                helper.updatePostLayout(this);
            }
        }
        for (int i2 = 0; i2 < widgetsCount; i2++) {
            View child = getChildAt(i2);
            LayoutParams params = (LayoutParams) child.getLayoutParams();
            ConstraintWidget widget = params.widget;
            if ((child.getVisibility() != 8 || params.isGuideline || params.isHelper || isInEditMode) && !params.isInPlaceholder) {
                int l = widget.getDrawX();
                int t = widget.getDrawY();
                int r = widget.getWidth() + l;
                int b2 = widget.getHeight() + t;
                child.layout(l, t, r, b2);
                if (child instanceof Placeholder) {
                    Placeholder holder = (Placeholder) child;
                    View content = holder.getContent();
                    if (content != null) {
                        content.setVisibility(0);
                        content.layout(l, t, r, b2);
                    }
                }
            }
        }
    }

    public void setOptimizationLevel(int level) {
        this.mLayoutWidget.setOptimizationLevel(level);
    }

    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(-2, -2);
    }

    @Override // android.view.ViewGroup
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override // android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public void setConstraintSet(ConstraintSet set) {
        this.mConstraintSet = set;
    }

    public View getViewById(int id) {
        return this.mChildrenByIds.get(id);
    }

    /* loaded from: classes.dex */
    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public int baselineToBaseline;
        public int bottomToBottom;
        public int bottomToTop;
        public String dimensionRatio;
        int dimensionRatioSide;
        float dimensionRatioValue;
        public int editorAbsoluteX;
        public int editorAbsoluteY;
        public int endToEnd;
        public int endToStart;
        public int goneBottomMargin;
        public int goneEndMargin;
        public int goneLeftMargin;
        public int goneRightMargin;
        public int goneStartMargin;
        public int goneTopMargin;
        public int guideBegin;
        public int guideEnd;
        public float guidePercent;
        public boolean helped;
        public float horizontalBias;
        public int horizontalChainStyle;
        boolean horizontalDimensionFixed;
        public float horizontalWeight;
        boolean isGuideline;
        boolean isHelper;
        boolean isInPlaceholder;
        public int leftToLeft;
        public int leftToRight;
        public int matchConstraintDefaultHeight;
        public int matchConstraintDefaultWidth;
        public int matchConstraintMaxHeight;
        public int matchConstraintMaxWidth;
        public int matchConstraintMinHeight;
        public int matchConstraintMinWidth;
        public float matchConstraintPercentHeight;
        public float matchConstraintPercentWidth;
        boolean needsBaseline;
        public int orientation;
        int resolveGoneLeftMargin;
        int resolveGoneRightMargin;
        float resolvedHorizontalBias;
        int resolvedLeftToLeft;
        int resolvedLeftToRight;
        int resolvedRightToLeft;
        int resolvedRightToRight;
        public int rightToLeft;
        public int rightToRight;
        public int startToEnd;
        public int startToStart;
        public int topToBottom;
        public int topToTop;
        public float verticalBias;
        public int verticalChainStyle;
        boolean verticalDimensionFixed;
        public float verticalWeight;
        ConstraintWidget widget;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            int i;
            int commaIndex;
            int i2 = -1;
            this.guideBegin = -1;
            this.guideEnd = -1;
            this.guidePercent = -1.0f;
            this.leftToLeft = -1;
            this.leftToRight = -1;
            this.rightToLeft = -1;
            this.rightToRight = -1;
            this.topToTop = -1;
            this.topToBottom = -1;
            this.bottomToTop = -1;
            this.bottomToBottom = -1;
            this.baselineToBaseline = -1;
            this.startToEnd = -1;
            this.startToStart = -1;
            this.endToStart = -1;
            this.endToEnd = -1;
            this.goneLeftMargin = -1;
            this.goneTopMargin = -1;
            this.goneRightMargin = -1;
            this.goneBottomMargin = -1;
            this.goneStartMargin = -1;
            this.goneEndMargin = -1;
            this.horizontalBias = 0.5f;
            this.verticalBias = 0.5f;
            this.dimensionRatio = null;
            this.dimensionRatioValue = 0.0f;
            this.dimensionRatioSide = 1;
            this.horizontalWeight = 0.0f;
            this.verticalWeight = 0.0f;
            int i3 = 0;
            this.horizontalChainStyle = 0;
            this.verticalChainStyle = 0;
            this.matchConstraintDefaultWidth = 0;
            this.matchConstraintDefaultHeight = 0;
            this.matchConstraintMinWidth = 0;
            this.matchConstraintMinHeight = 0;
            this.matchConstraintMaxWidth = 0;
            this.matchConstraintMaxHeight = 0;
            this.matchConstraintPercentWidth = 1.0f;
            this.matchConstraintPercentHeight = 1.0f;
            this.editorAbsoluteX = -1;
            this.editorAbsoluteY = -1;
            this.orientation = -1;
            this.horizontalDimensionFixed = true;
            this.verticalDimensionFixed = true;
            this.needsBaseline = false;
            this.isGuideline = false;
            this.isHelper = false;
            this.isInPlaceholder = false;
            this.resolvedLeftToLeft = -1;
            this.resolvedLeftToRight = -1;
            this.resolvedRightToLeft = -1;
            this.resolvedRightToRight = -1;
            this.resolveGoneLeftMargin = -1;
            this.resolveGoneRightMargin = -1;
            this.resolvedHorizontalBias = 0.5f;
            this.widget = new ConstraintWidget();
            this.helped = false;
            TypedArray a2 = c.obtainStyledAttributes(attrs, R.styleable.ConstraintLayout_Layout);
            int N = a2.getIndexCount();
            int i4 = 0;
            while (true) {
                int i5 = i4;
                if (i5 >= N) {
                    a2.recycle();
                    validate();
                    return;
                }
                int attr = a2.getIndex(i5);
                if (attr == R.styleable.ConstraintLayout_Layout_layout_constraintLeft_toLeftOf) {
                    this.leftToLeft = a2.getResourceId(attr, this.leftToLeft);
                    if (this.leftToLeft == i2) {
                        this.leftToLeft = a2.getInt(attr, i2);
                    }
                } else if (attr == R.styleable.ConstraintLayout_Layout_layout_constraintLeft_toRightOf) {
                    this.leftToRight = a2.getResourceId(attr, this.leftToRight);
                    if (this.leftToRight == i2) {
                        this.leftToRight = a2.getInt(attr, i2);
                    }
                } else if (attr == R.styleable.ConstraintLayout_Layout_layout_constraintRight_toLeftOf) {
                    this.rightToLeft = a2.getResourceId(attr, this.rightToLeft);
                    if (this.rightToLeft == i2) {
                        this.rightToLeft = a2.getInt(attr, i2);
                    }
                } else if (attr == R.styleable.ConstraintLayout_Layout_layout_constraintRight_toRightOf) {
                    this.rightToRight = a2.getResourceId(attr, this.rightToRight);
                    if (this.rightToRight == i2) {
                        this.rightToRight = a2.getInt(attr, i2);
                    }
                } else if (attr == R.styleable.ConstraintLayout_Layout_layout_constraintTop_toTopOf) {
                    this.topToTop = a2.getResourceId(attr, this.topToTop);
                    if (this.topToTop == i2) {
                        this.topToTop = a2.getInt(attr, i2);
                    }
                } else if (attr == R.styleable.ConstraintLayout_Layout_layout_constraintTop_toBottomOf) {
                    this.topToBottom = a2.getResourceId(attr, this.topToBottom);
                    if (this.topToBottom == i2) {
                        this.topToBottom = a2.getInt(attr, i2);
                    }
                } else if (attr == R.styleable.ConstraintLayout_Layout_layout_constraintBottom_toTopOf) {
                    this.bottomToTop = a2.getResourceId(attr, this.bottomToTop);
                    if (this.bottomToTop == i2) {
                        this.bottomToTop = a2.getInt(attr, i2);
                    }
                } else if (attr == R.styleable.ConstraintLayout_Layout_layout_constraintBottom_toBottomOf) {
                    this.bottomToBottom = a2.getResourceId(attr, this.bottomToBottom);
                    if (this.bottomToBottom == i2) {
                        this.bottomToBottom = a2.getInt(attr, i2);
                    }
                } else if (attr == R.styleable.ConstraintLayout_Layout_layout_constraintBaseline_toBaselineOf) {
                    this.baselineToBaseline = a2.getResourceId(attr, this.baselineToBaseline);
                    if (this.baselineToBaseline == i2) {
                        this.baselineToBaseline = a2.getInt(attr, i2);
                    }
                } else if (attr == R.styleable.ConstraintLayout_Layout_layout_editor_absoluteX) {
                    this.editorAbsoluteX = a2.getDimensionPixelOffset(attr, this.editorAbsoluteX);
                } else if (attr == R.styleable.ConstraintLayout_Layout_layout_editor_absoluteY) {
                    this.editorAbsoluteY = a2.getDimensionPixelOffset(attr, this.editorAbsoluteY);
                } else if (attr == R.styleable.ConstraintLayout_Layout_layout_constraintGuide_begin) {
                    this.guideBegin = a2.getDimensionPixelOffset(attr, this.guideBegin);
                } else if (attr == R.styleable.ConstraintLayout_Layout_layout_constraintGuide_end) {
                    this.guideEnd = a2.getDimensionPixelOffset(attr, this.guideEnd);
                } else if (attr == R.styleable.ConstraintLayout_Layout_layout_constraintGuide_percent) {
                    this.guidePercent = a2.getFloat(attr, this.guidePercent);
                } else if (attr == R.styleable.ConstraintLayout_Layout_android_orientation) {
                    this.orientation = a2.getInt(attr, this.orientation);
                } else if (attr == R.styleable.ConstraintLayout_Layout_layout_constraintStart_toEndOf) {
                    this.startToEnd = a2.getResourceId(attr, this.startToEnd);
                    if (this.startToEnd == i2) {
                        this.startToEnd = a2.getInt(attr, i2);
                    }
                } else if (attr == R.styleable.ConstraintLayout_Layout_layout_constraintStart_toStartOf) {
                    this.startToStart = a2.getResourceId(attr, this.startToStart);
                    if (this.startToStart == i2) {
                        this.startToStart = a2.getInt(attr, i2);
                    }
                } else if (attr == R.styleable.ConstraintLayout_Layout_layout_constraintEnd_toStartOf) {
                    this.endToStart = a2.getResourceId(attr, this.endToStart);
                    if (this.endToStart == i2) {
                        this.endToStart = a2.getInt(attr, i2);
                    }
                } else if (attr == R.styleable.ConstraintLayout_Layout_layout_constraintEnd_toEndOf) {
                    this.endToEnd = a2.getResourceId(attr, this.endToEnd);
                    if (this.endToEnd == i2) {
                        this.endToEnd = a2.getInt(attr, i2);
                    }
                } else if (attr == R.styleable.ConstraintLayout_Layout_layout_goneMarginLeft) {
                    this.goneLeftMargin = a2.getDimensionPixelSize(attr, this.goneLeftMargin);
                } else if (attr == R.styleable.ConstraintLayout_Layout_layout_goneMarginTop) {
                    this.goneTopMargin = a2.getDimensionPixelSize(attr, this.goneTopMargin);
                } else if (attr == R.styleable.ConstraintLayout_Layout_layout_goneMarginRight) {
                    this.goneRightMargin = a2.getDimensionPixelSize(attr, this.goneRightMargin);
                } else if (attr == R.styleable.ConstraintLayout_Layout_layout_goneMarginBottom) {
                    this.goneBottomMargin = a2.getDimensionPixelSize(attr, this.goneBottomMargin);
                } else if (attr == R.styleable.ConstraintLayout_Layout_layout_goneMarginStart) {
                    this.goneStartMargin = a2.getDimensionPixelSize(attr, this.goneStartMargin);
                } else if (attr == R.styleable.ConstraintLayout_Layout_layout_goneMarginEnd) {
                    this.goneEndMargin = a2.getDimensionPixelSize(attr, this.goneEndMargin);
                } else if (attr == R.styleable.ConstraintLayout_Layout_layout_constraintHorizontal_bias) {
                    this.horizontalBias = a2.getFloat(attr, this.horizontalBias);
                } else if (attr == R.styleable.ConstraintLayout_Layout_layout_constraintVertical_bias) {
                    this.verticalBias = a2.getFloat(attr, this.verticalBias);
                } else {
                    if (attr == R.styleable.ConstraintLayout_Layout_layout_constraintDimensionRatio) {
                        this.dimensionRatio = a2.getString(attr);
                        this.dimensionRatioValue = Float.NaN;
                        this.dimensionRatioSide = i2;
                        if (this.dimensionRatio != null) {
                            int len = this.dimensionRatio.length();
                            int commaIndex2 = this.dimensionRatio.indexOf(44);
                            if (commaIndex2 <= 0 || commaIndex2 >= len - 1) {
                                commaIndex = 0;
                            } else {
                                String dimension = this.dimensionRatio.substring(i3, commaIndex2);
                                if (dimension.equalsIgnoreCase("W")) {
                                    this.dimensionRatioSide = i3;
                                } else if (dimension.equalsIgnoreCase("H")) {
                                    this.dimensionRatioSide = 1;
                                }
                                commaIndex = commaIndex2 + 1;
                            }
                            int commaIndex3 = commaIndex;
                            int colonIndex = this.dimensionRatio.indexOf(58);
                            if (colonIndex < 0 || colonIndex >= len - 1) {
                                String r = this.dimensionRatio.substring(commaIndex3);
                                if (r.length() > 0) {
                                    try {
                                        this.dimensionRatioValue = Float.parseFloat(r);
                                    } catch (NumberFormatException e) {
                                    }
                                }
                            } else {
                                String nominator = this.dimensionRatio.substring(commaIndex3, colonIndex);
                                String denominator = this.dimensionRatio.substring(colonIndex + 1);
                                if (nominator.length() > 0 && denominator.length() > 0) {
                                    try {
                                        float nominatorValue = Float.parseFloat(nominator);
                                        float denominatorValue = Float.parseFloat(denominator);
                                        if (nominatorValue > 0.0f && denominatorValue > 0.0f) {
                                            if (this.dimensionRatioSide == 1) {
                                                this.dimensionRatioValue = Math.abs(denominatorValue / nominatorValue);
                                            } else {
                                                this.dimensionRatioValue = Math.abs(nominatorValue / denominatorValue);
                                            }
                                        }
                                    } catch (NumberFormatException e2) {
                                    }
                                }
                            }
                        }
                    } else if (attr == R.styleable.ConstraintLayout_Layout_layout_constraintHorizontal_weight) {
                        this.horizontalWeight = a2.getFloat(attr, 0.0f);
                    } else if (attr == R.styleable.ConstraintLayout_Layout_layout_constraintVertical_weight) {
                        this.verticalWeight = a2.getFloat(attr, 0.0f);
                    } else {
                        if (attr == R.styleable.ConstraintLayout_Layout_layout_constraintHorizontal_chainStyle) {
                            i = 0;
                            this.horizontalChainStyle = a2.getInt(attr, 0);
                        } else {
                            i = 0;
                            if (attr == R.styleable.ConstraintLayout_Layout_layout_constraintVertical_chainStyle) {
                                this.verticalChainStyle = a2.getInt(attr, 0);
                            } else if (attr == R.styleable.ConstraintLayout_Layout_layout_constraintWidth_default) {
                                this.matchConstraintDefaultWidth = a2.getInt(attr, 0);
                            } else if (attr == R.styleable.ConstraintLayout_Layout_layout_constraintHeight_default) {
                                this.matchConstraintDefaultHeight = a2.getInt(attr, 0);
                            } else if (attr == R.styleable.ConstraintLayout_Layout_layout_constraintWidth_min) {
                                try {
                                    this.matchConstraintMinWidth = a2.getDimensionPixelSize(attr, this.matchConstraintMinWidth);
                                } catch (InflateException e3) {
                                    int value = a2.getInt(attr, this.matchConstraintMinWidth);
                                    if (value == -2) {
                                        this.matchConstraintMinWidth = -2;
                                    }
                                }
                            } else if (attr == R.styleable.ConstraintLayout_Layout_layout_constraintWidth_max) {
                                try {
                                    this.matchConstraintMaxWidth = a2.getDimensionPixelSize(attr, this.matchConstraintMaxWidth);
                                } catch (InflateException e4) {
                                    int value2 = a2.getInt(attr, this.matchConstraintMaxWidth);
                                    if (value2 == -2) {
                                        this.matchConstraintMaxWidth = -2;
                                    }
                                }
                            } else if (attr == R.styleable.ConstraintLayout_Layout_layout_constraintWidth_percent) {
                                this.matchConstraintPercentWidth = a2.getFloat(attr, this.matchConstraintPercentWidth);
                            } else if (attr == R.styleable.ConstraintLayout_Layout_layout_constraintHeight_min) {
                                try {
                                    this.matchConstraintMinHeight = a2.getDimensionPixelSize(attr, this.matchConstraintMinHeight);
                                } catch (InflateException e5) {
                                    int value3 = a2.getInt(attr, this.matchConstraintMinHeight);
                                    if (value3 == -2) {
                                        this.matchConstraintMinHeight = -2;
                                    }
                                }
                            } else if (attr == R.styleable.ConstraintLayout_Layout_layout_constraintHeight_max) {
                                try {
                                    this.matchConstraintMaxHeight = a2.getDimensionPixelSize(attr, this.matchConstraintMaxHeight);
                                } catch (InflateException e6) {
                                    int value4 = a2.getInt(attr, this.matchConstraintMaxHeight);
                                    if (value4 == -2) {
                                        this.matchConstraintMaxHeight = -2;
                                    }
                                }
                            } else if (attr == R.styleable.ConstraintLayout_Layout_layout_constraintHeight_percent) {
                                this.matchConstraintPercentHeight = a2.getFloat(attr, this.matchConstraintPercentHeight);
                            } else if (attr != R.styleable.ConstraintLayout_Layout_layout_constraintLeft_creator && attr != R.styleable.ConstraintLayout_Layout_layout_constraintTop_creator && attr != R.styleable.ConstraintLayout_Layout_layout_constraintRight_creator && attr != R.styleable.ConstraintLayout_Layout_layout_constraintBottom_creator) {
                                int i6 = R.styleable.ConstraintLayout_Layout_layout_constraintBaseline_creator;
                            }
                        }
                        i4 = i5 + 1;
                        i3 = i;
                        i2 = -1;
                    }
                    i = 0;
                    i4 = i5 + 1;
                    i3 = i;
                    i2 = -1;
                }
                i = i3;
                i4 = i5 + 1;
                i3 = i;
                i2 = -1;
            }
        }

        public void validate() {
            this.isGuideline = false;
            this.horizontalDimensionFixed = true;
            this.verticalDimensionFixed = true;
            if (this.width == 0 || this.width == -1) {
                this.horizontalDimensionFixed = false;
            }
            if (this.height == 0 || this.height == -1) {
                this.verticalDimensionFixed = false;
            }
            if (this.guidePercent != -1.0f || this.guideBegin != -1 || this.guideEnd != -1) {
                this.isGuideline = true;
                this.horizontalDimensionFixed = true;
                this.verticalDimensionFixed = true;
                if (!(this.widget instanceof android.support.constraint.solver.widgets.Guideline)) {
                    this.widget = new android.support.constraint.solver.widgets.Guideline();
                }
                ((android.support.constraint.solver.widgets.Guideline) this.widget).setOrientation(this.orientation);
            }
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            this.guideBegin = -1;
            this.guideEnd = -1;
            this.guidePercent = -1.0f;
            this.leftToLeft = -1;
            this.leftToRight = -1;
            this.rightToLeft = -1;
            this.rightToRight = -1;
            this.topToTop = -1;
            this.topToBottom = -1;
            this.bottomToTop = -1;
            this.bottomToBottom = -1;
            this.baselineToBaseline = -1;
            this.startToEnd = -1;
            this.startToStart = -1;
            this.endToStart = -1;
            this.endToEnd = -1;
            this.goneLeftMargin = -1;
            this.goneTopMargin = -1;
            this.goneRightMargin = -1;
            this.goneBottomMargin = -1;
            this.goneStartMargin = -1;
            this.goneEndMargin = -1;
            this.horizontalBias = 0.5f;
            this.verticalBias = 0.5f;
            this.dimensionRatio = null;
            this.dimensionRatioValue = 0.0f;
            this.dimensionRatioSide = 1;
            this.horizontalWeight = 0.0f;
            this.verticalWeight = 0.0f;
            this.horizontalChainStyle = 0;
            this.verticalChainStyle = 0;
            this.matchConstraintDefaultWidth = 0;
            this.matchConstraintDefaultHeight = 0;
            this.matchConstraintMinWidth = 0;
            this.matchConstraintMinHeight = 0;
            this.matchConstraintMaxWidth = 0;
            this.matchConstraintMaxHeight = 0;
            this.matchConstraintPercentWidth = 1.0f;
            this.matchConstraintPercentHeight = 1.0f;
            this.editorAbsoluteX = -1;
            this.editorAbsoluteY = -1;
            this.orientation = -1;
            this.horizontalDimensionFixed = true;
            this.verticalDimensionFixed = true;
            this.needsBaseline = false;
            this.isGuideline = false;
            this.isHelper = false;
            this.isInPlaceholder = false;
            this.resolvedLeftToLeft = -1;
            this.resolvedLeftToRight = -1;
            this.resolvedRightToLeft = -1;
            this.resolvedRightToRight = -1;
            this.resolveGoneLeftMargin = -1;
            this.resolveGoneRightMargin = -1;
            this.resolvedHorizontalBias = 0.5f;
            this.widget = new ConstraintWidget();
            this.helped = false;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
            this.guideBegin = -1;
            this.guideEnd = -1;
            this.guidePercent = -1.0f;
            this.leftToLeft = -1;
            this.leftToRight = -1;
            this.rightToLeft = -1;
            this.rightToRight = -1;
            this.topToTop = -1;
            this.topToBottom = -1;
            this.bottomToTop = -1;
            this.bottomToBottom = -1;
            this.baselineToBaseline = -1;
            this.startToEnd = -1;
            this.startToStart = -1;
            this.endToStart = -1;
            this.endToEnd = -1;
            this.goneLeftMargin = -1;
            this.goneTopMargin = -1;
            this.goneRightMargin = -1;
            this.goneBottomMargin = -1;
            this.goneStartMargin = -1;
            this.goneEndMargin = -1;
            this.horizontalBias = 0.5f;
            this.verticalBias = 0.5f;
            this.dimensionRatio = null;
            this.dimensionRatioValue = 0.0f;
            this.dimensionRatioSide = 1;
            this.horizontalWeight = 0.0f;
            this.verticalWeight = 0.0f;
            this.horizontalChainStyle = 0;
            this.verticalChainStyle = 0;
            this.matchConstraintDefaultWidth = 0;
            this.matchConstraintDefaultHeight = 0;
            this.matchConstraintMinWidth = 0;
            this.matchConstraintMinHeight = 0;
            this.matchConstraintMaxWidth = 0;
            this.matchConstraintMaxHeight = 0;
            this.matchConstraintPercentWidth = 1.0f;
            this.matchConstraintPercentHeight = 1.0f;
            this.editorAbsoluteX = -1;
            this.editorAbsoluteY = -1;
            this.orientation = -1;
            this.horizontalDimensionFixed = true;
            this.verticalDimensionFixed = true;
            this.needsBaseline = false;
            this.isGuideline = false;
            this.isHelper = false;
            this.isInPlaceholder = false;
            this.resolvedLeftToLeft = -1;
            this.resolvedLeftToRight = -1;
            this.resolvedRightToLeft = -1;
            this.resolvedRightToRight = -1;
            this.resolveGoneLeftMargin = -1;
            this.resolveGoneRightMargin = -1;
            this.resolvedHorizontalBias = 0.5f;
            this.widget = new ConstraintWidget();
            this.helped = false;
        }

        @Override // android.view.ViewGroup.MarginLayoutParams, android.view.ViewGroup.LayoutParams
        @TargetApi(17)
        public void resolveLayoutDirection(int layoutDirection) {
            super.resolveLayoutDirection(layoutDirection);
            this.resolvedRightToLeft = -1;
            this.resolvedRightToRight = -1;
            this.resolvedLeftToLeft = -1;
            this.resolvedLeftToRight = -1;
            this.resolveGoneLeftMargin = -1;
            this.resolveGoneRightMargin = -1;
            this.resolveGoneLeftMargin = this.goneLeftMargin;
            this.resolveGoneRightMargin = this.goneRightMargin;
            this.resolvedHorizontalBias = this.horizontalBias;
            boolean isRtl = 1 == getLayoutDirection();
            if (!isRtl) {
                if (this.startToEnd != -1) {
                    this.resolvedLeftToRight = this.startToEnd;
                }
                if (this.startToStart != -1) {
                    this.resolvedLeftToLeft = this.startToStart;
                }
                if (this.endToStart != -1) {
                    this.resolvedRightToLeft = this.endToStart;
                }
                if (this.endToEnd != -1) {
                    this.resolvedRightToRight = this.endToEnd;
                }
                if (this.goneStartMargin != -1) {
                    this.resolveGoneLeftMargin = this.goneStartMargin;
                }
                if (this.goneEndMargin != -1) {
                    this.resolveGoneRightMargin = this.goneEndMargin;
                }
            } else {
                boolean startEndDefined = false;
                if (this.startToEnd == -1) {
                    if (this.startToStart != -1) {
                        this.resolvedRightToRight = this.startToStart;
                        startEndDefined = true;
                    }
                } else {
                    this.resolvedRightToLeft = this.startToEnd;
                    startEndDefined = true;
                }
                if (this.endToStart != -1) {
                    this.resolvedLeftToRight = this.endToStart;
                    startEndDefined = true;
                }
                if (this.endToEnd != -1) {
                    this.resolvedLeftToLeft = this.endToEnd;
                    startEndDefined = true;
                }
                if (this.goneStartMargin != -1) {
                    this.resolveGoneRightMargin = this.goneStartMargin;
                }
                if (this.goneEndMargin != -1) {
                    this.resolveGoneLeftMargin = this.goneEndMargin;
                }
                if (startEndDefined) {
                    this.resolvedHorizontalBias = 1.0f - this.horizontalBias;
                }
            }
            if (this.endToStart == -1 && this.endToEnd == -1) {
                if (this.rightToLeft == -1) {
                    if (this.rightToRight != -1) {
                        this.resolvedRightToRight = this.rightToRight;
                    }
                } else {
                    this.resolvedRightToLeft = this.rightToLeft;
                }
            }
            if (this.startToStart == -1 && this.startToEnd == -1) {
                if (this.leftToLeft == -1) {
                    if (this.leftToRight != -1) {
                        this.resolvedLeftToRight = this.leftToRight;
                        return;
                    }
                    return;
                }
                this.resolvedLeftToLeft = this.leftToLeft;
            }
        }
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        super.requestLayout();
        this.mDirtyHierarchy = true;
    }
}
