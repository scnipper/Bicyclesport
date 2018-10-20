package me.creese.sport.map;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

import me.creese.sport.App;
import me.creese.sport.R;
import me.creese.sport.models.RouteModel;
import me.creese.sport.util.FilterRoutes;

public class Route {
    private static final int COLOR_LINE = 0xffffff00;
    private static final String TAG = Route.class.getSimpleName();

    private final ArrayList<LatLng> points;
    private final MarkerOptions marker;
    private final PolylineOptions line;
    private double distance = 0.0D;

    public Route(Context context) {
        points = new ArrayList<>();
        marker = new MarkerOptions().draggable(true).flat(true).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.dot)));


        line = new PolylineOptions()

                .color(COLOR_LINE);

    }

    public MarkerOptions addPoint(LatLng point) {
        if (points.size() > 0) {
            distance += SphericalUtil.computeDistanceBetween(points.get(points.size() - 1), point);
        }
        points.add(point);
        marker.position(point);
        line.add(point);
        marker.title(makeDistance());


        return marker;
    }

    /**
     * Сохранение маршрута
     *
     * @param name
     */
    public void saveRoute(String name) {
        RouteModel model = new RouteModel(name, points, System.currentTimeMillis());

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
     * @param marker
     */
    public void update(Marker marker) {
        points.set(points.size() - 1, marker.getPosition());
        line.getPoints().set(points.size() - 1, marker.getPosition());

        marker.setTitle(makeDistance());

    }

    public void clear() {

    }

    public PolylineOptions getLine() {
        return line;
    }

    public ArrayList<LatLng> getPoints() {
        return points;
    }
}
