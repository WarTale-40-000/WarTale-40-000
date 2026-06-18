package com.warhammer.wartale.interactions;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.WaitForDataFrom;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.gui.ArmorVariantPage;
import javax.annotation.Nonnull;

public final class OpenArmorVariantPageInteraction extends SimpleInstantInteraction {

  public static final BuilderCodec<OpenArmorVariantPageInteraction> CODEC =
      BuilderCodec.builder(
              OpenArmorVariantPageInteraction.class,
              OpenArmorVariantPageInteraction::new,
              SimpleInstantInteraction.CODEC)
          .build();

  public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

  public OpenArmorVariantPageInteraction() {}

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
      LOGGER.atWarning().log("Could not open armor variant page: command buffer is null.");
      return;
    }

    Ref<EntityStore> ref = interactionContext.getEntity();

    if (!ref.isValid()) {
      interactionContext.getState().state = InteractionState.Failed;
      LOGGER.atWarning().log("Could not open armor variant page: entity ref is invalid.");
      return;
    }

    Store<EntityStore> store = ref.getStore();

    Player player = commandBuffer.getComponent(ref, Player.getComponentType());

    if (player == null) {
      interactionContext.getState().state = InteractionState.Failed;
      LOGGER.atWarning().log("Could not open armor variant page: player component not found.");
      return;
    }

    InventoryComponent.Armor armor =
        commandBuffer.getComponent(ref, InventoryComponent.Armor.getComponentType());

    if (armor == null) {
      interactionContext.getState().state = InteractionState.Failed;
      LOGGER.atWarning().log("Could not open armor variant page: armor inventory not found.");
      return;
    }

    PlayerRef playerRef = commandBuffer.getComponent(ref, PlayerRef.getComponentType());

    if (playerRef == null) {
      interactionContext.getState().state = InteractionState.Failed;
      LOGGER.atWarning().log("Could not open armor variant page: player ref not found.");
      return;
    }

    ArmorVariantPage page =
        new ArmorVariantPage(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction);

    player.getPageManager().openCustomPage(ref, store, page);
  }
}
