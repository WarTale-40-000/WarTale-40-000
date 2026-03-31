package com.warhammer.wartale.eventHandlers;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.warhammer.wartale.components.BaseProfessionComponent;
import com.warhammer.wartale.globalEvents.GiveProfessionExperienceEvent;
import com.warhammer.wartale.globalEvents.LevelUpProfessionEvent;

import java.util.function.Consumer;

/**
 * Handles {@link GiveProfessionExperienceEvent} by awarding XP to a player's profession.
 * <p>
 * After adding the XP, checks whether a level-up occurred and, if so,
 * dispatches a {@link LevelUpProfessionEvent} with the old and new level.
 */
public class GiveProfessionExperienceHandler implements Consumer<GiveProfessionExperienceEvent> {

    /**
     * Processes the XP award event.
     * <p>
     * Silently returns if the player reference is invalid, the player component is absent,
     * or the target profession component is not found.
     *
     * @param event the event carrying the player reference, XP amount, and profession type
     */
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
        boolean isLevelUp = profession.isLevelUp(profession.getExperience() + event.givenXP());
        profession.addExperience(event.givenXP());
        int newLevel = profession.getLevel();
        player.sendMessage(Message.raw("LevelUp?: %b, MissingXP: %d".formatted(isLevelUp, profession.getExperienceToNextLevel())));

        if (isLevelUp) {
            LevelUpProfessionEvent.dispatch(playerRef, oldLevel, newLevel, event.professionType());
        }
    }
}
