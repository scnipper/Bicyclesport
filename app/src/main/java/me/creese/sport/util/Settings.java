package me.creese.sport.util;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;

import java.util.LinkedList;

import me.creese.sport.R;

public class Settings {
    public static int TYPE_MAP = GoogleMap.MAP_TYPE_NORMAL;
    public static int ZOOM = 17;
    public static boolean AUTO_PAUSE = false;
    public static TypeSport TYPE_SPORT = TypeSport.BIKE;

    public static LinkedList<Changes> listChanges = new LinkedList<>();

    public static void init(String key,String value, Context context) {
        init(key,value,context,false);
    }
    public static void init(String key,String value, Context context,boolean whenChange) {
        if(key.equals(context.getResources().getString(R.string.pref_type_map))) {
            TYPE_MAP = Integer.valueOf(value);
            if(whenChange)
            listChanges.add(Changes.CHANGE_TYPE_MAP);
        }
        if(key.equals(context.getResources().getString(R.string.pref_zoom))) {
            ZOOM = Integer.valueOf(value);
        }
        if(key.equals(context.getResources().getString(R.string.pref_autopause))) {
            AUTO_PAUSE = Boolean.valueOf(value);
        }
        if(key.equals(context.getResources().getString(R.string.pref_sport_type))) {

            if(value.equals("bike"))
            TYPE_SPORT = TypeSport.BIKE;
            else TYPE_SPORT = TypeSport.RUN;
            if(whenChange)
            listChanges.add(Changes.CHANGE_TYPE_SPORT);
        }

    }
    public enum TypeSport {
        BIKE,
        RUN
    }

    public enum Changes {
        CHANGE_TYPE_MAP,
        CHANGE_TYPE_SPORT
    }
}
