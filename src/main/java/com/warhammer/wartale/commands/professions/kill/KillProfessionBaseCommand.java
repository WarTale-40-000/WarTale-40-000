package com.warhammer.wartale.commands.professions.kill;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

/**
 * Command collection for the kill-profession sub-system
 * ({@code /profession kill}).
 *
 * Requires the {@code profession.admin} permission. Registers
 * {@link KillProfessionSetXpCommand} and {@link KillProfessionStatsCommand}.
 */
public class KillProfessionBaseCommand extends AbstractCommandCollection {
    /** The command literal used to invoke this sub-command group. */
    public static final String COMMAND_NAME = "kill";

    /**
     * Constructs the kill-profession command group and registers its sub-commands.
     */
    public KillProfessionBaseCommand() {
        super(COMMAND_NAME, "Command to interaction with KillProfession system.");
        this.requirePermission("profession.admin");

        this.addSubCommand(new KillProfessionSetXpCommand());
        this.addSubCommand(new KillProfessionStatsCommand());
    }
}
