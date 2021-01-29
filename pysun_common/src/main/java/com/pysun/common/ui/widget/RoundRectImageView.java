package com.pysun.common.ui.widget;
/*
 * Create by yk on 2019/1/17
 * com.duia.ssx.lib_common.ui.widget
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import androidx.appcompat.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.duia.ssx.pysun_common.R;

public class RoundRectImageView extends AppCompatImageView {
    private Paint bitmapPaint;
    private RectF rect;
    private float roundRadius;

    public RoundRectImageView(Context context) {
        this(context,null);
    }

    public RoundRectImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RoundRectImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs,R.styleable.RoundRectImageView);
        roundRadius = array.getDimensionPixelSize(R.styleable.RoundRectImageView_roundRaidus,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX,10, getResources().getDisplayMetrics())) / 2;
        array.recycle();
        initial();
    }

    private void initial() {
        bitmapPaint = new Paint();
        bitmapPaint.setAntiAlias(true);
        bitmapPaint.setDither(true);
        bitmapPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        rect = new RectF();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        rect.set(0,0,getWidth(),getHeight());
        if(getDrawable() == null)
            super.onDraw(canvas);
        else {
            BitmapShader shader = new BitmapShader(drawable2Bitmap(getDrawable()), Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
            bitmapPaint.setShader(shader);
            canvas.drawRoundRect(rect, roundRadius, roundRadius, bitmapPaint);
        }
    }

    private Bitmap drawable2Bitmap(Drawable drawable){
        if(drawable instanceof BitmapDrawable)
            return ((BitmapDrawable) drawable).getBitmap();
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w,h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return bitmap;
    }
}
