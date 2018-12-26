package me.creese.sport.map;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
    private final MarkerOptions focusMarkerOptions;
    private List<PatternItem> patternPauseLine = Arrays.asList(new Dash(20), new Gap(30));

    private final MarkerOptions markerOptions;
    private final ArrayList<Point> points;
    private final PolylineOptions lineOptions;
    private final ArrayList<Polyline> lines;
    private int colorLine;
    private double distance = 0.0D;
    private GoogleMap googleMap;
    private ArrayList<Marker> markers;
    private ArrayList<LatLng> tmpPoints;
    private ArrayList<String> markerTitles;
    private boolean isMarker;
    private boolean isFocusRoute;
    private float calories;
    private boolean isFocusCenterRoute;
    private boolean markersIsAdded;
    private boolean isClickLine;
    private Marker focusMarker;

    public Route(Context context) {

        lines = new ArrayList<>();
        points = new ArrayList<>();

        markers = new ArrayList<>();
        markerOptions = new MarkerOptions().draggable(true).flat(true).anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.marker_icon)));
        markerTitles = new ArrayList<>();

        lineOptions = new PolylineOptions();

        focusMarkerOptions = new MarkerOptions()
                .anchor(0.5f,1);

        if(Settings.TYPE_SPORT == Settings.TypeSport.BIKE) {
            focusMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_bike));
        } else {
            focusMarkerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker_run));
        }

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

        for (Polyline line : lines) {
            if (line != null) {
                line.remove();
            }
        }
        lines.clear();
        lineOptions.getPoints().clear();

        for (Point point : points) {
            lineOptions.add(point.getLatLng());
            if (point.getTypePoint() == Point.DASH_START) {
                Polyline line = googleMap.addPolyline(lineOptions);
                line.setClickable(isClickLine);
                lines.add(line);
                lineOptions.getPoints().clear();
                lineOptions.add(point.getLatLng());
            }

            if (point.getTypePoint() == Point.DASH_END) {
                Polyline line = googleMap.addPolyline(lineOptions);
                line.setClickable(isClickLine);
                line.setPattern(patternPauseLine);
                lines.add(line);
                lineOptions.getPoints().clear();
                lineOptions.add(point.getLatLng());
            }

        }
        Polyline line = googleMap.addPolyline(lineOptions);
        line.setClickable(isClickLine);
        lines.add(line);
        if (isFocusRoute) focusOnPoint(points.get(points.size() - 1).getLatLng());

        if (isFocusCenterRoute) {
            focusOnPoint(points.get(points.size() / 2).getLatLng(), 12);
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


        for (int i = 0; i < points.size(); i++) {
            if (i + 1 < points.size())
                rotation = angleFromCoordinate(points.get(i).getLatLng(), points.get(i + 1).getLatLng());
            addMarker(points.get(i).getLatLng(), markerTitles.get(i), rotation);
            if (i == 0 || i == points.size() - 1) {
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

    public void removeLines() {
        for (Polyline line : lines) {
            line.remove();
        }
        lines.clear();
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
    public void addPoint(Point point) {

        if (points.size() > 0) {
            double delta = SphericalUtil.computeDistanceBetween(points.get(points.size() - 1).getLatLng(), point.getLatLng());
            distance += delta;

        }

        Point lastPoint = getLastPoint();
        if (lastPoint != null) {
            if (lastPoint.getTypePoint() == Point.DASH_START) point.setTypePoint(Point.DASH_END);
        }
        points.add(point);

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
    public void focusOnPoint(LatLng point, int zoom) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, zoom));

        if(isFocusRoute) {
            if (focusMarker == null) {
                focusMarkerOptions.position(point);
                focusMarker = googleMap.addMarker(focusMarkerOptions);
            }
            focusMarker.setPosition(point);
        }
    }


    /**
     * Добавление точки на маршрут и отображение на карте
     *
     * @param latLng
     */
    public void addPointOnMap(LatLng latLng) {

        addPoint(new Point(latLng));
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
        RouteModel model = new RouteModel(0, name, points, System.currentTimeMillis(), isFocusRoute, isMarker);

        SQLiteDatabase db = App.get().getData().getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(RoutesTable.NAME, model.getName());
        contentValues.put(RoutesTable.TIME, model.getTime());

        contentValues.put(RoutesTable.IS_RIDE, model.isFocusRoute() ? 1 : 0);
        contentValues.put(RoutesTable.IS_MARKER, model.isMarker() ? 1 : 0);


        long id = db.insert(RoutesTable.NAME_TABLE, null, contentValues);

        model.setId((int) id);

        for (Point point : model.getPoints()) {
            contentValues.clear();
            contentValues.put(PointsTable.ID_ROUTE, (int) id);
            contentValues.put(PointsTable.LATITUDE, point.getLatLng().latitude);
            contentValues.put(PointsTable.LONGTITUDE, point.getLatLng().longitude);
            contentValues.put(PointsTable.TYPE, point.getTypePoint());
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
            points.set(i, new Point(markers.get(i).getPosition()));

        }
        tmpPoints.clear();
        for (Marker m : markers) {
            tmpPoints.add(m.getPosition());
        }
        lines.get(lines.size() - 1).setPoints(tmpPoints);

        //viewText.setText(context.getString(R.string.distance)+" "+markers.get(markers.size()-1).getTitle());

    }

    public Point getLastPoint() {
        return points.size() > 0 ? points.get(points.size() - 1) : null;
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
        //calories+=random.nextInt(5);
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
        //distance+=random.nextInt(3);
        return distance;
    }

    public ArrayList<Polyline> getLines() {
        return lines;
    }
}
