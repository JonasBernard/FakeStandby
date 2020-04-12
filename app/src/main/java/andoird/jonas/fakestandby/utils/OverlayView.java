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

    private OverlayCancelListener overlayCancelListener = null;

    private final int MAXRADUIS = 100; //px
    private final long CANCELTHRESHOLD = 1000; //milliseconds

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
            yBorder += 10;
            invalidate();
            return;
        }

        if (this.timeToCancel > 0L && this.downtime > 0L) {
            long elapsed = System.currentTimeMillis() - downtime;
            double q = (double)(elapsed*1000) / (double)(CANCELTHRESHOLD*1000);

            if (q > 1) {
                this.downtime = -1L;
                this.timeToCancel = -1L;
                overlayCancelListener.onCancel();
                return;
            }

            Paint white = new Paint();
            white.setColor(Color.WHITE);

            canvas.drawCircle(width / 2, height / 2, (float) (MAXRADUIS * q), white);
            invalidate();
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
        invalidate();
    }

    public void setHiding(boolean hiding) {
        this.hiding = hiding;
        invalidate();
    }

}
