# WyTerm — Termux Build Guide

This project can be built from an Android phone using Termux.

## 1. Install Termux

Use a current Termux distribution. Open Termux and run:

    pkg update
    pkg upgrade
    pkg install git unzip openjdk-17 gradle

Or run:

    bash install-termux-prereqs.sh

## 2. Put the project in Termux

Extract this ZIP into Termux storage, or clone your Git repository.

Example:

    termux-setup-storage
    cd ~/storage/downloads
    unzip WyTerm-Termux.zip
    cd WyTerm-Termux

## 3. Android SDK

An Android APK build needs the Android SDK. Install Android command-line tools/SDK components appropriate for your device and make sure Android API 35 and build-tools are available.

Set your SDK path, for example:

    export ANDROID_HOME=$HOME/android-sdk
    export PATH=$ANDROID_HOME/platform-tools:$ANDROID_HOME/cmdline-tools/latest/bin:$PATH

If your SDK is somewhere else, use that path instead.

## 4. Build

From the WyTerm project root:

    gradle assembleDebug --no-daemon

Or:

    bash termux-build.sh

The debug APK will be under:

    app/build/outputs/apk/debug/

## 5. Install the APK

If the APK is in Downloads:

    cp app/build/outputs/apk/debug/app-debug.apk ~/storage/downloads/

Then tap the APK in your Android file manager and allow installation from that source when Android asks.

## Important

This is a Termux-buildable Android project, not a full Termux replacement yet.

The current terminal uses Android's local `sh` process. Python, Node.js, PHP, package management, persistent shells, and a full Linux userspace are separate features that need to be implemented next.

No GitHub Actions/workflow is required to build it.

## Phone-only workflow

GitHub = source storage
WyDev = edit repository files
Termux = build APK
Android = test APK

WyDev:
https://wydev.vercel.app

## Google Play Billing

The Pro purchase should be configured in Google Play Console as a one-time product. Do not test production purchases directly. Use Play's internal testing track and license/test accounts.
