package me.creese.sport.map;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;


public class MapWork implements OnMapReadyCallback,GpsListener,GoogleMap.OnMapClickListener {
    private static final String TAG = MapWork.class.getSimpleName();
    private final Gps gps;
    private final Context context;
    private GoogleMap googleMap;

    public MapWork(Context context) {
        this.context = context;
        gps = new Gps(context);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        this.googleMap = googleMap;

        googleMap.setOnMapClickListener(this);
        gps.getStartPosition(this);


    }

    @Override
    public void whenFindStartPos(LatLng pos) {
        googleMap.addMarker(new MarkerOptions()
                .position(pos)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos,17));

    }

    @Override
    public void onMapClick(LatLng latLng) {
        Log.w(TAG, "onMapClick: "+latLng );
    }
}
