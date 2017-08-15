package com.example.breezil.chatty.Activity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;

import com.example.breezil.chatty.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


/**
 * Created by breezil on 8/5/2017.
 */

public class FireMessagingServices  extends FirebaseMessagingService{
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String notificationTitle = remoteMessage.getNotification().getTitle();
        String notifcationMessage = remoteMessage.getNotification().getBody();

        String click_action = remoteMessage.getNotification().getClickAction();

        String from_user_id = remoteMessage.getData().get("from_user_id");



        android.support.v4.app.NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
               // .setLargeIcon(R.mipmap.ic_launcher)
                .setContentTitle(notificationTitle).setContentText(notifcationMessage);


        Intent resultIntent = new Intent(click_action);
        resultIntent.putExtra("user_id",from_user_id);


        PendingIntent resultPendingIntent = PendingIntent.getActivity(
                this,
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        mBuilder.setContentIntent(resultPendingIntent);


        int notificationId = (int) System.currentTimeMillis();
        NotificationManager mNotify = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

    mNotify.notify(notificationId,mBuilder.build());
    }
}
