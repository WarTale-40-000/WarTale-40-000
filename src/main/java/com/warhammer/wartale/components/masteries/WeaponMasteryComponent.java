package com.warhammer.wartale.components.masteries;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.components.BaseMasteryComponent;
import com.warhammer.wartale.masteryCore.MasteryCalculations;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

/**
 * Abstract mastery component for weapon-based progression.
 * <p>
 * Provides shared XP, levelling, and display logic for all weapon masteries.
 * Concrete subclasses must define their own {@code ComponentType} and {@code CODEC}
 * and register them during plugin initialisation.
 */
public abstract class WeaponMasteryComponent extends BaseMasteryComponent {

    protected WeaponMasteryComponent() {
        super();
    }

    protected WeaponMasteryComponent(String masterName) {
        super(masterName);
    }

    /**
     * Copy constructor.
     *
     * @param other the component to copy state from
     */
    protected WeaponMasteryComponent(WeaponMasteryComponent other) {
        super(other);
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
    public int getExperienceFromDefeatedLevel(int level) {
        return MasteryCalculations.getExperienceFromDefeatedLevel(level);
    }

    @NullableDecl
    @Override
    public abstract Component<EntityStore> clone();

    /**
     * Returns a string representation including mastery name, level, XP, and XP to next level.
     *
     * @return formatted debug string
     */
    @Override
    public String toString() {
        return this.masteryName + "{level=" + this.getLevel() +
                ", Experience=" + this.experience +
                ", toNext=" + this.getExperienceToNextLevel() + "}";
    }

    /**
     * Returns the human-readable name of this weapon mastery.
     *
     * @return mastery display name
     */
    @Override
    public String getMasteryName() {
        return this.masteryName;
    }

    /**
     * Returns the primary hex colour used in the level-up notification UI.
     *
     * @return CSS-style hex colour string {@code "#c300ff"}
     */
    @Override
    public String getLevelUpgradeColor() {
        return "#c300ff";
    }

    /**
     * Returns the secondary hex colour used in the level-up notification UI.
     *
     * @return CSS-style hex colour string {@code "#8b32ff"}
     */
    @Override
    public String getLevelUpgradeSecondaryColor() {
        return "#8b32ff";
    }

    /**
     * Returns the default icon identifier used in the level-up notification.
     * Subclasses may override this to return a weapon-specific icon.
     *
     * @return icon resource name {@code "Weapon_Boltpistol"}
     */
    @Override
    public String getLevelUpgradeIcon() {
        return "Weapon_Boltpistol";
    }
}
