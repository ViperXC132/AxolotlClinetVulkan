package io.github.axolotlclient.bridge.mixin.scoreboard;

import io.github.axolotlclient.bridge.scores.AxoTeam;
import net.minecraft.scoreboard.team.Team;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Team.class)
public abstract class TeamMixin implements AxoTeam {
	@Shadow
	public abstract String getMemberDisplayName(String string);

	@Override
	public String br$getMemberDisplayName(String s) {
		return getMemberDisplayName(s);
	}
}
