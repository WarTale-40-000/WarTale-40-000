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
 * Event fired when a player should receive XP for a specific mastery.
 * <p>
 * Dispatched by {@link com.warhammer.wartale.systems.KillSystem} after a valid kill,
 * and consumed by
 * {@link com.warhammer.wartale.eventHandlers.GiveMasteryExperienceHandler}.
 *
 * @param playerRef   reference to the player entity receiving the XP
 * @param givenXP     amount of XP to award (should be {@code > 0})
 * @param masteryType the component type identifying the target mastery
 * @param <T>         the specific {@link BaseMasteryComponent} subtype
 */
public record GiveMasteryExperienceEvent<T extends BaseMasteryComponent>(
        @Nonnull Ref<EntityStore> playerRef,
        int givenXP,
        @Nonnull ComponentType<EntityStore, T> masteryType
) implements IEvent<Void> {

    /**
     * Dispatches a {@link GiveMasteryExperienceEvent} on the server event bus,
     * but only if at least one listener is registered.
     *
     * @param playerRef   reference to the player entity receiving the XP
     * @param givenXP     amount of XP to award
     * @param masteryType the component type identifying the target mastery
     * @param <T>         the specific {@link BaseMasteryComponent} subtype
     */
    public static <T extends BaseMasteryComponent> void dispatch(
            @Nonnull Ref<EntityStore> playerRef,
            int givenXP,
            @Nonnull ComponentType<EntityStore, T> masteryType) {
        IEventDispatcher<GiveMasteryExperienceEvent<T>, GiveMasteryExperienceEvent<T>> dispatcher =
                HytaleServer.get().getEventBus().dispatchFor(GiveMasteryExperienceEvent.class);

        if (dispatcher.hasListener()) {
            dispatcher.dispatch(new GiveMasteryExperienceEvent<>(playerRef, givenXP, masteryType));
        }
    }
}
