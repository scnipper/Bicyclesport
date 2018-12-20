package me.creese.sport.util;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import me.creese.sport.App;
import me.creese.sport.R;
import me.creese.sport.data.ChartTable;
import me.creese.sport.data.DataHelper;
import me.creese.sport.data.FullTable;
import me.creese.sport.data.RideTable;
import me.creese.sport.map.MapWork;
import me.creese.sport.map.Route;
import me.creese.sport.map.gps.Gps;
import me.creese.sport.models.ChartModel;
import me.creese.sport.models.RideModel;
import me.creese.sport.ui.activities.StartActivity;
import me.creese.sport.ui.fragments.MainViewStatFragment;


public class UpdateInfo implements Runnable {

    public static final int PER_KILOMETR = 1;
    public static final int PER_MINUTE = 2;
    private static final String TAG = UpdateInfo.class.getSimpleName();
    private static final int NOTIFY_ID = 1354;
    private static UpdateInfo inst;
    private final ArrayList<ChartModel> chartInfo;
    private int perTime;
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
    private NotificationCompat.Builder builderNotfication;
    private NotificationManagerCompat notificationManager;


    private UpdateInfo() {
        time = 0;
        perKilometr = 1000; // 1 km
        perTime = 60000; // 1 min
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

    public void createViews() {
        Fragment fragment = startActivity.getSupportFragmentManager().findFragmentByTag(MainViewStatFragment.class.getSimpleName());
        if (fragment != null) {
            speedView = fragment.getView().findViewById(R.id.speed_view);
            distanceView = fragment.getView().findViewById(R.id.distance_view);
            timeView = fragment.getView().findViewById(R.id.time_view);
            kallView = fragment.getView().findViewById(R.id.kall_view);
        }


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


        Cursor cursor = database.query(FullTable.NAME_TABLE, null, null, null, null, null, null);


        contentValues.clear();


        if (cursor.moveToFirst()) {

            int tmpCal = cursor.getInt(cursor.getColumnIndex(FullTable.CALORIES));
            long tmpTime = cursor.getLong(cursor.getColumnIndex(FullTable.TIME));
            double tmpDis = cursor.getInt(cursor.getColumnIndex(FullTable.DISTANCE));
            int tmpMaxSpeed = cursor.getInt(cursor.getColumnIndex(FullTable.MAX_SPEED));

            contentValues.put(FullTable.CALORIES, rideModel.getCalories() + tmpCal);
            contentValues.put(FullTable.TIME, rideModel.getTimeRide() + tmpTime);
            contentValues.put(FullTable.DISTANCE, rideModel.getDistance() + tmpDis);
            contentValues.put(FullTable.MAX_SPEED, rideModel.getMaxSpeed() > tmpMaxSpeed ? rideModel.getMaxSpeed() : tmpMaxSpeed);
            database.update(FullTable.NAME_TABLE, contentValues, DataHelper.ID + "=1", null);
        } else {
            contentValues.put(FullTable.CALORIES, rideModel.getCalories());
            contentValues.put(FullTable.TIME, rideModel.getTimeRide());
            contentValues.put(FullTable.DISTANCE, rideModel.getDistance());
            contentValues.put(FullTable.MAX_SPEED, rideModel.getMaxSpeed());
            database.insert(FullTable.NAME_TABLE, null, contentValues);
        }

        cursor.close();


        if (chartInfo.size() > 0) {

            saveCharts(idRide, contentValues, database);

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
            contentValues.put(ChartTable.TYPE, chartModel.getType());
            database.insert(ChartTable.NAME_TABLE, null, contentValues);
        }


    }

    public void start() {
        createViews();
        if (mapWork == null) mapWork = startActivity.getMapWork();
        rideModel = new RideModel();
        chartInfo.clear();
        resume();
    }

    public void resume() {
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                UpdateInfo.this.run();
            }
        }, 0, 1);
    }

    public void stop() {
        pause();
        time = 0;
        perKilometr = 1000;
        perTime = 60000;
        if (notificationManager != null) {
            notificationManager.cancel(NOTIFY_ID);
        }

    }

    public void pause() {
        if (timer != null) {
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
            rideModel.setTimeRide(time / 1000);
            timeView.setText(formatTime(time / 1000));


            speedView.setText(((int) gps.getSpeed()) + " " + startActivity.getString(R.string.km_peer_hour));
            distanceView.setText(Route.makeDistance(rideModel.getDistance()));
            kallView.setText(rideModel.getCalories() + "");
        }

        updateNotifications();
    }

    private void updateNotifications() {
        if (builderNotfication == null) {
            PendingIntent contentIntent = PendingIntent.getActivity(startActivity, 0, new Intent(startActivity, StartActivity.class), PendingIntent.FLAG_CANCEL_CURRENT);
            builderNotfication = new NotificationCompat.Builder(startActivity);
            builderNotfication.setContentIntent(contentIntent).setSmallIcon(R.mipmap.ic_launcher);
        }

        builderNotfication.setContentText("Калории: " + rideModel.getCalories()).setContentTitle("Расстояние: " + Route.makeDistance(rideModel.getDistance()));

        if (notificationManager == null)
            notificationManager = NotificationManagerCompat.from(startActivity);
        Notification notification = builderNotfication.build();
        notification.flags = notification.flags | Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(NOTIFY_ID, notification);
    }

    public void setStartActivity(StartActivity startActivity) {
        this.startActivity = startActivity;

    }


    @Override
    public void run() {
        if (time % 1000 == 0) {
            startActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateViews();
                }
            });

            if (rideModel.getDistance() >= perKilometr) {
                chartInfo.add(new ChartModel(time / 1000, rideModel.getCalories(), (int) (perKilometr / 1000), PER_KILOMETR));
                perKilometr += 1000;
            }

            if (time >= perTime) {
                chartInfo.add(new ChartModel(perTime / 60000, rideModel.getCalories(), rideModel.getDistance(), PER_MINUTE));
                perTime += 60000;
            }
        }

        time++;
    }
}
