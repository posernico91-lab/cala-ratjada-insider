#!/bin/bash
# =============================================================================
# BUILD ALL 25 COUNTRY AABs (Android App Bundles)
# =============================================================================
# Usage: ./build-all-aabs.sh [flavor]
#   No argument  = build ALL 25 flavors
#   With flavor  = build only that flavor (e.g. ./build-all-aabs.sh germany)
# =============================================================================

set -e

FLAVORS=(
    germany usa brazil peru mexico uk albania georgia
    southafrica nigeria kenya egypt botswana capeverde
    morocco tanzania vietnam bhutan thailand tajikistan
    saudiarabia dubai japan australia europe
)

OUTPUT_DIR="build/aab-output"
mkdir -p "$OUTPUT_DIR"

build_aab() {
    local flavor=$1
    local capitalized="$(tr '[:lower:]' '[:upper:]' <<< ${flavor:0:1})${flavor:1}"
    local task="bundle${capitalized}Release"

    echo "============================================="
    echo "  Building AAB: $flavor"
    echo "  Task: $task"
    echo "============================================="

    ./gradlew "$task" --no-daemon

    # Copy AAB to output directory
    local aab_path="app/build/outputs/bundle/${flavor}Release/app-${flavor}-release.aab"
    if [ -f "$aab_path" ]; then
        cp "$aab_path" "$OUTPUT_DIR/${flavor}-release.aab"
        echo "  -> $OUTPUT_DIR/${flavor}-release.aab"
    else
        echo "  WARNING: AAB not found at $aab_path"
    fi
    echo ""
}

# Main
if [ -n "$1" ]; then
    echo "Building single flavor: $1"
    build_aab "$1"
else
    echo "Building all 25 country AABs..."
    echo ""
    for flavor in "${FLAVORS[@]}"; do
        build_aab "$flavor"
    done
fi

echo "============================================="
echo "  BUILD COMPLETE"
echo "  AABs in: $OUTPUT_DIR/"
echo "============================================="
ls -lh "$OUTPUT_DIR/"
