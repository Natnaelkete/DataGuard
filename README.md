# DataGuard - Mobile Data Monitor

A comprehensive Android application for monitoring and controlling device-wide mobile data usage with per-app tracking, background blocking, and intelligent idle detection.

## Features

### 📊 Device-Wide Data Tracking

- Real-time mobile data usage monitoring using `TrafficStats`
- Daily, weekly, and monthly usage statistics
- Current session data gauge
- Historical data storage with Room database

### 📱 Per-App Monitoring

- Track individual app data consumption (TX/RX bytes)
- Top 10 data-consuming apps list
- App-specific usage statistics
- Visual data usage indicators

### 🚫 Background Data Blocking

- Block specific apps' background data access
- NetworkPolicyManager integration
- Persistent blocked apps list
- One-tap toggle for app blocking

### ⚡ Auto Data Toggle

- Automatic mobile data disable on device idle (>5 minutes)
- Manual toggle controls
- ConnectivityManager + TelephonyManager integration
- Settings intent fallback for restricted APIs

### 🔔 Smart Notifications

- High usage alerts (customizable threshold)
- Idle detection notifications
- Per-app high usage warnings
- Foreground service notification

### 📈 Analytics & Export

- Line charts for usage trends
- CSV export functionality
- Share reports via intent
- Detailed usage history

### 🎨 Modern UI

- Material Design 3 theme
- Dark/Light mode support
- Dynamic color support (Android 12+)
- Jetpack Compose UI framework
- Bottom navigation with 4 screens

## Project Structure

```
app/
├── src/main/java/com/dataguard/
│   ├── di/                          # Hilt dependency injection
│   │   ├── DatabaseModule.kt
│   │   ├── RepositoryModule.kt
│   │   └── UtilsModule.kt
│   ├── data/                        # Data layer
│   │   ├── dao/
│   │   │   └── UsageDao.kt
│   │   ├── database/
│   │   │   └── DataGuardDatabase.kt
│   │   ├── entity/
│   │   │   └── UsageEntity.kt
│   │   └── repository/
│   │       └── DataRepository.kt
│   ├── domain/                      # Domain layer (Use Cases)
│   │   └── usecase/
│   │       ├── GetUsageStatsUseCase.kt
│   │       ├── ToggleMobileDataUseCase.kt
│   │       ├── BlockAppDataUseCase.kt
│   │       └── DetectIdleUseCase.kt
│   ├── service/                     # Services & Workers
│   │   ├── DataMonitorService.kt
│   │   ├── DataWorker.kt
│   │   └── BootReceiver.kt
│   ├── ui/                          # UI layer (Compose)
│   │   ├── screen/
│   │   │   ├── DashboardScreen.kt
│   │   │   ├── AppsScreen.kt
│   │   │   ├── StatsScreen.kt
│   │   │   └── SettingsScreen.kt
│   │   ├── viewmodel/
│   │   │   ├── DashboardViewModel.kt
│   │   │   ├── AppsViewModel.kt
│   │   │   └── SettingsViewModel.kt
│   │   ├── theme/
│   │   │   ├── Theme.kt
│   │   │   ├── Color.kt
│   │   │   └── Type.kt
│   │   └── MainActivity.kt
│   ├── utils/                       # Utilities
│   │   ├── NotificationHelper.kt
│   │   ├── PermissionHelper.kt
│   │   ├── DataFormatter.kt
│   │   └── CsvExporter.kt
│   └── DataGuardApp.kt
├── src/main/res/
│   ├── values/
│   │   ├── strings.xml
│   │   └── themes.xml
│   └── xml/
│       ├── data_extraction_rules.xml
│       ├── backup_descriptor.xml
│       └── file_paths.xml
├── src/test/java/com/dataguard/
│   ├── domain/usecase/
│   │   ├── GetUsageStatsUseCaseTest.kt
│   │   └── DetectIdleUseCaseTest.kt
│   └── utils/
│       └── DataFormatterTest.kt
├── build.gradle.kts
├── proguard-rules.pro
└── AndroidManifest.xml
```

## Requirements

- **Android API Level**: 34+ (Android 14)
- **Java Version**: 17
- **Gradle**: 8.2.0
- **Kotlin**: 1.9.22

## Dependencies

### Core Android

- `androidx.core:core-ktx:1.13.1`
- `androidx.lifecycle:lifecycle-runtime-ktx:2.8.3`
- `androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3`
- `androidx.activity:activity-compose:1.9.0`

### Compose UI

- `androidx.compose.ui:ui:1.6.4`
- `androidx.compose.material3:material3:1.2.0`
- `androidx.compose.material:material-icons-extended:1.6.4`
- `androidx.navigation:navigation-compose:2.7.7`

### Dependency Injection

- `com.google.dagger:hilt-android:2.51.1`
- `androidx.hilt:hilt-navigation-compose:1.2.0`
- `androidx.hilt:hilt-work:1.2.0`

### Database

- `androidx.room:room-runtime:2.6.1`
- `androidx.room:room-ktx:2.6.1`

### Background Tasks

- `androidx.work:work-runtime-ktx:2.9.1`

