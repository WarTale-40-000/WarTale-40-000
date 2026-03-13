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
    private boolean visible = true;
    private UICommandBuilder builder;

    public WeaponHUD(@Nonnull PlayerRef playerRef) {
        super(playerRef);
        this.builder = new UICommandBuilder();
    }

    /**
     * Update the HUD content
     */
    public void updateData(String currentAmmo, boolean visible) {
        this.currentAmmo = currentAmmo;
        this.visible = visible;
        this.builder.append("HUD/WeaponHUD.ui");
        this.builder.set("#CurrentAmmo.Text", this.currentAmmo);
    }

    /**
     * Show or hide the HUD
     */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    @Override
    protected void build(@Nonnull UICommandBuilder ui) {
        this.builder = ui;

        if (!this.visible) {
            return;
        }


        // Load the HUD template
        this.builder.append("HUD/WeaponHUD.ui");

        // Set HUD content
        this.builder.set("#CurrentAmmo.Text", this.currentAmmo);
    }

    public void updateUI(boolean clear_ui){
        update(clear_ui, this.builder);
    }
}
