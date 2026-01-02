# Building Sleepy Lsposed

This guide explains how to build the Sleepy Lsposed module from source.

## Prerequisites

### Required Software

- **Android Studio**: Arctic Fox (2020.3.1) or later
- **JDK**: Java Development Kit 17 or later
- **Android SDK**: 
  - Android SDK Platform 34 (Android 14)
  - Android SDK Build-Tools 34.0.0
- **Git**: For version control

### Optional

- **LSPosed**: For testing on a real device
- **Android Emulator**: For basic UI testing (Xposed hooks won't work)

## Setup

### 1. Install Android Studio

Download from [https://developer.android.com/studio](https://developer.android.com/studio)

### 2. Install SDK Components

Open Android Studio → SDK Manager → Install:
- Android SDK Platform 34
- Android SDK Build-Tools 34.0.0
- Android SDK Platform-Tools
- Android Emulator (optional)

### 3. Clone Repository

```bash
git clone https://github.com/gongfuture/sleepy-lsposed.git
cd sleepy-lsposed
```

### 4. Open Project

1. Launch Android Studio
2. Select "Open an Existing Project"
3. Navigate to the cloned repository
4. Click "OK"
5. Wait for Gradle sync to complete

## Building

### Command Line

#### Debug Build

```bash
# Linux/Mac
./gradlew assembleDebug

# Windows
gradlew.bat assembleDebug
```

Output: `app/build/outputs/apk/debug/app-debug.apk`

#### Release Build

```bash
# Linux/Mac
./gradlew assembleRelease

# Windows
gradlew.bat assembleRelease
```

Output: `app/build/outputs/apk/release/app-release-unsigned.apk`

### Android Studio

1. **Build Menu** → **Build Bundle(s) / APK(s)** → **Build APK(s)**
2. Wait for build to complete
3. Click "locate" in the notification to find the APK

## Running

### Install on Device

#### Via Android Studio

1. Connect your device via USB (with USB debugging enabled)
2. **Run** → **Run 'app'**
3. Select your device
4. Click OK

#### Via Command Line

```bash
# Install debug APK
./gradlew installDebug

# Install release APK
adb install app/build/outputs/apk/release/app-release-unsigned.apk
```

### Enable in LSPosed

1. Open LSPosed Manager
2. Go to **Modules**
3. Enable **Sleepy Lsposed**
4. Click on the module
5. Select scopes:
   - `android` (System Framework)
   - `com.android.systemui` (System UI)
6. **Reboot** your device
7. Open Sleepy Lsposed app
8. Configure settings
9. Start service from menu

## Troubleshooting

### Gradle Sync Failed

**Problem**: Gradle sync fails with dependency errors

**Solution**:
1. Check internet connection
2. Invalidate caches: **File** → **Invalidate Caches / Restart**
3. Delete `.gradle` folder and sync again
4. Update Gradle wrapper: `./gradlew wrapper --gradle-version 8.2`

### Build Failed

**Problem**: Build fails with compilation errors

**Solution**:
1. Clean project: `./gradlew clean`
2. Rebuild: `./gradlew assembleDebug`
3. Check JDK version: Must be 17
4. Update Android Studio and SDK

### Cannot Resolve Symbols

**Problem**: IDE shows red underlines for imports

**Solution**:
1. **File** → **Sync Project with Gradle Files**
2. **File** → **Invalidate Caches / Restart**
3. Reimport project

### Module Not Working

**Problem**: Module doesn't hook after installation

**Solution**:
1. Verify LSPosed is properly installed
2. Check module is enabled in LSPosed Manager
3. Ensure correct scopes are selected
4. Reboot device after enabling
5. Check logcat for errors: `adb logcat | grep SleepyLsposed`

## Development

### Project Structure

```
app/
├── build.gradle.kts          # App-level Gradle configuration
├── proguard-rules.pro        # ProGuard rules for release builds
└── src/main/
    ├── AndroidManifest.xml   # App manifest
    ├── assets/
    │   └── xposed_init       # Xposed module entry point
    ├── java/com/sleepy/lsposed/
    │   ├── config/           # Configuration management
    │   ├── data/             # Data models
    │   ├── network/          # HTTP client
    │   ├── service/          # Background service
    │   ├── ui/               # User interface
    │   ├── utils/            # Utility classes
    │   ├── SleepyXposedModule.kt  # Main hook module
    │   ├── ForegroundAppTracker.kt
    │   └── XposedLogger.kt
    └── res/                  # Android resources
        ├── layout/           # XML layouts
        ├── values/           # Strings, colors, themes
        ├── drawable/         # Icons and drawables
        ├── menu/             # Menus
        └── xml/              # Preferences
```

### Key Files

- **xposed_init**: Lists Xposed module entry points
- **SleepyXposedModule.kt**: Implements IXposedHookLoadPackage
- **SleepyMonitorService.kt**: Background monitoring service
- **SettingsActivity.kt**: Main UI activity
- **ConfigManager.kt**: Manages SharedPreferences
- **SleepyApiClient.kt**: HTTP API client

### Gradle Tasks

```bash
# Clean build directory
./gradlew clean

# Assemble debug APK
./gradlew assembleDebug

# Assemble release APK
./gradlew assembleRelease

# Run lint checks
./gradlew lintDebug
./gradlew lintRelease

# Run unit tests
./gradlew test

# Install on connected device
./gradlew installDebug

# Uninstall from device
./gradlew uninstallDebug

# List all tasks
./gradlew tasks
```

### Debugging

#### Logcat Filtering

```bash
# Filter by tag
adb logcat -s SleepyLsposed

# Filter by package
adb logcat | grep com.sleepy.lsposed

# Clear logs
adb logcat -c
```

#### Android Studio Debugger

1. Build and install debug APK
2. **Run** → **Attach Debugger to Android Process**
3. Select `com.sleepy.lsposed`
4. Set breakpoints in code
5. Use the service and observe

**Note**: Xposed hooks run in system process; standard debugging won't work for hook code. Use logging instead.

### Making Changes

1. **Code**: Edit Kotlin files in `app/src/main/java/`
2. **UI**: Edit XML files in `app/src/main/res/`
3. **Strings**: Edit `res/values/strings.xml`
4. **Build**: Run `./gradlew assembleDebug`
5. **Test**: Install and test on device

## Code Signing (Release)

For production releases, sign the APK:

### Generate Keystore

```bash
keytool -genkey -v -keystore sleepy-lsposed.keystore \
  -alias sleepy-lsposed -keyalg RSA -keysize 2048 -validity 10000
```

### Sign APK

```bash
jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
  -keystore sleepy-lsposed.keystore \
  app/build/outputs/apk/release/app-release-unsigned.apk \
  sleepy-lsposed
```

### Verify Signature

```bash
jarsigner -verify -verbose -certs \
  app/build/outputs/apk/release/app-release-unsigned.apk
```

## Continuous Integration

The project uses GitHub Actions for CI/CD:

- **android-ci.yml**: Builds on push/PR
- **release.yml**: Creates releases on tags

### Workflow

1. Push code to GitHub
2. GitHub Actions automatically:
   - Checks out code
   - Sets up JDK 17
   - Runs lint checks
   - Builds debug APK
   - Builds release APK
   - Uploads artifacts

### Creating a Release

```bash
# Tag version
git tag v1.0.0

# Push tag
git push origin v1.0.0
```

GitHub Actions will automatically build and create a GitHub Release.

## Performance Optimization

### ProGuard/R8

Release builds use R8 for:
- Code shrinking
- Obfuscation
- Optimization

Rules are in `app/proguard-rules.pro`

### Build Variants

- **Debug**: No optimization, debuggable
- **Release**: Optimized, obfuscated, signed

## Resources

- [Android Studio Guide](https://developer.android.com/studio/intro)
- [Gradle Build Guide](https://developer.android.com/studio/build)
- [Xposed API](https://api.xposed.info/)
- [LSPosed Documentation](https://github.com/LSPosed/LSPosed)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Material Design 3](https://m3.material.io/)

## Getting Help

- Check [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines
- Open an issue on GitHub for bugs
- Join discussions for questions

---

Happy building! 🚀
