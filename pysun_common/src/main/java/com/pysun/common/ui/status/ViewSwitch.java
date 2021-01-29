package com.pysun.common.ui.status;

import android.app.Activity;
import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ViewSwitch {


    public final static int TYPE_NORMAL = 0;
    public final static int TYPE_EMPTY = 1;
    public final static int TYPE_ERROR = 2;
    public final static int TYPE_LOADING = 3;
    public final static int TYPE_ERROR_NET = 4;
    private boolean mUseVisibility = false;
    private ViewGroup mRootView;

    private Activity activity;
    private int mType = -1;
    public SparseArray<View> mViews;
    public SparseArray<BaseExceptionView> mExceptionViews;

    private OnChangeShowViewListener onChangeShowViewListener;

    public void setOnChangeShowViewListener(OnChangeShowViewListener onChangeShowViewListener) {
        this.onChangeShowViewListener = onChangeShowViewListener;
    }

    public ViewSwitch(Activity activity) {
        this.mUseVisibility = false;
        this.activity = activity;
        this.mViews = new SparseArray<>();
        this.mExceptionViews = new SparseArray<>();
    }

    public ViewSwitch(ViewGroup rootView,int normalViewId) {
        this.mRootView = rootView;
        this.mUseVisibility = true;
        this.mViews = new SparseArray<>();
        this.mExceptionViews = new SparseArray<>();
        addNormalView(rootView.findViewById(normalViewId));
    }

    public void addNormalView(Context context, int id) {
        View view = LayoutInflater.from(context).inflate(id, null, false);
        addNormalView(view);

    }

    public void addNormalView(View view) {
        mViews.append(TYPE_NORMAL, view);

    }


    public void addEmptyView(BaseExceptionView view) {
        if (view == null || view.getContentView() == null) return;
        mViews.append(TYPE_EMPTY, view.getContentView());
        mExceptionViews.append(TYPE_EMPTY, view);


    }

    public void addLoadingView(Context context, int id) {
        View view = LayoutInflater.from(context).inflate(id, null, false);
        addLoadingView(view);

    }

    public void addLoadingView(View view) {
        mViews.append(TYPE_LOADING, view);

    }


    public void addErrorView(BaseExceptionView view) {
        if (view == null || view.getContentView() == null) return;
        mViews.append(TYPE_ERROR, view.getContentView());
        mExceptionViews.append(TYPE_ERROR, view);

    }
    public void addErrorNetView(BaseExceptionView view) {
        if (view == null || view.getContentView() == null) return;
        mViews.append(TYPE_ERROR_NET, view.getContentView());
        mExceptionViews.append(TYPE_ERROR_NET, view);

    }


    public int getType() {
        return mType;
    }

    public void showView(int viewType) {

        this.mType = viewType;

        switch (mType) {
            case TYPE_NORMAL: {
                View view = mViews.get(mType);
                if (null == view) return;
                switchView(view);
                if (onChangeShowViewListener != null) {
                    onChangeShowViewListener.onShowNormal();
                }
                break;
            }
            case TYPE_EMPTY: {
                View view = mViews.get(mType);
                if (null == view) return;
                switchView(view);
                if (onChangeShowViewListener != null) {
                    onChangeShowViewListener.onShowEmpty();
                }
                break;
            }
            case TYPE_ERROR: {
                View view = mViews.get(mType);
                if (null == view) return;
                switchView(view);
                if (onChangeShowViewListener != null) {
                    onChangeShowViewListener.onShowError();
                }
                break;
            }
            case TYPE_ERROR_NET: {
                View view = mViews.get(mType);
                if (null == view) return;
                switchView(view);
                if (onChangeShowViewListener != null) {
                    onChangeShowViewListener.onShowErrorNet();
                }
                break;
            }
            case TYPE_LOADING: {
                View view = mViews.get(mType);
                if (null == view) return;
                switchView(view);
                if (onChangeShowViewListener != null) {
                    onChangeShowViewListener.onShowLoading();
                }
                break;
            }
            default: {
                View view = mViews.get(mType);
                if (null == view) return;
                switchView(view);
                if (onChangeShowViewListener != null) {
                    onChangeShowViewListener.onShowEmpty();

                    break;
                }
            }

        }
    }

    private void switchView(View view) {
        if (mUseVisibility) {
            if (null != mRootView) {
                mRootView.removeAllViews();
                mRootView.addView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            }

        } else {
            activity.setContentView(view);
        }

    }

    public void setError(String msg, int resId) {
        BaseExceptionView baseExceptionView = mExceptionViews.get(mType);
        if (null != baseExceptionView) {
            baseExceptionView.setErrorImg(msg, resId);
        }

    }

    public void setErrorImg(int resId) {
        setError("", resId);

    }

    public static interface OnChangeShowViewListener {

        void onShowNormal();

        void onShowEmpty();

        void onShowError();
        void onShowErrorNet();

        void onShowLoading();

    }
    public static class SimpleOnChangeShowViewListener implements OnChangeShowViewListener {


        @Override
        public void onShowNormal() {

        }

        @Override
        public void onShowEmpty() {

        }

        @Override
        public void onShowError() {

        }

        @Override
        public void onShowErrorNet() {

        }

        @Override
        public void onShowLoading() {

        }
    }
}