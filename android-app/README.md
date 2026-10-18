# Arena Trainer

An original, offline Android training-game prototype inspired only by the broad
2D side-view arena-shooter format observed in the supplied APK. It does not
include the APK's code, branding, graphics, audio, maps, network services, or
other extracted assets.

## Current prototype

- Responsive menu and one generated training arena
- Touch movement joystick, jump, aim, and sustained fire
- Moving target dummies, hit points, and a hit counter
- Pause/resume and return-to-menu controls
- No network or storage permissions

The prototype uses a custom Android `View` and Canvas drawing, with no game
engine or third-party runtime dependency. It is not a complete replacement for
the original game: accounts, online play, matchmaking, progression, and the
original maps/content are out of scope.

## Android compatibility

The Gradle project is configured for `compileSdk`/`targetSdk` 37 (Android 17),
`minSdk` 26, and Java 17 bytecode. The layout is drawn in a screen-relative coordinate space and remains usable
when the Samsung Galaxy S25 Ultra rotates or applies large-screen orientation
policies. No S25 Ultra device-specific APIs are required.

Open `android-app/` in Android Studio with Android SDK Platform 37 installed,
then build and run the `app` configuration on an Android 17 emulator or device.
The package ID `dev.rebuild.arena` and app name are prototype defaults and
should be replaced with the owner's production identifiers before publishing.
