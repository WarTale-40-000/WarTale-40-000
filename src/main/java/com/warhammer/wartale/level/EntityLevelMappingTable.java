package com.warhammer.wartale.level;

import java.util.Map;

/**
 * Static lookup table mapping NPC role names to their spawn levels.
 * <p>
 * Entities not listed in the table fall back to {@code DEFAULT_LEVEL}.
 * Add new entries to {@code LEVEL_TABLE} to assign custom levels to additional
 * roles.
 */
public class EntityLevelMappingTable {
    /**
     * Level assigned to default entity whose role name is not found in
     * {@code LEVEL_TABLE}.
     */
    private static final int DEFAULT_LEVEL = 1;

    /** Maps NPC role name to its spawn level. */
    private static final Map<String, Integer> LEVEL_TABLE = Map.ofEntries(
            Map.entry("Chicken_Chick", 10),
            Map.entry("Crab", 100));

    /**
     * Returns the spawn level for the given NPC role name.
     *
     * @param entity the role name of the NPC (e.g. {@code "Crab"})
     * @return the mapped level, or {@code DEFAULT_LEVEL} if the role is not
     *         registered
     */
    public static int getLevelOfEntity(String entity) {
        return LEVEL_TABLE.getOrDefault(entity, DEFAULT_LEVEL);
    }
}
