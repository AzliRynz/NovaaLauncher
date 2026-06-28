package com.novaelauncher.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.novaelauncher.models.AppInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Persists and restores the home screen layout:
 *  - Which apps are on which page and grid position
 *  - Dock app order
 *  - Folder contents
 *
 * Data stored as JSON in SharedPreferences.
 */
public class HomeDataManager {

    private static final String PREFS_HOME = "novaa_home_layout";
    private static final String KEY_HOME_PAGES = "home_pages";
    private static final String KEY_DOCK_APPS  = "dock_apps";

    private final SharedPreferences prefs;

    public HomeDataManager(Context context) {
        prefs = context.getApplicationContext()
                       .getSharedPreferences(PREFS_HOME, Context.MODE_PRIVATE);
    }

    // ======= DOCK =======

    /**
     * Save dock app package names in order.
     */
    public void saveDockApps(List<AppInfo> dockApps) {
        JSONArray arr = new JSONArray();
        for (AppInfo app : dockApps) {
            arr.put(app.getPackageName());
        }
        prefs.edit().putString(KEY_DOCK_APPS, arr.toString()).apply();
    }

    /**
     * Load saved dock app package names.
     * Returns empty list if nothing saved yet.
     */
    public List<String> loadDockPackageNames() {
        List<String> list = new ArrayList<>();
        String json = prefs.getString(KEY_DOCK_APPS, null);
        if (json == null) return list;
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                list.add(arr.getString(i));
            }
        } catch (JSONException e) {
            // Ignore corrupt data
        }
        return list;
    }

    // ======= HOME PAGES =======

    /**
     * Save the entire home layout as JSON.
     * Structure: [ page0: [pkg1, pkg2, ...], page1: [...], ... ]
     */
    public void saveHomeLayout(List<List<AppInfo>> pages) {
        try {
            JSONArray pagesArr = new JSONArray();
            for (List<AppInfo> page : pages) {
                JSONArray pageArr = new JSONArray();
                for (AppInfo app : page) {
                    pageArr.put(app.getPackageName());
                }
                pagesArr.put(pageArr);
            }
            prefs.edit().putString(KEY_HOME_PAGES, pagesArr.toString()).apply();
        } catch (Exception e) {
            // Ignore
        }
    }

    /**
     * Load home layout as list of pages, each page = list of package names.
     * Returns null if no saved layout (caller should use default).
     */
    public List<List<String>> loadHomeLayout() {
        String json = prefs.getString(KEY_HOME_PAGES, null);
        if (json == null) return null;

        List<List<String>> pages = new ArrayList<>();
        try {
            JSONArray pagesArr = new JSONArray(json);
            for (int i = 0; i < pagesArr.length(); i++) {
                JSONArray pageArr = pagesArr.getJSONArray(i);
                List<String> page = new ArrayList<>();
                for (int j = 0; j < pageArr.length(); j++) {
                    page.add(pageArr.getString(j));
                }
                pages.add(page);
            }
        } catch (JSONException e) {
            return null;
        }
        return pages;
    }

    /**
     * Reconstruct AppInfo objects from saved package names.
     * Apps that have been uninstalled are silently dropped.
     */
    public List<AppInfo> resolvePackageNames(List<String> packageNames,
                                              List<AppInfo> allApps) {
        List<AppInfo> resolved = new ArrayList<>();
        for (String pkg : packageNames) {
            for (AppInfo app : allApps) {
                if (app.getPackageName().equals(pkg)) {
                    resolved.add(app);
                    break;
                }
            }
        }
        return resolved;
    }

    /**
     * Clears all saved layout data (reset to default).
     */
    public void clearAll() {
        prefs.edit().clear().apply();
    }
}
