package com.pysun.common.ui.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by ljh on 2015/9/2 0002.
 */
public class PullOutLayout extends RelativeLayout {
    private Paint paint;
    private Context context;
    private int width;
    private int height;
    private float left, top, right, bottom;
    private int x, y, radius;
    private static final int CIRCLE = 1;
    private static final int RECT = 2;
    private static final int ROUNDRECT = 3;
    private int TYPE = 0;
    private Path path;


    public PullOutLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PullOutLayout(Context context) {
        super(context);
        init(context);


    }

    private void init(Context context) {
        this.context = context;
        this.paint = new Paint();
        this.paint.setAntiAlias(true); //消除锯齿
        this.paint.setColor(Color.BLACK);
        this.paint.setAlpha(150);
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setStrokeWidth(1);
        this.path = new Path();
        setWillNotDraw(false);

    }

    public void setPaintColor(int color, int alpha) {
        this.paint.setColor(color);
        this.paint.setAlpha(alpha);
        invalidate();
    }

    public void setRectLocation(float left, float top, float right, float bottom) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        TYPE = RECT;
        invalidate();
    }

    public void setCircleLocation(int x, int y, int radius) {
        this.radius = radius;
        this.x = x;
        this.y = y;
        TYPE = CIRCLE;
        invalidate();
    }

    public void setRoundRectLocation(float left, float top, float right, float bottom, int radius) {
        this.left = left;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.radius = radius;
        TYPE = ROUNDRECT;
        invalidate();
    }


    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        width = getWidth();
        height = getHeight();

        switch (TYPE) {
            default:
            case CIRCLE:
                drawCircle(canvas);
                break;
            case RECT:
                drawRect(canvas);
                break;
            case ROUNDRECT:
                drawRoundRect(canvas);
                break;

        }

    }

    private void drawCircle(Canvas canvas) {

        path.addCircle(x, y, radius, Path.Direction.CCW);
        path.lineTo(width, y);
        path.lineTo(width, height);
        path.lineTo(0, height);
        path.lineTo(0, 0);
        path.lineTo(width, 0);
        path.lineTo(width, y);
        path.close();
        canvas.drawPath(path, paint);

    }

    private void drawRect(Canvas canvas) {
        path.addRect(new RectF(left, top, right, bottom), Path.Direction.CCW);
        path.lineTo(left, 0);
        path.lineTo(width, 0);
        path.lineTo(width, height);
        path.lineTo(0, height);
        path.lineTo(0, 0);
        path.lineTo(left, 0);
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawRoundRect(Canvas canvas) {
        path.addRoundRect(new RectF(left, top, right, bottom), radius, radius, Path.Direction.CCW);
        path.lineTo(right - radius, 0);
        path.lineTo(width, 0);
        path.lineTo(width, height);
        path.lineTo(0, height);
        path.lineTo(0, 0);
        path.lineTo(right - radius, 0);
        path.close();


//        path.moveTo(0,0);
//        path.lineTo(left + radius, 0);
//        path.lineTo(left + radius, top);
//        path.arcTo(new RectF(left, top, left + radius * 2, top + radius * 2), 270, -90);
//        path.lineTo(left, bottom - radius);
//        path.arcTo(new RectF(left, bottom - radius * 2, left + radius * 2, bottom), 180, -90);
//        path.lineTo(right - radius, bottom);
//        path.arcTo(new RectF(right - radius * 2, bottom - radius * 2, right, bottom), 90, -90);
//        path.lineTo(right, top + radius);
//        path.arcTo(new RectF(right - radius * 2, top, right, top + radius * 2), 0, -90);
//        path.lineTo(left + radius, top);
//        path.lineTo(left + radius, 0);
//        path.lineTo(width, 0);
//        path.lineTo(width, height);
//        path.lineTo(0, height);
//        path.close();
        canvas.drawPath(path, paint);
    }
}
