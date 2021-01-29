package com.pysun.lifecycle;

import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;



import io.reactivex.subjects.BehaviorSubject;


public class HttpLifecycleObserver implements LifecycleObserver {

    private final static String TAG = HttpLifecycleObserver.class.getSimpleName();
    private BehaviorSubject<Lifecycle.Event> behaviorSubject = BehaviorSubject.create();

    private Lifecycle lifecycle;
    private Lifecycle.Event untilState= Lifecycle.Event.ON_DESTROY;

    public HttpLifecycleObserver(Lifecycle lifecycle, Lifecycle.Event state) {
        this.lifecycle = lifecycle;
        this.untilState = state;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    void onCreate() {
        takeUntil(Lifecycle.Event.ON_CREATE);
//        LogUtils.LOGD(TAG, "onCreate state " + lifecycle.getCurrentState().name());
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    void onStart() {
        takeUntil(Lifecycle.Event.ON_START);
//        LogUtils.LOGD(TAG, "onStart state " + lifecycle.getCurrentState().name());
    }
       @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    void onResume() {
        takeUntil(Lifecycle.Event.ON_RESUME);
//        LogUtils.LOGD(TAG, "onResume state " + lifecycle.getCurrentState().name());
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    void onPause() {
        takeUntil(Lifecycle.Event.ON_PAUSE);
//        LogUtils.LOGD(TAG, "onPause state " + lifecycle.getCurrentState().name());
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    void onStop() {
        takeUntil(Lifecycle.Event.ON_STOP);
//        LogUtils.LOGD(TAG, "onStop state " + lifecycle.getCurrentState().name());
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    void onDestroy() {
        takeUntil(Lifecycle.Event.ON_DESTROY);



        Log.d(TAG, "onDestroy state " + lifecycle.getCurrentState().name());
    }


    private void takeUntil(Lifecycle.Event state) {
        if (state.ordinal()==untilState.ordinal()){
            behaviorSubject.onNext(state);
        }

    }

    public BehaviorSubject provider() {
        return behaviorSubject;
    }
}
