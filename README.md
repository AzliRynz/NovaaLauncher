# 🚀 Novaa Launcher

> Android Launcher mirip Infinix XOS & Oppo ColorOS — clean, modern, smooth.

---

## ✨ Fitur

| Fitur | Keterangan |
|---|---|
| **Home Screen** | Multi-halaman (swipe kiri/kanan), grid 3–6 kolom |
| **Clock Widget** | Jam besar + tanggal + sapaan otomatis (Selamat Pagi/Siang/Sore/Malam) |
| **Search Bar** | Pill frosted glass, klik buka Google |
| **App Drawer** | Slide-up drawer, frosted dark glass, A–Z sidebar scroll |
| **Dock** | Pill transparan di bawah, tahan drag untuk atur ulang |
| **Folder** | Long press icon → buat folder, klik buka popup |
| **Wallpaper** | Pilih dari galeri, atur kegelapan |
| **Icon Pack** | Support icon pack ADW/Nova/Apex format |
| **Transisi Halaman** | Scroll / Cube / Fade / Flip |
| **Gesture** | Swipe atas → Drawer, Swipe bawah → Notifikasi, Long press → Menu |
| **Haptic** | Feedback getar saat gestur |
| **Settings** | Grid, icon size, dock, wallpaper dim, transisi, gesture |

---

## 🏗 Struktur Project

```
NovaaLauncher/
├── app/src/main/
│   ├── java/com/novaelauncher/
│   │   ├── activities/
│   │   │   ├── HomeActivity.java          ← MAIN launcher screen
│   │   │   ├── AppDrawerActivity.java     ← Full-screen app drawer
│   │   │   ├── LauncherSettingsActivity.java
│   │   │   ├── WallpaperPickerActivity.java
│   │   │   ├── WidgetPickerActivity.java
│   │   │   ├── FolderActivity.java
│   │   │   └── IconPackPickerActivity.java
│   │   ├── adapters/
│   │   │   ├── HomePageAdapter.java       ← ViewPager2 home pages
│   │   │   ├── AppDrawerAdapter.java      ← Drawer grid + search filter
│   │   │   ├── DockAdapter.java           ← Bottom dock bar
│   │   │   └── AlphabetSectionAdapter.java← A–Z grouped drawer
│   │   ├── models/
│   │   │   ├── AppInfo.java               ← App data model
│   │   │   └── HomeItem.java              ← Home grid item model
│   │   ├── services/
│   │   │   ├── PackageChangeReceiver.java ← Listen install/uninstall
│   │   │   └── BootReceiver.java          ← Auto-start on boot
│   │   ├── utils/
│   │   │   ├── AppLoader.java             ← Load all installed apps
│   │   │   ├── AppLauncher.java           ← Launch apps + animations
│   │   │   ├── GestureHelper.java         ← Swipe/long-press/double-tap
│   │   │   ├── LauncherPreferences.java   ← All settings storage
│   │   │   ├── HomeDataManager.java       ← Persist home layout JSON
│   │   │   ├── DragDropHelper.java        ← Drag & drop icons
│   │   │   └── IconPackManager.java       ← Load ADW/Nova icon packs
│   │   └── widgets/
│   │       ├── ClockWidget.java           ← Live clock custom view
│   │       ├── SearchBarWidget.java       ← Google search pill
│   │       └── AlphabetSidebarView.java   ← A–Z fast-scroll sidebar
│   └── res/
│       ├── layout/          ← All XML layouts
│       ├── drawable/        ← Icons, dots, wallpaper gradient
│       ├── values/          ← Colors, strings, themes, styles
│       ├── menu/            ← Context menus
│       └── anim/            ← Slide up/down animations
```

---

## ⚙️ Setup

### Requirements
- Android Studio Hedgehog atau lebih baru
- Android SDK 26+ (minSdk 26)
- Gradle 8.2+
- Java 11

### Build Steps
```bash
# Clone / extract project
cd NovaaLauncher

# Buka di Android Studio
# File → Open → pilih folder NovaaLauncher

# Sync Gradle
# Tools → Android → Sync Project with Gradle Files

# Build & Install
./gradlew installDebug
```

### Set as Default Launcher
1. Install APK ke device
2. Tekan tombol **Home**
3. Pilih **Novaa Launcher** → **Always**

---

## 🎨 Kustomisasi

### Dari Home Screen
- **Long press wallpaper** → Change Wallpaper / Add Widget / Settings

### Settings (long press → Launcher Settings)
| Setting | Pilihan |
|---|---|
| Grid Columns | 3 – 6 |
| Grid Rows | 3 – 6 |
| Icon Size | 40dp – 80dp |
| Icon Labels | Show/Hide |
| Icon Shape | Circle / Squircle / Square |
| Dock items | 3 – 7 |
| Wallpaper Dim | 0% – 70% |
| Page Transition | Scroll / Cube / Fade / Flip |
| Swipe Up | Drawer / Search |
| Swipe Down | Notifications / Search |
| Haptic Feedback | On/Off |

---

## 📦 Dependencies

```gradle
implementation 'androidx.appcompat:appcompat:1.6.1'
implementation 'com.google.android.material:material:1.11.0'
implementation 'androidx.recyclerview:recyclerview:1.3.2'
implementation 'androidx.viewpager2:viewpager2:1.0.0'
implementation 'androidx.constraintlayout:constraintlayout:2.1.4'
implementation 'jp.wasabeef:blurry:4.0.0'
implementation 'com.github.bumptech.glide:glide:4.16.0'
```

---

## 🧠 Arsitektur & Alur Logika

```
Boot / Home button pressed
        ↓
   HomeActivity
   ├── applyWallpaper()       → WallpaperManager.getDrawable()
   ├── AppLoader.loadAsync()  → PackageManager.queryIntentActivities()
   ├── HomePageAdapter        → ViewPager2 pages
   ├── DockAdapter            → RecyclerView horizontal
   ├── GestureHelper          → fling up/down, long press
   │
   ├── Swipe UP  → AppDrawerActivity (slide-up anim)
   ├── Swipe DOWN → StatusBarManager.expandNotificationsPanel()
   └── Long press → PopupMenu (Wallpaper/Widget/Settings)

AppDrawerActivity
   ├── AppLoader              → load all apps
   ├── AlphabetSectionAdapter → grouped by letter
   ├── AlphabetSidebarView    → A-Z sidebar (custom drawn View)
   └── EditText filter        → AppDrawerAdapter.filter(query)
```

---

## 📋 Catatan

- **Swipe up** dari home otomatis buka drawer
- **Long press icon** → Info / Uninstall / Add to Dock
- **Long press dock** → Remove from Dock
- **Folder** → dibuat dari menu konteks (extend dari sini untuk full impl)
- **Icon Pack** → support format ADW/Nova/Apex (install pack dari Play Store dulu)
- **Widget** → pakai system AppWidget picker, dikembalikan sebagai widgetId

---

*Made with ❤️ — Novaa Launcher v1.0*
