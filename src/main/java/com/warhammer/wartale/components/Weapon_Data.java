package com.warhammer.wartale.components;

import java.util.HashMap;
import java.util.Map;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class Weapon_Data implements Component<EntityStore> {

    private Map<String, Integer> currentAmmo;

    public static final BuilderCodec<Weapon_Data> CODEC = Weapon_Data.buildCodec();

    public Weapon_Data() {
        this.currentAmmo = new HashMap<>();
    }

    public Weapon_Data(Weapon_Data other) {
        this.currentAmmo = new HashMap<>(other.currentAmmo);
    }

    public Map<String, Integer> getCurrentAmmo() {
        return currentAmmo;
    }

    public void setCurrentAmmo(Map<String, Integer> currentAmmo) {
        this.currentAmmo = currentAmmo;
    }

    @NullableDecl
    @Override
    public Component<EntityStore> clone() {
        return new Weapon_Data(this);
    }

    public static BuilderCodec<Weapon_Data> buildCodec() {
        var currentAmmoCodec = new KeyedCodec<>("CurrentAmmo", new MapCodec<>(Codec.INTEGER, HashMap::new, false));

        return BuilderCodec.builder(Weapon_Data.class, Weapon_Data::new)
                .append(
                        currentAmmoCodec,
                        (data, value) -> data.currentAmmo = value,
                        (data) -> data.currentAmmo)
                .addValidator(Validators.nonNull())
                .add()
                .build();
    }
}
