package com.warhammer.wartale.commands.masteries.weapon;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public class WeaponMasteryBaseCommand extends AbstractCommandCollection {

    public static final String COMMAND_NAME = "weapon";


    public WeaponMasteryBaseCommand() {
        super(COMMAND_NAME, "Command to interaction with WeaponMastery system.");
        this.requirePermission("mastery.admin");

        this.addSubCommand(new WeaponMasterySetXpCommand());
        this.addSubCommand(new WeaponMasteryStatsCommand());
    }
}
