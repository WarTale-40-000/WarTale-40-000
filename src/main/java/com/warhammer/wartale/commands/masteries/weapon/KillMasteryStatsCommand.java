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

/**
 * Admin command that prints a player's kill-mastery statistics ({@code /mastery kill stats}).
 * <p>
 * Sends the {@link WeaponMasteryComponent#toString()} output directly to the executing player.
 */
public class KillMasteryStatsCommand extends AbstractPlayerCommand {
    /**
     * The command literal used to invoke this command.
     */
    public static final String COMMAND_NAME = "stats";

    /**
     * Constructs the stats command.
     */
    public KillMasteryStatsCommand() {
        super(COMMAND_NAME, "Set XP for KillMastery");
    }

    /**
     * Sends the executing player's kill-mastery stats as a chat message.
     *
     * @param context   the command context
     * @param store     the entity component store
     * @param ref       reference to the executing player entity
     * @param playerRef the player reference used for messaging
     * @param world     the world the player is currently in
     */
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
