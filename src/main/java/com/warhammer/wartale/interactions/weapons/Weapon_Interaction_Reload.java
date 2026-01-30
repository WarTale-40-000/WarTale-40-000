package com.warhammer.wartale.interactions.weapons;

import java.util.Map;

import com.hypixel.hytale.protocol.*;
import com.warhammer.wartale.metadata.WarhammerMetadataCollection;
import com.warhammer.wartale.types.WarhammerWeaponMetadata;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.Wartale;

import com.warhammer.wartale.components.Weapon_Data;

public class Weapon_Interaction_Reload extends SimpleInstantInteraction {
    public static final BuilderCodec<Weapon_Interaction_Reload> CODEC = BuilderCodec.builder(
            Weapon_Interaction_Reload.class, Weapon_Interaction_Reload::new, SimpleInstantInteraction.CODEC).build();

    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    @Override
    protected void firstRun(InteractionType interactionType, InteractionContext interactionContext,
            CooldownHandler cooldownHandler) {
        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
        if (commandBuffer == null) {
            interactionContext.getState().state = InteractionState.Failed;
            LOGGER.atInfo().log("CommandBuffer is null");
            return;
        }

        // World world = commandBuffer.getExternalData().getWorld();
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

        Item item = itemStack.getItem();
        String weaponID = item.getId().toString();

        // Initialize ammunition component
        Weapon_Data weaponData = new Weapon_Data();
        if (commandBuffer.getComponent(ref, Wartale.WEAPON_DATA) != null) {
            // here we implement logic that updates the component
            weaponData = commandBuffer.getComponent(ref, Wartale.WEAPON_DATA);
        } else {
            // putComponent allows you to insert declared objects
            commandBuffer.putComponent(ref, Wartale.WEAPON_DATA, weaponData);
        }

        assert weaponData != null;
        Map<String, Integer> currentAmmoMap = weaponData.getCurrentAmmo();
        Map<String, WarhammerWeaponMetadata> metadata = WarhammerMetadataCollection.WEAPON_METADATA_MAP;
        WarhammerWeaponMetadata weaponMetadata = metadata.get(weaponID);
        if (weaponMetadata == null) {
            interactionContext.getState().state = InteractionState.Failed;
            player.sendMessage(Message.raw("The weapon " + weaponID + " is not a registered weapon."));
            LOGGER.atInfo().log("The weapon " + weaponID + " is not a registered weapon.");
            return;
        }
        Integer currentAmmoValue = currentAmmoMap.get(weaponID);

        // If current ammo is already full, do not reload
        if (currentAmmoValue != null && currentAmmoValue >= weaponMetadata.getMaxAmmo()) {
            player.sendMessage(Message.raw("Magazine is already full!"));
            LOGGER.atInfo().log("Magazine is already full for weapon: " + weaponID);
            interactionContext.getState().state = InteractionState.Failed;
            return;
        }

        // Initialize inventory
        var playerInventory = player.getInventory();
        var playerStorage = playerInventory.getStorage();

        // Check for available ammunition
        int countAmmoStacks = playerStorage.countItemStacks(stack -> "Weapon_Arrow_Iron".equals(stack.getItemId()));
        int requiredAmmoStacks = weaponMetadata.getMaxAmmo() - (currentAmmoValue != null ? currentAmmoValue : 0);

        // Check if there is enough ammunition to reload
        if (countAmmoStacks <= 0) {
            player.sendMessage(Message.raw("Out of ammunition! Cannot reload weapon without ammunition."));
            LOGGER.atInfo().log("No ammunition left in inventory for weapon: " + weaponID);
            interactionContext.getState().state = InteractionState.Failed;
            return;
        }

        // Reload weapon if enough ammunition is available
        int minStacksToUse = Math.min(countAmmoStacks, requiredAmmoStacks);
        currentAmmoMap.put(weaponID, currentAmmoValue != null ? currentAmmoValue + minStacksToUse : minStacksToUse);
        weaponData.setCurrentAmmo(currentAmmoMap);
        playerStorage
                .removeItemStack(new ItemStack("Weapon_Arrow_Iron", minStacksToUse));

        String message = "Reloaded weapon: " + weaponID + " with " + minStacksToUse + " Bullets";
        player.sendMessage(Message.raw(message));
        LOGGER.atInfo().log(message);
    }

}
