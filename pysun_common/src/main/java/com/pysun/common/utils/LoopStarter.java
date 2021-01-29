package com.pysun.common.utils;

import androidx.lifecycle.Lifecycle;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.subjects.BehaviorSubject;

public class LoopStarter {
    private BehaviorSubject<Lifecycle.Event> behaviorSubject = BehaviorSubject.create();
    private Observable observable;
    private CompositeDisposable disposable;

    private long delay, period;

    public LoopStarter(long delay, long period, OnLoopListener onLoopListener) {
        this.delay = delay;
        this.period = period;
        this.onLoopListener = onLoopListener;
        disposable = new CompositeDisposable();
        observable = Observable.interval(delay, period, TimeUnit.MILLISECONDS);
    }

    public void start() {
        if (null == observable) {
            observable = Observable.interval(delay, period, TimeUnit.MILLISECONDS);
        }
        pause();
        disposable.add(observable.observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Long>() {
            @Override
            public void accept(Long l) throws Exception {
                onLoopListener.onLoop(l);

            }
        }));
    }


    public void pause() {
        disposable.clear();
    }

    public void reset(long delay, long period) {
        observable=null;
        observable = Observable.interval(delay, period, TimeUnit.MILLISECONDS);
    }

    public void release() {
        disposable.clear();
        observable = null;
        disposable = null;
    }

    private OnLoopListener onLoopListener = new OnLoopListener() {
        @Override
        public void onLoop(long times) {

        }
    };

    public void setOnLoopListener(OnLoopListener onLoopListener) {
        this.onLoopListener = onLoopListener;
    }

    public interface OnLoopListener {
        void onLoop(long times);
    }
}
