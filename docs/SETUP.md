# Development Setup Guide

## Prerequisites

### System Requirements
- **macOS**: 12.0+ (for iOS development)
- **Windows/Linux**: Any recent version (for Android development)
- **RAM**: 8GB minimum, 16GB recommended
- **Storage**: 10GB free space for tools and dependencies

### Required Tools
1. **Android Studio**: Hedgehog (2023.1.1) or later
2. **Xcode**: 15.0+ (macOS only, for iOS development)
3. **JDK**: 17 or later
4. **Git**: Latest version

## Project Setup

### 1. Clone Repository
```bash
git clone https://github.com/suyash-frozo/vivaranKMPApp.git
cd vivaranKMPApp
```

### 2. Environment Configuration

Create `local.properties` in project root:
```properties
# Android SDK path
sdk.dir=/Users/username/Library/Android/sdk

# API Configuration
vivaran.api.baseUrl=https://api.vivaran.dev
vivaran.api.key=your_dev_api_key

# Feature Flags
feature.mockAuth=true
feature.offlineMode=false
```

### 3. Firebase Setup

#### Android Configuration
1. Go to [Firebase Console](https://console.firebase.google.com)
2. Create new project or use existing
3. Add Android app with package: `com.vivaran.app`
4. Download `google-services.json`
5. Place in `composeApp/src/androidMain/`

#### iOS Configuration
1. Add iOS app to Firebase project
2. Bundle ID: `com.vivaran.app`
3. Download `GoogleService-Info.plist`
4. Place in `composeApp/src/iosMain/`

### 4. Dependency Installation

The project uses Gradle Version Catalogs. Dependencies are managed in `gradle/libs.versions.toml`.

```bash
# Sync dependencies
./gradlew sync
```

## Building the Project

### Android Build
```bash
# Debug build
./gradlew :composeApp:assembleDebug

# Release build
./gradlew :composeApp:assembleRelease

# Install on device
./gradlew :composeApp:installDebug
```

### iOS Build
```bash
# iOS Simulator (ARM64)
./gradlew :composeApp:iosSimulatorArm64Test

# iOS Device
./gradlew :composeApp:iosArm64Test
```

### Running Tests
```bash
# Unit tests
./gradlew test

# Android tests
./gradlew :composeApp:connectedAndroidTest

# iOS tests
./gradlew :composeApp:iosSimulatorArm64Test
```

## IDE Configuration

### Android Studio
1. Install Kotlin Multiplatform Mobile plugin
2. Import project
3. Sync Gradle files
4. Configure run configurations for Android

### Xcode (iOS)
1. Open `iosApp/iosApp.xcworkspace`
2. Select development team
3. Configure signing certificates
4. Build and run

## Development Workflow

### Branch Strategy
```
main/
├── develop/
├── feature/feature-name
├── bugfix/issue-description
└── release/version-number
```

### Code Style
- **Kotlin**: Follow [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- **Formatting**: Use ktlint for consistent formatting
- **Architecture**: MVVM pattern with Clean Architecture principles

### Git Hooks
Setup pre-commit hooks:
```bash
# Install git hooks
cp scripts/pre-commit .git/hooks/
chmod +x .git/hooks/pre-commit
```

## Debugging

### Android Debugging
- Use Android Studio debugger
- Enable USB debugging on device
- Use `adb logcat` for logs

### iOS Debugging
- Use Xcode debugger
- Enable console logging
- Use Simulator for testing

### Common Issues

#### Build Failures
```bash
# Clean build
./gradlew clean

# Clear Gradle cache
rm -rf ~/.gradle/caches

# Reset Gradle daemon
./gradlew --stop
```

#### Dependency Issues
```bash
# Update dependencies
./gradlew dependencyUpdates

# Verify dependency integrity
./gradlew dependencies
```

## Environment Variables

### Development
```bash
export VIVARAN_ENV=development
export VIVARAN_API_URL=https://api.vivaran.dev
export VIVARAN_LOG_LEVEL=debug
```

### Production
```bash
export VIVARAN_ENV=production
export VIVARAN_API_URL=https://api.vivaran.com
export VIVARAN_LOG_LEVEL=info
```

## Performance Optimization

### Build Performance
```bash
# Enable parallel builds
org.gradle.parallel=true

# Increase heap size
org.gradle.jvmargs=-Xmx4g -XX:MaxMetaspaceSize=1g

# Enable configuration cache
org.gradle.configuration-cache=true
```

### Runtime Performance
- Use R8/ProGuard for release builds
- Enable resource shrinking
- Optimize image assets

## Troubleshooting

### Common Problems

1. **Gradle Sync Issues**
   - Check internet connection
   - Verify Gradle version compatibility
   - Clear Gradle cache

2. **Firebase Integration**
   - Verify configuration files placement
   - Check package name matches
   - Ensure Firebase services are enabled

3. **iOS Build Issues**
   - Update Xcode to latest version
   - Check iOS deployment target
   - Verify provisioning profiles

### Getting Help
- Check [GitHub Issues](https://github.com/suyash-frozo/vivaranKMPApp/issues)
- Review [Kotlin Multiplatform Documentation](https://kotlinlang.org/docs/multiplatform.html)
- Join [Kotlin Slack](https://kotlinlang.slack.com) community