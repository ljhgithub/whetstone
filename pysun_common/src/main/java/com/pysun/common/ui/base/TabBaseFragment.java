package com.pysun.common.ui.base;

import android.text.TextUtils;

public abstract class TabBaseFragment extends BaseFragment {
    public final static int HIDDEN=1;
    public final static int  SHOWN=2;

    public int currentState;
    private int containerId;
    private String fragmentTag;
    private int enterType;


    public void setEnterType(int enterType) {
        this.enterType = enterType;
    }

    public int getEnterType() {
        return enterType;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setContainerId(int containerId) {
        this.containerId = containerId;
    }

    public int getContainerId(){
        return containerId;
    }

    public void setFragmentTag(String fragmentTag) {
        this.fragmentTag = fragmentTag;
    }

    public String getFragmentTag() {
        return TextUtils.isEmpty(fragmentTag)?this.getClass().getSimpleName():fragmentTag;
    }

    public abstract void onExit(int type);

    public abstract void onEnter(int type);

    public abstract void onReenter(int type);



}
