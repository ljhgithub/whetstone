package com.pysun.common.ui.widget.drag;

import android.animation.Animator;
import android.animation.LayoutTransition;
import android.animation.ValueAnimator;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import androidx.core.view.ViewCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * 左右循环翻页自定义控件
 *
 * @author moujunfeng
 * @date 2018/6/13 11:04
 */
public class DragLoopView extends ViewGroup implements View.OnTouchListener {
    private final DecelerateInterpolator mDecelerateInterpolator;
    private static final float DECELERATE_INTERPOLATION_FACTOR = 2f;
    private static final int ANIMATE_TO_START_DURATION = 500;
    //设置每个视图的距离
    private int SPACE = 0;
    //最上面的view
    private View topView;
    //最上面view的矩阵
    private Rect topRectF;
    private Rect curTopRect = new Rect();
    //设置子控件添加删除布局的动画
    LayoutTransition mLayoutTransition;
    //设置显示的个数
    private int showNum = 3;
    private AdapterDataSetObserver adapterDataSetObserver;
    private List<View> listData = new ArrayList<View>();

    private List<ViewTag> viewTags = new ArrayList<>();
    private int firstChildWidth = 0;
    private int firstChildHeight = 0;

    private boolean mIsBeingDragged,mIsSliding;
    private float mTotalDragDistanceWidth = -1;
    private float mTotalDragDistanceHeight = -1;

    private float scaleFactor = 0.8f;
    private int offsetDistance = 20;

    private float dragDistanceFactor = 0.4f;
    public static final int HORIZONTAL_LEFT = 0;
    public static final int HORIZONTAL_RIGHT = 1;
    public static final int VERTICAL_TOP = 3;
    public static final int VERTICAL_BOTTOM = 4;
    private int orientation = HORIZONTAL_RIGHT;
    private boolean canSlide = false;

    private int duration = ANIMATE_TO_START_DURATION;

    public DragLoopView(Context context) {
        this(context, null);

    }

    private BaseDragLoopAdapter mAdapter;

    public void setShowNum(int showNum) {
        this.showNum = showNum;
        invalidate();
    }

    public void setAdapter(BaseDragLoopAdapter adapter, int showNum) {
        this.showNum = showNum;
        if (null != mAdapter) {
            mAdapter.registerDataSetObserver(adapterDataSetObserver);
        }
        this.mAdapter = adapter;
        adapterDataSetObserver = new AdapterDataSetObserver();
        mAdapter.registerDataSetObserver(adapterDataSetObserver);
        initAdapterView();
    }

    public void setAdapter(BaseDragLoopAdapter adapter) {
        setAdapter(adapter, showNum);
    }

    public BaseDragLoopAdapter getAdapter() {
        return mAdapter;
    }


    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getShowNum() {
        return showNum;
    }

    public void setOrientation(int orientation) {
        this.orientation = orientation;
    }

    public void setOffsetDistance(int offsetDistance) {
        this.offsetDistance = offsetDistance;
    }

    public void setScaleFactor(float scaleFactor) {
        this.scaleFactor = scaleFactor;
    }

    public void setCanSlide(boolean canSlide) {
        this.canSlide = canSlide;
    }

