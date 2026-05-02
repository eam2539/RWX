package android.graphics;

import android.os.Parcel;
import android.os.Parcelable;

/* JADX INFO: loaded from: game-lib.jar:android/graphics/PointF.class */
public class PointF implements Parcelable {

    /* JADX INFO: renamed from: a */
    public float x;

    /* JADX INFO: renamed from: b */
    public float y;

    public PointF() {
    }

    public PointF(float f, float f2) {
        this.x = f;
        this.y = f2;
    }

    public final void a(float f, float f2) {
        this.x = f;
        this.y = f2;
    }

    public final void a(PointF pointF) {
        this.x = pointF.x;
        this.y = pointF.y;
    }

    public final void b(float f, float f2) {
        this.x += f;
        this.y += f2;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeFloat(this.x);
        parcel.writeFloat(this.y);
    }
}
