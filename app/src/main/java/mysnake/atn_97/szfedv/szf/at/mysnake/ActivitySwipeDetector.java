package mysnake.atn_97.szfedv.szf.at.mysnake;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

/**
 * Created by ATN_97 on 10.02.2016.
 */
public class ActivitySwipeDetector implements OnTouchListener {
    static final String LOG_TAG = ActivitySwipeDetector.class.getSimpleName();
    private SwipeInterface activity;
    static final int MIN_SWIPE_DISTANCE = 100;
    static final int MAX_CLICK_TOLERANCE = 50;
    private float downX;
    private float downY;

    public ActivitySwipeDetector(SwipeInterface activity) { this.activity = activity; }

    public void onRightToLeftSwipe(View v) {
        Log.v(LOG_TAG, "onRightToLeftSwipe()");
        activity.right2left(v);
    }

    public void onLeftToRightSwipe(View v) {
        Log.v(LOG_TAG, "onLeftToRightSwipe()");
        activity.left2right(v);
    }

    public void onTopToBottomSwipe(View v) {
        Log.v(LOG_TAG, "onTopToBottomSwipe()");
        activity.top2bottom(v);
    }

    public void onBottomToTopSwipe(View v) {
        Log.v(LOG_TAG, "onBottomToTopSwipe()");
        activity.bottom2top(v);
    }

    private void onClick(View v, int x, int y) {
        Log.v(LOG_TAG, "onClick()");
        activity.onClick(v, x, y);
    }

    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                downX = event.getX();
                downY = event.getY();
                Log.d(LOG_TAG, "downX: " + downX + " | downY: " + downY);
                return true;
            case MotionEvent.ACTION_UP:
                float upX = event.getX();
                float upY = event.getY();

                float deltaX = downX - upX;
                float deltaY = downY - upY;

                // click?
                if (Math.abs(deltaX) <= MAX_CLICK_TOLERANCE && Math.abs(deltaY) <= MAX_CLICK_TOLERANCE) {
                    this.onClick(v, (int) upX, (int) upY);
                    return true;
                }

                // swipe horizontal
                if (Math.abs(deltaX) > MIN_SWIPE_DISTANCE) {
                    // top or down
                    if (deltaX < 0) {
                        this.onLeftToRightSwipe(v);
                        return true;
                    }
                    if (deltaX > 0) {
                        this.onRightToLeftSwipe(v);
                        return true;
                    }
                } else {
                    Log.i(LOG_TAG, "Horizontal swipe was only " + Math.abs(deltaX) + " long, need at least " + MIN_SWIPE_DISTANCE);
                }

                // swipe vertical
                if (Math.abs(deltaY) > MIN_SWIPE_DISTANCE) {
                    // top or down
                    if (deltaY < 0) {
                        this.onTopToBottomSwipe(v);
                        return true;
                    }
                    if (deltaY > 0) {
                        this.onBottomToTopSwipe(v);
                        return true;
                    }
                } else {
                    Log.i(LOG_TAG, "Vertical swipe was only " + Math.abs(deltaX) + ", need at last " + MIN_SWIPE_DISTANCE);
                }
        }
        return false;
    }
}
