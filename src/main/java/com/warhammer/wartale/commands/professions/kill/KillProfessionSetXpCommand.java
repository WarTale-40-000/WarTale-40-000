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
 * Admin command that directly sets a player's kill-profession XP ({@code /profession kill xp [amount]}).
 * <p>
 * If {@code amount} is omitted, defaults to {@code DEFAULT_AMOUNT}. The value must be {@code >= 0}.
 */
public class KillProfessionSetXpCommand extends AbstractPlayerCommand {
    /** The command literal used to invoke this command. */
    public static final String COMMAND_NAME = "xp";

    /** XP amount used when the optional argument is not provided. */
    private static final int DEFAULT_AMOUNT = 50;

    private final OptionalArg<Integer> amountArg;

    /**
     * Constructs the set-XP command and registers the optional {@code amount} argument.
     */
    public KillProfessionSetXpCommand() {
        super(COMMAND_NAME, "Set XP for KillProfession");
        this.amountArg = withOptionalArg("amount", "XP amount (>=0)", ArgTypes.INTEGER)
                .addValidator(Validators.greaterThanOrEqual(0));
    }

    /**
     * Sets the executing player's kill-profession XP to the given amount.
     *
     * @param context   the command context holding parsed arguments
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
        var amount = amountArg.get(context);
        if (amount == null) amount = DEFAULT_AMOUNT;

        KillProfessionComponent profession = store.getComponent(ref, KillProfessionComponent.getComponentType());
        if (profession == null) {
            playerRef.sendMessage(Message.raw("No Profession data found"));
            return;
        }

        profession.setExperience(amount);
        playerRef.sendMessage(Message.raw("Set Profession to %d XP".formatted(amount)));
    }
}
