package com.pysun.common.ui.widget.tab;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;

import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pools;
import androidx.core.view.ViewCompat;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;

import com.duia.ssx.pysun_common.R;
import com.google.android.material.internal.TextScale;


/**
 * Created by ljh on 2018/1/12.
 */

public class PsNavigationView extends FrameLayout {
    private static final long ACTIVE_ANIMATION_DURATION_MS = 115L;
    private TransitionSet mSet;
    private int mInactiveItemMaxWidth;
    private int mInactiveItemMinWidth;
    private int mActiveItemMaxWidth;
    private int mItemHeight;
    private int[] mTempChildWidths;
    private OnClickListener mOnClickListener;
    private boolean mShiftingMode = false;
    //    private int mSelectedItemId = 0;
    private int mSelectedItemPosition = 0;
    private TabMenuView[] mButtons;
    private final Pools.Pool<TabMenuView> mItemPool = new Pools.SynchronizedPool<>(5);
    private TabMenuData[] menuDatas;
    private ColorStateList mItemIconTint;
    private ColorStateList mItemTextColor;
    private int mItemBackgroundRes;

    public NavigationTabSelectedListener getNavigationTabSelectedListener() {
        return navigationTabSelectedListener;
    }

    public void setNavigationTabSelectedListener(NavigationTabSelectedListener navigationTabSelectedListener) {
        this.navigationTabSelectedListener = navigationTabSelectedListener;
    }

    private NavigationTabSelectedListener navigationTabSelectedListener;

    public PsNavigationView(@NonNull Context context) {
        this(context, null);
    }

    public PsNavigationView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        final Resources res = getResources();
        mInactiveItemMaxWidth = res.getDimensionPixelSize(R.dimen.ps_bottom_navigation_item_max_width);
        mInactiveItemMinWidth = res.getDimensionPixelSize(R.dimen.ps_bottom_navigation_item_min_width);
        mActiveItemMaxWidth = res.getDimensionPixelSize(R.dimen.ps_bottom_navigation_item_max_width_active);
        mItemHeight = res.getDimensionPixelSize(R.dimen.ps_bottom_navigation_height);

        mSet = new AutoTransition();
        mSet.setOrdering(TransitionSet.ORDERING_TOGETHER);
        mSet.setDuration(ACTIVE_ANIMATION_DURATION_MS);
        mSet.setInterpolator(new FastOutSlowInInterpolator());
        mSet.addTransition(new TextScale());
//        mSet.addTransition(new Texts);

        mTempChildWidths = new int[5];

        mOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                final TabMenuView itemView = (TabMenuView) v;
                previousSelectedId = mSelectedItemPosition;
                mSelectedItemPosition = itemView.getItemPosition();
                if (!itemView.getTabMenuData().isChecked()) {
                    menuDatas[previousSelectedId].setChecked(false);
                    itemView.getTabMenuData().setChecked(true);
                    updateMenuView();
                    if (null != navigationTabSelectedListener) {
                        navigationTabSelectedListener.onTabSelected(itemView);
                    }
                } else {
                    if (null != navigationTabSelectedListener) {
                        navigationTabSelectedListener.onTabReSelected(itemView);
                    }
                }


            }
        };

        mItemTextColor = getResources().getColorStateList(R.color.ps_main_tab_text_color_sel);
        mItemBackgroundRes = R.drawable.ps_main_tab_bg_sel;
        setClipChildren(false);
    }


    public void setMenuDatas(TabMenuData[] menuDatas) {
        this.menuDatas = menuDatas;

    }

    public TabMenuData[] getMenuDatas() {
        return menuDatas;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int count = getChildCount();
        final int heightSpec = MeasureSpec.makeMeasureSpec(mItemHeight, MeasureSpec.EXACTLY);

        if (mShiftingMode) {
            final int inactiveCount = count - 1;
            final int activeMaxAvailable = width - inactiveCount * mInactiveItemMinWidth;
            final int activeWidth = Math.min(activeMaxAvailable, mActiveItemMaxWidth);
            final int inactiveMaxAvailable = (width - activeWidth) / inactiveCount;
            final int inactiveWidth = Math.min(inactiveMaxAvailable, mInactiveItemMaxWidth);
            int extra = width - activeWidth - inactiveWidth * inactiveCount;

            for (int i = 0; i < count; i++) {
                mTempChildWidths[i] = (i == mSelectedItemPosition) ? activeWidth : inactiveWidth;
                if (extra > 0) {
                    mTempChildWidths[i]++;
                    extra--;
                }
            }
        } else {

            final int maxAvailable = width / (count == 0 ? 1 : count);
            final int childWidth = Math.min(maxAvailable, mActiveItemMaxWidth);
            int extra = width - childWidth * count;
            for (int i = 0; i < count; i++) {
                mTempChildWidths[i] = childWidth;
                if (extra > 0) {
                    mTempChildWidths[i]++;
                    extra--;
                }
            }
        }

        int totalWidth = 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            child.measure(MeasureSpec.makeMeasureSpec(mTempChildWidths[i], MeasureSpec.EXACTLY), heightSpec);
            ViewGroup.LayoutParams params = child.getLayoutParams();
            params.width = child.getMeasuredWidth();
            totalWidth += child.getMeasuredWidth();
        }

        setMeasuredDimension(View.resolveSizeAndState(totalWidth, MeasureSpec.makeMeasureSpec(totalWidth, MeasureSpec.EXACTLY), 0),
                View.resolveSizeAndState(mItemHeight, heightSpec, 0));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();
        final int width = right - left;
        final int height = bottom - top;
        int used = 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == GONE) {
                continue;
            }
            if (ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_RTL) {
                child.layout(width - used - child.getMeasuredWidth(), 0, width - used, height);
            } else {
                child.layout(used, 0, child.getMeasuredWidth() + used, height);
            }
            used += child.getMeasuredWidth();
        }

    }

    public void buildMenuView() {
        removeAllViews();
        int size = menuDatas.length;
        if (mButtons != null) {
            for (TabMenuView item : mButtons) {
                mItemPool.release(item);
            }
        }
        if (size == 0) {
//            mSelectedItemId = 0;
            mSelectedItemPosition = 0;
            mButtons = null;
            return;
        }
        mButtons = new TabMenuView[size];

        for (int i = 0; i < size; i++) {
            TabMenuView child = getNewItem();
            mButtons[i] = child;
//            child.setIconTintList(mItemIconTint);
            child.setTextColor(mItemTextColor);
            child.setItemBackground(mItemBackgroundRes);
            child.setShiftingMode(mShiftingMode);
            child.init(menuDatas[i]);
            child.hideNotify();
            child.setItemPosition(i);
            child.setOnClickListener(mOnClickListener);
            addView(child);
        }
        mSelectedItemPosition = Math.min(size - 1, mSelectedItemPosition);
        menuDatas[mSelectedItemPosition].setChecked(true);
        updateMenuView();
    }

    int previousSelectedId = -1;

    public void updateMenuView() {
        final int menuSize = menuDatas.length;
        if (menuSize != mButtons.length) {
            // The size has changed. Rebuild menu view from scratch.
            buildMenuView();
            return;
        }
//        int previousSelectedId = mSelectedItemPosition;

        for (int i = 0; i < menuSize; i++) {
            TabMenuData item = menuDatas[i];
            if (item.isChecked()) {
//                mSelectedItemId = i;
                mSelectedItemPosition = i;
            }
        }
        for (int i = 0; i < menuSize; i++) {
            mButtons[i].init(menuDatas[i]);
        }
        if (previousSelectedId != mSelectedItemPosition) {
            // Note: this has to be called before BottomNavigationItemView#initialize().
            TransitionManager.beginDelayedTransition(this, mSet);
        }

        for (int i = 0; i < menuSize; i++) {
            mButtons[i].init(menuDatas[i]);
        }

    }


    public void setShiftingMode(boolean mShiftingMode) {
        this.mShiftingMode = mShiftingMode;
    }

    private TabMenuView getNewItem() {
        TabMenuView item = mItemPool.acquire();
        if (item == null) {
            item = new TabMenuView(getContext());
        }
        return item;
    }

    public void setIconTintList(ColorStateList tint) {
        mItemIconTint = tint;
        if (mButtons == null) return;
        for (TabMenuView item : mButtons) {
            item.setIconTintList(tint);
        }
    }

    /**
     * Returns the tint which is applied to menu items' icons.
     *
     * @return the ColorStateList that is used to tint menu items' icons
     */
    @Nullable
    public ColorStateList getIconTintList() {
        return mItemIconTint;
    }

    /**
     * Sets the text color to be used on menu items.
     *
     * @param color the ColorStateList used for menu items' text.
     */
    public void setItemTextColor(ColorStateList color) {
        mItemTextColor = color;
        if (mButtons == null) return;
        for (TabMenuView item : mButtons) {
            item.setTextColor(color);
        }
    }

    /**
     * Returns the text color used on menu items.
     *
     * @return the ColorStateList used for menu items' text
     */
    public ColorStateList getItemTextColor() {
        return mItemTextColor;
    }

    /**
     * Sets the resource ID to be used for item background.
     *
     * @param background the resource ID of the background
     */
    public void setItemBackgroundRes(int background) {
        mItemBackgroundRes = background;
        if (mButtons == null) return;
        for (TabMenuView item : mButtons) {
            item.setItemBackground(background);
        }
    }

    /**
     * Returns the resource ID for the background of the menu items.
     *
     * @return the resource ID for the background
     */
    public int getItemBackgroundRes() {
        return mItemBackgroundRes;
    }

    public void setSelectedItemPosition(int position) {
        if (position < getMenuDatas().length && position >= 0) {
            this.previousSelectedId = mSelectedItemPosition;
            this.mSelectedItemPosition = position;

            for (TabMenuData tabMenuData : getMenuDatas()) {
                tabMenuData.setChecked(false);
            }
            getMenuDatas()[position].setChecked(true);
            updateMenuView();
        }


    }

    public interface NavigationTabSelectedListener {
        void onTabSelected(TabMenuView tabMenuView);

        void onTabReSelected(TabMenuView tabMenuView);
    }

    public void showNotify(int position, String count) {
        if (null != mButtons && position < mButtons.length && position >= 0) {
            mButtons[position].showNotify(count);

        }
    }

    public void showNotify(int position, String count, int left, int top) {
        if (null != mButtons && position < mButtons.length && position >= 0) {
            mButtons[position].setNotifyPosition(left, top);
            mButtons[position].showNotify(count);

        }
    }
}
