package me.creese.sport.models.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import me.creese.sport.App;
import me.creese.sport.R;
import me.creese.sport.map.Route;
import me.creese.sport.models.GoalsModel;
import me.creese.sport.util.UpdateInfo;

public class GoalsAdapter extends RecyclerView.Adapter<GoalsAdapter.GoalHolder> {

    private static final String TAG = GoalsAdapter.class.getSimpleName();

    @NonNull
    @Override
    public GoalHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_goal, viewGroup, false);
        return new GoalHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final GoalHolder goalHolder, final int i) {
        final int count = App.get().getGoals().get(i).getCount();
        final int passCount = App.get().getGoals().get(i).getPassCount();
        int type = App.get().getGoals().get(i).getType();
        long time = App.get().getGoals().get(i).getTime();
        goalHolder.goalProgress.setMax(count);
        goalHolder.goalProgress.setProgress(passCount);

        String textGoal = "";
        String textMaxGoal = "";
        String text = "";

        if(type == GoalsModel.DISTANCE) {
            textGoal = Route.makeDistance(passCount);
            textMaxGoal = Route.makeDistance(count);
            text = (i + 1) + ". Проезжать расстояние " + textMaxGoal;
        }
        if(type == GoalsModel.TIME) {
            textGoal = UpdateInfo.formatTime(passCount*60000);
            textMaxGoal = UpdateInfo.formatTime(count*60000);
            text = (i + 1) + ". Потратить время на тренировку " + textMaxGoal;
        }
        if(type == GoalsModel.CALORIES) {
            text = (i + 1) + ". Сжечь калории " + textMaxGoal;
            textGoal = passCount+" ккал";
            textMaxGoal = count+" ккал";
        }

        goalHolder.goalLeftTime.setText("Осталось время - "+UpdateInfo.formatTime(time - System.currentTimeMillis()));

        goalHolder.goalTitle.setText(text);
        goalHolder.goalMaxValue.setText(textMaxGoal);

        goalHolder.goalValue.setText(textGoal);
        goalHolder.goalValue.post(new Runnable() {
            @Override
            public void run() {
                ((LinearLayout.LayoutParams) goalHolder.goalValue.getLayoutParams()).leftMargin = (int) Math.max(0, Math.min(goalHolder.goalProgress.getWidth() - goalHolder.goalValue.getWidth(), (((float) passCount / count) * goalHolder.goalProgress.getWidth()) - goalHolder.goalValue.getWidth() / 2.f));

                goalHolder.goalValue.requestLayout();
            }
        });

        goalHolder.golaDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.get().getData().removeGoal(App.get().getGoals().get(i).getId());
                Toast.makeText(v.getContext(),"Цель удалена",Toast.LENGTH_SHORT).show();
                App.get().getGoals().remove(i);
                notifyItemRemoved(i);
            }
        });


    }

    @Override
    public int getItemCount() {
        return App.get().getGoals().size();
    }

    public class GoalHolder extends RecyclerView.ViewHolder {
        private final SeekBar goalProgress;
        private final TextView goalTitle;
        private final TextView goalMaxValue;
        private final TextView goalValue;
        private final TextView goalLeftTime;
        private final ImageButton golaDeleteBtn;

        public GoalHolder(@NonNull View itemView) {
            super(itemView);
            goalProgress = itemView.findViewById(R.id.goal_item_progress);
            goalProgress.setEnabled(false);
            goalTitle = itemView.findViewById(R.id.goal_item_title);
            goalMaxValue = itemView.findViewById(R.id.goal_item_max_value);
            goalValue = itemView.findViewById(R.id.goal_item_curr_value);
            goalLeftTime = itemView.findViewById(R.id.goal_item_left_time);
            golaDeleteBtn = itemView.findViewById(R.id.goal_item_delete_btn);
        }
    }
}
