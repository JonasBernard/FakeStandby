package android.jonas.fakestandby.quicktile;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.jonas.fakestandby.permissions.PermissionUtils;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.jonas.fakestandby.service.AccessibilityOverlayService;
import android.jonas.fakestandby.utils.Constants;

import androidx.fragment.app.DialogFragment;

@TargetApi(Build.VERSION_CODES.N)
public class OverlayQuickTile extends TileService {

    private Context context = null;
    private Runnable start_overlay = new Runnable() {
        @Override
        public void run() {
            if(!checkConditions()) {
                return;
            }

            Intent intent = new Intent(context, AccessibilityOverlayService.class);
            intent.putExtra(Constants.Intent.Extra.OverlayAction.KEY, Constants.Intent.Extra.OverlayAction.SHOW);
            startService(intent);

            Log.i(getClass().getName(), "Sent intent to show overlay");
        }
    };


    @Override
    public void onClick() {
        context = this;

        if (isLocked()) {
            unlockAndRun(start_overlay);
        }else {
            start_overlay.run();
        }

        onStartListening();
    }

    public boolean checkConditions() {
        Log.i(getClass().getName(), "Checking if required permissions are given and service is running...");
        if (!PermissionUtils.checkAccessibilityServiceRunning(context)) {
            if (!PermissionUtils.checkAccessibilityServiceEnabled(this)) {
                Log.i(getClass().getName(), "Service is not enabled. Prompting the user...");
                showDialog(PermissionUtils.getAccessibilityServiceNotEnabledAlertDialog(context));
                return false;
            }
            Log.i(getClass().getName(), "Service is not running. Prompting the user...");
            showDialog(PermissionUtils.getAccessibilityServiceNotRunningAlertDialog(context));
            return false;
        }
        if (!PermissionUtils.checkPermissionOverlay(context)) {
            Log.i(getClass().getName(), "No Overlay permission. Prompting the user...");
            showDialog(PermissionUtils.getPermissionOverlayRequestAlertDialog(context));
            return false;
        }
        Log.i(getClass().getName(), "Everything is fine. Overlay can be launched.");
        return true;
    }

    @Override
    public void onTileAdded() {
        onStartListening();
    }

    @Override
    public void onStartListening() {
        getQsTile().setState(Tile.STATE_INACTIVE);
        getQsTile().updateTile();
    }

}
