package com.warhammer.wartale.eventHandlers;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.protocol.ItemWithAllMetadata;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import com.warhammer.wartale.components.BaseMasteryComponent;
import com.warhammer.wartale.globalEvents.LevelUpMasteryEvent;
import com.warhammer.wartale.masteryCore.MasteryCalculations;

import java.util.function.Consumer;

public class LevelUpMasteryHandler<T extends BaseMasteryComponent> implements Consumer<LevelUpMasteryEvent<T>> {


    @Override
    public void accept(LevelUpMasteryEvent<T> event) {
        if (!event.ref().isValid()) return;

        Ref<EntityStore> ref = event.ref();
        Store<EntityStore> store = event.store();

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;

        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef == null) return;

        BaseMasteryComponent mastery = store.getComponent(ref, event.masteryType());
        if (mastery == null) return;

        String message = mastery.getMasteryName() + " +" + event.levelsGained() + " Level Up!";
        String submessage = "You leveled up from " + event.oldLevel() + " to " + event.newLevel();
        submessage += "\nGain " + (MasteryCalculations.getTotalExperienceForLevel(event.newLevel()) - mastery.getExperience()) + " Experience for next level!";

        var packetHandler = playerRef.getPacketHandler();
        var primaryMessage = Message.raw(message).color(mastery.getLevelUpgradeColor());
        var secondaryMessage = Message.raw(submessage).color(mastery.getLevelUpgradeSecondaryColor());
        var icon = new ItemStack(mastery.getLevelUpgradeIcon(), 1).toPacket();
        NotificationUtil.sendNotification(
                packetHandler,
                primaryMessage,
                secondaryMessage,
                (ItemWithAllMetadata) icon);
    }
}
