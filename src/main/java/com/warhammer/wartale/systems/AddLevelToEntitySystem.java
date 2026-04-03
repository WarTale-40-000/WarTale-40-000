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

public class AddLevelToEntitySystem extends RefSystem<EntityStore> {

    
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

    
    @Override
    public void onEntityRemove(
            @NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl RemoveReason removeReason,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer
    ) {
    }

    
    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Archetype.of(Objects.requireNonNull(NPCEntity.getComponentType()));
    }
}
