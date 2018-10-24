package me.creese.sport.activities;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import me.creese.sport.App;
import me.creese.sport.R;
import me.creese.sport.data.DataHelper;
import me.creese.sport.data.PointsTable;
import me.creese.sport.data.RoutesTable;
import me.creese.sport.models.RouteModel;
import me.creese.sport.models.adapters.ListRouteAdapter;

public class ListRoutesActivity extends AppCompatActivity {

    private ListRouteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_routes);

        RecyclerView recyclerView = findViewById(R.id.list_routes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ListRouteAdapter();
        recyclerView.setAdapter(adapter);

        addItems();
    }

    private void addItems() {
        SQLiteDatabase db = App.get().getData().getReadableDatabase();


        Cursor cursor = db.query(RoutesTable.NAME_TABLE, null, null,
                null, null, null, null);

        if(cursor.moveToFirst()) {
            do {
                ArrayList<LatLng> points = new ArrayList<>();
                Cursor cursor2 = db.query(PointsTable.NAME_TABLE, null,
                        PointsTable.ID_ROUTE + "=" + cursor.getInt(cursor.getColumnIndex(DataHelper.ID)), null, null, null, null);

                if(cursor2.moveToFirst()) {
                    do {
                        int i1 = cursor2.getColumnIndex(PointsTable.LATITUDE);
                        int i2 = cursor2.getColumnIndex(PointsTable.LONGTITUDE);
                        double lat = cursor2.getDouble(i1);
                        double lng = cursor2.getDouble(i2);
                        LatLng point = new LatLng(lat, lng);
                        points.add(point);
                    } while (cursor2.moveToNext());
                }

                cursor2.close();

                RouteModel model = new RouteModel(cursor.getString(cursor.getColumnIndex(RoutesTable.NAME)), points, cursor.getLong(cursor.getColumnIndex(RoutesTable.TIME)));

                adapter.addItem(model);

            } while (cursor.moveToNext());
        }

        cursor.close();

        adapter.notifyDataSetChanged();

    }
}
