package me.creese.sport.map;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.HashMap;

import me.creese.sport.App;
import me.creese.sport.R;
import me.creese.sport.models.RouteModel;
import me.creese.sport.util.FilterRoutes;

public class Route {
    private static final int COLOR_LINE = 0xffffff00;
    private static final String TAG = Route.class.getSimpleName();

    private final MarkerOptions marker;
    private final PolylineOptions lineOptions;
    private double distance = 0.0D;
    private GoogleMap googleMap;
    private ArrayList<Marker> markers;

    private Polyline line;
    private ArrayList<LatLng> tmpPoints;

    public Route(Context context) {
        markers = new ArrayList<>();
        marker = new MarkerOptions().draggable(true).flat(true).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.dot)));


        lineOptions = new PolylineOptions()

                .color(COLOR_LINE);

        tmpPoints = new ArrayList<>();
    }

    public void addPoint(LatLng point) {
        if (lineOptions.getPoints().size() > 0) {
            distance += SphericalUtil.computeDistanceBetween(lineOptions.getPoints().get(lineOptions.getPoints().size() - 1), point);
        }
        // points.add(point);
        marker.position(point);


        lineOptions.add(point);
        marker.title(makeDistance());

        if (line != null) {
            line.remove();
        }


        markers.add(googleMap.addMarker(marker));
        line = googleMap.addPolyline(lineOptions);


    }


    /**
     * Сохранение маршрута
     *
     * @param name
     */
    public void saveRoute(String name) {
        RouteModel model = new RouteModel(name, lineOptions.getPoints(), System.currentTimeMillis());

        String json = App.get().getGson().toJson(model);

        App.get().getFiles().saveDataCache(json.getBytes(), name + FilterRoutes.EXTENSION);


    }

    private String makeDistance() {
        if (distance < 1000) {
            return (int) distance + " m";
        } else {
            return Math.round((distance / 1000) * 10) / 10D + " km";
        }
    }

    /**
     * Обновление линии маршрута при перетаскивании маркера
     *
     */
    public void update() {
        if (markers.size() < 2) return;
        distance = 0;
        for (int i = 1; i < markers.size(); i++) {
            distance += SphericalUtil.computeDistanceBetween(markers.get(i - 1).getPosition(), markers.get(i).getPosition());
            markers.get(i).setTitle(makeDistance());
        }
        tmpPoints.clear();
        for (Marker m : markers) {
            tmpPoints.add(m.getPosition());
        }
        line.setPoints(tmpPoints);

    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

}
