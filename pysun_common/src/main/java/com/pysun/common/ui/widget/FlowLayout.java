package com.pysun.common.ui.widget;
/*
 * Create by yk on 2019-09-05
 * com.duia.ssx.lib_common.ui.widget
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RadioGroup;

/**
 * 流式布局
 */
public class FlowLayout extends RadioGroup {

    //默认25dp的子项间距
    private int flowPadding = (int) (getResources().getDisplayMetrics().density * 25 + 0.5f);
    private int flowLineSpacing = (int) (getResources().getDisplayMetrics().density * 40 + 0.5f);

    public FlowLayout(Context context) {
        super(context);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (parentWidth <= 0 || childCount == 0)
            return;
        int lineRest = parentWidth;
        int left = 0, top = 0;
        for (int i = 0; i < childCount; i++) {
            View childAt = getChildAt(i);
            measureChild(childAt, widthMeasureSpec, heightMeasureSpec);
            int childAtWith = childAt.getMeasuredWidth();
            //能放下一个
            if (lineRest - childAtWith > 0) {
                childAt.setTag(new MeasureInfo(left, top, left + childAtWith, top + childAt.getMeasuredHeight()));
                left = left + childAtWith + flowPadding;
                lineRest = lineRest - childAtWith - flowPadding;
            } else {
                //change line
                lineRest = parentWidth;
                left = 0;
                top += childAt.getMeasuredHeight() + flowLineSpacing;
                childAt.setTag(new MeasureInfo(left, top, left + childAtWith, top + childAt.getMeasuredHeight()));
                lineRest = lineRest - childAtWith - flowPadding;
                left = left + childAtWith + flowPadding;
            }
        }
        setMeasuredDimension(parentWidth, Math.max(parentHeight, top + getChildAt(childCount - 1).getMeasuredHeight()));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        int childCount = getChildCount();
        if (childCount == 0)
            return;
        else {
            for (int i = 0; i < childCount; i++) {
                View child = getChildAt(i);
                MeasureInfo info = (MeasureInfo) child.getTag();
                if (info == null)
                    break;
                child.layout(info.getLeft(), info.getTop(), info.getRight(), info.getBottom());
            }
        }
    }

    private class MeasureInfo {
        private int left, top, right, bottom;

        public MeasureInfo(int left, int top, int right, int bottom) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
        }

        public int getLeft() {
            return left;
        }

        public int getTop() {
            return top;
        }

        public int getRight() {
            return right;
        }

        public int getBottom() {
            return bottom;
        }
    }

}
