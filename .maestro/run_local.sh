#!/bin/bash
# Run Maestro UI tests locally against a connected device/emulator
# Prerequisites: maestro installed (curl -Ls "https://get.maestro.mobile.dev" | bash)
set -e

export PATH="$PATH:$HOME/.maestro/bin:$HOME/Work/Android/sdk/platform-tools"
export JAVA_HOME='/Applications/Android Studio.app/Contents/jbr/Contents/Home'

echo "Building debug APK..."
cd "$(dirname "$0")/.."
./gradlew assembleDebug

echo "Installing on device..."
adb install -r app/build/outputs/apk/debug/app-debug.apk

echo "Running Maestro flows..."
maestro test .maestro/

echo "Done! All UI flows passed."
