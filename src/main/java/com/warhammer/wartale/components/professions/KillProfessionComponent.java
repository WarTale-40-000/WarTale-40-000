package com.warhammer.wartale.components.professions;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.components.BaseProfessionComponent;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

/**
 * Profession component tracking a player's kill-based progression.
 * <p>
 * XP is awarded when the player defeats an NPC. The reward scales with the
 * enemy's level using the formula:
 * {@code xp = max(1, round(XP_PER_KILL * level ^ XP_LEVEL_EXPONENT))}
 * Serialised and deserialised via {@link #CODEC}.
 */
public class KillProfessionComponent extends BaseProfessionComponent {
    private static ComponentType<EntityStore, KillProfessionComponent> TYPE;

    /**
     * Codec used to serialise/deserialise this component.
     */
    public static final BuilderCodec<KillProfessionComponent> CODEC = buildCodec();

    /**
     * Base XP awarded per kill before level scaling is applied.
     */
    private static final int XP_PER_KILL = 50;

    /**
     * Exponent for the kill-XP scaling curve; {@code < 1} gives sub linear growth.
     */
    private static final double XP_LEVEL_EXPONENT = 0.75;

    /**
     * Sets the component type handle (called during plugin initialisation).
     *
     * @param type the registered component type
     */
    public static void setComponentType(ComponentType<EntityStore, KillProfessionComponent> type) {
        TYPE = type;
    }

    /**
     * Returns the shared component type handle.
     *
     * @return the component type for {@link KillProfessionComponent}
     */
    public static ComponentType<EntityStore, KillProfessionComponent> getComponentType() {
        return TYPE;
    }

    /**
     * Default constructor; initialises the profession at 0 XP.
     */
    public KillProfessionComponent() {
        super();
    }

    /**
     * Copy constructor.
     *
     * @param other the component to copy state from
     */
    public KillProfessionComponent(KillProfessionComponent other) {
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
        return Math.max(1, (int) Math.round(XP_PER_KILL * Math.pow(level, XP_LEVEL_EXPONENT)));
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return new KillProfessionComponent(this);
    }

    /**
     * Builds and returns the {@link BuilderCodec} used to serialise this component.
     *
     * @return the codec for {@link KillProfessionComponent}
     */
    public static BuilderCodec<KillProfessionComponent> buildCodec() {
        var playerExperience = new KeyedCodec<>("PlayerExperience", Codec.INTEGER);

        return BuilderCodec.builder(KillProfessionComponent.class, KillProfessionComponent::new)
                .append(
                        playerExperience,
                        (data, value) -> data.experience = value,
                        (data) -> data.experience)
                .addValidator(Validators.nonNull())
                .add()
                .build();
    }

    @Override
    public String toString() {
        return "KillProfessionComponent{level=" + this.getLevel() +
                ", Experience=" + this.experience +
                ", toNext=" + this.getExperienceToNextLevel() + "}";
    }

    /**
     * @return {@code "Kill Profession"}
     */
    @Override
    public String getProfessionName() {
        return "Kill Profession";
    }

    /**
     * @return primary level-up UI colour {@code "#c300ff"}
     */
    @Override
    public String getLevelUpgradeColor() {
        return "#c300ff";
    }

    /**
     * @return secondary level-up UI colour {@code "#8b32ff"}
     */
    @Override
    public String getLevelUpgradeSecondaryColor() {
        return "#8b32ff";
    }

    /**
     * @return level-up notification icon {@code "Weapon_Boltpistol"}
     */
    @Override
    public String getLevelUpgradeIcon() {
        return "Weapon_Boltpistol";
    }
}
