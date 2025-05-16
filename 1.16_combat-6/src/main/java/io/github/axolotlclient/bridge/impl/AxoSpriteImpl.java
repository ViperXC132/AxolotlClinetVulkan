package io.github.axolotlclient.bridge.impl;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.axolotlclient.AxolotlClientConfig.impl.options.GraphicsOption;
import io.github.axolotlclient.bridge.render.AxoSprite;
import io.github.axolotlclient.util.Util;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

public interface AxoSpriteImpl extends AxoSprite {
	void draw(MinecraftClient client, MatrixStack stack, int sX, int sY, int sW, int sH);

	record Simple(Identifier id, int x, int y, int width, int height) implements AxoSpriteImpl {
		@Override
		public void draw(MinecraftClient client, MatrixStack stack, int sX, int sY, int sW, int sH) {
			client.getTextureManager().bindTexture(id);
			DrawableHelper.drawTexture(stack, sX, sY, x, y, sW, sH, width, height);
		}
	}

	record Vanilla(Sprite sprite) implements AxoSpriteImpl {
		@Override
		public void draw(MinecraftClient client, MatrixStack stack, int sX, int sY, int sW, int sH) {
			MinecraftClient.getInstance().getTextureManager().bindTexture(sprite.getAtlas().getId());
			RenderSystem.color4f(1, 1, 1, 1);
			DrawableHelper.drawSprite(stack, sX, sY, 0, sW, sH, sprite);
		}
	}

	record Config(GraphicsOption option) implements AxoSpriteImpl {
		@Override
		public void draw(MinecraftClient client, MatrixStack stack, int sX, int sY, int sW, int sH) {
			Util.bindTexture(option);
			DrawableHelper.drawTexture(stack, sX, sY, 0, 0, sW, sH, option.get().getWidth(), option.get().getHeight());
		}
	}
}
