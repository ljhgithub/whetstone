package com.pysun.common.ui.widget;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Yasic on 2016/6/1.
 */
public class BubbleView extends RelativeLayout {
    private List<Drawable> drawableList = new ArrayList<>();


    private int riseDuration = 4000;

    private int bottomPadding = dp2pix(35);// 第一张图的高度
    private int originsOffset = dp2pix(10);
    private int originsOffbottom = dp2pix(3);


    private int interval;

    public BubbleView(Context context) {
        super(context);
    }

    public BubbleView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BubbleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BubbleView setDrawableList(List<Drawable> drawableList) {
        this.drawableList = drawableList;
        return this;
    }


    public void bubbleAnimation(int rankWidth, int rankHeight) {
        rankHeight -= bottomPadding;
        int seed = (int) (Math.random() * 3);
        switch (seed) {
            case 0:
                rankWidth -= originsOffset;
                break;
            case 1:
                rankWidth += originsOffset;
                break;
            case 2:
                rankHeight -= originsOffbottom;
                break;
        }
        interval = rankHeight / 5;
        int dWidth = drawableList.get(0).getIntrinsicWidth();
        int dHeight = drawableList.get(0).getIntrinsicHeight();
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dWidth, dHeight);
        ImageView tempImageView = new ImageView(getContext());
        tempImageView.setImageDrawable(drawableList.get(0));
        addView(tempImageView, layoutParams);

        ValueAnimator valueAnimator = getBesselAnimator(tempImageView, rankWidth, rankHeight);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.play(valueAnimator);
        animatorSet.start();
    }

    private ValueAnimator getBesselAnimator(final ImageView imageView, int rankWidth, int rankHeight) {
        float point0[] = new float[2];
        point0[0] = rankWidth / 2;
        point0[1] = rankHeight;

        float point1[] = new float[2];
        point1[0] = (float) ((rankWidth) * (0.10)) + (float) (Math.random() * (rankWidth) * (0.2));
        point1[1] = (float) (rankHeight - Math.random() * rankHeight * (0.5));

        float point2[] = new float[2];
        point2[0] = (float) (Math.random() * rankWidth * 0.8);
        point2[1] = (float) (Math.random() * (rankHeight - point1[1]));

        float point3[] = new float[2];
        point3[0] = (float) (Math.random() * rankWidth * 0.8);
        point3[1] = 0;

        BesselEvaluator besselEvaluator = new BesselEvaluator(point1, point2);
        ValueAnimator valueAnimator = ValueAnimator.ofObject(besselEvaluator, point0, point3);
        valueAnimator.setDuration(riseDuration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float[] currentPosition = new float[2];
                currentPosition = (float[]) animation.getAnimatedValue();
                imageView.setTranslationX(currentPosition[0]);
                imageView.setTranslationY(currentPosition[1]);
                Log.v("dfdfdf", "vvwww:" + currentPosition[1]);
                if (currentPosition[1] > interval * 4.5) {
                    imageView.setImageDrawable(drawableList.get(0));
                    int dWidth = drawableList.get(0).getIntrinsicWidth();
                    int dHeight = drawableList.get(0).getIntrinsicHeight();
                    ViewGroup.LayoutParams params = imageView.getLayoutParams();
                    params.height = dHeight;
                    params.width = dWidth;
                    imageView.setLayoutParams(params);
                } else if (currentPosition[1] > interval * 3) {
                    imageView.setImageDrawable(drawableList.get(1));
                    int dWidth = drawableList.get(1).getIntrinsicWidth();
                    int dHeight = drawableList.get(1).getIntrinsicHeight();
                    ViewGroup.LayoutParams params = imageView.getLayoutParams();
                    params.height = dHeight;
                    params.width = dWidth;
                    imageView.setLayoutParams(params);
                } else if (currentPosition[1] > interval * 1.5) {
                    imageView.setImageDrawable(drawableList.get(2));
                    int dWidth = drawableList.get(2).getIntrinsicWidth();
                    int dHeight = drawableList.get(2).getIntrinsicHeight();
                    ViewGroup.LayoutParams params = imageView.getLayoutParams();
                    params.height = dHeight;
                    params.width = dWidth;
                    imageView.setLayoutParams(params);
                } else if (currentPosition[1] > interval) {
                    imageView.setImageDrawable(drawableList.get(3));
                    int dWidth = drawableList.get(3).getIntrinsicWidth();
                    int dHeight = drawableList.get(3).getIntrinsicHeight();
                    ViewGroup.LayoutParams params = imageView.getLayoutParams();
                    params.height = dHeight;
                    params.width = dWidth;
                    imageView.setLayoutParams(params);
                } else {
                    imageView.setImageDrawable(drawableList.get(4));
                    int dWidth = drawableList.get(4).getIntrinsicWidth();
                    int dHeight = drawableList.get(4).getIntrinsicHeight();
                    ViewGroup.LayoutParams params = imageView.getLayoutParams();
                    params.height = dHeight;
                    params.width = dWidth;
                    imageView.setLayoutParams(params);
                }


            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                removeView(imageView);
                imageView.setImageDrawable(null);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        return valueAnimator;
    }

    public class BesselEvaluator implements TypeEvaluator<float[]> {
        private float point1[] = new float[2], point2[] = new float[2];

        public BesselEvaluator(float[] point1, float[] point2) {
            this.point1 = point1;
            this.point2 = point2;
        }

        @Override
        public float[] evaluate(float fraction, float[] point0, float[] point3) {
            float[] currentPosition = new float[2];
            currentPosition[0] = point0[0] * (1 - fraction) * (1 - fraction) * (1 - fraction)
                    + point1[0] * 3 * fraction * (1 - fraction) * (1 - fraction)
                    + point2[0] * 3 * (1 - fraction) * fraction * fraction
                    + point3[0] * fraction * fraction * fraction;
            currentPosition[1] = point0[1] * (1 - fraction) * (1 - fraction) * (1 - fraction)
                    + point1[1] * 3 * fraction * (1 - fraction) * (1 - fraction)
                    + point2[1] * 3 * (1 - fraction) * fraction * fraction
                    + point3[1] * fraction * fraction * fraction;
            return currentPosition;
        }
    }

    private int dp2pix(int dp) {
        float scale = getResources().getDisplayMetrics().density;
        int pix = (int) (dp * scale + 0.5f);
        return pix;
    }

    private int pix2dp(int pix) {
        float scale = getResources().getDisplayMetrics().density;
        int dp = (int) (pix / scale + 0.5f);
        return dp;
    }
}
