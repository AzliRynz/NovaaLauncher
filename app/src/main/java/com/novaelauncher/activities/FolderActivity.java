package com.novaelauncher.activities;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.novaelauncher.R;
import com.novaelauncher.adapters.AppDrawerAdapter;
import com.novaelauncher.models.AppInfo;

import java.util.ArrayList;

/**
 * Folder popup — shows apps inside a home screen folder.
 * Opens as a floating translucent card (like Oppo ColorOS folder behavior).
 *
 * Usage: start with intent extras:
 *   EXTRA_FOLDER_NAME — folder display name
 *   EXTRA_FOLDER_APPS — ArrayList<String> of package names
 */
public class FolderActivity extends AppCompatActivity {

    public static final String EXTRA_FOLDER_NAME = "folder_name";
    public static final String EXTRA_FOLDER_APPS = "folder_apps";

    private TextView tvFolderName;
    private EditText etFolderNameEdit;
    private ImageButton btnClose;
    private RecyclerView rvFolderApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Translucent window
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        setContentView(R.layout.activity_folder);

        tvFolderName  = findViewById(R.id.tv_folder_name);
        etFolderNameEdit = findViewById(R.id.et_folder_name_edit);
        btnClose      = findViewById(R.id.btn_folder_close);
        rvFolderApps  = findViewById(R.id.rv_folder_apps);

        String folderName = getIntent().getStringExtra(EXTRA_FOLDER_NAME);
        ArrayList<String> packageNames = getIntent().getStringArrayListExtra(EXTRA_FOLDER_APPS);

        tvFolderName.setText(folderName != null ? folderName : "Folder");

        // Allow renaming folder by tapping name
        tvFolderName.setOnClickListener(v -> {
            tvFolderName.setVisibility(View.GONE);
            etFolderNameEdit.setVisibility(View.VISIBLE);
            etFolderNameEdit.setText(tvFolderName.getText());
            etFolderNameEdit.requestFocus();
        });

        etFolderNameEdit.setOnEditorActionListener((v, actionId, event) -> {
            String newName = etFolderNameEdit.getText().toString().trim();
            if (!newName.isEmpty()) tvFolderName.setText(newName);
            etFolderNameEdit.setVisibility(View.GONE);
            tvFolderName.setVisibility(View.VISIBLE);
            return true;
        });

        btnClose.setOnClickListener(v -> finish());

        // Load apps for this folder from package names
        loadFolderApps(packageNames);
    }

    private void loadFolderApps(ArrayList<String> packageNames) {
        if (packageNames == null || packageNames.isEmpty()) return;

        android.content.pm.PackageManager pm = getPackageManager();
        ArrayList<AppInfo> apps = new ArrayList<>();
        for (String pkg : packageNames) {
            try {
                android.content.pm.ApplicationInfo ai = pm.getApplicationInfo(pkg, 0);
                AppInfo info = new AppInfo(
                    pm.getApplicationLabel(ai).toString(),
                    pkg,
                    "",
                    pm.getApplicationIcon(pkg)
                );
                apps.add(info);
            } catch (android.content.pm.PackageManager.NameNotFoundException e) {
                // App uninstalled, skip
            }
        }

        rvFolderApps.setLayoutManager(new GridLayoutManager(this, 3));
        AppDrawerAdapter adapter = new AppDrawerAdapter(this, apps);
        rvFolderApps.setAdapter(adapter);
    }
}
