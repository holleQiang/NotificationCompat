package com.zq.notificationcompat.utils;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationManagerCompat;

/**
 * 通知工具类
 * Author：zhangqiang
 * Date：2018/12/29 16:33:39
 * Email:852286406@qq.com
 * Github:https://github.com/holleQiang
 */
public class NotificationUtils {

    /**
     * 特殊的机型，直接跳到应用详情页面
     */
    private static final String[] extraModels = new String[]{"MI 6", "MI 6X", "Redmi Note 5"};

    /**
     * 判断通知是否打开
     *
     * @param context context
     */
    public static boolean isNotificationEnable(@NonNull Context context) {
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

    /**
     * 打开app详情页
     *
     * @param context context
     * @return boolean
     */
    private static boolean openAppDetail(Context context) {

        try {
            Intent intent = newOpenAppDetailIntent(context);
            setIntentFlags(context,intent);
            context.startActivity(intent);
        } catch (Throwable e) {
            try {
                Intent intent = newOpenSettingsIntent();
                setIntentFlags(context,intent);
                context.startActivity(intent);
            } catch (Throwable throwable) {
                return false;
            }
        }
        return true;
    }

    /**
     * 打开app详情页
     *
     * @param context context
     * @return boolean
     */
    private static boolean openAppDetailCompat(Context context) {

        if(isOPPO()){
            try {
                openOppoAppDetail(context);
                return true;
            }catch (Throwable throwable){
               return openAppDetail(context);
            }
        }else {
           return openAppDetail(context);
        }
    }

    /**
     * 打开通知设置页面
     * @param context context
     * @return boolean
     */
    public static boolean openNotificationSettings(@NonNull Context context) {

        if (isExtraModel()) {
            return openAppDetailCompat(context);
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            try {
                Intent intent = newOpenNotificationSettingsIntent(context);
                setIntentFlags(context,intent);
                context.startActivity(intent);
            } catch (Throwable throwable) {
                return openAppDetailCompat(context);
            }
            return true;
        } else {
            return openAppDetailCompat(context);
        }
    }

    /**
     * 创建打开通知设置页面的Intent
     *
     * @param context context
     */
    @NonNull
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private static Intent newOpenNotificationSettingsIntent(@NonNull Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

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
        }
        throw new RuntimeException("cannot use with sdk version smaller than LOLLIPOP");
    }

    /**
     * 跳转到应用详情的intent
     *
     * @param context context
     * @return Intent
     */
    private static Intent newOpenAppDetailIntent(@NonNull Context context) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        return intent;
    }

    /**
     * 跳转到设置页面的intent
     *
     * @return Intent
     */
    private static Intent newOpenSettingsIntent() {
        return new Intent(Settings.ACTION_SETTINGS);
    }

    /**
     * 打开oppo通知设置页面
     *
     * @param context 上下文
     */
    private static void openOppoAppDetail(Context context) {
        Intent intent = new Intent();
        ComponentName componentName = new ComponentName("com.oppo.notification.center",
                "com.oppo.notification.center.AppDetailActivity");
        intent.setComponent(componentName);
        intent.setAction("com.oppo.notification.center.app.detail");
        intent.setFlags(0x10800000);
        intent.putExtra("pkg_name", context.getPackageName());
        intent.putExtra("app_name", "趣头条");
        setIntentFlags(context,intent);
        context.startActivity(intent);
    }

    private static boolean isOPPO() {
        return "OPPO".equalsIgnoreCase(Build.BRAND);
    }

    /**
     * 是否是特殊的品牌
     *
     * @return boolean
     */
    private static boolean isExtraModel() {

        String model = Build.MODEL;
        for (String extraModel : extraModels) {
            if (extraModel.equalsIgnoreCase(model)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断context类型设置flag new task
     * 如果是非activity启动的，加上 FLAG_ACTIVITY_NEW_TASK，否则不加，让页面与app在一个栈内
     * @param context context
     * @param intent intent
     */
    private static void setIntentFlags(Context context,Intent intent){
        if (!(context instanceof Activity)) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
    }
}
