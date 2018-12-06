package me.creese.sport.util;

import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

import me.creese.sport.R;
import me.creese.sport.map.MapWork;
import me.creese.sport.map.Route;
import me.creese.sport.map.gps.Gps;
import me.creese.sport.ui.activities.StartActivity;


public class UpdateInfo implements Runnable {

    private static final String TAG = UpdateInfo.class.getSimpleName();
    private static UpdateInfo inst;
    private StartActivity startActivity;
    private Timer timer;
    private MapWork mapWork;
    private boolean isStarted;
    private long time;
    private TextView speedView;
    private TextView distanceView;
    private TextView timeView;
    private TextView kallView;


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

    private UpdateInfo() {
        time = 0;
    }

    private void createViews() {
        speedView = startActivity.findViewById(R.id.speed_view);
        distanceView = startActivity.findViewById(R.id.distance_view);
        timeView = startActivity.findViewById(R.id.time_view);
        kallView = startActivity.findViewById(R.id.kall_view);
    }

    public void start() {
        if (mapWork == null) mapWork = startActivity.getMapWork();

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
        if(mapWork.getRoutes().size() > 0) {
            Route route = mapWork.getRoutes().get(mapWork.getRoutes().size() - 1);


            timeView.setText(formatTime(time));

            speedView.setText(((int) gps.getSpeed()) + " " + startActivity.getString(R.string.km_peer_hour));
            distanceView.setText(Route.makeDistance(route.getDistance()));
            kallView.setText(((int) route.calculateCalories(gps.getSpeed())) + "");
        }
    }

    public void setStartActivity(StartActivity startActivity) {
        this.startActivity = startActivity;

        createViews();
    }

    public long getTime() {
        return time;
    }

    @Override
    public void run() {
        startActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateViews();
            }
        });
        time++;
    }
}