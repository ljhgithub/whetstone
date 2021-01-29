package com.pysun.common.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Environment;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ViewToBitmap {
    public ViewToBitmap() {
    }



    public static void shareImg(Context context,int width, int height , View v, String filename, SavePicCallBack callbacks) {

        layoutView(v, width, height);
        viewSaveToImage(context,v, filename, callbacks);
    }

    public static void viewSaveToImage(Context context,View v, String filename, SavePicCallBack callbacks) {
        Bitmap bitmap = loadBitmapFromView(v);
        String image_path = context.getExternalFilesDir(null).getAbsolutePath();
//        File appDir = new File(image_path);
//        if (!appDir.exists()) {
//            appDir.mkdirs();
//        }

        File picFile = new File(image_path,filename);

        try {
            FileOutputStream fos = new FileOutputStream(picFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException var8) {
            var8.printStackTrace();
            callbacks.onError();
        } catch (IOException var9) {
            var9.printStackTrace();
            callbacks.onError();
        }

        v.destroyDrawingCache();
        if (picFile.exists()) {
            callbacks.onSuccess(picFile.getAbsolutePath());
        } else {
            callbacks.onError();
        }

    }

    private static void layoutView(View v, int width, int height) {
        v.layout(0, 0, width, height);
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(height, View.MeasureSpec.EXACTLY);
        v.measure(measuredWidth, measuredHeight);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
    }

    private static Bitmap loadBitmapFromView(View v) {
        int w = v.getWidth();
        int h = v.getHeight();
        Bitmap bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmp);
        v.layout(0, 0, w, h);
        v.draw(c);
        return bmp;
    }

    private static String getExternalStoragePath() {
        StringBuilder sb = new StringBuilder();
        sb.append(Environment.getExternalStorageDirectory().getAbsolutePath());
        sb.append(File.separator);
        String ROOT_DIR = "Android/data/com.duia.duiaapp";
        sb.append(ROOT_DIR);
        sb.append(File.separator);
        return sb.toString();
    }

    public interface SavePicCallBack {
        void onSuccess(String var1);

        void onError();
    }
}
