package android.support.graphics.drawable;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.Keyframe;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.content.res.XmlResourceParser;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.os.Build;
import android.support.v4.content.res.TypedArrayUtils;
import android.support.v4.graphics.PathParser;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.util.Xml;
import android.view.InflateException;
import android.view.animation.Interpolator;
import java.io.IOException;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import xiaopeng.widget.BuildConfig;
/* loaded from: classes.dex */
public class AnimatorInflaterCompat {
    public static Animator loadAnimator(Context context, int id) throws Resources.NotFoundException {
        if (Build.VERSION.SDK_INT >= 24) {
            Animator objectAnimator = AnimatorInflater.loadAnimator(context, id);
            return objectAnimator;
        }
        Animator objectAnimator2 = loadAnimator(context, context.getResources(), context.getTheme(), id);
        return objectAnimator2;
    }

    public static Animator loadAnimator(Context context, Resources resources, Resources.Theme theme, int id) throws Resources.NotFoundException {
        return loadAnimator(context, resources, theme, id, 1.0f);
    }

    public static Animator loadAnimator(Context context, Resources resources, Resources.Theme theme, int id, float pathErrorScale) throws Resources.NotFoundException {
        XmlResourceParser parser = null;
        try {
            try {
                try {
                    parser = resources.getAnimation(id);
                    Animator animator = createAnimatorFromXml(context, resources, theme, parser, pathErrorScale);
                    return animator;
                } catch (IOException ex) {
                    Resources.NotFoundException rnf = new Resources.NotFoundException("Can't load animation resource ID #0x" + Integer.toHexString(id));
                    rnf.initCause(ex);
                    throw rnf;
                }
            } catch (XmlPullParserException ex2) {
                Resources.NotFoundException rnf2 = new Resources.NotFoundException("Can't load animation resource ID #0x" + Integer.toHexString(id));
                rnf2.initCause(ex2);
                throw rnf2;
            }
        } finally {
            if (parser != null) {
                parser.close();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: classes.dex */
    public static class PathDataEvaluator implements TypeEvaluator<PathParser.PathDataNode[]> {
        private PathParser.PathDataNode[] mNodeArray;

        private PathDataEvaluator() {
        }

        @Override // android.animation.TypeEvaluator
        public PathParser.PathDataNode[] evaluate(float fraction, PathParser.PathDataNode[] startPathData, PathParser.PathDataNode[] endPathData) {
            if (!PathParser.canMorph(startPathData, endPathData)) {
                throw new IllegalArgumentException("Can't interpolate between two incompatible pathData");
            }
            if (this.mNodeArray == null || !PathParser.canMorph(this.mNodeArray, startPathData)) {
                this.mNodeArray = PathParser.deepCopyNodes(startPathData);
            }
            for (int i = 0; i < startPathData.length; i++) {
                this.mNodeArray[i].interpolatePathDataNode(startPathData[i], endPathData[i], fraction);
            }
            return this.mNodeArray;
        }
    }

    private static PropertyValuesHolder getPVH(TypedArray styledAttributes, int valueType, int valueFromId, int valueToId, String propertyName) {
        int valueType2;
        PropertyValuesHolder returnValue;
        int valueTo;
        int valueTo2;
        int valueFrom;
        int valueTo3;
        int valueTo4;
        float valueTo5;
        PropertyValuesHolder ofFloat;
        float valueFrom2;
        float valueTo6;
        int toType;
        PropertyValuesHolder propertyValuesHolder;
        TypedValue tvFrom = styledAttributes.peekValue(valueFromId);
        boolean hasFrom = tvFrom != null;
        int fromType = hasFrom ? tvFrom.type : 0;
        TypedValue tvTo = styledAttributes.peekValue(valueToId);
        boolean hasTo = tvTo != null;
        int toType2 = hasTo ? tvTo.type : 0;
        if (valueType == 4) {
            if ((hasFrom && isColorType(fromType)) || (hasTo && isColorType(toType2))) {
                valueType2 = 3;
            } else {
                valueType2 = 0;
            }
        } else {
            valueType2 = valueType;
        }
        boolean getFloats = valueType2 == 0;
        if (valueType2 == 2) {
            String fromString = styledAttributes.getString(valueFromId);
            String toString = styledAttributes.getString(valueToId);
            PathParser.PathDataNode[] nodesFrom = PathParser.createNodesFromPathData(fromString);
            PathParser.PathDataNode[] nodesTo = PathParser.createNodesFromPathData(toString);
            if (nodesFrom == null && nodesTo == null) {
                toType = toType2;
                propertyValuesHolder = null;
            } else if (nodesFrom == null) {
                toType = toType2;
                propertyValuesHolder = null;
                if (nodesTo != null) {
                    returnValue = PropertyValuesHolder.ofObject(propertyName, new PathDataEvaluator(), nodesTo);
                }
            } else {
                TypeEvaluator evaluator = new PathDataEvaluator();
                if (nodesTo != null) {
                    if (PathParser.canMorph(nodesFrom, nodesTo)) {
                        returnValue = PropertyValuesHolder.ofObject(propertyName, evaluator, nodesFrom, nodesTo);
                        toType = toType2;
                    } else {
                        throw new InflateException(" Can't morph from " + fromString + " to " + toString);
                    }
                } else {
                    toType = toType2;
                    PropertyValuesHolder returnValue2 = PropertyValuesHolder.ofObject(propertyName, evaluator, nodesFrom);
                    returnValue = returnValue2;
                }
            }
            returnValue = propertyValuesHolder;
        } else {
            int toType3 = toType2;
            TypeEvaluator evaluator2 = valueType2 == 3 ? ArgbEvaluator.getInstance() : null;
            if (getFloats) {
                if (hasFrom) {
                    if (fromType == 5) {
                        valueFrom2 = styledAttributes.getDimension(valueFromId, 0.0f);
                    } else {
                        valueFrom2 = styledAttributes.getFloat(valueFromId, 0.0f);
                    }
                    if (!hasTo) {
                        ofFloat = PropertyValuesHolder.ofFloat(propertyName, valueFrom2);
                    } else {
                        if (toType3 == 5) {
                            valueTo6 = styledAttributes.getDimension(valueToId, 0.0f);
                        } else {
                            valueTo6 = styledAttributes.getFloat(valueToId, 0.0f);
                        }
                        PropertyValuesHolder returnValue3 = PropertyValuesHolder.ofFloat(propertyName, valueFrom2, valueTo6);
                        returnValue = returnValue3;
                    }
                } else {
                    if (toType3 == 5) {
                        valueTo5 = styledAttributes.getDimension(valueToId, 0.0f);
                    } else {
                        valueTo5 = styledAttributes.getFloat(valueToId, 0.0f);
                    }
                    ofFloat = PropertyValuesHolder.ofFloat(propertyName, valueTo5);
                }
                returnValue = ofFloat;
            } else if (hasFrom) {
                if (fromType == 5) {
                    int valueFrom3 = (int) styledAttributes.getDimension(valueFromId, 0.0f);
                    valueFrom = valueFrom3;
                } else {
                    valueFrom = isColorType(fromType) ? styledAttributes.getColor(valueFromId, 0) : styledAttributes.getInt(valueFromId, 0);
                }
                int valueFrom4 = valueFrom;
                if (!hasTo) {
                    returnValue = PropertyValuesHolder.ofInt(propertyName, valueFrom4);
                } else {
                    if (toType3 == 5) {
                        int valueTo7 = (int) styledAttributes.getDimension(valueToId, 0.0f);
                        valueTo4 = valueTo7;
                        valueTo3 = 0;
                    } else if (isColorType(toType3)) {
                        valueTo3 = 0;
                        valueTo4 = styledAttributes.getColor(valueToId, 0);
                    } else {
                        valueTo3 = 0;
                        valueTo4 = styledAttributes.getInt(valueToId, 0);
                    }
                    int[] iArr = new int[2];
                    iArr[valueTo3] = valueFrom4;
                    iArr[1] = valueTo4;
                    returnValue = PropertyValuesHolder.ofInt(propertyName, iArr);
                }
            } else if (hasTo) {
                if (toType3 == 5) {
                    int valueTo8 = (int) styledAttributes.getDimension(valueToId, 0.0f);
                    valueTo2 = valueTo8;
                    valueTo = 0;
                } else if (isColorType(toType3)) {
                    valueTo = 0;
                    valueTo2 = styledAttributes.getColor(valueToId, 0);
                } else {
                    valueTo = 0;
                    valueTo2 = styledAttributes.getInt(valueToId, 0);
                }
                int[] iArr2 = new int[1];
                iArr2[valueTo] = valueTo2;
                returnValue = PropertyValuesHolder.ofInt(propertyName, iArr2);
            } else {
                returnValue = null;
            }
            if (returnValue != null && evaluator2 != null) {
                returnValue.setEvaluator(evaluator2);
            }
        }
        return returnValue;
    }

    private static void parseAnimatorFromTypeArray(ValueAnimator anim, TypedArray arrayAnimator, TypedArray arrayObjectAnimator, float pixelSize, XmlPullParser parser) {
        long duration = TypedArrayUtils.getNamedInt(arrayAnimator, parser, "duration", 1, 300);
        long startDelay = TypedArrayUtils.getNamedInt(arrayAnimator, parser, "startOffset", 2, 0);
        int valueType = TypedArrayUtils.getNamedInt(arrayAnimator, parser, "valueType", 7, 4);
        if (TypedArrayUtils.hasAttribute(parser, "valueFrom") && TypedArrayUtils.hasAttribute(parser, "valueTo")) {
            if (valueType == 4) {
                valueType = inferValueTypeFromValues(arrayAnimator, 5, 6);
            }
            PropertyValuesHolder pvh = getPVH(arrayAnimator, valueType, 5, 6, BuildConfig.FLAVOR);
            if (pvh != null) {
                anim.setValues(pvh);
            }
        }
        anim.setDuration(duration);
        anim.setStartDelay(startDelay);
        anim.setRepeatCount(TypedArrayUtils.getNamedInt(arrayAnimator, parser, "repeatCount", 3, 0));
        anim.setRepeatMode(TypedArrayUtils.getNamedInt(arrayAnimator, parser, "repeatMode", 4, 1));
        if (arrayObjectAnimator != null) {
            setupObjectAnimator(anim, arrayObjectAnimator, valueType, pixelSize, parser);
        }
    }

    private static void setupObjectAnimator(ValueAnimator anim, TypedArray arrayObjectAnimator, int valueType, float pixelSize, XmlPullParser parser) {
        ObjectAnimator oa = (ObjectAnimator) anim;
        String pathData = TypedArrayUtils.getNamedString(arrayObjectAnimator, parser, "pathData", 1);
        if (pathData != null) {
            String propertyXName = TypedArrayUtils.getNamedString(arrayObjectAnimator, parser, "propertyXName", 2);
            String propertyYName = TypedArrayUtils.getNamedString(arrayObjectAnimator, parser, "propertyYName", 3);
            if (valueType == 2 || valueType == 4) {
            }
            if (propertyXName == null && propertyYName == null) {
                throw new InflateException(arrayObjectAnimator.getPositionDescription() + " propertyXName or propertyYName is needed for PathData");
            }
            Path path = PathParser.createPathFromPathData(pathData);
            setupPathMotion(path, oa, 0.5f * pixelSize, propertyXName, propertyYName);
            return;
        }
        String propertyName = TypedArrayUtils.getNamedString(arrayObjectAnimator, parser, "propertyName", 0);
        oa.setPropertyName(propertyName);
    }

    private static void setupPathMotion(Path path, ObjectAnimator oa, float precision, String propertyXName, String propertyYName) {
        PathMeasure measureForTotalLength = new PathMeasure(path, false);
        float totalLength = 0.0f;
        ArrayList<Float> contourLengths = new ArrayList<>();
        contourLengths.add(Float.valueOf(0.0f));
        do {
            float pathLength = measureForTotalLength.getLength();
            totalLength += pathLength;
            contourLengths.add(Float.valueOf(totalLength));
        } while (measureForTotalLength.nextContour());
        PathMeasure pathMeasure = new PathMeasure(path, false);
        int numPoints = Math.min(100, ((int) (totalLength / precision)) + 1);
        float[] mX = new float[numPoints];
        float[] mY = new float[numPoints];
        float[] position = new float[2];
        float step = totalLength / (numPoints - 1);
        float currentDistance = 0.0f;
        int contourIndex = 0;
        int contourIndex2 = 0;
        while (true) {
            int i = contourIndex2;
            if (i >= numPoints) {
                break;
            }
            PathMeasure measureForTotalLength2 = measureForTotalLength;
            pathMeasure.getPosTan(currentDistance - contourLengths.get(contourIndex).floatValue(), position, null);
            mX[i] = position[0];
            mY[i] = position[1];
            currentDistance += step;
            if (contourIndex + 1 < contourLengths.size() && currentDistance > contourLengths.get(contourIndex + 1).floatValue()) {
                contourIndex++;
                pathMeasure.nextContour();
            }
            contourIndex2 = i + 1;
            measureForTotalLength = measureForTotalLength2;
        }
        PropertyValuesHolder y = null;
        PropertyValuesHolder x = propertyXName != null ? PropertyValuesHolder.ofFloat(propertyXName, mX) : null;
        if (propertyYName != null) {
            y = PropertyValuesHolder.ofFloat(propertyYName, mY);
        }
        if (x == null) {
            oa.setValues(y);
        } else if (y == null) {
            oa.setValues(x);
        } else {
            oa.setValues(x, y);
        }
    }

    private static Animator createAnimatorFromXml(Context context, Resources res, Resources.Theme theme, XmlPullParser parser, float pixelSize) throws XmlPullParserException, IOException {
        return createAnimatorFromXml(context, res, theme, parser, Xml.asAttributeSet(parser), null, 0, pixelSize);
    }

    /* JADX WARN: Code restructure failed: missing block: B:42:0x00fe, code lost:
        if (r25 == null) goto L18;
     */
    /* JADX WARN: Code restructure failed: missing block: B:43:0x0100, code lost:
        if (r12 == null) goto L18;
     */
    /* JADX WARN: Code restructure failed: missing block: B:44:0x0102, code lost:
        r0 = new android.animation.Animator[r12.size()];
        r2 = 0;
        r3 = r12.iterator();
     */
    /* JADX WARN: Code restructure failed: missing block: B:46:0x0111, code lost:
        if (r3.hasNext() == false) goto L14;
     */
    /* JADX WARN: Code restructure failed: missing block: B:47:0x0113, code lost:
        r0[r2] = r3.next();
        r2 = r2 + 1;
     */
    /* JADX WARN: Code restructure failed: missing block: B:48:0x0120, code lost:
        if (r26 != 0) goto L17;
     */
    /* JADX WARN: Code restructure failed: missing block: B:49:0x0122, code lost:
        r25.playTogether(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:50:0x0126, code lost:
        r25.playSequentially(r0);
     */
    /* JADX WARN: Code restructure failed: missing block: B:51:0x0129, code lost:
        return r7;
     */
    /* JADX WARN: Removed duplicated region for block: B:36:0x00cf  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct add '--show-bad-code' argument
    */
    private static android.animation.Animator createAnimatorFromXml(android.content.Context r20, android.content.res.Resources r21, android.content.res.Resources.Theme r22, org.xmlpull.v1.XmlPullParser r23, android.util.AttributeSet r24, android.animation.AnimatorSet r25, int r26, float r27) throws org.xmlpull.v1.XmlPullParserException, java.io.IOException {
        /*
            Method dump skipped, instructions count: 298
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.graphics.drawable.AnimatorInflaterCompat.createAnimatorFromXml(android.content.Context, android.content.res.Resources, android.content.res.Resources$Theme, org.xmlpull.v1.XmlPullParser, android.util.AttributeSet, android.animation.AnimatorSet, int, float):android.animation.Animator");
    }

    private static PropertyValuesHolder[] loadValues(Context context, Resources res, Resources.Theme theme, XmlPullParser parser, AttributeSet attrs) throws XmlPullParserException, IOException {
        ArrayList<PropertyValuesHolder> values;
        ArrayList<PropertyValuesHolder> values2 = null;
        while (true) {
            values = values2;
            int type = parser.getEventType();
            if (type == 3 || type == 1) {
                break;
            } else if (type != 2) {
                parser.next();
                values2 = values;
            } else {
                String name = parser.getName();
                if (name.equals("propertyValuesHolder")) {
                    TypedArray a2 = TypedArrayUtils.obtainAttributes(res, theme, attrs, AndroidResources.STYLEABLE_PROPERTY_VALUES_HOLDER);
                    String propertyName = TypedArrayUtils.getNamedString(a2, parser, "propertyName", 3);
                    int valueType = TypedArrayUtils.getNamedInt(a2, parser, "valueType", 2, 4);
                    PropertyValuesHolder pvh = loadPvh(context, res, theme, parser, propertyName, valueType);
                    if (pvh == null) {
                        pvh = getPVH(a2, valueType, 0, 1, propertyName);
                    }
                    if (pvh != null) {
                        if (values == null) {
                            values = new ArrayList<>();
                        }
                        values.add(pvh);
                    }
                    a2.recycle();
                }
                values2 = values;
                parser.next();
            }
        }
        PropertyValuesHolder[] valuesArray = null;
        if (values != null) {
            int count = values.size();
            valuesArray = new PropertyValuesHolder[count];
            for (int i = 0; i < count; i++) {
                valuesArray[i] = values.get(i);
            }
        }
        return valuesArray;
    }

    private static int inferValueTypeOfKeyframe(Resources res, Resources.Theme theme, AttributeSet attrs, XmlPullParser parser) {
        TypedArray a2 = TypedArrayUtils.obtainAttributes(res, theme, attrs, AndroidResources.STYLEABLE_KEYFRAME);
        int valueType = 0;
        TypedValue keyframeValue = TypedArrayUtils.peekNamedValue(a2, parser, "value", 0);
        boolean hasValue = keyframeValue != null;
        if (hasValue && isColorType(keyframeValue.type)) {
            valueType = 3;
        }
        a2.recycle();
        return valueType;
    }

    private static int inferValueTypeFromValues(TypedArray styledAttributes, int valueFromId, int valueToId) {
        TypedValue tvFrom = styledAttributes.peekValue(valueFromId);
        boolean hasFrom = tvFrom != null;
        int fromType = hasFrom ? tvFrom.type : 0;
        TypedValue tvTo = styledAttributes.peekValue(valueToId);
        boolean hasTo = tvTo != null;
        int toType = hasTo ? tvTo.type : 0;
        return ((hasFrom && isColorType(fromType)) || (hasTo && isColorType(toType))) ? 3 : 0;
    }

    private static PropertyValuesHolder loadPvh(Context context, Resources res, Resources.Theme theme, XmlPullParser parser, String propertyName, int valueType) throws XmlPullParserException, IOException {
        int type;
        PropertyValuesHolder value;
        ArrayList<Keyframe> keyframes;
        int type2;
        float f;
        Resources resources;
        Resources.Theme theme2;
        XmlPullParser xmlPullParser;
        PropertyValuesHolder value2 = null;
        ArrayList<Keyframe> keyframes2 = null;
        int valueType2 = valueType;
        while (true) {
            int next = parser.next();
            type = next;
            if (next == 3 || type == 1) {
                break;
            }
            String name = parser.getName();
            if (name.equals("keyframe")) {
                if (valueType2 == 4) {
                    resources = res;
                    theme2 = theme;
                    xmlPullParser = parser;
                    valueType2 = inferValueTypeOfKeyframe(resources, theme2, Xml.asAttributeSet(parser), xmlPullParser);
                } else {
                    resources = res;
                    theme2 = theme;
                    xmlPullParser = parser;
                }
                Keyframe keyframe = loadKeyframe(context, resources, theme2, Xml.asAttributeSet(parser), valueType2, xmlPullParser);
                if (keyframe != null) {
                    if (keyframes2 == null) {
                        keyframes2 = new ArrayList<>();
                    }
                    keyframes2.add(keyframe);
                }
                parser.next();
            }
        }
        if (keyframes2 != null) {
            int size = keyframes2.size();
            int count = size;
            if (size > 0) {
                int i = 0;
                Keyframe firstKeyframe = keyframes2.get(0);
                Keyframe lastKeyframe = keyframes2.get(count - 1);
                float endFraction = lastKeyframe.getFraction();
                float f2 = 0.0f;
                if (endFraction < 1.0f) {
                    if (endFraction >= 0.0f) {
                        keyframes2.add(keyframes2.size(), createNewKeyframe(lastKeyframe, 1.0f));
                        count++;
                    } else {
                        lastKeyframe.setFraction(1.0f);
                    }
                }
                float startFraction = firstKeyframe.getFraction();
                if (startFraction != 0.0f) {
                    if (startFraction >= 0.0f) {
                        keyframes2.add(0, createNewKeyframe(firstKeyframe, 0.0f));
                        count++;
                    } else {
                        firstKeyframe.setFraction(0.0f);
                    }
                }
                Keyframe[] keyframeArray = new Keyframe[count];
                keyframes2.toArray(keyframeArray);
                while (i < count) {
                    Keyframe keyframe2 = keyframeArray[i];
                    if (keyframe2.getFraction() >= f2) {
                        value = value2;
                        keyframes = keyframes2;
                        type2 = type;
                        f = f2;
                    } else if (i == 0) {
                        keyframe2.setFraction(f2);
                        value = value2;
                        keyframes = keyframes2;
                        type2 = type;
                        f = f2;
                    } else if (i == count - 1) {
                        keyframe2.setFraction(1.0f);
                        value = value2;
                        keyframes = keyframes2;
                        type2 = type;
                        f = 0.0f;
                    } else {
                        int startIndex = i;
                        int endIndex = i;
                        int j = startIndex + 1;
                        value = value2;
                        int endIndex2 = endIndex;
                        while (true) {
                            int j2 = j;
                            keyframes = keyframes2;
                            type2 = type;
                            if (j2 < count - 1) {
                                f = 0.0f;
                                if (keyframeArray[j2].getFraction() >= 0.0f) {
                                    break;
                                }
                                endIndex2 = j2;
                                j = j2 + 1;
                                keyframes2 = keyframes;
                                type = type2;
                            } else {
                                f = 0.0f;
                                break;
                            }
                        }
                        float gap = keyframeArray[endIndex2 + 1].getFraction() - keyframeArray[startIndex - 1].getFraction();
                        distributeKeyframes(keyframeArray, gap, startIndex, endIndex2);
                    }
                    i++;
                    f2 = f;
                    value2 = value;
                    keyframes2 = keyframes;
                    type = type2;
                }
                PropertyValuesHolder value3 = PropertyValuesHolder.ofKeyframe(propertyName, keyframeArray);
                if (valueType2 == 3) {
                    value3.setEvaluator(ArgbEvaluator.getInstance());
                    return value3;
                }
                return value3;
            }
        }
        return null;
    }

    private static Keyframe createNewKeyframe(Keyframe sampleKeyframe, float fraction) {
        if (sampleKeyframe.getType() == Float.TYPE) {
            return Keyframe.ofFloat(fraction);
        }
        if (sampleKeyframe.getType() == Integer.TYPE) {
            return Keyframe.ofInt(fraction);
        }
        return Keyframe.ofObject(fraction);
    }

    private static void distributeKeyframes(Keyframe[] keyframes, float gap, int startIndex, int endIndex) {
        int count = (endIndex - startIndex) + 2;
        float increment = gap / count;
        for (int i = startIndex; i <= endIndex; i++) {
            keyframes[i].setFraction(keyframes[i - 1].getFraction() + increment);
        }
    }

    private static Keyframe loadKeyframe(Context context, Resources res, Resources.Theme theme, AttributeSet attrs, int valueType, XmlPullParser parser) throws XmlPullParserException, IOException {
        TypedArray a2 = TypedArrayUtils.obtainAttributes(res, theme, attrs, AndroidResources.STYLEABLE_KEYFRAME);
        Keyframe keyframe = null;
        float fraction = TypedArrayUtils.getNamedFloat(a2, parser, "fraction", 3, -1.0f);
        TypedValue keyframeValue = TypedArrayUtils.peekNamedValue(a2, parser, "value", 0);
        boolean hasValue = keyframeValue != null;
        if (valueType == 4) {
            if (hasValue && isColorType(keyframeValue.type)) {
                valueType = 3;
            } else {
                valueType = 0;
            }
        }
        if (hasValue) {
            if (valueType != 3) {
                switch (valueType) {
                    case 0:
                        float value = TypedArrayUtils.getNamedFloat(a2, parser, "value", 0, 0.0f);
                        keyframe = Keyframe.ofFloat(fraction, value);
                        break;
                }
            }
            int intValue = TypedArrayUtils.getNamedInt(a2, parser, "value", 0, 0);
            keyframe = Keyframe.ofInt(fraction, intValue);
        } else {
            keyframe = valueType == 0 ? Keyframe.ofFloat(fraction) : Keyframe.ofInt(fraction);
        }
        int resID = TypedArrayUtils.getNamedResourceId(a2, parser, "interpolator", 1, 0);
        if (resID > 0) {
            Interpolator interpolator = AnimationUtilsCompat.loadInterpolator(context, resID);
            keyframe.setInterpolator(interpolator);
        }
        a2.recycle();
        return keyframe;
    }

    private static ObjectAnimator loadObjectAnimator(Context context, Resources res, Resources.Theme theme, AttributeSet attrs, float pathErrorScale, XmlPullParser parser) throws Resources.NotFoundException {
        ObjectAnimator anim = new ObjectAnimator();
        loadAnimator(context, res, theme, attrs, anim, pathErrorScale, parser);
        return anim;
    }

    private static ValueAnimator loadAnimator(Context context, Resources res, Resources.Theme theme, AttributeSet attrs, ValueAnimator anim, float pathErrorScale, XmlPullParser parser) throws Resources.NotFoundException {
        TypedArray arrayAnimator = TypedArrayUtils.obtainAttributes(res, theme, attrs, AndroidResources.STYLEABLE_ANIMATOR);
        TypedArray arrayObjectAnimator = TypedArrayUtils.obtainAttributes(res, theme, attrs, AndroidResources.STYLEABLE_PROPERTY_ANIMATOR);
        if (anim == null) {
            anim = new ValueAnimator();
        }
        parseAnimatorFromTypeArray(anim, arrayAnimator, arrayObjectAnimator, pathErrorScale, parser);
        int resID = TypedArrayUtils.getNamedResourceId(arrayAnimator, parser, "interpolator", 0, 0);
        if (resID > 0) {
            Interpolator interpolator = AnimationUtilsCompat.loadInterpolator(context, resID);
            anim.setInterpolator(interpolator);
        }
        arrayAnimator.recycle();
        if (arrayObjectAnimator != null) {
            arrayObjectAnimator.recycle();
        }
        return anim;
    }

    private static boolean isColorType(int type) {
        return type >= 28 && type <= 31;
    }
}
