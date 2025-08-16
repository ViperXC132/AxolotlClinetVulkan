package io.github.axolotlclient.bridge.mixin.scoreboard;

import io.github.axolotlclient.bridge.scores.AxoTeam;
import net.minecraft.network.chat.Component;
import net.minecraft.world.scores.PlayerTeam;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerTeam.class)
public abstract class TeamMixin implements AxoTeam {

	@Override
	public String br$getMemberDisplayName(String s) {
		return PlayerTeam.formatNameForTeam((PlayerTeam) (Object) this, Component.nullToEmpty(s)).getString();
	}
}
