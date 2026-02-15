set dotenv-load := true

# Builds the mod and copies it to the Hytale mods folder
build:
    ./gradlew build && cp build/libs/*.jar ${HYTALE_MODS_PATH}

# Cleans the build and development server files
reset:
    rm -rf .gradle build devserver target assets

# Sets up the development server with the mod, allowing you to test it in a local environment
dev_setup:
    DEV_SERVER_ENABLED=true DEV_SERVER_ALLOW_OP=true ./gradlew setupServer

# Runs the development server with the mod loaded, allowing you to test it in a local environment
dev_run:
    DEV_SERVER_ENABLED=true DEV_SERVER_ALLOW_OP=true ./gradlew runServer
