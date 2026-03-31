package com.warhammer.wartale.systems;

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
import com.warhammer.wartale.gui.WartaleHUD;
import com.warhammer.wartale.items.WeaponMetadataKey;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Ticking system that keeps each player's HUD ammo display in sync with their held item.
 * <p>
 * Runs every server tick. Tracks the last-sent ammo string per player to avoid
 * redundant network packets — {@link WartaleHUD#setAmmoSection} is only called when
 * the display value or visibility has changed.
 */
public class HudTickingSystem extends EntityTickingSystem<EntityStore> {
    private final Map<UUID, String> lastAmmoDisplay = new HashMap<>();

    /**
     * Called every tick for each entity matching the query.
     * <p>
     * Reads ammo metadata ({@code max_ammo}, {@code current_ammo}) from the player's
     * held item and updates the HUD ammo section only when the state has changed.
     *
     * @param v             delta time for this tick
     * @param index         index of the entity within the archetype chunk
     * @param chunk         the archetype chunk containing the entity
     * @param store         the entity component store
     * @param commandBuffer buffer for deferred component mutations
     */
    @Override
    public void tick(float v, int index, @Nonnull ArchetypeChunk<EntityStore> chunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {

        PlayerRef playerRef = chunk.getComponent(index, PlayerRef.getComponentType());
        Player player = chunk.getComponent(index, Player.getComponentType());
        if (playerRef == null || player == null) return;

        UUID playerId = playerRef.getUuid();

        WartaleHUD hud = (WartaleHUD) player.getHudManager().getCustomHud();
        if (hud == null) return;

        // Determine ammo state
        ItemStack heldItem = player.getInventory().getItemInHand();
        boolean hasAmmo = false;
        String display = "";
        String displayName = "";
        boolean shouldReload = false;

        if (heldItem != null) {
            displayName = heldItem.getItemId();
            Integer maxMagSize = heldItem.getFromMetadataOrNull(WeaponMetadataKey.MAG_SIZE.key(), Codec.INTEGER);
            if (maxMagSize != null) {
                hasAmmo = true;
                Integer currentAmmo = heldItem.getFromMetadataOrNull(WeaponMetadataKey.CURRENT_AMMO.key(), Codec.INTEGER);
                int current = currentAmmo != null ? currentAmmo : 0;
                if (current == 0) {
                    shouldReload = true;
                }
                display = current + "/" + maxMagSize;
            }
        }

        // Only send a packet when something changed
        String lastDisplay = lastAmmoDisplay.getOrDefault(playerId, null);
        boolean sameDisplay = display.equals(lastDisplay);
        boolean wasVisible = lastDisplay != null && !lastDisplay.isEmpty();

        if (!sameDisplay || hasAmmo != wasVisible) {
            lastAmmoDisplay.put(playerId, hasAmmo ? display : "");
            hud.setAmmoSection(hasAmmo, display, displayName, shouldReload);
        }
    }

    /**
     * Returns the query that filters entities to those carrying both
     * {@link Player} and {@link PlayerRef} components.
     *
     * @return the compound query for this system
     */
    @Override
    public Query<EntityStore> getQuery() {
        return Query.and(Player.getComponentType(), PlayerRef.getComponentType());
    }
}
