package me.creese.sport.ui.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringJoiner;

import me.creese.sport.App;
import me.creese.sport.R;
import me.creese.sport.data.ChartTable;
import me.creese.sport.data.FullTable;
import me.creese.sport.map.Route;
import me.creese.sport.models.ChartModel;
import me.creese.sport.models.RideModel;
import me.creese.sport.models.RouteAndRide;
import me.creese.sport.models.RouteModel;
import me.creese.sport.ui.activities.StartActivity;
import me.creese.sport.util.AppSettings;
import me.creese.sport.util.UpdateInfo;
import me.creese.sport.util.chartformat.AxisFormat;

public class StatFragment extends Fragment implements RadioGroup.OnCheckedChangeListener {

    private static final String TAG = StatFragment.class.getSimpleName();
    private RouteAndRide model;
    private boolean isChangeTitle;
    private ArrayList<Entry> entriesPerKmTime;
    private ArrayList<Entry> entriesPerKmSpeed;
    private ArrayList<Entry> entriesPerKmCal;
    private ArrayList<Entry> entriesPerTimeDist;
    private ArrayList<Entry> entriesPerTimeCal;
    private ArrayList<Entry> entriesPerTimeSpeed;
    private LineChart chart;
    private RadioGroup radioGroupChart;
    private RadioGroup radioGroupType;

    public static Fragment newInstanse(@Nullable RouteAndRide rideModel) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(RouteAndRide.class.getSimpleName(), rideModel);

        Fragment fragment = new StatFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    private void loadChart(View view) {
        chart = view.findViewById(R.id.line_chart);

        List<ChartModel> allData = new ArrayList<>();
        entriesPerKmTime = new ArrayList<>();
        entriesPerKmSpeed = new ArrayList<>();
        entriesPerKmCal = new ArrayList<>();
        entriesPerTimeDist = new ArrayList<>();
        entriesPerTimeCal = new ArrayList<>();
        entriesPerTimeSpeed = new ArrayList<>();

        SQLiteDatabase db = App.get().getData().getReadableDatabase();

        Cursor cursor = db.query(ChartTable.NAME_TABLE, null, ChartTable.ID_RIDE + "=" + model.getRideModel().getIdRide(), null, null, null, null, null);

        if (cursor.moveToFirst()) {

            do {
                allData.add(new ChartModel(cursor.getLong(cursor.getColumnIndex(ChartTable.TIME)), cursor.getInt(cursor.getColumnIndex(ChartTable.CAL)), cursor.getDouble(cursor.getColumnIndex(ChartTable.KM)), cursor.getInt(cursor.getColumnIndex(ChartTable.TYPE))));
            } while (cursor.moveToNext());
            long lastTime = 0;
            double lastKm = 0;

            int lastCal = 0;
            int lastCal2 = 0;
            for (ChartModel chartModel : allData) {



                if (chartModel.getType() == UpdateInfo.PER_KILOMETR) {
                    long time = chartModel.getTime();
                    float cal = (float) (chartModel.getCalories() - lastCal2);
                    float speed = (float) ((1000 / (time - lastTime)) * 3.6);

                    entriesPerKmTime.add(new Entry((float) chartModel.getKilometr()*1000, (time - lastTime)));
                    entriesPerKmSpeed.add(new Entry((float) chartModel.getKilometr()*1000, speed));
                    entriesPerKmCal.add(new Entry((float) chartModel.getKilometr()*1000, cal));
                    lastTime = time;
                    lastCal2 = chartModel.getCalories();

                }

                if (chartModel.getType() == UpdateInfo.PER_MINUTE) {
                    long time = chartModel.getTime();
                    float cal = (float) (chartModel.getCalories() - lastCal);
                    float dist = (float) (chartModel.getKilometr() - lastKm);
                    entriesPerTimeDist.add(new Entry(time, dist));
                    entriesPerTimeCal.add(new Entry(time, cal));
                    entriesPerTimeSpeed.add(new Entry(time, (float) ((dist / 60) * 3.6)));
                    //entriesPerTimeTemp.add(new Entry(time, (float) ((dist/60)*3.6)));
                    lastKm = chartModel.getKilometr();
                    lastCal = chartModel.getCalories();

                }

            }




            int colorText = 0xffB2B0B0;
            XAxis xAxis = chart.getXAxis();
            xAxis.setDrawGridLines(false);
            xAxis.setTextColor(colorText);
            xAxis.setAxisLineColor(colorText);
            xAxis.setGranularity(1);
            xAxis.setLabelCount(3);

            xAxis.setTextSize(14);


            chart.getAxisRight().setEnabled(false);
            chart.setDescription(null);


            chart.getAxisLeft().setDrawAxisLine(false);
            chart.getAxisLeft().setGridColor(colorText);
            chart.getAxisLeft().setTextColor(colorText);
            chart.getAxisLeft().setTextSize(14);
            //chart.getAxisLeft().setValueFormatter(new AxisFormat());
            chart.getAxisLeft().setLabelCount(6);



            chart.getLegend().setEnabled(false);


            radioGroupType = view.findViewById(R.id.stat_radio_group_type);
            radioGroupType.setOnCheckedChangeListener(this);
            radioGroupChart = view.findViewById(R.id.stat_radio_group_chart);
            radioGroupChart.setOnCheckedChangeListener(this);

            onCheckedChanged(radioGroupType, radioGroupType.getCheckedRadioButtonId());
            //onCheckedChanged(radioGroupType, radioGroupChart.getCheckedRadioButtonId());


        } else {
            chart.setVisibility(View.GONE);
        }

        cursor.close();


    }

