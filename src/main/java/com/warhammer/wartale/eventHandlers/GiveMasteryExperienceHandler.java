package com.warhammer.wartale.eventHandlers;

import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.components.BaseMasteryComponent;
import com.warhammer.wartale.globalEvents.GiveMasteryExperienceEvent;
import com.warhammer.wartale.globalEvents.LevelUpMasteryEvent;
import com.warhammer.wartale.masteryCore.MasteryCalculations;
import java.util.function.Consumer;

public class GiveMasteryExperienceHandler implements Consumer<GiveMasteryExperienceEvent> {

  @Override
  public void accept(GiveMasteryExperienceEvent event) {
    if (event == null || !event.ref().isValid()) return;

    Ref<EntityStore> ref = event.ref();
    Store<EntityStore> store = event.store();

    PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
    if (playerRef == null) return;

    var masteryComponent = store.getComponent(ref, event.masteryType());
    if (masteryComponent == null) {
      playerRef.sendMessage(Message.raw("mastery is null"));
      return;
    }

    BaseMasteryComponent mastery = (BaseMasteryComponent) masteryComponent;
    playerRef.sendMessage(Message.raw("+%d XP".formatted(event.givenXP())));
    int oldLevel = MasteryCalculations.getLevel(mastery.getExperience());
    boolean isLevelUp = MasteryCalculations.isLevelUp(mastery.getExperience(), event.givenXP());
    mastery.addExperience(event.givenXP());
    int newLevel = MasteryCalculations.getLevel(mastery.getExperience());

    if (isLevelUp) {
      LevelUpMasteryEvent.dispatch(ref, store, oldLevel, newLevel, event.masteryType());
    }
  }
}
