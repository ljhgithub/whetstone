package com.pysun.common.ui.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.duia.ssx.pysun_common.R;
import com.pysun.common.utils.DrawableShapeUtils;
import com.pysun.common.utils.ScreenUtil;

public class CommonDialog extends BaseDialogFragment implements View.OnClickListener {

    private final static String ARGS_TITLE = "args_title";
    private final static String ARGS_CONTENT = "args_content";
    private final static String ARGS_POSITIVE = "args_positive";
    private final static String ARGS_NEGATIVE = "args_negative";
    private final static String ARGS_TYPE = "args_type";
    private View.OnClickListener positiveListener, negativeListener;

    private DialogInterface.OnDismissListener onDismissListener;
    private TextView tvTitle;
    private TextView tvContent;
    private Button btnPositive;
    private Button btnNegative;
    private String mTitle, mContent, mPositive, mNegative;

    public static CommonDialog newInstanceTwo(String title, String content) {
        CommonDialog instance = new CommonDialog();
        Bundle args = new Bundle();
        args.putString(ARGS_TITLE, title);
        args.putString(ARGS_CONTENT, content);

        instance.setArguments(args);
        return instance;
    }


    public static CommonDialog newInstanceTwo(String title, String content, String positive, String negative) {
        CommonDialog instance = new CommonDialog();
        Bundle args = new Bundle();
        args.putString(ARGS_TITLE, title);
        args.putString(ARGS_CONTENT, content);
        args.putString(ARGS_POSITIVE, positive);
        args.putString(ARGS_NEGATIVE, negative);
        instance.setArguments(args);
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parseArg();
        return inflater.inflate(R.layout.common_layout_dailog_two, container, true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View cl = view.findViewById(R.id.comm_dialog_cl);
        ViewGroup.LayoutParams lp = cl.getLayoutParams();
        lp.width = ScreenUtil.getDialogWidth(0.8f);
        cl.setLayoutParams(lp);
        tvTitle = (TextView) view.findViewById(R.id.comm_dialog_title);
        tvContent = (TextView) view.findViewById(R.id.comm_dialog_content);
        btnPositive = (Button) view.findViewById(R.id.comm_dialog_positive);
        btnNegative = (Button) view.findViewById(R.id.comm_dialog_negative);
        float radius = ScreenUtil.dip2px(6);
        float[] leftRadii = new float[]{.0f, .0f,
                .0f, .0f,
                radius, radius,
                .0f, .0f
        };

        float[] rightRadii = new float[]{.0f, .0f,
                .0f, .0f,
                .0f, .0f,
                radius, radius,
        };
        int pressedColor = ContextCompat.getColor(getContext(), R.color.comm_gray_e8);
        int normalColor = ContextCompat.getColor(getContext(), R.color.comm_white);
        Drawable leftDrawablePressed = DrawableShapeUtils.getDrawable(pressedColor, pressedColor, 0, leftRadii);
        Drawable leftDrawableNormal = DrawableShapeUtils.getDrawable(normalColor, normalColor, 0, leftRadii);

        Drawable rightDrawablePressed = DrawableShapeUtils.getDrawable(pressedColor, pressedColor, 0, rightRadii);
        Drawable rightDrawableNormal = DrawableShapeUtils.getDrawable(normalColor, normalColor, 0, rightRadii);
        Drawable leftDrawable = DrawableShapeUtils.generatePressedSelector(leftDrawablePressed, leftDrawableNormal);
        Drawable rightDrawable = DrawableShapeUtils.generatePressedSelector(rightDrawablePressed, rightDrawableNormal);
        btnPositive.setBackground(leftDrawable);
        btnNegative.setBackground(rightDrawable);

        btnNegative.setOnClickListener(this);
        btnPositive.setOnClickListener(this);
        if (TextUtils.isEmpty(mTitle)){
            tvTitle.setVisibility(View.GONE);
        }else {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(mTitle);
        }

        tvContent.setText(mContent);
        btnPositive.setText(mPositive);
        btnNegative.setText(mNegative);
    }

    private void parseArg() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mTitle = bundle.getString(ARGS_TITLE, "");
            mContent = bundle.getString(ARGS_CONTENT, "");
            mPositive = bundle.getString(ARGS_POSITIVE, getString(R.string.comm_confirm));
            mNegative = bundle.getString(ARGS_NEGATIVE, getString(R.string.comm_cancel));
        } else {
            mTitle = "";
            mContent = "";
            mPositive = getString(R.string.comm_confirm);
            mNegative = getString(R.string.comm_cancel);
        }

    }

    public void setPositiveListener(View.OnClickListener positiveListener) {
        this.positiveListener = positiveListener;
    }

    public void setNegativeListener(View.OnClickListener negativeListener) {
        this.negativeListener = negativeListener;
    }


    public void setOnDismissListener(DialogInterface.OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }


    public void setContent(@NonNull String text) {
        if (tvContent != null) {
            tvContent.setText(text);
        }
    }

    @Override
    public void onClick(View view) {
        if (R.id.comm_dialog_positive == view.getId()) {
            if (null != positiveListener) {
                positiveListener.onClick(view);
            }

            dismiss();

        } else if (R.id.comm_dialog_negative == view.getId()) {
            if (null != negativeListener) {
                negativeListener.onClick(view);
            }

            dismiss();

        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        return new Dialog(getContext(), R.style.CommonDialogNullBg);

    }

}
