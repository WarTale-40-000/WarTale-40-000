package com.warhammer.wartale.commands.masteries.weapon;

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
import com.warhammer.wartale.components.masteries.WeaponMasteryComponent;
import com.warhammer.wartale.components.masteries.weapons.BoltpistolMasteryComponent;

import javax.annotation.Nonnull;

public class WeaponMasterySetXpCCommand extends AbstractPlayerCommand {

    public static final String COMMAND_NAME = "xp";


    private static final int DEFAULT_AMOUNT = 50;

    private final OptionalArg<Integer> amountArg;


    public WeaponMasterySetXpCCommand() {
        super(COMMAND_NAME, "Set XP for WeaponMastery");
        this.amountArg = withOptionalArg("amount", "XP amount (>=0)", ArgTypes.INTEGER)
                .addValidator(Validators.greaterThanOrEqual(0));
    }


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

        WeaponMasteryComponent mastery = store.getComponent(ref, BoltpistolMasteryComponent.getComponentType());
        if (mastery == null) {
            playerRef.sendMessage(Message.raw("No Mastery data found"));
            return;
        }

        mastery.setExperience(amount);
        playerRef.sendMessage(Message.raw("Set Mastery to %d XP".formatted(amount)));
    }
}
