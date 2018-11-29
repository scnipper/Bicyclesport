package me.creese.sport.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.GeofencingRequest;
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

public class Gps extends LocationCallback {
    private static final String TAG = "Gps";
    private static final long DELTA_TIME = 30 * 1000; // 30 sec
    private final Activity context;
    private final LocationRequest locationRequest;
    private final FusedLocationProviderClient client;
    private final MapWork mapWork;
    private LatLng startPos;
    private GpsListener gpsListener;


    @SuppressLint("MissingPermission")
    public Gps(final Activity context, MapWork mapWork) {
        this.context = context;
        this.mapWork = mapWork;
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(100);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(50);
        client = LocationServices.getFusedLocationProviderClient(context);









        //client.flushLocations();
        //client.requestLocationUpdates()
    }


    private void checkAccessGps() {
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
                    mapWork.addRoute(new Route(context));
                    client.requestLocationUpdates(locationRequest, Gps.this, null);

                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        context,
                                        12);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.

                            break;
                    }
                }
            }
        });
    }

    public void getStartPosition(final GpsListener gpsListener) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Сделать разрешения на позицию
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        this.gpsListener = gpsListener;
        client.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSuccess(Location location) {
                Log.w(TAG, "onSuccess: time " + (System.currentTimeMillis() - location.getTime()));


                if(System.currentTimeMillis() - location.getTime() > DELTA_TIME) {
                    client.requestLocationUpdates(locationRequest, Gps.this, null);
                }
                else
                gpsListener.whenFindStartPos(new LatLng(location.getLatitude(),location.getLongitude()));


            }
        });
    }

    /**
     * Обновление позиции и посторение пути
     */
    @SuppressLint("MissingPermission")
    public void startUpdatePosition() {

        checkAccessGps();


    }

    /**
     * Остановка обновления позиции
     */
    public void stopUpdatePosition() {
        client.removeLocationUpdates(this);
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onLocationResult(LocationResult locationResult) {
        Log.w(TAG, "onLocationResult: "+locationResult.getLastLocation());
        Location location = locationResult.getLastLocation();



        if (gpsListener != null) {
            gpsListener.whenFindStartPos(new LatLng(location.getLatitude(),location.getLongitude()));
            gpsListener = null;
            client.removeLocationUpdates(this);
        } else {
            Route lastRoute = mapWork.getRoutes().get(mapWork.getRoutes().size() - 1);

            lastRoute.addPointOnMap(new LatLng(location.getLatitude(),location.getLongitude()));
        }


    }

    @Override
    public void onLocationAvailability(LocationAvailability locationAvailability) {
        Log.w(TAG, "onLocationAvailability: "+locationAvailability.isLocationAvailable());
    }
}
