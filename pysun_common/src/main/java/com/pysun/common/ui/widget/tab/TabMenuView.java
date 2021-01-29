package com.pysun.common.ui.widget.tab;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.duia.ssx.pysun_common.R;


/**
 * Created by ljh on 2018/1/12.
 */

public class TabMenuView extends FrameLayout {
    public static final int INVALID_ITEM_POSITION = -1;
    private int mDefaultMargin;
    private int mShiftAmount;
    private float mScaleUpFactor;
    private float mScaleDownFactor;

    private boolean mShiftingMode;

    private RelativeLayout lrTobIcon;
    private ImageView mIcon;
    private TextView mSmallLabel;
    private TextView mLargeLabel;
    private TextView mNotify;
    private TabMenuData tabMenuData;
    private ColorStateList mIconTint;
    private int mItemPosition = INVALID_ITEM_POSITION;
    private boolean isOnlyShowBig = true;

    public TabMenuView(Context context) {
        this(context, null);
    }

    public TabMenuView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public TabMenuView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final Resources res = getResources();
        int inactiveLabelSize =
                res.getDimensionPixelSize(R.dimen.ps_bottom_navigation_text_size);
        int activeLabelSize = res.getDimensionPixelSize(
                R.dimen.ps_bottom_navigation_text_size_active);
        mDefaultMargin = res.getDimensionPixelSize(R.dimen.ps_bottom_navigation_margin);
        mShiftAmount = inactiveLabelSize - activeLabelSize;
        mScaleUpFactor = 1f * activeLabelSize / inactiveLabelSize;
        mScaleDownFactor = 1f * inactiveLabelSize / activeLabelSize;

        LayoutInflater.from(context).inflate(R.layout.tab_bottom_navigation, this, true);
        mIcon = findViewById(R.id.icon);
        mNotify = mLargeLabel = findViewById(R.id.tab_notify);
        mSmallLabel = findViewById(R.id.smallLabel);
        mLargeLabel = findViewById(R.id.largeLabel);
        lrTobIcon = findViewById(R.id.rl_icon);
        if (isOnlyShowBig) {
            mSmallLabel.setVisibility(INVISIBLE);
            mLargeLabel.setVisibility(VISIBLE);
        }
        setClipChildren(false);
    }


    public void init(TabMenuData tabMenuData) {
        this.tabMenuData = tabMenuData;

        setIcon(tabMenuData.getIcon());
        setTitle(tabMenuData.getLabel());
        setChecked(tabMenuData.isChecked());

    }

    public TabMenuData getTabMenuData() {
        return tabMenuData;
    }

    public void setTitle(String title) {
        mSmallLabel.setText(title);
        mLargeLabel.setText(title);
    }


    public void setItemPosition(int position) {
        mItemPosition = position;
    }

    public int getItemPosition() {
        return mItemPosition;
    }

    public void setShiftingMode(boolean enabled) {
        mShiftingMode = enabled;
    }


    public void setChecked(boolean checked) {


        if (mShiftingMode) {
            if (checked) {
                LayoutParams iconParams = (LayoutParams) lrTobIcon.getLayoutParams();
                iconParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                iconParams.topMargin = mDefaultMargin;
                lrTobIcon.setLayoutParams(iconParams);
                mLargeLabel.setVisibility(VISIBLE);
                mLargeLabel.setScaleX(1f);
                mLargeLabel.setScaleY(1f);
                setSelected(true);
            } else {
                LayoutParams iconParams = (LayoutParams) lrTobIcon.getLayoutParams();
                iconParams.gravity = Gravity.CENTER;
                iconParams.topMargin = mDefaultMargin;
                lrTobIcon.setLayoutParams(iconParams);
                mLargeLabel.setVisibility(INVISIBLE);
                mLargeLabel.setScaleX(0.5f);
                mLargeLabel.setScaleY(0.5f);
                setSelected(false);
            }
            mSmallLabel.setVisibility(INVISIBLE);
        } else {
            if (checked) {
                LayoutParams iconParams = (LayoutParams) lrTobIcon.getLayoutParams();
                iconParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                iconParams.topMargin = mDefaultMargin + mShiftAmount;
                lrTobIcon.setLayoutParams(iconParams);

                if (isOnlyShowBig) {
                    mLargeLabel.setVisibility(VISIBLE);
                    mSmallLabel.setVisibility(INVISIBLE);

//                    mLargeLabel.setScaleX(1f);`
//                    mLargeLabel.setScaleY(1f);
//                    mSmallLabel.setScaleX(mScaleUpFactor);
//                    mSmallLabel.setScaleY(mScaleUpFactor);
                } else {
                    mLargeLabel.setVisibility(VISIBLE);
                    mSmallLabel.setVisibility(INVISIBLE);

                    mLargeLabel.setScaleX(1f);
                    mLargeLabel.setScaleY(1f);
                    mSmallLabel.setScaleX(mScaleUpFactor);
                    mSmallLabel.setScaleY(mScaleUpFactor);
                }

                setSelected(true);
            } else {
                LayoutParams iconParams = (LayoutParams) lrTobIcon.getLayoutParams();
                iconParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;
                iconParams.topMargin = mDefaultMargin;
                lrTobIcon.setLayoutParams(iconParams);
                if (isOnlyShowBig) {
                    mLargeLabel.setVisibility(VISIBLE);
                    mSmallLabel.setVisibility(INVISIBLE);

//                    mLargeLabel.setScaleX(mScaleDownFactor);
//                    mLargeLabel.setScaleY(mScaleDownFactor);
//                    mSmallLabel.setScaleX(1f);
//                    mSmallLabel.setScaleY(1f);
                } else {
                    mLargeLabel.setVisibility(INVISIBLE);
                    mSmallLabel.setVisibility(VISIBLE);

                    mLargeLabel.setScaleX(mScaleDownFactor);
                    mLargeLabel.setScaleY(mScaleDownFactor);
                    mSmallLabel.setScaleX(1f);
                    mSmallLabel.setScaleY(1f);
                }

                setSelected(false);
            }
        }
        refreshDrawableState();
    }

    public void setIcon(Drawable icon) {
        if (tabMenuData.isGif()) {
                Glide.with(mIcon).asGif().load(tabMenuData.getIconRes()).into(mIcon);

        } else {
            if (icon != null) {
                Drawable.ConstantState state = icon.getConstantState();
                icon = DrawableCompat.wrap(state == null ? icon : state.newDrawable()).mutate();
                DrawableCompat.setTintList(icon, mIconTint);
                mIcon.setImageDrawable(icon);

            }

        }
    }

    public void setIconTintList(ColorStateList tint) {
        mIconTint = tint;
        if (tabMenuData != null) {
            // Update the icon so that the tint takes effect
            setIcon(tabMenuData.getIcon());
        }
    }

    public void setTextColor(ColorStateList color) {
        mSmallLabel.setTextColor(color);
        mLargeLabel.setTextColor(color);
    }

    public void setItemBackground(int background) {
        Drawable backgroundDrawable = background == 0
                ? null : ContextCompat.getDrawable(getContext(), background);
        ViewCompat.setBackground(this, backgroundDrawable);
    }

    public int getTabId() {
        return tabMenuData.getTagId();
    }


    public void showNotify(String count) {

        mNotify.setText(count);
        mNotify.setVisibility(VISIBLE);
    }

    public void hideNotify() {
        mNotify.setText("");
        mNotify.setVisibility(GONE);
    }

    public void setNotifyPosition(int left, int top) {
        LayoutParams layoutParams = (LayoutParams) mNotify.getLayoutParams();
        layoutParams.setMargins(left, top, 0, 0);
    }
}
