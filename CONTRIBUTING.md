# Contributing to Sleepy Lsposed

Thank you for your interest in contributing to Sleepy Lsposed! This document provides guidelines and instructions for contributing to the project.

## Code of Conduct

- Be respectful and inclusive
- Welcome newcomers and help them learn
- Focus on constructive feedback
- Follow the project's coding standards

## Getting Started

### Prerequisites

- Android Studio Arctic Fox or later
- JDK 17
- Android SDK with API 34
- LSPosed development environment (optional, for testing)
- Git

### Development Setup

1. **Clone the repository**:
   ```bash
   git clone https://github.com/gongfuture/sleepy-lsposed.git
   cd sleepy-lsposed
   ```

2. **Open in Android Studio**:
   - Open Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned repository
   - Wait for Gradle sync to complete

3. **Build the project**:
   ```bash
   ./gradlew assembleDebug
   ```

4. **Install on device** (with LSPosed):
   ```bash
   ./gradlew installDebug
   ```

## Project Structure

```
sleepy-lsposed/
├── app/
│   ├── src/main/
│   │   ├── java/com/sleepy/lsposed/
│   │   │   ├── config/         # Configuration management
│   │   │   ├── data/           # Data models
│   │   │   ├── network/        # API client
│   │   │   ├── service/        # Background service
│   │   │   ├── ui/             # User interface
│   │   │   ├── utils/          # Utilities
│   │   │   └── *.kt            # Xposed hooks
│   │   ├── res/                # Android resources
│   │   └── AndroidManifest.xml
│   └── build.gradle.kts
├── .github/workflows/          # CI/CD pipelines
├── docs/                       # Documentation (if needed)
├── README.md
├── ARCHITECTURE.md
├── CHANGELOG.md
└── CONTRIBUTING.md
```

## Coding Standards

### Kotlin Style Guide

Follow the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html):

- Use 4 spaces for indentation
- Use camelCase for names
- Use PascalCase for class names
- Place opening braces on the same line
- Use meaningful variable names
- Add KDoc comments for public APIs

### Example

```kotlin
/**
 * Monitors media playback sessions
 * 
 * @param context Android application context
 */
class MediaMonitor(private val context: Context) {
    
    /**
     * Get current media playback information
     * 
     * @return MediaInfo object with current playback state
     */
    fun getCurrentMediaInfo(): MediaInfo {
        // Implementation
    }
}
```

### Android Best Practices

- Follow Material Design 3 guidelines
- Use AndroidX libraries
- Implement proper lifecycle management
- Handle configuration changes
- Use Kotlin Coroutines for async operations
- Minimize memory leaks
- Optimize battery usage

### Code Quality

- Write clean, readable code
- Add comments for complex logic
- Keep functions small and focused
- Avoid deep nesting
- Handle errors gracefully
- Log appropriately (not excessively)

## Making Changes

### Branching Strategy

- `main`: Stable releases
- `develop`: Development branch
- `feature/*`: New features
- `bugfix/*`: Bug fixes
- `hotfix/*`: Critical fixes

### Commit Messages

Use descriptive commit messages following this format:

```
<type>: <subject>

<body>

<footer>
```

**Types**:
- `feat`: New feature
- `fix`: Bug fix
- `docs`: Documentation changes
- `style`: Code style changes (formatting)
- `refactor`: Code refactoring
- `test`: Adding tests
- `chore`: Maintenance tasks

**Example**:
```
feat: Add standalone media reporting mode

- Implement separate device reporting for media
- Add configuration options in settings
- Update API client to support multiple devices

Closes #123
```

### Pull Request Process

1. **Create a feature branch**:
   ```bash
   git checkout -b feature/your-feature-name
   ```

2. **Make your changes**:
   - Write code
   - Add tests if applicable
   - Update documentation
   - Follow coding standards

3. **Test your changes**:
   ```bash
   ./gradlew test
   ./gradlew lintDebug
   ./gradlew assembleDebug
   ```

4. **Commit your changes**:
   ```bash
   git add .
   git commit -m "feat: Your feature description"
   ```

5. **Push to your fork**:
   ```bash
   git push origin feature/your-feature-name
   ```

6. **Create a Pull Request**:
   - Go to GitHub
   - Click "New Pull Request"
   - Select your branch
   - Fill in the PR template
   - Request review

### Pull Request Checklist

- [ ] Code follows project style guidelines
- [ ] Self-review completed
- [ ] Comments added for complex code
- [ ] Documentation updated
- [ ] No new warnings
- [ ] Tests added/updated (if applicable)
- [ ] All tests pass
- [ ] Lint checks pass
- [ ] CHANGELOG.md updated
- [ ] PR description is clear

## Testing

### Running Tests

```bash
# Unit tests
./gradlew test

# Lint checks
./gradlew lintDebug

# Build APK
./gradlew assembleDebug
```

### Manual Testing

For Xposed module functionality:
1. Install the APK on a device with LSPosed
2. Enable the module in LSPosed Manager
3. Select scopes: `android`, `com.android.systemui`
4. Reboot device
5. Open the app and configure settings
6. Start the service
7. Test various scenarios:
   - App switching
   - Media playback
   - Screen on/off
   - Battery changes
   - Network connectivity

## Reporting Issues

### Bug Reports

When reporting bugs, include:
- Clear title and description
- Steps to reproduce
- Expected behavior
- Actual behavior
- Screenshots (if applicable)
- Device information (model, Android version)
- LSPosed version
- App version
- Logs (if available)

### Feature Requests

When requesting features:
- Clear title and description
- Use case and motivation
- Expected behavior
- Alternative solutions considered
- Additional context

## Documentation

### Updating Documentation

- Update README.md for user-facing changes
- Update ARCHITECTURE.md for architectural changes
- Update CHANGELOG.md for all changes
- Add inline comments for complex code
- Update KDoc for API changes

### Documentation Style

- Use clear, concise language
- Include code examples
- Add diagrams for complex flows
- Keep it up-to-date
- Write for beginners when possible

## Release Process

1. Update version in `app/build.gradle.kts`
2. Update CHANGELOG.md
3. Create a git tag: `git tag v1.x.x`
4. Push tag: `git push origin v1.x.x`
5. GitHub Actions will automatically build and create release

## Getting Help

- Check existing documentation
- Search for existing issues
- Ask in discussions
- Contact maintainers

## License

By contributing, you agree that your contributions will be licensed under the AGPL-3.0 License.

## Recognition

Contributors will be:
- Listed in the repository
- Credited in release notes
- Mentioned in the README (for significant contributions)

Thank you for contributing to Sleepy Lsposed! 🎉
