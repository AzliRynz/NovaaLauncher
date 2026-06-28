package com.novaelauncher.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.novaelauncher.R;
import com.novaelauncher.utils.IconPackManager;
import com.novaelauncher.utils.LauncherPreferences;

import java.util.List;

/**
 * Shows all installed icon packs and lets user select one.
 * Current selection is persisted via LauncherPreferences.
 */
public class IconPackPickerActivity extends AppCompatActivity {

    private RecyclerView rvIconPacks;
    private IconPackManager iconPackManager;
    private LauncherPreferences prefs;
    private List<IconPackManager.IconPackInfo> iconPacks;
    private String selectedPackage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon_pack_picker);

        Toolbar toolbar = findViewById(R.id.toolbar_icon_pack);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Icon Packs");
        }

        prefs = LauncherPreferences.getInstance(this);
        selectedPackage = prefs.getIconPack();
        iconPackManager = new IconPackManager(this);
        iconPacks = iconPackManager.getInstalledIconPacks();

        rvIconPacks = findViewById(R.id.rv_icon_packs);
        rvIconPacks.setLayoutManager(new LinearLayoutManager(this));
        rvIconPacks.setAdapter(new IconPackAdapter());

        if (iconPacks.isEmpty()) {
            TextView tvEmpty = findViewById(R.id.tv_no_icon_packs);
            if (tvEmpty != null) tvEmpty.setVisibility(View.VISIBLE);
        }
    }

    private class IconPackAdapter extends RecyclerView.Adapter<IconPackAdapter.VH> {
        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_icon_pack, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            // Default "System" option at position 0
            if (position == 0) {
                holder.tvName.setText("System Default");
                holder.ivIcon.setImageResource(android.R.drawable.sym_def_app_icon);
                holder.rb.setChecked(selectedPackage == null || selectedPackage.isEmpty());
                holder.itemView.setOnClickListener(v -> {
                    selectedPackage = "";
                    prefs.setIconPack("");
                    notifyDataSetChanged();
                    Toast.makeText(IconPackPickerActivity.this,
                        "Reverted to system icons", Toast.LENGTH_SHORT).show();
                });
            } else {
                IconPackManager.IconPackInfo pack = iconPacks.get(position - 1);
                holder.tvName.setText(pack.label);
                holder.ivIcon.setImageDrawable(pack.icon);
                holder.rb.setChecked(pack.packageName.equals(selectedPackage));
                holder.itemView.setOnClickListener(v -> {
                    selectedPackage = pack.packageName;
                    prefs.setIconPack(pack.packageName);
                    notifyDataSetChanged();
                    Toast.makeText(IconPackPickerActivity.this,
                        pack.label + " applied!", Toast.LENGTH_SHORT).show();
                });
            }
        }

        @Override
        public int getItemCount() { return iconPacks.size() + 1; }  // +1 for "System Default"

        class VH extends RecyclerView.ViewHolder {
            ImageView ivIcon;
            TextView tvName;
            RadioButton rb;
            VH(View v) {
                super(v);
                ivIcon = v.findViewById(R.id.iv_icon_pack_icon);
                tvName = v.findViewById(R.id.tv_icon_pack_name);
                rb     = v.findViewById(R.id.rb_icon_pack_select);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() { onBackPressed(); return true; }
}
