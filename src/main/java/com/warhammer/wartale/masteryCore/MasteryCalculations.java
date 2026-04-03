package com.warhammer.wartale.masteryCore;

import static java.lang.Math.floor;

public class MasteryCalculations {

    private static final double LEVELINCREASEFACTOR = 1.4;


    private static final double SCALINGFACTOR = 100.0;


    private static final int XP_PER_KILL = 50;


    private static final double XP_LEVEL_EXPONENT = 0.75;


    public static int getLevel(int experience) {
        if (experience < 0) return 1;
        return (int) floor(Math.pow((experience / SCALINGFACTOR), (1 / LEVELINCREASEFACTOR))) + 1;
    }


    public static int getTotalExperienceForLevel(int level) {
        if (level < 1) return 0;
        double result = SCALINGFACTOR * Math.pow(level, LEVELINCREASEFACTOR);
        if (result >= Integer.MAX_VALUE) return Integer.MAX_VALUE;
        return (int) floor(result);
    }

    public static int getExperienceToNextLevel(int experience) {
        return getTotalExperienceForLevel(getLevel(experience));
    }


    public static boolean isLevelUp(int experience, int gainedExperience) {
        return getExperienceToNextLevel(experience) <= gainedExperience;
    }

    public static int getExperienceFromDefeatedLevel(int level) {
        return Math.max(1, (int) Math.round(XP_PER_KILL * Math.pow(level, XP_LEVEL_EXPONENT)));
    }
}
