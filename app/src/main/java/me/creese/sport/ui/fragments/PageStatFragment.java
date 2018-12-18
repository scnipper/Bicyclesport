package me.creese.sport.ui.fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.List;

import me.creese.sport.App;
import me.creese.sport.R;
import me.creese.sport.data.ChartTable;
import me.creese.sport.data.FullTable;
import me.creese.sport.map.Route;
import me.creese.sport.models.RideModel;
import me.creese.sport.util.UpdateInfo;

public class PageStatFragment extends Fragment {
    private static final String PAGE = "page";
    private static final String TAG = PageStatFragment.class.getSimpleName();
    private int page;
    private RideModel model;
    private boolean isChangeTitle;

    public static Fragment newInstanse(int i, @Nullable RideModel rideModel) {
        Bundle bundle = new Bundle();
        bundle.putInt(PAGE, i);
        bundle.putParcelable(RideModel.class.getSimpleName(), rideModel);

        Fragment fragment = new PageStatFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void loadChart(View view) {
        view.findViewById(R.id.progress_in_chart).setVisibility(View.GONE);
        LineChart chart = view.findViewById(R.id.line_chart);
        chart.setVisibility(View.VISIBLE);

        List<Entry> entries = new ArrayList<>();

        SQLiteDatabase db = App.get().getData().getReadableDatabase();

        Cursor cursor = db.query(ChartTable.NAME_TABLE, null, ChartTable.ID_RIDE + "=" + model.getIdRide(), null, null, null, ChartTable.KM, null);

        if (cursor.moveToFirst()) {
            long lastTime = 0;
            do {

                long time = cursor.getLong(cursor.getColumnIndex(ChartTable.TIME));

                float speed = (float) ((1000 / (time - lastTime)) * 3.6);
                lastTime = time;

                entries.add(new Entry(time, speed));
            } while (cursor.moveToNext());
            LineDataSet dataSet = new LineDataSet(entries, "Скорость");
            chart.setData(new LineData(dataSet));
        }



    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RideModel.class.getSimpleName(),model);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            page = arguments.getInt(PAGE);
            model = arguments.getParcelable(RideModel.class.getSimpleName());
        }
        if(model == null)
        loadFullStat(savedInstanceState);
        else isChangeTitle = true;


    }

    private void loadFullStat(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            SQLiteDatabase database = App.get().getData().getReadableDatabase();

            Cursor cursor = database.query(FullTable.NAME_TABLE, null, null, null, null, null, null);
            model = new RideModel();
            if (cursor.moveToFirst()) {
                model.setDistance(cursor.getDouble(cursor.getColumnIndex(FullTable.DISTANCE)));
                model.setCalories(cursor.getInt(cursor.getColumnIndex(FullTable.CALORIES)));
                model.setTimeRide(cursor.getLong(cursor.getColumnIndex(FullTable.TIME)));
                model.setMaxSpeed(cursor.getInt(cursor.getColumnIndex(FullTable.MAX_SPEED)));
            }

            cursor.close();

        } else {
            model = savedInstanceState.getParcelable(RideModel.class.getSimpleName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = null;
        switch (page) {
            case 0:


                view = inflater.inflate(R.layout.stat_fragment_page_1, container, false);

                if(isChangeTitle) ((TextView) view.findViewById(R.id.stat_title)).setText("Статистика маршрута");
                TextView dist = view.findViewById(R.id.stat_dist);
                dist.setText(Route.makeDistance(model.getDistance()) + "");


                ((TextView) view.findViewById(R.id.stat_ride_time)).setText(UpdateInfo.formatTime(model.getTimeRide()));
                ((TextView) view.findViewById(R.id.stat_av_speed)).setText(((int) ((model.getDistance() / model.getTimeRide()) * 3.6f)) +" "+ getString(R.string.km_peer_hour));
                ((TextView) view.findViewById(R.id.stat_cal)).setText(model.getCalories() + "");
                ((TextView) view.findViewById(R.id.stat_max_speed)).setText(model.getMaxSpeed() +" "+ getString(R.string.km_peer_hour));




                double temp = ((model.getDistance() / model.getTimeRide()) * 60);
                temp = Math.round((temp) * 10) / 10D;

                ((TextView) view.findViewById(R.id.stat_temp)).setText(temp+" мин/км");
                break;
            case 1:
                view = inflater.inflate(R.layout.stat_fragment_page_2, container, false);

                final View finalView = view;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        while (UpdateInfo.get().isLoadChart()) {

                            try {
                                Thread.sleep(50);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadChart(finalView);
                            }
                        });
                    }
                }).start();


                break;

        }

        return view;
    }
}
