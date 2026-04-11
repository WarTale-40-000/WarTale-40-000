package com.warhammer.wartale.interactions.weapons;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
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

public class LoadMagazineInteraction extends SimpleInstantInteraction {
  public static final BuilderCodec<LoadMagazineInteraction> CODEC =
      BuilderCodec.builder(
              LoadMagazineInteraction.class,
              LoadMagazineInteraction::new,
              SimpleInstantInteraction.CODEC)
          .appendInherited(
              new KeyedCodec<>("ReloadAmount", Codec.INTEGER, true),
              (obj, val) -> obj.reloadAmount = val,
              obj -> obj.reloadAmount,
              (obj, p) -> obj.reloadAmount = p.reloadAmount)
          .addValidator(Validators.greaterThan(-1))
          .add()
          .build();
  public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
  private int reloadAmount;

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
    LOGGER.atInfo().log("LoadMagazineInteraction firstRun");
    ItemStack itemStack = interactionContext.getHeldItem();
    if (itemStack == null) {
      interactionContext.getState().state = InteractionState.Failed;
      LOGGER.atInfo().log("ItemStack is null");
      return;
    }

    if (interactionContext.getHeldItemContainer() == null) {
      interactionContext.getState().state = InteractionState.Failed;
      LOGGER.atInfo().log("HeldItemContainer is null");
      return;
    }

    ItemStack newItemStack =
        itemStack
            .withMetadata(WeaponMetadataKey.CURRENT_AMMO.key(), Codec.INTEGER, reloadAmount)
            .withMetadata(WeaponMetadataKey.MAG_SIZE.key(), Codec.INTEGER, reloadAmount);

    interactionContext
        .getHeldItemContainer()
        .setItemStackForSlot(interactionContext.getHeldItemSlot(), newItemStack);
    interactionContext.setHeldItem(newItemStack);
  }
}
