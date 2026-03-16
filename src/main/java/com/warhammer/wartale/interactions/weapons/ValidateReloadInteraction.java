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

public class ValidateReloadInteraction extends SimpleInstantInteraction {
    private String ammoItemId;
    private int maxMagSize;

    public static final BuilderCodec<ValidateReloadInteraction> CODEC = BuilderCodec
            .builder(ValidateReloadInteraction.class, ValidateReloadInteraction::new, SimpleInstantInteraction.CODEC)
            .appendInherited(
                    new KeyedCodec<>("AmmoItemId", Codec.STRING, true), (obj, val) -> obj.ammoItemId = val, obj -> obj.ammoItemId, (obj, p) -> obj.ammoItemId = p.ammoItemId
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

        // Check that the player has enough ammo items to fill the magazine
        CombinedItemContainer inventory = player.getInventory().getCombinedHotbarFirst();

        int ammoCount = inventory.countItemStacks(stack -> ammoItemId.equals(stack.getItemId()));

        if (ammoCount <= 0) {
            interactionContext.getState().state = InteractionState.Failed;
            LOGGER.atInfo().log("No ammo " + ammoItemId + " available for weapon: " + weaponID);
        }
    }
}
