package me.creese.sport.models.adapters;

import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import me.creese.sport.models.RideModel;
import me.creese.sport.models.RouteModel;
import me.creese.sport.ui.fragments.PageStatFragment;

public class StatPageAdapter extends FragmentStatePagerAdapter {

    private static final int COUNT_PAGE = 2;
    private final RideModel model;

    private String[] titles = new String[]{"Статистика","Графики"};

    public StatPageAdapter(FragmentManager fm, RideModel model) {
        super(fm);

        this.model = model;
    }

    @Override
    public Fragment getItem(int i) {
        return PageStatFragment.newInstanse(i);
    }

    @Override
    public int getCount() {
        return COUNT_PAGE;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }
}
