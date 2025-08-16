package io.github.axolotlclient.bridge.mixin.scoreboard;

import io.github.axolotlclient.bridge.scores.AxoTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Team.class)
public abstract class TeamMixin implements AxoTeam {

	@Override
	public String br$getMemberDisplayName(String s) {
		return Team.decorateName((Team)(Object)this, Text.of(s)).getString();
	}
}
