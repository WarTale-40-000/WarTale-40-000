package com.warhammer.wartale.assets.armorVariants;

import com.hypixel.hytale.assetstore.AssetUpdateQuery;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public final class ArmorVariantStore extends HytaleAssetStore<
        String,
        ArmorVariantConfig,
        IndexedLookupTableAssetMap<String, ArmorVariantConfig>> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private static final String PATH = "Wartale/ArmorVariants";

    private static ArmorVariantStore INSTANCE;
    private static ArmorVariantCatalog CATALOG = ArmorVariantCatalog.empty();

    private final Map<String, ArmorVariantConfig> loadedConfigsById = new LinkedHashMap<>();

    private ArmorVariantStore(
            @Nonnull Builder<String, ArmorVariantConfig, IndexedLookupTableAssetMap<String, ArmorVariantConfig>> builder
    ) {
        super(builder);
    }

    @Nonnull
    public static ArmorVariantStore create() {
        var map = new IndexedLookupTableAssetMap<>(ArmorVariantConfig[]::new);
        var b = HytaleAssetStore.builder(String.class, ArmorVariantConfig.class, map);

        b.setPath(PATH)
                .setCodec(ArmorVariantConfig.CODEC)
                .setKeyFunction(ArmorVariantConfig::getId)
                .setIdProvider(ArmorVariantConfig.class)
                .setIsUnknown(ArmorVariantConfig::isUnknown);

        b.setReplaceOnRemove(ArmorVariantStore::missing);

        ArmorVariantStore store = new ArmorVariantStore(b);
        INSTANCE = store;
        return store;
    }

    @Nullable
    public static ArmorVariantStore getInstance() {
        return INSTANCE;
    }

    @Nonnull
    public static ArmorVariantCatalog getCatalog() {
        return CATALOG;
    }

    @Nonnull
    public static Iterable<ArmorVariantConfig> getLoadedConfigs() {
        ArmorVariantStore instance = INSTANCE;
        if (instance == null) {
            return Collections.emptyList();
        }

        return Collections.unmodifiableCollection(instance.loadedConfigsById.values());
    }

    @Override
    protected void handleRemoveOrUpdate(
            @Nullable Set<String> removedKeys,
            @Nullable Map<String, ArmorVariantConfig> loadedOrUpdated,
            @Nonnull AssetUpdateQuery query
    ) {
        super.handleRemoveOrUpdate(removedKeys, loadedOrUpdated, query);

        if (removedKeys != null) {
            for (String removedKey : removedKeys) {
                if (removedKey != null) {
                    loadedConfigsById.remove(removedKey);
                }
            }
        }

        if (loadedOrUpdated != null) {
            for (ArmorVariantConfig cfg : loadedOrUpdated.values()) {
                if (cfg == null) {
                    continue;
                }

                loadedConfigsById.put(cfg.getId(), cfg);
                validateConfig(cfg);
            }
        }

        CATALOG = ArmorVariantCatalog.build(loadedConfigsById.values());

        LOGGER.atInfo().log(
                "loaded=%d removed=%d total=%d catalogItems=%d",
                loadedOrUpdated == null ? 0 : loadedOrUpdated.size(),
                removedKeys == null ? 0 : removedKeys.size(),
                loadedConfigsById.size(),
                CATALOG.size()
        );
    }

    private static void validateConfig(@Nonnull ArmorVariantConfig cfg) {
        if (cfg.isUnknown()) {
            return;
        }

        for (String itemId : cfg.getItemIds()) {
            if (itemId == null || itemId.isBlank()) {
                continue;
            }

            Item item = Item.getAssetMap().getAsset(itemId);

            if (item == null) {
                LOGGER.atWarning().log(
                        "%s references missing item %s",
                        cfg.getId(),
                        itemId
                );
                continue;
            }

            if (item.getArmor() == null) {
                LOGGER.atWarning().log(
                        "%s references non-armor item %s",
                        cfg.getId(),
                        itemId
                );
            }
        }
    }

    private static ArmorVariantConfig missing(String id) {
        ArmorVariantConfig cfg = new ArmorVariantConfig();
        cfg.id = (id == null ? "" : id);
        cfg.unknown = true;
        cfg.itemIds = new String[0];
        return cfg;
    }
}