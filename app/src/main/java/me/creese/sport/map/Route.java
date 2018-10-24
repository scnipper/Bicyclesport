package me.creese.sport.map;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.view.View;
import android.widget.TextView;

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
import me.creese.sport.data.PointsTable;
import me.creese.sport.data.RoutesTable;
import me.creese.sport.models.RouteModel;
import me.creese.sport.util.FilterRoutes;

public class Route {
    private static final int COLOR_LINE = 0xffffff00;
    private static final String TAG = Route.class.getSimpleName();

    private final MarkerOptions marker;
    private final PolylineOptions lineOptions;
    private final Context context;
    private double distance = 0.0D;
    private GoogleMap googleMap;
    private ArrayList<Marker> markers;

    private Polyline line;
    private ArrayList<LatLng> tmpPoints;
    private TextView viewText;

    public Route(Context context) {
        this.context = context;
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
        marker.position(point);


        lineOptions.add(point);
        marker.title(makeDistance());

        if (line != null) {
            line.remove();
        }


        markers.add(googleMap.addMarker(marker));
        line = googleMap.addPolyline(lineOptions);
        viewText.setVisibility(View.VISIBLE);
        viewText.setText(context.getString(R.string.distance)+" "+markers.get(markers.size()-1).getTitle());

    }


    /**
     * Сохранение маршрута
     *
     * @param name
     */
    public void saveRoute(String name) {
        RouteModel model = new RouteModel(name, lineOptions.getPoints(), System.currentTimeMillis());

        SQLiteDatabase db = App.get().getData().getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(RoutesTable.NAME,model.getName());
        contentValues.put(RoutesTable.TIME,model.getTime());

        long id = db.insert(RoutesTable.NAME_TABLE, null, contentValues);


        for (LatLng latLng : model.getPoints()) {
            contentValues.clear();
            contentValues.put(PointsTable.ID_ROUTE,(int)id);
            contentValues.put(PointsTable.LATITUDE,latLng.latitude);
            contentValues.put(PointsTable.LONGTITUDE,latLng.longitude);
            db.insert(PointsTable.NAME_TABLE,null,contentValues);
        }

        db.close();



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
        viewText.setText(context.getString(R.string.distance)+" "+markers.get(markers.size()-1).getTitle());

    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }


    public void setViewText(TextView viewText) {
        this.viewText = viewText;
    }

    public ArrayList<Marker> getMarkers() {
        return markers;
    }
}
