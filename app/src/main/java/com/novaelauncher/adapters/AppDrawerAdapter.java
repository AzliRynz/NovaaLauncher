package com.novaelauncher.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.novaelauncher.R;
import com.novaelauncher.models.AppInfo;
import com.novaelauncher.utils.AppLauncher;

import java.util.ArrayList;
import java.util.List;

/**
 * Grid adapter for the App Drawer.
 * Supports search filtering and long-press context menu.
 */
public class AppDrawerAdapter extends RecyclerView.Adapter<AppDrawerAdapter.AppViewHolder> {

    public interface OnAppLongClickListener {
        void onAppLongClick(AppInfo app, View view);
    }

    private final Context context;
    private List<AppInfo> allApps;
    private List<AppInfo> filteredApps;
    private OnAppLongClickListener longClickListener;

    public AppDrawerAdapter(Context context, List<AppInfo> apps) {
        this.context = context;
        this.allApps = new ArrayList<>(apps);
        this.filteredApps = new ArrayList<>(apps);
    }

    public void setOnAppLongClickListener(OnAppLongClickListener listener) {
        this.longClickListener = listener;
    }

    public void updateApps(List<AppInfo> apps) {
        this.allApps = new ArrayList<>(apps);
        this.filteredApps = new ArrayList<>(apps);
        notifyDataSetChanged();
    }

    /**
     * Filter apps by search query. Empty query restores full list.
     */
    public void filter(String query) {
        filteredApps.clear();
        if (query == null || query.trim().isEmpty()) {
            filteredApps.addAll(allApps);
        } else {
            String lower = query.toLowerCase().trim();
            for (AppInfo app : allApps) {
                if (app.getAppName().toLowerCase().contains(lower) ||
                    app.getPackageName().toLowerCase().contains(lower)) {
                    filteredApps.add(app);
                }
            }
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
            .inflate(R.layout.item_drawer_app, parent, false);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        holder.bind(filteredApps.get(position));
    }

    @Override
    public int getItemCount() {
        return filteredApps.size();
    }

    class AppViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvName;

        AppViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_drawer_icon);
            tvName = itemView.findViewById(R.id.tv_drawer_name);
        }

        void bind(AppInfo app) {
            ivIcon.setImageDrawable(app.getIcon());
            tvName.setText(app.getAppName());

            itemView.setOnClickListener(v ->
                AppLauncher.launchApp(context, app, ivIcon)
            );

            itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.onAppLongClick(app, itemView);
                }
                return true;
            });
        }
    }
}
