package me.creese.sport.models.adapters;

import android.support.annotation.NonNull;
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
import me.creese.sport.models.RouteModel;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryHolder> {
    private final ArrayList<RouteModel> items;

    public HistoryAdapter() {
        items = new ArrayList<>();
    }

    public void addItem(RouteModel model) {
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
        SimpleDateFormat format = new SimpleDateFormat("dd.mm.yyyy",Locale.getDefault());
        Date date = new Date();
        date.setTime(items.get(i).getTime());
        historyHolder.date.setText(format.format(date));
        historyHolder.dist.setText(Route.makeDistance(items.get(i).getDistance()));
        historyHolder.time.setText(items.get(i).getTimeRoute()+"");
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class HistoryHolder extends RecyclerView.ViewHolder {
        private final TextView time;
        private final TextView date;
        private final TextView dist;

        public HistoryHolder(@NonNull View itemView) {
            super(itemView);

            time = itemView.findViewById(R.id.history_time);
            date = itemView.findViewById(R.id.history_date);
            dist = itemView.findViewById(R.id.history_dist);
        }
    }
}
