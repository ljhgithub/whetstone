package com.pysun.common.utils;

import android.content.Context;

public class ResourceUtil {

    public static int getDrawableId(Context context,String paramString) {
        if (context == null)
            return 0;
        return context.getResources().getIdentifier(paramString, "drawable", context.getPackageName());
    }

    public static int getMipmapId(Context context,String paramString) {
        if (context == null)
            return 0;
        return context.getResources().getIdentifier(paramString, "mipmap", context.getPackageName());
    }
}
