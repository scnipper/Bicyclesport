package me.creese.sport.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import me.creese.sport.BuildConfig;
import me.creese.sport.R;
import me.creese.sport.ui.activities.EnterDataUserActivity;
import me.creese.sport.util.Settings;

import static android.support.constraint.Constraints.TAG;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        addPreferencesFromResource(R.xml.preferences);


        Preference version_app = findPreference("version_app");

        version_app.setSummary(BuildConfig.VERSION_NAME);


        SwitchPreference goal = (SwitchPreference) findPreference(getString(R.string.pref_goal_switch));

        checkGoal(goal);

    }

    private void checkGoal(SwitchPreference goal) {
        EditTextPreference dist = (EditTextPreference) findPreference(getString(R.string.pref_goal_cal));
        EditTextPreference cal = (EditTextPreference) findPreference(getString(R.string.pref_goal_dist));

        dist.setEnabled(goal.isChecked());
        cal.setEnabled(goal.isChecked());
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        Log.w(TAG, "onPreferenceTreeClick: " + preference);
        String key = preference.getKey();

        if (key.equals(getString(R.string.pref_enter_data))) {
            preference.getContext().startActivity(new Intent(getContext(), EnterDataUserActivity.class));
        }
        if (key.equals(getString(R.string.pref_goal_switch))) {
            checkGoal((SwitchPreference) preference);
        }

        String value = null;

        if (preference instanceof ListPreference) {
            value = ((ListPreference) preference).getValue();
        }

        if (preference instanceof SwitchPreference) {
            value = String.valueOf(((SwitchPreference) preference).isChecked());
        }


        Settings.init(key, value, getContext());


        return super.onPreferenceTreeClick(preference);

    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        Log.w(TAG, "onCreatePreferences: " + s);

    }


}
