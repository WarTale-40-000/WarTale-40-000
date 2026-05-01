package com.warhammer.wartale.gui;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.ItemArmorSlot;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.protocol.packets.interface_.CustomUIEventBindingType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.entities.player.pages.InteractiveCustomUIPage;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.ui.builder.EventData;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.ui.builder.UIEventBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.assets.armorVariants.ArmorVariantStore;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;

public final class ArmorVariantPage extends InteractiveCustomUIPage<ArmorVariantPage.BindingData> {

    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    private static final String UI_PATH = "Pages/ArmorVariantPage.ui";
    private static final int VISIBLE_VARIANTS = 3;

    private static final String ACTION_CLOSE = "CLOSE";
    private static final String ACTION_PREV = "PREV";
    private static final String ACTION_NEXT = "NEXT";
    private static final String ACTION_SELECT_CURRENT = "SELECT_CURRENT";
    private static final String ACTION_SELECT_VARIANT = "SELECT_VARIANT";

    private final EnumMap<ArmorPiece, Integer> carouselOffsets = new EnumMap<>(ArmorPiece.class);

    public ArmorVariantPage(
            @Nonnull PlayerRef playerRef,
            @Nonnull CustomPageLifetime lifetime
    ) {
        super(playerRef, lifetime, BindingData.CODEC);

        for (ArmorPiece piece : ArmorPiece.values()) {
            this.carouselOffsets.put(piece, 0);
        }
    }

    @Override
    public void build(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull UICommandBuilder ui,
            @Nonnull UIEventBuilder events,
            @Nonnull Store<EntityStore> store
    ) {
        ui.append(UI_PATH);
        bindEvents(events);
        writeSnapshot(ui, buildSnapshot(ref, store));
    }

    @Override
    public void handleDataEvent(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull Store<EntityStore> store,
            @Nonnull BindingData data
    ) {
        String action = safe(data.action);

        if (action.isEmpty()) {
            return;
        }

        if (ACTION_CLOSE.equals(action)) {
            close();
            return;
        }

        ArmorPiece piece = ArmorPiece.fromWireName(data.piece);
        if (piece == null) {
            refresh(ref, store);
            return;
        }

        if (ACTION_SELECT_CURRENT.equals(action)) {
            carouselOffsets.put(piece, 0);
            refresh(ref, store);
            return;
        }

        InventoryComponent.Armor armor = getArmorComponent(store, ref);
        if (armor == null) {
            refresh(ref, store);
            return;
        }

        ItemStack currentStack = getArmorStack(armor, piece);
        VariantMatch match = findVariantMatch(currentStack);
        List<String> variants = match == null ? Collections.emptyList() : match.variantItemIds;

        int offset = getClampedOffset(piece, variants.size());

        switch (action) {
            case ACTION_PREV -> {
                carouselOffsets.put(piece, Math.max(0, offset - VISIBLE_VARIANTS));
                refresh(ref, store);
            }

            case ACTION_NEXT -> {
                int maxOffset = Math.max(0, variants.size() - VISIBLE_VARIANTS);
                carouselOffsets.put(piece, Math.min(maxOffset, offset + VISIBLE_VARIANTS));
                refresh(ref, store);
            }

            case ACTION_SELECT_VARIANT -> {
                int visibleIndex = parseIndex(data.index);
                int absoluteIndex = offset + visibleIndex;

                if (absoluteIndex >= 0 && absoluteIndex < variants.size()) {
                    String selectedItemId = variants.get(absoluteIndex);
                    applyArmorVariant(armor, piece, selectedItemId);
                    carouselOffsets.put(piece, 0);
                }

                refresh(ref, store);
            }

            default -> refresh(ref, store);
        }
    }

    public void refresh(@Nonnull Ref<EntityStore> ref, @Nonnull Store<EntityStore> store) {
        UICommandBuilder patch = new UICommandBuilder();
        writeSnapshot(patch, buildSnapshot(ref, store));
        sendUpdate(patch, false);
    }

    public void refresh() {
        Ref<EntityStore> ref = playerRef.getReference();
        if (ref == null) {
            return;
        }

        refresh(ref, ref.getStore());
    }

    private void bindEvents(@Nonnull UIEventBuilder events) {
        bind(events, "#CloseButton", ACTION_CLOSE, "", -1);

        bind(events, "#CurrentHelmetButton", ACTION_SELECT_CURRENT, ArmorPiece.HELMET.wireName, -1);
        bind(events, "#CurrentChestButton", ACTION_SELECT_CURRENT, ArmorPiece.CHEST.wireName, -1);
        bind(events, "#CurrentHandsButton", ACTION_SELECT_CURRENT, ArmorPiece.HANDS.wireName, -1);
        bind(events, "#CurrentLegsButton", ACTION_SELECT_CURRENT, ArmorPiece.LEGS.wireName, -1);

        bindPieceEvents(events, ArmorPiece.HELMET);
        bindPieceEvents(events, ArmorPiece.CHEST);
        bindPieceEvents(events, ArmorPiece.HANDS);
        bindPieceEvents(events, ArmorPiece.LEGS);
    }

