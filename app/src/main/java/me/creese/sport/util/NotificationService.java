package me.creese.sport.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import androidx.annotation.Nullable;
import me.creese.sport.R;
import me.creese.sport.map.Route;
import me.creese.sport.models.RideModel;
import me.creese.sport.ui.activities.StartActivity;

public class NotificationService extends Service {
    private static final String TAG = NotificationService.class.getSimpleName();
    private static final int NOTIFY_ID = 1354;
    public static final String ACTION_START_SERVICE = "start_service";
    public static final String ACTION_UPDATE_INFO = "update_info";
    public static final String ACTION_STOP_SERVICE = "stop_service";

    private NotificationCompat.Builder builderNotfication;
    private NotificationManagerCompat notificationManager;
    private static  boolean isServiceRunning;


    public void createNotification() {
        if (isServiceRunning) return;
        isServiceRunning = true;
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            notificationManager = NotificationManagerCompat.from(this);
            Intent intent = new Intent(this, StartActivity.class);
            intent.setAction(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            builderNotfication = new NotificationCompat.Builder(this);
            builderNotfication.setContentIntent(contentIntent).setSmallIcon(R.mipmap.ic_launcher);
        }
    }
    public void updateInfo(RideModel rideModel) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            builderNotfication.setContentText("Калории: " + rideModel.getCalories()).setOngoing(true).setContentTitle("Расстояние: " + Route.makeDistance(rideModel.getDistance()));

            Notification notification = builderNotfication.build();
            startForeground(NOTIFY_ID, notification);
        }
    }
    private void stopService() {
        stopForeground(true);
        stopSelf();
        isServiceRunning = false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotification();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            if(intent.getAction().equals(ACTION_START_SERVICE))
            createNotification();

            if(intent.getAction().equals(ACTION_UPDATE_INFO)) {
                updateInfo((RideModel) intent.getParcelableExtra(RideModel.class.getSimpleName()));
            }
            if(intent.getAction().equals(ACTION_STOP_SERVICE)) {
                stopService();
            }
        }
        else stopService();
        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        isServiceRunning = false;
    }
}
