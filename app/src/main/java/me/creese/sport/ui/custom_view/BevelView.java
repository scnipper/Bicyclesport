package me.creese.sport.ui.custom_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BevelView extends FrameLayout {
    public BevelView(@NonNull @android.support.annotation.NonNull Context context) {
        super(context);
        init();
    }

    public BevelView(@NonNull @android.support.annotation.NonNull Context context, @Nullable @android.support.annotation.Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BevelView(@NonNull @android.support.annotation.NonNull Context context, @Nullable @android.support.annotation.Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public BevelView(@NonNull @android.support.annotation.NonNull Context context, @Nullable @android.support.annotation.Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float vert[] = new float[]{0, 0, 0, getHeight() * 0.4f, getWidth(), getHeight(), getWidth(), 0};

        Paint paint = new Paint();
        paint.setColor(0xffffffff);
        paint.setStyle(Paint.Style.FILL);

        Path path = new Path();
        path.reset();
        path.moveTo(0,getHeight());
        path.lineTo(0,getHeight()*0.4f);
        path.lineTo(getWidth(),0);
        path.lineTo(getWidth(),getHeight());
        canvas.drawPath(path,paint);

    }
}
