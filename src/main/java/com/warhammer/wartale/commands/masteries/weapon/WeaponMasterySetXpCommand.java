package com.warhammer.wartale.commands.masteries.weapon;

import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.DefaultArg;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.components.BaseMasteryComponent;
import com.warhammer.wartale.masteryCore.ItemMasteryMappingTable;
import javax.annotation.Nonnull;

public class WeaponMasterySetXpCommand extends AbstractPlayerCommand {

  public static final String COMMAND_NAME = "xp";

  private static final int DEFAULT_AMOUNT = 50;

  private final DefaultArg<Integer> amountArg;

  private final RequiredArg<String> masteryArg;

  public WeaponMasterySetXpCommand() {
    super(COMMAND_NAME, "Set XP for WeaponMastery");
    this.masteryArg =
        withRequiredArg("mastery", "The kind of mastery you want to set XP of", ArgTypes.STRING);
    this.amountArg =
        withDefaultArg("amount", "XP amount (>=0)", ArgTypes.INTEGER, 50, "Defaults to 50")
            .addValidator(Validators.greaterThanOrEqual(0));
  }

  @Override
  protected void execute(
      @Nonnull CommandContext context,
      @Nonnull Store<EntityStore> store,
      @Nonnull Ref<EntityStore> ref,
      @Nonnull PlayerRef playerRef,
      @Nonnull World world) {
    var amount = amountArg.get(context);
    if (amount == null) amount = DEFAULT_AMOUNT;

    var masteryComponentType =
        ItemMasteryMappingTable.getMasteryTypeFromName(this.masteryArg.get(context));
    if (masteryComponentType == null) {
      playerRef.sendMessage(
          Message.raw("No Mastery found with name " + this.masteryArg.get(context)));
      return;
    }

    BaseMasteryComponent mastery = store.getComponent(ref, masteryComponentType);
    if (mastery == null) {
      playerRef.sendMessage(Message.raw("No Mastery data found"));
      return;
    }

    mastery.setExperience(amount);
    playerRef.sendMessage(Message.raw("Set Mastery to %d XP".formatted(amount)));
  }
}
