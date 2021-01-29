package com.pysun.common.utils;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.core.app.NotificationManagerCompat;

import java.util.List;

public class SysUtils {

    /**
     * 获取版本号
     */
    public static int getAppVersionCode(Context context) {
        int res = 0;
        PackageManager manager;
        PackageInfo info = null;
        manager = context.getPackageManager();
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
            res = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return res;
    }

    /**
     * 获取版本号
     */
    public static String getAppVersionName(Context context) {
        String res = "";
        PackageManager manager;
        PackageInfo info = null;
        manager = context.getPackageManager();
        try {
            info = manager.getPackageInfo(context.getPackageName(), 0);
            res = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return res;
    }

    /**
     * 获取系统的版本
     *
     * @return
     */
    public static int getAndroidSDKVersion() {
        int version = 0;
        try {
            version = Integer.valueOf(android.os.Build.VERSION.SDK);
        } catch (NumberFormatException e) {
        }
        return version;
    }

    /**
     * 判断当前应用是否启动
     *
     * @param context
     * @return
     */
    public static boolean getCurrentTask(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //获取当前所有存活task的信息
        List<ActivityManager.RunningTaskInfo> appProcessInfos = activityManager.getRunningTasks(Integer.MAX_VALUE);
        //遍历，若task的name与当前task的name相同，则返回true，否则，返回false
        for (ActivityManager.RunningTaskInfo process : appProcessInfos) {
            if (process.baseActivity.getPackageName().equals(context.getPackageName())
                    || process.topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 返回app运行状态
     *
     * @param context 一个context
     * @return int 1:前台 2:后台 0:不存在
     */
    public static int isAppAlive(Context context) {
        String packageName = getCurrentProcessName(context);
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningTaskInfo> listInfos = activityManager.getRunningTasks(20);
        // 判断程序是否在栈顶
        if (listInfos.get(0).topActivity.getPackageName().equals(packageName)) {
            return 1;
        } else {
            // 判断程序是否在栈里
            for (ActivityManager.RunningTaskInfo info : listInfos) {
                if (info.topActivity.getPackageName().equals(packageName)) {
                    return 2;
                }
            }
            return 0;// 栈里找不到，返回0
        }
    }


    public static String getCurrentProcessName(Context context) {
        int pid = android.os.Process.myPid();
        String processName = "";
        ActivityManager manager = (ActivityManager) context.getSystemService
                (Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo process : manager.getRunningAppProcesses()) {
            if (process.pid == pid) {
                processName = process.processName;
            }
        }
        return processName;
    }


    public static boolean checkNotifySetting(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return NotificationManagerCompat.from(context).getImportance() != NotificationManager.IMPORTANCE_NONE;
        }
        return NotificationManagerCompat.from(context).areNotificationsEnabled();

    }

    public static void openNotify(Context context){

        try {
            Intent localIntent = new Intent();
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //直接跳转到应用通知设置的代码：
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                localIntent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
                localIntent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
                context.startActivity(localIntent);
                return;
            }
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
                localIntent.putExtra("app_package", context.getPackageName());
                localIntent.putExtra("app_uid", context.getApplicationInfo().uid);
                context.startActivity(localIntent);
                return;
            }
            if (android.os.Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
                localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                localIntent.addCategory(Intent.CATEGORY_DEFAULT);
                localIntent.setData(Uri.parse("package:" + context.getPackageName()));
                context.startActivity(localIntent);
                return;
            }

            //4.4以下没有从app跳转到应用通知设置页面的Action，可考虑跳转到应用详情页面,

            if (Build.VERSION.SDK_INT >= 9) {
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
                context.startActivity(localIntent);
                return;
            }

            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(" cxx   pushPermission 有问题");
        }
    }
}
