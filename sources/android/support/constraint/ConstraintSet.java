package android.support.constraint;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.Constraints;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.Xml;
import android.view.View;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
/* loaded from: classes.dex */
public class ConstraintSet {
    private static final int[] VISIBILITY_FLAGS = {0, 4, 8};
    private static SparseIntArray mapToConstant = new SparseIntArray();
    private HashMap<Integer, Constraint> mConstraints = new HashMap<>();

    static {
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintLeft_toLeftOf, 25);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintLeft_toRightOf, 26);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintRight_toLeftOf, 29);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintRight_toRightOf, 30);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintTop_toTopOf, 36);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintTop_toBottomOf, 35);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintBottom_toTopOf, 4);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintBottom_toBottomOf, 3);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintBaseline_toBaselineOf, 1);
        mapToConstant.append(R.styleable.ConstraintSet_layout_editor_absoluteX, 6);
        mapToConstant.append(R.styleable.ConstraintSet_layout_editor_absoluteY, 7);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintGuide_begin, 17);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintGuide_end, 18);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintGuide_percent, 19);
        mapToConstant.append(R.styleable.ConstraintSet_android_orientation, 27);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintStart_toEndOf, 32);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintStart_toStartOf, 33);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintEnd_toStartOf, 10);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintEnd_toEndOf, 9);
        mapToConstant.append(R.styleable.ConstraintSet_layout_goneMarginLeft, 13);
        mapToConstant.append(R.styleable.ConstraintSet_layout_goneMarginTop, 16);
        mapToConstant.append(R.styleable.ConstraintSet_layout_goneMarginRight, 14);
        mapToConstant.append(R.styleable.ConstraintSet_layout_goneMarginBottom, 11);
        mapToConstant.append(R.styleable.ConstraintSet_layout_goneMarginStart, 15);
        mapToConstant.append(R.styleable.ConstraintSet_layout_goneMarginEnd, 12);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintVertical_weight, 40);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintHorizontal_weight, 39);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintHorizontal_chainStyle, 41);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintVertical_chainStyle, 42);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintHorizontal_bias, 20);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintVertical_bias, 37);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintDimensionRatio, 5);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintLeft_creator, 61);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintTop_creator, 61);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintRight_creator, 61);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintBottom_creator, 61);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintBaseline_creator, 61);
        mapToConstant.append(R.styleable.ConstraintSet_android_layout_marginLeft, 24);
        mapToConstant.append(R.styleable.ConstraintSet_android_layout_marginRight, 28);
        mapToConstant.append(R.styleable.ConstraintSet_android_layout_marginStart, 31);
        mapToConstant.append(R.styleable.ConstraintSet_android_layout_marginEnd, 8);
        mapToConstant.append(R.styleable.ConstraintSet_android_layout_marginTop, 34);
        mapToConstant.append(R.styleable.ConstraintSet_android_layout_marginBottom, 2);
        mapToConstant.append(R.styleable.ConstraintSet_android_layout_width, 23);
        mapToConstant.append(R.styleable.ConstraintSet_android_layout_height, 21);
        mapToConstant.append(R.styleable.ConstraintSet_android_visibility, 22);
        mapToConstant.append(R.styleable.ConstraintSet_android_alpha, 43);
        mapToConstant.append(R.styleable.ConstraintSet_android_elevation, 44);
        mapToConstant.append(R.styleable.ConstraintSet_android_rotationX, 45);
        mapToConstant.append(R.styleable.ConstraintSet_android_rotationY, 46);
        mapToConstant.append(R.styleable.ConstraintSet_android_rotation, 60);
        mapToConstant.append(R.styleable.ConstraintSet_android_scaleX, 47);
        mapToConstant.append(R.styleable.ConstraintSet_android_scaleY, 48);
        mapToConstant.append(R.styleable.ConstraintSet_android_transformPivotX, 49);
        mapToConstant.append(R.styleable.ConstraintSet_android_transformPivotY, 50);
        mapToConstant.append(R.styleable.ConstraintSet_android_translationX, 51);
        mapToConstant.append(R.styleable.ConstraintSet_android_translationY, 52);
        mapToConstant.append(R.styleable.ConstraintSet_android_translationZ, 53);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintWidth_default, 54);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintHeight_default, 55);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintWidth_max, 56);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintHeight_max, 57);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintWidth_min, 58);
        mapToConstant.append(R.styleable.ConstraintSet_layout_constraintHeight_min, 59);
        mapToConstant.append(R.styleable.ConstraintSet_android_id, 38);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class Constraint {
        public float alpha;
        public boolean applyElevation;
        public int baselineToBaseline;
        public int bottomMargin;
        public int bottomToBottom;
        public int bottomToTop;
        public String dimensionRatio;
        public int editorAbsoluteX;
        public int editorAbsoluteY;
        public float elevation;
        public int endMargin;
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
        public int heightDefault;
        public int heightMax;
        public int heightMin;
        public float horizontalBias;
        public int horizontalChainStyle;
        public float horizontalWeight;
        public int leftMargin;
        public int leftToLeft;
        public int leftToRight;
        public int mBarrierDirection;
        public int mHeight;
        public int mHelperType;
        boolean mIsGuideline;
        public int[] mReferenceIds;
        int mViewId;
        public int mWidth;
        public int orientation;
        public int rightMargin;
        public int rightToLeft;
        public int rightToRight;
        public float rotation;
        public float rotationX;
        public float rotationY;
        public float scaleX;
        public float scaleY;
        public int startMargin;
        public int startToEnd;
        public int startToStart;
        public int topMargin;
        public int topToBottom;
        public int topToTop;
        public float transformPivotX;
        public float transformPivotY;
        public float translationX;
        public float translationY;
        public float translationZ;
        public float verticalBias;
        public int verticalChainStyle;
        public float verticalWeight;
        public int visibility;
        public int widthDefault;
        public int widthMax;
        public int widthMin;

        private Constraint() {
            this.mIsGuideline = false;
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
            this.horizontalBias = 0.5f;
            this.verticalBias = 0.5f;
            this.dimensionRatio = null;
            this.editorAbsoluteX = -1;
            this.editorAbsoluteY = -1;
            this.orientation = -1;
            this.leftMargin = -1;
            this.rightMargin = -1;
            this.topMargin = -1;
            this.bottomMargin = -1;
            this.endMargin = -1;
            this.startMargin = -1;
            this.visibility = 0;
            this.goneLeftMargin = -1;
            this.goneTopMargin = -1;
            this.goneRightMargin = -1;
            this.goneBottomMargin = -1;
            this.goneEndMargin = -1;
            this.goneStartMargin = -1;
            this.verticalWeight = 0.0f;
            this.horizontalWeight = 0.0f;
            this.horizontalChainStyle = 0;
            this.verticalChainStyle = 0;
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
            this.widthDefault = -1;
            this.heightDefault = -1;
            this.widthMax = -1;
            this.heightMax = -1;
            this.widthMin = -1;
            this.heightMin = -1;
            this.mBarrierDirection = -1;
            this.mHelperType = -1;
        }

        /* renamed from: clone */
        public Constraint m0clone() {
            Constraint clone = new Constraint();
            clone.mIsGuideline = this.mIsGuideline;
            clone.mWidth = this.mWidth;
            clone.mHeight = this.mHeight;
            clone.guideBegin = this.guideBegin;
            clone.guideEnd = this.guideEnd;
            clone.guidePercent = this.guidePercent;
            clone.leftToLeft = this.leftToLeft;
            clone.leftToRight = this.leftToRight;
            clone.rightToLeft = this.rightToLeft;
            clone.rightToRight = this.rightToRight;
            clone.topToTop = this.topToTop;
            clone.topToBottom = this.topToBottom;
            clone.bottomToTop = this.bottomToTop;
            clone.bottomToBottom = this.bottomToBottom;
            clone.baselineToBaseline = this.baselineToBaseline;
            clone.startToEnd = this.startToEnd;
            clone.startToStart = this.startToStart;
            clone.endToStart = this.endToStart;
            clone.endToEnd = this.endToEnd;
            clone.horizontalBias = this.horizontalBias;
            clone.verticalBias = this.verticalBias;
            clone.dimensionRatio = this.dimensionRatio;
            clone.editorAbsoluteX = this.editorAbsoluteX;
            clone.editorAbsoluteY = this.editorAbsoluteY;
            clone.horizontalBias = this.horizontalBias;
            clone.horizontalBias = this.horizontalBias;
            clone.horizontalBias = this.horizontalBias;
            clone.horizontalBias = this.horizontalBias;
            clone.horizontalBias = this.horizontalBias;
            clone.orientation = this.orientation;
            clone.leftMargin = this.leftMargin;
            clone.rightMargin = this.rightMargin;
            clone.topMargin = this.topMargin;
            clone.bottomMargin = this.bottomMargin;
            clone.endMargin = this.endMargin;
            clone.startMargin = this.startMargin;
            clone.visibility = this.visibility;
            clone.goneLeftMargin = this.goneLeftMargin;
            clone.goneTopMargin = this.goneTopMargin;
            clone.goneRightMargin = this.goneRightMargin;
            clone.goneBottomMargin = this.goneBottomMargin;
            clone.goneEndMargin = this.goneEndMargin;
            clone.goneStartMargin = this.goneStartMargin;
            clone.verticalWeight = this.verticalWeight;
            clone.horizontalWeight = this.horizontalWeight;
            clone.horizontalChainStyle = this.horizontalChainStyle;
            clone.verticalChainStyle = this.verticalChainStyle;
            clone.alpha = this.alpha;
            clone.applyElevation = this.applyElevation;
            clone.elevation = this.elevation;
            clone.rotation = this.rotation;
            clone.rotationX = this.rotationX;
            clone.rotationY = this.rotationY;
            clone.scaleX = this.scaleX;
            clone.scaleY = this.scaleY;
            clone.transformPivotX = this.transformPivotX;
            clone.transformPivotY = this.transformPivotY;
            clone.translationX = this.translationX;
            clone.translationY = this.translationY;
            clone.translationZ = this.translationZ;
            clone.widthDefault = this.widthDefault;
            clone.heightDefault = this.heightDefault;
            clone.widthMax = this.widthMax;
            clone.heightMax = this.heightMax;
            clone.widthMin = this.widthMin;
            clone.heightMin = this.heightMin;
            clone.mBarrierDirection = this.mBarrierDirection;
            clone.mHelperType = this.mHelperType;
            if (this.mReferenceIds != null) {
                clone.mReferenceIds = Arrays.copyOf(this.mReferenceIds, this.mReferenceIds.length);
            }
            return clone;
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void fillFromConstraints(ConstraintHelper helper, int viewId, Constraints.LayoutParams param) {
            fillFromConstraints(viewId, param);
            if (helper instanceof Barrier) {
                this.mHelperType = 1;
                Barrier barrier = (Barrier) helper;
                this.mBarrierDirection = barrier.getType();
                this.mReferenceIds = barrier.getReferencedIds();
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void fillFromConstraints(int viewId, Constraints.LayoutParams param) {
            fillFrom(viewId, param);
            this.alpha = param.alpha;
            this.rotation = param.rotation;
            this.rotationX = param.rotationX;
            this.rotationY = param.rotationY;
            this.scaleX = param.scaleX;
            this.scaleY = param.scaleY;
            this.transformPivotX = param.transformPivotX;
            this.transformPivotY = param.transformPivotY;
            this.translationX = param.translationX;
            this.translationY = param.translationY;
            this.translationZ = param.translationZ;
            this.elevation = param.elevation;
            this.applyElevation = param.applyElevation;
        }

        private void fillFrom(int viewId, ConstraintLayout.LayoutParams param) {
            this.mViewId = viewId;
            this.leftToLeft = param.leftToLeft;
            this.leftToRight = param.leftToRight;
            this.rightToLeft = param.rightToLeft;
            this.rightToRight = param.rightToRight;
            this.topToTop = param.topToTop;
            this.topToBottom = param.topToBottom;
            this.bottomToTop = param.bottomToTop;
            this.bottomToBottom = param.bottomToBottom;
            this.baselineToBaseline = param.baselineToBaseline;
            this.startToEnd = param.startToEnd;
            this.startToStart = param.startToStart;
            this.endToStart = param.endToStart;
            this.endToEnd = param.endToEnd;
            this.horizontalBias = param.horizontalBias;
            this.verticalBias = param.verticalBias;
            this.dimensionRatio = param.dimensionRatio;
            this.editorAbsoluteX = param.editorAbsoluteX;
            this.editorAbsoluteY = param.editorAbsoluteY;
            this.orientation = param.orientation;
            this.guidePercent = param.guidePercent;
            this.guideBegin = param.guideBegin;
            this.guideEnd = param.guideEnd;
            this.mWidth = param.width;
            this.mHeight = param.height;
            this.leftMargin = param.leftMargin;
            this.rightMargin = param.rightMargin;
            this.topMargin = param.topMargin;
            this.bottomMargin = param.bottomMargin;
            this.verticalWeight = param.verticalWeight;
            this.horizontalWeight = param.horizontalWeight;
            this.verticalChainStyle = param.verticalChainStyle;
            this.horizontalChainStyle = param.horizontalChainStyle;
            this.widthDefault = param.matchConstraintDefaultWidth;
            this.heightDefault = param.matchConstraintDefaultHeight;
            this.widthMax = param.matchConstraintMaxWidth;
            this.heightMax = param.matchConstraintMaxHeight;
            this.widthMin = param.matchConstraintMinWidth;
            this.heightMin = param.matchConstraintMinHeight;
            int currentapiVersion = Build.VERSION.SDK_INT;
            if (currentapiVersion >= 17) {
                this.endMargin = param.getMarginEnd();
                this.startMargin = param.getMarginStart();
            }
        }

        public void applyTo(ConstraintLayout.LayoutParams param) {
            param.leftToLeft = this.leftToLeft;
            param.leftToRight = this.leftToRight;
            param.rightToLeft = this.rightToLeft;
            param.rightToRight = this.rightToRight;
            param.topToTop = this.topToTop;
            param.topToBottom = this.topToBottom;
            param.bottomToTop = this.bottomToTop;
            param.bottomToBottom = this.bottomToBottom;
            param.baselineToBaseline = this.baselineToBaseline;
            param.startToEnd = this.startToEnd;
            param.startToStart = this.startToStart;
            param.endToStart = this.endToStart;
            param.endToEnd = this.endToEnd;
            param.leftMargin = this.leftMargin;
            param.rightMargin = this.rightMargin;
            param.topMargin = this.topMargin;
            param.bottomMargin = this.bottomMargin;
            param.goneStartMargin = this.goneStartMargin;
            param.goneEndMargin = this.goneEndMargin;
            param.horizontalBias = this.horizontalBias;
            param.verticalBias = this.verticalBias;
            param.dimensionRatio = this.dimensionRatio;
            param.editorAbsoluteX = this.editorAbsoluteX;
            param.editorAbsoluteY = this.editorAbsoluteY;
            param.verticalWeight = this.verticalWeight;
            param.horizontalWeight = this.horizontalWeight;
            param.verticalChainStyle = this.verticalChainStyle;
            param.horizontalChainStyle = this.horizontalChainStyle;
            param.matchConstraintDefaultWidth = this.widthDefault;
            param.matchConstraintDefaultHeight = this.heightDefault;
            param.matchConstraintMaxWidth = this.widthMax;
            param.matchConstraintMaxHeight = this.heightMax;
            param.matchConstraintMinWidth = this.widthMin;
            param.matchConstraintMinHeight = this.heightMin;
            param.orientation = this.orientation;
            param.guidePercent = this.guidePercent;
            param.guideBegin = this.guideBegin;
            param.guideEnd = this.guideEnd;
            param.width = this.mWidth;
            param.height = this.mHeight;
            if (Build.VERSION.SDK_INT >= 17) {
                param.setMarginStart(this.startMargin);
                param.setMarginEnd(this.endMargin);
            }
            param.validate();
        }
    }

    public void clone(Constraints constraints) {
        int count = constraints.getChildCount();
        this.mConstraints.clear();
        for (int i = 0; i < count; i++) {
            View view = constraints.getChildAt(i);
            Constraints.LayoutParams param = (Constraints.LayoutParams) view.getLayoutParams();
            int id = view.getId();
            if (!this.mConstraints.containsKey(Integer.valueOf(id))) {
                this.mConstraints.put(Integer.valueOf(id), new Constraint());
            }
            Constraint constraint = this.mConstraints.get(Integer.valueOf(id));
            if (view instanceof ConstraintHelper) {
                ConstraintHelper helper = (ConstraintHelper) view;
                constraint.fillFromConstraints(helper, id, param);
            }
            constraint.fillFromConstraints(id, param);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void applyToInternal(ConstraintLayout constraintLayout) {
        int count = constraintLayout.getChildCount();
        HashSet<Integer> used = new HashSet<>(this.mConstraints.keySet());
        for (int i = 0; i < count; i++) {
            View view = constraintLayout.getChildAt(i);
            int id = view.getId();
            if (this.mConstraints.containsKey(Integer.valueOf(id))) {
                used.remove(Integer.valueOf(id));
                Constraint constraint = this.mConstraints.get(Integer.valueOf(id));
                if (constraint.mHelperType != -1 && constraint.mHelperType == 1) {
                    Barrier barrier = (Barrier) view;
                    barrier.setId(id);
                    barrier.setReferencedIds(constraint.mReferenceIds);
                    barrier.setType(constraint.mBarrierDirection);
                    constraint.applyTo(constraintLayout.generateDefaultLayoutParams());
                }
                ConstraintLayout.LayoutParams param = (ConstraintLayout.LayoutParams) view.getLayoutParams();
                constraint.applyTo(param);
                view.setLayoutParams(param);
                view.setVisibility(constraint.visibility);
                if (Build.VERSION.SDK_INT >= 17) {
                    view.setAlpha(constraint.alpha);
                    view.setRotation(constraint.rotation);
                    view.setRotationX(constraint.rotationX);
                    view.setRotationY(constraint.rotationY);
                    view.setScaleX(constraint.scaleX);
                    view.setScaleY(constraint.scaleY);
                    view.setPivotX(constraint.transformPivotX);
                    view.setPivotY(constraint.transformPivotY);
                    view.setTranslationX(constraint.translationX);
                    view.setTranslationY(constraint.translationY);
                    if (Build.VERSION.SDK_INT >= 21) {
                        view.setTranslationZ(constraint.translationZ);
                        if (constraint.applyElevation) {
                            view.setElevation(constraint.elevation);
                        }
                    }
                }
            }
        }
        Iterator<Integer> it = used.iterator();
        while (it.hasNext()) {
            Integer id2 = it.next();
            Constraint constraint2 = this.mConstraints.get(id2);
            if (constraint2.mHelperType != -1 && constraint2.mHelperType == 1) {
                Barrier barrier2 = new Barrier(constraintLayout.getContext());
                barrier2.setId(id2.intValue());
                barrier2.setReferencedIds(constraint2.mReferenceIds);
                barrier2.setType(constraint2.mBarrierDirection);
                ConstraintLayout.LayoutParams param2 = constraintLayout.generateDefaultLayoutParams();
                constraint2.applyTo(param2);
                constraintLayout.addView(barrier2, param2);
            }
            if (constraint2.mIsGuideline) {
                Guideline g = new Guideline(constraintLayout.getContext());
                g.setId(id2.intValue());
                ConstraintLayout.LayoutParams param3 = constraintLayout.generateDefaultLayoutParams();
                constraint2.applyTo(param3);
                constraintLayout.addView(g, param3);
            }
        }
    }

    public void load(Context context, int resourceId) {
        Resources res = context.getResources();
        XmlPullParser parser = res.getXml(resourceId);
        try {
            for (int eventType = parser.getEventType(); eventType != 1; eventType = parser.next()) {
                if (eventType == 0) {
                    parser.getName();
                } else {
                    switch (eventType) {
                        case 2:
                            String tagName = parser.getName();
                            Constraint constraint = fillFromAttributeList(context, Xml.asAttributeSet(parser));
                            if (tagName.equalsIgnoreCase("Guideline")) {
                                constraint.mIsGuideline = true;
                            }
                            this.mConstraints.put(Integer.valueOf(constraint.mViewId), constraint);
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (XmlPullParserException e2) {
            e2.printStackTrace();
        }
    }

    private static int lookupID(TypedArray a2, int index, int def) {
        int ret = a2.getResourceId(index, def);
        if (ret == -1) {
            return a2.getInt(index, -1);
        }
        return ret;
    }

    private Constraint fillFromAttributeList(Context context, AttributeSet attrs) {
        Constraint c = new Constraint();
        TypedArray a2 = context.obtainStyledAttributes(attrs, R.styleable.ConstraintSet);
        populateConstraint(c, a2);
        a2.recycle();
        return c;
    }

    private void populateConstraint(Constraint c, TypedArray a2) {
        int N = a2.getIndexCount();
        for (int i = 0; i < N; i++) {
            int attr = a2.getIndex(i);
            int i2 = mapToConstant.get(attr);
            switch (i2) {
                case 1:
                    c.baselineToBaseline = lookupID(a2, attr, c.baselineToBaseline);
                    continue;
                case 2:
                    c.bottomMargin = a2.getDimensionPixelSize(attr, c.bottomMargin);
                    continue;
                case 3:
                    c.bottomToBottom = lookupID(a2, attr, c.bottomToBottom);
                    continue;
                case 4:
                    c.bottomToTop = lookupID(a2, attr, c.bottomToTop);
                    continue;
                case 5:
                    c.dimensionRatio = a2.getString(attr);
                    continue;
                case 6:
                    c.editorAbsoluteX = a2.getDimensionPixelOffset(attr, c.editorAbsoluteX);
                    continue;
                case 7:
                    c.editorAbsoluteY = a2.getDimensionPixelOffset(attr, c.editorAbsoluteY);
                    continue;
                case 8:
                    c.endMargin = a2.getDimensionPixelSize(attr, c.endMargin);
                    continue;
                case 9:
                    c.bottomToTop = lookupID(a2, attr, c.endToEnd);
                    continue;
                case 10:
                    c.endToStart = lookupID(a2, attr, c.endToStart);
                    continue;
                case 11:
                    c.goneBottomMargin = a2.getDimensionPixelSize(attr, c.goneBottomMargin);
                    continue;
                case 12:
                    c.goneEndMargin = a2.getDimensionPixelSize(attr, c.goneEndMargin);
                    continue;
                case 13:
                    c.goneLeftMargin = a2.getDimensionPixelSize(attr, c.goneLeftMargin);
                    continue;
                case 14:
                    c.goneRightMargin = a2.getDimensionPixelSize(attr, c.goneRightMargin);
                    continue;
                case 15:
                    c.goneStartMargin = a2.getDimensionPixelSize(attr, c.goneStartMargin);
                    continue;
                case 16:
                    c.goneTopMargin = a2.getDimensionPixelSize(attr, c.goneTopMargin);
                    continue;
                case 17:
                    c.guideBegin = a2.getDimensionPixelOffset(attr, c.guideBegin);
                    continue;
                case 18:
                    c.guideEnd = a2.getDimensionPixelOffset(attr, c.guideEnd);
                    continue;
                case 19:
                    c.guidePercent = a2.getFloat(attr, c.guidePercent);
                    continue;
                case 20:
                    c.horizontalBias = a2.getFloat(attr, c.horizontalBias);
                    continue;
                case 21:
                    c.mHeight = a2.getLayoutDimension(attr, c.mHeight);
                    continue;
                case 22:
                    c.visibility = a2.getInt(attr, c.visibility);
                    c.visibility = VISIBILITY_FLAGS[c.visibility];
                    continue;
                case 23:
                    c.mWidth = a2.getLayoutDimension(attr, c.mWidth);
                    continue;
                case 24:
                    c.leftMargin = a2.getDimensionPixelSize(attr, c.leftMargin);
                    continue;
                case 25:
                    c.leftToLeft = lookupID(a2, attr, c.leftToLeft);
                    continue;
                case 26:
                    c.leftToRight = lookupID(a2, attr, c.leftToRight);
                    continue;
                case 27:
                    c.orientation = a2.getInt(attr, c.orientation);
                    continue;
                case 28:
                    c.rightMargin = a2.getDimensionPixelSize(attr, c.rightMargin);
                    continue;
                case 29:
                    c.rightToLeft = lookupID(a2, attr, c.rightToLeft);
                    continue;
                case 30:
                    c.rightToRight = lookupID(a2, attr, c.rightToRight);
                    continue;
                case 31:
                    c.startMargin = a2.getDimensionPixelSize(attr, c.startMargin);
                    continue;
                case 32:
                    c.startToEnd = lookupID(a2, attr, c.startToEnd);
                    continue;
                case 33:
                    c.startToStart = lookupID(a2, attr, c.startToStart);
                    continue;
                case 34:
                    c.topMargin = a2.getDimensionPixelSize(attr, c.topMargin);
                    continue;
                case 35:
                    c.topToBottom = lookupID(a2, attr, c.topToBottom);
                    continue;
                case 36:
                    c.topToTop = lookupID(a2, attr, c.topToTop);
                    continue;
                case 37:
                    c.verticalBias = a2.getFloat(attr, c.verticalBias);
                    continue;
                case 38:
                    c.mViewId = a2.getResourceId(attr, c.mViewId);
                    continue;
                case 39:
                    c.horizontalWeight = a2.getFloat(attr, c.horizontalWeight);
                    continue;
                case 40:
                    c.verticalWeight = a2.getFloat(attr, c.verticalWeight);
                    continue;
                case 41:
                    c.horizontalChainStyle = a2.getInt(attr, c.horizontalChainStyle);
                    continue;
                case 42:
                    c.verticalChainStyle = a2.getInt(attr, c.verticalChainStyle);
                    continue;
                case 43:
                    c.alpha = a2.getFloat(attr, c.alpha);
                    continue;
                case 44:
                    c.applyElevation = true;
                    c.elevation = a2.getDimension(attr, c.elevation);
                    continue;
                case 45:
                    break;
                case 46:
                    c.rotationY = a2.getFloat(attr, c.rotationY);
                    continue;
                case 47:
                    c.scaleX = a2.getFloat(attr, c.scaleX);
                    continue;
                case 48:
                    c.scaleY = a2.getFloat(attr, c.scaleY);
                    continue;
                case 49:
                    c.transformPivotX = a2.getFloat(attr, c.transformPivotX);
                    continue;
                case 50:
                    c.transformPivotY = a2.getFloat(attr, c.transformPivotY);
                    continue;
                case 51:
                    c.translationX = a2.getDimension(attr, c.translationX);
                    continue;
                case 52:
                    c.translationY = a2.getDimension(attr, c.translationY);
                    continue;
                case 53:
                    c.translationZ = a2.getDimension(attr, c.translationZ);
                    continue;
                default:
                    switch (i2) {
                        case 60:
                            c.rotation = a2.getFloat(attr, c.rotation);
                            break;
                        case 61:
                            Log.w("ConstraintSet", "unused attribute 0x" + Integer.toHexString(attr) + "   " + mapToConstant.get(attr));
                            break;
                        default:
                            Log.w("ConstraintSet", "Unknown attribute 0x" + Integer.toHexString(attr) + "   " + mapToConstant.get(attr));
                            continue;
                    }
            }
            c.rotationX = a2.getFloat(attr, c.rotationX);
        }
    }
}
