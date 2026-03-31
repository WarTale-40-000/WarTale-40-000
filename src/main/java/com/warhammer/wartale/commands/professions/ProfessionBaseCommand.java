package com.warhammer.wartale.commands.professions;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.warhammer.wartale.commands.professions.kill.KillProfessionBaseCommand;

/**
 * Root command collection for the profession system ({@code /profession}).
 * <p>
 * Requires the {@code profession.admin} permission. Registers all profession
 * sub-command groups (e.g. {@link KillProfessionBaseCommand}).
 */
public class ProfessionBaseCommand extends AbstractCommandCollection {
    /** The root command literal used to invoke this command. */
    public static final String COMMAND_NAME = "profession";

    /**
     * Constructs the profession command and registers all sub-command groups.
     */
    public ProfessionBaseCommand() {
        super(COMMAND_NAME, "Command to interaction with profession system.");
        this.requirePermission("profession.admin");

        this.addSubCommand(new KillProfessionBaseCommand());
    }
}
