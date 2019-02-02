package me.creese.sport.ui.custom_view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import me.creese.sport.R;
import me.creese.sport.util.P;

public class ScrollButtons extends HorizontalScrollView {
    private static final String TAG = ScrollButtons.class.getSimpleName();
    private LinearLayout buttonsContainer;
    private BevelView bevel;

    public ScrollButtons(Context context) {
        super(context);
        init();
    }

    public ScrollButtons(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ScrollButtons(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ScrollButtons(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        post(new Runnable() {
            @Override
            public void run() {
                whenCreate();
             }
        });
    }

    private void whenCreate() {
        buttonsContainer = findViewById(R.id.main_buttons_container);
        bevel = ((BevelLayout) getParent()).findViewById(R.id.bevel);



        if(buttonsContainer.getWidth()  > getWidth() ) {
            setPadding(P.getPixelFromDP(20),getPaddingTop(),P.getPixelFromDP(20),getPaddingBottom());
        } else {
            ((LayoutParams) buttonsContainer.getLayoutParams()).gravity = Gravity.CENTER_HORIZONTAL;
        }


    }



    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if (buttonsContainer != null) {
            int childCount = buttonsContainer.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = buttonsContainer.getChildAt(i);
                float yCalc = bevel.calcY(-getScrollX()+(child.getX()+child.getWidth()/2)) + child.getHeight()/2;
                child.setY(yCalc);

            }
        }
    }
}
