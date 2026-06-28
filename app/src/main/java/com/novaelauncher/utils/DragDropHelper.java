package com.novaelauncher.utils;

import android.content.ClipData;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.DragEvent;
import android.view.View;
import android.widget.ImageView;

import com.novaelauncher.models.AppInfo;

/**
 * Handles drag-and-drop for rearranging icons on the home screen.
 *
 * Flow:
 *   1. User long-presses icon → startDrag() begins
 *   2. User drags over other icons → drop zones highlight
 *   3. User releases → onDrop() fires, positions swap
 *
 * Uses Android's built-in View.startDragAndDrop() (API 24+).
 */
public class DragDropHelper {

    public interface DragDropCallback {
        void onDragStarted(AppInfo app, int fromPage, int fromIndex);
        void onDroppedOnIcon(AppInfo draggedApp, AppInfo targetApp, int toPage, int toIndex);
        void onDroppedOnDock(AppInfo app);
        void onDragCancelled();
    }

    private final DragDropCallback callback;
    private AppInfo draggingApp;
    private int fromPage;
    private int fromIndex;

    public DragDropHelper(DragDropCallback callback) {
        this.callback = callback;
    }

    /**
     * Start dragging an icon.
     * Call from the icon's OnLongClickListener.
     */
    public boolean startDrag(View iconView, AppInfo app, int page, int index) {
        draggingApp = app;
        fromPage = page;
        fromIndex = index;

        // Create drag shadow from the icon's current visual
        View.DragShadowBuilder shadow = new View.DragShadowBuilder(iconView);
        ClipData data = ClipData.newPlainText("app_drag", app.getPackageName());

        boolean started;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            started = iconView.startDragAndDrop(data, shadow, app, 0);
        } else {
            //noinspection deprecation
            started = iconView.startDrag(data, shadow, app, 0);
        }

        if (started) {
            // Dim the source icon while dragging
            iconView.setAlpha(0.4f);
            callback.onDragStarted(app, page, index);
        }
        return started;
    }

    /**
     * Attach drag listener to a potential drop target (another icon).
     */
    public void attachDropTarget(View targetView, AppInfo targetApp, int toPage, int toIndex) {
        targetView.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED:
                    // Visual highlight
                    v.setAlpha(0.6f);
                    return true;

                case DragEvent.ACTION_DRAG_EXITED:
                    v.setAlpha(1.0f);
                    return true;

                case DragEvent.ACTION_DROP:
                    v.setAlpha(1.0f);
                    if (draggingApp != null) {
                        callback.onDroppedOnIcon(draggingApp, targetApp, toPage, toIndex);
                    }
                    return true;

                case DragEvent.ACTION_DRAG_ENDED:
                    v.setAlpha(1.0f);
                    if (!event.getResult()) {
                        callback.onDragCancelled();
                    }
                    draggingApp = null;
                    return true;

                default:
                    return false;
            }
        });
    }

    /**
     * Attach drag listener to the dock RecyclerView.
     */
    public void attachDockDropTarget(View dockView) {
        dockView.setOnDragListener((v, event) -> {
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_ENTERED:
                    dockView.setAlpha(0.8f);
                    return true;
                case DragEvent.ACTION_DRAG_EXITED:
                    dockView.setAlpha(1.0f);
                    return true;
                case DragEvent.ACTION_DROP:
                    dockView.setAlpha(1.0f);
                    if (draggingApp != null) {
                        callback.onDroppedOnDock(draggingApp);
                    }
                    return true;
                case DragEvent.ACTION_DRAG_ENDED:
                    dockView.setAlpha(1.0f);
                    return true;
                default:
                    return false;
            }
        });
    }
}
