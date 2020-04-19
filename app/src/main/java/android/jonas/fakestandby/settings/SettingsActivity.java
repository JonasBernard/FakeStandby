package android.jonas.fakestandby.settings;

import android.content.Intent;
import android.jonas.fakestandby.permissions.AccessibilityServiceNotEnabledDialog;
import android.jonas.fakestandby.permissions.AccessibilityServiceNotRunningDialog;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceFragmentCompat;
import android.util.Log;
import android.view.View;
import android.jonas.fakestandby.R;
import android.jonas.fakestandby.permissions.OverlayPermissionRequiredDialog;
import android.jonas.fakestandby.service.AccessibilityOverlayService;
import android.jonas.fakestandby.utils.Constants;
import android.jonas.fakestandby.permissions.PermissionUtils;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_settings);
        setSupportActionBar(toolbar);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkConditions()) {
                    return;
                }

                Intent intent = new Intent(getApplicationContext(), AccessibilityOverlayService.class);
                intent.putExtra(Constants.Intent.Extra.OverlayAction.KEY, Constants.Intent.Extra.OverlayAction.SHOW);
                startService(intent);

                Log.i(getClass().getName(), "Sent intent to show overlay");
            }
        });
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
        }
    }

    public boolean checkConditions() {
        Log.i(getClass().getName(), "Checking if required permissions are given and service is running...");
        if (!PermissionUtils.checkAccessibilityServiceRunning(this)) {
            if (!PermissionUtils.checkAccessibilityServiceEnabled(this)) {
                Log.i(getClass().getName(), "Service is not enabled. Prompting the user...");
                DialogFragment CASE = new AccessibilityServiceNotEnabledDialog();
                CASE.show(getSupportFragmentManager(), "accessibility_service_not_enabled");
                return false;
            }
            Log.i(getClass().getName(), "Service is not running. Prompting the user...");
            DialogFragment CASR = new AccessibilityServiceNotRunningDialog();
            CASR.show(getSupportFragmentManager(), "accessibility_service_not_running");
            return false;
        }
        if (!PermissionUtils.checkPermissionOverlay(this)) {
            Log.i(getClass().getName(), "No Overlay permission. Prompting the user...");
            DialogFragment CPO = new OverlayPermissionRequiredDialog();
            CPO.show(getSupportFragmentManager(), "overlay_permission_required");
            return false;
        }
        Log.i(getClass().getName(), "Everything is fine. Overlay can be launched.");
        return true;
    }

}
