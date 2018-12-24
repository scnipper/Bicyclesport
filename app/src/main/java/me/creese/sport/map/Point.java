package me.creese.sport.map;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

public class Point implements Parcelable {
    public static final int DEFAULT_TYPE = 0;
    public static final int DASH_START = 1;
    public static final int DASH_END = 2;

    private LatLng latLng;
    private int typePoint = DEFAULT_TYPE;

    public Point(LatLng latLng) {
        this.latLng = latLng;
    }

    protected Point(Parcel in) {
        latLng = in.readParcelable(LatLng.class.getClassLoader());
        typePoint = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(latLng, flags);
        dest.writeInt(typePoint);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Point> CREATOR = new Creator<Point>() {
        @Override
        public Point createFromParcel(Parcel in) {
            return new Point(in);
        }

        @Override
        public Point[] newArray(int size) {
            return new Point[size];
        }
    };

    public LatLng getLatLng() {
        return latLng;
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public int getTypePoint() {
        return typePoint;
    }

    public void setTypePoint(int typePoint) {
        this.typePoint = typePoint;
    }
}
