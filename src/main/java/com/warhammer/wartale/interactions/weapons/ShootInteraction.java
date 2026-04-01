package com.warhammer.wartale.interactions.weapons;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Rotation3f;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.WaitForDataFrom;
import com.hypixel.hytale.server.core.asset.type.projectile.config.Projectile;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.ProjectileComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.component.Intangible;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.modules.time.TimeResource;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import com.warhammer.wartale.items.WeaponMetadataKey;
import org.joml.Vector3d;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Interaction that fires a projectile from the player's held weapon.
 * <p>
 * Decrements the weapon's {@code current_ammo} metadata by one, assembles and
 * spawns a {@link com.hypixel.hytale.server.core.entity.entities.ProjectileComponent}
 * in the direction the player is looking, and optionally applies random spread.
 * Fails if the magazine is empty or any required context component is absent.
 */
public class ShootInteraction extends SimpleInstantInteraction {
    /** Asset ID of the projectile definition to spawn on each shot. */
    private String projectileId;
    /** Maximum random angular offset (in degrees) applied to each shot; {@code 0} means no spread. */
    private float maxSpreadAngle = 0f;

    /** Codec used to serialise/deserialise this interaction from item definition JSON. */
    public static final BuilderCodec<ShootInteraction> CODEC = BuilderCodec
            .builder(ShootInteraction.class, ShootInteraction::new, SimpleInstantInteraction.CODEC)
            .appendInherited(
                    new KeyedCodec<>("ProjectileId", Codec.STRING), (obj, val) -> obj.projectileId = val, obj -> obj.projectileId, (obj, p) -> obj.projectileId = p.projectileId
            )
            .addValidator(Validators.nonNull())
            .addValidator(Projectile.VALIDATOR_CACHE.getValidator().late())
            .add()
            .appendInherited(
                    new KeyedCodec<>("MaxSpreadAngle", Codec.FLOAT, false), (obj, val) -> obj.maxSpreadAngle = val, obj -> obj.maxSpreadAngle, (obj, p) -> obj.maxSpreadAngle = p.maxSpreadAngle
            )
            .add()
            .build();

    /** Logger for this interaction class. */
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    /**
     * Indicates that interaction data is authorised from the server side.
     *
     * @return {@link WaitForDataFrom#Server}
     */
    @Nonnull
    @Override
    public WaitForDataFrom getWaitForDataFrom() {
        return WaitForDataFrom.Server;
    }

    /**
     * Decrements ammo and spawns a projectile in the player's look direction.
     * <p>
     * Fails if the held item is absent, the magazine is empty, the command buffer
     * is unavailable, or the shooter is not a living entity.
     * When {@link #maxSpreadAngle} is greater than zero, a random yaw and pitch
     * offset is applied to simulate bullet spread.
     *
     * @param interactionType    the type of interaction being processed
     * @param interactionContext context providing orientation, inventory, and entity references
     * @param cooldownHandler    handler for managing interaction cooldown timers
     */
    @Override
    protected void firstRun(@Nonnull InteractionType interactionType, @Nonnull InteractionContext interactionContext, @Nonnull CooldownHandler cooldownHandler) {
        ItemStack itemStack = interactionContext.getHeldItem();
        if (itemStack == null) {
            interactionContext.getState().state = InteractionState.Failed;
            LOGGER.atInfo().log("ItemStack is null");
            return;
        }

        String weaponID = itemStack.getItem().getId();
        Integer currentAmmoAmount = itemStack.getFromMetadataOrNull(WeaponMetadataKey.CURRENT_AMMO.key(), Codec.INTEGER);

        if (currentAmmoAmount == null || currentAmmoAmount <= 0) {
            interactionContext.getState().state = InteractionState.Failed;
            LOGGER.atInfo().log("Mag empty for weapon: " + weaponID);
            return;
        }

        ItemStack newItemStack = itemStack.withMetadata(WeaponMetadataKey.CURRENT_AMMO.key(), Codec.INTEGER, currentAmmoAmount - 1);
        if (interactionContext.getHeldItemContainer() == null) {
            interactionContext.getState().state = InteractionState.Failed;
            LOGGER.atInfo().log("ItemStack is null");
            return;
        }

        interactionContext.getHeldItemContainer().setItemStackForSlot(interactionContext.getHeldItemSlot(), newItemStack);
        interactionContext.setHeldItem(newItemStack);

        CommandBuffer<EntityStore> commandBuffer = interactionContext.getCommandBuffer();
        assert commandBuffer != null;
        Ref<EntityStore> sourceRef = interactionContext.getEntity();

        if (!(EntityUtils.getEntity(sourceRef, commandBuffer) instanceof LivingEntity)) {
            interactionContext.getState().state = InteractionState.Failed;
            LOGGER.atInfo().log("Entity is not living entity");
            return;
        }

        Transform lookVec = TargetUtil.getLook(sourceRef, commandBuffer);
        Vector3d lookPosition = lookVec.getPosition();
        Rotation3f lookRotation = lookVec.getRotation();
        UUIDComponent sourceUuidComponent = commandBuffer.getComponent(sourceRef, UUIDComponent.getComponentType());
        if (sourceUuidComponent != null) {
            UUID sourceUuid = sourceUuidComponent.getUuid();
            TimeResource timeResource = commandBuffer.getResource(TimeResource.getResourceType());
            Holder<EntityStore> holder = ProjectileComponent.assembleDefaultProjectile(timeResource, this.projectileId, lookPosition, lookRotation);
            ProjectileComponent projectileComponent = holder.getComponent(ProjectileComponent.getComponentType());

            assert projectileComponent != null;

            holder.ensureComponent(Intangible.getComponentType());
            if (projectileComponent.getProjectile() == null) {
                projectileComponent.initialize();
                if (projectileComponent.getProjectile() == null) {
                    return;
                }
            }

            float yaw = lookRotation.yaw();
            float pitch = lookRotation.pitch();
            if (this.maxSpreadAngle > 0) {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                double spreadRadius = random.nextDouble() * Math.toRadians(this.maxSpreadAngle);
                double spreadDirection = random.nextDouble() * 2 * Math.PI;
                yaw += (float) (spreadRadius * Math.cos(spreadDirection));
                pitch += (float) (spreadRadius * Math.sin(spreadDirection));
            }

            projectileComponent.shoot(
                    holder, sourceUuid, lookPosition.x(), lookPosition.y(), lookPosition.z(), yaw, pitch
            );
            commandBuffer.addEntity(holder, AddReason.SPAWN);
        }
    }
}
