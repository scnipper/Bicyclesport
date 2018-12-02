package me.creese.sport.ui.fragments;

import android.os.Bundle;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import me.creese.sport.R;

import static android.support.constraint.Constraints.TAG;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        addPreferencesFromResource(R.xml.preferences);




    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        Log.w(TAG, "onCreatePreferences: "+s );

    }


}
