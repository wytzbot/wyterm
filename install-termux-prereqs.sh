#!/data/data/com.termux/files/usr/bin/bash
set -e
pkg update
pkg install -y openjdk-17 gradle git unzip wget
echo
echo "Termux prerequisites installed."
echo "You still need an Android SDK with platform 35 and build-tools installed."
echo "Then set:"
echo 'export ANDROID_HOME=$HOME/android-sdk'
echo 'export PATH=$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH'
