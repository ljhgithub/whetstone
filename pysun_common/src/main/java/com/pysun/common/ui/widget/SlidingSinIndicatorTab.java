package com.pysun.common.ui.widget;
/*
 * Create by yk on 2018/11/21
 * com.duia.ssx.lib_common.ui.widget
 */

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;

import com.duia.ssx.pysun_common.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 自定义 可以滑动切换的 Sin函数动画的指示器的自定义Tab
 */
public class SlidingSinIndicatorTab extends View {

    private int focusedTextSize, unFocusedTextSize, focusedTextColor, indicatorColor, unFocusedTextColor, tabPadding,
            indicatorStrokeWith, selectIndex = 0, sinWith, startAngel = 30, sinIndicatorHeight, indicatorTextPadding;
    private float textHeight;
    private float indicatorX;
    private Paint focusedPaint, unFocusedPaint, indicatorPaint;
    private List<String> tabTitles = new ArrayList<>();
    private float[] withRecorder, leftCoordinator;

    //static are
    private static final int PI = 180;
    private static final int DEFAULT_TEXT_SELECT_SIZE = 21;
    private static final int DEFAULT_TEXT_UNSELECTED_SIZE = 18;

    public SlidingSinIndicatorTab(Context context) {
        this(context, null);
    }

    public SlidingSinIndicatorTab(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingSinIndicatorTab(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.SlidingSinIndicatorTab);
        focusedTextSize = array.getDimensionPixelSize(R.styleable.SlidingSinIndicatorTab_foucusedTextSize,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TEXT_SELECT_SIZE, getResources().getDisplayMetrics()));
        unFocusedTextSize = array.getDimensionPixelSize(R.styleable.SlidingSinIndicatorTab_unFocusedTextSize,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_TEXT_UNSELECTED_SIZE, getResources().getDisplayMetrics()));
        focusedTextColor = array.getColor(R.styleable.SlidingSinIndicatorTab_foucusedTextColor, getContext().getResources().getColor(R.color.main_theme_color));
        unFocusedTextColor = array.getColor(R.styleable.SlidingSinIndicatorTab_unFocusedTextColor, Color.rgb(0x99, 0x99, 0x99));
        indicatorColor = focusedTextColor;
        indicatorStrokeWith = array.getDimensionPixelSize(R.styleable.SlidingSinIndicatorTab_indicatorStrokeWith,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, getResources().getDisplayMetrics()));
        tabPadding = array.getDimensionPixelSize(R.styleable.SlidingSinIndicatorTab_tabPadding,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, getResources().getDisplayMetrics()));
        sinIndicatorHeight = array.getDimensionPixelSize(R.styleable.SlidingSinIndicatorTab_sinIndicatorHeight,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
        sinWith = array.getDimensionPixelSize(R.styleable.SlidingSinIndicatorTab_sinWidth,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 39, getResources().getDisplayMetrics()));
        indicatorTextPadding = array.getDimensionPixelSize(R.styleable.SlidingSinIndicatorTab_indicatorPaddingText,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
        array.recycle();
        initial();
    }

    private float transX = 0;
    private float lastX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (!isScrolling && !set.isRunning()) {
                    for (int i = 0; i < tabTitles.size(); i++) {
                        float sw;
                        if (i == selectIndex) {
                            sw = focusedPaint.measureText(tabTitles.get(i));
                        } else
                            sw = unFocusedPaint.measureText(tabTitles.get(i));
                        if (event.getX() > leftCoordinator[i] + transX && event.getX() < leftCoordinator[i] + Math.max(sw, sinWith) + transX) {
                            //reset coordinator
                            adjustLeftCoordinator(selectIndex, i);
                            if (onTabItemSelectListener != null) {
                                onTabItemSelectListener.onTabItemSelect(i, selectIndex, tabTitles.get(i));
                            }
                            move2CenterHorizontal(i);
                            float endX = measureText(tabTitles.get(i), true) < sinWith ?
                                    (leftCoordinator[i] + sinWith * 1.0f / 6) : (leftCoordinator[i] + focusedPaint.measureText(tabTitles.get(i)) / 2 - sinWith * 1.0f / 3);
                            startSinAnimation(endX, i);
                            break;
                        }
                    }
                }
                isScrolling = false;
                break;
            case MotionEvent.ACTION_MOVE:
                //偏移量大于0 证明 从左往右滑动，则内容向右滑动
                //偏移量小于0 证明 从右往左滑动，则内容向左滑动
                if (tabTitles.size() <= 0)
                    return true;
                float dis = (event.getX() - lastX);
                //判断满足滑动条件
                if (dis >= 0 && dis < ViewConfiguration.get(getContext()).getScaledTouchSlop()
                        || (dis < 0 && dis > -ViewConfiguration.get(getContext()).getScaledTouchSlop())) {
                    isScrolling = false;
                }
                dis = dis * 20;
                //如果实际内容长度小于等于宽度 则不能滑动
                float actualWith = leftCoordinator[getTabSize() - 1] +
                        withRecorder[getTabSize() - 1];
                if (actualWith < getWidth()) {
                    isScrolling = false;
                } else {
                    if (transX + dis < 0 && (getWidth() - transX - dis <= actualWith)) {
                        isScrolling = true;
                        startTransAnimation(transX + dis);
                    } else if (transX + dis > 0) {
                        isScrolling = true;
                        startTransAnimation(0);
                    } else if ((getWidth() - transX - dis >= actualWith)) {
                        isScrolling = true;
                        startTransAnimation(getWidth() - actualWith);
                    } else {
                        isScrolling = false;
                    }
                }
                break;
            case MotionEvent.ACTION_DOWN:
                break;
            default:
                break;
        }
        lastX = event.getX();
        return true;
    }

    ValueAnimator animator, transAnimator, sinAnimator;

    private void startTransAnimation(final float dest) {
        transAnimator = ValueAnimator.ofFloat(transX, dest);
        transAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                transX = (float) animation.getAnimatedValue();
                if (transX == dest) {
                    isScrolling = false;
                }
                invalidate();
            }
        });
        transAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                isScrolling = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        transAnimator.setInterpolator(new LinearInterpolator());

        transAnimator.setDuration(200);
        transAnimator.start();
    }

    AnimatorSet set = new AnimatorSet();

    private void startSinAnimation(float endX, final int index) {
        if (set.isRunning()) {
            set.end();
        }
        sinAnimator = ValueAnimator.ofFloat(indicatorX, endX);
        sinAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                indicatorX = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        animator = ValueAnimator.ofInt(selectIndex * 360 + 30, index * 360 + 30);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                startAngel = (int) animation.getAnimatedValue();
            }
        });
        set.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                selectIndex = index;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        set.setDuration(200);
        set.setInterpolator(new LinearInterpolator());
        set.play(sinAnimator)
                .with(animator);
        set.start();
    }

    private boolean isScrolling = false;

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        //check params
        checkParams();
    }

    /**
     * 校验sin高度是否合法
     */
    private void checkParams() {
        if (indicatorTextPadding + 2 * sinIndicatorHeight + getHeight() / 2 + textHeight > getHeight()) {
            sinIndicatorHeight = (int) ((getHeight() / 2 - indicatorTextPadding - textHeight) / 2);
            Log.d(SlidingSinIndicatorTab.class.getSimpleName(), "the indicator cannot taller than the tab, indicator height has reset");
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //drawText
        drawText(canvas);
        //drawSin
        drawSin(canvas);
    }

    private void drawText(Canvas canvas) {
        for (int i = 0; i < tabTitles.size(); i++) {
            float startDrawX = leftCoordinator[i] + transX;
            if (withRecorder[i] < sinWith) {
                startDrawX += (sinWith / 2 - withRecorder[i] / 2);
            }
            if (selectIndex == i) {
                canvas.drawText(tabTitles.get(i), startDrawX, getHeight() / 2 + textHeight, focusedPaint);
            } else {
                canvas.drawText(tabTitles.get(i), startDrawX, getHeight() / 2 + textHeight, unFocusedPaint);
            }
        }
    }


    private void drawSin(Canvas canvas) {
        Path path = new Path();
        path.moveTo(transX + indicatorX, getRatioHeight(startAngel));
        for (int i = 0; i < 119; i++) {
            path.lineTo(transX + indicatorX + (i * 1.0f / PI * sinWith), getRatioHeight(startAngel + i));
        }
        canvas.drawPath(path, indicatorPaint);
    }

    private float getRatioHeight(int angel) {
        return (float) (Math.sin(angel * 1.0f / PI * Math.PI) * sinIndicatorHeight + getHeight() / 2 + textHeight)
                 + indicatorTextPadding;
    }

    public float getBaseline(Paint p) {
        Paint.FontMetrics fontMetrics = p.getFontMetrics();
        return (fontMetrics.descent - fontMetrics.ascent) / 2 - fontMetrics.descent;
    }

    /**
     * 初始化paint等操作
     */
    private void initial() {

        focusedPaint = new Paint();
        focusedPaint.setColor(focusedTextColor);
        focusedPaint.setAntiAlias(true);
        focusedPaint.setDither(true);
        focusedPaint.setStrokeWidth(3);
        focusedPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        focusedPaint.setTextSize(focusedTextSize);

        unFocusedPaint = new Paint();
        unFocusedPaint.setColor(unFocusedTextColor);
        unFocusedPaint.setAntiAlias(true);
        unFocusedPaint.setDither(true);
        unFocusedPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        unFocusedPaint.setTextSize(unFocusedTextSize);

        indicatorPaint = new Paint();
        indicatorPaint.setColor(indicatorColor);
        indicatorPaint.setStrokeWidth(indicatorStrokeWith);
        indicatorPaint.setPathEffect(new CornerPathEffect(5));
        indicatorPaint.setAntiAlias(true);
        indicatorPaint.setDither(true);
        indicatorPaint.setStrokeCap(Paint.Cap.ROUND);
        indicatorPaint.setStyle(Paint.Style.STROKE);
        textHeight = getBaseline(focusedPaint);
    }

    public interface OnTabItemSelectListener {
        void onTabItemSelect(int selectIndex, int oldIndex, String selectText);
    }

    OnTabItemSelectListener onTabItemSelectListener;

    public void setOnTabItemSelectListener(OnTabItemSelectListener onTabItemSelectListener) {
        this.onTabItemSelectListener = onTabItemSelectListener;
    }


    public void setTabTitles(String... tabTitles) {
        this.tabTitles = Arrays.asList(tabTitles);
        this.setTabTitles(this.tabTitles, 0);
    }

    public void setTabTitles(List<String> tabTitles) {
        this.setTabTitles(tabTitles, 0);
    }

    public void setTabTitles(List<String> tabTitles, int selectIndex) {
        this.tabTitles = tabTitles;
        this.selectIndex = selectIndex;
        initSyntax();
        if (onTabItemSelectListener != null) {
            onTabItemSelectListener.onTabItemSelect(selectIndex, -1, tabTitles.get(selectIndex));
        }
        invalidate();
        postDelayed(new Runnable() {
            @Override
            public void run() {
                move2CenterHorizontal(selectIndex);
            }
        }, 500);
    }

    /**
     * 初始化参数
     */
    private void initSyntax() {
        float left = 0f;
        transX = 0;
        withRecorder = new float[tabTitles.size()];
        leftCoordinator = new float[tabTitles.size()];
        for (int i = 0; i < tabTitles.size(); i++) {
            leftCoordinator[i] = left;
            if (i == selectIndex) {
                withRecorder[i] = focusedPaint.measureText(tabTitles.get(i));
                if (withRecorder[i] < sinWith) {
                    indicatorX = left + sinWith * 1.0f / 6;
                } else {
                    indicatorX = left + withRecorder[i] / 2 - sinWith * 1.0f / 3;
                }
            } else
                withRecorder[i] = unFocusedPaint.measureText(tabTitles.get(i));
            if (withRecorder[i] < sinWith) {
                left += sinWith + tabPadding;
            } else {
                left += withRecorder[i] + tabPadding;
            }
        }
    }

    /**
     * 调整x坐标
     *
     * @param oldIndex 老下标
     * @param newIndex 新下标
     */
    private void adjustLeftCoordinator(int oldIndex, int newIndex) {
        withRecorder[oldIndex] = measureText(tabTitles.get(oldIndex), false);
        withRecorder[newIndex] = measureText(tabTitles.get(newIndex), true);
        if (oldIndex < newIndex) {
            float sizeBeforeChanged = measureText(tabTitles.get(oldIndex), true)
                    - measureText(tabTitles.get(oldIndex), false);
            for (int i = oldIndex + 1; i <= newIndex; i++) {
                leftCoordinator[i] -= sizeBeforeChanged;
            }
            float sizeAfterChanged = measureText(tabTitles.get(newIndex), true)
                    - measureText(tabTitles.get(newIndex), false) - sizeBeforeChanged;
//            Log.d("yklfj",String.format("向后移动====>%1$d-%2$d缩小%3$.1f====>%4$d后变化%5$.1f",
//                    oldIndex,newIndex,sizeBeforeChanged, oldIndex, sizeAfterChanged));
            for (int i = newIndex + 1; i < tabTitles.size(); i++) {
                leftCoordinator[i] += sizeAfterChanged;
            }
        } else if (newIndex < oldIndex) {
            float sizeBeforeChanged = measureText(tabTitles.get(newIndex), true)
                    - measureText(tabTitles.get(newIndex), false);
            for (int i = newIndex + 1; i <= oldIndex; i++) {
                leftCoordinator[i] += sizeBeforeChanged;
            }
            float sizeAfterChanged = measureText(tabTitles.get(oldIndex), false)
                    - measureText(tabTitles.get(oldIndex), true) + sizeBeforeChanged;
//            Log.d("yklfj",String.format("向前移动====>%1$d-%2$d扩张%3$.1f====>%4$d后变化%5$.1f",
//                    newIndex,oldIndex,sizeBeforeChanged, oldIndex, sizeAfterChanged));
            for (int i = oldIndex + 1; i < tabTitles.size(); i++) {
                leftCoordinator[i] += sizeAfterChanged;
            }
        }
        //do nothing

    }

    /**
     * 根据状态量出合法的值
     *
     * @param text    目标内容
     * @param focused 是否选中
     * @return 真正长度
     */
    private float measureText(String text, boolean focused) {
        if (focused) {
            float w = focusedPaint.measureText(text);
            return sinWith > w ? sinWith : w;
        } else {
            float w = unFocusedPaint.measureText(text);
            return sinWith > w ? sinWith : w;
        }
    }


    /**
     * 移动到中间
     *
     * @param index 索引值
     */
    public void move2CenterHorizontal(int index) {
        int last = tabTitles.size() - 1;
        float rightSide = leftCoordinator[last] +
                withRecorder[getTabSize() - 1];
        float trans = getWidth() * 1.0f / 2 - (leftCoordinator[index] + transX + measureText(tabTitles.get(index), last == index) / 2);
        if (trans + transX < 0 && getWidth() - transX - trans <= rightSide) {
            startTransAnimation(transX + trans);
        } else if (trans + transX > 0) {
            startTransAnimation(0);
        } else if (trans + transX < 0 && getWidth() - transX - trans > rightSide
                && getWidth() < leftCoordinator[getTabSize() - 1] + withRecorder[getTabSize() - 1]) {
            startTransAnimation(getWidth() - rightSide);
        }
    }

    public int getTabSize() {
        return tabTitles == null ? 0 : tabTitles.size();
    }
}
