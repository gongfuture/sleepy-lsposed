# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [1.0.0] - 2024-01-01

### Added
- Initial release of Sleepy Lsposed
- Foreground app monitoring via Xposed hooks
- Media playback detection using MediaSessionManager
- Material Design 3 configuration interface
- Configurable status reporting to Sleepy server
- Battery information in status reports
- Flexible media reporting modes (combined/standalone)
- Background monitoring service with foreground notification
- Comprehensive configuration options
- English and Chinese documentation
- GitHub Actions CI/CD workflows
- Recommended hook scopes for LSPosed

### Features
- Monitor current foreground app by name (not package)
- Detect active media playback (title, artist, album)
- Report device status to configurable API endpoint
- Skip duplicate status updates to save bandwidth
- Screen state awareness (stop reporting when screen off)
- Configurable check intervals
- Battery level and charging status
- JSON API integration
- Material Design 3 UI with preference screen

### Technical
- Built with Kotlin
- Minimum Android 8.0 (API 26)
- Target Android 14 (API 34)
- Xposed API 82
- OkHttp for networking
- Kotlin Coroutines for async operations
- Material Design 3 components
