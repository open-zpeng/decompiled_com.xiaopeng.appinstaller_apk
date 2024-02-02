package com.xiaopeng.xui.vui.floatinglayer;

import android.util.Log;
import com.xiaopeng.xui.vui.floatinglayer.VuiFloatingLayer;
/* loaded from: classes.dex */
class a {
    /* JADX INFO: Access modifiers changed from: package-private */
    public static int[] a(int i, VuiFloatingLayer.LayerInfo layerInfo, int i2, int i3) {
        int[] iArr = new int[2];
        if (layerInfo == null) {
            return iArr;
        }
        Log.v("VuiFloatingLocation", layerInfo.toString());
        Log.v("VuiFloatingLocation", "view Width  " + i2 + " Height is " + i3);
        int[] iArr2 = layerInfo.location;
        int i4 = iArr2[0];
        if (i != 0) {
            if (i != 1) {
                return iArr;
            }
            int i5 = layerInfo.targetWidth;
            int i6 = ((i5 / 2) + i4) - (i2 / 2);
            int i7 = (iArr2[1] - i3) + 80;
            int i8 = i6 + layerInfo.mCenterOffsetX;
            if (i8 < i4 || i8 > i4 + i5) {
                Log.v("VuiFloatingLocation", "offset more or less than current view width");
            }
            Log.v("VuiFloatingLocation", " windows x:" + i8 + ":y " + i7);
            iArr[0] = i8;
            iArr[1] = i7;
            return iArr;
        }
        int i9 = ((layerInfo.targetWidth / 2) + i4) - (i2 / 2);
        int i10 = (iArr2[1] + (layerInfo.targetHeight / 2)) - (i3 / 2);
        Log.v("VuiFloatingLocation", " windows x:" + i9 + ":y " + i10 + "mCenterOffsetX  is" + layerInfo.mCenterOffsetX + " mCenterOffsetY is " + layerInfo.mCenterOffsetY);
        int i11 = i9 + layerInfo.mCenterOffsetX;
        int i12 = i10 + layerInfo.mCenterOffsetY;
        StringBuilder sb = new StringBuilder();
        sb.append("offsetStart  is");
        sb.append(i11);
        Log.v("VuiFloatingLocation", sb.toString());
        if (i11 < i4 || i11 > i4 + layerInfo.targetWidth) {
            Log.v("VuiFloatingLocation", "offset more or less than current view width");
        }
        iArr[0] = i11;
        iArr[1] = i12;
        return iArr;
    }
}
