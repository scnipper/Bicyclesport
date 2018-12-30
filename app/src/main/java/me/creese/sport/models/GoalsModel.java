package me.creese.sport.models;

public class GoalsModel {
    public static final int DISTANCE = 0;
    public static final int CALORIES = 1;
    public static final int TIME = 2;
    private int id;

    private long time;
    private int count;
    private int type;
    private int passCount;

    public GoalsModel(long time, int count, int type,int id) {
        this.time = time;
        this.count = count;
        this.type = type;
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPassCount() {
        return passCount;
    }

    public void setPassCount(int passCount) {
        this.passCount = passCount;
    }
}
