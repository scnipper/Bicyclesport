package me.creese.sport.ui.custom_view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class BevelView extends FrameLayout {
    private Paint paint;
    private Path path;

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

    private void init() {
        setWillNotDraw(false);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        path = new Path();
    }

    public float calcY(float x) {
        float y1 = getHeight() * 0.6f;
        float m = (y1 * -1) / getWidth();
        return m * x + y1;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        path.reset();
        path.moveTo(0, getHeight());
        path.lineTo(0, calcY(0));
        path.lineTo(getWidth(), calcY(getWidth()));
        path.lineTo(getWidth(), getHeight());
        canvas.drawPath(path, paint);
    }
}
