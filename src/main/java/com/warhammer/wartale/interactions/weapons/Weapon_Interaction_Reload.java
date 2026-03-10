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

public class Weapon_Interaction_Reload extends SimpleInstantInteraction {
    private String ammoItemId;
    private int maxMagSize;

    public static final BuilderCodec<Weapon_Interaction_Reload> CODEC = BuilderCodec.builder(Weapon_Interaction_Reload.class, Weapon_Interaction_Reload::new, SimpleInstantInteraction.CODEC).append(new KeyedCodec<>("AmmoItemId", Codec.STRING, true), (obj, val) -> obj.ammoItemId = val, obj -> obj.ammoItemId).add().append(new KeyedCodec<>("MaxMagSize", Codec.INTEGER, true), (obj, val) -> obj.maxMagSize = val, obj -> obj.maxMagSize).add().build();

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
        int ammoNeeded = maxMagSize;
        CombinedItemContainer inventory = player.getInventory().getCombinedHotbarFirst();

        int ammoCount = inventory.countItemStacks(stack -> ammoItemId.equals(stack.getItemId()));

        if (ammoCount <= 0) {
            interactionContext.getState().state = InteractionState.Failed;
            LOGGER.atInfo().log("No ammo " + ammoItemId + " available for weapon: " + weaponID);
            return;
        }

        // Consume as many ammo items as needed to fill the mag, capped by what the player has
        int currentAmmo = currentAmmoAmount != null ? currentAmmoAmount : 0;
        int ammoToFill = ammoNeeded - currentAmmo;
        int ammoToConsume = Math.min(ammoToFill, ammoCount);

        inventory.removeItemStack(new ItemStack(ammoItemId, ammoToConsume));

        ItemStack newItemStack = itemStack.withMetadata("current_ammo", Codec.INTEGER, currentAmmo + ammoToConsume).withMetadata("max_ammo", Codec.INTEGER, maxMagSize);
        interactionContext.getHeldItemContainer().setItemStackForSlot(interactionContext.getHeldItemSlot(), newItemStack);
        interactionContext.setHeldItem(newItemStack);
    }
}
