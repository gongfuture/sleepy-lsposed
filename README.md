# Sleepy Lsposed

A modern Xposed/Lsposed module for monitoring Android device activity and media playback, built with Kotlin and Material Design 3.

## Features

- 🎯 **Foreground App Monitoring**: Tracks the currently active application (by name, not package)
- 🎵 **Media Playback Detection**: Monitors music and video playback from any media app
- 🔋 **Battery Information**: Includes battery level and charging status in reports
- 📡 **Flexible Reporting**: Choose to report media separately or combined with app status
- 🎨 **Material Design 3**: Modern, intuitive configuration interface
- ⚙️ **Fully Configurable**: All settings accessible through in-app preferences
- 🚀 **Efficient**: Minimal battery impact with configurable check intervals

## Requirements

- Android 8.0 (API 26) or higher
- Xposed Framework or LSPosed module framework
- Notification listener permission for media detection

## Installation

1. Install LSPosed on your device
2. Download the latest APK from [Releases](https://github.com/gongfuture/sleepy-lsposed/releases)
3. Install the APK
4. Enable the module in LSPosed Manager
5. Select the recommended scopes:
   - `android` (System Framework)
   - `com.android.systemui` (System UI)
6. Reboot your device

## Configuration

1. Open the Sleepy Lsposed app
2. Configure the required settings:

### Server Configuration
- **API URL**: Your Sleepy server endpoint (must end with `/device/set`)
- **Secret Key**: Authentication secret for API requests

### Device Configuration
- **Device ID**: Unique identifier for this device
- **Device Display Name**: Friendly name shown on the server

### Behavior Settings
- **Check Interval**: Time between status checks (milliseconds, default: 3000)
- **Skip Duplicate Updates**: Don't send requests if status hasn't changed
- **Include Battery Info**: Add battery level and charging status to reports

### Media Settings
- **Enable Media Detection**: Monitor and report media playback
- **Media Report Mode**: 
  - **Combined**: Include media info in app status
  - **Standalone**: Report media as a separate device
- **Media Device ID/Name**: Used when reporting media separately

3. Start the service from the menu (three dots in top-right)

## Architecture

### Components

- **SleepyXposedModule**: Hooks into Android system to monitor foreground apps
- **SleepyMonitorService**: Background service that periodically checks status
- **MediaMonitor**: Monitors active media sessions
- **SleepyApiClient**: Handles HTTP communication with the server
- **ConfigManager**: Manages user preferences
- **SettingsActivity**: Material Design 3 configuration interface

### Hook Mechanism

The module hooks into `com.android.server.wm.ActivityRecord.setState()` to detect when apps enter the foreground. This provides real-time app switching detection without polling.

### Media Detection

Uses Android's `MediaSessionManager` API to detect active media playback from any app that properly implements media sessions (Spotify, YouTube Music, etc.).

## API Protocol

The module sends JSON POST requests to the configured API endpoint:

```json
{
  "secret": "your_secret",
  "id": "device_id",
  "show_name": "Device Name",
  "using": true,
  "app_name": "[🔋85%⚡] 前台应用: Chrome\n【正在播放】: ♪Song Title - Artist"
}
```

## Development

### Building

```bash
./gradlew assembleRelease
```

The APK will be in `app/build/outputs/apk/release/`

### Project Structure

```
app/src/main/
├── java/com/sleepy/lsposed/
│   ├── config/          # Configuration management
│   ├── data/            # Data models
│   ├── network/         # API client
│   ├── service/         # Background service
│   ├── ui/              # User interface
│   ├── utils/           # Utility classes
│   └── *.kt             # Xposed hooks
├── res/                 # Resources
└── AndroidManifest.xml
```

### Technologies

- **Language**: Kotlin
- **UI**: Material Design 3
- **Networking**: OkHttp
- **Async**: Kotlin Coroutines
- **Framework**: Xposed/LSPosed API

## Best Practices Implemented

- ✅ Material Design 3 guidelines
- ✅ Android Architecture Components
- ✅ Kotlin Coroutines for async operations
- ✅ Foreground service for background work
- ✅ Proper notification channels
- ✅ SharedPreferences for configuration
- ✅ Efficient battery usage
- ✅ Error handling and logging

## Troubleshooting

### Module not working
1. Ensure LSPosed is properly installed
2. Check that the module is enabled in LSPosed Manager
3. Verify the correct scopes are selected (android, com.android.systemui)
4. Reboot after enabling/changing scopes

### Media detection not working
1. Grant notification listener permission
2. Ensure the media app properly implements MediaSession API
3. Check that "Enable Media Detection" is turned on in settings

### Status not updating
1. Verify all configuration fields are filled
2. Check your API URL is correct
3. Review logs for network errors
4. Ensure the service is running (check notification)

## Credits

Based on the [Sleepy project](https://github.com/sleepy-project/sleepy):
- JavaScript client: `autoxjs_device.js`
- Python client: `win_device_ds.py`

## License

This project is licensed under the GNU Affero General Public License v3.0 (AGPL-3.0).

See [LICENSE](LICENSE) file for details.

## Contributing

Contributions are welcome! Please:
1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Submit a pull request

## Support

For issues, questions, or suggestions, please open an issue on GitHub.
