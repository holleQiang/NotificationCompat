package com.zq.notificationcompat;

import android.app.Activity;
import android.app.AppOpsManager;
import android.app.NotificationChannel;
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

import com.zq.utils.StatusbarUtil;
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

        try {
//            Class<?> aClass = Class.forName("com.android.settings.Settings$NotificationFilterActivity");
            Class<?> aClass = Class.forName("com.zq.notificationcompat.MainActivity");
            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field declaredField : declaredFields) {
                String name = declaredField.getName();
                System.out.println("=================" + name);
            }
        } catch (ClassNotFoundException e) {
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

    @OnClick(R.id.bt_open_notification_settings)
    public void onViewClicked() {
        startActivityForResult(NotificationUtils.newOpenNotificationSettingsIntent(this),REQUEST_CODE_OPEN_NOTIFICATION_SETTINGS);
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
