package me.creese.sport.models;

import android.os.Parcel;
import android.os.Parcelable;

public class RideModel implements Parcelable {
    private int calories;
    private long timeRide;
    private int maxSpeed;
    private double distance;
    private int idRoute;
    private int idRide;
    private String rideAdress;

    public RideModel() {
    }

    public RideModel(int calories, long timeRide, int maxSpeed, double distance, int idRoute) {
        this.idRoute = idRoute;
        this.calories = calories;
        this.timeRide = timeRide;
        this.maxSpeed = maxSpeed;
        this.distance = distance;
    }

    protected RideModel(Parcel in) {
        calories = in.readInt();
        timeRide = in.readLong();
        maxSpeed = in.readInt();
        distance = in.readDouble();
        idRoute = in.readInt();
        idRide = in.readInt();
        rideAdress = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(calories);
        dest.writeLong(timeRide);
        dest.writeInt(maxSpeed);
        dest.writeDouble(distance);
        dest.writeInt(idRoute);
        dest.writeInt(idRide);
        dest.writeString(rideAdress);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RideModel> CREATOR = new Creator<RideModel>() {
        @Override
        public RideModel createFromParcel(Parcel in) {
            return new RideModel(in);
        }

        @Override
        public RideModel[] newArray(int size) {
            return new RideModel[size];
        }
    };

    public int getCalories() {
        return calories;
    }

    public long getTimeRide() {
        return timeRide;
    }

    public void setTimeRide(long timeRide) {
        this.timeRide = timeRide;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getIdRoute() {
        return idRoute;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setIdRoute(int idRoute) {
        this.idRoute = idRoute;
    }

    public int getIdRide() {
        return idRide;
    }

    public void setIdRide(int idRide) {
        this.idRide = idRide;
    }


    public String getRideAdress() {
        return rideAdress;
    }

    public void setRideAdress(String rideAdress) {
        this.rideAdress = rideAdress;
    }
}
