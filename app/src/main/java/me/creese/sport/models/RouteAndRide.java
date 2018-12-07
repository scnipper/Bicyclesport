package me.creese.sport.models;

import android.os.Parcel;
import android.os.Parcelable;

public class RouteAndRide implements Parcelable {
    private RideModel rideModel;
    private RouteModel routeModel;

    public RouteAndRide(RideModel rideModel, RouteModel routeModel) {
        this.rideModel = rideModel;
        this.routeModel = routeModel;
    }

    protected RouteAndRide(Parcel in) {
        rideModel = in.readParcelable(RideModel.class.getClassLoader());
        routeModel = in.readParcelable(RouteModel.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(rideModel, flags);
        dest.writeParcelable(routeModel, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RouteAndRide> CREATOR = new Creator<RouteAndRide>() {
        @Override
        public RouteAndRide createFromParcel(Parcel in) {
            return new RouteAndRide(in);
        }

        @Override
        public RouteAndRide[] newArray(int size) {
            return new RouteAndRide[size];
        }
    };

    public RideModel getRideModel() {
        return rideModel;
    }

    public RouteModel getRouteModel() {
        return routeModel;
    }
}
