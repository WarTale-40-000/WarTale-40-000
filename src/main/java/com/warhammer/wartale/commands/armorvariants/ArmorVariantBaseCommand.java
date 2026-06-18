package com.warhammer.wartale.commands.armorvariants;

import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;

public final class ArmorVariantBaseCommand extends AbstractCommandCollection {

  public static final String COMMAND_NAME = "armorvariants";

  public ArmorVariantBaseCommand() {
    super(COMMAND_NAME, "Command to interact with armor variant UI.");
    this.requirePermission("armorvariants.admin");

    this.addSubCommand(new ArmorVariantOpenCommand());
  }
}
