package me.creese.sport.ui.custom_view;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import me.creese.sport.R;

public class BevelLayout extends FrameLayout {


    private LinearLayout buttons;
    private BevelView bevel;

    public BevelLayout(@NonNull @android.support.annotation.NonNull Context context) {
        super(context);
    }

    public BevelLayout(@NonNull @android.support.annotation.NonNull Context context, @Nullable @android.support.annotation.Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public BevelLayout(@NonNull @android.support.annotation.NonNull Context context, @Nullable @android.support.annotation.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BevelLayout(@NonNull @android.support.annotation.NonNull Context context, @Nullable @android.support.annotation.Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        bevel = findViewById(R.id.bevel);
        buttons = findViewById(R.id.main_buttons_container);
        int childCount = buttons.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = buttons.getChildAt(i);
            float x = view.getX() + view.getWidth() / 2;
            float y = view.getHeight() / 2;
            ((LinearLayout.LayoutParams) view.getLayoutParams()).topMargin = (int) (bevel.calcY(x) + y);
        }

    }

}
