package me.creese.sport.util;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import me.creese.sport.App;
import me.creese.sport.data.DataHelper;
import me.creese.sport.data.PointsTable;
import me.creese.sport.data.RoutesTable;
import me.creese.sport.models.RouteModel;
import me.creese.sport.models.adapters.HistoryAdapter;

public class RouteFromDb {
    public static ArrayList<RouteModel> addItems(boolean isHistory) {
        SQLiteDatabase db = App.get().getData().getReadableDatabase();
        ArrayList<RouteModel> models = new ArrayList<>();

        Cursor cursor = db.query(RoutesTable.NAME_TABLE, null, RoutesTable.IS_RIDE+"='"+(isHistory?1:0)+"'",
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

                RouteModel model = new RouteModel(cursor.getString(cursor.getColumnIndex(RoutesTable.NAME)),
                        points, cursor.getLong(cursor.getColumnIndex(RoutesTable.TIME)),
                        cursor.getDouble(cursor.getColumnIndex(RoutesTable.DEST)),
                        cursor.getInt(cursor.getColumnIndex(RoutesTable.IS_RIDE)) == 1,
                        cursor.getLong(cursor.getColumnIndex(RoutesTable.TIME_ROUTE)));

                models.add(model);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

       // adapter.notifyDataSetChanged();
        return models;
    }
}
