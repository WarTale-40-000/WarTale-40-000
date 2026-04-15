package com.warhammer.wartale.assets.armorVariants;

import com.hypixel.hytale.assetstore.codec.ContainedAssetCodec;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;

import java.util.LinkedHashSet;

public final class ArmorVariantConfigCodec {

    private ArmorVariantConfigCodec() {}

    public static final Codec<String> ITEM_REF =
            new ContainedAssetCodec<>(Item.class, Item.CODEC);

    public static final BuilderCodec<ArmorVariantConfig> CODEC;

    static {
        var b = BuilderCodec.builder(ArmorVariantConfig.class, ArmorVariantConfig::new);

        b.appendInherited(
                        new KeyedCodec<>("Items", new ArrayCodec<>(ITEM_REF, String[]::new)),
                        (o, v) -> o.itemIds = (v == null ? new String[0] : v),
                        (o) -> o.itemIds,
                        (o, p) -> o.itemIds = p.itemIds
                )
                .documentation("List of Item asset ids grouped by this armor variant entry.")
                .addValidator(Validators.nonNull())
                .add();

        b.afterDecode((o, extra) -> {
            if (o.id == null) o.id = "";
            o.itemIds = sanitize(o.itemIds);
        });

        CODEC = b.build();
    }

    private static String[] sanitize(String[] raw) {
        if (raw == null || raw.length == 0) {
            return new String[0];
        }

        LinkedHashSet<String> unique = new LinkedHashSet<>();
        for (String value : raw) {
            if (value == null) continue;

            String id = value.trim();
            if (!id.isEmpty()) {
                unique.add(id);
            }
        }

        return unique.toArray(new String[0]);
    }
}