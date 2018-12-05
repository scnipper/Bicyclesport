package me.creese.sport;

import android.app.Application;
import android.location.LocationManager;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Random;

import me.creese.sport.data.DataHelper;
import me.creese.sport.models.RouteModel;
import me.creese.sport.util.Files;
import me.creese.sport.util.RouteModelConvert;
import me.creese.sport.util.UserData;

public class App extends Application {
    private static App instanse;
    private Files files;
    private Gson gson;
    private Random random;
    private RouteModel model;
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
        random = new Random();
        data = new DataHelper(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        setUserData();
    }

    private void setUserData() {
        // TODO: 05.12.2018 Считать с базы данных

        UserData.WEIGHT = 80;
        UserData.SEX = 0;

    }
    public DataHelper getData() {
        return data;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public Random getRandom() {
        return random;
    }

    public Files getFiles() {
        return files;
    }

    public Gson getGson() {
        return gson;
    }

    public void setModel(RouteModel model) {
        this.model = model;
    }

    public RouteModel getModel() {
        return model;
    }
}
