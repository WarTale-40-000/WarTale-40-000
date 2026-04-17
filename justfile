set dotenv-load := true

# Builds the mod and copies it to the Hytale mods folder
[group('Build')]
build:
    ./gradlew build && cp $(ls build/libs/*.jar | grep -v '\-sources\|\-javadoc') ${HYTALE_MODS_PATH}

# Cleans the build and development server files
[group('Build')]
reset:
    rm -rf .gradle target build devserver assets backups logs mods run universe

# Sets up the development server with the mod, allowing you to test it in a local environment
[group('Dev')]
setup:
    ./gradlew prepareRunServer

# Runs the development server with the mod loaded, allowing you to test it in a local environment
[group('Dev')]
run:
    ./gradlew runServer -Dhotswap=true

# Update the mod manifest
[group('Build')]
[group('Dev')]
manifest:
    ./gradlew updatePluginManifest

# Verify Hot Swap setup
[group('Dev')]
doctor:
    ./gradlew hytaleJvmDoctor

# lint mod
[group('Linting & Formatting')]
lint:
    ./gradlew check

# format mod
[group('Linting & Formatting')]
format:
    ./gradlew spotlessApply

# validate mod format
[group('Linting & Formatting')]
format-check:
    ./gradlew spotlessCheck
