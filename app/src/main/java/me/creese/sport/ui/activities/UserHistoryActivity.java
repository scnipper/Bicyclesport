package me.creese.sport.ui.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import me.creese.sport.App;
import me.creese.sport.R;
import me.creese.sport.models.RideModel;
import me.creese.sport.models.RouteAndRide;
import me.creese.sport.models.RouteModel;
import me.creese.sport.models.adapters.HistoryAdapter;

public class UserHistoryActivity extends AppCompatActivity {

    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_history);

        RecyclerView recyclerView = findViewById(R.id.recycle_history);
        adapter = new HistoryAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ArrayList<RouteModel> models = App.get().getData().getRoutesModelFromDB(true);
        ArrayList<RideModel> rideModels = App.get().getData().getRidesFromDB();

        for (RideModel rideModel : rideModels) {
            int idRoute = rideModel.getIdRoute();

            for (RouteModel model : models) {
                if(model.getId() == idRoute) {
                    adapter.addItem(new RouteAndRide(rideModel,model));
                    break;
                }
            }
        }
    }


}
