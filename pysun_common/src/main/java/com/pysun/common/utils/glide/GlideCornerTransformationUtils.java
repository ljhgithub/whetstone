package com.pysun.common.utils.glide;
/*
 * Create by yk on 2018/12/15
 * com.duia.ssx.lib_common.utils.glide
 */

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RectF;
import android.graphics.Shader;
import android.os.Build;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Synthetic;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public final class GlideCornerTransformationUtils {

    private GlideCornerTransformationUtils() {
    }

    public static Bitmap roundedCorners(
            @NonNull BitmapPool pool, @NonNull Bitmap inBitmap, int roundingRadius, boolean left,
            boolean top, boolean right, boolean bottom) {
        Preconditions.checkArgument(roundingRadius > 0, "roundingRadius must be greater than 0.");

        // Alpha is required for this transformation.
        Bitmap.Config safeConfig = getAlphaSafeConfig(inBitmap);
        Bitmap toTransform = getAlphaSafeBitmap(pool, inBitmap);
        Bitmap result = pool.get(toTransform.getWidth(), toTransform.getHeight(), safeConfig);

        result.setHasAlpha(true);

        BitmapShader shader = new BitmapShader(toTransform, Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(shader);
        BITMAP_DRAWABLE_LOCK.lock();
        try {
            startDraw(result,left,top,right,bottom,roundingRadius, paint);
        } finally {
            BITMAP_DRAWABLE_LOCK.unlock();
        }

        if (!toTransform.equals(inBitmap)) {
            pool.put(toTransform);
        }

        return result;
    }

    private static void startDraw(Bitmap result, boolean left,
                           boolean top,boolean right,boolean bottom,int roundingRadius, Paint paint){
        Canvas canvas = new Canvas(result);
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
        Path path = new Path();
        if(top){
            path.arcTo(new RectF(0,0,2 * roundingRadius,2 * roundingRadius),180,90);
        }else
            path.moveTo(0,0);
        if(right){
            path.lineTo(result.getWidth() - 2 * roundingRadius, 0);
            path.arcTo(new RectF(result.getWidth() - 2* roundingRadius,0,result.getWidth(),2 * roundingRadius),270,90);
        }else
            path.lineTo(result.getWidth(),0);
        if(bottom){
            path.lineTo(result.getWidth(), result.getHeight() - 2 * roundingRadius);
            path.arcTo(new RectF(result.getWidth() - 2* roundingRadius,result.getHeight() - 2 * roundingRadius
                    ,result.getWidth(),result.getHeight()),0,90);
        }else
            path.lineTo(result.getWidth(),result.getHeight());
        if(left){
            path.lineTo(result.getWidth() - 2 * roundingRadius, result.getHeight());
            path.arcTo(new RectF(0,result.getHeight() - 2 * roundingRadius
                    ,2 * roundingRadius,result.getHeight()),90,90);
        }else
            path.lineTo(0,result.getHeight());
        path.close();
        canvas.drawPath(path,paint);
        clear(canvas);
    }

    // Avoids warnings in M+.
    private static void clear(Canvas canvas) {
        canvas.setBitmap(null);
    }

    @NonNull
    private static Bitmap.Config getAlphaSafeConfig(@NonNull Bitmap inBitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Avoid short circuiting the sdk check.
            if (Bitmap.Config.RGBA_F16.equals(inBitmap.getConfig())) { // NOPMD
                return Bitmap.Config.RGBA_F16;
            }
        }

        return Bitmap.Config.ARGB_8888;
    }

    private static Bitmap getAlphaSafeBitmap(
            @NonNull BitmapPool pool, @NonNull Bitmap maybeAlphaSafe) {
        Bitmap.Config safeConfig = getAlphaSafeConfig(maybeAlphaSafe);
        if (safeConfig.equals(maybeAlphaSafe.getConfig())) {
            return maybeAlphaSafe;
        }

        Bitmap argbBitmap =
                pool.get(maybeAlphaSafe.getWidth(), maybeAlphaSafe.getHeight(), safeConfig);
        new Canvas(argbBitmap).drawBitmap(maybeAlphaSafe, 0 /*left*/, 0 /*top*/, null /*paint*/);

        // We now own this Bitmap. It's our responsibility to replace it in the pool outside this method
        // when we're finished with it.
        return argbBitmap;
    }


    // See #738.
    private static final Set<String> MODELS_REQUIRING_BITMAP_LOCK =
            new HashSet<>(
                    Arrays.asList(
                            // Moto X gen 2
                            "XT1085",
                            "XT1092",
                            "XT1093",
                            "XT1094",
                            "XT1095",
                            "XT1096",
                            "XT1097",
                            "XT1098",
                            // Moto G gen 1
                            "XT1031",
                            "XT1028",
                            "XT937C",
                            "XT1032",
                            "XT1008",
                            "XT1033",
                            "XT1035",
                            "XT1034",
                            "XT939G",
                            "XT1039",
                            "XT1040",
                            "XT1042",
                            "XT1045",
                            // Moto G gen 2
                            "XT1063",
                            "XT1064",
                            "XT1068",
                            "XT1069",
                            "XT1072",
                            "XT1077",
                            "XT1078",
                            "XT1079"
                    )
            );

    /**
     * https://github.com/bumptech/glide/issues/738 On some devices, bitmap drawing is not thread
     * safe.
     * This lock only locks for these specific devices. For other types of devices the lock is always
     * available and therefore does not impact performance
     */
    private static final Lock BITMAP_DRAWABLE_LOCK =
            MODELS_REQUIRING_BITMAP_LOCK.contains(Build.MODEL)
                    ? new ReentrantLock() : new NoLock();


    private static final class NoLock implements Lock {

        @Synthetic
        NoLock() {
        }

        @Override
        public void lock() {
            // do nothing
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {
            // do nothing
        }

        @Override
        public boolean tryLock() {
            return true;
        }

        @Override
        public boolean tryLock(long time, @NonNull TimeUnit unit) throws InterruptedException {
            return true;
        }

        @Override
        public void unlock() {
            // do nothing
        }

        @NonNull
        @Override
        public Condition newCondition() {
            throw new UnsupportedOperationException("Should not be called");
        }
    }
}
