package com.novaelauncher.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.novaelauncher.R;
import com.novaelauncher.models.AppInfo;
import com.novaelauncher.utils.AppLauncher;
import com.novaelauncher.utils.LauncherPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewPager2 adapter for home screen pages.
 * Each page is a grid of app icons.
 */
public class HomePageAdapter extends RecyclerView.Adapter<HomePageAdapter.PageViewHolder> {

    private final Context context;
    private final List<List<AppInfo>> pages;     // list of pages, each page = list of apps
    private final LauncherPreferences prefs;
    private OnIconLongClickListener longClickListener;

    public interface OnIconLongClickListener {
        void onIconLongClick(AppInfo appInfo, View iconView, int pageIndex, int iconIndex);
    }

    public HomePageAdapter(Context context, List<List<AppInfo>> pages) {
        this.context = context;
        this.pages = pages;
        this.prefs = LauncherPreferences.getInstance(context);
    }

    public void setOnIconLongClickListener(OnIconLongClickListener listener) {
        this.longClickListener = listener;
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
            .inflate(R.layout.layout_home_page, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        holder.bind(pages.get(position), position);
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

    /**
     * Distribute apps into pages based on grid size.
     */
    public static List<List<AppInfo>> distributeToPages(List<AppInfo> apps,
                                                         int rows, int cols) {
        int perPage = rows * cols;
        List<List<AppInfo>> pages = new ArrayList<>();
        List<AppInfo> currentPage = new ArrayList<>();

        for (AppInfo app : apps) {
            currentPage.add(app);
            if (currentPage.size() == perPage) {
                pages.add(new ArrayList<>(currentPage));
                currentPage.clear();
            }
        }
        if (!currentPage.isEmpty()) {
            pages.add(currentPage);
        }
        if (pages.isEmpty()) {
            pages.add(new ArrayList<>());
        }
        return pages;
    }

    class PageViewHolder extends RecyclerView.ViewHolder {
        GridLayout gridLayout;

        PageViewHolder(View itemView) {
            super(itemView);
            gridLayout = itemView.findViewById(R.id.grid_home_page);
        }

        void bind(List<AppInfo> apps, int pageIndex) {
            int cols = prefs.getGridColumns();
            int rows = prefs.getGridRows();
            gridLayout.setColumnCount(cols);
            gridLayout.setRowCount(rows);
            gridLayout.removeAllViews();

            LayoutInflater inflater = LayoutInflater.from(context);

            for (int i = 0; i < apps.size() && i < rows * cols; i++) {
                AppInfo app = apps.get(i);
                View iconView = inflater.inflate(R.layout.item_home_icon, gridLayout, false);

                ImageView ivIcon = iconView.findViewById(R.id.iv_app_icon);
                TextView tvName = iconView.findViewById(R.id.tv_app_name);

                ivIcon.setImageDrawable(app.getIcon());
                tvName.setText(app.getAppName());
                tvName.setVisibility(prefs.isIconLabelVisible() ? View.VISIBLE : View.GONE);

                // === Launch app on click ===
                iconView.setOnClickListener(v -> AppLauncher.launchApp(context, app, ivIcon));

                // === Long click → context menu ===
                final int finalI = i;
                iconView.setOnLongClickListener(v -> {
                    if (longClickListener != null) {
                        longClickListener.onIconLongClick(app, iconView, pageIndex, finalI);
                    }
                    return true;
                });

                // Grid spec
                GridLayout.Spec rowSpec = GridLayout.spec(i / cols, 1, 1f);
                GridLayout.Spec colSpec = GridLayout.spec(i % cols, 1, 1f);
                GridLayout.LayoutParams lp = new GridLayout.LayoutParams(rowSpec, colSpec);
                lp.setGravity(android.view.Gravity.FILL);

                gridLayout.addView(iconView, lp);
            }
        }
    }
}