    private static void bindPieceEvents(@Nonnull UIEventBuilder events, @Nonnull ArmorPiece piece) {
        String prefix = piece.uiPrefix;

        bind(events, "#" + prefix + "PrevButton", ACTION_PREV, piece.wireName, -1);
        bind(events, "#" + prefix + "NextButton", ACTION_NEXT, piece.wireName, -1);

        bind(events, "#" + prefix + "Variant1Button", ACTION_SELECT_VARIANT, piece.wireName, 0);
        bind(events, "#" + prefix + "Variant2Button", ACTION_SELECT_VARIANT, piece.wireName, 1);
        bind(events, "#" + prefix + "Variant3Button", ACTION_SELECT_VARIANT, piece.wireName, 2);
    }

    private static void bind(
            @Nonnull UIEventBuilder events,
            @Nonnull String selector,
            @Nonnull String action,
            @Nonnull String piece,
            int index
    ) {
        events.addEventBinding(
                CustomUIEventBindingType.Activating,
                selector,
                new EventData()
                        .append("Action", action)
                        .append("Piece", piece)
                        .append("Index", String.valueOf(index)),
                false
        );
    }

    @Nonnull
    private PageSnapshot buildSnapshot(
            @Nonnull Ref<EntityStore> ref,
            @Nonnull Store<EntityStore> store
    ) {
        InventoryComponent.Armor armor = getArmorComponent(store, ref);

        PageSnapshot snapshot = new PageSnapshot();

        snapshot.helmet = buildPieceView(armor, ArmorPiece.HELMET);
        snapshot.chest = buildPieceView(armor, ArmorPiece.CHEST);
        snapshot.hands = buildPieceView(armor, ArmorPiece.HANDS);
        snapshot.legs = buildPieceView(armor, ArmorPiece.LEGS);

        return snapshot;
    }

    @Nonnull
    private PieceView buildPieceView(@Nullable InventoryComponent.Armor armor, @Nonnull ArmorPiece piece) {
        PieceView view = new PieceView();

        if (armor == null) {
            return view;
        }

        ItemStack currentStack = getArmorStack(armor, piece);
        if (ItemStack.isEmpty(currentStack)) {
            return view;
        }

        VariantMatch match = findVariantMatch(currentStack);

        if (match == null) {
            view.currentItemId = itemIdOf(currentStack);
            return view;
        }

        view.currentItemId = match.currentItemId;

        int offset = getClampedOffset(piece, match.variantItemIds.size());
        int end = Math.min(match.variantItemIds.size(), offset + VISIBLE_VARIANTS);

        if (offset >= 0 && offset < end) {
            view.visibleVariantItemIds = new ArrayList<>(match.variantItemIds.subList(offset, end));
        }

        view.canPrev = offset > 0;
        view.canNext = offset + VISIBLE_VARIANTS < match.variantItemIds.size();

        return view;
    }

    @Nullable
    private VariantMatch findVariantMatch(@Nullable ItemStack currentStack) {
        if (ItemStack.isEmpty(currentStack)) {
            return null;
        }

        String currentItemId = safe(currentStack.getItemId());
        if (currentItemId.isEmpty()) {
            return null;
        }

        List<String> variants = ArmorVariantStore
                .getCatalog()
                .findVariantItemIdsExcluding(currentItemId);

        if (variants.isEmpty()) {
            return null;
        }

        VariantMatch match = new VariantMatch();
        match.currentItemId = currentItemId;
        match.variantItemIds = variants;
        return match;
    }

    private void applyArmorVariant(
            @Nonnull InventoryComponent.Armor armor,
            @Nonnull ArmorPiece piece,
            @Nonnull String newItemId
    ) {
        String cleanItemId = safe(newItemId);
        if (cleanItemId.isEmpty()) {
            return;
        }

        Item newItem = Item.getAssetMap().getAsset(cleanItemId);
        if (newItem == null || newItem == Item.UNKNOWN) {
            LOGGER.atWarning().log("Armor variant item does not exist: %s", cleanItemId);
            return;
        }

        if (newItem.getArmor() == null) {
            LOGGER.atWarning().log("Armor variant item is not armor: %s", cleanItemId);
            return;
        }

        ItemStack oldStack = getArmorStack(armor, piece);
        ItemStack newStack = createVariantStack(cleanItemId, newItem, oldStack);

        if (!setArmorStack(armor, piece, newStack)) {
            LOGGER.atWarning().log("Could not set armor slot %s to %s", piece.wireName, cleanItemId);
            return;
        }

        armor.markDirty();
    }

