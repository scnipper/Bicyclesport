package me.creese.sport;

import android.app.Application;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.Random;

import me.creese.sport.models.RouteModel;
import me.creese.sport.util.Files;
import me.creese.sport.util.RouteModelConvert;

public class App extends Application {
    private static App instanse;
    private Files files;
    private Gson gson;
    private Random random;
    private RouteModel model;

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
