package com.warhammer.wartale.config;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.warhammer.wartale.types.WarhammerWeaponMetadata;

import java.util.HashMap;
import java.util.Map;

public class WeaponConfig {
    private Map<String, WarhammerWeaponMetadata> weapons = new HashMap<String, WarhammerWeaponMetadata>() {
        {
            put("Warhammer_Bolter_Pistol", new WarhammerWeaponMetadata("Warhammer_Bolter_Pistol", "Warhammer_Ammo_Bolter_Pistol", 13, 1));
            put("Warhammer_Autogun", new WarhammerWeaponMetadata("Warhammer_Autogun", "Warhammer_Ammo_Autogun", 35, 3));
        }
    };

    public static final BuilderCodec<WeaponConfig> CODEC = BuilderCodec
            .builder(WeaponConfig.class, WeaponConfig::new)
            .append(new KeyedCodec<Map<String, WarhammerWeaponMetadata>>("WarhammerWeaponConfig",
                            new MapCodec<>(WarhammerWeaponMetadata.CODEC, HashMap<String, WarhammerWeaponMetadata>::new)),
                    (config, value) -> config.weapons = value,
                    (config) -> config.weapons)
            .add()
            .build();

    public WeaponConfig() {
    }

    public Map<String, WarhammerWeaponMetadata> getWeapons() {
        return weapons;
    }

}
