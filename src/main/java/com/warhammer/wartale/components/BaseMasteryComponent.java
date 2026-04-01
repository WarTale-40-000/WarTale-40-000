package com.warhammer.wartale.components;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.masteryCore.MasteryCalculations;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

/**
 * Abstract base for all mastery components.
 * <p>
 * Provides a shared XP and levelling implementation driven by two tunable constants:
 * <ul>
 *   <li>{@code scalingFactor} — XP required to reach level 2 (anchor point).</li>
 *   <li>{@code levelIncreaseFactor} — exponent controlling how steeply XP cost rises per level.</li>
 * </ul>
 * The level formula is: {@code level = floor((xp / scalingFactor) ^ (1 / levelIncreaseFactor)) + 1}
 */
public abstract class BaseMasteryComponent implements Component<EntityStore> {
    /**
     * Accumulated experience points for this mastery.
     */
    protected int experience;

    /** Human-readable display name for this mastery (e.g. {@code "Boltpistol Mastery"}). */
    protected String masteryName;

    protected BaseMasteryComponent(String masterName) {
        this.experience = 0;
        this.masteryName = masterName;
    }

    protected BaseMasteryComponent() {
        this.masteryName = "";
        this.experience = 0;
    }

    /**
     * Copy constructor.
     *
     * @param other the component to copy state from
     */
    protected BaseMasteryComponent(BaseMasteryComponent other) {
        this.experience = other.experience;
        this.masteryName = other.masteryName;
    }

    /**
     * Returns the player's total accumulated XP for this mastery.
     *
     * @return current experience points
     */
    public int getExperience() {
        return this.experience;
    }

    /**
     * Directly sets the player's XP, bypassing the level-up check.
     *
     * @param gainedExperience the new XP value (should be {@code >= 0})
     */
    public void setExperience(int gainedExperience) {
        this.experience = gainedExperience;
    }


    /**
     * Returns the player's current level derived from accumulated XP.
     *
     * @return current level (minimum 1)
     */
    public int getLevel() {
        return MasteryCalculations.getLevel(this.experience);
    }

    /**
     * Returns the total cumulative XP required to reach the given level.
     *
     * @param level the target level (must be {@code >= 1})
     * @return total XP needed to reach {@code level}, or {@code 0} if {@code level < 1}
     */
    public int getTotalExperienceForLevel(int level) {
        return MasteryCalculations.getTotalExperienceForLevel(level);
    }

    /**
     * Returns the cumulative XP threshold that must be reached to advance past the current level.
     *
     * @return XP required for the next level-up
     */
    public int getExperienceToNextLevel() {
        return this.getTotalExperienceForLevel(this.getLevel());
    }

    /**
     * Returns {@code true} if the given XP gain would trigger a level-up.
     *
     * @param gainedExperience the XP about to be added
     * @return {@code true} if a level-up occurs
     */
    public boolean isLevelUp(int gainedExperience) {
        return this.getExperienceToNextLevel() <= gainedExperience;
    }

    /**
     * Adds XP to this mastery's total.
     *
     * @param gainedExperience the amount of XP to add
     */
    public void addExperience(int gainedExperience) {
        this.experience += gainedExperience;
    }

    /**
     * {@inheritDoc}
     */
    @NullableDecl
    @Override
    public abstract Component<EntityStore> clone();

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract String toString();

    /**
     * Returns the display name of this mastery.
     *
     * @return human-readable mastery name
     */
    public abstract String getMasteryName();

    /**
     * Returns the primary hex colour used in the level-up UI.
     *
     * @return CSS-style hex colour string (e.g. {@code "#c300ff"})
     */
    public abstract String getLevelUpgradeColor();

    /**
     * Returns the secondary hex colour used in the level-up UI.
     *
     * @return CSS-style hex colour string
     */
    public abstract String getLevelUpgradeSecondaryColor();

    /**
     * Returns the icon identifier displayed in the level-up notification.
     *
     * @return icon resource name
     */
    public abstract String getLevelUpgradeIcon();
}
