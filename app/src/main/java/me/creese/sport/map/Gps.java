package me.creese.sport.map;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class Gps extends LocationCallback {
    private static final String TAG = "Gps";
    private static final long DELTA_TIME = 30 * 1000; // 30 sec
    private final Context context;
    private final LocationRequest locationRequest;
    private final FusedLocationProviderClient client;
    private LatLng startPos;
    private GpsListener gpsListener;


    public Gps(Context context) {
        this.context = context;

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(100);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setFastestInterval(50);
        client = LocationServices.getFusedLocationProviderClient(context);
        //client.flushLocations();
        //client.requestLocationUpdates()
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


    @SuppressLint("MissingPermission")
    @Override
    public void onLocationResult(LocationResult locationResult) {
        Log.w(TAG, "onLocationResult: "+locationResult.getLastLocation());
        Location location = locationResult.getLastLocation();
        if (gpsListener != null) {
            gpsListener.whenFindStartPos(new LatLng(location.getLatitude(),location.getLongitude()));
        }
        client.removeLocationUpdates(this);

    }

    @Override
    public void onLocationAvailability(LocationAvailability locationAvailability) {
        Log.w(TAG, "onLocationAvailability: "+locationAvailability.toString() );
    }
}
