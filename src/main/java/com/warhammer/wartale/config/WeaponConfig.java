package com.warhammer.wartale.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.warhammer.wartale.types.WarhammerWeaponMetadata;

import java.util.HashMap;
import java.util.Map;

public class WeaponConfig {
    // Registered Weapons
    private Map<String, WarhammerWeaponMetadata> weapons = new HashMap<String, WarhammerWeaponMetadata>() {
        {
            put("Warhammer_Bolter_Pistol", new WarhammerWeaponMetadata("Warhammer_Bolter_Pistol", "Warhammer_Ammo_Bolter_Pistol", 13, 1));
            put("Warhammer_Autogun", new WarhammerWeaponMetadata("Warhammer_Autogun", "Warhammer_Ammo_Autogun", 35, 3));
        }
    };

    // MetadataKey for consistent integration
    private String metadataKey;

    public static final BuilderCodec<WeaponConfig> CODEC = BuilderCodec
            .builder(WeaponConfig.class, WeaponConfig::new)
            .append(new KeyedCodec<Map<String, WarhammerWeaponMetadata>>("WarhammerWeaponConfig",
                            new MapCodec<>(WarhammerWeaponMetadata.CODEC, HashMap<String, WarhammerWeaponMetadata>::new)),
                    (config, value) -> config.weapons = value,
                    (config) -> config.weapons)
            .add()
            .append(new KeyedCodec<String>("WarhammerWeaponMetadataKey",
                            Codec.STRING),
                    (config, value) -> config.metadataKey = value,
                    (config) -> config.metadataKey)
            .add()
            .build();

    public WeaponConfig() {
        this.metadataKey = "WarhammerAmmo";
    }

    public Map<String, WarhammerWeaponMetadata> getWeapons() {
        return this.weapons;
    }

    public String getMetadataKey() {
        return this.metadataKey;
    }

}
