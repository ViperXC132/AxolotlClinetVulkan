package io.github.axolotlclient.bridge.mixin.world;

import io.github.axolotlclient.bridge.entity.AxoEntity;
import io.github.axolotlclient.bridge.world.AxoWorld;
import java.util.Collections;
import java.util.List;
import net.minecraft.entity.living.player.PlayerEntity;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Implements;
import org.spongepowered.asm.mixin.Interface;
import org.spongepowered.asm.mixin.Intrinsic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(World.class)
@Implements({
	@Interface(iface = AxoWorld.class, prefix = "bridge$")
})
public abstract class WorldMixin {
	@Shadow
	public abstract long getTimeOfDay();

	@Shadow
	@Final
	public List<PlayerEntity> players;

	@Intrinsic
	public long bridge$getTimeOfDay() {
		return getTimeOfDay();
	}

	public List<? extends AxoEntity> bridge$getPlayers() {
		return Collections.unmodifiableList(this.players);
	}
}
