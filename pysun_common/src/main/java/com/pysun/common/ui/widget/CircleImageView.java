package com.pysun.common.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatImageView;

public class CircleImageView extends AppCompatImageView {

    private Paint imagePaint;
    private Shader mShader;
    //最终控件的大小由控件指定宽高的最小值决定
    private int circleRadius;
    //缩放
    private Matrix mMatrix;
    private Bitmap bitmap;

    public CircleImageView(Context context) {
        this(context,null);
    }

    public CircleImageView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CircleImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        imagePaint = new Paint();
        imagePaint.setAntiAlias(true);
        imagePaint.setDither(true);
        mMatrix = new Matrix();
    }

    //计算缩放程度
    private void
    initScale(Bitmap bitmap){
        imagePaint = new Paint();
        imagePaint.setAntiAlias(true);
        imagePaint.setDither(true);
        mMatrix = new Matrix();
        float scaleX,scaleY;
        scaleX = (2* circleRadius) *1.0f / bitmap.getWidth();
        scaleY = (2* circleRadius) *1.0f / bitmap.getHeight();
        scaleX = Math.min(scaleX,scaleY);
        mShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mMatrix.setScale(scaleX,scaleX);
        mShader.setLocalMatrix(mMatrix);
        imagePaint.setShader(mShader);
    }

    //drawable转bitmap
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec),height = MeasureSpec.getSize(heightMeasureSpec);
        circleRadius = Math.min(width, height) / 2;
        setMeasuredDimension(2* circleRadius, 2*circleRadius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if(getDrawable() != null){
            initScale(drawable2Bitmap(getDrawable()));
        }
        if(getDrawable() != null)
            canvas.drawCircle(circleRadius,circleRadius,circleRadius,imagePaint);
        else
            super.onDraw(canvas);
    }
}
