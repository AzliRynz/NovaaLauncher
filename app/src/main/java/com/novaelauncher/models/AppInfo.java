package com.novaelauncher.models;

import android.graphics.drawable.Drawable;

/**
 * Model class representing an installed application.
 * Contains all info needed to display and launch an app.
 */
public class AppInfo {

    private String appName;
    private String packageName;
    private String activityName;
    private Drawable icon;
    private boolean isPinned;       // pinned to home screen dock
    private boolean isFavorite;     // in favorites section of drawer
    private boolean isHidden;       // hidden from drawer
    private long installTime;
    private long lastUsedTime;

    public AppInfo() {}

    public AppInfo(String appName, String packageName, String activityName, Drawable icon) {
        this.appName = appName;
        this.packageName = packageName;
        this.activityName = activityName;
        this.icon = icon;
        this.isPinned = false;
        this.isFavorite = false;
        this.isHidden = false;
    }

    // ===== Getters =====
    public String getAppName() { return appName; }
    public String getPackageName() { return packageName; }
    public String getActivityName() { return activityName; }
    public Drawable getIcon() { return icon; }
    public boolean isPinned() { return isPinned; }
    public boolean isFavorite() { return isFavorite; }
    public boolean isHidden() { return isHidden; }
    public long getInstallTime() { return installTime; }
    public long getLastUsedTime() { return lastUsedTime; }

    // ===== Setters =====
    public void setAppName(String appName) { this.appName = appName; }
    public void setPackageName(String packageName) { this.packageName = packageName; }
    public void setActivityName(String activityName) { this.activityName = activityName; }
    public void setIcon(Drawable icon) { this.icon = icon; }
    public void setPinned(boolean pinned) { isPinned = pinned; }
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
    public void setHidden(boolean hidden) { isHidden = hidden; }
    public void setInstallTime(long installTime) { this.installTime = installTime; }
    public void setLastUsedTime(long lastUsedTime) { this.lastUsedTime = lastUsedTime; }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AppInfo)) return false;
        AppInfo other = (AppInfo) obj;
        return packageName != null && packageName.equals(other.packageName);
    }

    @Override
    public int hashCode() {
        return packageName != null ? packageName.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "AppInfo{name=" + appName + ", pkg=" + packageName + "}";
    }
}
