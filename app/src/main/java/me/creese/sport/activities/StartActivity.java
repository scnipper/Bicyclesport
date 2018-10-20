package me.creese.sport.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.google.android.gms.maps.MapView;

import me.creese.sport.R;
import me.creese.sport.map.MapWork;
import me.creese.sport.models.RouteModel;

public class StartActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = StartActivity.class.getSimpleName();
    private MapView map;
    private MapWork mapWork;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);


        map = findViewById(R.id.route_map);
        map.onCreate(savedInstanceState);
        mapWork = new MapWork(this);

        map.getMapAsync(mapWork);

        NavigationView nav = findViewById(R.id.nav_bar);
        nav.setNavigationItemSelectedListener(this);
        nav.bringToFront();


    }

    /**
     * Кнопка сохранения маршрута
     *
     * @param v
     */
    public void saveRoute(View v) {

        final Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.dialog_enter_name);


        dialog.show();
        dialog.findViewById(R.id.button_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText text = dialog.findViewById(R.id.name_route);
                if(!text.getText().toString().equals("")) {
                    mapWork.getCurrentRoute().saveRoute(text.getText().toString());
                    mapWork.getGoogleMap().clear();
                    mapWork.showStartPosition();

                    findViewById(R.id.save_route_btn).setVisibility(View.GONE);
                    dialog.dismiss();
                }
            }
        });

        dialog.findViewById(R.id.button_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }

    /**
     * Кнопка старта движения
     *
     * @param v
     */
    public void playSport(View v) {
        ImageButton button = (ImageButton) v;


        button.setImageResource(R.drawable.baseline_stop_black_36);
    }

    /**
     * Кнопка открытия бокового меню
     *
     * @param v
     */
    public void openDrawer(View v) {
        DrawerLayout drawerLayout = findViewById(R.id.drawer);

        drawerLayout.openDrawer(GravityCompat.START);
    }

    /**
     * Кнопка начала создания маршрута
     *
     * @param v
     */
    public void startMakeRoute(View v) {
       ImageButton button = findViewById(R.id.save_route_btn);
        button.setVisibility(View.VISIBLE);
        mapWork.makeRoute();
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Log.w(TAG, "onNavigationItemSelected:"+menuItem );
        switch (menuItem.getItemId()) {
            case R.id.menu_routes:
                startActivity(new Intent(this,ListRoutesActivity.class));
                break;
        }

        return true;
    }
}
