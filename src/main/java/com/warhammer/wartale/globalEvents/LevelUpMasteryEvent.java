package com.warhammer.wartale.globalEvents;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.components.BaseMasteryComponent;

import javax.annotation.Nonnull;

/**
 * Event fired when a player's mastery advances one or more levels.
 * <p>
 * Dispatched by
 * {@link com.warhammer.wartale.eventHandlers.GiveMasteryExperienceHandler} after
 * XP is added, and consumed by
 * {@link com.warhammer.wartale.eventHandlers.LevelUpMasteryHandler}.
 *
 * @param playerRef   reference to the player entity that levelled up
 * @param oldLevel    the player's level before the XP gain
 * @param newLevel    the player's level after the XP gain
 * @param masteryType the component type identifying the levelled mastery
 * @param <T>         the specific {@link BaseMasteryComponent} subtype
 */
public record LevelUpMasteryEvent<T extends BaseMasteryComponent>(
        @Nonnull Ref<EntityStore> playerRef,
        int oldLevel,
        int newLevel,
        @Nonnull ComponentType<EntityStore, T> masteryType
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
     * Dispatches a {@link LevelUpMasteryEvent} on the server event bus,
     * but only if at least one listener is registered.
     *
     * @param playerRef   reference to the player entity that levelled up
     * @param oldLevel    the level before the XP gain
     * @param newLevel    the level after the XP gain
     * @param masteryType the component type identifying the levelled mastery
     * @param <T>         the specific {@link BaseMasteryComponent} subtype
     */
    public static <T extends BaseMasteryComponent> void dispatch(Ref<EntityStore> playerRef, int oldLevel, int newLevel, @Nonnull ComponentType<EntityStore, T> masteryType) {
        IEventDispatcher<LevelUpMasteryEvent<T>, LevelUpMasteryEvent<T>> dispatcher =
                HytaleServer.get().getEventBus().dispatchFor(LevelUpMasteryEvent.class);

        if (dispatcher.hasListener()) {
            dispatcher.dispatch(new LevelUpMasteryEvent(playerRef, oldLevel, newLevel, masteryType));
        }
    }
}
