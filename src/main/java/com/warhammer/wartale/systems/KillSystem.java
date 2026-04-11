package com.warhammer.wartale.systems;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.warhammer.wartale.components.BaseMasteryComponent;
import com.warhammer.wartale.components.EntityLevelComponent;
import com.warhammer.wartale.globalEvents.GiveMasteryExperienceEvent;
import com.warhammer.wartale.masteryCore.ItemMasteryMappingTable;
import com.warhammer.wartale.masteryCore.MasteryCalculations;
import java.util.Objects;
import javax.annotation.Nonnull;

public class KillSystem extends DeathSystems.OnDeathSystem {

  @Override
  public void onComponentAdded(
      @Nonnull Ref<EntityStore> ref,
      @Nonnull DeathComponent deathComponent,
      @Nonnull Store<EntityStore> store,
      @Nonnull CommandBuffer<EntityStore> commandBuffer) {
    var deathInfo = deathComponent.getDeathInfo();
    if (deathInfo == null) return;

    if (!(deathInfo.getSource() instanceof Damage.EntitySource source)) return;

    // Get reference from death source (killer)
    var killerRef = source.getRef();
    if (!killerRef.isValid()) return;

    // Check if the killer is a player
    PlayerRef playerRef = store.getComponent(killerRef, PlayerRef.getComponentType());
    if (playerRef == null) return;
    // Check if the killer is a player
    Player player = store.getComponent(killerRef, Player.getComponentType());
    if (player == null) return;

    // Check if target is entity
    NPCEntity targetEntity =
        store.getComponent(ref, Objects.requireNonNull(NPCEntity.getComponentType()));
    if (targetEntity == null) return;

    // Check if target has entity level component
    EntityLevelComponent entityLevelComponent =
        store.getComponent(ref, EntityLevelComponent.getComponentType());
    if (entityLevelComponent == null) return;

    // Get expectedMasteryFromWeapon
    var expectedMasteryComponentType = getMasteryComponentType(player);
    if (expectedMasteryComponentType == null) return;

    // Check if mastery exists on players
    BaseMasteryComponent mastery = store.getComponent(killerRef, expectedMasteryComponentType);
    if (mastery == null) return;

    // Send event to give experience
    GiveMasteryExperienceEvent.dispatch(
        killerRef,
        store,
        MasteryCalculations.getExperienceFromDefeatedLevel(entityLevelComponent.getLevel()),
        expectedMasteryComponentType);
  }

  @Nonnull
  @Override
  public Query<EntityStore> getQuery() {
    return Query.and(Archetype.of(DeathComponent.getComponentType()));
  }

  private ComponentType<EntityStore, ? extends BaseMasteryComponent> getMasteryComponentType(
      @Nonnull Player player) {
    ItemStack itemStack = player.getInventory().getItemInHand();
    if (itemStack == null) return null;
    String weaponID = itemStack.getItem().getId();

    return ItemMasteryMappingTable.getMasteryTypeFromItemId(weaponID);
  }
}
