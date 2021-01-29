package com.pysun.common.recyclerview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class StickyHeaderDecoration extends RecyclerView.ItemDecoration {

    private final static String TAG = StickyHeaderDecoration.class.getSimpleName();
    private int mTextSize = 28;
    private int mMarginLeft = 16;
    private int mHeaderHeight = 40;
    private Paint mPaint, mTextPaint;

    private final Rect mBounds = new Rect();
    private final Rect mOutRect = new Rect();
    private final Rect mHeaderRect =new Rect();



    public StickyHeaderDecoration(int headerHeight, int textSize,int textColor,int bgColor, GroupInfoCallback callback) {

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(bgColor);
        mHeaderHeight = headerHeight;

        mTextPaint = new Paint(mPaint);
        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(textSize);
        mGroupInfoCallback = callback;
        mTextSize=textSize;

    }




    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
//        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        view.setTag(position);

        if (null != mGroupInfoCallback) {
           GroupInfo groupInfo = mGroupInfoCallback.getGroupInfo(position);
            if (groupInfo.isFirstViewInGroup()) {
                outRect.top = mHeaderHeight;
            }
        }


    }


    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
//        super.onDrawOver(c, parent, state);
        drawVertical(c, parent);
    }

    private void drawVertical(Canvas canvas, RecyclerView parent) {
        canvas.save();
        int top;
        int bottom;
        if (parent.getClipToPadding()) {
            top = parent.getPaddingTop();
            bottom = parent.getHeight() - parent.getPaddingBottom();
            canvas.clipRect(parent.getPaddingLeft(), top, parent.getWidth() - parent.getPaddingRight(), bottom);//设置画布的显示区域
        } else {
            top = 0;
            bottom = parent.getHeight();
        }
        mHeaderRect.set(parent.getPaddingLeft(),top,parent.getWidth() - parent.getPaddingRight(),top+mHeaderHeight);
        int childCount = parent.getChildCount();
        LinearLayoutManager llm = (LinearLayoutManager) parent.getLayoutManager();
        int index;
        for (int i = 0; i < childCount; ++i) {
            View child = parent.getChildAt(i);
            index = (int) child.getTag();
            parent.getLayoutManager().getDecoratedBoundsWithMargins(child, this.mBounds);


//            LogUtils.LOGD(TAG, i + " drawVertical " + index + "  " + index + "   " + llm.findFirstVisibleItemPosition());

            if (null != mGroupInfoCallback) {
               GroupInfo groupInfo = mGroupInfoCallback.getGroupInfo(index);
                if (groupInfo.isFirstViewInGroup()) {
                    mOutRect.set(mBounds.left, child.getTop() - mHeaderHeight, mBounds.right, child.getTop());
                    drawHeader(canvas,groupInfo.getTitle(),mOutRect);

                }
                if (index == llm.findFirstVisibleItemPosition()) {
                    drawStickyHeader(canvas, groupInfo.getTitle());
                }
            }


        }

        canvas.restore();
    }

    private void drawStickyHeader(Canvas canvas, String title) {
        canvas.drawRect(mHeaderRect, mPaint);
        canvas.drawText(title, mHeaderRect.left+mMarginLeft, mHeaderRect.bottom-(mHeaderHeight-mTextSize)/2, mTextPaint);
    }

    private void drawHeader(Canvas canvas, String title, Rect rect) {

        canvas.drawRect(mOutRect, mPaint);
        canvas.drawText(title, rect.left+mMarginLeft, rect.bottom-(mHeaderHeight-mTextSize)/2, mTextPaint);


    }

    private GroupInfoCallback mGroupInfoCallback;

    public interface GroupInfoCallback {
        GroupInfo getGroupInfo(int position);
    }
}
