#!/bin/sh
if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
fi
echo "Gradle is not installed and gradle-wrapper.jar is not bundled in this generated workspace." >&2
echo "Install Gradle 8.8 or import build.gradle in IntelliJ with JDK 17, then run: gradle build" >&2
exit 1
