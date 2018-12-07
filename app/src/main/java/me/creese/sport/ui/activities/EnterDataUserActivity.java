package me.creese.sport.ui.activities;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import me.creese.sport.App;
import me.creese.sport.R;
import me.creese.sport.data.DataHelper;
import me.creese.sport.data.UserTable;
import me.creese.sport.util.UserData;

public class EnterDataUserActivity extends AppCompatActivity {

    private TextView weight;
    private TextView height;
    private TextView birth;
    private Spinner sex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_data_user);

        Toolbar toolbar = findViewById(R.id.enter_data_toolbar);

        weight = findViewById(R.id.enter_data_wieght);
        weight.setText(UserData.WEIGHT + "");
        height = findViewById(R.id.enter_data_height);
        height.setText(UserData.HEIGHT + "");
        birth = findViewById(R.id.enter_data_age);
        birth.setText(UserData.BIRTH_DATE + "");

        sex = findViewById(R.id.enter_data_sex);
        sex.setSelection(UserData.SEX);


    }

    public void saveData(View v) {


        if (weight.getText().equals("") || height.getText().equals("") || birth.getText().equals("")) {
            Toast.makeText(this, "Не введены все данные", Toast.LENGTH_SHORT).show();
            return;
        }


        SQLiteDatabase database = App.get().getData().getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        UserData.HEIGHT = Double.valueOf(height.getText().toString());
        UserData.WEIGHT = Double.valueOf(weight.getText().toString());
        UserData.BIRTH_DATE = Long.valueOf(birth.getText().toString());
        UserData.SEX = (int) sex.getSelectedItemId();
        contentValues.put(UserTable.HEIGHT, UserData.HEIGHT);
        contentValues.put(UserTable.WIEGHT, UserData.WEIGHT);
        contentValues.put(UserTable.TIME_BIRTH, UserData.BIRTH_DATE);
        contentValues.put(UserTable.SEX, UserData.SEX);

        int r = database.update(UserTable.NAME_TABLE, contentValues, DataHelper.ID + "=1", null);

        if (r > 0) Toast.makeText(this, "Данные успешно сохранены", Toast.LENGTH_SHORT).show();
    }
}
