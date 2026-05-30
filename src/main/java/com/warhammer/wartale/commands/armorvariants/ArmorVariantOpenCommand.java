package com.warhammer.wartale.commands.armorvariants;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.packets.interface_.CustomPageLifetime;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.InventoryComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.gui.ArmorVariantPage;
import javax.annotation.Nonnull;

public final class ArmorVariantOpenCommand extends AbstractPlayerCommand {

  public static final String COMMAND_NAME = "open";

  public ArmorVariantOpenCommand() {
    super(COMMAND_NAME, "Open armor variant selection page.");
  }

  @Override
  protected void execute(
      @Nonnull CommandContext context,
      @Nonnull Store<EntityStore> store,
      @Nonnull Ref<EntityStore> ref,
      @Nonnull PlayerRef playerRef,
      @Nonnull World world) {
    Player player = store.getComponent(ref, Player.getComponentType());
    if (player == null) {
      playerRef.sendMessage(
          Message.raw("Could not open armor variant page: player component not found."));
      return;
    }

    InventoryComponent.Armor armor =
        store.getComponent(ref, InventoryComponent.Armor.getComponentType());
    if (armor == null) {
      playerRef.sendMessage(
          Message.raw("Could not open armor variant page: armor inventory not found."));
      return;
    }

    ArmorVariantPage page =
        new ArmorVariantPage(playerRef, CustomPageLifetime.CanDismissOrCloseThroughInteraction);

    player.getPageManager().openCustomPage(ref, store, page);
  }
}
