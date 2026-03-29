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

public class KillProfessionComponent extends BaseProfessionComponent {
    private static ComponentType<EntityStore, KillProfessionComponent> TYPE;
    public static final BuilderCodec<KillProfessionComponent> CODEC = buildCodec();

    public static void setComponentType(ComponentType<EntityStore, KillProfessionComponent> type) {
        TYPE = type;
    }

    public static ComponentType<EntityStore, KillProfessionComponent> getComponentType() {
        return TYPE;
    }

    public KillProfessionComponent() {
        super();
    }

    public KillProfessionComponent(KillProfessionComponent other) {
        super(other);
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return new KillProfessionComponent(this);
    }

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

    @NullableDecl
    @Override
    public String toString() {
        return "KillProfessionComponent{level=" + this.getLevel() +
                ", Experience=" + this.experience +
                ", toNext=" + this.getExperienceToNextLevel() + "}";
    }

    @Override
    public String getProfessionName() {
        return "Kill Profession";
    }
}