    public void createDataChart(ArrayList<Entry> entries, int color, AxisFormat yAxisFormatDistance) {
        LineDataSet data = formatDataSet(new LineDataSet(entries, ""));
        data.setColor(color);
        LineData lineData = new LineData(data);
        chart.getAxisLeft().setValueFormatter(yAxisFormatDistance);

        chart.setData(lineData);
        chart.invalidate();

    }

    private void createTimeChart() {
        chart.clear();
        chart.getXAxis().setValueFormatter(new AxisFormat(AxisFormat.TypeAxis.TIME));
    }

    private LineDataSet formatDataSet(LineDataSet dataSet) {
        dataSet.setLineWidth(3);
        dataSet.setDrawValues(false);
        dataSet.setDrawCircles(false);
        dataSet.setDrawCircleHole(false);
        dataSet.setHighlightEnabled(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        return dataSet;
    }

    private void createDistanceChart() {
        chart.clear();
        chart.getXAxis().setValueFormatter(new AxisFormat(AxisFormat.TypeAxis.DISTANCE));
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(RouteAndRide.class.getSimpleName(), model);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            model = arguments.getParcelable(RouteAndRide.class.getSimpleName());
        }
        if (model == null) loadFullStat(savedInstanceState);
        else isChangeTitle = true;


    }

