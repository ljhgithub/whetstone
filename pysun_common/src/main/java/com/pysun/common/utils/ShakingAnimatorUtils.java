package com.pysun.common.utils;
/*
 * Create by yk on 2019-08-22
 * com.duia.ssx.lib_common.utils
 */

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.BounceInterpolator;

public class ShakingAnimatorUtils {

    private static AnimatorSet shakingSet = new AnimatorSet();

    public static void startShaking(View view, int shakingDegree, float maxScale ,int durationMillSeconds){
        shakingSet.cancel();
        view.setPivotX(view.getWidth() * 0.5f);
        view.setPivotY(view.getHeight() * 0.5f);
        ObjectAnimator rotating = ObjectAnimator.ofFloat(view, "rotation", 320, 400);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f , maxScale, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f , maxScale, 1.0f);
        rotating.setRepeatCount(ObjectAnimator.INFINITE);
        rotating.setRepeatMode(ObjectAnimator.REVERSE);
        scaleX.setRepeatCount(ObjectAnimator.INFINITE);
        scaleX.setRepeatMode(ObjectAnimator.RESTART);
        scaleY.setRepeatCount(ObjectAnimator.INFINITE);
        scaleY.setRepeatMode(ObjectAnimator.RESTART);
        shakingSet.play(rotating)
                .with(scaleX)
                .with(scaleY);
        shakingSet.setDuration(durationMillSeconds);
        shakingSet.setInterpolator(new BounceInterpolator());
        shakingSet.start();
    }


    public static void stopShaking(){
        shakingSet.cancel();
    }

}
