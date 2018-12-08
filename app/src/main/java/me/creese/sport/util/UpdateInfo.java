package me.creese.sport.util;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import me.creese.sport.App;
import me.creese.sport.R;
import me.creese.sport.data.ChartTable;
import me.creese.sport.data.RideTable;
import me.creese.sport.map.MapWork;
import me.creese.sport.map.Route;
import me.creese.sport.map.gps.Gps;
import me.creese.sport.models.ChartModel;
import me.creese.sport.models.RideModel;
import me.creese.sport.ui.activities.StartActivity;


public class UpdateInfo implements Runnable {

    private static final String TAG = UpdateInfo.class.getSimpleName();
    private static UpdateInfo inst;
    private final ArrayList<ChartModel> chartInfo;
    private RideModel rideModel;
    private StartActivity startActivity;
    private Timer timer;
    private MapWork mapWork;
    private boolean isStarted;
    private long time;
    private TextView speedView;
    private TextView distanceView;
    private TextView timeView;
    private TextView kallView;
    private double perKilometr;
    private boolean isLoadChart;


    private UpdateInfo() {
        time = 0;
        perKilometr = 1000;
        chartInfo = new ArrayList<>();
    }

    public static UpdateInfo get() {
        if (inst == null) {
            inst = new UpdateInfo();
        }

        return inst;
    }

    public static String formatTime(long time) {
        long tmp = time;
        int hour = (int) (time / 3600);
        tmp -= hour * 3600;
        int min = (int) (tmp / 60);
        int sec = (int) (tmp - min * 60);

        String hourText = hour < 10 ? "0" + hour : "" + hour;
        String minText = min < 10 ? "0" + min : "" + min;
        String secText = sec < 10 ? "0" + sec : "" + sec;


        return hourText + ":" + minText + ":" + secText;
    }

    private void createViews() {
        speedView = startActivity.findViewById(R.id.speed_view);
        distanceView = startActivity.findViewById(R.id.distance_view);
        timeView = startActivity.findViewById(R.id.time_view);
        kallView = startActivity.findViewById(R.id.kall_view);
    }

    public RideModel saveRide(int idRoute) {

        final SQLiteDatabase database = App.get().getData().getWritableDatabase();

        final ContentValues contentValues = new ContentValues();
        contentValues.put(RideTable.CAL, rideModel.getCalories());
        contentValues.put(RideTable.DISTANCE, rideModel.getDistance());
        contentValues.put(RideTable.MAX_SPEED, rideModel.getMaxSpeed());
        contentValues.put(RideTable.ID_ROUTE, idRoute);
        contentValues.put(RideTable.TIME_RIDE, rideModel.getTimeRide());

        final int idRide = (int) database.insert(RideTable.NAME_TABLE, null, contentValues);
        if (chartInfo.size() > 0) {
            isLoadChart = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    saveCharts(idRide, contentValues, database);
                }
            }).start();
        }


        rideModel.setIdRide(idRide);
        rideModel.setIdRoute(idRoute);

        return rideModel;
    }

    private void saveCharts(int idRide, ContentValues contentValues, SQLiteDatabase database) {

        contentValues.clear();
        for (ChartModel chartModel : chartInfo) {

            contentValues.put(ChartTable.CAL, chartModel.getCalories());
            contentValues.put(ChartTable.TIME, chartModel.getTime());
            contentValues.put(ChartTable.KM, chartModel.getKilometr());
            contentValues.put(ChartTable.ID_RIDE, idRide);
            database.insert(ChartTable.NAME_TABLE, null, contentValues);
        }
        isLoadChart = false;

    }

    public void start() {
        if (mapWork == null) mapWork = startActivity.getMapWork();
        rideModel = new RideModel();
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                UpdateInfo.this.run();
            }
        }, 0, 1000);

    }

    public void stop() {
        if (timer != null) {
            time = 0;
            timer.cancel();
            timer = null;
        }

    }

    private void updateViews() {

        Gps gps = mapWork.getGps();
        if (mapWork.getRoutes().size() > 0) {
            Route route = mapWork.getLastRoute();
            rideModel.setDistance(route.getDistance());
            rideModel.setCalories((int) route.calculateCalories(gps.getSpeed()));
            rideModel.setMaxSpeed((int) gps.getMaxSpeed());
            rideModel.setTimeRide(time);
            timeView.setText(formatTime(time));

            speedView.setText(((int) gps.getSpeed()) + " " + startActivity.getString(R.string.km_peer_hour));
            distanceView.setText(Route.makeDistance(rideModel.getDistance()));
            kallView.setText(rideModel.getCalories() + "");
        }
    }

    public void setStartActivity(StartActivity startActivity) {
        this.startActivity = startActivity;

        createViews();
    }


    public boolean isLoadChart() {
        return isLoadChart;
    }

    @Override
    public void run() {

        startActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateViews();
            }
        });

        if (rideModel.getDistance() >= perKilometr) {
            chartInfo.add(new ChartModel(time, rideModel.getCalories(), (int) (perKilometr / 1000)));
            perKilometr += 1000;
        }

        time++;
    }
}
