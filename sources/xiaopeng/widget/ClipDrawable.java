package xiaopeng.widget;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;
import android.util.AttributeSet;
import org.xmlpull.v1.XmlPullParser;
/* loaded from: classes.dex */
public class ClipDrawable extends DrawableWrapper {
    private static final int MAX_LEVEL = 10000;
    private static final String TAG = "XClipDrawable";
    protected Path mClipPath;
    protected int mRadiusX;
    protected int mRadiusY;

    public ClipDrawable() {
        this(null);
    }

    public ClipDrawable(Drawable drawable) {
        super(drawable);
        init();
    }

    public static Path generateRoundRect(float f, float f2, float f3, float f4, float f5, float f6) {
        Path path = new Path();
        path.addRoundRect(f, f2, f3, f4, new float[]{0.0f, 0.0f, f5, f6, f5, f6, 0.0f, 0.0f}, Path.Direction.CW);
        return path;
    }

    private void inflateChildDrawable(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) {
        int depth = xmlPullParser.getDepth();
        Drawable drawable = null;
        while (true) {
            int next = xmlPullParser.next();
            if (next == 1 || (next == 3 && xmlPullParser.getDepth() <= depth)) {
                break;
            } else if (next == 2) {
                drawable = Drawable.createFromXmlInner(resources, xmlPullParser, attributeSet, theme);
            }
        }
        if (drawable != null) {
            setDrawable(drawable);
        }
    }

    private void init() {
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void draw(Canvas canvas) {
        Rect bounds = getBounds();
        int level = getLevel();
        int width = bounds.width();
        int i = width - (((10000 - level) * width) / MAX_LEVEL);
        int height = bounds.height();
        int i2 = bounds.left;
        int i3 = bounds.top;
        this.mClipPath = generateRoundRect(i2, i3, i2 + i, i3 + height, this.mRadiusX, this.mRadiusY);
        drawSliderRect(this.mClipPath, canvas);
    }

    protected void drawSliderRect(Path path, Canvas canvas) {
        Drawable drawable = getDrawable();
        canvas.save();
        canvas.clipPath(path);
        drawable.draw(canvas);
        canvas.restore();
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    public void inflate(Resources resources, XmlPullParser xmlPullParser, AttributeSet attributeSet, Resources.Theme theme) {
        super.inflate(resources, xmlPullParser, attributeSet, theme);
        TypedArray obtainStyledAttributes = theme != null ? theme.obtainStyledAttributes(attributeSet, R.styleable.ClipDrawable, 0, 0) : resources.obtainAttributes(attributeSet, R.styleable.ClipDrawable);
        Drawable drawable = obtainStyledAttributes.getDrawable(R.styleable.ClipDrawable_android_drawable);
        if (drawable != null) {
            setDrawable(drawable);
        } else {
            inflateChildDrawable(resources, xmlPullParser, attributeSet, theme);
        }
        this.mRadiusX = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.ClipDrawable_radius_x, 0);
        this.mRadiusY = obtainStyledAttributes.getDimensionPixelOffset(R.styleable.ClipDrawable_radius_y, 0);
        obtainStyledAttributes.recycle();
    }

    @Override // android.graphics.drawable.DrawableWrapper, android.graphics.drawable.Drawable
    protected boolean onLevelChange(int i) {
        super.onLevelChange(i);
        invalidateSelf();
        return true;
    }
}
