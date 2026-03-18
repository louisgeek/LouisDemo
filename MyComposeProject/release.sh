#!/bin/bash

# Release script for Android app

set -e

echo "Starting release process..."

# Check if version is provided
if [ -z "$1" ]; then
    echo "Usage: ./release.sh <version>"
    echo "Example: ./release.sh 1.0.0"
    exit 1
fi

VERSION=$1

echo "Building release version $VERSION..."

# Clean build
./gradlew clean

# Run tests
echo "Running tests..."
./gradlew test

# Build release APK
echo "Building release APK..."
./gradlew assembleRelease

# Build release AAB
echo "Building release AAB..."
./gradlew bundleRelease

echo "Release build completed!"
echo "APK: app/build/outputs/apk/release/app-release.apk"
echo "AAB: app/build/outputs/bundle/release/app-release.aab"

# Create git tag
echo "Creating git tag v$VERSION..."
git tag -a "v$VERSION" -m "Release version $VERSION"

echo "Release process completed successfully!"
echo "Don't forget to:"
echo "1. Push the tag: git push origin v$VERSION"
echo "2. Upload to Play Console"
echo "3. Create GitHub release"
