package com.warhammer.wartale.commands.masteries.weapon;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.command.system.basecommands.AbstractCommandCollection;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.components.BaseMasteryComponent;
import com.warhammer.wartale.components.masteries.weapons.BoltpistolMasteryComponent;

public class WeaponMasteryBaseCommand extends AbstractCommandCollection {

    public static final String COMMAND_NAME = "weapon";


    public WeaponMasteryBaseCommand() {
        super(COMMAND_NAME, "Command to interaction with WeaponMastery system.");
        this.requirePermission("mastery.admin");

        this.addSubCommand(new WeaponMasterySetXpCommand());
        this.addSubCommand(new WeaponMasteryStatsCommand());
    }

    protected ComponentType<EntityStore, ? extends BaseMasteryComponent> getBaseMasteryComponent(String masteryName) {
        switch (masteryName) {
            case "boltpistol":
                return BoltpistolMasteryComponent.getComponentType();
            default:
                return null;
        }
    }
}
