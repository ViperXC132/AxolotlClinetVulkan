package io.github.axolotlclient.modules.auth.skin;

import io.github.axolotlclient.util.IdentifiablePiPRenderState;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.state.pip.PictureInPictureRenderState;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public record SkinRenderState(boolean classicVariant,
							  ResourceLocation skinTexture,
							  @Nullable ResourceLocation cape,
							  float rotationX,
							  float rotationY,
							  float pivotY,
							  int x0,
							  int y0,
							  int x1,
							  int y1,
							  float scale,
							  @Nullable ScreenRectangle scissorArea,
							  @Nullable ScreenRectangle bounds,
							  SkinRenderer renderer,
							  int color) implements PictureInPictureRenderState, IdentifiablePiPRenderState<SkinRenderer> {

	public SkinRenderState(boolean classicVariant,
						   ResourceLocation skinTexture,
						   @Nullable ResourceLocation cape,
						   float rotationX,
						   float rotationY,
						   float pivotY,
						   int x0,
						   int y0,
						   int x1,
						   int y1,
						   float scale,
						   @Nullable ScreenRectangle scissorArea,
						   SkinRenderer renderer,
						   int color) {
		this(classicVariant, skinTexture, cape, rotationX, rotationY, pivotY, x0, y0, x1, y1, scale, scissorArea, PictureInPictureRenderState.getBounds(x0, y0, x1, y1, scissorArea), renderer, color);
	}
}
