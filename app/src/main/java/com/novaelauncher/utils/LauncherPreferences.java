package com.novaelauncher.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Manages all launcher settings using SharedPreferences.
 * Handles grid size, icon style, theme, transition effects, etc.
 */
public class LauncherPreferences {

    private static final String PREFS_NAME = "novaa_launcher_prefs";

    // Grid
    private static final String KEY_GRID_COLUMNS = "grid_columns";
    private static final String KEY_GRID_ROWS = "grid_rows";
    // Theme
    private static final String KEY_THEME_MODE = "theme_mode"; // 0=auto,1=light,2=dark
    private static final String KEY_ACCENT_COLOR = "accent_color";
    private static final String KEY_ICON_PACK = "icon_pack";
    private static final String KEY_ICON_SIZE = "icon_size"; // dp
    private static final String KEY_ICON_LABEL_VISIBLE = "icon_label_visible";
    private static final String KEY_ICON_SHAPE = "icon_shape"; // circle, squircle, square, adaptive
    // Wallpaper
    private static final String KEY_WALLPAPER_BLUR = "wallpaper_blur";
    private static final String KEY_WALLPAPER_DIM = "wallpaper_dim";
    // Drawer
    private static final String KEY_DRAWER_BLUR_BG = "drawer_blur_bg";
    private static final String KEY_DRAWER_SORT = "drawer_sort"; // 0=alpha, 1=install, 2=usage
    private static final String KEY_DRAWER_COLUMNS = "drawer_columns";
    // Effects
    private static final String KEY_PAGE_TRANSITION = "page_transition"; // scroll, cube, fade, flip
    private static final String KEY_SCROLL_HAPTIC = "scroll_haptic";
    private static final String KEY_SWIPE_UP_ACTION = "swipe_up_action"; // 0=drawer, 1=search
    private static final String KEY_SWIPE_DOWN_ACTION = "swipe_down_action"; // 0=notif, 1=search
    // Dock
    private static final String KEY_DOCK_ENABLED = "dock_enabled";
    private static final String KEY_DOCK_BG_VISIBLE = "dock_bg_visible";
    private static final String KEY_DOCK_ITEM_COUNT = "dock_item_count";
    // Status bar
    private static final String KEY_STATUS_BAR_HIDDEN = "status_bar_hidden";
    // First run
    private static final String KEY_FIRST_RUN = "first_run";

    private final SharedPreferences prefs;
    private static LauncherPreferences instance;

