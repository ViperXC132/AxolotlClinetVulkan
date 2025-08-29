package io.github.axolotlclient.util;

import net.minecraft.client.gui.render.pip.PictureInPictureRenderer;

public interface IdentifiablePiPRenderState<T extends PictureInPictureRenderer<?>> {
	T renderer();
}
