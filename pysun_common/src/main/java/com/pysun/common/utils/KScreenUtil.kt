package com.pysun.common.utils

import android.content.Context
import kotlin.math.roundToInt

object KScreenUtil {
    private const val RATIO = 0.85f
    private var screenWidth: Int = 0
    private var screenHeight = 0
    private var screenMin = 0
    private var screenMax = 0

    private var density = 0f
    private var scaleDensity = 0f
    private var xdpi = 0f
    private var ydpi = 0f
    private var densityDpi = 0


    fun init(context: Context) {
        val dm = context.applicationContext.resources.displayMetrics
        screenWidth = dm.widthPixels
        screenHeight = dm.heightPixels
        if (screenHeight > screenWidth) {
            screenMin = screenWidth
            screenMax = screenHeight
        } else {
            screenMin = screenHeight
            screenMax = screenWidth
        }


        screenWidth = screenMin
        density = dm.density
        scaleDensity = dm.scaledDensity
        xdpi = dm.xdpi
        ydpi = dm.ydpi
        densityDpi = dm.densityDpi
        KLogUtils.LOGD(KScreenUtil::class.java.simpleName, "screenWidth=" + screenWidth + " screenHeight=" + screenHeight + " density=" + density)
    }

    fun getDialogWidth(ratio: Float = RATIO): Float {

        return screenMin * ratio;
    }

    fun dip3px(dipValue: Float): Int {
        return (dipValue * density + 0.5f).roundToInt()
    }

    fun sp3px(spValue: Float): Int {
        return (spValue * scaleDensity + 0.5f).roundToInt()
    }

    fun px3dp(pxValue: Float): Int {
        return (pxValue / density + 0.5f).roundToInt()
    }
}