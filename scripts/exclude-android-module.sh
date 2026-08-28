#!/bin/sh

set -eu

SETTINGS_FILE="${1:-settings.gradle.kts}"

sed -i '/include(":android/d' "$SETTINGS_FILE"