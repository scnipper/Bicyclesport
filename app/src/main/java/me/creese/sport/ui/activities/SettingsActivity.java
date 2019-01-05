package me.creese.sport.ui.activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.List;

import me.creese.sport.R;
import me.creese.sport.ui.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_WRITE_TO_SD = 1444;
    public static final String FILE_OR_DIRECTORY = "file_dir";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity_content);
        Toolbar toolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportFragmentManager().beginTransaction().replace(R.id.c_settings, new SettingsFragment()).commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_WRITE_TO_SD:
                if(grantResults.length > 0) {
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        boolean isFile = getIntent().getBooleanExtra(FILE_OR_DIRECTORY, false);

                        List<Fragment> fragments = getSupportFragmentManager().getFragments();

                        for (Fragment fragment : fragments) {
                            if(fragment instanceof SettingsFragment) {
                                ((SettingsFragment) fragment).showFileChooser(isFile);
                            }
                        }
                    }
                }
                break;

        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
