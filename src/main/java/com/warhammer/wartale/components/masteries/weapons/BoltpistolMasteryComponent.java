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

public class BoltpistolMasteryComponent extends WeaponMasteryComponent {

    private static ComponentType<EntityStore, BoltpistolMasteryComponent> TYPE;

    
    public static final BuilderCodec<BoltpistolMasteryComponent> CODEC = buildCodec();

    
    public static void setComponentType(ComponentType<EntityStore, BoltpistolMasteryComponent> type) {
        TYPE = type;
    }

    
    public static ComponentType<EntityStore, BoltpistolMasteryComponent> getComponentType() {
        return TYPE;
    }

    
    public BoltpistolMasteryComponent() {
        super("Boltpistol Mastery");
    }

    
    public BoltpistolMasteryComponent(BoltpistolMasteryComponent other) {
        super(other);
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return new BoltpistolMasteryComponent(this);
    }

    
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
