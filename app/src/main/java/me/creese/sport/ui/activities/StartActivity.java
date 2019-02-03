package me.creese.sport.ui.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import java.util.List;
import java.util.Map;

import me.creese.sport.R;
import me.creese.sport.map.MapWork;
import me.creese.sport.map.Point;
import me.creese.sport.map.Route;
import me.creese.sport.models.RideModel;
import me.creese.sport.models.RouteAndRide;
import me.creese.sport.models.RouteModel;
import me.creese.sport.ui.fragments.GoalsFragment;
import me.creese.sport.ui.fragments.HistoryFragment;
import me.creese.sport.ui.fragments.ListRoutesFragment;
import me.creese.sport.ui.fragments.MainViewStatFragment;
import me.creese.sport.ui.fragments.MinMenuFragment;
import me.creese.sport.ui.fragments.StatFragment;
import me.creese.sport.util.AppSettings;
import me.creese.sport.util.P;
import me.creese.sport.util.UpdateInfo;

public class StartActivity extends AppCompatActivity {

    public static final int CHECK_GPS_ENABLED = 1122;

    public static final int REQUEST_PERMISIONS_GPS = 1010;
    public static final int REQUEST_PERMISIONS_GPS_PLAY_BTN = 1011;
    private static final String TAG = StartActivity.class.getSimpleName();
    private MapView map;
    private MapWork mapWork;
    private int saveIdPlayBtn;
    private View subStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.w(TAG, "onCreate: ");
        setContentView(R.layout.content);
        setParams();
        initPrefs();

        UpdateInfo.get().setStartActivity(this);
        map = findViewById(R.id.route_map);

        map.onCreate(savedInstanceState);

        if (getLastNonConfigurationInstance() == null) mapWork = new MapWork();
        else mapWork = (MapWork) getLastCustomNonConfigurationInstance();


        mapWork.setContext(this);
        map.getMapAsync(mapWork);

        subStatus = findViewById(R.id.sub_status_bar);

