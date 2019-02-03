package me.creese.sport.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;
import java.util.List;

import me.creese.sport.R;
import me.creese.sport.map.gps.Gps;
import me.creese.sport.map.gps.GpsListener;
import me.creese.sport.models.RouteModel;
import me.creese.sport.ui.activities.StartActivity;
import me.creese.sport.ui.fragments.MinMenuFragment;
import me.creese.sport.util.AppSettings;
import me.creese.sport.util.P;


public class MapWork implements OnMapReadyCallback, GpsListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener, GoogleMap.OnPolylineClickListener, GoogleMap.OnCameraMoveListener {
    private static final String TAG = MapWork.class.getSimpleName();
    private final Gps gps;
    private StartActivity context;
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

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        List<Point> points = model.getPoints();
        for(int i = 0; i < points.size();i++){
            builder.include(points.get(i).getLatLng());
        }
        LatLngBounds bounds = builder.build();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0));


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
        context.addMinMenu(MinMenuFragment.TYPE_CREATE_ROUTE);
    }



    /**
     * Определение стартовой позиции
     */
    public void showStartPosition() {
        if(requestPermission(StartActivity.REQUEST_PERMISIONS_GPS)) {
            gps.addListeners();
            gps.getStartPosition(this);
        }
    }

    public boolean requestPermission(int requestCode) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(context,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},
                        requestCode);
                return false;
            }
        }
        return true;
    }

    public void clearRoutes() {
        for (Route route : routes) {
            route.clearMarkers();
            route.removeLines();
        }
        routes.clear();
        googleMap.setPadding(0,0,0, P.getPixelFromDP(47));
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
        googleMap.setPadding(0,0,0, P.getPixelFromDP(47));
        googleMap.getUiSettings().setCompassEnabled(false);

        googleMap.setMapType(AppSettings.TYPE_MAP);

        for (Route route : routes) {
            route.setGoogleMap(googleMap);
        }

        googleMap.setOnMapClickListener(this);
        googleMap.setOnMarkerDragListener(this);
        googleMap.setOnPolylineClickListener(this);
        googleMap.setOnCameraMoveListener(this);

        if (routes.size() == 0) {
            if (startMarker != null) {
                startMarker = googleMap.addMarker(new MarkerOptions().position(startMarker.getPosition()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startMarker.getPosition(), AppSettings.ZOOM));
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
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, AppSettings.ZOOM));

    }


    @Override
    public void onMapClick(LatLng latLng) {
        if(gps.isStartWay()) {
            isRouteMode = false;
            Fragment fragment = context.getSupportFragmentManager().findFragmentById(R.id.sub_content);
            if (fragment == null) {
                context.clearAllFragments();
                context.getSupportFragmentManager().executePendingTransactions();
                context.addMinMenu(MinMenuFragment.TYPE_MOVING);
            }

        }
        if (routes.size() == 0) {
            context.clearAllFragments();
            makeRoute();
            googleMap.setPadding(0,0,0, 0);

        }
        if (isRouteMode) {
            getLastRoute().addPointOnMap(latLng);
            TextView textView = context.findViewById(R.id.dist_text);
            if (textView != null) {
                textView.setText(Route.makeDistance(getLastRoute().getDistance()).toUpperCase());
            }

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
