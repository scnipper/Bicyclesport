package me.creese.sport.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import me.creese.sport.App;
import me.creese.sport.map.Point;
import me.creese.sport.models.GoalsModel;
import me.creese.sport.models.RideModel;
import me.creese.sport.models.RouteModel;

public class DataHelper extends SQLiteOpenHelper {



    private static final int DATABASE_VERSION = 1;
    public static final String DATA_BASE_FILE = "sport_data";

    public final static String ID = "_id";


    public DataHelper(Context context) {
        super(context, DATA_BASE_FILE, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String ROUTES = "CREATE TABLE "+RoutesTable.NAME_TABLE+" (" +
                ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                RoutesTable.TIME+" BIGINT," +
                RoutesTable.IS_RIDE+ " INTEGER DEFAULT (0),"+
                RoutesTable.IS_MARKER+ " INTEGER DEFAULT (0),"+
                RoutesTable.NAME+" VARCHAR (255));";


        final String RIDES = "CREATE TABLE "+RideTable.NAME_TABLE+" (" +
                ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                RideTable.TIME_RIDE+" BIGINT," +
                RideTable.ID_ROUTE+" INTEGER NOT NULL," +
                RideTable.MAX_SPEED+" INTEGER," +
                RideTable.CAL+" INTEGER," +
                RideTable.DISTANCE+" DOUBLE);";
        final String POINTS = "CREATE TABLE "+PointsTable.NAME_TABLE+" (" +
                ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                PointsTable.LATITUDE+" DOUBLE," +
                PointsTable.LONGTITUDE+" DOUBLE," +
                PointsTable.TYPE+" INTEGER DEFAULT(0)," +
                PointsTable.ID_ROUTE+" INTEGER);";

        final String CHARTS = "CREATE TABLE "+ChartTable.NAME_TABLE+" (" +
                ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                ChartTable.CAL+" INTEGER," +
                ChartTable.KM+" DOUBLE," +
                ChartTable.TIME+" BIGINT," +
                ChartTable.TYPE+" INTEGER," +
                ChartTable.ID_RIDE+" INTEGER);";

        final String USER_DATA = "CREATE TABLE "+UserTable.NAME_TABLE+" (" +
                ID+" INTEGER DEFAULT(1)," +
                UserTable.WIEGHT+" DOUBLE DEFAULT(70)," +
                UserTable.HEIGHT+" DOUBLE DEFAULT(170)," +
                UserTable.TIME_BIRTH+" BIGINT DEFAULT(0)," +
                UserTable.SEX+" INTEGER DEFAULT(0));";

        final String FULL_STAT = "CREATE TABLE "+FullTable.NAME_TABLE+" (" +
                ID+" INTEGER DEFAULT(1)," +
                FullTable.DISTANCE+" DOUBLE DEFAULT(0)," +
                FullTable.CALORIES+" INTEGER DEFAULT(0)," +
                FullTable.MAX_SPEED+" INTEGER DEFAULT(0)," +
                FullTable.TIME+" BIGINT DEFAULT(0));";

        final String GOALS = "CREATE TABLE "+GoalsTable.NAME_TABLE+" (" +
                ID+" INTEGER PRIMARY KEY AUTOINCREMENT," +
                GoalsTable.TYPE+" INTEGER DEFAULT(0)," +
                GoalsTable.COUNT+" BIGINT DEFAULT(0)," +
                GoalsTable.PASS_COUNT+" BIGINT DEFAULT(0)," +
                GoalsTable.TIME+" BIGINT DEFAULT(0));";


        db.execSQL(ROUTES);
        db.execSQL(FULL_STAT);
        db.execSQL(CHARTS);
        db.execSQL(RIDES);
        db.execSQL(POINTS);
        db.execSQL(USER_DATA);
        db.execSQL(GOALS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public ArrayList<RouteModel> getRoutesModelFromDB(boolean isHistory) {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<RouteModel> models = new ArrayList<>();

        Cursor cursor = db.query(RoutesTable.NAME_TABLE, null, RoutesTable.IS_RIDE+"='"+(isHistory?1:0)+"'",
                null, null, null, null);

        if(cursor.moveToFirst()) {
            do {
                ArrayList<Point> points = new ArrayList<>();
                Cursor cursor2 = db.query(PointsTable.NAME_TABLE, null,
                        PointsTable.ID_ROUTE + "=" + cursor.getInt(cursor.getColumnIndex(DataHelper.ID)), null, null, null, null);

                if(cursor2.moveToFirst()) {
                    do {
                        int i1 = cursor2.getColumnIndex(PointsTable.LATITUDE);
                        int i2 = cursor2.getColumnIndex(PointsTable.LONGTITUDE);
                        double lat = cursor2.getDouble(i1);
                        double lng = cursor2.getDouble(i2);
                        LatLng point = new LatLng(lat, lng);
                        Point p = new Point(point);
                        p.setTypePoint(cursor2.getInt(cursor2.getColumnIndex(PointsTable.TYPE)));
                        points.add(p);

                    } while (cursor2.moveToNext());
                }

                cursor2.close();

                RouteModel model = new RouteModel(cursor.getInt(cursor.getColumnIndex(DataHelper.ID)),
                        cursor.getString(cursor.getColumnIndex(RoutesTable.NAME)),
                        points, cursor.getLong(cursor.getColumnIndex(RoutesTable.TIME)),
                        cursor.getInt(cursor.getColumnIndex(RoutesTable.IS_RIDE)) == 1,
                        cursor.getFloat(cursor.getColumnIndex(RoutesTable.IS_MARKER))==1);

                models.add(model);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return models;
    }

    public ArrayList<RideModel> getRidesFromDB() {
        SQLiteDatabase db = getReadableDatabase();
        ArrayList<RideModel> models = new ArrayList<>();

        Cursor cursor = db.query(RideTable.NAME_TABLE, null, null,
                null, null, null, null);
        if(cursor.moveToFirst()) {
            do {
                RideModel model = new RideModel(cursor.getInt(cursor.getColumnIndex(RideTable.CAL)),
                        cursor.getLong(cursor.getColumnIndex(RideTable.TIME_RIDE)),
                        cursor.getInt(cursor.getColumnIndex(RideTable.MAX_SPEED)),
                        cursor.getDouble(cursor.getColumnIndex(RideTable.DISTANCE)),
                        cursor.getInt(cursor.getColumnIndex(RideTable.ID_ROUTE)));

                model.setIdRide(cursor.getInt(cursor.getColumnIndex(DataHelper.ID)));
                models.add(model);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return models;
    }


    public void insertGoals(GoalsModel goalsModel) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(GoalsTable.COUNT,goalsModel.getCount());
        cv.put(GoalsTable.TIME,goalsModel.getTime());
        cv.put(GoalsTable.TYPE,goalsModel.getType());

        long id = db.insert(GoalsTable.NAME_TABLE, null, cv);
        if(id != -1) {
            goalsModel.setId((int) id);
            App.get().getGoals().add(goalsModel);
        }
    }

    public void removeGoal(int id ) {
        SQLiteDatabase db = getWritableDatabase();

        db.delete(GoalsTable.NAME_TABLE,ID+"="+id,null);
    }

}
