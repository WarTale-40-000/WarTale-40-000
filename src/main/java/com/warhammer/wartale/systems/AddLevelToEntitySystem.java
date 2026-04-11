package com.warhammer.wartale.systems;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.warhammer.wartale.components.EntityLevelComponent;
import com.warhammer.wartale.masteryCore.EntityLevelMappingTable;
import java.util.Objects;
import javax.annotation.Nonnull;

public class AddLevelToEntitySystem extends RefSystem<EntityStore> {

  @Override
  public void onEntityAdded(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull AddReason addReason,
      @Nonnull Store<EntityStore> store,
      @Nonnull CommandBuffer<EntityStore> commandBuffer) {
    NPCEntity entity = store.getComponent(ref, NPCEntity.getComponentType());
    if (entity == null) return;

    var entityLevelType = EntityLevelComponent.getComponentType();
    var entityLevel = store.getComponent(ref, entityLevelType);

    if (entityLevel == null) {
      int spawnedLevel = EntityLevelMappingTable.getLevelOfEntity(entity.getRoleName());
      commandBuffer.addComponent(ref, entityLevelType, new EntityLevelComponent(spawnedLevel));
    }
  }

  @Override
  public void onEntityRemove(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull RemoveReason removeReason,
      @Nonnull Store<EntityStore> store,
      @Nonnull CommandBuffer<EntityStore> commandBuffer) {}

  @Nonnull
  @Override
  public Query<EntityStore> getQuery() {
    return Archetype.of(Objects.requireNonNull(NPCEntity.getComponentType()));
  }
}
