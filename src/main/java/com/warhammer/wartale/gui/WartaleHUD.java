package com.warhammer.wartale.gui;

import com.hypixel.hytale.server.core.entity.entities.player.hud.CustomUIHud;
import com.hypixel.hytale.server.core.ui.builder.UICommandBuilder;
import com.hypixel.hytale.server.core.universe.PlayerRef;

import javax.annotation.Nonnull;

public class WartaleHUD extends CustomUIHud {

    public WartaleHUD(@Nonnull PlayerRef playerRef) {
        super(playerRef);
    }

    @Override
    protected void build(@Nonnull UICommandBuilder ui) {
        ui.append("HUD/WartaleHUD.ui");
    }

    public void setAmmoSection(boolean visible, String ammoText) {
        UICommandBuilder patch = new UICommandBuilder();
        patch.set("#Content.Visible", visible);
        if (visible) {
            patch.set("#CurrentAmmo.Text", ammoText);
        }
        update(false, patch);
    }
}
