package com.windherd.topactivity;

import static android.app.PendingIntent.FLAG_IMMUTABLE;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
public class NotificationActionReceiver extends BroadcastReceiver {
    public static final int NOTIFICATION_ID = 1;
    public static final String ACTION_NOTIFICATION_RECEIVER = "com.willme.topactivity.ACTION_NOTIFICATION_RECEIVER";
    public static final int ACTION_PAUSE = 0;
    public static final int ACTION_RESUME = 1;
    public static final int ACTION_STOP = 2;
    public static final String EXTRA_NOTIFICATION_ACTION = "command";

    public static void showNotification(Context context, boolean isPaused) {
        if (!SPHelper.isNotificationToggleEnabled(context)) {
            return;
        }
        PendingIntent pIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), FLAG_IMMUTABLE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,"topactivity")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(context.getString(R.string.is_running,
                        context.getString(R.string.app_name)))
                .setContentText(context.getString(R.string.touch_to_open))
                .setColor(0xFFe215e0)
                .setVisibility(NotificationCompat.VISIBILITY_SECRET)
                .setOngoing(!isPaused);
        if (isPaused) {
            builder.addAction(R.drawable.ic_noti_action_resume, context.getString(R.string.noti_action_resume),
                    getPendingIntent(context, ACTION_RESUME));
        } else {
            builder.addAction(R.drawable.ic_noti_action_pause,
                    context.getString(R.string.noti_action_pause),
                    getPendingIntent(context, ACTION_PAUSE));
        }

        builder.addAction(R.drawable.ic_noti_action_stop,
                context.getString(R.string.noti_action_stop),
                getPendingIntent(context, ACTION_STOP))
                .setContentIntent(pIntent);
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, builder.build());
    }

    public static PendingIntent getPendingIntent(Context context, int command) {
        Intent intent = new Intent(ACTION_NOTIFICATION_RECEIVER);
        intent.putExtra(EXTRA_NOTIFICATION_ACTION, command);
        return PendingIntent.getBroadcast(context, command, intent, FLAG_IMMUTABLE);
    }

    public static void cancelNotification(Context context) {
        NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        int command = intent.getIntExtra(EXTRA_NOTIFICATION_ACTION, -1);
        switch (command) {
            case ACTION_RESUME:
                showNotification(context, false);
                SPHelper.setIsShowWindow(context, true);
                TasksWindow.show(context, null);
                break;
            case ACTION_PAUSE:
                showNotification(context, true);
                TasksWindow.dismiss(context);
                SPHelper.setIsShowWindow(context, false);
                break;
            case ACTION_STOP:
                TasksWindow.dismiss(context);
                SPHelper.setIsShowWindow(context, false);
                cancelNotification(context);
                break;
        }
        context.sendBroadcast(new Intent(QuickSettingTileService.ACTION_UPDATE_TITLE));
    }
}
