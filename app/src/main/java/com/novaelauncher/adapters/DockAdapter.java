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
 * Adapter for the bottom dock (favorite/pinned apps bar).
 * Displayed as a horizontal row of large icons.
 */
public class DockAdapter extends RecyclerView.Adapter<DockAdapter.DockViewHolder> {

    public interface OnDockLongClickListener {
        void onDockLongClick(AppInfo app, View view, int position);
    }

    private final Context context;
    private List<AppInfo> dockApps;
    private OnDockLongClickListener longClickListener;

    public DockAdapter(Context context, List<AppInfo> dockApps) {
        this.context = context;
        this.dockApps = new ArrayList<>(dockApps);
    }

    public void setOnDockLongClickListener(OnDockLongClickListener listener) {
        this.longClickListener = listener;
    }

    public void setDockApps(List<AppInfo> apps) {
        this.dockApps = new ArrayList<>(apps);
        notifyDataSetChanged();
    }

    public List<AppInfo> getDockApps() {
        return dockApps;
    }

    public void addApp(AppInfo app) {
        if (!dockApps.contains(app)) {
            dockApps.add(app);
            notifyItemInserted(dockApps.size() - 1);
        }
    }

    public void removeApp(int position) {
        if (position >= 0 && position < dockApps.size()) {
            dockApps.remove(position);
            notifyItemRemoved(position);
        }
    }

    @NonNull
    @Override
    public DockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
            .inflate(R.layout.item_dock_icon, parent, false);
        return new DockViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DockViewHolder holder, int position) {
        holder.bind(dockApps.get(position), position);
    }

    @Override
    public int getItemCount() {
        return dockApps.size();
    }

    class DockViewHolder extends RecyclerView.ViewHolder {
        ImageView ivIcon;
        TextView tvName;

        DockViewHolder(View itemView) {
            super(itemView);
            ivIcon = itemView.findViewById(R.id.iv_dock_icon);
            tvName = itemView.findViewById(R.id.tv_dock_name);
        }

        void bind(AppInfo app, int position) {
            ivIcon.setImageDrawable(app.getIcon());
            tvName.setText(app.getAppName());

            itemView.setOnClickListener(v ->
                AppLauncher.launchApp(context, app, ivIcon)
            );

            itemView.setOnLongClickListener(v -> {
                if (longClickListener != null) {
                    longClickListener.onDockLongClick(app, itemView, position);
                }
                return true;
            });
        }
    }
}
