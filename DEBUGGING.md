# Debugging Guide for Sleepy Lsposed

## For Android 15 Users

### Getting Crash Logs via ADB

#### Prerequisites
1. Enable USB Debugging on your Android device:
   - Go to **Settings** → **About Phone**
   - Tap **Build Number** 7 times to enable Developer Options
   - Go to **Settings** → **Developer Options**
   - Enable **USB Debugging**

2. Install ADB on your computer:
   - **Windows**: Download [Platform Tools](https://developer.android.com/studio/releases/platform-tools)
   - **Mac**: `brew install android-platform-tools`
   - **Linux**: `sudo apt install adb` or `sudo pacman -S android-tools`

#### Connecting Your Device
```bash
# Connect device via USB cable
adb devices

# If device shows as "unauthorized", check your phone for authorization prompt
```

#### Capturing App Crash Logs

**Method 1: Real-time monitoring (recommended)**
```bash
# Clear existing logs
adb logcat -c

# Open the app on your phone, then run:
adb logcat | grep -E "SleepyLsposed|AndroidRuntime|FATAL"
```

**Method 2: Save to file**
```bash
# Start logging
adb logcat > sleepy_crash_log.txt

# Open the app on your phone
# When it crashes, press Ctrl+C to stop logging
# Share the sleepy_crash_log.txt file
```

**Method 3: Get last crash only**
```bash
adb logcat -d | grep -A 100 "FATAL EXCEPTION"
```

#### Specific Log Filters

**App-specific logs:**
```bash
adb logcat -s com.sleepy.lsposed:V
```

**Xposed/LSPosed logs:**
```bash
adb logcat | grep -E "Xposed|LSPosed|EdXposed"
```

**Service logs:**
```bash
adb logcat | grep "SleepyMonitorService"
```

**Activity logs:**
```bash
adb logcat | grep "SettingsActivity"
```

### Common Issues on Android 15

#### Issue 1: App crashes immediately on launch

**Cause**: Missing resources or theme issues

**Check:**
```bash
adb logcat | grep -E "ResourceNotFound|InflateException"
```

**Solution**: Ensure app is properly installed

#### Issue 2: App crashes after enabling in LSPosed

**Cause**: Xposed hooks not active (reboot required)

**Solution**: 
1. Enable module in LSPosed
2. Select scopes (android, com.android.systemui)
3. **REBOOT the device** (required!)
4. Open app after reboot

#### Issue 3: Media monitoring permission error

**Cause**: Missing notification listener permission

**Check:**
```bash
adb logcat | grep -E "SecurityException|permission"
```

**Note**: Current version has disabled media monitoring to prevent crashes. This will be implemented in a future update with proper NotificationListenerService.

#### Issue 4: Service won't start

**Cause**: Missing configuration or permissions

**Solution**:
1. Open app
2. Configure all required settings (API URL, Secret, Device ID, Device Name)
3. Grant notification permission when prompted (Android 13+)
4. Start service from menu

### Getting Complete Debug Information

Run this command and share the output:
```bash
# System info + crash logs
{
  echo "=== DEVICE INFO ==="
  adb shell getprop ro.build.version.release
  adb shell getprop ro.product.model
  echo ""
  echo "=== APP INFO ==="
  adb shell pm list packages | grep sleepy
  adb shell dumpsys package com.sleepy.lsposed | grep -E "versionCode|versionName|enabled"
  echo ""
  echo "=== LSPOSED STATUS ==="
  adb shell ls -la /data/adb/modules/
  echo ""
  echo "=== CRASH LOGS ==="
  adb logcat -d | grep -A 50 "FATAL EXCEPTION"
} > debug_info.txt
```

### Android 15 Specific Checks

```bash
# Check if app has all required permissions
adb shell dumpsys package com.sleepy.lsposed | grep permission

# Check notification permission (Android 13+)
adb shell pm list permissions -g | grep NOTIFICATION

# Check foreground service restrictions
adb shell dumpsys activity services com.sleepy.lsposed
```

### Wireless ADB (for advanced users)

If USB connection is problematic:
```bash
# On device: Settings → Developer Options → Wireless Debugging → ON
# Note the IP address and port

# On computer:
adb connect <device-ip>:port
# Example: adb connect 192.168.1.100:5555
```

## Quick Checklist

Before reporting a crash:
- [ ] USB Debugging is enabled
- [ ] ADB is working (`adb devices` shows device)
- [ ] Captured crash log with `adb logcat`
- [ ] Tried reboot after enabling in LSPosed
- [ ] Confirmed Android version and device model
- [ ] All required settings are configured in app

## Additional Resources

- [Android Developer: ADB](https://developer.android.com/studio/command-line/adb)
- [LSPosed Documentation](https://github.com/LSPosed/LSPosed)
- [Android Logcat Reference](https://developer.android.com/studio/command-line/logcat)
