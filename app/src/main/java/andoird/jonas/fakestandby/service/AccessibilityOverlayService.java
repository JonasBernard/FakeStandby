package andoird.jonas.fakestandby.service;

import android.accessibilityservice.AccessibilityService;
import android.animation.Animator;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import andoird.jonas.fakestandby.R;
import andoird.jonas.fakestandby.compatibility.OverlayNotification;
import andoird.jonas.fakestandby.utils.Constants;
import andoird.jonas.fakestandby.utils.OnSwipeListener;
import andoird.jonas.fakestandby.utils.OverlayCancelListener;
import andoird.jonas.fakestandby.utils.OverlayView;

public class AccessibilityOverlayService extends AccessibilityService {

    WindowManager windowManager;
    WindowManager.LayoutParams layoutParams;
    OverlayView view;

    public float BasePX = 0;

    private boolean is_active_now = false;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        Log.i(getClass().getName(), "Accessibility Event triggered (Action:" + event.getAction() + ")");
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        Log.i(getClass().getName(), "Key Event triggered");
        if (event.getKeyCode() == KeyEvent.KEYCODE_POWER) {
            hide();
            return true;
        }
        return false;
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        init();
        initializeNotification();
        writePref(false);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_STICKY_COMPATIBILITY;
        }

        if (windowManager == null) {
            return START_STICKY_COMPATIBILITY;
        }

        if (flags == START_FLAG_RETRY || flags == START_FLAG_REDELIVERY) {
            return START_STICKY_COMPATIBILITY;
        }

        byte action = intent.getByteExtra(Constants.Intent.Extra.OverlayAction.KEY, Constants.Intent.Extra.OverlayAction.DEFAULT);
        int source = startId;
        switch (action) {
            case Constants.Intent.Extra.OverlayAction.SHOW:
                Log.i(getClass().getName(), "Recived intent to show overlay from " + source);

                show();
                break;
            case Constants.Intent.Extra.OverlayAction.HIDE:
                Log.i(getClass().getName(), "Recived intent to hide overlay from " + source);

                hide();
                break;
            case Constants.Intent.Extra.OverlayAction.NOTHING:
                Log.i(getClass().getName(), "Recived intent to do nothing with the overlay from " + source);
                break;
        }

        return START_STICKY_COMPATIBILITY;
    }

    private void init() {
        getApplication().setTheme(R.style.AppTheme);

        view = new OverlayView(getApplicationContext(), new OverlayCancelListener() {
            @Override
            public void onCancel() {
                hide();
            }
        });
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

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    view.onCancelingStateChanged(System.currentTimeMillis());
                    return true;
                }else if (event.getAction() == MotionEvent.ACTION_UP) {
                    view.onCancelingStateChanged(-1L);
                    return true;
                }

                if(event.getPointerCount() >= 4) {
                    hide();
                    return true;
                }
                return true;
            }
        });

        view.setOnTouchListener(new OnSwipeListener(this, view.getHeight(), new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getPointerCount() >= 4) {
                    hide();
                    return true;
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        BasePX = event.getY();
                        view.SetYBorder(0);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        view.SetYBorder(BasePX - event.getY());
                        break;
                }

                return true;
            }
        }, new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        BasePX = 0;
                        view.setHiding(true);
                        break;
                }

                return true;
            }
        }, new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        BasePX = 0;
                        view.SetYBorder(0);
                        break;
                }

                return true;
            }
        }) {
            @Override
            public void onSwipeTop() {
                hide();
            }
        });
        view.setLayoutParams(
                new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                )
        );
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);

        view.setLayoutParams(layoutParams);
    }

    private void addView() {
        windowManager.addView(view, layoutParams);
        Log.i(getClass().getName(), "Successfully added view");
    }

    private void removeView() {
        try {
            windowManager.removeView(view);
            Log.i(getClass().getName(), "Successfully removed view");
        } catch (IllegalArgumentException e) {
            Log.e(getClass().getName(), "Failed removing view");
        }
    }

    private void show() {
        if (is_active_now) {
            return;
        }

        Intent closeIntent = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        sendBroadcast(closeIntent);

        addView();
        view.SetYBorder(0);
        if (view.getAlpha() == 0f) {
            view.animate().alpha(1f).setDuration(600).setListener(null).start();

            Log.i(getClass().getName(), "Successfully started blending to black");
        }
        writePref(true);
    }

    private void hide() {
        if (!is_active_now) {
            return;
        }

        if (view.getAlpha() == 1f) {
            view.animate().alpha(0f).setDuration(600).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    removeView();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            }).start();

            Log.i(getClass().getName(), "Successfully started blending to transparent");
        }
        writePref(false);
    }

    private void initializeNotification() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            OverlayNotification notification = new OverlayNotification(this);
            notification.drop();
        }
    }

    private void writePref(boolean value) {
        //getSharedPreferences(Constants.Preferences.PREFERENCE_NAME, Context.MODE_PRIVATE).edit().putBoolean(Constants.Preferences.IS_ACTIVE_NOW, value).apply();
        is_active_now = value;
        Log.i(getClass().getName(), "Successfully wrote preference to " + (value ? "true":"false"));
    }

    @Override
    public void onInterrupt() {
        hide();
    }

}
