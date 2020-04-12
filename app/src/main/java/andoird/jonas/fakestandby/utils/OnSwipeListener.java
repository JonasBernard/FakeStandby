package andoird.jonas.fakestandby.utils;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class OnSwipeListener implements OnTouchListener {

    private final GestureDetector gestureDetector;
    private final OnTouchListener parent, success, fail;

    public OnSwipeListener (Context context, int height, OnTouchListener parent, OnTouchListener success, OnTouchListener fail){
        gestureDetector = new GestureDetector(context, new GestureListener(height / 2));
        this.parent = parent;
        this.success = success;
        this.fail = fail;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        this.parent.onTouch(v, event);
        boolean result = gestureDetector.onTouchEvent(event);
        if (result) {
            this.success.onTouch(v, event);
        }else {
            this.fail.onTouch(v, event);
        }
        return result;
    }

    private final class GestureListener extends SimpleOnGestureListener {

        private final int SWIPE_THRESHOLD;
        private final int SWIPE_VELOCITY_THRESHOLD = 500;

        public GestureListener(int SWIPE_THRESHOLD) {
            this.SWIPE_THRESHOLD = SWIPE_THRESHOLD;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                        result = true;
                    }
                }
                else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                    result = true;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void onSwipeRight() {}

    public void onSwipeLeft() {}

    public void onSwipeTop() {}

    public void onSwipeBottom() {}
}