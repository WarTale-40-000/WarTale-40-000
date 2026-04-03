package com.warhammer.wartale.globalEvents;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.components.BaseMasteryComponent;

import javax.annotation.Nonnull;

public record LevelUpMasteryEvent<T extends BaseMasteryComponent>(
        @Nonnull Ref<EntityStore> playerRef,
        int oldLevel,
        int newLevel,
        @Nonnull ComponentType<EntityStore, T> masteryType
) implements IEvent<Void> {

    
    public int levelsGained() {
        return newLevel - oldLevel;
    }

    
    public static <T extends BaseMasteryComponent> void dispatch(Ref<EntityStore> playerRef, int oldLevel, int newLevel, @Nonnull ComponentType<EntityStore, T> masteryType) {
        IEventDispatcher<LevelUpMasteryEvent<T>, LevelUpMasteryEvent<T>> dispatcher =
                HytaleServer.get().getEventBus().dispatchFor(LevelUpMasteryEvent.class);

        if (dispatcher.hasListener()) {
            dispatcher.dispatch(new LevelUpMasteryEvent(playerRef, oldLevel, newLevel, masteryType));
        }
    }
}
