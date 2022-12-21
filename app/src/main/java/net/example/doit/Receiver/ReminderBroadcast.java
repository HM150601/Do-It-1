package net.example.doit.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import net.example.doit.R;

import java.util.Date;

public class ReminderBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyDoIt")
                .setSmallIcon(R.drawable.ic_round_notifications_active_24)
                .setContentTitle("Remind task for me")
                .setContentText("Hi, please you check your task today...")
                .setPriority(NotificationCompat.PRIORITY_MAX);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(getNotificationID(), builder.build());
    }

    private int getNotificationID(){
        return (int) new Date().getTime();
    }
}
