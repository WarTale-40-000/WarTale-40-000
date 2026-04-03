package com.warhammer.wartale.masteryCore;

import java.util.Map;

public class EntityLevelMappingTable {
    
    private static final int DEFAULT_LEVEL = 1;

    
    private static final Map<String, Integer> LEVEL_TABLE = Map.ofEntries(
            Map.entry("Chicken_Chick", 10),
            Map.entry("Crab", 100));

    
    public static int getLevelOfEntity(String entity) {
        return LEVEL_TABLE.getOrDefault(entity, DEFAULT_LEVEL);
    }
}
