package com.zq.notificationcompat;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationManagerCompat;

/**
 * 通知工具类
 * Author：zhangqiang
 * Date：2018/12/29 16:33:39
 * Email:852286406@qq.com
 * Github:https://github.com/holleQiang
 */
@SuppressWarnings("unused")
public class NotificationUtils {

    /**
     * 判断通知是否打开
     *
     * @param context context
     */
    public static boolean isNotificationEnable(@NonNull Context context) {
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

    /**
     * 创建打开通知设置页面的Intent
     *
     * @param context context
     */
    @NonNull
    public static Intent newOpenNotificationSettingsIntent(@NonNull Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            if("MI 6".equalsIgnoreCase(Build.MODEL)){
                //小米6 单独跳应用详情页面
                return newOpenAppDetailIntent(context);
            }

            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            //这种方案适用于 API 26, 即8.0（含8.0）以上可以用
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
            //用于给指定channel设置高亮
//                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channelId);
            return intent;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //这种方案适用于 API21——25，即 5.0——7.1 之间的版本可以使用
            Intent intent = new Intent();
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
            return intent;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //4.4-5.0跳转到app详情页
            return newOpenAppDetailIntent(context);
        } else {
            return new Intent(Settings.ACTION_SETTINGS);
        }
    }

    /**
     * 跳转到应用详情的intent
     * @param context context
     * @return Intent
     */
    public static Intent newOpenAppDetailIntent(@NonNull Context context){
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        return intent;
    }
}
