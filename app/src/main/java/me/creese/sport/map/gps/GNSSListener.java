package me.creese.sport.map.gps;

import android.annotation.TargetApi;
import android.location.GnssStatus;
import android.os.Build;

@TargetApi(Build.VERSION_CODES.N)
public class GNSSListener extends GnssStatus.Callback {
    @Override
    public void onStarted() {
        super.onStarted();
    }

    @Override
    public void onStopped() {
        super.onStopped();
    }

    @Override
    public void onFirstFix(int ttffMillis) {
        super.onFirstFix(ttffMillis);
    }

    @Override
    public void onSatelliteStatusChanged(GnssStatus status) {
        super.onSatelliteStatusChanged(status);
    }
}
