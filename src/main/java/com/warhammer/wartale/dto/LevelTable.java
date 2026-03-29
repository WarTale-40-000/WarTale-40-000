package com.warhammer.wartale.dto;

import java.util.Map;

public class LevelTable {
    // Key = level, Value = cumulative XP required to reach that level
    private static final Map<Integer, Integer> LEVEL_TABLE = Map.ofEntries(
            Map.entry(1, 300),
            Map.entry(2, 900),
            Map.entry(3, 1500),
            Map.entry(4, 2100),
            Map.entry(5, 3000),
            Map.entry(6, 3900),
            Map.entry(7, 4800),
            Map.entry(8, 5700),
            Map.entry(9, 6900),
            Map.entry(10, 8100),
            Map.entry(11, 9300),
            Map.entry(12, 10500),
            Map.entry(13, 11700),
            Map.entry(14, 13050),
            Map.entry(15, 14400),
            Map.entry(16, 15750),
            Map.entry(17, 17100),
            Map.entry(18, 18450),
            Map.entry(19, 20250),
            Map.entry(20, 22050),
            Map.entry(21, 23850),
            Map.entry(22, 25650),
            Map.entry(23, 27450),
            Map.entry(24, 29250),
            Map.entry(25, 31500)
    );

    public static int getLevel(int experience) {
        return LEVEL_TABLE.entrySet().stream()
                .filter(e -> experience >= e.getValue())
                .mapToInt(Map.Entry::getKey)
                .max()
                .orElse(0);
    }

    public static int getMaxExperienceForLevel(int level) {
        return LEVEL_TABLE.getOrDefault(level, 0);
    }

    public static int getExperienceToNextLevel(int experience) {
        int currentLevel = getLevel(experience);
        int nextLevel = currentLevel + 1;
        int maxExperienceForNextLevel = getMaxExperienceForLevel(nextLevel);
        return maxExperienceForNextLevel - experience;
    }

    public static int getMaxLevel() {
        return LEVEL_TABLE.keySet().stream().max(Integer::compareTo).orElse(0);
    }
}
