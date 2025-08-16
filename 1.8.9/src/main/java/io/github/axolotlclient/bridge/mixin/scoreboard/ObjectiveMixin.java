package io.github.axolotlclient.bridge.mixin.scoreboard;

import io.github.axolotlclient.bridge.scores.AxoObjective;
import io.github.axolotlclient.bridge.scores.AxoScoreboard;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardObjective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ScoreboardObjective.class)
public abstract class ObjectiveMixin implements AxoObjective {

	@Shadow
	public abstract Scoreboard getScoreboard();

	@Shadow
	public abstract String getDisplayName();

	@Override
	public AxoScoreboard br$getScoreboard() {
		return getScoreboard();
	}

	@Override
	public String br$getDisplayName() {
		return getDisplayName();
	}
}
