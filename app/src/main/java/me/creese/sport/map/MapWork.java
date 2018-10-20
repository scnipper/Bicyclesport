package me.creese.sport.map;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

import me.creese.sport.App;
import me.creese.sport.models.RouteModel;


public class MapWork implements OnMapReadyCallback, GpsListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener {
    private static final String TAG = MapWork.class.getSimpleName();
    private final Gps gps;
    private final Context context;
    private final ArrayList<LatLng> poly;
    private Route currentRoute;
    private GoogleMap googleMap;
    private boolean isRouteMode;
    private Polyline lastLine;

    public MapWork(Context context) {
        this.context = context;
        gps = new Gps(context);
        poly = new ArrayList<>();
        currentRoute = new Route(context);
    }

    /**
     * Добавление на карту ранее созданного маршрута
     *
     * @param model
     */
    private void showRoute(RouteModel model) {
        getGoogleMap().clear();
        poly.clear();

        clearRoute();

        for (LatLng latLng : model.getPoints()) {
            addPointRoute(latLng);
        }
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(lastLine.getPoints().get(0), 15));
    }

    /**
     * Функция создания маршрута
     */
    public void makeRoute() {
        clearRoute();
        googleMap.clear();
        poly.clear();
        isRouteMode = true;
    }

    private void addPointRoute(LatLng latLng) {
        googleMap.addMarker(currentRoute.addPoint(latLng));
        lastLine = googleMap.addPolyline(currentRoute.getLine());
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        this.googleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        googleMap.setOnMapClickListener(this);
        googleMap.setOnMarkerDragListener(this);
        if (App.get().getModel() != null) {
            showRoute(App.get().getModel());
            App.get().setModel(null);
        } else showStartPosition();


    }

    public void showStartPosition() {
        gps.getStartPosition(this);
    }

    public void clearRoute() {
        currentRoute = new Route(context);
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public Gps getGps() {
        return gps;
    }

    public Route getCurrentRoute() {
        return currentRoute;
    }

    @Override
    public void whenFindStartPos(LatLng pos) {
        googleMap.addMarker(new MarkerOptions().position(pos).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 17));

    }



    @Override
    public void onMapClick(LatLng latLng) {
        if (isRouteMode) {
            addPointRoute(latLng);
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {
        currentRoute.update(marker);


        lastLine.remove();
        lastLine = googleMap.addPolyline(currentRoute.getLine());


    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }
}
