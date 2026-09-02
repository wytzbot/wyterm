#!/usr/bin/env bash
set -e
gradle assembleDebug --no-daemon
echo "Built: app/build/outputs/apk/debug/app-debug.apk"
