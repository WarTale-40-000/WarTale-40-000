package com.warhammer.wartale.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.gui.WeaponHUD;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class WeaponHudTickingSystem extends EntityTickingSystem<EntityStore> {
    private final Map<UUID, WeaponHUD> huds = new HashMap<>();

    @Override
    public void tick(float v, int index, @Nonnull ArchetypeChunk<EntityStore> chunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        PlayerRef playerRef = chunk.getComponent(index, PlayerRef.getComponentType());
        Player player = chunk.getComponent(index, Player.getComponentType());

        if (playerRef == null || player == null) return;

        WeaponHUD hud = huds.computeIfAbsent(playerRef.getUuid(), _ -> new WeaponHUD(playerRef));

        ItemStack heldItem = player.getInventory().getActiveHotbarItem();

        Integer currentAmmo = heldItem != null ? heldItem.getFromMetadataOrNull("current_ammo", Codec.INTEGER) : null;

        boolean hasWeapon = currentAmmo != null;
        hud.updateData(hasWeapon ? currentAmmo.toString() : "", hasWeapon);

        player.getHudManager().setCustomHud(playerRef, hud);
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType(), PlayerRef.getComponentType());
    }
}
