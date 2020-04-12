package andoird.jonas.fakestandby.compatibility;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import andoird.jonas.fakestandby.R;
import andoird.jonas.fakestandby.service.AccessibilityOverlayService;
import andoird.jonas.fakestandby.utils.Constants;

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
