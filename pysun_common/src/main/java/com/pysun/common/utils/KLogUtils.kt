package com.pysun.common.utils

import android.util.Log
import com.duia.ssx.pysun_common.BuildConfig

object KLogUtils {

    private const val LOG_PREFIX = "ps_"
    private const val LOG_PREFIX_LENGTH = LOG_PREFIX.length
    private const val MAX_LOG_TAG_LENGTH = 23
    private val TAG = KLogUtils::class.simpleName

    var LOGGING_ENABLED = !BuildConfig.BUILD_TYPE.equals("release", ignoreCase = true)

    // Since logging is disabled for release, we can set our logging level to DEBUG.
    var LOG_LEVEL = Log.DEBUG


    fun makeLogTag(str: String): String {
        return if (str.length > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1)
        } else LOG_PREFIX + str
    }

    fun makeLogTag(cls: Class<*>): String {
        return makeLogTag(cls.simpleName)
    }

    fun LOGD( message: String?) {
        if (LOGGING_ENABLED) {
            Log.d(TAG, message)
        }
    }

    fun LOGD(tag: String , message: String?) {
        if (LOGGING_ENABLED) {
            Log.d(tag, message)
        }
    }


    fun LOGD(tag: String , message: String?, cause: Throwable?) {
        if (LOGGING_ENABLED) {
            Log.d(tag, message, cause)
        }
    }


    fun LOGV(  message: String?) {
        if (LOGGING_ENABLED) {
            Log.v(TAG, message)
        }
    }
    fun LOGV(tag: String , message: String?) {
        if (LOGGING_ENABLED) {
            Log.v(tag, message)
        }
    }

    fun LOGV(tag: String , message: String?, cause: Throwable?) {
        if (LOGGING_ENABLED) {
            Log.v(tag, message, cause)
        }
    }

    fun LOGI( message: String?) {
        if (LOGGING_ENABLED) {
            Log.i(TAG, message)
        }
    }
    fun LOGI(tag: String , message: String?) {
        if (LOGGING_ENABLED) {
            Log.i(tag, message)
        }
    }

    fun LOGI(tag: String , message: String?, cause: Throwable?) {
        if (LOGGING_ENABLED) {
            Log.i(tag, message, cause)
        }
    }

    fun LOGW( message: String?) {
        if (LOGGING_ENABLED) {
            Log.w(TAG, message)
        }
    }
    fun LOGW(tag: String , message: String?) {
        if (LOGGING_ENABLED) {
            Log.w(tag, message)
        }
    }

    fun LOGW(tag: String , message: String?, cause: Throwable?) {
        if (LOGGING_ENABLED) {
            Log.w(tag, message, cause)
        }
    }

    fun LOGE(tag: String , message: String?) {
        if (LOGGING_ENABLED) {
            Log.e(tag, message)
        }
    }

    fun LOGE(tag: String , message: String?, cause: Throwable?) {
        if (LOGGING_ENABLED) {
            Log.e(tag, message, cause)
        }
    }
}