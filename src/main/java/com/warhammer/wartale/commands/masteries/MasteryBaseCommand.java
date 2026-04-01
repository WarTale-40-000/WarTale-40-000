package com.warhammer.wartale.commands.masteries;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.warhammer.wartale.commands.masteries.weapon.KillMasteryBaseCommand;

/**
 * Root command collection for the mastery system ({@code /mastery}).
 * <p>
 * Requires the {@code mastery.admin} permission. Registers all mastery
 * sub-command groups (e.g. {@link KillMasteryBaseCommand}).
 */
public class MasteryBaseCommand extends AbstractCommandCollection {
    /** The root command literal used to invoke this command. */
    public static final String COMMAND_NAME = "mastery";

    /**
     * Constructs the mastery command and registers all sub-command groups.
     */
    public MasteryBaseCommand() {
        super(COMMAND_NAME, "Command to interaction with mastery system.");
        this.requirePermission("mastery.admin");

        this.addSubCommand(new KillMasteryBaseCommand());
    }
}
