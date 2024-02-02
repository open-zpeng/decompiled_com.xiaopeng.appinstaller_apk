package android.support.v4.app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import java.util.List;
import java.util.Map;
/* loaded from: classes.dex */
public abstract class SharedElementCallback {
    private Matrix mTempMatrix;

    /* loaded from: classes.dex */
    public interface OnSharedElementsReadyListener {
        void onSharedElementsReady();
    }

    public void onSharedElementStart(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
    }

    public void onSharedElementEnd(List<String> sharedElementNames, List<View> sharedElements, List<View> sharedElementSnapshots) {
    }

    public void onRejectSharedElements(List<View> rejectedSharedElements) {
    }

    public void onMapSharedElements(List<String> names, Map<String, View> sharedElements) {
    }

    public Parcelable onCaptureSharedElementSnapshot(View sharedElement, Matrix viewToGlobalMatrix, RectF screenBounds) {
        Bitmap bitmap;
        if (sharedElement instanceof ImageView) {
            ImageView imageView = (ImageView) sharedElement;
            Drawable d = imageView.getDrawable();
            Drawable bg = imageView.getBackground();
            if (d != null && bg == null && (bitmap = createDrawableBitmap(d)) != null) {
                Bundle bundle = new Bundle();
                bundle.putParcelable("sharedElement:snapshot:bitmap", bitmap);
                bundle.putString("sharedElement:snapshot:imageScaleType", imageView.getScaleType().toString());
                if (imageView.getScaleType() == ImageView.ScaleType.MATRIX) {
                    Matrix matrix = imageView.getImageMatrix();
                    float[] values = new float[9];
                    matrix.getValues(values);
                    bundle.putFloatArray("sharedElement:snapshot:imageMatrix", values);
                }
                return bundle;
            }
        }
        int bitmapWidth = Math.round(screenBounds.width());
        int bitmapHeight = Math.round(screenBounds.height());
        if (bitmapWidth <= 0 || bitmapHeight <= 0) {
            return null;
        }
        float scale = Math.min(1.0f, 1048576.0f / (bitmapWidth * bitmapHeight));
        int bitmapWidth2 = (int) (bitmapWidth * scale);
        int bitmapHeight2 = (int) (bitmapHeight * scale);
        if (this.mTempMatrix == null) {
            this.mTempMatrix = new Matrix();
        }
        this.mTempMatrix.set(viewToGlobalMatrix);
        this.mTempMatrix.postTranslate(-screenBounds.left, -screenBounds.top);
        this.mTempMatrix.postScale(scale, scale);
        Bitmap bitmap2 = Bitmap.createBitmap(bitmapWidth2, bitmapHeight2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap2);
        canvas.concat(this.mTempMatrix);
        sharedElement.draw(canvas);
        return bitmap2;
    }

    private static Bitmap createDrawableBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        if (width <= 0 || height <= 0) {
            return null;
        }
        float scale = Math.min(1.0f, 1048576.0f / (width * height));
        if ((drawable instanceof BitmapDrawable) && scale == 1.0f) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        int bitmapWidth = (int) (width * scale);
        int bitmapHeight = (int) (height * scale);
        Bitmap bitmap = Bitmap.createBitmap(bitmapWidth, bitmapHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Rect existingBounds = drawable.getBounds();
        int left = existingBounds.left;
        int top = existingBounds.top;
        int right = existingBounds.right;
        int bottom = existingBounds.bottom;
        drawable.setBounds(0, 0, bitmapWidth, bitmapHeight);
        drawable.draw(canvas);
        drawable.setBounds(left, top, right, bottom);
        return bitmap;
    }

    public View onCreateSnapshotView(Context context, Parcelable snapshot) {
        if (!(snapshot instanceof Bundle)) {
            if (!(snapshot instanceof Bitmap)) {
                return null;
            }
            ImageView view = new ImageView(context);
            view.setImageBitmap((Bitmap) snapshot);
            return view;
        }
        Bundle bundle = (Bundle) snapshot;
        Bitmap bitmap = (Bitmap) bundle.getParcelable("sharedElement:snapshot:bitmap");
        if (bitmap == null) {
            return null;
        }
        ImageView imageView = new ImageView(context);
        imageView.setImageBitmap(bitmap);
        imageView.setScaleType(ImageView.ScaleType.valueOf(bundle.getString("sharedElement:snapshot:imageScaleType")));
        if (imageView.getScaleType() != ImageView.ScaleType.MATRIX) {
            return imageView;
        }
        float[] values = bundle.getFloatArray("sharedElement:snapshot:imageMatrix");
        Matrix matrix = new Matrix();
        matrix.setValues(values);
        imageView.setImageMatrix(matrix);
        return imageView;
    }

    public void onSharedElementsArrived(List<String> sharedElementNames, List<View> sharedElements, OnSharedElementsReadyListener listener) {
        listener.onSharedElementsReady();
    }
}
