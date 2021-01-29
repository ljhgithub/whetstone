package com.pysun.common.recyclerview;

public class GroupInfo {

    //组号
    private int mGroupID;
    // Header 的 title
    private String mTitle;
    //ItemView 在组内的位置
    private int position=-1;
    // 组的成员个数
    private int mGroupLength;
    public GroupInfo(int groupId, String title) {
        this.mGroupID = groupId;
        this.mTitle = title;
    }


    public boolean isFirstViewInGroup () {
        return position == 0;
    }

    public boolean isLastViewInGroup () {
        return position == mGroupLength - 1 && position >= 0;
    }



    public void setGroupLength(int groupLength) {
        this.mGroupLength = groupLength;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }
}
