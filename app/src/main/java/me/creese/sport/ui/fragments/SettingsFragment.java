package me.creese.sport.ui.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v14.preference.SwitchPreference;
import android.support.v4.app.ActivityCompat;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.widget.Toast;


import net.rdrei.android.dirchooser.DirectoryChooserConfig;
import net.rdrei.android.dirchooser.DirectoryChooserFragment;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import me.creese.sport.App;
import me.creese.sport.BuildConfig;
import me.creese.sport.R;
import me.creese.sport.data.DataHelper;
import me.creese.sport.ui.activities.EnterDataUserActivity;
import me.creese.sport.ui.activities.SettingsActivity;
import me.creese.sport.util.AppSettings;

public class SettingsFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener,DirectoryChooserFragment.OnFragmentInteractionListener {


    private DirectoryChooserFragment dialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        Preference version_app = findPreference("version_app");
        version_app.setSummary(BuildConfig.VERSION_NAME);
    }


    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        String key = preference.getKey();
        if (key.equals(getString(R.string.pref_enter_data))) {
            preference.getContext().startActivity(new Intent(getContext(), EnterDataUserActivity.class));
        }
        if (key.equals(getString(R.string.pref_save_data))) {
            showFileChooser(false);
        }
        if(key.equals(getString(R.string.pref_restore_data))) {
            showFileChooser(true);
        }
        return super.onPreferenceTreeClick(preference);
    }

    public void showFileChooser(boolean isFile) {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("lol").show();
            }

            if (ActivityCompat.checkSelfPermission(getContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                getActivity().getIntent().putExtra(SettingsActivity.FILE_OR_DIRECTORY,isFile);
                ActivityCompat.requestPermissions(
                        getActivity(),new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        SettingsActivity.REQUEST_CODE_WRITE_TO_SD);

                return;
            }
        }


        final DirectoryChooserConfig config = DirectoryChooserConfig.builder()
                .newDirectoryName("")
                .allowNewDirectoryNameModification(true)
                .setShowFile(isFile)
                .build();
        dialog = DirectoryChooserFragment.newInstance(config);
        dialog.setTargetFragment(this, 0);
        dialog.show(getFragmentManager(), null);
    }

    private void copyFile(File from,File to) {
        try {
            to.createNewFile();
            FileInputStream fin = new FileInputStream(from);
            FileOutputStream fout = new FileOutputStream(to);
            int readData;
            while ((readData = fin.read()) != -1) {
                fout.write(readData);
            }
            fin.close();
            fout.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
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


        AppSettings.init(key, value, getContext(), true);

    }

    @Override
    public void onSelectDirectory(@NonNull String path) {

        dialog.dismiss();
        final File pathBegin = new File(path);
        final File dataFile = new File(App.get().getData().getReadableDatabase().getPath());
        if(pathBegin.isDirectory()) {
            if (!path.equals("")) {
                File to = new File(path + "/Bicycle_Data.back");
                copyFile(dataFile,to);
                Toast.makeText(getContext(), "Данные успешно скопированы в директорию\n" + to.getAbsolutePath(), Toast.LENGTH_LONG).show();
            }
        }

        if(pathBegin.isFile()) {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Внимание данное действие приведет к потере текущих данных программы. Продолжить?")
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            if (dataFile.delete()) {
                                File to = new File(dataFile.getParent()+"/"+DataHelper.DATA_BASE_FILE);
                                copyFile(pathBegin,to);
                                Toast.makeText(getContext(),"Данные успешно восстановлены",Toast.LENGTH_SHORT).show();
                                Toast.makeText(getContext(),"Перезагрузите приложение",Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getContext(),"Не получилось восстановить данные :(",Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

        }

    }

    @Override
    public void onCancelChooser() {
        dialog.dismiss();
    }
}
