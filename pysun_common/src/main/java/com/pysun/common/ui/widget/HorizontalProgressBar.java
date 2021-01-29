package com.pysun.common.ui.widget;
/*
 * Create by yk on 2018/11/27
 * com.duia.ssx.lib_common.ui.widget
 */

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.duia.ssx.pysun_common.R;

public class HorizontalProgressBar extends View{

    private int progressedColorStart,progressedColorEnd,unProgressColor,progress;
    private Paint mProgressPaint,mUnProgressPaint;
    public HorizontalProgressBar(Context context) {
        this(context,null);
    }

    public HorizontalProgressBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public HorizontalProgressBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.HorizontalProgressBar);
        progress = arr.getInteger(R.styleable.HorizontalProgressBar_progress, 50);
        progressedColorStart = arr.getColor(R.styleable.HorizontalProgressBar_startColor, Color.argb(0xFF,0x22,0xBC,0xF0));
        progressedColorEnd = arr.getColor(R.styleable.HorizontalProgressBar_endColor , Color.argb(0xFF,0x23,0xC7,0x8C));
        unProgressColor = arr.getColor(R.styleable.HorizontalProgressBar_unProgressColor,Color.rgb(0xF2,0xF2,0xF2));
        arr.recycle();
        init();
    }

    private void init(){
        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setDither(true);

        mUnProgressPaint = new Paint();
        mUnProgressPaint.setAntiAlias(true);
        mUnProgressPaint.setDither(true);
        mUnProgressPaint.setColor(unProgressColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mProgressPaint.setStrokeWidth(getHeight());
        mProgressPaint.setShader(new LinearGradient(0f,0f,(progress * 1.0f /100)* getWidth(),0f,progressedColorStart,progressedColorEnd, Shader.TileMode.CLAMP));
        mUnProgressPaint.setStrokeWidth(getHeight());
        canvas.drawLine(0,0, (progress * 1.0f /100)* getWidth(),0,mProgressPaint);
        canvas.drawLine((progress * 1.0f /100)* getWidth(),0,getWidth(),0,mUnProgressPaint);
    }


    public void setProgress(int progress){
        this.progress = progress;
        invalidate();
    }
}
