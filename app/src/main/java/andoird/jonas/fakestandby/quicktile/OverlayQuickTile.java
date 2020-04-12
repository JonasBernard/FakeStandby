package andoird.jonas.fakestandby.quicktile;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;
import java.util.List;
import andoird.jonas.fakestandby.R;
import andoird.jonas.fakestandby.service.AccessibilityOverlayService;
import andoird.jonas.fakestandby.utils.Constants;

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

    private boolean checkConditions() {
        return (checkAccessibilityServiceRunning() && checkPermissionOverlay());
    }

    private boolean checkPermissionOverlay() {
        if (!Settings.canDrawOverlays(this)) {

            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle(getString(R.string.accessibility_error_no_overlay_permission_title));
            alertDialog.setMessage(getString(R.string.accessibility_error_no_overlay_permission_message));
            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(android.R.string.cancel),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.accessibility_error_settings_label),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                            Intent i = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
                            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            startActivity(i);
                        }
                    });
            showDialog(alertDialog);
            return false;
        }
        return true;
    }

    private boolean checkAccessibilityServiceRunning() {
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> enabledServices = am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK);

        for (AccessibilityServiceInfo enabledService : enabledServices) {
            ServiceInfo enabledServiceInfo = enabledService.getResolveInfo().serviceInfo;
            if (enabledServiceInfo.packageName.equals(context.getPackageName()) && enabledServiceInfo.name.equals(AccessibilityOverlayService.class.getName()))
                return true;
        }

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle(getString(R.string.accessibility_error_not_running_title));
        alertDialog.setMessage(getString(R.string.accessibility_error_not_running_message));
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getString(android.R.string.cancel),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getString(R.string.accessibility_error_settings_label),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        Intent i = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        startActivity(i);
                    }
                });
        showDialog(alertDialog);

        return false;
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
