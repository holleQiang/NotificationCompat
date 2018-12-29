package com.zq.notificationcompat;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.zq.utils.StringUtil;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_OPEN_NOTIFICATION_SETTINGS = 1000;
    @BindView(R.id.tv_notification_open_status)
    TextView tvNotificationOpenStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        refreshOpenStatus();
    }

    @OnClick(R.id.bt_open_notification_settings)
    public void onViewClicked() {
        openNotificationSettings(this, REQUEST_CODE_OPEN_NOTIFICATION_SETTINGS);
    }

    public void refreshOpenStatus() {
        tvNotificationOpenStatus.setText(StringUtil.format(this, R.string.notification_open_status, isNotificationEnable(this)));
    }

    public static boolean isNotificationEnable(Context context) {
        return NotificationManagerCompat.from(context).areNotificationsEnabled();
    }

    public static void openNotificationSettings(Activity activity, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            //这种方案适用于 API 26, 即8.0（含8.0）以上可以用
            intent.putExtra(Settings.EXTRA_APP_PACKAGE, activity.getPackageName());
            //用于给指定channel设置高亮
//                intent.putExtra(Settings.EXTRA_CHANNEL_ID, channelId);
            activity.startActivityForResult(intent, requestCode);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //这种方案适用于 API21——25，即 5.0——7.1 之间的版本可以使用
            Intent intent = new Intent();
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", activity.getPackageName());
            intent.putExtra("app_uid", activity.getApplicationInfo().uid);
            activity.startActivityForResult(intent, requestCode);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //4.4-5.0跳转到app详情页
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + activity.getPackageName()));
            activity.startActivityForResult(intent, requestCode);
        } else {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            activity.startActivityForResult(intent, requestCode);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN_NOTIFICATION_SETTINGS) {
            refreshOpenStatus();
        }
    }
}
