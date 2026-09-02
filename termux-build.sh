#!/data/data/com.termux/files/usr/bin/bash
set -e
echo "== WyTerm Termux build =="
command -v java >/dev/null || { echo "Java is missing. Install it first."; exit 1; }
command -v gradle >/dev/null || { echo "Gradle is missing. Install it first."; exit 1; }
[ -n "$ANDROID_HOME" ] || { echo "ANDROID_HOME is not set."; echo "Set it to your Android SDK directory."; exit 1; }
"$ANDROID_HOME/platform-tools/adb" version >/dev/null 2>&1 || true
echo "Using Android SDK: $ANDROID_HOME"
gradle --version
gradle assembleDebug --no-daemon
echo
echo "APK:"
find app/build/outputs/apk -type f -name "*.apk" -print
