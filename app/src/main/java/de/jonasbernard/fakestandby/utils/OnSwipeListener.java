package android.jonas.fakestandby.utils;

import android.content.Context;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class OnSwipeListener implements OnTouchListener {

    private final GestureDetector gestureDetector;
    private final OnTouchListener parent;

    public OnSwipeListener (Context context, int height, OnTouchListener parent){
        gestureDetector = new GestureDetector(context, new GestureListener(height / 2));
        this.parent = parent;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        this.parent.onTouch(v, event);
        return gestureDetector.onTouchEvent(event);
    }

    private final class GestureListener extends SimpleOnGestureListener {

        private final int SWIPE_THRESHOLD;
        private final int SWIPE_VELOCITY_THRESHOLD = 1000;

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
                if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD && velocityY < 0) {
                    onSwipeTop(Math.abs(velocityY));
                    result = true;
                } else {
                    onSwipeFail();
                    result = false;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void onSwipeFail() {}

    public void onSwipeTop(float velocity) {}
}