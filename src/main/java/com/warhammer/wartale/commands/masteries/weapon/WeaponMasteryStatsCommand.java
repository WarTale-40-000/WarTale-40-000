package com.warhammer.wartale.commands.masteries.weapon;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.components.masteries.WeaponMasteryComponent;
import com.warhammer.wartale.components.masteries.weapons.BoltpistolMasteryComponent;

import javax.annotation.Nonnull;

public class WeaponMasteryStatsCommand extends AbstractPlayerCommand {

    public static final String COMMAND_NAME = "stats";


    public WeaponMasteryStatsCommand() {
        super(COMMAND_NAME, "Set XP for WeaponMastery system.");
    }


    @Override
    protected void execute(
            @Nonnull CommandContext context,
            @Nonnull Store<EntityStore> store,
            @Nonnull Ref<EntityStore> ref,
            @Nonnull PlayerRef playerRef,
            @Nonnull World world
    ) {
        WeaponMasteryComponent mastery = store.getComponent(ref, BoltpistolMasteryComponent.getComponentType());
        if (mastery == null) {
            playerRef.sendMessage(Message.raw("No Mastery data found"));
            return;
        }

        playerRef.sendMessage(Message.raw(mastery.toString()));
    }
}
