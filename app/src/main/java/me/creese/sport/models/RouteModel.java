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

    private final boolean isFocusRoute;
    private int id;

    private boolean isMarker;
    private String name;
    private List<LatLng> points;
    private long time;


    public RouteModel(int id,String name, List<LatLng> points, long time, boolean isFocusRoute, boolean isMarker) {
        this.id = id;
        this.isMarker = isMarker;
        this.name = name;
        this.points = points;
        this.time = time;
        this.isFocusRoute = isFocusRoute;
        //int id = App.get().
    }


    protected RouteModel(Parcel in) {
        isFocusRoute = in.readByte() != 0;
        name = in.readString();
        points = in.createTypedArrayList(LatLng.CREATOR);
        time = in.readLong();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte((byte) (isFocusRoute ? 1 : 0));
        dest.writeString(name);
        dest.writeTypedList(points);
        dest.writeLong(time);
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

    public List<LatLng> getPoints() {
        return points;
    }

    public long getTime() {
        return time;
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
