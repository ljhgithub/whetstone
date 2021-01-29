package com.pysun.common.recyclerview;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class DividerDecoration extends RecyclerView.ItemDecoration {

    private final static String TAG = DividerDecoration.class.getSimpleName();
    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;
    private int mDividerHeight;
    private int mOffset = 0;
    private int mStarOffset = 0;
    private int mEndOffset = 0;
    private int mStarDividerHeight = 0;
    private int mEndDividerHeight = 0;
    private Paint mPaint, mStartPaint, mEndPaint;

    private final Rect mBounds = new Rect();
    private final Rect mOutRect = new Rect();
    private int mOrientation = 0;

    public DividerDecoration(int color, int dividerHeight) {
        this(HORIZONTAL, color, dividerHeight);

    }

    public DividerDecoration(int orientation, int color, int dividerHeight) {
        mOrientation = orientation;
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(color);
        mDividerHeight = dividerHeight;
        mStartPaint = new Paint(mPaint);
        mEndPaint = new Paint(mPaint);

    }

    public void setOrientation(int orientation) {
        if (orientation != 0 && orientation != 1) {
            throw new IllegalArgumentException("Invalid orientation. It should be either HORIZONTAL or VERTICAL");
        } else {
            this.mOrientation = orientation;
        }
    }

    public void setContentDivider(int offset, int dividerHeight, int dividerColor) {
        this.mOffset = offset;
        this.mDividerHeight = dividerHeight;
        mPaint.setColor(dividerColor);
    }

    public void setStartDivider(int offset, int dividerHeight, int dividerColor) {
        this.mStarOffset = offset;
        this.mStarDividerHeight = dividerHeight;
        mStartPaint.setColor(dividerColor);
    }

    public void setEndDivider(int offset, int dividerHeight, int dividerColor) {
        this.mEndOffset = offset;
        this.mEndDividerHeight = dividerHeight;
        mEndPaint.setColor(dividerColor);
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
//        super.getItemOffsets(outRect, view, parent, state);
        int position = parent.getChildAdapterPosition(view);
        view.setTag(position);
        int itemCount=0;
        if (null!=parent.getAdapter()){
            itemCount = parent.getAdapter().getItemCount();
        }
        if (mOrientation == HORIZONTAL) {

            if (itemCount == 1) {
                int l = mStarDividerHeight;
                int r = mEndDividerHeight;
                outRect.set(l, 0, r, 0);

            } else {
                if (position == 0) {
                    int l = mStarDividerHeight;
                    int r = mDividerHeight;
                    outRect.set(l, 0, r, 0);

                } else if (position == itemCount - 1) {
                    int r = mEndDividerHeight;
                    outRect.set(0, 0, r, 0);
                } else {
                    int r = mDividerHeight;
                    outRect.set(0, 0, r, 0);
                }
            }
        } else {
            if (itemCount == 1) {
                outRect.top = mStarDividerHeight;
                outRect.bottom = mEndDividerHeight;

            } else {
                if (position == 0) {
                    outRect.top = mStarDividerHeight;
                    outRect.bottom = mDividerHeight;

                } else if (position == itemCount - 1) {
                    outRect.bottom = mEndDividerHeight;
                } else {
                    outRect.bottom = mDividerHeight;
                }
            }
        }

    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
//        super.onDraw(c, parent, state);
        if (mOrientation == VERTICAL) {
            drawVertical(c, parent);
        } else {
            drawHorizontal(c, parent);
        }

    }

    private void  drawVertical(Canvas canvas, RecyclerView parent) {
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

        int childCount = parent.getChildCount();

        int itemCount=0;
        if (null!=parent.getAdapter()){
            itemCount = parent.getAdapter().getItemCount();
        }

//        LogUtils.LOGD(TAG, " drawVertical " + childCount + "  " + itemCount);
        if (itemCount == 1) {
            View child = parent.getChildAt(0);
            parent.getLayoutManager().getDecoratedBoundsWithMargins(child, this.mBounds);
//            mOutRect.set(mBounds.left + mStarOffset, mBounds.top, mBounds.right - mStarOffset, mBounds.top + mStarDividerHeight);
            mOutRect.set(mBounds.left + mStarOffset, child.getTop()-mStarDividerHeight, mBounds.right - mStarOffset, child.getTop());
            canvas.drawRect(mOutRect, mStartPaint);
//            mOutRect.set(mBounds.left + mEndOffset, mBounds.bottom - mEndDividerHeight, mBounds.right - mEndOffset, mBounds.bottom);
            mOutRect.set(mBounds.left + mEndOffset, child.getBottom() , mBounds.right - mEndOffset, child.getBottom()+ mEndDividerHeight);
            canvas.drawRect(mOutRect, mEndPaint);
        } else {

            int index;
            for (int i = 0; i < childCount; ++i) {
                View child = parent.getChildAt(i);
                index = (int) child.getTag();
                parent.getLayoutManager().getDecoratedBoundsWithMargins(child, this.mBounds);
                if (0 == index) {
                    //第一个item顶部分割线
//                    mOutRect.set(mBounds.left + mStarOffset, mBounds.top, mBounds.right - mStarOffset, mBounds.top + mStarDividerHeight);
                    mOutRect.set(mBounds.left + mStarOffset, child.getTop()-mStarDividerHeight, mBounds.right - mStarOffset, child.getTop());
                    canvas.drawRect(mOutRect, mStartPaint);
//                    mOutRect.set(mBounds.left + mOffset, mBounds.bottom - mDividerHeight, mBounds.right - mOffset, mBounds.bottom);
                    mOutRect.set(mBounds.left + mOffset, child.getBottom() , mBounds.right - mOffset, child.getBottom()+ mDividerHeight);
                    canvas.drawRect(mOutRect, mPaint);
                } else if (itemCount - 1 == index) {
                    //最后一个item低部分割线
//                    mOutRect.set(mBounds.left + mEndOffset, mBounds.bottom - mEndDividerHeight, mBounds.right - mEndOffset, mBounds.bottom);
                    mOutRect.set(mBounds.left + mEndOffset, child.getBottom() , mBounds.right - mEndOffset, child.getBottom()+ mEndDividerHeight);
                    canvas.drawRect(mOutRect, mEndPaint);

                } else {
//                    mOutRect.set(mBounds.left + mOffset, mBounds.bottom - mDividerHeight, mBounds.right - mOffset, mBounds.bottom);
                    mOutRect.set(mBounds.left + mOffset, child.getBottom() , mBounds.right - mOffset, child.getBottom()+ mDividerHeight);
                    canvas.drawRect(mOutRect, mPaint);
                }
            }
        }

        canvas.restore();
    }

    private void drawHorizontal(Canvas canvas, RecyclerView parent) {
        canvas.save();
        int left;
        int right;
        if (parent.getClipToPadding()) {
            left = parent.getPaddingLeft();
            right = parent.getWidth() - parent.getPaddingRight();
            canvas.clipRect(left, parent.getPaddingTop(), right, parent.getHeight() - parent.getPaddingBottom());
        } else {
            left = 0;
            right = parent.getWidth();
        }

        int childCount = parent.getChildCount();
        int itemCount=0;
        if (null!=parent.getAdapter()){
            itemCount = parent.getAdapter().getItemCount();
        }
//        LogUtils.LOGD(TAG, " drawVertical " + childCount + "  " + itemCount);
        if (itemCount == 1) {
            View child = parent.getChildAt(0);
            parent.getLayoutManager().getDecoratedBoundsWithMargins(child, this.mBounds);
//            mOutRect.set(mBounds.left, mBounds.top + mStarOffset, mBounds.right + mStarDividerHeight, mBounds.top - mStarOffset);
            mOutRect.set(child.getLeft()-mStarDividerHeight, mBounds.top + mStarOffset, child.getLeft(), mBounds.bottom - mStarOffset);
            canvas.drawRect(mOutRect, mStartPaint);
//            mOutRect.set(mBounds.right - mEndDividerHeight, mBounds.top + mEndOffset, mBounds.right, mBounds.bottom - mEndOffset);
            mOutRect.set(child.getRight(), mBounds.top + mEndOffset, child.getRight()+ mEndDividerHeight, mBounds.bottom - mEndOffset);
            canvas.drawRect(mOutRect, mEndPaint);
        } else {

            int index;
            for (int i = 0; i < childCount; ++i) {
                View child = parent.getChildAt(i);

                index = (int) child.getTag();
                parent.getLayoutManager().getDecoratedBoundsWithMargins(child, this.mBounds);
                if (0 == index) {
                    //第一个item左侧分割线
//                    mOutRect.set(mBounds.left, mBounds.top + mStarOffset, mBounds.left + mStarDividerHeight, mBounds.bottom - mStarOffset);
                    mOutRect.set(child.getLeft()-mStarDividerHeight, mBounds.top + mStarOffset, child.getLeft(), mBounds.bottom - mStarOffset);
                    canvas.drawRect(mOutRect, mStartPaint);
//                    mOutRect.set(mBounds.right - mDividerHeight, mBounds.top + mOffset, mBounds.right, mBounds.bottom - mOffset);
                    mOutRect.set(child.getRight(), mBounds.top + mOffset, child.getRight()+ mDividerHeight, mBounds.bottom - mOffset);
                    canvas.drawRect(mOutRect, mPaint);
                } else if (itemCount - 1 == index) {
                    //最后一个item右侧侧分割线
//                    mOutRect.set(mBounds.right - mEndDividerHeight, mBounds.top + mEndOffset, mBounds.right, mBounds.bottom - mEndOffset);
                    mOutRect.set(child.getRight(), mBounds.top + mEndOffset, child.getRight()+ mEndDividerHeight, mBounds.bottom - mEndOffset);
                    canvas.drawRect(mOutRect, mEndPaint);

                } else {
//                    mOutRect.set(mBounds.right - mDividerHeight, mBounds.top + mOffset, mBounds.right, mBounds.bottom - mOffset);
                    mOutRect.set(child.getRight() , mBounds.top + mOffset,child.getRight() + mDividerHeight, mBounds.bottom - mOffset);
                    canvas.drawRect(mOutRect, mPaint);
                }
            }
        }
        canvas.restore();
    }
}