    public DragLoopView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragLoopView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDecelerateInterpolator = new DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);
        init();
    }

    private void init() {
        mLayoutTransition = new LayoutTransition();
        setLayoutTransition(mLayoutTransition);
        setOnTouchListener(this);

    }


    public void initAdapterView() {
        removeAllViews();
        if (null == mAdapter || mAdapter.isEmpty()) return;
        listData.clear();
        viewTags.clear();
        int count = mAdapter.getCount();
        View view;
        int showIndex;
        int minCount = Math.min(count, showNum);
        for (int i = 0; i < minCount; i++) {
            viewTags.add(i, new ViewTag(i, i));
            view = mAdapter.onCreateView(i, this);
            mAdapter.onBindView(i,view,DragLoopView.this);
            listData.add(view);
        }


        LayoutParams layoutParams;
        for (int i = 0; i < minCount; i++) {
            showIndex = minCount - 1 - i;
            view = listData.get(showIndex);
            layoutParams = view.getLayoutParams();
            DragLoopView.this.addViewInLayout(view, i, layoutParams == null ? generateDefaultLayoutParams() : layoutParams);

        }
        requestLayout();
    }

    private View getRecycler(int position) {

        if (listData.size() > position) {
            return listData.get(position);
        }
        return null;
    }

    private void setData(List<View> views) {
        if (null == views || views.isEmpty()) return;
        listData.clear();
        listData.addAll(views);

        viewTags = new ArrayList<>();
        int count = listData.size();
        View view;
        int showIndex;
        for (int i = 0; i < count; i++) {
            view = listData.get(i);
            viewTags.add(i, new ViewTag(view, i));


        }
        for (int i = 0; i < count && i < showNum; i++) {
            showIndex = count - 1 - i;
            view = listData.get(showIndex);
            DragLoopView.this.addView(view, i);

        }
    }


    private void moveToOffsetLeftAndRight(int position) {
        int offset = position - topView.getLeft();
        ViewCompat.offsetLeftAndRight(topView, offset);
    }

    private void moveToOffsetTopAndBottom(int position) {
        int offset = position - topView.getTop();

        ViewCompat.offsetTopAndBottom(topView, offset);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int count = getChildCount();
        for (int i = 0; i < count; i++) {
            ViewTag viewTag = viewTags.get(i);
            View view = viewTag.getView();
//            mAdapter.onBindView(viewTag.getPosition(), view, this);
//            viewTag.getRect().set(l + viewTag.getOffsetWidth(), t + viewTag.getOffsetHeight(), l + viewTag.getWidth() + viewTag.getOffsetWidth(), t + viewTag.getHeight() + viewTag.getOffsetHeight());
            viewTag.getRect().set(0 , 0 , 0 + viewTag.getWidth(), 0 + viewTag.getHeight());

            view.layout(viewTag.getRect().left, viewTag.getRect().top, viewTag.getRect().right, viewTag.getRect().bottom);
            float scale= (float) Math.pow(scaleFactor, i);
            view.setScaleX(scale);
            view.setScaleY(scale);

            if (HORIZONTAL_RIGHT == orientation) {

                view.setTranslationX(+( (viewTag.getWidth()*(1-scale)/2.0f)+offsetDistance*(i)));
            } else if (HORIZONTAL_LEFT == orientation) {

                view.setTranslationX( -(viewTag.getWidth()*(1-scale)/2.0f)+offsetDistance*(count-i-1));


            } else if (VERTICAL_TOP == orientation) {

                view.setTranslationY( -(viewTag.getWidth()*(1-scale)/2.0f)+offsetDistance*(count-i-1));


            } else if (VERTICAL_BOTTOM == orientation) {

                view.setTranslationY( +((viewTag.getHeight()*(1-scale)/2.0f)+offsetDistance*(i)));

            } else {

                view.setTranslationX(+( (viewTag.getWidth()*(1-scale)/2.0f)+offsetDistance*(i)));

            }




        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        //获得控件的宽高
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int paddingRight = getPaddingRight();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingBottom = getPaddingTop();
        int count = getChildCount();
        int firstChildWidth = 0;
        int firstChildHeight = 0;
        int flexibleWidth = 0;
        int flexibleHeight = 0;
        ViewTag viewTag;
        flexibleWidth = offsetDistance * (getChildCount() - 1);
        flexibleHeight = flexibleWidth;
        for (int i = 0; i < count; i++) {
            viewTag = viewTags.get(i);
//
//            int childWidthMeasureSpec1 = MeasureSpec.makeMeasureSpec(width, widthMode == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST : widthMode);
//            int childHeightMeasureSpec1 = MeasureSpec.makeMeasureSpec(height, heightMode == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST : heightMode);
//






            View childView = viewTag.getView();//最后一个view,做为第一个看板


            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, widthMode);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, heightMode);

            //
            //测量子控件
            childView.measure(childWidthMeasureSpec, childHeightMeasureSpec);


            if (i == 0) {
//                childMeasure(childView, 0, widthMeasureSpec, heightMeasureSpec);
                firstChildWidth = childView.getMeasuredWidth();
                firstChildHeight = childView.getMeasuredHeight();
                mTotalDragDistanceWidth = firstChildWidth * dragDistanceFactor;
                mTotalDragDistanceHeight = firstChildHeight * dragDistanceFactor;
                viewTag.setTag(0);
                viewTag.setSize(firstChildWidth, firstChildHeight);
                if (HORIZONTAL_LEFT == orientation) {
                    viewTag.setOffset(offsetDistance * (count - 1), 0);
                } else if (VERTICAL_TOP == orientation) {
                    viewTag.setOffset(0, offsetDistance * (count - 1));
                } else {
                    viewTag.setOffset(0, 0);
                }


//                Log.e("LooperPagerView", i + ",width otherWidth =" + firstChildWidth + " height =" + firstChildHeight);
            } else {
                int otherWidth = (int) (firstChildWidth * Math.pow(scaleFactor, i));
                int otherHeight = (int) (firstChildHeight * Math.pow(scaleFactor, i));

//                int otherWidth = (firstChildWidth );
//                int otherHeight = (firstChildHeight );


//                childMeasure(childView, 0, MeasureSpec.makeMeasureSpec(otherWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(otherHeight, MeasureSpec.EXACTLY));
//                childMeasure(childView, 0, MeasureSpec.makeMeasureSpec(firstChildWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(firstChildHeight, MeasureSpec.EXACTLY));



                viewTag.setTag(i);
//                viewTag.setSize(otherWidth, otherHeight);
                viewTag.setSize(firstChildWidth, firstChildHeight);
                if (HORIZONTAL_RIGHT == orientation) {
                    viewTag.setOffset(firstChildWidth - otherWidth + offsetDistance * i, (firstChildHeight - otherWidth) / 2);//左右

                } else if (HORIZONTAL_LEFT == orientation) {
                    viewTag.setOffset(offsetDistance * (count - 1 - i), (firstChildHeight - otherWidth) / 2);//左右

                } else if (VERTICAL_TOP == orientation) {
                    viewTag.setOffset((firstChildWidth - otherWidth) / 2, offsetDistance * (count - 1 - i));//上下


                } else if (VERTICAL_BOTTOM == orientation) {
                    viewTag.setOffset((firstChildWidth - otherWidth) / 2, (firstChildHeight - otherHeight + offsetDistance * i));//上下

                } else {
                    viewTag.setOffset(firstChildWidth - otherWidth + offsetDistance * i, (firstChildHeight - otherWidth) / 2);//左右


                }

            }
        }

