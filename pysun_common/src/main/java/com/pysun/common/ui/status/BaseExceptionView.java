package com.pysun.common.ui.status;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public abstract class BaseExceptionView {

    protected View mContentView;
    protected OnRetryListener mOnRetryListener;
    protected TextView mTitle;
    protected ImageView mErrorImg;

    public BaseExceptionView(View content, OnRetryListener onRetryListener) {
        this.mContentView = content;
        this.mOnRetryListener = onRetryListener;
    }

    public View getContentView() {
        return mContentView;
    }

    public interface OnRetryListener {
        void onRetry(int id);
    }

    public static class DefaultOnRetryListener implements OnRetryListener {

        @Override
        public void onRetry(int id) {

        }
    }

    public abstract void setErrorImg(String msg, int resId);
}
