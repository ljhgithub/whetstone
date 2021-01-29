package com.pysun.common.utils;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;

import org.apache.commons.io.IOUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HandlerAnimationDrawable {
    public static class MyFrame {
        byte[] bytes;
        int duration;
        int index;
        Drawable drawable;
        boolean isReady = false;
    }


    private ExecutorService executor;
    private Handler handler;

    public HandlerAnimationDrawable(){
        executor=Executors.newSingleThreadExecutor();
    }


    private  ArrayList<MyFrame> loadFromXml(int resourceId, Context context) {
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


    private void release(){
        if (null != executor) {
            executor.shutdownNow();
            executor=null;
        }

        if (null!=handler){
            handler.removeMessages(0);
            handler=null;
        }
    }

    public void start(int resId, ImageView imageView, boolean isRepeat) {
        if (null == executor) {
            executor = Executors.newSingleThreadExecutor();
        }

        if (null!=handler){
            handler.removeMessages(0);
            handler=null;
        }
        handler = new Handler(imageView.getContext().getMainLooper()) {

            @Override
            public void dispatchMessage(Message msg) {
                super.dispatchMessage(msg);

                int index = msg.arg1;
                ArrayList<MyFrame> myFrames = (ArrayList<MyFrame>) msg.obj;
                MyFrame myFrame = myFrames.get(index);
                if (index == 0) {
                    myFrame.drawable = new BitmapDrawable(imageView.getContext().getResources(), BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length));
                    myFrame.isReady = true;
                } else {
//
                    MyFrame preFrame = myFrames.get(index - 1);
                    LogUtils.LOGD("test", preFrame.drawable + "  null" + index + preFrame);
                    ((BitmapDrawable) preFrame.drawable).getBitmap().recycle();
                    preFrame.drawable = null;
                    preFrame.isReady = false;
                }

                LogUtils.LOGD("test", myFrame.isReady + "  isReady" + index + myFrame.toString());
                if (!myFrame.isReady) {
                    myFrame.drawable = new BitmapDrawable(imageView.getContext().getResources(), BitmapFactory.decodeByteArray(myFrame.bytes, 0, myFrame.bytes.length));

                }
                imageView.setImageDrawable(myFrame.drawable);

                Message message = Message.obtain();
                message.arg1 = index + 1;
                message.obj = myFrames;


                if (index + 1 < myFrames.size()) {
                    sendMessageDelayed(message, myFrame.duration);
                    MyFrame nextFrame = myFrames.get(index + 1);
                    executor.execute(new Runnable() {
                        @Override
                        public void run() {
                            LogUtils.LOGD("test", Thread.currentThread().getName() + index);
                            nextFrame.drawable = new BitmapDrawable(imageView.getContext().getResources(), BitmapFactory.decodeByteArray(nextFrame.bytes, 0, nextFrame.bytes.length));
                            nextFrame.isReady = true;
                        }
                    });
                } else {

                    if (isRepeat) {
                        message.arg1 = 0;
                        sendMessageDelayed(message, myFrame.duration);
                    }
                }


            }

        };


        executor.execute(new Runnable() {
            @Override
            public void run() {
                LogUtils.LOGD("test2 ", Thread.currentThread().getName());
                ArrayList<MyFrame> myFrames = loadFromXml(resId, imageView.getContext());
                Message message = Message.obtain(handler, 0);
                message.obj = myFrames;
                message.arg1=0;
                handler.sendMessage(message);

            }
        });

    }

}
