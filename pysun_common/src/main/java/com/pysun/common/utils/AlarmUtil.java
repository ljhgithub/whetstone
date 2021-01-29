package com.pysun.common.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class AlarmUtil {

    public final static String LOCAL_NOTIFY_EXTRA = "local_notify_extra";

    public static void pushLocalNotify(Context context, String className, String extra, int requestCode, long triggerAtMillis) {

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent("com.duia.ssx.lib_common.local.notify");
        intent.addCategory(context.getPackageName());
        ComponentName componentName=new ComponentName(context.getApplicationContext(),className);
//        intent.setFlags(0x01000000);
        intent.setComponent(componentName);
        intent.putExtra(LOCAL_NOTIFY_EXTRA, extra);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context.getApplicationContext(),
                requestCode, intent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, triggerAtMillis, pendingIntent);
        }

    }
}
