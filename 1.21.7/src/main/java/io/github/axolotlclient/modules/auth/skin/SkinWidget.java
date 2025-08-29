package io.github.axolotlclient.modules.auth.skin;

import java.util.concurrent.CompletableFuture;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.vertex.VertexConsumer;
import io.github.axolotlclient.api.util.UUIDHelper;
import io.github.axolotlclient.bridge.util.AxoIdentifier;
import io.github.axolotlclient.mixin.GameRendererAccessor;
import io.github.axolotlclient.mixin.GuiGraphicsAccessor;
import io.github.axolotlclient.modules.auth.Account;
import io.github.axolotlclient.modules.auth.Auth;
import io.github.axolotlclient.modules.auth.MSApi;
import io.github.axolotlclient.util.ClientColors;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.render.TextureSetup;
import net.minecraft.client.gui.render.state.GuiElementRenderState;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2f;

public class SkinWidget extends AbstractWidget {
	private static final float MODEL_HEIGHT = 2.125F;
	private static final float FIT_SCALE = 0.97F;
	private static final float ROTATION_SENSITIVITY = 2.5F;
	private static final float DEFAULT_ROTATION_X = -5.0F;
	private static final float DEFAULT_ROTATION_Y = 30.0F;
	private static final float ROTATION_X_LIMIT = 50.0F;
	private float rotationX = DEFAULT_ROTATION_X;
	@Setter
	private float rotationY = DEFAULT_ROTATION_Y;
	@Getter
	@Setter
	private Skin skin;
	@Getter
	@Setter
	private Cape cape;
	private final Account owner;
	private boolean noCape, noCapeActive;
	private boolean highlightIfEquipped;

	public SkinWidget(int width, int height, Skin skin, @Nullable Cape cape, Account owner) {
		super(0, 0, width, height, CommonComponents.EMPTY);
		this.skin = skin;
		this.cape = cape;
		this.owner = owner;
	}

	public SkinWidget(int width, int height, Skin skin, Account owner) {
		this(width, height, skin, null, owner);
	}

	public void noCape(boolean noCapeActive) {
		noCape = true;
		this.noCapeActive = noCapeActive;
	}

	public SkinWidget highlightIfEquipped() {
		highlightIfEquipped = true;
		return this;
	}

	@Override
	protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		var minecraft = Minecraft.getInstance();

		float scale = FIT_SCALE * this.getHeight() / MODEL_HEIGHT;
		float pivotY = -1.0625F;

		AxoIdentifier skinRl;
		boolean classic;
		boolean equipped = isEquipped();
		SkinManager skinManager = Auth.getInstance().getSkinManager();
		CompletableFuture<AxoIdentifier> loader = skin == null ? null : skinManager.loadSkin(skin, owner);
		if (loader != null && loader.isDone()) {
			skinRl = loader.join();
			classic = skin.isClassicVariant();
		} else {
			var skin = DefaultPlayerSkin.get(UUIDHelper.fromUndashed(owner.getUuid()));
			classic = skin.model() == PlayerSkin.Model.WIDE;
			skinRl = skin.texture();
		}
		var capeRl = cape == null ? null : skinManager.loadCape(cape);
		if (highlightIfEquipped && equipped) {
			GradientHoleRectangleRenderState.create(guiGraphics, getX()-1, getY()-4, getRight()+1, getBottom(), getWidth() / 6, ClientColors.SELECTOR_GREEN.toInt(), 0).submit();
		}

