package com.novaelauncher.activities;

import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.novaelauncher.R;
import com.novaelauncher.adapters.DockAdapter;
import com.novaelauncher.adapters.HomePageAdapter;
import com.novaelauncher.models.AppInfo;
import com.novaelauncher.utils.AppLauncher;
import com.novaelauncher.utils.AppLoader;
import com.novaelauncher.utils.GestureHelper;
import com.novaelauncher.utils.LauncherPreferences;
import com.novaelauncher.widgets.ClockWidget;

import java.util.ArrayList;
import java.util.List;

/**
 * THE MAIN LAUNCHER HOME SCREEN
 *
 * Architecture:
 *  - ViewPager2 for swipeable home pages
 *  - Each page uses GridLayout for icon grid
 *  - Bottom dock: horizontal RecyclerView
 *  - Top area: custom ClockWidget
 *  - Gesture handling: swipe up → drawer, swipe down → notifications
 *  - Long press on wallpaper → settings/customize menu
 */
public class HomeActivity extends AppCompatActivity implements
        GestureHelper.GestureCallback,
        HomePageAdapter.OnIconLongClickListener {

    // Views
    private ViewPager2 viewPagerHome;
    private RecyclerView rvDock;
    private LinearLayout dotsContainer;
    private ImageView ivWallpaper;
    private View viewDimOverlay;
    private ClockWidget clockWidget;

    // Adapters
    private HomePageAdapter pageAdapter;
    private DockAdapter dockAdapter;

    // State
    private List<AppInfo> allApps = new ArrayList<>();
    private List<AppInfo> dockApps = new ArrayList<>();
    private LauncherPreferences prefs;
    private GestureHelper gestureHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Edge-to-edge: launcher should draw behind system bars
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        );

        setContentView(R.layout.activity_home);

        prefs = LauncherPreferences.getInstance(this);
        initViews();
        setupGestures();
        loadApps();
    }

    // =====================================================
    //  INIT
    // =====================================================

    private void initViews() {
        viewPagerHome  = findViewById(R.id.vp_home);
        rvDock         = findViewById(R.id.rv_dock);
        dotsContainer  = findViewById(R.id.ll_dots);
        ivWallpaper    = findViewById(R.id.iv_wallpaper);
        viewDimOverlay = findViewById(R.id.view_dim_overlay);
        clockWidget    = findViewById(R.id.widget_clock);

        setupDock();
        applyWallpaper();
    }

    private void setupDock() {
        dockApps = new ArrayList<>();  // will be populated after apps load
        dockAdapter = new DockAdapter(this, dockApps);
        dockAdapter.setOnDockLongClickListener((app, view, position) ->
            showDockContextMenu(app, view, position)
        );
        rvDock.setLayoutManager(
            new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        );
        rvDock.setAdapter(dockAdapter);
    }

    @SuppressLint("MissingPermission")
    private void applyWallpaper() {
        try {
            WallpaperManager wm = WallpaperManager.getInstance(this);
            Drawable wallpaper = wm.getDrawable();

            if (wallpaper != null) {
                ivWallpaper.setImageDrawable(wallpaper);
            }
        } catch (Exception e) {
            ivWallpaper.setBackgroundResource(R.drawable.default_wallpaper);
        }

        float dimAlpha = prefs.getWallpaperDim() / 100f * 0.7f;
        viewDimOverlay.setAlpha(dimAlpha);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupGestures() {
        gestureHelper = new GestureHelper(this, this);
        viewPagerHome.setOnTouchListener((v, event) -> {
            // Let ViewPager2 handle horizontal, GestureHelper handles vertical + long press
            gestureHelper.onTouchEvent(event);
            return false;
        });
    }

    // =====================================================
    //  LOAD APPS
    // =====================================================

    private void loadApps() {
        new AppLoader(this).loadAppsAsync(apps -> {
            allApps = apps;
            setupHomePager();
            setupDefaultDock(apps);
        });
    }

    private void setupHomePager() {
        int rows = prefs.getGridRows();
        int cols = prefs.getGridColumns();
        // Home grid excludes dock apps for cleanliness
        List<AppInfo> homeApps = new ArrayList<>(allApps);
        // Remove first 5 for dock
        List<List<AppInfo>> pages = HomePageAdapter.distributeToPages(homeApps, rows, cols);

        pageAdapter = new HomePageAdapter(this, pages);
        pageAdapter.setOnIconLongClickListener(this);
        viewPagerHome.setAdapter(pageAdapter);
        viewPagerHome.setOffscreenPageLimit(2);

        // Page transition
        applyPageTransition();

        // Dots indicator
        setupPageDots(pages.size());
        viewPagerHome.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateDots(position);
            }
        });
    }

    private void setupDefaultDock(List<AppInfo> apps) {
        // First time: pick top 5 apps as dock
        List<AppInfo> dock = new ArrayList<>();
        int count = Math.min(prefs.getDockItemCount(), apps.size());
        for (int i = 0; i < count; i++) {
            dock.add(apps.get(i));
        }
        dockAdapter.setDockApps(dock);
    }

    // =====================================================
    //  PAGE DOTS
    // =====================================================

    private void setupPageDots(int count) {
        dotsContainer.removeAllViews();
        int sizePx = dpToPx(8);
        int marginPx = dpToPx(4);
        for (int i = 0; i < count; i++) {
            View dot = new View(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(sizePx, sizePx);
            lp.setMargins(marginPx, 0, marginPx, 0);
            dot.setLayoutParams(lp);
            dot.setBackgroundResource(i == 0 ?
                R.drawable.dot_active : R.drawable.dot_inactive);
            dotsContainer.addView(dot);
        }
    }

    private void updateDots(int selected) {
        for (int i = 0; i < dotsContainer.getChildCount(); i++) {
            View dot = dotsContainer.getChildAt(i);
            dot.setBackgroundResource(i == selected ?
                R.drawable.dot_active : R.drawable.dot_inactive);
        }
    }

    // =====================================================
    //  PAGE TRANSITIONS
    // =====================================================

    private void applyPageTransition() {
        String transition = prefs.getPageTransition();
        switch (transition) {
            case "cube":
                viewPagerHome.setPageTransformer(new CubePageTransformer());
                break;
            case "fade":
                viewPagerHome.setPageTransformer(new FadePageTransformer());
                break;
            case "flip":
                viewPagerHome.setPageTransformer(new FlipPageTransformer());
                break;
            default: // "scroll" — default ViewPager2 behavior
                viewPagerHome.setPageTransformer(null);
                break;
        }
    }

    // =====================================================
    //  GESTURE CALLBACKS
    // =====================================================

    @Override
    public void onSwipeUp() {
        // Open app drawer with slide-up animation
        Intent intent = new Intent(this, AppDrawerActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_up_enter, R.anim.no_anim);
    }

    @Override
    public void onSwipeDown() {
        AppLauncher.expandNotificationShade(this);
    }

    @Override
    public void onSwipeLeft() {
        // ViewPager2 handles this, but we can navigate programmatically if needed
    }

    @Override
    public void onSwipeRight() {
        // Optional: navigate to leftmost page
    }

    @Override
    public void onLongPress() {
        showWallpaperMenu();
    }

    @Override
    public void onDoubleTap() {
        // Optional: sleep screen or open search
    }

    // =====================================================
    //  CONTEXT MENUS
    // =====================================================

    @Override
    public void onIconLongClick(AppInfo appInfo, View iconView, int pageIndex, int iconIndex) {
        PopupMenu popup = new PopupMenu(this, iconView);
        popup.getMenuInflater().inflate(R.menu.menu_icon_context, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_app_info) {
                AppLauncher.openAppInfo(this, appInfo.getPackageName());
                return true;
            } else if (id == R.id.menu_uninstall) {
                AppLauncher.uninstallApp(this, appInfo.getPackageName());
                return true;
            } else if (id == R.id.menu_add_to_dock) {
                dockAdapter.addApp(appInfo);
                Toast.makeText(this, appInfo.getAppName() + " added to dock",
                    Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void showDockContextMenu(AppInfo app, View view, int position) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.menu_dock_context, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_remove_from_dock) {
                dockAdapter.removeApp(position);
                return true;
            } else if (id == R.id.menu_app_info) {
                AppLauncher.openAppInfo(this, app.getPackageName());
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void showWallpaperMenu() {
        // Long press on home → customize options
        View anchor = findViewById(R.id.view_dim_overlay);
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.menu_home_long_press, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_change_wallpaper) {
                startActivity(new Intent(this, WallpaperPickerActivity.class));
                return true;
            } else if (id == R.id.menu_launcher_settings) {
                startActivity(new Intent(this, LauncherSettingsActivity.class));
                return true;
            } else if (id == R.id.menu_add_widget) {
                startActivity(new Intent(this, WidgetPickerActivity.class));
                return true;
            }
            return false;
        });
        popup.show();
    }

    // =====================================================
    //  LIFECYCLE
    // =====================================================

    @Override
    protected void onResume() {
        super.onResume();
        applyWallpaper();
        // Refresh apps if a new app was installed while we were away
        new AppLoader(this).loadAppsAsync(apps -> {
            if (apps.size() != allApps.size()) {
                allApps = apps;
                setupHomePager();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Back on home: go to page 0
        if (viewPagerHome.getCurrentItem() != 0) {
            viewPagerHome.setCurrentItem(0, true);
        }
        // Don't call super — we never want to "exit" the launcher
    }

    // =====================================================
    //  INNER CLASSES: Page Transformers
    // =====================================================

    static class CubePageTransformer implements ViewPager2.PageTransformer {
        @Override
        public void transformPage(View page, float position) {
            page.setPivotX(position < 0 ? page.getWidth() : 0);
            page.setPivotY(page.getHeight() / 2f);
            page.setRotationY(90f * position);
        }
    }

    static class FadePageTransformer implements ViewPager2.PageTransformer {
        @Override
        public void transformPage(View page, float position) {
            page.setAlpha(1 - Math.abs(position));
            page.setTranslationX(-position * page.getWidth());
        }
    }

    static class FlipPageTransformer implements ViewPager2.PageTransformer {
        @Override
        public void transformPage(View page, float position) {
            float rotation = 180f * position;
            page.setRotationY(rotation);
            page.setAlpha(Math.abs(position) < 0.5f ? 1f : 0f);
        }
    }

    // =====================================================
    //  HELPERS
    // =====================================================

    private int dpToPx(int dp) {
        float density = getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
