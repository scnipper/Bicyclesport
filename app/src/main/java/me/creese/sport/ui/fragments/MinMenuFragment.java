package me.creese.sport.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import me.creese.sport.R;
import me.creese.sport.util.UpdateInfo;

public class MinMenuFragment extends Fragment {

    public static final int TYPE_CREATE_ROUTE = 0;
    public static final int TYPE_MOVING = 1;

    private static final String KEY_TYPE = "type";

    public static MinMenuFragment instance(int type) {
        MinMenuFragment menuFragment = new MinMenuFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_TYPE, type);
        menuFragment.setArguments(bundle);
        return menuFragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.min_menu, null);
        Bundle arguments = getArguments();
        int type = 0;
        if (arguments != null) {
            type = arguments.getInt(KEY_TYPE);
        }

        setType(type, view);

        return view;
    }

    public void setType(int type, View view) {
        View stopBtn = view.findViewById(R.id.stop_btn_minmenu);
        View space = view.findViewById(R.id.space_minmenu);
        TextView text = view.findViewById(R.id.text_minmenu);
        if (type == TYPE_CREATE_ROUTE) {
            stopBtn.setVisibility(View.GONE);
            text.setTextColor(getResources().getColor(R.color.text_color_main));
            text.setText("Начать");
            space.setVisibility(View.VISIBLE);
        }

        if (type == TYPE_MOVING) {
            view.findViewById(R.id.dist_text).setVisibility(View.GONE);
            stopBtn.setVisibility(View.VISIBLE);
            space.setVisibility(View.GONE);
            text.setTextColor(getResources().getColor(R.color.text_color_red));
            if (UpdateInfo.get().isPause()) {


                text.setText("Стоп");
            } else {
                ImageButton playBtn = view.findViewById(R.id.play_button_minmenu);
                playBtn.setImageResource(R.drawable.pause_icon_black);
                playBtn.setTag("play");
            }

            view.findViewById(R.id.save_route_btn_minmenu).setVisibility(View.GONE);
        }
    }
}
