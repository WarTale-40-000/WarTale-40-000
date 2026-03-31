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
 * Event fired when a player should receive XP for a specific profession.
 * <p>
 * Dispatched by {@link com.warhammer.wartale.systems.KillSystem} after a valid kill,
 * and consumed by
 * {@link com.warhammer.wartale.eventHandlers.GiveProfessionExperienceHandler}.
 *
 * @param playerRef      reference to the player entity receiving the XP
 * @param givenXP        amount of XP to award (should be {@code > 0})
 * @param professionType the component type identifying the target profession
 * @param <T>            the specific {@link BaseProfessionComponent} subtype
 */
public record GiveProfessionExperienceEvent<T extends BaseProfessionComponent>(
        @Nonnull Ref<EntityStore> playerRef,
        int givenXP,
        @Nonnull ComponentType<EntityStore, T> professionType
) implements IEvent<Void> {

    /**
     * Dispatches a {@link GiveProfessionExperienceEvent} on the server event bus,
     * but only if at least one listener is registered.
     *
     * @param playerRef      reference to the player entity receiving the XP
     * @param givenXP        amount of XP to award
     * @param professionType the component type identifying the target profession
     * @param <T>            the specific {@link BaseProfessionComponent} subtype
     */
    public static <T extends BaseProfessionComponent> void dispatch(
            @Nonnull Ref<EntityStore> playerRef,
            int givenXP,
            @Nonnull ComponentType<EntityStore, T> professionType) {
        IEventDispatcher<GiveProfessionExperienceEvent<T>, GiveProfessionExperienceEvent<T>> dispatcher =
                HytaleServer.get().getEventBus().dispatchFor(GiveProfessionExperienceEvent.class);

        if (dispatcher.hasListener()) {
            dispatcher.dispatch(new GiveProfessionExperienceEvent<>(playerRef, givenXP, professionType));
        }
    }
}
