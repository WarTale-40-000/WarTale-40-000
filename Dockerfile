FROM eclipse-temurin:25-jre

WORKDIR /hytale

# Server binaries - image-only, never mounted as a volume
COPY hytale-home/install/pre-release/package/game/latest/Server ./bin
COPY hytale-home/install/pre-release/package/game/latest/Assets.zip ./Assets.zip

# Mod JAR - image-only, never mounted as a volume
RUN mkdir -p ./mods
COPY build/libs/Wartale-*.jar ./mods/

EXPOSE 5520/udp

# Runtime data directory - this is what gets volume-mounted
WORKDIR /hytale/data

ENTRYPOINT ["java", "-Xms4G", "-Xmx8G", "-Djava.library.path=/hytale/bin", "-XX:AOTCache=HytaleServer.aot", "-jar", "/hytale/bin/HytaleServer.jar", "--assets", "/hytale/Assets.zip", "--mods", "/hytale/mods", "--allow-op", "--disable-sentry"]
