package com.novaelauncher.activities;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.novaelauncher.R;

/**
 * Widget Picker that uses Android's built-in AppWidget system.
 * Flow:
 *  1. Bind AppWidgetHost
 *  2. Launch system widget picker
 *  3. If widget needs config → launch config activity
 *  4. Return widget ID to HomeActivity to embed
 */
public class WidgetPickerActivity extends AppCompatActivity {

    private static final int REQUEST_PICK_WIDGET  = 1001;
    private static final int REQUEST_BIND_WIDGET  = 1002;
    private static final int REQUEST_CONFIG_WIDGET = 1003;
    private static final int APPWIDGET_HOST_ID     = 1024;

    private AppWidgetManager appWidgetManager;
    private AppWidgetHost appWidgetHost;
    private int pendingWidgetId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_widget_picker);

        Toolbar toolbar = findViewById(R.id.toolbar_widget);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Add Widget");
        }

        appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetHost = new AppWidgetHost(this, APPWIDGET_HOST_ID);
        appWidgetHost.startListening();

        launchWidgetPicker();
    }

    private void launchWidgetPicker() {
        pendingWidgetId = appWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, pendingWidgetId);
        startActivityForResult(pickIntent, REQUEST_PICK_WIDGET);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            // User cancelled
            if (pendingWidgetId != -1) {
                appWidgetHost.deleteAppWidgetId(pendingWidgetId);
            }
            finish();
            return;
        }

        if (requestCode == REQUEST_PICK_WIDGET) {
            int widgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            AppWidgetProviderInfo info = appWidgetManager.getAppWidgetInfo(widgetId);

            if (info != null && info.configure != null) {
                // Widget needs configuration
                Intent configIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
                configIntent.setComponent(info.configure);
                configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
                startActivityForResult(configIntent, REQUEST_CONFIG_WIDGET);
            } else {
                // No config needed: add widget directly
                addWidgetToHome(widgetId);
            }

        } else if (requestCode == REQUEST_CONFIG_WIDGET) {
            int widgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            addWidgetToHome(widgetId);
        }
    }

    private void addWidgetToHome(int widgetId) {
        // Return widget ID to HomeActivity
        Intent result = new Intent();
        result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        setResult(RESULT_OK, result);
        Toast.makeText(this, "Widget added!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        appWidgetHost.stopListening();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
