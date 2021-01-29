package com.pysun.common.utils;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.SparseArray;
import android.widget.ImageView;

import org.apache.commons.io.IOUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class RxAnimationDrawable {
    public static class MyFrame {
        byte[] bytes;
        int duration;
        int index;
        Drawable drawable;
        boolean isReady = false;
    }


    public RxAnimationDrawable() {
        disposableSparseArray = new SparseArray<>();
    }

    public static RxAnimationDrawable getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        private static final RxAnimationDrawable INSTANCE = new RxAnimationDrawable();
    }

    private SparseArray<Disposable> disposableSparseArray;



    public Disposable loadRaw(final int resourceId, ImageView imageView, boolean isRepeat) {

        Disposable disposable = disposableSparseArray.get(resourceId);
        if (null != disposable) {
            disposableSparseArray.remove(resourceId);
            disposable.dispose();
            disposable = null;

        }

        Disposable myDisposable = Observable.just(resourceId)
                .map(new Function<Integer, ArrayList<MyFrame>>() {
                    @Override
                    public ArrayList<MyFrame> apply(Integer integer) throws Exception {
                        return loadFromXml(integer, imageView.getContext());
                    }
                })
                .subscribeOn(Schedulers.io())
                .flatMap(new Function<ArrayList<MyFrame>, ObservableSource<MyFrame>>() {
                    @Override
                    public ObservableSource<MyFrame> apply(ArrayList<MyFrame> myFrames) throws Exception {
                        MyFrame myFrame = myFrames.get(0);

                        Observable tmpObservable
                                = Observable.interval(0, myFrame.duration, TimeUnit.MILLISECONDS)
                                .take(myFrames.size())
                                .observeOn(AndroidSchedulers.mainThread())
                                .map(new Function<Long, MyFrame>() {
                                    @Override
                                    public MyFrame apply(Long aLong) throws Exception {

                                        MyFrame curFrame = myFrames.get(aLong.intValue());
                                        int index = curFrame.index - 1;
                                        if (index > 0) {
                                            MyFrame previousFrame = myFrames.get(index);
//                                            ((BitmapDrawable) previousFrame.drawable).getBitmap().recycle();
                                            previousFrame.drawable = null;
                                            previousFrame.isReady = false;
                                        }
                                        if (!curFrame.isReady) {
                                            curFrame.drawable = new BitmapDrawable(imageView.getContext().getResources(), BitmapFactory.decodeByteArray(curFrame.bytes, 0, curFrame.bytes.length));
                                            curFrame.isReady = true;
                                        }


                                        return curFrame;
                                    }
                                })
                                .observeOn(Schedulers.io())
                                .doAfterNext(new Consumer<MyFrame>() {
                                    @Override
                                    public void accept(MyFrame frame) throws Exception {
                                        int index = frame.index + 1;
                                        if (index < myFrames.size()) {
                                            MyFrame nextFrame = myFrames.get(index);
                                            nextFrame.drawable = new BitmapDrawable(imageView.getContext().getResources(), BitmapFactory.decodeByteArray(nextFrame.bytes, 0, nextFrame.bytes.length));
                                            nextFrame.isReady = true;
                                        }
                                    }
                                });

                        if (isRepeat) {
                            return tmpObservable.repeat();
                        } else {
                            return tmpObservable
                                    ;
                        }

                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<MyFrame>() {
                    @Override
                    public void accept(MyFrame frame) throws Exception {
                        imageView.setImageDrawable(frame.drawable);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
//                        Toast.makeText(imageView.getContext(),"dddddd",Toast.LENGTH_SHORT).show();
                    }
                });

        disposableSparseArray.put(resourceId, myDisposable);

        return myDisposable;


    }

    private ArrayList<MyFrame> loadFromXml(int resourceId, Context context) {
        ArrayList<MyFrame> myFrames = new ArrayList<>();
        XmlResourceParser parser = context.getResources().getXml(resourceId);

        try {
            int eventType = parser.getEventType();
            int index = 0;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_DOCUMENT) {

                } else if (eventType == XmlPullParser.START_TAG) {

                    if (parser.getName().equals("item")) {
                        byte[] bytes = null;
                        int duration = 1000;

                        for (int i = 0; i < parser.getAttributeCount(); i++) {
                            if (parser.getAttributeName(i).equals("drawable")) {
                                int resId = Integer.parseInt(parser.getAttributeValue(i).substring(1));
                                bytes = IOUtils.toByteArray(context.getResources().openRawResource(resId));
                            } else if (parser.getAttributeName(i).equals("duration")) {
                                duration = parser.getAttributeIntValue(i, 1000);
                            }
                        }

                        MyFrame myFrame = new MyFrame();
                        myFrame.bytes = bytes;
                        myFrame.duration = duration;
                        myFrame.index = index;
                        myFrames.add(myFrame);
                        index++;
                    }

                } else if (eventType == XmlPullParser.END_TAG) {

                } else if (eventType == XmlPullParser.TEXT) {

                }

                eventType = parser.next();
            }
        } catch (IOException | XmlPullParserException e) {
            e.printStackTrace();
        }


        return myFrames;
    }


}
