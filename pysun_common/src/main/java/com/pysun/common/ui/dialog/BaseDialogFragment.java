package com.pysun.common.ui.dialog;


import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

public class BaseDialogFragment extends DialogFragment {


    @Override
    public void show(FragmentManager manager, String tag) {

        try {
            super.show(manager, tag);
        } catch (IllegalStateException  e) {

        }

    }


}
