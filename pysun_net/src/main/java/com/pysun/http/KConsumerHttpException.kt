package com.pysun.http

import io.reactivex.exceptions.CompositeException
import io.reactivex.exceptions.Exceptions
import io.reactivex.functions.Consumer
import io.reactivex.plugins.RxJavaPlugins
import retrofit2.HttpException
import java.net.UnknownHostException


class KConsumerHttpException : Consumer<Throwable> {


    private val actionHttpExceptionHashMap = hashMapOf<String, ActionHttpException>()


    constructor(codes: Array<String>, actionHttpException: ActionHttpException) {
        for (i in codes.indices) {
            registerAction(codes[i], actionHttpException)
        }
    }

    override fun accept(throwable: Throwable?) {
        var intercept: Boolean? = false
        try {
            when (throwable) {
                is HttpException -> {
                    intercept = actionHttpExceptionHashMap[KCustomHttpException.CODE_HTTP_ERROR]?.let {
                        it.onAction(KCustomHttpException.CODE_HTTP_ERROR, throwable.message)
                    }
                }
                is UnknownHostException -> {
                    intercept = actionHttpExceptionHashMap[KCustomHttpException.CODE_UNKNOWN_HOST]?.let {
                        it.onAction(KCustomHttpException.CODE_UNKNOWN_HOST, throwable.message)
                    }

                }

                is KCustomHttpException -> {
                    intercept = actionHttpExceptionHashMap[throwable.getCode()]?.let {
                        it.onAction(throwable.getCode(), throwable.message)
                    }
                }
                else -> {
                    intercept = actionHttpExceptionHashMap[KCustomHttpException.CODE_DEFAULT]?.let {
                        throwable?.message?.let { msg ->
                            it.onAction(KCustomHttpException.CODE_DEFAULT, msg)

                        }
                    }

                }
            }


            if (!intercept!! && null != throwable) {
                throwableAction?.onAction(throwable)
            }

        } catch (e: Throwable) {
            Exceptions.throwIfFatal(e)
            RxJavaPlugins.onError(CompositeException(throwable, e))
        }
    }

    fun registerAction(code: String, action: ActionHttpException) {
        actionHttpExceptionHashMap[code] = action
    }

    fun unregisterAction(code: String): KConsumerHttpException {
        if (actionHttpExceptionHashMap.containsKey(code)) {
            actionHttpExceptionHashMap.remove(code)
        }
        return this
    }

    fun registerDefaultAction(action: ActionHttpException) {
        actionHttpExceptionHashMap[KCustomHttpException.CODE_DEFAULT] = action
    }

    fun unregisterAllActions() {
        actionHttpExceptionHashMap.clear()
    }



    fun unregisterActions(code: String) {
        if (actionHttpExceptionHashMap.containsKey(code)) {
            actionHttpExceptionHashMap.remove(code)
        }
    }

    interface ActionHttpException {
        fun onAction(code: String?, msg: String?): Boolean
    }

    private var throwableAction: ThrowableAction? = null

    fun setThrowableAction(throwableAction: ThrowableAction?) {
        this.throwableAction = throwableAction
    }

    interface ThrowableAction {
        fun onAction(throwable: Throwable?)
    }
}