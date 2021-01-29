package com.pysun.common.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.pysun.common.utils.LogUtils;
import com.google.gson.GsonBuilder;

import io.reactivex.subjects.BehaviorSubject;

public class PermissionActivity extends Activity {


    private final static int REQUEST_PERMISSION = 1;
    private static final String EXTRA_PERMISSION = "extra_permission";

    private String[] mPermissions;
    private String[] deniedPermissions;
    private static BehaviorSubject<Boolean> behaviorSubject = BehaviorSubject.create();

    public static void start(Context context, String[] permissions,BehaviorSubject<Boolean> booleanBehaviorSubject) {
        if (permissions == null || permissions.length == 0) {

            return;
        }

        behaviorSubject=booleanBehaviorSubject==null?behaviorSubject:booleanBehaviorSubject;
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Bundle bundle = new Bundle();
        bundle.putStringArray(EXTRA_PERMISSION, permissions);
        intent.putExtras(bundle);
        context.startActivity(intent);
        if (context instanceof Activity) {
            ((Activity) context).overridePendingTransition(0, 0);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) return;
        this.mPermissions = getIntent().getStringArrayExtra(EXTRA_PERMISSION);

        deniedPermissions = PermissionUtil.getDeniedPermissions(this, mPermissions);
        LogUtils.LOGD("deniedPermissions", deniedPermissions.length + "");
        if (deniedPermissions.length == 0) {

            behaviorSubject.onNext(true);
            behaviorSubject.onComplete();
            finish();
        } else {

            // Permission is not granted
//
//            if (PermissionUtil.shouldShowRequestPermissionRationale(this,deniedPermissions)){
//                // 可以提示下用户，dialog 或Snackbar
//                ActivityCompat.requestPermissions(this, deniedPermissions,
//                        REQUEST_PERMISSION);
//            }else {
//
//                behaviorSubject.onNext(false);
//                behaviorSubject.onComplete();
////                finish();
//                ActivityCompat.requestPermissions(this, deniedPermissions,
//                        REQUEST_PERMISSION);
//            }

            ActivityCompat.requestPermissions(this, deniedPermissions,
                    REQUEST_PERMISSION);
        }

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (REQUEST_PERMISSION == requestCode) {
            boolean grant = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    grant = false;
                }
            }

            LogUtils.LOGD("onRequestPermissionsResult", new GsonBuilder().create().toJson(permissions) + "  " + new GsonBuilder().create().toJson(grantResults));
            behaviorSubject.onNext(grant);
            behaviorSubject.onComplete();

//           behaviorSubject.onNext(Arrays.equals(grantResults,deniedPermissions));
        } else {
            behaviorSubject.onNext(false);
            behaviorSubject.onComplete();
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }
}
