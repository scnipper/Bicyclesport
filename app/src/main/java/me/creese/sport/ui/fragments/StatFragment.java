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
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.creese.sport.App;
import me.creese.sport.R;
import me.creese.sport.data.ChartTable;
import me.creese.sport.data.FullTable;
import me.creese.sport.map.Route;
import me.creese.sport.models.ChartModel;
import me.creese.sport.models.RideModel;
import me.creese.sport.util.UpdateInfo;
import me.creese.sport.util.chartformat.XAxisFormatTime;
import me.creese.sport.util.chartformat.YAxisFormatDistance;

public class StatFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {

    private static final String TAG = StatFragment.class.getSimpleName();
    private RideModel model;
    private boolean isChangeTitle;
    private ArrayList<Entry> entriesPerKm;
    private ArrayList<Entry> entriesPerTimeDist;
    private ArrayList<Entry> entriesPerTimeCal;
    private LineData lineData;
    private LineChart chart;

    public static Fragment newInstanse(@Nullable RideModel rideModel) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(RideModel.class.getSimpleName(), rideModel);

        Fragment fragment = new StatFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void loadChart(View view) {
        chart = view.findViewById(R.id.line_chart);

        List<ChartModel> allData = new ArrayList<>();
        entriesPerKm = new ArrayList<>();
        entriesPerTimeDist = new ArrayList<>();
        entriesPerTimeCal = new ArrayList<>();

        SQLiteDatabase db = App.get().getData().getReadableDatabase();

        Cursor cursor = db.query(ChartTable.NAME_TABLE, null, ChartTable.ID_RIDE + "=" + model.getIdRide(), null, null, null, null, null);

        if (cursor.moveToFirst()) {

            do {
                allData.add(new ChartModel(cursor.getLong(cursor.getColumnIndex(ChartTable.TIME)),
                        cursor.getInt(cursor.getColumnIndex(ChartTable.CAL)),
                        cursor.getDouble(cursor.getColumnIndex(ChartTable.KM)),
                        cursor.getInt(cursor.getColumnIndex(ChartTable.TYPE))));
            } while (cursor.moveToNext());
            long lastTime = 0;
            double lastKm = 0;
            int lastCal = 0;
            for (ChartModel chartModel : allData) {
                long time = chartModel.getTime();
                if (chartModel.getType() == UpdateInfo.PER_KILOMETR) {
                    float speed = (float) ((1000 / (time - lastTime)) * 3.6);
                    lastTime = time;
                    entriesPerKm.add(new Entry(time, speed));
                }

                if(chartModel.getType() == UpdateInfo.PER_MINUTE) {
                    entriesPerTimeDist.add(new Entry(time, (float) (chartModel.getKilometr()-lastKm)));
                    entriesPerTimeCal.add(new Entry(time, (float) (chartModel.getCalories()-lastCal)));
                    lastKm = chartModel.getKilometr();
                    lastCal = chartModel.getCalories();
                }
            }

            RadioGroup radioGroup = view.findViewById(R.id.stat_radio_group);
            radioGroup.setOnCheckedChangeListener(this);






            int colorText = 0xffB2B0B0;
            XAxis xAxis = chart.getXAxis();
            xAxis.setDrawGridLines(false);
            xAxis.setTextColor(colorText);
            xAxis.setAxisLineColor(colorText);
            xAxis.setGranularity(1);

            xAxis.setTextSize(14);


            chart.getAxisRight().setEnabled(false);


            chart.getAxisLeft().setDrawAxisLine(false);
            chart.getAxisLeft().setGridColor(colorText);
            chart.getAxisLeft().setTextColor(colorText);
            chart.getAxisLeft().setTextSize(14);
            chart.getAxisLeft().setValueFormatter(new YAxisFormatDistance());
            chart.getAxisLeft().setLabelCount(6);
            chart.getLegend().setEnabled(false);

            lineData = new LineData();

            onCheckedChanged(null,R.id.stat_radio_button_2);

        } else {
            chart.setVisibility(View.GONE);
        }

        cursor.close();


    }

    private void createTimeChart() {
        chart.clear();
        chart.getXAxis().setValueFormatter(new XAxisFormatTime());
        LineDataSet dataSetMin = new LineDataSet(entriesPerTimeDist, "");
        dataSetMin.setColor(0xffFA507D);
        dataSetMin.setLineWidth(3);
        dataSetMin.setDrawValues(false);
        dataSetMin.setDrawCircles(false);
        dataSetMin.setDrawCircleHole(false);
        dataSetMin.setHighlightEnabled(false);
        lineData.addDataSet(dataSetMin);
        chart.setData(lineData);
    }

    private void createDistanceChart() {
        chart.clear();
       /* LineDataSet dataSetMin = new LineDataSet(entriesPerTimeDist, "");
        //dataSetMin.setColor();
        dataSetMin.setLineWidth(3);
        dataSetMin.setDrawValues(false);
        dataSetMin.setDrawCircles(false);
        dataSetMin.setDrawCircleHole(false);
        dataSetMin.setHighlightEnabled(false);
        LineDataSet dataSetKm = new LineDataSet(entriesPerKm, "");
        LineData lineData = new LineData(dataSetKm,dataSetMin);
        chart.setData(lineData);*/
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RideModel.class.getSimpleName(), model);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            model = arguments.getParcelable(RideModel.class.getSimpleName());
        }
        if (model == null) loadFullStat(savedInstanceState);
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


        View view = inflater.inflate(R.layout.stat_fragment, container, false);

        if (isChangeTitle)
            ((TextView) view.findViewById(R.id.stat_title)).setText("Статистика маршрута");
        TextView dist = view.findViewById(R.id.stat_dist);
        dist.setText(Route.makeDistance(model.getDistance()) + "");


        ((TextView) view.findViewById(R.id.stat_ride_time)).setText(UpdateInfo.formatTime(model.getTimeRide()));
        ((TextView) view.findViewById(R.id.stat_av_speed)).setText(((int) ((model.getDistance() / model.getTimeRide()) * 3.6f)) + " " + getString(R.string.km_peer_hour));
        ((TextView) view.findViewById(R.id.stat_cal)).setText(model.getCalories() + "");
        ((TextView) view.findViewById(R.id.stat_max_speed)).setText(model.getMaxSpeed() + " " + getString(R.string.km_peer_hour));


        double temp = ((model.getTimeRide() / model.getDistance()) * 1000) / 60;
        temp = Math.round(temp * 10) / 10D;

        ((TextView) view.findViewById(R.id.stat_temp)).setText(temp + " мин/км");

        loadChart(view);


        return view;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.stat_radio_button_1:
                createDistanceChart();
                break;
            case R.id.stat_radio_button_2:
                createTimeChart();
                break;
        }
    }
}
