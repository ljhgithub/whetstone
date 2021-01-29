package com.pysun.common.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class ReverseStroke extends View {

    private Paint paint;
    private int offsetX = 100;
    private int offsetY = 140;

    private int color = 0xfff7f7f7;

    public ReverseStroke(Context context) {
        this(context, null);
    }

    public ReverseStroke(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReverseStroke(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.RED);
        paint.setStrokeWidth(2);

    }

    public void setParams(int color, int offsetX, int offsetY) {
        this.color = color;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        invalidate();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        Path path = new Path();
        RectF rectF = new RectF();
        int top = 0;

        rectF.set(-offsetX, top, width + offsetX, top + offsetY);

        path.addArc(rectF, 0, 180);


        Path path2 = new Path();
        RectF rectF2 = new RectF();

        rectF2.set(0, top + offsetY / 2, width, height+offsetY);
        path2.addRect(rectF2, Path.Direction.CCW);

        path.addRect(rectF2, Path.Direction.CCW);

        path2.op(path, Path.Op.INTERSECT);

        int a = width / 2 + offsetX;
        int b = offsetY / 2;
        path2.offset(0, -(top + b + getLineArcPointByX(-width / 2, a, b, b)));

        paint.setColor(color);
        canvas.drawPath(path2, paint);
//        paint.setColor(Color.BLACK);


//        canvas.drawLine(20, top, 20, top + b + getLineArcPointByX(-width / 2, a, b, b), paint);
    }

    private long getLineArcPointByX(int x, int a, int b, int defaultY) {

        double d = defaultY;
        try {
            d = Math.sqrt((1 - Math.pow(x, 2) / Math.pow(a, 2)) * Math.pow(b, 2));
        } catch (Exception e) {


        }


        return Math.round(d);
    }
}
