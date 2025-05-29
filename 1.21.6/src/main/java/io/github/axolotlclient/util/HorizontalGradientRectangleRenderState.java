package io.github.axolotlclient.util;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.axolotlclient.mixin.GameRendererAccessor;
import io.github.axolotlclient.mixin.GuiGraphicsAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.renderer.RenderPipelines;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

public record HorizontalGradientRectangleRenderState(RenderPipeline pipeline,
													 TextureSetup textureSetup,
													 Matrix3x2f pose,
													 int x0,
													 int y0,
													 int x1,
													 int y1,
													 int col1,
													 int col2,
													 @Nullable ScreenRectangle scissorArea,
													 @Nullable ScreenRectangle bounds) implements GuiElementRenderState {

	public static HorizontalGradientRectangleRenderState create(GuiGraphics graphics, int x0, int y0, int x1, int y1, int col1, int col2) {
		var matrix = new Matrix3x2f(graphics.pose());
		var area = ((GuiGraphicsAccessor) graphics).getScissorStack().peek();
		return new HorizontalGradientRectangleRenderState(RenderPipelines.GUI, TextureSetup.noTexture(), matrix, x0, y0, x1, y1, col1, col2, area, getBounds(x0, y0, x1, y1, matrix, area));
	}

	public void submit() {
		((GameRendererAccessor) Minecraft.getInstance().gameRenderer).getGuiRenderState().submitGuiElement(this);
	}

	@Override
	public void buildVertices(VertexConsumer vertexConsumer, float f) {
		vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y0(), f).setColor(this.col1());
		vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y1(), f).setColor(this.col1());
		vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y1(), f).setColor(this.col2());
		vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y0(), f).setColor(this.col2());
	}

	@Nullable
	private static ScreenRectangle getBounds(int i, int j, int k, int l, Matrix3x2f matrix3x2f, @Nullable ScreenRectangle screenRectangle) {
		ScreenRectangle screenRectangle2 = new ScreenRectangle(i, j, k - i, l - j).transformMaxBounds(matrix3x2f);
		return screenRectangle != null ? screenRectangle.intersection(screenRectangle2) : screenRectangle2;
	}
}
