package io.github.axolotlclient.bridge.mixin;

import com.mojang.authlib.GameProfile;
import io.github.axolotlclient.bridge.AxoPlayerListEntry;
import java.util.UUID;
import net.minecraft.client.network.PlayerInfo;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(PlayerInfo.class)
public class PlayerInfoMixin implements AxoPlayerListEntry {
	@Shadow
	@Final
	private GameProfile profile;

	@Override
	public String br$getName() {
		return profile.getName();
	}

	@Override
	public UUID br$getId() {
		return profile.getId();
	}
}
