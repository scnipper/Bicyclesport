package me.creese.sport.ui.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import me.creese.sport.App;
import me.creese.sport.R;
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

        ArrayList<RouteModel> models = App.get().getData().addItems(false);

        for (RouteModel model : models) {
            adapter.addItem(model);
        }
    }


}