        setIndicator();
        ((ImageButton) findViewById(R.id.play_button)).setImageResource(mapWork.getGps().isStartWay() ? R.drawable.pause_icon : R.drawable.play_icon);

    }

    /**
     * Установка размера экрана и плотность пикселей
     */
    private void setParams() {

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        P.WIDTH = dm.widthPixels;
        P.HEIGHT = dm.heightPixels;
        P.DENSITY = getResources().getDisplayMetrics().density;


    }

    private void setIndicator() {
        ImageView indicator = findViewById(R.id.indicator_sport);

        if (AppSettings.TYPE_SPORT == AppSettings.TypeSport.BIKE) {
            indicator.setImageResource(R.drawable.bike_indicator);
        } else {
            indicator.setImageResource(R.drawable.run_indicator);
        }
    }

    /**
     * Инициализация настрек
     */
    private void initPrefs() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        Map<String, ?> all = preferences.getAll();

        for (Object obj : all.entrySet()) {
            Map.Entry pair = (Map.Entry) obj;
            AppSettings.init(String.valueOf(pair.getKey()), String.valueOf(pair.getValue()), this);

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


                    dialog.dismiss();
                    clearMakeRoute(findViewById(R.id.img_button_cross_close_route), false);
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
        int id = button.getId();

        if (!mapWork.requestPermission(REQUEST_PERMISIONS_GPS_PLAY_BTN)) {
            saveIdPlayBtn = id;
            return;
        }

        Log.w(TAG, "playSport: play" );
        mapWork.getGps().addListeners();

        if (button.getTag().equals("pause")) {
            button.setTag("play");
            button.setImageResource(id == R.id.play_button ? R.drawable.pause_icon : R.drawable.pause_icon_black);
            if (!mapWork.getGps().isPause()) {
                if (id == R.id.play_button) clearAllFragments();
                else {
                    MinMenuFragment fragment = (MinMenuFragment) getSupportFragmentManager().findFragmentById(R.id.sub_content);

                    fragment.setType(MinMenuFragment.TYPE_MOVING, fragment.getView());
                }
                mapWork.getGps().startUpdatePosition();
                findViewById(R.id.stop_button).setVisibility(View.VISIBLE);
            } else {
                mapWork.getLastRoute().getLastPoint().setTypePoint(Point.DASH_START);
                mapWork.getGps().setPause(false);
                UpdateInfo.get().resume();
            }

        } else {
            button.setImageResource(id == R.id.play_button ? R.drawable.play_icon : R.drawable.play_icon_black);
            v.setTag("pause");
            mapWork.getGps().setPause(true);
            UpdateInfo.get().pause();

        }
    }

    /**
     * Кнопка остановки движения
     *
     * @param button
     */
    public void stopGps(View button) {
        if (button.getId() == R.id.stop_btn_minmenu) {
            addMainButtons();
            clearAllFragments();
            findViewById(R.id.stop_button).setVisibility(View.GONE);
        } else {
            button.setVisibility(View.GONE);
        }
        ((ImageButton) findViewById(R.id.play_button)).setImageResource(R.drawable.play_icon);
        findViewById(R.id.play_button).setTag("pause");
        mapWork.getGps().stopUpdatePosition();
        Route lastRoute = mapWork.getLastRoute();
        if (lastRoute != null) {
            RouteModel model = lastRoute.saveRoute();
            RideModel rideModel = UpdateInfo.get().saveRide(model.getId());
            UpdateInfo.get().checkGoals();
            showStatFragment(new RouteAndRide(rideModel, model));
        }



    }

    public void showStatFragment(RouteAndRide model) {
        if (mapWork.getRoutes().size() == 0) mapWork.showRoute(model.getRouteModel());

        Fragment panel = getSupportFragmentManager().findFragmentByTag(MainViewStatFragment.class.getSimpleName());
        if (panel != null) {
            getSupportFragmentManager().beginTransaction().remove(panel).commit();
        }
        getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null).replace(R.id.main_content, StatFragment.newInstanse(model)).commit();

    }


    public void clearMakeRoute(View v) {
        if (v.getId() == R.id.clear_route_btn_minmenu && mapWork.getGps().isStartWay()) {
            clearAllFragments();
            addMainView();
            UpdateInfo.get().createViews();
            return;
        }
        clearMakeRoute(v, true);
    }

    public void clearMakeRoute(final View v, boolean isShowDialog) {
        final Runnable deleteRun = new Runnable() {
            @Override
            public void run() {
                ((ViewGroup) v.getParent()).setVisibility(View.GONE);
                mapWork.setRouteMode(false);
                mapWork.clearRoutes();
                mapWork.showStartPosition();
                findViewById(R.id.routes_main_btn).setVisibility(View.VISIBLE);

                clearAllFragments();
                findViewById(R.id.menu_buttons).setVisibility(View.VISIBLE);
            }
        };

        if (isShowDialog) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            builder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    deleteRun.run();
                    dialog.dismiss();
                }
            });
            builder.setTitle(R.string.is_remove_route).create().show();
        } else {
            deleteRun.run();
        }

    }

    /**
     * Кнопка начала создания маршрута
     *
     * @param v
     */
    public void startMakeRoute(View v) {
        mapWork.makeRoute();
    }

    public void clickOnMainButton(View view) {
        int id = view.getId();
        if(!mapWork.getGps().isStartWay())
        clearAllFragments();
        switch (id) {
            case R.id.routes_main_btn:
                if(!mapWork.getGps().isStartWay())
                getSupportFragmentManager().beginTransaction().replace(R.id.main_content, new ListRoutesFragment()).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                break;
            case R.id.settings_main_btn:
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.history_main_btn:
                if(!mapWork.getGps().isStartWay())
                getSupportFragmentManager().beginTransaction().replace(R.id.main_content, new HistoryFragment()).addToBackStack(null).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                break;
            case R.id.stat_main_btn:
                if(!mapWork.getGps().isStartWay())
                getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(null).replace(R.id.main_content, StatFragment.newInstanse(null)).commit();
                break;
            case R.id.goals_btn:
                if(!mapWork.getGps().isStartWay())
                getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).addToBackStack(null).replace(R.id.main_content, new GoalsFragment()).commit();
                break;
        }
        getSupportFragmentManager().executePendingTransactions();
        final ScrollView scrollView = findViewById(R.id.root_scroll);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_FORWARD);
            }
        });

    }

    private void addMainButtons() {
        View buttons = findViewById(R.id.menu_buttons);
        buttons.setVisibility(View.VISIBLE);
        ImageButton playBtn = findViewById(R.id.play_button);

        if (UpdateInfo.get().isPause()) {
            playBtn.setTag("pause");
            playBtn.setImageResource(R.drawable.play_icon);
        } else {
            playBtn.setTag("play");
            playBtn.setImageResource(R.drawable.pause_icon);
        }
    }

    public void addMainView() {
        if (mapWork.getGps().isFirstFix()) addMainButtons();

        MainViewStatFragment mainVIewStatFragment = new MainViewStatFragment();
        getSupportFragmentManager().beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).replace(R.id.main_content, mainVIewStatFragment, MainViewStatFragment.class.getSimpleName()).commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    public void addMinMenu(int typeCreateRoute) {
        getSupportFragmentManager().beginTransaction().add(R.id.sub_content, MinMenuFragment.instance(typeCreateRoute)).commit();
        getSupportFragmentManager().executePendingTransactions();
        if (typeCreateRoute == MinMenuFragment.TYPE_MOVING) UpdateInfo.get().createViews();

        findViewById(R.id.menu_buttons).setVisibility(View.GONE);
    }

    public void clearAllFragments() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        for (Fragment fragment : fragments) {
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();

        }
        getSupportFragmentManager().popBackStack();
    }

    /**
     * Получение высоты статус бара
     *
     * @return
     */
    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void doWithRequestCodePermissions(int requestCode) {
        if (requestCode == REQUEST_PERMISIONS_GPS) mapWork.showStartPosition();

        if (requestCode == REQUEST_PERMISIONS_GPS_PLAY_BTN) playSport(findViewById(saveIdPlayBtn));
    }

    public MapWork getMapWork() {
        return mapWork;
    }

    @Override
    public void onBackPressed() {

        if (mapWork.getGps().isStartWay()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setTitle(R.string.are_you_sure_to_exit).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mapWork.getGps().stopUpdatePosition();
                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(StartActivity.this);
                    notificationManager.cancelAll();

                    StartActivity.super.onBackPressed();
                }
            }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();

        } else {
            super.onBackPressed();
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            if (AppSettings.TYPE_MAP == GoogleMap.MAP_TYPE_NORMAL) {
                subStatus.setVisibility(View.VISIBLE);
                subStatus.getLayoutParams().height = getStatusBarHeight();
            } else {
                subStatus.setVisibility(View.GONE);
            }


        }
        while (AppSettings.listChanges.size() > 0) {
            switch (AppSettings.listChanges.pop()) {
                case CHANGE_TYPE_MAP:
                    mapWork.getGoogleMap().setMapType(AppSettings.TYPE_MAP);
                    break;
                case CHANGE_TYPE_SPORT:
                    setIndicator();
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        map.onDestroy();
        UpdateInfo.get().stop();


    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISIONS_GPS || requestCode == REQUEST_PERMISIONS_GPS_PLAY_BTN) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    doWithRequestCodePermissions(requestCode);
                } else {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0]) || ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[1])) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(R.string.rationale_text_gps).setPositiveButton(R.string.agree, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        }).setNegativeButton(R.string.again, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                doWithRequestCodePermissions(requestCode);
                            }
                        }).setCancelable(false).show();
                    } else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(R.string.no_rationale_text_gps).setPositiveButton(R.string.go_to, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivityForResult(intent, requestCode);
                            }
                        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        }).setCancelable(false).show();
                    }
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.w(TAG, "onActivityResult: " + requestCode + " " + resultCode);

        if (requestCode == CHECK_GPS_ENABLED && resultCode == -1) {
            mapWork.getGps().startUpdatePosition();
        }
        if (requestCode == CHECK_GPS_ENABLED && resultCode == 0) {
            stopGps(findViewById(R.id.stop_button));
        }

        if (requestCode == REQUEST_PERMISIONS_GPS || requestCode == REQUEST_PERMISIONS_GPS_PLAY_BTN) {
            doWithRequestCodePermissions(requestCode);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        map.onStart();
        if (mapWork.getGps().isFirstFix()) {
            UpdateInfo.get().createViews();
        }
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mapWork;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setParams();
    }
}
