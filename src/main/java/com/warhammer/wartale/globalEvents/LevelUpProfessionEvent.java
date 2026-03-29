package com.warhammer.wartale.globalEvents;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.components.BaseProfessionComponent;

import javax.annotation.Nonnull;

public record LevelUpProfessionEvent<T extends BaseProfessionComponent>(
        @Nonnull Ref<EntityStore> playerRef,
        int oldLevel,
        int newLevel,
        @Nonnull ComponentType<EntityStore, T> professionType
) implements IEvent<Void> {

    public int levelsGained() {
        return newLevel - oldLevel;
    }

    public static <T extends BaseProfessionComponent> void dispatch(Ref<EntityStore> playerRef, int oldLevel, int newLevel, @Nonnull ComponentType<EntityStore, T> professionType) {
        IEventDispatcher<LevelUpProfessionEvent<T>, LevelUpProfessionEvent<T>> dispatcher =
                HytaleServer.get().getEventBus().dispatchFor(LevelUpProfessionEvent.class);

        if (dispatcher.hasListener()) {
            dispatcher.dispatch(new LevelUpProfessionEvent(playerRef, oldLevel, newLevel, professionType));
        }
    }
}