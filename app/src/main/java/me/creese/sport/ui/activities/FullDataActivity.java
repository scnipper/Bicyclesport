package me.creese.sport.ui.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.creese.sport.App;
import me.creese.sport.R;
import me.creese.sport.data.FullTable;
import me.creese.sport.map.Route;
import me.creese.sport.models.RideModel;
import me.creese.sport.ui.fragments.PageStatFragment;
import me.creese.sport.util.UpdateInfo;

public class FullDataActivity extends AppCompatActivity {

    private RideModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_data);

        if(savedInstanceState == null) {
            SQLiteDatabase database = App.get().getData().getReadableDatabase();

            Cursor cursor = database.query(FullTable.NAME_TABLE,null,null,
                    null,null,null,null);
            model = new RideModel();
            if(cursor.moveToFirst()) {
                model.setDistance(cursor.getDouble(cursor.getColumnIndex(FullTable.DISTANCE)));
                model.setCalories(cursor.getInt(cursor.getColumnIndex(FullTable.CALORIES)));
                model.setTimeRide(cursor.getLong(cursor.getColumnIndex(FullTable.TIME)));
                model.setMaxSpeed(cursor.getInt(cursor.getColumnIndex(FullTable.MAX_SPEED)));
            }

            cursor.close();


            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.root_full_stat,PageStatFragment.newInstanse(0,model))
                    .commit();

        }
    }
}
