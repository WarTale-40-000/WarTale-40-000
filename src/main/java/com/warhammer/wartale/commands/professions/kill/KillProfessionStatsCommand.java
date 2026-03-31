package com.warhammer.wartale.commands.professions.kill;

import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.CommandContext;
import com.hypixel.hytale.server.core.command.system.arguments.system.OptionalArg;
import com.hypixel.hytale.server.core.command.system.arguments.types.ArgTypes;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractPlayerCommand;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.components.professions.KillProfessionComponent;

import javax.annotation.Nonnull;

/**
 * Admin command that prints a player's kill-profession statistics ({@code /profession kill stats}).
 * <p>
 * Sends the {@link KillProfessionComponent#toString()} output directly to the executing player.
 */
public class KillProfessionStatsCommand extends AbstractPlayerCommand {
    /** The command literal used to invoke this command. */
    public static final String COMMAND_NAME = "stats";

    /**
     * Constructs the stats command.
     */
    public KillProfessionStatsCommand() {
        super(COMMAND_NAME, "Set XP for KillProfession");
    }

    /**
     * Sends the executing player's kill-profession stats as a chat message.
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
        KillProfessionComponent profession = store.getComponent(ref, KillProfessionComponent.getComponentType());
        if (profession == null) {
            playerRef.sendMessage(Message.raw("No Profession data found"));
            return;
        }

        playerRef.sendMessage(Message.raw(profession.toString()));
    }
}
