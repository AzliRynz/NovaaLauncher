package com.novaelauncher.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.novaelauncher.R;
import com.novaelauncher.models.AppInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter that groups apps by their first letter with section headers.
 * Used in AppDrawerActivity for the alphabetical fast-scroll sidebar.
 *
 * Item types:
 *   TYPE_HEADER (0) — letter section header (A, B, C …)
 *   TYPE_APP    (1) — normal app icon cell
 */
public class AlphabetSectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_APP    = 1;

    public interface OnAppClickListener {
        void onAppClick(AppInfo app);
        void onAppLongClick(AppInfo app, View view);
    }

    // Combined list of headers + apps
    private final List<Object> items = new ArrayList<>();   // String for header, AppInfo for app
    private OnAppClickListener clickListener;
    private final Context context;

    public AlphabetSectionAdapter(Context context, List<AppInfo> apps) {
        this.context = context;
        buildItems(apps);
    }

    public void setOnAppClickListener(OnAppClickListener listener) {
        this.clickListener = listener;
    }

    public void updateApps(List<AppInfo> apps) {
        items.clear();
        buildItems(apps);
        notifyDataSetChanged();
    }

    private void buildItems(List<AppInfo> apps) {
        char currentLetter = 0;
        for (AppInfo app : apps) {
            char first = Character.toUpperCase(app.getAppName().charAt(0));
            // Non-alpha apps go under "#"
            if (!Character.isLetter(first)) first = '#';
            if (first != currentLetter) {
                items.add(String.valueOf(first));   // header
                currentLetter = first;
            }
            items.add(app);
        }
    }

    /**
     * Returns the position of the first app starting with the given letter.
     * Used by the sidebar to scroll to a section.
     */
    public int getPositionForLetter(char letter) {
        char target = Character.toUpperCase(letter);
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i) instanceof String) {
                String header = (String) items.get(i);
                if (!header.isEmpty() && Character.toUpperCase(header.charAt(0)) == target) {
                    return i;
                }
            }
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position) instanceof String ? TYPE_HEADER : TYPE_APP;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        if (viewType == TYPE_HEADER) {
            View v = inflater.inflate(R.layout.item_section_header, parent, false);
            return new HeaderVH(v);
        } else {
            View v = inflater.inflate(R.layout.item_drawer_app, parent, false);
            return new AppVH(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderVH) {
            ((HeaderVH) holder).tvLetter.setText((String) items.get(position));
        } else if (holder instanceof AppVH) {
            AppInfo app = (AppInfo) items.get(position);
            AppVH vh = (AppVH) holder;
            vh.ivIcon.setImageDrawable(app.getIcon());
            vh.tvName.setText(app.getAppName());
            vh.itemView.setOnClickListener(v -> {
                if (clickListener != null) clickListener.onAppClick(app);
            });
            vh.itemView.setOnLongClickListener(v -> {
                if (clickListener != null) clickListener.onAppLongClick(app, v);
                return true;
            });
        }
    }

    @Override
    public int getItemCount() { return items.size(); }

    // === GridLayoutManager SpanSizeLookup: headers span full width ===
    public GridLayoutManager.SpanSizeLookup getSpanSizeLookup(int spanCount) {
        return new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return getItemViewType(position) == TYPE_HEADER ? spanCount : 1;
            }
        };
    }

    // === ViewHolders ===

    static class HeaderVH extends RecyclerView.ViewHolder {
        TextView tvLetter;
        HeaderVH(View v) {
            super(v);
            tvLetter = v.findViewById(R.id.tv_section_letter);
        }
    }

    static class AppVH extends RecyclerView.ViewHolder {
        android.widget.ImageView ivIcon;
        TextView tvName;
        AppVH(View v) {
            super(v);
            ivIcon = v.findViewById(R.id.iv_drawer_icon);
            tvName = v.findViewById(R.id.tv_drawer_name);
        }
    }
}
