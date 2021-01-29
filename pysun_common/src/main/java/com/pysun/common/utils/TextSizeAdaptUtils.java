package com.pysun.common.utils;
/*
 * Create by yk on 2019-08-16
 * com.duia.ssx.lib_common.utils
 */

import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

public class TextSizeAdaptUtils {

    public static void adaptHalf(TextView tv, int adjustSize, int fixSize){
        TextPaint tp = new TextPaint();
        tp.setTextSize(ScreenUtil.dip2px(fixSize));
        float textMeasuredLength = tp.measureText(tv.getText().toString());
        int   textViewDisplayWidth = (int) (ScreenUtil.getDisplayWidth(tv.getContext()) * 0.5f - tv.getPaddingLeft() - tv.getPaddingRight());
        Log.d("TextSizeAdaptUtils", "textViewDisplayWidth:" + textViewDisplayWidth);
        if(textMeasuredLength > textViewDisplayWidth){
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, adjustSize);
        }else {
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fixSize);
        }

    }


    public static void adapt(TextView tv, int adjustSize, int fixSize){
        TextPaint tp = tv.getPaint();
        tp.setTextSize(ScreenUtil.dip2px(fixSize));
        float textMeasuredLength = tp.measureText(tv.getText().toString());
        int   textViewDisplayWidth = ScreenUtil.getDisplayWidth(tv.getContext()) - ScreenUtil.dip2px(220) - tv.getPaddingLeft() - tv.getPaddingRight();
        if(textMeasuredLength > textViewDisplayWidth){
            tp.setTextSize(ScreenUtil.dip2px(adjustSize));
            float after = tp.measureText(tv.getText().toString());
            if(after > textViewDisplayWidth){
                tv.setWidth(textViewDisplayWidth);
            }else {
                tv.setWidth((int) after);
            }

            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, adjustSize);
        }else {
            tv.setWidth((int) textMeasuredLength);
//            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fixSize);
        }

    }

}
