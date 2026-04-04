package com.warhammer.wartale;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.warhammer.wartale.commands.masteries.MasteryBaseCommand;
import com.warhammer.wartale.components.EntityLevelComponent;
import com.warhammer.wartale.components.masteries.weapons.BoltpistolMasteryComponent;
import com.warhammer.wartale.eventHandlers.GiveMasteryExperienceHandler;
import com.warhammer.wartale.eventHandlers.LevelUpMasteryHandler;
import com.warhammer.wartale.eventHandlers.PlayerEventHandler;
import com.warhammer.wartale.globalEvents.GiveMasteryExperienceEvent;
import com.warhammer.wartale.globalEvents.LevelUpMasteryEvent;
import com.warhammer.wartale.interactions.InventoryHasItemAmountInteraction;
import com.warhammer.wartale.interactions.weapons.LoadMagazineInteraction;
import com.warhammer.wartale.interactions.weapons.ShootInteraction;
import com.warhammer.wartale.masteryCore.ItemMasteryMappingTable;
import com.warhammer.wartale.systems.AddLevelToEntitySystem;
import com.warhammer.wartale.systems.HudTickingSystem;
import com.warhammer.wartale.systems.KillSystem;
import com.warhammer.wartale.systems.PlayerJoinSystem;

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
        //Interactions
        this.getCodecRegistry(Interaction.CODEC)
            .register("Interaction_Weapon_Shoot", ShootInteraction.class, ShootInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC)
            .register("LoadMagazine", LoadMagazineInteraction.class, LoadMagazineInteraction.CODEC);
        this.getCodecRegistry(Interaction.CODEC)
            .register("InventoryHasItemAmount",
                      InventoryHasItemAmountInteraction.class,
                      InventoryHasItemAmountInteraction.CODEC);
        //Global events
        this.getEventRegistry().registerGlobal(AddPlayerToWorldEvent.class, PlayerEventHandler::onAddPlayerToWorld);
        this.getEventRegistry().register(GiveMasteryExperienceEvent.class, new GiveMasteryExperienceHandler());
        this.getEventRegistry().register(LevelUpMasteryEvent.class, new LevelUpMasteryHandler<>());
        //Systems
        this.getEntityStoreRegistry().registerSystem(new HudTickingSystem());
        this.getEntityStoreRegistry().registerSystem(new PlayerJoinSystem());
        this.getEntityStoreRegistry().registerSystem(new KillSystem());
        this.getEntityStoreRegistry().registerSystem(new AddLevelToEntitySystem());

        // Components
        var entityLevelType = this.getEntityStoreRegistry().registerComponent(EntityLevelComponent.class, "EntityLevelComponent", EntityLevelComponent.CODEC);
        EntityLevelComponent.setComponentType(entityLevelType);

        // Masteries
        this.registerMasteries();

        // Commands
        this.getCommandRegistry().registerCommand(new MasteryBaseCommand());
    }


    @Override
    protected void start() {
        LOGGER.atInfo().log("Wartale started.");
    }


    @Override
    protected void shutdown() {
        LOGGER.atInfo().log("Wartale has been disabled.");
    }


    private void registerMasteries() {
        // Boltpistol Mastery
        var boltpistolMasteryComponentComponentType = this.getEntityStoreRegistry().registerComponent(BoltpistolMasteryComponent.class, "BoltpistolMastery", BoltpistolMasteryComponent.CODEC);
        BoltpistolMasteryComponent.setComponentType(boltpistolMasteryComponentComponentType);
        ItemMasteryMappingTable.registerFromWeaponTierMap(new BoltpistolMasteryComponent(), boltpistolMasteryComponentComponentType);
    }
}
