package com.pysun.common.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.duia.ssx.pysun_common.R;


public class LoadingDialog extends AlertDialog {
    private Context context;
    private ImageView mimg;

    public LoadingDialog(Context context) {
        super(context, R.style.DialogFullWindow);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = LayoutInflater.from(context).inflate(R.layout.sch_loading_layout, null);
        setContentView(view);
        mimg = (ImageView) view.findViewById(R.id.sch_img_loading);
        Glide.with(mimg)
                .asGif()
                .load(R.raw.sch_data_transfering)

                .into(mimg);
    }

}
