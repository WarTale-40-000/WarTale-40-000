package com.warhammer.wartale.types;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;

public class WarhammerWeaponMetadata {
    private String id;
    private String ammoId;
    private Integer maxAmmo;
    private Integer reload;

    public static final BuilderCodec<WarhammerWeaponMetadata> CODEC = BuilderCodec.<WarhammerWeaponMetadata>builder(WarhammerWeaponMetadata.class, WarhammerWeaponMetadata::new)
            .append(new KeyedCodec<String>("WarhammerWeaponId", Codec.STRING),
                    (data, value) -> data.id = value,
                    (data) -> data.id)
            .add()
            .append(new KeyedCodec<String>("WarhammerAmmoId", Codec.STRING),
                    (data, value) -> data.ammoId = value,
                    (data) -> data.ammoId)
            .add()
            .append(new KeyedCodec<Integer>("WarhammerMaxAmmo", Codec.INTEGER),
                    (data, value) -> data.maxAmmo = value,
                    (data) -> data.maxAmmo)
            .add()
            .append(new KeyedCodec<Integer>("WarhammerReload", Codec.INTEGER),
                    (data, value) -> data.reload = value,
                    (data) -> data.reload)
            .add()
            .build();

    public WarhammerWeaponMetadata() {
    }

    public WarhammerWeaponMetadata(String id, String ammoId, Integer maxAmmo, Integer reload) {
        this.id = id;
        this.ammoId = ammoId;
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

    public String getAmmoId() {
        return this.ammoId;
    }
}
