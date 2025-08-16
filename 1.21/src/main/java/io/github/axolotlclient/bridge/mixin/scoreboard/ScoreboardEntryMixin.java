package io.github.axolotlclient.bridge.mixin.scoreboard;

import io.github.axolotlclient.bridge.scores.AxoScoreboardScore;
import net.minecraft.scoreboard.ScoreboardEntry;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ScoreboardEntry.class)
public abstract class ScoreboardEntryMixin implements AxoScoreboardScore {

	@Shadow
	public abstract String owner();

	@Shadow
	public abstract boolean isHidden();

	@Override
	public @Nullable String br$getOwner() {
		return owner();
	}

	@Override
	public boolean br$isHidden() {
		return br$getOwner() == null || isHidden();
	}
}
