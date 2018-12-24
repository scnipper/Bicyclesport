package me.creese.sport.map;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

import me.creese.sport.R;
import me.creese.sport.map.gps.Gps;
import me.creese.sport.map.gps.GpsListener;
import me.creese.sport.models.RouteModel;
import me.creese.sport.ui.activities.StartActivity;
import me.creese.sport.util.Settings;


public class MapWork implements OnMapReadyCallback, GpsListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnPolylineClickListener, GoogleMap.OnCameraMoveListener {
    private static final String TAG = MapWork.class.getSimpleName();
    private final Gps gps;
    private AppCompatActivity context;
    private GoogleMap googleMap;
    private boolean isRouteMode;

    private ArrayList<Route> routes;
    private Marker startMarker;
    private CameraPosition currentCameraPosition;

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
        /*showRouteWhenReady = new Runnable() {
            @Override
            public void run() {*/
        googleMap.clear();
        clearRoutes();
        Route route = new Route(context);
        route.setFocusCenterRoute(true);
        route.setMarker(model.isMarker());
        route.setClickLine(true);
        addRoute(route);
        for (Point point : model.getPoints()) {
            route.addPoint(point);
        }


        route.showOnMap();
        //route.focusOnCenterRoute();

              /*  if (last != null) {
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(last, 15));
                }*/

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



        context.findViewById(R.id.routes_main_btn).setVisibility(View.GONE);

        ImageButton btnSave = context.findViewById(R.id.save_route_btn);
        btnSave.setScaleX(0);
        btnSave.setScaleY(0);
        btnSave.setVisibility(View.VISIBLE);
        btnSave.animate().scaleX(1).scaleY(1).start();

    }



    /**
     * Определение стартовой позиции
     */
    public void showStartPosition() {
        gps.getStartPosition(this);
    }

    public void clearRoutes() {
        for (Route route : routes) {
            route.clearMarkers();
            route.removeLines();
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
        return routes.size() > 0 ? routes.get(routes.size() - 1) : null;
    }

    public Marker getStartMarker() {
        return startMarker;
    }

    public void setStartMarker(Marker startMarker) {
        this.startMarker = startMarker;
    }

    public boolean isRouteMode() {
        return isRouteMode;
    }

    public void setRouteMode(boolean routeMode) {
        isRouteMode = routeMode;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {

        this.googleMap = googleMap;

        googleMap.setMapType(Settings.TYPE_MAP);

        for (Route route : routes) {
            route.setGoogleMap(googleMap);
        }

        googleMap.setOnMapClickListener(this);
        googleMap.setOnMarkerDragListener(this);
        googleMap.setOnPolylineClickListener(this);
        googleMap.setOnCameraMoveListener(this);
/*        if (App.get().getModel() != null) {
            showRoute(App.get().getModel());
            App.get().setModel(null);
        }
        if (showRouteWhenReady != null) {
            showRouteWhenReady.run();
            showRouteWhenReady = null;
        }*/

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

        if (currentCameraPosition != null) {
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(currentCameraPosition));
        }

    }

    @Override
    public void whenFindStartPos(LatLng pos) {
        startMarker = googleMap.addMarker(new MarkerOptions().position(pos).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, Settings.ZOOM));

    }


    @Override
    public void onMapClick(LatLng latLng) {
        LinearLayout distView = context.findViewById(R.id.distance_panel);

        if (routes.size() == 0) {
            makeRoute();
            distView.setVisibility(View.VISIBLE);
        }
        if (isRouteMode) {
            getLastRoute().addPointOnMap(latLng);
            ((TextView) distView.findViewById(R.id.dist_text)).setText(Route.makeDistance(getLastRoute().getDistance()).toUpperCase());

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

    @Override
    public void onPolylineClick(Polyline polyline) {
        for (Route route : routes) {
            for (Polyline routeLine : route.getLines()) {
                if (routeLine.equals(polyline)) {
                    route.clickOnRoute();
                    return;
                }
            }
        }
    }

    @Override
    public void onCameraMove() {
        currentCameraPosition = googleMap.getCameraPosition();
    }
}
