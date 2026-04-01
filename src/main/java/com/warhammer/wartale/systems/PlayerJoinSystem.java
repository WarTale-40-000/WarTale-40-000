package com.warhammer.wartale.systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.components.masteries.weapons.BoltpistolMasteryComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

/**
 * Handles player entity load events.
 * <p>
 * On {@link AddReason#LOAD}, initialises a {@link WeaponMasteryComponent} for new players
 * or greets returning players with their current level and XP.
 */
public class PlayerJoinSystem extends RefSystem<EntityStore> {

    /**
     * Called when a player entity is added to the world.
     * <p>
     * Ignored for any reason other than {@link AddReason#LOAD}. Greets the player and,
     * if no {@link WeaponMasteryComponent} exists yet, creates one at level 1.
     *
     * @param ref           reference to the entity being added
     * @param addReason     reason the entity was added
     * @param store         the entity component store
     * @param commandBuffer buffer for deferred component mutations
     */
    @Override
    public void onEntityAdded(
            @NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl AddReason addReason,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer
    ) {
        if (addReason != AddReason.LOAD) return;

        var playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef == null) return;

        var killMasteryType = BoltpistolMasteryComponent.getComponentType();
        var killMastery = store.getComponent(ref, killMasteryType);

        if (killMastery != null) {
            playerRef.sendMessage(Message.raw(
                    "Welcome back! Level %d (%d XP)".formatted(killMastery.getLevel(), killMastery.getExperience())
            ));
        } else {
            commandBuffer.addComponent(ref, killMasteryType, new BoltpistolMasteryComponent());
            playerRef.sendMessage(Message.raw("Welcome! Your adventure begins at Level 1."));
        }
    }

    /**
     * Called when a player entity is removed from the world.
     *
     * @param ref           reference to the entity being removed
     * @param removeReason  reason the entity was removed
     * @param store         the entity component store
     * @param commandBuffer buffer for deferred component mutations
     */
    @Override
    public void onEntityRemove(
            @NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl RemoveReason removeReason,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer
    ) {
    }

    /**
     * Returns the query that filters entities to those carrying a {@link PlayerRef} component.
     *
     * @return the archetype query for this system
     */
    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Archetype.of(PlayerRef.getComponentType());
    }
}
