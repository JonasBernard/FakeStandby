package android.jonas.fakestandby.actions;

import android.content.Intent;
import android.jonas.fakestandby.permissions.PermissionUtils;
import android.jonas.fakestandby.service.AccessibilityOverlayService;
import android.jonas.fakestandby.utils.Constants;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class StartOverlay extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        if(checkConditions()) {
            Intent intent = new Intent(getApplicationContext(), AccessibilityOverlayService.class);
            intent.putExtra(Constants.Intent.Extra.OverlayAction.KEY, Constants.Intent.Extra.OverlayAction.SHOW);
            startService(intent);

            Log.i(getClass().getName(), "Sent intent to show overlay");
        }

        finish();

        super.onCreate(savedInstanceState);

    }

    public boolean checkConditions() {
        Log.i(getClass().getName(), "Checking if required permissions are given and service is running...");
        if (!PermissionUtils.checkAccessibilityServiceRunning(this)) {
            return false;
        }
        if (!PermissionUtils.checkPermissionOverlay(this)) {
            return false;
        }
        Log.i(getClass().getName(), "Everything is fine. Overlay can be launched.");
        return true;
    }
}
