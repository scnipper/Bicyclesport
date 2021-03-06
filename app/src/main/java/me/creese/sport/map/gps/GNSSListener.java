package me.creese.sport.map.gps;

import android.annotation.TargetApi;
import android.location.GnssStatus;
import android.os.Build;
import android.util.Log;

@TargetApi(Build.VERSION_CODES.N)
public class GNSSListener extends GnssStatus.Callback {
    private static final String TAG = GNSSListener.class.getSimpleName();
    private final Gps gps;

    public GNSSListener(Gps gps) {
        this.gps = gps;
    }

    @Override
    public void onStarted() {
        gps.onStartGps();
    }

    @Override
    public void onStopped() {
        gps.stopedStatus();
    }

    @Override
    public void onFirstFix(int ttffMillis) {
        gps.firstFixGps();
    }

    @Override
    public void onSatelliteStatusChanged(GnssStatus status) {
        int num = 0;
        float levelSignal = 0;
        int countSat = status.getSatelliteCount();

        for (int i = 0; i < countSat; i++) {
            if (status.usedInFix(i)) {
                float tmp = status.getCn0DbHz(i);
                if (tmp > 0) {
                    levelSignal += tmp;
                    num++;
                }

            }
        }
        levelSignal /= num;

        Log.w(TAG, "onGpsStatusChanged: signal " + levelSignal);
        gps.updateSignal(levelSignal);
    }
}
