package com.warhammer.wartale.systems;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.Message;
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
import com.warhammer.wartale.components.masteries.WeaponMasteryComponent;
import com.warhammer.wartale.globalEvents.GiveMasteryExperienceEvent;
import com.warhammer.wartale.masteryCore.ItemMasteryMappingTable;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import javax.annotation.Nonnull;
import java.util.Objects;

public class KillSystem extends DeathSystems.OnDeathSystem {
    
    @Override
    public void onComponentAdded(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull DeathComponent deathComponent,
            @Nonnull Store<EntityStore> store,
            @Nonnull CommandBuffer<EntityStore> commandBuffer
    ) {
        var deathInfo = deathComponent.getDeathInfo();
        if (deathInfo == null) return;

        if (!(deathInfo.getSource() instanceof Damage.EntitySource source)) return;

        // Get reference from death source (killer)
        var killerRef = source.getRef();
        if (!killerRef.isValid()) return;

        // Check if the killer is a player
        var killer = store.getComponent(killerRef, PlayerRef.getComponentType());
        if (killer == null) return;
        // Check if the killer is a player
        var playerComponent = store.getComponent(killerRef, Player.getComponentType());
        if (playerComponent == null) return;
        Player player = (Player) playerComponent;

        // Check if target is entity
        var targetEntityComponent = store.getComponent(ref, Objects.requireNonNull(NPCEntity.getComponentType()));
        if (targetEntityComponent == null) return;
        NPCEntity targetEntity = (NPCEntity) targetEntityComponent;

        // Check if target has entity level component
        var entityLevelComponent = store.getComponent(ref, EntityLevelComponent.getComponentType());
        if (entityLevelComponent == null) return;
        EntityLevelComponent entityLevel = (EntityLevelComponent) entityLevelComponent;

        // Get expectedMasteryFromWeapon
        var expectedMasteryComponentType = getMasteryComponentType(store, player, killerRef);
        if (expectedMasteryComponentType == null) return;

        // Check if mastery exists on players
        var masteryComponent = store.getComponent(killerRef, expectedMasteryComponentType);
        if (masteryComponent == null) return;
        WeaponMasteryComponent killMastery = (WeaponMasteryComponent) masteryComponent;

        player.sendMessage(Message.raw("Found " + killMastery.getMasteryName()));

        GiveMasteryExperienceEvent.dispatch(killerRef, killMastery.getExperienceFromDefeatedLevel(entityLevel.getLevel()), expectedMasteryComponentType);
    }

    
    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Archetype.of(DeathComponent.getComponentType());
    }

    
    private ComponentType<EntityStore, ? extends BaseMasteryComponent> getMasteryComponentType(
            @Nonnull Store<EntityStore> store,
            @Nonnull Player player,
            @Nonnull Ref<EntityStore> playerRef) {
        ItemStack itemStack = player.getInventory().getItemInHand();
        if (itemStack == null) return null;
        String weaponID = itemStack.getItem().getId();

        return ItemMasteryMappingTable.getMasteryType(weaponID);
    }
}
