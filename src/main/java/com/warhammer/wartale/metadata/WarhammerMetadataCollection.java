package com.warhammer.wartale.metadata;

import com.warhammer.wartale.types.WarhammerWeaponMetadata;

import java.util.HashMap;
import java.util.Map;

public class WarhammerMetadataCollection {
    public static final Map<String, WarhammerWeaponMetadata> WEAPON_METADATA_MAP = buildWarhammerMetadataCollection();

    private static Map<String, WarhammerWeaponMetadata> buildWarhammerMetadataCollection() {
        Map<String, WarhammerWeaponMetadata> map = new HashMap<>();
        map.put("Warhammer_Bolter_Pistol_custom", new WarhammerWeaponMetadata("Warhammer_Bolter_Pistol_custom", 13, 5));
        map.put("Boltgun_Reload", new WarhammerWeaponMetadata("Boltgun_Reload", 13, 5));

        return map;
    }
}
