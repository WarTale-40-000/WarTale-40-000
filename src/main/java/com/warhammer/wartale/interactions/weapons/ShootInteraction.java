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
import com.hypixel.hytale.server.core.modules.entity.component.Intangible;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.SimpleInstantInteraction;
import com.hypixel.hytale.server.core.modules.time.TimeResource;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import org.joml.Vector3d;

import javax.annotation.Nonnull;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class ShootInteraction extends SimpleInstantInteraction {
    
    private String projectileId;
    
    private float maxSpreadAngle = 0f;

    
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

    
    public static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

    
    @Nonnull
    @Override
    public WaitForDataFrom getWaitForDataFrom() {
        return WaitForDataFrom.Server;
    }

    
    @Override
    protected void firstRun(@Nonnull InteractionType interactionType, @Nonnull InteractionContext interactionContext, @Nonnull CooldownHandler cooldownHandler) {
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
