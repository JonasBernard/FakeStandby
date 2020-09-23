package android.jonas.fakestandby.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.jonas.fakestandby.utils.Constants;
import android.jonas.fakestandby.utils.Utils;
import android.os.PowerManager;
import android.util.Log;

public class PhoneLockReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Utils.isOverlayShowing(context)) {
            Intent new_intent = new Intent(context, AccessibilityOverlayService.class);
            new_intent.putExtra(Constants.Intent.Extra.OverlayAction.KEY, Constants.Intent.Extra.OverlayAction.HIDE_IMMEDIATELY);
            context.startService(new_intent);

            Log.i(getClass().getName(), "Sent intent to hide overlay");

            PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "fakestandby:overlaystopped");
            wakeLock.acquire(10000);
            wakeLock.release();
        }
    }
}
