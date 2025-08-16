package io.github.axolotlclient.bridge.mixin.scoreboard;

import io.github.axolotlclient.bridge.scores.AxoObjective;
import io.github.axolotlclient.bridge.scores.AxoScoreboard;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Objective.class)
public abstract class ObjectiveMixin implements AxoObjective {

	@Shadow
	public abstract Scoreboard getScoreboard();

	@Shadow
	public abstract Component getDisplayName();

	@Override
	public AxoScoreboard br$getScoreboard() {
		return getScoreboard();
	}

	@Override
	public String br$getDisplayName() {
		return getDisplayName().getString();
	}
}
