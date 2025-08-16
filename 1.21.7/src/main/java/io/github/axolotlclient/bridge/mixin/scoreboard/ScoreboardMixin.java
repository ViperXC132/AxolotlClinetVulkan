package io.github.axolotlclient.bridge.mixin.scoreboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import io.github.axolotlclient.bridge.scores.AxoObjective;
import io.github.axolotlclient.bridge.scores.AxoScoreboard;
import io.github.axolotlclient.bridge.scores.AxoScoreboardScore;
import io.github.axolotlclient.bridge.scores.AxoTeam;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerScoreEntry;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Scoreboard.class)
public abstract class ScoreboardMixin implements AxoScoreboard {

	@Shadow
	@Nullable
	public abstract PlayerTeam getPlayerTeam(String teamName);

	@Shadow
	public abstract Collection<PlayerScoreEntry> listPlayerScores(Objective objective);

	@Override
	public Collection<? extends AxoScoreboardScore> br$getScores(AxoObjective objective) {
		var entries = new ArrayList<>(listPlayerScores((Objective) objective));
		entries.sort(Comparator.comparing(PlayerScoreEntry::value).reversed().thenComparing(PlayerScoreEntry::owner, String.CASE_INSENSITIVE_ORDER));
		return entries;
	}

	@Override
	public AxoTeam br$getTeamOfMember(String s) {
		return getPlayerTeam(s);
	}
}
