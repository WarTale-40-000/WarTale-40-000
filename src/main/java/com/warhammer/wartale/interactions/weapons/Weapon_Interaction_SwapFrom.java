package com.warhammer.wartale.interactions.weapons;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.WartalePlugin;
import com.warhammer.wartale.components.Weapon_Data;
import com.warhammer.wartale.config.WeaponConfig;
import com.warhammer.wartale.core.ServiceRegistry;
import com.warhammer.wartale.types.WarhammerWeaponMetadata;

import javax.annotation.Nonnull;
import java.util.Map;

public class Weapon_Interaction_SwapFrom extends SimpleInstantInteraction {
    public static final BuilderCodec<Weapon_Interaction_SwapFrom> CODEC = BuilderCodec.builder(
            Weapon_Interaction_SwapFrom.class, Weapon_Interaction_SwapFrom::new, SimpleInstantInteraction.CODEC).build();

    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    @Override
    protected void firstRun(
            @Nonnull InteractionType interactionType,
            @Nonnull InteractionContext interactionContext,
            @Nonnull CooldownHandler cooldownHandler
    ) {
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

        Item item = itemStack.getItem();
        String weaponID = item.getId();

        // Initialize ammunition component
        Weapon_Data weaponData = new Weapon_Data();
        if (commandBuffer.getComponent(ref, WartalePlugin.WEAPON_DATA) != null) {
            // here we implement logic that updates the component
            weaponData = commandBuffer.getComponent(ref, WartalePlugin.WEAPON_DATA);
        } else {
            // putComponent allows you to insert declared objects
            commandBuffer.putComponent(ref, WartalePlugin.WEAPON_DATA, weaponData);
        }

        assert weaponData != null;
        Map<String, Integer> currentAmmoMap = weaponData.getCurrentAmmo();
        WeaponConfig weaponConfig = ServiceRegistry.get(WeaponConfig.class);
        Map<String, WarhammerWeaponMetadata> metadata = weaponConfig.getWeapons();
        WarhammerWeaponMetadata weaponMetadata = metadata.get(weaponID);
        if (weaponMetadata == null) {
            player.sendMessage(Message.raw("The weapon " + weaponID + " is not a registered weapon."));
            LOGGER.atInfo().log("The weapon " + weaponID + " is not a registered weapon.");
            interactionContext.getState().state = InteractionState.Failed;
        } else {
            Integer currentAmmoValue = currentAmmoMap.get(weaponID);
            weaponData.setCurrentWeaponId(weaponID);
            weaponData.setHudVisible(false);
        }
    }

}
