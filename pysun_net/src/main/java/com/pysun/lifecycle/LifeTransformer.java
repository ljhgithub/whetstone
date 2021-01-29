package com.pysun.lifecycle;

import org.reactivestreams.Publisher;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Completable;
import io.reactivex.CompletableSource;
import io.reactivex.CompletableTransformer;
import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.MaybeTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.SingleTransformer;


public class LifeTransformer<T> implements ObservableTransformer<T, T>,
        FlowableTransformer<T, T>,
        SingleTransformer<T, T>,
        MaybeTransformer<T, T>,
        CompletableTransformer {

    private Observable<?> observable;

    public LifeTransformer(Observable<?> observable) {
        this.observable = observable;
    }



    @Override
    public CompletableSource apply(Completable upstream) {
        return Completable.ambArray(upstream, observable.flatMapCompletable(Functions.CANCEL_COMPLETABLE));
    }

    @Override
    public Publisher<T> apply(Flowable<T> upstream) {
        return upstream.takeUntil(observable.toFlowable(BackpressureStrategy.LATEST));
    }

    @Override
    public MaybeSource<T> apply(Maybe<T> upstream) {
        return upstream.takeUntil(observable.firstElement());
    }

    @Override
    public SingleSource<T> apply(Single<T> upstream) {
        return upstream.takeUntil(observable.firstOrError());
    }

    @Override
    public ObservableSource<T> apply(Observable<T> upstream) {
        return upstream.takeUntil(observable);
    }
}
