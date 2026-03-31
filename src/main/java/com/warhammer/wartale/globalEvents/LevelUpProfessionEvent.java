package com.warhammer.wartale.globalEvents;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.components.BaseProfessionComponent;

import javax.annotation.Nonnull;

/**
 * Event fired when a player's profession advances one or more levels.
 * <p>
 * Dispatched by
 * {@link com.warhammer.wartale.eventHandlers.GiveProfessionExperienceHandler} after
 * XP is added, and consumed by
 * {@link com.warhammer.wartale.eventHandlers.LevelUpProfessionHandler}.
 *
 * @param playerRef      reference to the player entity that levelled up
 * @param oldLevel       the player's level before the XP gain
 * @param newLevel       the player's level after the XP gain
 * @param professionType the component type identifying the levelled profession
 * @param <T>            the specific {@link BaseProfessionComponent} subtype
 */
public record LevelUpProfessionEvent<T extends BaseProfessionComponent>(
        @Nonnull Ref<EntityStore> playerRef,
        int oldLevel,
        int newLevel,
        @Nonnull ComponentType<EntityStore, T> professionType
) implements IEvent<Void> {

    /**
     * Returns the number of levels gained in this event.
     *
     * @return {@code newLevel - oldLevel}
     */
    public int levelsGained() {
        return newLevel - oldLevel;
    }

    /**
     * Dispatches a {@link LevelUpProfessionEvent} on the server event bus,
     * but only if at least one listener is registered.
     *
     * @param playerRef      reference to the player entity that levelled up
     * @param oldLevel       the level before the XP gain
     * @param newLevel       the level after the XP gain
     * @param professionType the component type identifying the levelled profession
     * @param <T>            the specific {@link BaseProfessionComponent} subtype
     */
    public static <T extends BaseProfessionComponent> void dispatch(Ref<EntityStore> playerRef, int oldLevel, int newLevel, @Nonnull ComponentType<EntityStore, T> professionType) {
        IEventDispatcher<LevelUpProfessionEvent<T>, LevelUpProfessionEvent<T>> dispatcher =
                HytaleServer.get().getEventBus().dispatchFor(LevelUpProfessionEvent.class);

        if (dispatcher.hasListener()) {
            dispatcher.dispatch(new LevelUpProfessionEvent(playerRef, oldLevel, newLevel, professionType));
        }
    }
}
