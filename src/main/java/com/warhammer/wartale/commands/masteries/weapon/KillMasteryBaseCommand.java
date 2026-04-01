package com.warhammer.wartale.commands.masteries.weapon;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

/**
 * Command collection for the kill-mastery sub-system
 * ({@code /mastery kill}).
 * <p>
 * Requires the {@code mastery.admin} permission. Registers
 * {@link KillMasterySetXpCommand} and {@link KillMasteryStatsCommand}.
 */
public class KillMasteryBaseCommand extends AbstractCommandCollection {
    /**
     * The command literal used to invoke this sub-command group.
     */
    public static final String COMMAND_NAME = "weapon";

    /**
     * Constructs the kill-mastery command group and registers its sub-commands.
     */
    public KillMasteryBaseCommand() {
        super(COMMAND_NAME, "Command to interaction with KillMastery system.");
        this.requirePermission("mastery.admin");

        this.addSubCommand(new KillMasterySetXpCommand());
        this.addSubCommand(new KillMasteryStatsCommand());
    }
}
