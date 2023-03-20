package android.jonas.fakestandby.service;

import android.accessibilityservice.AccessibilityService;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.PixelFormat;
import android.jonas.fakestandby.settings.NoCloseOptionSelectedNotification;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.jonas.fakestandby.R;
import android.jonas.fakestandby.compatibility.OverlayNotification;
import android.jonas.fakestandby.utils.Constants;
import android.jonas.fakestandby.utils.OnHideFinishedListener;
import android.jonas.fakestandby.utils.OnSwipeListener;
import android.jonas.fakestandby.utils.OverlayView;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AccessibilityOverlayService extends AccessibilityService {

    private static final int pixelOffset = 400;

    public static boolean running = false;

    private PhoneLockReceiver phoneLockReceiver;

    // Static variables for screen dimensions
    private DisplayMetrics dm;

    private final int flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
            WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | WindowManager.LayoutParams.FLAG_FULLSCREEN |
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN |
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;

    // Some objects that make the rendering possible
    static WindowManager windowManager;
    static WindowManager.LayoutParams layoutParams;

    // Self implemented view that renders mainly black but can also get transparent.
    OverlayView view;

    // The Service holds an instance of that class to manage a notification that can be dropped to start the overlay
    OverlayNotification notification;

    // Store the y-location where dragging started
    public static float BasePX = 0;

    // Handler to have one global timeout for secure mode
    private Handler handler = new Handler();
    private Runnable hide_runnable = new Runnable() {
        public void run() {
            hide();
        }
    };

    // Store the current state of the Overlay
    public static byte state = Constants.Overlay.State.UNSET;
    // ##############################  Lifecycle of the overlay ####################################
    //
    //  UNSET                                 Set to as default and never entered again after the overlay was initialized successfully.
    //     |
    //     v
    //  INITIALIZING                          Set to while initializing the view component for the overlay.
    //  INITIALIZED                           Set to when the overlay is ready.
    //     |
    //     v
    //  ADDED  <----------------                           Set to after the overlay view component was added to the screen.
    //     v                   |
    //  SHOWING                |                           Set to while the view is blending from transparent to black.
    //     v                   |
    //  VISIBLE <--------      |                           Set to when the overlay is visible but the user is not interacting with it.
    //     v            |      |
    //  DRAGGING ---> FALLING  |                           DRAGGING: Set to as soon as the user taps on the touch screen and while he is dragging.
    //     |--------           |                           FALLING: Set to when the user releases the touch screen but did not swipe upwards to hide the overlay. Remains while the falling down animation is performed.
    //     v       |           |
    //  HIDING     |           |                           Set to when the user releases the touch screen and did swipe upwards to hide the overlay. Remains while the hiding animation is performed.
    //     |       |           |                           This state is skipped, if the requested action is "HIDE_IMMEDIATELY" and hide_immediately() is called.
    //     v       |           |
    //  HIDDEN <----           |                           Set to when the overlay is invisible to the user but still there.
    //     v                   |
    //  REMOVED ----------------                           Set to when the overlay view component was removed from screen. Remains while the overlay is hidden.
    //                                                     From there it jumps back to "ADDED" as soon as the service gets an intent to show the overlay again.


    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        //Log.i(getClass().getName(), "Accessibility Event triggered (Action:" + event.getAction() + ")");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        // Initialize everything to be ready to display the overlay.
        init();
        // When the device does not support QuickTiles a custom notification is dropped
        initializeNotification();
        // Initialize broadcast receiver
        initializeBroadcastReceiver();

        // Set preference that the service is now running
        writeServiceRunningPref(true);
        Log.i(getClass().getName(), "Accessibility service started.");

        if (getStartOnBootPref()) {
            show();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null || windowManager == null || flags == START_FLAG_RETRY || flags == START_FLAG_REDELIVERY) {
            return START_STICKY_COMPATIBILITY;
        }

        // Get the action that should be performed
        byte action = intent.getByteExtra(Constants.Intent.Extra.OverlayAction.KEY, Constants.Intent.Extra.OverlayAction.DEFAULT);
        switch (action) {
            case Constants.Intent.Extra.OverlayAction.SHOW:
                Log.i(getClass().getName(), "Received intent to show overlay");

                // The requested action is to show the overlay. Let's do it.
                show();
                break;
            case Constants.Intent.Extra.OverlayAction.HIDE:
                Log.i(getClass().getName(), "Received intent to hide overlay");

                // The requested action is to hide the overlay. Let's do it.
                hide();
                break;
            case Constants.Intent.Extra.OverlayAction.HIDE_IMMEDIATELY:
                Log.i(getClass().getName(), "Received intent to hide overlay (immediately)");

                // The requested action is to hide the overlay immediately. Let's do it.
                hide_immediately();
                break;
            case Constants.Intent.Extra.OverlayAction.SHOW_NOTIFICATION:
                Log.i(getClass().getName(), "Received intent to show the compat notification");

                //Drop the notification
                dropNotification();
                break;
            case Constants.Intent.Extra.OverlayAction.HIDE_NOTIFICATION:
                Log.i(getClass().getName(), "Received intent to hide the compat notification");

                //Cancel the notification
                cancelNotification();
                break;
            case Constants.Intent.Extra.OverlayAction.NOTHING:
                Log.i(getClass().getName(), "Received intent to do nothing");

                // You may say that this is useless. But wait and see...
                break;
            default:
                Log.i(getClass().getName(), "Received intent without usable information");
                break;
        }

        return START_STICKY_COMPATIBILITY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (state == Constants.Overlay.State.SHOWING || state == Constants.Overlay.State.VISIBLE) {
            removeView();
            addView();
        }
        if (state == Constants.Overlay.State.DRAGGING || state == Constants.Overlay.State.FALLING) {
            hide_immediately();
            addView();
        }
    }

    private void init() {
        // Set the right state and log it.
        state = Constants.Overlay.State.INITIALIZING;
        Log.i(getClass().getName(), "Initializing...");

        // Theme the app (is used for some dialogs later).
        getApplication().setTheme(R.style.AppTheme);

        // Get display dimensions
        dm = getResources().getDisplayMetrics();

        // Initialize the self implemented view that renders mainly black but can also get transparent.
        view = new OverlayView(getApplicationContext());
        // Manage some layout parameters fro example to match the whole screen and set that the user cannot touch through the overlay.
        layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, //TYPE_PHONE OR TYPE_SYSTEM_OVERLAY
                flags,
                PixelFormat.TRANSLUCENT);
        layoutParams.alpha = 1;
        layoutParams.gravity = Gravity.TOP | Gravity.START;
        layoutParams.x = -pixelOffset;
        layoutParams.y = -pixelOffset;

        // The overlay can be stopped by dragging upwards or tapping on the screen with 4 or more fingers.
        // To manage the dragging initialize a new OnSwipeListener that extends OnTouchListener
        // and give it (as parameter) another OnTouchListener that is also called on every touch.
        view.setOnTouchListener(new OnSwipeListener(this, view.getHeight(), new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // When the overlay is in state "falling", "hiding" or "showing" it means,
                // that it is currently reacting to user touch input. When another action is started now,
                // it could lead to two actions running simultaneously. The variable "state" then
                // gets out of sync from the actual state of the overlay. This could stop some functions to work
                // as they require certain states. To prevent the app from being stuck and
                // in a status where the user cannot access his phone anymore, do not accept any
                // touch input while in animation.
                if (state == Constants.Overlay.State.FALLING ||
                        state == Constants.Overlay.State.HIDING ||
                        state == Constants.Overlay.State.SHOWING) {
                    return false;
                }

                if (getIsCloseOptionEnabled(getResources().getStringArray(R.array.close_options_values)[0])) {
                    // One method to hide the overlay is to trigger 4 or more touches at the same time.
                    // This is only accepted while the overlay is just visible and doing nothing ("VISIBLE")
                    // or when it is currently dragged by the user ("DRAGGING").
                    if(event.getPointerCount() >= 4 &&
                            (state == Constants.Overlay.State.VISIBLE ||
                                    state == Constants.Overlay.State.DRAGGING)) {
                        Log.i(getClass().getName(), "Hiding due to 4 or more simultaneous touches");

                        return hide();
                    }

                }

                if (getIsCloseOptionEnabled(getResources().getStringArray(R.array.close_options_values)[1])) {
                    // Alternatively to hide the overlay the user can drag. While dragging, the overlay reveals
                    // the screen content underlying the overlay by creating a transparent area.
                    // This area starts at the bottom of the screen an rises as the user swipes up more and more.
                    // This is similar to the effect of closing the android notification drawer.
                    // The height (in px) of the transparent area is called "yBorder".
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            // When the touch screen is tapped it starts tracking the movement
                            Log.i(getClass().getName(), "Started tracking a touch event");

                            // Set the state to dargging
                            state = Constants.Overlay.State.DRAGGING;
                            // Store the position where the drag started
                            BasePX = event.getY();
                            // Reset the overlay to display everything in black
                            view.setyBorder(0);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            // While dragging, keep revealing the screen content by setting the height of
                            // the transparent area of the overlay to the (vertical) distance of the drag
                            view.setyBorder(BasePX - event.getY());
                            break;
                        case MotionEvent.ACTION_UP:
                            // When the touch screen is released, dragging is finished
                            Log.i(getClass().getName(), "Touchscreen released. Stopped tracking touch event");

                            // For now we cannot determine weather the user just let the overlay go or swiped upwards
                            // so lets trigger to minimize the transparent area of the overlay again.
                            // This is called "falling" here. When the user swiped, the should not "fall down" again,
                            // but start hiding. This action is started (see following code) and interrupts this action.
                            // TODO fix: starting to fall even if the user swiped upwards to hide the overlay
                            Log.i(getClass().getName(), "Released touchscreen. Falling back down...");
                            Log.i(getClass().getName(), "If the user swiped, the action will be just cancelled");

                            fall();
                            break;
                    }
                }
                return true;
            }
        }) {
            @Override
            // The user just released the touch screen and a swipe (upwards) was recognized
            public void onSwipeTop(float velocity) {
                if (getIsCloseOptionEnabled(getResources().getStringArray(R.array.close_options_values)[1])) {
                    Log.i(getClass().getName(), "User swiped upwards. Hiding overlay...");

                    // Hide the overlay by moving it out of the way with the velocity of the swipe.
                    hide(velocity);
                }
            }

            @Override
            // The user just released the touch screen but a swipe was not recognized
            public void onSwipeFail() {
                if (getIsCloseOptionEnabled(getResources().getStringArray(R.array.close_options_values)[1])) {
                    Log.i(getClass().getName(), "User swiped but in wrong direction or to slow. Falling back down...");

                    // Start the "falling effect" to make the whole overlay black again
                    fall();
                }
            }
        });

        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);

        // Assign the params defined earlier to the view component
        view.setLayoutParams(layoutParams);

        // Finished initialization. Set the sate to initialized.
        state = Constants.Overlay.State.INITIALIZED;
        Log.i(getClass().getName(), "Initialization finished");
    }

    private void addView() {
        // Check for the right state
        if (state == Constants.Overlay.State.INITIALIZED ||
                state == Constants.Overlay.State.REMOVED) {
            // Set dimensions to current width and height (these may change from time to time due to device rotation)
            layoutParams.width = dm.widthPixels + 2 * pixelOffset;
            layoutParams.height = dm.heightPixels + 2 * pixelOffset;


            // Set color of the overlay
            view.setInvertColor(this.getInvertOverlayColorPref());

            // Add the view component
            windowManager.addView(view, layoutParams);
            // Set the state
            state = Constants.Overlay.State.ADDED;
            Log.i(getClass().getName(), "Successfully added view");
        } else {
            Log.e(getClass().getName(), "Overlay is not in required state. Cancel adding view. Overlay is in state " + Constants.Overlay.getStateName(state));
        }
    }

    private void removeView() {
        try {
            // Reset secure mode countdown
            handler.removeCallbacks(hide_runnable);
            // Remove the view component
            windowManager.removeView(view);
            // Set the state
            state = Constants.Overlay.State.REMOVED;

            writeOverlayShowingPref(false);
            Log.i(getClass().getName(), "Successfully removed view");
        } catch (IllegalArgumentException e) {
            Log.e(getClass().getName(), "Failed to remove view");
        }
    }

    private void show() {
        // Check whether at least one option to close the overlay is selected in the prefs
        String[] options = getResources().getStringArray(R.array.close_options_values);
        int enabled_options = 0;
        for (String option : options) {
            if (getIsCloseOptionEnabled(option)) enabled_options++;
        }
        if (enabled_options < 1) {
            NoCloseOptionSelectedNotification notification = new NoCloseOptionSelectedNotification(this);
            notification.drop();

            return;
        }

        // Check for the right state
        if (state == Constants.Overlay.State.INITIALIZED ||
                state == Constants.Overlay.State.REMOVED) {
            // Close navigation drawer
            // Intent closeIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            // sendBroadcast(closeIntent);
            // OR
            // this.performGlobalAction(AccessibilityService.GLOBAL_ACTION_DISMISS_NOTIFICATION_SHADE);

            // Add the view then show it
            addView();
            state = Constants.Overlay.State.SHOWING;
            // Set it to fully black
            view.setyBorder(0);
            // Fade to black
            view.animate().alpha(1f).setDuration(600).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    // When finished set the state to visible
                    state = Constants.Overlay.State.VISIBLE;
                    writeOverlayShowingPref(true);

                    Log.i(getClass().getName(), "Finished blending to black");
                }
            }).start();

            if (this.getIsSecureModeOn()) {
                handler.removeCallbacks(hide_runnable);
                handler.postDelayed(hide_runnable, 15000);
            }

            Log.i(getClass().getName(), "Successfully started blending to black");
        } else {
            Log.e(getClass().getName(), "Overlay already visible. Overlay is in state " + Constants.Overlay.getStateName(state));
        }
    }

    public void fall() {
        // Check for the right state
        if (state == Constants.Overlay.State.DRAGGING) {
            BasePX = 0;
            // Start falling animation
            view.setFalling(true);
            state = Constants.Overlay.State.FALLING;
            Log.i(getClass().getName(), "Started falling");
        } else {
            Log.e(getClass().getName(), "Overlay is not in required state. Cancel falling. Overlay is in state " + Constants.Overlay.getStateName(state));
        }
    }

    public boolean hide(float velocity) {
        // Check for the right state. The hiding action can be started while the overlay is only visible or
        // while the user is dragging or while the overlay is falling back to its base position
        if (state == Constants.Overlay.State.VISIBLE ||
                state == Constants.Overlay.State.DRAGGING ||
                state == Constants.Overlay.State.FALLING) {
            state = Constants.Overlay.State.HIDING;
            BasePX = 0;
            // Start hiding animation
            view.setHiding(true);
            view.setHidingVelocity(velocity/50);
            view.setOnHideFinishedListener(new OnHideFinishedListener() {
                @Override
                public void onHideFinished() {
                    // When finished set the state and remove the (invisible) view component
                    state = Constants.Overlay.State.HIDDEN;
                    removeView();
                }
            });

            Log.i(getClass().getName(), "Successfully started hiding with animation");

            return true;
        } else {
            Log.e(getClass().getName(), "Overlay is not in required state. Cancel hiding. Overlay is in state " + Constants.Overlay.getStateName(state));
            return false;
        }
    }

    private boolean hide() {
        // Check for the right state. The hiding action can be started while the overlay is only visible or
        // while the user is dragging or while the overlay is falling back to its base position
        if (state == Constants.Overlay.State.VISIBLE ||
                state == Constants.Overlay.State.DRAGGING ||
                state == Constants.Overlay.State.FALLING) {
            state = Constants.Overlay.State.HIDING;
            // Start hiding animation
            view.setHiding(false);
            view.setFalling(false);
            view.animate().alpha(0f).setDuration(600).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {}

                @Override
                public void onAnimationEnd(Animator animation) {
                    // When finished set the state and remove the (invisible) view component
                    state = Constants.Overlay.State.HIDDEN;
                    removeView();
                }

                @Override
                public void onAnimationCancel(Animator animation) {}

                @Override
                public void onAnimationRepeat(Animator animation) {}
            }).start();

            Log.i(getClass().getName(), "Successfully started blending to transparent");

            return true;
        } else {
            Log.e(getClass().getName(), "Overlay is not in required state. Cancel hiding. Overlay is in state " + Constants.Overlay.getStateName(state));
            return false;
        }
    }

    private void hide_immediately() {
        // Check for the right state. The hiding action can be started while the overlay is only visible or
        // while the user is dragging or while the overlay is falling back to its base position
        if (state == Constants.Overlay.State.VISIBLE ||
                state == Constants.Overlay.State.DRAGGING ||
                state == Constants.Overlay.State.FALLING) {

            // When finished set the state and remove the (invisible) view component
            state = Constants.Overlay.State.HIDDEN;
            removeView();

            Log.i(getClass().getName(), "Successfully hidden overlay");
        } else {
            Log.e(getClass().getName(), "Overlay is not in required state. Cancel hiding. Overlay is in state " + Constants.Overlay.getStateName(state));
        }
    }

    public void initializeNotification() {
        // When the device does not support QuickTiles a custom notification is dropped.
        // It gives users with older devices the ability to also start the overlay.

        notification = new OverlayNotification(this);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N || getShowNotificationPref()) {
            notification.drop();
        }
    }

    private void dropNotification() {
        notification.drop();
    }

    private void cancelNotification() {
        notification.cancel();
    }

    public void initializeBroadcastReceiver() {
        if (this.phoneLockReceiver == null) this.phoneLockReceiver = new PhoneLockReceiver();
        registerReceiver(this.phoneLockReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        registerReceiver(this.phoneLockReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    }

    public void unregisterBroadcastReceiver() {
        unregisterReceiver(this.phoneLockReceiver);
        unregisterReceiver(this.phoneLockReceiver);
    }

    private void writeServiceRunningPref(boolean value) {
        running = value;
        getSharedPreferences(Constants.Preferences.PREFERENCE_NAME, MODE_PRIVATE).edit().putBoolean(Constants.Preferences.IS_SERVICE_RUNNING, value).apply();
        Log.i(getClass().getName(), "Successfully wrote preference " + Constants.Preferences.IS_SERVICE_RUNNING + " to " + (value ? "true":"false"));
    }

    private void writeOverlayShowingPref(boolean value) {
        getSharedPreferences(Constants.Preferences.PREFERENCE_NAME, MODE_PRIVATE).edit().putBoolean(Constants.Preferences.IS_OVERLAY_SHOWING, value).apply();
        Log.i(getClass().getName(), "Successfully wrote preference " + Constants.Preferences.IS_OVERLAY_SHOWING + " to " + (value ? "true":"false"));
    }

    private boolean getIsSecureModeOn() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean("setting_secure_mode", true);
    }

    private boolean getInvertOverlayColorPref() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean("setting_invert_overlay_color", false);
    }

    private boolean getStartOnBootPref() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean("setting_start_on_boot", false);
    }

    private boolean getShowNotificationPref() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean("setting_show_notification", false);
    }

    private boolean getUseWakeLockPref() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean("setting_use_wake_lock", true);
    }

    private boolean getIsCloseOptionEnabled(String valueName) {
        String[] defaults = getResources().getStringArray(R.array.close_options_values);

        Set<String> defaults_set = new HashSet<String>(Arrays.asList(defaults));
        Set<String> options_set = PreferenceManager.getDefaultSharedPreferences(this).getStringSet("setting_close_options", defaults_set);

        if (options_set.contains(valueName)) {
            Log.i(getClass().getName(), "Close option with key " + valueName + " is enabled");
            return true;
        }
        Log.i(getClass().getName(), "Close option with key " + valueName + " is disabled");
        return false;
    }

    @Override
    public void onInterrupt() { }

    @Override
    public boolean onUnbind(Intent intent) {
        writeServiceRunningPref(false);
        // When the AccessibilityService is stopped for whatever reason try to hide the view
        if (!hide()) {
            // If the view cannot be hidden because for example it is in th wrong state just remove it
            removeView();
        }

        unregisterBroadcastReceiver();

        Log.i(getClass().getName(), "Accessibility service stopped.");

        return false;
    }

}
