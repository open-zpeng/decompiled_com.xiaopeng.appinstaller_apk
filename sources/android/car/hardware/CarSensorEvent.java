package android.car.hardware;

import android.os.Parcel;
import android.os.Parcelable;
/* loaded from: classes.dex */
public class CarSensorEvent implements Parcelable {
    public static final Parcelable.Creator<CarSensorEvent> CREATOR = null;
    public final float[] floatValues = null;
    public final int[] intValues = null;
    public final long[] longValues = null;

    CarSensorEvent() {
        throw new RuntimeException("Stub!");
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        throw new RuntimeException("Stub!");
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        throw new RuntimeException("Stub!");
    }
}
