package me.creese.sport.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import me.creese.sport.App;

public class RouteModel {
    private String name;
    private List<LatLng> points;
    private long time;

    public RouteModel(String name, List<LatLng> points, long time) {
        this.name = name;
        this.points = points;
        this.time = time;
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




}
