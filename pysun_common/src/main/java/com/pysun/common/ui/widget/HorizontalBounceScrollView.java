package com.pysun.common.ui.widget;
/*
 * Create by yk on 2019/1/17
 * com.duia.ssx.lib_common.ui.widget
 */

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.RecyclerView;

public class HorizontalBounceScrollView extends RelativeLayout {

    private RecyclerView mHorizontalRecyclerView;

    private boolean mShowMore = true;
    private float mHintLeftMargin = 0;
    private int mOffsetWidth = 0;
    private float mLastX;
    private float mLastY;
    private boolean mConsumeMoveEvent = false;
    private int mMoveIndex = 0;

    // 回弹动画
    private ValueAnimator ReboundAnim;

    private static final float RATIO = 0.4f;

    public HorizontalBounceScrollView(Context context) {
        this(context, null);
    }

    public HorizontalBounceScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalBounceScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mOffsetWidth = -(int) (context.getResources().getDisplayMetrics().density*65 + 0.5f);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        child.setLayoutParams(params);
        addView(child);
    }

    @Override
    public void addView(final View child) {
        if (child instanceof RecyclerView) {
            mHorizontalRecyclerView = (RecyclerView) child;
        }
        super.addView(child);
    }

    private ViewParent getParentListView(ViewParent viewParent) {
        if (viewParent == null) return null;
        if (viewParent instanceof RecyclerView || viewParent instanceof ListView) {
            return viewParent;
        } else {
            getParentListView(viewParent.getParent());
        }
        return null;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (mHorizontalRecyclerView == null) {
            return super.dispatchTouchEvent(ev);
        }
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mHintLeftMargin = 0;
                mMoveIndex = 0;
                mConsumeMoveEvent = false;
                mLastX = ev.getRawX();
                mLastY = ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                // 释放动画
                if (ReboundAnim != null && ReboundAnim.isRunning()) {
                    break;
                }
                float mDeltaX = (ev.getRawX() - mLastX);
                float mDeltaY = ev.getRawY() - mLastY;

                if (!mConsumeMoveEvent) {
                    // 处理事件冲突
                    if (Math.abs(mDeltaX) > Math.abs(mDeltaY)) {
                        getParent().requestDisallowInterceptTouchEvent(true);
                    } else {
                        getParent().requestDisallowInterceptTouchEvent(false);
                    }
                }

                mMoveIndex++;

                if (mMoveIndex > 2) {
                    mConsumeMoveEvent = true;
                }

                mLastX = ev.getRawX();
                mLastY = ev.getRawY();
                mDeltaX = mDeltaX * RATIO;

                // 右滑
                if (mDeltaX > 0) {
                    if (!mHorizontalRecyclerView.canScrollHorizontally(-1) || mHorizontalRecyclerView.getTranslationX() < 0) {
                        float transX = mDeltaX + mHorizontalRecyclerView.getTranslationX();
                        if (mHorizontalRecyclerView.canScrollHorizontally(-1) && transX >= 0) {
                            transX = 0;
                        }
                        mHorizontalRecyclerView.setTranslationX(transX);
                    }
                } else if (mDeltaX < 0) { // 左滑
                    if (!mHorizontalRecyclerView.canScrollHorizontally(1) || mHorizontalRecyclerView.getTranslationX() > 0) {
                        float transX = mDeltaX + mHorizontalRecyclerView.getTranslationX();
                        if (transX <= 0 && mHorizontalRecyclerView.canScrollHorizontally(1)) {
                            transX = 0;
                        }
                        mHorizontalRecyclerView.setTranslationX(transX);
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                // 释放动画
                if (ReboundAnim != null && ReboundAnim.isRunning()) {
                    break;
                }

                if (mOffsetWidth != 0 && mHintLeftMargin <= mOffsetWidth && mListener != null) {
                    mListener.onRelease();
                }

                ReboundAnim = ValueAnimator.ofFloat(1.0f, 0);
                ReboundAnim.setDuration(300);
                ReboundAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float value = (float) animation.getAnimatedValue();
                        mHorizontalRecyclerView.setTranslationX(value * mHorizontalRecyclerView.getTranslationX());
                    }
                });
                ReboundAnim.start();

                break;

        }
        return mHorizontalRecyclerView.getTranslationX() != 0 || super.dispatchTouchEvent(ev);
    }

    public boolean getShowMore() {
        return mShowMore;
    }

    public void setShowMore(boolean showMore) {
        mShowMore = showMore;
    }

    public interface OnReleaseListener {
        void onRelease();
    }

    private OnReleaseListener mListener;

    public void setOnReleaseListener(OnReleaseListener listener) {
        this.mListener = listener;
    }
}
