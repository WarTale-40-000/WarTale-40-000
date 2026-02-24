package com.warhammer.wartale.components;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.config.WeaponConfig;
import com.warhammer.wartale.core.ServiceRegistry;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.HashMap;
import java.util.Map;

public class Weapon_Data implements Component<EntityStore> {

    private String currentWeaponId;

    private boolean isHudVisible;

    private Map<String, Integer> currentAmmo;

    public static final BuilderCodec<Weapon_Data> CODEC = Weapon_Data.buildCodec();

    public Weapon_Data() {
        this.currentWeaponId = null;
        this.isHudVisible = false;
        this.currentAmmo = new HashMap<>();
    }

    public Weapon_Data(Weapon_Data other) {
        this.currentWeaponId = other.getCurrentWeaponId();
        this.isHudVisible = other.isHudVisible;
        this.currentAmmo = new HashMap<>(other.currentAmmo);
    }

    public Map<String, Integer> getCurrentAmmo() {
        return this.currentAmmo;
    }

    public void setCurrentAmmo(Map<String, Integer> currentAmmo) {
        this.currentAmmo = currentAmmo;
    }

    public String getCurrentWeaponId() {
        return this.currentWeaponId;
    }

    public void setCurrentWeaponId(String id) {
        WeaponConfig weaponConfig = ServiceRegistry.get(WeaponConfig.class);
        if (!weaponConfig.getWeapons().containsKey(id)) {
            throw new IllegalArgumentException("The given weapon Id does not exist in the config files.");
        }
        this.currentWeaponId = id;
    }

    public boolean isHudVisible() {
        return this.isHudVisible;
    }

    public void setHudVisible(boolean visible) {
        this.isHudVisible = visible;
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