//        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
//                height);

        setMeasuredDimension(flexibleWidth + firstChildWidth,
                flexibleHeight + firstChildHeight);


    }

    private void childMeasure(View childView, int usedSize, int widthMeasureSpec, int heightMeasureSpec) {
        LayoutParams layoutParams = childView.getLayoutParams();
        childView.measure(resolveSize(usedSize, widthMeasureSpec, layoutParams.width), resolveSize(usedSize, heightMeasureSpec, layoutParams.height));
//        Log.e("tag", "childMeasure" + childView.getMeasuredWidth() + "  " + childView.getMeasuredHeight());

    }

    /**
     * @param usedSize    已经被使用的空间
     * @param measureSpec
     * @param lpSize      子view LayoutParams获取的height或者width
     * @return
     */
    private int resolveSize(int usedSize, int measureSpec, int lpSize) {

        int selfSpecMode = MeasureSpec.getMode(measureSpec);
        int selfSpecSize = MeasureSpec.getSize(measureSpec);
        int childMeasureSpec;

        switch (lpSize) {
            case LayoutParams.MATCH_PARENT:
                //子类view要求填充父类view空间,父类mode为EXACTLY或AT_MOST
                if (selfSpecMode == MeasureSpec.EXACTLY || selfSpecMode == MeasureSpec.AT_MOST) {
                    //此时子view的可用空间是父类view measureSpec中的size
                    childMeasureSpec = MeasureSpec.makeMeasureSpec(selfSpecSize, MeasureSpec.EXACTLY);

                } else {
                    //父类mode为UNSPECIFIED，无法满足子view的MATCH_PARENT，子view mode 设置UNSPECIFIED，size写0即可
                    childMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                }

                break;
            case LayoutParams.WRAP_CONTENT:
                //父类mode为EXACTLY或AT_MOST
                if (selfSpecMode == MeasureSpec.EXACTLY || selfSpecMode == MeasureSpec.AT_MOST) {
                    //给子view 的mode为AT_MOST，给size设置一个最大值让其自己测量
                    childMeasureSpec = MeasureSpec.makeMeasureSpec(selfSpecSize - usedSize, MeasureSpec.AT_MOST);
                } else {
                    //父类mode为UNSPECIFIED 对子view 不做限制
                    childMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
                }
                break;

            default: //子view size有具体值

                childMeasureSpec = MeasureSpec.makeMeasureSpec(lpSize, MeasureSpec.EXACTLY);

                break;
        }

        return childMeasureSpec;
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        return super.dispatchTouchEvent(ev);
    }

    int beforeX = 0;
    int beforeY = 0;

    int differX;
    int differY;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!canSlide) return false;
