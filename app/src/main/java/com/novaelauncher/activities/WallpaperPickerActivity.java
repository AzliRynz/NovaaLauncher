package com.novaelauncher.activities;

import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.novaelauncher.R;

import java.io.IOException;
import java.io.InputStream;

/**
 * Wallpaper picker: lets user choose from gallery or built-in wallpapers.
 * Applies the selected wallpaper using WallpaperManager.
 */
public class WallpaperPickerActivity extends AppCompatActivity {

    private ImageView ivPreview;
    private Button btnPickGallery;
    private Button btnApply;
    private Bitmap selectedBitmap;

    private final ActivityResultLauncher<Intent> galleryLauncher =
        registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri uri = result.getData().getData();
                loadImageFromUri(uri);
            }
        });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallpaper_picker);

        Toolbar toolbar = findViewById(R.id.toolbar_wallpaper);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Choose Wallpaper");
        }

        ivPreview     = findViewById(R.id.iv_wallpaper_preview);
        btnPickGallery = findViewById(R.id.btn_pick_gallery);
        btnApply      = findViewById(R.id.btn_apply_wallpaper);

        btnPickGallery.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            galleryLauncher.launch(intent);
        });

        btnApply.setOnClickListener(v -> {
            if (selectedBitmap != null) {
                applyWallpaper(selectedBitmap);
            } else {
                Toast.makeText(this, "Please select an image first", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadImageFromUri(Uri uri) {
        try {
            InputStream stream = getContentResolver().openInputStream(uri);
            selectedBitmap = BitmapFactory.decodeStream(stream);
            ivPreview.setImageBitmap(selectedBitmap);
        } catch (Exception e) {
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    }

    private void applyWallpaper(Bitmap bitmap) {
        try {
            WallpaperManager wm = WallpaperManager.getInstance(this);
            wm.setBitmap(bitmap);
            Toast.makeText(this, "Wallpaper applied!", Toast.LENGTH_SHORT).show();
            finish();
        } catch (IOException e) {
            Toast.makeText(this, "Failed to apply wallpaper", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
