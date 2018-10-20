package me.creese.sport.models.adapters;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import me.creese.sport.App;
import me.creese.sport.R;
import me.creese.sport.activities.ListRoutesActivity;
import me.creese.sport.activities.StartActivity;
import me.creese.sport.models.RouteModel;

public class ListRouteAdapter extends RecyclerView.Adapter<ListRouteAdapter.RouteHolder> {

    private static final String TAG = ListRoutesActivity.class.getSimpleName();
    private final ArrayList<RouteModel> listRoute;

    public ListRouteAdapter() {
        listRoute = new ArrayList<>();
    }

    public void addItem(RouteModel model) {
        listRoute.add(model);
    }

    @NonNull
    @Override
    public RouteHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_route, viewGroup, false);
        return new RouteHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RouteHolder routeHolder, int i) {

        routeHolder.bind(listRoute.get(i));

    }

    @Override
    public int getItemCount() {
        return listRoute.size();
    }

    class RouteHolder extends RecyclerView.ViewHolder {
        private final TextView dateRoute;
        private final TextView nameRoute;
        private final FrameLayout root;
        private RouteModel model;

        RouteHolder(@NonNull View itemView) {
            super(itemView);

            dateRoute = itemView.findViewById(R.id.item_route_date_route);
            nameRoute = itemView.findViewById(R.id.item_route_name_route);
            root = itemView.findViewById(R.id.item_route_root);
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(root.getContext(),StartActivity.class);

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    App.get().setModel(model);
                    root.getContext().startActivity(intent);
                }
            });

        }

        void bind(RouteModel model) {
            this.model = model;
            Calendar date = Calendar.getInstance();
            date.setTimeInMillis(model.getTime());

            dateRoute.setText(date.get(Calendar.HOUR_OF_DAY)+":"+date.get(Calendar.MINUTE)+":"+date.get(Calendar.SECOND)+" "+
                    date.get(Calendar.DAY_OF_MONTH)+"/"+(date.get(Calendar.MONTH)+1)+"/"+date.get(Calendar.YEAR));
            nameRoute.setText(model.getName());
        }
    }
}
