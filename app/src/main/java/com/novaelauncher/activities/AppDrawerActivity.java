package com.novaelauncher.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.novaelauncher.R;
import com.novaelauncher.adapters.AppDrawerAdapter;
import com.novaelauncher.models.AppInfo;
import com.novaelauncher.utils.AppLauncher;
import com.novaelauncher.utils.AppLoader;
import com.novaelauncher.utils.LauncherPreferences;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * App Drawer: full-screen grid of all installed apps.
 * Features:
 *  - Real-time search filtering
 *  - Sort: alphabetical / install date / usage
 *  - Long press context menu per app
 *  - Slide-up / slide-down animation when opening/closing
 *  - Frosted glass / blur background
 */
public class AppDrawerActivity extends AppCompatActivity
        implements AppDrawerAdapter.OnAppLongClickListener {

    private RecyclerView rvApps;
    private EditText etSearch;
    private ImageButton btnClose;
    private ImageButton btnSort;
    private TextView tvAppCount;

    private AppDrawerAdapter adapter;
    private List<AppInfo> allApps = new ArrayList<>();
    private LauncherPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Translucent + edge-to-edge
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        setContentView(R.layout.activity_app_drawer);

        prefs = LauncherPreferences.getInstance(this);
        initViews();
        loadApps();
    }

    private void initViews() {
        rvApps      = findViewById(R.id.rv_drawer_apps);
        etSearch    = findViewById(R.id.et_drawer_search);
        btnClose    = findViewById(R.id.btn_drawer_close);
        btnSort     = findViewById(R.id.btn_drawer_sort);
        tvAppCount  = findViewById(R.id.tv_app_count);

        // Grid layout
        int cols = prefs.getDrawerColumns();
        rvApps.setLayoutManager(new GridLayoutManager(this, cols));

        // Close button → slide down animation
        btnClose.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.no_anim, R.anim.slide_down_exit);
        });

        // Sort button
        btnSort.setOnClickListener(v -> showSortMenu(v));

        // Search
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int st, int c, int a) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (adapter != null) adapter.filter(s.toString());
            }
        });
    }

    private void loadApps() {
        new AppLoader(this).loadAppsAsync(apps -> {
            allApps = apps;
            adapter = new AppDrawerAdapter(this, apps);
            adapter.setOnAppLongClickListener(this);
            rvApps.setAdapter(adapter);
            tvAppCount.setText(apps.size() + " apps");
        });
    }

    private void showSortMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor);
        popup.getMenuInflater().inflate(R.menu.menu_drawer_sort, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.sort_alpha) {
                sortApps(0);
            } else if (id == R.id.sort_install) {
                sortApps(1);
            }
            return true;
        });
        popup.show();
    }

    private void sortApps(int mode) {
        prefs.setDrawerSort(mode);
        switch (mode) {
            case 0: // Alphabetical
                Collections.sort(allApps, (a, b) ->
                    a.getAppName().compareToIgnoreCase(b.getAppName()));
                break;
            case 1: // Install date
                Collections.sort(allApps, (a, b) ->
                    Long.compare(b.getInstallTime(), a.getInstallTime()));
                break;
        }
        adapter.updateApps(allApps);
    }

    @Override
    public void onAppLongClick(AppInfo app, View view) {
        PopupMenu popup = new PopupMenu(this, view);
        popup.getMenuInflater().inflate(R.menu.menu_icon_context, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.menu_app_info) {
                AppLauncher.openAppInfo(this, app.getPackageName());
                return true;
            } else if (id == R.id.menu_uninstall) {
                AppLauncher.uninstallApp(this, app.getPackageName());
                return true;
            }
            return false;
        });
        popup.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.no_anim, R.anim.slide_down_exit);
    }
}
