package com.warhammer.wartale.eventHandlers;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.warhammer.wartale.components.BaseMasteryComponent;
import com.warhammer.wartale.globalEvents.GiveMasteryExperienceEvent;
import com.warhammer.wartale.globalEvents.LevelUpMasteryEvent;

import java.util.function.Consumer;

public class GiveMasteryExperienceHandler implements Consumer<GiveMasteryExperienceEvent> {


    @Override
    public void accept(GiveMasteryExperienceEvent event) {
        if (!event.playerRef().isValid()) return;

        var playerRef = event.playerRef();
        var store = playerRef.getStore();

        var playerComponent = store.getComponent(playerRef, Player.getComponentType());
        if (playerComponent == null) return;
        Player player = (Player) playerComponent;

        var masteryComponent = store.getComponent(playerRef, event.masteryType());
        if (masteryComponent == null) {
            player.sendMessage(Message.raw("mastery is null"));
            return;
        }

        BaseMasteryComponent mastery = (BaseMasteryComponent) masteryComponent;
        player.sendMessage(Message.raw("+%d XP".formatted(event.givenXP())));
        int oldLevel = MasteryCalculations.getLevel(mastery.getExperience());
        boolean isLevelUp = MasteryCalculations.isLevelUp(mastery.getExperience(), event.givenXP());
        mastery.addExperience(event.givenXP());
        int newLevel = MasteryCalculations.getLevel(mastery.getExperience());
        player.sendMessage(Message.raw("LevelUp?: %b, MissingXP: %d".formatted(isLevelUp, MasteryCalculations.getExperienceToNextLevel(mastery.getExperience()))));

        if (isLevelUp) {
            LevelUpMasteryEvent.dispatch(playerRef, oldLevel, newLevel, event.masteryType());
        }
    }
}
