package com.pysun.common.permission;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.pysun.common.utils.LogUtils;

import java.util.ArrayList;

public class PermissionUtil {


    public static String[] getDeniedPermissions(Context context, String[] permissions) {

//        String[] deniedPermissions =new String[]{};
//        ArrayList<String> grantedPermissions = new ArrayList<>();
        ArrayList<String> deniedPermissions = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {

            LogUtils.LOGD("Rationale", ActivityCompat.checkSelfPermission(context, permissions[i])+"");
            if (PackageManager.PERMISSION_DENIED == ActivityCompat.checkSelfPermission(context, permissions[i])) {
                deniedPermissions.add(permissions[i]);

            }
//            else {
////                grantedPermissions.add(permissions[i]);
////            }


        }
        String[] denied = new String[deniedPermissions.size()];
        deniedPermissions.toArray(denied);
        return denied;

    }


    public static boolean shouldShowRequestPermissionRationale(Activity activity, String[] permissions) {

        LogUtils.LOGD("Rationale",permissions.length+" len");
        for (int i = 0; i < permissions.length; i++) {
            LogUtils.LOGD("Rationale",ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i])+"");
            if ( ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i])) {


//                return true;

            }

        }

        return false;

    }

}
