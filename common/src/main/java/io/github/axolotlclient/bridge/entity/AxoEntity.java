package io.github.axolotlclient.bridge.entity;

import io.github.axolotlclient.bridge.entity.effect.AxoStatusEffectInstance;
import io.github.axolotlclient.bridge.internal.BridgeUtil;
import io.github.axolotlclient.bridge.math.Vec3;
import java.util.List;
import java.util.UUID;
import org.jetbrains.annotations.Nullable;

public interface AxoEntity {
	@Nullable
	default AxoEntity getVehicle() {
		throw BridgeUtil.noImpl();
	}

	default Vec3 getPos() {
		throw BridgeUtil.noImpl();
	}

	default Vec3 getVelocity() {
		throw BridgeUtil.noImpl();
	}

	default boolean isOnGround() {
		throw BridgeUtil.noImpl();
	}

	default float getYaw() {
		throw BridgeUtil.noImpl();
	}

	default float getPitch() {
		throw BridgeUtil.noImpl();
	}

	default int getNetId() {
		throw BridgeUtil.noImpl();
	}

	default Vec3 getRotation(float deltaTick) {
		throw BridgeUtil.noImpl();
	}

	default UUID getUuid() {
		throw BridgeUtil.noImpl();
	}

	default List<AxoStatusEffectInstance> getStatusEffects() {
		throw BridgeUtil.noImpl();
	}
}
