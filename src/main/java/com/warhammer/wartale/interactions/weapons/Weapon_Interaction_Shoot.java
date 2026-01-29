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

public class Weapon_Interaction_Shoot extends SimpleInstantInteraction {
    public static final BuilderCodec<Weapon_Interaction_Shoot> CODEC = BuilderCodec.builder(
            Weapon_Interaction_Shoot.class, Weapon_Interaction_Shoot::new, SimpleInstantInteraction.CODEC).build();

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

        //
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

        if (currentAmmoValue == null || currentAmmoValue <= 0) {
            player.sendMessage(Message.raw("Out of ammunition! Reloading weapon."));
            LOGGER.atInfo().log("No ammunition left for weapon: " + weaponID);
            currentAmmoMap.put(weaponID, weaponMetadata.getMaxAmmo());
            weaponData.setCurrentAmmo(currentAmmoMap);
            player.sendMessage(Message.raw("Weapon " + weaponID + " has " + weaponMetadata.getMaxAmmo()
                    + " as max ammo and a reload of " + weaponMetadata.getReload()));
            return;
        }

        // Decrease ammunition count
        currentAmmoMap.put(weaponID, currentAmmoValue - 1);
        commandBuffer.putComponent(ref, Wartale.WEAPON_DATA, weaponData);
        // weaponData.setCurrentAmmo(currentAmmo);
        String message = "Fired weapon: " + weaponID + ". Magazine: " + (currentAmmoValue - 1) + "/"
                + weaponMetadata.getMaxAmmo();
        player.sendMessage(Message.raw(message));
        LOGGER.atInfo().log(message);

        // TODO: Animation and sound logic would go here
    }

}
