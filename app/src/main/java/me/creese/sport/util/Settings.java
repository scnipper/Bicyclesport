package me.creese.sport.util;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;

import me.creese.sport.R;

public class Settings {
    public static int TYPE_MAP = GoogleMap.MAP_TYPE_NORMAL;
    public static int ZOOM = 17;
    public static boolean AUTO_PAUSE = false;

    public static void init(String key,String value, Context context) {
        if(key.equals(context.getResources().getString(R.string.pref_type_map))) {
            TYPE_MAP = Integer.valueOf(value);
        }
        if(key.equals(context.getResources().getString(R.string.pref_zoom))) {
            ZOOM = Integer.valueOf(value);
        }
        if(key.equals(context.getResources().getString(R.string.pref_autopause))) {
            AUTO_PAUSE = Boolean.valueOf(value);
        }
    }
}
