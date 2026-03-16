package com.warhammer.wartale;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.warhammer.wartale.interactions.weapons.ValidateReloadInteraction;
import com.warhammer.wartale.systems.HudTickingSystem;
import com.warhammer.wartale.globalEvents.PlayerEventHandler;
import com.warhammer.wartale.interactions.weapons.ReloadInteraction;
import com.warhammer.wartale.interactions.weapons.ShootInteraction;

import javax.annotation.Nonnull;

public class WartalePlugin extends JavaPlugin {

    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private static WartalePlugin instance;

    public WartalePlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
        LOGGER.atInfo().log("Initializing Wartale...");
    }

    public static WartalePlugin get() {
        return instance;
    }

    @Override
    protected void setup() {
        //Codecs
        this.getCodecRegistry(Interaction.CODEC).register("Warhammer_Weapon_Shoot", ShootInteraction.class, ShootInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC).register("Warhammer_Weapon_Reload", ReloadInteraction.class, ReloadInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC).register("Warhammer_Weapon_ValidateReload", ValidateReloadInteraction.class, ValidateReloadInteraction.CODEC);
        //Global events
        this.getEventRegistry().registerGlobal(AddPlayerToWorldEvent.class, PlayerEventHandler::onAddPlayerToWorld);
        //Systems
        this.getEntityStoreRegistry().registerSystem(new HudTickingSystem());
    }

    @Override
    protected void start() {
        LOGGER.atInfo().log("Wartale started.");
    }

    @Override
    protected void shutdown() {
        LOGGER.atInfo().log("Wartale has been disabled.");
    }
}
