package com.warhammer.wartale.eventHandlers;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.warhammer.wartale.components.BaseMasteryComponent;
import com.warhammer.wartale.globalEvents.GiveMasteryExperienceEvent;
import com.warhammer.wartale.globalEvents.LevelUpMasteryEvent;

import java.util.function.Consumer;

/**
 * Handles {@link GiveMasteryExperienceEvent} by awarding XP to a player's mastery.
 * <p>
 * After adding the XP, checks whether a level-up occurred and, if so,
 * dispatches a {@link LevelUpMasteryEvent} with the old and new level.
 */
public class GiveMasteryExperienceHandler implements Consumer<GiveMasteryExperienceEvent> {

    /**
     * Processes the XP award event.
     * <p>
     * Silently returns if the player reference is invalid, the player component is absent,
     * or the target mastery component is not found.
     *
     * @param event the event carrying the player reference, XP amount, and mastery type
     */
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
        int oldLevel = mastery.getLevel();
        boolean isLevelUp = mastery.isLevelUp(mastery.getExperience() + event.givenXP());
        mastery.addExperience(event.givenXP());
        int newLevel = mastery.getLevel();
        player.sendMessage(Message.raw("LevelUp?: %b, MissingXP: %d".formatted(isLevelUp, mastery.getExperienceToNextLevel())));

        if (isLevelUp) {
            LevelUpMasteryEvent.dispatch(playerRef, oldLevel, newLevel, event.masteryType());
        }
    }
}
