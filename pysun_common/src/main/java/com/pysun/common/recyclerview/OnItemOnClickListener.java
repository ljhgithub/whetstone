package com.pysun.common.recyclerview;


import androidx.recyclerview.widget.RecyclerView;

public interface OnItemOnClickListener<T> {

    void onItem(RecyclerView.ViewHolder viewHolder, T t, int position);

    OnItemOnClickListener EMPTY  = new  OnItemOnClickListener<Object>(){

        @Override
        public void onItem(RecyclerView.ViewHolder viewHolder, Object o, int position) {

        }
    };
}
