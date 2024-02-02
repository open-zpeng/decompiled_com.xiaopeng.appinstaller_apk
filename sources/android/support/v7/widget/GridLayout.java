package android.support.v7.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewGroupCompat;
import android.support.v4.widget.Space;
import android.support.v7.gridlayout.R;
import android.util.AttributeSet;
import android.util.LogPrinter;
import android.util.Pair;
import android.util.Printer;
import android.view.View;
import android.view.ViewGroup;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public class GridLayout extends ViewGroup {
    int mAlignmentMode;
    int mDefaultGap;
    final Axis mHorizontalAxis;
    int mLastLayoutParamsHashCode;
    int mOrientation;
    Printer mPrinter;
    boolean mUseDefaultMargins;
    final Axis mVerticalAxis;
    static final Printer LOG_PRINTER = new LogPrinter(3, GridLayout.class.getName());
    static final Printer NO_PRINTER = new Printer() { // from class: android.support.v7.widget.GridLayout.1
        @Override // android.util.Printer
        public void println(String x) {
        }
    };
    private static final int ORIENTATION = R.styleable.GridLayout_orientation;
    private static final int ROW_COUNT = R.styleable.GridLayout_rowCount;
    private static final int COLUMN_COUNT = R.styleable.GridLayout_columnCount;
    private static final int USE_DEFAULT_MARGINS = R.styleable.GridLayout_useDefaultMargins;
    private static final int ALIGNMENT_MODE = R.styleable.GridLayout_alignmentMode;
    private static final int ROW_ORDER_PRESERVED = R.styleable.GridLayout_rowOrderPreserved;
    private static final int COLUMN_ORDER_PRESERVED = R.styleable.GridLayout_columnOrderPreserved;
    static final Alignment UNDEFINED_ALIGNMENT = new Alignment() { // from class: android.support.v7.widget.GridLayout.2
        @Override // android.support.v7.widget.GridLayout.Alignment
        int getGravityOffset(View view, int cellDelta) {
            return Integer.MIN_VALUE;
        }

        @Override // android.support.v7.widget.GridLayout.Alignment
        public int getAlignmentValue(View view, int viewSize, int mode) {
            return Integer.MIN_VALUE;
        }

        @Override // android.support.v7.widget.GridLayout.Alignment
        String getDebugString() {
            return "UNDEFINED";
        }
    };
    private static final Alignment LEADING = new Alignment() { // from class: android.support.v7.widget.GridLayout.3
        @Override // android.support.v7.widget.GridLayout.Alignment
        int getGravityOffset(View view, int cellDelta) {
            return 0;
        }

        @Override // android.support.v7.widget.GridLayout.Alignment
        public int getAlignmentValue(View view, int viewSize, int mode) {
            return 0;
        }

        @Override // android.support.v7.widget.GridLayout.Alignment
        String getDebugString() {
            return "LEADING";
        }
    };
    private static final Alignment TRAILING = new Alignment() { // from class: android.support.v7.widget.GridLayout.4
        @Override // android.support.v7.widget.GridLayout.Alignment
        int getGravityOffset(View view, int cellDelta) {
            return cellDelta;
        }

        @Override // android.support.v7.widget.GridLayout.Alignment
        public int getAlignmentValue(View view, int viewSize, int mode) {
            return viewSize;
        }

        @Override // android.support.v7.widget.GridLayout.Alignment
        String getDebugString() {
            return "TRAILING";
        }
    };
    public static final Alignment TOP = LEADING;
    public static final Alignment BOTTOM = TRAILING;
    public static final Alignment START = LEADING;
    public static final Alignment END = TRAILING;
    public static final Alignment LEFT = createSwitchingAlignment(START, END);
    public static final Alignment RIGHT = createSwitchingAlignment(END, START);
    public static final Alignment CENTER = new Alignment() { // from class: android.support.v7.widget.GridLayout.6
        @Override // android.support.v7.widget.GridLayout.Alignment
        int getGravityOffset(View view, int cellDelta) {
            return cellDelta >> 1;
        }

        @Override // android.support.v7.widget.GridLayout.Alignment
        public int getAlignmentValue(View view, int viewSize, int mode) {
            return viewSize >> 1;
        }

        @Override // android.support.v7.widget.GridLayout.Alignment
        String getDebugString() {
            return "CENTER";
        }
    };
    public static final Alignment BASELINE = new Alignment() { // from class: android.support.v7.widget.GridLayout.7
        @Override // android.support.v7.widget.GridLayout.Alignment
        int getGravityOffset(View view, int cellDelta) {
            return 0;
        }

        @Override // android.support.v7.widget.GridLayout.Alignment
        public int getAlignmentValue(View view, int viewSize, int mode) {
            if (view.getVisibility() == 8) {
                return 0;
            }
            int baseline = view.getBaseline();
            if (baseline == -1) {
                return Integer.MIN_VALUE;
            }
            return baseline;
        }

        @Override // android.support.v7.widget.GridLayout.Alignment
        public Bounds getBounds() {
            return new Bounds() { // from class: android.support.v7.widget.GridLayout.7.1
                private int size;

                @Override // android.support.v7.widget.GridLayout.Bounds
                protected void reset() {
                    super.reset();
                    this.size = Integer.MIN_VALUE;
                }

                @Override // android.support.v7.widget.GridLayout.Bounds
                protected void include(int before, int after) {
                    super.include(before, after);
                    this.size = Math.max(this.size, before + after);
                }

                @Override // android.support.v7.widget.GridLayout.Bounds
                protected int size(boolean min) {
                    return Math.max(super.size(min), this.size);
                }

                @Override // android.support.v7.widget.GridLayout.Bounds
                protected int getOffset(GridLayout gl, View c, Alignment a2, int size, boolean hrz) {
                    return Math.max(0, super.getOffset(gl, c, a2, size, hrz));
                }
            };
        }

        @Override // android.support.v7.widget.GridLayout.Alignment
        String getDebugString() {
            return "BASELINE";
        }
    };
    public static final Alignment FILL = new Alignment() { // from class: android.support.v7.widget.GridLayout.8
        @Override // android.support.v7.widget.GridLayout.Alignment
        int getGravityOffset(View view, int cellDelta) {
            return 0;
        }

        @Override // android.support.v7.widget.GridLayout.Alignment
        public int getAlignmentValue(View view, int viewSize, int mode) {
            return Integer.MIN_VALUE;
        }

        @Override // android.support.v7.widget.GridLayout.Alignment
        public int getSizeInCell(View view, int viewSize, int cellSize) {
            return cellSize;
        }

        @Override // android.support.v7.widget.GridLayout.Alignment
        String getDebugString() {
            return "FILL";
        }
    };

    public GridLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mHorizontalAxis = new Axis(true);
        this.mVerticalAxis = new Axis(false);
        this.mOrientation = 0;
        this.mUseDefaultMargins = false;
        this.mAlignmentMode = 1;
        this.mLastLayoutParamsHashCode = 0;
        this.mPrinter = LOG_PRINTER;
        this.mDefaultGap = context.getResources().getDimensionPixelOffset(R.dimen.default_gap);
        TypedArray a2 = context.obtainStyledAttributes(attrs, R.styleable.GridLayout);
        try {
            setRowCount(a2.getInt(ROW_COUNT, Integer.MIN_VALUE));
            setColumnCount(a2.getInt(COLUMN_COUNT, Integer.MIN_VALUE));
            setOrientation(a2.getInt(ORIENTATION, 0));
            setUseDefaultMargins(a2.getBoolean(USE_DEFAULT_MARGINS, false));
            setAlignmentMode(a2.getInt(ALIGNMENT_MODE, 1));
            setRowOrderPreserved(a2.getBoolean(ROW_ORDER_PRESERVED, true));
            setColumnOrderPreserved(a2.getBoolean(COLUMN_ORDER_PRESERVED, true));
        } finally {
            a2.recycle();
        }
    }

    public GridLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridLayout(Context context) {
        this(context, null);
    }

    public void setOrientation(int orientation) {
        if (this.mOrientation != orientation) {
            this.mOrientation = orientation;
            invalidateStructure();
            requestLayout();
        }
    }

    public void setRowCount(int rowCount) {
        this.mVerticalAxis.setCount(rowCount);
        invalidateStructure();
        requestLayout();
    }

    public void setColumnCount(int columnCount) {
        this.mHorizontalAxis.setCount(columnCount);
        invalidateStructure();
        requestLayout();
    }

    public void setUseDefaultMargins(boolean useDefaultMargins) {
        this.mUseDefaultMargins = useDefaultMargins;
        requestLayout();
    }

    public void setAlignmentMode(int alignmentMode) {
        this.mAlignmentMode = alignmentMode;
        requestLayout();
    }

    public void setRowOrderPreserved(boolean rowOrderPreserved) {
        this.mVerticalAxis.setOrderPreserved(rowOrderPreserved);
        invalidateStructure();
        requestLayout();
    }

    public void setColumnOrderPreserved(boolean columnOrderPreserved) {
        this.mHorizontalAxis.setOrderPreserved(columnOrderPreserved);
        invalidateStructure();
        requestLayout();
    }

    static int max2(int[] a2, int valueIfEmpty) {
        int result = valueIfEmpty;
        for (int i : a2) {
            result = Math.max(result, i);
        }
        return result;
    }

    static <T> T[] append(T[] a2, T[] b2) {
        T[] result = (T[]) ((Object[]) Array.newInstance(a2.getClass().getComponentType(), a2.length + b2.length));
        System.arraycopy(a2, 0, result, 0, a2.length);
        System.arraycopy(b2, 0, result, a2.length, b2.length);
        return result;
    }

    static Alignment getAlignment(int gravity, boolean horizontal) {
        int mask = horizontal ? 7 : 112;
        int shift = horizontal ? 0 : 4;
        int flags = (gravity & mask) >> shift;
        if (flags != 1) {
            if (flags == 3) {
                return horizontal ? LEFT : TOP;
            } else if (flags == 5) {
                return horizontal ? RIGHT : BOTTOM;
            } else if (flags != 7) {
                if (flags != 8388611) {
                    if (flags == 8388613) {
                        return END;
                    }
                    return UNDEFINED_ALIGNMENT;
                }
                return START;
            } else {
                return FILL;
            }
        }
        return CENTER;
    }

    private int getDefaultMargin(View c, boolean horizontal, boolean leading) {
        if (c.getClass() == Space.class || c.getClass() == android.widget.Space.class) {
            return 0;
        }
        return this.mDefaultGap / 2;
    }

    private int getDefaultMargin(View c, boolean isAtEdge, boolean horizontal, boolean leading) {
        return getDefaultMargin(c, horizontal, leading);
    }

    private int getDefaultMargin(View c, LayoutParams p, boolean horizontal, boolean leading) {
        boolean leading1;
        boolean isAtEdge = false;
        if (this.mUseDefaultMargins) {
            Spec spec = horizontal ? p.columnSpec : p.rowSpec;
            Axis axis = horizontal ? this.mHorizontalAxis : this.mVerticalAxis;
            Interval span = spec.span;
            if (horizontal && isLayoutRtlCompat()) {
                leading1 = !leading;
            } else {
                leading1 = leading;
            }
            if (!leading1 ? span.max == axis.getCount() : span.min == 0) {
                isAtEdge = true;
            }
            return getDefaultMargin(c, isAtEdge, horizontal, leading);
        }
        return 0;
    }

    int getMargin1(View view, boolean horizontal, boolean leading) {
        LayoutParams lp = getLayoutParams(view);
        int margin = horizontal ? leading ? lp.leftMargin : lp.rightMargin : leading ? lp.topMargin : lp.bottomMargin;
        return margin == Integer.MIN_VALUE ? getDefaultMargin(view, lp, horizontal, leading) : margin;
    }

    private boolean isLayoutRtlCompat() {
        return ViewCompat.getLayoutDirection(this) == 1;
    }

    private int getMargin(View view, boolean horizontal, boolean leading) {
        if (this.mAlignmentMode == 1) {
            return getMargin1(view, horizontal, leading);
        }
        Axis axis = horizontal ? this.mHorizontalAxis : this.mVerticalAxis;
        int[] margins = leading ? axis.getLeadingMargins() : axis.getTrailingMargins();
        LayoutParams lp = getLayoutParams(view);
        Spec spec = horizontal ? lp.columnSpec : lp.rowSpec;
        int index = leading ? spec.span.min : spec.span.max;
        return margins[index];
    }

    private int getTotalMargin(View child, boolean horizontal) {
        return getMargin(child, horizontal, true) + getMargin(child, horizontal, false);
    }

    private static boolean fits(int[] a2, int value, int start, int end) {
        if (end > a2.length) {
            return false;
        }
        for (int i = start; i < end; i++) {
            if (a2[i] > value) {
                return false;
            }
        }
        return true;
    }

    private static void procrusteanFill(int[] a2, int start, int end, int value) {
        int length = a2.length;
        Arrays.fill(a2, Math.min(start, length), Math.min(end, length), value);
    }

    private static void setCellGroup(LayoutParams lp, int row, int rowSpan, int col, int colSpan) {
        lp.setRowSpecSpan(new Interval(row, row + rowSpan));
        lp.setColumnSpecSpan(new Interval(col, col + colSpan));
    }

    private static int clip(Interval minorRange, boolean minorWasDefined, int count) {
        int size = minorRange.size();
        if (count == 0) {
            return size;
        }
        int min = minorWasDefined ? Math.min(minorRange.min, count) : 0;
        return Math.min(size, count - min);
    }

    private void validateLayoutParams() {
        int N;
        GridLayout gridLayout = this;
        boolean horizontal = gridLayout.mOrientation == 0;
        Axis axis = horizontal ? gridLayout.mHorizontalAxis : gridLayout.mVerticalAxis;
        int count = axis.definedCount != Integer.MIN_VALUE ? axis.definedCount : 0;
        int major = 0;
        int minor = 0;
        int[] maxSizes = new int[count];
        int i = 0;
        int N2 = getChildCount();
        while (i < N2) {
            LayoutParams lp = (LayoutParams) gridLayout.getChildAt(i).getLayoutParams();
            Spec majorSpec = horizontal ? lp.rowSpec : lp.columnSpec;
            Interval majorRange = majorSpec.span;
            boolean majorWasDefined = majorSpec.startDefined;
            int majorSpan = majorRange.size();
            if (majorWasDefined) {
                major = majorRange.min;
            }
            Spec minorSpec = horizontal ? lp.columnSpec : lp.rowSpec;
            Interval minorRange = minorSpec.span;
            boolean minorWasDefined = minorSpec.startDefined;
            Axis axis2 = axis;
            int minorSpan = clip(minorRange, minorWasDefined, count);
            if (minorWasDefined) {
                minor = minorRange.min;
            }
            if (count != 0) {
                if (!majorWasDefined || !minorWasDefined) {
                    while (true) {
                        N = N2;
                        int N3 = minor + minorSpan;
                        if (fits(maxSizes, major, minor, N3)) {
                            break;
                        }
                        if (minorWasDefined) {
                            major++;
                        } else {
                            int N4 = minor + minorSpan;
                            if (N4 <= count) {
                                minor++;
                            } else {
                                minor = 0;
                                major++;
                            }
                        }
                        N2 = N;
                    }
                } else {
                    N = N2;
                }
                procrusteanFill(maxSizes, minor, minor + minorSpan, major + majorSpan);
            } else {
                N = N2;
            }
            if (horizontal) {
                setCellGroup(lp, major, majorSpan, minor, minorSpan);
            } else {
                setCellGroup(lp, minor, minorSpan, major, majorSpan);
            }
            minor += minorSpan;
            i++;
            axis = axis2;
            N2 = N;
            gridLayout = this;
        }
    }

    private void invalidateStructure() {
        this.mLastLayoutParamsHashCode = 0;
        if (this.mHorizontalAxis != null) {
            this.mHorizontalAxis.invalidateStructure();
        }
        if (this.mVerticalAxis != null) {
            this.mVerticalAxis.invalidateStructure();
        }
        invalidateValues();
    }

    private void invalidateValues() {
        if (this.mHorizontalAxis != null && this.mVerticalAxis != null) {
            this.mHorizontalAxis.invalidateValues();
            this.mVerticalAxis.invalidateValues();
        }
    }

    final LayoutParams getLayoutParams(View c) {
        return (LayoutParams) c.getLayoutParams();
    }

    static void handleInvalidParams(String msg) {
        throw new IllegalArgumentException(msg + ". ");
    }

    private void checkLayoutParams(LayoutParams lp, boolean horizontal) {
        String groupName = horizontal ? "column" : "row";
        Spec spec = horizontal ? lp.columnSpec : lp.rowSpec;
        Interval span = spec.span;
        if (span.min != Integer.MIN_VALUE && span.min < 0) {
            handleInvalidParams(groupName + " indices must be positive");
        }
        Axis axis = horizontal ? this.mHorizontalAxis : this.mVerticalAxis;
        int count = axis.definedCount;
        if (count != Integer.MIN_VALUE) {
            if (span.max > count) {
                handleInvalidParams(groupName + " indices (start + span) mustn't exceed the " + groupName + " count");
            }
            if (span.size() > count) {
                handleInvalidParams(groupName + " span mustn't exceed the " + groupName + " count");
            }
        }
    }

    @Override // android.view.ViewGroup
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        if (p instanceof LayoutParams) {
            LayoutParams lp = (LayoutParams) p;
            checkLayoutParams(lp, true);
            checkLayoutParams(lp, false);
            return true;
        }
        return false;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.view.ViewGroup
    public LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
        if (lp instanceof LayoutParams) {
            return new LayoutParams((LayoutParams) lp);
        }
        if (lp instanceof ViewGroup.MarginLayoutParams) {
            return new LayoutParams((ViewGroup.MarginLayoutParams) lp);
        }
        return new LayoutParams(lp);
    }

    private int computeLayoutParamsHashCode() {
        int result = 1;
        int N = getChildCount();
        for (int i = 0; i < N; i++) {
            View c = getChildAt(i);
            if (c.getVisibility() != 8) {
                LayoutParams lp = (LayoutParams) c.getLayoutParams();
                result = (31 * result) + lp.hashCode();
            }
        }
        return result;
    }

    private void consistencyCheck() {
        if (this.mLastLayoutParamsHashCode == 0) {
            validateLayoutParams();
            this.mLastLayoutParamsHashCode = computeLayoutParamsHashCode();
        } else if (this.mLastLayoutParamsHashCode != computeLayoutParamsHashCode()) {
            this.mPrinter.println("The fields of some layout parameters were modified in between layout operations. Check the javadoc for GridLayout.LayoutParams#rowSpec.");
            invalidateStructure();
            consistencyCheck();
        }
    }

    private void measureChildWithMargins2(View child, int parentWidthSpec, int parentHeightSpec, int childWidth, int childHeight) {
        int childWidthSpec = getChildMeasureSpec(parentWidthSpec, getTotalMargin(child, true), childWidth);
        int childHeightSpec = getChildMeasureSpec(parentHeightSpec, getTotalMargin(child, false), childHeight);
        child.measure(childWidthSpec, childHeightSpec);
    }

    private void measureChildrenWithMargins(int widthSpec, int heightSpec, boolean firstPass) {
        int N = getChildCount();
        int i = 0;
        while (true) {
            int N2 = N;
            if (i < N2) {
                View c = getChildAt(i);
                if (c.getVisibility() != 8) {
                    LayoutParams lp = getLayoutParams(c);
                    if (firstPass) {
                        measureChildWithMargins2(c, widthSpec, heightSpec, lp.width, lp.height);
                    } else {
                        boolean horizontal = this.mOrientation == 0;
                        Spec spec = horizontal ? lp.columnSpec : lp.rowSpec;
                        if (spec.getAbsoluteAlignment(horizontal) == FILL) {
                            Interval span = spec.span;
                            Axis axis = horizontal ? this.mHorizontalAxis : this.mVerticalAxis;
                            int[] locations = axis.getLocations();
                            int cellSize = locations[span.max] - locations[span.min];
                            int viewSize = cellSize - getTotalMargin(c, horizontal);
                            if (horizontal) {
                                measureChildWithMargins2(c, widthSpec, heightSpec, viewSize, lp.height);
                            } else {
                                measureChildWithMargins2(c, widthSpec, heightSpec, lp.width, viewSize);
                            }
                        }
                    }
                }
                i++;
                N = N2;
            } else {
                return;
            }
        }
    }

    static int adjust(int measureSpec, int delta) {
        return View.MeasureSpec.makeMeasureSpec(View.MeasureSpec.getSize(measureSpec + delta), View.MeasureSpec.getMode(measureSpec));
    }

    @Override // android.view.View
    protected void onMeasure(int widthSpec, int heightSpec) {
        int heightSansPadding;
        int widthSansPadding;
        consistencyCheck();
        invalidateValues();
        int hPadding = getPaddingLeft() + getPaddingRight();
        int vPadding = getPaddingTop() + getPaddingBottom();
        int widthSpecSansPadding = adjust(widthSpec, -hPadding);
        int heightSpecSansPadding = adjust(heightSpec, -vPadding);
        measureChildrenWithMargins(widthSpecSansPadding, heightSpecSansPadding, true);
        if (this.mOrientation == 0) {
            widthSansPadding = this.mHorizontalAxis.getMeasure(widthSpecSansPadding);
            measureChildrenWithMargins(widthSpecSansPadding, heightSpecSansPadding, false);
            heightSansPadding = this.mVerticalAxis.getMeasure(heightSpecSansPadding);
        } else {
            heightSansPadding = this.mVerticalAxis.getMeasure(heightSpecSansPadding);
            measureChildrenWithMargins(widthSpecSansPadding, heightSpecSansPadding, false);
            widthSansPadding = this.mHorizontalAxis.getMeasure(widthSpecSansPadding);
        }
        int measuredWidth = Math.max(widthSansPadding + hPadding, getSuggestedMinimumWidth());
        int measuredHeight = Math.max(heightSansPadding + vPadding, getSuggestedMinimumHeight());
        setMeasuredDimension(View.resolveSizeAndState(measuredWidth, widthSpec, 0), View.resolveSizeAndState(measuredHeight, heightSpec, 0));
    }

    private int getMeasurement(View c, boolean horizontal) {
        return horizontal ? c.getMeasuredWidth() : c.getMeasuredHeight();
    }

    final int getMeasurementIncludingMargin(View c, boolean horizontal) {
        if (c.getVisibility() == 8) {
            return 0;
        }
        return getMeasurement(c, horizontal) + getTotalMargin(c, horizontal);
    }

    @Override // android.view.View, android.view.ViewParent
    public void requestLayout() {
        super.requestLayout();
        invalidateStructure();
    }

    @Override // android.view.ViewGroup, android.view.View
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int N;
        int i;
        GridLayout gridLayout = this;
        consistencyCheck();
        int targetWidth = right - left;
        int targetHeight = bottom - top;
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();
        gridLayout.mHorizontalAxis.layout((targetWidth - paddingLeft) - paddingRight);
        gridLayout.mVerticalAxis.layout((targetHeight - paddingTop) - paddingBottom);
        int[] hLocations = gridLayout.mHorizontalAxis.getLocations();
        int[] vLocations = gridLayout.mVerticalAxis.getLocations();
        int N2 = getChildCount();
        int cx = 0;
        while (true) {
            int N3 = N2;
            if (cx < N3) {
                View c = gridLayout.getChildAt(cx);
                if (c.getVisibility() == 8) {
                    N = N3;
                    i = cx;
                } else {
                    LayoutParams lp = gridLayout.getLayoutParams(c);
                    Spec columnSpec = lp.columnSpec;
                    Spec rowSpec = lp.rowSpec;
                    Interval colSpan = columnSpec.span;
                    Interval rowSpan = rowSpec.span;
                    int x1 = hLocations[colSpan.min];
                    int y1 = vLocations[rowSpan.min];
                    int x2 = hLocations[colSpan.max];
                    int y2 = vLocations[rowSpan.max];
                    int cellWidth = x2 - x1;
                    int cellHeight = y2 - y1;
                    int pWidth = gridLayout.getMeasurement(c, true);
                    int pHeight = gridLayout.getMeasurement(c, false);
                    Alignment hAlign = columnSpec.getAbsoluteAlignment(true);
                    Alignment vAlign = rowSpec.getAbsoluteAlignment(false);
                    Bounds boundsX = gridLayout.mHorizontalAxis.getGroupBounds().getValue(cx);
                    Bounds boundsY = gridLayout.mVerticalAxis.getGroupBounds().getValue(cx);
                    int gravityOffsetX = hAlign.getGravityOffset(c, cellWidth - boundsX.size(true));
                    int gravityOffsetY = vAlign.getGravityOffset(c, cellHeight - boundsY.size(true));
                    int leftMargin = gridLayout.getMargin(c, true, true);
                    int topMargin = gridLayout.getMargin(c, false, true);
                    int rightMargin = gridLayout.getMargin(c, true, false);
                    int bottomMargin = gridLayout.getMargin(c, false, false);
                    int sumMarginsX = leftMargin + rightMargin;
                    int sumMarginsY = topMargin + bottomMargin;
                    int i2 = pWidth + sumMarginsX;
                    GridLayout gridLayout2 = gridLayout;
                    N = N3;
                    i = cx;
                    int alignmentOffsetX = boundsX.getOffset(gridLayout2, c, hAlign, i2, true);
                    int alignmentOffsetY = boundsY.getOffset(gridLayout2, c, vAlign, pHeight + sumMarginsY, false);
                    int width = hAlign.getSizeInCell(c, pWidth, cellWidth - sumMarginsX);
                    int height = vAlign.getSizeInCell(c, pHeight, cellHeight - sumMarginsY);
                    int dx = x1 + gravityOffsetX + alignmentOffsetX;
                    int cx2 = !isLayoutRtlCompat() ? paddingLeft + leftMargin + dx : (((targetWidth - width) - paddingRight) - rightMargin) - dx;
                    int alignmentOffsetY2 = paddingTop + y1 + gravityOffsetY + alignmentOffsetY + topMargin;
                    if (width != c.getMeasuredWidth() || height != c.getMeasuredHeight()) {
                        c.measure(View.MeasureSpec.makeMeasureSpec(width, 1073741824), View.MeasureSpec.makeMeasureSpec(height, 1073741824));
                    }
                    c.layout(cx2, alignmentOffsetY2, cx2 + width, alignmentOffsetY2 + height);
                }
                cx = i + 1;
                N2 = N;
                gridLayout = this;
            } else {
                return;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public final class Axis {
        static final /* synthetic */ boolean $assertionsDisabled = false;
        public Arc[] arcs;
        PackedMap<Interval, MutableInt> backwardLinks;
        public int[] deltas;
        PackedMap<Interval, MutableInt> forwardLinks;
        PackedMap<Spec, Bounds> groupBounds;
        public boolean hasWeights;
        public final boolean horizontal;
        public int[] leadingMargins;
        public int[] locations;
        public int[] trailingMargins;
        public int definedCount = Integer.MIN_VALUE;
        private int maxIndex = Integer.MIN_VALUE;
        public boolean groupBoundsValid = false;
        public boolean forwardLinksValid = false;
        public boolean backwardLinksValid = false;
        public boolean leadingMarginsValid = false;
        public boolean trailingMarginsValid = false;
        public boolean arcsValid = false;
        public boolean locationsValid = false;
        public boolean hasWeightsValid = false;
        boolean orderPreserved = true;
        private MutableInt parentMin = new MutableInt(0);
        private MutableInt parentMax = new MutableInt(-100000);

        Axis(boolean horizontal) {
            this.horizontal = horizontal;
        }

        private int calculateMaxIndex() {
            int result = -1;
            int N = GridLayout.this.getChildCount();
            for (int i = 0; i < N; i++) {
                View c = GridLayout.this.getChildAt(i);
                LayoutParams params = GridLayout.this.getLayoutParams(c);
                Spec spec = this.horizontal ? params.columnSpec : params.rowSpec;
                Interval span = spec.span;
                result = Math.max(Math.max(Math.max(result, span.min), span.max), span.size());
            }
            if (result == -1) {
                return Integer.MIN_VALUE;
            }
            return result;
        }

        private int getMaxIndex() {
            if (this.maxIndex == Integer.MIN_VALUE) {
                this.maxIndex = Math.max(0, calculateMaxIndex());
            }
            return this.maxIndex;
        }

        public int getCount() {
            return Math.max(this.definedCount, getMaxIndex());
        }

        public void setCount(int count) {
            if (count != Integer.MIN_VALUE && count < getMaxIndex()) {
                StringBuilder sb = new StringBuilder();
                sb.append(this.horizontal ? "column" : "row");
                sb.append("Count must be greater than or equal to the maximum of all grid indices ");
                sb.append("(and spans) defined in the LayoutParams of each child");
                GridLayout.handleInvalidParams(sb.toString());
            }
            this.definedCount = count;
        }

        public void setOrderPreserved(boolean orderPreserved) {
            this.orderPreserved = orderPreserved;
            invalidateStructure();
        }

        private PackedMap<Spec, Bounds> createGroupBounds() {
            Assoc<Spec, Bounds> assoc = Assoc.of(Spec.class, Bounds.class);
            int N = GridLayout.this.getChildCount();
            for (int i = 0; i < N; i++) {
                View c = GridLayout.this.getChildAt(i);
                LayoutParams lp = GridLayout.this.getLayoutParams(c);
                Spec spec = this.horizontal ? lp.columnSpec : lp.rowSpec;
                Bounds bounds = spec.getAbsoluteAlignment(this.horizontal).getBounds();
                assoc.put(spec, bounds);
            }
            return assoc.pack();
        }

        private void computeGroupBounds() {
            Bounds[] values = this.groupBounds.values;
            for (Bounds bounds : values) {
                bounds.reset();
            }
            int N = GridLayout.this.getChildCount();
            for (int i = 0; i < N; i++) {
                View c = GridLayout.this.getChildAt(i);
                LayoutParams lp = GridLayout.this.getLayoutParams(c);
                Spec spec = this.horizontal ? lp.columnSpec : lp.rowSpec;
                int size = GridLayout.this.getMeasurementIncludingMargin(c, this.horizontal) + (spec.weight == 0.0f ? 0 : getDeltas()[i]);
                this.groupBounds.getValue(i).include(GridLayout.this, c, spec, this, size);
            }
        }

        public PackedMap<Spec, Bounds> getGroupBounds() {
            if (this.groupBounds == null) {
                this.groupBounds = createGroupBounds();
            }
            if (!this.groupBoundsValid) {
                computeGroupBounds();
                this.groupBoundsValid = true;
            }
            return this.groupBounds;
        }

        private PackedMap<Interval, MutableInt> createLinks(boolean min) {
            Assoc<Interval, MutableInt> result = Assoc.of(Interval.class, MutableInt.class);
            Spec[] keys = getGroupBounds().keys;
            int N = keys.length;
            for (int i = 0; i < N; i++) {
                Interval span = min ? keys[i].span : keys[i].span.inverse();
                result.put(span, new MutableInt());
            }
            return result.pack();
        }

        private void computeLinks(PackedMap<Interval, MutableInt> links, boolean min) {
            MutableInt[] spans = links.values;
            for (MutableInt mutableInt : spans) {
                mutableInt.reset();
            }
            Bounds[] bounds = getGroupBounds().values;
            for (int i = 0; i < bounds.length; i++) {
                int size = bounds[i].size(min);
                MutableInt valueHolder = links.getValue(i);
                valueHolder.value = Math.max(valueHolder.value, min ? size : -size);
            }
        }

        private PackedMap<Interval, MutableInt> getForwardLinks() {
            if (this.forwardLinks == null) {
                this.forwardLinks = createLinks(true);
            }
            if (!this.forwardLinksValid) {
                computeLinks(this.forwardLinks, true);
                this.forwardLinksValid = true;
            }
            return this.forwardLinks;
        }

        private PackedMap<Interval, MutableInt> getBackwardLinks() {
            if (this.backwardLinks == null) {
                this.backwardLinks = createLinks(false);
            }
            if (!this.backwardLinksValid) {
                computeLinks(this.backwardLinks, false);
                this.backwardLinksValid = true;
            }
            return this.backwardLinks;
        }

        private void include(List<Arc> arcs, Interval key, MutableInt size, boolean ignoreIfAlreadyPresent) {
            if (key.size() == 0) {
                return;
            }
            if (ignoreIfAlreadyPresent) {
                for (Arc arc : arcs) {
                    Interval span = arc.span;
                    if (span.equals(key)) {
                        return;
                    }
                }
            }
            arcs.add(new Arc(key, size));
        }

        private void include(List<Arc> arcs, Interval key, MutableInt size) {
            include(arcs, key, size, true);
        }

        Arc[][] groupArcsByFirstVertex(Arc[] arcs) {
            int N = getCount() + 1;
            Arc[][] result = new Arc[N];
            int[] sizes = new int[N];
            for (Arc arc : arcs) {
                int i = arc.span.min;
                sizes[i] = sizes[i] + 1;
            }
            for (int i2 = 0; i2 < sizes.length; i2++) {
                result[i2] = new Arc[sizes[i2]];
            }
            Arrays.fill(sizes, 0);
            for (Arc arc2 : arcs) {
                int i3 = arc2.span.min;
                Arc[] arcArr = result[i3];
                int i4 = sizes[i3];
                sizes[i3] = i4 + 1;
                arcArr[i4] = arc2;
            }
            return result;
        }

        /* JADX WARN: Type inference failed for: r0v0, types: [android.support.v7.widget.GridLayout$Axis$1] */
        private Arc[] topologicalSort(final Arc[] arcs) {
            return new Object() { // from class: android.support.v7.widget.GridLayout.Axis.1
                static final /* synthetic */ boolean $assertionsDisabled = false;
                Arc[][] arcsByVertex;
                int cursor;
                Arc[] result;
                int[] visited;

                {
                    this.result = new Arc[arcs.length];
                    this.cursor = this.result.length - 1;
                    this.arcsByVertex = Axis.this.groupArcsByFirstVertex(arcs);
                    this.visited = new int[Axis.this.getCount() + 1];
                }

                void walk(int loc) {
                    Arc[] arcArr;
                    switch (this.visited[loc]) {
                        case 0:
                            this.visited[loc] = 1;
                            for (Arc arc : this.arcsByVertex[loc]) {
                                walk(arc.span.max);
                                Arc[] arcArr2 = this.result;
                                int i = this.cursor;
                                this.cursor = i - 1;
                                arcArr2[i] = arc;
                            }
                            this.visited[loc] = 2;
                            return;
                        case 1:
                        default:
                            return;
                    }
                }

                Arc[] sort() {
                    int N = this.arcsByVertex.length;
                    for (int loc = 0; loc < N; loc++) {
                        walk(loc);
                    }
                    return this.result;
                }
            }.sort();
        }

        private Arc[] topologicalSort(List<Arc> arcs) {
            return topologicalSort((Arc[]) arcs.toArray(new Arc[arcs.size()]));
        }

        private void addComponentSizes(List<Arc> result, PackedMap<Interval, MutableInt> links) {
            for (int i = 0; i < links.keys.length; i++) {
                Interval key = links.keys[i];
                include(result, key, links.values[i], false);
            }
        }

        private Arc[] createArcs() {
            List<Arc> mins = new ArrayList<>();
            List<Arc> maxs = new ArrayList<>();
            addComponentSizes(mins, getForwardLinks());
            addComponentSizes(maxs, getBackwardLinks());
            if (this.orderPreserved) {
                for (int i = 0; i < getCount(); i++) {
                    include(mins, new Interval(i, i + 1), new MutableInt(0));
                }
            }
            int N = getCount();
            include(mins, new Interval(0, N), this.parentMin, false);
            include(maxs, new Interval(N, 0), this.parentMax, false);
            Arc[] sMins = topologicalSort(mins);
            Arc[] sMaxs = topologicalSort(maxs);
            return (Arc[]) GridLayout.append(sMins, sMaxs);
        }

        private void computeArcs() {
            getForwardLinks();
            getBackwardLinks();
        }

        public Arc[] getArcs() {
            if (this.arcs == null) {
                this.arcs = createArcs();
            }
            if (!this.arcsValid) {
                computeArcs();
                this.arcsValid = true;
            }
            return this.arcs;
        }

        private boolean relax(int[] locations, Arc entry) {
            if (entry.valid) {
                Interval span = entry.span;
                int u = span.min;
                int v = span.max;
                int value = entry.value.value;
                int candidate = locations[u] + value;
                if (candidate > locations[v]) {
                    locations[v] = candidate;
                    return true;
                }
                return false;
            }
            return false;
        }

        private void init(int[] locations) {
            Arrays.fill(locations, 0);
        }

        private String arcsToString(List<Arc> arcs) {
            StringBuilder sb;
            String var = this.horizontal ? "x" : "y";
            StringBuilder result = new StringBuilder();
            boolean first = true;
            for (Arc arc : arcs) {
                if (first) {
                    first = false;
                } else {
                    result = result.append(", ");
                }
                int src = arc.span.min;
                int dst = arc.span.max;
                int value = arc.value.value;
                if (src < dst) {
                    sb = new StringBuilder();
                    sb.append(var);
                    sb.append(dst);
                    sb.append("-");
                    sb.append(var);
                    sb.append(src);
                    sb.append(">=");
                    sb.append(value);
                } else {
                    sb = new StringBuilder();
                    sb.append(var);
                    sb.append(src);
                    sb.append("-");
                    sb.append(var);
                    sb.append(dst);
                    sb.append("<=");
                    sb.append(-value);
                }
                result.append(sb.toString());
            }
            return result.toString();
        }

        private void logError(String axisName, Arc[] arcs, boolean[] culprits0) {
            List<Arc> culprits = new ArrayList<>();
            List<Arc> removed = new ArrayList<>();
            for (int c = 0; c < arcs.length; c++) {
                Arc arc = arcs[c];
                if (culprits0[c]) {
                    culprits.add(arc);
                }
                if (!arc.valid) {
                    removed.add(arc);
                }
            }
            Printer printer = GridLayout.this.mPrinter;
            printer.println(axisName + " constraints: " + arcsToString(culprits) + " are inconsistent; permanently removing: " + arcsToString(removed) + ". ");
        }

        private boolean solve(Arc[] arcs, int[] locations) {
            return solve(arcs, locations, true);
        }

        private boolean solve(Arc[] arcs, int[] locations, boolean modifyOnError) {
            String axisName = this.horizontal ? "horizontal" : "vertical";
            int N = getCount() + 1;
            boolean[] originalCulprits = null;
            for (int p = 0; p < arcs.length; p++) {
                init(locations);
                for (int i = 0; i < N; i++) {
                    boolean changed = false;
                    for (Arc arc : arcs) {
                        changed |= relax(locations, arc);
                    }
                    if (!changed) {
                        if (originalCulprits != null) {
                            logError(axisName, arcs, originalCulprits);
                        }
                        return true;
                    }
                }
                if (!modifyOnError) {
                    return false;
                }
                boolean[] culprits = new boolean[arcs.length];
                for (int i2 = 0; i2 < N; i2++) {
                    int length = arcs.length;
                    for (int j = 0; j < length; j++) {
                        culprits[j] = culprits[j] | relax(locations, arcs[j]);
                    }
                }
                if (p == 0) {
                    originalCulprits = culprits;
                }
                int i3 = 0;
                while (true) {
                    if (i3 >= arcs.length) {
                        break;
                    }
                    if (culprits[i3]) {
                        Arc arc2 = arcs[i3];
                        if (arc2.span.min >= arc2.span.max) {
                            arc2.valid = false;
                            break;
                        }
                    }
                    i3++;
                }
            }
            return true;
        }

        private void computeMargins(boolean leading) {
            int[] margins = leading ? this.leadingMargins : this.trailingMargins;
            int N = GridLayout.this.getChildCount();
            for (int i = 0; i < N; i++) {
                View c = GridLayout.this.getChildAt(i);
                if (c.getVisibility() != 8) {
                    LayoutParams lp = GridLayout.this.getLayoutParams(c);
                    Spec spec = this.horizontal ? lp.columnSpec : lp.rowSpec;
                    Interval span = spec.span;
                    int index = leading ? span.min : span.max;
                    margins[index] = Math.max(margins[index], GridLayout.this.getMargin1(c, this.horizontal, leading));
                }
            }
        }

        public int[] getLeadingMargins() {
            if (this.leadingMargins == null) {
                this.leadingMargins = new int[getCount() + 1];
            }
            if (!this.leadingMarginsValid) {
                computeMargins(true);
                this.leadingMarginsValid = true;
            }
            return this.leadingMargins;
        }

        public int[] getTrailingMargins() {
            if (this.trailingMargins == null) {
                this.trailingMargins = new int[getCount() + 1];
            }
            if (!this.trailingMarginsValid) {
                computeMargins(false);
                this.trailingMarginsValid = true;
            }
            return this.trailingMargins;
        }

        private boolean solve(int[] a2) {
            return solve(getArcs(), a2);
        }

        private boolean computeHasWeights() {
            int N = GridLayout.this.getChildCount();
            for (int i = 0; i < N; i++) {
                View child = GridLayout.this.getChildAt(i);
                if (child.getVisibility() != 8) {
                    LayoutParams lp = GridLayout.this.getLayoutParams(child);
                    Spec spec = this.horizontal ? lp.columnSpec : lp.rowSpec;
                    if (spec.weight != 0.0f) {
                        return true;
                    }
                }
            }
            return false;
        }

        private boolean hasWeights() {
            if (!this.hasWeightsValid) {
                this.hasWeights = computeHasWeights();
                this.hasWeightsValid = true;
            }
            return this.hasWeights;
        }

        public int[] getDeltas() {
            if (this.deltas == null) {
                this.deltas = new int[GridLayout.this.getChildCount()];
            }
            return this.deltas;
        }

        private void shareOutDelta(int totalDelta, float totalWeight) {
            Arrays.fill(this.deltas, 0);
            int N = GridLayout.this.getChildCount();
            for (int i = 0; i < N; i++) {
                View c = GridLayout.this.getChildAt(i);
                if (c.getVisibility() != 8) {
                    LayoutParams lp = GridLayout.this.getLayoutParams(c);
                    Spec spec = this.horizontal ? lp.columnSpec : lp.rowSpec;
                    float weight = spec.weight;
                    if (weight != 0.0f) {
                        int delta = Math.round((totalDelta * weight) / totalWeight);
                        this.deltas[i] = delta;
                        totalDelta -= delta;
                        totalWeight -= weight;
                    }
                }
            }
        }

        private void solveAndDistributeSpace(int[] a2) {
            Arrays.fill(getDeltas(), 0);
            solve(a2);
            boolean validSolution = true;
            int deltaMax = (this.parentMin.value * GridLayout.this.getChildCount()) + 1;
            if (deltaMax < 2) {
                return;
            }
            int deltaMin = 0;
            float totalWeight = calculateTotalWeight();
            int validDelta = -1;
            while (deltaMin < deltaMax) {
                int delta = (int) ((deltaMin + deltaMax) / 2);
                invalidateValues();
                shareOutDelta(delta, totalWeight);
                validSolution = solve(getArcs(), a2, false);
                if (validSolution) {
                    validDelta = delta;
                    deltaMin = delta + 1;
                } else {
                    deltaMax = delta;
                }
            }
            if (validDelta > 0 && !validSolution) {
                invalidateValues();
                shareOutDelta(validDelta, totalWeight);
                solve(a2);
            }
        }

        private float calculateTotalWeight() {
            float totalWeight = 0.0f;
            int N = GridLayout.this.getChildCount();
            for (int i = 0; i < N; i++) {
                View c = GridLayout.this.getChildAt(i);
                if (c.getVisibility() != 8) {
                    LayoutParams lp = GridLayout.this.getLayoutParams(c);
                    Spec spec = this.horizontal ? lp.columnSpec : lp.rowSpec;
                    totalWeight += spec.weight;
                }
            }
            return totalWeight;
        }

        private void computeLocations(int[] a2) {
            if (!hasWeights()) {
                solve(a2);
            } else {
                solveAndDistributeSpace(a2);
            }
            if (!this.orderPreserved) {
                int a0 = a2[0];
                int N = a2.length;
                for (int i = 0; i < N; i++) {
                    a2[i] = a2[i] - a0;
                }
            }
        }

        public int[] getLocations() {
            if (this.locations == null) {
                int N = getCount() + 1;
                this.locations = new int[N];
            }
            if (!this.locationsValid) {
                computeLocations(this.locations);
                this.locationsValid = true;
            }
            return this.locations;
        }

        private int size(int[] locations) {
            return locations[getCount()];
        }

        private void setParentConstraints(int min, int max) {
            this.parentMin.value = min;
            this.parentMax.value = -max;
            this.locationsValid = false;
        }

        private int getMeasure(int min, int max) {
            setParentConstraints(min, max);
            return size(getLocations());
        }

        public int getMeasure(int measureSpec) {
            int mode = View.MeasureSpec.getMode(measureSpec);
            int size = View.MeasureSpec.getSize(measureSpec);
            if (mode != Integer.MIN_VALUE) {
                if (mode != 0) {
                    if (mode != 1073741824) {
                        return 0;
                    }
                    return getMeasure(size, size);
                }
                return getMeasure(0, 100000);
            }
            return getMeasure(0, size);
        }

        public void layout(int size) {
            setParentConstraints(size, size);
            getLocations();
        }

        public void invalidateStructure() {
            this.maxIndex = Integer.MIN_VALUE;
            this.groupBounds = null;
            this.forwardLinks = null;
            this.backwardLinks = null;
            this.leadingMargins = null;
            this.trailingMargins = null;
            this.arcs = null;
            this.locations = null;
            this.deltas = null;
            this.hasWeightsValid = false;
            invalidateValues();
        }

        public void invalidateValues() {
            this.groupBoundsValid = false;
            this.forwardLinksValid = false;
            this.backwardLinksValid = false;
            this.leadingMarginsValid = false;
            this.trailingMarginsValid = false;
            this.arcsValid = false;
            this.locationsValid = false;
        }
    }

    /* loaded from: classes.dex */
    public static class LayoutParams extends ViewGroup.MarginLayoutParams {
        public Spec columnSpec;
        public Spec rowSpec;
        private static final Interval DEFAULT_SPAN = new Interval(Integer.MIN_VALUE, -2147483647);
        private static final int DEFAULT_SPAN_SIZE = DEFAULT_SPAN.size();
        private static final int MARGIN = R.styleable.GridLayout_Layout_android_layout_margin;
        private static final int LEFT_MARGIN = R.styleable.GridLayout_Layout_android_layout_marginLeft;
        private static final int TOP_MARGIN = R.styleable.GridLayout_Layout_android_layout_marginTop;
        private static final int RIGHT_MARGIN = R.styleable.GridLayout_Layout_android_layout_marginRight;
        private static final int BOTTOM_MARGIN = R.styleable.GridLayout_Layout_android_layout_marginBottom;
        private static final int COLUMN = R.styleable.GridLayout_Layout_layout_column;
        private static final int COLUMN_SPAN = R.styleable.GridLayout_Layout_layout_columnSpan;
        private static final int COLUMN_WEIGHT = R.styleable.GridLayout_Layout_layout_columnWeight;
        private static final int ROW = R.styleable.GridLayout_Layout_layout_row;
        private static final int ROW_SPAN = R.styleable.GridLayout_Layout_layout_rowSpan;
        private static final int ROW_WEIGHT = R.styleable.GridLayout_Layout_layout_rowWeight;
        private static final int GRAVITY = R.styleable.GridLayout_Layout_layout_gravity;

        private LayoutParams(int width, int height, int left, int top, int right, int bottom, Spec rowSpec, Spec columnSpec) {
            super(width, height);
            this.rowSpec = Spec.UNDEFINED;
            this.columnSpec = Spec.UNDEFINED;
            setMargins(left, top, right, bottom);
            this.rowSpec = rowSpec;
            this.columnSpec = columnSpec;
        }

        public LayoutParams(Spec rowSpec, Spec columnSpec) {
            this(-2, -2, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, rowSpec, columnSpec);
        }

        public LayoutParams() {
            this(Spec.UNDEFINED, Spec.UNDEFINED);
        }

        public LayoutParams(ViewGroup.LayoutParams params) {
            super(params);
            this.rowSpec = Spec.UNDEFINED;
            this.columnSpec = Spec.UNDEFINED;
        }

        public LayoutParams(ViewGroup.MarginLayoutParams params) {
            super(params);
            this.rowSpec = Spec.UNDEFINED;
            this.columnSpec = Spec.UNDEFINED;
        }

        public LayoutParams(LayoutParams source) {
            super((ViewGroup.MarginLayoutParams) source);
            this.rowSpec = Spec.UNDEFINED;
            this.columnSpec = Spec.UNDEFINED;
            this.rowSpec = source.rowSpec;
            this.columnSpec = source.columnSpec;
        }

        public LayoutParams(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.rowSpec = Spec.UNDEFINED;
            this.columnSpec = Spec.UNDEFINED;
            reInitSuper(context, attrs);
            init(context, attrs);
        }

        private void reInitSuper(Context context, AttributeSet attrs) {
            TypedArray a2 = context.obtainStyledAttributes(attrs, R.styleable.GridLayout_Layout);
            try {
                int margin = a2.getDimensionPixelSize(MARGIN, Integer.MIN_VALUE);
                this.leftMargin = a2.getDimensionPixelSize(LEFT_MARGIN, margin);
                this.topMargin = a2.getDimensionPixelSize(TOP_MARGIN, margin);
                this.rightMargin = a2.getDimensionPixelSize(RIGHT_MARGIN, margin);
                this.bottomMargin = a2.getDimensionPixelSize(BOTTOM_MARGIN, margin);
            } finally {
                a2.recycle();
            }
        }

        private void init(Context context, AttributeSet attrs) {
            TypedArray a2 = context.obtainStyledAttributes(attrs, R.styleable.GridLayout_Layout);
            try {
                int gravity = a2.getInt(GRAVITY, 0);
                int column = a2.getInt(COLUMN, Integer.MIN_VALUE);
                int colSpan = a2.getInt(COLUMN_SPAN, DEFAULT_SPAN_SIZE);
                float colWeight = a2.getFloat(COLUMN_WEIGHT, 0.0f);
                this.columnSpec = GridLayout.spec(column, colSpan, GridLayout.getAlignment(gravity, true), colWeight);
                int row = a2.getInt(ROW, Integer.MIN_VALUE);
                int rowSpan = a2.getInt(ROW_SPAN, DEFAULT_SPAN_SIZE);
                float rowWeight = a2.getFloat(ROW_WEIGHT, 0.0f);
                this.rowSpec = GridLayout.spec(row, rowSpan, GridLayout.getAlignment(gravity, false), rowWeight);
            } finally {
                a2.recycle();
            }
        }

        @Override // android.view.ViewGroup.LayoutParams
        protected void setBaseAttributes(TypedArray attributes, int widthAttr, int heightAttr) {
            this.width = attributes.getLayoutDimension(widthAttr, -2);
            this.height = attributes.getLayoutDimension(heightAttr, -2);
        }

        final void setRowSpecSpan(Interval span) {
            this.rowSpec = this.rowSpec.copyWriteSpan(span);
        }

        final void setColumnSpecSpan(Interval span) {
            this.columnSpec = this.columnSpec.copyWriteSpan(span);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            LayoutParams that = (LayoutParams) o;
            if (this.columnSpec.equals(that.columnSpec) && this.rowSpec.equals(that.rowSpec)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            int result = this.rowSpec.hashCode();
            return (31 * result) + this.columnSpec.hashCode();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class Arc {
        public final Interval span;
        public boolean valid = true;
        public final MutableInt value;

        public Arc(Interval span, MutableInt value) {
            this.span = span;
            this.value = value;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(this.span);
            sb.append(" ");
            sb.append(!this.valid ? "+>" : "->");
            sb.append(" ");
            sb.append(this.value);
            return sb.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class MutableInt {
        public int value;

        public MutableInt() {
            reset();
        }

        public MutableInt(int value) {
            this.value = value;
        }

        public void reset() {
            this.value = Integer.MIN_VALUE;
        }

        public String toString() {
            return Integer.toString(this.value);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class Assoc<K, V> extends ArrayList<Pair<K, V>> {
        private final Class<K> keyType;
        private final Class<V> valueType;

        private Assoc(Class<K> keyType, Class<V> valueType) {
            this.keyType = keyType;
            this.valueType = valueType;
        }

        public static <K, V> Assoc<K, V> of(Class<K> keyType, Class<V> valueType) {
            return new Assoc<>(keyType, valueType);
        }

        public void put(K key, V value) {
            add(Pair.create(key, value));
        }

        public PackedMap<K, V> pack() {
            int N = size();
            Object[] objArr = (Object[]) Array.newInstance((Class<?>) this.keyType, N);
            Object[] objArr2 = (Object[]) Array.newInstance((Class<?>) this.valueType, N);
            for (int i = 0; i < N; i++) {
                objArr[i] = get(i).first;
                objArr2[i] = get(i).second;
            }
            return new PackedMap<>(objArr, objArr2);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class PackedMap<K, V> {
        public final int[] index;
        public final K[] keys;
        public final V[] values;

        PackedMap(K[] keys, V[] values) {
            this.index = createIndex(keys);
            this.keys = (K[]) compact(keys, this.index);
            this.values = (V[]) compact(values, this.index);
        }

        public V getValue(int i) {
            return this.values[this.index[i]];
        }

        private static <K> int[] createIndex(K[] keys) {
            int size = keys.length;
            int[] result = new int[size];
            Map<K, Integer> keyToIndex = new HashMap<>();
            for (int i = 0; i < size; i++) {
                K key = keys[i];
                Integer index = keyToIndex.get(key);
                if (index == null) {
                    index = Integer.valueOf(keyToIndex.size());
                    keyToIndex.put(key, index);
                }
                result[i] = index.intValue();
            }
            return result;
        }

        private static <K> K[] compact(K[] a2, int[] index) {
            int size = a2.length;
            Class<?> componentType = a2.getClass().getComponentType();
            K[] result = (K[]) ((Object[]) Array.newInstance(componentType, GridLayout.max2(index, -1) + 1));
            for (int i = 0; i < size; i++) {
                result[index[i]] = a2[i];
            }
            return result;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class Bounds {
        public int after;
        public int before;
        public int flexibility;

        Bounds() {
            reset();
        }

        protected void reset() {
            this.before = Integer.MIN_VALUE;
            this.after = Integer.MIN_VALUE;
            this.flexibility = 2;
        }

        protected void include(int before, int after) {
            this.before = Math.max(this.before, before);
            this.after = Math.max(this.after, after);
        }

        protected int size(boolean min) {
            if (!min && GridLayout.canStretch(this.flexibility)) {
                return 100000;
            }
            return this.before + this.after;
        }

        protected int getOffset(GridLayout gl, View c, Alignment a2, int size, boolean horizontal) {
            return this.before - a2.getAlignmentValue(c, size, ViewGroupCompat.getLayoutMode(gl));
        }

        protected final void include(GridLayout gl, View c, Spec spec, Axis axis, int size) {
            this.flexibility &= spec.getFlexibility();
            boolean horizontal = axis.horizontal;
            Alignment alignment = spec.getAbsoluteAlignment(horizontal);
            int before = alignment.getAlignmentValue(c, size, ViewGroupCompat.getLayoutMode(gl));
            include(before, size - before);
        }

        public String toString() {
            return "Bounds{before=" + this.before + ", after=" + this.after + '}';
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static final class Interval {
        public final int max;
        public final int min;

        public Interval(int min, int max) {
            this.min = min;
            this.max = max;
        }

        int size() {
            return this.max - this.min;
        }

        Interval inverse() {
            return new Interval(this.max, this.min);
        }

        public boolean equals(Object that) {
            if (this == that) {
                return true;
            }
            if (that == null || getClass() != that.getClass()) {
                return false;
            }
            Interval interval = (Interval) that;
            if (this.max == interval.max && this.min == interval.min) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            int result = this.min;
            return (31 * result) + this.max;
        }

        public String toString() {
            return "[" + this.min + ", " + this.max + "]";
        }
    }

    /* loaded from: classes.dex */
    public static class Spec {
        static final Spec UNDEFINED = GridLayout.spec(Integer.MIN_VALUE);
        final Alignment alignment;
        final Interval span;
        final boolean startDefined;
        final float weight;

        private Spec(boolean startDefined, Interval span, Alignment alignment, float weight) {
            this.startDefined = startDefined;
            this.span = span;
            this.alignment = alignment;
            this.weight = weight;
        }

        Spec(boolean startDefined, int start, int size, Alignment alignment, float weight) {
            this(startDefined, new Interval(start, start + size), alignment, weight);
        }

        public Alignment getAbsoluteAlignment(boolean horizontal) {
            if (this.alignment != GridLayout.UNDEFINED_ALIGNMENT) {
                return this.alignment;
            }
            if (this.weight == 0.0f) {
                return horizontal ? GridLayout.START : GridLayout.BASELINE;
            }
            return GridLayout.FILL;
        }

        final Spec copyWriteSpan(Interval span) {
            return new Spec(this.startDefined, span, this.alignment, this.weight);
        }

        final int getFlexibility() {
            return (this.alignment == GridLayout.UNDEFINED_ALIGNMENT && this.weight == 0.0f) ? 0 : 2;
        }

        public boolean equals(Object that) {
            if (this == that) {
                return true;
            }
            if (that == null || getClass() != that.getClass()) {
                return false;
            }
            Spec spec = (Spec) that;
            if (this.alignment.equals(spec.alignment) && this.span.equals(spec.span)) {
                return true;
            }
            return false;
        }

        public int hashCode() {
            int result = this.span.hashCode();
            return (31 * result) + this.alignment.hashCode();
        }
    }

    public static Spec spec(int start, int size, Alignment alignment, float weight) {
        return new Spec(start != Integer.MIN_VALUE, start, size, alignment, weight);
    }

    public static Spec spec(int start, int size, Alignment alignment) {
        return spec(start, size, alignment, 0.0f);
    }

    public static Spec spec(int start, int size) {
        return spec(start, size, UNDEFINED_ALIGNMENT);
    }

    public static Spec spec(int start) {
        return spec(start, 1);
    }

    /* loaded from: classes.dex */
    public static abstract class Alignment {
        abstract int getAlignmentValue(View view, int i, int i2);

        abstract String getDebugString();

        abstract int getGravityOffset(View view, int i);

        Alignment() {
        }

        int getSizeInCell(View view, int viewSize, int cellSize) {
            return viewSize;
        }

        Bounds getBounds() {
            return new Bounds();
        }

        public String toString() {
            return "Alignment:" + getDebugString();
        }
    }

    private static Alignment createSwitchingAlignment(final Alignment ltr, final Alignment rtl) {
        return new Alignment() { // from class: android.support.v7.widget.GridLayout.5
            @Override // android.support.v7.widget.GridLayout.Alignment
            int getGravityOffset(View view, int cellDelta) {
                boolean isLayoutRtl = ViewCompat.getLayoutDirection(view) == 1;
                return (!isLayoutRtl ? Alignment.this : rtl).getGravityOffset(view, cellDelta);
            }

            @Override // android.support.v7.widget.GridLayout.Alignment
            public int getAlignmentValue(View view, int viewSize, int mode) {
                boolean isLayoutRtl = ViewCompat.getLayoutDirection(view) == 1;
                return (!isLayoutRtl ? Alignment.this : rtl).getAlignmentValue(view, viewSize, mode);
            }

            @Override // android.support.v7.widget.GridLayout.Alignment
            String getDebugString() {
                return "SWITCHING[L:" + Alignment.this.getDebugString() + ", R:" + rtl.getDebugString() + "]";
            }
        };
    }

    static boolean canStretch(int flexibility) {
        return (flexibility & 2) != 0;
    }
}
