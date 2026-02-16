package com.warhammer.wartale.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.hypixel.hytale.logger.HytaleLogger.Api;
import com.warhammer.wartale.utils.PluginLogger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.logging.Level;

public class PluginConfig {
    private static final Gson GSON = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(Instant.class, new TypeAdapter<Instant>() {
                @Override
                public void write(JsonWriter out, Instant value) throws IOException {
                    out.value(value == null ? null : value.toString());
                }

                @Override
                public Instant read(JsonReader in) throws IOException {
                    String s = in.nextString();
                    return s == null ? null : Instant.parse(s);
                }
            })
            .create();

    private final WeaponConfig weapons = new WeaponConfig();

    public static PluginConfig load(Path path) {
        if (Files.exists(path)) {
            try {
                String content = Files.readString(path);
                PluginConfig config = (PluginConfig) GSON.fromJson(content, PluginConfig.class);
                PluginLogger.get().at(Level.INFO).log("Loaded configuration from %s", path.getFileName());
                return config;
            } catch (IOException var3) {
                throw new RuntimeException("Failed to load config", var3);
            } catch (JsonSyntaxException var4) {
                throw new RuntimeException("Invalid JSON syntax in config file: " + var4.getMessage(), var4);
            }
        } else {
            PluginConfig config = new PluginConfig();
            config.save(path);
            PluginLogger.get().at(Level.INFO).log("Created default configuration at %s", path.getFileName());
            return config;
        }
    }

    public static PluginConfig reload(Path path, PluginConfig fallback) {
        if (!Files.exists(path)) {
            PluginLogger.get().at(Level.WARNING).log("Config file not found at %s, keeping current config", path);
            return fallback;
        } else {
            try {
                String content = Files.readString(path);
                PluginConfig config = (PluginConfig) GSON.fromJson(content, PluginConfig.class);
                PluginLogger.get().at(Level.INFO).log("Reloaded configuration from %s", path.getFileName());
                return config;
            } catch (IOException var4) {
                ((Api) PluginLogger.get().at(Level.SEVERE).withCause(var4)).log("Failed to reload config, keeping current config");
                return fallback;
            } catch (JsonSyntaxException var5) {
                PluginLogger.get().at(Level.SEVERE).log("Invalid JSON syntax in config file: %s. Keeping current config.", var5.getMessage());
                return fallback;
            }
        }
    }

    public void save(Path path) {
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, GSON.toJson(this));
        } catch (IOException var3) {
            throw new RuntimeException("Failed to save config", var3);
        }
    }

    public WeaponConfig getWeapons() {
        return this.weapons;
    }
}
