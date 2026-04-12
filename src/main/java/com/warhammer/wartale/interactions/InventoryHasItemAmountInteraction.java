package com.warhammer.wartale.interactions;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.WaitForDataFrom;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.items.WeaponMetadataKey;

import javax.annotation.Nonnull;

public class InventoryHasItemAmountInteraction extends SimpleInstantInteraction {
    public static final BuilderCodec<InventoryHasItemAmountInteraction> CODEC = BuilderCodec.builder(
                    InventoryHasItemAmountInteraction.class,
                    InventoryHasItemAmountInteraction::new,
                    SimpleInstantInteraction.CODEC)
            .appendInherited(new KeyedCodec<>("ItemId", Codec.STRING, true),
                    (obj, val) -> obj.itemId = val,
                    obj -> obj.itemId,
                    (obj, p) -> obj.itemId = p.itemId)
            .addValidator(Validators.nonNull())
            .addValidatorLate(() -> Item.VALIDATOR_CACHE.getValidator().late())
            .add()
            .appendInherited(new KeyedCodec<>("Amount", Codec.INTEGER, true),
                    (obj, val) -> obj.amount = val,
                    obj -> obj.amount,
                    (obj, p) -> obj.amount = p.amount)
            .addValidator(Validators.greaterThan(0))
            .add()
            .build();
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private String itemId;
    private int amount;

  @Nonnull
  @Override
  public WaitForDataFrom getWaitForDataFrom() {
    return WaitForDataFrom.Server;
  }

  @Override
  protected void firstRun(
      @Nonnull InteractionType interactionType,
      @Nonnull InteractionContext interactionContext,
      @Nonnull CooldownHandler cooldownHandler) {
    CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
    if (commandBuffer == null) {
      interactionContext.getState().state = InteractionState.Failed;
      LOGGER.atWarning().log("CommandBuffer is null");
      return;
    }

        Ref<EntityStore> ref = interactionContext.getEntity();
        Player player = commandBuffer.getComponent(ref, Player.getComponentType());
        if (player == null) {
            interactionContext.getState().state = InteractionState.Failed;
            LOGGER.atWarning().log("Player is null");
            return;
        }

        // Add Mag ID to metadata for item
        ItemStack itemStack = interactionContext.getHeldItem();
        if (itemStack == null) {
            interactionContext.getState().state = InteractionState.Failed;
            LOGGER.atInfo().log("ItemStack is null");
            return;
        }
        ItemStack newItemStack = itemStack.withMetadata(
                WeaponMetadataKey.MAG_ID.key(),
                Codec.STRING,
                itemId
        );
        interactionContext.getHeldItemContainer().setItemStackForSlot(interactionContext.getHeldItemSlot(), newItemStack);

        CombinedItemContainer inventory = InventoryComponent.getCombined(commandBuffer,
                ref,
                InventoryComponent.HOTBAR_FIRST);
        int itemCount = inventory.countItemStacks(stack -> itemId.equals(stack.getItemId()));

    if (itemCount < amount) {
      interactionContext.getState().state = InteractionState.Failed;
    }
  }
}
