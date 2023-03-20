package android.jonas.fakestandby.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.jonas.fakestandby.BuildConfig;
import android.jonas.fakestandby.onboarding.OnBoardingActivity;
import android.jonas.fakestandby.permissions.AccessibilityServiceNotEnabledDialog;
import android.jonas.fakestandby.permissions.AccessibilityServiceNotRunningDialog;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import android.util.Log;
import android.view.View;
import android.jonas.fakestandby.R;
import android.jonas.fakestandby.permissions.OverlayPermissionRequiredDialog;
import android.jonas.fakestandby.service.AccessibilityOverlayService;
import android.jonas.fakestandby.utils.Constants;
import android.jonas.fakestandby.permissions.PermissionUtils;

public class SettingsActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (BuildConfig.DEBUG && !getIntent().getBooleanExtra("skip_intro", false)) {
        //if (Utils.isFirstOpen(this) || BuildConfig.DEBUG) {
            Intent i = new Intent(this, OnBoardingActivity.class);
            //startActivity(i);
        }

        inflateSettings();

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

        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Log.i(getClass().getName(), "Preference changed: " + key);
        if (key.equals("setting_show_notification")) {
            Intent intent = new Intent(getApplicationContext(), AccessibilityOverlayService.class);
            if (sharedPreferences.getBoolean("setting_show_notification", false)) {
                intent.putExtra(Constants.Intent.Extra.OverlayAction.KEY, Constants.Intent.Extra.OverlayAction.SHOW_NOTIFICATION);
            } else {
                intent.putExtra(Constants.Intent.Extra.OverlayAction.KEY, Constants.Intent.Extra.OverlayAction.HIDE_NOTIFICATION);
            }
            startService(intent);
        }
    }

    private void inflateSettings() {
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.title_activity_settings);
        setSupportActionBar(toolbar);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
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
