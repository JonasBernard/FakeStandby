package android.jonas.fakestandby.compatibility;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.jonas.fakestandby.R;
import android.jonas.fakestandby.service.AccessibilityOverlayService;
import android.jonas.fakestandby.utils.Constants;

public class OverlayNotification {

    Intent intent;
    PendingIntent pendingIntent;
    NotificationCompat.Builder builder;
    NotificationManagerCompat notificationManager;

    public OverlayNotification(Context context) {
        intent = new Intent(context, AccessibilityOverlayService.class);
        intent.putExtra(Constants.Intent.Extra.OverlayAction.KEY, Constants.Intent.Extra.OverlayAction.SHOW);
        pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.simple_tile_icon_36dp)
                .setContentTitle(context.getString(R.string.app_name))
                .setContentText(context.getString(R.string.notification_message))
                .setAutoCancel(false)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_MIN);

        notificationManager = NotificationManagerCompat.from(context);
    }

    public void drop() {
        notificationManager.notify(Constants.Notification.ID, builder.build());
    }

}
