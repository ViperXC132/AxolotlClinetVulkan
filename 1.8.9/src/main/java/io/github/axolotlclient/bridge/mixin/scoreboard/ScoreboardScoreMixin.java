package io.github.axolotlclient.bridge.mixin.scoreboard;

import io.github.axolotlclient.bridge.scores.AxoScoreboardScore;
import net.minecraft.scoreboard.ScoreboardScore;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ScoreboardScore.class)
public abstract class ScoreboardScoreMixin implements AxoScoreboardScore {

	@Shadow
	public abstract String getOwner();

	@Override
	public @Nullable String br$getOwner() {
		return getOwner();
	}
}
