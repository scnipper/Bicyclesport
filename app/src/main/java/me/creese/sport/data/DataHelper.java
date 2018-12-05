package me.creese.sport.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import me.creese.sport.App;
import me.creese.sport.models.RouteModel;

public class DataHelper extends SQLiteOpenHelper {



    private static final int DATABASE_VERSION = 1;
    private static final String DATA_BASE_FILE = "sport_data";

    public final static String ID = "_id";


    public DataHelper(Context context) {
        super(context, DATA_BASE_FILE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String ROUTES = "CREATE TABLE "+RoutesTable.NAME_TABLE+" (" +
                ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                RoutesTable.TIME+" BIGINT," +
                RoutesTable.DEST+ " DOUBLE DEFAULT (0),"+
                RoutesTable.IS_RIDE+ " INTEGER DEFAULT (0),"+
                RoutesTable.TIME_ROUTE+ " BIGINT,"+
                RoutesTable.KAL+ " INTEGER DEFAULT(0),"+
                RoutesTable.NAME+" VARCHAR (255));";

        final String POINTS = "CREATE TABLE "+PointsTable.NAME_TABLE+" (" +
                ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                PointsTable.LATITUDE+" DOUBLE," +
                PointsTable.LONGTITUDE+" DOUBLE," +
                PointsTable.ID_ROUTE+" INTEGER);";


        db.execSQL(ROUTES);
        db.execSQL(POINTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<RouteModel> addItems(boolean isHistory) {
        SQLiteDatabase db = App.get().getData().getReadableDatabase();
        ArrayList<RouteModel> models = new ArrayList<>();

        Cursor cursor = db.query(RoutesTable.NAME_TABLE, null, RoutesTable.IS_RIDE+"='"+(isHistory?1:0)+"'",
                null, null, null, null);

        if(cursor.moveToFirst()) {
            do {
                ArrayList<LatLng> points = new ArrayList<>();
                Cursor cursor2 = db.query(PointsTable.NAME_TABLE, null,
                        PointsTable.ID_ROUTE + "=" + cursor.getInt(cursor.getColumnIndex(DataHelper.ID)), null, null, null, null);

                if(cursor2.moveToFirst()) {
                    do {
                        int i1 = cursor2.getColumnIndex(PointsTable.LATITUDE);
                        int i2 = cursor2.getColumnIndex(PointsTable.LONGTITUDE);
                        double lat = cursor2.getDouble(i1);
                        double lng = cursor2.getDouble(i2);
                        LatLng point = new LatLng(lat, lng);
                        points.add(point);
                    } while (cursor2.moveToNext());
                }

                cursor2.close();

                RouteModel model = new RouteModel(cursor.getString(cursor.getColumnIndex(RoutesTable.NAME)),
                        points, cursor.getLong(cursor.getColumnIndex(RoutesTable.TIME)),
                        cursor.getDouble(cursor.getColumnIndex(RoutesTable.DEST)),
                        cursor.getInt(cursor.getColumnIndex(RoutesTable.IS_RIDE)) == 1,
                        cursor.getLong(cursor.getColumnIndex(RoutesTable.TIME_ROUTE)));

                models.add(model);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return models;
    }




}
