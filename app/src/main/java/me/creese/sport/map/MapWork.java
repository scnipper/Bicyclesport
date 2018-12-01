package me.creese.sport.map;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import me.creese.sport.map.gps.Gps;
import me.creese.sport.map.gps.GpsListener;
import me.creese.sport.models.RouteModel;


public class MapWork implements OnMapReadyCallback, GpsListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener {
    private static final String TAG = MapWork.class.getSimpleName();
    private final Gps gps;
    private Activity context;
    //private Route currentRoute;
    private GoogleMap googleMap;
    private boolean isRouteMode;
    private TextView distText;
    private ArrayList<Route> routes;
    private Marker startMarker;

    public MapWork( TextView distText) {

        this.distText = distText;
        gps = new Gps(this);
        routes = new ArrayList<>();
    }


    /**
     * Добавление на карту ранее созданного маршрута
     *
     * @param model
     */
    private void showRoute(RouteModel model) {
        googleMap.clear();

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

    /**
     * Добавление маршрута
     * @param route
     */
    public void addRoute(Route route) {
        route.setGoogleMap(googleMap);

        routes.add(route);
    }

    /**
     * Функция создания маршрута
     */
    public void makeRoute() {

        clearRoutes();
        googleMap.clear();
        isRouteMode = true;
        Route route = new Route(context);
        route.setMarker(true);
        addRoute(route);

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

    public void setContext(AppCompatActivity context) {
        this.context = context;
        gps.setContext(context);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        this.googleMap = googleMap;
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        //currentRoute.setGoogleMap(googleMap);

        for (Route route : routes) {
            route.setGoogleMap(googleMap);
        }

        googleMap.setOnMapClickListener(this);
        googleMap.setOnMarkerDragListener(this);
        if (App.get().getModel() != null) {
            showRoute(App.get().getModel());
            App.get().setModel(null);
        }

        if(routes.size() == 0) {
            if (startMarker != null) {
                startMarker = googleMap.addMarker(new MarkerOptions().position(startMarker.getPosition()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startMarker.getPosition(), 17));
            } else showStartPosition();
        } else {

            for (Route route : routes) {
                route.showOnMap();
                if(!route.isFocusRoute())
                route.focusOnPoint(route.getMarkers().get(route.getMarkers().size()-1).getPosition());
            }

        }


    }

    @Override
    public void whenFindStartPos(LatLng pos) {
        startMarker = googleMap.addMarker(new MarkerOptions().position(pos).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 17));

    }


    @Override
    public void onMapClick(LatLng latLng) {
        if (isRouteMode) {
            routes.get(routes.size() - 1).addPointOnMap(latLng);
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {


    }

    @Override
    public void onMarkerDrag(Marker marker) {

        routes.get(routes.size() - 1).update();

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

}
