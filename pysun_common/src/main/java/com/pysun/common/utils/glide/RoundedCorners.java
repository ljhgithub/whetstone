package com.pysun.common.utils.glide;
/*
 * Create by yk on 2018/12/15
 * com.duia.ssx.lib_common.utils.glide
 */

import android.graphics.Bitmap;

import androidx.annotation.NonNull;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import com.bumptech.glide.util.Preconditions;
import com.bumptech.glide.util.Util;

import java.nio.ByteBuffer;
import java.security.MessageDigest;

public class RoundedCorners extends BitmapTransformation {
    private static final String ID = "com.bumptech.glide.load.resource.bitmap.RoundedCorners";
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);

    private final int roundingRadius;
    private final boolean left, top, right, bottom;

    public RoundedCorners(int roundingRadius) {
        this(roundingRadius,true,true,true,true);
    }

    /**
     * @param roundingRadius the corner radius (in device-specific pixels).
     * @throws IllegalArgumentException if rounding radius is 0 or less.
     */
    public RoundedCorners(int roundingRadius, boolean top, boolean right, boolean bottom, boolean left) {
        Preconditions.checkArgument(roundingRadius > 0, "roundingRadius must be greater than 0.");
        this.roundingRadius = roundingRadius;
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    @Override
    protected Bitmap transform(
            @NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        toTransform = TransformationUtils.centerCrop(pool, toTransform, outWidth, outHeight);
        return GlideCornerTransformationUtils.roundedCorners(pool, toTransform, roundingRadius, left, top, right, bottom);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof RoundedCorners) {
            RoundedCorners other = (RoundedCorners) o;
            return roundingRadius == other.roundingRadius;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Util.hashCode(ID.hashCode(),
                Util.hashCode(roundingRadius));
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);

        byte[] radiusData = ByteBuffer.allocate(4).putInt(roundingRadius).array();
        messageDigest.update(radiusData);
    }
}
