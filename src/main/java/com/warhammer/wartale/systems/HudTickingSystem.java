package com.warhammer.wartale.systems;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.gui.WartaleHUD;
import com.warhammer.wartale.items.WeaponMetadataKey;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;

public class HudTickingSystem extends EntityTickingSystem<EntityStore> {
  private final Map<UUID, String> lastAmmoDisplay = new HashMap<>();

  @Override
  public void tick(
      float v,
      int index,
      @Nonnull ArchetypeChunk<EntityStore> chunk,
      @Nonnull Store<EntityStore> store,
      @Nonnull CommandBuffer<EntityStore> commandBuffer) {

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
        String displayName;
        boolean shouldReload = false;

        if (heldItem != null) {
            displayName = heldItem.getItemId();
            Integer maxMagSize = heldItem.getFromMetadataOrNull(WeaponMetadataKey.MAG_SIZE.key(), Codec.INTEGER);
            if (maxMagSize != null) {
                hasAmmo = true;
                Integer currentAmmo = heldItem.getFromMetadataOrNull(WeaponMetadataKey.CURRENT_AMMO.key(), Codec.INTEGER);
                String magId = heldItem.getFromMetadataOrNull(WeaponMetadataKey.MAG_ID.key(), Codec.STRING);
                int current = currentAmmo != null ? currentAmmo : 0;
                if (current == 0) {
                    shouldReload = true;
                }

                Ref<EntityStore> ref = chunk.getReferenceTo(index);
                CombinedItemContainer inventory = InventoryComponent.getCombined(commandBuffer,
                        ref,
                        InventoryComponent.HOTBAR_FIRST);
                int itemCount = inventory.countItemStacks(stack -> stack.getItemId().equals(magId));
                display = current + " / " + (itemCount * maxMagSize);
            }
        } else {
            displayName = "";
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

  @Override
  public Query<EntityStore> getQuery() {
    return Query.and(Player.getComponentType(), PlayerRef.getComponentType());
  }
}
