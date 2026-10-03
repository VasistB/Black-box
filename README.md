# BlackBox - Sensor Hardware Puzzle Game

**BlackBox** is a modern, minimalist Android puzzle game inspired by iOS Blackbox. Players solve interactive puzzles not by tapping buttons, but by interacting with physical hardware sensors, ambient environment parameters, gestures, and device states.

---

## 🎮 Game Challenges & Sensor Mechanics

| Challenge | Primary Hardware Sensor | Objective & Puzzle Mechanic |
| :--- | :--- | :--- |
| **Gravity Inversion** | Accelerometer & Gravity Sensor | Flip phone completely upside down to invert gravity |
| **Spark of Life** | Battery / Power Receiver | Connect a charger cable (USB / AC) |
| **Voice of Thunder** | Microphone (`AudioRecord` dB) | Shout or talk loudly into the microphone (>75 dB) |
| **Dark Meditation** | Power Button / Light / Proximity | Turn off screen / hold power button / cover light sensor for 5 sec |
| **Sonic Spectrum** | `AudioManager` Speaker Volume | Mute speaker volume to 0% or crank to 100% Max |
| **The Wanderer** | Fused Location / GPS Tracker | Walk/move 10 meters in the real world |
| **Earthquake** | Motion / Accelerometer | Shake the phone vigorously |

---

## 🛠️ Tech Stack & Key Libraries

- **UI Framework**: [Jetpack Compose](https://developer.android.com/jetpack/compose) with Cyberpunk Dark Blackbox Theme
- **Dependency Injection**: [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
- **Local Persistence**: [Room 2.8](https://developer.android.com/training/data-storage/room) with KSP
- **Asynchronous & Reactive Streams**: [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html) & `StateFlow`
- **Hardware Integration**:
  - `SensorManager` (Accelerometer, Gravity, Ambient Light, Proximity)
  - `AudioRecord` (Real-time decibel dB amplitude calculation)
  - `AudioManager` & `ContentObserver` (Speaker volume monitoring)
  - `BroadcastReceiver` (Battery charging status, screen ON/OFF timing)
  - `LocationManager` (GPS distance tracking)

---

## 🏗️ Architecture Breakdown

```
com.vasist.blackbox
├── BlackBoxApplication.kt            # @HiltAndroidApp entry
├── MainActivity.kt                   # Host Activity dispatches GameScreen
│
├── core
│   ├── hardware                      # Hardware & Sensor Flow Monitors
│   │   ├── OrientationSensorManager.kt
│   │   ├── ChargingStateMonitor.kt
│   │   ├── AudioVolumeMonitor.kt
│   │   ├── MicrophoneLoudnessMonitor.kt
│   │   ├── ScreenPowerStateMonitor.kt
│   │   ├── LightProximityMonitor.kt
│   │   ├── LocationTracker.kt
│   │   └── HardwareSensorRepository.kt # Reactive stream merging all sensors
│   ├── database                      # Room DB, ChallengeDao, ChallengeEntity
│   └── di                            # Hilt Modules (Database, Dispatchers, Repositories)
│
├── data
│   └── repository                    # ChallengeRepositoryImpl
│
├── domain                            # Pure Kotlin Domain & Business Logic
│   ├── model                         # Challenge, ChallengeType, SensorReadings
│   ├── repository                    # ChallengeRepository
│   └── usecase                       # EvaluateChallengeProgressUseCase, ObserveSensorReadingsUseCase
│
└── ui
    └── game                          # Cyberpunk Blackbox Grid Matrix UI & Live Sensor HUD
```

---

## 🚀 Building & Running

```bash
# Build Debug APK
./gradlew :app:assembleDebug

# Install on connected Android Device
./gradlew :app:installDebug
```