### Charts & Visualization

- `com.github.PhilJay:MPAndroidChart:v3.1.0`

### Image Loading

- `io.coil-kt:coil-compose:2.6.0`

### Permissions

- `com.google.accompanist:accompanist-permissions:0.34.0`

### Serialization

- `com.google.code.gson:gson:2.10.1`

## Permissions Required

```xml
<uses-permission android:name="android.permission.PACKAGE_USAGE_STATS" />
<uses-permission android:name="android.permission.QUERY_ALL_PACKAGES" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.CHANGE_NETWORK_STATE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
<uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" />
```

## Building the App

### Local Build

```bash
# Clone the repository
git clone <repository-url>
cd mobile-data-monitor

# Build debug APK
./gradlew assembleDebug

# Build release APK (unsigned)
./gradlew assembleRelease

# Run tests
./gradlew testDebugUnitTest

# Run lint
./gradlew lint
```

### GitHub Actions Build

The project includes a GitHub Actions workflow that automatically:

1. Builds debug and release APKs
2. Runs unit tests
3. Performs lint checks
4. Uploads artifacts for download

**Artifacts Available:**

- `dataguard-debug` - Debug APK for testing
- `dataguard-release-unsigned` - Release APK (unsigned)
- `build-reports` - Build reports
- `test-reports` - Unit test reports
- `lint-reports` - Lint analysis reports

## Installation

### From GitHub Actions

1. Go to Actions tab in GitHub
2. Select the latest successful build
3. Download the desired APK from artifacts
4. Transfer to Android device
5. Install via: `adb install app-debug.apk`

### From Local Build

```bash
./gradlew installDebug
```

## Usage

### First Launch

1. Grant required permissions:

   - Usage Stats Permission (Settings → Apps → Special app access → Usage access)
   - Battery Optimization Exemption
   - Notification Permission (Android 13+)

2. The app will start monitoring data usage automatically

### Dashboard

- View current session data usage
- See daily, weekly, monthly statistics
- Toggle mobile data on/off
- Refresh data manually

### Apps Screen

- Browse top data-consuming apps
- Toggle background data blocking per app
- View TX/RX breakdown
- Real-time app usage tracking

### Statistics

- View usage trends over time
- Export data as CSV
- Share reports via email/messaging

### Settings

- Configure data limits (daily/weekly/monthly)
- Check permission status
- Reset all statistics
- View app information

## Architecture

### Clean Architecture

- **Presentation Layer**: Jetpack Compose screens + ViewModels
- **Domain Layer**: Use cases with business logic
- **Data Layer**: Room database + repositories

### Dependency Injection

- Hilt for automatic dependency injection
- Singleton scoped services
- Constructor injection in ViewModels

### State Management

- StateFlow for reactive UI updates
- ViewModel for lifecycle-aware state
- Coroutines for async operations

## Testing

### Unit Tests

```bash
./gradlew testDebugUnitTest
```

Test coverage includes:

- `GetUsageStatsUseCaseTest` - Usage stats retrieval
- `DetectIdleUseCaseTest` - Idle detection logic
- `DataFormatterTest` - Data formatting utilities

### Test Scenarios

- High data usage detection (>500MB)
- Idle state detection (>5 minutes)
- App blocking functionality
- Data toggle operations

## Performance Optimizations

1. **Efficient Data Polling**: 30-second intervals with WorkManager
2. **Database Indexing**: Optimized queries for fast retrieval
3. **Memory Management**: Proper coroutine cancellation
4. **Battery Optimization**: Constraints-aware background work
5. **ProGuard Minification**: Reduced APK size

## Security

- **Data Encryption**: Room database with SQLCipher support
- **Permission Validation**: Runtime permission checks
- **Network Security**: Cleartext traffic disabled
- **Backup Configuration**: Selective data backup

## Troubleshooting

### App Not Detecting Data Usage

1. Check if Usage Stats permission is granted
2. Go to Settings → Apps → Special app access → Usage access
3. Enable DataGuard
4. Restart the app

### Mobile Data Toggle Not Working

1. Ensure CHANGE_NETWORK_STATE permission is granted
2. Try manual toggle via Settings intent
3. Check if device has dual SIM (may require manual toggle)

### High Battery Drain

1. Disable background data blocking for unused apps
2. Increase idle detection threshold in settings
3. Check if foreground service is running properly

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit changes (`git commit -m 'Add amazing feature'`)
4. Push to branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see LICENSE file for details.

## Support

For issues, feature requests, or questions:

- Open an issue on GitHub
- Check existing issues for solutions
- Provide device info and Android version

## Changelog

### Version 1.0.0 (Initial Release)

- Device-wide data tracking
- Per-app monitoring
- Background data blocking
- Auto data toggle on idle
- Smart notifications
- Data export (CSV)
- Material Design 3 UI
- Dark mode support

## Future Enhancements

- [ ] VPN data tracking
- [ ] WiFi data monitoring
- [ ] Predictive usage alerts
- [ ] Custom app grouping
- [ ] Data usage comparison
- [ ] Cloud backup support
- [ ] Widget support
- [ ] Wear OS companion app
