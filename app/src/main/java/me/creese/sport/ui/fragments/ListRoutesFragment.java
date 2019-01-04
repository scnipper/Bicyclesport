package me.creese.sport.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import me.creese.sport.App;
import me.creese.sport.R;
import me.creese.sport.models.RouteModel;
import me.creese.sport.models.adapters.ListRouteAdapter;

public class ListRoutesFragment extends Fragment {
    private static final String TAG = ListRoutesFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_routes,null);

        ArrayList<RouteModel> models = App.get().getData().getRoutesModelFromDB(false);

        if(models.size() == 0){
            Toast.makeText(getContext(),"Нет маршрутов",Toast.LENGTH_SHORT).show();
            getFragmentManager().beginTransaction().remove(this).commit();
            return null;
        }

        ((TextView) view.findViewById(R.id.head_text_list_routes)).setText("Маршруты");
        RecyclerView recyclerView = view.findViewById(R.id.list_routes);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        recyclerView.setLayoutManager(linearLayoutManager);

        ListRouteAdapter adapter = new ListRouteAdapter(this);
        recyclerView.setAdapter(adapter);



        for (RouteModel model : models) {
            adapter.addItem(model);
        }
        return view;
    }
}
