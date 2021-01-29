package com.pysun.common.ui.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.duia.ssx.pysun_common.R;
import com.pysun.common.ui.dialog.LoadingDialog;
import com.pysun.common.ui.status.BaseExceptionView;
import com.pysun.common.ui.status.LoadErrorView;
import com.pysun.common.ui.status.ViewSwitch;
import com.pysun.common.utils.NetworkUtils;
import com.gyf.immersionbar.ImmersionBar;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public abstract class BaseActivity extends AppCompatActivity implements ViewSwitch.OnChangeShowViewListener, BaseExceptionView.OnRetryListener {
    private ViewSwitch mViewSwitch;
    protected int statusBarColor = R.color.white;
    private final CompositeDisposable mDisposable = new CompositeDisposable();
    private LoadingDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        if (this.isImmersionBarEnabled()) {
            this.initImmersionBar(statusBarColor);
        }

        initViewModel();
        initView();


    }

    protected boolean isImmersionBarEnabled() {
        return true;
    }

    protected void initImmersionBar(int barColor) {
        ImmersionBar.with(this).fitsSystemWindows(true).statusBarDarkFont(true, 0.2F).statusBarColor(barColor).navigationBarEnable(false).fullScreen(false).init();
    }

    protected abstract int getLayoutId();

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void addDisposable(Disposable disposable) {
        mDisposable.add(disposable);
    }

    public void initViewModel() {

    }

    public void initView() {

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    //
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mDisposable.clear();
        if (this.isImmersionBarEnabled()) {
            ImmersionBar.destroy(this, null); //必须调用该方法，防止内存泄漏
        }
        if (null != this.dialog) {
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }
            this.dialog = null;
        }
//        PuwManager.getInstance().onHomePageDestroy();

    }
//

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // 如果你的app可以横竖屏切换，并且适配4.4或者emui3手机请务必在onConfigurationChanged方法里添加这句话
        if (this.isImmersionBarEnabled()) {
            this.initImmersionBar(statusBarColor);
        }

    }



    public void showDialog(final boolean cancelable) {
        DialogInterface.OnKeyListener keyListener = new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode != 3 && keyCode != 84) {
                    if (keyCode == 4 && event.getAction() == 1 && !cancelable) {
                        ;
                    }

                    return false;
                } else {
                    return true;
                }
            }
        };
        if (this.dialog != null && this.dialog.isShowing()) {
            this.dialog.dismiss();
            this.dialog = null;
        }

        this.dialog = new LoadingDialog(this);
        this.dialog.setCanceledOnTouchOutside(false);
        this.dialog.setOnKeyListener(keyListener);
        this.dialog.setCancelable(cancelable);
        this.dialog.show();
    }

    public void dismissDialog() {
        if (null != this.dialog && this.dialog.isShowing()) {
            this.dialog.dismiss();
            this.dialog = null;
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
        this.mViewSwitch = viewSwitch;
        if (null != mViewSwitch)
            mViewSwitch.addErrorView(new LoadErrorView(LayoutInflater.from(this).inflate(R.layout.comm_load_error_view, null, false), R.id.comm_error_msg, R.id.comm_error_img, this));
        mViewSwitch.addErrorNetView(new LoadErrorView(LayoutInflater.from(this).inflate(R.layout.comm_load_error_net_view, null, false), R.id.comm_error_net_msg, R.id.comm_error_net_img, this));
        mViewSwitch.addEmptyView(new LoadErrorView(LayoutInflater.from(this).inflate(R.layout.comm_load_nothing_view, null, false), R.id.comm_nothing_msg, R.id.comm_nothing_img, this));

        mViewSwitch.setOnChangeShowViewListener(this);
    }

    protected void showErrorView() {
        if (null != mViewSwitch) {
            if (mViewSwitch.getType() != ViewSwitch.TYPE_NORMAL) {
                Toast.makeText(this, "请稍后再试", Toast.LENGTH_SHORT).show();
            }
            if (NetworkUtils.hasNetWorkConection(this)) {
                mViewSwitch.showView(ViewSwitch.TYPE_ERROR);
            } else {
                mViewSwitch.showView(ViewSwitch.TYPE_ERROR_NET);
            }
        }

    }

    protected void showEmptyView() {
        if (null != mViewSwitch) {
            if (mViewSwitch.getType() != ViewSwitch.TYPE_NORMAL) {
                Toast.makeText(this, "请稍后再试", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(this, "数据加载失败", Toast.LENGTH_SHORT).show();
        } else {
            showErrorView();
        }
    }

}

