package io.github.axolotlclient.bridge.mixin.scoreboard;

import java.util.Collection;

import io.github.axolotlclient.bridge.scores.AxoObjective;
import io.github.axolotlclient.bridge.scores.AxoScoreboard;
import io.github.axolotlclient.bridge.scores.AxoScoreboardScore;
import io.github.axolotlclient.bridge.scores.AxoTeam;
import net.minecraft.scoreboard.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Scoreboard.class)
public abstract class ScoreboardMixin implements AxoScoreboard {

	@Shadow
	public abstract Team getPlayerTeam(String par1);

	@Shadow
	public abstract Collection<ScoreboardEntry> getEntriesForObjective(ScoreboardObjective par1);

	@Override
	public Collection<? extends AxoScoreboardScore> br$getScores(AxoObjective objective) {
		return getEntriesForObjective((ScoreboardObjective) objective);
	}

	@Override
	public AxoTeam br$getTeamOfMember(String s) {
		return getPlayerTeam(s);
	}
}
