package com.warhammer.wartale.interactions.weapons;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;

import javax.annotation.Nonnull;

public class Weapon_Interaction_SwapTo extends SimpleInstantInteraction {
    public static final BuilderCodec<Weapon_Interaction_SwapTo> CODEC = BuilderCodec.builder(Weapon_Interaction_SwapTo.class, Weapon_Interaction_SwapTo::new, SimpleInstantInteraction.CODEC).build();

    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    @Override
    protected void firstRun(@Nonnull InteractionType interactionType, @Nonnull InteractionContext interactionContext, @Nonnull CooldownHandler cooldownHandler) {

    }

}
