package andoird.jonas.fakestandby.utils;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

public class OverlayView extends View {
    private long downtime = -1L;
    private long timeToCancel = -1L;
    private float yBorder = 0;

    private boolean hiding = false;
    private float hidingVelocity = 40;

    private boolean bouncing = false;
    private float maxY = 0;
    private int direction = 1;

    private OverlayCancelListener overlayCancelListener = null;

    private final int MAXRADUIS = 100; //px
    private final long CANCELTHRESHOLD = 1000; //milliseconds
    private final int BOUNCESTOP = 10; //px

    public OverlayView(Context context, OverlayCancelListener overlayCancelListener) {
        super(context);
        this.overlayCancelListener = overlayCancelListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();
        //canvas.drawColor(Color.BLACK);

        canvas.drawRect(0,0, (float) width, height-yBorder, new Paint());

        if (hiding) {
            yBorder += hidingVelocity;
            invalidate();
            return;
        }

        Paint white = new Paint();
        white.setColor(Color.WHITE);

        if (bouncing) {
            if (maxY < BOUNCESTOP) {
                bouncing = false;
                yBorder = 0;
                invalidate();
                return;
            }

            if (yBorder > maxY) {
                direction = -1;
                yBorder = maxY-2;
            }else if (yBorder < 0) {
                direction = 1;
                yBorder = 2;
                maxY /= 2;
            }else {
                yBorder += direction*40;
            }
            invalidate();
            return;
        }
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    public long onCancelingStateChanged(long downtime) {
        this.downtime = downtime;
        if (downtime < 0) {
            this.timeToCancel = -1L;
        } else {
            this.timeToCancel = downtime + CANCELTHRESHOLD;
        }

        invalidate();
        return timeToCancel;
    }

    public void SetYBorder(float Y) {
        this.yBorder = Y;
        this.hiding = false;
        this.bouncing = false;
        invalidate();
    }

    public void setHiding(boolean hiding) {
        this.bouncing = false;

        this.hiding = hiding;
        invalidate();
    }

    public void setBouncing(boolean bouncing, int height) {
        this.hiding = false;

        this.bouncing = bouncing;
        this.maxY = height;
        invalidate();
    }

    public void setHidingVelocity(float velocity) {
        this.hidingVelocity = velocity;
    }

}
