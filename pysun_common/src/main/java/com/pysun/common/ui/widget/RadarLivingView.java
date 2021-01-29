package com.pysun.common.ui.widget;
/*
 * Create by yk on 2018/11/22
 * com.duia.ssx.lib_common.ui.widget
 */

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.animation.AccelerateDecelerateInterpolator;

import androidx.annotation.Nullable;

import com.duia.ssx.pysun_common.R;


public class RadarLivingView extends androidx.appcompat.widget.AppCompatImageView {

    private Paint circlePaint;
    private int paintColor;
    private float imgRadius;
    private boolean playing;
    private int duration;
    private Paint imagePaint;

    public RadarLivingView(Context context) {
        this(context, null);
    }

    public RadarLivingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    //    <attr name="startRadius" format="dimension"/>
//        <attr name="duration" format="integer"/>
//        <attr name="solidColor" format="color"/>
//        <attr name="playing" format="boolean"/>
    public RadarLivingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.RadarLivingView);
        paintColor = arr.getColor(R.styleable.RadarLivingView_solidColor, Color.rgb(0xFF, 0x44, 0x80));
        imgRadius = arr.getDimensionPixelSize(R.styleable.RadarLivingView_imgRadius,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 0, getResources().getDisplayMetrics())) * 0.5f;
        playing = arr.getBoolean(R.styleable.RadarLivingView_playing, false);
        duration = arr.getInteger(R.styleable.RadarLivingView_duration, 2000);
        arr.recycle();
        init();
    }

    public void setPlaying(boolean playing) {
        this.playing = playing;
        invalidate();
    }

    private void init() {
        circlePaint = new Paint();
        circlePaint.setColor(paintColor);
        circlePaint.setAntiAlias(true);
        circlePaint.setDither(true);
        circlePaint.setStyle(Paint.Style.FILL);
    }

    private int alpha;
    private float radius, mCurrentRadius;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        radius = Math.min(getWidth(), getHeight()) * 1.0f / 2;
        if (playing) {
            startAnimation();
        } else
            stopAnimation();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (playing) {
            circlePaint.setAlpha(alpha);
            canvas.drawCircle(radius, radius, mCurrentRadius, circlePaint);
        }
        if (getDrawable() != null) {
            initScale(drawable2Bitmap(getDrawable()));
        }
        if (getDrawable() != null)
            canvas.drawCircle(radius, radius, imgRadius, imagePaint);
        else
            super.onDraw(canvas);
    }

    //drawable转bitmap
    private Bitmap drawable2Bitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable)
            return ((BitmapDrawable) drawable).getBitmap();
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return bitmap;
    }

    //计算缩放程度
    private void initScale(Bitmap bitmap) {
        imagePaint = new Paint();
        imagePaint.setAntiAlias(true);
        imagePaint.setDither(true);
        Matrix mMatrix = new Matrix();
        float scaleX,scaleY;
        scaleX =  imgRadius * 2.0f/ bitmap.getWidth();
        scaleY =  imgRadius * 2.0f/ bitmap.getHeight();
        Shader mShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mMatrix.postTranslate((getWidth() - bitmap.getWidth()) * 0.5f,(getHeight() -  bitmap.getHeight()) * 0.5f);
        mMatrix.postScale(scaleX, scaleY, getWidth() * 0.5f, getHeight() * 0.5f);
        mShader.setLocalMatrix(mMatrix);
        imagePaint.setShader(mShader);
    }

    final AnimatorSet aniSet = new AnimatorSet();

    public void startAnimation() {
        stopAnimation();
        ValueAnimator radiusAnimator = ValueAnimator.ofFloat(imgRadius, radius);
        radiusAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentRadius = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        radiusAnimator.setRepeatCount(ValueAnimator.INFINITE);
        radiusAnimator.setRepeatMode(ValueAnimator.RESTART);
        ValueAnimator alphaAnimator = ValueAnimator.ofInt(200, 0);
        alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                alpha = (int) animation.getAnimatedValue();
            }
        });
        alphaAnimator.setRepeatCount(ValueAnimator.INFINITE);
        alphaAnimator.setRepeatMode(ValueAnimator.RESTART);
        aniSet.play(radiusAnimator)
                .with(alphaAnimator);
        aniSet.setInterpolator(new AccelerateDecelerateInterpolator());
        aniSet.setDuration(duration);
        aniSet.start();
    }

    public void stopAnimation() {
        aniSet.cancel();
        mCurrentRadius = imgRadius;
        invalidate();
    }

}
