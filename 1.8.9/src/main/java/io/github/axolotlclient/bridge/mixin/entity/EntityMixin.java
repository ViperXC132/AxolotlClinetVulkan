package io.github.axolotlclient.bridge.mixin.entity;

import io.github.axolotlclient.bridge.entity.AxoEntity;
import io.github.axolotlclient.bridge.math.Vec3;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Entity.class)
@Implements({
	@Interface(iface = AxoEntity.class, prefix = "bridge$")
})
public abstract class EntityMixin {
	@Shadow
	@Nullable
	public Entity vehicle;

	@Shadow
	public double z;

	@Shadow
	public double y;

	@Shadow
	public double x;

	@Shadow
	public double velocityX;

	@Shadow
	public double velocityY;

	@Shadow
	public double velocityZ;

	@Shadow
	public boolean onGround;

	@Shadow
	public abstract Vec3d getRotationVec(float tickDelta);

	@Shadow
	public float yaw;

	@Shadow
	public abstract UUID getUuid();

	@Nullable
	public AxoEntity bridge$getVehicle() {
		return vehicle;
	}

	public Vec3 bridge$getPos() {
		return new Vec3(this.x, this.y, this.z);
	}

	public Vec3 bridge$getVelocity() {
		return new Vec3(this.velocityX, this.velocityY, this.velocityZ);
	}

	public boolean bridge$isOnGround() {
		return onGround;
	}

	public float bridge$getYaw() {
		return yaw;
	}

	public Vec3 bridge$getRotation(float deltaTick) {
		final var vec = getRotationVec(deltaTick);
		return new Vec3(vec.x, vec.y, vec.z);
	}

	@Intrinsic
	public UUID bridge$getUuid() {
		return getUuid();
	}
}
