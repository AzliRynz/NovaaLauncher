package com.novaelauncher.widgets;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.novaelauncher.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Custom clock widget shown on the home screen.
 * Displays time + date, updates every minute via BroadcastReceiver.
 * Style inspired by Infinix/Oppo: large time, subtle date below.
 */
public class ClockWidget extends LinearLayout {

    private TextView tvTime;
    private TextView tvDate;
    private TextView tvGreeting;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private BroadcastReceiver tickReceiver;

    private final SimpleDateFormat timeFmt = new SimpleDateFormat("hh:mm", Locale.getDefault());
    private final SimpleDateFormat amPmFmt = new SimpleDateFormat("a", Locale.getDefault());
    private final SimpleDateFormat dateFmt  = new SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault());

    public ClockWidget(Context context) { super(context); init(); }
    public ClockWidget(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    public ClockWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle); init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_clock, this, true);
        tvTime     = findViewById(R.id.tv_clock_time);
        tvDate     = findViewById(R.id.tv_clock_date);
        tvGreeting = findViewById(R.id.tv_clock_greeting);
        updateClock();
    }

    private void updateClock() {
        Date now = new Date();
        String time   = timeFmt.format(now);
        String amPm   = amPmFmt.format(now);
        String date   = dateFmt.format(now);

        tvTime.setText(time + " " + amPm);
        tvDate.setText(date);
        tvGreeting.setText(getGreeting());
    }

    private String getGreeting() {
        int hour = new java.util.Calendar.Builder().build().get(java.util.Calendar.HOUR_OF_DAY);
        java.util.Calendar cal = java.util.Calendar.getInstance();
        hour = cal.get(java.util.Calendar.HOUR_OF_DAY);
        if (hour >= 5 && hour < 12)  return "Selamat Pagi ☀️";
        if (hour >= 12 && hour < 15) return "Selamat Siang 🌤";
        if (hour >= 15 && hour < 18) return "Selamat Sore 🌇";
        return "Selamat Malam 🌙";
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        tickReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateClock();
            }
        };
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        getContext().registerReceiver(tickReceiver, filter);
        updateClock();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (tickReceiver != null) {
            try { getContext().unregisterReceiver(tickReceiver); } catch (Exception ignored) {}
        }
        handler.removeCallbacksAndMessages(null);
    }
}
