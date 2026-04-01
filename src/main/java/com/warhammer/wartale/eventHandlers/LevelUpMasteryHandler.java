package com.warhammer.wartale.eventHandlers;

import com.hypixel.hytale.protocol.ItemWithAllMetadata;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import com.warhammer.wartale.components.BaseMasteryComponent;
import com.warhammer.wartale.globalEvents.LevelUpMasteryEvent;

import java.util.function.Consumer;

/**
 * Handles {@link LevelUpMasteryEvent} by sending a level-up notification to the player.
 * <p>
 * Reads the mastery's display colours and icon from {@link BaseMasteryComponent} and
 * uses {@link com.hypixel.hytale.server.core.util.NotificationUtil} to push a rich UI notification
 * containing the mastery name, levels gained, and XP required for the next level.
 *
 * @param <T> the specific {@link BaseMasteryComponent} subtype associated with this event
 */
public class LevelUpMasteryHandler<T extends BaseMasteryComponent> implements Consumer<LevelUpMasteryEvent<T>> {

    /**
     * Processes the level-up event and sends a notification to the player.
     * <p>
     * Silently returns if the player reference is invalid or any required component is absent.
     *
     * @param event the event carrying the player reference, old/new levels, and mastery type
     */
    @Override
    public void accept(LevelUpMasteryEvent<T> event) {
        if (!event.playerRef().isValid()) return;

        var ref = event.playerRef();
        var store = ref.getStore();

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;

        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef == null) return;

        BaseMasteryComponent mastery = store.getComponent(ref, event.masteryType());
        if (mastery == null) return;

        String message = mastery.getMasteryName() + " +" + event.levelsGained() + " Level Up!";
        String submessage = "You leveled up from " + event.oldLevel() + " to " + event.newLevel();
        submessage += "\nGain " + mastery.getExperienceToNextLevel() + " Experience for next level!";

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
