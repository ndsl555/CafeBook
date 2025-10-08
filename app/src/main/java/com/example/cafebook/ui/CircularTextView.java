package com.example.cafebook.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;


public class CircularTextView extends androidx.appcompat.widget.AppCompatTextView {

    private Paint paint;

    public CircularTextView(Context context) {
        super(context);
        init();
    }

    public CircularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(getResources().getColor(com.example.cafebook.R.color.ave_color));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        int radius = Math.min(centerX, centerY);
        canvas.drawCircle(centerX, centerY, radius, paint);
        super.onDraw(canvas);
    }
}
