package com.pysun.common.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.duia.ssx.pysun_common.R;
import com.pysun.common.utils.ScreenUtil;

public class CommOneImageDialog extends BaseDialogFragment implements View.OnClickListener {

    private final static String ARGS_ONE_IMG_ID = "args_one_image_id";
    private final static String ARGS_ONE_IMG_URL = "args_one_image_url";

    private View.OnClickListener closeListener;

    private DialogInterface.OnDismissListener onDismissListener;
    private ImageView ivOneImg;
    private ImageView ivOneImgClose;
    private String url;
    private int resId;

    public static CommOneImageDialog newInstance(int resId) {
        CommOneImageDialog instance = new CommOneImageDialog();
        Bundle args = new Bundle();
        args.putInt(ARGS_ONE_IMG_ID, resId);
        args.putString(ARGS_ONE_IMG_URL, "");
        instance.setArguments(args);
        return instance;
    }

    public static CommOneImageDialog newInstance(String url) {
        CommOneImageDialog instance = new CommOneImageDialog();
        Bundle args = new Bundle();
        args.putInt(ARGS_ONE_IMG_ID, -1);
        args.putString(ARGS_ONE_IMG_URL, url);
        instance.setArguments(args);
        return instance;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parseArg();
        return inflater.inflate(R.layout.comm_one_image_dialog, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View cl = view.findViewById(R.id.comm_one_img_content);
        ViewGroup.LayoutParams lp = cl.getLayoutParams();
        lp.width = ScreenUtil.getDialogWidth(1.f);
        cl.setLayoutParams(lp);

        ivOneImg = view.findViewById(R.id.comm_one_img);
        ivOneImgClose = view.findViewById(R.id.comm_one_img_close);
        ivOneImgClose.setOnClickListener(this);

        if (resId != -1 && resId != 0) {

            ivOneImg.setImageResource(resId);
        } else if (!TextUtils.isEmpty(url)) {

            Glide.with(ivOneImg).load(url).into(ivOneImg);
        }


    }

    private void parseArg() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            url = bundle.getString(ARGS_ONE_IMG_URL, "");
            resId = bundle.getInt(ARGS_ONE_IMG_ID, -1);
        } else {
            url = "";
            resId = -1;

        }

    }


    public void setCloseListener(View.OnClickListener closeListener) {
        this.closeListener = closeListener;
    }

    @Override
    public void onClick(View view) {
        if (R.id.comm_one_img_close == view.getId()) {
            if (null != closeListener) {
                closeListener.onClick(view);
            }

            dismiss();

        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new Dialog(getContext(), R.style.CommonDialogNullBg);

    }

}
