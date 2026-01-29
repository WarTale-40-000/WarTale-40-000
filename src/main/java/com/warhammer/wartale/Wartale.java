package com.warhammer.wartale;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import javax.annotation.Nonnull;

import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.components.Weapon_Data;
import com.warhammer.wartale.interactions.weapons.Weapon_Interaction_Shoot;

public class Wartale extends JavaPlugin {

    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    public static ComponentType<EntityStore, Weapon_Data> WEAPON_DATA;

    public Wartale(@Nonnull JavaPluginInit init) {
        super(init);
        LOGGER.atInfo().log("Initializing Wartale...");
    }

    @Override
    protected void setup() {
        LOGGER.atInfo().log("Registering Reloading Interaction...");
        WEAPON_DATA = this.getEntityStoreRegistry().registerComponent(Weapon_Data.class, "Weapon_Data_Component",
                Weapon_Data.CODEC);

        this.getCodecRegistry(Interaction.CODEC).register("Warhammer_Weapon_Interaction_Shoot",
                Weapon_Interaction_Shoot.class,
                Weapon_Interaction_Shoot.CODEC);
    }
}
