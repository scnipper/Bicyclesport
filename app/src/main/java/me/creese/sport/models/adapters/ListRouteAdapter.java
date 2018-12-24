package me.creese.sport.models.adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.maps.android.SphericalUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.creese.sport.R;
import me.creese.sport.map.Route;
import me.creese.sport.ui.activities.StartActivity;
import me.creese.sport.models.RouteModel;
import me.creese.sport.ui.fragments.ListRoutesFragment;

public class ListRouteAdapter extends RecyclerView.Adapter<ListRouteAdapter.RouteHolder> {

    private static final String TAG = ListRouteAdapter.class.getSimpleName();
    private final ArrayList<RouteModel> listRoute;
    private final ListRoutesFragment listRoutesFragment;

    public ListRouteAdapter(ListRoutesFragment listRoutesFragment) {
        this.listRoutesFragment = listRoutesFragment;
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
        private final TextView distRoute;
        private final TextView timeRoute;
        private RouteModel model;

        RouteHolder(@NonNull View itemView) {
            super(itemView);

            dateRoute = itemView.findViewById(R.id.item_route_date_route);
            timeRoute = itemView.findViewById(R.id.item_route_time_route);
            nameRoute = itemView.findViewById(R.id.item_route_name_route);
            distRoute = itemView.findViewById(R.id.item_route_dist_route);
            root = itemView.findViewById(R.id.item_route_root);
            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((StartActivity) listRoutesFragment.getActivity()).getMapWork().showRoute(model);
                    listRoutesFragment.getFragmentManager()
                            .beginTransaction()
                            .remove(listRoutesFragment)
                            .commit();
                }
            });

        }

        void bind(RouteModel model) {
            this.model = model;
            Date date = new Date(model.getTime());
            SimpleDateFormat formatDate = new SimpleDateFormat("dd.MM.yyyy",Locale.getDefault());
            SimpleDateFormat formatTime = new SimpleDateFormat("H:mm:ss",Locale.getDefault());

            if(model.getTmpDistance() == 0) {
                if(model.getPoints().size() > 1) {
                    for (int i = 1; i < model.getPoints().size(); i++) {
                        model.setTmpDistance(model.getTmpDistance()
                                +SphericalUtil.computeDistanceBetween(model.getPoints().get(i-1).getLatLng(),
                                model.getPoints().get(i).getLatLng()));
                    }
                }
            }

            distRoute.setText(Route.makeDistance(model.getTmpDistance()));
            timeRoute.setText(formatTime.format(date));
            dateRoute.setText(formatDate.format(date));
            nameRoute.setText(model.getName());
        }
    }
}
