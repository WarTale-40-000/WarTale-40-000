package com.warhammer.wartale.globalEvents;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.components.BaseMasteryComponent;

import javax.annotation.Nonnull;

public record GiveMasteryExperienceEvent<T extends BaseMasteryComponent>(
        @Nonnull Ref<EntityStore> playerRef,
        int givenXP,
        @Nonnull ComponentType<EntityStore, T> masteryType
) implements IEvent<Void> {

    
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
