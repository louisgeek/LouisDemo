#!/bin/bash

echo "Cleaning project..."

# Clean Gradle
./gradlew clean

# Remove build directories
find . -type d -name "build" -exec rm -rf {} +

# Remove .gradle cache
rm -rf .gradle

echo "Clean complete!"
