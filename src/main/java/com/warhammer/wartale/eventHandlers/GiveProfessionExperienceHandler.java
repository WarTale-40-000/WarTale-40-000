package com.warhammer.wartale.eventHandlers;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.warhammer.wartale.components.BaseProfessionComponent;
import com.warhammer.wartale.globalEvents.GiveProfessionExperienceEvent;
import com.warhammer.wartale.globalEvents.LevelUpProfessionEvent;

import java.util.function.Consumer;

public class GiveProfessionExperienceHandler implements Consumer<GiveProfessionExperienceEvent> {
    @Override
    public void accept(GiveProfessionExperienceEvent event) {
        if (!event.playerRef().isValid()) return;

        var playerRef = event.playerRef();
        var store = playerRef.getStore();

        var playerComponent = store.getComponent(playerRef, Player.getComponentType());
        if (playerComponent == null) return;
        Player player = (Player) playerComponent;

        var professionComponent = store.getComponent(playerRef, event.professionType());
        if (professionComponent == null) {
            player.sendMessage(Message.raw("profession is null"));
            return;
        }

        BaseProfessionComponent profession = (BaseProfessionComponent) professionComponent;
        player.sendMessage(Message.raw("+%d XP".formatted(event.givenXP())));
        int oldLevel = profession.getLevel();
        boolean isLevelUp = profession.isLevelUp(event.givenXP());
        profession.addExperience(event.givenXP());
        int newLevel = profession.getLevel();
        if (isLevelUp) {
            LevelUpProfessionEvent.dispatch(playerRef, oldLevel, newLevel, event.professionType());
        }
    }
}
