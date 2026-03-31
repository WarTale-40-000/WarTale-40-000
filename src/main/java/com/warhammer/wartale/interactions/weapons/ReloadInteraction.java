package com.warhammer.wartale.interactions.weapons;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

import javax.annotation.Nonnull;

public class ReloadInteraction extends SimpleInstantInteraction {
    private String magazineItemId;
    private int maxMagSize;

    public static final BuilderCodec<ReloadInteraction> CODEC = BuilderCodec
            .builder(ReloadInteraction.class, ReloadInteraction::new, SimpleInstantInteraction.CODEC)
            .appendInherited(
                    new KeyedCodec<>("MagazineItemId", Codec.STRING, true), (obj, val) -> obj.magazineItemId = val, obj -> obj.magazineItemId, (obj, p) -> obj.magazineItemId = p.magazineItemId
            )
            .add()
            .appendInherited(
                    new KeyedCodec<>("MaxMagSize", Codec.INTEGER, true), (obj, val) -> obj.maxMagSize = val, obj -> obj.maxMagSize, (obj, p) -> obj.maxMagSize = p.maxMagSize
            )
            .add()
            .build();

    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    @Override
    protected void firstRun(@Nonnull InteractionType interactionType, @Nonnull InteractionContext interactionContext, @Nonnull CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
        if (commandBuffer == null) {
            interactionContext.getState().state = InteractionState.Failed;
            LOGGER.atInfo().log("CommandBuffer is null");
            return;
        }

        Ref<EntityStore> ref = interactionContext.getEntity();
        Player player = commandBuffer.getComponent(ref, Player.getComponentType());
        if (player == null) {
            interactionContext.getState().state = InteractionState.Failed;
            LOGGER.atInfo().log("Player is null");
            return;
        }

        ItemStack itemStack = interactionContext.getHeldItem();
        if (itemStack == null) {
            interactionContext.getState().state = InteractionState.Failed;
            LOGGER.atInfo().log("ItemStack is null");
            return;
        }

        String weaponID = itemStack.getItem().getId();
        Integer currentAmmoAmount = itemStack.getFromMetadataOrNull("current_ammo", Codec.INTEGER);

        if (currentAmmoAmount != null && currentAmmoAmount >= maxMagSize) {
            interactionContext.getState().state = InteractionState.Failed;
            LOGGER.atInfo().log("Mag already full for weapon: " + weaponID);
            return;
        }

        // Each magazine item represents a full magazine refill.
        CombinedItemContainer inventory = player.getInventory().getCombinedHotbarFirst();

        int magazineCount = inventory.countItemStacks(stack -> magazineItemId.equals(stack.getItemId()));

        if (magazineCount <= 0) {
            interactionContext.getState().state = InteractionState.Failed;
            LOGGER.atInfo().log("No magazine " + magazineItemId + " available for weapon: " + weaponID);
            return;
        }

        inventory.removeItemStack(new ItemStack(magazineItemId, 1));

        ItemStack newItemStack = itemStack
                .withMetadata("current_ammo", Codec.INTEGER, maxMagSize)
                .withMetadata("max_ammo", Codec.INTEGER, maxMagSize);

        assert interactionContext.getHeldItemContainer() != null;
        interactionContext.getHeldItemContainer().setItemStackForSlot(interactionContext.getHeldItemSlot(), newItemStack);
        interactionContext.setHeldItem(newItemStack);
    }
}
