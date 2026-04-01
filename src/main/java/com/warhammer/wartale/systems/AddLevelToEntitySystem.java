package com.warhammer.wartale.systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.warhammer.wartale.components.EntityLevelComponent;
import com.warhammer.wartale.masteryCore.EntityLevelMappingTable;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Objects;

/**
 * Assigns a level to NPC entities when they first spawn.
 * <p>
 * On {@link AddReason#SPAWN}, looks up the entity's role name in
 * {@link EntityLevelMappingTable} and attaches an {@link EntityLevelComponent}
 * if one is not already present.
 */
public class AddLevelToEntitySystem extends RefSystem<EntityStore> {

    /**
     * Called when an NPC entity is added to the world.
     * <p>
     * Only acts on {@link AddReason#SPAWN}. Skips entities that already carry an
     * {@link EntityLevelComponent}. The assigned level is determined by
     * {@link EntityLevelMappingTable#getLevelOfEntity(String)}.
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
        var entityComponent = store.getComponent(ref, NPCEntity.getComponentType());
        if (entityComponent == null) return;
        NPCEntity entity = (NPCEntity) entityComponent;

        var entityLevelType = EntityLevelComponent.getComponentType();
        var entityLevel = store.getComponent(ref, entityLevelType);

        if (entityLevel == null) {
            int spawnedLevel = EntityLevelMappingTable.getLevelOfEntity(entity.getRoleName());
            commandBuffer.addComponent(ref, entityLevelType, new EntityLevelComponent(spawnedLevel));
        }
    }

    /**
     * Called when an NPC entity is removed from the world. No action is taken.
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
     * Returns the query that filters entities to those carrying an {@link NPCEntity} component.
     *
     * @return the archetype query for this system
     */
    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Archetype.of(Objects.requireNonNull(NPCEntity.getComponentType()));
    }
}
