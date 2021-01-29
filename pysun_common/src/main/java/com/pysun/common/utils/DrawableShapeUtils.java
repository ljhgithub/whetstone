package com.pysun.common.utils;

import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;

public class DrawableShapeUtils {

    public static GradientDrawable getDrawable(int solidColor, int strokeColor, int strokeWidth, float radius) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(radius);
        drawable.setColor(solidColor);
        drawable.setStroke(strokeWidth, strokeColor);
        return drawable;
    }

    public static GradientDrawable getDrawable(int solidColor, int strokeColor, int strokeWidth, float radius,int width,int height) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(radius);
        drawable.setColor(solidColor);
        drawable.setSize(width,height);
        drawable.setStroke(strokeWidth, strokeColor);
        return drawable;
    }
    public static GradientDrawable getDrawable(int solidColor, int strokeColor, int strokeWidth,  float [] radii) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadii(radii);
        drawable.setColor(solidColor);
        drawable.setStroke(strokeWidth, strokeColor);
        return drawable;
    }

    /**
     * 用java代码的方式动态生成状态选择器
     */
    public static Drawable generatePressedSelector(Drawable pressed, Drawable normal) {
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{android.R.attr.state_pressed}, pressed);//  状态  , 设置按下的图片
        drawable.addState(new int[]{}, normal);//默认状态,默认状态下的图片
        //根据SDK版本设置状态选择器过度动画/渐变选择器/渐变动画
        if (Build.VERSION.SDK_INT > 10) {
            drawable.setEnterFadeDuration(500);
            drawable.setExitFadeDuration(500);
        }

        return drawable;
    }

    /**
     * 用java代码的方式动态生成状态选择器
     */
    public static Drawable generateSelector(Drawable pressed, Drawable normal) {
        StateListDrawable drawable = new StateListDrawable();
        drawable.addState(new int[]{android.R.attr.state_selected}, pressed);//  状态  , 设置按下的图片

        drawable.addState(new int[]{}, normal);//默认状态,默认状态下的图片

        return drawable;
    }
}
