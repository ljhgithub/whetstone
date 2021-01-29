package com.pysun.common.ui.status;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class LoadErrorView extends BaseExceptionView {
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (null != mOnRetryListener) {
                mOnRetryListener.onRetry(mContentView.getId());
            }
        }
    };

    public LoadErrorView(View content,int titleId,int imgId, OnRetryListener onRetryListener) {
        super(content, onRetryListener);
        content.setOnClickListener(mOnClickListener);
        mTitle = (TextView) content.findViewById(titleId);
        mErrorImg = (ImageView) content.findViewById(imgId);
    }


    @Override
    public void setErrorImg(String title, int resId) {
        if (null != mTitle && !TextUtils.isEmpty(title)) {
            mTitle.setText(title);
        }
        if (null != mErrorImg) {
            mErrorImg.setImageResource(resId);
        }
    }

}
