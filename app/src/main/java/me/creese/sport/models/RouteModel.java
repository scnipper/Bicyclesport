package me.creese.sport.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.creese.sport.App;

public class RouteModel implements Parcelable{
    private final double distance;
    private final boolean isFocusRoute;
    private final float calories;
    private boolean isMarker;
    private String name;
    private List<LatLng> points;
    private long time;
    private long timeRoute;

    public RouteModel(String name, List<LatLng> points, long time, double distance,
                      boolean isFocusRoute, long timeRoute, float calories, boolean isMarker) {
        this.isMarker = isMarker;
        this.calories = calories;
        this.timeRoute = timeRoute;
        this.name = name;
        this.points = points;
        this.time = time;
        this.distance = distance;
        this.isFocusRoute = isFocusRoute;
        //int id = App.get().
    }


    protected RouteModel(Parcel in) {
        distance = in.readDouble();
        isFocusRoute = in.readByte() != 0;
        name = in.readString();
        points = in.createTypedArrayList(LatLng.CREATOR);
        time = in.readLong();
        timeRoute = in.readLong();
        calories = in.readFloat();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(distance);
        dest.writeByte((byte) (isFocusRoute ? 1 : 0));
        dest.writeString(name);
        dest.writeTypedList(points);
        dest.writeLong(time);
        dest.writeLong(timeRoute);
        dest.writeFloat(calories);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RouteModel> CREATOR = new Creator<RouteModel>() {
        @Override
        public RouteModel createFromParcel(Parcel in) {
            return new RouteModel(in);
        }

        @Override
        public RouteModel[] newArray(int size) {
            return new RouteModel[size];
        }
    };

    public float getCalories() {
        return calories;
    }

    public String getName() {
        return name;
    }

    public List<LatLng> getPoints() {
        return points;
    }

    public long getTime() {
        return time;
    }


    public boolean isMarker() {
        return isMarker;
    }

    public double getDistance() {
        return distance;
    }

    public boolean isFocusRoute() {
        return isFocusRoute;
    }

    public long getTimeRoute() {
        return timeRoute;
    }
}
