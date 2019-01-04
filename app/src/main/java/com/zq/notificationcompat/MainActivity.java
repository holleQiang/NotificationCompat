package com.zq.notificationcompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.zq.notificationcompat.utils.NotificationUtils;
import com.zq.utils.StringUtil;

import java.lang.reflect.Field;
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

        try {
            Context mmsCtx = createPackageContext("com.android.settings",
                    Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            PackageInfo packageInfo = mmsCtx.getPackageManager().getPackageInfo(mmsCtx.getPackageName(), PackageManager.GET_ACTIVITIES);
            ActivityInfo[] activities = packageInfo.activities;
            for (ActivityInfo activity : activities) {
                if ("com.android.settings.Settings$AppNotificationSettingsActivity".equals(activity.targetActivity)) {
                    System.out.println("^^^^^^^^^^^^^^^^" + activity.targetActivity);
                }
            }
            Class<?> aClass = Class.forName("com.android.settings.Settings$NotificationFilterActivity",true,mmsCtx.getClassLoader());
//            Class<?> aClass = Class.forName("com.zq.notificationcompat.MainActivity");
            Intent intent = new Intent(mmsCtx, aClass);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mmsCtx.startActivity(intent);
            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                String name = declaredField.getName();
                System.out.println("=================" + name);
            }
            Method[] methods = aClass.getDeclaredMethods();
            for (Method method : methods) {
                String name = method.getName();
                System.out.println("********************" + name);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {

            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager == null) {
                return;
            }
            NotificationChannel channel = new NotificationChannel(getResources().getString(R.string.notification_channel_id_default),
                    getResources().getString(R.string.notification_channel_name_default),
                    NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @OnClick({R.id.bt_open_notification_settings,R.id.bt_send_notification})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bt_send_notification:
                Notification notification = new NotificationCompat.Builder(this, getResources().getString(R.string.notification_channel_id_default))
                        .setContentTitle("通知标题")
                        .setContentInfo("通知内容")
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setSmallIcon(R.drawable.ic_nitification_small)
                        .build();
                NotificationManagerCompat.from(this).notify(0,notification);
                break;
            case R.id.bt_open_notification_settings:
                NotificationUtils.openNotificationSettings(this.getApplicationContext());
                break;
        }
    }

    public void refreshOpenStatus() {
        tvNotificationOpenStatus.setText(StringUtil.format(this, R.string.notification_open_status, NotificationUtils.isNotificationEnable(this)));
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_OPEN_NOTIFICATION_SETTINGS) {
            refreshOpenStatus();
        }
    }
}
