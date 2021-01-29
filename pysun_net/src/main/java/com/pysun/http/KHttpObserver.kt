package com.pysun.http

import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import io.reactivex.exceptions.CompositeException
import io.reactivex.exceptions.Exceptions
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.internal.disposables.DisposableHelper
import io.reactivex.internal.functions.Functions
import io.reactivex.observers.LambdaConsumerIntrospection
import io.reactivex.plugins.RxJavaPlugins
import java.util.concurrent.atomic.AtomicReference


/**
 * can delete
 */
class KHttpObserver<T> : AtomicReference<Disposable>, Observer<T>, Disposable, LambdaConsumerIntrospection {
    private val serialVersionUID = -6251123623727029452L
    var onNext: Consumer<in T>
    var onError: Consumer<Throwable>
    var onComplete: Action
    var onSubscribe: Consumer<in Disposable>

    companion object {
        val EMPTY_CONSUMER: Consumer<Disposable> = Consumer { }
    }

    constructor(onNext: Consumer<T>) : this(onNext, Functions.ON_ERROR_MISSING) {

    }

    constructor(onNext: Consumer<T>, onError: Consumer<Throwable>) : this(onNext, onError, Functions.EMPTY_ACTION) {

    }

    constructor(onNext: Consumer<T>, onError: Consumer<Throwable>, onComplete: Action) : this(onNext, onError, onComplete, EMPTY_CONSUMER ) {

    }

    constructor(onNext: Consumer<T>, onError: Consumer<Throwable>, onComplete: Action, onSubscribe: Consumer<in Disposable>) : super() {
        this.onNext = onNext
        this.onError = onError
        this.onComplete = onComplete
        this.onSubscribe = onSubscribe
    }

    override fun onComplete() {
        if (!isDisposed) {
            lazySet(DisposableHelper.DISPOSED)
            try {
                onComplete.run()
            } catch (e: Throwable) {
                Exceptions.throwIfFatal(e)
                RxJavaPlugins.onError(e)
            }
        }
    }

    override fun onSubscribe(d: Disposable) {

        if (DisposableHelper.setOnce(this, d)) {
            try {
                onSubscribe.accept(this)
            } catch (ex: Throwable) {
                Exceptions.throwIfFatal(ex)
                d.dispose()
                onError(ex)
            }
        }
    }

    override fun onNext(tHttpResult: T) {
        if (!isDisposed) {
            try {
                onNext.accept(tHttpResult)
            } catch (e: Throwable) {
                Exceptions.throwIfFatal(e)
                get().dispose()
                onError(e)
            }
        }
    }

    override fun onError(e: Throwable) {
        if (!isDisposed) {
            lazySet(DisposableHelper.DISPOSED)
            try {
                onError.accept(e)
            } catch (e: Throwable) {
                Exceptions.throwIfFatal(e)
                RxJavaPlugins.onError(CompositeException(e, e))
            }
        }
    }


    override fun isDisposed(): Boolean {
        return get() == DisposableHelper.DISPOSED
    }

    override fun dispose() {
        DisposableHelper.dispose(this)
    }

    override fun hasCustomOnError(): Boolean {
        return onError != Functions.ON_ERROR_MISSING
    }

}