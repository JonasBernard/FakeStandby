package android.jonas.fakestandby.settings;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.jonas.fakestandby.R;
import android.jonas.fakestandby.service.AccessibilityOverlayService;
import android.jonas.fakestandby.utils.Constants;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NoCloseOptionSelectedNotification {

    Intent intent;
    PendingIntent pendingIntent;
    NotificationCompat.Builder builder;
    NotificationManagerCompat notificationManager;

    public NoCloseOptionSelectedNotification(Context context) {
        intent = new Intent(context, SettingsActivity.class);
        pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel erorrs = new NotificationChannel("errors", context.getString(R.string.notification_channel_errors_name), NotificationManager.IMPORTANCE_HIGH);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(erorrs);

        }

        builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.simple_tile_icon_36dp)
                .setContentTitle(context.getString(R.string.close_option_error_no_option_selected_title))
                .setContentText(context.getString(R.string.close_option_error_no_option_selected_description))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setChannelId("errors")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        notificationManager = NotificationManagerCompat.from(context);
    }

    public void drop() {
        notificationManager.notify(Constants.Notification.ID, builder.build());
    }

}
