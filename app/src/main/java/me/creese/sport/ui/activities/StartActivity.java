package me.creese.sport.ui.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.maps.MapView;

import java.util.List;
import java.util.Map;

import me.creese.sport.R;
import me.creese.sport.map.MapWork;
import me.creese.sport.models.RideModel;
import me.creese.sport.models.RouteAndRide;
import me.creese.sport.models.RouteModel;
import me.creese.sport.ui.fragments.HistoryFragment;
import me.creese.sport.ui.fragments.ListRoutesFragment;
import me.creese.sport.ui.fragments.PageStatFragment;
import me.creese.sport.ui.fragments.StatFragment;
import me.creese.sport.util.Settings;
import me.creese.sport.util.UpdateInfo;

public class StartActivity extends AppCompatActivity {

    public static final int CHECK_GPS_ENABLED = 1122;
    private static final String TAG = StartActivity.class.getSimpleName();
    private MapView map;
    private MapWork mapWork;
    //private View bottomMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(TAG, "onCreate: ");
        setContentView(R.layout.activity_start);

        initPrefs();

        //bottomMenu = findViewById(R.id.view_table);
        if (savedInstanceState != null) {
            int visTable = savedInstanceState.getInt("" + R.id.view_table);
//            findViewById(R.id.view_table).setVisibility(visTable);
        }

        UpdateInfo.get().setStartActivity(this);
        map = findViewById(R.id.route_map);
        map.onCreate(savedInstanceState);

        if (getLastNonConfigurationInstance() == null)
            mapWork = new MapWork();
        else mapWork = (MapWork) getLastCustomNonConfigurationInstance();


        mapWork.setContext(this);
        map.getMapAsync(mapWork);


/*        NavigationView nav = findViewById(R.id.nav_bar);
        nav.setNavigationItemSelectedListener(this);
        nav.bringToFront();*/

        if (mapWork.getGps().isStartWay()) {
           /* ImageButton button = findViewById(R.id.play_button);
            button.setImageResource(R.drawable.baseline_stop_black_36);
            button.setTag("play");*/
        }

       /* RouteModel model = getIntent().getParcelableExtra(RouteModel.class.getSimpleName());
        RouteAndRide routeAndRide = getIntent().getParcelableExtra(RouteAndRide.class.getSimpleName());

        if (routeAndRide != null) {
            showStatFragment(routeAndRide);
        }
        if (model != null) {
            if (mapWork.getRoutes().size() == 0) mapWork.showRoute(model);
        }*/

    }

    /**
     * Инициализация настрек
     */
    private void initPrefs() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        Map<String, ?> all = preferences.getAll();

        for (Object obj : all.entrySet()) {
            Map.Entry pair = (Map.Entry) obj;
            Settings.init(String.valueOf(pair.getKey()), String.valueOf(pair.getValue()), this);

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
            //button.setImageResource(R.drawable.stop_icon);
            button.setTag("play");
            mapWork.getGps().startUpdatePosition();

        } else {
            stopGps(button);
            RouteModel model = mapWork.getLastRoute().saveRoute();
            RideModel rideModel = UpdateInfo.get().saveRide(model.getId());

            showStatFragment(new RouteAndRide(rideModel, model));


        }
    }

    private void stopGps(ImageButton button) {
       // button.setImageResource(R.drawable.baseline_play_arrow_black_36);
        button.setTag("stop");

        mapWork.getGps().stopUpdatePosition();
    }

    public void showStatFragment(RouteAndRide model) {
        if (mapWork.getRoutes().size() == 0) mapWork.showRoute(model.getRouteModel());

        //bottomMenu.setVisibility(View.GONE);
        getSupportFragmentManager()
                .beginTransaction()
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .replace(R.id.main_content,PageStatFragment.newInstanse(0,model.getRideModel()))
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

    public void clickOnMainButton(View view) {
        int id = view.getId();

        switch (id) {
            case R.id.routes_main_btn:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_content,new ListRoutesFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
                break;
            case R.id.settings_main_btn:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.history_main_btn:
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_content,new HistoryFragment())
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
                break;
            case R.id.stat_main_btn:
                getSupportFragmentManager()
                        .beginTransaction()
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .replace(R.id.main_content,PageStatFragment.newInstanse(0, null))
                .commit();
                break;
        }
    }

    public MapWork getMapWork() {
        return mapWork;
    }

    @Override
    public void onBackPressed() {

        if (mapWork.getGps().isStartWay()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(R.string.are_you_sure_to_exit);
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mapWork.getGps().stopUpdatePosition();
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(StartActivity.this);
                    notificationManager.cancelAll();

                    StartActivity.super.onBackPressed();
                }
            });
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.create().show();

        } else {


            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (Fragment fragment : fragments) {
                if (fragment instanceof StatFragment) {
                    if (((StatFragment) fragment).isShowStartPosWhenClose()) {
                        mapWork.getGoogleMap().clear();
                        mapWork.clearRoutes();
                        mapWork.showStartPosition();
                    }
                }
            }


            super.onBackPressed();
            //if (bottomMenu.getVisibility() == View.GONE) bottomMenu.setVisibility(View.VISIBLE);
        }


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

   /* @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.menu_routes:
                //startActivity(new Intent(this, ListRoutesActivity.class));
                break;
            case R.id.menu_settings:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.menu_history:
               // startActivity(new Intent(this, UserHistoryActivity.class));
                break;
            case R.id.menu_stat:
                //startActivity(new Intent(this, FullDataActivity.class));
                break;
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer);

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
*/
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.w(TAG, "onActivityResult: " + requestCode + " " + resultCode);

        if (requestCode == CHECK_GPS_ENABLED && resultCode == -1) {
            mapWork.getGps().startUpdatePosition();
        }
        if (requestCode == CHECK_GPS_ENABLED && resultCode == 0) {
            //stopGps((ImageButton) findViewById(R.id.play_button));
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mapWork.getGps().isFirstFix()) {
            UpdateInfo.get().createViews();
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

        outState.putInt("" + R.id.view_table, visTable);
    }
}
