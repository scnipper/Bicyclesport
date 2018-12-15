package me.creese.sport.map;

import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import me.creese.sport.map.gps.Gps;
import me.creese.sport.map.gps.GpsListener;
import me.creese.sport.models.RouteModel;
import me.creese.sport.ui.activities.StartActivity;
import me.creese.sport.util.Settings;


public class MapWork implements OnMapReadyCallback, GpsListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener {
    private static final String TAG = MapWork.class.getSimpleName();
    private final Gps gps;
    private AppCompatActivity context;
    //private Route currentRoute;
    private GoogleMap googleMap;
    private boolean isRouteMode;

    private ArrayList<Route> routes;
    private Marker startMarker;
    private Runnable showRouteWhenReady;
    private Route focusRoute;

    public MapWork() {


        gps = new Gps(this);
        routes = new ArrayList<>();
    }


    /**
     * Добавление на карту ранее созданного маршрута
     *
     * @param model
     */
    public void showRoute(final RouteModel model) {
        showRouteWhenReady = new Runnable() {
            @Override
            public void run() {
                googleMap.clear();
                clearRoutes();
                Route route = new Route(context);
                route.setFocusCenterRoute(true);
                addRoute(route);
                for (LatLng latLng : model.getPoints()) {
                    route.addPoint(latLng);
                }
                route.setMarker(model.isMarker());


                //route.focusOnCenterRoute();

              /*  if (last != null) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(last, 15));
                }*/
            }
        };
    }

    /**
     * Добавление маршрута
     *
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

    public void setContext(StartActivity context) {
        this.context = context;
        gps.setContext(context);
    }

    public Route getLastRoute() {
        return routes.get(routes.size() - 1);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        this.googleMap = googleMap;
        googleMap.setMapType(Settings.TYPE_MAP);
        //currentRoute.setGoogleMap(googleMap);

        for (Route route : routes) {
            route.setGoogleMap(googleMap);
        }

        googleMap.setOnMapClickListener(this);
        googleMap.setOnMarkerDragListener(this);
/*        if (App.get().getModel() != null) {
            showRoute(App.get().getModel());
            App.get().setModel(null);
        }*/
        if (showRouteWhenReady != null) {
            showRouteWhenReady.run();
            showRouteWhenReady = null;
        }

        if (routes.size() == 0) {
            if (startMarker != null) {
                startMarker = googleMap.addMarker(new MarkerOptions().position(startMarker.getPosition()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startMarker.getPosition(), Settings.ZOOM));
            } else showStartPosition();
        } else {

            for (Route route : routes) {
                route.showOnMap();
            }

        }


    }

    @Override
    public void whenFindStartPos(LatLng pos) {
        startMarker = googleMap.addMarker(new MarkerOptions().position(pos).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, Settings.ZOOM));

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

    public Marker getStartMarker() {
        return startMarker;
    }

    public void setStartMarker(Marker startMarker) {
        this.startMarker = startMarker;
    }
}
