package com.pysun.net

import android.util.Log
import io.reactivex.Observable
import io.reactivex.functions.Function
import java.util.concurrent.TimeUnit

/*
 * Create by yk on 2019-11-07
 * com.pysun.net
 */
class RetryWithFunction(private val maxRetries: Int) : Function<Observable<out Throwable?>, Observable<*>> {
    private var retryCount = 0
    @Throws(Exception::class)
    override fun apply(observable: Observable<out Throwable?>): Observable<*> {
        return observable.flatMap { throwable: Throwable? ->
            if (retryCount <= maxRetries) {
                Log.d("RetryWithFunction", "error occurs, retry on " + retryCount + "times")
                return@flatMap Observable.just(retryCount).delay(++retryCount * 10.toLong(), TimeUnit.SECONDS)
            }
            Observable.error<Any?>(throwable)
        }
    }

}