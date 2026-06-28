package com.novaelauncher.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * Side scrollbar showing A–Z letters for the app drawer.
 * User can drag finger to quickly jump to a letter section.
 * Draws itself: a vertical pill with letters, highlights the touched one.
 *
 * Callback: onLetterSelected(char) → caller should scroll RecyclerView.
 */
public class AlphabetSidebarView extends View {

    public interface OnLetterSelectedListener {
        void onLetterSelected(char letter);
    }

    private static final char[] LETTERS = {
        '#','A','B','C','D','E','F','G','H','I','J','K','L','M',
        'N','O','P','Q','R','S','T','U','V','W','X','Y','Z'
    };

    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint bgPaint   = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint activePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private OnLetterSelectedListener listener;
    private int selectedIndex = -1;
    private float itemHeight;

    public AlphabetSidebarView(Context context) { super(context); init(); }
    public AlphabetSidebarView(Context context, AttributeSet attrs) { super(context, attrs); init(); }

    private void init() {
        textPaint.setColor(Color.WHITE);
        textPaint.setTextSize(dpToPx(11));
        textPaint.setTextAlign(Paint.Align.CENTER);

        bgPaint.setColor(0x33FFFFFF);     // translucent white pill
        bgPaint.setStyle(Paint.Style.FILL);

        activePaint.setColor(0xFF6C63FF); // accent color for selected letter
        activePaint.setStyle(Paint.Style.FILL);
    }

    public void setOnLetterSelectedListener(OnLetterSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int w = getWidth();
        int h = getHeight();
        itemHeight = (float) h / LETTERS.length;

        // Draw background pill
        float radius = w / 2f;
        canvas.drawRoundRect(new RectF(0, 0, w, h), radius, radius, bgPaint);

        // Draw letters
        float cx = w / 2f;
        for (int i = 0; i < LETTERS.length; i++) {
            float cy = i * itemHeight + itemHeight / 2f;

            if (i == selectedIndex) {
                // Highlight circle
                canvas.drawCircle(cx, cy, itemHeight / 2f, activePaint);
                textPaint.setColor(Color.WHITE);
                textPaint.setTextSize(dpToPx(12));
            } else {
                textPaint.setColor(0xCCFFFFFF);
                textPaint.setTextSize(dpToPx(10));
            }

            // Center text vertically
            float textY = cy - (textPaint.descent() + textPaint.ascent()) / 2f;
            canvas.drawText(String.valueOf(LETTERS[i]), cx, textY, textPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float y = event.getY();
        int index = (int) (y / (getHeight() / (float) LETTERS.length));
        index = Math.max(0, Math.min(index, LETTERS.length - 1));

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                if (index != selectedIndex) {
                    selectedIndex = index;
                    invalidate();
                    if (listener != null) {
                        listener.onLetterSelected(LETTERS[index]);
                    }
                }
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                selectedIndex = -1;
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    private float dpToPx(float dp) {
        return dp * getContext().getResources().getDisplayMetrics().density;
    }
}
