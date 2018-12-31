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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Calendar;

import me.creese.sport.App;
import me.creese.sport.R;
import me.creese.sport.models.GoalsModel;
import me.creese.sport.models.adapters.GoalsAdapter;

public class GoalsFragment extends Fragment implements View.OnClickListener {
    private GoalsAdapter adapter;
    private RecyclerView recycleGoals;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout view = (LinearLayout) inflater.inflate(R.layout.goals_fragment,null);
        updateRecycle(view);

        view.findViewById(R.id.goals_btn_add).setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        View view = getView();
        EditText textGoals = view.findViewById(R.id.goals_text);
        String stringGoals = textGoals.getText().toString();
        if(stringGoals.equals("")) {
            Toast.makeText(getContext(),R.string.enter_goal,Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            int goalsCount = Integer.valueOf(stringGoals);
            if(goalsCount < 0) throw new NumberFormatException();
            CheckBox checkDay = view.findViewById(R.id.goals_day);
            CheckBox checkWeek = view.findViewById(R.id.goals_week);
            CheckBox checkMonth = view.findViewById(R.id.goals_month);
            CheckBox checkYear = view.findViewById(R.id.goals_year);

            if(!checkDay.isChecked() && !checkWeek.isChecked() && !checkMonth.isChecked() && !checkYear.isChecked()) {
                Toast.makeText(getContext(),R.string.change_time,Toast.LENGTH_SHORT).show();
                return;
            }
            long timeGoal = 0;
            int type = 0;
            RadioGroup goalsTypes = view.findViewById(R.id.goals_types);
            switch (goalsTypes.getCheckedRadioButtonId()) {
                case R.id.goals_radio_dist:
                    type = GoalsModel.DISTANCE;
                    break;
                case R.id.goals_radio_time:
                    type = GoalsModel.TIME;
                    break;
                case R.id.goals_radio_calories:
                    type = GoalsModel.CALORIES;
                    break;
            }
            if(checkDay.isChecked()) {
                timeGoal = System.currentTimeMillis()+(24 * 60 * 60 * 1000);

                GoalsModel goalsModel = new GoalsModel(timeGoal, goalsCount, type, -1);
                App.get().getData().insertGoals(goalsModel);
            }
            if(checkWeek.isChecked()) {
                timeGoal = System.currentTimeMillis()+ (7 * 24 * 60 * 60 * 1000);

                GoalsModel goalsModel = new GoalsModel(timeGoal, goalsCount, type, -1);
                App.get().getData().insertGoals(goalsModel);
            }
            if(checkMonth.isChecked()) {
                Calendar dateAfter = Calendar.getInstance();
                dateAfter.add(Calendar.MONTH,1);

                timeGoal = dateAfter.getTimeInMillis();

                GoalsModel goalsModel = new GoalsModel(timeGoal, goalsCount, type, -1);
                App.get().getData().insertGoals(goalsModel);
            }

            if(checkYear.isChecked()) {
                Calendar dateAfter = Calendar.getInstance();
                dateAfter.add(Calendar.YEAR,1);

                timeGoal = dateAfter.getTimeInMillis();

                GoalsModel goalsModel = new GoalsModel(timeGoal, goalsCount, type, -1);
                App.get().getData().insertGoals(goalsModel);
            }

            updateRecycle(view);


        } catch (NumberFormatException e) {
            Toast.makeText(getContext(),R.string.wrong_number,Toast.LENGTH_SHORT).show();
        }
    }

    private void updateRecycle(View view) {
        if(App.get().getGoals().size() > 0) {
            view.findViewById(R.id.goals_current_goals).setVisibility(View.VISIBLE);
            if(adapter == null) {
                adapter = new GoalsAdapter();
                recycleGoals = view.findViewById(R.id.goals_recycle);
                recycleGoals.setLayoutManager(new LinearLayoutManager(getContext()));
                recycleGoals.setAdapter(adapter);
            }

            adapter.notifyDataSetChanged();

        }
    }
}
