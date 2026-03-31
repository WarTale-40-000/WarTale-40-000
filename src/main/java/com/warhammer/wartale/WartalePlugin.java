package com.warhammer.wartale;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.warhammer.wartale.commands.professions.ProfessionBaseCommand;
import com.warhammer.wartale.components.EntityLevelComponent;
import com.warhammer.wartale.components.professions.KillProfessionComponent;
import com.warhammer.wartale.eventHandlers.GiveProfessionExperienceHandler;
import com.warhammer.wartale.eventHandlers.LevelUpProfessionHandler;
import com.warhammer.wartale.globalEvents.GiveProfessionExperienceEvent;
import com.warhammer.wartale.globalEvents.LevelUpProfessionEvent;
import com.warhammer.wartale.interactions.weapons.ValidateReloadInteraction;
import com.warhammer.wartale.systems.AddLevelToEntitySystem;
import com.warhammer.wartale.systems.HudTickingSystem;
import com.warhammer.wartale.eventHandlers.PlayerEventHandler;
import com.warhammer.wartale.interactions.weapons.ReloadInteraction;
import com.warhammer.wartale.globalEvents.PlayerEventHandler;
import com.warhammer.wartale.interactions.InventoryHasItemAmountInteraction;
import com.warhammer.wartale.interactions.weapons.LoadMagazineInteraction;
import com.warhammer.wartale.interactions.weapons.ShootInteraction;
import com.warhammer.wartale.systems.KillSystem;
import com.warhammer.wartale.systems.PlayerJoinSystem;
import com.warhammer.wartale.systems.HudTickingSystem;

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
        this.getEventRegistry().register(GiveProfessionExperienceEvent.class, new GiveProfessionExperienceHandler());
        this.getEventRegistry().register(LevelUpProfessionEvent.class, new LevelUpProfessionHandler());
        //Systems
        this.getEntityStoreRegistry().registerSystem(new HudTickingSystem());
        this.getEntityStoreRegistry().registerSystem(new PlayerJoinSystem());
        this.getEntityStoreRegistry().registerSystem(new KillSystem());
        this.getEntityStoreRegistry().registerSystem(new AddLevelToEntitySystem());

        // Components
        var killProfessionType = this.getEntityStoreRegistry().registerComponent(KillProfessionComponent.class, "KillProfession", KillProfessionComponent.CODEC);
        KillProfessionComponent.setComponentType(killProfessionType);
        var entityLevelType = this.getEntityStoreRegistry().registerComponent(EntityLevelComponent.class, "EntityLevelComponent", EntityLevelComponent.CODEC);
        EntityLevelComponent.setComponentType(entityLevelType);

        // Commands
        this.getCommandRegistry().registerCommand(new ProfessionBaseCommand());
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
