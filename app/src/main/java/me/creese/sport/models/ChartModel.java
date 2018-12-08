package me.creese.sport.models;

public class ChartModel {
    private long time;
    private int calories;
    private int kilometr;
    private int idRide;

    public ChartModel(long time, int calories, int kilometr) {
        this.idRide = idRide;
        this.time = time;
        this.calories = calories;
        this.kilometr = kilometr;
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

    public int getKilometr() {
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
