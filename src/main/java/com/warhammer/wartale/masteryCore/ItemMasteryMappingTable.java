package com.warhammer.wartale.masteryCore;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.components.BaseMasteryComponent;

import java.util.HashMap;
import java.util.Map;

public class ItemMasteryMappingTable {
    /**
     * Maps weapon item ID to its associated mastery component type.
     * Populated during plugin initialisation via {@link #register}.
     */
    private static final Map<String, ComponentType<EntityStore, ? extends BaseMasteryComponent>> ITEM_TABLE = new HashMap<>();

    /**
     * Registers a weapon item ID → mastery component type mapping.
     * Must be called after the component type has been registered with the ECS.
     *
     * @param itemId the weapon item ID (e.g. {@code "Weapon_Boltpistol"})
     * @param type   the mastery component type associated with that weapon
     * @param <T>    the concrete mastery component subtype
     */
    public static <T extends BaseMasteryComponent> void register(
            String itemId,
            ComponentType<EntityStore, T> type) {
        ITEM_TABLE.put(itemId, type);
    }

    /**
     * Returns the mastery component type for the given weapon item ID.
     *
     * @param itemId the weapon item ID (e.g. {@code "Weapon_Boltpistol"})
     * @return the mapped {@link ComponentType}, or {@code null} if not registered
     */
    public static ComponentType<EntityStore, ? extends BaseMasteryComponent> getMasteryType(String itemId) {
        return ITEM_TABLE.get(itemId);
    }
}
