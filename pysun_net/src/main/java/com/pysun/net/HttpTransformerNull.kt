package com.pysun.net

import com.pysun.http.KCustomHttpException
import com.pysun.http.KHttpDataNull
import com.pysun.http.KHttpResult
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers



class HttpTransformerNull<T>(schedulerMain: Boolean = true) : ObservableTransformer<KHttpResult<T>, KHttpDataNull<T>> {

    private val schedulerMain: Boolean = schedulerMain
    override fun apply(upstream: Observable<KHttpResult<T>>): ObservableSource<KHttpDataNull<T>> {

        var observable = upstream
                .flatMap<KHttpDataNull<T>> { httpResult: KHttpResult<T> ->
                    Observable.create { emitter: ObservableEmitter<KHttpDataNull<T>> ->

                        when (httpResult.responseCode()) {
                            KHttpResult.CODE_SUCCESS -> {
                                emitter.onNext(KHttpDataNull(httpResult.getData()))
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