    private LauncherPreferences(Context context) {
        prefs = context.getApplicationContext()
                       .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static LauncherPreferences getInstance(Context context) {
        if (instance == null) {
            instance = new LauncherPreferences(context);
        }
        return instance;
    }

    // ======= GRID =======
    public int getGridColumns() { return prefs.getInt(KEY_GRID_COLUMNS, 4); }
    public void setGridColumns(int v) { prefs.edit().putInt(KEY_GRID_COLUMNS, v).apply(); }
    public int getGridRows() { return prefs.getInt(KEY_GRID_ROWS, 5); }
    public void setGridRows(int v) { prefs.edit().putInt(KEY_GRID_ROWS, v).apply(); }

    // ======= THEME =======
    public int getThemeMode() { return prefs.getInt(KEY_THEME_MODE, 0); }
    public void setThemeMode(int v) { prefs.edit().putInt(KEY_THEME_MODE, v).apply(); }
    public int getAccentColor() { return prefs.getInt(KEY_ACCENT_COLOR, 0xFF6C63FF); }
    public void setAccentColor(int v) { prefs.edit().putInt(KEY_ACCENT_COLOR, v).apply(); }
    public String getIconPack() { return prefs.getString(KEY_ICON_PACK, ""); }
    public void setIconPack(String v) { prefs.edit().putString(KEY_ICON_PACK, v).apply(); }
    public int getIconSize() { return prefs.getInt(KEY_ICON_SIZE, 56); }
    public void setIconSize(int v) { prefs.edit().putInt(KEY_ICON_SIZE, v).apply(); }
    public boolean isIconLabelVisible() { return prefs.getBoolean(KEY_ICON_LABEL_VISIBLE, true); }
    public void setIconLabelVisible(boolean v) { prefs.edit().putBoolean(KEY_ICON_LABEL_VISIBLE, v).apply(); }
    public String getIconShape() { return prefs.getString(KEY_ICON_SHAPE, "squircle"); }
    public void setIconShape(String v) { prefs.edit().putString(KEY_ICON_SHAPE, v).apply(); }

    // ======= WALLPAPER =======
    public int getWallpaperBlur() { return prefs.getInt(KEY_WALLPAPER_BLUR, 0); }
    public void setWallpaperBlur(int v) { prefs.edit().putInt(KEY_WALLPAPER_BLUR, v).apply(); }
    public int getWallpaperDim() { return prefs.getInt(KEY_WALLPAPER_DIM, 0); }
    public void setWallpaperDim(int v) { prefs.edit().putInt(KEY_WALLPAPER_DIM, v).apply(); }

    // ======= DRAWER =======
    public boolean isDrawerBlurBg() { return prefs.getBoolean(KEY_DRAWER_BLUR_BG, true); }
    public void setDrawerBlurBg(boolean v) { prefs.edit().putBoolean(KEY_DRAWER_BLUR_BG, v).apply(); }
    public int getDrawerSort() { return prefs.getInt(KEY_DRAWER_SORT, 0); }
    public void setDrawerSort(int v) { prefs.edit().putInt(KEY_DRAWER_SORT, v).apply(); }
    public int getDrawerColumns() { return prefs.getInt(KEY_DRAWER_COLUMNS, 4); }
    public void setDrawerColumns(int v) { prefs.edit().putInt(KEY_DRAWER_COLUMNS, v).apply(); }

    // ======= EFFECTS =======
    public String getPageTransition() { return prefs.getString(KEY_PAGE_TRANSITION, "scroll"); }
    public void setPageTransition(String v) { prefs.edit().putString(KEY_PAGE_TRANSITION, v).apply(); }
    public boolean isScrollHaptic() { return prefs.getBoolean(KEY_SCROLL_HAPTIC, true); }
    public void setScrollHaptic(boolean v) { prefs.edit().putBoolean(KEY_SCROLL_HAPTIC, v).apply(); }
    public int getSwipeUpAction() { return prefs.getInt(KEY_SWIPE_UP_ACTION, 0); }
    public void setSwipeUpAction(int v) { prefs.edit().putInt(KEY_SWIPE_UP_ACTION, v).apply(); }
    public int getSwipeDownAction() { return prefs.getInt(KEY_SWIPE_DOWN_ACTION, 0); }
    public void setSwipeDownAction(int v) { prefs.edit().putInt(KEY_SWIPE_DOWN_ACTION, v).apply(); }

    // ======= DOCK =======
    public boolean isDockEnabled() { return prefs.getBoolean(KEY_DOCK_ENABLED, true); }
    public void setDockEnabled(boolean v) { prefs.edit().putBoolean(KEY_DOCK_ENABLED, v).apply(); }
    public boolean isDockBgVisible() { return prefs.getBoolean(KEY_DOCK_BG_VISIBLE, true); }
    public void setDockBgVisible(boolean v) { prefs.edit().putBoolean(KEY_DOCK_BG_VISIBLE, v).apply(); }
    public int getDockItemCount() { return prefs.getInt(KEY_DOCK_ITEM_COUNT, 5); }
    public void setDockItemCount(int v) { prefs.edit().putInt(KEY_DOCK_ITEM_COUNT, v).apply(); }

    // ======= STATUS BAR =======
    public boolean isStatusBarHidden() { return prefs.getBoolean(KEY_STATUS_BAR_HIDDEN, false); }
    public void setStatusBarHidden(boolean v) { prefs.edit().putBoolean(KEY_STATUS_BAR_HIDDEN, v).apply(); }

    // ======= FIRST RUN =======
    public boolean isFirstRun() { return prefs.getBoolean(KEY_FIRST_RUN, true); }
    public void setFirstRun(boolean v) { prefs.edit().putBoolean(KEY_FIRST_RUN, v).apply(); }
}
