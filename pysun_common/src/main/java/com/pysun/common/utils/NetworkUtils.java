package com.pysun.common.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.duia.ssx.pysun_common.R;

public class NetworkUtils {

    /**
     * 判断是否具有网络连接
     *
     * @return
     */
    public static final boolean hasNetWorkConection(Context ctx) {
        // 获取连接活动管理器
        final ConnectivityManager connectivityManager = (ConnectivityManager) ctx
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取连接的网络信息
        final NetworkInfo networkInfo = connectivityManager
                .getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isAvailable());
    }



    public static void hasNetWorkConectionTip(Context ctx) {
        if (!hasNetWorkConection(ctx)) {
            Toast.makeText(ctx, R.string.comm_error_net, Toast.LENGTH_SHORT).show();
        }
    }
}
