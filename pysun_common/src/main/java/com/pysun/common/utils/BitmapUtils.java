package com.pysun.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;

public class BitmapUtils {



    //图片上绘制文字
    public static Bitmap drawBitmapToBitmap(Context context,Bitmap  bitmapLogo, Bitmap bitmap, Rect bounds) {
        android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setDither(true); // 获取跟清晰的图像采样
        paint.setFilterBitmap(true);// 过滤一些
        if (bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.RGB_565;
        }
        bitmap = bitmap.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmap);

        canvas.drawBitmap(bitmapLogo,bitmapLogo.getWidth(),bitmapLogo.getHeight(),paint);

        return bitmap;
    }


    public static  void  hollowRoundBitmap(View view) {

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        PorterDuffXfermode porterDuffXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);


        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                Bitmap bm = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.RGB_565);
                paint.setColor(Color.TRANSPARENT);
                Canvas canvas = new Canvas(bm);
//                canvas.drawBitmap(bm,0,0,paint);

                paint.setColor(Color.RED);
                paint.setXfermode(porterDuffXfermode);
                canvas.drawRoundRect(new RectF(0f, 0f, view.getWidth(), view.getHeight()), 50f, 50f, paint);

            }
        }, 20);


    }




}
