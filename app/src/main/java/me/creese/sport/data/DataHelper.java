package me.creese.sport.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

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




}
