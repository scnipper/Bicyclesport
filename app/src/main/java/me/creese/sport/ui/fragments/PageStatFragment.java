package me.creese.sport.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.creese.sport.R;

public class PageStatFragment extends Fragment {
    private static final String PAGE = "page";
    private int page;

    public static Fragment newInstanse(int i) {
        Bundle bundle = new Bundle();
        bundle.putInt(PAGE, i);

        Fragment fragment = new PageStatFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        assert getArguments() != null;
        page = getArguments().getInt(PAGE);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int res = 0;

        switch (page) {
            case 0:
                res = R.layout.stat_fragment_page_1;
                break;
            case 1:
                res = R.layout.stat_fragment_page_2;
                break;

        }

        return inflater.inflate(res, container, false);
    }
}
