package android.jonas.fakestandby.compatibility;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.jonas.fakestandby.R;
import android.jonas.fakestandby.service.AccessibilityOverlayService;
import android.jonas.fakestandby.settings.SettingsActivity;
import android.jonas.fakestandby.utils.Constants;
import android.os.Build;


public class OverlayNotification {

    Intent start_intent, settings_intent;
    PendingIntent start_pendingIntent, settings_pendingIntent;
    NotificationCompat.Builder builder;
    NotificationManagerCompat notificationManager;

    public OverlayNotification(Context context) {
        start_intent = new Intent(context, AccessibilityOverlayService.class);
        start_intent.putExtra(Constants.Intent.Extra.OverlayAction.KEY, Constants.Intent.Extra.OverlayAction.SHOW);
        start_pendingIntent = PendingIntent.getService(context, 0, start_intent, PendingIntent.FLAG_IMMUTABLE);

        settings_intent = new Intent(context, SettingsActivity.class);
        settings_pendingIntent = PendingIntent.getActivity(context, 1, settings_intent, PendingIntent.FLAG_IMMUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.notification_default_channel_name);
            String description = context.getString(R.string.notification_default_channel_description);
            int importance = NotificationManager.IMPORTANCE_MIN;
            NotificationChannel channel = new NotificationChannel(Constants.Notification.CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(context, Constants.Notification.CHANNEL_ID)
                    .setSmallIcon(R.mipmap.simple_tile_icon_36dp)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(context.getString(R.string.notification_message))
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .addAction(R.drawable.ic_baseline_settings_24,
                            context.getString(R.string.title_activity_settings),
                            settings_pendingIntent)
                    .setContentIntent(start_pendingIntent);
        } else {
            builder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.simple_tile_icon_36dp)
                    .setContentTitle(context.getString(R.string.app_name))
                    .setContentText(context.getString(R.string.notification_message))
                    .setAutoCancel(false)
                    .setOngoing(true)
                    .addAction(R.drawable.ic_baseline_settings_24,
                            context.getString(R.string.title_activity_settings),
                            settings_pendingIntent)
                    .setContentIntent(start_pendingIntent)
                    .setPriority(NotificationCompat.PRIORITY_MIN);
        }


        notificationManager = NotificationManagerCompat.from(context);
    }

    public void drop() {
        notificationManager.notify(Constants.Notification.ID, builder.build());
    }

    public void cancel() {
        notificationManager.cancel(Constants.Notification.ID);
    }

}
