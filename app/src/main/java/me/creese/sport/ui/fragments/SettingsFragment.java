package me.creese.sport.ui.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v14.preference.SwitchPreference;
import android.support.v7.preference.EditTextPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;

import me.creese.sport.BuildConfig;
import me.creese.sport.R;
import me.creese.sport.ui.activities.EnterDataUserActivity;
import me.creese.sport.util.Settings;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        addPreferencesFromResource(R.xml.preferences);


        Preference version_app = findPreference("version_app");

        version_app.setSummary(BuildConfig.VERSION_NAME);
    }


    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (preference.getKey().equals(getString(R.string.pref_enter_data))) {
            preference.getContext().startActivity(new Intent(getContext(), EnterDataUserActivity.class));
        }
        return super.onPreferenceTreeClick(preference);
    }

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

    }

    @Override
    public void onPause() {
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);

        String value = null;

        if (preference instanceof ListPreference) {
            value = ((ListPreference) preference).getValue();
        }

        if (preference instanceof SwitchPreference) {
            value = String.valueOf(((SwitchPreference) preference).isChecked());
        }


        Settings.init(key, value, getContext(), true);

    }
}
