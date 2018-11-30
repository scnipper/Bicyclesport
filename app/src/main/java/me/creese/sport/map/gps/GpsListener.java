package me.creese.sport.map.gps;

import com.google.android.gms.maps.model.LatLng;

public interface GpsListener {
    public void whenFindStartPos(LatLng pos);
}
