package com.warhammer.wartale.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.components.professions.KillProfessionComponent;

/**
 * Component that stores the spawn level of an NPC entity.
 * <p>
 * Attached by {@link com.warhammer.wartale.systems.AddLevelToEntitySystem} on spawn
 * and used by {@link com.warhammer.wartale.systems.KillSystem} to calculate XP rewards.
 * Serialised and deserialised via {@link #CODEC}.
 */
public class EntityLevelComponent implements Component<EntityStore> {
    private static ComponentType<EntityStore, EntityLevelComponent> TYPE;

    /** Codec used to serialise/deserialise this component. */
    public static final BuilderCodec<EntityLevelComponent> CODEC = buildCodec();

    /** The entity's level (minimum 1). */
    private int level;

    /**
     * Default constructor; initialises the entity at level 1.
     */
    public EntityLevelComponent() {
        this.level = 1;
    }

    /**
     * Constructs the component with a specific level.
     *
     * @param level the starting level (must be {@code >= 1})
     */
    public EntityLevelComponent(int level) {
        this.level = level;
    }

    /**
     * Copy constructor.
     *
     * @param other the component to copy state from
     */
    public EntityLevelComponent(EntityLevelComponent other) {
        this.level = other.level;
    }

    /**
     * Returns this entity's level.
     *
     * @return current level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Sets this entity's level.
     *
     * @param level the new level
     * @throws IllegalArgumentException if {@code level} is less than 1
     */
    public void setLevel(int level) {
        if (level < 1) {
            throw new IllegalArgumentException("Level cannot be less than 1");
        }
        this.level = level;
    }

    /**
     * Returns the shared component type handle.
     *
     * @return the component type for {@link EntityLevelComponent}
     */
    public static ComponentType<EntityStore, EntityLevelComponent> getComponentType() {
        return TYPE;
    }

    /**
     * Sets the component type handle (called during plugin initialisation).
     *
     * @param type the registered component type
     */
    public static void setComponentType(ComponentType<EntityStore, EntityLevelComponent> type) {
        TYPE = type;
    }

    /**
     * Builds and returns the {@link BuilderCodec} used to serialise this component.
     *
     * @return the codec for {@link EntityLevelComponent}
     */
    public static BuilderCodec<EntityLevelComponent> buildCodec() {
        var playerExperience = new KeyedCodec<>("EntityLevel", Codec.INTEGER);

        return BuilderCodec.builder(EntityLevelComponent.class, EntityLevelComponent::new)
                .append(
                        playerExperience,
                        (data, value) -> data.level = value,
                        (data) -> data.level)
                .addValidator(Validators.nonNull())
                .add()
                .build();
    }

    @Override
    public Component<EntityStore> clone() {
        return new EntityLevelComponent(this);
    }

    @Override
    public String toString() {
        return "EntityLevelComponent{level=" + this.getLevel() + "}";
    }
}
