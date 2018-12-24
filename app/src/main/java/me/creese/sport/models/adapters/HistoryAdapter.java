package me.creese.sport.models.adapters;

import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import me.creese.sport.R;
import me.creese.sport.map.Point;
import me.creese.sport.map.Route;
import me.creese.sport.models.RouteAndRide;
import me.creese.sport.ui.activities.StartActivity;
import me.creese.sport.ui.fragments.HistoryFragment;
import me.creese.sport.util.UpdateInfo;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryHolder> {
    private final ArrayList<RouteAndRide> items;
    private final HistoryFragment historyFragment;

    public HistoryAdapter(HistoryFragment historyFragment) {
        this.historyFragment = historyFragment;
        items = new ArrayList<>();
    }

    public void addItem(RouteAndRide model) {
        items.add(model);
    }

    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_route, viewGroup, false);
        return new HistoryHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryHolder historyHolder, int i) {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy",Locale.getDefault());
        Date date = new Date();
        date.setTime(items.get(i).getRouteModel().getTime());
        historyHolder.date.setText(format.format(date));
        historyHolder.dist.setText(Route.makeDistance(items.get(i).getRideModel().getDistance()));
        historyHolder.time.setText(UpdateInfo.formatTime(items.get(i).getRideModel().getTimeRide()));

        if(items.get(i).getRideModel().getRideAdress() == null) {
            items.get(i).getRideModel().setRideAdress("");
            Geocoder gc = new Geocoder(historyHolder.date.getContext(), Locale.getDefault());
            int last = items.get(i).getRouteModel().getPoints().size()-1;
            if(last > -1) {
                try {
                    Point p1 = items.get(i).getRouteModel().getPoints().get(0);
                    Point p2 = items.get(i).getRouteModel().getPoints().get(last);
                    List<Address> fromLocationStart = gc.getFromLocation(p1.getLatLng().latitude, p1.getLatLng().longitude, 1);

                    List<Address> fromLocationEnd = gc.getFromLocation(p2.getLatLng().latitude, p2.getLatLng().longitude, 1);

                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(fromLocationStart.get(0).getThoroughfare()).append(" ").append(fromLocationStart.get(0).getSubThoroughfare());

                    if (!fromLocationStart.get(0).getLocality().equals(fromLocationEnd.get(0).getLocality()))
                        stringBuilder.append(", ").append(fromLocationStart.get(0).getLocality());
                    stringBuilder.append(" - ").append(fromLocationEnd.get(0).getThoroughfare()).append(" ").append(fromLocationEnd.get(0).getSubThoroughfare()).append(", ").append(fromLocationEnd.get(0).getLocality());
                    items.get(i).getRideModel().setRideAdress(stringBuilder.toString());


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        historyHolder.name.setText(items.get(i).getRideModel().getRideAdress());
        historyHolder.idModel = i;


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class HistoryHolder extends RecyclerView.ViewHolder {
        private final TextView time;
        private final TextView date;
        private final TextView dist;
        private final TextView name;
        private int idModel;

        public HistoryHolder(@NonNull final View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*Intent intent = new Intent(itemView.getContext(),StartActivity.class);
                    intent.putExtra(RouteAndRide.class.getSimpleName(),items.get(idModel));
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    itemView.getContext().startActivity(intent);*/
                    ((StartActivity) historyFragment.getActivity()).showStatFragment(items.get(getAdapterPosition()));
                }
            });

            time = itemView.findViewById(R.id.item_route_time_route);
            date = itemView.findViewById(R.id.item_route_date_route);
            dist = itemView.findViewById(R.id.item_route_dist_route);
            name = itemView.findViewById(R.id.item_route_name_route);
        }
    }
}
