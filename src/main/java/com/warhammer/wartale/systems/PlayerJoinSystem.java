package com.warhammer.wartale.systems;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.components.masteries.weapons.BoltpistolMasteryComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class PlayerJoinSystem extends RefSystem<EntityStore> {

  @Override
  public void onEntityAdded(
      @NonNullDecl Ref<EntityStore> ref,
      @NonNullDecl AddReason addReason,
      @NonNullDecl Store<EntityStore> store,
      @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
    if (addReason != AddReason.LOAD) return;

    var playerRef = store.getComponent(ref, PlayerRef.getComponentType());
    if (playerRef == null) return;

    // Mount Masteries
    var boltpistolMastery = store.getComponent(ref, BoltpistolMasteryComponent.getComponentType());
    if (boltpistolMastery == null) {
      commandBuffer.addComponent(
          ref, BoltpistolMasteryComponent.getComponentType(), new BoltpistolMasteryComponent());
    }
  }

  @Override
  public void onEntityRemove(
      @NonNullDecl Ref<EntityStore> ref,
      @NonNullDecl RemoveReason removeReason,
      @NonNullDecl Store<EntityStore> store,
      @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {}

  @NullableDecl
  @Override
  public Query<EntityStore> getQuery() {
    return Archetype.of(PlayerRef.getComponentType());
  }
}
