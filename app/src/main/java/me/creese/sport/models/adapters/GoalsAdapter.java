package me.creese.sport.models.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import me.creese.sport.App;
import me.creese.sport.R;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.GoalHolder> {

    @NonNull
    @Override
    public GoalHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_goal, viewGroup, false);
        return new GoalHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalHolder goalHolder, int i) {
        int count = App.get().getGoals().get(i).getCount();
        goalHolder.goalProgress.setMax(count);
        goalHolder.goalProgress.setProgress(App.get().getGoals().get(i).getPassCount());

        goalHolder.goalTitle.setText((i+1)+". Пробегать или проезжать "+count);

    }

    @Override
    public int getItemCount() {
        return App.get().getGoals().size();
    }

    public class GoalHolder extends RecyclerView.ViewHolder {
        private final SeekBar goalProgress;
        private final TextView goalTitle;

        public GoalHolder(@NonNull View itemView) {
            super(itemView);
            goalProgress = itemView.findViewById(R.id.goal_item_progress);
            goalTitle = itemView.findViewById(R.id.goal_item_title);
        }
    }
}
