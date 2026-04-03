package com.warhammer.wartale.commands.masteries.weapon;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.RequiredArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.components.BaseMasteryComponent;
import com.warhammer.wartale.masteryCore.ItemMasteryMappingTable;

import javax.annotation.Nonnull;

public class WeaponMasteryStatsCommand extends AbstractPlayerCommand {

    public static final String COMMAND_NAME = "stats";

    private final RequiredArg<String> masteryArg;


    public WeaponMasteryStatsCommand() {
        super(COMMAND_NAME, "Set XP for WeaponMastery system.");
        this.masteryArg = withRequiredArg("mastery", "The kind of mastery you want to set XP of", ArgTypes.STRING);
    }


    @Override
    protected void execute(
            @Nonnull CommandContext context,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world
    ) {
        var masteryComponentType = ItemMasteryMappingTable.getMasteryTypeFromName(this.masteryArg.get(context));
        if (masteryComponentType == null) {
            playerRef.sendMessage(Message.raw("No Mastery found with name " + this.masteryArg.get(context)));
            return;
        }

        BaseMasteryComponent mastery = store.getComponent(ref, masteryComponentType);
        if (mastery == null) {
            playerRef.sendMessage(Message.raw("No Mastery data found for name " + this.masteryArg.get(context)));
            return;
        }

        playerRef.sendMessage(Message.raw(mastery.toString()));
    }
}
