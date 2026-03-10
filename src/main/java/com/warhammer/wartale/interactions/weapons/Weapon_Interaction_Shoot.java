package com.warhammer.wartale.interactions.weapons;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.WartalePlugin;
import com.warhammer.wartale.config.WeaponConfig;
import com.warhammer.wartale.types.WarhammerWeaponMetadata;

import javax.annotation.Nonnull;

public class Weapon_Interaction_Shoot extends SimpleInstantInteraction {
    public static final BuilderCodec<Weapon_Interaction_Shoot> CODEC = BuilderCodec.builder(Weapon_Interaction_Shoot.class, Weapon_Interaction_Shoot::new, SimpleInstantInteraction.CODEC).build();

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
            LOGGER.atInfo().log("Player is null");
            interactionContext.getState().state = InteractionState.Failed;
            return;
        }

        ItemStack itemStack = interactionContext.getHeldItem();
        if (itemStack == null) {
            LOGGER.atInfo().log("ItemStack is null");
            interactionContext.getState().state = InteractionState.Failed;
            return;
        }

        WeaponConfig weaponConfig = WartalePlugin.get().getWeaponConfig().get();

        String weaponID = itemStack.getItem().getId();
        Integer currentAmmoAmount = itemStack.getFromMetadataOrNull(weaponConfig.getMetadataKey(), Codec.INTEGER);

        WarhammerWeaponMetadata weaponMetadata = weaponConfig.getWeapons().get(weaponID);
        if (weaponMetadata == null) {
            interactionContext.getState().state = InteractionState.Failed;
            LOGGER.atInfo().log("The weapon " + weaponID + " is not a registered weapon.");
            return;
        }

        // Check if weapon contains Ammo
        if (currentAmmoAmount == null || currentAmmoAmount <= 0) {
            interactionContext.getState().state = InteractionState.Failed;
            LOGGER.atInfo().log("Mag empty for weapon: " + weaponID);
            return;
        }

        byte slot = player.getInventory().getActiveHotbarSlot();
        player.getInventory().getHotbar().setItemStackForSlot(slot, itemStack.withMetadata(weaponConfig.getMetadataKey(), Codec.INTEGER, currentAmmoAmount - 1));
    }
}
