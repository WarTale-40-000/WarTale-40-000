package com.warhammer.wartale.systems;

import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.warhammer.wartale.components.EntityLevelComponent;
import com.warhammer.wartale.components.professions.KillProfessionComponent;
import com.warhammer.wartale.globalEvents.GiveProfessionExperienceEvent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Objects;


/**
 * Processes entity death events and awards kill XP to the responsible player.
 * <p>
 * Listens for {@link DeathComponent} additions. When a player kills an NPC that
 * carries an {@link com.warhammer.wartale.components.EntityLevelComponent}, XP is
 * calculated from the enemy's level and dispatched via
 * {@link com.warhammer.wartale.globalEvents.GiveProfessionExperienceEvent}.
 */
public class KillSystem extends DeathSystems.OnDeathSystem {
    /**
     * Called when a {@link DeathComponent} is added to an entity (i.e. it dies).
     * <p>
     * Validates that the kill source is a player, that the victim is an NPC with a
     * level component, and that the player has a {@link KillProfessionComponent}.
     * Awards scaled XP on success.
     *
     * @param ref             reference to the entity that died
     * @param deathComponent  the death component containing kill source information
     * @param store           the entity component store
     * @param commandBuffer   buffer for deferred component mutations
     */
    @Override
    public void onComponentAdded(
            @NonNullDecl Ref<EntityStore> ref,
            @NonNullDecl DeathComponent deathComponent,
            @NonNullDecl Store<EntityStore> store,
            @NonNullDecl CommandBuffer<EntityStore> commandBuffer
    ) {
        var deathInfo = deathComponent.getDeathInfo();
        if (deathInfo == null) return;

        if (!(deathInfo.getSource() instanceof Damage.EntitySource source)) return;

        // Get reference from death source (killer)
        var killerRef = source.getRef();
        if (!killerRef.isValid()) return;

        // Check if the killer is a player
        var killer = store.getComponent(killerRef, PlayerRef.getComponentType());
        if (killer == null) return;
        // Check if the killer is a player
        var playerComponent = store.getComponent(killerRef, Player.getComponentType());
        if (playerComponent == null) return;
        Player player = (Player) playerComponent;

        // Check if target is entity
        var targetEntityComponent = store.getComponent(ref, Objects.requireNonNull(NPCEntity.getComponentType()));
        if (targetEntityComponent == null) return;
        NPCEntity targetEntity = (NPCEntity) targetEntityComponent;

        // Check if target has entity level component
        var entityLevelComponent = store.getComponent(ref, EntityLevelComponent.getComponentType());
        if (entityLevelComponent == null) return;
        EntityLevelComponent entityLevel = (EntityLevelComponent) entityLevelComponent;


        player.sendMessage(Message.raw("You killed [%d]%s".formatted(entityLevel.getLevel(), targetEntity.getRoleName())));

        var professionComponent = store.getComponent(killerRef, KillProfessionComponent.getComponentType());
        if (professionComponent == null) return;
        KillProfessionComponent killProfession = (KillProfessionComponent) professionComponent;

        GiveProfessionExperienceEvent.dispatch(killerRef, killProfession.getExperienceFromDefeatedLevel(entityLevel.getLevel()), KillProfessionComponent.getComponentType());
    }

    /**
     * Returns the query that filters entities to those carrying a {@link DeathComponent}.
     *
     * @return the archetype query for this system
     */
    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Archetype.of(DeathComponent.getComponentType());
    }
}
