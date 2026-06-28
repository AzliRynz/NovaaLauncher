# NovaaLauncher ProGuard Rules

# Keep model classes (JSON serialization)
-keep class com.novaelauncher.models.** { *; }

# Keep utils
-keep class com.novaelauncher.utils.** { *; }

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep class * extends com.bumptech.glide.module.AppGlideModule { *; }
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

# Material components
-keep class com.google.android.material.** { *; }

# AndroidX
-keep class androidx.** { *; }
