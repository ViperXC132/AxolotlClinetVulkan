package io.github.axolotlclient.bridge.impl;

import io.github.axolotlclient.AxolotlClientConfig.impl.options.GraphicsOption;
import io.github.axolotlclient.bridge.render.AxoSprite;
import io.github.axolotlclient.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiElement;
import net.minecraft.resource.Identifier;

public interface AxoSpriteImpl extends AxoSprite {
	void draw(Minecraft client, int sX, int sY, int sW, int sH);

	record Simple(Identifier id, int x, int y, int width, int height) implements AxoSpriteImpl {
		@Override
		public void draw(Minecraft client, int sX, int sY, int sW, int sH) {
			client.getTextureManager().bind(id);
			GuiElement.drawTexture(sX, sY, x, y, sW, sH, width, height);
		}
	}

	record Config(GraphicsOption option) implements AxoSpriteImpl {
		@Override
		public void draw(Minecraft client, int sX, int sY, int sW, int sH) {
			Util.bindTexture(option);
			GuiElement.drawTexture(sX, sY, 0, 0, sW, sH, option.get().getWidth(), option.get().getHeight());
		}
	}
}
