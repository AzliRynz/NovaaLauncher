package com.novaelauncher.models;

/**
 * Represents an item on the home screen grid.
 * Can be an app shortcut, folder, or widget placeholder.
 */
public class HomeItem {

    public enum ItemType {
        APP_SHORTCUT,
        FOLDER,
        WIDGET
    }

    private ItemType type;
    private AppInfo appInfo;    // for APP_SHORTCUT
    private String folderName;  // for FOLDER
    private int gridRow;
    private int gridCol;
    private int pageIndex;
    private int widgetId;       // for WIDGET (AppWidgetId)
    private int widgetWidth;    // in grid cells
    private int widgetHeight;   // in grid cells

    public HomeItem(ItemType type, AppInfo appInfo, int pageIndex, int gridRow, int gridCol) {
        this.type = type;
        this.appInfo = appInfo;
        this.pageIndex = pageIndex;
        this.gridRow = gridRow;
        this.gridCol = gridCol;
    }

    // Getters
    public ItemType getType() { return type; }
    public AppInfo getAppInfo() { return appInfo; }
    public String getFolderName() { return folderName; }
    public int getGridRow() { return gridRow; }
    public int getGridCol() { return gridCol; }
    public int getPageIndex() { return pageIndex; }
    public int getWidgetId() { return widgetId; }
    public int getWidgetWidth() { return widgetWidth; }
    public int getWidgetHeight() { return widgetHeight; }

    // Setters
    public void setType(ItemType type) { this.type = type; }
    public void setAppInfo(AppInfo appInfo) { this.appInfo = appInfo; }
    public void setFolderName(String folderName) { this.folderName = folderName; }
    public void setGridRow(int gridRow) { this.gridRow = gridRow; }
    public void setGridCol(int gridCol) { this.gridCol = gridCol; }
    public void setPageIndex(int pageIndex) { this.pageIndex = pageIndex; }
    public void setWidgetId(int widgetId) { this.widgetId = widgetId; }
    public void setWidgetWidth(int widgetWidth) { this.widgetWidth = widgetWidth; }
    public void setWidgetHeight(int widgetHeight) { this.widgetHeight = widgetHeight; }
}
