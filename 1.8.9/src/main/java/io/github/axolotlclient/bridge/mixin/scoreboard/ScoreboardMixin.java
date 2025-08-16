package io.github.axolotlclient.bridge.mixin.scoreboard;

import java.util.Collection;

import io.github.axolotlclient.bridge.scores.AxoObjective;
import io.github.axolotlclient.bridge.scores.AxoScoreboard;
import io.github.axolotlclient.bridge.scores.AxoScoreboardScore;
import io.github.axolotlclient.bridge.scores.AxoTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardScore;
import net.minecraft.scoreboard.team.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Scoreboard.class)
public abstract class ScoreboardMixin implements AxoScoreboard {

	@Shadow
	public abstract Collection<ScoreboardScore> getScores();

	@Shadow
	public abstract Team getTeamOfMember(String string);

	@Shadow
	public abstract Team getTeam(String string);

	@Override
	public Collection<? extends AxoScoreboardScore> br$getScores(AxoObjective objective) {
		return getScores();
	}

	@Override
	public AxoTeam br$getTeamOfMember(String s) {
		return getTeamOfMember(s);
	}

	@Override
	public AxoTeam br$getTeam(String s) {
		return getTeam(s);
	}
}
