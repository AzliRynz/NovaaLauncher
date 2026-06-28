package com.novaelauncher.activities;

import android.os.Bundle;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.novaelauncher.R;
import com.novaelauncher.utils.LauncherPreferences;

/**
 * Settings screen where users customize the launcher.
 * Covers grid size, icons, dock, wallpaper effects, gestures, transitions.
 */
public class LauncherSettingsActivity extends AppCompatActivity {

    private LauncherPreferences prefs;

    // Grid
    private SeekBar sbGridColumns, sbGridRows;
    private TextView tvColumnsVal, tvRowsVal;

    // Icons
    private SeekBar sbIconSize;
    private TextView tvIconSizeVal;
    private Switch swIconLabel;
    private RadioGroup rgIconShape;

    // Dock
    private Switch swDockEnabled;
    private Switch swDockBg;
    private SeekBar sbDockItems;
    private TextView tvDockItemsVal;

    // Wallpaper effects
    private SeekBar sbWallpaperDim;
    private TextView tvDimVal;

    // Transitions
    private RadioGroup rgTransition;

    // Gestures
    private RadioGroup rgSwipeUp;
    private RadioGroup rgSwipeDown;
    private Switch swHaptic;

    // Other
    private Switch swStatusBarHide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        prefs = LauncherPreferences.getInstance(this);

        Toolbar toolbar = findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(R.string.launcher_settings);
        }

        initViews();
        loadCurrentSettings();
        attachListeners();
    }

    private void initViews() {
        sbGridColumns  = findViewById(R.id.sb_grid_columns);
        sbGridRows     = findViewById(R.id.sb_grid_rows);
        tvColumnsVal   = findViewById(R.id.tv_columns_val);
        tvRowsVal      = findViewById(R.id.tv_rows_val);

        sbIconSize     = findViewById(R.id.sb_icon_size);
        tvIconSizeVal  = findViewById(R.id.tv_icon_size_val);
        swIconLabel    = findViewById(R.id.sw_icon_label);
        rgIconShape    = findViewById(R.id.rg_icon_shape);

        swDockEnabled  = findViewById(R.id.sw_dock_enabled);
        swDockBg       = findViewById(R.id.sw_dock_bg);
        sbDockItems    = findViewById(R.id.sb_dock_items);
        tvDockItemsVal = findViewById(R.id.tv_dock_items_val);

        sbWallpaperDim = findViewById(R.id.sb_wallpaper_dim);
        tvDimVal       = findViewById(R.id.tv_dim_val);

        rgTransition   = findViewById(R.id.rg_transition);
        rgSwipeUp      = findViewById(R.id.rg_swipe_up);
        rgSwipeDown    = findViewById(R.id.rg_swipe_down);
        swHaptic       = findViewById(R.id.sw_haptic);
        swStatusBarHide= findViewById(R.id.sw_statusbar_hide);
    }

    private void loadCurrentSettings() {
        // Grid
        sbGridColumns.setProgress(prefs.getGridColumns() - 3);
        sbGridRows.setProgress(prefs.getGridRows() - 3);
        tvColumnsVal.setText(String.valueOf(prefs.getGridColumns()));
        tvRowsVal.setText(String.valueOf(prefs.getGridRows()));

        // Icons
        sbIconSize.setProgress(prefs.getIconSize() - 40);
        tvIconSizeVal.setText(prefs.getIconSize() + "dp");
        swIconLabel.setChecked(prefs.isIconLabelVisible());

        // Dock
        swDockEnabled.setChecked(prefs.isDockEnabled());
        swDockBg.setChecked(prefs.isDockBgVisible());
        sbDockItems.setProgress(prefs.getDockItemCount() - 3);
        tvDockItemsVal.setText(String.valueOf(prefs.getDockItemCount()));

        // Wallpaper
        sbWallpaperDim.setProgress(prefs.getWallpaperDim());
        tvDimVal.setText(prefs.getWallpaperDim() + "%");

        // Haptic
        swHaptic.setChecked(prefs.isScrollHaptic());
        swStatusBarHide.setChecked(prefs.isStatusBarHidden());
    }

    private void attachListeners() {
        // Grid columns: 3–6
        sbGridColumns.setOnSeekBarChangeListener(new SimpleSeekBarListener() {
            @Override public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                int val = progress + 3;
                prefs.setGridColumns(val);
                tvColumnsVal.setText(String.valueOf(val));
            }
        });

        sbGridRows.setOnSeekBarChangeListener(new SimpleSeekBarListener() {
            @Override public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                int val = progress + 3;
                prefs.setGridRows(val);
                tvRowsVal.setText(String.valueOf(val));
            }
        });

        // Icon size: 40–80dp
        sbIconSize.setOnSeekBarChangeListener(new SimpleSeekBarListener() {
            @Override public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                int val = progress + 40;
                prefs.setIconSize(val);
                tvIconSizeVal.setText(val + "dp");
            }
        });

        swIconLabel.setOnCheckedChangeListener((btn, checked) ->
            prefs.setIconLabelVisible(checked));

        // Dock items: 3–7
        sbDockItems.setOnSeekBarChangeListener(new SimpleSeekBarListener() {
            @Override public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                int val = progress + 3;
                prefs.setDockItemCount(val);
                tvDockItemsVal.setText(String.valueOf(val));
            }
        });

        swDockEnabled.setOnCheckedChangeListener((btn, checked) ->
            prefs.setDockEnabled(checked));
        swDockBg.setOnCheckedChangeListener((btn, checked) ->
            prefs.setDockBgVisible(checked));

        // Wallpaper dim: 0–70%
        sbWallpaperDim.setOnSeekBarChangeListener(new SimpleSeekBarListener() {
            @Override public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                prefs.setWallpaperDim(progress);
                tvDimVal.setText(progress + "%");
            }
        });

        // Transition
        rgTransition.setOnCheckedChangeListener((rg, id) -> {
            if (id == R.id.rb_transition_scroll)       prefs.setPageTransition("scroll");
            else if (id == R.id.rb_transition_cube)    prefs.setPageTransition("cube");
            else if (id == R.id.rb_transition_fade)    prefs.setPageTransition("fade");
            else if (id == R.id.rb_transition_flip)    prefs.setPageTransition("flip");
        });

        swHaptic.setOnCheckedChangeListener((btn, checked) ->
            prefs.setScrollHaptic(checked));
        swStatusBarHide.setOnCheckedChangeListener((btn, checked) ->
            prefs.setStatusBarHidden(checked));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    // Simple helper to avoid implementing all 3 abstract methods everywhere
    abstract static class SimpleSeekBarListener implements SeekBar.OnSeekBarChangeListener {
        @Override public void onStartTrackingTouch(SeekBar sb) {}
        @Override public void onStopTrackingTouch(SeekBar sb) {}
    }
}
