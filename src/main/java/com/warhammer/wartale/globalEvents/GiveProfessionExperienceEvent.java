package com.warhammer.wartale.globalEvents;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.components.BaseProfessionComponent;

import javax.annotation.Nonnull;

public record GiveProfessionExperienceEvent<T extends BaseProfessionComponent>(
        @Nonnull Ref<EntityStore> playerRef,
        int givenXP,
        @Nonnull ComponentType<EntityStore, T> professionType
) implements IEvent<Void> {

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
