package com.warhammer.wartale.gui;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import javax.annotation.Nonnull;

/**
 * Custom HUD shown to every player that joined the world.
 * <p>
 * Renders the {@code HUD/WartaleHUD.ui} UI layout and exposes helpers to
 * update the ammo counter section in real time via
 * {@link com.warhammer.wartale.systems.HudTickingSystem}.
 */
public class WartaleHUD extends CustomUIHud {

    /**
     * Constructs the HUD for the given player.
     *
     * @param playerRef reference to the owning player entity
     */
    public WartaleHUD(@Nonnull PlayerRef playerRef) {
        super(playerRef);
    }

    /**
     * Appends the main Wartale HUD layout to the UI builder.
     *
     * @param ui the UI command builder used to construct the HUD
     */
    @Override
    protected void build(@Nonnull UICommandBuilder ui) {
        ui.append("HUD/WartaleHUD.ui");
    }

    /**
     * Updates the ammo counter section of the HUD.
     * <p>
     * When {@code visible} is {@code true}, sets the weapon icon, ammo text, and
     * text colour (red when {@code shouldReload} is {@code true}, white otherwise).
     * When {@code false}, hides the entire ammo section.
     *
     * @param visible      whether the ammo section should be shown
     * @param ammoText     formatted ammo string (e.g. {@code "12/30"})
     * @param weaponId     item ID used to resolve the weapon icon path
     * @param shouldReload {@code true} when current ammo is zero, triggers a red tint
     */
    public void setAmmoSection(boolean visible, String ammoText, String weaponId, boolean shouldReload) {
        UICommandBuilder patch = new UICommandBuilder();
        patch.set("#Content.Visible", visible);

        if (visible) {
            patch.set("#WeaponIcon.Background", "Icons/Weapons/" + weaponId + ".png");
            patch.set("#CurrentAmmo.Text", ammoText);

            // Recolors Text if Ammo low
            if (shouldReload) {
                patch.set("#CurrentAmmo.Style.TextColor", "#ff0000");
            } else  {
                patch.set("#CurrentAmmo.Style.TextColor", "#ffffff");
            }
        }
        update(false, patch);
    }
}
