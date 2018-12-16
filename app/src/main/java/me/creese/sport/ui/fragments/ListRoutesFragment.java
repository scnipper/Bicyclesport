package me.creese.sport.ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import me.creese.sport.App;
import me.creese.sport.R;
import me.creese.sport.models.RouteModel;
import me.creese.sport.models.adapters.ListRouteAdapter;

public class ListRoutesFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_routes,null);
        ((TextView) view.findViewById(R.id.head_text_list_routes)).setText("Маршруты");
        RecyclerView recyclerView = view.findViewById(R.id.list_routes);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ListRouteAdapter adapter = new ListRouteAdapter();
        recyclerView.setAdapter(adapter);

        ArrayList<RouteModel> models = App.get().getData().getRoutesModelFromDB(false);

        for (RouteModel model : models) {
            adapter.addItem(model);
        }
        return view;
    }
}
