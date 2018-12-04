package me.creese.sport.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import me.creese.sport.R;
import me.creese.sport.models.adapters.StatPageAdapter;
import me.creese.sport.ui.custom_view.CustomPager;

public class StatFragment extends Fragment {
    private void init(View view) {
        CustomPager viewPager = view.findViewById(R.id.view_pager);
        viewPager.setAdapter(new StatPageAdapter(getActivity().getSupportFragmentManager()));
        viewPager.measure(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);

        tabLayout.setupWithViewPager(viewPager,true);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.stat_route,container,false);
        init(view);
        view.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT));
        return view;
    }
}
