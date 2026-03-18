#!/bin/bash

echo "Checking for dependency updates..."

./gradlew dependencyUpdates

echo "Check complete. See build/dependencyUpdates/report.txt for details."
