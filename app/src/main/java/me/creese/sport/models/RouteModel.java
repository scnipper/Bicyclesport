package me.creese.sport.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.creese.sport.App;
import me.creese.sport.map.Point;

public class RouteModel implements Parcelable{

    private final boolean isFocusRoute;
    private int id;

    private boolean isMarker;
    private String name;
    private List<Point> points;
    private long time;
    // not use in db calculate runtime
    private double tmpDistance;


    public RouteModel(int id, String name, List<Point> points, long time, boolean isFocusRoute, boolean isMarker) {
        this.id = id;
        this.isMarker = isMarker;
        this.name = name;
        this.points = points;
        this.time = time;
        this.isFocusRoute = isFocusRoute;
    }


    protected RouteModel(Parcel in) {
        isFocusRoute = in.readByte() != 0;
        name = in.readString();
        points = in.createTypedArrayList(Point.CREATOR);
        time = in.readLong();
        tmpDistance = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isFocusRoute ? 1 : 0));
        dest.writeString(name);
        dest.writeTypedList(points);
        dest.writeLong(time);
        dest.writeDouble(tmpDistance);
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


    public String getName() {
        return name;
    }

    public List<Point> getPoints() {
        return points;
    }

    public long getTime() {
        return time;
    }


    public double getTmpDistance() {
        return tmpDistance;
    }

    public void setTmpDistance(double tmpDistance) {
        this.tmpDistance = tmpDistance;
    }

    public boolean isMarker() {
        return isMarker;
    }

    public int getId() {
        return id;
    }

    public boolean isFocusRoute() {
        return isFocusRoute;
    }

    public void setId(int id) {
        this.id = id;
    }
}
