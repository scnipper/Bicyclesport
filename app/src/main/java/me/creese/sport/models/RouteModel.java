package me.creese.sport.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.creese.sport.App;

public class RouteModel {
    private final double distance;
    private final boolean isFocusRoute;
    private String name;
    private List<LatLng> points;
    private long time;
    private long timeRoute;

    public RouteModel(String name, List<LatLng> points, long time, double distance, boolean isFocusRoute,long timeRoute) {
        this.timeRoute = timeRoute;
        this.name = name;
        this.points = points;
        this.time = time;
        this.distance = distance;
        this.isFocusRoute = isFocusRoute;
        //int id = App.get().
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
