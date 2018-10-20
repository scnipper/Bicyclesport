package me.creese.sport.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.io.File;
import java.io.FileFilter;

import me.creese.sport.App;
import me.creese.sport.R;
import me.creese.sport.models.RouteModel;
import me.creese.sport.models.adapters.ListRouteAdapter;
import me.creese.sport.util.FilterRoutes;

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
        File[] list = App.get().getFiles().getCacheDir().listFiles(new FilterRoutes());
        for (int i = 0; i < list.length; i++) {

            RouteModel model = App.get().getGson().fromJson(
                    new String(App.get().getFiles().read(list[i].getAbsolutePath())),RouteModel.class);

            adapter.addItem(model);
        }
        adapter.notifyDataSetChanged();

    }
}
