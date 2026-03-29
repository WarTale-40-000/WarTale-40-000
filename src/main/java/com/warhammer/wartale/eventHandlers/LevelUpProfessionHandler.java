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

public class LevelUpProfessionHandler implements Consumer<LevelUpProfessionEvent> {
    @Override
    public void accept(LevelUpProfessionEvent event) {
        if (!event.playerRef().isValid()) return;

        var ref = event.playerRef();
        var store = ref.getStore();

        var playerComponent = store.getComponent(ref, Player.getComponentType());
        if (playerComponent == null) return;
        Player player = (Player) playerComponent;

        var playerRefComponent = store.getComponent(ref, PlayerRef.getComponentType());
        if (playerRefComponent == null) return;
        PlayerRef playerRef = (PlayerRef) playerRefComponent;

        player.sendMessage(Message.raw("Handle Level Up Event"));

        var professionComponent = store.getComponent(ref, event.professionType());
        if (professionComponent == null) {
            player.sendMessage(Message.raw("profession is null"));
            return;
        }
        BaseProfessionComponent profession = (BaseProfessionComponent)professionComponent;

        String message = profession.getProfessionName() + " +" + event.levelsGained() + " Level Up!";
        String submessage = "You leveled up from " + event.oldLevel() + " to " + event.newLevel();
        player.sendMessage(Message.raw(submessage));

        String color = "#c300ff";
        String secondaryColor = "#8b32ff";
        var packetHandler = playerRef.getPacketHandler();
        var primaryMessage = Message.raw(message).color(color);
        var secondaryMessage = Message.raw(submessage).color(secondaryColor);
        var icon = new ItemStack("Weapon_Boltpistol", 1).toPacket();
        NotificationUtil.sendNotification(
                packetHandler,
                primaryMessage,
                secondaryMessage,
                (ItemWithAllMetadata) icon);
    }
}
