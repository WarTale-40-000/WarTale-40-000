package com.warhammer.wartale.items;

public enum WeaponMetadataKey {
    CURRENT_AMMO("current_ammo"),
    MAG_SIZE("mag_size");

    private final String key;

    WeaponMetadataKey(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }
}
