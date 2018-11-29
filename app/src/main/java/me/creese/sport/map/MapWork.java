package me.creese.sport.map;

import android.app.Activity;
import android.content.Context;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import me.creese.sport.App;
import me.creese.sport.models.RouteModel;


public class MapWork implements OnMapReadyCallback, GpsListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener {
    private static final String TAG = MapWork.class.getSimpleName();
    private final Gps gps;
    private final Context context;
    private final ArrayList<LatLng> poly;
    //private Route currentRoute;
    private GoogleMap googleMap;
    private boolean isRouteMode;
    private TextView distText;
    private ArrayList<Route> routes;

    public MapWork(Activity context, TextView distText) {
        this.context = context;
        this.distText = distText;
        gps = new Gps(context,this);
        poly = new ArrayList<>();
        routes = new ArrayList<>();
    }

    /**
     * Добавление на карту ранее созданного маршрута
     *
     * @param model
     */
    private void showRoute(RouteModel model) {
        googleMap.clear();
        poly.clear();

        clearRoutes();
        LatLng last = null;

        Route route = new Route(context);
        for (LatLng latLng : model.getPoints()) {
            route.addPointOnMap(latLng);
            last = latLng;
        }
        if (last != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(last, 15));
        }
    }


    public void addRoute(Route route) {
        route.setGoogleMap(googleMap);

        routes.add(route);
    }

    /**
     * Функция создания маршрута
     *
     */
    public void makeRoute() {

        clearRoutes();
        googleMap.clear();
        poly.clear();
        isRouteMode = true;
    }


    /**
     * Определение стартовой позиции
     */
    public void showStartPosition() {
        gps.getStartPosition(this);
    }

    public void clearRoutes() {
        for (Route route : routes) {
            route.getMarkers().clear();
        }
        routes.clear();
    }

    public GoogleMap getGoogleMap() {
        return googleMap;
    }

    public Gps getGps() {
        return gps;
    }


    public ArrayList<Route> getRoutes() {
        return routes;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        this.googleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //currentRoute.setGoogleMap(googleMap);

        googleMap.setOnMapClickListener(this);
        googleMap.setOnMarkerDragListener(this);
        if (App.get().getModel() != null) {
            showRoute(App.get().getModel());
            App.get().setModel(null);
        }
        showStartPosition();


    }

    @Override
    public void whenFindStartPos(LatLng pos) {
        googleMap.addMarker(new MarkerOptions().position(pos).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 17));

    }



    @Override
    public void onMapClick(LatLng latLng) {
        if (isRouteMode) {
            routes.get(routes.size()-1).addPoint(latLng);
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {


    }

    @Override
    public void onMarkerDrag(Marker marker) {
        routes.get(routes.size()-1).update();

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }
}
