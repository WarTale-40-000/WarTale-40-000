package com.warhammer.wartale.eventHandlers;

import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.warhammer.wartale.gui.WartaleHUD;

/**
 * Static handler for player-related world events.
 */
public class PlayerEventHandler {

    /**
     * Called when a player is added to the world.
     * <p>
     * Creates a new {@link WartaleHUD} for the player and registers it with the
     * player's HUD manager as the active custom HUD.
     *
     * @param event the event containing the player's entity holder
     */
    public static void onAddPlayerToWorld(AddPlayerToWorldEvent event) {
        Holder<EntityStore> holder = event.getHolder();
        PlayerRef playerRef = holder.getComponent(PlayerRef.getComponentType());
        Player player = holder.getComponent(Player.getComponentType());
        if (playerRef == null || player == null) return;

        WartaleHUD hud = new WartaleHUD(playerRef);
        player.getHudManager().setCustomHud(playerRef, hud);
    }
}
