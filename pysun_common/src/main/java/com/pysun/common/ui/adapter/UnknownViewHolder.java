package com.pysun.common.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;

import androidx.recyclerview.widget.RecyclerView;

import com.duia.ssx.pysun_common.R;

public class UnknownViewHolder extends RecyclerView.ViewHolder {
    public UnknownViewHolder(Context context) {
        super(LayoutInflater.from(context).inflate(R.layout.unknown_item,null,false));
    }
}
