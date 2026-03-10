package com.warhammer.wartale;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.Config;
import com.warhammer.wartale.components.Weapon_Data;
import com.warhammer.wartale.config.WeaponConfig;
import com.warhammer.wartale.interactions.weapons.Weapon_Interaction_Reload;
import com.warhammer.wartale.interactions.weapons.Weapon_Interaction_Shoot;
import com.warhammer.wartale.interactions.weapons.Weapon_Interaction_SwapFrom;
import com.warhammer.wartale.interactions.weapons.Weapon_Interaction_SwapTo;

import javax.annotation.Nonnull;

public class WartalePlugin extends JavaPlugin {

    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private static WartalePlugin instance;

    private ComponentType<EntityStore, Weapon_Data> weaponData;

    private final Config<WeaponConfig> weaponConfig;

    public WartalePlugin(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
        this.weaponConfig = this.withConfig("warhammer-config", WeaponConfig.CODEC);
        LOGGER.atInfo().log("Initializing Wartale...");
    }

    public static WartalePlugin get() {
        return instance;
    }

    public Config<WeaponConfig> getWeaponConfig() {
        return this.weaponConfig;
    }

    public ComponentType<EntityStore, Weapon_Data> getWeaponData() {
        return this.weaponData;
    }

    @Override
    protected void setup() {
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

        this.weaponData = this.getEntityStoreRegistry().registerComponent(Weapon_Data.class, "Weapon_Data_Component",
                Weapon_Data.CODEC);
        this.weaponConfig.save();
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
