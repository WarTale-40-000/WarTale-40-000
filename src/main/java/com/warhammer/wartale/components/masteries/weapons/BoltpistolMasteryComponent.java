package com.warhammer.wartale.components.masteries.weapons;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.components.masteries.WeaponMasteryComponent;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

/**
 * Mastery component tracking a player's Boltpistol proficiency.
 * <p>
 * Inherits XP and levelling logic from {@link WeaponMasteryComponent}.
 * Serialised and deserialised via {@link #CODEC}.
 */
public class BoltpistolMasteryComponent extends WeaponMasteryComponent {

    private static ComponentType<EntityStore, BoltpistolMasteryComponent> TYPE;

    /**
     * Codec used to serialise/deserialise this component.
     */
    public static final BuilderCodec<BoltpistolMasteryComponent> CODEC = buildCodec();

    /**
     * Sets the component type handle (called during plugin initialisation).
     *
     * @param type the registered component type
     */
    public static void setComponentType(ComponentType<EntityStore, BoltpistolMasteryComponent> type) {
        TYPE = type;
    }

    /**
     * Returns the shared component type handle.
     *
     * @return the component type for {@link BoltpistolMasteryComponent}
     */
    public static ComponentType<EntityStore, BoltpistolMasteryComponent> getComponentType() {
        return TYPE;
    }

    /**
     * Default constructor; initialises the mastery with the Boltpistol weapon ID.
     */
    public BoltpistolMasteryComponent() {
        super("Boltpistol Mastery");
    }

    /**
     * Copy constructor.
     *
     * @param other the component to copy state from
     */
    public BoltpistolMasteryComponent(BoltpistolMasteryComponent other) {
        super(other);
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return new BoltpistolMasteryComponent(this);
    }

    /**
     * @return level-up notification icon {@code "Weapon_Boltpistol"}
     */
    @Override
    public String getLevelUpgradeIcon() {
        return "Weapon_Boltpistol";
    }

    private static BuilderCodec<BoltpistolMasteryComponent> buildCodec() {
        var playerExperience = new KeyedCodec<>("PlayerExperience", Codec.INTEGER);

        return BuilderCodec.builder(BoltpistolMasteryComponent.class, BoltpistolMasteryComponent::new)
                .append(
                        playerExperience,
                        (data, value) -> data.experience = value,
                        (data) -> data.experience)
                .addValidator(Validators.nonNull())
                .add()
                .build();
    }
}