//        Log.e("tag","MotionEvent"+isLayout);
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN://按下y
                beforeX = (int) ev.getX();
                beforeY = (int) ev.getY();

                mIsBeingDragged = false;
                break;
            case MotionEvent.ACTION_MOVE://移动
                mIsBeingDragged = true;
//                Log.e("MotionEvent", "ACTION_MOVE");
//                int new_x = (int) ev.getX();
//                int new_y = (int) ev.getY();
//                //判断有水平滑动的意向
//                int move_x = Math.abs(new_x - beforeX);//x轴滑动的距离
//                int move_y = Math.abs(new_y - beforeY);//y轴滑动的距离
//                //10的偏移量
//                if (move_x > move_y) {
//                    //请求父类不要拦截
//                    getParent().requestDisallowInterceptTouchEvent(true);
//                    return true;//传递给字View
//                } else {//下面这句 应该不会执行 但是保险起见还是放在这里
//                    getParent().requestDisallowInterceptTouchEvent(false);
//                    return false;
//                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                mIsBeingDragged = false;
//                if (topRectF.contains((int) ev.getX(), (int) ev.getY())) {
//                    return false;
//                }
                break;
        }
        return mIsBeingDragged;

    }

    ValueAnimator offsetOriginPositionAnimator;

    private void animateOffsetToStartPosition(Animator.AnimatorListener listener) {

        final int originPosition;
        final int currentPosition;
        final int originOffset;
        if (HORIZONTAL_LEFT == orientation || HORIZONTAL_RIGHT == orientation) {
            originPosition = topRectF.left;
            currentPosition = curTopRect.left;

            originOffset = originPosition - curTopRect.left;
        } else {
            originPosition = topRectF.left;
            currentPosition = curTopRect.left;

            originOffset = originPosition - curTopRect.left;
        }


        offsetOriginPositionAnimator = new ValueAnimator();
        offsetOriginPositionAnimator.setIntValues(currentPosition, originPosition);

        if (null != listener) {
            offsetOriginPositionAnimator.addListener(listener);
        }

        offsetOriginPositionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float interpolatedTime = animation.getAnimatedFraction();
                if (HORIZONTAL_RIGHT == orientation || HORIZONTAL_LEFT == orientation) {
                    moveToOffsetLeftAndRight((int) (originOffset * interpolatedTime + currentPosition));
                } else {
                    moveToOffsetTopAndBottom((int) (originOffset * interpolatedTime + currentPosition));
                }

            }
        });
        offsetOriginPositionAnimator.setDuration(ANIMATE_TO_START_DURATION);
        offsetOriginPositionAnimator.setInterpolator(mDecelerateInterpolator);
        offsetOriginPositionAnimator.start();

    }

    ValueAnimator offsetOutLeftAnimator;

    private void animateOffsetToOutTop(Animator.AnimatorListener listener) {
        int outLeftPosition = -topRectF.height();
        final int currentPosition = curTopRect.top;

        final int outLeftOriginOffset = outLeftPosition - curTopRect.top;
        offsetOutLeftAnimator = new ValueAnimator();
        offsetOutLeftAnimator.setIntValues(currentPosition, outLeftPosition);

        if (null != listener) {
            offsetOutLeftAnimator.addListener(listener);
        }

        offsetOutLeftAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float interpolatedTime = animation.getAnimatedFraction();
                moveToOffsetTopAndBottom((int) (outLeftOriginOffset * interpolatedTime + currentPosition));

            }
        });
        offsetOutLeftAnimator.setDuration(duration);
        offsetOutLeftAnimator.setInterpolator(mDecelerateInterpolator);
        offsetOutLeftAnimator.start();

    }


    private void animateOffsetToOutLeft(Animator.AnimatorListener listener) {
        int outLeftPosition = -topRectF.width();
        final int currentPosition = curTopRect.left;

        final int outLeftOriginOffset = outLeftPosition - curTopRect.left;
        offsetOutLeftAnimator = new ValueAnimator();
        offsetOutLeftAnimator.setIntValues(currentPosition, outLeftPosition);

        if (null != listener) {
            offsetOutLeftAnimator.addListener(listener);
        }

        offsetOutLeftAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float interpolatedTime = animation.getAnimatedFraction();
                moveToOffsetLeftAndRight((int) (outLeftOriginOffset * interpolatedTime + currentPosition));
            }
        });
        offsetOutLeftAnimator.setDuration(duration);
        offsetOutLeftAnimator.setInterpolator(mDecelerateInterpolator);
        offsetOutLeftAnimator.start();

    }

    ValueAnimator offsetOutRightAnimator;

    private void animateOffsetToOutRight(Animator.AnimatorListener listener) {
        int outLeftPosition = getWidth() + topRectF.width();
        final int currentPosition = curTopRect.left;

        final int outLeftOriginOffset = outLeftPosition - curTopRect.left;
        offsetOutRightAnimator = new ValueAnimator();
        offsetOutRightAnimator.setIntValues(currentPosition, outLeftPosition);

        if (null != listener) {
            offsetOutRightAnimator.addListener(listener);
        }

        offsetOutRightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float interpolatedTime = animation.getAnimatedFraction();
                moveToOffsetLeftAndRight((int) (outLeftOriginOffset * interpolatedTime + currentPosition));
            }
        });
        offsetOutRightAnimator.setDuration(duration);
        offsetOutRightAnimator.setInterpolator(mDecelerateInterpolator);
        offsetOutRightAnimator.start();

    }


    private void animateOffsetToOutBottom(Animator.AnimatorListener listener) {
        int outLeftPosition = getHeight() + topRectF.height();
        final int currentPosition = curTopRect.top;

        final int outLeftOriginOffset = outLeftPosition - curTopRect.top;
        offsetOutRightAnimator = new ValueAnimator();
        offsetOutRightAnimator.setIntValues(currentPosition, outLeftPosition);

        if (null != listener) {
            offsetOutRightAnimator.addListener(listener);
        }

        offsetOutRightAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float interpolatedTime = animation.getAnimatedFraction();

                moveToOffsetTopAndBottom((int) (outLeftOriginOffset * interpolatedTime + currentPosition));

            }
        });
        offsetOutRightAnimator.setDuration(duration);
        offsetOutRightAnimator.setInterpolator(mDecelerateInterpolator);
        offsetOutRightAnimator.start();

    }

    public void auto() {

        if (mIsBeingDragged) return;
        mTotalDragDistanceWidth = -10;
        mTotalDragDistanceHeight = -10;
        topView = viewTags.get(0).getView();
        topRectF = viewTags.get(0).getRect();
        curTopRect.set(topRectF);
        curTopRect.offset(0, 0);
        finishDrag(-1);
    }

    private void finishDrag(float overscrollLeft) {

        float totalDragDistance = (HORIZONTAL_LEFT == orientation || HORIZONTAL_RIGHT == orientation) ? mTotalDragDistanceWidth : mTotalDragDistanceHeight;
        if (Math.abs(overscrollLeft) < totalDragDistance) {
            animateOffsetToStartPosition(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    mIsBeingDragged = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    mIsBeingDragged = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    mIsBeingDragged = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        } else {

            if (overscrollLeft < 0) {//左
                Animator.AnimatorListener listener = new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mIsBeingDragged = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
//                        ViewTag viewTag = viewTags.get(0);
//                        viewTags.remove(0);
//                        viewTags.add(viewTag);
//                        removeView(viewTag.getView());
//                        addView(viewTag.getView(), 0);

                        if (mAdapter.getCount()<=0)return;
                        int newPosition;
                        for (ViewTag viewTag : viewTags) {
                            newPosition = viewTag.getPosition() + 1;
                            newPosition = newPosition % mAdapter.getCount();
                            viewTag.setPosition(newPosition);
                        }
                        View view = listData.get(0);
                        listData.remove(0);

                      ViewTag viewTag=  viewTags.get(0);
                      mAdapter.onBindView(viewTag.getPosition(),view,DragLoopView.this);



                        listData.add(view);

                        if (null!=dragLoopOutListener){
                            dragLoopOutListener.outListener(view);
                        }

                        DragLoopView.this.removeViewInLayout(view);
                        DragLoopView.this.addView(view, 0);
                        mIsBeingDragged = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        mIsBeingDragged = false;
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                };
                if (HORIZONTAL_RIGHT == orientation || HORIZONTAL_LEFT == orientation) {
                    animateOffsetToOutLeft(listener);
                } else {
                    animateOffsetToOutTop(listener);
                }

            } else {
                Animator.AnimatorListener listener = new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        mIsBeingDragged = true;
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
//                        ViewTag viewTag = viewTags.get(0);
//                        viewTags.remove(0);
//                        viewTags.add(1, viewTag);
//                        removeView(viewTag.getView());
//                        addView(viewTag.getView(), 1);



                        viewTags.get(0).setPosition(1);
                        viewTags.get(1).setPosition(0);


                        View view = listData.get(0);
                        listData.remove(0);

                        ViewTag viewTag=  viewTags.get(1);
                        mAdapter.onBindView(viewTag.getPosition(),view,DragLoopView.this);

                        listData.add(1, view);


                        if (null!=dragLoopOutListener){
                            dragLoopOutListener.outListener(view);
                        }


                       DragLoopView.this. removeViewInLayout(view);
                        DragLoopView.this.addView(view, 1);
                        mIsBeingDragged = false;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        mIsBeingDragged = false;
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                };

                if (HORIZONTAL_RIGHT == orientation || HORIZONTAL_LEFT == orientation) {
                    animateOffsetToOutRight(listener);
                } else {
                    animateOffsetToOutBottom(listener);
                }


            }

        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!canSlide) return true;
        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                beforeX = (int) event.getX();
                beforeY = (int) event.getY();
                mIsBeingDragged = false;
                topView = viewTags.get(0).getView();
                topRectF = viewTags.get(0).getRect();
                curTopRect.set(topRectF);

                break;
            case MotionEvent.ACTION_MOVE: {

                int temp;
                if (HORIZONTAL_LEFT == orientation || HORIZONTAL_RIGHT == orientation) {
                    int cusX = (int) event.getX();
                    differX = cusX - beforeX;
                    beforeX = cusX;
                    temp = Math.abs(differX);

                    if (temp <= 10) {
                        return false;
                    }
                    mIsBeingDragged = true;

                    topView = viewTags.get(0).getView();
                    topRectF = viewTags.get(0).getRect();
                    curTopRect.offset(differX, 0);
//                topView.layout(beofreRect.left, beofreRect.top, beofreRect.right, beofreRect.bottom);
                    ViewCompat.offsetLeftAndRight(topView, differX);

                } else {

                    int cusY = (int) event.getY();
                    differY = cusY - beforeY;
                    beforeY = cusY;
                    temp = Math.abs(differY);


                    if (temp <= 10) {
                        return false;
                    }
                    mIsBeingDragged = true;

                    topView = viewTags.get(0).getView();
                    topRectF = viewTags.get(0).getRect();
                    curTopRect.offset(0, differY);
//                topView.layout(beofreRect.left, beofreRect.top, beofreRect.right, beofreRect.bottom);


                    ViewCompat.offsetTopAndBottom(topView, differY);
                }


            }
            break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                if (mIsBeingDragged) {


                    if (HORIZONTAL_RIGHT == orientation || HORIZONTAL_LEFT == orientation) {
                        ViewTag viewTag = viewTags.get(0);
                        int offsetX = topView.getLeft() - viewTag.getRect().left;
                        finishDrag(offsetX);
                    } else {
                        ViewTag viewTag = viewTags.get(0);
                        int offsetY = topView.getTop() - viewTag.getRect().top;
                        finishDrag(offsetY);
                    }
                    mIsBeingDragged = false;

                }
                return false;

            }

        }
        return true;
    }


    /**
     * 获得缩放值
     *
     * @param curValue
     * @return
     */
    private float getScale(float min, float max, float curValue) {
        float temp = (float) curValue / SPACE;
        //区间值
        float diff = max - min;
        float resultDiff = temp * diff;
        float result = min + resultDiff;
        return result;
    }


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (null != mAdapter) {
            mAdapter.unregisterDataSetObserver(adapterDataSetObserver);
        }
    }

    public class ViewTag {
        private Integer tag;
        private View view;
        private Integer position;
        private Rect rect = new Rect();
        private int offsetWidth, offsetHeight;
        private int width, height;

        public ViewTag(Integer tag, Integer position) {
            this.tag = tag;
            this.position = position;
        }

        public ViewTag(View view, Integer tag) {
            this.view = view;
            this.tag = tag;
        }

        public void setRect(Rect rect) {
            this.rect = rect;
        }

        public Rect getRect() {
            return rect;
        }

        public void setSize(int width, int height) {
            this.width = width;
            this.height = height;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public void setWidth(int width) {
            this.width = width;
        }

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }

        public void setTag(Integer tag) {
            this.tag = tag;
        }

        public Integer getTag() {
            return tag;
        }

        public View getView() {
            return listData.get(tag);
        }

        public void setOffset(int offsetWidth, int offsetHeight) {
            this.offsetWidth = offsetWidth;
            this.offsetHeight = offsetHeight;
        }

        public void setOffsetHeight(int offsetHeight) {
            this.offsetHeight = offsetHeight;
        }

        public void setOffsetWidth(int offsetWidth) {
            this.offsetWidth = offsetWidth;
        }

        public int getOffsetHeight() {
            return offsetHeight;
        }

        public int getOffsetWidth() {
            return offsetWidth;
        }

        public void setPosition(Integer position) {
            this.position = position;
        }

        public Integer getPosition() {
            return position;
        }
    }

    public class AdapterDataSetObserver extends DataSetObserver {

        @Override
        public void onChanged() {
            super.onChanged();
            initAdapterView();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
        }
    }


    DragLoopOutListener dragLoopOutListener;

    public void setDragLoopOutListener(DragLoopOutListener dragLoopOutListener) {
        this.dragLoopOutListener = dragLoopOutListener;
    }

    public interface DragLoopOutListener{

        void outListener(View view);
    }
}

