package com.pysun.net

import com.pysun.http.KCustomHttpException
import com.pysun.http.KHttpResult
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class HttpTransformer<T>(private val schedulerMain: Boolean = true) : ObservableTransformer<KHttpResult<T>, T> {

    override fun apply(upstream: Observable<KHttpResult<T>>): ObservableSource<T> {

        var observable = upstream
                .flatMap<T> { httpResult: KHttpResult<T> ->
                    Observable.create { emitter: ObservableEmitter<T> ->
                        when (httpResult.responseCode()) {
                            KHttpResult.CODE_SUCCESS -> {
                                httpResult.getData()?.let {
                                    emitter.onNext(it)

                                } ?: let {
                                    emitter.onError(KCustomHttpException(KHttpResult.CODE_DATA_NULL, "code is success but data is null"))
                                }

                            }
                            KHttpResult.CODE_ERROR -> {
                                emitter.onError(KCustomHttpException(httpResult.getCode(), httpResult.getMsg()))
                            }
                            else -> {
                                emitter.onError(KCustomHttpException(KHttpResult.CODE_UNKNOWN, httpResult.getMsg()))
                            }
                        }

                        emitter.onComplete()
                    }
                }.subscribeOn(Schedulers.io())


        observable = if (schedulerMain) observable.observeOn(AndroidSchedulers.mainThread()) else observable

        return observable

    }
}