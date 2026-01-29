package com.warhammer.wartale.types;

public class WarhammerWeaponMetadata {
    private String id;
    private Integer maxAmmo;
    private Integer reload;

    public WarhammerWeaponMetadata(String id, Integer maxAmmo, Integer reload) {
        this.id = id;
        this.maxAmmo = maxAmmo;
        this.reload = reload;
    }

    public String getId() {
        return this.id;
    }

    public Integer getMaxAmmo() {
        return this.maxAmmo;
    }

    public Integer getReload() {
        return this.reload;
    }
}
