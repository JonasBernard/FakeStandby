package android.jonas.fakestandby.service;

import android.accessibilityservice.AccessibilityService;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.jonas.fakestandby.R;
import android.jonas.fakestandby.compatibility.OverlayNotification;
import android.jonas.fakestandby.utils.Constants;
import android.jonas.fakestandby.utils.OnHideFinishedListener;
import android.jonas.fakestandby.utils.OnSwipeListener;
import android.jonas.fakestandby.utils.OverlayView;

public class AccessibilityOverlayService extends AccessibilityService {

    public static boolean running = false;

    // Some objects that make the rendering possible
    static WindowManager windowManager;
    static WindowManager.LayoutParams layoutParams;

    // Self implemented view that renders mainly black but can also get transparent.
    static OverlayView view;

    // Store the y-location where dragging started
    public static float BasePX = 0;

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
    //  ADDED  <---                           Set to after the overlay view component was added to the screen.
    //  SHOWING   |                           Set to while the view is blending from transparent to black.
    //  VISIBLE   |                           Set to when the overlay is visible but the user is not interacting with it.
    //  DRAGGING  |                           Set to as soon as the user taps on the touch screen and while he is dragging.
    //  FALLING   |                           Set to when the user releases the touch screen but did not swipe upwards to hide the overlay. Remains while the falling down animation is performed.
    //  HIDING    |                           Set to when the user releases the touch screen and did swipe upwards to hide the overlay. Remains while the hiding animation is performed
    //  HIDDEN    |                           Set to when the overlay is invisible to the user but still there.
    //  REMOVED ---                           Set to when the overlay view component was removed from screen. Remains while the overlay is hidden.
    //                                        From there it jumps back to "ADDED" as soon as the service gets an intent to show the overlay again.


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

        // Set preference that the service is now running
        writePref(true);
        Log.i(getClass().getName(), "Accessibility service started.");
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
            case Constants.Intent.Extra.OverlayAction.NOTHING:
                Log.i(getClass().getName(), "Received intent to do nothing with the overlay");
                break;
            default:
                Log.i(getClass().getName(), "Received intent without usable information");
                break;
        }

        return START_STICKY_COMPATIBILITY;
    }

    private void init() {
        // Set the right state and log it.
        state = Constants.Overlay.State.INITIALIZING;
        Log.i(getClass().getName(), "Initializing...");

        // Theme the app (is used for some dialogs later).
        getApplication().setTheme(R.style.AppTheme);

        // Initialize the self implemented view that renders mainly black but can also get transparent.
        view = new OverlayView(getApplicationContext());
        // Manage some layout parameters fro example to match the whole screen and set that the user cannot touch through the overlay.
        layoutParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY, //TYPE_PHONE OR TYPE_SYSTEM_OVERLAY
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS |
                        WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION | WindowManager.LayoutParams.FLAG_FULLSCREEN |
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
                        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                PixelFormat.TRANSLUCENT);
        layoutParams.alpha = 1;
        layoutParams.gravity = Gravity.TOP | Gravity.START;
        layoutParams.x = 0;
        layoutParams.y = 0;

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
                        state == Constants.Overlay.State.HIDING||
                        state == Constants.Overlay.State.SHOWING) {
                    return false;
                }

                // One method to hide the overlay is to trigger 4 or more touches at the same time.
                // This is only accepted while the overlay is just visible and doing nothing ("VISIBLE")
                // or when it is currently dragged by the user ("DRAGGING").
                if(event.getPointerCount() >= 4 &&
                        (state == Constants.Overlay.State.VISIBLE ||
                                state == Constants.Overlay.State.DRAGGING)) {
                    Log.i(getClass().getName(), "Hiding due to 4 or more simultaneous touches");

                    return hide();
                }

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
                return true;
            }
        }) {
            @Override
            // The user just released the touch screen and a swipe (upwards) was recognized
            public void onSwipeTop(float velocity) {
                Log.i(getClass().getName(), "User swiped upwards. Hiding overlay...");

                // Hide the overlay by moving it out of the way with the velocity of the swipe.
                hide(velocity);
            }

            @Override
            // The user just released the touch screen but a swipe was not recognized
            public void onSwipeFail() {
                Log.i(getClass().getName(), "User swiped but in wrong direction or to slow. Falling back down...");

                // Start the "falling effect" to make the whole overlay black again
                fall();
            }
        });

        view.setLayoutParams(
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );
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
            // Remove the view component
            windowManager.removeView(view);
            // Set the state
            state = Constants.Overlay.State.REMOVED;
            Log.i(getClass().getName(), "Successfully removed view");
        } catch (IllegalArgumentException e) {
            Log.e(getClass().getName(), "Failed to remove view");
        }
    }

    private void show() {
        // Check for the right state
        if (state == Constants.Overlay.State.INITIALIZED ||
                state == Constants.Overlay.State.REMOVED) {
            // Close navigation drawer
            Intent closeIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            sendBroadcast(closeIntent);

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
                    Log.i(getClass().getName(), "Finished blending to black");
                }
            }).start();

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

    private void initializeNotification() {
        // When the device does not support QuickTiles a custom notification is dropped.
        // It gives users with older devices the ability to also start the overlay.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            OverlayNotification notification = new OverlayNotification(this);
            notification.drop();
        }
    }

    private void writePref(boolean value) {
        running = value;
        getSharedPreferences(Constants.Preferences.PREFERENCE_NAME, MODE_PRIVATE).edit().putBoolean(Constants.Preferences.IS_SERVICE_RUNNING, value).apply();
        Log.i(getClass().getName(), "Successfully wrote preference " + Constants.Preferences.IS_SERVICE_RUNNING + " to " + (value ? "true":"false"));
    }

    @Override
    public void onInterrupt() {
        writePref(false);
        // When the AccessibilityService is stopped for whatever reason try to hide the view
        if (!hide()) {
            // If the view cannot be hidden because for example it is in th wrong state just remove it
            removeView();
        }
        Log.i(getClass().getName(), "Accessibility service started.");
    }
}
