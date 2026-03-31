package com.warhammer.wartale.components;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.components.professions.KillProfessionComponent;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import static java.lang.Math.floor;

/**
 * Abstract base for all profession components.
 * <p>
 * Provides a shared XP and levelling implementation driven by two tunable constants:
 * <ul>
 *   <li>{@code scalingFactor} — XP required to reach level 2 (anchor point).</li>
 *   <li>{@code levelIncreaseFactor} — exponent controlling how steeply XP cost rises per level.</li>
 * </ul>
 * The level formula is: {@code level = floor((xp / scalingFactor) ^ (1 / levelIncreaseFactor)) + 1}
 */
public abstract class BaseProfessionComponent implements Component<EntityStore> {
    private static ComponentType<EntityStore, KillProfessionComponent> TYPE;

    /** Exponent applied to the XP curve: higher values steepen per-level XP cost. */
    private final double levelIncreaseFactor;

    /** Divisor that anchors the level curve; 100 means 100 XP transitions level 1 → 2. */
    private final double scalingFactor;

    /** Accumulated experience points for this profession. */
    protected int experience;

    /**
     * Returns the shared component type handle.
     *
     * @return the component type for this profession
     */
    public static ComponentType<EntityStore, KillProfessionComponent> getComponentType() {
        return TYPE;
    }

    /**
     * Default constructor using {@code levelIncreaseFactor = 1.4} and {@code scalingFactor = 100.0}.
     */
    protected BaseProfessionComponent() {
        this.levelIncreaseFactor = 1.4;
        this.scalingFactor = 100.0;
        this.experience = 0;
    }

    /**
     * Constructor allowing custom curve parameters.
     *
     * @param levelIncreaseFactor exponent controlling XP cost growth per level
     * @param scalingFactor       XP anchor point for the level curve
     */
    protected BaseProfessionComponent(int levelIncreaseFactor, double scalingFactor) {
        this.levelIncreaseFactor = levelIncreaseFactor;
        this.scalingFactor = scalingFactor;
        this.experience = 0;
    }

    /**
     * Copy constructor.
     *
     * @param other the component to copy state from
     */
    protected BaseProfessionComponent(BaseProfessionComponent other) {
        this.experience = other.experience;
        this.levelIncreaseFactor = other.levelIncreaseFactor;
        this.scalingFactor = other.scalingFactor;
    }

    /**
     * Returns the player's total accumulated XP for this profession.
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
     * Calculates the current level from accumulated XP.
     * <p>
     * Returns {@code 1} for negative XP values to avoid {@code NaN} from fractional exponents.
     *
     * @return current level (minimum 1)
     */
    public int getLevel() {
        if (this.experience < 0) return 1;
        return (int) floor(Math.pow((this.experience / scalingFactor), (1 / levelIncreaseFactor))) + 1;
    }

    /**
     * Returns the total cumulative XP required to reach the given level.
     * <p>
     * Returns {@code 0} for levels below 1. Clamps to {@link Integer#MAX_VALUE} on overflow.
     *
     * @param level the target level (must be {@code >= 1})
     * @return total XP needed to reach {@code level}
     */
    public int getTotalExperienceForLevel(int level) {
        if (level < 1) return 0;
        double result = scalingFactor * Math.pow(level, levelIncreaseFactor);
        if (result >= Integer.MAX_VALUE) return Integer.MAX_VALUE;
        return (int) floor(result);
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
     * Adds XP to this profession's total.
     *
     * @param gainedExperience the amount of XP to add
     */
    public void addExperience(int gainedExperience) {
        this.experience += gainedExperience;
    }

    /** {@inheritDoc} */
    @NullableDecl
    @Override
    public abstract Component<EntityStore> clone();

    /** {@inheritDoc} */
    @Override
    public abstract String toString();

    /**
     * Returns the display name of this profession.
     *
     * @return human-readable profession name
     */
    public abstract String getProfessionName();

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
