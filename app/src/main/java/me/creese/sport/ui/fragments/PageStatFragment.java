package me.creese.sport.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import me.creese.sport.R;
import me.creese.sport.map.Route;
import me.creese.sport.models.RideModel;
import me.creese.sport.models.RouteModel;
import me.creese.sport.util.UpdateInfo;

public class PageStatFragment extends Fragment {
    private static final String PAGE = "page";
    private int page;
    private RideModel model;

    public static Fragment newInstanse(int i, RideModel model) {
        Bundle bundle = new Bundle();
        bundle.putInt(PAGE, i);
        bundle.putParcelable(RideModel.class.getSimpleName(), model);

        Fragment fragment = new PageStatFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            page = arguments.getInt(PAGE);
            model = arguments.getParcelable(RideModel.class.getSimpleName());
        }


    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {


        View view = null;
        switch (page) {
            case 0:

                view = inflater.inflate(R.layout.stat_fragment_page_1, container, false);
                TextView dist = view.findViewById(R.id.stat_dist);
                dist.setText(Route.makeDistance(model.getDistance()) + "");


                ((TextView) view.findViewById(R.id.stat_ride_time)).setText(UpdateInfo.formatTime(model.getTimeRide()));
                ((TextView) view.findViewById(R.id.stat_av_speed)).setText(((int) ((model.getDistance() / model.getTimeRide()) / 3.6f)) + "");
                ((TextView) view.findViewById(R.id.stat_cal)).setText( model.getCalories() + "");
                ((TextView) view.findViewById(R.id.stat_max_speed)).setText( model.getMaxSpeed() + "");
                break;
            case 1:
                view = inflater.inflate(R.layout.stat_fragment_page_2, container, false);
                break;

        }

        return view;
    }
}
