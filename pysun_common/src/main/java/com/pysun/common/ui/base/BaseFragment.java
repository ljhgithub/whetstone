package com.pysun.common.ui.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.duia.ssx.pysun_common.R;
import com.pysun.common.ui.status.BaseExceptionView;
import com.pysun.common.ui.status.LoadErrorView;
import com.pysun.common.ui.status.ViewSwitch;
import com.pysun.common.utils.NetworkUtils;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseFragment extends Fragment implements ViewSwitch.OnChangeShowViewListener, BaseExceptionView.OnRetryListener {


    private ViewSwitch mViewSwitch;
    public Activity mActivity;
    private boolean destroyed;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private static final String STATE_SAVE_IS_HIDDEN = "STATE_SAVE_IS_HIDDEN";

    protected final boolean isDestroyed() {
        return destroyed;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        destroyed = false;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViewModel();
    }


    public void addDisposable(Disposable disposable) {
        mDisposable.add(disposable);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(getLayoutId(), container, false);
        initViewModel();
        initView(rootView);
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            boolean isSupportHidden = savedInstanceState.getBoolean(STATE_SAVE_IS_HIDDEN);
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            if (isSupportHidden) {
                ft.hide(this);
            } else {
                ft.show(this);
            }
            ft.commit();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_SAVE_IS_HIDDEN, isHidden());
    }

    public abstract int getLayoutId();


    public void initViewModel() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mDisposable.clear();
        destroyed = true;
    }


    protected void initView(View rootView) {

    }

    /**
     * 此方法用于清除掉所有的发射事件
     * 在刷新页面时候用于清除掉以前所产生重复请求
     */
    public void clearRequest() {
        if (mDisposable.size() > 0) {
            mDisposable.clear();
        }
    }


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

    protected void buildViewSwitch(ViewSwitch viewSwitch) {
        this.mViewSwitch=viewSwitch;
        if (null != mViewSwitch)

            mViewSwitch.addErrorView(new LoadErrorView(LayoutInflater.from(getContext()).inflate(R.layout.comm_load_error_view, null, false), R.id.comm_error_msg, R.id.comm_error_img, this));
        mViewSwitch.addErrorNetView(new LoadErrorView(LayoutInflater.from(getContext()).inflate(R.layout.comm_load_error_net_view, null, false), R.id.comm_error_net_msg, R.id.comm_error_net_img, this));
        mViewSwitch.addEmptyView(new LoadErrorView(LayoutInflater.from(getContext()).inflate(R.layout.comm_load_nothing_view, null, false), R.id.comm_nothing_msg, R.id.comm_nothing_img, this));

        mViewSwitch.setOnChangeShowViewListener(this);
    }


    protected void showErrorView() {
        if (null != mViewSwitch) {
            if (mViewSwitch.getType() != ViewSwitch.TYPE_NORMAL) {
                Toast.makeText(mActivity, "请稍后再试", Toast.LENGTH_SHORT).show();
            }
            if (NetworkUtils.hasNetWorkConection(mActivity)) {
                mViewSwitch.showView(ViewSwitch.TYPE_ERROR);
            } else {
                mViewSwitch.showView(ViewSwitch.TYPE_ERROR_NET);
            }
        }

    }

    protected void showEmptyView() {
        if (null != mViewSwitch) {
            if (mViewSwitch.getType() != ViewSwitch.TYPE_NORMAL) {
                Toast.makeText(mActivity, "请稍后再试", Toast.LENGTH_SHORT).show();
            }
            mViewSwitch.showView(ViewSwitch.TYPE_EMPTY);
        }

    }
    protected void showNormalView() {
        if (null != mViewSwitch) {
            if (ViewSwitch.TYPE_NORMAL != mViewSwitch.getType()) {
                mViewSwitch.showView(ViewSwitch.TYPE_NORMAL);
            }

        }

    }
    @Override
    public void onRetry(int id) {

    }

    protected void refreshDataEnd(int datas) {
        if (datas > 0) {
            Toast.makeText(mActivity, "数据刷新失败", Toast.LENGTH_SHORT).show();
        } else {
            showErrorView();
        }
    }
}
