package com.novaelauncher.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Listens for app installs/uninstalls and refreshes the launcher app list.
 * Sends a local broadcast that HomeActivity listens for.
 */
public class PackageChangeReceiver extends BroadcastReceiver {

    public static final String ACTION_APPS_CHANGED = "com.novaelauncher.APPS_CHANGED";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (Intent.ACTION_PACKAGE_ADDED.equals(action) ||
            Intent.ACTION_PACKAGE_REMOVED.equals(action) ||
            Intent.ACTION_PACKAGE_CHANGED.equals(action)) {

            // Notify launcher to reload apps
            Intent broadcast = new Intent(ACTION_APPS_CHANGED);
            context.sendBroadcast(broadcast);
        }
    }
}
