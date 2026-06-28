package com.novaelauncher.utils;

import android.app.ActivityOptions;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import com.novaelauncher.models.AppInfo;

/**
 * Handles launching apps with proper animations and error handling.
 */
public class AppLauncher {

    /**
     * Launch an app with a scale-up animation from the icon view.
     */
    public static void launchApp(Context context, AppInfo appInfo, View iconView) {
        if (appInfo == null || appInfo.getPackageName() == null) return;

        Intent intent = context.getPackageManager()
            .getLaunchIntentForPackage(appInfo.getPackageName());

        if (intent == null) {
            Toast.makeText(context, "Cannot open app", Toast.LENGTH_SHORT).show();
            return;
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);

        try {
            if (iconView != null) {
                // Clip-reveal animation from icon center
                ActivityOptions options = ActivityOptions.makeScaleUpAnimation(
                    iconView,
                    0, 0,
                    iconView.getWidth(),
                    iconView.getHeight()
                );
                context.startActivity(intent, options.toBundle());
            } else {
                context.startActivity(intent);
            }
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Open system app info screen for the given package.
     */
    public static void openAppInfo(Context context, String packageName) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + packageName));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Uninstall the given app.
     */
    public static void uninstallApp(Context context, String packageName) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse("package:" + packageName));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    /**
     * Expand/collapse notification shade via StatusBarManager reflection.
     * This is a common hack used by launchers since no public API exists.
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void expandNotificationShade(Context context) {
        try {
            @SuppressLint("WrongConstant")
            Object service = context.getSystemService("statusbar");
            Class cls = Class.forName("android.app.StatusBarManager");
            cls.getMethod("expandNotificationsPanel").invoke(service);
        } catch (Exception e) {
            // Silently fail — not all devices expose this
        }
    }
}
