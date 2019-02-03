package me.creese.sport.map.gps;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.IntentSender;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.ImageView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import me.creese.sport.App;
import me.creese.sport.R;
import me.creese.sport.map.MapWork;
import me.creese.sport.map.Route;
import me.creese.sport.ui.fragments.DialogFindGps;
import me.creese.sport.ui.activities.StartActivity;
import me.creese.sport.util.UpdateInfo;


@SuppressLint("MissingPermission")
public class Gps extends LocationCallback implements GpsStatus.Listener {
    private static final String TAG = "Gps";
    private static final long DELTA_TIME = 30 * 1000; // 30 sec
    private final LocationRequest locationRequest;
    private StartActivity context;
    private FusedLocationProviderClient client;
    private MapWork mapWork;
    private ImageView gpsStatusView;
    private GpsListener gpsListener;

    private boolean isStartWay;
    private boolean isFixGps;
    private float speed;

    private float maxSpeed;
    private boolean pause;
    private boolean autoPause;
    private boolean isAddListeners;


    public Gps(MapWork mapWork) {

        this.mapWork = mapWork;
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(500);


    }

    public void addListeners() {
        if (!isAddListeners) {
            isAddListeners = true;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                App.get().getLocationManager().registerGnssStatusCallback(new GNSSListener(this));
            } else {
                App.get().getLocationManager().addGpsStatusListener(this);
            }
        }
    }


    /**
     * Проверка на то что включен gps или нет
     */
    private void startGpsUpdate() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);


        LocationServices.getSettingsClient(context).checkLocationSettings(builder.build()).addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onComplete(Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    // All location settings are satisfied. The client can initialize location
                    // requests here.
                    isStartWay = true;
                    client.requestLocationUpdates(locationRequest, Gps.this, null);

                } catch (ApiException exception) {
                    isStartWay = false;
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.

                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(context, StartActivity.CHECK_GPS_ENABLED);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            AlertDialog.Builder b = new AlertDialog.Builder(context);

                            b.setMessage(R.string.no_gps_function)
                                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    }).show();
                            break;
                    }
                }
            }
        });
    }

    /**
     * Получение начальной позиции
     *
     * @param gpsListener
     */
    public void getStartPosition(final GpsListener gpsListener) {

        this.gpsListener = gpsListener;
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSuccess(Location location) {
                if (location == null) return;
                long time = System.currentTimeMillis() - location.getTime();
                if (time > DELTA_TIME) {
                    client.requestLocationUpdates(locationRequest, Gps.this, null);
                } else
                    gpsListener.whenFindStartPos(new LatLng(location.getLatitude(), location.getLongitude()));


            }
        });
    }

    /**
     * Обновление позиции и посторение пути
     */

    public void startUpdatePosition() {

        findAndDismisDialog();
        DialogFindGps dialogWait = new DialogFindGps();

        if (!dialogWait.isAdded())
            dialogWait.show(context.getSupportFragmentManager(), DialogFindGps.TAG);



        gpsListener = null;
        startGpsUpdate();

        for (Route route : mapWork.getRoutes()) {
            route.clearMarkers();
        }

    }

    /**
     * Остановка обновления позиции
     */
    public void stopUpdatePosition() {
        findAndDismisDialog();
        client.removeLocationUpdates(this);
    }


    /**
     * Обновление графического отображения сигнала
     *
     * @param levelSignal
     */
    public void updateSignal(float levelSignal) {
        if (gpsStatusView == null) {
            gpsStatusView = context.findViewById(R.id.gps_stauts);
        }

        if (levelSignal > 10) gpsStatusView.setImageResource(R.drawable.one_gps);
        if (levelSignal > 20) gpsStatusView.setImageResource(R.drawable.half_gps);
        if (levelSignal > 30) gpsStatusView.setImageResource(R.drawable.full_gps);
    }

    /**
     * Соединение со спутником
     */
    public void firstFixGps() {
        Log.w(TAG, "onGpsStatusChanged: gps first fix");
        if (!isStartWay) return;

        Fragment fragment = context.getSupportFragmentManager().findFragmentById(R.id.sub_content);
        if (fragment == null) {

            context.addMainView();
        }
        UpdateInfo.get().start();

        isFixGps = true;
        Route route = new Route(context);
        route.setColorLine(0xffff0000);
        route.setFocusRoute(true);
        mapWork.addRoute(route);
        findAndDismisDialog();

        if (mapWork.getStartMarker() != null) {
            mapWork.getStartMarker().remove();
        }

    }

    /**
     * Поиск и удаление диалога "поиск gps"
     */
    private void findAndDismisDialog() {
        FragmentManager fragmentManager = context.getSupportFragmentManager();
        DialogFindGps findGps = (DialogFindGps) fragmentManager.findFragmentByTag(DialogFindGps.TAG);
        if (findGps != null) {
            findGps.dismiss();
            fragmentManager.beginTransaction().remove(findGps).commit();
        }
    }

    public void setContext(StartActivity context) {
        this.context = context;
        client = LocationServices.getFusedLocationProviderClient(context);
    }

    public void setMapWork(MapWork mapWork) {
        this.mapWork = mapWork;
    }

    public boolean isStartWay() {
        return isStartWay;
    }


    public void onStartGps() {
        Log.w(TAG, "onGpsStatusChanged: gps event started");
    }

    public void stopedStatus() {
        Log.w(TAG, "onGpsStatusChanged: gps event stop");
        isStartWay = false;
        isFixGps = false;
        pause = false;
        UpdateInfo.get().stop();
        speed = 0;
        maxSpeed = 0;
    }

    public boolean isFirstFix() {
        return isFixGps;
    }

    public float getSpeed() {
        return speed;
    }

    public float getMaxSpeed() {
        return maxSpeed;
    }

    public boolean isPause() {
        return pause;
    }

    public void setPause(boolean pause) {
        this.pause = pause;
        if (pause) {
            autoPause = false;
            speed = 0;
        }
    }

    public void setAutoPause(boolean autoPause) {
        this.autoPause = autoPause;
    }

    /**
     * Обновление данных позиции со спутников
     *
     * @param locationResult
     */
    @Override
    public void onLocationResult(LocationResult locationResult) {
        Location location = locationResult.getLastLocation();
        if (pause || autoPause) {
            if (autoPause && location.getSpeed() > 0) {
                autoPause = false;
                UpdateInfo.get().resume();
            }

            return;
        }


        if (gpsListener != null) {
            gpsListener.whenFindStartPos(new LatLng(location.getLatitude(), location.getLongitude()));
            gpsListener = null;
            client.removeLocationUpdates(this);
        } else if (isFixGps) {
            Route lastRoute = mapWork.getRoutes().get(mapWork.getRoutes().size() - 1);
            LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
            lastRoute.addPointOnMap(point);


            speed = location.getSpeed() * 3.6f;
            if (speed > maxSpeed) maxSpeed = speed;


        }


    }

    @Override
    public void onLocationAvailability(LocationAvailability locationAvailability) {
        Log.w(TAG, "onLocationAvailability: " + locationAvailability.isLocationAvailable());
    }

    /**
     * Если API < 24
     *
     * @param event
     */

    @Override
    public void onGpsStatusChanged(int event) {
        GpsStatus status = App.get().getLocationManager().getGpsStatus(null);

        int num = 0;
        float levelSignal = 0;
        for (GpsSatellite gpsSatellite : status.getSatellites()) {
            if (gpsSatellite.usedInFix()) {
                float tmp = gpsSatellite.getSnr();
                if (tmp > 0) {
                    levelSignal += tmp;
                    num++;
                }

            }
        }
        levelSignal /= num;

        updateSignal(levelSignal);

        Log.w(TAG, "onGpsStatusChanged: signal " + levelSignal);
        switch (event) {
            case GpsStatus.GPS_EVENT_FIRST_FIX:


                if (isStartWay) firstFixGps();

                break;
            case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                Log.w(TAG, "onGpsStatusChanged: gps event satellite status");
                break;
            case GpsStatus.GPS_EVENT_STARTED:
                onStartGps();
                break;
            case GpsStatus.GPS_EVENT_STOPPED:

                stopedStatus();
                break;


        }
    }


}
