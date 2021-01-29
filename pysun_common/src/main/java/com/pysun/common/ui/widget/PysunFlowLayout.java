package com.pysun.common.ui.widget;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.duia.ssx.pysun_common.R;

import java.util.ArrayList;
import java.util.List;


public class PysunFlowLayout extends ViewGroup {
    private int horizontalSpace;
    private int verticalSpace;
    private Integer lines;
    public final static int SPREAD = 1;
    public final static int SPREAD_INSIDE = 2;
    private int chainType =SPREAD_INSIDE;

    private List<Row> rows;
    private Row curRow;
    private int preElementBottom;

    public PysunFlowLayout(Context context) {
        this(context, null);
    }

    public PysunFlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PysunFlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typed = context.obtainStyledAttributes(attrs, R.styleable.PysunFlowLayout);
        horizontalSpace = typed.getDimensionPixelOffset(R.styleable.PysunFlowLayout_flHorizontalSpacing, 20);
        verticalSpace = typed.getDimensionPixelOffset(R.styleable.PysunFlowLayout_flVerticalSpacing, 15);
        lines = typed.getInteger(R.styleable.PysunFlowLayout_flLines, Integer.MAX_VALUE);
        typed.recycle();

        init();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        int paddingRight = getPaddingRight();
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int useWidth = width - paddingLeft - paddingRight;
        int useHeight = height - paddingTop - getPaddingBottom();
        //开始遍历所有的子控件
        int childCount = getChildCount();
        rows.clear();
        curRow.width = 10000;
        curRow.height = -verticalSpace;
        preElementBottom = paddingTop;
        int flowWidth = paddingLeft;
        for (int i = 0; i < childCount; i++) {

            View childView = getChildAt(i);
            //获取子控件的尺寸，与测量模式
            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(useWidth, widthMode == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST : widthMode);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(useHeight, heightMode == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST : heightMode);
            //测量子控件
            childView.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            int childWidth = childView.getMeasuredWidth();//子控件的宽度
            int childHeight = childView.getMeasuredHeight();//子控件的高度


            if (curRow.width + childWidth - paddingLeft > useWidth) {//新加一行

                if (rows.size() >= lines) {

                    break;
                }
                preElementBottom += (curRow.height + verticalSpace);
                if (curRow.width != 10000) {
                    curRow.width -= horizontalSpace;//上一行的宽度的horizontalSpace
                    flowWidth = Math.max(flowWidth, curRow.width);
                }

                rows.add(curRow);
                curRow = new Row(paddingLeft, 0);
                curRow.setParentWidth(useWidth);

            }
            curRow.addElement(new Element(childView, curRow.width, preElementBottom, curRow.width + childWidth, childHeight + preElementBottom));
            curRow.width += (childWidth + horizontalSpace);
            curRow.height = Math.max(curRow.height, childHeight);//行内元素最大高度作为行高
//            if (i == childCount - 1) {//如果最后一行只有一个元素，计算FlowLayout的最终高度
//                preElementBottom += curRow.height;
//                curRow.width -= horizontalSpace;//最后一行的宽度的horizontalSpace
//                rows.add(curRow);
//                rows.remove(0);//移除默认添加的第一行
//                flowWidth = Math.max(flowWidth, curRow.width);//最后一行
//
//            }

        }


        preElementBottom += curRow.height;//最后一行底部Y坐标
        curRow.width -= horizontalSpace;//最后一行的宽度的horizontalSpace
        rows.add(curRow);
        curRow.setParentWidth(useWidth);
        rows.remove(0);//移除默认添加的第一行
        flowWidth = Math.max(flowWidth, curRow.width);//最后一行


        setMeasuredDimension(flowWidth + getPaddingRight(), preElementBottom +

                getPaddingBottom());
    }


    public void setChainType(int type){
        this.chainType =type;
    }
    public void init() {
        rows = new ArrayList<>();
        curRow = new Row(10000, -verticalSpace);//默认添加的第一行
        preElementBottom = 0;

        addDefaultDisappearingAnim();

    }

    public void addDefaultDisappearingAnim() {
        LayoutTransition mLayoutTransition = new LayoutTransition();
        PropertyValuesHolder disappearingTranslationY = PropertyValuesHolder.ofFloat("translationY", 10, 0, 10, 0);
        PropertyValuesHolder disappearingTranslationX = PropertyValuesHolder.ofFloat("translationX", 10, 0, 10, 0);
        ObjectAnimator mAnimatorDisappearing = ObjectAnimator.ofPropertyValuesHolder((Object) null, disappearingTranslationY, disappearingTranslationX);
        mAnimatorDisappearing.setDuration(300);
        mLayoutTransition.setAnimator(LayoutTransition.DISAPPEARING, mAnimatorDisappearing);
        setLayoutTransition(mLayoutTransition);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        for (Row row : rows) {

            row.buildFixedSize(chainType);
            for (int i = 0; i < row.elements.size(); i++) {
                Element element = row.elements.get(i);
                if (chainType == SPREAD) {
                    element.layout(row.getFixedSize() * (row.elements.size() == 1 ? 1 : (i + 1)) - horizontalSpace * (i));
                } else if (chainType == SPREAD_INSIDE) {
                    element.layout(row.getFixedSize() * (row.elements.size() == 1 ? 1 : i) - horizontalSpace * (i));
                } else {
                    element.layout();
                }

//                element.toString();
            }
        }

    }

    public class Row {
        private int parentWidth;
        List<Element> elements;
        int width;
        int height;


        private int fixedSize;

        Row(int width, int height) {
            elements = new ArrayList<>();
            this.width = width;
            this.height = height;
        }

        public void setParentWidth(int parentWidth) {
            this.parentWidth = parentWidth;
        }


        public void buildFixedSize(int chainType) {
            int freeSpace = parentWidth;
            for (Element element : elements) {
                freeSpace -= (element.right - element.left);
            }
            if (elements.size() > 1) {
                if (chainType==SPREAD){
                    fixedSize = freeSpace / (elements.size() + 1);
                }else if (chainType==SPREAD_INSIDE){
                    fixedSize = freeSpace / (elements.size() - 1);
                }else {
                    fixedSize = freeSpace / (elements.size() - 1);
                }

            } else if (elements.size() == 1) {
                fixedSize = freeSpace / 2;
            }
        }

        public int getFixedSize() {
            return fixedSize;
        }

        void addElement(Element element) {
            elements.add(element);
        }
    }

    public void setLines(Integer lines) {
        this.lines = lines;
        invalidate();
    }

    public void setHorizontalSpace(int horizontalSpace) {
        this.horizontalSpace = horizontalSpace;
        invalidate();
    }

    public void setVerticalSpace(int verticalSpace) {
        this.verticalSpace = verticalSpace;
        invalidate();
    }

    public class Element {
        private View view;
        private int top, left, right, bottom;

        Element(View view, int left, int top, int right, int bottom) {
            this.view = view;
            this.top = top;
            this.left = left;
            this.right = right;
            this.bottom = bottom;
        }

        public void layout() {
            view.layout(left, top, right, bottom);
        }

        public void layout(int offset) {
            view.layout(left + offset, top, right + offset, bottom);
        }

        @Override
        public String toString() {
            return "top = " + top + "left = " + left + " right = " + right + " bottom = " + bottom;
        }
    }
}
