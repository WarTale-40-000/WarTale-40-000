package com.warhammer.wartale.masteryCore;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.components.BaseMasteryComponent;
import com.warhammer.wartale.components.masteries.WeaponMasteryComponent;
import com.warhammer.wartale.components.masteries.weapons.BoltpistolMasteryComponent;
import java.util.HashMap;
import java.util.Map;

public class ItemMasteryMappingTable {

  private static final Map<String, ComponentType<EntityStore, ? extends BaseMasteryComponent>>
      ITEM_TABLE = new HashMap<>();

  public static <T extends BaseMasteryComponent> void register(
      String itemId, ComponentType<EntityStore, T> type) {
    ITEM_TABLE.put(itemId, type);
  }

  public static <T extends BaseMasteryComponent> void registerFromWeaponTierMap(
      WeaponMasteryComponent masteryComponent, ComponentType<EntityStore, T> type) {
    for (String itemId : masteryComponent.getWeaponTierMap().keySet()) {
      register(itemId, type);
    }
  }

  public static ComponentType<EntityStore, ? extends BaseMasteryComponent> getMasteryTypeFromItemId(
      String itemId) {
    return ITEM_TABLE.get(itemId);
  }

  public static ComponentType<EntityStore, ? extends BaseMasteryComponent> getMasteryTypeFromName(
      String name) {
    switch (name) {
      case "boltpistol":
        return BoltpistolMasteryComponent.getComponentType();
      default:
        return null;
    }
  }
}