    @Nonnull
    private static ItemStack createVariantStack(
            @Nonnull String newItemId,
            @Nonnull Item newItem,
            @Nullable ItemStack oldStack
    ) {
        if (ItemStack.isEmpty(oldStack)) {
            return new ItemStack(newItemId, 1);
        }

        int quantity = Math.max(1, oldStack.getQuantity());

        double newMaxDurability = newItem.getMaxDurability();
        double oldMaxDurability = oldStack.getMaxDurability();
        double newDurability;

        if (newMaxDurability <= 0.0D || oldMaxDurability <= 0.0D) {
            newDurability = newMaxDurability;
        } else {
            double ratio = oldStack.getDurability() / oldMaxDurability;
            newDurability = Math.clamp(ratio * newMaxDurability, 0.0D, newMaxDurability);
        }

        ItemStack newStack = new ItemStack(
                newItemId,
                quantity,
                newDurability,
                newMaxDurability,
                oldStack.getMetadata()
        );

        newStack.setOverrideDroppedItemAnimation(oldStack.getOverrideDroppedItemAnimation());

        return newStack;
    }

    private static boolean setArmorStack(
            @Nonnull InventoryComponent.Armor armor,
            @Nonnull ArmorPiece piece,
            @Nonnull ItemStack stack
    ) {
        ItemContainer container = armor.getInventory();
        if (container == null) {
            return false;
        }

        if (!isValidSlot(container, piece.slotIndex)) {
            return false;
        }

        return container.setItemStackForSlot(piece.slotIndex, stack, true).succeeded();
    }

    @Nullable
    private static InventoryComponent.Armor getArmorComponent(
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref
    ) {
        return store.getComponent(ref, InventoryComponent.Armor.getComponentType());
    }

    @Nullable
    private static ItemStack getArmorStack(
            @Nonnull InventoryComponent.Armor armor,
            @Nonnull ArmorPiece piece
    ) {
        ItemContainer container = armor.getInventory();

        if (container == null || !isValidSlot(container, piece.slotIndex)) {
            return null;
        }

        return container.getItemStack(piece.slotIndex);
    }

    private static boolean isValidSlot(@Nonnull ItemContainer container, short slot) {
        return slot >= 0 && slot < container.getCapacity();
    }

    private int getClampedOffset(@Nonnull ArmorPiece piece, int variantCount) {
        int currentOffset = carouselOffsets.getOrDefault(piece, 0);
        int maxOffset = Math.max(0, variantCount - VISIBLE_VARIANTS);

        int clamped = Math.clamp(currentOffset, 0, maxOffset);
        carouselOffsets.put(piece, clamped);

        return clamped;
    }

    private static void writeSnapshot(
            @Nonnull UICommandBuilder patch,
            @Nonnull PageSnapshot snapshot
    ) {
        writeCurrentSlot(patch, "#CurrentHelmetButton", "#CurrentHelmetIcon", snapshot.helmet.currentItemId);
        writeCurrentSlot(patch, "#CurrentChestButton", "#CurrentChestIcon", snapshot.chest.currentItemId);
        writeCurrentSlot(patch, "#CurrentHandsButton", "#CurrentHandsIcon", snapshot.hands.currentItemId);
        writeCurrentSlot(patch, "#CurrentLegsButton", "#CurrentLegsIcon", snapshot.legs.currentItemId);

        writeVariantRow(patch, ArmorPiece.HELMET, snapshot.helmet);
        writeVariantRow(patch, ArmorPiece.CHEST, snapshot.chest);
        writeVariantRow(patch, ArmorPiece.HANDS, snapshot.hands);
        writeVariantRow(patch, ArmorPiece.LEGS, snapshot.legs);
    }

    private static void writeCurrentSlot(
            @Nonnull UICommandBuilder patch,
            @Nonnull String buttonSelector,
            @Nonnull String iconSelector,
            @Nullable String itemIdRaw
    ) {
        String itemId = safe(itemIdRaw);
        boolean hasItem = !itemId.isEmpty();

        patch.set(buttonSelector + ".Disabled", !hasItem);
        patch.set(iconSelector + ".Visible", hasItem);
        patch.set(iconSelector + ".ItemId", itemId);
    }

