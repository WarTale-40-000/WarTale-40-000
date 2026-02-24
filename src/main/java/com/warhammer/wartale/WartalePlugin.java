package com.warhammer.wartale;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import javax.annotation.Nonnull;

import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.components.WeaponHudTickingSystem;
import com.warhammer.wartale.components.Weapon_Data;
import com.warhammer.wartale.config.PluginConfig;
import com.warhammer.wartale.config.WeaponConfig;
import com.warhammer.wartale.core.ServiceRegistry;
import com.warhammer.wartale.interactions.weapons.Weapon_Interaction_Reload;
import com.warhammer.wartale.interactions.weapons.Weapon_Interaction_Shoot;
import com.warhammer.wartale.interactions.weapons.Weapon_Interaction_SwapFrom;
import com.warhammer.wartale.interactions.weapons.Weapon_Interaction_SwapTo;

import java.nio.file.Path;

public class WartalePlugin extends JavaPlugin {

    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public static ComponentType<EntityStore, Weapon_Data> WEAPON_DATA;
    private PluginConfig config;

    public WartalePlugin(@Nonnull JavaPluginInit init) {
        super(init);
        LOGGER.atInfo().log("Initializing Wartale...");
    }

    @Override
    protected void setup() {
        this.initializeConfig();
        this.initializeRegistries();
    }

    @Override
    protected void start() {
        LOGGER.atInfo().log("Registering Reloading Interaction...");

    }

    protected void shutdown() {
        LOGGER.atInfo().log("EconomyAPI has been disabled!");
    }

    public Path getConfigPath() {
        return this.getDataDirectory().resolve("config.json");
    }

    private void initializeConfig() {
        this.config = PluginConfig.load(this.getConfigPath());
        ServiceRegistry.register(PluginConfig.class, this.config);
    }

    private void initializeRegistries() {
        WeaponConfig weaponConfig = this.config.getWeapons();

        this.getCodecRegistry(Interaction.CODEC).register("Warhammer_Weapon_Interaction_Shoot",
                Weapon_Interaction_Shoot.class,
                Weapon_Interaction_Shoot.CODEC);
        this.getCodecRegistry(Interaction.CODEC).register("Warhammer_Weapon_Interaction_Reload",
                Weapon_Interaction_Reload.class,
                Weapon_Interaction_Reload.CODEC);
        this.getCodecRegistry(Interaction.CODEC).register("Warhammer_Weapon_Interaction_SwapTo",
                Weapon_Interaction_SwapTo.class,
                Weapon_Interaction_SwapTo.CODEC);
        this.getCodecRegistry(Interaction.CODEC).register("Warhammer_Weapon_Interaction_SwapFrom",
                Weapon_Interaction_SwapFrom.class,
                Weapon_Interaction_SwapFrom.CODEC);

        WEAPON_DATA = this.getEntityStoreRegistry().registerComponent(Weapon_Data.class, "Weapon_Data_Component",
                Weapon_Data.CODEC);
        ServiceRegistry.register(WeaponConfig.class, weaponConfig);

//        this.getEntityStoreRegistry().registerSystem(new WeaponHudTickingSystem());
    }
}