		var renderer = SkinRenderer.getOrCreate(minecraft.renderBuffers().bufferSource(), minecraft, "" + hashCode());
		((GuiGraphicsAccessor) guiGraphics).getGuiRenderState()
			.submitPicturesInPictureState(
				new SkinRenderState(classic, (ResourceLocation) skinRl, (ResourceLocation) capeRl, this.rotationX, this.rotationY, pivotY, this.getX(), this.getY(), this.getRight(), this.getBottom(), scale, guiGraphics.scissorStack.peek(), renderer, -1));
	}

	@Override
	protected void onDrag(double mouseX, double mouseY, double dragX, double dragY) {
		this.rotationX = Mth.clamp(this.rotationX - (float) dragY * ROTATION_SENSITIVITY, -ROTATION_X_LIMIT, ROTATION_X_LIMIT);
		this.rotationY += (float) dragX * ROTATION_SENSITIVITY;
	}

	private record GradientHoleRectangleRenderState(RenderPipeline pipeline, TextureSetup textureSetup, Matrix3x2f pose,
													int x0, int y0, int x1, int y1, int gradientWidth, int col1,
													int col2, @Nullable ScreenRectangle scissorArea,
													@Nullable ScreenRectangle bounds) implements GuiElementRenderState {

		public static GradientHoleRectangleRenderState create(GuiGraphics graphics, int x0, int y0, int x1, int y1, int gradientWidth, int col1, int col2) {
			var matrix = new Matrix3x2f(graphics.pose());
			var area = ((GuiGraphicsAccessor) graphics).getScissorStack().peek();
			return new GradientHoleRectangleRenderState(RenderPipelines.GUI, TextureSetup.noTexture(), matrix, x0, y0, x1, y1, gradientWidth, col1, col2, area, getBounds(x0, y0, x1, y1, matrix, area));
		}

		public void submit() {
			((GameRendererAccessor) Minecraft.getInstance().gameRenderer).getGuiRenderState().submitGuiElement(this);
		}

		@Override
		public void buildVertices(VertexConsumer vertexConsumer, float f) {
			{ //top
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y0(), f).setColor(this.col1());
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x0() + gradientWidth(), this.y0() + gradientWidth(), f).setColor(this.col2());
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x1() - gradientWidth(), this.y0() + gradientWidth(), f).setColor(this.col2());
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y0(), f).setColor(this.col1());
			}
			{ //left
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y1(), f).setColor(this.col1());
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x0() + gradientWidth(), this.y1() - gradientWidth(), f).setColor(this.col2());
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x0() + gradientWidth(), this.y0() + gradientWidth(), f).setColor(this.col2());
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y0(), f).setColor(this.col1());
			}
			{ //bottom
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y1(), f).setColor(this.col1());
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x1() - gradientWidth(), this.y1() - gradientWidth(), f).setColor(this.col2());
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x0() + gradientWidth(), this.y1() - gradientWidth(), f).setColor(this.col2());
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x0(), this.y1(), f).setColor(this.col1());
			}
			{ //right
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y0(), f).setColor(this.col1());
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x1() - gradientWidth(), this.y0() + gradientWidth(), f).setColor(this.col2());
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x1() - gradientWidth(), this.y1() - gradientWidth(), f).setColor(this.col2());
				vertexConsumer.addVertexWith2DPose(this.pose(), this.x1(), this.y1(), f).setColor(this.col1());
			}
		}

		@Nullable
		private static ScreenRectangle getBounds(int i, int j, int k, int l, Matrix3x2f matrix3x2f, @Nullable ScreenRectangle screenRectangle) {
			ScreenRectangle screenRectangle2 = new ScreenRectangle(i, j, k - i, l - j).transformMaxBounds(matrix3x2f);
			return screenRectangle != null ? screenRectangle.intersection(screenRectangle2) : screenRectangle2;
		}
	}

	@Override
	public void playDownSound(SoundManager handler) {
	}

	@Override
	protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Nullable
	@Override
	public ComponentPath nextFocusPath(FocusNavigationEvent event) {
		return null;
	}

	public boolean isEquipped() {
		return noCape ? noCapeActive : (cape != null ? cape.isActive() : skin != null && skin.isActive());
	}

	public CompletableFuture<MSApi.MCProfile> equip() {
		var msApi = Auth.getInstance().getMsApi();
		if (noCape) {
			return msApi.hideCape(owner);
		}
		if (cape != null) {
			return cape.equip(msApi, owner);
		}
		if (skin != null) {
			return skin.equip(msApi, owner);
		}
		return msApi.resetSkin(owner);
	}
}
