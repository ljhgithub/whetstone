package com.pysun.common.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.duia.ssx.pysun_common.R;
import com.pysun.common.utils.KScreenUtil;

import java.util.ArrayList;

public class CommCustomDialog extends BaseDialogFragment {

    private final static String ARGS_STYLE_ID = "args_style_id";
    private final static String ARGS_RES_IDS = "args_res_ids";
    private final static String ARGS_LAYOUT_ID = "args_layout_id";

    private View.OnClickListener customListener;

    private DialogInterface.OnDismissListener onDismissListener;

    private int layoutId,styleId=R.style.CommonDialogNullBg;
   private ArrayList<Integer> resIds;
    public static CommCustomDialog newInstance(int layoutId,ArrayList<Integer> resIds) {
        CommCustomDialog instance = new CommCustomDialog();
        Bundle args = new Bundle();
        args.putIntegerArrayList(ARGS_RES_IDS, resIds);
        args.putInt(ARGS_LAYOUT_ID, layoutId);
        instance.setArguments(args);
        return instance;
    }


    public static CommCustomDialog newInstance(int layoutId,ArrayList<Integer> resIds,int styleId) {
        CommCustomDialog instance = new CommCustomDialog();
        Bundle args = new Bundle();
        args.putIntegerArrayList(ARGS_RES_IDS, resIds);
        args.putInt(ARGS_LAYOUT_ID, layoutId);
        args.putInt(ARGS_STYLE_ID, styleId);
        instance.setArguments(args);
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parseArg();
        return inflater.inflate(layoutId, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View cl = view.findViewById(R.id.comm_dialog_cl);
        ViewGroup.LayoutParams lp = cl.getLayoutParams();
        lp.width = (int) KScreenUtil.INSTANCE.getDialogWidth(0.85f);
        cl.setLayoutParams(lp);

        for (int id:resIds){
            view.findViewById(id).setOnClickListener(customListener);
        }




    }

    private void parseArg() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            styleId = bundle.getInt(ARGS_STYLE_ID,R.style.CommonDialogNullBg);
            resIds = bundle.getIntegerArrayList(ARGS_RES_IDS);
            layoutId = bundle.getInt(ARGS_LAYOUT_ID, 0);
        } else {

        }

    }


    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    public void setCustomListener(View.OnClickListener customListener) {
        this.customListener = customListener;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new Dialog(getContext(), styleId);

    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (null != onDismissListener) {
            onDismissListener.onDismiss(dialog);
        }
    }

}
