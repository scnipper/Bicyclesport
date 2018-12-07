package me.creese.sport.ui.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.MapView;

import java.util.List;

import me.creese.sport.App;
import me.creese.sport.R;
import me.creese.sport.map.MapWork;
import me.creese.sport.map.Route;
import me.creese.sport.models.RouteModel;
import me.creese.sport.ui.fragments.SettingsFragment;
import me.creese.sport.ui.fragments.StatFragment;
import me.creese.sport.util.UpdateInfo;
import me.creese.sport.util.UserData;
import me.creese.sport.util.impl.OnBackPressed;

public class StartActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static final int CHECK_GPS_ENABLED = 1122;
    private static final String TAG = StartActivity.class.getSimpleName();
    private MapView map;
    private MapWork mapWork;
    private UpdateInfo updateInfo;
    private View bottomMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(TAG, "onCreate: " );
        setContentView(R.layout.activity_start);

        bottomMenu = findViewById(R.id.bottom_menu);
        if(savedInstanceState != null) {
            int visTable = savedInstanceState.getInt(""+R.id.view_table);
            findViewById(R.id.view_table).setVisibility(visTable);
        }

        UpdateInfo.get().setStartActivity(this);
        map = findViewById(R.id.route_map);
        map.onCreate(savedInstanceState);

        if(getLastNonConfigurationInstance() == null)
        mapWork = new MapWork( (TextView) findViewById(R.id.dist_text));
        else mapWork = (MapWork) getLastCustomNonConfigurationInstance();
        mapWork.setContext(this);
        map.getMapAsync(mapWork);


        NavigationView nav = findViewById(R.id.nav_bar);
        nav.setNavigationItemSelectedListener(this);
        nav.bringToFront();

        if(mapWork.getGps().isStartWay()) {
            ImageButton button = findViewById(R.id.play_button);
            button.setImageResource(R.drawable.baseline_stop_black_36);
            button.setTag("play");
        }

        RouteModel model = getIntent().getParcelableExtra(RouteModel.class.getSimpleName());

        if (model != null) {
            if(model.isFocusRoute())
            showStatFragment(model);
            else {
                if(mapWork.getRoutes().size() == 0)
                mapWork.showRoute(model);
            }
        }

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
                if (!text.getText().toString().equals("")) {
                    mapWork.getRoutes().get(mapWork.getRoutes().size() - 1).saveRoute(text.getText().toString());
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


        if (button.getTag().equals("stop")) {
            button.setImageResource(R.drawable.baseline_stop_black_36);
            button.setTag("play");
            mapWork.getGps().startUpdatePosition();

        } else {
            stopGps(button);
            RouteModel model = mapWork.getLastRoute().saveRoute();


            showStatFragment(model);



        }
    }

    private void stopGps(ImageButton button) {
        button.setImageResource(R.drawable.baseline_play_arrow_black_36);
        button.setTag("stop");

        mapWork.getGps().stopUpdatePosition();
    }

    private void showStatFragment(RouteModel model) {

        if(mapWork.getRoutes().size() == 0)
        mapWork.showRoute(model);
        Bundle bundle = new Bundle();
        bundle.putParcelable(RouteModel.class.getSimpleName(),model);

        StatFragment statFragment = new StatFragment();
        statFragment.setArguments(bundle);

        bottomMenu.setVisibility(View.GONE);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.root_linear,statFragment)
                .addToBackStack(null)
                .commit();
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

    public MapWork getMapWork() {
        return mapWork;
    }

    @Override
    public void onBackPressed() {

        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            if(fragment instanceof StatFragment) {
                if(((StatFragment) fragment).isShowStartPosWhenClose()) {
                    mapWork.getGoogleMap().clear();
                    mapWork.clearRoutes();
                    mapWork.showStartPosition();
                }
            }
        }

        super.onBackPressed();
        if(bottomMenu.getVisibility() == View.GONE)
        bottomMenu.setVisibility(View.VISIBLE);


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

        switch (menuItem.getItemId()) {
            case R.id.menu_routes:
                startActivity(new Intent(this, ListRoutesActivity.class));
                break;
            case R.id.menu_settings:
                startActivity(new Intent(this,SettingsActivity.class));
                break;
            case R.id.menu_history:
                startActivity(new Intent(this,UserHistoryActivity.class));
                break;
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer);

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.w(TAG, "onActivityResult: " + requestCode + " " + resultCode);

        if (requestCode == CHECK_GPS_ENABLED && resultCode == -1) {
            mapWork.getGps().startUpdatePosition();
        }
        if (requestCode == CHECK_GPS_ENABLED && resultCode == 0) {
            stopGps((ImageButton) findViewById(R.id.play_button));
        }

    }


    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mapWork;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        int visTable = findViewById(R.id.view_table).getVisibility();

        outState.putInt(""+R.id.view_table,visTable);
    }
}
