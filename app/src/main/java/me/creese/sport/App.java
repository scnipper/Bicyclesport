package me.creese.sport;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.support.v7.preference.PreferenceManager;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;

import me.creese.sport.data.DataHelper;
import me.creese.sport.data.GoalsTable;
import me.creese.sport.data.UserTable;
import me.creese.sport.models.GoalsModel;
import me.creese.sport.models.RouteModel;
import me.creese.sport.util.Files;
import me.creese.sport.util.RouteModelConvert;
import me.creese.sport.util.UserData;

public class App extends Application {
    private static App instanse;
    private Files files;
    private Gson gson;
    private DataHelper data;
    private LocationManager locationManager;
    private ArrayList<GoalsModel> goals;

    public App() {
        instanse = this;
    }

    public static App get() {
        return instanse;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        files = new Files(this);
        gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(RouteModel.class,new RouteModelConvert())
                .create();
        data = new DataHelper(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        SQLiteDatabase db = data.getWritableDatabase();
        setUserData(db);
        loadGoals(db);
        db.close();
    }

    /**
     * Загрузка целей
     * @param db
     */
    private void loadGoals(SQLiteDatabase db) {
        goals = new ArrayList<>();
        Cursor cursor = db.query(GoalsTable.NAME_TABLE,null,null,null,
                null,null,null);
        if(cursor.moveToFirst()) {
            do {

                GoalsModel goalsModel = new GoalsModel(cursor.getLong(cursor.getColumnIndex(GoalsTable.TIME)),
                        cursor.getInt(cursor.getColumnIndex(GoalsTable.COUNT)),cursor.getInt(cursor.getColumnIndex(GoalsTable.TYPE)),
                        cursor.getInt(cursor.getColumnIndex(DataHelper.ID)));
                goalsModel.setPassCount(cursor.getInt(cursor.getColumnIndex(GoalsTable.PASS_COUNT)));
                goals.add(goalsModel);


            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    /**
     * Загрузка пользовательских данных и БД
     * @param db
     */
    public void setUserData(SQLiteDatabase db) {
        Cursor cursor = db.query(UserTable.NAME_TABLE,null,null,
                null,null,null,null);

        if(cursor.moveToFirst()) {
            UserData.WEIGHT = cursor.getDouble(cursor.getColumnIndex(UserTable.WIEGHT));
            UserData.SEX = cursor.getInt(cursor.getColumnIndex(UserTable.SEX));
            UserData.BIRTH_DATE = cursor.getLong(cursor.getColumnIndex(UserTable.TIME_BIRTH));
            UserData.HEIGHT = cursor.getLong(cursor.getColumnIndex(UserTable.HEIGHT));
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DataHelper.ID,1);
            long i = db.insert(UserTable.NAME_TABLE,null,contentValues);
            if(i != -1) {
                cursor.close();
                setUserData(db);
            }
        }

        cursor.close();

    }
    public DataHelper getData() {
        return data;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public Files getFiles() {
        return files;
    }

    public Gson getGson() {
        return gson;
    }

    public ArrayList<GoalsModel> getGoals() {
        return goals;
    }
}
