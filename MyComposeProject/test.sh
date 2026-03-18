#!/bin/bash

# Test script for Android app

set -e

echo "Running all tests..."

# Unit tests
echo "Running unit tests..."
./gradlew test

# Generate test report
echo "Generating test report..."
./gradlew testDebugUnitTest

# Code coverage (if configured)
# ./gradlew jacocoTestReport

echo "Test report available at: app/build/reports/tests/testDebugUnitTest/index.html"

# Lint checks
echo "Running lint checks..."
./gradlew lint

echo "Lint report available at: app/build/reports/lint-results.html"

echo "All tests completed!"