    private void loadFullStat(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            SQLiteDatabase database = App.get().getData().getReadableDatabase();

            Cursor cursor = database.query(FullTable.NAME_TABLE,
                    null, null, null, null, null, null);
            model = new RouteAndRide(new RideModel(),null);
            if (cursor.moveToFirst()) {
                model.getRideModel().setDistance(cursor.getDouble(cursor.getColumnIndex(FullTable.DISTANCE)));
                model.getRideModel().setCalories(cursor.getInt(cursor.getColumnIndex(FullTable.CALORIES)));
                model.getRideModel().setTimeRide(cursor.getLong(cursor.getColumnIndex(FullTable.TIME)));
                model.getRideModel().setMaxSpeed(cursor.getInt(cursor.getColumnIndex(FullTable.MAX_SPEED)));
            }

            cursor.close();

        } else {
            model = savedInstanceState.getParcelable(RideModel.class.getSimpleName());
        }
    }

    @SuppressLint("SetTextI18n")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.stat_fragment, container, false);

        if (isChangeTitle) {
            ((TextView) view.findViewById(R.id.stat_title)).setText("Статистика маршрута");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy - HH:mm:ss",Locale.getDefault());
            Date date =new Date(model.getRouteModel().getTime());
            ((TextView) view.findViewById(R.id.stat_ride_date)).setText(simpleDateFormat.format(date));
        }
        else {
            view.findViewById(R.id.stat_date_field).setVisibility(View.GONE);
            view.findViewById(R.id.stat_all_chart).setVisibility(View.GONE);
        }

        TextView dist = view.findViewById(R.id.stat_dist);
        dist.setText(Route.makeDistance(model.getRideModel().getDistance()) + "");



        ((TextView) view.findViewById(R.id.stat_ride_time)).setText(UpdateInfo.formatTime(model.getRideModel().getTimeRide()));
        int avSpeed = (int) ((model.getRideModel().getDistance() / model.getRideModel().getTimeRide()) * 3.6f);


        ((TextView) view.findViewById(R.id.stat_cal)).setText(model.getRideModel().getCalories() + "");
        if(AppSettings.UNIT_SYSTEM.equals(AppSettings.UnitsSystem.METRIC)) {
            ((TextView) view.findViewById(R.id.stat_max_speed)).setText(model.getRideModel().getMaxSpeed() + " " + getString(R.string.km_peer_hour));
            ((TextView) view.findViewById(R.id.stat_av_speed)).setText(avSpeed + " " + getString(R.string.km_peer_hour));
            double temp = ((model.getRideModel().getTimeRide() / model.getRideModel().getDistance()) * 1000) / 60;
            temp = Math.round(temp * 10) / 10D;
            ((TextView) view.findViewById(R.id.stat_temp)).setText(temp + " мин/км");
        }
        if(AppSettings.UNIT_SYSTEM.equals(AppSettings.UnitsSystem.IMPERIAL)) {
            ((TextView) view.findViewById(R.id.stat_av_speed)).setText(((int) (avSpeed * AppSettings.IMP_COEF * 1000)) + " миль/ч");
            ((TextView) view.findViewById(R.id.stat_max_speed)).setText(((int) (model.getRideModel().getMaxSpeed() * AppSettings.IMP_COEF * 1000)) + " миль/ч");
            double temp = ((model.getRideModel().getTimeRide() / (model.getRideModel().getDistance()*AppSettings.IMP_COEF))) / 60;
            temp = Math.round(temp * 10) / 10D;
            ((TextView) view.findViewById(R.id.stat_temp)).setText(temp + " мин/миль");
        }










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
            case R.id.stat_radio_dist_time:
                if (radioGroupType.getCheckedRadioButtonId() == R.id.stat_radio_button_2) {
                    createDataChart(entriesPerTimeDist,0xffFA507D,new AxisFormat(AxisFormat.TypeAxis.DISTANCE));
                } else {
                    createDataChart(entriesPerKmTime,0xffFA507D,new AxisFormat(AxisFormat.TypeAxis.TIME));
                }

                break;
            case R.id.stat_radio_cal:
                if (radioGroupType.getCheckedRadioButtonId() == R.id.stat_radio_button_2) {
                    createDataChart(entriesPerTimeCal,0xffFA507D,new AxisFormat(AxisFormat.TypeAxis.CALORIES));
                } else {
                    createDataChart(entriesPerKmCal,0xffFA507D,new AxisFormat(AxisFormat.TypeAxis.CALORIES));
                }
                break;

            case R.id.stat_radio_speed:
                if (radioGroupType.getCheckedRadioButtonId() == R.id.stat_radio_button_2) {
                    createDataChart(entriesPerTimeSpeed,0xffFA507D,new AxisFormat(AxisFormat.TypeAxis.SPEED));
                } else {
                    createDataChart(entriesPerKmSpeed,0xffFA507D,new AxisFormat(AxisFormat.TypeAxis.SPEED));
                }
                break;

        }

        if(group.getId() == R.id.stat_radio_group_type) {
            onCheckedChanged(radioGroupChart,radioGroupChart.getCheckedRadioButtonId());
        }


    }

    @Override
    public void onDetach() {
        StartActivity activity = (StartActivity) getActivity();
        ArrayList<Route> routes = activity.getMapWork().getRoutes();
        for (Route route : routes) {
            route.remove();
        }
        routes.clear();
        activity.getMapWork().showStartPosition();
        super.onDetach();

    }


}
