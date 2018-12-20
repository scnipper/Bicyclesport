package me.creese.sport.models;

public class ChartModel {
    private int type;
    private long time;
    private int calories;
    private double kilometr;
    private int idRide;

    public ChartModel(long time, int calories, double kilometr,int type) {
        this.type = type;
        this.time = time;
        this.calories = calories;
        this.kilometr = kilometr;
    }


    public int getType() {
        return type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public double getKilometr() {
        return kilometr;
    }

    public void setKilometr(int kilometr) {
        this.kilometr = kilometr;
    }

    public int getIdRide() {
        return idRide;
    }

    public void setIdRide(int idRide) {
        this.idRide = idRide;
    }
}
