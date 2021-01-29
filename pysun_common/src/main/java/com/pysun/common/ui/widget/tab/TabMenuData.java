package com.pysun.common.ui.widget.tab;

import android.graphics.drawable.Drawable;

/**
 * Created by ljh on 2018/1/14.
 */

public class TabMenuData {

    private int tagId;
    private Drawable icon;
    private int iconRes;
    private String label;
    private int notifyCount;
    private boolean isChecked = false;

    private boolean isGif;

    public TabMenuData(Drawable icon, String label) {
        this.icon = icon;
        this.label = label;
    }
    public TabMenuData(int iconRes, String label,boolean isGif) {
        this.iconRes = iconRes;
        this.label = label;
        this.isGif = isGif;
    }

    public boolean isGif() {
        return isGif;
    }

    public int getIconRes() {
        return iconRes;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getNotifyCount() {
        return notifyCount;
    }

    public void setNotifyCount(int notifyCount) {
        this.notifyCount = notifyCount;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }
}
