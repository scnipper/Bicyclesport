package me.creese.sport.activities;

import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.android.gms.maps.MapView;

import me.creese.sport.map.MapWork;
import me.creese.sport.R;

public class StartActivity extends AppCompatActivity {

    private static final String TAG = StartActivity.class.getSimpleName();
    private MapView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        map = findViewById(R.id.route_map);
        map.onCreate(savedInstanceState);

        map.getMapAsync(new MapWork(this));
    }

    public void openDrawer(View v) {
        DrawerLayout drawerLayout = findViewById(R.id.drawer);

        drawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    protected void onPause() {
        super.onPause();
        map.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        map.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        map.onDestroy();
    }
}
