package com.warhammer.wartale.utils;

import com.hypixel.hytale.logger.HytaleLogger;
import com.warhammer.wartale.WartalePlugin;

public final class PluginLogger {
    private static HytaleLogger logger;

    private PluginLogger() {
    }

    public static void init(WartalePlugin plugin) {
        logger = plugin.getLogger();
    }

    public static HytaleLogger get() {
        if (logger == null) {
            logger = HytaleLogger.get("Economy");
        }

        return logger;
    }
}
