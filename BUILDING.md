# Building Dracarys Mod

## Required
- JDK 17
- Gradle 8.8 (or regenerate a Gradle 8.8 wrapper)
- Internet access on the first build so ForgeGradle can resolve Minecraft/Forge dependencies

## Windows
```bat
gradle build
```

## Linux/macOS
```bash
gradle build
```

The reobfuscated runtime mod JAR will be placed in `build/libs/`.

If you want a normal Gradle wrapper after installing Gradle 8.8 once:
```bash
gradle wrapper --gradle-version 8.8
./gradlew build
```

## Automated CI
A GitHub Actions workflow is included at `.github/workflows/build.yml`. When this project is pushed to GitHub, the workflow installs Temurin JDK 17 and Gradle 8.8, runs the static validator, builds the mod, and uploads `build/libs/*.jar` as an artifact.
