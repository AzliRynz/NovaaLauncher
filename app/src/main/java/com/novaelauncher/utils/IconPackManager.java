package com.novaelauncher.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages third-party icon packs (ADW / Nova / Apex format).
 *
 * Icon packs expose their icons via resources named after package names.
 * Format: drawable resource named after the component (e.g., "com_google_android_gm")
 *
 * Detection: icon packs declare an intent filter for
 *   org.adw.launcher.THEMES or com.novalauncher.THEME
 */
public class IconPackManager {

    public static class IconPackInfo {
        public String packageName;
        public String label;
        public Drawable icon;

        public IconPackInfo(String pkg, String label, Drawable icon) {
            this.packageName = pkg;
            this.label = label;
            this.icon = icon;
        }
    }

    private final Context context;
    private final PackageManager pm;
    private String currentPackageName;
    private Resources packResources;
    private final Map<String, String> iconCache = new HashMap<>();

    // Icon pack intent filter actions (standard across launchers)
    private static final String[] ICON_PACK_ACTIONS = {
        "org.adw.launcher.THEMES",
        "com.novalauncher.THEME",
        "org.adwfreak.launcher.THEMES",
        "com.gau.go.launcherex.theme"
    };

    public IconPackManager(Context context) {
        this.context = context.getApplicationContext();
        this.pm = context.getPackageManager();
    }

    /**
     * Returns list of all installed icon packs on the device.
     */
    public List<IconPackInfo> getInstalledIconPacks() {
        List<IconPackInfo> packs = new ArrayList<>();
        for (String action : ICON_PACK_ACTIONS) {
            Intent intent = new Intent(action);
            List<ResolveInfo> list = pm.queryIntentActivities(intent, 0);
            for (ResolveInfo ri : list) {
                String pkg = ri.activityInfo.packageName;
                // Avoid duplicates
                boolean exists = false;
                for (IconPackInfo info : packs) {
                    if (info.packageName.equals(pkg)) { exists = true; break; }
                }
                if (!exists) {
                    packs.add(new IconPackInfo(
                        pkg,
                        ri.loadLabel(pm).toString(),
                        ri.loadIcon(pm)
                    ));
                }
            }
        }
        return packs;
    }

    /**
     * Load the icon pack resources for a given package.
     * Call this once when the user selects a pack.
     */
    public boolean loadIconPack(String packageName) {
        try {
            packResources = pm.getResourcesForApplication(packageName);
            currentPackageName = packageName;
            iconCache.clear();
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            packResources = null;
            currentPackageName = null;
            return false;
        }
    }

    /**
     * Get the icon for a specific app from the loaded icon pack.
     * Returns null if no custom icon found (caller should use default).
     */
    public Drawable getIconForPackage(String appPackageName) {
        if (packResources == null || currentPackageName == null) return null;

        // Cache lookup
        if (iconCache.containsKey(appPackageName)) {
            String resName = iconCache.get(appPackageName);
            if (resName == null) return null;
            int resId = packResources.getIdentifier(resName, "drawable", currentPackageName);
            if (resId != 0) {
                try { return packResources.getDrawable(resId, null); }
                catch (Exception e) { return null; }
            }
        }

        // Convert package name to drawable resource name format
        // e.g. "com.google.android.gm" → "com_google_android_gm"
        String resName = appPackageName.replace(".", "_").toLowerCase();
        int resId = packResources.getIdentifier(resName, "drawable", currentPackageName);

        if (resId != 0) {
            iconCache.put(appPackageName, resName);
            try { return packResources.getDrawable(resId, null); }
            catch (Exception ignored) {}
        }

        // Cache miss (no icon in pack)
        iconCache.put(appPackageName, null);
        return null;
    }

    /**
     * Clears the loaded icon pack and reverts to system icons.
     */
    public void clearIconPack() {
        packResources = null;
        currentPackageName = null;
        iconCache.clear();
    }

    public boolean hasIconPack() {
        return packResources != null;
    }

    public String getCurrentPackageName() {
        return currentPackageName;
    }
}
