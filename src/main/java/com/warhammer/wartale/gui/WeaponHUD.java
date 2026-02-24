package com.warhammer.wartale.gui;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.Anchor;
import com.hypixel.hytale.server.core.ui.Value;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import javax.annotation.Nonnull;

public class WeaponHUD extends CustomUIHud {
    // HUD data fields - update these to change what's displayed
    private String currentAmmo = "";
    private String maxAmmo = "";
    private boolean visible = true;

    public WeaponHUD(@Nonnull PlayerRef playerRef) {
        super(playerRef);
    }

    /**
     * Update the HUD content
     */
    public void updateData(String currentAmmo, String maxAmmo, boolean visible) {
        this.currentAmmo = currentAmmo;
        this.maxAmmo = maxAmmo;
        this.visible = visible;
    }

    /**
     * Show or hide the HUD
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    protected void build(@Nonnull UICommandBuilder ui) {
        if (!this.visible) {
            return;
        }

        // Load the HUD template
        ui.append("HUD/WeaponHUD.ui");

        // Set HUD content
        ui.set("#CurrentAmmo.Text", this.currentAmmo);
        ui.set("#MaxAmmo.Text", this.maxAmmo);
    }
}
