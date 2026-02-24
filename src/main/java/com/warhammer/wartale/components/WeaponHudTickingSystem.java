package com.warhammer.wartale.components;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import com.warhammer.wartale.WartalePlugin;
import com.warhammer.wartale.config.WeaponConfig;
import com.warhammer.wartale.core.ServiceRegistry;
import com.warhammer.wartale.gui.WeaponHUD;
import com.warhammer.wartale.types.WarhammerWeaponMetadata;

public class WeaponHudTickingSystem extends EntityTickingSystem<EntityStore> {
    private final Map<UUID, WeaponHUD> huds = new HashMap<>();

    @Override
    public void tick(float v, int index, @Nonnull ArchetypeChunk<EntityStore> chunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        PlayerRef playerRef = chunk.getComponent(index, PlayerRef.getComponentType());
        Player player = chunk.getComponent(index, Player.getComponentType());

        if (playerRef == null || player == null) return;

        WeaponHUD hud = huds.computeIfAbsent(playerRef.getUuid(), uuid -> {
            return new WeaponHUD(playerRef);
        });

        // Get Ref
        Ref<EntityStore> ref = chunk.getReferenceTo(index);

        // Initialize ammunition component
        Weapon_Data weaponData = new Weapon_Data();
        if (commandBuffer.getComponent(ref, WartalePlugin.WEAPON_DATA) != null) {
            // here we implement logic that updates the component
            weaponData = commandBuffer.getComponent(ref, WartalePlugin.WEAPON_DATA);
        } else {
            // putComponent allows you to insert declared objects
            commandBuffer.putComponent(ref, WartalePlugin.WEAPON_DATA, weaponData);
        }

        assert weaponData != null;
        Map<String, Integer> currentAmmoMap = weaponData.getCurrentAmmo();
        WeaponConfig weaponConfig = ServiceRegistry.get(WeaponConfig.class);
        Map<String, WarhammerWeaponMetadata> metadata = weaponConfig.getWeapons();
        WarhammerWeaponMetadata weaponMetadata = metadata.get(weaponData.getCurrentWeaponId());

        if (weaponMetadata == null) {
            player.sendMessage(Message.raw("The weapon " + weaponData.getCurrentWeaponId() + " is not a registered weapon."));
            return;
        } else {
            Integer currentAmmoValue = currentAmmoMap.get(weaponData.getCurrentWeaponId());

            // Update HUD data
            hud.updateData(currentAmmoValue.toString(), weaponMetadata.getMaxAmmo().toString(), weaponData.isHudVisible());
        }

        // Show the HUD
        player.getHudManager().setCustomHud(playerRef, hud);
    }

    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType());
    }
}
