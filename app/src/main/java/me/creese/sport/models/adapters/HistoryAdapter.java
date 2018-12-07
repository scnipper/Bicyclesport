package me.creese.sport.models.adapters;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import me.creese.sport.R;
import me.creese.sport.map.Route;
import me.creese.sport.models.RouteAndRide;
import me.creese.sport.models.RouteModel;
import me.creese.sport.ui.activities.StartActivity;
import me.creese.sport.util.UpdateInfo;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryHolder> {
    private final ArrayList<RouteAndRide> items;

    public HistoryAdapter() {
        items = new ArrayList<>();
    }

    public void addItem(RouteAndRide model) {
        items.add(model);
    }

    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.history_item, viewGroup, false);
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
        private int idModel;

        public HistoryHolder(@NonNull final View itemView) {
            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(itemView.getContext(),StartActivity.class);
                    intent.putExtra(RouteAndRide.class.getSimpleName(),items.get(idModel));
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    itemView.getContext().startActivity(intent);
                }
            });

            time = itemView.findViewById(R.id.history_time);
            date = itemView.findViewById(R.id.history_date);
            dist = itemView.findViewById(R.id.history_dist);
        }
    }
}
