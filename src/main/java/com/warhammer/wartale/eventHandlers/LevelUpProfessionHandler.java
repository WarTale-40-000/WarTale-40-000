package com.warhammer.wartale.eventHandlers;

import com.hypixel.hytale.protocol.ItemWithAllMetadata;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import com.warhammer.wartale.components.BaseProfessionComponent;
import com.warhammer.wartale.globalEvents.LevelUpProfessionEvent;

import java.util.function.Consumer;

/**
 * Handles {@link LevelUpProfessionEvent} by sending a level-up notification to the player.
 * <p>
 * Reads the profession's display colours and icon from {@link BaseProfessionComponent} and
 * uses {@link com.hypixel.hytale.server.core.util.NotificationUtil} to push a rich UI notification
 * containing the profession name, levels gained, and XP required for the next level.
 *
 * @param <T> the specific {@link BaseProfessionComponent} subtype associated with this event
 */
public class LevelUpProfessionHandler<T extends BaseProfessionComponent> implements Consumer<LevelUpProfessionEvent<T>> {

    /**
     * Processes the level-up event and sends a notification to the player.
     * <p>
     * Silently returns if the player reference is invalid or any required component is absent.
     *
     * @param event the event carrying the player reference, old/new levels, and profession type
     */
    @Override
    public void accept(LevelUpProfessionEvent<T> event) {
        if (!event.playerRef().isValid()) return;

        var ref = event.playerRef();
        var store = ref.getStore();

        Player player = store.getComponent(ref, Player.getComponentType());
        if (player == null) return;

        PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRef == null) return;

        BaseProfessionComponent profession = store.getComponent(ref, event.professionType());
        if (profession == null) return;

        String message = profession.getProfessionName() + " +" + event.levelsGained() + " Level Up!";
        String submessage = "You leveled up from " + event.oldLevel() + " to " + event.newLevel();
        submessage += "\nGain " + profession.getExperienceToNextLevel() + " Experience for next level!";

        var packetHandler = playerRef.getPacketHandler();
        var primaryMessage = Message.raw(message).color(profession.getLevelUpgradeColor());
        var secondaryMessage = Message.raw(submessage).color(profession.getLevelUpgradeSecondaryColor());
        var icon = new ItemStack(profession.getLevelUpgradeIcon(), 1).toPacket();
        NotificationUtil.sendNotification(
                packetHandler,
                primaryMessage,
                secondaryMessage,
                (ItemWithAllMetadata) icon);
    }
}