    private static void writeVariantRow(
            @Nonnull UICommandBuilder patch,
            @Nonnull ArmorPiece piece,
            @Nonnull PieceView view
    ) {
        String prefix = piece.uiPrefix;

        patch.set("#" + prefix + "PrevButton.Disabled", !view.canPrev);
        patch.set("#" + prefix + "NextButton.Disabled", !view.canNext);

        writeVariantSlot(patch, "#" + prefix + "Variant1Button", "#" + prefix + "Variant1Icon", getOrEmpty(view.visibleVariantItemIds, 0));
        writeVariantSlot(patch, "#" + prefix + "Variant2Button", "#" + prefix + "Variant2Icon", getOrEmpty(view.visibleVariantItemIds, 1));
        writeVariantSlot(patch, "#" + prefix + "Variant3Button", "#" + prefix + "Variant3Icon", getOrEmpty(view.visibleVariantItemIds, 2));
    }

    private static void writeVariantSlot(
            @Nonnull UICommandBuilder patch,
            @Nonnull String buttonSelector,
            @Nonnull String iconSelector,
            @Nullable String itemIdRaw
    ) {
        String itemId = safe(itemIdRaw);
        boolean hasItem = !itemId.isEmpty();

        patch.set(buttonSelector + ".Disabled", !hasItem);
        patch.set(iconSelector + ".Visible", hasItem);
        patch.set(iconSelector + ".ItemId", itemId);
    }

    @Nonnull
    private static String itemIdOf(@Nullable ItemStack stack) {
        if (ItemStack.isEmpty(stack)) {
            return "";
        }

        return safe(stack.getItemId());
    }

    @Nonnull
    private static String getOrEmpty(@Nonnull List<String> values, int index) {
        if (index < 0 || index >= values.size()) {
            return "";
        }

        return safe(values.get(index));
    }

    @Nonnull
    private static String safe(@Nullable String value) {
        return value == null ? "" : value.trim();
    }

    private static int parseIndex(@Nullable String value) {
        String clean = safe(value);

        if (clean.isEmpty()) {
            return -1;
        }

        try {
            return Integer.parseInt(clean);
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }

    public enum ArmorPiece {
        HELMET("HELMET", "Helmet", (short) ItemArmorSlot.Head.getValue()),
        CHEST("CHEST", "Chest", (short) ItemArmorSlot.Chest.getValue()),
        HANDS("HANDS", "Hands", (short) ItemArmorSlot.Hands.getValue()),
        LEGS("LEGS", "Legs", (short) ItemArmorSlot.Legs.getValue());

        private final String wireName;
        private final String uiPrefix;
        private final short slotIndex;

        ArmorPiece(@Nonnull String wireName, @Nonnull String uiPrefix, short slotIndex) {
            this.wireName = wireName;
            this.uiPrefix = uiPrefix;
            this.slotIndex = slotIndex;
        }

        @Nullable
        public static ArmorPiece fromWireName(@Nullable String valueRaw) {
            String value = safe(valueRaw);

            for (ArmorPiece piece : values()) {
                if (piece.wireName.equals(value)) {
                    return piece;
                }
            }

            return null;
        }
    }

    private static final class PageSnapshot {
        private PieceView helmet = new PieceView();
        private PieceView chest = new PieceView();
        private PieceView hands = new PieceView();
        private PieceView legs = new PieceView();
    }

    private static final class PieceView {
        private String currentItemId = "";
        private List<String> visibleVariantItemIds = Collections.emptyList();
        private boolean canPrev = false;
        private boolean canNext = false;
    }

    private static final class VariantMatch {
        private String currentItemId = "";
        private List<String> variantItemIds = Collections.emptyList();
    }

    public static final class BindingData {
        public static final BuilderCodec<BindingData> CODEC;

        static {
            var b = BuilderCodec.builder(BindingData.class, BindingData::new);

            b.append(
                            new KeyedCodec<>("Action", Codec.STRING),
                            (data, value) -> data.action = value == null ? "" : value,
                            data -> data.action
                    )
                    .documentation("UI action sent by the custom page event.")
                    .add();

            b.append(
                            new KeyedCodec<>("Piece", Codec.STRING),
                            (data, value) -> data.piece = value == null ? "" : value,
                            data -> data.piece
                    )
                    .documentation("Armor piece affected by the UI event.")
                    .add();

            b.append(
                            new KeyedCodec<>("Index", Codec.STRING),
                            (data, value) -> data.index = value == null ? "-1" : value,
                            data -> data.index
                    )
                    .documentation("Visible carousel index selected by the player.")
                    .add();

            b.afterDecode((data, _) -> {
                if (data.action == null) data.action = "";
                if (data.piece == null) data.piece = "";
                if (data.index == null || data.index.isBlank()) data.index = "-1";
            });

            CODEC = b.build();
        }

        public String action = "";
        public String piece = "";
        public String index = "-1";
    }
}