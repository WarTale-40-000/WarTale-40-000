package com.warhammer.wartale.interactions.weapons;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.WaitForDataFrom;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.warhammer.wartale.items.WeaponMetadataKey;

import javax.annotation.Nonnull;

public class DecrementAmmoInteraction extends SimpleInstantInteraction {

    public static final BuilderCodec<DecrementAmmoInteraction> CODEC = BuilderCodec.builder(
            DecrementAmmoInteraction.class,
            DecrementAmmoInteraction::new,
            SimpleInstantInteraction.CODEC
    ).build();

    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    @Nonnull
    @Override
    public WaitForDataFrom getWaitForDataFrom() {
        return WaitForDataFrom.Server;
    }

    @Override
    protected void firstRun(@Nonnull InteractionType interactionType,
                            @Nonnull InteractionContext interactionContext,
                            @Nonnull CooldownHandler cooldownHandler) {
        ItemStack itemStack = interactionContext.getHeldItem();
        if (itemStack == null) {
            interactionContext.getState().state = InteractionState.Failed;
            LOGGER.atInfo().log("ItemStack is null");
            return;
        }

        Integer currentAmmoAmount = itemStack.getFromMetadataOrNull(WeaponMetadataKey.CURRENT_AMMO.key(), Codec.INTEGER);

        if (currentAmmoAmount == null || currentAmmoAmount <= 0) {
            interactionContext.getState().state = InteractionState.Failed;
            LOGGER.atInfo().log("Mag empty for weapon: " + itemStack.getItem().getId());
            return;
        }

        if (interactionContext.getHeldItemContainer() == null) {
            interactionContext.getState().state = InteractionState.Failed;
            LOGGER.atInfo().log("HeldItemContainer is null");
            return;
        }

        ItemStack newItemStack = itemStack.withMetadata(
                WeaponMetadataKey.CURRENT_AMMO.key(),
                Codec.INTEGER,
                currentAmmoAmount - 1
        );

        interactionContext.getHeldItemContainer().setItemStackForSlot(interactionContext.getHeldItemSlot(), newItemStack);
        interactionContext.setHeldItem(newItemStack);
    }
}
