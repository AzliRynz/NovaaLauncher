package com.novaelauncher.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;

import com.novaelauncher.models.AppInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Utility class that queries PackageManager to get all launchable apps.
 * Uses AsyncTask so loading never blocks the UI thread.
 */
public class AppLoader {

    public interface AppLoadCallback {
        void onAppsLoaded(List<AppInfo> apps);
    }

    private final Context context;

    public AppLoader(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Asynchronously loads all installed, launchable apps.
     */
    public void loadAppsAsync(AppLoadCallback callback) {
        new LoadTask(context, callback).execute();
    }

    /**
     * Synchronously loads all installed, launchable apps.
     * Call only from a background thread!
     */
    public static List<AppInfo> loadAppsSync(Context context) {
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, 0);
        List<AppInfo> apps = new ArrayList<>();

        String ownPackage = context.getPackageName();

        for (ResolveInfo ri : resolveInfoList) {
            String pkg = ri.activityInfo.packageName;

            // Skip our own launcher
            if (pkg.equals(ownPackage)) continue;

            AppInfo info = new AppInfo(
                ri.loadLabel(pm).toString(),
                pkg,
                ri.activityInfo.name,
                ri.loadIcon(pm)
            );

            try {
                info.setInstallTime(
                    pm.getPackageInfo(pkg, 0).firstInstallTime
                );
            } catch (PackageManager.NameNotFoundException e) {
                info.setInstallTime(0);
            }

            apps.add(info);
        }

        // Default sort: alphabetical
        Collections.sort(apps, (a, b) ->
            a.getAppName().compareToIgnoreCase(b.getAppName())
        );

        return apps;
    }

    @SuppressWarnings("deprecation")
    private static class LoadTask extends AsyncTask<Void, Void, List<AppInfo>> {
        private final Context context;
        private final AppLoadCallback callback;

        LoadTask(Context context, AppLoadCallback callback) {
            this.context = context;
            this.callback = callback;
        }

        @Override
        protected List<AppInfo> doInBackground(Void... voids) {
            return loadAppsSync(context);
        }

        @Override
        protected void onPostExecute(List<AppInfo> apps) {
            if (callback != null) {
                callback.onAppsLoaded(apps);
            }
        }
    }
}
