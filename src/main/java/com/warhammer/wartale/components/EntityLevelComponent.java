package com.warhammer.wartale.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class EntityLevelComponent implements Component<EntityStore> {
    private static ComponentType<EntityStore, EntityLevelComponent> TYPE;

    
    public static final BuilderCodec<EntityLevelComponent> CODEC = buildCodec();

    
    private int level;

    
    public EntityLevelComponent() {
        this.level = 1;
    }

    
    public EntityLevelComponent(int level) {
        this.level = level;
    }

    
    public EntityLevelComponent(EntityLevelComponent other) {
        this.level = other.level;
    }

    
    public int getLevel() {
        return level;
    }

    
    public void setLevel(int level) {
        if (level < 1) {
            throw new IllegalArgumentException("Level cannot be less than 1");
        }
        this.level = level;
    }

    
    public static ComponentType<EntityStore, EntityLevelComponent> getComponentType() {
        return TYPE;
    }

    
    public static void setComponentType(ComponentType<EntityStore, EntityLevelComponent> type) {
        TYPE = type;
    }

    
    public static BuilderCodec<EntityLevelComponent> buildCodec() {
        KeyedCodec<Integer> entityLevel = new KeyedCodec<>("EntityLevel", Codec.INTEGER);

        return BuilderCodec.builder(EntityLevelComponent.class, EntityLevelComponent::new)
                .append(
                        entityLevel,
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
