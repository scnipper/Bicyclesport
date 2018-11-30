package me.creese.sport.map;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;

import me.creese.sport.App;
import me.creese.sport.R;
import me.creese.sport.data.PointsTable;
import me.creese.sport.data.RoutesTable;
import me.creese.sport.models.RouteModel;

public class Route implements Parcelable {

    private static final String TAG = Route.class.getSimpleName();

    private final MarkerOptions markerOptions;
    private final PolylineOptions lineOptions;
   // private final Context context;
    private int colorLine;
    private double distance = 0.0D;
    private GoogleMap googleMap;
    private ArrayList<Marker> markers;

    private Polyline line;
    private ArrayList<LatLng> tmpPoints;
    private ArrayList<String> markerTitles;
    private boolean isMarker;
    //private TextView viewText;

    public Route(Context context) {


        markers = new ArrayList<>();
        markerOptions = new MarkerOptions().draggable(true).flat(true).icon(BitmapDescriptorFactory
                .fromBitmap(BitmapFactory.decodeResource(context.getResources(), R.drawable.dot)));
        markerTitles = new ArrayList<>();

        lineOptions = new PolylineOptions();

        setColorLine(0xffffff00);

        tmpPoints = new ArrayList<>();
    }

    protected Route(Parcel in) {
        markerOptions = in.readParcelable(MarkerOptions.class.getClassLoader());
        lineOptions = in.readParcelable(PolylineOptions.class.getClassLoader());
        colorLine = in.readInt();
        distance = in.readDouble();
        tmpPoints = in.createTypedArrayList(LatLng.CREATOR);
        markerTitles = in.createStringArrayList();
        isMarker = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(markerOptions, flags);
        dest.writeParcelable(lineOptions, flags);
        dest.writeInt(colorLine);
        dest.writeDouble(distance);
        dest.writeTypedList(tmpPoints);
        dest.writeStringList(markerTitles);
        dest.writeByte((byte) (isMarker ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Route> CREATOR = new Creator<Route>() {
        @Override
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };

    /**
     * Показать маршрут на карте
     */
    public void showOnMap() {
        if (line != null) {
            line.remove();
        }
        line = googleMap.addPolyline(lineOptions);

        for (int i = 0; i < lineOptions.getPoints().size(); i++) {
            markerOptions.position(lineOptions.getPoints().get(i));
            markerOptions.title(markerTitles.get(i));
            if(isMarker)
            markers.add(googleMap.addMarker(markerOptions));
        }


    }
    /**
     * Добавление точки на маршрут
     * @param point
     */
    public void addPoint(LatLng point) {
        if (lineOptions.getPoints().size() > 0) {
            distance += SphericalUtil.computeDistanceBetween(lineOptions.getPoints().get(lineOptions.getPoints().size() - 1), point);
        }
        lineOptions.add(point);
        markerTitles.add(makeDistance());
        //viewText.setVisibility(View.VISIBLE);
        //viewText.setText(context.getString(R.string.distance)+" "+markers.get(markers.size()-1).getTitle());

    }

    public void focusOnPoint(LatLng point) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 17));
    }

    /**
     * Добавление точки на маршрут и отображение на карте
     * @param point
     */
    public void addPointOnMap(LatLng point) {
        addPoint(point);
        showOnMap();

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
            String dist = makeDistance();
            markers.get(i).setTitle(dist);
            markerTitles.set(i,dist);
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

    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    public void setViewMarker(boolean marker) {
        isMarker = marker;
    }

    public ArrayList<Marker> getMarkers() {
        return markers;
    }
}
