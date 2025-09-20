package io.github.axolotlclient.mixin;

import io.github.axolotlclient.util.duck.NameTagSubmitExtension;
import net.minecraft.client.renderer.SubmitNodeStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(SubmitNodeStorage.NameTagSubmit.class)
public abstract class NameTagSubmitMixin implements NameTagSubmitExtension {
	@Unique
	private boolean hasBadge, forLevelHead;

	@Override
	public void axolotlclient$hasBadge(boolean b) {
		this.hasBadge = b;
	}

	@Override
	public boolean axolotlclient$hasBadge() {
		return hasBadge;
	}

	@Override
	public boolean axolotlclient$isForLevelHead() {
		return forLevelHead;
	}

	@Override
	public void axolotlclient$isForLevelHead(boolean b) {
		forLevelHead = b;
	}
}
