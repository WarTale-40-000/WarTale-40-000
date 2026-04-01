package com.warhammer.wartale.masteryCore;

import static java.lang.Math.floor;

public class MasteryCalculations {
    /**
     * Exponent applied to the XP curve: higher values steepen per-level XP cost.
     */
    private static final double LEVELINCREASEFACTOR = 1.4;

    /**
     * Divisor that anchors the level curve; 100 means 100 XP transitions level 1 → 2.
     */
    private static final double SCALINGFACTOR = 100.0;

    /**
     * Base XP awarded per kill before level scaling is applied.
     */
    private static final int XP_PER_KILL = 50;

    /**
     * Exponent for the kill-XP scaling curve; {@code < 1} gives sub linear growth.
     */
    private static final double XP_LEVEL_EXPONENT = 0.75;

    /**
     * Calculates the current level from accumulated XP.
     * <p>
     * Returns {@code 1} for negative XP values to avoid {@code NaN} from fractional exponents.
     *
     * @return current level (minimum 1)
     */
    public static int getLevel(int experience) {
        if (experience < 0) return 1;
        return (int) floor(Math.pow((experience / SCALINGFACTOR), (1 / LEVELINCREASEFACTOR))) + 1;
    }

    /**
     * Returns the total cumulative XP required to reach the given level.
     * <p>
     * Returns {@code 0} for levels below 1. Clamps to {@link Integer#MAX_VALUE} on overflow.
     *
     * @param level the target level (must be {@code >= 1})
     * @return total XP needed to reach {@code level}
     */
    public static int getTotalExperienceForLevel(int level) {
        if (level < 1) return 0;
        double result = SCALINGFACTOR * Math.pow(level, LEVELINCREASEFACTOR);
        if (result >= Integer.MAX_VALUE) return Integer.MAX_VALUE;
        return (int) floor(result);
    }

    /**
     * Calculates the XP reward for defeating an enemy of the given level.
     * <p>
     * Uses a sub-linear power curve so higher-level enemies yield more XP,
     * but the gain tapers off at extreme levels. Returns at least {@code 1}.
     *
     * @param level the defeated enemy's level
     * @return XP to award (minimum 1)
     */
    public static int getExperienceFromDefeatedLevel(int level) {
        return Math.max(1, (int) Math.round(XP_PER_KILL * Math.pow(level, XP_LEVEL_EXPONENT)));
    }
}
