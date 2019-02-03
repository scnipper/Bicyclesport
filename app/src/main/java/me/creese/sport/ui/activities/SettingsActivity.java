package me.creese.sport.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.List;

import me.creese.sport.R;
import me.creese.sport.ui.fragments.SettingsFragment;

public class SettingsActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_WRITE_TO_SD_RESTORE = 1444;
    public static final int REQUEST_CODE_WRITE_TO_SD_ARCHIVE = 1445;
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

    private SettingsFragment findSettingsFragment() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();

        for (Fragment fragment : fragments) {
            if(fragment instanceof SettingsFragment) {
                return (SettingsFragment) fragment;
            }
        }
        return null;
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_WRITE_TO_SD_ARCHIVE:
            case REQUEST_CODE_WRITE_TO_SD_RESTORE:
                if(grantResults.length > 0) {
                    if(grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        boolean isFile = getIntent().getBooleanExtra(FILE_OR_DIRECTORY, false);

                        List<Fragment> fragments = getSupportFragmentManager().getFragments();

                        for (Fragment fragment : fragments) {
                            if(fragment instanceof SettingsFragment) {
                                ((SettingsFragment) fragment).showFileChooser(isFile);
                            }
                        }
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setMessage(R.string.rationale_text_write_storage).setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setNegativeButton(R.string.again, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();

                                    findSettingsFragment().showFileChooser(requestCode == REQUEST_CODE_WRITE_TO_SD_RESTORE);

                                }
                            }).setCancelable(false).show();
                        } else {
                            AlertDialog.Builder builder = new AlertDialog.Builder(this);
                            builder.setMessage(R.string.no_rationale_text_write_storage).setPositiveButton(R.string.go_to, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                    Uri uri = Uri.fromParts("package", getPackageName(), null);
                                    intent.setData(uri);
                                    startActivityForResult(intent, requestCode);
                                }
                            }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }).setCancelable(false).show();
                        }
                    }
                }
                break;

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE_WRITE_TO_SD_ARCHIVE || requestCode == REQUEST_CODE_WRITE_TO_SD_RESTORE) {
            findSettingsFragment().showFileChooser(requestCode == REQUEST_CODE_WRITE_TO_SD_RESTORE);
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
