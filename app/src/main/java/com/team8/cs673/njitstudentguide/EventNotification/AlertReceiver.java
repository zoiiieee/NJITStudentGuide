package com.team8.cs673.njitstudentguide.EventNotification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.team8.cs673.njitstudentguide.Dashboard_Activity;
import com.team8.cs673.njitstudentguide.R;


public class AlertReceiver extends BroadcastReceiver {

    // Called when a broadcast is made targeting this class
    @Override
    public void onReceive(Context context, Intent intent) {

        createNotification(intent, context);

    }

    public void createNotification(Intent intent,Context context){
        String msg = intent.getStringExtra("msg");
        String msgText = intent.getStringExtra("msgText");
        String msgAlert = intent.getStringExtra("msgAlert");
        // Define an Intent and an action to perform with it by another application
        PendingIntent notificIntent = PendingIntent.getActivity(context, 0,
                new Intent(context, Dashboard_Activity.class), 0);

        // Builds a notification
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(msg)
                        .setTicker(msgAlert)
                        .setContentText(msgText);

        // Defines the Intent to fire when the notification is clicked
        mBuilder.setContentIntent(notificIntent);

        // Set the default notification option
        // DEFAULT_SOUND : Make sound
        // DEFAULT_VIBRATE : Vibrate
        // DEFAULT_LIGHTS : Use the default light notification
        mBuilder.setDefaults(Notification.DEFAULT_SOUND);

        // Auto cancels the notification when clicked on in the task bar
       // mBuilder.setAutoCancel(true);

        // Gets a NotificationManager which is used to notify the user of the background event
        NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        int id =Integer.parseInt(intent.getData().getSchemeSpecificPart());
        // Post the notification
        mNotificationManager.notify(id, mBuilder.build());

    }
}
