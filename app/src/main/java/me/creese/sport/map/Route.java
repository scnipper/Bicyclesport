package me.creese.sport.map;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Cap;
import com.google.android.gms.maps.model.CustomCap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Random;

import me.creese.sport.App;
import me.creese.sport.R;
import me.creese.sport.data.PointsTable;
import me.creese.sport.data.RoutesTable;
import me.creese.sport.models.RouteModel;
import me.creese.sport.util.Settings;
import me.creese.sport.util.UserData;

public class Route {

    private static final String TAG = Route.class.getSimpleName();

    private final MarkerOptions markerOptions;
    private final PolylineOptions lineOptions;
    private final AppCompatActivity context;
    // private final Context context;
    private int colorLine;
    private double distance = 0.0D;
    private GoogleMap googleMap;
    private ArrayList<Marker> markers;

    private Polyline line;
    private ArrayList<LatLng> tmpPoints;
    private ArrayList<String> markerTitles;
    private boolean isMarker;
    private boolean isFocusRoute;
    private TextView distanceView;
    private float calories;
    private boolean isFocusCenterRoute;
    private boolean markersIsAdded;
    private boolean isClickLine;
    //private TextView viewText;

    public Route(AppCompatActivity context) {
        this.context = context;

        markers = new ArrayList<>();
        markerOptions = new MarkerOptions()
                .draggable(true)
                .flat(true)
                .anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.marker_icon)));
        markerTitles = new ArrayList<>();

        lineOptions = new PolylineOptions();


        setColorLine(0xffffff00);

        tmpPoints = new ArrayList<>();
        calories = 0;
    }

    public static String makeDistance(double distance) {
        if (distance < 1000) {
            return (int) distance + " m";
        } else {
            return Math.round((distance / 1000) * 10) / 10D + " km";
        }
    }


    /**
     * Показать маршрут на карте
     */
    public void showOnMap() {

        if (line != null) {
            line.remove();
        }
        line = googleMap.addPolyline(lineOptions);
        line.setClickable(isClickLine);

        if (isFocusRoute)
            focusOnPoint(lineOptions.getPoints().get(lineOptions.getPoints().size() - 1));

        if (isFocusCenterRoute) {
            focusOnPoint(lineOptions.getPoints().get(lineOptions.getPoints().size() / 2), 12);
        }

        if (isMarker) {
            addMarkers();
        }


    }

    /**
     * Добавление маркеров на точки в маршруте
     */
    private void addMarkers() {

        clearMarkers();

        double rotation = 0;


        for (int i = 0; i < lineOptions.getPoints().size(); i++) {
            if (i + 1 < lineOptions.getPoints().size())
                rotation = angleFromCoordinate(lineOptions.getPoints().get(i), lineOptions.getPoints().get(i + 1));


            addMarker(lineOptions.getPoints().get(i), markerTitles.get(i), rotation);

            if(i == 0 || i == lineOptions.getPoints().size()-1) {
                markers.get(i).setIcon(BitmapDescriptorFactory.fromResource(R.drawable.cap));
            }
        }


    }

    private double angleFromCoordinate(LatLng latFrom, LatLng latTo) {

        double dLon = (latTo.longitude - latFrom.longitude);

        double y = Math.sin(dLon) * Math.cos(latTo.latitude);
        double x = Math.cos(latFrom.latitude) * Math.sin(latTo.latitude) - Math.sin(latFrom.latitude) * Math.cos(latTo.latitude) * Math.cos(dLon);

        double angle = Math.atan2(y, x);

        angle = Math.toDegrees(angle);
        angle = (angle + 360) % 360;
        angle = 360 - angle;

        return angle;
    }

    /**
     * Удаление всех маркеров с маршрута
     */
    public void clearMarkers() {
        for (Marker marker : markers) {
            marker.remove();
        }
        markers.clear();

    }

    /**
     * Добавление точки на маршрут
     *
     * @param point
     */
    public void addPoint(LatLng point) {

        if (lineOptions.getPoints().size() > 0) {
            double delta = SphericalUtil.computeDistanceBetween(lineOptions.getPoints().get(lineOptions.getPoints().size() - 1), point);
            distance += delta;

        }
        lineOptions.add(point);

        if (isMarker) {
            markerTitles.add(makeDistance(distance));

        }


    }


    /**
     * Добавление маркера на маршрут
     *
     * @param point
     * @param title
     * @param rotation
     */
    public void addMarker(LatLng point, String title, double rotation) {
        markerOptions.position(point);
        markerOptions.title(title);
        markers.add(googleMap.addMarker(markerOptions));

        markers.get(markers.size() - 1).setRotation((float) rotation);
    }

    public void focusOnPoint(LatLng point) {
        focusOnPoint(point, Settings.ZOOM);
    }

    /**
     * Сделать фокус на точке
     *
     * @param point
     */
    public void focusOnPoint(LatLng point, int zooom) {

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, zooom));
    }


    /**
     * Добавление точки на маршрут и отображение на карте
     *
     * @param point
     */
    public void addPointOnMap(LatLng point) {

        addPoint(point);
        showOnMap();

    }

    public RouteModel saveRoute() {
        Random random = new Random();
        byte[] bytes = new byte[10];
        random.nextBytes(bytes);


        return saveRoute(new String(bytes));
    }

    /**
     * Сохранение маршрута
     *
     * @param name
     */
    public RouteModel saveRoute(String name) {
        RouteModel model = new RouteModel(0, name, lineOptions.getPoints(), System.currentTimeMillis(), isFocusRoute, isMarker);

        SQLiteDatabase db = App.get().getData().getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(RoutesTable.NAME, model.getName());
        contentValues.put(RoutesTable.TIME, model.getTime());

        contentValues.put(RoutesTable.IS_RIDE, model.isFocusRoute() ? 1 : 0);
        contentValues.put(RoutesTable.IS_MARKER, model.isMarker() ? 1 : 0);


        long id = db.insert(RoutesTable.NAME_TABLE, null, contentValues);

        model.setId((int) id);

        for (LatLng latLng : model.getPoints()) {
            contentValues.clear();
            contentValues.put(PointsTable.ID_ROUTE, (int) id);
            contentValues.put(PointsTable.LATITUDE, latLng.latitude);
            contentValues.put(PointsTable.LONGTITUDE, latLng.longitude);
            db.insert(PointsTable.NAME_TABLE, null, contentValues);
        }

        db.close();

        return model;
    }

    public void clickOnRoute() {
        if (!isMarker) {
            addMarkers();
            isMarker = true;
        } else {
            clearMarkers();
            isMarker = false;
        }
    }

    /**
     * Обновление линии маршрута и маркеров при перетаскивании маркера
     */
    public void update() {
        if (markers.size() < 2) return;
        distance = 0;
        for (int i = 1; i < markers.size(); i++) {
            distance += SphericalUtil.computeDistanceBetween(markers.get(i - 1).getPosition(), markers.get(i).getPosition());
            String dist = makeDistance(distance);

            markers.get(i - 1).setRotation((float) angleFromCoordinate(markers.get(i - 1).getPosition(), markers.get(i).getPosition()));

            markers.get(i).setTitle(dist);
            markerTitles.set(i, dist);
            lineOptions.getPoints().set(i, markers.get(i).getPosition());

        }
        tmpPoints.clear();
        for (Marker m : markers) {
            tmpPoints.add(m.getPosition());
        }
        line.setPoints(tmpPoints);

        //viewText.setText(context.getString(R.string.distance)+" "+markers.get(markers.size()-1).getTitle());

    }

    public void setColorLine(int colorLine) {
        this.colorLine = colorLine;
        lineOptions.color(colorLine);
    }

    /**
     * Расчет калорий в секуду
     *
     * @param speed
     * @return
     */
    public float calculateCalories(float speed) {
        calories += speed * 0.00006944f * UserData.WEIGHT;
        return calories;
    }

    public boolean isClickLine() {
        return isClickLine;
    }

    public void setClickLine(boolean clickLine) {
        isClickLine = clickLine;
    }

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public ArrayList<Marker> getMarkers() {
        return markers;
    }

    public Polyline getLine() {
        return line;
    }

    public boolean isFocusRoute() {
        return isFocusRoute;
    }

    public void setFocusRoute(boolean focusRoute) {
        isFocusRoute = focusRoute;
    }

    public void setFocusCenterRoute(boolean focusCenterRoute) {
        isFocusCenterRoute = focusCenterRoute;
    }

    public void setMarker(boolean marker) {
        isMarker = marker;
    }

    public double getDistance() {
        return distance;
    }
}
