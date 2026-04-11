package com.warhammer.wartale.commands.masteries;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.warhammer.wartale.commands.masteries.weapon.WeaponMasteryBaseCommand;

public class MasteryBaseCommand extends AbstractCommandCollection {

  public static final String COMMAND_NAME = "mastery";

  public MasteryBaseCommand() {
    super(COMMAND_NAME, "Command to interaction with mastery system.");
    this.requirePermission("mastery.admin");

    this.addSubCommand(new WeaponMasteryBaseCommand());
  }
}
