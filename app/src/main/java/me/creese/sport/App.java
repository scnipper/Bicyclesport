package me.creese.sport;

import android.app.Application;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Random;

import me.creese.sport.data.DataHelper;
import me.creese.sport.data.UserTable;
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

        setUserData();
    }

    /**
     * Загрузка пользовательских данных и БД
     */
    public void setUserData() {

        SQLiteDatabase readDb = data.getWritableDatabase();

        Cursor cursor = readDb.query(UserTable.NAME_TABLE,null,null,
                null,null,null,null);

        if(cursor.moveToFirst()) {
            UserData.WEIGHT = cursor.getDouble(cursor.getColumnIndex(UserTable.WIEGHT));
            UserData.SEX = cursor.getInt(cursor.getColumnIndex(UserTable.SEX));
            UserData.BIRTH_DATE = cursor.getLong(cursor.getColumnIndex(UserTable.TIME_BIRTH));
            UserData.HEIGHT = cursor.getLong(cursor.getColumnIndex(UserTable.HEIGHT));
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DataHelper.ID,1);
            long i = readDb.insert(UserTable.NAME_TABLE,null,contentValues);
            if(i != -1) {
                cursor.close();
                setUserData();
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

}
