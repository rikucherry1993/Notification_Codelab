package com.rikucherry.notifyme;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

public class MainActivity extends AppCompatActivity {

    private Button buttonNotify;
    private Button buttonUpdate;
    private Button buttonCancel;

    // Both channel and notification need id
    private static final String PRIMARY_CHANNEL_ID = "primary_notification_channel";
    private static final int NOTIFICATION_ID = 1234;

    // note: 安卓系统靠Notification manager向用户传递通知
    private NotificationManager mNotifyManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonNotify = findViewById(R.id.notify);
        buttonUpdate = findViewById(R.id.update);
        buttonCancel = findViewById(R.id.cancel);

        setNotificationButtonState(true,false,false);

        createNotificationChannel();
        buttonNotify.setOnClickListener(view -> {
            sendNotification();
            setNotificationButtonState(false,true,true);
        });

        buttonUpdate.setOnClickListener(view -> {
            updateNotification();
            setNotificationButtonState(false,false,true);
        });
        buttonCancel.setOnClickListener(view -> {
            cancelNotification();
            setNotificationButtonState(true,false,false);
        });

    }

    public void createNotificationChannel() {
        mNotifyManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // O以上必须
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            // create a Noc channel
            NotificationChannel channelPrimary = new NotificationChannel(PRIMARY_CHANNEL_ID,
                    "Mascot Notification",NotificationManager.IMPORTANCE_HIGH);
            // configuration
            channelPrimary.enableLights(true);
            channelPrimary.setLightColor(Color.RED);
            channelPrimary.enableVibration(true);
            channelPrimary.setDescription("Notification from Mascot");
            mNotifyManager.createNotificationChannel(channelPrimary);
        }
    }

    private NotificationCompat.Builder  getNotificationBuilder(){

        Intent notificationIntent = new Intent(this,MainActivity.class);
        // note：通知的content intent必须被pending intent封装
        // Flag indicating that if the described PendingIntent already exists,
        // then keep it but replace its extra data with what is in this new Intent。
        PendingIntent notificationPendingIntent = PendingIntent.getActivity(this,
                NOTIFICATION_ID,notificationIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, PRIMARY_CHANNEL_ID)
                .setContentTitle("收到一则提醒！")
                .setContentText("开饭了！")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true); // 用户点击后自动关闭

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
                builder.setPriority(NotificationCompat.PRIORITY_HIGH) // for API < 28
                .setDefaults(NotificationCompat.DEFAULT_ALL);}

        return builder;
    }

    public void sendNotification() {
        Notification customisedNotification = getNotificationBuilder().build();
        mNotifyManager.notify(NOTIFICATION_ID,customisedNotification);
    }

    private void updateNotification() {
        Bitmap notificationImage = BitmapFactory.decodeResource(getResources(), R.drawable.mascot_1);
        Notification updatedNotification = getNotificationBuilder()
                .setStyle(new NotificationCompat.BigPictureStyle()
                .bigPicture(notificationImage)
                .setBigContentTitle("已更新您的通知！"))
                .build();

        mNotifyManager.notify(NOTIFICATION_ID,updatedNotification);
    }

    private void cancelNotification() {
        mNotifyManager.cancel(NOTIFICATION_ID);
    }

    private void setNotificationButtonState (Boolean isNotifyEnabled,
                                             Boolean isUpdateEnabled,
                                             Boolean isCancelEnabled) {
        buttonNotify.setEnabled(isNotifyEnabled);
        buttonUpdate.setEnabled(isUpdateEnabled);
        buttonCancel.setEnabled(isCancelEnabled);
    }

}