# Sleepy Lsposed Architecture

## Overview

Sleepy Lsposed is an Xposed/LSPosed module that monitors Android device activity and reports it to a Sleepy server. The module uses modern Android development practices and follows Material Design 3 guidelines.

## Architecture Diagram

```
┌─────────────────────────────────────────────────────────────┐
│                         Android System                        │
│  ┌────────────────────────────────────────────────────────┐  │
│  │         ActivityManagerService (Hooked)                │  │
│  │  - Tracks app lifecycle changes                        │  │
│  │  - Detects foreground app switches                     │  │
│  └────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                            ↓ Broadcast Intent
┌─────────────────────────────────────────────────────────────┐
│                    Sleepy Lsposed Module                      │
│  ┌────────────────────────────────────────────────────────┐  │
│  │           SleepyMonitorService (Foreground)            │  │
│  │  - Receives foreground app updates                     │  │
│  │  - Monitors screen state                               │  │
│  │  - Checks battery info                                 │  │
│  │  - Queries media sessions                              │  │
│  │  - Builds status reports                               │  │
│  │  - Sends HTTP requests to server                       │  │
│  └────────────────────────────────────────────────────────┘  │
│  ┌────────────────────────────────────────────────────────┐  │
│  │              SettingsActivity (UI)                     │  │
│  │  - Material Design 3 interface                         │  │
│  │  - Preference configuration                            │  │
│  │  - Service control                                     │  │
│  └────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────┘
                            ↓ HTTP POST (JSON)
┌─────────────────────────────────────────────────────────────┐
│                        Sleepy Server                          │
│  - Receives device status updates                            │
│  - Tracks user activity                                       │
└─────────────────────────────────────────────────────────────┘
```

## Components

### 1. Xposed Module (`SleepyXposedModule`)

**Purpose**: Hook into Android system to detect app changes

**Implementation**:
- Hooks `ActivityRecord.setState()` in `com.android.server.wm` package
- Detects when activities enter RESUMED state (foreground)
- Extracts package name and resolves to app name
- Broadcasts changes to the monitoring service

**Key Methods**:
- `handleLoadPackage()`: Entry point, filters for "android" package
- `hookActivityManagerService()`: Sets up the hook
- `notifyForegroundAppChanged()`: Broadcasts app changes

### 2. Background Service (`SleepyMonitorService`)

**Purpose**: Continuously monitor device state and report to server

**Lifecycle**:
- Started as foreground service with persistent notification
- Runs independently of UI
- Uses coroutines for async operations
- Handles service lifecycle (onCreate, onStartCommand, onDestroy)

**Monitoring Loop**:
```kotlin
while (isActive) {
    1. Check screen state
    2. Get current foreground app
    3. Read battery info (if enabled)
    4. Query media sessions (if enabled)
    5. Build status string
    6. Send to server (if changed or bypass disabled)
    7. Sleep for configured interval
}
```

**Key Features**:
- BroadcastReceiver for foreground app updates
- PowerManager integration for screen state
- Configurable check intervals
- Duplicate status filtering
- Graceful shutdown with final status report

### 3. Media Monitoring (`MediaMonitor`)

**Purpose**: Detect and track media playback

**Implementation**:
- Uses `MediaSessionManager` API
- Queries active media sessions
- Extracts metadata (title, artist, album)
- Checks playback state

**Modes**:
- **Combined**: Appends media info to app status
- **Standalone**: Reports media as separate device

### 4. Configuration (`ConfigManager`)

**Purpose**: Manage user preferences

**Storage**: SharedPreferences

**Settings**:
- Server configuration (URL, secret)
- Device identification (ID, name)
- Behavior (interval, bypass, battery)
- Media options (enabled, mode, device info)

**Validation**:
- Checks required fields are filled
- Returns validation status

### 5. UI (`SettingsActivity`)

**Purpose**: User interface for configuration

**Design**: Material Design 3
- AppCompat with Material components
- PreferenceFragmentCompat for settings
- MaterialAlertDialogBuilder for dialogs
- Toolbar with menu actions

**Features**:
- Preference categories with headers
- Input validation
- Service start/stop controls
- About dialog

### 6. Network (`SleepyApiClient`)

**Purpose**: HTTP communication with server

**Implementation**:
- OkHttp client with timeouts
- JSON POST requests
- Coroutine-based async API
- Error handling and logging

**Request Format**:
```json
{
  "secret": "auth_secret",
  "id": "device_id",
  "show_name": "Device Name",
  "using": true,
  "app_name": "[🔋85%⚡] 前台应用: App Name\n【正在播放】: ♪Title - Artist"
}
```

### 7. Utilities

**BatteryUtil**:
- Reads battery broadcast intent
- Calculates percentage
- Detects charging state

**Data Models**:
- `DeviceStatus`: Status report data
- `MediaInfo`: Media playback info
- `BatteryInfo`: Battery state

## Data Flow

### App Change Detection

```
ActivityRecord.setState() [Android System]
    ↓ Hook intercepts
SleepyXposedModule
    ↓ Extract package & app name
Broadcast Intent
    ↓ Received by
SleepyMonitorService.foregroundAppReceiver
    ↓ Update current app
Main monitoring loop
    ↓ Build status
Send to server
```

### Status Reporting

```
Timer tick (check interval)
    ↓
Check screen state
    ↓ If screen on
Get foreground app (from receiver)
    ↓
Get battery info (if enabled)
    ↓
Get media info (if enabled)
    ↓
Build status string
    ↓ If changed or bypass disabled
Send HTTP POST request
    ↓
Update last sent status
```

## Threading Model

- **Main Thread**: UI operations, BroadcastReceiver
- **Coroutine (Main)**: Service lifecycle, coordination
- **Coroutine (IO)**: HTTP requests, file operations
- **Xposed Hook Thread**: System process context

## Security Considerations

1. **Secret Protection**: 
   - Stored in SharedPreferences (device-encrypted)
   - Never logged in plain text
   - Transmitted over HTTPS (user configured)

2. **Permissions**:
   - INTERNET: Network communication
   - FOREGROUND_SERVICE: Background monitoring
   - BIND_NOTIFICATION_LISTENER_SERVICE: Media detection

3. **Hook Safety**:
   - Try-catch around hook code
   - Graceful failure handling
   - No modifications to system state

## Performance

- **Memory**: ~10-20 MB (service + libraries)
- **CPU**: Negligible (mostly idle, brief spikes on checks)
- **Battery**: ~1-2% per day (configurable interval)
- **Network**: Minimal (small JSON payloads, configurable frequency)

## Best Practices Applied

1. **Android Architecture**:
   - Separation of concerns
   - Repository pattern (ConfigManager)
   - Lifecycle awareness

2. **Kotlin**:
   - Null safety
   - Coroutines for async
   - Data classes
   - Extension functions (toDisplayString)

3. **Material Design 3**:
   - Modern color palette
   - Proper spacing and typography
   - Consistent components
   - Accessibility support

4. **Error Handling**:
   - Try-catch blocks
   - Logging
   - Graceful degradation
   - User feedback (toasts, dialogs)

## Future Enhancements

Potential improvements:
- Notification listener service for more reliable media detection
- Encrypted credential storage
- Multiple server endpoints
- Custom status format templates
- Usage statistics and graphs
- Accessibility service mode (fallback)
- Widget for quick status view
