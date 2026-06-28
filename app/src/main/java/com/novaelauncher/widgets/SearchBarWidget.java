package com.novaelauncher.widgets;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.cardview.widget.CardView;

import com.novaelauncher.R;

/**
 * Google-search-style search bar widget for the home screen.
 * Tapping it launches a voice/text search intent.
 * Styled like Oppo ColorOS search bar: rounded pill, frosted glass.
 */
public class SearchBarWidget extends LinearLayout {

    public SearchBarWidget(Context context) { super(context); init(); }
    public SearchBarWidget(Context context, AttributeSet attrs) { super(context, attrs); init(); }
    public SearchBarWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle); init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.widget_search_bar, this, true);

        // Clicking anywhere on the bar opens web search
        CardView card = findViewById(R.id.card_search_bar);
        card.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
            intent.putExtra("query", "");
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            try {
                getContext().startActivity(intent);
            } catch (Exception e) {
                // Fallback: open browser
                Intent browser = new Intent(Intent.ACTION_VIEW,
                    android.net.Uri.parse("https://www.google.com"));
                browser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(browser);
            }
        });
    }
}
