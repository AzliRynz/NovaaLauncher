package com.novaelauncher.utils;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.MotionEvent;

/**
 * Handles swipe gestures on the home screen:
 *  - Swipe Up   → Open App Drawer
 *  - Swipe Down → Pull down notification shade
 *  - Long press → Enter edit mode / show wallpaper menu
 *  - Double tap → Optional action
 */
public class GestureHelper extends GestureDetector.SimpleOnGestureListener {

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;

    public interface GestureCallback {
        void onSwipeUp();
        void onSwipeDown();
        void onSwipeLeft();
        void onSwipeRight();
        void onLongPress();
        void onDoubleTap();
    }

    private final GestureCallback callback;
    private final GestureDetector detector;
    private final Vibrator vibrator;
    private final LauncherPreferences prefs;

    public GestureHelper(Context context, GestureCallback callback) {
        this.callback = callback;
        this.detector = new GestureDetector(context, this);
        this.detector.setIsLongpressEnabled(true);
        this.vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        this.prefs = LauncherPreferences.getInstance(context);
    }

    public boolean onTouchEvent(MotionEvent event) {
        return detector.onTouchEvent(event);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2,
                           float velocityX, float velocityY) {
        if (e1 == null || e2 == null) return false;

        float dX = e2.getX() - e1.getX();
        float dY = e2.getY() - e1.getY();
        float absX = Math.abs(dX);
        float absY = Math.abs(dY);

        if (absY > absX) {
            // Vertical swipe
            if (absY > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (dY < 0) {
                    callback.onSwipeUp();
                } else {
                    callback.onSwipeDown();
                }
                return true;
            }
        } else {
            // Horizontal swipe (handled by ViewPager2, but we intercept extremes)
            if (absX > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                if (dX < 0) {
                    callback.onSwipeLeft();
                } else {
                    callback.onSwipeRight();
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        hapticFeedback(50);
        callback.onLongPress();
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        callback.onDoubleTap();
        return true;
    }

    private void hapticFeedback(long ms) {
        if (prefs.isScrollHaptic() && vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(VibrationEffect.createOneShot(ms,
                VibrationEffect.DEFAULT_AMPLITUDE));
        }
    }
}
