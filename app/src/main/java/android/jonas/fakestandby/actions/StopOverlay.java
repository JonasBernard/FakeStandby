package android.jonas.fakestandby.actions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.jonas.fakestandby.service.AccessibilityOverlayService;
import android.jonas.fakestandby.utils.Constants;
import android.os.Bundle;
import android.util.Log;

public class StopOverlay extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = new Intent(getApplicationContext(), AccessibilityOverlayService.class);
        intent.putExtra(Constants.Intent.Extra.OverlayAction.KEY, Constants.Intent.Extra.OverlayAction.HIDE);
        startService(intent);

        Log.i(getClass().getName(), "Sent intent to hide overlay");

        finish();

        super.onCreate(savedInstanceState);
    }
}